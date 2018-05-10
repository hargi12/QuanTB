package org.msh.quantb.view.panel;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.msh.quantb.services.calc.DateUtils;
import org.msh.quantb.services.calc.MedicineConsumption;
import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.mvp.Messages;

/**
 * Dashboard panel
 */
public class DashBoardPanel extends JPanel {	
	private static final long MILLI_SECONDS = 0x5265c00L;
	private static final long serialVersionUID = 7644282351658836625L;
	private ChartPanel chartPanel;
	private JScrollPane scrollPane;

	public DashBoardPanel() {
		setSize(new Dimension(1000, 490));
		setLayout(new BorderLayout(0, 0));

		scrollPane = new JScrollPane();
		add(scrollPane);
	}

	/**
	 * Set data into table of dashboard
	 * 
	 * @param fui forecasting ui adapter
	 * @param medConsList medicine consumptions
	 */
	public void setData(ForecastUIAdapter fui, final List<MedicineConsumption> medConsList) {
		if (chartPanel != null) remove(chartPanel);
		assert (fui != null && medConsList != null);
		Date minDate = medConsList.iterator().next().getCons().iterator().next().getMonth().getFirstDate();
		Date maxDate = medConsList.iterator().next().getCons().get(medConsList.iterator().next().getCons().size() - 1).getMonth().getLastDate();
		DashBoardModel dBmodel = new DashBoardModel(medConsList);
		JFreeChart jfreechart = ChartFactory.createStackedBarChart("", "", "", dBmodel.createDataSet(), PlotOrientation.HORIZONTAL, false, false, false);	
		jfreechart.setBackgroundPaint(Color.white);
	
		
		CategoryPlot categoryplot = (CategoryPlot) jfreechart.getPlot();
		categoryplot.setDomainGridlinesVisible(true);
		categoryplot.setDomainGridlinePaint(Color.BLACK);
		categoryplot.setRangeGridlinesVisible(true);
		categoryplot.setRangeGridlinePaint(Color.BLACK);
		categoryplot.setRangePannable(true);
		categoryplot.setBackgroundPaint(Color.white);

		CategoryAxis categoryAxis = categoryplot.getDomainAxis();
		Font font = categoryAxis.getTickLabelFont();
		categoryAxis.setMaximumCategoryLabelLines(3);
		//categoryAxis.setTickLabelFont(new Font(font.getName(), font.getStyle(), 8));
		Font catFnt = font.deriveFont(Font.PLAIN, 8.0f);
		categoryAxis.setTickLabelFont(catFnt);
		categoryAxis.setTickLabelPaint(Color.BLACK);
		long lFrom = getDaysFromBegin(minDate, fui.getMinStock()) * 0x5265c00L;
		long lTo = getDaysFromBegin(minDate, fui.getMaxStock()) * MILLI_SECONDS;
		double min = lFrom+minDate.getTime();
		double max = lTo +minDate.getTime();
		IntervalMarker intervalmarker = new IntervalMarker(min, max);		
		intervalmarker.setPaint(Color.PINK);
		categoryplot.addRangeMarker(intervalmarker, Layer.BACKGROUND);

		ValueMarker minMarker = new ValueMarker(min);
		minMarker.setLabel(Messages.getString("ForecastingDocumentWindow.dashBoard.column.Min"));
		minMarker.setLabelFont(new Font("SansSerif", 2, 11));
		minMarker.setLabelAnchor(RectangleAnchor.TOP);
		minMarker.setPaint(Color.PINK);		
		minMarker.setStroke(new BasicStroke(2.0f));
		minMarker.setLabelTextAnchor(TextAnchor.CENTER_RIGHT);
		minMarker.setLabelOffset(new RectangleInsets(20,-205,0,0));
		categoryplot.addRangeMarker(minMarker);

		ValueMarker maxMarker = new ValueMarker(max);
		maxMarker.setLabel(Messages.getString("ForecastingDocumentWindow.dashBoard.column.Max"));
		maxMarker.setLabelFont(new Font("SansSerif", 2, 11));
		maxMarker.setLabelAnchor(RectangleAnchor.TOP);
		maxMarker.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
		maxMarker.setPaint(Color.PINK);
		maxMarker.setStroke(new BasicStroke(2.0f));
		maxMarker.setLabelOffset(new RectangleInsets(20,-10,0,0));
		categoryplot.addRangeMarker(maxMarker);
		
		//forecasting period marker
		double leadTime = fui.getLeadTimeEnd().getTimeInMillis();
		ValueMarker lTMarker = new ValueMarker(leadTime);
		lTMarker.setLabel(Messages.getString("ForecastingDocumentWindow.dashBoard.column.LT"));
		lTMarker.setLabelFont(new Font("SansSerif", Font.ITALIC, 11));
		lTMarker.setLabelAnchor(RectangleAnchor.TOP);
		lTMarker.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
		lTMarker.setPaint(Color.ORANGE);
		lTMarker.setStroke(new BasicStroke(4.0f));
		lTMarker.setLabelOffset(new RectangleInsets(5,-10,0,0));
		categoryplot.addRangeMarker(lTMarker);
		
		double buffTime = fui.getRealReviewEnd().getTimeInMillis();
		ValueMarker bFMarker = new ValueMarker(buffTime);
		bFMarker.setLabel(Messages.getString("ForecastingDocumentWindow.dashBoard.column.Buf"));
		bFMarker.setLabelFont(new Font("SansSerif", Font.ITALIC, 11));
		bFMarker.setLabelAnchor(RectangleAnchor.TOP);
		bFMarker.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
		bFMarker.setPaint(Color.ORANGE);
		bFMarker.setStroke(new BasicStroke(4.0f));
		bFMarker.setLabelOffset(new RectangleInsets(5,-10,0,0));
		categoryplot.addRangeMarker(bFMarker);

		TextTitle tt = new TextTitle(				 
				Messages.getString("ForecastingDocumentWindow.dashBoard.column.rd") +
				" " + DateFormat.getDateInstance(DateFormat.MEDIUM).format(fui.getReferenceDt())+ " " +  //TODO check it!!!!
				Messages.getString("ForecastingDocumentWindow.dashBoard.column.totalEnrolled")+": "+
				new DecimalFormat("###,###,###,##0").format(fui.getTotalEnrolled()) + " " +
				Messages.getString("ForecastingDocumentWindow.dashBoard.column.totalExpected")+": "+
				new DecimalFormat("###,###,###,##0").format(fui.getTotalExpected()), 
				new Font("SansSerif", Font.BOLD, 12));
		tt.setPosition(RectangleEdge.TOP);
		tt.setHorizontalAlignment(HorizontalAlignment.CENTER);
		tt.setMargin(0.0, 0.0, 0.0, 0.0);
		jfreechart.addSubtitle(tt);        

		StackedBarRenderer stackedbarrenderer = (StackedBarRenderer) categoryplot.getRenderer();
		stackedbarrenderer.setBase(minDate.getTime());
		stackedbarrenderer.setMaximumBarWidth(0.05);
		stackedbarrenderer.setDrawBarOutline(false);
		
		dBmodel.determineSeries(stackedbarrenderer);
		


		//set legend, we are need only first 4 legends
		final LegendItemCollection legendItemsOld = stackedbarrenderer.getLegendItems();
		LegendItemSource source = new LegendItemSource() {
			public LegendItemCollection getLegendItems() {
				LegendItemCollection lic = new LegendItemCollection();
				for (int i = 0; i < 4; i++) {
					lic.add(legendItemsOld.get(i));
				}
				return lic;
			}
		};
		LegendTitle lt = new LegendTitle(source);
		lt.setItemFont(new Font("SansSerif", Font.PLAIN, 10));
		jfreechart.addLegend(lt);

		//Set date period
		DateAxis dateaxis = new DateAxis("");
		dateaxis.setMinimumDate(minDate);
		dateaxis.setMaximumDate(maxDate);
		Font font1 = dateaxis.getTickLabelFont();
		dateaxis.setTickLabelFont(new Font(font1.getName(), font1.getStyle(), 8));
		dateaxis.setDateFormatOverride(new SimpleDateFormat("         MMM yy"));
		dateaxis.setTickUnit(new DateTickUnit(DateTickUnitType.MONTH, 1));
		dateaxis.setTickLabelInsets(new RectangleInsets(0, 0, 5, 0));
		dateaxis.setVerticalTickLabels(DateUtils.monthsBetween(minDate, maxDate)>20);
		categoryplot.setRangeAxis(dateaxis);

		chartPanel = new ChartPanel(jfreechart);		
		chartPanel.setDomainZoomable(false);
		chartPanel.setRangeZoomable(false);
		chartPanel.setDismissDelay(100000);
		scrollPane.setViewportView(chartPanel);		

	}
	/**
	 * Get exact quantity of days from min date to minDate + months quantity
	 * @param minDate - current
	 * @param offsetMonths
	 * @return days between
	 */
	private int getDaysFromBegin(Date minDate, int offsetMonths) {
		Calendar resultCal = GregorianCalendar.getInstance();
		resultCal.setTime(minDate);
		resultCal.add(Calendar.MONTH, offsetMonths);
		return DateUtils.daysBetween(resultCal.getTime(), minDate);
	}

}
