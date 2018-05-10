package org.msh.quantb.view;

import java.awt.Color;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

/**
 * Colors cell renderer
 * @author User
 *
 */
public class CellRendererColor extends SubstanceDefaultTableCellRenderer {	
	private static final long serialVersionUID = 2317143799874019032L;
	private Color color;
	/**
	 * Constructor
	 * @param color cell color
	 */
	public CellRendererColor(Color color) {
		this.color = color;
	}	
	
	@Override
	public Color getBackground() {		
		return color;
	}
}
