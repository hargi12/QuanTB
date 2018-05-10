package org.msh.quantb.view.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.msh.quantb.services.calc.ConsumptionMonth;
import org.msh.quantb.services.calc.MedicineConsumption;
import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.view.BoldTextCellRenderer;
import org.msh.quantb.view.MonthCellRenderer;
import org.msh.quantb.view.tableExt.MedConsTableCellRenderer;
import org.msh.quantb.view.tableExt.DashZeroCellRenderer;
import org.msh.quantb.view.tableExt.SimpleHeaderRenderer;
import org.msh.quantb.view.tableExt.columnSpan.CMapImpl;
import org.msh.quantb.view.tableExt.columnSpan.CTable;

/**
 * Panel of medicine consumption
 */
public class MedicineConsumptionPanel extends JPanel {

	private static final long serialVersionUID = 8610728809340876493L;
	private static final int MINSIZE_IN_ROW = 15;
	private JScrollPane scroll;
	private Object[][] data;
	private Object[] columnNames;
	final int numberOfDetailsForSingleMedCons = 8;
	private JLabel detailsLbl;

	public MedicineConsumptionPanel() {
		setLayout(new BorderLayout(0, 0));
		scroll = new JScrollPane();
		add(scroll);
		setSize(new Dimension(1000, 480));
		
				detailsLbl = new JLabel();
				detailsLbl.setPreferredSize(new Dimension(46, 25));
				add(detailsLbl, BorderLayout.NORTH);
	}

	/**
	 * Set data to medicine consumption table
	 * 
	 * @param forecast forecasting
	 * 
	 * @param modelFactory model factory
	 * 
	 * @param medCons list of medicine consumption
	 * 
	 */
	public void setData(ForecastUIAdapter forecast, List<MedicineConsumption> medCons) {
		if (medCons != null && !medCons.isEmpty() && forecast != null) {
			detailsLbl.setText(forecast.getDetailsInformationHTML());
			//MonthUIAdapter rdm = forecast.getRDMonth(modelFactory);
			int numCols = medCons.iterator().next().getCons().size();
			prepareData(medCons);
			//Set data into table
			DefaultTableModel tm = new DefaultTableModel(data, columnNames) {
				private static final long serialVersionUID = 213247331472464687L;

				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			//Build table
			CMapImpl cMap = new CMapImpl();
			for (int i = 0; i < medCons.size(); i++) {
				cMap.addColumnSpan(i * numberOfDetailsForSingleMedCons, 0, numCols + 1);
			}
			CTable table = new CTable(cMap, tm);
			table.setEnabled(false);
			table.getTableHeader().setReorderingAllowed(false);
			table.getTableHeader().setResizingAllowed(false);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.getColumnModel().getColumn(0).setCellRenderer(new BoldTextCellRenderer());
			table.getColumnModel().getColumn(0).setPreferredWidth(120);
			int rowHeight = table.getRowHeight();
			//table.setRowHeight(rowHeight * 2);
			for (int i = 0; i < table.getRowCount(); i++) {
				if (i != i * numberOfDetailsForSingleMedCons) {
					setRowHeight(i, table);
				}
			}
			for (int i = 0; i < medCons.size(); i++) {
				table.setRowHeight(i * numberOfDetailsForSingleMedCons, rowHeight * 3);
			}
			for (int i = 0; i < numCols; i++) {
				table.getColumnModel().getColumn(i + 1).setCellRenderer(new MedConsTableCellRenderer(true, Color.RED, 3,numberOfDetailsForSingleMedCons));
				table.getColumnModel().getColumn(i + 1).setPreferredWidth(70);
			}
			//Setup multiline headers
			SimpleHeaderRenderer renderer = new SimpleHeaderRenderer();
			MonthCellRenderer mrend = new MonthCellRenderer();
			table.getColumnModel().getColumn(0).setHeaderRenderer(renderer);
			Enumeration<TableColumn> en = table.getColumnModel().getColumns();
			while (en.hasMoreElements()) {
				TableColumn col = ((TableColumn) en.nextElement());
				if (col.getModelIndex() > 0) {
					col.setHeaderValue(columnNames[col.getModelIndex()]);
					col.setHeaderRenderer(mrend);
				}
			}
			table.getTableHeader().invalidate();
			table.revalidate();
			table.repaint();
			//Set table to scroll
			scroll.setViewportView(table);
		}
	}

	/**
	 * Prepare array of objects based on list of medicine consumption
	 * 
	 * @param medCons
	 */
	private void prepareData(List<MedicineConsumption> medCons) {
		int numRows = medCons.size() * numberOfDetailsForSingleMedCons;
		int numCols = medCons.iterator().next().getCons().size();
		//prepare column names
		columnNames = new Object[numCols + 1];
		columnNames[0] = Messages.getString("ForecastingDocumentWindow.tbSummary.column.Medicine");
		MedicineConsumption mCons = medCons.iterator().next();
		int index = 0;
		for (ConsumptionMonth cm : mCons.getCons()) {
			columnNames[++index] = cm.getMonth();
		}
		//Prepare data
		data = new Object[numRows][numCols + 1];
		index = 0;
		for (MedicineConsumption mc : medCons) {
			int i = 0;
			data[index * numberOfDetailsForSingleMedCons][0] = "\r\n" + mc.getMed().getNameForDisplay() + "\r\n";
			data[index * numberOfDetailsForSingleMedCons + 1][0] = Messages.getString("ForecastingDocumentWindow.tbMedicinesReport.column.StockOnHand");
			data[index * numberOfDetailsForSingleMedCons + 2][0] = Messages.getString("ForecastingDocumentWindow.tbMedicinesReport.column.QuantityMissing");
			data[index * numberOfDetailsForSingleMedCons + 3][0] = Messages.getString("ForecastingDocumentWindow.tbMedicinesReport.column.QuantityLostDue");
			data[index * numberOfDetailsForSingleMedCons + 4][0] = Messages.getString("ForecastingDocumentWindow.tbMedicinesReport.column.StockOnOrder");
			data[index * numberOfDetailsForSingleMedCons + 5][0] = Messages.getString("ForecastingDocumentWindow.tbMedicinesReport.column.ConsumptionPrev");
			data[index * numberOfDetailsForSingleMedCons + 6][0] = Messages.getString("ForecastingDocumentWindow.tbMedicinesReport.column.ConsumptionNew");
			data[index * numberOfDetailsForSingleMedCons + 7][0] = Messages.getString("ForecastingDocumentWindow.tbMedicinesReport.column.total");
			//MonthUIAdapter mu = null;
			for (ConsumptionMonth cm : mc.getCons()) {
				//mu = rdm.incrementClone(modelFactory, i);
				data[index * numberOfDetailsForSingleMedCons + 1][i + 1] = cm.getOnHandInt();
				data[index * numberOfDetailsForSingleMedCons + 2][i + 1] = cm.getMissingInt();
				data[index * numberOfDetailsForSingleMedCons + 3][i + 1] = cm.getExpired();
				data[index * numberOfDetailsForSingleMedCons + 4][i + 1] = cm.getOrder();
				data[index * numberOfDetailsForSingleMedCons + 5][i + 1] = cm.getConsOldInt();
				data[index * numberOfDetailsForSingleMedCons + 6][i + 1] = cm.getConsNewInt();
				data[index * numberOfDetailsForSingleMedCons + 7][i + 1] = cm.getConsNewInt() + cm.getConsOldInt();
				i++;
			}
			index++;
		}
	}

	/**
	 * Set row heidht for row index, in order with lenght of row in first cell.
	 * 
	 * @param index row index
	 * @param table table with data
	 */
	private void setRowHeight(int index, CTable table) {
		int rowHeight = table.getRowHeight();
		if (data[index][0] != null && !String.valueOf(data[index][0]).isEmpty()) {
			String[] words = String.valueOf(data[index][0]).split(" ");
			if (words != null && words.length > 0) {
				int currL = 0;
				int lines = 1;
				for (String s : words) {
					if (currL + s.length() >= MINSIZE_IN_ROW) {
						currL = 0;
						lines++;
					}
					currL += s.length();
				}
				if (currL >= MINSIZE_IN_ROW) lines++;
				table.setRowHeight(index, rowHeight * lines);
			}
		}
	}
}
