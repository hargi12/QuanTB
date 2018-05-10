package org.msh.quantb.view;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

/**
 * Cell renderer with bold text data
 * 
 */
public class BoldTextCellRenderer extends JTextArea implements TableCellRenderer {
	private static final String DASH = "-";
	private static final long serialVersionUID = 992457156921685413L;
	/**
	 * Constructor
	 */
	public BoldTextCellRenderer() {
		setLineWrap(true);
		setWrapStyleWord(true);
		setOpaque(true);		
	}
	/**
	 * align content on center
	 */
	public void alignCenter(){

	}
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected) {
			setForeground(table.getSelectionForeground());
			setBackground(table.getSelectionBackground());
		} else {
			setForeground(table.getForeground());
			setBackground(table.getBackground());
		}
		setFont(new Font(table.getFont().getName(),Font.BOLD,table.getFont().getSize()));		
		if (hasFocus) {
			setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
			if (table.isCellEditable(row, column)) {
				setForeground(UIManager.getColor("Table.focusCellForeground"));
				setBackground(UIManager.getColor("Table.focusCellBackground"));
			}
		} else {
			setBorder(new EmptyBorder(1, 2, 1, 2));
		}
		String res = DASH;
		if (value != null){
			if(value.toString().equals("0")){
				res =DASH;
			}else{
				res = value.toString();
			}
		}
		setText(res);
		return this;		
	}

}
