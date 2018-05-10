package org.msh.quantb.view.tableExt;

import java.awt.Color;
import java.awt.Component;
import java.text.NumberFormat;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

/**
 * Multiline text cell renderer
 *
 */
public class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {
	public static final String COLOR = ".COLOR";
	private static final long serialVersionUID = 9164463139744861122L;

	/**
	 * Constructor simple.<br>
	 * Because text area can't format text at all, some dirty trick use:
	 * <ul>
	 * <li>data to display is text
	 * <li> if text is end with substring .COLOR, font color will be set as red
	 * <li> .COLOR suffix will not be display
	 * </ul> 
	 * Subject to invent some more advanced
	 */
	public MultiLineCellRenderer() {
		super();
		setLineWrap(true);
		setWrapStyleWord(true);
		setOpaque(true);
	}


	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected) {
			setForeground(table.getSelectionForeground());
			setBackground(table.getSelectionBackground());
		} else {
			setForeground(table.getForeground());
			setBackground(table.getBackground());
		}
		setFont(table.getFont());
		if (hasFocus) {
			setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
			if (table.isCellEditable(row, column)) {
				setForeground(UIManager.getColor("Table.focusCellForeground"));
				setBackground(UIManager.getColor("Table.focusCellBackground"));
			}
		} else {
			setBorder(new EmptyBorder(1, 2, 1, 2));
		}
		String text ="";
		if (value != null){
			text = value.toString();
			if (text.endsWith(COLOR)){
				text = text.substring(0, text.lastIndexOf(COLOR));
				setForeground(Color.red);
			}
		}
		setText(text);
		return this;
	}

}
