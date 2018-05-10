package org.msh.quantb.services.calc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.msh.quantb.model.forecast.ForecastingRegimen;
import org.msh.quantb.model.forecast.MonthQuantity;
import org.msh.quantb.model.gen.RegimenTypesEnum;
import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.ForecastingBatchUIAdapter;
import org.msh.quantb.services.io.ForecastingMedicineUIAdapter;
import org.msh.quantb.services.io.ForecastingOrderUIAdapter;
import org.msh.quantb.services.io.ForecastingRegimenUIAdapter;
import org.msh.quantb.services.io.RegimenUIAdapter;
import org.msh.quantb.services.io.SliceFCUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.CanShowMessages;

/**
 * Controller to merge forecasts
 * contains all necessary methods to merge
 * @author Alexey Kurasov
 *
 */
public class ForecastMergeControl {

	private List<ForecastUIAdapter> forecasts;
	private CanShowMessages messager;
	Set<RegimenUIAdapter> regimes = new HashSet<RegimenUIAdapter>();

	/**
	 * Only valid constructor
	 * @param fcToMerge list to merge
	 * @param viewFactory for messages
	 */
	public ForecastMergeControl(List<ForecastUIAdapter> fcToMerge, CanShowMessages viewFactory){
		this.forecasts = fcToMerge;
		this.messager = viewFactory;
	}



	public List<ForecastUIAdapter> getForecasts() {
		return forecasts;
	}



	public void setForecasts(List<ForecastUIAdapter> forecasts) {
		this.forecasts = forecasts;
	}



	public CanShowMessages getMessager() {
		return messager;
	}



	public void setMessager(CanShowMessages messager) {
		this.messager = messager;
	}



	public Set<RegimenUIAdapter> getRegimes() {
		return regimes;
	}



	public void setRegimes(Set<RegimenUIAdapter> regimes) {
		this.regimes = regimes;
	}



	/**
	 * validate the whole set of forecasts
	 * @return false, when at least one forecast from set cannot took part in merging
	 */
	public boolean validate() {
		if (ruleAtLeastTwo()){
			if(ruleEquialDates()){
				if(ruleEquialAlgorithm()){
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		}else{
			getMessager().showError(Messages.getString("Merge.errors.atleasttwo"));
			return false;
		}
	}

	/**
	 * For each forecast regimen's type should equal
	 * For existing and enrolled cases currently allowed only "by quantity"
	 * @return
	 */
	private boolean ruleEquialAlgorithm() {
		int multi = 0, single = 0;
		for(ForecastUIAdapter fcUi : getForecasts()){
			if(fcUi.getRegimensType() == RegimenTypesEnum.MULTI_DRUG){
				multi++;
			}else
				single++;
		}
		if(multi == getForecasts().size()){// all Forecasts RegimenTypesEnum.MULTI_DRUG
			/* if all forecasts RegimenTypesEnum.MULTI_DRUG 
			 * 1) fcUi.isEnrolledCasesPercents() and fcUi.isExpectedCasesPercents()
			 * 2) fcUi.isEnrolledCasesPercents()=false and fcUi.isExpectedCasesPercents()=false
			 */
			boolean isEnrolled = getForecasts().get(0).isEnrolledCasesPercents();
			boolean isExpected = getForecasts().get(0).isExpectedCasesPercents();
			for(ForecastUIAdapter fcUi : getForecasts()){
				if(fcUi.isEnrolledCasesPercents() != fcUi.isExpectedCasesPercents()){
					getMessager().showError(getAlgorithmError(fcUi.getName(), !fcUi.isEnrolledCasesPercents()));
					return false;
				}
				if(isEnrolled != fcUi.isEnrolledCasesPercents()){//
					getMessager().showError(getAlgorithmError(fcUi.getName(), !isEnrolled));
					return false;
				}
				if(isExpected != fcUi.isExpectedCasesPercents()){// new cases
					getMessager().showError(getAlgorithmError(fcUi.getName(), !isExpected));
					return false;
				}
			}
			if(isEnrolled && isExpected){
				// одинаковые режими и проценты по режимам
				ForecastUIAdapter first = getForecasts().get(0);
				Map<RegimenUIAdapter, Float> map = new HashMap<RegimenUIAdapter, Float>();
				Map<RegimenUIAdapter, Float> map_new = new HashMap<RegimenUIAdapter, Float>();
				
				for(ForecastingRegimenUIAdapter regim:first.getRegimes()){
					map.put(regim.getRegimen(), regim.getPercentCasesOnTreatment());
					map_new.put(regim.getRegimen(), regim.getPercentNewCases());
				}
				for(int i = 1; i < getForecasts().size(); i++){
					ForecastUIAdapter forecast = getForecasts().get(i);
					for(ForecastingRegimenUIAdapter regim:forecast.getRegimes()){
						RegimenUIAdapter reg = regim.getRegimen();
						//PercentCasesOnTreatment
						Float value = map.get(reg);
						if(value != null){
							// сравниваем значение в мапе с текущим regim.getPercentCasesOnTreatment()
							BigDecimal v = new BigDecimal(value.toString());
							v = v.setScale(2, RoundingMode.HALF_UP);
							
							BigDecimal cur = new BigDecimal(regim.getPercentCasesOnTreatment().toString());
							cur = cur.setScale(2, RoundingMode.HALF_UP);
							
							if(v.compareTo(cur) != 0){
								getMessager().showError(Messages.getString("Merge.errors.eqpersenroll"));
								return false;
							}
						}else{// not regimen in first forecast
							// режимы  лечения должны быть одинаковыми
							getMessager().showError(Messages.getString("Merge.errors.eqpersenroll"));
							return false;
						}
						//PercentNewCases
						value = map_new.get(reg);
						if(value != null){
							// сравниваем значение в мапе с текущим regim.getPercentCasesOnTreatment()
							BigDecimal v = new BigDecimal(value.toString());
							v = v.setScale(2, RoundingMode.HALF_UP);
							
							BigDecimal cur = new BigDecimal(regim.getPercentNewCases().toString());
							cur = cur.setScale(2, RoundingMode.HALF_UP);
							
							if(v.compareTo(cur) != 0){
								getMessager().showError(Messages.getString("Merge.errors.eqpersexp"));
								return false;
							}
						}else{// not regimen in first forecast
							// режимы лечения должны быть одинаковыми
							getMessager().showError(Messages.getString("Merge.errors.eqpersexp"));
							return false;
						}
					}
				}
			}
			/*for(ForecastUIAdapter fcUi : getForecasts()){
				if(fcUi.isEnrolledCasesPercents() || fcUi.isExpectedCasesPercents()){
					getMessager().showError(getAlgorithmError(fcUi.getName()));
					return false;
				}
			}*/
		}else if(single == getForecasts().size()){// all Forecasts RegimenTypesEnum.SINGLE_DRUG
			// у препаратов должны быть равные проценты
			ForecastUIAdapter first = getForecasts().get(0);
			Map<RegimenUIAdapter, Float> map = new HashMap<RegimenUIAdapter, Float>();
			Map<RegimenUIAdapter, Float> map_new = new HashMap<RegimenUIAdapter, Float>();
			
			for(ForecastingRegimenUIAdapter regim:first.getRegimes()){
				map.put(regim.getRegimen(), regim.getPercentCasesOnTreatment());
				map_new.put(regim.getRegimen(), regim.getPercentNewCases());
			}
			for(int i = 1; i < getForecasts().size(); i++){
				ForecastUIAdapter forecast = getForecasts().get(i);
				for(ForecastingRegimenUIAdapter regim:forecast.getRegimes()){
					RegimenUIAdapter reg = regim.getRegimen();
					//PercentCasesOnTreatment
					Float value = map.get(reg);
					if(value != null){
						// сравниваем значение в мапе с текущим regim.getPercentCasesOnTreatment()
						BigDecimal v = new BigDecimal(value.toString());
						v = v.setScale(2, RoundingMode.HALF_UP);
						
						BigDecimal cur = new BigDecimal(regim.getPercentCasesOnTreatment().toString());
						cur = cur.setScale(2, RoundingMode.HALF_UP);
						
						if(v.compareTo(cur) != 0){
							getMessager().showError(Messages.getString("Merge.errors.eqpersenroll"));
							return false;
						}
					}else// нет такого значения - добавляем
						map.put(reg, regim.getPercentCasesOnTreatment());
					//PercentNewCases
					value = map_new.get(reg);
					if(value != null){
						// TODO сравниваем значение в мапе с текущим regim.getPercentNewCases()
						BigDecimal v = new BigDecimal(value.toString());
						v = v.setScale(2, RoundingMode.HALF_UP);
						
						BigDecimal cur = new BigDecimal(regim.getPercentNewCases().toString());
						cur = cur.setScale(2, RoundingMode.HALF_UP);
						
						if(v.compareTo(cur) != 0){
							getMessager().showError(Messages.getString("Merge.errors.eqpersexp"));
							return false;
						}
					}else// нет такого значения - добавляем
						map_new.put(reg, regim.getPercentNewCases());
				}
			}
		}else{
			RegimenTypesEnum regType = getForecasts().get(0).getRegimensType();
			for(ForecastUIAdapter fcUi : getForecasts()){
				if(fcUi.getRegimensType() != regType){
					getMessager().showError(getRegTypeError(fcUi.getName(), regType));
					return false;
				}
			}
		}
		return true;
	}


	private String getAlgorithmError(String fcName, boolean byNumber) {
		return 
				Messages.getString("Merge.errors.impossible") + " " +
				fcName + ". " +
				Messages.getString("Merge.errors.algorithm") + " " +
				(byNumber?Messages.getString("DlgForecastingWizard.caseType.BY_NUMBER"):
					Messages.getString("DlgForecastingWizard.caseType.BY_PERCENTAGE"));
	}

	/**
	 * Simply get error message for the wrong regimen type
	 * @param fcName forecast name
	 * @param regType correct type of regimen
	 * @return
	 */
	private String getRegTypeError(String fcName, RegimenTypesEnum regType) {
		return
				Messages.getString("Merge.errors.impossible")+" "+
				fcName + ". "+
				Messages.getString("Merge.errors.regtypes") + " "+
				Messages.getString("Regimen.types."+ regType.toString());
	}



	/**
	 * Forecasts dates, lead time, buffer stock, min and max stocks should be equal
	 * @return
	 */
	private boolean ruleEquialDates() {
		Calendar referenceDate=getForecasts().get(0).getReferenceDate(); //20160825 appropriate
		Calendar iniDate=getForecasts().get(0).getIniDate();
		Calendar endDate=getForecasts().get(0).getEndDate();
		Integer bufferStockTime=getForecasts().get(0).getBufferStockTime();
		Integer leadTime=getForecasts().get(0).getLeadTime();
		Integer minStock=getForecasts().get(0).getMinStock();
		Integer maxStock=getForecasts().get(0).getMaxStock();
		for(ForecastUIAdapter fcUi : getForecasts()){
			if (!checkDates(Messages.getString("Merge.errors.rd"), referenceDate, fcUi.getReferenceDate(), fcUi.getName())){ //20160825 appropriate
				getMessager().showError(Messages.getString("Merge.errors.rdnotsame"));
				return false;
			}
			if (!checkDates(Messages.getString("Merge.errors.id"), iniDate, fcUi.getIniDate(), fcUi.getName())){
				getMessager().showError(Messages.getString("Merge.errors.ininotsame"));
				return false;
			}
			/*			if (!checkDates(Messages.getString("Merge.errors.ed"), endDate, fcUi.getEndDate(), fcUi.getName())){
				return false;
			}
			if(!checkInteger(Messages.getString("Merge.errors.bs"), bufferStockTime, fcUi.getBufferStockTime(), fcUi.getName())){
				return false;
			};*/
			/*	if(!checkInteger(Messages.getString("Merge.errors.lt"), leadTime, fcUi.getLeadTime(), fcUi.getName())){
				return false;
			};*/
			/*			if(!checkInteger(Messages.getString("Merge.errors.minS"), minStock, fcUi.getMinStock(), fcUi.getName())){
				return false;
			};
			if(!checkInteger(Messages.getString("Merge.errors.maxS"), maxStock, fcUi.getMaxStock(), fcUi.getName())){
				return false;
			};*/
		}

		return true;
	}

	/**
	 * General check integers for equial
	 * @param specError
	 * @param rightInt
	 * @param checkInt
	 * @param fcName
	 * @return
	 */
	private boolean checkInteger(String specError, Integer rightInt,
			Integer checkInt, String fcName) {
		String genError = Messages.getString("Merge.errors.impossible");
		if (rightInt.compareTo(checkInt) !=0){
			getMessager().showError(genError +" "+ fcName +". "
					+ specError + " " + rightInt);
			return false;
		}else{
			return true;
		}
	}



	/**
	 * General check dates for equial
	 * @param specError specific part of the error message
	 * @param rightDate good date
	 * @param checkDate date for check
	 * @param fcName name of forecasting
	 * @return
	 */
	public boolean checkDates(String specError, Calendar rightDate, Calendar checkDate, String fcName) {
		String genError = Messages.getString("Merge.errors.impossible");
		if (DateUtils.compareDates(rightDate, checkDate) !=0){
			/*			getMessager().showError(genError +" "+ fcName +". "
					+ specError + " " + DateUtils.formatMedium(rightDate));*/
			return false;
		}else{
			return true;
		}
	}



	/**
	 * To merge shoul be at least two forecasts
	 * @return
	 */
	private boolean ruleAtLeastTwo() {
		return getForecasts().size()>1;
	}
	/**
	 * merge set of forecasting
	 * @return
	 */
	public ForecastUIAdapter merge() {
		ForecastUIAdapter fcMerged = prepareEmpty();
		getRegimes().clear();
		for(ForecastUIAdapter fcUi : getForecasts()){
			addRegimens(fcUi, fcMerged);
		}
		prepareQuantAndMeds(fcMerged);
		fcMerged.getForecastObj().setComment("");
		Integer leadTime = -1;
		
		for(ForecastUIAdapter fcUi : getForecasts()){
			addMedicines(fcUi, fcMerged);
			boolean isAll = true;
			ForecastUIAdapter f = getForecasts().get(0);
			if(f.getRegimensType().equals(RegimenTypesEnum.SINGLE_DRUG))
				isAll = false;
			else {
				if(f.isEnrolledCasesPercents() && f.isExpectedCasesPercents())
					isAll = false;
			}
			if(isAll)
				addQuantities(fcUi, fcMerged);
			else
				addQuantitiesSINGLE(fcUi, fcMerged);

			mergeComments(fcUi, fcMerged);
			if(fcUi.getLeadTime() > leadTime){
				leadTime=fcUi.getLeadTime();
				fcMerged.getForecastObj().setLeadTime(leadTime.intValue()); //max LT
			}
		}

		return fcMerged;
	}

	private void mergeComments(ForecastUIAdapter fcUi, ForecastUIAdapter fcMerged) {
		String s = fcMerged.getComment();
		if(s==null){
			s="";
		}
		String s1 = fcUi.getComment();
		if(s1==null){
			s1="";
		}
		if(s.length()>0){
			s = s+"; " + s1;
		}else{
			s=s1;
		}
		if(s.length() > 177)
			s = s.substring(0, 177);
		
		fcMerged.getForecastObj().setComment(s);
	}



	/**
	 * Prepare empty forecast in accordance with following rules:
	 * <ul>
	 * <li> RD, InitD and Lead Time should be taken from the first FC to merge, because for all FC to merge these parameters should be equial
	 * <li> BS should be 0
	 * <li> End of FC should be date of the longest forecast (end date + BS)
	 * <li> Min and Max stocks should be 0
	 * <li> Institution, Address and comments should be empty
	 * <li> Type of regimes type - only regimes, not medicines (TODO temporary)
	 * <li> TYpe of algorithms - both by quantity (TODO next release should ask user about type of algorithms - both by quantity or both by percents
	 * or enrolled by quantity, expected by percents)) 
	 * </ul>
	 * @return empty forecast, only parameters filled
	 */
	private ForecastUIAdapter prepareEmpty() {
		ForecastUIAdapter fcUi = new ForecastUIAdapter(Presenter.getFactory().createForecasting(""));
		fcUi.setReferenceDate(getForecasts().get(0).getReferenceDate());  //checked 20160825
		fcUi.setLeadTime(DateUtils.monthsBetween(getForecasts().get(0).getFirstFCDate().getTime(), getForecasts().get(0).getIniDt()));
		fcUi.setIniDate(getForecasts().get(0).getIniDate());

		fcUi.setBufferStockTime(0);
		fcUi.setEndDate(getLatestEnd());
		fcUi.setMaxStock(0);
		fcUi.setMinStock(0);
		fcUi.setInstitution("");
		fcUi.setAddress("");
		fcUi.setComment("");
		//TODO only for first iteration
		fcUi.setRegimensType(getForecasts().get(0).getRegimensType());
		fcUi.getForecastObj().setIsOldPercents(getForecasts().get(0).getForecastObj().isIsOldPercents());
		fcUi.getForecastObj().setIsNewPercents(getForecasts().get(0).getForecastObj().isIsNewPercents());
		fcUi.setQtbVersion(Presenter.getVersion());

		return fcUi;
	}


	/**
	 * Get the most latest end of forecast - means end date plus buffer stock months 
	 * @return
	 */
	private Calendar getLatestEnd() {
		Calendar ret = getForecasts().get(0).getLastDate();
		for(ForecastUIAdapter fcUi : getForecasts()){
			Calendar tmp = fcUi.getLastDate();
			if (tmp.after(ret)){
				ret = tmp;
			}
		}
		return ret;
	}



	/**
	 * Add quantities of enroll and expected cases from the source to the target
	 * Assume that all slots for cases quantities are ready and zero 
	 * Put attention on use static methods of SliceUIAdapter
	 * @param fromFc the source
	 * @param toFc the target
	 */
	private void addQuantities(ForecastUIAdapter fromFc, ForecastUIAdapter toFc) {
		for(ForecastingRegimen srcReg : fromFc.getForecastObj().getRegimes()){
			ForecastingRegimen trgReg = SliceFCUIAdapter.findRegimen(srcReg, toFc.getForecastObj().getRegimes());
			// enrolled
			for(MonthQuantity srcMq : srcReg.getCasesOnTreatment()){
				SliceFCUIAdapter.findAndSet(srcMq, trgReg.getCasesOnTreatment());
			}
			// expected
			for(MonthQuantity srcMq : srcReg.getNewCases()){
				SliceFCUIAdapter.findAndSet(srcMq, trgReg.getNewCases());
			}
		}

	}

	/**
	 * Add quantities of enroll and expected cases from the source to the target
	 * Assume that all slots for cases quantities are ready and zero 
	 * Put attention on use static methods of SliceUIAdapter
	 * by RegimenTypesEnum.SINGLE_DRUG
	 * @param fromFc the source
	 * @param toFc the target
	 */
	private void addQuantitiesSINGLE(ForecastUIAdapter fromFc, ForecastUIAdapter toFc) {
		for(MonthQuantity srcMq : fromFc.getForecastObj().getCasesOnTreatment()){
			SliceFCUIAdapter.findAndSet(srcMq, toFc.getForecastObj().getCasesOnTreatment());
		}
		for(MonthQuantity srcMq : fromFc.getForecastObj().getNewCases()){
			SliceFCUIAdapter.findAndSet(srcMq, toFc.getForecastObj().getNewCases());
		}
	}

	/**
	 * For each medicine batch in source forecast add batch to target
	 * Then do same with orders
	 * Equal batches should be merged
	 * Generate error if medicine not found - should be!
	 * @param fromFc
	 * @param toFc
	 */
	private void addMedicines(ForecastUIAdapter fromFc, ForecastUIAdapter toFc) {
		for(ForecastingMedicineUIAdapter srcFcMed : fromFc.getMedicines()){
			ForecastingMedicineUIAdapter trgFcMed = toFc.getMedicine(srcFcMed.getMedicine());
			addBatches(srcFcMed, trgFcMed);
			addOrders(srcFcMed, trgFcMed);
		}
	}

	/**
	 * Add or merge orders from the source to the target
	 * Merge only - same order, same batch
	 * @param srcFcMed source
	 * @param trgFcMed target
	 */
	private void addOrders(ForecastingMedicineUIAdapter srcFcMed,
			ForecastingMedicineUIAdapter trgFcMed) {
		for(ForecastingOrderUIAdapter fOuI : srcFcMed.getOrders()){
			trgFcMed.addOrMergeOrder(fOuI);
		}

	}



	/**
	 * Add or merge batches from the source to the target
	 * @param srcFcMed source
	 * @param trgFcMed target
	 */
	private void addBatches(ForecastingMedicineUIAdapter srcFcMed, ForecastingMedicineUIAdapter trgFcMed) {
		for(ForecastingBatchUIAdapter fBuI: srcFcMed.getBatchesToExpire()){
			trgFcMed.addOrMergeBatch(fBuI);
		}
	}


	/**
	 * Prepare zero slots for cases quantity and zero slots for medicines
	 * @param fcMerged
	 */
	private void prepareQuantAndMeds(ForecastUIAdapter fcMerged) {
		Presenter.refreshMedicinesInFc(fcMerged); //create medicines list
		//create enrolled cases with zero quantities
		fcMerged.shiftOldCasesRegimens();
		fcMerged.shiftOldCasesPercents();
		//create expected cases with zero quantities
		fcMerged.shiftNewCasesRegimens();
		fcMerged.shiftNewCasesPercents();	
	}



	/**
	 * only add regimen from the source to the target to the forecast
	 * no duplication
	 * @param fromFc source forecast
	 * @param toFc target forecast
	 */
	private void addRegimens(ForecastUIAdapter fromFc, ForecastUIAdapter toFc) {
		for(ForecastingRegimenUIAdapter frUi : fromFc.getRegimes()){
			RegimenUIAdapter rUi = frUi.getRegimen().makeClone();
			rUi.setName(frUi.getRegimen().getName());
			if (!getRegimes().contains(rUi)){
				getRegimes().add(rUi);
				ForecastingRegimen fr = Presenter.getFactory().createForecastingRegimen(rUi.getRegimen());
				toFc.getForecastObj().getRegimes().add(fr);
				fr.setPercentCasesOnTreatment(frUi.getFcRegimenObj().getPercentCasesOnTreatment());
				fr.setPercentNewCases(frUi.getFcRegimenObj().getPercentNewCases());
			}
		}
	}





}
