package org.msh.quantb.services.calc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.msh.quantb.model.forecast.Forecast;
import org.msh.quantb.model.forecast.ForecastingMedicine;
import org.msh.quantb.model.forecast.ForecastingRegimen;
import org.msh.quantb.model.forecast.ForecastingRegimenResult;
import org.msh.quantb.model.forecast.ForecastingResult;
import org.msh.quantb.model.forecast.MedicineCons;
import org.msh.quantb.model.forecast.Month;
import org.msh.quantb.model.mvp.ModelFactory;
import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.ForecastingBatchUIAdapter;
import org.msh.quantb.services.io.ForecastingMedicineUIAdapter;
import org.msh.quantb.services.io.ForecastingOrderUIAdapter;
import org.msh.quantb.services.io.ForecastingRegimenResultUIAdapter;
import org.msh.quantb.services.io.ForecastingRegimenUIAdapter;
import org.msh.quantb.services.io.ForecastingResultUIAdapter;
import org.msh.quantb.services.io.ForecastingTotalMedicine;
import org.msh.quantb.services.io.MedicationUIAdapter;
import org.msh.quantb.services.io.MedicineConsUIAdapter;
import org.msh.quantb.services.io.MedicineUIAdapter;
import org.msh.quantb.services.io.MonthUIAdapter;
import org.msh.quantb.services.io.PhaseUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;

/**
 * Forecasting calculator
 * @author alexey
 *
 */
public class ForecastingCalculation {
	private static final int MAX_ALLOWED_CASES = 5000000;
	private Forecast forecasting;
	private ForecastUIAdapter forecastUI;
	private List<ForecastingError> error = new ArrayList<ForecastingError>();
	private List<ForecastingError> warning = new ArrayList<ForecastingError>();
	private ModelFactory modelFactory;
	private List<MedicineResume> medicineSummary;
	//it is for consumption report and delivery schedule
	private ArrayList<MedicineConsumption> medicineConsumption;

	/**
	 * Only valid constructor
	 * @param _forecast forecast to calculation
	 * @param _modelFactory model factory
	 */
	public ForecastingCalculation(Forecast _forecast, ModelFactory _modelFactory){
		this.forecasting = _forecast;
		this.forecastUI = new ForecastUIAdapter(this.forecasting);
		this.modelFactory = _modelFactory;
	}

	/**
	 * Execute the forecasting<br>
	 * All old forecasting calculation results will be removed
	 * @return empty list if all OK or error messages list otherwise
	 */
	public List<ForecastingError> execute(){
		validate();
		if (this.error.size() == 0){
			clearResults();
			calcCasesOnTreatment();
			calcNewCases();
			calcMedicinesRegimes();
			adjustResults();
			calcMedicines();
			calcMedicinesResults();
		}
		return this.error;
	}

	/**
	 * Adjust calculated quantities of medicines in regimen<br>
	 * Previous method calculates medicines consumptions uses assumption that all cases treats completely<br>
	 * But, in real life only part of cases treats completely. For a number of cases, treatment will not be completed fully
	 * due to many reasons.<br>
	 * QuanTB allows to determine adjustment coefficients for medicines. It allows more precisely accounts real medicines needs
	 */
	public void adjustResults() {
		for(ForecastingMedicineUIAdapter fmUi: this.getForecastUI().getMedicines()){
			for (ForecastingRegimenUIAdapter fr : this.getForecastUI().getRegimes()){
				for (ForecastingRegimenResultUIAdapter frr : fr.getResults()){
					MedicineConsUIAdapter mConsUI = frr.getMedConsunption(fmUi.getMedicine());
					if (mConsUI != null){
						MedicineCons mCons = mConsUI.getMedicineConsObj();
						mCons.setConsIntensiveOld(calcPercents(mCons.getConsIntensiveOld(), fmUi.getAdjustmentEnrolled()));
						mCons.setConsIntensiveNew(calcPercents(mCons.getConsIntensiveNew(), fmUi.getAdjustmentExpected()));
						mCons.setConsContiOld(calcPercents(mCons.getConsContiOld(), fmUi.getAdjustmentEnrolled()));
						mCons.setConsContiNew(calcPercents(mCons.getConsContiNew(), fmUi.getAdjustmentExpected()));
						for(int i=0; i< mCons.getConsOtherOld().size(); i++){
							mCons.getConsOtherOld().set(i, calcPercents(mCons.getConsOtherOld().get(i), fmUi.getAdjustmentEnrolled()));
						};
						for(int i=0; i< mCons.getConsOtherOld().size(); i++){
							mCons.getConsOtherNew().set(i, calcPercents(mCons.getConsOtherNew().get(i), fmUi.getAdjustmentExpected()));
						};
					}
				}
			}
		}

	}
	/**
	 * Simply calc percents from the value
	 * Rounds by common rules
	 * @param value
	 * @param percents
	 * @return adjusted value
	 */
	public static BigDecimal calcPercents(BigDecimal value, BigDecimal percents){
		//TODO compare percents to 100 ?
		BigDecimal res = value.multiply(percents);
		res = res.setScale(4);
		res = res.divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP);
		res = res.setScale(2, BigDecimal.ROUND_HALF_UP);
		return res;
	}
	/**
	 * now for each medicine on each month consumptions are known
	 * so, determine calculation results based on batch information
	 * Rules:
	 * <ul>
	 * <li> medicines must consumed from the batches
	 * <li> there are existing batches and batches in orders
	 * <li> existing batches available from reference date
	 * <li> batches in orders will be available from orders arrive date
	 * <li> consumption must begin from batch with available quantity and minimum expire date (FIFO)
	 * <li> if no medicines available from any batch in any particular month it is missing quantity
	 * </ul>
	 * Bathes manipulation, for convenience and testable, implements as special class named BatchCalculator
	 */
	public void calcMedicinesResults() {
		BatchCalculator bcalc = new BatchCalculator(this.forecastUI,this.modelFactory);
		bcalc.consume();
	}
	/**
	 * Regimes consumption must be calculated before call this method.
	 * For all medicines from initial calculation data, calculate medicine consumption
	 * for each medicine of each day based on regimen consumption
	 * 
	 */
	public void calcMedicines() {
		// create slots for results
		for(ForecastingMedicineUIAdapter fm : forecastUI.getMedicines()){
			fm.createResults(modelFactory, forecastUI);
			for(ForecastingResult fr : fm.getFcMedicineObj().getResults()){
				ForecastingResultUIAdapter frU = new ForecastingResultUIAdapter(fr);
				frU.setCasesAndMedCons(forecastUI, fm);
			}
		}
	}
	/**
	 * Calculate new cases quantity for all regimes<br>
	 * Must be called after calcCasesOnTreatment !!!!!
	 */
	public void calcNewCases() {
		Calendar begin = forecastUI.getVeryFirstDate();
		Calendar end = forecastUI.getLastDate();
		for(ForecastingRegimenUIAdapter fr : forecastUI.getRegimes()){
			CasesCalculator newCases = new CasesCalculator(begin,end, fr.getRegimen());
			for(CalendarQuantity cQ :forecastUI.getCalendarNewCases(fr)){
				newCases.add(cQ.getCalendar(), cQ.getQuantity());
				//System.out.println(DateUtils.formatDate(cQ.getCalendar().getTime(), "dd.MM.yy") + " quantity " + cQ.getQuantity());
			}
			List<ForecastingRegimenResult> results = fr.getFcRegimenObj().getResults();
			for(int i=0; i<newCases.getIntensive().size(); i++){
				results.get(i).getIntensive().setNewCases(newCases.getIntensive().get(i));
				results.get(i).getContinious().setNewCases(newCases.getContinuous().get(i));
				int j=0;
				for(List<BigDecimal> res : newCases.getOthers()){
					results.get(i).getAddPhases().get(j).setNewCases(res.get(i));
					j++;
				}
			}
		}
	}

	/**
	 * create list of old cases quantity for all regimens
	 * public for test purpose
	 */
	public void calcCasesOnTreatment() {
		//Calculate cases
		Calendar begin = forecastUI.getVeryFirstDate();
		Calendar end = forecastUI.getLastDate();
		for(ForecastingRegimenUIAdapter fr : forecastUI.getRegimes()){		
			CasesCalculator oldCases = new CasesCalculator(begin, end, fr.getRegimen());
			for(CalendarQuantity cQ : forecastUI.getCalendarOldCases(fr)){
				oldCases.add(cQ.getCalendar(), cQ.getQuantity());; 
			}
			//create results
			for(int i=0; i<oldCases.getIntensive().size();i++){
				ForecastingRegimenResult res = oldCases.createResult(modelFactory, i);
				fr.getFcRegimenObj().getResults().add(res);
			}
		}
	}


	/**
	 * Calculate medicines consumption for all regimes for every day
	 * cases on cure and new cases must be already calculated
	 */
	public void calcMedicinesRegimes(){
		for(ForecastingRegimenUIAdapter fr : forecastUI.getRegimes()){
			for(ForecastingRegimenResultUIAdapter frr : fr.getResults()){
				calcMedDay(fr, frr); 
			}
		}
	}
	/**
	 * Calc medicines consumption for particular day in particular regimen
	 * @param fr regimen
	 * @param frr month cases
	 */
	public void calcMedDay(ForecastingRegimenUIAdapter fr,
			ForecastingRegimenResultUIAdapter frr) {
		// calculate all phases, old and new cases
		BigDecimal cases = frr.getIntensive().getOldCases();
		MedicineBuffer intOld = new MedicineBuffer();
		calcMedPhase(cases,fr.getRegimen().getIntensive(),frr.getMonth(), frr.getFromDay(), intOld);
		cases = frr.getIntensive().getNewCases();
		MedicineBuffer intNew = new MedicineBuffer();
		calcMedPhase(cases,fr.getRegimen().getIntensive(),frr.getMonth(), frr.getFromDay(), intNew);
		cases = frr.getContinious().getOldCases();
		MedicineBuffer contOld = new MedicineBuffer();
		calcMedPhase(cases,fr.getRegimen().getContinious(),frr.getMonth(), frr.getFromDay(), contOld);
		cases = frr.getContinious().getNewCases();
		MedicineBuffer contNew = new MedicineBuffer();
		calcMedPhase(cases,fr.getRegimen().getContinious(),frr.getMonth(), frr.getFromDay(), contNew);
		List<MedicineBuffer> otherOld = new ArrayList<MedicineBuffer>();
		List<MedicineBuffer> otherNew = new ArrayList<MedicineBuffer>();
		// for additional phases
		int i = 0;
		for(PhaseUIAdapter addPhUi: fr.getRegimen().getAddPhases()){
			MedicineBuffer mbOld = new MedicineBuffer();
			MedicineBuffer mbNew = new MedicineBuffer();
			BigDecimal oldCases = frr.getAdditionalPhases().get(i).getOldCases();
			BigDecimal newCases = frr.getAdditionalPhases().get(i).getNewCases();
			calcMedPhase(oldCases, addPhUi, frr.getMonth(),frr.getFromDay(),mbOld);
			calcMedPhase(newCases, addPhUi, frr.getMonth(),frr.getFromDay(),mbNew);
			otherOld.add(mbOld);
			otherNew.add(mbNew);
			i++;
		}
		// now we are have buffers with calculation results, so write result
		Set<MedicineUIAdapter> resBuf = new TreeSet<MedicineUIAdapter>();
		resBuf.addAll(intOld.getMeds());
		resBuf.addAll(intNew.getMeds());
		resBuf.addAll(contOld.getMeds());
		resBuf.addAll(contNew.getMeds());
		for(MedicineBuffer mb : otherOld){
			resBuf.addAll(mb.getMeds());
		}
		for(MedicineBuffer mb : otherNew){
			resBuf.addAll(mb.getMeds());
		}
		for(MedicineUIAdapter med : resBuf){
			MedicineCons mc = modelFactory.createMedicineCons(med.getMedicine());
			MedicineConsUIAdapter mcU = new MedicineConsUIAdapter(mc);
			//fill results
			mcU.setConsContiNew(contNew.get(med));
			mcU.setConsContiOld(contOld.get(med));
			mcU.setConsIntensiveNew(intNew.get(med));
			mcU.setConsIntensiveOld(intOld.get(med));
			for(MedicineBuffer mb : otherOld){
				mcU.getConsOtherOld().add(mb.get(med)); 
			}
			for(MedicineBuffer mb  : otherNew){
				mcU.getConsOtherNew().add(mb.get(med)); 
			}
			frr.getResultObj().getCons().add(mcU.getMedicineConsObj());
		}

	}
	/**
	 * Calculate medicine consumption for particular phase, day, regimen
	 * @param cases cases number
	 * @param phase phase to determine medications
	 * @param month month to calculate
	 * @param dayInMonth day in this month
	 * @param buffer temporary calculation buffer
	 */
	public void calcMedPhase(BigDecimal cases,
			PhaseUIAdapter phase, MonthUIAdapter month, int dayInMonth, MedicineBuffer buffer) {
		IdaysCalculator daysCalc = Presenter.getDIMStrategy();
		for(MedicationUIAdapter medi : phase.getMedications()){
			/*			if(medi.getMedicine().getNameForDisplayWithAbbrev().contains("penem") && month.getYear()==2014 & month.getMonth()==4 && dayInMonth==17){
				System.out.println(month+"at"+ dayInMonth+" cases " + cases);
			}*/
			int days = daysCalc.calculatePeriod(month, dayInMonth, dayInMonth, medi.getDaysPerWeek());
			long dd = days*medi.getDosage();
			BigDecimal res = cases.multiply(BigDecimal.valueOf(dd));
			res = res.setScale(2, BigDecimal.ROUND_UP);
			buffer.add(medi.getMedicine(),res);
		}
	}

	/**
	 * clear all previous calculation results
	 */
	public void clearResults() {
		for(ForecastingMedicine m : forecasting.getMedicines()){
			m.getResults().clear();
		}
		for(ForecastingRegimen r : forecasting.getRegimes()){
			r.getResults().clear();
		}
	}
	/**
	 * validate forecasting data<br>
	 * This is public method, because also may use not only for calculations<br>
	 * adds errors to error list if any occurred
	 */
	public void validate() {
		// check name and dates
		this.error.clear();
		String s=forecastUI.verifyParameters();
		if (s.length()>0){
			this.error.add(new ForecastingError(ForecastingError.MAIN,s));
		}
		checkRegimensAndMedicines(); //in some cases when user change regimen set
		checkPercents(); //is percents valid ?
		checkTotalCases(); //does we have at least one case - enrolled or expected
		checkBatches();  //check batches dates
		checkGeneralWarnings();
		checkRegimensWarning();
	}

	/**
	 * get general warnings
	 */
	private void checkGeneralWarnings() {
		List<String> warns = getForecastUI().getWarnings();
		for(String s : warns){
			this.warning.add(new ForecastingError(ForecastingError.MAIN, s));
		}

	}
	/**
	 * Batches and orders expire dates can't be before the Reference Date
	 * Orders arrive dates can't be before the Reference Date
	 */
	private void checkBatches() {
		Calendar rd = this.getForecastUI().getFirstFCDate();
		List<ForecastingMedicineUIAdapter> meds = this.getForecastUI().getMedicines();
		for(ForecastingMedicineUIAdapter fmUi : meds){
			//check batches
			for(ForecastingBatchUIAdapter fbUi : fmUi.getBatchesToExpire()){
				if (fbUi.getExpired().compareTo(rd)<0 && !fbUi.isExclude()){
					this.error.add(new ForecastingError(ForecastingError.MEDICINES,fmUi.getMedicine().getNameForDisplay() +": "+
							Messages.getString("Error.Validation.BatchSave.ExpireDate")));
					return;
				}
			}
			//check orders
			for(ForecastingOrderUIAdapter foUi : fmUi.getOrders()){
				String err = foUi.validate(getForecastUI().getFirstFCDate());
				if(err.length()>0){
					this.error.add(new ForecastingError(ForecastingError.MEDICINES, fmUi.getMedicine().getNameForDisplay()+": "+err));
				}
			}
		}

	}
	/**
	 * Anyway we need at least one case - enrolled or expected
	 */
	private void checkTotalCases() {
		int tot = this.getForecastUI().getTotalEnrolled() + this.getForecastUI().getTotalExpected();
		if (tot == 0){
			if(getForecastUI().isNew()){
				this.error.add(new ForecastingError(ForecastingError.MAIN, 
						Presenter.getMessage("Forecasting.error.casesOnTreatment.allzeroNew")));
			}else{
				this.error.add(new ForecastingError(ForecastingError.MAIN, 
						Presenter.getMessage("Forecasting.error.casesOnTreatment.allzero")));
			}
		}
		if(tot > MAX_ALLOWED_CASES){
			this.error.add(new ForecastingError(ForecastingError.MAIN, 
					Presenter.getMessage("Forecasting.error.casesOnTreatment.toomany")));
		}


	}
	/**
	 * check percents rules
	 */
	private void checkPercents() {
		if (this.getForecastUI().isOnly100Allowed()){
			//only 100% or 0% in total allowed for regimens - enrolled cases
			if (this.getForecastUI().isEnrolledCasesPercents()){
				BigDecimal tot = this.forecastUI.getTotalPercentageOld();
				if (!(tot.compareTo(new BigDecimal("100.00")) == 0 ||
						tot.compareTo(new BigDecimal("0.00")) == 0)){
					this.error.add(new ForecastingError(ForecastingError.ENROLLED_CASES,
							Presenter.getMessage("Forecasting.error.percold")));
				}
			}
			//only 100% or 0% in total allowed for regimens - expected cases
			if (this.getForecastUI().isExpectedCasesPercents()){
				BigDecimal tot = this.forecastUI.getTotalPercentage();
				if (!(tot.compareTo(new BigDecimal("100.00")) == 0 ||
						tot.compareTo(new BigDecimal("0.00")) == 0)){
					this.error.add(new ForecastingError(ForecastingError.NEW_CASES,
							Presenter.getMessage("Forecasting.error.percnew")));
				}
			}



		}else{ //REALLY we don't need this check, because form validation must catch this situation
			// also, for both enrolled adn expected we are have percentage style calculation
			//not more then 100% allowed for any regimen - enrolled cases
			List<ForecastingRegimenUIAdapter> frUiList = this.getForecastUI().getRegimes();
			for(ForecastingRegimenUIAdapter frUi : frUiList){
				Float persOnTreat = frUi.getPercentCasesOnTreatment();
				Float persExpect = frUi.getPercentNewCases();
				if (persOnTreat > 100.0f || persOnTreat.toString().contains("-")){
					this.error.add(new ForecastingError(ForecastingError.ENROLLED_CASES,
							Presenter.getMessage("Error.Validation.bigpercent")+ ": " + frUi.getRegimen().getNameWithForDisplay()));
					return;
				}
				if (persExpect > 100.0f || persExpect.toString().contains("-")){
					this.error.add(new ForecastingError(ForecastingError.NEW_CASES,
							Presenter.getMessage("Error.Validation.bigpercent")+ ": " + frUi.getRegimen().getNameWithForDisplay()));
					return;
				}
			}
		}

	}
	/**
	 * Display warning, if zero cases quantity for enrolled and/or for expected cases
	 */
	private void checkRegimensWarning() {
		// for enrolled
		for(ForecastingRegimenUIAdapter fru : this.getForecastUI().getRegimes()){
			getForecastUI().getNewCasesQuantity(fru,modelFactory);
			if(getForecastUI().getOldCasesQuantity(fru,modelFactory) == 0){
				warning.add(new ForecastingError(ForecastingError.ENROLLED_CASES,
						Presenter.getMessage("Forecasting.warning.enrolledEmpty") + fru.getRegimen().getName()));
			}
		}
		// for expected
		for(ForecastingRegimenUIAdapter fru : this.getForecastUI().getRegimes()){
			getForecastUI().getNewCasesQuantity(fru,modelFactory);
			if(getForecastUI().getNewCasesQuantity(fru,modelFactory) == 0){
				warning.add(new ForecastingError(ForecastingError.NEW_CASES,
						Presenter.getMessage("Forecasting.warning.expectedEmpty") + fru.getRegimen().getName()));
			}
		}
	}


	/**
	 * Check regimens and medicines in the forecasting, rules:
	 * <ul>
	 * <li> must exist a last one regimen and
	 * <li> must be defined all medicines used in regiments and
	 * <li> must be defined only medicines used in regimes
	 * </ul>
	 */
	private void checkRegimensAndMedicines() {
		//check forecasting regimes, must be at least one
		if (this.forecasting.getRegimes().size()==0){
			this.error.add(new ForecastingError(ForecastingError.MEDICINES,
					Presenter.getMessage("Forecasting.error.regimes")));
			return; // it is not any sense to check medicines !
		}
		Set<MedicineUIAdapter> medicinesInReg = new HashSet<MedicineUIAdapter>();
		Set<MedicineUIAdapter> medicinesInFc = new HashSet<MedicineUIAdapter>();
		for(ForecastingRegimenUIAdapter r : forecastUI.getRegimes()){
			for(MedicationUIAdapter m : r.getRegimen().getIntensive().getMedications()){
				medicinesInReg.add(m.getMedicine());
			}
			for(MedicationUIAdapter m : r.getRegimen().getContinious().getMedications()){
				medicinesInReg.add(m.getMedicine());
			}
			for(PhaseUIAdapter phUi : r.getRegimen().getAddPhases()){
				for(MedicationUIAdapter m : phUi.getMedications()){
					medicinesInReg.add(m.getMedicine());
				}
			}
		}
		for(ForecastingMedicineUIAdapter fm : forecastUI.getMedicines()){
			medicinesInFc.add(fm.getMedicine());
		}
		// search for not defined
		for(MedicineUIAdapter m : medicinesInReg){
			if (!medicinesInFc.contains(m)) this.error.add(new ForecastingError(ForecastingError.MEDICINES,
					m.getName() + " " + Presenter.getMessage("Forecasting.error.medicine.notdef")));
		}
		// search for excess medicines
		for(MedicineUIAdapter m : medicinesInFc){
			if(!medicinesInReg.contains(m)) this.error.add(new ForecastingError(ForecastingError.MEDICINES,
					m.getName() + " " + Presenter.getMessage("Forecasting.error.medicine.excess")));
		}

	}

	/**
	 * lead time cannot be overlapped end of review period
	 * @return
	 */
	private boolean checkLeadTime() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(forecastUI.getReferenceDt());
		cal.add(Calendar.MONTH, forecastUI.getLeadTime());
		return cal.before(forecastUI.getEndDate());
	}
	/**
	 * @return the error
	 */
	public List<ForecastingError> getError() {
		return error;
	}
	/**
	 * Does forecasting calculation has errors
	 * @return
	 */
	public boolean hasErrors() {
		return getError().size()>0;
	}

	/**
	 * get medicines resume list from the forecasting
	 * also calculate medicine consumption - take it by getter!
	 * also calculated medicine totals - take it by getter!
	 * execute must be call before!!!
	 * @return resume for medicines or empty list
	 */
	public List<MedicineResume> getResume(){
		//prepare results lists
		medicineSummary = new ArrayList<MedicineResume>();  //for each medicine
		medicineConsumption = new ArrayList<MedicineConsumption>(); //for each medicine
		//determine borders of periods Begin review and end of lead time as previous day of begin review period
		LocalDate begReview = new LocalDate(getForecastUI().getIniDate());
		LocalDate endLT = begReview.minusDays(1);
		LocalDate lastDate = new LocalDate(forecastUI.getLastDate());
		LocalDate rd = calcFirstFCDate();
		LocalDate lastMonthCal = new LocalDate(endLT.getYear(),endLT.getMonthOfYear(),1);
		LocalDate firstMonthCal = new LocalDate(begReview.getYear(),begReview.getMonthOfYear(), begReview.dayOfMonth().getMaximumValue());
		MonthUIAdapter lastMonth = new MonthUIAdapter(Presenter.getFactory().createMonth(lastMonthCal));
		MonthUIAdapter firstMonth = new MonthUIAdapter(Presenter.getFactory().createMonth(firstMonthCal));

		for(ForecastingMedicineUIAdapter med : forecastUI.getMedicines()){
			MedicineResume mr = new MedicineResume(med.getMedicine());
			MedicineConsumption mc = new MedicineConsumption(med.getMedicine());
			medicineSummary.add(mr);
			medicineConsumption.add(mc);
			//Review periods for summary
			PeriodResume lt = new PeriodResume(rd,endLT); 					//to lead time
			PeriodResume review = new PeriodResume(begReview, lastDate);	//to review period
			PeriodResume allPeriods = new PeriodResume(rd,lastDate);		//whole
			//periods for last lead time month and first review month. It is solely for delivery schedule
			PeriodResume lastLtMonth = new PeriodResume(lastMonthCal, endLT);
			PeriodResume firstReviewMonth = new PeriodResume(begReview, firstMonthCal);
			//bind periods to medicine resume
			lt.setIncomingBalance(med.getBatchesToExpireInt()); //TODO 20151105 CHECK IT!
			mr.setLeadPeriod(lt);
			mr.setReviewPeriod(review);
			mr.setLastLeadTimeMonth(lastLtMonth);
			mr.setFirstReviewMonth(firstReviewMonth);
			//calculate periods and monthly medicine consumptions for current medicine
			for(ForecastingResultUIAdapter fr : med.getResults()){
				mr.account(fr, forecastUI.getLeadTime());
				if(allPeriods.match(fr)){ // 2016-02-06 bug in DateUtils!!!! February 28!
					mc.account(fr);
				}
			}
			//account exact months values
			mc.setExactLastLead(new ConsumptionMonth(lastLtMonth, lastMonth));
			mc.setExactFirstForecast(new ConsumptionMonth(firstReviewMonth, firstMonth));
		}
		//create medicine totals for future order
		if (validateMedicineSummaries()){
			Collections.sort(medicineSummary);
			return medicineSummary;
		}else{
			return null;
		}
	}
	/**
	 * Recalculate provisional deliveries
	 */

	public LocalDate calcFirstFCDate() {
		LocalDate rd = new LocalDate(getForecastUI().getFirstFCDate());
		return rd;
	}


	/**
	 * Medicine summaries can't contain any negative number
	 * write to the error name of this medicine
	 * @return
	 */
	private boolean validateMedicineSummaries() {
		for(MedicineResume mres : getMedicineSummary()){
			if (!validateMedicineSummary(mres)){
				this.error.add(new ForecastingError(ForecastingError.MAIN,
						mres.getMedicine().getNameForDisplayWithAbbrev()));
				return false;
			}
		}
		return true;
	}

	/**
	 * validate medicine summaries - can't contain any negative number	
	 * @param mres
	 * @return
	 */
	private boolean validateMedicineSummary(MedicineResume mres) {
		if (validatePreiod(mres.getLeadPeriod()) && validatePreiod(mres.getReviewPeriod())){
			if (mres.getQuantityToProcured().add(mres.getLeadPeriod().getMissing()).compareTo(BigDecimal.ZERO)>=0){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}


	/**
	 * Validate the review period, can't contain any negative number
	 * @param period
	 * @return
	 */
	private boolean validatePreiod(PeriodResume period) {
		if (period.getConsumedNew().compareTo(BigDecimal.ZERO) >=0 &&
				period.getConsumedOld().compareTo(BigDecimal.ZERO) >=0 &&
				period.getDispensedInt() >= 0 &&
				period.getExpired()>=0 &&
				period.getIncomingBalance()>=0 &&
				period.getMissing().compareTo(BigDecimal.ZERO)>=0 &&
				period.getTransit()>=0){
			return true;

		}else{
			return false;
		}
	}

	/**
	 * Get forecasting UI used
	 * @return
	 */
	public ForecastUIAdapter getForecastUI() {
		return forecastUI;
	}
	/**
	 * @return the medicineSummary
	 */
	public List<MedicineResume> getMedicineSummary() {
		return medicineSummary;
	}
	/**
	 * Get medicine consumption for the medicine report
	 * Must be called after getResume
	 * @return the medicineConsumption
	 */
	public ArrayList<MedicineConsumption> getMedicineConsumption() {
		Collections.sort(medicineConsumption);
		return medicineConsumption;
	}
	/**
	 * Calculate medicine consumption by months of medicine given
	 * @param med medicine
	 * @return result or empty list
	 */
	public ArrayList<PeriodResume> calcMedicineResume(MedicineUIAdapter med){
		ArrayList<PeriodResume> res = new ArrayList<PeriodResume>();
		ForecastingMedicineUIAdapter fm = forecastUI.getMedicine(med);
		if (fm != null){
			res.addAll(createEmptyPeriods());
			List<ForecastingResultUIAdapter> fRes = fm.getResults();
			for(ForecastingResultUIAdapter fr : fRes){
				for(PeriodResume pre : res){
					pre.account(fr);
				}
			}
		}
		return res;
	}

	/**
	 * Create empty periods for accounting
	 * 20160824 periods changed, Joda time implemented
	 * @return list or empty list
	 */
	private List<PeriodResume> createEmptyPeriods() {
		List<PeriodResume> res = new ArrayList<PeriodResume>(); //list for periods
		LocalDate beginPeriod = calcFirstFCDate();
		LocalDate end = new LocalDate(getForecastUI().getReviewEnd());
		while(beginPeriod.isBefore(end)){
			int periodDay = beginPeriod.dayOfMonth().getMaximumValue();
			//with an exception...
			if(Months.monthsBetween(beginPeriod, end).getMonths() == 0){
				periodDay = end.getDayOfMonth();
			}
			LocalDate endPeriod =  beginPeriod.dayOfMonth().setCopy(periodDay);
			PeriodResume pr = new PeriodResume(beginPeriod, endPeriod);
			res.add(pr);
			beginPeriod = beginPeriod.dayOfMonth().setCopy(1).plusMonths(1);
		}
		Collections.sort(res);
		return res;
	}
	/**
	 * @return the warning
	 */
	public List<ForecastingError> getWarning() {
		return warning;
	}

}
