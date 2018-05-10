package org.msh.quantb.services.io;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.msh.quantb.services.calc.DateUtils;

/**
 * Allows to pick up dates
 * Compatibility with BeansBinding
 * @author Alexey Kurasov
 *
 */
public class SelectableDates extends AbstractUIAdapter implements Comparable<SelectableDates> {
	private Calendar calendar=null;
	boolean visible;
	
	/**
	 * Only valid constructor
	 * @param cal date
	 * @param _visible true, if should be visible
	 * @param _checked true, if checked
	 */
	public SelectableDates(Calendar cal, boolean _visible, boolean _checked){
		setCalendar(cal);
		setVisible(_visible);
		setChecked(_checked);
	}

	public Calendar getCalendar() {
		return calendar;
	}
	
	/**
	 * Get text that represents date
	 * @return
	 */
	public String getDateTxt(){
		return DateUtils.formatMedium(getCalendar());
	}

	public void setCalendar(Calendar calendar) {
		Calendar oldValue = getCalendar();
		if (calendar != null){
		this.calendar = DateUtils.getCleanCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
		}else{
			this.calendar = null;
		}
		firePropertyChange("calendar", oldValue, getCalendar());
	}
	
	

	public boolean isVisible() {
		return visible;
	}
	

	public void setVisible(boolean visible) {
		this.visible = visible;
		
	}

	@Override
	public int compareTo(SelectableDates o) {
		if (o == null){
			return 1;
		}
		if (this.getCalendar().getTimeInMillis() > o.getCalendar().getTimeInMillis()){
			return 1;
		}
		if (this.getCalendar().getTimeInMillis() < o.getCalendar().getTimeInMillis()){
			return -1;
		}
		return 0;
	}

	@Override
	public String toString() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return "SelectableDates [getCalendar()=" + format.format(getCalendar().getTime())
				+ ", getChecked()=" + getChecked() +", isVisiable()="+isVisible()+"]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((calendar == null) ? 0 : calendar.hashCode());
		result = prime * result + (visible ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SelectableDates other = (SelectableDates) obj;
		if (calendar == null) {
			if (other.calendar != null)
				return false;
		} else if (!calendar.equals(other.calendar))
			return false;
		if (visible != other.visible)
			return false;
		return true;
	}

	/**
	 * Get clone of calendar for calculations
	 * @return
	 */
	public Calendar getCalendarClone() {
		Calendar ret = GregorianCalendar.getInstance();
		ret.setTime(getCalendar().getTime());
		DateUtils.cleanTime(ret);
		return ret;
	}

	/**
	 * Set checked value without property change event
	 * @param b
	 */
	public void setCheckedSilently(boolean b) {
		checked = b;
		
	}
	
	

}
