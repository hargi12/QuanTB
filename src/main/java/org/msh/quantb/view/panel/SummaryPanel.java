package org.msh.quantb.view.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.msh.quantb.services.calc.DateUtils;
import org.msh.quantb.services.calc.MedicineConsumption;
import org.msh.quantb.services.calc.MedicineResume;
import org.msh.quantb.services.calc.OrderCalculator;
import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.MedicineUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.view.DashZeroCellRenderer;
import org.msh.quantb.view.tableExt.ColumnGroup;
import org.msh.quantb.view.tableExt.DivMultiLineCellRenderer;
import org.msh.quantb.view.tableExt.DivMultiLineHeaderRenderer;
import org.msh.quantb.view.tableExt.GroupableTableColumnModel;
import org.msh.quantb.view.tableExt.GroupableTableHeader;
import org.msh.quantb.view.tableExt.SimpleHeaderRenderer;

/**
 * Panel of forecasting calculation summary
 *
 */
public class SummaryPanel extends JPanel {
	private static final long serialVersionUID = 5150875580730330671L;
	private static final double MINSIZE_IN_ROW = 20.0;
	private JTable table;
	private Object[][] data;
	private Object[] columnNames = new Object[] { Messages.getString("ForecastingDocumentWindow.tbSummary.column.Medicine"),
			Messages.getString("ForecastingDocumentWindow.tbSummary.column.StockOnHand"),
			Messages.getString("ForecastingDocumentWindow.tbSummary.column.monthsOfStock"),
			Messages.getString("ForecastingDocumentWindow.tbSummary.column.StockOnOrder"),
			Messages.getString("ForecastingDocumentWindow.tbSummary.column.DispensingQuantity"),
			Messages.getString("ForecastingDocumentWindow.tbSummary.column.QuantityLost"),
			Messages.getString("ForecastingDocumentWindow.tbSummary.column.StockOnHandAfter"),
			Messages.getString("ForecastingDocumentWindow.tbSummary.column.StockOnOrder"),
			Messages.getString("ForecastingDocumentWindow.tbSummary.column.QuantityLost"),
			Messages.getString("ForecastingDocumentWindow.tbSummary.column.EstimatedConsumptionPrev"),
			Messages.getString("ForecastingDocumentWindow.tbSummary.column.EstimatedConsumptionNew"),
			Messages.getString("ForecastingDocumentWindow.tbSummary.column.accQuantity"),
			Messages.getString("ForecastingDocumentWindow.tbSummary.column.EstimatedQuantity"),
			Messages.getString("ForecastingDocumentWindow.tbSummary.column.total")};
	private DefaultTableModel tableModel;
	private JScrollPane scroll;
	private JLabel detailsLbl;
	private SummaryDataModelHelper model;

	public SummaryPanel() {
		setMinimumSize(new Dimension(688, 500));
		setLayout(new BorderLayout(0, 0));
		scroll = new JScrollPane();
		add(scroll);
		setSize(new Dimension(1000, 490));

		detailsLbl = new JLabel();
		detailsLbl.setPreferredSize(new Dimension(46, 25));
		add(detailsLbl, BorderLayout.NORTH);
	}
	
	

	/**
	 * Set data to summary table
	 * 
	 * @param fui forecasting 
	 * @param resMed list of medicines
	 * @param mCons list of medicine consumptions
	 * @param oCalc order calculator
	 */
	public void setData(ForecastUIAdapter fui, List<MedicineResume> resMed, List<MedicineConsumption> mCons, OrderCalculator oCalc) {
		if (resMed != null && !resMed.isEmpty() && fui!=null) {	
			detailsLbl.setText(fui.getDetailsInformationHTML());			
			model = new SummaryDataModelHelper(fui, resMed, mCons,oCalc);
			// Setup table
			this.data = model.getData();
			tableModel = new DefaultTableModel(data, columnNames){
				private static final long serialVersionUID = -1507415714053499716L;
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			table = new JTable();
			table.setEnabled(true);
			table.setRowSelectionAllowed(false);
			table.setCellSelectionEnabled(false);
			table.getColumnModel().setColumnSelectionAllowed(false);
			table.setColumnModel(new GroupableTableColumnModel());
			table.setTableHeader(new GroupableTableHeader((GroupableTableColumnModel) table.getColumnModel()));
			table.setModel(tableModel);
			model.setTableModel(tableModel);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			//setRowHeight(medicineNameSize);
			setRowHeight(model.getMedicineNameSize());
			table.getColumnModel().getColumn(0).setCellRenderer(new DivMultiLineCellRenderer(JLabel.LEFT, true));
			table.getColumnModel().getColumn(0).setMinWidth(127);
			table.getColumnModel().getColumn(1).setMinWidth(76);
			table.getColumnModel().getColumn(1).setCellRenderer(new DashZeroCellRenderer(false,null, null));
			table.getColumnModel().getColumn(2).setMinWidth(65);
			table.getColumnModel().getColumn(2).setCellRenderer(new DivMultiLineCellRenderer(JLabel.RIGHT, false));
			table.getColumnModel().getColumn(3).setMinWidth(76);
			//table.getColumnModel().getColumn(3).setCellRenderer(new DivMultiLineCellRenderer(JLabel.LEFT, false));
			table.getColumnModel().getColumn(3).setCellRenderer(new DashZeroCellRenderer(false,null, null));
			table.getColumnModel().getColumn(4).setMinWidth(84);
			table.getColumnModel().getColumn(4).setCellRenderer(new DashZeroCellRenderer(false,null, null));
			table.getColumnModel().getColumn(5).setMinWidth(76);
			table.getColumnModel().getColumn(5).setCellRenderer(new DashZeroCellRenderer(true,null, Color.RED));
			table.getColumnModel().getColumn(6).setMinWidth(76);
			table.getColumnModel().getColumn(6).setCellRenderer(new DashZeroCellRenderer(false,null, null));
			table.getColumnModel().getColumn(7).setMinWidth(76);
			table.getColumnModel().getColumn(7).setCellRenderer(new DashZeroCellRenderer(false,null, null));
			table.getColumnModel().getColumn(8).setMinWidth(87);
			table.getColumnModel().getColumn(8).setCellRenderer(new DashZeroCellRenderer(true,null, Color.RED));
			table.getColumnModel().getColumn(9).setMinWidth(76);
			table.getColumnModel().getColumn(9).setCellRenderer(new DashZeroCellRenderer(false,null, null));
			table.getColumnModel().getColumn(10).setMinWidth(76);
			table.getColumnModel().getColumn(10).setCellRenderer(new DashZeroCellRenderer(false,null, null));
			table.getColumnModel().getColumn(11).setMinWidth(76);
			table.getColumnModel().getColumn(11).setCellRenderer(new DashZeroCellRenderer(false,null, Color.RED));
			table.getColumnModel().getColumn(12).setMinWidth(76);
			table.getColumnModel().getColumn(12).setCellRenderer(new DashZeroCellRenderer(false,null, null));
			table.getColumnModel().getColumn(13).setMinWidth(87);
			table.getColumnModel().getColumn(13).setCellRenderer(new DashZeroCellRenderer(false,null, null));
			//table.getColumnModel().getColumn(14).setMinWidth(76);
			//table.getColumnModel().getColumn(14).setCellRenderer(new DashZeroCellRenderer(false,null, null));
			table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);			
			/*			table.getColumnModel().getColumn(12).setPreferredWidth(50);
			table.getColumnModel().getColumn(13).setPreferredWidth(50);*/
			// Setup Column Groups
			GroupableTableColumnModel cm = (GroupableTableColumnModel) table.getColumnModel();
			MedicineResume firstMed = resMed.get(0);
			
			ColumnGroup rDGroup = new ColumnGroup(new SimpleHeaderRenderer(), 
					"<html><center>" +
					Messages.getString("ForecastingDocumentWindow.tbSummary.column.group.RD")+
					"<br>"+
					DateUtils.formatMedium(fui.getReferenceDate())+  //20160825 appropriate
					"<br><br></center></html>"
					);
			rDGroup.add(cm.getColumn(1));
			rDGroup.add(cm.getColumn(2));
			
			ColumnGroup leadTimeGroup = new ColumnGroup(new SimpleHeaderRenderer(), "<html><center>" + 
					Messages.getString("ForecastingDocumentWindow.tbSummary.column.group.leadTime")+"<br>"+
					firstMed.getLeadPeriod().getFromTxt()+" - "+firstMed.getLeadPeriod().getToTxt()+"<br>"+
					firstMed.getLeadPeriod().getDaysBetweenPeriodTxt()+ "</center></html>") ;
			leadTimeGroup.add(cm.getColumn(3));
			leadTimeGroup.add(cm.getColumn(4));
			leadTimeGroup.add(cm.getColumn(5));
			ColumnGroup reviewPeriodGroup = new ColumnGroup(new SimpleHeaderRenderer(), "<html><center>"+
					Messages.getString("ForecastingDocumentWindow.tbSummary.column.group.reviewPeriod")+"<br>"+
					fui.getStartReviewPeriodTxt()+" - " + fui.getEndReviewPeriodTxt() + "<br>" +
					fui.getDurationOfReviewPeriodInDays()+
			"</center></html>");
			reviewPeriodGroup.add(cm.getColumn(6));
			reviewPeriodGroup.add(cm.getColumn(7));
			reviewPeriodGroup.add(cm.getColumn(8));
			reviewPeriodGroup.add(cm.getColumn(9));
			reviewPeriodGroup.add(cm.getColumn(10));
			
			
			ColumnGroup totalGroup = new ColumnGroup(new SimpleHeaderRenderer(), "<html><center>"+
					Messages.getString("ForecastingDocumentWindow.tbSummary.column.group.total")+"<br>"+
					firstMed.getLeadPeriod().getFromTxt()+" - "+ fui.getEndReviewPeriodTxt() + "<br>"+
					fui.getForecastingDurationDays()+
			"</center></html>");
			totalGroup.add(cm.getColumn(11));
			totalGroup.add(cm.getColumn(12));
			totalGroup.add(cm.getColumn(13));
			
			cm.addColumnGroup(rDGroup);
			cm.addColumnGroup(leadTimeGroup);
			cm.addColumnGroup(reviewPeriodGroup);
			cm.addColumnGroup(totalGroup);
			
			//Setup multiline headers
			DivMultiLineHeaderRenderer renderer = new DivMultiLineHeaderRenderer();
			Enumeration<TableColumn> en = table.getColumnModel().getColumns();
			while (en.hasMoreElements()) {
				((TableColumn) en.nextElement()).setHeaderRenderer(renderer);
			}
			table.revalidate();
			table.repaint();
			scroll.setViewportView(table);
		}
	}


	/**
	 * Set row heidht for summary table, in order with
	 * lenght of row in first cell.
	 * @param medicineNameSize array of sizes of length first cells
	 */
	private void setRowHeight(int[] medicineNameSize) {
		int rowHeight = table.getRowHeight();
		for (int i = 0; i < medicineNameSize.length; i++){				
			//int lines = (int)(medicineNameSize[i]/MINSIZE_IN_ROW) + (medicineNameSize[i]%MINSIZE_IN_ROW>0?2:0);
			double s = new Double(medicineNameSize[i]);

			BigDecimal perc = new BigDecimal(s/MINSIZE_IN_ROW);
			perc = perc.setScale(0, RoundingMode.HALF_UP);
			
			int lines = perc.intValue() + 2;
			table.setRowHeight(i, rowHeight * lines );	
		}
	}

	/**
	 * @return the data model
	 */
	public Object[][] getData() {
		return data;
	}



	public DefaultTableModel getTableModel() {
		return tableModel;
	}


	public SummaryDataModelHelper getModel() {
		return model;
	}



	/**
	 * Change total for a medicine given
	 * @param medicine a medicine
	 * @param totalA total in accel order
	 * @param totalR total in regular order
	 * @param total totalA+totalR
	 */
	public void changeOrderTotal(MedicineUIAdapter medicine, Integer totalA, Integer totalR, Integer total) {
		int i=0;
		for(MedicineResume mRes :getModel().getResMed()){
			if(mRes.getMedicine().compareTo(medicine)==0){
				break;
			}
			i++;
		}
		getTableModel().setValueAt(totalA, i, SummaryDataModelHelper.ACCEL_TOTAL_COLUMN);
		getTableModel().fireTableCellUpdated(i, SummaryDataModelHelper.ACCEL_TOTAL_COLUMN);
		getTableModel().setValueAt(totalR, i, SummaryDataModelHelper.REGULAR_TOTAL_COLUMN);
		getTableModel().fireTableCellUpdated(i, SummaryDataModelHelper.REGULAR_TOTAL_COLUMN);
		getTableModel().setValueAt(total, i, SummaryDataModelHelper.TOTAL_COLUMN);
		getTableModel().fireTableCellUpdated(i, SummaryDataModelHelper.TOTAL_COLUMN);
		
	}
	
	
}
