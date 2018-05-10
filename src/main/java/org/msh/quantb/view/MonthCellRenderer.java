package org.msh.quantb.view;

import java.awt.Color;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.JTable;

import org.msh.quantb.services.io.MonthUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

/**
 * Cell renderer for MonthUIAdapter.
 * Show month-year under locale
 * Paint in yellow buffer months
 * Suit for cells and headers
 * @author User
 *
 */
public class MonthCellRenderer extends SubstanceDefaultTableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1376450684953771277L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-yyyy", new Locale(Messages.getLanguage(), Messages.getCountry()));
		Calendar cal = GregorianCalendar.getInstance();
		MonthUIAdapter adapter = (MonthUIAdapter) value;
		int year = adapter.getYear();
		int month = adapter.getMonth();
		int date = 1;		
		cal.set(year, month, date);
		String res = dateFormat.format(cal.getTime());
		setText(res);
		if (Presenter.monthInBuffer(adapter)){
			setBackground(Color.YELLOW);
			putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, new Double(1.0));
		}else{
			setBackground(table.getBackground());
		}
		return this;
	}
}
