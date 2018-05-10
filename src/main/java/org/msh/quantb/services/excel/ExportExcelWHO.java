package org.msh.quantb.services.excel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jxl.write.WritableSheet;
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
 * Export to the Excel denormalized data for WHO BI system
 * @author alexey
 *
 */
public class ExportExcelWHO extends JXLProcessor {

	private List<MedicineConsumption> cons;
	private ForecastUIAdapter fcU;
	private WritableSheet sheet;

	/**
	 * Create WHO report based on calculation results
	 * @param calc
	 * @throws IOException 
	 * @throws WriteException 
	 */
	public void createReport(ForecastingCalculation calc) throws WriteException, IOException {
		//prepare Excel
		createWorkBook();
		//prepare data
		fcU = calc.getForecastUI();
		calc.getResume(); // calculate medicine consumptions
		List<RegimenMonthResult> result = buildMonthRegimen();
		cons = calc.getMedicineConsumption();
		// iterate on results
		int i =1;
		for(RegimenMonthResult rmr : result){
			// Intensive phase
			i = outPhaseResults(rmr, true, i);
			// Continuous phase
			i = outPhaseResults(rmr, false, i);
		}
	}

	/**
	 * Fill the forecasting related fields
	 * @param i
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	private void fillFcFields(int i) throws RowsExceededException, WriteException {
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
		addDate(sheet, 0, i, fcU.getForecastObj().getRecordingDate().toGregorianCalendar().getTime());
		addLabel(sheet, 1, i, fcU.getName());
		addLabel(sheet, 2, i, country);
		addLabel(sheet, 3, i, reg);
		addLabel(sheet, 4, i, fac);
		addLabel(sheet, 5, i, fcU.getCalculator());
		addInteger(sheet, 6, i, fcU.getLeadTime());
		addDate(sheet, 7, i, fcU.getIniDt());
		addDate(sheet, 8, i, fcU.getEndDt());
		addInteger(sheet, 9, i, fcU.getBufferStockTime());
		addInteger(sheet, 10, i, fcU.getMinStock());
		addInteger(sheet, 11, i, fcU.getMaxStock());
		addDate(sheet, 16, i, fcU.getReferenceDt()); //20160825 appropriate
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
	private int outPhaseResults(RegimenMonthResult rmr, boolean isIntensive, int row) throws RowsExceededException, WriteException {
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
			addDateMY(sheet, 17, row, rmr.getResult().getFromDate().getTime()); // date of the result
			addLabel(sheet, 27, row, rmr.getRegimen().getRegimen().getNameWithForDisplay()); // regimen name
			addInteger(sheet, 28, row, regEnrollCases); // enrolled for regimen
			addInteger(sheet, 29, row, regExpCases); // expected for regimen
			addLabel(sheet, 26, row, phaseName); // phase name
			addLabel(sheet, 12, row, mUi.getMedicine().getName()); // medicine name
			addLabel(sheet, 13, row, mUi.getMedicine().getAbbrevName());
			addLabel(sheet, 14, row, mUi.getMedicine().getStrength()); // medicine strength
			addLabel(sheet, 15, row, mUi.getMedicine().getDosage()); // medicine dosage form
			addInteger(sheet, 30, row,mUi.getDosage()); // medicine doses per day
			addInteger(sheet, 31, row,mUi.getDaysPerWeek()); // medicines days per week
			addInteger(sheet, 32, row,mUi.getDuration()); // duration in months
			if (cM != null){
				addInteger(sheet, 18, row, cM.getOnHand().intValue()); // medicines on hand
				addInteger(sheet, 19, row, cM.getMissing().intValue()); // medicines needed
				addInteger(sheet, 20, row, cM.getExpired()); // medicines expired
				addInteger(sheet, 21, row, cM.getOrder()); // medicines expired
				addInteger(sheet, 22, row, cM.getConsOld().intValue() ); // medicines consumption enrolled
				addInteger(sheet, 23, row, cM.getConsNew().intValue()); // medicines consumption expected
				addInteger(sheet, 24, row, cM.getOldCases().intValue()); // medicines cases enrolled 
				addInteger(sheet, 25, row, cM.getNewCases().intValue()); // medicines cases expected 
			}
			row++;
		}
		return row;
	}

	/**
	 * Create new workbook and paint the header
	 * @throws IOException 
	 * @throws WriteException 
	 */
	private void createWorkBook() throws WriteException, IOException {
		sheet = this.getWorkbook().createSheet(Presenter.getMessage("WHO.title"), 0);
		addCaption(sheet, 0, 0, Messages.getString("WHO.headers.fcDate"), 15);
		addCaption(sheet, 1, 0, Messages.getString("WHO.headers.fcName"), 35);
		addCaption(sheet, 2, 0, Messages.getString("WHO.headers.fcCountry"), 15);
		addCaption(sheet, 3, 0, Messages.getString("WHO.headers.region"), 15);
		addCaption(sheet, 4, 0, Messages.getString("WHO.headers.facility"), 15);
		addCaption(sheet, 5, 0, Messages.getString("WHO.headers.person"), 15);
		addCaption(sheet, 6, 0, Messages.getString("WHO.headers.lead"), 15);
		addCaption(sheet, 7, 0, Messages.getString("WHO.headers.fcStart"), 15);
		addCaption(sheet, 8, 0, Messages.getString("WHO.headers.fcEnd"), 15);
		addCaption(sheet, 9, 0, Messages.getString("WHO.headers.buffer"), 15);
		addCaption(sheet, 10, 0, Messages.getString("WHO.headers.minStock"), 15);
		addCaption(sheet, 11, 0, Messages.getString("WHO.headers.maxStock"), 15);
		addCaption(sheet, 12, 0, Messages.getString("WHO.headers.INName"), 35);
		addCaption(sheet, 13, 0, Messages.getString("WHO.headers.abbrName"), 35);
		addCaption(sheet, 14, 0, Messages.getString("WHO.headers.strength"), 15);
		addCaption(sheet, 15, 0, Messages.getString("WHO.headers.dosageForm"), 15);
		addCaption(sheet, 16, 0, Messages.getString("WHO.headers.RD"), 15);
		addCaption(sheet, 17, 0, Messages.getString("WHO.headers.date"), 15);
		addCaption(sheet, 18, 0, Messages.getString("WHO.headers.onHand"), 15);
		addCaption(sheet, 19, 0, Messages.getString("WHO.headers.needed"), 15);
		addCaption(sheet, 20, 0, Messages.getString("WHO.headers.expire"), 15);
		addCaption(sheet, 21, 0, Messages.getString("WHO.headers.onOrder"), 15);
		addCaption(sheet, 22, 0, Messages.getString("WHO.headers.consEnrolled"), 15);
		addCaption(sheet, 23, 0, Messages.getString("WHO.headers.consExpected"), 15);
		addCaption(sheet, 24, 0, Messages.getString("WHO.headers.medCasesEnroll"), 15);
		addCaption(sheet, 25, 0, Messages.getString("WHO.headers.medicineCasesExpected"), 15);
		addCaption(sheet, 26, 0, Messages.getString("WHO.headers.treatmentPhase"), 35);
		addCaption(sheet, 27, 0, Messages.getString("WHO.headers.regimen"), 35);
		addCaption(sheet, 28, 0, Messages.getString("WHO.headers.regimenEnroll"), 15);
		addCaption(sheet, 29, 0, Messages.getString("WHO.headers.regimenExpected"), 15);
		addCaption(sheet, 30, 0, Messages.getString("WHO.headers.dosesPerDay"), 15);
		addCaption(sheet, 31, 0, Messages.getString("WHO.headers.daysPerWeek"), 15);
		addCaption(sheet, 32, 0, Messages.getString("WHO.headers.duration"), 15);

	}


	/**
	 * Get on hand, needed, expired and on order
	 * @param month month to fetch
	 * @param medicine medicine to fetch
	 * @return ConsumptionMonth or null if not found
	 */
	private ConsumptionMonth fetchMonthConsumption(MonthUIAdapter month,
			MedicineUIAdapter medicine) {
		ConsumptionMonth res = null;
		for(MedicineConsumption mc : cons){
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

}
