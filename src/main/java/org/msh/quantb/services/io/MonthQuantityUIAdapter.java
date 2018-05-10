package org.msh.quantb.services.io;

import java.math.BigDecimal;

import org.msh.quantb.model.forecast.Month;
import org.msh.quantb.model.forecast.MonthQuantity;
import org.msh.quantb.model.mvp.ModelFactory;

/**
 * MonthQuantity object adapted to UI
 * @author alexey
 *
 *
 */
public class MonthQuantityUIAdapter extends AbstractUIAdapter implements Comparable<MonthQuantityUIAdapter> {
	private MonthQuantity monthQuantityObj;
	private MonthUIAdapter monthUIadapter=null;
	/**
	 * Only valid constructor
	 * @param monthQuantityObj
	 */
	public MonthQuantityUIAdapter(MonthQuantity monthQuantityObj) {
		super();
		this.monthQuantityObj = monthQuantityObj;
	}
	/**
	 * @return the monthQuantityObj
	 */
	public MonthQuantity getMonthQuantityObj() {
		return monthQuantityObj;
	}
	/**
	 * @param monthQuantityObj the monthQuantityObj to set
	 */
	public void setMonthQuantityObj(MonthQuantity monthQuantityObj) {
		MonthQuantity oldValue = getMonthQuantityObj();
		this.monthQuantityObj = monthQuantityObj;
		firePropertyChange("monthQuantityObj", oldValue, getMonthQuantityObj());
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.MonthQuantity#getIQuantity()
	 */
	public Integer getIQuantity() {
		return monthQuantityObj.getIQuantity();
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.MonthQuantity#setIQuantity(java.lang.Integer)
	 */
	public void setIQuantity(Integer value) {
		Integer oldValue = getIQuantity();
		monthQuantityObj.setIQuantity(value);
		firePropertyChange("iQuantity", oldValue, getIQuantity());
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.MonthQuantity#getMonth()
	 */
	public MonthUIAdapter getMonth() {
		if (monthUIadapter == null) monthUIadapter = new MonthUIAdapter(monthQuantityObj.getMonth());
		return monthUIadapter;
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.MonthQuantity#setMonth(org.msh.quantb.model.forecast.Month)
	 */
	public void setMonth(MonthUIAdapter value) {
		MonthUIAdapter oldValue = getMonth();
		monthQuantityObj.setMonth(value.getMonthObj());
		firePropertyChange("month", oldValue, getMonth());
	}

	@Override
	public boolean equals(Object _another){
		if (super.equals(_another)){
			MonthQuantityUIAdapter another = (MonthQuantityUIAdapter) _another;
			return this.getMonth().equals(another.getMonth()) && this.getIQuantity().equals(another.getIQuantity());
		}else return(false);
	}
	@Override
	public int compareTo(MonthQuantityUIAdapter o) {
		if (o == null) return 1;
		int ret = this.getMonth().compareTo(o.getMonth());
		if (ret == 0){
			if (this.getIQuantity().equals(o.getIQuantity())) return 0;
			else return ret;
		}else return ret;
	}
	@Override
	public String toString(){
		return this.getMonth().toString() + " - " + this.getIQuantity();
	}
	/**
	 * Same as getMonth, by will return deep clone of month object<br>
	 * So, you can free modify this copy. Any modification will not reflects to object
	 * @param modelFactory - model factory
	 * @return
	 */
	public MonthUIAdapter getMonthCopy(ModelFactory modelFactory) {
		Month m = modelFactory.createMonth(this.getMonth().getMonthObj().getYear(), this.getMonth().getMonthObj().getMonth());
		return new MonthUIAdapter(m);
	}
	/**
	 * Create deep clone of this object
	 * @param modelFactory
	 * @return
	 */
	public MonthQuantityUIAdapter getClone(ModelFactory modelFactory) {
		MonthQuantity mq = modelFactory.createMonthQuantity(this.getMonth().getYear(), this.getMonth().getMonth(), this.getIQuantity());
		return new MonthQuantityUIAdapter(mq);
	}
	/**
	 * Get quantity as BigDecimal
	 * @return
	 */
	public BigDecimal getBDQuantity() {
		return new BigDecimal(getIQuantity());
	}


}
