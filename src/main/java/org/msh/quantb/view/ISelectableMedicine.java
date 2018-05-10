package org.msh.quantb.view;

import org.msh.quantb.services.io.MedicineUIAdapter;

/**
 * This interface must implements all classes need to use MedicineSelectDlg<br>
 * For example, we are need to select medicine, when edit the Phase in the regimen, so regimen dialog must implement this
 * @author alexey
 *
 */
public interface ISelectableMedicine {
	/**
	 * provide way to get currently selected medicine
	 * @return
	 */
	MedicineUIAdapter getSelectedMedicine();
	/**
	 * provide way to set currently selected medicine
	 * @param selected
	 */
	void setSelectedMedicine(MedicineUIAdapter selected);
	
}
