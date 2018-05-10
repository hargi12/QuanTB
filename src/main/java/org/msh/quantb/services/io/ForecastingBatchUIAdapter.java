package org.msh.quantb.services.io;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.msh.quantb.model.forecast.ForecastingBatch;
import org.msh.quantb.model.mvp.ModelFactory;
import org.msh.quantb.services.calc.DateUtils;

/**
 * UI Adapter for forecasting batch
 * @author alexey
 *
 */
public class ForecastingBatchUIAdapter extends AbstractUIAdapter implements Comparable<ForecastingBatchUIAdapter> {
	private ForecastingBatchExt forecastingBatchObj;
	/**
	 * Only valid constructor
	 * @param batch
	 */
	public ForecastingBatchUIAdapter(ForecastingBatch batch){
		this.forecastingBatchObj = new ForecastingBatchExt(batch);
	}

	/**
	 * @return the forecatcingBatchObj
	 */
	public ForecastingBatchExt getForecastingBatchObj() {
		return forecastingBatchObj;
	}

	/**
	 * Equivalence from merge operation point of view
	 */
	public boolean equalsForMerge(ForecastingBatchUIAdapter another) {
		if(this == another){
			return true;
		}
		if(DateUtils.compareDates(getExpired(), another.getExpired())==0){
			if ((this.getAvailFrom() == null && another.getAvailFrom() == null) ||
					(DateUtils.compareDates(getAvailFrom(), another.getAvailFrom())==0)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Equivalence form the general point of view - never equal, because we know nothing about medicine
	 * @param another
	 * @return
	 */
	@Override
	public boolean equals(Object o){
		return this == o;
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingBatch#getExpiryDate()
	 */
	public MonthUIAdapter getExpiryDate() {
		return new MonthUIAdapter(forecastingBatchObj.getExpiryDate());
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingBatch#setExpiryDate(org.msh.quantb.model.forecast.Month)
	 */
	public void setExpiryDate(MonthUIAdapter value) {
		MonthUIAdapter oldValue = getExpiryDate();
		forecastingBatchObj.setExpiryDate(value.getMonthObj());
		firePropertyChange("expiryDate", oldValue, getExpiryDate());
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingBatch#getQuantity()
	 */
	public Integer getQuantity() {
		return forecastingBatchObj.getQuantity();
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingBatch#setQuantity(int)
	 */
	public void setQuantity(Integer value) {
		Integer oldValue = getQuantity();
		forecastingBatchObj.setQuantity(value);
		firePropertyChange("quantity", oldValue, getQuantity());
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingBatch#getQuantityAvailable()
	 */
	public BigDecimal getQuantityAvailable() {
		return forecastingBatchObj.getQuantityAvailable();
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingBatch#setQuantityAvailable(int)
	 */
	public void setQuantityAvailable(BigDecimal value) {
		BigDecimal oldValue = getQuantityAvailable();
		forecastingBatchObj.setQuantityAvailable(value);
		firePropertyChange("quantityAvailable", oldValue, getQuantityAvailable());
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingBatch#getQuantityExpired()
	 */
	public Integer getQuantityExpired() {
		return forecastingBatchObj.getQuantityExpired();
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingBatch#setQuantityExpired(int)
	 */
	public void setQuantityExpired(Integer value) {
		Integer oldValue = getQuantity();
		forecastingBatchObj.setQuantityExpired(value);
		firePropertyChange("quantityExpired", oldValue, getQuantityExpired());
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingBatch#getConsumptionInMonth()
	 */
	public BigDecimal getConsumptionInMonth() {
		return forecastingBatchObj.getConsumptionInMonth();
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingBatch#setConsumptionInMonth(int)
	 */
	public void setConsumptionInMonth(BigDecimal value) {
		BigDecimal oldValue = getConsumptionInMonth();
		forecastingBatchObj.setConsumptionInMonth(value);
		firePropertyChange("consumptionInMonth", oldValue, getConsumptionInMonth());
	}
	@Override
	/**
	 * compare by expiration dates only, but never equals
	 */
	public int compareTo(ForecastingBatchUIAdapter another) {
		int res = this.getExpired().compareTo(another.getExpired());
		if (res == 0){
			if(this.equals(another)){
				return 0;
			}else{
				res = this.getQuantity().compareTo(another.getQuantity());
				if (res==0){
					res = this.getAvailFrom().compareTo(another.getAvailFrom());
					if(res==0){
						if(System.identityHashCode(this)>System.identityHashCode(another)){
							return 1;
						}else{
							return -1;
						}
					}
				}else{
					return res;
				}
			}
		}else{
			return res;
		}
		return res;
	}
	/**
	 * make deep clone
	 * @param factory
	 * @return
	 */
	public ForecastingBatchUIAdapter makeClone(ModelFactory factory) {
		ForecastingBatch clone = factory.createForecastingBatchExact(this.getForecastingBatchObj().getExpired().toGregorianCalendar());
		clone.setConsumptionInMonth(this.getConsumptionInMonth());
		clone.setQuantity(this.getQuantity());
		clone.setQuantityAvailable(this.getQuantityAvailable());
		clone.setQuantityExpired(this.getQuantityExpired());
		clone.setAvailFrom(this.getForecastingBatchObj().getAvailFrom());
		clone.setComment(getComment());
		clone.setExclude(this.isExclude());
		return new ForecastingBatchUIAdapter(clone);
	}


	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingBatch#getExpired()
	 */
	public Calendar getExpired() {
		if (forecastingBatchObj.getExpired() != null){
			XMLGregorianCalendar xmlG = forecastingBatchObj.getExpired();
			xmlG.setTimezone(DatatypeConstants.FIELD_UNDEFINED );
			Calendar cal = xmlG.toGregorianCalendar();
			cal.setTime(DateUtils.getcleanDate(cal));
			return cal;
		}else{
			return null;
		}
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingBatch#setExpired(XMLGregorianCalendar)
	 */
	public void setExpired(Calendar value) {
		if (value != null){
			value.setTime(DateUtils.getcleanDate(value));
		}
		Calendar oldValue = getExpired();
		Date oldValue1 = getExpiredDtEdit();
		try {
			if (value != null){
				forecastingBatchObj.setExpired(DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar)value));
				MonthUIAdapter monthUi = this.getExpiryDate();
				monthUi.setMonth(value.get(Calendar.MONTH));
				monthUi.setYear(value.get(Calendar.YEAR));
				setExpiryDate(monthUi);
			}else{
				forecastingBatchObj.setExpired(null);
			}
			firePropertyChange("expired", oldValue, getExpired());
			firePropertyChange("expiredDtEdit", oldValue1, getExpiredDtEdit());
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingBatch#getExpired()
	 */
	public Date getExpiredDt() {
		if (forecastingBatchObj.getExpired() != null){
			Calendar cal = forecastingBatchObj.getExpired().toGregorianCalendar();
			Calendar cal1 = GregorianCalendar.getInstance();
			cal1.setTime(DateUtils.getcleanDate(cal));
			return cal1.getTime();
		}else{
			return null;
		}
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingBatch#setExpired(XMLGregorianCalendar)
	 */
	public void setExpiredDt(Date value) {
		if (value != null){
			Calendar tmp = new GregorianCalendar();
			tmp.setTime(DateUtils.getcleanDate(value));
			Date oldValue = getExpiredDt();	
			Calendar oldValueCal = getExpired();
			try {
				forecastingBatchObj.setExpired(DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar)tmp));
				MonthUIAdapter monthUi = this.getExpiryDate();
				monthUi.setMonth(tmp.get(Calendar.MONTH));
				monthUi.setYear(tmp.get(Calendar.YEAR));
				setExpiryDate(monthUi);
				firePropertyChange("expiredDt", oldValue, getExpiredDt());
				firePropertyChange("expired", oldValueCal, getExpired());
			} catch (DatatypeConfigurationException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * expire this batch
	 */
	public void expire() {
		this.setQuantityExpired(this.getQuantityAvailable().setScale(0, BigDecimal.ROUND_UP).intValue());
		this.setQuantityAvailable(BigDecimal.ZERO);
	}
	/**
	 * Check does batch will expire in date given
	 * Attention!!! batch will be expired at next day after expiration date
	 * @param month year and month
	 * @param fromDay day
	 * @return true, if so
	 */
	public boolean justExpired(MonthUIAdapter month, Integer fromDay) {
		Calendar realExp = GregorianCalendar.getInstance();
		realExp.setTime(this.getExpiredDt());
		DateUtils.cleanTime(realExp);
		realExp.add(Calendar.DAY_OF_MONTH, 1);
		if (realExp.get(Calendar.YEAR) == month.getYear() && 
				realExp.get(Calendar.MONTH) == month.getMonth() && 
				realExp.get(Calendar.DAY_OF_MONTH)==fromDay){
			return true;
		}else{
			return false;
		}

		/*		if (this.getExpiryDate().equals(month)){
			return(this.getExpired().get(Calendar.DAY_OF_MONTH) == fromDay);
		} else{
			return false;
		}*/
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingBatch#getAvailFrom()
	 */
	public Calendar getAvailFrom() {
		XMLGregorianCalendar xmlG = forecastingBatchObj.getAvailFrom(); 
		xmlG.setTimezone(DatatypeConstants.FIELD_UNDEFINED );
		return xmlG.toGregorianCalendar();
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingBatch#setAvailFrom(javax.xml.datatype.XMLGregorianCalendar)
	 */
	public void setAvailFrom(Calendar value) {
		DateUtils.cleanTime(value);
		Calendar oldValue = value;
		try {
			forecastingBatchObj.setAvailFrom(DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar)value));
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		firePropertyChange("availFrom", oldValue, getAvailFrom());
	}
	/**
	 * Get expired date only for edit purpose
	 * @return
	 */
	public Date getExpiredDtEdit(){
		Date ret = getExpiredDt();
		if (ret != null){
			if (ret.getYear()>3000){
				return null;
			}
		}
		return ret;
	}

	/**
	 * set expired date for manual edit only
	 * @param value
	 */
	public void setExpiredDtEdit(Date value){
		Date oldValue = getExpiredDtEdit();
		setExpiredDt(value);
		firePropertyChange("expiredDtEdit", oldValue, getExpiredDtEdit());
	}

	public void setComment(String comment){
		String oldValue = getComment();
		getForecastingBatchObj().setComment(comment);
		firePropertyChange("comment", oldValue,getComment());
	}

	public String getComment() {
		return getForecastingBatchObj().getComment();
	}

	public Boolean isExclude(){
		return getForecastingBatchObj().isExclude();
	}

	/**
	 * Exclude from calculation
	 * @param value
	 */
	public void setExclude(Boolean value){
		Boolean oldValue = isExclude();
		getForecastingBatchObj().setExclude(value);
		firePropertyChange("exclude", oldValue, isExclude());
		firePropertyChange("include", !oldValue, getInclude());
	}

	public Boolean getInclude(){
		return !getForecastingBatchObj().isExclude();
	}

	/**
	 * Exclude from calculation
	 * @param value
	 */
	public void setInclude(Boolean value){
		Boolean oldValue = getInclude();
		Integer oldValue1 = getQuantity();
		getForecastingBatchObj().setExclude(!value);
		firePropertyChange("include", oldValue, getInclude());
		firePropertyChange("exclude", !oldValue, isExclude());
		firePropertyChange("quantity", oldValue1, getQuantity());
	}

	@Override
	public String toString() {
		return "ForecastingBatchUIAdapter [forecastingBatchObj="
				+ forecastingBatchObj + ", getExpiryDate()=" + getExpiryDate()
				+ ", getQuantity()=" + getQuantity()
				+ ", getQuantityAvailable()=" + getQuantityAvailable()
				+ ", getQuantityExpired()=" + getQuantityExpired()
				+ ", getConsumptionInMonth()=" + getConsumptionInMonth()
				+ ", getExpired()=" + getExpired() + ", isExclude()="
				+ isExclude() + "]";
	}


}
