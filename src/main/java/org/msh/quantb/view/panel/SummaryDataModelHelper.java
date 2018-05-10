package org.msh.quantb.view.panel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.msh.quantb.services.calc.ConsumptionMonth;
import org.msh.quantb.services.calc.DateUtils;
import org.msh.quantb.services.calc.MedicineConsumption;
import org.msh.quantb.services.calc.MedicineResume;
import org.msh.quantb.services.calc.OrderCalculator;
import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.ForecastingTotalMedicine;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.view.tableExt.DivMultiLineCellRenderer;

/**
 * This class solely responsible for summary table data model
 * Uses for build summary pane and corresponding Excel sheet
 * @author Alexey Kurasov
 *
 */
public class SummaryDataModelHelper {
	public static final int ACCEL_TOTAL_COLUMN = 11;
	public static final int REGULAR_TOTAL_COLUMN=12;
	public static final int TOTAL_COLUMN=13;
	int[] medicineNameSize; //to calculate height of row
	private Object[][] data; //data itself
	ForecastUIAdapter forecast;
	private List<MedicineResume> resMed;
	private List<MedicineConsumption> mCons;
	private OrderCalculator oCalc;
	private DefaultTableModel tableModel;

	/**
	 * Calculate the data model
	 * @param fui forecast
	 * @param _resMed medicines resume
	 * @param _mCons medicines consumptions
	 * @param _oCalc order calculator
	 */
	public SummaryDataModelHelper(ForecastUIAdapter fui,
			List<MedicineResume> _resMed, List<MedicineConsumption> _mCons, OrderCalculator _oCalc) {
		forecast=fui;
		resMed = _resMed;
		mCons = _mCons;
		oCalc = _oCalc;
		data = new Object[resMed.size()][15];
		medicineNameSize = new int[resMed.size()];
		recalcModel();
	}

	/**
	 * Recalculate the model
	 */
	public void recalcModel() {
		int index = 0;
		for (MedicineResume med : resMed) {								
			data[index][0] = med.getMedicine().getNameForDisplay();
			medicineNameSize[index] = String.valueOf(data[index][0]).length();
			data[index][1] = med.getLeadPeriod().getIncomingBalance();
			setMonthsOfStock(index, med, mCons);
			data[index][3] = med.getLeadPeriod().getTransit();
			data[index][4] = med.getLeadPeriod().getDispensedInt();
			data[index][5] = med.getLeadPeriod().getExpired();
			data[index][6] = med.getReviewPeriod().getIncomingBalance();
			data[index][7] = med.getReviewPeriod().getTransit();
			data[index][8] = med.getReviewPeriod().getExpired();
			data[index][9] = med.getReviewPeriod().getConsumedOldInt();
			data[index][10] = med.getReviewPeriod().getConsumedNewInt();
			ForecastingTotalMedicine medTotalA = oCalc.getTotalA().fetchMedicineTotal(med.getMedicine());
			if(medTotalA != null){
				data[index][ACCEL_TOTAL_COLUMN] = medTotalA.getAdjustAccel();
			}else{
				data[index][ACCEL_TOTAL_COLUMN] = 0;
			}
			ForecastingTotalMedicine medTotal = oCalc.getTotalR().fetchMedicineTotal(med.getMedicine());
			if(medTotal != null){
				data[index][REGULAR_TOTAL_COLUMN] = medTotal.getAdjustedRegular();
			}else{
				data[index][REGULAR_TOTAL_COLUMN] = 0;
			}
			if(medTotalA != null && medTotal != null){
				data[index][TOTAL_COLUMN] = medTotal.getTotal();
			}else{
				data[index][TOTAL_COLUMN] = 0;
			}
			index++;
		}
		
	}


	/**
	 * Array of medicines names size, important for row height
	 * @return
	 */
	public int[] getMedicineNameSize() {
		return medicineNameSize;
	}


	/**
	 * Data model for the table
	 * @return
	 */
	public Object[][] getData() {
		return data;
	}



	/**
	 * Calculate and set month of stock for particular medicine
	 * @param index row in data table that correspond with medicine
	 * @param med medicine result
	 * @param mCons  medicine consumption
	 */
	private void setMonthsOfStock(int index, MedicineResume med, List<MedicineConsumption> mCons) {
		int stockMonths = 0;
		String suffix = "";	
		for(MedicineConsumption mc : mCons){
			if (mc.getMed().equals(med.getMedicine())){
				//sorted by month
				int onHand = mc.getCons().get(0).getOnHandInt();
				for(ConsumptionMonth cm : mc.getCons()){
					if(cm.getConsAllInt()>0 && cm.getMissingInt() == 0){
						onHand -=(cm.getConsAllInt()+cm.getExpired());
						if (onHand >=0){
							stockMonths++;
						}else{
							break;
						}
					}
				}
			}
		}
		//stock months can't be negative
		if (stockMonths < 0){
			stockMonths =0;
		}
		//set color
		if(stockMonths <= this.forecast.getMinStock() ||  stockMonths >= this.forecast.getMaxStock()){
			suffix = DivMultiLineCellRenderer.COLOR;
		}
		data[index][2] = new Integer(stockMonths).toString() + suffix;
	}

	/**
	 * Print date as end of forecasting period minus Lead Time
	 * @param fui
	 * @param index
	 */
	private void printAfterDate(ForecastUIAdapter fui, int index) {
		//data[index][3] = Messages.getString("ForecastingDocumentWindow.tbSummary.after")+
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(fui.getEndDt());
		cal.add(Calendar.MONTH,fui.getLeadTime()*-1);
		data[index][3] = DateUtils.formatDate(cal.getTime(),"MMM dd, yyyy");
	}
	/**
	 * Set table for this data
	 * @param _tableModel - model for table to run data change event!
	 */
	public void setTableModel(DefaultTableModel _tableModel) {
		tableModel = _tableModel;;
		
	}

	public List<MedicineResume> getResMed() {
		return resMed;
	}
	
	
}
