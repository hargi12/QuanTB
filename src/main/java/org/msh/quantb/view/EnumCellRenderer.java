package org.msh.quantb.view;

import java.awt.Component;

import javax.swing.JTable;

import org.msh.quantb.services.mvp.Presenter;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

/**
 * Universal renderer enum to the table cell
 * @author alexey
 *
 */
public class EnumCellRenderer extends SubstanceDefaultTableCellRenderer {


	private static final long serialVersionUID = -3773449999912165557L;
	private String prefix;
	private String unknown = "UNKNOWN";
	
	/**
	 * Only valid constructor
	 * @param _prefix prefix for enum elements in Messages keys to show list elements in user friendly form<br>
	 * f.e. Regimen.types is prefix enum may be COMPLEX, SIMPLE. Keye in Messages are Regimen.types.COMPLEX, Regimen.types.SIMPLE
	 */
	public EnumCellRenderer(String _prefix){
		super();
		this.prefix = _prefix;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable arg0, Object value,
			boolean arg2, boolean arg3, int row, int column) {
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

}
