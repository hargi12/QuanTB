package org.msh.quantb.view;

import java.awt.Color;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import org.msh.quantb.view.mvp.ViewFactory;

/**
 * Format BigDecimal as whole numbers, without dec fraction
 * @author Alex Kurasoff
 *
 */
public class BigDecimalCellRenderer extends DashZeroCellRenderer {

	private static final long serialVersionUID = -4676540251009040280L;

	public BigDecimalCellRenderer(boolean bold, Color bg, Color fg, List<Integer> _colorRows) {
		super(bold, bg, fg, _colorRows);
		// TODO Auto-generated constructor stub
	}

	public BigDecimalCellRenderer(boolean bold, Color bg, Color fg) {
		super(bold, bg, fg);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void setValue(Object value) {
		if (value instanceof Float || value instanceof BigDecimal){
			BigDecimal tmp = null;
			if (value instanceof Float){
				tmp = new BigDecimal(value.toString());
			}else{
				tmp = (BigDecimal) value;
			}
			if (tmp.compareTo(BigDecimal.ZERO) == 0){
				setHorizontalAlignment(CENTER);
				setText("-");
			}else{
				setHorizontalAlignment(RIGHT);
				DecimalFormat formatter = new DecimalFormat(ViewFactory.BIG_WHOLE_FORMAT);
				super.setValue(formatter.format(value));
			}
		}else{
			super.setValue(value);
		}
	}
	
}
