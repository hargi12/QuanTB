package org.msh.quantb.services.io;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.jdesktop.observablecollections.ObservableCollections;

/**
 * This class represent buffer storage for the selected forecasting medicine
 * @author alexey
 */
public class ForecastingMedicineTmpStore extends AbstractUIAdapter {
	private ForecastingMedicineUIAdapter fcMedicine=null;
	private List<ForecastingOrderUIAdapter> orders;
	private List<ForecastingResultUIAdapter> results;
	private PropertyChangeListener batchIncExcl;
	private PropertyChangeListener orderInclExcl;
	/**
	 * Only valid constructor
	 * @param _listener - listener that should be added to ForecastingMedicineUIAdapter
	 */
	public ForecastingMedicineTmpStore(PropertyChangeListener _batchInclExcl, PropertyChangeListener _orderInclExcl) {
		super();
		this.batchIncExcl = _batchInclExcl;
		this.orderInclExcl = _orderInclExcl;
	}



	public PropertyChangeListener getBatchIncExcl() {
		return batchIncExcl;
	}



	public void setBatchIncExcl(PropertyChangeListener batchIncExcl) {
		this.batchIncExcl = batchIncExcl;
	}



	public PropertyChangeListener getOrderInclExcl() {
		return orderInclExcl;
	}



	public void setOrderInclExcl(PropertyChangeListener orderInclExcl) {
		this.orderInclExcl = orderInclExcl;
	}



	/**
	 * @return the fcMedicineObj
	 */
	public ForecastingMedicineUIAdapter getFcMedicine() {
		return fcMedicine;
	}
	/**
	 * @param fcMedicineObj the fcMedicineObj to set
	 */
	public void setFcMedicine(ForecastingMedicineUIAdapter fcMedicineObj) {
		ForecastingMedicineUIAdapter oldValue = getFcMedicine();
		this.fcMedicine = fcMedicineObj;
		if (this.fcMedicine != null){
			this.fcMedicine.setBatchInclExclListener(getBatchIncExcl());
			this.fcMedicine.setOrdersIncExclListener(getOrderInclExcl());
		}
		firePropertyChange("fcMedicine", oldValue, getFcMedicine());
	}    

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingMedicine#getBatchesToExpire()
	 */
	public List<ForecastingBatchUIAdapter> getBatchesToExpire() {
		if (getFcMedicine() != null){
			return getFcMedicine().getBatchesToExpire();
		}else{
			return null;
		}
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingMedicine#getOrders()
	 * @deprecated do not use it at all!!!
	 */
	public List<ForecastingOrderUIAdapter> getOrders() {
		ArrayList<ForecastingOrderUIAdapter> oas = new ArrayList<ForecastingOrderUIAdapter>();
		if (fcMedicine!=null) oas.addAll(fcMedicine.getOrders());    			
		this.orders = ObservableCollections.observableList(oas);		
		return this.orders;       
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingMedicine#getResults()
	 */
	public List<ForecastingResultUIAdapter> getResults() {
		ArrayList<ForecastingResultUIAdapter> oas = new ArrayList<ForecastingResultUIAdapter>();
		if (fcMedicine!=null) oas.addAll(fcMedicine.getResults());    			
		this.results = ObservableCollections.observableList(oas);		
		return this.results;
	}
}
