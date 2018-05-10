package org.msh.quantb.view.tableExt;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * Same as DivMultiLineCellRenderer, but add inset border needed for header
 * @author alexey
 *
 */
public class DivMultiLineHeaderRenderer extends DivMultiLineCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6724417895112403994L;

	/**
	 * Only valid constructor
	 */
	public DivMultiLineHeaderRenderer() {
		super(JLabel.CENTER, false);
	}
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component ret = super.getTableCellRendererComponent(table, value, isSelected, true, row, column);
		JLabel label = (JLabel) ret;
		Color borderColor = UIManager.getColor("ComboBox.selectionBackground");
		//Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		Border border = BorderFactory.createLineBorder(borderColor);
		label.setBorder(border);
		return ret;
	}

}
