package org.msh.quantb.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.swing.table.AbstractTableModel;

import org.msh.quantb.model.gen.RegimenTypesEnum;
import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.ForecastingRegimenUIAdapter;
import org.msh.quantb.services.io.MonthUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.view.panel.ForecastingDocumentPanel;

/**
 * Table model for forecasting regimens cases on treatment.
 * 
 * @author user
 * 
 */
public class ForecastingRegimensTableModel extends AbstractTableModel implements HasRegimenData {

	private static final long serialVersionUID = -2111904609994478109L;
	private List<ForecastingRegimenUIAdapter> data;
	private Integer columnCount;
	private ForecastingRegimenUIAdapter maxRegimen;
	private ForecastUIAdapter forecast;
	private ForecastingDocumentPanel mainTabPane;

	/**
	 * Constructor
	 * 
	 * @param _forecast current forecast
	 * @param forecastingDocumentPAnel - panel
	 */
	public ForecastingRegimensTableModel(ForecastUIAdapter _forecast, ForecastingDocumentPanel forecastingDocumentPanel) {
		this.data = _forecast.getRegimes();
		this.forecast = _forecast;
		this.mainTabPane=forecastingDocumentPanel;
	}
	

	public List<ForecastingRegimenUIAdapter> getData() {
		return data;
	}



	@Override
	public int getRowCount() {
		return data != null ? data.size() : 0;
	}

	@Override
	public int getColumnCount() {
		if (columnCount==null){
			int maxLength = 0;
			if (data != null) {
				for (ForecastingRegimenUIAdapter frui : data) {
					if (frui != null && frui.getCasesOnTreatment().size() > maxLength) {
						maxLength = frui.getCasesOnTreatment().size();
						maxRegimen = frui;
					}
				}
			}
			columnCount = new Integer(maxLength+2);
		}
		return columnCount;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		//if (data == null || rowIndex < 0 || rowIndex >= data.size() || columnIndex < 0 || (!data.isEmpty() && columnIndex > data.iterator().next().getCasesOnTreatment().size())) return null;
		int durationInMonths = 0;
		//nothing to do
		if (data == null || rowIndex < 0 || rowIndex >= data.size() || columnIndex < 0 || columnIndex > columnCount){
			return null;
		}
		//the checkbox
		if (columnIndex==0){
			return !data.get(rowIndex).isExcludeCasesOnTreatment();
		}
		//regimen (medicine) name
		if (columnIndex == 1) {
			return data.get(rowIndex).getRegimen().getNameWithForDisplay();
		}
		//may be on empty leftmost cells, not all regimes have equal lengths
		if (!data.isEmpty() && columnIndex>0){
			durationInMonths = data.get(rowIndex).getCasesOnTreatment().size();			
			if (columnIndex-2 <columnCount-2 - durationInMonths) return null;
		}		
		//real quantities
		return data.get(rowIndex).getCasesOnTreatment().get(columnIndex + durationInMonths - columnCount).getIQuantity();
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		//if (data == null || rowIndex < 0 || rowIndex >= data.size() || columnIndex <= 0 || (!data.isEmpty() && columnIndex > data.iterator().next().getCasesOnTreatment().size())) return;
		int durationInMonths = 0;
		//nothing to set
		if (data == null || rowIndex < 0 || rowIndex >= data.size() || columnIndex < 0  || columnIndex > columnCount){
			return;
		}
		// set include - exclude flag
		if(columnIndex==0){
			data.get(rowIndex).setExcludeCasesOnTreatment(!(Boolean) aValue);
			this.fireTableRowsUpdated(rowIndex, rowIndex);
			this.mainTabPane.setVisibleCalculationDetailsTabs(false);
		}
		//try to set quantities
		if (!data.isEmpty() && columnIndex>1){
			durationInMonths = data.get(rowIndex).getCasesOnTreatment().size();	
			//do not set any, on leftmost empty cells (if exists)
			if (columnIndex-2 <columnCount-2 - durationInMonths){
				return;
			}
			//really set
			try {
				int value = Integer.valueOf((String) aValue);
				data.get(rowIndex).getCasesOnTreatment().get(columnIndex + durationInMonths - columnCount).setIQuantity(new Integer(value));
				this.mainTabPane.setVisibleCalculationDetailsTabs(false);
			} catch (NumberFormatException ex) {}
		}
	}

	@Override
	public String getColumnName(int column) {
		if (data == null || data.isEmpty() || column < 0 || column > columnCount) { return null; }
		//it's include - exclude flag
		if(column==0){
			return Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.NewCases.disable");
		}
		String result = null;
		if (column > 1) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-yyyy", new Locale(Messages.getLanguage(), Messages.getCountry()));
			Calendar cal = GregorianCalendar.getInstance();
			MonthUIAdapter adapter = maxRegimen.getCasesOnTreatment().get(column - 2).getMonth();
			int year = adapter.getYear();
			int month = adapter.getMonth();
			int date = 1;
			cal.set(year, month, date);
			result = dateFormat.format(cal.getTime());
		} else {
			if (this.forecast.getRegimensType() == RegimenTypesEnum.MULTI_DRUG){
				result = Messages.getString("Regimen.clmn.Regimen");
			}else{
				result = Messages.getString("Regimen.clmn.medicines");
			}
		}
		return result;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {	
		if (columnIndex == 0){
			return true; //include - exclude flag
		}
		if (columnIndex == 1) {
			return false; // name of medicine (regime)
		}
		if (data!=null && !data.isEmpty() && rowIndex<data.size() && columnIndex!=0){
			int durationInMonths = data.get(rowIndex).getCasesOnTreatment().size();			
			if (columnIndex-2 <columnCount-2 - durationInMonths){
				return false; //leftmost empty cells (if exist)
			}else{
				return true; // quantities
			}
		}else{
			return false; //something impossible
		}
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0){
			return Boolean.class;
		}else{
			return Object.class;
		}
	}
	/**
	 * Is this row editable
	 * @param row
	 * @return
	 */
	public boolean isRowEditable(int row) {
		return !data.get(row).isExcludeCasesOnTreatment();
	}
	/**
	 * Check possibility to edit all rows
	 * @param rows array with rows numbers
	 * @return
	 */
	public boolean isRowsEditable(int[] rows) {
		boolean ret = true;
		for(int i=0; i<rows.length; i++){
			ret = isRowEditable(rows[i]) & ret;
		}
		return ret;
	}
}
