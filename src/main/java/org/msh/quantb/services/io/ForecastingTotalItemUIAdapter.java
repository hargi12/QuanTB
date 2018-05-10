package org.msh.quantb.services.io;

import java.math.BigDecimal;

import org.msh.quantb.model.forecast.ForecastingTotalItem;
import org.msh.quantb.services.mvp.Presenter;

/**
 * One additional cost item for order
 * @author alexey
 *
 */
public class ForecastingTotalItemUIAdapter extends AbstractUIAdapter {
	private ForecastingTotalItem fcItemObj;

	
	/**
	 * Only valid constructor
	 * @param item fcItemObject
	 */
	public ForecastingTotalItemUIAdapter(ForecastingTotalItem item){
		this.fcItemObj = item;
	}
	
	/**
	 * @return the item
	 */
	public String getItem() {
		return fcItemObj.getItem();
	}
	/**
	 * @param item the item to set
	 */
	public void setItem(String item) {
		String oldValue = getItem();
		if(item==null){
			item = ""; //prevent null!
		}
		fcItemObj.setItem(item);
		firePropertyChange("item", oldValue, getItem());
	}
	/**
	 * @return the perCents
	 */
	public BigDecimal getPerCents() {
		return fcItemObj.getPerCents();
	}
	/**
	 * @param perCents the perCents to set
	 */
	public void setPerCents(BigDecimal perCents) {
		if (perCents == null) return;
		BigDecimal oldValue = getPerCents();
		fcItemObj.setPerCents(perCents);
		firePropertyChange("perCents", oldValue, getPerCents());
		firePropertyChange("perCentsOrZero", oldValue, getPerCents());
	}
	
	/**
	 * Get percents, but only if calculation method is by percents
	 * @return
	 */
	public BigDecimal getPerCentsOrZero(){
		if(getFcItemObj().isIsValue()){
			return BigDecimal.ZERO;
		}else{
			return getPerCents();
		}
	}
	/**
	 * Setter is important
	 * @param pers
	 */
	public void setPerCentsOrZero(BigDecimal pers){
		setPerCents(pers);
	}
	
	/**
	 * @return the fcItemObj
	 */
	public ForecastingTotalItem getFcItemObj() {
		return fcItemObj;
	}

	/**
	 * @return the value
	 */
	public BigDecimal getValue() {
		BigDecimal val = this.getFcItemObj().getValue();
		if (val == null){
			val = new BigDecimal("0");
		}
		return val;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(BigDecimal value) {
		BigDecimal oldValue = getValue();
		getFcItemObj().setValue(value);
		firePropertyChange("value", oldValue, getValue());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getFcItemObj().getItem();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fcItemObj.getItem() == null) ? 0 : fcItemObj.getItem().hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if(this.getItem() == null) return false;
		if(obj.getClass().equals(this.getClass())){
			ForecastingTotalItemUIAdapter another = (ForecastingTotalItemUIAdapter) obj;
			return this.getItem().equalsIgnoreCase(another.getItem());
		}else{
			return false;
		}
	}
	/**
	 * There are two calculation methods - by quantity and by percents
	 * @return
	 */
	public String getCalculationMethod(){
		String res = Presenter.getMessage("ForecastingDocumentWindow.tbSummary.byPercents");
		if(getFcItemObj().isIsValue()){
			res = Presenter.getMessage("ForecastingDocumentWindow.tbSummary.byValue");
		}
		return res;
	}
	/**
	 * Change calculation method
	 * @param newValue true - by value, false - by percent's (default)
	 */
	public void setIsValue(Boolean newValue){
		Boolean oldValue = getFcItemObj().isIsValue();
		getFcItemObj().setIsValue(newValue);
		firePropertyChange("calculationMethod", oldValue, newValue);
	}
	
	
	
	/**
	 * make a clone
	 */
	public ForecastingTotalItemUIAdapter clone(){
		ForecastingTotalItem obj = Presenter.getFactory().createForecastingTotalItem(getItem(), getPerCents());
		obj.setIsValue(getFcItemObj().isIsValue());
		obj.setValue(getValue());
		return new ForecastingTotalItemUIAdapter(obj);
	}
	
}
