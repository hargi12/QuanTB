package org.msh.quantb.services.io;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.msh.quantb.model.forecast.Month;
import org.msh.quantb.model.mvp.ModelFactory;
import org.msh.quantb.services.calc.DateUtils;

/**
 * Month object adapted to UI operations
 * @author alexey
 *
 */
public class MonthUIAdapter extends AbstractUIAdapter implements Comparable<MonthUIAdapter> {
	private Month monthObj;
	/**
	 * only valid constructor
	 * @param _month
	 */
	public MonthUIAdapter(Month _month){
		this.monthObj = _month;
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.Month#getYear()
	 */
	public Integer getYear() {
		return monthObj.getYear();
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.Month#setYear(int)
	 */
	public void setYear(Integer value) {
		Integer old = getYear();
		monthObj.setYear(value);
		firePropertyChange("year", old, monthObj.getYear());
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.Month#getMonth()
	 */
	public Integer getMonth() {
		return monthObj.getMonth();
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.Month#setMonth(int)
	 */
	public void setMonth(Integer value) {
		Integer oldValue = getMonth();
		monthObj.setMonth(value);
		firePropertyChange("month", oldValue, getMonth());
	}
	/**
	 * @return the monthObj
	 */
	public Month getMonthObj() {
		return monthObj;
	}
	/**
	 * @param monthObj the monthObj to set
	 */
	public void setMonthObj(Month monthObj) {
		Month oldValue = getMonthObj();
		this.monthObj = monthObj;
		firePropertyChange("monthObj", oldValue, getMonthObj());
	}
	@Override
	public boolean equals(Object _another){
		if (super.equals(_another)){
			MonthUIAdapter another = (MonthUIAdapter) _another;
			return this.getMonth().equals(another.getMonth()) && this.getYear().equals(another.getYear());
		}else return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + monthObj.getMonth();
		result = prime * result + monthObj.getYear();
		return result;
	}

	@Override
	public int compareTo(MonthUIAdapter o) {
		if (o == null) return 1;
		if (this.getYear() > o.getYear()) return 1;
		if (o.getYear() > this.getYear()) return -1;
		if (this.getMonth() > o.getMonth()) return 1;
		if (o.getMonth() > this.getMonth()) return -1;
		return 0;
	}
	@Override
	public String toString(){
		//return "year "  +this.getYear() + " month " + this.getMonth();
		Calendar cal = new GregorianCalendar();
		cal.set(this.getYear(), this.getMonth(), 1);
		DateUtils.cleanTime(cal);
		return DateUtils.formatDate(cal.getTime(), "MMM-yy");
	}
	
	/**
	 * Get string representation of full date
	 * @return
	 */
	public String getFirstDateAsString(){
		Calendar cal = DateUtils.getCleanCalendar(this.getYear(), this.getMonth(), 1);
		return DateUtils.formatMedium(cal);
	}
	
	/**
	 * For the current object incremnet month on index given<br>
	 * Be carefully!!! CURRENT OBJECT
	 * @param i
	 * @return
	 */
	public MonthUIAdapter incrementMonth(int i) {
		Calendar cal = GregorianCalendar.getInstance();
		DateUtils.cleanTime(cal);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH,this.getMonth().intValue());
		cal.set(Calendar.YEAR,this.getYear().intValue());
		cal.add(Calendar.MONTH, i);
		this.setMonth(cal.get(Calendar.MONTH));
		this.setYear(cal.get(Calendar.YEAR));
		return this;
	}
	/**
	 * Increment month and make a clone
	 * @param modelFactory
	 * @param i
	 * @return
	 */
	public MonthUIAdapter incrementClone(ModelFactory modelFactory, int i) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH,this.getMonth().intValue());
		cal.set(Calendar.YEAR,this.getYear().intValue());
		cal.add(Calendar.MONTH, i);
		Month m = modelFactory.createMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
		return new MonthUIAdapter(m);
	}
	/**
	 * Get first day of month
	 * @return
	 */
	public Date getFirstDate() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH,1);
		setCalendarFields(cal);
		return cal.getTime();
	}
	/**
	 * set general calendar fields
	 * @param cal
	 */
	private void setCalendarFields(Calendar cal) {
		cal.set(Calendar.MONTH, this.getMonth());
		cal.set(Calendar.YEAR, this.getYear());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}
	/**
	 * get last day of month
	 * @return
	 */
	public Date getLastDate() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(getFirstDate());
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}
	/**
	 * Get month days quantity
	 * @return
	 */
	public int getDays() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(getFirstDate());
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	/**
	 * Get calendar object for day in the month
	 * @param day
	 * @return date, based on input parameters or NULL, if day wrong
	 */
	public Calendar getAnyDate(int day) {
		Calendar cal = GregorianCalendar.getInstance();
		DateUtils.cleanTime(cal);
		cal.set(Calendar.DAY_OF_MONTH,day);
		setCalendarFields(cal);
		int oldMonth = this.getMonth();
		if (this.getMonth() == oldMonth){
			return cal;
		}else{
			return null;
		}
	}
}
