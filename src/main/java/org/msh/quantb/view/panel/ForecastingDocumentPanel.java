package org.msh.quantb.view.panel;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.text.PlainDocument;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.joda.time.LocalDate;
import org.msh.quantb.model.gen.RegimenTypesEnum;
import org.msh.quantb.services.calc.DateParser;
import org.msh.quantb.services.calc.ForecastingCalculation;
import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.ForecastUIVerify;
import org.msh.quantb.services.io.ForecastingBatchTmpStore;
import org.msh.quantb.services.io.ForecastingBatchUIAdapter;
import org.msh.quantb.services.io.ForecastingMedicineTmpStore;
import org.msh.quantb.services.io.ForecastingMedicineUIAdapter;
import org.msh.quantb.services.io.ForecastingOrderTmpStore;
import org.msh.quantb.services.io.ForecastingOrderUIAdapter;
import org.msh.quantb.services.io.ForecastingRegimenUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.DashZeroCellRenderer;
import org.msh.quantb.view.DateCellRenderer;
import org.msh.quantb.view.FixedColumnTable;
import org.msh.quantb.view.ForecastingRegimensNewCasesModel;
import org.msh.quantb.view.ForecastingRegimensNewCasesModelPers;
import org.msh.quantb.view.ForecastingRegimensOldCasesModelPers;
import org.msh.quantb.view.ForecastingRegimensTableModel;
import org.msh.quantb.view.HasRegimenData;
import org.msh.quantb.view.MonthCellRenderer;
import org.msh.quantb.view.NewCasesTableModel;
import org.msh.quantb.view.NumericCellEditor;
import org.msh.quantb.view.PercentageCellEditor;
import org.msh.quantb.view.TextAreaFilter;
import org.msh.quantb.view.ToolTipCellRenderer;
import org.msh.quantb.view.dialog.MedicinesAdjustDlg;
import org.msh.quantb.view.tableExt.DivMultiLineHeaderRenderer;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableHeaderCellRenderer;

import com.toedter.calendar.JDateChooser;
import javax.swing.border.LineBorder;
import javax.swing.border.EtchedBorder;
import java.awt.Font;

/**
 * Implementation of document of forecasting
 * 
 * @author User
 * 
 */
public class ForecastingDocumentPanel extends JPanel {
	private JTableBinding<ForecastingMedicineUIAdapter, ForecastUIAdapter, JTable> medicinesTableBinding;
	private static final String BY_PERCENTS = "byPercents";
	private static final String BY_QUANTITY = "byQuantity";
	private static final long serialVersionUID = -6021491035350834861L;
	private JTable casesOnTreatmentTable;
	private JTable percentageNewCasesTable;
	private JTable estimatedNumberTable;
	private JTable medicinesTable;
	private JTable stockOnOrderTable;
	private ForecastUIAdapter forecast;
	ForecastingCalculation calculator;
	private JDateChooser referenceDateDtCh;
	private JSpinner leadTimeSp;
	private JDateChooser endDateDtCh;
	private JLabel totalPercentLbl;
	private JTable batchesToExpireTable;

	private ForecastingMedicineTmpStore selectedMedicine;
	private ForecastingOrderTmpStore selectedOrder = new ForecastingOrderTmpStore(null);
	private ForecastingBatchTmpStore selectedBatch = new ForecastingBatchTmpStore(null);

	private JScrollPane stockOnOrderScrollPane;
	private JScrollPane batchesToExpireScrollPane;
	private int selectedBatchType;
	private JButton addBtn;
	private JButton delBtn;
	private JLabel batcheslbl;
	private JScrollPane byQuantityScroll;
	private File workFile;
	private SummaryPanel summaryPnl;
	private DashBoardPanel dashBoardPnl;
	private StockGraphPanel stockGraphPnl;
	private MedicineConsumptionPanel medicineConsumptionPnl;
	private DetailsPanel detailsPnl;
	private CasesOnTreatmentPanel casesOnTreatmentPnl;
	private JTabbedPane mainTabPane;
	private JTextField calculatorTxt;
	private JPanel cardsBatches;
	protected boolean isStockOnOrderVisible = true;
	private JTextField addrField;
	private ForecastingTotalPanel totalPnl;

	private JLabel monthLbl;
	private JTextArea commentTxt;
	private JTable percentageOldCasesTable;
	private JTabbedPane subTabPane;
	private JScrollPane numCasesOfTreatmentPane;
	private JLabel totalOldPercLbl;
	private JScrollPane oldCasesPercPanelScroll;
	private JTable numCasesOntreatmentTable;
	private JPanel byQuantityPanel;
	private JPanel casesOnTreatmentPanel;
	private JPanel layoutCOTPanel;	
	private JPanel layoutNECPanel;
	private JScrollPane byQNECScroll;
	private JTable newCasesTable;
	private JScrollPane newCasesPersPanelScroll;
	private JScrollPane numCasesNewPane;
	private JPanel detailsPanel_1;
	private JSpinner minStockSp;
	private JLabel maxStockLbl;
	private JLabel minMonthsLbl;
	private JLabel maxMonthsLbl;
	private JSpinner maxStockSp;
	private JPanel expectedTotalPanel;
	private JPanel enrolledTotalPanel;
	private JPanel newTotalPanel;
	private JPanel panel_6;
	private JLabel lblNewLabel_6;
	private JPanel panel_10;
	private JLabel lblNewLabel_7;
	private JButton enrolledPasteBtn;
	private JPanel panel_12;
	private JButton enrolledPastePersQtyBtn;
	private JPanel panel_13;
	private JButton expectedPasteBtn;
	private JPanel panel_14;
	private JButton expectedQPasteBtn;
	private JButton ediBtn;
	private ForecastingRegimensNewCasesModelPers modelP;
	private ForecastingRegimensOldCasesModelPers modelOp;
	private JButton expToPersBtn;
	private JButton expToQuanBtn;
	private JPanel panel_15;
	private JPanel panel_16;
	private JButton enrlToPersBtn;
	private JPanel panel_20;
	private JButton enrlToQuanBtn;
	private JLabel lblNewLabel_8;
	private JLabel lblNewLabel_10;
	private JLabel totalDuration;
	private JPanel panel_18;
	private JButton enrolledCopyBtn;
	private JButton enrolledCopyPersQtyBtn;
	private JButton expectedQCopyBtn;
	private JPanel panel_19;
	private JPanel panel_21;
	private JPanel panel_22;
	private JButton expectedCopyBtn;

	/*public class TextAreaDocFilter extends DocumentFilter {
		 the number of characters including spaces 
		private int maxLen = 177;

		public TextAreaDocFilter () { } 

		public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String str, AttributeSet attr) throws BadLocationException {
			if ((fb.getDocument().getLength() + str.length()) <= this.maxLen)
				super.replace(fb, offset, length, str, attr);
			else{
				String substr = "";
				if(fb.getDocument().getLength() < maxLen){
					int len = maxLen - fb.getDocument().getLength();
					if(len > 0)
						substr = str.substring(0, len);
					else
						substr = str;
				}
				super.replace(fb, offset, length, substr, attr);
			}
		}
	}*/

	/**
	 * Create the panel.
	 */
	public ForecastingDocumentPanel(ForecastUIAdapter forecast) {
		// create listeners for select unselect batches
		PropertyChangeListener oListen = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				getSelectedMedicine().getFcMedicine().firePropertyChange("stockOnOrderInt", null, getSelectedMedicine().getFcMedicine().getStockOnOrderInt());
				showEditOrderBatchBtns();
				setVisibleCalculationDetailsTabs(false);
			}
		};

		PropertyChangeListener bListen = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				getSelectedMedicine().getFcMedicine().firePropertyChange("batchesToExpireInt", 
						null, getSelectedMedicine().getFcMedicine().getBatchesToExpireInt());
				showEditBatchBtns();
				setVisibleCalculationDetailsTabs(false);
			}
		};

		selectedMedicine = new ForecastingMedicineTmpStore(bListen, oListen);

		this.setBackground(Color.WHITE);
		this.putClientProperty(
				SubstanceLookAndFeel.COLORIZATION_FACTOR, new Double(1.0));

		// paint forecasting parameters and result
		this.forecast = forecast;
		setSize(1100, 546);
		setLayout(new BorderLayout(0, 0));
		mainTabPane = new JTabbedPane(JTabbedPane.TOP);

		mainTabPane.setMinimumSize(new Dimension(1000, 5));
		add(mainTabPane);

		// paint forecasting parameters - both detail and subtabs with tables
		JPanel parametersPanel = new JPanel();
		parametersPanel.setMinimumSize(new Dimension(1000, 10));
		mainTabPane.addTab(Messages.getString("ForecastingDocumentWindow.tbParameters.title"), null, parametersPanel, null);
		parametersPanel.setLayout(new BorderLayout(0, 0));

		//paint forecasting parameters - detail only
		detailsPanel_1 = new JPanel();
		detailsPanel_1.setMinimumSize(new Dimension(1000, 120));
		detailsPanel_1.setPreferredSize(new Dimension(1000, 200));
		parametersPanel.add(detailsPanel_1, BorderLayout.NORTH);
		paintDetailParameters(detailsPanel_1);

		//paint forecasting parameters - subtabs only
		subTabPane = new JTabbedPane(JTabbedPane.TOP);
		subTabPane.setMaximumSize(new Dimension(1000, 250));
		subTabPane.setMinimumSize(new Dimension(1000, 300));
		subTabPane.setPreferredSize(new Dimension(1000, 300));
		parametersPanel.add(subTabPane);

		// paint all subtabs
		paintOldCasesSubTab();
		paintNewCasesSubTab();
		paintMedicinesSubTab();

		// put to work
		setAdditionalListeners();
		initDataBindings();
		redrawTables();
	}


	/**
	 * Paint medicines batches and orders subtab
	 */
	private void paintMedicinesSubTab() {
		JPanel selectedMedicinesPanel = new JPanel();
		subTabPane.addTab(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.title"), null, selectedMedicinesPanel, null);
		selectedMedicinesPanel.setLayout(new GridLayout(0, 2, 20, 0));

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(295, 70));
		panel.setMinimumSize(new Dimension(295, 70));
		selectedMedicinesPanel.add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JLabel lblNewLabel = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.subtitle"));
		lblNewLabel.setMinimumSize(new Dimension(100, 28));
		lblNewLabel.setPreferredSize(new Dimension(100, 28));
		panel.add(lblNewLabel, BorderLayout.NORTH);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setPreferredSize(new Dimension(295, 70));
		scrollPane_2.setMinimumSize(new Dimension(295, 50));
		panel.add(scrollPane_2);

		medicinesTable = new JTable();
		medicinesTable.getTableHeader().setReorderingAllowed(false);
		medicinesTable.getTableHeader().setResizingAllowed(false);
		medicinesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		medicinesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		scrollPane_2.setViewportView(medicinesTable);

		JPanel panel_4 = new JPanel();
		panel_4.setMinimumSize(new Dimension(295, 70));
		panel_4.setPreferredSize(new Dimension(295, 70));
		selectedMedicinesPanel.add(panel_4);
		panel_4.setLayout(new BorderLayout(0, 0));

		JPanel panel_2 = new JPanel();
		panel_2.setMinimumSize(new Dimension(295, 25));
		panel_2.setPreferredSize(new Dimension(295, 25));
		panel_4.add(panel_2, BorderLayout.NORTH);
		panel_2.setLayout(new BorderLayout(0, 0));

		batcheslbl = new JLabel();
		panel_2.add(batcheslbl, BorderLayout.WEST);

		JPanel panel_1 = new JPanel();
		panel_1.setPreferredSize(new Dimension(300, 25));
		panel_1.setMinimumSize(new Dimension(300, 25));
		panel_2.add(panel_1, BorderLayout.EAST);
		panel_1.setLayout(new GridLayout(0, 3, 5, 0));

		addBtn = new JButton(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.addBtn"));
		addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_1.add(addBtn);
		addBtn.setEnabled(false);
		addBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.addBatchToForecastingMedicine(selectedMedicine, selectedBatchType);
			}
		});

		ediBtn = new JButton(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.editBtn"));
		ediBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectedBatchType == 1 && selectedOrder.getForecastingOrder() != null){
					Presenter.openForEditForecastingOrder();
				}else{
					Presenter.openForEditForecastingButch();
				}
			}
		});
		ediBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_1.add(ediBtn);
		ediBtn.setEnabled(false);

		delBtn = new JButton(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.delBtn"));
		delBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		delBtn.setPreferredSize(new Dimension(43, 23));
		delBtn.setMinimumSize(new Dimension(43, 23));
		delBtn.setMaximumSize(new Dimension(43, 23));
		panel_1.add(delBtn);
		delBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectedBatchType != -1 && (selectedBatch.getForecastingBatch() != null || selectedOrder.getForecastingOrder() != null)) {
					Presenter.deleteSelectedBatches(selectedMedicine, selectedBatchType, selectedOrder, selectedBatch);
				}
			}
		});
		delBtn.setEnabled(false);

		cardsBatches = new JPanel();
		cardsBatches.setVisible(false);
		cardsBatches.setPreferredSize(new Dimension(395, 50));
		cardsBatches.setMinimumSize(new Dimension(395, 50));
		panel_4.add(cardsBatches);
		cardsBatches.setLayout(new CardLayout(0, 0));

		stockOnOrderScrollPane = new JScrollPane();
		stockOnOrderScrollPane.setMinimumSize(new Dimension(295, 70));
		stockOnOrderScrollPane.setPreferredSize(new Dimension(295, 70));
		cardsBatches.add(stockOnOrderScrollPane, "name_695117500322247");
		stockOnOrderScrollPane.setVisible(false);

		stockOnOrderTable = new JTable();
		stockOnOrderTable.getTableHeader().setReorderingAllowed(false);
		stockOnOrderTable.getTableHeader().setResizingAllowed(false);
		stockOnOrderTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		stockOnOrderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		stockOnOrderTable.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				// nothing to do				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() > 1) {
					if(getSelectedOrder() != null){
						if(getSelectedOrder().getForecastingOrder()!=null){
							if (getSelectedOrder().getForecastingOrder().getBatchInclude()){
								Presenter.openForEditForecastingOrder();
								//setVisibleCalculationDetailsTabs(false);
							}
						}
					}
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				stockOnOrderTable.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				stockOnOrderTable.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// nothing to do				
			}
		});
		stockOnOrderScrollPane.setViewportView(stockOnOrderTable);

		batchesToExpireScrollPane = new JScrollPane();
		batchesToExpireScrollPane.setMinimumSize(new Dimension(295, 70));
		batchesToExpireScrollPane.setPreferredSize(new Dimension(295, 70));
		cardsBatches.add(batchesToExpireScrollPane, "name_695117674572885");
		batchesToExpireScrollPane.setVisible(false);

		batchesToExpireTable = new JTable();
		batchesToExpireTable.getTableHeader().setReorderingAllowed(false);
		batchesToExpireTable.getTableHeader().setResizingAllowed(false);
		batchesToExpireTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		batchesToExpireTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		batchesToExpireTable.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				// nothing to do				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() > 1) {
					if(getSelectedBatch() != null){
						if(getSelectedBatch().getForecastingBatch() !=null){
							if (getSelectedBatch().getForecastingBatch().getInclude()){
								Presenter.openForEditForecastingButch();
								//setVisibleCalculationDetailsTabs(false);
							}
						}
					}
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				batchesToExpireTable.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				batchesToExpireTable.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// nothing to do				
			}
		});
		batchesToExpireScrollPane.setViewportView(batchesToExpireTable);

	}

	/**
	 * Paint new cases subtab
	 */
	private void paintNewCasesSubTab() {
		// main panel
		JPanel newCasesPanel = new JPanel();
		subTabPane.addTab(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.NewCases.title"), null, newCasesPanel, null);
		newCasesPanel.setLayout(new BorderLayout(5,5));
		//Group the radio buttons.
		ButtonGroup group = new ButtonGroup();

		layoutNECPanel = new JPanel();
		newCasesPanel.add(layoutNECPanel, BorderLayout.CENTER);
		layoutNECPanel.setLayout(new CardLayout(0, 0));

		//by quantity
		JPanel byQNECPanel = new JPanel();
		byQNECPanel.setLayout(new BorderLayout(5,5));
		byQNECScroll = new JScrollPane();
		byQNECPanel.add(byQNECScroll, BorderLayout.CENTER);
		layoutNECPanel.add(byQNECPanel, BY_QUANTITY);

		panel_10 = new JPanel();
		byQNECPanel.add(panel_10, BorderLayout.NORTH);
		panel_10.setLayout(new BorderLayout(5, 5));

		lblNewLabel_7 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.totalexpect") + " " +
				Messages.getString("ForecastingDocumentWindow.tbSummary.switchTo"));
		lblNewLabel_7.setHorizontalAlignment(SwingConstants.LEFT);
		panel_10.add(lblNewLabel_7, BorderLayout.WEST);

		panel_13 = new JPanel();
		panel_10.add(panel_13, BorderLayout.CENTER);
		panel_13.setLayout(new BorderLayout(5, 10));

		panel_21 = new JPanel();
		panel_13.add(panel_21, BorderLayout.EAST);



		expectedCopyBtn = new JButton(Messages.getString("ForecastingDocumentWindow.tbParameters.excel.copy")); //$NON-NLS-1$
		panel_21.add(expectedCopyBtn);
		expectedCopyBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Presenter.copyJTableCells(newCasesTable, 2);
			};
		}
				);

		panel_22 = new JPanel();
		panel_13.add(panel_22, BorderLayout.WEST);

		expToPersBtn = new JButton(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.CasesOnTreatment.byPercents")); //$NON-NLS-1$
		panel_22.add(expToPersBtn);
		expToPersBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Presenter.convertExpectedToPers()){
					switchToNewPercents();
					redrawEstimateNumberOfNewCasesTable();
					setVisibleCalculationDetailsTabs(false);
				}
			}
		});
		expectedPasteBtn = new JButton(Messages.getString("ForecastingDocumentWindow.tbParameters.excel.paste"));
		panel_21.add(expectedPasteBtn);
		expectedPasteBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (Presenter.pasteExpected(getForecast(), newCasesTable)){
					newCasesTable.repaint();
				};
			}
		});

		//by percents
		JPanel byPNECPanel = new JPanel();
		byPNECPanel.setLayout(new BorderLayout(3, 0));
		byPNECPanel.setMinimumSize(new Dimension(400, 70));
		layoutNECPanel.add(byPNECPanel, BY_PERCENTS);


		JPanel regPersNew = new JPanel();
		byPNECPanel.add(regPersNew, BorderLayout.WEST);
		regPersNew.setMinimumSize(new Dimension(350, 70));
		regPersNew.setPreferredSize(new Dimension(350, 70));
		regPersNew.setLayout(new BorderLayout(10, 0));

		String s1 = Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.NewCases.percTitle");
		if (this.getForecast().getRegimensType() == RegimenTypesEnum.SINGLE_DRUG){
			s1= Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.NewCases.percTitleMed");
		}

		JPanel panel_9 = new JPanel();
		panel_9.setPreferredSize(new Dimension(395, 28));
		panel_9.setMinimumSize(new Dimension(395, 28));
		regPersNew.add(panel_9, BorderLayout.NORTH);
		panel_9.setLayout(new BorderLayout(10, 0));

		panel_15 = new JPanel();
		panel_9.add(panel_15, BorderLayout.EAST);
		panel_15.setLayout(new BoxLayout(panel_15, BoxLayout.X_AXIS));
		JLabel lblNewLabel_2 = new JLabel(s1 + ". " + Messages.getString("ForecastingDocumentWindow.tbSummary.switchTo")); 
		lblNewLabel_2.setBorder(new EmptyBorder(0, 0, 0, 5));
		panel_15.add(lblNewLabel_2);
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);

		expToQuanBtn = new JButton(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.CasesOnTreatment.byQuantity")); //$NON-NLS-1$
		panel_15.add(expToQuanBtn);
		expToQuanBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Presenter.convertExpectedToQuan()){
					switchToNewQuantity();;
					redrawNewCasesTable();
					setVisibleCalculationDetailsTabs(false);
				}
			}
		});

		newCasesPersPanelScroll = new JScrollPane();
		regPersNew.add(newCasesPersPanelScroll, BorderLayout.CENTER);
		regPersNew.setMinimumSize(new Dimension(400, 70));
		regPersNew.setPreferredSize(new Dimension(600, 70));
		newCasesPersPanelScroll.setPreferredSize(new Dimension(295, 70));
		newCasesPersPanelScroll.setMinimumSize(new Dimension(395, 50));

		newTotalPanel = new JPanel();
		regPersNew.add(newTotalPanel, BorderLayout.SOUTH);
		newTotalPanel.setLayout(new BorderLayout(0, 0));

		expectedTotalPanel = new JPanel();
		expectedTotalPanel.setPreferredSize(new Dimension(85, 20));
		expectedTotalPanel.setMinimumSize(new Dimension(85, 20));
		newTotalPanel.add(expectedTotalPanel, BorderLayout.EAST);
		expectedTotalPanel.setLayout(null);
		totalPercentLbl = new JLabel();
		expectedTotalPanel.add(totalPercentLbl);
		totalPercentLbl.setBounds(0, -1, 50, 20);
		totalPercentLbl.setMinimumSize(new Dimension(50, 20));
		totalPercentLbl.setPreferredSize(new Dimension(50, 20));
		totalPercentLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		if (this.forecast.getRegimensType() == RegimenTypesEnum.SINGLE_DRUG){
			newTotalPanel.setVisible(false);
		}

		JLabel lblNewLabel_3 = new JLabel("%"); //$NON-NLS-1$
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_3.setBounds(62, -1, 23, 20);
		expectedTotalPanel.add(lblNewLabel_3);

		JLabel lblNewLabel_4 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.NewCases.TotalLbl"));
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.CENTER);
		newTotalPanel.add(lblNewLabel_4, BorderLayout.CENTER);

		JPanel regPersQNew = new JPanel();
		regPersQNew.setPreferredSize(new Dimension(480, 100));
		regPersQNew.setMinimumSize(new Dimension(450, 100));
		byPNECPanel.add(regPersQNew, BorderLayout.EAST);
		regPersQNew.setLayout(new BorderLayout(10, 5));

		numCasesNewPane = new JScrollPane();
		regPersQNew.add(numCasesNewPane);

		panel_14 = new JPanel();
		panel_14.setPreferredSize(new Dimension(10, 23));
		regPersQNew.add(panel_14, BorderLayout.NORTH);
		panel_14.setLayout(new BoxLayout(panel_14, BoxLayout.X_AXIS));

		JLabel label_7 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.NewCases.topRightLabel")+"   ");
		label_7.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_14.add(label_7);
		label_7.setHorizontalAlignment(SwingConstants.RIGHT);

		expectedQCopyBtn = new JButton(Messages.getString("ForecastingDocumentWindow.tbParameters.excel.copy"));
		panel_14.add(expectedQCopyBtn);
		expectedQCopyBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.copyJTableCells(estimatedNumberTable, 0);
			}
		});

		expectedQPasteBtn = new JButton(Messages.getString("ForecastingDocumentWindow.tbParameters.excel.paste"));
		expectedQPasteBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		expectedQPasteBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.expectedPastePersQty(getForecast(), estimatedNumberTable);
				estimatedNumberTable.repaint();
			}
		});
		panel_14.add(expectedQPasteBtn);




		redrawEstimateNumberOfNewCasesTable();
		//hide NEC panel if need
		if (this.forecast.getRegimensType() == RegimenTypesEnum.SINGLE_DRUG){			
			getForecast().getForecastObj().setIsNewPercents(true);
		}
		//set selected
		if(getForecast().getForecastObj().isIsNewPercents()){
			switchToNewPercents();
		}else{
			switchToNewQuantity();
		}

	}
	/**
	 * Paint tab for enrolled (old) cases parameters
	 */
	private void paintOldCasesSubTab() {
		// main panel for enrolled cases
		casesOnTreatmentPanel = new JPanel();
		casesOnTreatmentPanel.setLayout(new BorderLayout(5,5));
		subTabPane.addTab(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.CasesOnTreatment.title"), null, casesOnTreatmentPanel, null);
		//Group the radio buttons.
		ButtonGroup group = new ButtonGroup();

		layoutCOTPanel = new JPanel();
		casesOnTreatmentPanel.add(layoutCOTPanel, BorderLayout.CENTER);
		layoutCOTPanel.setLayout(new CardLayout(0, 0));
		//by quantity
		byQuantityPanel = new JPanel();
		byQuantityPanel.setLayout(new BorderLayout(5,5));
		byQuantityScroll = new JScrollPane();
		byQuantityScroll.setBorder(null);
		byQuantityPanel.add(byQuantityScroll, BorderLayout.CENTER);
		layoutCOTPanel.add(byQuantityPanel, BY_QUANTITY);

		panel_6 = new JPanel();
		byQuantityPanel.add(panel_6, BorderLayout.NORTH);
		panel_6.setLayout(new BorderLayout(0, 0));

		lblNewLabel_6 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.totalenroll"));
		lblNewLabel_6.setHorizontalAlignment(SwingConstants.LEFT);
		panel_6.add(lblNewLabel_6, BorderLayout.WEST);

		panel_16 = new JPanel();
		panel_16.setBorder(new EmptyBorder(0, 5, 0, 0));
		panel_6.add(panel_16, BorderLayout.CENTER);
		panel_16.setLayout(new BoxLayout(panel_16, BoxLayout.X_AXIS));

		lblNewLabel_8 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbSummary.switchTo"));
		lblNewLabel_8.setBorder(null);
		panel_16.add(lblNewLabel_8);

		enrlToPersBtn = new JButton(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.CasesOnTreatment.byPercents"));
		enrlToPersBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Presenter.convertEnrolledToPers()){
					switchToOldPercents();
					redrawNumCasesOnTreatmentTable();
					setVisibleCalculationDetailsTabs(false);
				}
			}
		});
		panel_16.add(enrlToPersBtn);

		panel_18 = new JPanel();
		panel_6.add(panel_18, BorderLayout.EAST);
		panel_18.setLayout(new GridLayout(0, 2, 0, 0));



		enrolledCopyBtn = new JButton(Messages.getString("ForecastingDocumentWindow.tbParameters.excel.copy")); //$NON-NLS-1$
		panel_18.add(enrolledCopyBtn);
		enrolledCopyBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Presenter.copyJTableCells(casesOnTreatmentTable,2);
			}
		});

		enrolledPasteBtn = new JButton(Messages.getString("ForecastingDocumentWindow.tbParameters.excel.paste"));
		panel_18.add(enrolledPasteBtn);
		enrolledPasteBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(Presenter.pasteEnrolled(getForecast(), casesOnTreatmentTable)){
					casesOnTreatmentTable.repaint();
				}

			}
		});



		//by percents
		JPanel byPercents = new JPanel();
		byPercents.setLayout(new BorderLayout(0, 0));
		JPanel regPersOld = new JPanel();
		byPercents.add(regPersOld, BorderLayout.WEST);
		regPersOld.setMinimumSize(new Dimension(400, 70));
		regPersOld.setPreferredSize(new Dimension(600, 70));
		regPersOld.setLayout(new BorderLayout(0, 0));
		layoutCOTPanel.add(byPercents, BY_PERCENTS);

		JPanel panel_9 = new JPanel();
		panel_9.setPreferredSize(new Dimension(395, 28));
		panel_9.setMinimumSize(new Dimension(395, 28));
		regPersOld.add(panel_9, BorderLayout.NORTH);
		panel_9.setLayout(new BorderLayout(5, 0));

		String s = Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.CasesOnTreatment.percTitle");
		if (this.getForecast().getRegimensType() == RegimenTypesEnum.SINGLE_DRUG){
			s = Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.CasesOnTreatment.percTitleMed");
		}

		panel_20 = new JPanel();
		panel_9.add(panel_20, BorderLayout.EAST);
		panel_20.setLayout(new BoxLayout(panel_20, BoxLayout.X_AXIS));
		JLabel label_3 = new JLabel(s + ". " + Messages.getString("ForecastingDocumentWindow.tbSummary.switchTo"));
		label_3.setPreferredSize(new Dimension(500, 14));
		label_3.setMaximumSize(new Dimension(500, 14));
		label_3.setBorder(new EmptyBorder(0, 0, 0, 5));
		panel_20.add(label_3);
		label_3.setMinimumSize(new Dimension(500, 28));
		label_3.setHorizontalAlignment(SwingConstants.RIGHT);

		enrlToQuanBtn = new JButton(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.CasesOnTreatment.byQuantity"));
		enrlToQuanBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Presenter.convertEnrolledToQuan()){
					switchToOldQuantity();
					redrawCasesOnTreatmentTable();
					setVisibleCalculationDetailsTabs(false);
				}
			}
		});
		panel_20.add(enrlToQuanBtn);

		oldCasesPercPanelScroll = new JScrollPane();
		oldCasesPercPanelScroll.setPreferredSize(new Dimension(295, 70));
		oldCasesPercPanelScroll.setMinimumSize(new Dimension(395, 50));
		regPersOld.add(oldCasesPercPanelScroll, BorderLayout.CENTER);

		enrolledTotalPanel = new JPanel();
		enrolledTotalPanel.setPreferredSize(new Dimension(278, 20));
		regPersOld.add(enrolledTotalPanel, BorderLayout.SOUTH);
		enrolledTotalPanel.setLayout(new BorderLayout(0, 0));

		JLabel label_4 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.NewCases.TotalLbl"));
		label_4.setHorizontalAlignment(SwingConstants.LEFT);
		enrolledTotalPanel.add(label_4, BorderLayout.WEST);

		JPanel panel_11 = new JPanel();
		panel_11.setLayout(null);
		panel_11.setPreferredSize(new Dimension(85, 20));
		panel_11.setMinimumSize(new Dimension(85, 20));
		enrolledTotalPanel.add(panel_11, BorderLayout.CENTER);

		if (this.forecast.getRegimensType() == RegimenTypesEnum.SINGLE_DRUG){
			enrolledTotalPanel.setVisible(false);
		}

		totalOldPercLbl = new JLabel();
		totalOldPercLbl.setPreferredSize(new Dimension(50, 20));
		totalOldPercLbl.setMinimumSize(new Dimension(50, 20));
		totalOldPercLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		totalOldPercLbl.setBounds(19, 0, 55, 20);
		panel_11.add(totalOldPercLbl);

		JLabel lblNewLabel_5 = new JLabel("%"); //$NON-NLS-1$
		lblNewLabel_5.setBounds(77, 0, 46, 20);
		panel_11.add(lblNewLabel_5);

		JPanel regPersQOld = new JPanel();
		regPersQOld.setPreferredSize(new Dimension(480, 100));
		regPersQOld.setMinimumSize(new Dimension(480, 100));
		byPercents.add(regPersQOld, BorderLayout.EAST);
		regPersQOld.setLayout(new BorderLayout(10, 5));

		numCasesOfTreatmentPane = new JScrollPane();
		numCasesOfTreatmentPane.setPreferredSize(new Dimension(190, 70));
		numCasesOfTreatmentPane.setMinimumSize(new Dimension(190, 70));
		regPersQOld.add(numCasesOfTreatmentPane, BorderLayout.CENTER);

		panel_12 = new JPanel();
		panel_12.setPreferredSize(new Dimension(10, 23));
		panel_12.setAlignmentY(0.7f);
		regPersQOld.add(panel_12, BorderLayout.NORTH);
		panel_12.setLayout(new BorderLayout(5, 5));

		JLabel label_7 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.CasesOnTreatment.cases"));
		panel_12.add(label_7, BorderLayout.WEST);
		label_7.setPreferredSize(new Dimension(240, 28));
		label_7.setMinimumSize(new Dimension(240, 28));

		enrolledCopyPersQtyBtn = new JButton(Messages.getString("ForecastingDocumentWindow.tbParameters.excel.copy")); //$NON-NLS-1$
		panel_12.add(enrolledCopyPersQtyBtn, BorderLayout.CENTER);
		enrolledCopyPersQtyBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Presenter.copyJTableCells(numCasesOntreatmentTable, 0);
			}
		});
		enrolledPastePersQtyBtn = new JButton(Messages.getString("ForecastingDocumentWindow.tbParameters.excel.paste"));
		/*		enrolledPastePersQtyBtn.setPreferredSize(new Dimension(110, 20));
		enrolledPastePersQtyBtn.setMinimumSize(new Dimension(110, 20));
		enrolledPastePersQtyBtn.setMaximumSize(new Dimension(110, 20));
		enrolledPastePersQtyBtn.setAlignmentY(Component.TOP_ALIGNMENT);*/
		enrolledPastePersQtyBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Presenter.enrolledPastePersQty(getForecast(),numCasesOntreatmentTable);
				numCasesOntreatmentTable.repaint();
			}
		});
		panel_12.add(enrolledPastePersQtyBtn, BorderLayout.EAST);



		JPanel spacePAnel = new JPanel();
		byPercents.add(spacePAnel, BorderLayout.CENTER);
		//hide COT panel if need
		if (this.forecast.getRegimensType() == RegimenTypesEnum.SINGLE_DRUG){			
			getForecast().getForecastObj().setIsOldPercents(true);
		}
		//set selected
		if(getForecast().getForecastObj().isIsOldPercents()){
			switchToOldPercents();
		}else{
			switchToOldQuantity();
		}

	}

	/**
	 * Redraw new cases percentage table, apply new binding
	 */
	public void redrawNewCasesPercentsTable(){
		//remove old table
		if (percentageNewCasesTable != null){
			newCasesPersPanelScroll.remove(percentageNewCasesTable);
			percentageNewCasesTable = null;
		}
		modelP = new ForecastingRegimensNewCasesModelPers(this.getForecast(), this);
		percentageNewCasesTable = new JTable(modelP);
		percentageNewCasesTable.getTableHeader().setReorderingAllowed(false);
		percentageNewCasesTable.getTableHeader().setResizingAllowed(true);
		percentageNewCasesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		percentageNewCasesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		newCasesPersPanelScroll.setViewportView(percentageNewCasesTable);
		percentageNewCasesTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		percentageNewCasesTable.setCellSelectionEnabled(true);

		//adjust columns
		String s = Messages.getString("Regimen.clmn.RegimenFull");
		if (this.getForecast().getRegimensType() == RegimenTypesEnum.SINGLE_DRUG){
			s = Messages.getString("Regimen.clmn.medicine");
		}
		percentageNewCasesTable.getColumnModel().getColumn(0).setHeaderValue(
				"<html>"+Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.NewCases.disable")+"<br>&nbsp</html>");
		percentageNewCasesTable.getColumnModel().getColumn(0).setPreferredWidth(70);
		percentageNewCasesTable.getColumnModel().getColumn(1).setHeaderValue(s);
		percentageNewCasesTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		percentageNewCasesTable.getColumnModel().getColumn(1).setPreferredWidth(400);
		percentageNewCasesTable.getColumnModel().getColumn(1).setCellRenderer(new ToolTipCellRenderer(Color.BLUE));
		percentageNewCasesTable.getColumnModel().getColumn(2).setHeaderValue(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.NewCases.percentClmn"));
		percentageNewCasesTable.getColumnModel().getColumn(2).setHeaderRenderer(new DivMultiLineHeaderRenderer());
		percentageNewCasesTable.getColumnModel().getColumn(2).setPreferredWidth(170);
		percentageNewCasesTable.getColumnModel().getColumn(2).setCellEditor(new PercentageCellEditor(new JTextField(), false));
		percentageNewCasesTable.getColumnModel().getColumn(2).setCellRenderer(new DashZeroCellRenderer(false, null, null){
			private static final long serialVersionUID = -2073398399078026563L;
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (modelP.isRowEditable(row)){
					setBackground(Color.WHITE);
				}else{
					setBackground(component.getBackground());
				}
				return component;
			}
		});
		addHandCursorToFirstCol(percentageNewCasesTable);
		addRegimenDecoder(modelP, percentageNewCasesTable, 1);
	}
	/**
	 * Redraw Old cases percentage table? apply new binding
	 */
	public void redrawOldCasesPercentsTable() {
		//remove old table
		if (percentageOldCasesTable != null){
			oldCasesPercPanelScroll.remove(percentageOldCasesTable);
			percentageOldCasesTable = null;
		}
		modelOp = new ForecastingRegimensOldCasesModelPers(this.getForecast(), this);
		percentageOldCasesTable = new JTable(modelOp);
		percentageOldCasesTable.getTableHeader().setReorderingAllowed(false);
		percentageOldCasesTable.getTableHeader().setResizingAllowed(true);
		percentageOldCasesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		percentageOldCasesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		oldCasesPercPanelScroll.setViewportView(percentageOldCasesTable);
		//adjust columns
		String s = Messages.getString("Regimen.clmn.RegimenFull");
		if (this.getForecast().getRegimensType() == RegimenTypesEnum.SINGLE_DRUG){
			s = Messages.getString("Regimen.clmn.medicine");
		}
		percentageOldCasesTable.getColumnModel().getColumn(1).setHeaderValue(s);
		percentageOldCasesTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		percentageOldCasesTable.getColumnModel().getColumn(1).setPreferredWidth(400);
		percentageOldCasesTable.getColumnModel().getColumn(1).setCellRenderer(new ToolTipCellRenderer(Color.BLUE));
		percentageOldCasesTable.getColumnModel().getColumn(2).setHeaderValue(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.CasesOnTreatment.percentClmn"));
		percentageOldCasesTable.getColumnModel().getColumn(2).setHeaderRenderer(new DivMultiLineHeaderRenderer());
		percentageOldCasesTable.getColumnModel().getColumn(2).setPreferredWidth(170);
		percentageOldCasesTable.getColumnModel().getColumn(2).setCellEditor(new PercentageCellEditor(new JTextField(), false));
		percentageOldCasesTable.getColumnModel().getColumn(2).setCellRenderer(new DashZeroCellRenderer(false, null, null){
			private static final long serialVersionUID = -6671527338063904879L;
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (modelP.isRowEditable(row)){
					setBackground(Color.WHITE);
				}else{
					setBackground(component.getBackground());
				}
				return component;
			}
		});
		addHandCursorToFirstCol(percentageOldCasesTable);
		addRegimenDecoder(modelOp, percentageOldCasesTable, 1);

	}

	/**
	 * paint form for detail forecasting parameters
	 * @param detailsPanel
	 */
	private void paintDetailParameters(JPanel detailsPanel) {
		detailsPanel_1.setLayout(new BoxLayout(detailsPanel_1, BoxLayout.X_AXIS));
		JPanel panel_3 = new JPanel();
		panel_3.setPreferredSize(new Dimension(550, 0));
		detailsPanel.add(panel_3);
		panel_3.setLayout(new GridLayout(3, 1, 0, 5));

		JPanel panel_5 = new JPanel();
		panel_3.add(panel_5);
		panel_5.setPreferredSize(new Dimension(110, 20));
		panel_5.setLayout(null);

		JLabel referenceDateLbl = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.referenceDate"));
		referenceDateLbl.setBounds(8, 0, 124, 32);
		panel_5.add(referenceDateLbl);

		referenceDateDtCh = new JDateChooser();
		referenceDateDtCh.getDateEditor().setEnabled(false);
		referenceDateDtCh.setLocale(new Locale(Messages.getLanguage(), Messages.getCountry()));
		referenceDateDtCh.setBounds(8, 43, 143, 20);
		panel_5.add(referenceDateDtCh);

		JLabel leadTime = new JLabel("<html><body style='width:130px'/>"+Messages.getString("ForecastingDocumentWindow.tbParameters.leadTime"));
		leadTime.setBounds(142, 0, 130, 32);
		panel_5.add(leadTime);

		leadTimeSp = new JSpinner();
		leadTimeSp.setBounds(162, 43, 48, 20);
		panel_5.add(leadTimeSp);
		leadTimeSp.setModel(new SpinnerNumberModel(1, 0, 30, 1));

		if (leadTimeSp.getEditor() instanceof JSpinner.DefaultEditor) {
			JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) leadTimeSp.getEditor();
			editor.getTextField().setEnabled(true);
			editor.getTextField().setEditable(false);
		}


		monthLbl = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.month"));
		monthLbl.setBounds(219, 46, 70, 14);
		panel_5.add(monthLbl);

		JLabel lblNewLabel_9 = new JLabel("<html><body style='width:130px'/>" + Messages.getString("ForecastingDocumentWindow.tbParameters.until"));
		lblNewLabel_9.setBounds(282, 0, 155, 35);
		panel_5.add(lblNewLabel_9);
		endDateDtCh = new JDateChooser();
		endDateDtCh.setBounds(292, 40, 122, 20);
		panel_5.add(endDateDtCh);
		endDateDtCh.getDateEditor().setEnabled(false);
		endDateDtCh.setLocale(new Locale(Messages.getLanguage(), Messages.getCountry()));

		lblNewLabel_10 = new JLabel("<html><body style='width:150px'/>" +Messages.getString("ForecastingDocumentWindow.tbParameters.quantperiod"));
		lblNewLabel_10.setBounds(447, 0, 140, 35);
		panel_5.add(lblNewLabel_10);

		totalDuration = new JLabel("");
		totalDuration.setBounds(447, 43, 183, 14);
		panel_5.add(totalDuration);
		endDateDtCh.getJCalendar().setWeekOfYearVisible(false);

		JPanel panel_7 = new JPanel();
		panel_7.setMinimumSize(new Dimension(10, 55));
		panel_7.setPreferredSize(new Dimension(10, 55));
		panel_3.add(panel_7);
		panel_7.setLayout(null);

		JLabel minStockLbl = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.minstock"));
		minStockLbl.setBounds(10, 0, 204, 14);
		panel_7.add(minStockLbl);

		minStockSp = new JSpinner();
		minStockSp.setBounds(20, 25, 49, 20);
		panel_7.add(minStockSp);
		minStockSp.setModel(new SpinnerNumberModel(1, 0, 30, 1));
		minStockSp.addChangeListener(new ChangeListener() {					
			@Override
			public void stateChanged(ChangeEvent e) {						
				ajustLabels();
			}
		});
		spinnerZeroAsDash(minStockSp);

		minMonthsLbl = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.month"));
		minMonthsLbl.setBounds(79, 32, 80, 14);
		panel_7.add(minMonthsLbl);

		maxStockLbl = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.maxstock"));
		maxStockLbl.setBounds(283, 0, 194, 14);
		panel_7.add(maxStockLbl);

		maxStockSp = new JSpinner();
		maxStockSp.setBounds(293, 25, 47, 20);
		panel_7.add(maxStockSp);
		maxStockSp.setModel(new SpinnerNumberModel(1, 0, 30, 1));
		maxStockSp.addChangeListener(new ChangeListener() {					
			@Override
			public void stateChanged(ChangeEvent e) {						
				ajustLabels();
			}
		});
		spinnerZeroAsDash(maxStockSp);

		maxMonthsLbl = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.month"));
		maxMonthsLbl.setBounds(350, 28, 87, 14);
		panel_7.add(maxMonthsLbl);


		JPanel panel_8 = new JPanel();
		panel_3.add(panel_8);
		panel_8.setLayout(null);

		Box horizontalBox = Box.createHorizontalBox();
		horizontalBox.setBounds(10, 0, 577, 63);
		panel_8.add(horizontalBox);
		horizontalBox.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		JLabel lblNewLabel_1 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.comment"));
		lblNewLabel_1.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		lblNewLabel_1.setSize(new Dimension(0, 14));
		lblNewLabel_1.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel_1.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		horizontalBox.add(lblNewLabel_1);
		lblNewLabel_1.setBorder(new EmptyBorder(0, 0, 0, 17));

		commentTxt = new JTextArea();
		commentTxt.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		TextAreaFilter filter = new TextAreaFilter(177);
		((PlainDocument) commentTxt.getDocument()).setDocumentFilter(filter);

		commentTxt.setAlignmentX(Component.RIGHT_ALIGNMENT);
		horizontalBox.add(commentTxt);
		commentTxt.setRows(4);
		commentTxt.setWrapStyleWord(true);
		commentTxt.setLineWrap(true);
		commentTxt.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		commentTxt.setColumns(10);

		referenceDateDtCh.getJCalendar().setWeekOfYearVisible(false);

		JPanel panel_17 = new JPanel();
		panel_17.setMinimumSize(new Dimension(450, 10));
		panel_17.setPreferredSize(new Dimension(450, 10));
		detailsPanel.add(panel_17);
		panel_17.setLayout(null);

		calculatorTxt = new JTextField();
		calculatorTxt.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		calculatorTxt.setBounds(40, 65, 447, 20);
		panel_17.add(calculatorTxt);
		calculatorTxt.setColumns(10);

		JLabel label_1 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.calculator"));
		label_1.setBounds(10, 48, 430, 14);
		panel_17.add(label_1);

		JLabel label_2 = new JLabel(Messages.getString("DlgForecastingWizard.address.label"));
		label_2.setBounds(10, 5, 258, 14);
		panel_17.add(label_2);

		addrField = new JTextField();
		addrField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		addrField.setColumns(10);
		addrField.setBounds(40, 25, 447, 20);
		panel_17.add(addrField);

		JButton selectRegimsBtn = new JButton(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.NewCases.selectRegimen")); //$NON-NLS-1$
		selectRegimsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Presenter.selectRegimens(ForecastingDocumentPanel.this);
				setVisibleCalculationDetailsTabs(false);
			}
		});
		selectRegimsBtn.setBounds(252, 96, 245, 44);
		panel_17.add(selectRegimsBtn);

		JButton executeBtn = new JButton(Messages.getString("ForecastingDocumentWindow.execute")); //$NON-NLS-1$
		executeBtn.setFont(new Font("SansSerif", Font.PLAIN, 16));
		executeBtn.setIconTextGap(50);
		executeBtn.setHorizontalAlignment(SwingConstants.LEFT);
		executeBtn.setIcon(new ImageIcon(ForecastingDocumentPanel.class.getResource("/org/msh/quantb/view/images/btnExecute.jpg")));
		executeBtn.setBackground(SystemColor.info);
		executeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Presenter.runForecastingCalculation();
			}
		});
		executeBtn.setBounds(78, 151, 335, 49);
		panel_17.add(executeBtn);

		JButton btnNewButton = new JButton("<html><body style='width:190px'/>" +Messages.getString("ForecastingDocumentWindow.adjustmed"));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MedicinesAdjustDlg dlg =Presenter.getView().createMedicinesAjustDlg(getForecast().getMedicines());
				dlg.setVisible(true);
			}
		});
		btnNewButton.setBounds(0, 96, 245, 44);
		panel_17.add(btnNewButton);
		ajustLabels();
		addListeners();
	}

	/**
	 * Add listeners to the forecasting
	 */
	private void addListeners() {
		referenceDateDtCh.addPropertyChangeListener("date",new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String err = ForecastUIVerify.checkInventoryDate(evt.getNewValue(), getForecast());
				if (err.length()>0){
					//referenceDateDtCh.setDate((Date) evt.getOldValue());
					Presenter.showWarningStringStrict(err);
				}
				totalDuration.setText(getDurationOfPeriod());
			}
		});

		endDateDtCh.addPropertyChangeListener("date", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String err = ForecastUIVerify.checkEndDate(evt.getNewValue(), getForecast());
				if (err.length()>0){
					endDateDtCh.setDate((Date) evt.getOldValue());
					Presenter.showError(err);
				}
				totalDuration.setText(getDurationOfPeriod());
			}
		});
	}
	

	public JSpinner getMinStockSp() {
		return minStockSp;
	}

	/**
	 * Fine tune the spinner - set zero as dash etc
	 * @param spinner 
	 */
	private void spinnerZeroAsDash(JSpinner spinner) {
		if (spinner.getEditor() instanceof JSpinner.DefaultEditor) {
			JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
			JFormattedTextField fld = editor.getTextField();
			fld.setFormatterFactory(new AbstractFormatterFactory() {
				@Override
				public AbstractFormatter getFormatter(JFormattedTextField tf) {
					return new AbstractFormatter(){
						@Override
						public Object stringToValue(String text) throws ParseException {
							if(text.equals("-")){
								return new Integer(0);
							}else{
								return new Integer(text);
							}
						}

						@Override
						public String valueToString(Object value) throws ParseException {
							if(value instanceof Integer){
								Integer intValue = (Integer) value;
								if(intValue == 0){
									return "-";
								}else{
									return intValue.toString();
								}
							}
							return null;
						}
					};
				}
			});
			fld.setEnabled(true);
			fld.setEditable(false);
		}
	}

	/**
	 * Make some labels adjustment, really need for not-English languages
	 */
	private void ajustLabels() {
		int value = ((SpinnerNumberModel)minStockSp.getModel()).getNumber().intValue();
		minMonthsLbl.setText("("+DateParser.getMonthLabel(value)+")");
		value = ((SpinnerNumberModel)maxStockSp.getModel()).getNumber().intValue();
		maxMonthsLbl.setText("("+DateParser.getMonthLabel(value)+")");
	}
	/**
	 * Standard binding not suit all situations.
	 * So, this method is place for all additional listeners
	 */
	private void setAdditionalListeners() {

		// recalculate quantities, validate percentage changes for percents of new cases in regimens and fill total
		this.forecast.addRegimensListener("percentNewCases", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				totalPercentLbl.setText(String.valueOf(ForecastingDocumentPanel.this.forecast.getTotalPercentage()));
			}
		});

		// validate percentage changes for percents of enrolled cases in regimens and fill total
		this.forecast.addRegimensListener("percentCasesOnTreatment", new PropertyChangeListener(){
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				totalOldPercLbl.setText(String.valueOf(ForecastingDocumentPanel.this.forecast.getTotalPercentageOld()));
			}
		});
		// listen to end date change
		endDateDtCh.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("date")) {
					totalDuration.setText(getDurationOfPeriod());
				}
			}
		});
		// lead time may be changed, modify label on screen
		leadTimeSp.addChangeListener(new ChangeListener() {					
			@Override
			public void stateChanged(ChangeEvent e) {						
				int value = ((SpinnerNumberModel)leadTimeSp.getModel()).getNumber().intValue();
				monthLbl.setText("("+DateParser.getMonthLabel(value)+")");
			}
		});

		// reference date changed in object, recalculated tables
		this.getForecast().addPropertyChangeListener("referenceDt", new PropertyChangeListener(){
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				Presenter.onReferenceDateChange();
			}
		});
		// end period date change
		this.getForecast().addPropertyChangeListener("endDate", new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Presenter.onReferenceDateChange();
			}

		});

		this.getForecast().addPropertyChangeListener("minStock", new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Integer bs = (Integer) evt.getNewValue();
				Presenter.onReferenceDateChange();

			}
		});
		// change order
		selectedOrder.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				showEditOrderBatchBtns();
			}
		});
		// change batch
		selectedBatch.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				showEditBatchBtns();
			}
		});
		// link medicines table to batches or orders
		medicinesTable.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
			@Override
			public void columnSelectionChanged(ListSelectionEvent e) {			
				if (medicinesTable.getSelectedColumn() == 1 && isStockOnOrderVisible) {
					((CardLayout) cardsBatches.getLayout()).next(cardsBatches);
					isStockOnOrderVisible = false;
				}
				if (medicinesTable.getSelectedColumn() == 2 && !isStockOnOrderVisible) {
					((CardLayout) cardsBatches.getLayout()).previous(cardsBatches);
					isStockOnOrderVisible = true;
				}
				selectedBatchType = (medicinesTable.getSelectedColumn() == 2 ? 1 : (medicinesTable.getSelectedColumn() == 1 ? 0 : -1));
				cardsBatches.setVisible(selectedBatchType != -1);
				batcheslbl.setText(selectedBatchType == 1 ? Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.stockLbl") : (selectedBatchType == 0 ? Messages
						.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.batchesLbl") : ""));
				addBtn.setEnabled(selectedBatchType != -1);
			}

			@Override
			public void columnRemoved(TableColumnModelEvent e) {
				// doing nothing
			}

			@Override
			public void columnMoved(TableColumnModelEvent e) {
				// doing nothing
			}

			@Override
			public void columnMarginChanged(ChangeEvent e) {
				// doing nothing
			}

			@Override
			public void columnAdded(TableColumnModelEvent e) {
				// doing nothing
			}
		});
	}

	/**
	 * Redraw tables after DataBinding
	 */
	public void redrawTables() {
		//redraw
		redrawCasesOnTreatmentTable();
		redrawNumCasesOnTreatmentTable();
		redrawOldCasesPercentsTable();
		redrawNewCasesTable();
		redrawEstimateNumberOfNewCasesTable();
		redrawNewCasesPercentsTable();
		//adjust
		medicinesTable.getColumnModel().getColumn(0).setHeaderValue(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.medicines"));
		medicinesTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		medicinesTable.getColumnModel().getColumn(0).setPreferredWidth(360);
		medicinesTable.getColumnModel().getColumn(0).setCellRenderer(new ToolTipCellRenderer());
		medicinesTable.getColumnModel().getColumn(1).setHeaderValue(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.stockOnHand"));
		medicinesTable.getColumnModel().getColumn(1).setPreferredWidth(100);
		medicinesTable.getColumnModel().getColumn(1).setCellRenderer(new DashZeroCellRenderer(false, null, null){
			private static final long serialVersionUID = -7689948999465009077L;
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if(getForecast().getMedicines().get(row).hasExcludedBatchesToExpire()){
					setBackground(Color.YELLOW);
				}else{
					setBackground(component.getBackground());
				}
				return component;
			}

		});
		medicinesTable.getColumnModel().getColumn(2).setHeaderValue(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.stockOnOrder"));
		medicinesTable.getColumnModel().getColumn(2).setPreferredWidth(100);
		medicinesTable.getColumnModel().getColumn(2).setCellRenderer(new DashZeroCellRenderer(false, null, null){
			private static final long serialVersionUID = 5797214604693264237L;
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if(getForecast().getMedicines().get(row).hasExcludedOrderBatchesToExpire()){
					setBackground(Color.YELLOW);
				}else{
					setBackground(component.getBackground());
				}
				return component;
			}
		});
		medicinesTable.getSelectionModel().setSelectionInterval(0, 0);

		stockOnOrderTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		if (stockOnOrderTable.getColumnModel().getColumnCount() != 0) {
			stockOnOrderTable.getColumnModel().getColumn(0).setHeaderValue("<html>" + 
					Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.NewCases.disable") + "<br>&nbsp</html>");
			stockOnOrderTable.getColumnModel().getColumn(1).setHeaderValue(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.receivingDate"));
			stockOnOrderTable.getColumnModel().getColumn(1).setCellRenderer(new DateCellRenderer());
			stockOnOrderTable.getColumnModel().getColumn(1).setPreferredWidth(100);
			stockOnOrderTable.getColumnModel().getColumn(2).setHeaderValue("<html><body style='width:80px'/>" +Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.expirationDate"));
			//stockOnOrderTable.getColumnModel().getColumn(2).setCellEditor(new MonthCellEditor(100, 20));
			stockOnOrderTable.getColumnModel().getColumn(2).setCellRenderer(new DateCellRenderer());
			stockOnOrderTable.getColumnModel().getColumn(2).setPreferredWidth(100);
			stockOnOrderTable.getColumnModel().getColumn(3).setHeaderValue(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.quantity"));
			//stockOnOrderTable.getColumnModel().getColumn(2).setCellEditor(new NumericCellEditor(new JTextField()));
			stockOnOrderTable.getColumnModel().getColumn(3).setPreferredWidth(108);
			stockOnOrderTable.getColumnModel().getColumn(3).setCellRenderer(new DashZeroCellRenderer(false, null, null));
			stockOnOrderTable.getColumnModel().getColumn(4).setHeaderValue(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.comment"));
			stockOnOrderTable.getColumnModel().getColumn(4).setCellRenderer(new ToolTipCellRenderer());
			stockOnOrderTable.getColumnModel().getColumn(4).setPreferredWidth(300);
		}
		batchesToExpireTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		if (batchesToExpireTable.getColumnModel().getColumnCount() != 0) {
			batchesToExpireTable.getColumnModel().getColumn(0).setHeaderValue("<html>" + 
					Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.NewCases.disable") + "<br>&nbsp</html>");
			batchesToExpireTable.getColumnModel().getColumn(1).setHeaderValue("<html><body style='width:80px'/>" +Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.expirationDate"));
			batchesToExpireTable.getColumnModel().getColumn(1).setCellRenderer(new DateCellRenderer());
			batchesToExpireTable.getColumnModel().getColumn(1).setPreferredWidth(80);
			batchesToExpireTable.getColumnModel().getColumn(2).setHeaderValue("<html><body style='width:88px'/>" +Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.quantity"));
			batchesToExpireTable.getColumnModel().getColumn(2).setPreferredWidth(88);
			batchesToExpireTable.getColumnModel().getColumn(2).setCellRenderer(new DashZeroCellRenderer(false, null, null));
			batchesToExpireTable.getColumnModel().getColumn(3).setHeaderValue(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.comment"));
			batchesToExpireTable.getColumnModel().getColumn(3).setCellRenderer(new ToolTipCellRenderer());
			batchesToExpireTable.getColumnModel().getColumn(3).setPreferredWidth(300);
		}

	}
	/**
	 * Enable quantity style only for old cases
	 */
	private void switchToOldQuantity() {
		//TODO
		//setIsOldPercents(false);		
		forecast.getForecastObj().setIsOldPercents(false);

		//switch to oldPercents
		CardLayout cl = (CardLayout) layoutCOTPanel.getLayout();
		cl.show(layoutCOTPanel, BY_QUANTITY);
	}
	/**
	 * Enable percentage style only for old cases
	 */
	private void switchToOldPercents() {
		//setIsOldPercents(true);
		forecast.getForecastObj().setIsOldPercents(true);

		//switch to oldPercents
		CardLayout cl = (CardLayout) layoutCOTPanel.getLayout();
		cl.show(layoutCOTPanel, BY_PERCENTS);
	}

	/**
	 * Enable quantity style only for new cases
	 */
	private void switchToNewQuantity() {
		//setIsNewPercents(false);
		forecast.getForecastObj().setIsNewPercents(false);

		//switch to oldPercents
		CardLayout cl = (CardLayout) layoutNECPanel.getLayout();
		cl.show(layoutNECPanel, BY_QUANTITY);
	}
	/**
	 * Enable percentage style only for new cases
	 */
	private void switchToNewPercents() {
		//setIsNewPercents(true);
		forecast.getForecastObj().setIsNewPercents(true);

		//switch to oldPercents
		CardLayout cl = (CardLayout) layoutNECPanel.getLayout();
		cl.show(layoutNECPanel, BY_PERCENTS);
	}

	/**
	 * Get forecasting UI object
	 * 
	 * @return UI obj
	 */
	public ForecastUIAdapter getForecast() {
		return forecast;
	}

	/**
	 * Set new regimens
	 * 
	 * @param selectedList regimens
	 */
	public void setRegimes(List<ForecastingRegimenUIAdapter> selectedList) {
		this.forecast.setRegimes(selectedList);
		//initDataBindings();
		redrawTables();
	}

	/*	*//**
	 * Set new cases
	 * 
	 * @param list new cases
	 *//*
	public void setNewCases(List<MonthQuantityUIAdapter> list) {
		this.forecast.setNewCases(list);
		initDataBindings();
		redrawTables();
	}*/

	/**
	 * Set new medicines and refresh batches tables
	 * 
	 * @param selectedList medicines
	 */
	public void setMedisinesAndRefresh(List<ForecastingMedicineUIAdapter> selectedList) {
		int selectedColumn = medicinesTable.getSelectedColumn();
		int selectedRow = medicinesTable.getSelectedRow();
		this.forecast.setMedicines(selectedList);
		//initDataBindings();
		redrawTables();
		medicinesTable.getSelectionModel().clearSelection();
		medicinesTable.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
		medicinesTable.getColumnModel().getSelectionModel().setSelectionInterval(selectedColumn, selectedColumn);
		this.selectedMedicine.getFcMedicine().refreshStockOnOrderInt();
		this.selectedMedicine.getFcMedicine().refreshBatchesToExpireInt();
	}

	/**
	 * Unselect all selections in forecasting medicine table and select, again,
	 * last selection.
	 */
	public void refreshBatchTables() {
		int selectedColumn = medicinesTable.getSelectedColumn();
		int selectedRow = medicinesTable.getSelectedRow();
		medicinesTable.getSelectionModel().clearSelection();
		medicinesTable.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
		medicinesTable.getColumnModel().getSelectionModel().setSelectionInterval(selectedColumn, selectedColumn);
		if(getSelectedMedicine().getFcMedicine() != null){
			getSelectedMedicine().getFcMedicine().refreshStockOnOrderInt();
			getSelectedMedicine().getFcMedicine().refreshBatchesToExpireInt();
		}
	}

	/**
	 * Refresh medicines table after global data changes like im[port form Excel
	 */
	public void refreshMedicinesTable(){
		refreshBatchTables();
		List<ForecastingMedicineUIAdapter> meds = getMedicinesTableBinding().getTargetValueForSource().getValue();
		for(ForecastingMedicineUIAdapter fMuI : meds){
			fMuI.firePropertyChange("batchesToExpireInt", null , fMuI.getBatchesToExpireInt());
			fMuI.firePropertyChange("stockOnOrderInt", null , fMuI.getStockOnOrderInt());
		}
	}

	/**
	 * Recreate forecasting regiment cases on treatment table
	 */
	public void redrawCasesOnTreatmentTable() {
		final ForecastingRegimensTableModel model = new ForecastingRegimensTableModel(this.forecast, this);
		if (casesOnTreatmentTable != null) {
			byQuantityScroll.remove(casesOnTreatmentTable);
		}
		casesOnTreatmentTable = new JTable(model);
		casesOnTreatmentTable.getTableHeader().setReorderingAllowed(false);
		casesOnTreatmentTable.getTableHeader().setResizingAllowed(false);
		casesOnTreatmentTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		casesOnTreatmentTable.setRowSelectionAllowed(false);
		byQuantityScroll.setViewportView(casesOnTreatmentTable);
		casesOnTreatmentTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		casesOnTreatmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		casesOnTreatmentTable.getColumnModel().getColumn(1).setPreferredWidth(400);
		casesOnTreatmentTable.getColumnModel().getColumn(1).setCellRenderer(new ToolTipCellRenderer(Color.BLUE));
		for (int i = 2; i < casesOnTreatmentTable.getColumnModel().getColumnCount(); i++) {
			casesOnTreatmentTable.getColumnModel().getColumn(i).setCellEditor(new NumericCellEditor(new JTextField(10)));
			casesOnTreatmentTable.getColumnModel().getColumn(i).setCellRenderer(new DashZeroCellRenderer(false, null, null){
				private static final long serialVersionUID = 1L;
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					if (model.isRowEditable(row)){
						if(model.isCellEditable(row, column+2)){  //the correction (+2) is important, because of use fixed column table
							setBackground(Color.WHITE);
						}else{
							setBackground(component.getBackground()); //leftmost may be empty
						}
					}else{
						setBackground(component.getBackground());
					}
					return component;
				}
			});
		}
		casesOnTreatmentTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		casesOnTreatmentTable.setCellSelectionEnabled(true);
		// right mouse menu
		casesOnTreatmentTable.addMouseListener(new PopUpMouseListener(new PastePopUp(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Presenter.pasteEnrolled(getForecast(), casesOnTreatmentTable);
				casesOnTreatmentTable.repaint();
			}

		},	new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Presenter.copyJTableCells(casesOnTreatmentTable, 2);

			}
		}
				)));
		// hotkey ctrl-v
		casesOnTreatmentTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK,false),
				"paste");
		casesOnTreatmentTable.getActionMap().put("paste",
				new AbstractAction(){
			private static final long serialVersionUID = -722958233367468235L;
			@Override
			public void actionPerformed(ActionEvent e) {
				Presenter.pasteEnrolled(getForecast(), casesOnTreatmentTable);
				casesOnTreatmentTable.repaint();
			}
		});
		final FixedColumnTable fCt = new FixedColumnTable(2, byQuantityScroll);
		JTable fixedPart = fCt.getFixedTable();
		addRegimenDecoder(model, fixedPart,1);
		addHandCursorToFirstCol(fixedPart);
	}
	/**
	 * Add hand cursor to the first col
	 * @param jt
	 */
	private void addHandCursorToFirstCol(final JTable jt) {
		MouseMotionAdapter mma;
		mma = new MouseMotionAdapter ()
		{
			public void mouseMoved (MouseEvent e)
			{
				// Return the pixel position of the mouse cursor
				// hotspot.

				Point p = e.getPoint ();

				// Convert the pixel position to the zero-based
				// column index of the table column over which the
				// mouse cursor hotspot is located. The result is a
				// view-based column index. If that index refers to
				// the leftmost column, display a crosshair cursor.
				// Otherwise, display a hand cursor.

				if (jt.columnAtPoint (p) == 0 || jt.columnAtPoint(p)==1)
					jt.setCursor (Cursor.getPredefinedCursor
							(Cursor.HAND_CURSOR));
				else
					jt.setCursor (Cursor.getPredefinedCursor
							(Cursor.DEFAULT_CURSOR));
			}
		};
		jt.addMouseMotionListener (mma);
	}

	/**
	 * Add to the table capability to display details of regimen 
	 * @param model
	 * @param table
	 * @param column
	 */
	private void addRegimenDecoder(final HasRegimenData model, final JTable table, final int column) {
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int row = table.rowAtPoint(evt.getPoint());
				int col = table.columnAtPoint(evt.getPoint());
				if(col == column){
					Presenter.getView().showRegimen(model.getData().get(row).getRegimen());
				}
			}
		});
	}

	/**
	 * Recreate forecasting regiment cases on treatment table
	 */
	public void redrawNewCasesTable() {
		final ForecastingRegimensNewCasesModel model = new ForecastingRegimensNewCasesModel(this.forecast, this);
		if (newCasesTable != null) {
			byQNECScroll.remove(newCasesTable);
		}
		newCasesTable = new JTable(model);
		newCasesTable.getTableHeader().setReorderingAllowed(false);
		newCasesTable.getTableHeader().setResizingAllowed(false);
		newCasesTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		byQNECScroll.setViewportView(newCasesTable);
		newCasesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		newCasesTable.setRowSelectionAllowed(false);
		newCasesTable.getColumnModel().getColumn(0).setPreferredWidth(70);
		newCasesTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
		newCasesTable.getColumnModel().getColumn(1).setPreferredWidth(400);
		newCasesTable.getColumnModel().getColumn(1).setCellRenderer(new ToolTipCellRenderer(Color.BLUE));
		//for (int i = 1; i < newCasesTable.getColumnModel().getColumnCount(); i++) { 20151013
		for (int i = 2; i < newCasesTable.getColumnModel().getColumnCount(); i++) {
			newCasesTable.getColumnModel().getColumn(i).setCellEditor(new NumericCellEditor(new JTextField(10)));
			newCasesTable.getColumnModel().getColumn(i).setCellRenderer(new DashZeroCellRenderer(false, null, null){
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					//setBackground(model.isCellEditable(row, column)?Color.WHITE:component.getBackground());
					if (model.isRowEditable(row)){
						setBackground(Color.WHITE);
					}else{
						setBackground(component.getBackground());
					}
					return component;
				}
			});
			newCasesTable.getColumnModel().getColumn(i).setHeaderRenderer(new SubstanceDefaultTableHeaderCellRenderer(){

				/**
				 * 
				 */
				private static final long serialVersionUID = 5766382897185773539L;
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					SubstanceDefaultTableHeaderCellRenderer render = (SubstanceDefaultTableHeaderCellRenderer) table.getTableHeader().getDefaultRenderer();
					String header = (String)value;
					Component component = (Component) render;
					if (header.endsWith("*")){
						component.setBackground(Color.YELLOW);
						putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, new Double(1.0));
						header = header.substring(0, header.length()-1);
					}else{
						component.setBackground(Color.LIGHT_GRAY);
						putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, new Double(1.0));
					}
					render.setText(header);
					return component;
				}

			});
		}
		newCasesTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		newCasesTable.setCellSelectionEnabled(true);
		// right mouse menu
		newCasesTable.addMouseListener(new PopUpMouseListener(new PastePopUp(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Presenter.pasteExpected(getForecast(), newCasesTable);
				newCasesTable.repaint();
			}
		}, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Presenter.copyJTableCells(newCasesTable, 2);
			}

		})));
		// hotkey ctrl-v
		newCasesTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK,false),
				"paste");
		newCasesTable.getActionMap().put("paste",
				new AbstractAction(){
			private static final long serialVersionUID = -722958233367468235L;
			@Override
			public void actionPerformed(ActionEvent e) {
				Presenter.pasteExpected(getForecast(), newCasesTable);
				newCasesTable.repaint();
			}
		});
		FixedColumnTable fCt = new FixedColumnTable(2, byQNECScroll);
		JTable fixed = fCt.getFixedTable();
		addRegimenDecoder(model, fixed, 1);
		addHandCursorToFirstCol(fixed);
	}






	/**
	 * Recreate table of estimated number of new cases
	 */
	public void redrawEstimateNumberOfNewCasesTable() {
		if (estimatedNumberTable != null){
			numCasesNewPane.remove(estimatedNumberTable);
			estimatedNumberTable = null;
		}

		estimatedNumberTable = new JTable(new NewCasesTableModel(this.forecast.getNewCases(), this));
		estimatedNumberTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		estimatedNumberTable.getTableHeader().setReorderingAllowed(false);
		estimatedNumberTable.getTableHeader().setResizingAllowed(false);
		estimatedNumberTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		estimatedNumberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		estimatedNumberTable.getColumnModel().getColumn(0).setPreferredWidth(120);
		estimatedNumberTable.getColumnModel().getColumn(0).setHeaderValue(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.NewCases.Month"));
		estimatedNumberTable.getColumnModel().getColumn(1).setCellEditor(new NumericCellEditor(new JTextField(10)));
		estimatedNumberTable.getColumnModel().getColumn(0).setCellRenderer(new MonthCellRenderer());
		estimatedNumberTable.getColumnModel().getColumn(1).setPreferredWidth(230);
		estimatedNumberTable.getColumnModel().getColumn(1).setHeaderValue(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.NewCases.estimatedClmn"));
		estimatedNumberTable.getColumnModel().getColumn(1).setCellRenderer(new DashZeroCellRenderer(false, Color.WHITE, null));
		numCasesNewPane.setViewportView(estimatedNumberTable);

		estimatedNumberTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		estimatedNumberTable.setCellSelectionEnabled(true);
		// right mouse menu
		estimatedNumberTable.addMouseListener(new PopUpMouseListener(new PastePopUp(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Presenter.expectedPastePersQty(getForecast(),estimatedNumberTable);
				estimatedNumberTable.repaint();
			}

		}, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Presenter.copyJTableCells(estimatedNumberTable, 0);
			}

		})));
		// hotkey ctrl-v
		estimatedNumberTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK,false),
				"paste");
		estimatedNumberTable.getActionMap().put("paste",
				new AbstractAction(){
			private static final long serialVersionUID = -722958233367468235L;
			@Override
			public void actionPerformed(ActionEvent e) {
				Presenter.expectedPastePersQty(getForecast(),estimatedNumberTable);
				estimatedNumberTable.repaint();
			}
		});
		getForecast().firePropertyChange("totalPercentage", null, getForecast().getTotalPercentage()); //recalc the total
	}

	/**
	 * Recreate table of estimated number cases on treatment
	 */
	public void redrawNumCasesOnTreatmentTable() {
		if (numCasesOntreatmentTable != null){
			numCasesOfTreatmentPane.remove(numCasesOntreatmentTable);
			numCasesOntreatmentTable = null;
		}
		numCasesOntreatmentTable = new JTable(new NewCasesTableModel(this.forecast.getCasesOnTreatment(), this));
		numCasesOntreatmentTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		numCasesOntreatmentTable.getTableHeader().setReorderingAllowed(false);
		numCasesOntreatmentTable.getTableHeader().setResizingAllowed(false);
		numCasesOntreatmentTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		numCasesOntreatmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		numCasesOfTreatmentPane.setViewportView(numCasesOntreatmentTable);
		numCasesOntreatmentTable.getColumnModel().getColumn(0).setPreferredWidth(120);
		numCasesOntreatmentTable.getColumnModel().getColumn(0).setHeaderValue(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.NewCases.Month"));
		numCasesOntreatmentTable.getColumnModel().getColumn(1).setCellEditor(new NumericCellEditor(new JTextField(10)));
		numCasesOntreatmentTable.getColumnModel().getColumn(0).setCellRenderer(new MonthCellRenderer());
		numCasesOntreatmentTable.getColumnModel().getColumn(1).setPreferredWidth(120);
		numCasesOntreatmentTable.getColumnModel().getColumn(1).setHeaderValue(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.CasesOnTreatment.existedClmn"));
		numCasesOntreatmentTable.getColumnModel().getColumn(1).setCellRenderer(new DashZeroCellRenderer(false, Color.WHITE, null));

		numCasesOntreatmentTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		numCasesOntreatmentTable.setCellSelectionEnabled(true);
		// right mouse menu
		numCasesOntreatmentTable.addMouseListener(new PopUpMouseListener(new PastePopUp(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Presenter.enrolledPastePersQty(getForecast(), numCasesOntreatmentTable);
				numCasesOntreatmentTable.repaint();
			}

		}, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Presenter.copyJTableCells(numCasesOntreatmentTable, 0);
			}

		})));
		// hotkey ctrl-v
		numCasesOntreatmentTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK,false),
				"paste");
		numCasesOntreatmentTable.getActionMap().put("paste",
				new AbstractAction(){
			private static final long serialVersionUID = -722958233367468235L;
			@Override
			public void actionPerformed(ActionEvent e) {
				Presenter.enrolledPastePersQty(getForecast(),numCasesOntreatmentTable);
				numCasesOntreatmentTable.repaint();
			}
		});
		getForecast().firePropertyChange("totalPercentageOld", null, getForecast().getTotalPercentage()); //recalc the total
	}



	/**
	 * Get duration of review period
	 * 
	 * @return number of months of the duration of review period
	 */
	public String getDurationOfPeriod() {		
		String s = "";
		if(getForecast().getFirstFCDate() != null && getForecast().getEndDate() != null){
			if(getForecast().getEndDate().compareTo(getForecast().getFirstFCDate())>0){
				return DateParser.getDurationOfPeriod(getForecast().getFirstFCDate().getTime(), getForecast().getEndDt());
			}
		}
		return "(" + s + ")";
	}


	/**
	 * Switch results tab on-off
	 * @param show true - on
	 */
	public void setVisibleCalculationDetailsTabs(boolean show) {
		getForecast().setDirty(!show);
		for(int i=1; i< mainTabPane.getComponentCount();i++){
			mainTabPane.setEnabledAt(i, show);
		}
	}

	/**
	 * Show details of forecasting calculation tab
	 */
	public void showCalculationDetailsTabs() {
		if (mainTabPane.getComponentCount() == 1) {
			JPanel summaryPanel = new JPanel();
			mainTabPane.addTab(Messages.getString("ForecastingDocumentWindow.tbSummary.title"), null, summaryPanel, null);

			this.summaryPnl = new SummaryPanel();

			summaryPanel.setBounds(0, 0, 985, 560);
			summaryPanel.setLayout(new BorderLayout(0, 0));
			summaryPanel.add(this.summaryPnl);

			this.medicineConsumptionPnl = new MedicineConsumptionPanel();

			JPanel medicinesReportPanel = new JPanel();
			medicinesReportPanel.setBounds(0, 0, 985, 560);
			medicinesReportPanel.setLayout(new BorderLayout(0, 0));
			medicinesReportPanel.add(this.medicineConsumptionPnl);
			mainTabPane.addTab(Messages.getString("ForecastingDocumentWindow.tbMedicinesReport.title"), null, medicinesReportPanel, null);

			this.casesOnTreatmentPnl = new CasesOnTreatmentPanel();
			JPanel casesReportPanel = new JPanel();
			casesReportPanel.setBounds(0, 0, 985, 560);
			casesReportPanel.setLayout(new BorderLayout(0, 0));
			casesReportPanel.add(casesOnTreatmentPnl);
			mainTabPane.addTab(Messages.getString("ForecastingDocumentWindow.tbCasesReport.title"), null, casesReportPanel, null);

			this.detailsPnl = new DetailsPanel();
			JPanel detailedReportPanel = new JPanel();
			detailedReportPanel.setBounds(0, 0, 985, 560);
			detailedReportPanel.setLayout(new BorderLayout(0, 0));
			detailedReportPanel.add(detailsPnl);
			mainTabPane.addTab(Messages.getString("ForecastingDocumentWindow.tbDetailedReport.title"), null, detailedReportPanel, null);

			totalPnl = new ForecastingTotalPanel();
			JPanel totalPanel = new JPanel();
			totalPanel.setBounds(0, 0, 985, 560);
			totalPanel.setLayout(new BorderLayout(0, 0));
			totalPanel.add(totalPnl);
			mainTabPane.addTab(Messages.getString("ForecastingDocumentWindow.order.tabname"), null, totalPanel, null);

			JTabbedPane dashBoardPanel = new JTabbedPane();
			//dashBoardPanel.setBounds(0, 0, 985, 560);
			//dashBoardPanel.setLayout(new BorderLayout(0, 0));
			dashBoardPnl = new DashBoardPanel();
			stockGraphPnl = new StockGraphPanel();
			dashBoardPanel.addTab(Messages.getString("ForecastingDocumentWindow.graph.stock.tabname"), null, stockGraphPnl, null);
			dashBoardPanel.addTab(Messages.getString("ForecastingDocumentWindow.dashBoard.tabname"), null, dashBoardPnl, null);
			mainTabPane.addTab(Messages.getString("ForecastingDocumentWindow.graph.tabname"), null, dashBoardPanel, null);
		}
		setVisibleCalculationDetailsTabs(true);
		mainTabPane.setSelectedIndex(1);
	}

	/**
	 * Set working file for current forecasting document
	 * 
	 * @param file file
	 */
	public void setWorkingFile(File file) {
		this.workFile = file;
	}

	/**
	 * Get working file for current forecasting document
	 * 
	 * @return file
	 */
	public File getWorkingFile() {
		return workFile;
	}

	/**
	 * @return the summaryPanel
	 */
	public SummaryPanel getSummaryPanel() {
		return summaryPnl;
	}

	/**
	 * @return the medicineConsumptionPanel
	 */
	public MedicineConsumptionPanel getMedicineConsumptionPanel() {
		return medicineConsumptionPnl;
	}

	/**
	 * @return the detailsPnl
	 */
	public DetailsPanel getDetailsPnl() {
		return detailsPnl;
	}

	/**
	 * @return the casesOnTreatmentPnl
	 */
	public CasesOnTreatmentPanel getCasesOnTreatmentPnl() {
		return casesOnTreatmentPnl;
	}

	/**
	 * @return the selectedOrder
	 */
	public ForecastingOrderTmpStore getSelectedOrder() {
		return selectedOrder;
	}

	/**
	 * @return the selectedBatch
	 */
	public ForecastingBatchTmpStore getSelectedBatch() {
		return selectedBatch;
	}

	/**
	 * @return the selectedMedicine
	 */
	public ForecastingMedicineTmpStore getSelectedMedicine() {
		return selectedMedicine;
	}

	/**
	 * @return the totalPnl
	 */
	public ForecastingTotalPanel getTotalPnl() {
		return totalPnl;
	}

	/**
	 * @return the dashBoardPanel
	 */
	public DashBoardPanel getDashBoardPanel() {
		return dashBoardPnl;
	}



	public StockGraphPanel getStockGraphPnl() {
		return stockGraphPnl;
	}

	/**
	 * Set new name of tab
	 * 
	 * @param newTabName
	 */
	public void setWorkingName(String newTabName) {
		setName(newTabName);
	}



	/**
	 * @return the subTabPane
	 */
	public JTabbedPane getSubTabPane() {
		return subTabPane;
	}


	public JTable getBatchesToExpireTable() {
		return batchesToExpireTable;
	}



	public JTable getMedicinesTable() {
		return medicinesTable;
	}
	public JTableBinding<ForecastingMedicineUIAdapter, ForecastUIAdapter, JTable> getMedicinesTableBinding() {
		return medicinesTableBinding;
	}
	public boolean isEnrolledCasesByPercentage(){
		return forecast.getForecastObj().isIsOldPercents();
	}

	public boolean isExpectedCasesByPercentage(){
		return forecast.getForecastObj().isIsNewPercents();
	}



	public ForecastingCalculation getCalculator() {
		return calculator;
	}

	public void setCalculator(ForecastingCalculation calculator) {
		this.calculator = calculator;
	}

	/**
	 * Show or hide edit batch in order buttons
	 */
	public void showEditOrderBatchBtns() {
		boolean included = false;
		if (selectedOrder.getForecastingOrder() != null){
			included = selectedOrder.getForecastingOrder().getBatchInclude();
		}
		delBtn.setEnabled(selectedBatchType == 1 && selectedOrder.getForecastingOrder() != null && included);
		ediBtn.setEnabled(selectedBatchType == 1 && selectedOrder.getForecastingOrder() != null && included);
	}
	public void showEditBatchBtns() {
		boolean included = false;
		if (selectedBatch.getForecastingBatch() != null){
			included = selectedBatch.getForecastingBatch().getInclude();
		}
		delBtn.setEnabled(selectedBatchType == 0 && selectedBatch.getForecastingBatch() != null && included);
		ediBtn.setEnabled(selectedBatchType == 0 && selectedBatch.getForecastingBatch() != null && included);
	}
	protected void initDataBindings() {
		BeanProperty<ForecastUIAdapter, Integer> forecastUIAdapterBeanProperty_1 = BeanProperty.create("leadTime");
		BeanProperty<JSpinner, Object> jSpinnerBeanProperty = BeanProperty.create("value");
		AutoBinding<ForecastUIAdapter, Integer, JSpinner, Object> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, forecast, forecastUIAdapterBeanProperty_1, leadTimeSp, jSpinnerBeanProperty);
		autoBinding_1.bind();
		//
		BeanProperty<ForecastUIAdapter, List<ForecastingMedicineUIAdapter>> forecastUIAdapterBeanProperty_7 = BeanProperty.create("medicines");
		medicinesTableBinding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE, forecast, forecastUIAdapterBeanProperty_7, medicinesTable);
		//
		BeanProperty<ForecastingMedicineUIAdapter, String> forecastingMedicineUIAdapterBeanProperty = BeanProperty.create("medicine.nameForDisplayWithAbbrev");
		medicinesTableBinding.addColumnBinding(forecastingMedicineUIAdapterBeanProperty).setColumnName("medicine").setEditable(false);
		//
		BeanProperty<ForecastingMedicineUIAdapter, Integer> forecastingMedicineUIAdapterBeanProperty_1 = BeanProperty.create("batchesToExpireInt");
		medicinesTableBinding.addColumnBinding(forecastingMedicineUIAdapterBeanProperty_1).setColumnName("batches to expire").setEditable(false);
		//
		BeanProperty<ForecastingMedicineUIAdapter, Integer> forecastingMedicineUIAdapterBeanProperty_2 = BeanProperty.create("stockOnOrderInt");
		medicinesTableBinding.addColumnBinding(forecastingMedicineUIAdapterBeanProperty_2).setColumnName("stock on order").setEditable(false);
		//
		medicinesTableBinding.bind();
		//
		BeanProperty<JTable, ForecastingMedicineUIAdapter> jTableBeanProperty = BeanProperty.create("selectedElement");
		BeanProperty<ForecastingMedicineTmpStore, ForecastingMedicineUIAdapter> forecastingMedicineTmpStoreBeanProperty = BeanProperty.create("fcMedicine");
		AutoBinding<JTable, ForecastingMedicineUIAdapter, ForecastingMedicineTmpStore, ForecastingMedicineUIAdapter> autoBinding_5 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, medicinesTable, jTableBeanProperty, selectedMedicine, forecastingMedicineTmpStoreBeanProperty);
		autoBinding_5.bind();
		//
		BeanProperty<JTable, List<ForecastingOrderUIAdapter>> jTableBeanProperty_1 = BeanProperty.create("selectedElement.ordersByArrival");
		JTableBinding<ForecastingOrderUIAdapter, JTable, JTable> jTableBinding_1 = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE, medicinesTable, jTableBeanProperty_1, stockOnOrderTable);
		//
		BeanProperty<ForecastingOrderUIAdapter, Boolean> forecastingOrderUIAdapterBeanProperty = BeanProperty.create("batchInclude");
		jTableBinding_1.addColumnBinding(forecastingOrderUIAdapterBeanProperty).setColumnClass(Boolean.class);
		//
		BeanProperty<ForecastingOrderUIAdapter, Date> forecastingOrderUIAdapterBeanProperty_1 = BeanProperty.create("arrivedDt");
		jTableBinding_1.addColumnBinding(forecastingOrderUIAdapterBeanProperty_1).setColumnName("arrival date").setEditable(false);
		//
		BeanProperty<ForecastingOrderUIAdapter, Date> forecastingOrderUIAdapterBeanProperty_2 = BeanProperty.create("batch.expiredDt");
		jTableBinding_1.addColumnBinding(forecastingOrderUIAdapterBeanProperty_2).setColumnName("expired").setEditable(false);
		//
		BeanProperty<ForecastingOrderUIAdapter, Integer> forecastingOrderUIAdapterBeanProperty_3 = BeanProperty.create("batchQuantity");
		jTableBinding_1.addColumnBinding(forecastingOrderUIAdapterBeanProperty_3).setColumnName("quantity").setEditable(false);
		//
		BeanProperty<ForecastingOrderUIAdapter, String> forecastingOrderUIAdapterBeanProperty_4 = BeanProperty.create("comment");
		jTableBinding_1.addColumnBinding(forecastingOrderUIAdapterBeanProperty_4).setColumnName("New Column").setEditable(false);
		//
		jTableBinding_1.bind();
		//
		BeanProperty<JTable, ForecastingOrderUIAdapter> jTableBeanProperty_3 = BeanProperty.create("selectedElement");
		BeanProperty<ForecastingOrderTmpStore, ForecastingOrderUIAdapter> forecastingOrderTmpStoreBeanProperty = BeanProperty.create("forecastingOrder");
		AutoBinding<JTable, ForecastingOrderUIAdapter, ForecastingOrderTmpStore, ForecastingOrderUIAdapter> autoBinding_6 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, stockOnOrderTable, jTableBeanProperty_3, selectedOrder, forecastingOrderTmpStoreBeanProperty);
		autoBinding_6.bind();
		//
		BeanProperty<ForecastUIAdapter, BigDecimal> forecastUIAdapterBeanProperty_8 = BeanProperty.create("totalPercentage");
		BeanProperty<JLabel, String> jLabelBeanProperty = BeanProperty.create("text");
		AutoBinding<ForecastUIAdapter, BigDecimal, JLabel, String> autoBinding_8 = Bindings.createAutoBinding(UpdateStrategy.READ, forecast, forecastUIAdapterBeanProperty_8, totalPercentLbl, jLabelBeanProperty);
		autoBinding_8.bind();
		//
		BeanProperty<ForecastUIAdapter, Date> forecastUIAdapterBeanProperty = BeanProperty.create("referenceDt");
		BeanProperty<JDateChooser, Date> jDateChooserBeanProperty = BeanProperty.create("date");
		AutoBinding<ForecastUIAdapter, Date, JDateChooser, Date> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, forecast, forecastUIAdapterBeanProperty, referenceDateDtCh, jDateChooserBeanProperty);
		autoBinding.bind();
		//
		BeanProperty<ForecastUIAdapter, Date> forecastUIAdapterBeanProperty_3 = BeanProperty.create("endDt");
		AutoBinding<ForecastUIAdapter, Date, JDateChooser, Date> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, forecast, forecastUIAdapterBeanProperty_3, endDateDtCh, jDateChooserBeanProperty);
		autoBinding_3.bind();
		//
		BeanProperty<ForecastUIAdapter, String> forecastUIAdapterBeanProperty_6 = BeanProperty.create("calculator");
		BeanProperty<JTextField, String> jTextFieldBeanProperty = BeanProperty.create("text");
		AutoBinding<ForecastUIAdapter, String, JTextField, String> autoBinding_9 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, forecast, forecastUIAdapterBeanProperty_6, calculatorTxt, jTextFieldBeanProperty);
		autoBinding_9.bind();
		//
		BeanProperty<ForecastUIAdapter, String> forecastUIAdapterBeanProperty_9 = BeanProperty.create("address");
		BeanProperty<JTextField, String> jTextFieldBeanProperty_1 = BeanProperty.create("text");
		AutoBinding<ForecastUIAdapter, String, JTextField, String> autoBinding_10 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, forecast, forecastUIAdapterBeanProperty_9, addrField, jTextFieldBeanProperty_1);
		autoBinding_10.bind();
		//
		BeanProperty<ForecastUIAdapter, BigDecimal> forecastUIAdapterBeanProperty_11 = BeanProperty.create("totalPercentageOld");
		AutoBinding<ForecastUIAdapter, BigDecimal, JLabel, String> autoBinding_12 = Bindings.createAutoBinding(UpdateStrategy.READ, forecast, forecastUIAdapterBeanProperty_11, totalOldPercLbl, jLabelBeanProperty);
		autoBinding_12.bind();
		//
		BeanProperty<ForecastUIAdapter, Integer> forecastUIAdapterBeanProperty_5 = BeanProperty.create("minStock");
		AutoBinding<ForecastUIAdapter, Integer, JSpinner, Object> autoBinding_13 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, forecast, forecastUIAdapterBeanProperty_5, minStockSp, jSpinnerBeanProperty);
		autoBinding_13.bind();
		//
		BeanProperty<ForecastUIAdapter, Integer> forecastUIAdapterBeanProperty_12 = BeanProperty.create("maxStock");
		AutoBinding<ForecastUIAdapter, Integer, JSpinner, Object> autoBinding_14 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, forecast, forecastUIAdapterBeanProperty_12, maxStockSp, jSpinnerBeanProperty);
		autoBinding_14.bind();
		//
		BeanProperty<ForecastingMedicineTmpStore, List<ForecastingBatchUIAdapter>> forecastingMedicineTmpStoreBeanProperty_1 = BeanProperty.create("fcMedicine.batchesToExpire");
		JTableBinding<ForecastingBatchUIAdapter, ForecastingMedicineTmpStore, JTable> jTableBinding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE, selectedMedicine, forecastingMedicineTmpStoreBeanProperty_1, batchesToExpireTable);
		//
		BeanProperty<ForecastingBatchUIAdapter, Boolean> forecastingBatchUIAdapterBeanProperty = BeanProperty.create("include");
		jTableBinding.addColumnBinding(forecastingBatchUIAdapterBeanProperty).setColumnName("New Column").setColumnClass(Boolean.class);
		//
		BeanProperty<ForecastingBatchUIAdapter, Date> forecastingBatchUIAdapterBeanProperty_1 = BeanProperty.create("expiredDt");
		jTableBinding.addColumnBinding(forecastingBatchUIAdapterBeanProperty_1).setColumnName("New Column").setEditable(false);
		//
		BeanProperty<ForecastingBatchUIAdapter, Integer> forecastingBatchUIAdapterBeanProperty_2 = BeanProperty.create("quantity");
		jTableBinding.addColumnBinding(forecastingBatchUIAdapterBeanProperty_2).setColumnName("New Column").setEditable(false);
		//
		BeanProperty<ForecastingBatchUIAdapter, String> forecastingBatchUIAdapterBeanProperty_3 = BeanProperty.create("comment");
		jTableBinding.addColumnBinding(forecastingBatchUIAdapterBeanProperty_3).setColumnName("New Column").setEditable(false);
		//
		jTableBinding.bind();
		//
		BeanProperty<ForecastingBatchTmpStore, ForecastingBatchUIAdapter> forecastingBatchTmpStoreBeanProperty = BeanProperty.create("forecastingBatch");
		BeanProperty<JTable, ForecastingBatchUIAdapter> jTableBeanProperty_2 = BeanProperty.create("selectedElement");
		AutoBinding<ForecastingBatchTmpStore, ForecastingBatchUIAdapter, JTable, ForecastingBatchUIAdapter> autoBinding_7 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, selectedBatch, forecastingBatchTmpStoreBeanProperty, batchesToExpireTable, jTableBeanProperty_2);
		autoBinding_7.bind();
		//
		BeanProperty<ForecastUIAdapter, String> forecastUIAdapterBeanProperty_10 = BeanProperty.create("comment");
		BeanProperty<JTextArea, String> jTextAreaBeanProperty = BeanProperty.create("text");
		AutoBinding<ForecastUIAdapter, String, JTextArea, String> autoBinding_11 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, forecast, forecastUIAdapterBeanProperty_10, commentTxt, jTextAreaBeanProperty);
		autoBinding_11.bind();
		//
		BeanProperty<ForecastUIAdapter, Boolean> forecastUIAdapterBeanProperty_13 = BeanProperty.create("only100Allowed");
		BeanProperty<JButton, Boolean> jButtonBeanProperty = BeanProperty.create("visible");
		AutoBinding<ForecastUIAdapter, Boolean, JButton, Boolean> autoBinding_15 = Bindings.createAutoBinding(UpdateStrategy.READ, forecast, forecastUIAdapterBeanProperty_13, enrlToPersBtn, jButtonBeanProperty);
		autoBinding_15.bind();
		//
		AutoBinding<ForecastUIAdapter, Boolean, JButton, Boolean> autoBinding_16 = Bindings.createAutoBinding(UpdateStrategy.READ, forecast, forecastUIAdapterBeanProperty_13, enrlToQuanBtn, jButtonBeanProperty);
		autoBinding_16.bind();
		//
		AutoBinding<ForecastUIAdapter, Boolean, JButton, Boolean> autoBinding_17 = Bindings.createAutoBinding(UpdateStrategy.READ, forecast, forecastUIAdapterBeanProperty_13, expToPersBtn, jButtonBeanProperty);
		autoBinding_17.bind();
		//
		AutoBinding<ForecastUIAdapter, Boolean, JButton, Boolean> autoBinding_18 = Bindings.createAutoBinding(UpdateStrategy.READ, forecast, forecastUIAdapterBeanProperty_13, expToQuanBtn, jButtonBeanProperty);
		autoBinding_18.bind();
	}
}
