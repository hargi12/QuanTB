package org.msh.quantb.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

/**
 * Cell renderer for java.util.Date. Show date-month-year under locale (dd/MM/yyyy)
 * 
 * @author User
 * 
 */
public class DateCellRenderer extends SubstanceDefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 965917008768015706L;

	@Override
	public void setValue(Object value) {
		String dateTxt = "-";
		if (value instanceof Date){
			if (value != null){
				String pattern = ((SimpleDateFormat) DateFormat.getDateInstance(DateFormat.MEDIUM)).toPattern();
				SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
				Calendar cal = GregorianCalendar.getInstance();
				Date date = (Date) value;
				cal.setTime(date);
				if (cal.get(Calendar.YEAR)!=9999){
					dateTxt = dateFormat.format(cal.getTime());	
				}
			}
		}
		setHorizontalAlignment(CENTER);
		setText(dateTxt);
	}




	/*	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		String dateTxt = "";
		String pattern = ((SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT)).toPattern();
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		Calendar cal = GregorianCalendar.getInstance();
		Date date = (Date) value;
		cal.setTime(date);
		if (cal.get(Calendar.YEAR)!=9999 && cal.get(Calendar.MONTH)!=9){
			dateTxt = dateFormat.format(cal.getTime());	
		}
		JLabel resultLbl = new JLabel(dateTxt);
		resultLbl.setHorizontalAlignment(SwingConstants.CENTER);
		return resultLbl;
	}*/

}
