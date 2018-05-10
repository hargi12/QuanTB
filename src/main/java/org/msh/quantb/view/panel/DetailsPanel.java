package org.msh.quantb.view.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.msh.quantb.services.calc.ForecastingCalculation;
import org.msh.quantb.services.calc.MedicineConsumption;
import org.msh.quantb.services.calc.MedicineResume;
import org.msh.quantb.services.calc.PeriodResume;
import org.msh.quantb.services.io.MedicineUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.DashZeroCellRenderer;
import org.msh.quantb.view.PeriodResumeCellRenderer;
import org.msh.quantb.view.tableExt.DivMultiLineHeaderRenderer;

/**
 * Details panel
 * 
 */
public class DetailsPanel extends JPanel {
	private static final long serialVersionUID = -3018374203982702095L;
	private JScrollPane scroll;
	private Object[][] data;
	private Object[] columnNames = new Object[] { Messages.getString("ForecastingDocumentWindow.tbDetailedReport.column.period"), Messages.getString("ForecastingDocumentWindow.tbDetailedReport.column.stockOnHand"), Messages.getString("ForecastingDocumentWindow.tbDetailedReport.column.EstimatedConsumptionPrev"), Messages.getString("ForecastingDocumentWindow.tbDetailedReport.column.EstimatedConsumptionNew"), Messages.getString("ForecastingDocumentWindow.tbDetailedReport.column.EstimatedConsumptionTotal"), Messages.getString("ForecastingDocumentWindow.tbDetailedReport.column.StockOnOrder"), Messages.getString("ForecastingDocumentWindow.tbDetailedReport.column.QuantityLost"), Messages.getString("ForecastingDocumentWindow.tbDetailedReport.column.QuantityMissing") };
	private JComboBox medicineLst;
	private List<MedicineConsumption> medicines;
	private JPanel panel;
	private JPanel panel_1;
	private JLabel detailsLbl;
	private List<Integer> rowReds;
	private List<MedicineResume> summaryResume;
	private ForecastingCalculation calc;
	private Map<MedicineUIAdapter, List<PeriodResume>> cache = new HashMap<MedicineUIAdapter, List<PeriodResume>>();

	public DetailsPanel() {
		setLayout(new BorderLayout(0, 20));
		setSize(new Dimension(1000, 470));

		panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new GridLayout(0, 3, 0, 0));

		JLabel lblNewLabel = new JLabel(Messages.getString("ForecastingDocumentWindow.tbDetailedReport.medicine"));
		panel.add(lblNewLabel);
		lblNewLabel.setMinimumSize(new Dimension(122, 20));
		lblNewLabel.setPreferredSize(new Dimension(122, 20));
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		//Initialize JList
		medicineLst = new JComboBox();
		panel.add(medicineLst);
		medicineLst.setPreferredSize(new Dimension(350, 25));
		medicineLst.setMinimumSize(new Dimension(350, 25));
		medicineLst.addItem(Messages.getString("ForecastingDocumentWindow.tbDetailedReport.empty"));

		panel_1 = new JPanel();
		add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));

		detailsLbl = new JLabel();		
		detailsLbl.setMinimumSize(new Dimension(80, 25));
		detailsLbl.setPreferredSize(new Dimension(80, 25));
		panel_1.add(detailsLbl, BorderLayout.NORTH);
		scroll = new JScrollPane();
		panel_1.add(scroll);
		medicineLst.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = medicineLst.getSelectedIndex();
				if (selectedIndex == -1)
					return;
				if (selectedIndex == 0) {
					panel_1.remove(scroll);
					scroll = new JScrollPane();
					scroll.setBounds(0, 50, 1000, 429);
					panel_1.add(scroll);	
					detailsLbl.setText("");					
				} else {
					//Presenter.calculateMedicineResume(medicines.get(selectedIndex - 1).getMed());
					MedicineUIAdapter med = medicines.get(selectedIndex - 1).getMed();
					Presenter.getView().getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					List<PeriodResume> res = cache.get(med);
					if(res == null){
						res = getCalc().calcMedicineResume(med);
						cache.put(med, res);
					}
					Presenter.getView().getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					setData(res, med);
					setForecastingDetails(getCalc().getForecastUI().getDetailsInformationHTML());
				}
			}
		});
		//Initialize medicine consumption list
		medicines = new ArrayList<MedicineConsumption>();
		medicines.add(null);
	}
	

	public List<MedicineResume> getSummaryResume() {
		return summaryResume;
	}


	public void setSummaryResume(List<MedicineResume> summaryResume) {
		this.summaryResume = summaryResume;
	}

	public List<MedicineConsumption> getMedicines() {
		return medicines;
	}

	/**
	 * Set details information 
	 * @param details information (Country/Region/Facility/Person)
	 */
	public void setForecastingDetails(String details){
		detailsLbl.setText(details!=null?details:"");
	}
	/**
	 * Set medicine consumption list
	 * 
	 * @param medConsList
	 */
	public void setMedicines(List<MedicineConsumption> medConsList) {
		if (medicines != null && !medicines.isEmpty()) {
			List<String> medNames = new ArrayList<String>();
			medicines = medConsList;
			for (MedicineConsumption m : medicines) {
				medNames.add(m.getMed().getNameForDisplay());
			}
			medicineLst.removeAllItems();
			medicineLst.addItem(Messages.getString("ForecastingDocumentWindow.tbDetailedReport.empty"));
			for (Object o : medNames.toArray()) {
				medicineLst.addItem(o);
			}
		}
	}

	/**
	 * Set data to the table
	 * @param periodResumeList list of periods - exactly one month
	 * @param medicine to display
	 */
	public void setData(List<PeriodResume> periodResumeList, MedicineUIAdapter medicine) {
		if (periodResumeList != null && !periodResumeList.isEmpty()) {
			prepareData(periodResumeList, medicine);
			TableModel md = new DefaultTableModel(data, columnNames){
				private static final long serialVersionUID = 176864591773635354L;
				@Override
				public boolean isCellEditable(int row, int column) {				
					return false;
				}
			};
			JTable table = new JTable(md);
			table.setEnabled(true);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.getTableHeader().setResizingAllowed(false);
			table.getTableHeader().setReorderingAllowed(false);
			DashZeroCellRenderer cellRenderer = new DashZeroCellRenderer(false,null, null);
			DashZeroCellRenderer cellRenderer_bold = new DashZeroCellRenderer(true, null, Color.RED);
			DashZeroCellRenderer cellRenderer_red = new DashZeroCellRenderer(false, null, Color.RED, rowReds);

			table.getColumnModel().getColumn(0).setPreferredWidth(250);
			table.getColumnModel().getColumn(0).setCellRenderer(new PeriodResumeCellRenderer());
			table.getColumnModel().getColumn(1).setPreferredWidth(105);
			table.getColumnModel().getColumn(1).setCellRenderer(cellRenderer);
			table.getColumnModel().getColumn(2).setPreferredWidth(105);
			table.getColumnModel().getColumn(2).setCellRenderer(cellRenderer);
			table.getColumnModel().getColumn(3).setPreferredWidth(105);
			table.getColumnModel().getColumn(3).setCellRenderer(cellRenderer);
			table.getColumnModel().getColumn(4).setPreferredWidth(105);
			table.getColumnModel().getColumn(4).setCellRenderer(cellRenderer);
			table.getColumnModel().getColumn(5).setPreferredWidth(105);
			table.getColumnModel().getColumn(5).setCellRenderer(cellRenderer);
			table.getColumnModel().getColumn(6).setPreferredWidth(105);
			table.getColumnModel().getColumn(6).setCellRenderer(cellRenderer_bold);

			table.getColumnModel().getColumn(7).setCellRenderer(cellRenderer_red);
			//table.getColumnModel().getColumn(7).setCellRenderer(new MultiLineCellRenderer());
			Enumeration<TableColumn> en = table.getColumnModel().getColumns();
			while (en.hasMoreElements()) {
				((TableColumn) en.nextElement()).setHeaderRenderer(new DivMultiLineHeaderRenderer());
			}
			table.revalidate();
			table.repaint();
			table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			scroll.setViewportView(table);
		}
	}

	/**
	 * Prepare array of objects for detail table. Based on period resume list.
	 * @param periodResumeList period resume list.
	 * @param medicine 
	 */
	private void prepareData(List<PeriodResume> periodResumeList, MedicineUIAdapter medicine) {
		Integer accelQuantity = 0;
		for(MedicineResume mR : getSummaryResume()){
			if(mR.getMedicine().equals(medicine)){
				accelQuantity = mR.getLeadPeriod().getMissingInt();
				break;
			}
		}
		data = new Object[periodResumeList.size()][8];
		int index = 0;
		Integer totalRed = 0;
		rowReds = new ArrayList<Integer>();
		for (PeriodResume pr: periodResumeList){
			data[index][0] = pr;
			data[index][1] = pr.getIncomingBalance();
			data[index][2] = pr.getConsumedOldInt();
			data[index][3] = pr.getConsumedNewInt(); 
			data[index][4] = pr.getConsumedNewInt()+pr.getConsumedOldInt();
			data[index][5] = pr.getTransit();
			data[index][6] = pr.getExpired();
			data[index][7] = pr.getMissingInt();
			if(totalRed<accelQuantity){ //previous less then
				rowReds.add(index);
			}
			totalRed+=pr.getMissingInt(); 
			index++;
		}
	}

	/**
	 * Set current forecasting calculator
	 * @param calc
	 */
	public void setCalc(ForecastingCalculation calc, List<MedicineResume> resume) {
		this.calc=calc;
		clearCache(); //clear previous results if exist
		setMedicines(calc.getMedicineConsumption());
		setSummaryResume(resume);
	}

	private void clearCache() {
		this.cache.clear();
	}


	/**
	 * Get current forecasting calculator
	 * @return
	 */
	public ForecastingCalculation getCalc() {
		return calc;
	}
	


}
