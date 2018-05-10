package org.msh.quantb.view;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.ForecastingRegimenUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.view.panel.ForecastingDocumentPanel;

/**
 * Table model for forecasting regimens new cases in percents mode
 * @author Alexey Kurasov
 *
 */
public class ForecastingRegimensNewCasesModelPers extends AbstractTableModel implements HasRegimenData {

	private static final long serialVersionUID = 1L;
	/**
	 * Constructor
	 * 
	 * @param fcUI data for table
	 * @param forecastingDocumentPanel 
	 */
	private List<ForecastingRegimenUIAdapter> data;
	private ForecastingDocumentPanel mainTabPane;
	
	/**
	 * Constructor
	 * @param fcUI data for table
	 * @param forecastingDocumentPanel 
	 */
	public ForecastingRegimensNewCasesModelPers(ForecastUIAdapter fcUI, ForecastingDocumentPanel forecastingDocumentPanel) {
		this.data = fcUI.getRegimes();
		this.mainTabPane = forecastingDocumentPanel;
	}

	@Override
	public int getRowCount() {
		return data != null ? data.size() : 0;
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (data == null || rowIndex < 0 || rowIndex >= data.size() || columnIndex < 0 || columnIndex > getColumnCount()) return null;
		if(data.isEmpty()){
			return null;
		}
		if (columnIndex==0){
			return !data.get(rowIndex).isExcludeNewCases(); //20151013
		}
		if (columnIndex == 1) {
			return data.get(rowIndex).getRegimen().getNameWithForDisplay();
		} else {			
			return data.get(rowIndex).getPercentNewCases();
		}
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (data == null || rowIndex < 0 || rowIndex >= data.size() || columnIndex < 0  || columnIndex > getColumnCount()) return;
		if(columnIndex == 0){
			data.get(rowIndex).setExcludeNewCases(!(Boolean) aValue);
			this.fireTableRowsUpdated(rowIndex, rowIndex);
			this.mainTabPane.setVisibleCalculationDetailsTabs(false);
		}
		if(columnIndex == 2){
			data.get(rowIndex).setPercentNewCases((Float) aValue);
			this.mainTabPane.setVisibleCalculationDetailsTabs(false);
		}
	}

	/**
	 * Ww will do it after
	 */
	@Override
	public String getColumnName(int column) {
		if (data == null || data.isEmpty() || column < 0 || column > getColumnCount()) { return null; }
		if(column==0){
			return "<html>" + Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.NewCases.disable") + "<br>&nbsp</html>";
		}
		return "";
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
