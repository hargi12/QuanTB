package org.msh.quantb.view.panel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.msh.quantb.model.gen.DeliveryScheduleEnum;
import org.msh.quantb.services.calc.ConsumptionMonth;
import org.msh.quantb.services.calc.DeliveryOrdersControl;
import org.msh.quantb.services.calc.LogisticCalculatorI;
import org.msh.quantb.services.calc.LogisticCalculatorsFactory;
import org.msh.quantb.services.calc.MedicineConsumption;
import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.ForecastingTotalMedicine;
import org.msh.quantb.services.mvp.Messages;

/**
 * Data source for the Stock Graph
 * Provides datasets etc
 * @author Alex Kurasoff
 *
 */
public class StockGraphModel {

	private MedicineConsumption mCons;
	private ForecastUIAdapter forecast;
	private List<ConsumptionMonth> consumptionSet = new ArrayList<ConsumptionMonth>();
	private DeliveryOrdersControl control;
	private CategoryToolTipGenerator tootltip = new CategoryToolTipGenerator() {
		@Override
		public String generateToolTip(CategoryDataset dataset, int row, int column) {
			return (String) dataset.getRowKeys().get(row);
		}
	};
	private boolean excludeDeliveries;

	/**
	 * Only valid constructor
	 * @param mCons current medicine consumption
	 * @param forecast current forecasting
	 * @param control 
	 * @param excludeDeliveries exclude deliveries from the graph
	 * @param orderData additional order's data
	 */
	public StockGraphModel(MedicineConsumption mCons, ForecastUIAdapter forecast, 
			DeliveryOrdersControl control, boolean excludeDeliveries, List<ForecastingTotalMedicine> orderData){
		this.mCons=mCons;
		this.forecast = forecast;
		this.control = control;
		this.excludeDeliveries = excludeDeliveries;
		buildConsumptionSet();
	}
	
	
	public CategoryToolTipGenerator getTootltip() {
		return tootltip;
	}


	public void setTootltip(CategoryToolTipGenerator tootltip) {
		this.tootltip = tootltip;
	}

	/**
	 * Build the consumption set based on the current delivery schedule
	 * all calculation will be on the clone of real consumption list
	 */
	private void buildConsumptionSet() {
		//create a clone only for month consumption
		getConsumptionSet().clear();  
		for(ConsumptionMonth cM : getmCons().getCons()){ 
			getConsumptionSet().add(cM.getClone());
		}
		//recalculate deliveries in accordance with the schedule, currently is Provisional!
		//clean up all deliveries
		for(ConsumptionMonth cM: getConsumptionSet()){
			cM.setDelivery(BigDecimal.ZERO);
		}
		//calculate incoming without deliveries
		LogisticCalculatorI lCalc = LogisticCalculatorsFactory.getLolgisticCalculator(getForecast());
		lCalc.recalcPStocks(getConsumptionSet(), getmCons().getMed());
		//add recalculated deliveries from the control
		for(ConsumptionMonth cM :getConsumptionSet()){
			BigDecimal delivery = control.fetchDelivery(getmCons().getMed(), cM.getMonth());
			if (delivery.compareTo(BigDecimal.ZERO)>0){
				if(! isExcludeDeliveries()){
					cM.setDelivery(delivery);
				}
			}
			//calculate incoming with deliveries just added
			lCalc.recalcPStocks(getConsumptionSet(),getmCons().getMed());
		}
	}

	protected MedicineConsumption getmCons() {
		return mCons;
	}

	protected void setmCons(MedicineConsumption mCons) {
		this.mCons = mCons;
	}

	public ForecastUIAdapter getForecast() {
		return forecast;
	}

	protected void setForecast(ForecastUIAdapter forecast) {
		this.forecast = forecast;
	}


	protected List<ConsumptionMonth> getConsumptionSet() {
		return consumptionSet;
	}

	protected void setConsumptionSet(List<ConsumptionMonth> consumptionSet) {
		this.consumptionSet = consumptionSet;
	}

	public DeliveryOrdersControl getControl() {
		return control;
	}
	public void setControl(DeliveryOrdersControl control) {
		this.control = control;
	}


	public CategoryToolTipGenerator getToolTip() {
		return tootltip;
	}
	public void setToolTip(CategoryToolTipGenerator toolTipBar) {
		this.tootltip = toolTipBar;
	}

	public boolean isExcludeDeliveries() {
		return excludeDeliveries;
	}
	public void setExcludeDeliveries(boolean excludeDeliveries) {
		this.excludeDeliveries = excludeDeliveries;
	}
	/**
	 * Create data for bars
	 * @return
	 */
	public CategoryDataset createBarDataSet() {
		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
		for(ConsumptionMonth cM : getConsumptionSet()){
			dataSet.addValue(cM.getConsAllInt(), Messages.getString("ForecastingDocumentWindow.graph.stock.consumption"), cM.getMonth());
			dataSet.addValue(cM.getExpired(), Messages.getString("ForecastingDocumentWindow.graph.stock.expired"), cM.getMonth());
			dataSet.addValue(cM.getOrder(), Messages.getString("ForecastingDocumentWindow.graph.stock.oldOrder"), cM.getMonth());
			dataSet.addValue(cM.getDelivery(), Messages.getString("ForecastingDocumentWindow.graph.stock.delivery"), cM.getMonth());
		}
		return dataSet;
	}
	/**
	 * Create data for lines
	 * @return
	 */
	public CategoryDataset createLineDataSet() {
		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
		for(ConsumptionMonth cM : getConsumptionSet()){
			dataSet.addValue(cM.getMaxStock(), Messages.getString("ForecastingDocumentWindow.graph.stock.max"), cM.getMonth());
			dataSet.addValue(cM.getMinStock(), Messages.getString("ForecastingDocumentWindow.graph.stock.min"), cM.getMonth());
			dataSet.addValue(cM.getpStock(), Messages.getString("ForecastingDocumentWindow.graph.stock.projected"), cM.getMonth());
		}
		return dataSet;
	}
	/**
	 * true if it is many months, so labels on chart must be vertical
	 * @return
	 */
	public boolean isVertical() {
		return getConsumptionSet().size()>=12;
	}


}
