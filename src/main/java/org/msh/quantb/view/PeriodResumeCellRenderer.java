package org.msh.quantb.view;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;

import org.msh.quantb.services.calc.PeriodResume;
import org.msh.quantb.services.mvp.Presenter;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

/**
 * Cell renderer for two dates.
 * Renders both as interval
 * If at least one month belongs to buffer time, paint yellow
 * Show month-year under locale
 * @author User
 *
 */
public class PeriodResumeCellRenderer extends SubstanceDefaultTableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1376450684953771277L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		PeriodResume adapter = (PeriodResume) value;
		String res = adapter.getFromTxt() + "..." + adapter.getToTxt() +" "+ adapter.getDaysBetweenPeriodTxt();
		setText(res);
		if (Presenter.periodInBuffer(adapter)){
			setBackground(Color.YELLOW);
			putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, new Double(1.0));
		}else{
			setBackground(table.getBackground());
		}
		return this;
	}
}
