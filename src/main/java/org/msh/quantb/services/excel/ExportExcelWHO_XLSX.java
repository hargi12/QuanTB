package org.msh.quantb.services.excel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.msh.quantb.services.calc.ConsumptionMonth;
import org.msh.quantb.services.calc.ForecastingCalculation;
import org.msh.quantb.services.calc.MedicineConsumption;
import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.ForecastingRegimenResultUIAdapter;
import org.msh.quantb.services.io.ForecastingRegimenUIAdapter;
import org.msh.quantb.services.io.MedicationUIAdapter;
import org.msh.quantb.services.io.MedicineUIAdapter;
import org.msh.quantb.services.io.MonthUIAdapter;
import org.msh.quantb.services.io.PhaseUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;

/**
 * Create WHO report based on calculation results
 * @author Irina
 *
 */
public class ExportExcelWHO_XLSX {

	private POIProcessor processor;
	private List<MedicineConsumption> medicineCons;
	private ForecastUIAdapter fcU;
	
	public ExportExcelWHO_XLSX(File excelFile) {
		processor = new POIProcessor(excelFile);
	}

	/**
	 * Save result
	 * @throws IOException 
	 */
	public void save() throws IOException {
		processor.save();
	}
	
	public void createReport(ForecastingCalculation calc){
		createHeaders();
		
		fcU = calc.getForecastUI();
		// calculate medicine consumptions
		calc.getResume();
		List<RegimenMonthResult> result = buildMonthRegimen();
		medicineCons = calc.getMedicineConsumption();
		// iterate on results
		int i = 1;
		for(RegimenMonthResult rmr : result){
			// Intensive phase
			i = outPhaseResults(rmr, true, i);
			// Continuous phase
			i = outPhaseResults(rmr, false, i);
		}
	}
	
	/**
	 * Output information for date-regimen-phase-medicines
	 * @param rmr
	 * @param isIntensive what is the phase
	 * @param row line from
	 * @return next free row number
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	private int outPhaseResults(RegimenMonthResult rmr, boolean isIntensive, int row) {
		String phaseName = "";
		PhaseUIAdapter pUi = null;
		if(isIntensive){
			phaseName = Messages.getString("Regimen.phase.intensive");
			pUi = rmr.getRegimen().getRegimen().getIntensive();
		}else{
			phaseName = Messages.getString("Regimen.phase.continious");
			pUi = rmr.getRegimen().getRegimen().getContinious();
		}

		Integer regEnrollCases = 0;
		Integer regExpCases = 0;
		for(MedicationUIAdapter mUi : pUi.getMedications()){
			ConsumptionMonth cM = fetchMonthConsumption(rmr.getResult().getMonth(), mUi.getMedicine());
			if(isIntensive){
				regEnrollCases = rmr.getResult().getIntensive().getOldCases().intValue();
				regExpCases = rmr.getResult().getIntensive().getNewCases().intValue();
			}else{
				regEnrollCases = rmr.getResult().getContinious().getOldCases().intValue();
				regExpCases = rmr.getResult().getContinious().getNewCases().intValue();
			}
			//Excel output
			fillFcFields(row); // all the forecasting information 
			processor.addDateMY(17, row, rmr.getResult().getFromDate().getTime()); // date of the result
			processor.addLabel(27, row, rmr.getRegimen().getRegimen().getNameWithForDisplay()); // regimen name
			processor.addInteger(28, row, regEnrollCases); // enrolled for regimen
			processor.addInteger(29, row, regExpCases); // expected for regimen
			processor.addLabel(26, row, phaseName); // phase name
			processor.addLabel(12, row, mUi.getMedicine().getName()); // medicine name
			processor.addLabel(13, row, mUi.getMedicine().getAbbrevName());
			processor.addLabel(14, row, mUi.getMedicine().getStrength()); // medicine strength
			processor.addLabel(15, row, mUi.getMedicine().getDosage()); // medicine dosage form
			processor.addInteger(30, row,mUi.getDosage()); // medicine doses per day
			processor.addInteger(31, row,mUi.getDaysPerWeek()); // medicines days per week
			processor.addInteger(32, row,mUi.getDuration()); // duration in months
			if (cM != null){
				processor.addInteger(18, row, cM.getOnHand().intValue()); // medicines on hand
				processor.addInteger(19, row, cM.getMissing().intValue()); // medicines needed
				processor.addInteger(20, row, cM.getExpired()); // medicines expired
				processor.addInteger(21, row, cM.getOrder()); // medicines expired
				processor.addInteger(22, row, cM.getConsOld().intValue() ); // medicines consumption enrolled
				processor.addInteger(23, row, cM.getConsNew().intValue()); // medicines consumption expected
				processor.addInteger(24, row, cM.getOldCases().intValue()); // medicines cases enrolled 
				processor.addInteger(25, row, cM.getNewCases().intValue()); // medicines cases expected 
			}
			row++;
		}
		return row;
	}
	
	/**
	 * Fill the forecasting related fields
	 * @param i
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	private void fillFcFields(int i){
		//parse address
		String addr = fcU.getAddress();
		String[] acomp = addr.split("/");
		String country = "";
		String reg = "";
		String fac = "";
		if (acomp.length == 3){
			country = acomp[0];
			reg = acomp[1];
			fac = acomp[2];
		}
		if(acomp.length == 2){
			country = acomp[0];
			reg = acomp[1];
		}
		if(acomp.length == 1){
			country = acomp[0];
		}
		processor.addDate(0, i, fcU.getForecastObj().getRecordingDate().toGregorianCalendar().getTime());
		processor.addLabel(1, i, fcU.getName());
		processor.addLabel(2, i, country);
		processor.addLabel(3, i, reg);
		processor.addLabel(4, i, fac);
		processor.addLabel(5, i, fcU.getCalculator());
		processor.addInteger(6, i, fcU.getLeadTime());
		processor.addDate(7, i, fcU.getIniDt());
		processor.addDate(8, i, fcU.getEndDt());
		processor.addInteger(9, i, fcU.getBufferStockTime());
		processor.addInteger(10, i, fcU.getMinStock());
		processor.addInteger(11, i, fcU.getMaxStock());
		processor.addDate(16, i, fcU.getReferenceDt()); //20160825 appropriate
	}
	
	/**
	 * Build regimen results sorted by the month
	 * @return list of the regimen result sorted by month
	 */
	private List<RegimenMonthResult> buildMonthRegimen() {
		List<ForecastingRegimenUIAdapter> reg = fcU.getRegimes();
		//build lines by month
		List<RegimenMonthResult> result = new ArrayList<RegimenMonthResult>();
		// iterate on regimes
		for (ForecastingRegimenUIAdapter r : reg) {
			//inside regimes - year_month
			for (ForecastingRegimenResultUIAdapter rr : r.getMonthsResults(fcU.getFirstFCDate(), Presenter.getFactory())){
				result.add(new RegimenMonthResult(r,rr));
			}
		}
		// sorting results by year and month
		Collections.sort(result);
		return result;
	}
	
	/**
	 * Get on hand, needed, expired and on order
	 * @param month month to fetch
	 * @param medicine medicine to fetch
	 * @return ConsumptionMonth or null if not found
	 */
	private ConsumptionMonth fetchMonthConsumption(MonthUIAdapter month, MedicineUIAdapter medicine) {
		ConsumptionMonth res = null;
		for(MedicineConsumption mc : medicineCons){
			if (mc.getMed().equals(medicine)){
				for(ConsumptionMonth cM : mc.getCons()){
					if (cM.getMonth().equals(month)){
						res = cM;
						break;
					}
				}
				break;
			}
		}
		return res;
	}
	
	/**
	 * Create new workbook and paint the header
	 */
	private void createHeaders(){
		processor.createSheet(Presenter.getMessage("WHO.title"), 0);
		processor.addCaption(0, 0, Messages.getString("WHO.headers.fcDate"), 15);
		processor.addCaption(1, 0, Messages.getString("WHO.headers.fcName"), 35);
		processor.addCaption(2, 0, Messages.getString("WHO.headers.fcCountry"), 15);
		processor.addCaption(3, 0, Messages.getString("WHO.headers.region"), 15);
		processor.addCaption(4, 0, Messages.getString("WHO.headers.facility"), 15);
		processor.addCaption(5, 0, Messages.getString("WHO.headers.person"), 15);
		processor.addCaption(6, 0, Messages.getString("WHO.headers.lead"), 15);
		processor.addCaption(7, 0, Messages.getString("WHO.headers.fcStart"), 15);
		processor.addCaption(8, 0, Messages.getString("WHO.headers.fcEnd"), 15);
		processor.addCaption(9, 0, Messages.getString("WHO.headers.buffer"), 15);
		processor.addCaption(10, 0, Messages.getString("WHO.headers.minStock"), 15);
		processor.addCaption(11, 0, Messages.getString("WHO.headers.maxStock"), 15);
		processor.addCaption(12, 0, Messages.getString("WHO.headers.INName"), 35);
		processor.addCaption(13, 0, Messages.getString("WHO.headers.abbrName"), 35);
		processor.addCaption(14, 0, Messages.getString("WHO.headers.strength"), 15);
		processor.addCaption(15, 0, Messages.getString("WHO.headers.dosageForm"), 15);
		processor.addCaption(16, 0, Messages.getString("WHO.headers.RD"), 15);
		processor.addCaption(17, 0, Messages.getString("WHO.headers.date"), 15);
		processor.addCaption(18, 0, Messages.getString("WHO.headers.onHand"), 15);
		processor.addCaption(19, 0, Messages.getString("WHO.headers.needed"), 15);
		processor.addCaption(20, 0, Messages.getString("WHO.headers.expire"), 15);
		processor.addCaption(21, 0, Messages.getString("WHO.headers.onOrder"), 15);
		processor.addCaption(22, 0, Messages.getString("WHO.headers.consEnrolled"), 15);
		processor.addCaption(23, 0, Messages.getString("WHO.headers.consExpected"), 15);
		processor.addCaption(24, 0, Messages.getString("WHO.headers.medCasesEnroll"), 15);
		processor.addCaption(25, 0, Messages.getString("WHO.headers.medicineCasesExpected"), 15);
		processor.addCaption(26, 0, Messages.getString("WHO.headers.treatmentPhase"), 35);
		processor.addCaption(27, 0, Messages.getString("WHO.headers.regimen"), 35);
		processor.addCaption(28, 0, Messages.getString("WHO.headers.regimenEnroll"), 15);
		processor.addCaption(29, 0, Messages.getString("WHO.headers.regimenExpected"), 15);
		processor.addCaption(30, 0, Messages.getString("WHO.headers.dosesPerDay"), 15);
		processor.addCaption(31, 0, Messages.getString("WHO.headers.daysPerWeek"), 15);
		processor.addCaption(32, 0, Messages.getString("WHO.headers.duration"), 15);
	}
}
