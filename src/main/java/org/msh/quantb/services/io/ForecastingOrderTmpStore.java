package org.msh.quantb.services.io;

/**
 * This class represent buffer storage for the selected forecasting medicine
 * @author alexey
 *
 */
public class ForecastingOrderTmpStore extends AbstractUIAdapter {
	
	private ForecastingOrderUIAdapter forecastingOrder;

	public ForecastingOrderUIAdapter getForecastingOrder() {
		return forecastingOrder;
	}

	/**
	 * Set buffer storage
	 * @param forecastingOrder
	 */
	public void setForecastingOrder(ForecastingOrderUIAdapter forecastingOrder) {
		ForecastingOrderUIAdapter oldValue = getForecastingOrder();
		this.forecastingOrder = forecastingOrder;
		firePropertyChange("forecastingOrder", oldValue, getForecastingOrder());
	}
	
	/**
	 * @return the forecasting medicine
	 */
	public ForecastingOrderTmpStore(ForecastingOrderUIAdapter forecastingOrder){
		ForecastingOrderUIAdapter oldValue = getForecastingOrder();
		this.forecastingOrder = forecastingOrder;
		firePropertyChange("forecastingOrder", oldValue, getForecastingOrder());
	}	
}