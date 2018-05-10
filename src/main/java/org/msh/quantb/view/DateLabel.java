package org.msh.quantb.view;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JLabel;

/**
 * Swing label to display date in medium format
 * setText works as JLabel
 * @author Alex Kurasoff
 *
 */
public class DateLabel extends JLabel {
	private static final long serialVersionUID = -5271864542614702559L;
	private DateFormat format;

	public DateLabel(){
		super();
		this.format = DateFormat.getDateInstance(DateFormat.MEDIUM);
	}
	
	
	/**
	 * current format
	 * @return
	 */
	public DateFormat getFormat() {
		return format;
	}


	/**
	 * To change format from default medium
	 * @param format
	 */
	public void setFormat(DateFormat format) {
		this.format = format;
	}

	/**
	 * setter for java.util.Date
	 * @param dt
	 */
	public void setDate(Date dt) {
		this.setText(getFormat().format(dt));
	}
	/**
	 * setter for java.util.Calendar
	 * @param cal
	 */
	public void setCalendar(Calendar cal){
		setDate(cal.getTime());
	}

}
