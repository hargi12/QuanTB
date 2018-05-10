package org.msh.quantb.view.tableExt;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
/**
 * This class intends only for very long tables, placed inside scroll bar
 * Headers extended form SubstanceDefaultTableHeaderCellRenderer not work properly in this case. I don't know and don't wish to know why.
 * You can change 
 * @author alexey
 *
 */
public class DivSimpleHeaderRenderer extends JLabel implements TableCellRenderer {

	/**
	 * Only valid constructor
	 */
	public DivSimpleHeaderRenderer(){
		super();
		setOpaque(true);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2023790357104587095L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		int width = table.getColumnModel().getColumn(column).getWidth();
		width = width - width/4;
		String text = String.format("<html><div style=\"width:%dpx;text-align:%s;padding:5px;word-wrap:break-word;\">%s</div><html>", width, "center", value.toString());
		setText(text);
		return this;
	}

}
