package org.msh.quantb.view.tableExt;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.msh.quantb.view.mvp.ViewFactory;
/**
 * Dash instead zero for Float and Integer
 * Also format float as need
 * Also may change color and boldeness
 * Also may paint foreground color and boldness for defined row
 * @author alexey
 *
 */
public class DashZeroCellRenderer extends DefaultTableCellRenderer {


	private boolean bold;
	private Color bcolor;
	/**
	 * From this line and use redInterval table rows will be painted with different foreground color
	 * if redStorm is -1, then no actions will be happened
	 */
	private int redStorm = -1; private int redInterval = 0;
	/**
	 * the most popular constructor
	 * @param bold if need bold font
	 * @param bg - set background color, if null, do nothing
	 */
	public DashZeroCellRenderer(boolean bold, Color bg){
		super();
		if (bold){
			setFont(new Font(getFont().getName(),Font.BOLD,getFont().getSize()));
		}
		this.bold = bold;
		this.bcolor = bg;
		if (this.bcolor != null){
			setBackground(bcolor);
		}
	}

	/**
	 * the most popular constructor
	 * @param bold if need bold font
	 * @param bg - set background color, if null, do nothing
	 * @param redStorm - red storm will be begin from this line
	 * @param redStorm will be with this interval from the redStrom line
	 */
	public DashZeroCellRenderer(boolean bold, Color bg, int redStorm, int redInterval){
		super();
		this.bold = bold;
		this.bcolor = bg;
		setRedStorm(redStorm);
		setRedInterval(redInterval);
	}


	/**
	 * @param redStorm the redStorm to set
	 */
	public void setRedStorm(int redStorm) {
		this.redStorm = redStorm;
	}



	/**
	 * @return the redStorm
	 */
	public int getRedStorm() {
		return redStorm;
	}


	/**
	 * @return the redInterval
	 */
	public int getRedInterval() {
		return redInterval;
	}

	/**
	 * @param redInterval the redInterval to set
	 */
	public void setRedInterval(int redInterval) {
		this.redInterval = redInterval;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component ret = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (getRedStorm() != -1 && getRedInterval()  >0){
			int shiftRow = row - getRedStorm();
			if ((shiftRow == 0) || (shiftRow % getRedInterval() == 0)){
				ret.setForeground(Color.RED);
				if(this.bold){
					setFont(new Font(getFont().getName(),Font.BOLD,getFont().getSize()));
				}
			}else{
				ret.setForeground(Color.BLACK);
			}
		}

		return ret;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = -6184487317719709149L;
	@Override
	public void setValue(Object value) {
		if (getRedStorm() == 0){
			if(this.bold){
				setFont(new Font(getFont().getName(),Font.BOLD,getFont().getSize()));
			}
			if (this.bcolor != null){
				setBackground(bcolor);
			}
		}
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
				DecimalFormat formatter = new DecimalFormat(ViewFactory.DECIMAL_FORMAT);
				super.setValue(formatter.format(value));
			}
		}else{
			if (value instanceof Integer){
				Integer tmp = (Integer) value;
				if (tmp==0){
					setHorizontalAlignment(CENTER);
					setText("-");
				}else{
					setHorizontalAlignment(RIGHT);
					DecimalFormat formatter = new DecimalFormat("###,###,###");
					super.setValue(formatter.format(value));
				}
			}else{
				super.setValue(value);
			}
		}
	}


}
