package org.msh.quantb.view.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.msh.quantb.model.mvp.ModelFactory;
import org.msh.quantb.services.calc.ConsumptionMonth;
import org.msh.quantb.services.calc.MedicineConsumption;
import org.msh.quantb.services.io.ForecastingRegimenResultUIAdapter;
import org.msh.quantb.services.io.ForecastingRegimenUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.view.FixedColumnCTable;
import org.msh.quantb.view.MonthCellRenderer;
import org.msh.quantb.view.tableExt.DashZeroCellRenderer;
import org.msh.quantb.view.tableExt.DivMultiLineCellRenderer;
import org.msh.quantb.view.tableExt.SimpleHeaderRenderer;
import org.msh.quantb.view.tableExt.columnSpan.CMapImpl;
import org.msh.quantb.view.tableExt.columnSpan.CTable;

public class CasesOnTreatmentPanel extends JPanel {
	private static final long serialVersionUID = -8941965219628994510L;
	private static final int numerOfRecordsPerSingleRegimen = 3;
	private JScrollPane scrollReg;
	private JScrollPane scrollMed;
	private Object[][] dataReg;
	private Object[][] dataMed;
	private Object[] columnNamesReg;
	private Object[] columnNamesMed;
	private JLabel detailsLbl;
	private JTabbedPane tabbedPane;

	public CasesOnTreatmentPanel() {
		setSize(new Dimension(1000, 490));
		setLayout(new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane);
		scrollReg = new JScrollPane();
		tabbedPane.addTab(Messages.getString("ForecastingDocumentWindow.tbCasesReport.column.TreatmentRegimen"), null, scrollReg, null);
		scrollMed = new JScrollPane();
		tabbedPane.addTab(Messages.getString("ForecastingDocumentWindow.tbSummary.column.Medicine"), null, scrollMed, null);
		
		detailsLbl = new JLabel();
		detailsLbl.setPreferredSize(new Dimension(46, 25));
		add(detailsLbl, BorderLayout.NORTH);
	}
	
	/**
	 * Set details information 
	 * @param details information (Country/Region/Facility/Person)
	 */
	public void setForecastingDetails(String details){
		detailsLbl.setText(details!=null?details:"");
	}
	
	/**
	 * Set data into medicine table
	 * 
	 * @param mCons
	 * @param modelFactory
	 */
	public void setDataForMedicine(List<MedicineConsumption> mCons) {
		if (mCons != null) {
			prepareDataForMedicine(mCons);
			// Setup table
			TableModel tableModel = new DefaultTableModel(dataMed, columnNamesMed);
			CMapImpl cMap = new CMapImpl();
			for (int i = 0; i < mCons.size(); i++) {
				cMap.addRowSpan(i * numerOfRecordsPerSingleRegimen, 0, 3);
			}			
			CTable table = new CTable(cMap, tableModel);
			table.getTableHeader().setReorderingAllowed(false);			
			table.getTableHeader().setResizingAllowed(false);
			table.setEnabled(false);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			//set row height
			int rowHeight = table.getRowHeight();
			table.setRowHeight(rowHeight * 4);
			for (int i = 0; i < mCons.size(); i++) {
				table.setRowHeight(i * numerOfRecordsPerSingleRegimen + 2, rowHeight);
			}		
			table.setRowHeight(table.getRowCount()-1, 1);
			//set column sizes
			table.getColumnModel().getColumn(0).setPreferredWidth(200);
			table.getColumnModel().getColumn(0).setMinWidth(200);
			table.getColumnModel().getColumn(0).setCellRenderer(new DivMultiLineCellRenderer(JLabel.LEFT, true));
			table.getColumnModel().getColumn(1).setPreferredWidth(80);
			table.getColumnModel().getColumn(0).setMinWidth(80);
			table.getColumnModel().getColumn(1).setCellRenderer(new DivMultiLineCellRenderer(JLabel.LEFT, true));
			for (int i = 2; i < table.getColumnModel().getColumnCount(); i++) {
				table.getColumnModel().getColumn(i).setPreferredWidth(70);
				table.getColumnModel().getColumn(i).setMinWidth(70);
				table.getColumnModel().getColumn(i).setCellRenderer(new DashZeroCellRenderer(false, null));
			}
			//Setup multiline headers
			SimpleHeaderRenderer renderer = new SimpleHeaderRenderer();
			MonthCellRenderer mrend = new MonthCellRenderer();
			table.getColumnModel().getColumn(0).setHeaderRenderer(renderer);
			Enumeration<TableColumn> en = table.getColumnModel().getColumns();
			while (en.hasMoreElements()) {
				TableColumn col = ((TableColumn) en.nextElement());
				if (col.getModelIndex() > 1){
					col.setHeaderValue(columnNamesMed[col.getModelIndex()]);
					col.setHeaderRenderer(mrend);
				}
			}
			table.revalidate();
			table.repaint();
			scrollMed.setViewportView(table);
			scrollMed.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
			new FixedColumnCTable(2, scrollMed);
			
		}
	}

	/**
	 * Set data into treatment regimen table
	 * @param referenceDate 
	 * 
	 * @param resReg
	 * @param modelFactory
	 */
	public void setDataForRegimen(Calendar referenceDate, List<ForecastingRegimenUIAdapter> resReg, ModelFactory modelFactory) {
		if (resReg != null && modelFactory != null) {
			//prepare data
			prepareDataForRegimen(referenceDate, resReg, modelFactory);
			// Setup table
			TableModel tableModel = new DefaultTableModel(dataReg, columnNamesReg);
			CMapImpl cMap = new CMapImpl();
			for (int i = 0; i < resReg.size(); i++) {
				cMap.addRowSpan(i * numerOfRecordsPerSingleRegimen, 0, 3);
			}
			cMap.addColumnSpan((resReg.size() + 1) * numerOfRecordsPerSingleRegimen - 1, 0, 2);
			cMap.addColumnSpan((resReg.size() + 1) * numerOfRecordsPerSingleRegimen - 2, 0, 2);
			cMap.addColumnSpan((resReg.size() + 1) * numerOfRecordsPerSingleRegimen - 3, 0, 2);
			CTable table = new CTable(cMap, tableModel);
			table.getTableHeader().setReorderingAllowed(false);
			table.getTableHeader().setResizingAllowed(false);			
			table.setEnabled(false);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);		
			//set row height
			int rowHeight = table.getRowHeight();
			table.setRowHeight(rowHeight * 4);
			for (int i = 0; i < resReg.size(); i++) {
				table.setRowHeight(i * numerOfRecordsPerSingleRegimen + 2, rowHeight);
			}
			table.setRowHeight(table.getRowCount() - 1, rowHeight);			
			//set column sizes
			table.getColumnModel().getColumn(0).setPreferredWidth(200);
			table.getColumnModel().getColumn(0).setMinWidth(200);
			table.getColumnModel().getColumn(0).setCellRenderer(new DivMultiLineCellRenderer(JLabel.LEFT, true));
			table.getColumnModel().getColumn(1).setPreferredWidth(80);
			table.getColumnModel().getColumn(0).setMinWidth(80);
			table.getColumnModel().getColumn(1).setCellRenderer(new DivMultiLineCellRenderer(JLabel.LEFT, true));
			for (int i = 2; i < table.getColumnModel().getColumnCount(); i++) {
				table.getColumnModel().getColumn(i).setPreferredWidth(70);
				table.getColumnModel().getColumn(i).setMinWidth(70);
				table.getColumnModel().getColumn(i).setCellRenderer(new DashZeroCellRenderer(false,null));
			}
			//Setup multiline headers
			SimpleHeaderRenderer renderer = new SimpleHeaderRenderer();
			MonthCellRenderer mrend = new MonthCellRenderer();
			table.getColumnModel().getColumn(0).setHeaderRenderer(renderer);
			Enumeration<TableColumn> en = table.getColumnModel().getColumns();
			while (en.hasMoreElements()) {
				TableColumn col = ((TableColumn) en.nextElement());
				if (col.getModelIndex() > 1){
					col.setHeaderValue(columnNamesReg[col.getModelIndex()]);
					col.setHeaderRenderer(mrend);
				}
			}
			table.revalidate();
			table.repaint();
			scrollReg.setViewportView(table);
			scrollReg.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
			new FixedColumnCTable(2, scrollReg);
		}
	}

	/**
	 * Prepare data as array of objects based on List of forecasting medicine consumption
	 * 
	 * @param mCons
	 *            forecasting medicine consumption
	 * @param modelFactory
	 *            model factory
	 */
	private void prepareDataForMedicine(List<MedicineConsumption> mCons) {

		// initialize data and column names
		int numberOfMonths = mCons.iterator().next().getCons().size();
		dataMed = new Object[mCons.size() * numerOfRecordsPerSingleRegimen+1][numberOfMonths + 2];
		columnNamesMed = new Object[numberOfMonths + 2];
		//set column names
		columnNamesMed[0] = Messages.getString("ForecastingDocumentWindow.tbSummary.column.Medicine");
		columnNamesMed[1] = "";
		int index = 2;
		List<ConsumptionMonth> res = mCons.get(0).getCons();
		for (ConsumptionMonth fr : res) {
/*			Calendar cal = new GregorianCalendar();
			cal.set(fr.getMonth().getYear(), fr.getMonth().getMonth(), 1);
			columnNamesMed[index++] = DateUtils.formatDate(cal.getTime(), "MMM-yy").replace("-", "-\r\n");*/
			columnNamesMed[index++] = fr.getMonth();
		}
		//set data
		index = 0;
		BigDecimal[] totals = new BigDecimal[mCons.get(0).getCons().size()];
		for (MedicineConsumption mc : mCons) {
			dataMed[index * numerOfRecordsPerSingleRegimen][0] = mc.getMed().getNameForDisplay();
			dataMed[index * numerOfRecordsPerSingleRegimen][1] = Messages.getString("ForecastingDocumentWindow.tbCasesReport.column.PrevCases");
			dataMed[index * numerOfRecordsPerSingleRegimen + 1][1] = Messages.getString("ForecastingDocumentWindow.tbCasesReport.column.NewCases");
			dataMed[index * numerOfRecordsPerSingleRegimen + 2][1] = Messages.getString("ForecastingDocumentWindow.tbCasesReport.column.Total");
			int innerIndex = 2;
			for (ConsumptionMonth cm : mc.getCons()) {
				BigDecimal prevCases = cm.getOldCases();
				BigDecimal newCases = cm.getNewCases();
				BigDecimal totCases = prevCases.add(newCases);
				totals[innerIndex - 2] = totCases;
				dataMed[index * numerOfRecordsPerSingleRegimen][innerIndex] = prevCases;
				dataMed[index * numerOfRecordsPerSingleRegimen + 1][innerIndex] = newCases;
				dataMed[index * numerOfRecordsPerSingleRegimen + 2][innerIndex++] = totCases;
			}
			index++;
		}
	}

	/**
	 * Prepare data as array of objects based on List of forecasting regimen ui adapter
	 * @param referenceDate 
	 * 
	 * @param resReg
	 *            forecasting regimen
	 * @param modelFactory
	 *            model factory
	 */
	private void prepareDataForRegimen(Calendar referenceDate, List<ForecastingRegimenUIAdapter> resReg, ModelFactory modelFactory) {
		// initialize data and column names
		int numberOfMonths = resReg.iterator().next().getMonthsResults(referenceDate,modelFactory).size();
		dataReg = new Object[resReg.size() * numerOfRecordsPerSingleRegimen + 1][numberOfMonths + 2];
		columnNamesReg = new Object[numberOfMonths + 2];
		//set column names
		columnNamesReg[0] = Messages.getString("ForecastingDocumentWindow.tbCasesReport.column.TreatmentRegimen");
		columnNamesReg[1] = "";
		int index = 2;
		List<ForecastingRegimenResultUIAdapter> res = resReg.get(0).getMonthsResults(referenceDate,modelFactory);
		for (ForecastingRegimenResultUIAdapter fr : res) {
			columnNamesReg[index++] = fr.getMonth();
		}
		//set data
		index = 0;
		BigDecimal[] totals = new BigDecimal[resReg.get(0).getMonthsResults(referenceDate,modelFactory).size()];
		for(int i=0; i<totals.length;i++){
			BigDecimal bd = new BigDecimal(0.00);
			bd = bd.setScale(2);
			totals[i] = bd;
		}
		for (ForecastingRegimenUIAdapter fr : resReg) {
			dataReg[index * numerOfRecordsPerSingleRegimen][0] = fr.getRegimen().getName();
			dataReg[index * numerOfRecordsPerSingleRegimen][1] = Messages.getString("ForecastingDocumentWindow.tbCasesReport.column.PrevCases");
			dataReg[index * numerOfRecordsPerSingleRegimen + 1][1] = Messages.getString("ForecastingDocumentWindow.tbCasesReport.column.NewCases");
			dataReg[index * numerOfRecordsPerSingleRegimen + 2][1] = Messages.getString("ForecastingDocumentWindow.tbCasesReport.column.Total");
			int innerIndex = 2;
			for (ForecastingRegimenResultUIAdapter frr : fr.getMonthsResults(referenceDate,modelFactory)) {
				BigDecimal prevCases = frr.getEnrolled(); //TODO something wrong!!!
				BigDecimal newCases = frr.getExpected();
				totals[innerIndex - 2] = totals[innerIndex - 2].add((prevCases.add(newCases)));
				dataReg[index * numerOfRecordsPerSingleRegimen][innerIndex] = prevCases;
				dataReg[index * numerOfRecordsPerSingleRegimen + 1][innerIndex] = newCases;
				dataReg[index * numerOfRecordsPerSingleRegimen + 2][innerIndex++] = prevCases.add(newCases);
			}
			index++;
		}
		//Set global totals
		for (int i = 0; i < totals.length; i++) {
			dataReg[dataReg.length - 1][i + 2] = totals[i];
		}		
		dataReg[dataReg.length - 1][0] = Messages.getString("ForecastingDocumentWindow.tbCasesReport.column.GrandTotal");
	}
	
	/**
	 * Disable regimen tab
	 * For some cases this tab hasn't sense. Presenter knows.
	 */
	public void disableRegimenTab() {
		if (tabbedPane.getTabCount()>1){
			tabbedPane.setEnabledAt(0, false);
			tabbedPane.setSelectedIndex(1);
		}
		
	}
}
