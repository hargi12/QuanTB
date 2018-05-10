package org.msh.quantb.view;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.msh.quantb.services.mvp.Presenter;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultListCellRenderer;

/**
 * Universal renderer enum to List. May be used for combo boxes
 * @author alexey
 *
 */
public class EnumListRenderer extends SubstanceDefaultListCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6651561315866045534L;
	private String prefix;
	private String unknown = "UNKNOWN";

	/**
	 * Only valid constructor
	 * @param _prefix prefix for enum elements in Messages keys to show list elements in user friendly form<br>
	 * f.e. Regimen.types is prefix enum may be COMPLEX, SIMPLE. Keye in Messages are Regimen.types.COMPLEX, Regimen.types.SIMPLE
	 */
	public EnumListRenderer(String _prefix){
		super();
		this.prefix = _prefix;
	}

	@Override
	public
	Component getListCellRendererComponent(
			JList list,
			Object value,
			int index,
			boolean isSelected,
			boolean cellHasFocus){
		String suffix = "-";
		if(value != null){
			if(value.toString().equalsIgnoreCase("UNKNOWN")){
				suffix = this.unknown;
			}else{
				suffix = value.toString();
			}
			this.setText(Presenter.getMessage(this.prefix + "." + suffix));
		}else{
			this.setText("-");
		}
		
		return this;

	}
	/**
	 * Change suffix for UNKNOWN, for example All to - etc
	 */
	public void showUnknownAsEmpty() {
		this.unknown = "OTHER";

	}



}
