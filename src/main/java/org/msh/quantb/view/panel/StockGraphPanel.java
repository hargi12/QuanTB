package org.msh.quantb.view.panel;

import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.msh.quantb.model.gen.DeliveryScheduleEnum;
import org.msh.quantb.services.calc.DateUtils;
import org.msh.quantb.services.calc.DeliveryOrdersControl;
import org.msh.quantb.services.calc.MedicineConsumption;
import org.msh.quantb.services.calc.OrderCalculator;
import org.msh.quantb.services.calc.PeriodResume;
import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.ForecastingTotalMedicine;
import org.msh.quantb.services.io.MedicineUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.EnumListRenderer;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import javax.swing.JCheckBox;

/**
 * Panel to display stock graphs for medicines
 * @author Alex Kurasoff
 *
 */
public class StockGraphPanel extends JPanel {
	private static final long serialVersionUID = 8242335465265960322L;

	private JComboBox medicineLst;
	private ChartPanel chartPanel;
	private List<MedicineConsumption> medConsumptions;
	private OrderCalculator orderCalculator;

	private JScrollPane scrollPane;

	private StockGraphModel chartModel;
	private JComboBox<DeliveryScheduleEnum> deliverySchedule;

	private JCheckBox turnOffDeliveries;



	/**
	 * Simple constructor
	 */
	public StockGraphPanel() {
		setLayout(new BorderLayout(0, 20));
		setSize(new Dimension(1000, 470));

		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		JLabel lblNewLabel = new JLabel(Messages.getString("ForecastingDocumentWindow.tbDetailedReport.medicine"));
		panel.add(lblNewLabel);
		lblNewLabel.setMinimumSize(new Dimension(122, 20));
		lblNewLabel.setPreferredSize(new Dimension(122, 20));
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		//Initialize JList
		medicineLst = new JComboBox();
		medicineLst.setMaximumSize(new Dimension(300, 32767));
		panel.add(medicineLst);
		medicineLst.setPreferredSize(new Dimension(100, 25));
		medicineLst.setMinimumSize(new Dimension(100, 25));
		medicineLst.addItem(Messages.getString("ForecastingDocumentWindow.tbDetailedReport.empty"));
		medicineLst.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				recalcGraph();
			}
		});

		JLabel lblNewLabel_1 = new JLabel("  " + Messages.getString("DeliveryOrder.labels.schedule"));
		panel.add(lblNewLabel_1);

		deliverySchedule = new JComboBox<DeliveryScheduleEnum>(); //we do not need action listener here. ForecastingTotalPanel will do all, because of binding!
		deliverySchedule.setMaximumSize(new Dimension(200, 32767));
		deliverySchedule.setModel(new DefaultComboBoxModel<DeliveryScheduleEnum>(DeliveryScheduleEnum.values()));
		deliverySchedule.setRenderer(new EnumListRenderer("DeliveryOrder.enum"));
		panel.add(deliverySchedule);

		turnOffDeliveries = new JCheckBox(Messages.getString("DeliveryOrder.labels.onoff"));
		turnOffDeliveries.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hideShowSchedule();
				recalcGraph();
			}
		});
		panel.add(turnOffDeliveries);

		scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		hideShowSchedule();
	}

	/**
	 * Enable - disable delivery schedule choice
	 */
	protected void hideShowSchedule() {
		getDeliverySchedule().setEnabled(!getTurnOffDeliveries().isSelected());
	}


	/**
	 * paint the graph
	 * @param orderData 
	 */
	protected void paintGraph(MedicineConsumption consumption, List<ForecastingTotalMedicine> orderData) {
		assert (getForecast() != null && getControl() != null);
		if(getScrollPane() == null){
			setScrollPane(new JScrollPane());
			add(getScrollPane(), BorderLayout.CENTER);
		}
		chartModel = new StockGraphModel(consumption,getForecast(),getControl(), getTurnOffDeliveries().isSelected(), orderData);
		//		JFreeChart chart = ChartFactory.createStackedBarChart("", "", "", chartModel.createBarDataSet(), 
		//				PlotOrientation.VERTICAL, true, true, false);
		BarRenderer.setDefaultBarPainter(new StandardBarPainter());
		JFreeChart chart = ChartFactory.createBarChart("", "", "", chartModel.createBarDataSet(), 
				PlotOrientation.VERTICAL, true, true, false);
		BarRenderer.setDefaultBarPainter(new StandardBarPainter());
		paintBars(chart);
		paintLines(chart);
		paintMarkers(chart);
		paintLegend(chart);
		scrollPane.setViewportView(getChartPanel());		

	}

	/**
	 * Place legend on the top
	 * @param chart 
	 */
	private void paintLegend(JFreeChart chart) {
		LegendTitle legend = chart.getLegend();
		legend.setPosition(RectangleEdge.TOP);
		legend.setItemFont(new Font("SansSerif", Font.PLAIN, 10));
	}


	/**
	 * Paint all necessary markers on the chart
	 * @param chart
	 */
	private void paintMarkers(JFreeChart chart) {
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		//paint begin of Forecasting period
		CategoryMarker beginMarker = new CategoryMarker(getForecast().getIniMonth());
		beginMarker.setLabel(Messages.getString("ForecastingDocumentWindow.dashBoard.column.LT"));
		beginMarker.setLabelFont(new Font("SansSerif", Font.ITALIC, 11));
		beginMarker.setLabelAnchor(RectangleAnchor.TOP);
		beginMarker.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
		beginMarker.setPaint(Color.ORANGE);
		beginMarker.setStroke(new BasicStroke(3.0f));
		beginMarker.setLabelOffset(new RectangleInsets(5,-10,0,0));
		beginMarker.setAlpha(0.5f);
		beginMarker.setDrawAsLine(true);
		plot.addDomainMarker(beginMarker);

	}


	/**
	 * paint lines on graph
	 * @param chart
	 * @param chartModel 
	 */
	private void paintLines(JFreeChart chart) {
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		final CategoryItemRenderer lineRenderer = new LineAndShapeRenderer();
		plot.setDataset(1, getChartModel().createLineDataSet());
		lineRenderer.setBaseToolTipGenerator(getChartModel().getToolTip());
		lineRenderer.setSeriesPaint(0, Color.BLUE);
		lineRenderer.setSeriesPaint(1, Color.MAGENTA);
		lineRenderer.setSeriesPaint(2, Color.GREEN);
		plot.setRenderer(1, lineRenderer);

	}


	/**
	 * paint bars on graph
	 * @param chart
	 */
	private void paintBars(JFreeChart chart) {
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		CategoryAxis xAxis = new CategoryAxis("");
		if(getChartModel().isVertical()){
			xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		}

		plot.setDomainAxis(xAxis);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.BLACK);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);
		plot.setRangePannable(true);
		plot.setBackgroundPaint(Color.white);
		plot.setDomainAxisLocation(AxisLocation.TOP_OR_LEFT);
		setChartPanel(new ChartPanel(chart));		
		getChartPanel().setDomainZoomable(false);
		getChartPanel().setRangeZoomable(false);
		getChartPanel().setDismissDelay(100000);
		getChartPanel().setPreferredSize(scrollPane.getPreferredSize());
		//StackedBarRenderer barrenderer = (StackedBarRenderer) plot.getRenderer();
		BarRenderer barrenderer = (BarRenderer) plot.getRenderer();
		barrenderer.setMaximumBarWidth(30.0);
		barrenderer.setMaximumBarWidth(30.0);
		barrenderer.setDrawBarOutline(false);
		barrenderer.setBaseToolTipGenerator(getChartModel().getToolTip());
		barrenderer.setSeriesPaint(0, Color.CYAN);
		barrenderer.setSeriesPaint(1, Color.RED);
		barrenderer.setSeriesPaint(2, Color.YELLOW);
		barrenderer.setSeriesPaint(3, Color.LIGHT_GRAY);
		barrenderer.setShadowVisible(false);
		barrenderer.setDrawBarOutline(true);
		//lines below emphases series paint.
		barrenderer.setBase(0.1);
		barrenderer.setItemMargin(0.0);


	}



	/**
	 * Completly remove the graph area
	 */
	public void cleanGraph() {
		if(getScrollPane() != null){
			remove(getScrollPane());
			getMedicineLst().setSelectedIndex(-1);
			setScrollPane(null);
			repaint();
		}
	}

	public StockGraphModel getChartModel() {
		return chartModel;
	}


	protected void setChartModel(StockGraphModel chartModel) {
		this.chartModel = chartModel;
	}


	protected List<MedicineConsumption> getMedConsumptions() {
		return medConsumptions;
	}


	protected void setMedConsumptions(List<MedicineConsumption> medConsumptions) {
		this.medConsumptions = medConsumptions;
	}


	protected ForecastUIAdapter getForecast() {
		return getOrderCalculator().getForecast();
	}


	protected JScrollPane getScrollPane() {
		return scrollPane;
	}



	protected void setScrollPane(JScrollPane scrollPane) {
		this.scrollPane = scrollPane;
	}



	protected JComboBox getMedicineLst() {
		return medicineLst;
	}




	protected void setMedicineLst(JComboBox medicineLst) {
		this.medicineLst = medicineLst;
	}




	protected ChartPanel getChartPanel() {
		return chartPanel;
	}




	protected void setChartPanel(ChartPanel chartPanel) {
		this.chartPanel = chartPanel;
	}

	public DeliveryOrdersControl getControl() {
		return getOrderCalculator().getControl();
	}




	public OrderCalculator getOrderCalculator() {
		return orderCalculator;
	}


	public void setOrderCalculator(OrderCalculator orderCalculator) {
		this.orderCalculator = orderCalculator;
	}


	public JCheckBox getTurnOffDeliveries() {
		return turnOffDeliveries;
	}


	public void setTurnOffDeliveries(JCheckBox turnOffDeliveries) {
		this.turnOffDeliveries = turnOffDeliveries;
	}


	public JComboBox<DeliveryScheduleEnum> getDeliverySchedule() {
		return deliverySchedule;
	}

	public void setDeliverySchedule(JComboBox<DeliveryScheduleEnum> deliverySchedule) {
		this.deliverySchedule = deliverySchedule;
	}

	public void setData(OrderCalculator oCalc) {
		int stored = getMedicineLst().getSelectedIndex();
		setOrderCalculator(oCalc);
		cleanGraph();
		setMedicines(oCalc.getControl().getConsumptions());
		initDataBindings();
		if(stored != -1){
			getMedicineLst().setSelectedIndex(stored);
		}
	}

	/**
	 * Set medicine consumption list
	 * 
	 * @param medConsList
	 */
	public void setMedicines(List<MedicineConsumption> medConsList) {
		setMedConsumptions(medConsList);
		List<String> medNames = new ArrayList<String>();
		for (MedicineConsumption m : medConsList) {
			medNames.add(m.getMed().getNameForDisplay());
		}
		medicineLst.removeAllItems();
		medicineLst.addItem(Messages.getString("ForecastingDocumentWindow.tbDetailedReport.empty"));
		for (Object o : medNames.toArray()) {
			medicineLst.addItem(o);
		}
	}

	/**
	 * recalculate graph if exists
	 */
	public void recalculate() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				recalcGraph();
			}
		});
	}

	/**
	 * Recalc the graph
	 */
	private void recalcGraph() {
		int selectedIndex = medicineLst.getSelectedIndex();
		if (selectedIndex == -1)
			return;
		if (selectedIndex == 0) {
			cleanGraph();				
		} else {
			paintGraph(getMedConsumptions().get(selectedIndex-1), getOrderCalculator().getMedicineTotals());
		}
	}
	protected void initDataBindings() {
		BeanProperty<OrderCalculator, DeliveryScheduleEnum> orderCalculatorBeanProperty_1 = BeanProperty.create("forecast.deliverySchedule");
		BeanProperty<JComboBox, Object> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
		AutoBinding<OrderCalculator, DeliveryScheduleEnum, JComboBox, Object> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, orderCalculator, orderCalculatorBeanProperty_1, deliverySchedule, jComboBoxBeanProperty);
		autoBinding_1.bind();
	}
}
