package org.msh.quantb.services.io;
/**
 * This class represent buffer storage for the selected medicine
 * @author alexey
 *
 */
public class MedicineTmpStore extends AbstractUIAdapter {
	private MedicineUIAdapter medicine;
	
	public MedicineTmpStore(MedicineUIAdapter _medicine){
		medicine = _medicine;
	}

	/**
	 * @return the medicine
	 */
	public MedicineUIAdapter getMedicine() {
		return medicine;
	}

	/**
	 * @param medicine the medicine to set
	 */
	public void setMedicine(MedicineUIAdapter medicine) {
		MedicineUIAdapter old = getMedicine();
		this.medicine = medicine;
		firePropertyChange("medicine", old, getMedicine());
	}
	
	
}
