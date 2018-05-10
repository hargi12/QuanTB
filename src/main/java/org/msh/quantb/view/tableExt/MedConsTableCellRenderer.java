package org.msh.quantb.view.tableExt;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
/**
 * Specific renderer for Medicine Consumption table. It's impossible to "froze" first column in this table, so use tooltip instead
 * @author Alexey Kurasov
 *
 */
public class MedConsTableCellRenderer extends DashZeroCellRenderer {

	private static final long serialVersionUID = 8617969565862010354L;
	private int medNameRow;

	public MedConsTableCellRenderer(boolean bold, Color bg, int redStorm,
			int redInterval) {
		super(bold, bg, redStorm, redInterval);
		this.medNameRow = redInterval;
	}



	public int getMedNameRow() {
		return medNameRow;
	}



	public void setMedNameRow(int medNameRow) {
		this.medNameRow = medNameRow;
	}



	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus,
			int row, int column) {
		if(column>0){
			int off = row % getMedNameRow();
			if (off != 0){
				String s = "<html> <body color='BLACK'>" + (String)table.getValueAt(row-off, 0) + "<br>" + (String) table.getValueAt(row, 0) + "</body></html>";
				this.setToolTipText(s);
			}
		}
		return super.getTableCellRendererComponent(table, value, isSelected,hasFocus,row,column);
	}

}
