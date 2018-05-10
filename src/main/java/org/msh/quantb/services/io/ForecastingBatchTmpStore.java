package org.msh.quantb.services.io;
/**
 * This class represent buffer storage for the selected forecasting batches to expire
 * @author alexey
 *
 */
public class ForecastingBatchTmpStore extends AbstractUIAdapter {
	private ForecastingBatchUIAdapter forecastingBatch;

	public ForecastingBatchTmpStore(ForecastingBatchUIAdapter forecastingBatch){
		this.forecastingBatch = forecastingBatch;
	}

	/**
	 * Get forecasting batches to expire
	 * @return 
	 */
	public ForecastingBatchUIAdapter getForecastingBatch() {
		return forecastingBatch;
	}
	
	/**
	 * Set buffer storage of forecasting batches to expire
	 * @param forecastingOrder 
	 */
	public void setForecastingBatch(ForecastingBatchUIAdapter forecastingBatch) {
		ForecastingBatchUIAdapter oldValue = getForecastingBatch();
		this.forecastingBatch = forecastingBatch;
		firePropertyChange("forecastingBatch", oldValue, getForecastingBatch());
	}
}
