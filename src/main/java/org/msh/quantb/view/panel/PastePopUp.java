package org.msh.quantb.view.panel;

import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.msh.quantb.services.mvp.Messages;
/**
 * Pop up menu for paste and copy operation
 * @author User
 *
 */
public class PastePopUp extends JPopupMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5159981782095666910L;

	public PastePopUp(ActionListener pasteAction, ActionListener copyAction){
		JMenuItem anItem = new JMenuItem(Messages.getString("ForecastingDocumentWindow.tbParameters.excel.paste"));
		anItem.addActionListener(pasteAction);
		add(anItem);
		if(copyAction != null){
			JMenuItem cpItem = new JMenuItem(Messages.getString("ForecastingDocumentWindow.tbParameters.excel.copy"));
			cpItem.addActionListener(copyAction);
			add(cpItem);
		}
	}
}