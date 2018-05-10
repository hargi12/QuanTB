package org.msh.quantb.view.dialog;

import org.msh.quantb.services.io.MedicineUIAdapter;

/**
 * For single drug regimen all phases should have a same medicine
 * @author Alexey Kurasov
 *
 */
public interface ISingleMedSelection {
	/**
	 * Set medicine for all phases
	 * @param med
	 */
	void adjustPhases(MedicineUIAdapter med);
}
