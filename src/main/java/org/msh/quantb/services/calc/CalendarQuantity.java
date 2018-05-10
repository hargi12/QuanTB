package org.msh.quantb.services.calc;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * Save some counter together with calendar date
 * @author alexey
 *
 */
public class CalendarQuantity implements Comparable<CalendarQuantity> {

	private Calendar calendar;
	private BigDecimal quantity;

	public CalendarQuantity(Calendar cal, BigDecimal quantity){
		DateUtils.cleanTime(cal);
		this.calendar = cal;
		this.quantity = quantity;
	}

	/**
	 * @return the calendar
	 */
	public Calendar getCalendar() {
		return calendar;
	}

	/**
	 * @param calendar the calendar to set
	 */
	public void setCalendar(Calendar calendar) {
		DateUtils.cleanTime(calendar);
		this.calendar = calendar;
	}

	/**
	 * @return the quantity
	 */
	public BigDecimal getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CalendarQuantity [calendar=" + DateUtils.formatDate(calendar.getTime(),"dd-MM-yyyy") + ", quantity="
				+ quantity + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((calendar == null) ? 0 : calendar.hashCode());
		result = prime * result
				+ ((quantity == null) ? 0 : quantity.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CalendarQuantity other = (CalendarQuantity) obj;
		if (calendar == null) {
			if (other.calendar != null) {
				return false;
			}
		} else if (!calendar.equals(other.calendar)) {
			return false;
		}
		if (quantity == null) {
			if (other.quantity != null) {
				return false;
			}
		} else if (quantity.compareTo(other.quantity) != 0) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(CalendarQuantity arg0) {
		return this.getCalendar().compareTo(arg0.getCalendar());
	}
	
	
	
}
