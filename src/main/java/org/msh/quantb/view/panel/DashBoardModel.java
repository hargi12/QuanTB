package org.msh.quantb.view.panel;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.msh.quantb.services.calc.MedicineConsumption;
import org.msh.quantb.services.mvp.Messages;

/**
 * This class is data model for the Dash Board
 * Allows to have right numbers of colored bars
 * @author Alexey Kurasov
 *
 */
public class DashBoardModel {

	public static final int GRAY=0;
	public static final int GREEN=1;
	public static final int YELLOW=2;
	public static final int RED=3;

	CategoryToolTipGenerator toolTip = new CategoryToolTipGenerator() {
		@Override
		public String generateToolTip(CategoryDataset dataset, int row, int column) {
			String rowKey = (String) dataset.getColumnKey(column);
			DashBoardRowModel dBr = null;
			for(DashBoardRowModel dBrM : getRows()){
				if (dBrM.getKey().equalsIgnoreCase(rowKey)){
					dBr = dBrM;
					break;
				}
			}
			if (dBr != null){
				return dBr.getToolTip(row);
			}
			else{
				return "";
			}
		}
	};
	
	CategoryItemLabelGenerator labels = new CategoryItemLabelGenerator(){

		@Override
		public String generateColumnLabel(CategoryDataset arg0, int arg1) {
			return null;
		}

		@Override
		public String generateLabel(CategoryDataset dataset, int row, int column) {
			String rowKey = (String) dataset.getColumnKey(column);
			DashBoardRowModel dBr = null;
			for(DashBoardRowModel dBrM : getRows()){
				if (dBrM.getKey().equalsIgnoreCase(rowKey)){
					dBr = dBrM;
					break;
				}
			}
			if (dBr != null){
				return dBr.getWarning(row);
			}
			else{
				return "";
			}
		}

		@Override
		public String generateRowLabel(CategoryDataset arg0, int arg1) {
			return null;
		}
		
	};

	List<DashBoardRowModel> rows;
	private List<MedicineConsumption> medConsList;

	public DashBoardModel(List<MedicineConsumption> medConsList) {
		this.medConsList = medConsList;
		rows = new ArrayList<DashBoardRowModel>();
		for(MedicineConsumption mc :medConsList){
			rows.add(new DashBoardRowModel(mc));
		}
	}

	/**
	 * Create data model for the chart
	 * @return
	 */
	public CategoryDataset createDataSet(){
		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
		for(DashBoardRowModel row : rows){
			row.addToDataSet(dataSet);
		}
		return dataSet;
	}

	/**
	 * Maximum numbers of series is months quantity
	 * Series colors grouped by four Gray, Green, Red, Yellow
	 * @param sBr
	 */
	public void determineSeries(StackedBarRenderer sBr){
		Font font = sBr.getBaseItemLabelFont();
		sBr.setBaseItemLabelFont(font.deriveFont(Font.BOLD, 9.0f));
		sBr.setBaseToolTipGenerator(toolTip);
		sBr.setBaseItemLabelsVisible(true);
		sBr.setBaseItemLabelGenerator(labels);
		for(int i=0; i<medConsList.iterator().next().getCons().size();i++){
			sBr.setSeriesPaint(i*4+GRAY, Color.LIGHT_GRAY);
			sBr.setSeriesPaint(i*4+GREEN, Color.GREEN);
			sBr.setSeriesPaint(i*4+YELLOW, Color.YELLOW);
			sBr.setSeriesPaint(i*4+RED, Color.RED);
		}
	}

	public List<DashBoardRowModel> getRows() {
		return rows;
	}

	public void setRows(List<DashBoardRowModel> rows) {
		this.rows = rows;
	}



}
