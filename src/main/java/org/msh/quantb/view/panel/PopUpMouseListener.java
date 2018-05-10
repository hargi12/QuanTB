package org.msh.quantb.view.panel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

/**
 * Show pop up on right click
 * @author User
 *
 */
public class PopUpMouseListener extends MouseAdapter {
	
	private JPopupMenu menu;

	public PopUpMouseListener(JPopupMenu _menu){
		this.menu = _menu;
		
	}
	 public void mousePressed(MouseEvent e) {
	        maybeShowPopup(e);
	    }

	    public void mouseReleased(MouseEvent e) {
	        maybeShowPopup(e);
	    }

	    private void maybeShowPopup(MouseEvent e) {
	        if (e.isPopupTrigger()) {
	           menu.show(e.getComponent(),
	                       e.getX(), e.getY());
	        }
	    }

}
