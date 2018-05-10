package org.msh.quantb.view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.msh.quantb.services.calc.ConsumptionMonth;
import org.msh.quantb.services.calc.MedicineConsumption;
import org.msh.quantb.services.io.MedicineUIAdapter;
import org.msh.quantb.services.io.MonthUIAdapter;
import org.msh.quantb.services.mvp.Messages;

/**
 * This is a model for Delivery schedule table
 * @author Alex Kurasoff
 *
 */
public class ScheduleDataModel extends AbstractTableModel {

	private class ScheduleData{
		MonthUIAdapter month;
		BigDecimal	quantity;
		public ScheduleData(MonthUIAdapter month, BigDecimal quantity) {
			super();
			this.month = month;
			this.quantity = quantity;
		}
		public MonthUIAdapter getMonth() {
			return month;
		}
		public BigDecimal getQuantity() {
			return quantity;
		}

	}

	private class ScheduleRowModel{
		MedicineUIAdapter medicine;
		List<ScheduleData> deliveries = new ArrayList<ScheduleData>();
		public ScheduleRowModel(MedicineUIAdapter medicine) {
			super();
			this.medicine = medicine;
		}
		public MedicineUIAdapter getMedicine() {
			return medicine;
		}
		public List<ScheduleData> getDeliveries() {
			return deliveries;
		}
		public BigDecimal getTotal() {
			BigDecimal ret = BigDecimal.ZERO;
			for(ScheduleData col : getDeliveries()){
				ret = ret.add(col.getQuantity());
			}
			return ret;
		}


	}


	private static final long serialVersionUID = 2248741120091582458L;

	private List<ScheduleRowModel> tableData=new ArrayList<ScheduleRowModel>();
	private List<MonthUIAdapter> headers=new ArrayList<MonthUIAdapter>();



	public ScheduleDataModel(List<MedicineConsumption> data) {
		super();
		buildModel(data);
	}


	/**
	 * Build table model
	 * @param data list of medicine consumptions
	 */
	private void buildModel(List<MedicineConsumption> data) {
		getTableData().clear();
		getHeaders().clear();
		if(data != null){
			// build data
			for(MedicineConsumption mC : data){
				ScheduleRowModel row = new ScheduleRowModel(mC.getMed());
				getTableData().add(row);
				for(ConsumptionMonth cM : mC.getCons()){
					if(cM.getDelivery().compareTo(BigDecimal.ZERO)>0){
						row.getDeliveries().add(new ScheduleData(cM.getMonth(), cM.getDelivery()));
					}
				}
			}
			//build headers
			for(ScheduleRowModel row : getTableData()){
				for(ScheduleData col : row.getDeliveries()){
					if(!getHeaders().contains(col.getMonth())){
						getHeaders().add(col.getMonth());
					}
				}
			}
			//sort data and headers
			Collections.sort(getTableData(), new Comparator<ScheduleRowModel>() {
				@Override
				public int compare(ScheduleRowModel o1, ScheduleRowModel o2) {
					return o1.getMedicine().compareTo(o2.getMedicine());
				}

			});

			Collections.sort(getHeaders(), new Comparator<MonthUIAdapter>() {
				@Override
				public int compare(MonthUIAdapter o1, MonthUIAdapter o2) {
					return o1.compareTo(o2);
				}
			});
		}
	}



	public List<ScheduleRowModel> getTableData() {
		return tableData;
	}

	public List<MonthUIAdapter> getHeaders() {
		return headers;
	}

	@Override
	public int getRowCount() {
		return getTableData().size();
	}

	@Override
	public int getColumnCount() {
		return getHeaders().size()+2;
	}


	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(getTableData().size()>rowIndex){
			ScheduleRowModel row = getTableData().get(rowIndex);
			if(columnIndex == 0){
				return row.getMedicine().getNameForDisplayWithAbbrev();
			}
			if(columnIndex == getColumnCount()-1){
				return row.getTotal();
			}
			return fetchQuantity(row, columnIndex);
		}else{
			return BigDecimal.ZERO;
		}
	}
	
	@Override
	public String getColumnName(int column) {
		if(column == 0){
			return Messages.getString("ForecastingDocumentWindow.order.ordcolumns.medicines");
		}
		if(column == getColumnCount()-1){
			return Messages.getString("ForecastingDocumentWindow.order.totalDelivery");
		}
		return getHeaders().get(column-1).toString();
	}

	/**
	 * fetch quantity for month given
	 * @param row
	 * @param columnIndex
	 * @return
	 */
	private BigDecimal fetchQuantity(ScheduleRowModel row, int columnIndex) {
		if(getHeaders().size()>columnIndex-1){
			MonthUIAdapter month = getHeaders().get(columnIndex-1);
			BigDecimal ret = BigDecimal.ZERO;
			for(ScheduleData col : row.getDeliveries()){
				if(col.getMonth().compareTo(month)==0){
					ret = col.getQuantity();
					break;
				}
			}
			return ret;
		}
		else{
			return BigDecimal.ZERO;
		}
	}

}
