package org.msh.quantb.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Map;

import javax.swing.JTable;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;
/**
 * This class intends for render long text value, that not always fit to cell, so show it as tooltip also
 * @author alexey
 *
 */
public class ToolTipCellRenderer extends SubstanceDefaultTableCellRenderer {

	private Color color=null;

	/**
	 * Main constructor
	 */
	public ToolTipCellRenderer(){
		super();
	}
	/**
	 * Constructor with font color
	 * @param _color
	 */
	public ToolTipCellRenderer(Color _color){
		super();
		this.color = _color;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1652352430025623452L;
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus,
			int row, int column) {
		String s = value!=null?value.toString():"";
		this.setToolTipText(s);
		Component comp = super.getTableCellRendererComponent(table, value, isSelected,hasFocus,row,column);
		if(color != null){
			comp.setForeground(color);
		}
		return comp;
	}


}
