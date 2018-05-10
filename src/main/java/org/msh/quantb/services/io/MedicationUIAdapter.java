package org.msh.quantb.services.io;

import org.msh.quantb.model.gen.Medicine;
import org.msh.quantb.model.gen.MedicineRegimen;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;


/**
 * Medication in phase object adapted to UI operations
 * @author alexey
 *
 */
public class MedicationUIAdapter extends AbstractUIAdapter implements Comparable<MedicationUIAdapter> {
	private MedicineRegimen medication;
	/**
	 * only valid constructor
	 * @param _medication
	 */
	public MedicationUIAdapter(MedicineRegimen _medication){
		this.medication = _medication;
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.gen.MedicationRegimen#getMedicine()
	 */
	public MedicineUIAdapter getMedicine() {
		return new MedicineUIAdapter(medication.getMedicine());
	}
	
	
	
	/**
	 * @param value
	 * @see org.msh.quantb.model.gen.MedicationRegimen#setMedicine(org.msh.quantb.model.gen.Medicine)
	 */
	public void setMedicine(MedicineUIAdapter value) {
		MedicineUIAdapter old = getMedicine();
		medication.setMedicine(value.getMedicine());
		firePropertyChange("medicine", old, getMedicine());
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.gen.MedicationRegimen#getDosage()
	 */
	public Integer getDosage() {
		return medication.getDosage();
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.gen.MedicationRegimen#setDosage(int)
	 */
	public void setDosage(Integer value) {
		Integer old = getDosage();
		medication.setDosage(value);
		firePropertyChange("dosage", old, getDosage());
	}
	/**
	 * get days per week. Used only for calculation!!!
	 * @return
	 * @see org.msh.quantb.model.gen.MedicationRegimen#getDaysPerWeek()
	 */
	public Integer getDaysPerWeek() {
		return medication.getDaysPerWeek();
	}
	/**
	 * set days per week. Used only for calculation!!!
	 * @param value
	 * @see org.msh.quantb.model.gen.MedicationRegimen#setDaysPerWeek(int)
	 */
	public void setDaysPerWeek(Integer value) {
		Integer old = getDaysPerWeek();
		medication.setDaysPerWeek(value);
		firePropertyChange("daysPerWeek", old, getDaysPerWeek());
	}
	
	/**
	 * @return
	 * @see org.msh.quantb.model.gen.MedicationRegimen#getDurationInMonths()
	 */
	public Integer getDuration() {
		return medication.getDuration();
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.gen.MedicationRegimen#setDuration(int)
	 */
	public void setDuration(Integer value) {
		Integer old = getDuration();
		medication.setDuration(value);
		firePropertyChange("duration", old, getDuration());
	}
	/**
	 * return the real medication
	 * @return
	 */
	public MedicineRegimen getMedication() {
		return this.medication;
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.gen.MedicineRegimen#getStartWeek()
	 */
	public Integer getStartWeek() {
		return medication.getStartWeek();
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.gen.MedicineRegimen#setStartWeek(int)
	 */
	public void setStartWeek(Integer value) {
		Integer oldValue = getStartWeek();
		medication.setStartWeek(value);
		firePropertyChange("startWeek", oldValue, getStartWeek());
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.gen.MedicineRegimen#getEndWeek()
	 */
	public Integer getEndWeek() {
		return medication.getEndWeek();
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.gen.MedicineRegimen#setEndWeek(int)
	 */
	public void setEndWeek(Integer value) {
		Integer oldValue = getEndWeek();
		medication.setEndWeek(value);
		firePropertyChange("endWeek", oldValue, getEndWeek());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + medication.getDaysPerWeek();
		result = prime * result + medication.getDosage();
		result = prime * result + medication.getDuration();
		result = prime * result + medication.getEndWeek();
		result = prime * result
				+ ((getMedicine() == null) ? 0 : getMedicine().hashCode());
		result = prime * result + medication.getStartWeek();
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MedicationUIAdapter other = (MedicationUIAdapter) obj;
		if (getDaysPerWeek() != other.getDaysPerWeek()) {
			return false;
		}
		if (getDosage() != other.getDosage()) {
			return false;
		}
		if (getDuration() != other.getDuration()) {
			return false;
		}
		if (getEndWeek() != other.getEndWeek()) {
			return false;
		}
		if (getMedicine() == null) {
			if (other.getMedicine() != null) {
				return false;
			}
		} else if (!getMedicine().equals(other.getMedicine())) {
			return false;
		}
		if (getStartWeek() != other.getStartWeek()) {
			return false;
		}
		return true;
	}
	
	
	/**
	 * By start week
	 * @param another
	 * @return
	 */
	@Override
	public int compareTo(MedicationUIAdapter another) {
		if (this.getStartWeek() == null) return -1;
		if (another.getStartWeek() == null) return 1;
		if (this.getStartWeek() > another.getStartWeek()) return 1;
		if(this.getStartWeek() < another.getStartWeek()) return -1;
		int eqMed = this.getMedicine().compareTo(another.getMedicine());
		if (eqMed != 0) return eqMed;
		if (this.equals(another)) return 0;
		return 1;
	}
	/**
	 * validate the medication
	 * @return empty string OK, error message - wrong
	 */
	public String validate() {
		if (this.getMedication().getMedicine() == null){
			return this.getMedicine().getNameForDisplayWithAbbrev() + " "  +Messages.getString("Error.Validation.phase.medicine");
		}
		if(this.getDaysPerWeek().intValue() <= 0){
			return this.getMedicine().getNameForDisplayWithAbbrev() + " "  +Messages.getString("Error.Validation.phase.daysperweek"); //TODO name of medicine!!!
		}
		if(this.getDosage().intValue()<= 0){
			return this.getMedicine().getNameForDisplayWithAbbrev() + " "  +Messages.getString("Error.Validation.phase.dosage");
		}
		//TODO weekly if needed
		return "";
	}
	/**
	 * Create a clone of this medication
	 * @return
	 */
	public MedicationUIAdapter createClone() {
		MedicineUIAdapter mUi = this.getMedicine().createClone(Presenter.getFactory());
		MedicineRegimen mR = Presenter.getFactory().createMedication(mUi.getMedicine(), this.getDuration(),
				this.getDosage(), this.getDaysPerWeek());
		return new MedicationUIAdapter(mR);
		

	}
	
}
