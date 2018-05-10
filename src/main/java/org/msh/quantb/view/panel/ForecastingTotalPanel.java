package org.msh.quantb.view.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.msh.quantb.model.gen.DeliveryScheduleEnum;
import org.msh.quantb.services.calc.DeliveryOrdersControl;
import org.msh.quantb.services.calc.OrderCalculator;
import org.msh.quantb.services.io.DeliveryOrderUI;
import org.msh.quantb.services.io.ForecastingTotal;
import org.msh.quantb.services.io.ForecastingTotalMedicine;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.DashZeroCellRenderer;
import org.msh.quantb.view.EnumListRenderer;
import org.msh.quantb.view.NumericCellEditor;
import org.msh.quantb.view.PercentageCellEditor;
import org.msh.quantb.view.ToolTipCellRenderer;
import org.msh.quantb.view.tableExt.DivSimpleHeaderRenderer;

public class ForecastingTotalPanel extends JPanel {
	private static final int WORKAREA_WIDTH = 950;
	private static final int ADDSIZECOST = 20;
	private static final int ORDTABLE_COL_SIZE = 90;	
	private static final long serialVersionUID = -8941965219628994510L;
	private JTable orderTable;
	private JTable accOrderTable;
	private JLabel grandTotalCost;
	private JPanel commen1Panel;
	private JLabel comment1Lbl;
	private JTextField comment1Fld;
	private JPanel orderPanel;
	private JTabbedPane tabbedPane;
	private JScrollPane scrollOrder;
	private JScrollPane scrollAccOrder;
	private JScrollPane scrollOrderPanel;
	private JScrollPane scrollTotalOrder;
	private JTable totOrderTable;
	private TableColumn totalOrderCol;
	private FinalCostPanel totalOrdPanel;
	private FinalCostPanel regularOrdPanel;
	private FinalCostPanel accOrderPanel;
	private OrderCalculator orderCalculator;
	private JPanel regSubTotalPane;
	private JPanel accSubTotalPane;
	private JLabel regSubTotalLbl;
	private JLabel accSubTotalLbl;
	private JScrollPane scrollTotalPane;
	private JPanel totalPanel;
	private JLabel pSAccCopyBtn;
	private JLabel pPAccCopyBtn;
	private JPanel panel;
	private JPanel panel_1;
	private JLabel psRegularCopy;
	private JLabel pPRegularCopy;
	private JPanel schedulePane;
	private JScrollPane scheduleScrollPane;
	private JTable scheduleTable;
	private JPanel deliveriesPanel;
	private JPanel panel_2;
	private JLabel lblNewLabel;
	private JComboBox<DeliveryScheduleEnum> deliverySchedule;
	private JLabel lblNewLabel_1;
	private JComboBox<DeliveryScheduleEnum> acceleratedSchedule;

	/**
	 * Constructors pains UI, only UI and nothing else
	 */
	public ForecastingTotalPanel() {
		setSize(new Dimension(1000, 600));
		setLayout(new BorderLayout(5, 0));
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane);

		// main panel must be scroll panel
		scrollOrderPanel = new JScrollPane();
		//scrollOrderPanel.setSize(950, 2000);

		orderPanel = new JPanel();
		//orderPanel.setSize(950, 2000);
		JPanel placeForTotal = new JPanel();
		placeForTotal.setMinimumSize(new Dimension(WORKAREA_WIDTH, 50));
		placeForTotal.setPreferredSize(new Dimension(WORKAREA_WIDTH, 50));
		orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
		orderPanel.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
		scrollOrderPanel.setViewportView(orderPanel);

		commen1Panel = new JPanel();
		commen1Panel.setMinimumSize(new Dimension(950, 30));
		commen1Panel.setMaximumSize(new Dimension(950, 30));
		commen1Panel.setPreferredSize(new Dimension(950, 30));
		orderPanel.add(commen1Panel);
		commen1Panel.setLayout(null);

		comment1Lbl = new JLabel(Messages.getString("ForecastingDocumentWindow.order.commentlbl"));
		comment1Lbl.setBounds(152, 0, 133, 23);
		commen1Panel.add(comment1Lbl);

		comment1Fld = new JTextField();
		comment1Fld.setBounds(295, 0, 473, 23);
		commen1Panel.add(comment1Fld);
		comment1Fld.setColumns(60);



		// the first scroll - Regular order
		paintRegularOrder();
		// the second scroll - Accelerated order
		paintAccOrder();
		// the third scroll - Total order
		paintTotalOrder();

		orderPanel.add(placeForTotal);
		placeForTotal.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		grandTotalCost = new JLabel("");
		grandTotalCost.setSize(100, 70);
		grandTotalCost.setFont(new Font(this.getFont().getName(),Font.BOLD,this.getFont().getSize()));
		placeForTotal.add(grandTotalCost);

		// paint the final cost tab
		scrollTotalPane = new JScrollPane();
		scrollTotalPane.setBorder(null);
		totalPanel = new JPanel();
		scrollTotalPane.setViewportView(totalPanel);
		totalPanel.setLayout(new BoxLayout(totalPanel, BoxLayout.Y_AXIS));

		regularOrdPanel = new FinalCostPanel(Messages.getString("ForecastingDocumentWindow.order.regular"));
		regularOrdPanel.setPreferredSize(new Dimension(950, 290));
		totalPanel.add(regularOrdPanel);

		accOrderPanel = new FinalCostPanel(Messages.getString("ForecastingDocumentWindow.order.accel"));
		accOrderPanel.setPreferredSize(new Dimension(950, 290));
		totalPanel.add(accOrderPanel);

		totalOrdPanel = new FinalCostPanel(Messages.getString("ForecastingDocumentWindow.order.total"));
		totalOrdPanel.getFinalCostPanel().setVisible(false);
		totalOrdPanel.getMedTotalPanel().setVisible(false);
		totalOrdPanel.getTitlePanel().setVisible(false);
		totalOrdPanel.setMaximumSize(new Dimension(10000, 10000));
		totalOrdPanel.setPreferredSize(new Dimension(950, 290));
		Font gTf = totalOrdPanel.getGrandTotalValueLabel().getFont();
		totalOrdPanel.getGrandTotalValueLabel().setFont(gTf.deriveFont(gTf.getSize2D() + 2.0f));
		totalPanel.add(totalOrdPanel);

		//paint tabs
		tabbedPane.addTab(Messages.getString("ForecastingDocumentWindow.order.titleorder"), null, scrollOrderPanel, null);
		tabbedPane.addTab(Messages.getString("ForecastingDocumentWindow.order.totalTabName"), null, scrollTotalPane, null);

		schedulePane = new JPanel();
		tabbedPane.addTab(Messages.getString("ForecastingDocumentWindow.order.schedTabName"), null, schedulePane, null);
		schedulePane.setLayout(new BorderLayout(0, 0));
		scheduleScrollPane = new JScrollPane();
		schedulePane.add(scheduleScrollPane, BorderLayout.CENTER);

		deliveriesPanel = new JPanel();
		scheduleScrollPane.setViewportView(deliveriesPanel);

		panel_2 = new JPanel();
		schedulePane.add(panel_2, BorderLayout.NORTH);


		lblNewLabel = new JLabel(Messages.getString("DeliveryOrder.labels.schedule"));
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_2.add(lblNewLabel);

		deliverySchedule = new JComboBox<DeliveryScheduleEnum>();
		deliverySchedule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.getView().getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				paintDeliveries();
				Presenter.repaintStockGraph();
				Presenter.getView().getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});
		deliverySchedule.setModel(new DefaultComboBoxModel<DeliveryScheduleEnum>(DeliveryScheduleEnum.values()));
		deliverySchedule.setRenderer(new EnumListRenderer("DeliveryOrder.enum"));
		panel_2.add(deliverySchedule);

	}

	public JScrollPane getScheduleScrollPane() {
		return scheduleScrollPane;
	}

	public JTable getScheduleTable() {
		return scheduleTable;
	}

	public void setScheduleTable(JTable scheduleTable) {
		this.scheduleTable = scheduleTable;
	}

	public JPanel getSchedulePane() {
		return schedulePane;
	}

	public void setSchedulePane(JPanel schedulePane) {
		this.schedulePane = schedulePane;
	}

	public JPanel getDeliveriesPanel() {
		return deliveriesPanel;
	}

	public void setDeliveriesPanel(JPanel deliveriesPanel) {
		this.deliveriesPanel = deliveriesPanel;
	}


	public OrderCalculator getOrderCalculator() {
		return orderCalculator;
	}

	public void setOrderCalculator(OrderCalculator orderCalculator) {
		this.orderCalculator = orderCalculator;
	}

	public DeliveryOrdersControl getDeliveries() {
		return getOrderCalculator().getControl();
	}

	public JComboBox<DeliveryScheduleEnum> getDeliverySchedule() {
		return deliverySchedule;
	}

	public void setDeliverySchedule(JComboBox<DeliveryScheduleEnum> deliverySchedule) {
		this.deliverySchedule = deliverySchedule;
	}


	/**
	 * paint regular and accelerated order deliveries
	 * @param deliveries
	 */
	public void paintDeliveries() {
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
        		repaintDeliveries();
            };
		});
	}
	
	/**
	 * Re - calculate and paint regular and accelerated order deliveries
	 */
	public void recalcAndPaintDeliveries(){
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	getOrderCalculator().reExecute();
        		repaintDeliveries();
            };
		});
	}
	
	/**
	 * Paint deliveries logic
	 */
	public void repaintDeliveries() {
		getDeliveriesPanel().removeAll();
		getDeliveriesPanel().setVisible(false);
		DeliveryOrdersControl control = getDeliveries();
		control.buildAllExact();
		getDeliveriesPanel().setLayout(new GridLayout(0, 2,5,5));
		int i =1;
		//accelerated deliveries
		for(DeliveryOrderUI delivery : control.getAccelerated()){
			DeliveryOrderPanel dOp = new DeliveryOrderPanel("<html><font color='red'>"+Messages.getString("DeliveryOrder.labels.accOrdNo"));
			dOp.rebuildTable(delivery,i);
			getDeliveriesPanel().add(dOp);
			i++;
		}
		i=1;
		//regular deliveries
		for(DeliveryOrderUI delivery : control.getRegular()){
			DeliveryOrderPanel dOp = new DeliveryOrderPanel(Messages.getString("DeliveryOrder.labels.regularNo"));
			dOp.rebuildTable(delivery,i);
			getDeliveriesPanel().add(dOp);
			i++;
		}
		getDeliveriesPanel().setVisible(true);
	}

	/**
	 * Paint the regular order table
	 */
	private void paintRegularOrder() {
		JPanel regOrderPnl = new JPanel();
		regOrderPnl.setLayout(new BorderLayout(5, 5));
		JLabel regOrderLbl = new JLabel(Messages.getString("ForecastingDocumentWindow.order.regular"));
		regOrderLbl.setFont(new Font(this.getFont().getName(),Font.BOLD,this.getFont().getSize()));
		regOrderPnl.add(regOrderLbl, BorderLayout.WEST);
		orderPanel.add(regOrderPnl);


		panel = new JPanel();
		regOrderPnl.add(panel, BorderLayout.EAST);
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 5));

		pSAccCopyBtn = new JLabel(Messages.getString("ForecastingDocumentWindow.order.buttons.psacopy"));
		pSAccCopyBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (Presenter.showWarningStringStrict(Messages.getString("ForecastingDocumentWindow.order.buttons.psacopy.warning"))){
					getTotal().copyPackSizesFromAccel();
				}
			}
		});
		pSAccCopyBtn.setHorizontalAlignment(SwingConstants.RIGHT);
		paintLabelAsLink(pSAccCopyBtn);
		panel.add(pSAccCopyBtn);

		pPAccCopyBtn = new JLabel(Messages.getString("ForecastingDocumentWindow.order.buttons.ppacopy"));
		pPAccCopyBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (Presenter.showWarningStringStrict(Messages.getString("ForecastingDocumentWindow.order.buttons.ppacopy.warning"))){
					getTotal().copyPackPricesFromAccel();
				}
			}
		});
		pPAccCopyBtn.setHorizontalAlignment(SwingConstants.RIGHT);
		paintLabelAsLink(pPAccCopyBtn);
		panel.add(pPAccCopyBtn);

		scrollOrder = new JScrollPane();
		scrollOrder.setMinimumSize(new Dimension(WORKAREA_WIDTH, 300));
		scrollOrder.setPreferredSize(new Dimension(WORKAREA_WIDTH, 300));
		orderPanel.add(scrollOrder);
		if(orderTable != null){
			scrollOrder.remove(orderTable);
		}
		orderTable = new JTable();
		scrollOrder.setViewportView(orderTable);

		regSubTotalPane = new JPanel();
		orderPanel.add(regSubTotalPane);
		FlowLayout flowLayout_2 = (FlowLayout) regSubTotalPane.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		regSubTotalLbl = new JLabel("");
		regSubTotalLbl.setFont(new Font(this.getFont().getName(),Font.BOLD,this.getFont().getSize()));
		regSubTotalPane.add(regSubTotalLbl);
	}



	/**
	 * paint label in underline blue with the hand cursor
	 * @param label
	 */
	private void paintLabelAsLink(JLabel label) {
		label.setForeground(Color.BLUE);
		Font font = label.getFont();
		Map attributes = font.getAttributes();
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		label.setFont(font.deriveFont(attributes));
		label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	/**
	 * Paint the accelerated order table
	 */
	private void paintAccOrder() {
		JPanel accOrderPnl = new JPanel();
		accOrderPnl.setLayout(new BorderLayout(5, 5));
		JLabel label = new JLabel(Messages.getString("ForecastingDocumentWindow.order.accel"));
		label.setFont(new Font(this.getFont().getName(),Font.BOLD,this.getFont().getSize()));
		accOrderPnl.add(label, BorderLayout.WEST);
		orderPanel.add(accOrderPnl);

		panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setHgap(20);
		accOrderPnl.add(panel_1, BorderLayout.EAST);

		psRegularCopy = new JLabel(Messages.getString("ForecastingDocumentWindow.order.buttons.psrcopy"));
		psRegularCopy.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (Presenter.showWarningStringStrict(Messages.getString("ForecastingDocumentWindow.order.buttons.psrcopy.warning"))){
					getTotal().copyPackSizesFromRegular();
				}
			}
		});
		paintLabelAsLink(psRegularCopy);
		panel_1.add(psRegularCopy);

		pPRegularCopy = new JLabel(Messages.getString("ForecastingDocumentWindow.order.buttons.pprcopy"));
		pPRegularCopy.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (Presenter.showWarningStringStrict(Messages.getString("ForecastingDocumentWindow.order.buttons.pprcopy.warning"))){
					getTotal().copyPackPricesFromRegular();
				}
			}
		});
		paintLabelAsLink(pPRegularCopy);
		panel_1.add(pPRegularCopy);

		scrollAccOrder = new JScrollPane();
		scrollAccOrder.setMinimumSize(new Dimension(WORKAREA_WIDTH, 255));
		scrollAccOrder.setPreferredSize(new Dimension(WORKAREA_WIDTH, 255));
		orderPanel.add(scrollAccOrder);
		if(accOrderTable != null){
			scrollAccOrder.remove(accOrderTable);
		}
		accOrderTable = new JTable();
		scrollAccOrder.setViewportView(accOrderTable);

		accSubTotalPane = new JPanel();
		orderPanel.add(accSubTotalPane);
		FlowLayout flowLayout_2 = (FlowLayout) accSubTotalPane.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		accSubTotalLbl = new JLabel();
		accSubTotalLbl.setFont(new Font(this.getFont().getName(),Font.BOLD,this.getFont().getSize()));
		accSubTotalPane.add(accSubTotalLbl);
	}

	/**
	 * Paint the accelerated order table
	 */
	private void paintTotalOrder() {
		JPanel labelPnl = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) labelPnl.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		JLabel label = new JLabel(Messages.getString("ForecastingDocumentWindow.order.total"));
		label.setFont(new Font(this.getFont().getName(),Font.BOLD,this.getFont().getSize()));
		labelPnl.add(label);
		orderPanel.add(labelPnl);

		scrollTotalOrder = new JScrollPane();
		scrollTotalOrder.setMinimumSize(new Dimension(WORKAREA_WIDTH, 255));
		scrollTotalOrder.setPreferredSize(new Dimension(WORKAREA_WIDTH, 255));
		orderPanel.add(scrollTotalOrder);
		if(totOrderTable != null){
			scrollTotalOrder.remove(totOrderTable);
		}
		totOrderTable = new JTable();
		scrollTotalOrder.setViewportView(totOrderTable);
	}

	/**
	 * Show or hide the total cost tab depends on order medicine cost
	 */
	private void checkTotalTab() {
		checkAnyTotalTab(getTotal(), 1);
		checkAnyTotalTab(getTotal(), 2);
	}


	private void checkAnyTotalTab(ForecastingTotal tot, int tabNo) {
		if(tot == null){
			tabbedPane.setEnabledAt(tabNo, false);
			return;
		}
		if(tot.getMedTotal() == null){
			tabbedPane.setEnabledAt(tabNo, false);
			return;
		}
		if (tot.getMedTotal().compareTo(new BigDecimal(0)) == 0){
			tabbedPane.setEnabledAt(tabNo, false);
		}else{
			tabbedPane.setEnabledAt(tabNo, true);
		}

	}

	/**
	 * Set and bind total data
	 * @param oCalc order calculator, if null assume use existing
	 */
	public void setAndBind(OrderCalculator oCalc){
		if(oCalc != null){
			setOrderCalculator(oCalc);
		}
		if(orderTable != null){
			scrollOrder.remove(orderTable);
		}
		orderTable = new JTable();
		if(accOrderTable != null){
			scrollAccOrder.remove(accOrderTable);
		}
		accOrderTable = new JTable();
		//recalc total order's items. totals are from order calculator. Already computed!
		getTotal().addOrder(getTotalA());
		getTotal().addOrder(getTotalR());
		getTotal().cleanUpGrand();
		getTotal().recalcItemsFromOrders();
		initDataBindings();
		paintDeliveries();
		adjustOrderTable();
		adjustAccOrderTable();
		adjustTotalOrderTable();
		checkTotalTab();

		//define multiline headers
		DivSimpleHeaderRenderer renderer = new DivSimpleHeaderRenderer();
		DivSimpleHeaderRenderer rendererWhite = new DivSimpleHeaderRenderer();
		rendererWhite.setBackground(new Color(184,203,124,203));
		//assign multiline headers to the order table
		Enumeration<TableColumn> en = orderTable.getColumnModel().getColumns();
		int i=0;
		while (en.hasMoreElements()) {
			if (i>1 && i<5){
				((TableColumn) en.nextElement()).setHeaderRenderer(rendererWhite);
			}else{
				((TableColumn) en.nextElement()).setHeaderRenderer(renderer);
			}
			i++;
		}
		// make first column fixed for the order table
		int height = calcOrderTableHeight();
		scrollOrder.setPreferredSize(new Dimension(WORKAREA_WIDTH, height));
		scrollOrder.setMinimumSize(new Dimension(WORKAREA_WIDTH, height));
		scrollOrder.setViewportView(orderTable);

		//new FixedColumnTable(1, scrollOrder);

		//assign multiline headers to the accOrder table
		en = accOrderTable.getColumnModel().getColumns();
		i=0;
		while (en.hasMoreElements()) {
			TableColumn col = en.nextElement();
			if (i>1 && i<5){
				col.setHeaderRenderer(rendererWhite);
			}else{
				col.setHeaderRenderer(renderer);
			}
			i++;
		}
		// show the accelerated order table
		scrollAccOrder.setPreferredSize(new Dimension(WORKAREA_WIDTH, height));
		scrollAccOrder.setMinimumSize(new Dimension(WORKAREA_WIDTH, height));
		scrollAccOrder.setViewportView(accOrderTable);

		//assign multiline headers to the totalOrder table
		en = totOrderTable.getColumnModel().getColumns();
		i=0;
		while (en.hasMoreElements()) {
			TableColumn col = en.nextElement();
			col.setHeaderRenderer(renderer);
			i++;
		}
		// show the accelerated order table
		scrollTotalOrder.setPreferredSize(new Dimension(WORKAREA_WIDTH, height));
		scrollTotalOrder.setMinimumSize(new Dimension(WORKAREA_WIDTH, height));
		scrollTotalOrder.setViewportView(totOrderTable);
		// set and bind total, regular and accelerated order tabs
		totalOrdPanel.setAndBind(getTotal(), getTotal());
		regularOrdPanel.setAndBind(getTotalR(),getTotalA());
		accOrderPanel.setAndBind(getTotalA(), getTotalR());
		addListeners();
	}

	public void addListeners() {
		//additional listeners for regular and accelerated orders
		for(ForecastingTotalMedicine totm : getTotalA().getMedItems()){
			addChangeListeners(totm);
		}
		for(ForecastingTotalMedicine totm : getTotalR().getMedItems()){
			addChangeListeners(totm);
		}
		getTotalA().addPropertyChangeListener("itemValue", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				paintDeliveries();
			}
		});
		
		getTotalR().addPropertyChangeListener("itemValue", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				paintDeliveries();
			}
		});
		
		//show - hide
		getTotal().addPropertyChangeListener("medTotal", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				checkTotalTab();
				adjustTotalColumn();
			}
		});
		getTotalR().addPropertyChangeListener("medTotal", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				checkTotalTab();
				adjustTotalColumn();
			}
		});
		getTotalA().addPropertyChangeListener("medTotal", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				checkTotalTab();
				adjustTotalColumn();
			}
		});

	}
	/**
	 * Add change event listeners
	 * @param totm
	 */
	public void addChangeListeners(ForecastingTotalMedicine totm) {
		totm.addPropertyChangeListener("adjustIt", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				listenOrderParameters(evt);
			}
		});
		totm.addPropertyChangeListener("adjustItAccel", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				listenOrderParameters(evt);
			}
		});
		
		totm.addPropertyChangeListener("packSize", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				listenOrderParameters(evt);
			}
		});
		totm.addPropertyChangeListener("packSizeAccel", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				listenOrderParameters(evt);
			}
		});
		totm.addPropertyChangeListener("packPrice", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				listenOrderParameters(evt);
			}
		});
		totm.addPropertyChangeListener("packPriceAccel", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				listenOrderParameters(evt);
			}
		});

	}
	/**
	 * Listen order parameters recalculate and repaint
	 * @param evt
	 */
	public void listenOrderParameters(PropertyChangeEvent evt) {
		ForecastingTotalMedicine medTot = (ForecastingTotalMedicine) evt.getSource();
		getOrderCalculator().reExecuteForMedicine(medTot);
		paintDeliveries();
		Presenter.cleanStockGraph();
		Presenter.changeTotalOrderOnSummary(medTot);
	}

	/**
	 * Calculate any order table height. 
	 * Very empirical !
	 * @return empirical height
	 */
	private int calcOrderTableHeight() {
		int height = 300;
		if (getTotal() != null){
			if (getTotal().getMedItems() != null){
				height = getTotal().getMedItems().size() * 300 / 8;
				if (height < 150){
					height = 150;
				}
			}
		}
		return height;
	}


	/**
	 * Adjust order table columns and headers
	 */
	private void adjustOrderTable() {
		orderTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		orderTable.getTableHeader().setReorderingAllowed(false);
		orderTable.getTableHeader().setResizingAllowed(false);
		orderTable.setRowSelectionAllowed(true);
		orderTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		orderTable.getColumnModel().getColumn(0).setHeaderValue(Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.medicines"));
		orderTable.getColumnModel().getColumn(0).setMinWidth(400);
		orderTable.getColumnModel().getColumn(0).setPreferredWidth(400);
		orderTable.getColumnModel().getColumn(0).setCellRenderer(new ToolTipCellRenderer());
		orderTable.getColumnModel().getColumn(1).setHeaderValue(Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.quantityneeded"));
		orderTable.getColumnModel().getColumn(1).setMinWidth(ORDTABLE_COL_SIZE);
		orderTable.getColumnModel().getColumn(1).setPreferredWidth(ORDTABLE_COL_SIZE);
		orderTable.getColumnModel().getColumn(1).setCellRenderer(new DashZeroCellRenderer(false,null, null));
		orderTable.getColumnModel().getColumn(2).setHeaderValue(Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.adjcoef"));
		orderTable.getColumnModel().getColumn(2).setMinWidth(ORDTABLE_COL_SIZE);
		orderTable.getColumnModel().getColumn(2).setPreferredWidth(ORDTABLE_COL_SIZE);
		orderTable.getColumnModel().getColumn(2).setCellRenderer(new DashZeroCellRenderer(true, Color.WHITE, null));
		orderTable.getColumnModel().getColumn(2).setCellEditor(new PercentageCellEditor(new JTextField(), true));
		orderTable.getColumnModel().getColumn(3).setHeaderValue(Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.packsize"));
		orderTable.getColumnModel().getColumn(3).setMinWidth(ORDTABLE_COL_SIZE);
		orderTable.getColumnModel().getColumn(3).setPreferredWidth(ORDTABLE_COL_SIZE);
		orderTable.getColumnModel().getColumn(3).setCellEditor(new NumericCellEditor(new JTextField(10)));
		orderTable.getColumnModel().getColumn(3).setCellRenderer(new DashZeroCellRenderer(true, Color.WHITE, null));
		orderTable.getColumnModel().getColumn(4).setHeaderValue(Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.packprice"));
		orderTable.getColumnModel().getColumn(4).setMinWidth(ORDTABLE_COL_SIZE);
		orderTable.getColumnModel().getColumn(4).setPreferredWidth(ORDTABLE_COL_SIZE);
		orderTable.getColumnModel().getColumn(4).setCellRenderer(new DashZeroCellRenderer(true, Color.WHITE, null));
		orderTable.getColumnModel().getColumn(4).setCellEditor(new PercentageCellEditor(new JTextField(), true));
		orderTable.getColumnModel().getColumn(5).setHeaderValue(Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.quantityadjusted"));
		orderTable.getColumnModel().getColumn(5).setMinWidth(ORDTABLE_COL_SIZE+ADDSIZECOST);
		orderTable.getColumnModel().getColumn(5).setPreferredWidth(ORDTABLE_COL_SIZE+ADDSIZECOST);
		orderTable.getColumnModel().getColumn(5).setCellRenderer(new DashZeroCellRenderer(false,null, null));
		orderTable.getColumnModel().getColumn(6).setHeaderValue(Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.packajusted"));
		orderTable.getColumnModel().getColumn(6).setMinWidth(ORDTABLE_COL_SIZE);
		orderTable.getColumnModel().getColumn(6).setPreferredWidth(ORDTABLE_COL_SIZE);
		orderTable.getColumnModel().getColumn(6).setCellRenderer(new DashZeroCellRenderer(false,null, null));
		orderTable.getColumnModel().getColumn(7).setHeaderValue(Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.regordcost"));
		orderTable.getColumnModel().getColumn(7).setMinWidth(ORDTABLE_COL_SIZE+ADDSIZECOST);
		orderTable.getColumnModel().getColumn(7).setPreferredWidth(ORDTABLE_COL_SIZE);
		orderTable.getColumnModel().getColumn(7).setCellRenderer(new DashZeroCellRenderer(false,null, null));
	}

	/**
	 * Adjust the accelerated order table columns and headers
	 */
	private void adjustAccOrderTable() {
		accOrderTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		accOrderTable.getTableHeader().setReorderingAllowed(false);
		accOrderTable.getTableHeader().setResizingAllowed(false);
		accOrderTable.setRowSelectionAllowed(true);
		accOrderTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		accOrderTable.getColumnModel().getColumn(0).setHeaderValue(Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.medicines"));
		accOrderTable.getColumnModel().getColumn(0).setMinWidth(400);
		accOrderTable.getColumnModel().getColumn(0).setPreferredWidth(400);
		accOrderTable.getColumnModel().getColumn(0).setCellRenderer(new ToolTipCellRenderer());
		accOrderTable.getColumnModel().getColumn(1).setHeaderValue(Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.quantityneeded"));
		accOrderTable.getColumnModel().getColumn(1).setMinWidth(ORDTABLE_COL_SIZE);
		accOrderTable.getColumnModel().getColumn(1).setPreferredWidth(ORDTABLE_COL_SIZE);
		accOrderTable.getColumnModel().getColumn(1).setCellRenderer(new DashZeroCellRenderer(false,null, null));
		accOrderTable.getColumnModel().getColumn(2).setHeaderValue(Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.adjcoef"));
		accOrderTable.getColumnModel().getColumn(2).setMinWidth(ORDTABLE_COL_SIZE);
		accOrderTable.getColumnModel().getColumn(2).setPreferredWidth(ORDTABLE_COL_SIZE);
		accOrderTable.getColumnModel().getColumn(2).setCellRenderer(new DashZeroCellRenderer(true, Color.WHITE, null));
		accOrderTable.getColumnModel().getColumn(2).setCellEditor(new PercentageCellEditor(new JTextField(), true));
		accOrderTable.getColumnModel().getColumn(3).setHeaderValue(Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.packsize"));
		accOrderTable.getColumnModel().getColumn(3).setMinWidth(ORDTABLE_COL_SIZE);
		accOrderTable.getColumnModel().getColumn(3).setPreferredWidth(ORDTABLE_COL_SIZE);
		accOrderTable.getColumnModel().getColumn(3).setCellEditor(new NumericCellEditor(new JTextField(10)));
		accOrderTable.getColumnModel().getColumn(3).setCellRenderer(new DashZeroCellRenderer(true, Color.WHITE, null));
		accOrderTable.getColumnModel().getColumn(4).setHeaderValue(Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.packprice"));
		accOrderTable.getColumnModel().getColumn(4).setMinWidth(ORDTABLE_COL_SIZE);
		accOrderTable.getColumnModel().getColumn(4).setPreferredWidth(ORDTABLE_COL_SIZE);
		accOrderTable.getColumnModel().getColumn(4).setCellRenderer(new DashZeroCellRenderer(true, Color.WHITE, null));
		accOrderTable.getColumnModel().getColumn(4).setCellEditor(new PercentageCellEditor(new JTextField(), true));
		accOrderTable.getColumnModel().getColumn(5).setHeaderValue(Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.quantityadjusted"));
		accOrderTable.getColumnModel().getColumn(5).setMinWidth(ORDTABLE_COL_SIZE+ADDSIZECOST);
		accOrderTable.getColumnModel().getColumn(5).setPreferredWidth(ORDTABLE_COL_SIZE+ADDSIZECOST);
		accOrderTable.getColumnModel().getColumn(5).setCellRenderer(new DashZeroCellRenderer(false,null, null));
		accOrderTable.getColumnModel().getColumn(6).setHeaderValue(Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.packajusted"));
		accOrderTable.getColumnModel().getColumn(6).setMinWidth(ORDTABLE_COL_SIZE);
		accOrderTable.getColumnModel().getColumn(6).setPreferredWidth(ORDTABLE_COL_SIZE);
		accOrderTable.getColumnModel().getColumn(6).setCellRenderer(new DashZeroCellRenderer(false,null, null));
		accOrderTable.getColumnModel().getColumn(7).setHeaderValue(Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.accordcost"));
		accOrderTable.getColumnModel().getColumn(7).setMinWidth(ORDTABLE_COL_SIZE + ADDSIZECOST);
		accOrderTable.getColumnModel().getColumn(7).setPreferredWidth(ORDTABLE_COL_SIZE);
		accOrderTable.getColumnModel().getColumn(7).setCellRenderer(new DashZeroCellRenderer(false,null, null));
	}

	/**
	 * Adjust total order table columns and headers
	 */
	private void adjustTotalOrderTable() {
		totOrderTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		totOrderTable.getTableHeader().setReorderingAllowed(false);
		totOrderTable.getTableHeader().setResizingAllowed(false);
		totOrderTable.setRowSelectionAllowed(true);
		totOrderTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		totOrderTable.getColumnModel().getColumn(0).setHeaderValue(Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.medicines"));
		totOrderTable.getColumnModel().getColumn(0).setMinWidth(400);
		totOrderTable.getColumnModel().getColumn(0).setPreferredWidth(400);
		totOrderTable.getColumnModel().getColumn(0).setCellRenderer(new ToolTipCellRenderer());

		totOrderTable.getColumnModel().getColumn(1).setHeaderValue(Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.quantityneeded"));
		totOrderTable.getColumnModel().getColumn(1).setMinWidth(ORDTABLE_COL_SIZE);
		totOrderTable.getColumnModel().getColumn(1).setPreferredWidth(ORDTABLE_COL_SIZE);
		totOrderTable.getColumnModel().getColumn(1).setCellRenderer(new DashZeroCellRenderer(false,null, null));

		totOrderTable.getColumnModel().getColumn(2).setHeaderValue(Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.quantityadjusted"));
		totOrderTable.getColumnModel().getColumn(2).setMinWidth(ORDTABLE_COL_SIZE);
		totOrderTable.getColumnModel().getColumn(2).setPreferredWidth(ORDTABLE_COL_SIZE);
		totOrderTable.getColumnModel().getColumn(2).setCellRenderer(new DashZeroCellRenderer(false,null, null));
		if (totOrderTable.getColumnModel().getColumnCount()==4){
			totOrderTable.getColumnModel().getColumn(3).setHeaderValue(Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.totalcost"));
			totOrderTable.getColumnModel().getColumn(3).setMinWidth(ORDTABLE_COL_SIZE);
			totOrderTable.getColumnModel().getColumn(3).setPreferredWidth(ORDTABLE_COL_SIZE + ADDSIZECOST);
			totOrderTable.getColumnModel().getColumn(3).setCellRenderer(new DashZeroCellRenderer(false,null, null));
			totalOrderCol = totOrderTable.getColumnModel().getColumn(3);
		}
		adjustTotalColumn();
	}

	/**
	 * Show or hide total order column
	 * if grand total = 0 - hide, otherwise - show
	 */
	private void adjustTotalColumn() {
		if (this.getTotal().getMedTotal().compareTo(BigDecimal.ZERO)> 0){
			if (totOrderTable.getColumnModel().getColumnCount()==3){
				totOrderTable.getColumnModel().addColumn(totalOrderCol);
			}
		}else{
			if (totOrderTable.getColumnModel().getColumnCount()==4){
				totOrderTable.getColumnModel().removeColumn(totalOrderCol);
			}
		}
	}

	/**
	 * Get forecasting total
	 * @return
	 */
	public ForecastingTotal getTotal() {
		return getOrderCalculator().getTotal();
	}

	/**
	 * Get forecasting total regular order
	 * @return
	 */
	public ForecastingTotal getTotalR() {
		return getOrderCalculator().getTotalR();
	}
	/**
	 * Get forecasting total accelerated order
	 * @return
	 */
	public ForecastingTotal getTotalA() {
		return getOrderCalculator().getTotalA();
	}
	protected void initDataBindings() {
		BeanProperty<OrderCalculator, List<ForecastingTotalMedicine>> orderCalculatorBeanProperty = BeanProperty.create("totalR.medItems");
		JTableBinding<ForecastingTotalMedicine, OrderCalculator, JTable> jTableBinding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE, orderCalculator, orderCalculatorBeanProperty, orderTable);
		//
		BeanProperty<ForecastingTotalMedicine, String> forecastingTotalMedicineBeanProperty = BeanProperty.create("medicine.nameForDisplay");
		jTableBinding.addColumnBinding(forecastingTotalMedicineBeanProperty).setColumnName("New Column").setEditable(false);
		//
		BeanProperty<ForecastingTotalMedicine, Integer> forecastingTotalMedicineBeanProperty_1 = BeanProperty.create("regularQuant");
		jTableBinding.addColumnBinding(forecastingTotalMedicineBeanProperty_1).setColumnName("New Column").setEditable(false);
		//
		BeanProperty<ForecastingTotalMedicine, BigDecimal> forecastingTotalMedicineBeanProperty_2 = BeanProperty.create("adjustIt");
		jTableBinding.addColumnBinding(forecastingTotalMedicineBeanProperty_2).setColumnName("New Column");
		//
		BeanProperty<ForecastingTotalMedicine, Integer> forecastingTotalMedicineBeanProperty_3 = BeanProperty.create("packSize");
		jTableBinding.addColumnBinding(forecastingTotalMedicineBeanProperty_3).setColumnName("New Column");
		//
		BeanProperty<ForecastingTotalMedicine, BigDecimal> forecastingTotalMedicineBeanProperty_4 = BeanProperty.create("packPrice");
		jTableBinding.addColumnBinding(forecastingTotalMedicineBeanProperty_4).setColumnName("New Column");
		//
		BeanProperty<ForecastingTotalMedicine, Integer> forecastingTotalMedicineBeanProperty_5 = BeanProperty.create("adjustedRegular");
		jTableBinding.addColumnBinding(forecastingTotalMedicineBeanProperty_5).setColumnName("New Column").setEditable(false);
		//
		BeanProperty<ForecastingTotalMedicine, Integer> forecastingTotalMedicineBeanProperty_6 = BeanProperty.create("adjustedRegularPack");
		jTableBinding.addColumnBinding(forecastingTotalMedicineBeanProperty_6).setColumnName("New Column").setEditable(false);
		//
		BeanProperty<ForecastingTotalMedicine, BigDecimal> forecastingTotalMedicineBeanProperty_19 = BeanProperty.create("regularCost");
		jTableBinding.addColumnBinding(forecastingTotalMedicineBeanProperty_19).setColumnName("New Column").setEditable(false);
		//
		jTableBinding.bind();
		//
		BeanProperty<OrderCalculator, List<ForecastingTotalMedicine>> orderCalculatorBeanProperty_1 = BeanProperty.create("totalA.medItems");
		JTableBinding<ForecastingTotalMedicine, OrderCalculator, JTable> jTableBinding_1 = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE, orderCalculator, orderCalculatorBeanProperty_1, accOrderTable);
		//
		BeanProperty<ForecastingTotalMedicine, String> forecastingTotalMedicineBeanProperty_7 = BeanProperty.create("medicine.nameForDisplay");
		jTableBinding_1.addColumnBinding(forecastingTotalMedicineBeanProperty_7).setColumnName("New Column").setEditable(false);
		//
		BeanProperty<ForecastingTotalMedicine, Integer> forecastingTotalMedicineBeanProperty_8 = BeanProperty.create("accelQuant");
		jTableBinding_1.addColumnBinding(forecastingTotalMedicineBeanProperty_8).setColumnName("New Column").setEditable(false);
		//
		BeanProperty<ForecastingTotalMedicine, BigDecimal> forecastingTotalMedicineBeanProperty_9 = BeanProperty.create("adjustItAccel");
		jTableBinding_1.addColumnBinding(forecastingTotalMedicineBeanProperty_9).setColumnName("New Column");
		//
		BeanProperty<ForecastingTotalMedicine, Integer> forecastingTotalMedicineBeanProperty_10 = BeanProperty.create("packSizeAccel");
		jTableBinding_1.addColumnBinding(forecastingTotalMedicineBeanProperty_10).setColumnName("New Column");
		//
		BeanProperty<ForecastingTotalMedicine, BigDecimal> forecastingTotalMedicineBeanProperty_11 = BeanProperty.create("packPriceAccel");
		jTableBinding_1.addColumnBinding(forecastingTotalMedicineBeanProperty_11).setColumnName("New Column");
		//
		BeanProperty<ForecastingTotalMedicine, Integer> forecastingTotalMedicineBeanProperty_12 = BeanProperty.create("adjustAccel");
		jTableBinding_1.addColumnBinding(forecastingTotalMedicineBeanProperty_12).setColumnName("New Column").setEditable(false);
		//
		BeanProperty<ForecastingTotalMedicine, Integer> forecastingTotalMedicineBeanProperty_13 = BeanProperty.create("adjustedAccelPack");
		jTableBinding_1.addColumnBinding(forecastingTotalMedicineBeanProperty_13).setColumnName("New Column").setEditable(false);
		//
		BeanProperty<ForecastingTotalMedicine, BigDecimal> forecastingTotalMedicineBeanProperty_14 = BeanProperty.create("accelCost");
		jTableBinding_1.addColumnBinding(forecastingTotalMedicineBeanProperty_14).setColumnName("New Column").setEditable(false);
		//
		jTableBinding_1.bind();
		//
		BeanProperty<OrderCalculator, List<ForecastingTotalMedicine>> orderCalculatorBeanProperty_2 = BeanProperty.create("total.medItems");
		JTableBinding<ForecastingTotalMedicine, OrderCalculator, JTable> jTableBinding_2 = SwingBindings.createJTableBinding(UpdateStrategy.READ, orderCalculator, orderCalculatorBeanProperty_2, totOrderTable);
		//
		BeanProperty<ForecastingTotalMedicine, String> forecastingTotalMedicineBeanProperty_15 = BeanProperty.create("medicine.nameForDisplayWithAbbrev");
		jTableBinding_2.addColumnBinding(forecastingTotalMedicineBeanProperty_15).setColumnName("New Column");
		//
		BeanProperty<ForecastingTotalMedicine, Integer> forecastingTotalMedicineBeanProperty_16 = BeanProperty.create("bruttoQuant");
		jTableBinding_2.addColumnBinding(forecastingTotalMedicineBeanProperty_16).setColumnName("New Column");
		//
		BeanProperty<ForecastingTotalMedicine, Integer> forecastingTotalMedicineBeanProperty_17 = BeanProperty.create("total");
		jTableBinding_2.addColumnBinding(forecastingTotalMedicineBeanProperty_17).setColumnName("New Column");
		//
		BeanProperty<ForecastingTotalMedicine, BigDecimal> forecastingTotalMedicineBeanProperty_18 = BeanProperty.create("totalCost");
		jTableBinding_2.addColumnBinding(forecastingTotalMedicineBeanProperty_18).setColumnName("New Column");
		//
		jTableBinding_2.setEditable(false);
		jTableBinding_2.bind();
		//
		BeanProperty<OrderCalculator, String> orderCalculatorBeanProperty_3 = BeanProperty.create("forecast.totalComment1");
		BeanProperty<JTextField, String> jTextFieldBeanProperty = BeanProperty.create("text");
		AutoBinding<OrderCalculator, String, JTextField, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, orderCalculator, orderCalculatorBeanProperty_3, comment1Fld, jTextFieldBeanProperty);
		autoBinding.bind();
		//
		BeanProperty<OrderCalculator, String> orderCalculatorBeanProperty_4 = BeanProperty.create("totalR.formattedTotal");
		BeanProperty<JLabel, String> jLabelBeanProperty = BeanProperty.create("text");
		AutoBinding<OrderCalculator, String, JLabel, String> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ, orderCalculator, orderCalculatorBeanProperty_4, regSubTotalLbl, jLabelBeanProperty);
		autoBinding_1.bind();
		//
		BeanProperty<OrderCalculator, String> orderCalculatorBeanProperty_5 = BeanProperty.create("totalA.formattedTotal");
		AutoBinding<OrderCalculator, String, JLabel, String> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ, orderCalculator, orderCalculatorBeanProperty_5, accSubTotalLbl, jLabelBeanProperty);
		autoBinding_2.bind();
		//
		BeanProperty<OrderCalculator, String> orderCalculatorBeanProperty_6 = BeanProperty.create("total.formattedGrandMedTotal");
		AutoBinding<OrderCalculator, String, JLabel, String> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ, orderCalculator, orderCalculatorBeanProperty_6, grandTotalCost, jLabelBeanProperty);
		autoBinding_3.bind();
		//
		BeanProperty<OrderCalculator, DeliveryScheduleEnum> orderCalculatorBeanProperty_7 = BeanProperty.create("forecast.deliverySchedule");
		BeanProperty<JComboBox, Object> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
		AutoBinding<OrderCalculator, DeliveryScheduleEnum, JComboBox, Object> autoBinding_4 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, orderCalculator, orderCalculatorBeanProperty_7, deliverySchedule, jComboBoxBeanProperty);
		autoBinding_4.bind();
	}




}
