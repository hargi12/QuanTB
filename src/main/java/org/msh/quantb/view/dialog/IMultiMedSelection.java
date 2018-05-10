package org.msh.quantb.view.dialog;

import java.util.List;

import org.msh.quantb.services.io.MedicineUIAdapter;

/**
 * This interface must be implemented by any class used MedicineSelectWeekDialog
 * @author alexey
 *
 */
public interface IMultiMedSelection {
	/**
	 * Add selected medicines
	 * @param selected
	 */
	void addSelected(List<MedicineUIAdapter> selected);

}
