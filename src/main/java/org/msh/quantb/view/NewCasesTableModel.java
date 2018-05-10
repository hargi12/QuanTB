package org.msh.quantb.view;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.msh.quantb.services.io.MonthQuantityUIAdapter;
import org.msh.quantb.services.io.MonthUIAdapter;
import org.msh.quantb.view.panel.ForecastingDocumentPanel;

/**
 * Table model for table of estimated number of new cases in forecasting document
 * @author User
 */
public class NewCasesTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -4518322477968640650L;
	private List<MonthQuantityUIAdapter> data;
	private ForecastingDocumentPanel mainTabPane;

	public NewCasesTableModel(List<MonthQuantityUIAdapter> data, ForecastingDocumentPanel forecastingDocumentPanel){
		this.data = data;
		this.mainTabPane = forecastingDocumentPanel;
	}
	@Override
	public int getRowCount() {
		return data != null ? data.size() : 0;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (data==null || data.isEmpty()) return null;
		return columnIndex == 0 ? data.get(rowIndex).getMonth() : data.get(rowIndex).getIQuantity();
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (data == null || data.isEmpty()) return;
		if (columnIndex == 0){
			data.get(rowIndex).setMonth((MonthUIAdapter)aValue);
		}else{			
			try{
				int value = Integer.parseInt((String)aValue);
				data.get(rowIndex).setIQuantity(new Integer((value)));
				this.mainTabPane.setVisibleCalculationDetailsTabs(false);
			}catch(NumberFormatException ex){}			
		}		
	}
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {		
		return columnIndex==1;
	}
}
