package org.msh.quantb.services.io;

import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.msh.quantb.model.forecast.ForecastingOrder;
import org.msh.quantb.model.forecast.Month;
import org.msh.quantb.model.mvp.ModelFactory;
import org.msh.quantb.services.calc.DateUtils;
import org.msh.quantb.services.calc.ForecastingError;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
/**
 * Object ForecastingOrder adopted for UI
 * @author alexey
 *
 */
public class ForecastingOrderUIAdapter extends AbstractUIAdapter implements Comparable<ForecastingOrderUIAdapter>  {
	private ForecastingOrder forecastingOrderObj;
	private PropertyChangeListener listener;
	private String propertyName;	
	/**
	 * Only valid constructor
	 * @param forecastingOrder
	 */
	public ForecastingOrderUIAdapter(ForecastingOrder forecastingOrderObj){
		this.forecastingOrderObj = forecastingOrderObj;	
	}

	/**
	 * @return forecastingOrderobj
	 */
	public ForecastingOrder getForecastingOrderObj() {
		return forecastingOrderObj;
	}

	/**
	 * @param forecastingOrderObj
	 */
	public void setForecastingOrderObj(ForecastingOrder forecastingOrderObj) {
		ForecastingOrder oldValue = getForecastingOrderObj();
		this.forecastingOrderObj = forecastingOrderObj;
		firePropertyChange("forecastingOrderObj", oldValue, getForecastingOrderObj());
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingOrder#getBatch()
	 */
	public ForecastingBatchUIAdapter getBatch() {		
		ForecastingBatchUIAdapter forecastingBatchUIAdapter = new ForecastingBatchUIAdapter(forecastingOrderObj.getBatch());
		if (propertyName!=null && listener!=null) forecastingBatchUIAdapter.addPropertyChangeListener(propertyName, listener);
		return forecastingBatchUIAdapter;
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingOrder#getArrivalDate()
	 */
	public MonthUIAdapter getArrivalDate() {
		return new MonthUIAdapter(forecastingOrderObj.getArrivalDate());
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingOrder#setBatch(org.msh.quantb.model.forecast.ForecastingBatch))
	 */
	public void setBatch(ForecastingBatchUIAdapter value) {
		ForecastingBatchUIAdapter oldValue = getBatch();
		forecastingOrderObj.setBatch(value.getForecastingBatchObj().getOriginal());
		firePropertyChange("batch", oldValue, getBatch());
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingOrder#setArrivalDate(org.msh.quantb.model.forecast.Month)
	 */
	public void setArrivalDate(MonthUIAdapter value) {
		MonthUIAdapter oldValue = getArrivalDate();
		forecastingOrderObj.setArrivalDate(value.getMonthObj());
		firePropertyChange("arrivalDate", oldValue, getArrivalDate());
	}

	/**
	 * Always false, no sense to compare
	 */
	public boolean equals(Object _another) {
		return false;
	}

	/**
	 * Set property change listener to current ForecastingOrderUiAdapter
	 * @param propertyName property name
	 * @param listener property change listener
	 */
	public void setProgertyChangeListener(String propertyName, PropertyChangeListener listener){
		this.listener = listener;
		this.propertyName = propertyName;
	}

	@Override
	public int compareTo(ForecastingOrderUIAdapter o) {
		int res = this.getArrivalDate().compareTo(o.getArrivalDate());
		if(res == 0) res = this.getBatch().getExpiryDate().compareTo(o.getBatch().getExpiryDate());
		if (res == 0) res = -1;
		return res;
	}
	/**
	 * Make deep clone of this order object
	 * @param factory
	 * @return clone of this order object
	 */
	public ForecastingOrderUIAdapter makeClone(ModelFactory factory) {
		ForecastingOrder ord = factory.createForecastingOrder(this.getArrivalDate().incrementClone(factory, 0).getMonthObj(),
				this.getBatch().getExpiryDate().incrementClone(factory, 0).getMonthObj());
		ord.setBatch(this.getBatch().makeClone(factory).getForecastingBatchObj().getOriginal());
		ord.setArrived(this.getForecastingOrderObj().getArrived());
		return new ForecastingOrderUIAdapter(ord);
	}
	/**
	 * Make empty clone
	 * @param factory
	 * @return
	 */
	public ForecastingOrderUIAdapter makeEmptyClone(ModelFactory factory) {
		ForecastingOrderUIAdapter res = makeClone(factory);
		res.getBatch().getForecastingBatchObj().setConsumptionInMonth(BigDecimal.ZERO);
		//quantity must be order quantity, but not available till arrive month
		res.getBatch().getForecastingBatchObj().setQuantityAvailable(BigDecimal.ZERO);
		res.getBatch().getForecastingBatchObj().setQuantityExpired(0);
		return res;
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingOrder#getArrived()
	 */
	public Calendar getArrived() {
		XMLGregorianCalendar xmlG = forecastingOrderObj.getArrived();
		xmlG.setTimezone(DatatypeConstants.FIELD_UNDEFINED );
		Calendar tmp = xmlG.toGregorianCalendar();
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(DateUtils.getcleanDate(tmp));
		return cal;
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingOrder#setArrived(javax.xml.datatype.XMLGregorianCalendar)
	 */
	public void setArrived(Calendar value) {
		value.setTime(DateUtils.getcleanDate(value));
		Calendar oldValue = getArrived();
		MonthUIAdapter oldMArrived = getArrivalDate();
		try {
			forecastingOrderObj.setArrived(DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar)value));
			Month month = forecastingOrderObj.getArrivalDate();
			month.setMonth(value.get(Calendar.MONTH));
			month.setYear(value.get(Calendar.YEAR));
			firePropertyChange("arrived", oldValue, getArrived());
			firePropertyChange("arrivalDate", oldMArrived, getArrivalDate());
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}		
	}	

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingOrder#getArrived()
	 */
	public Date getArrivedDt() {
		return getArrived().getTime();
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingOrder#setArrived(javax.xml.datatype.XMLGregorianCalendar)
	 */
	public void setArrivedDt(Date value) {
		Calendar tmp = new GregorianCalendar();
		tmp.setTime(DateUtils.getcleanDate(value));		
		Date oldValue = getArrivedDt();
		try {
			forecastingOrderObj.setArrived(DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar)tmp));
			MonthUIAdapter monthUi = this.getArrivalDate();
			monthUi.setMonth(tmp.get(Calendar.MONTH));
			monthUi.setYear(tmp.get(Calendar.YEAR));
			setArrivalDate(monthUi);
			firePropertyChange("arrivedDt", oldValue, getArrivedDt());
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}		
	}	

	public String getComment(){
		return getForecastingOrderObj().getBatch().getComment();
	}

	public void setComment(String value){
		String oldValue = getComment();
		getForecastingOrderObj().getBatch().setComment(value);
		firePropertyChange("comment", oldValue, getComment());
	}

	public Boolean getBatchInclude(){
		if(getBatch() != null){
			return getBatch().getInclude();
		}else{
			return false;
		}
	}

	public void setBatchInclude(Boolean value){
		if(getBatch()!=null){
			Boolean oldValue = getBatch().getInclude();
			Integer oldValue1 = getBatchQuantity();
			getBatch().setInclude(value);
			firePropertyChange("batchInclude", oldValue, getBatchInclude());
			firePropertyChange("batchQuantity", oldValue1, getBatchQuantity());

		}
	}

	public void setBatchQuantity(Integer value){
		Integer oldValue = getBatch().getQuantity();
		getBatch().setQuantity(value);
		firePropertyChange("batchQuantity", oldValue, getBatchQuantity());
	}

	public Integer getBatchQuantity() {
		return getBatch().getQuantity();
	}

	public boolean hasBatches() {
		// TODO Auto-generated method stub
		return false;
	}
	/**
	 * Validate this order will return error string while at least a error will exist
	 * @param firstFCDate first forecasting date
	 * @return Error string if error or empty string if no error(s)
	 */
	public String validate(Calendar firstFCDate){
		if(!getBatch().isExclude()){
			if(getArrived().compareTo(firstFCDate)<0){
				return Messages.getString("Error.Validation.OrderSave.ReciveDate");
			}
			if(getBatch().getExpired().compareTo(getArrived())<0){
				return Messages.getString("Error.Validation.OrderSave.ExpireDate");
			}
			return "";
		}else{
			return "";
		}
	}

}
