package org.msh.quantb.view.tableExt;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

/**
 * Cell renderer for multi line text. Use HTML div.
 * Automatically wrap text.
 * Can align text inside the cell.
 * Can change text color by condition:
 * <ul>
 * <li>data to display is text
 * <li> if text is end with substring .COLOR, font color will be set as red
 * <li> .COLOR suffix will not be display
 * </ul> 
 *
 */
public class DivMultiLineCellRenderer extends SubstanceDefaultTableCellRenderer{
	public static final String COLOR = ".COLOR";
	private static final long serialVersionUID = 9164463139744861122L;
	private String align;
	private boolean isBold;
	private int MAX_CHART_LINE = 15;

	/**
	 * Only valid constructor
	 * @param _align - use SWING constants such are JLabel.CENTER, default LEFT
	 * @param _isBold does text must be bold
	 */
	public DivMultiLineCellRenderer(int _align, boolean _isBold) {
		super();
		setOpaque(true);
		this.align = "left";
		if(_align == JLabel.CENTER){
			this.align="center";
		}
		if(_align == JLabel.RIGHT){
			this.align = "right";
		}
		this.isBold = _isBold;
	}


	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = this;
		try {
			label = (JLabel)super.getTableCellRendererComponent(table,
					value, isSelected, hasFocus, row, column);
		} catch (Exception e) {
			//nothing to do
		}
		if (column == -1){ //header!!
			column = 0;
		}
		String text ="";
		if (value != null){
			text = value.toString();
			if (text.endsWith(COLOR)){
				text = text.substring(0, text.lastIndexOf(COLOR));
				label.setForeground(Color.red);
			}
		}
		if(isBold){
			label.setFont(new Font(table.getFont().getName(),Font.BOLD,table.getFont().getSize()));
		}
		int width = table.getColumnModel().getColumn(column).getWidth();
		width = width - width/4;
		if(isBold){
			width = width - 12;
		}

		if(column == 0){
			if(text.length() > MAX_CHART_LINE){
				String[] mass = text.split("\\ ");
				text = "";
				for(String m:mass){
					m = m.trim();
					if(!m.isEmpty()){
						if(m.length() > MAX_CHART_LINE){
							boolean isAdd = false;
							String[] chars = new String[2];
							chars[0] = "+";
							chars[1] = "/";
							for(String charr:chars){
								String str = m.substring(0, MAX_CHART_LINE);
								int ind = str.lastIndexOf(charr);
								if(ind != -1){
									if(ind != (str.length() - 1)){// т.е. строка закончилась не на charr
										ind = str.lastIndexOf(charr);
										text += (!text.isEmpty()?" ":"") + m.substring(0, ind + 1) + " " + m.substring(ind + 1);
									}else// т.е. строка закончилась на charr
										text += (!text.isEmpty()?" ":"") + m.substring(0, ind + 1) + " " + m.substring(ind + 1);
									isAdd = true;
									break;
								}
							}
							
							if(!isAdd){
								text += (!text.isEmpty()?" ":"") + m.substring(0, MAX_CHART_LINE + 1) + " " + m.substring(MAX_CHART_LINE + 1);
							}
						}else
							text += (!text.isEmpty()?" ":"") + m;
					}
				}
				text.trim();
			}

		}
		text = String.format("<html><div style=\"width:%dpx;text-align:%s;padding:3px;word-wrap:break-word;\">%s</div><html>", width, this.align, text.trim());
		label.setText(text);
		return label;
	}

}
