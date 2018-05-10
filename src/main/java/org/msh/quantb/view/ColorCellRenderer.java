package org.msh.quantb.view;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

/**
 * Cellrenderer which specify background color of cells
 * 
 */
public class ColorCellRenderer extends SubstanceDefaultTableCellRenderer {

	private static final long serialVersionUID = -3228503319186463319L;
	private int border;

	public ColorCellRenderer() {
		border = 0;
	}

	/**
	 * Constructor
	 * 
	 * @param borderComponent 0 - whitiout border, 1 - minBorder, 2 - maxBorder
	 */
	public ColorCellRenderer(int border) {
		this.border = border;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = this;
		try {
			label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		} catch (Exception e) {
			//nothing to do
		}
		Color colVal = null;
		try {
			colVal = (Color) value;
		} catch (ClassCastException ex) {
			colVal = Color.WHITE;
		}
		String text = "";		
		label.setText(text);				
		setBackground(colVal);
		return label;
	}
}
