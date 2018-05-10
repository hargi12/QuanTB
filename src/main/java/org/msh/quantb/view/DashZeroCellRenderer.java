package org.msh.quantb.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.JTable;

import org.msh.quantb.view.mvp.ViewFactory;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;
/**
 * Dash instead zero for Float and Integer
 * Also format float as need
 * @author alexey
 *
 */
public class DashZeroCellRenderer extends SubstanceDefaultTableCellRenderer {

	private boolean bold;
	private Color bcolor;
	private Color fcolor;
	private Color defaultFg;
	private List<Integer> colorRows = null;
	/**
	 * One possible constructor
	 * @param bold if need bold font
	 * @param bg - set background color, if null, do nothing
	 * @param fg set foreground color, if null, use default. Foreground color not applied for dash!!!
	 */
	public DashZeroCellRenderer(boolean bold, Color bg, Color fg){
		super();
		init(bold, bg, fg);
	}

	public DashZeroCellRenderer(boolean bold, Color bg, Color fg, List<Integer> _colorRows){
		super();
		init(bold, bg, fg);
		this.colorRows = _colorRows;
	}

	private void init(boolean bold, Color bg, Color fg) {
		if (bold){
			setFont(new Font(getFont().getName(),Font.BOLD,getFont().getSize()));
		}
		this.bold = bold;
		this.bcolor = bg;
		this.fcolor = fg;
		if (this.bcolor != null){
			setBackground(bcolor);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6184487317719709149L;

	@Override
	public void setValue(Object value) {
		this.defaultFg = getForeground();
		if(this.bold){
			setFont(new Font(getFont().getName(),Font.BOLD,getFont().getSize()));
		}
		if (this.bcolor != null){
			setBackground(bcolor);
		}
		if (value instanceof Float || value instanceof BigDecimal){
			BigDecimal tmp = null;
			if (value instanceof Float){
				try {
					tmp = new BigDecimal(value.toString());
				} catch (Exception e) {
					tmp = BigDecimal.ZERO;
				}
			}else{
				tmp = (BigDecimal) value;
			}
			if (tmp.compareTo(BigDecimal.ZERO) == 0){
				setHorizontalAlignment(CENTER);
				setText("-");
			}else{
				setHorizontalAlignment(RIGHT);
				DecimalFormat formatter = new DecimalFormat(ViewFactory.DECIMAL_FORMAT);
				if(this.fcolor != null){
					setForeground(fcolor);
				}
				super.setValue(formatter.format(value));
			}
		}else{
			if (value instanceof Integer){

				Integer tmp = (Integer) value;
				if (tmp==0){
					setHorizontalAlignment(CENTER);
					setText("-");
				}else{
					if(this.fcolor != null){
						setForeground(fcolor);
					}
					setHorizontalAlignment(RIGHT);
					DecimalFormat formatter = new DecimalFormat("#,###,###");
					super.setValue(formatter.format(value));
				}
			}else{
				super.setValue(value);
			}
		}
	}

	@Override
	public Color getForeground(){
		return super.getForeground();
	}
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if(colorRows != null){
			if(colorRows.size() > 0){
				if(!colorRows.contains(new Integer(row)))
					comp.setForeground(getDefaultFg());
			}else
				comp.setForeground(getDefaultFg());
		}
		return comp;
	}

	public boolean isBold() {
		return bold;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public Color getBcolor() {
		return bcolor;
	}

	public void setBcolor(Color bcolor) {
		this.bcolor = bcolor;
	}

	public Color getFcolor() {
		return fcolor;
	}

	public void setFcolor(Color fcolor) {
		this.fcolor = fcolor;
	}

	public Color getDefaultFg() {
		return defaultFg;
	}

	public void setDefaultFg(Color defaultFg) {
		this.defaultFg = defaultFg;
	}

}
