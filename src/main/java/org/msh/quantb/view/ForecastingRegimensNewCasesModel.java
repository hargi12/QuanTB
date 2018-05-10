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
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.panel.ForecastingDocumentPanel;

/**
 * Table model for forecasting regimens new cases
 * 
 * @author user
 * 
 */
public class ForecastingRegimensNewCasesModel extends AbstractTableModel implements HasRegimenData {

	private static final long serialVersionUID = -43346283695655683L;
	private List<ForecastingRegimenUIAdapter> data;
	private Integer columnCount;
	private ForecastUIAdapter forecast;
	private ForecastingDocumentPanel mainTabPane;

	/**
	 * Constructor
	 * 
	 * @param fcUI data for table
	 * @param forecastingDocumentPanel 
	 */
	public ForecastingRegimensNewCasesModel(ForecastUIAdapter fcUI, ForecastingDocumentPanel forecastingDocumentPanel) {
		this.forecast = fcUI;
		this.data = fcUI.getRegimes();
		this.mainTabPane = forecastingDocumentPanel;
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
				if(!data.isEmpty()){
					maxLength = data.get(0).getNewCases().size();
				}
			}
			columnCount = new Integer(maxLength+2);
		}
		return columnCount;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (data == null || rowIndex < 0 || rowIndex >= data.size() || columnIndex < 0 || columnIndex > columnCount) return null;
		if(data.isEmpty()){
			return null;
		}
		if (columnIndex==0){
			return !data.get(rowIndex).isExcludeNewCases(); //20151013
		}
		if (columnIndex == 1) {
			return data.get(rowIndex).getRegimen().getNameWithForDisplay();
		} else {			
			return data.get(rowIndex).getNewCases().get(columnIndex-2).getIQuantity();
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (data == null || rowIndex < 0 || rowIndex >= data.size() || columnIndex < 0  || columnIndex > columnCount) return;
		//if (columnIndex > 0) { //20151013
		if(columnIndex > 1){
			try {
				int value = Integer.valueOf((String) aValue);
				//data.get(rowIndex).getNewCases().get(columnIndex-1).setIQuantity(value); 20151013
				data.get(rowIndex).getNewCases().get(columnIndex-2).setIQuantity(value);
				this.mainTabPane.setVisibleCalculationDetailsTabs(false);
			} catch (NumberFormatException ex) {}
		}else if (columnIndex == 0){
			data.get(rowIndex).setExcludeNewCases(!(Boolean) aValue);
			this.fireTableRowsUpdated(rowIndex, rowIndex);
			this.mainTabPane.setVisibleCalculationDetailsTabs(false);
		}
	}

	@Override
	public String getColumnName(int column) {
		if (data == null || data.isEmpty() || column < 0 || column > columnCount) { return null; }
		if(column==0){
			return Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.NewCases.disable");
		}
		String result = null;
		//if (column > 0) { 20151013
		if(column>1){
			SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-yyyy", new Locale(Messages.getLanguage(), Messages.getCountry()));
			Calendar cal = GregorianCalendar.getInstance();
			//MonthUIAdapter adapter = data.get(0).getNewCases().get(column - 1).getMonth(); 20151013
			MonthUIAdapter adapter = data.get(0).getNewCases().get(column - 2).getMonth();
			int year = adapter.getYear();
			int month = adapter.getMonth();
			int date = 1;
			cal.set(year, month, date);
			result = dateFormat.format(cal.getTime());
			if (Presenter.monthInBuffer(adapter, this.forecast)){
				result = result + "*";
			}
		} else
			if(column == 1){
				if(this.forecast.getRegimensType() == RegimenTypesEnum.MULTI_DRUG){
					result = Messages.getString("Regimen.clmn.Regimen");
				}else{
					result = Messages.getString("Regimen.clmn.medicines");
				}
			}else{
				return "";
			}
		return result;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {	
		//if (columnIndex == 0) return false; //20151013
		if (columnIndex == 1) {
			return false;
		}
		if(!isRowEditable(rowIndex) && columnIndex != 0){
			return false;
		}
		return true;
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
		return !data.get(row).isExcludeNewCases();
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

	@Override
	public List<ForecastingRegimenUIAdapter> getData() {
		return data;
	}


}
