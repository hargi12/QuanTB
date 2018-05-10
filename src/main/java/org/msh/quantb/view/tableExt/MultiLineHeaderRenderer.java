package org.msh.quantb.view.tableExt;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.border.EtchedBorder;
import javax.swing.table.TableCellRenderer;

/**
 * Multiline table header renderer
 *
 */
public class MultiLineHeaderRenderer extends JList implements TableCellRenderer {
	private static final long serialVersionUID = -2194767947550321119L;

	public MultiLineHeaderRenderer() {
		setOpaque(true);		
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		ListCellRenderer renderer = getCellRenderer();
		((JLabel) renderer).setHorizontalAlignment(JLabel.CENTER);
		setCellRenderer(renderer);		
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		setFont(table.getFont());
		String str = (value == null) ? "" : value.toString();
		BufferedReader br = new BufferedReader(new StringReader(str));
		String line;
		Vector<String> v = new Vector<String>();
		try {
			while ((line = br.readLine()) != null) {
				v.addElement(line);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		setListData(v);
		return this;
	}
}
