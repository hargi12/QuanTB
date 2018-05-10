package org.msh.quantb.services.io;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.msh.quantb.services.calc.DateUtils;

/**
 * Regimen or phase duration isn't easy entity because:
 * <ul>
 * <li> the duration measure may be in weeks or in months, in a future - in doses
 * <li> particular months has different number of days, so for determine particular dates in the duration period we need to know exact begin or exact end date
 * </ul>
 * This class provides convenient set of methods to work with the regimen or phase duration
 * @author alexey
 *
 */
public class RegimenPeriod {
	int months = 0;
	int weeks = 0;
	
	
	
	/**
	 * @return the months
	 */
	public int getMonths() {
		return months;
	}

	/**
	 * @param months the months to set
	 */
	public void setMonths(int months) {
		this.months = months;
	}

	/**
	 * @return the weeks
	 */
	public int getWeeks() {
		return weeks;
	}

	/**
	 * @param weeks the weeks to set
	 */
	public void setWeeks(int weeks) {
		this.weeks = weeks;
	}

	/**
	 * Calculate the first day of the phase or regimen
	 * @param endDay end day of the phase or regimen
	 * @return the first day of the phase or regimen
	 */
	public Calendar calcReverse(Calendar endDay){
		Calendar ret = calcCalendar(endDay, false);
		return ret;
		
	}

	
	/**
	 * Calculate the last day of the phase or regimen
	 * @param from first day of the regimen
	 * @return the last day of the phase or regimen
	 */
	public Calendar calcDirect(Calendar from) {
		Calendar ret = calcCalendar(from, true);
		return ret;
	}
	
	/**
	 * Calculate last or first day of the phase or regimen
	 * @param cal known last or first date
	 * @param isFirst if true, first parameter is first day of the regimen, otherwise  - last
	 * @return if isFirst then last, else first day of the regimen
	 */
	private Calendar calcCalendar(Calendar cal, boolean isFirst) {
		Calendar ret = GregorianCalendar.getInstance();
		DateUtils.cleanTime(cal);
		ret.setTime(cal.getTime());
		int mul=1;
		if (! isFirst){
			mul=-1;
		}
		ret.add(Calendar.MONTH, this.getMonths()*mul);
		ret.add(Calendar.DAY_OF_MONTH, getWeeks()*7*mul);
		ret.add(Calendar.DAY_OF_MONTH, mul*-1); //real begin is always at previous day
		return ret;
	}
	
	/**
	 * Add value to the months
	 * @param duration
	 */
	public void addMonth(int duration) {
		this.months = this.months + duration;
	}
	/**
	 * Add the weeks value
	 * @param duration
	 */
	public void addWeeks(int duration) {
		this.weeks = this.weeks + duration;
		
	}
	/**
	 * Add period to this
	 * @param intP period to add
	 */
	public void add(RegimenPeriod intP) {
		this.months = this.months + intP.getMonths();
		this.weeks = this.weeks + intP.getWeeks();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RegimenPeriod [months=" + months + ", weeks=" + weeks + "]";
	}



}
