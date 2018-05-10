package org.msh.quantb.view.tableExt;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableHeaderCellRenderer;
/**
 * Very basic header renderer compatible with the Substance
 * @author alexey
 *
 */
public class SimpleHeaderRenderer extends
		SubstanceDefaultTableHeaderCellRenderer {

	private static final long serialVersionUID = 2197066521201751613L;
	
	/**
	 * Only valid constructor
	 */
	public SimpleHeaderRenderer(){
		super();
		this.setHorizontalAlignment(JLabel.CENTER);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		boolean enable = table.isEnabled();
		table.setEnabled(true);
		Component ret = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		table.setEnabled(enable);
		JLabel label = (JLabel) ret;
		Color borderColor = UIManager.getColor("ComboBox.selectionBackground");
		Border border = BorderFactory.createLineBorder(borderColor);
		label.setBorder(border);
		return ret;
	}


}
