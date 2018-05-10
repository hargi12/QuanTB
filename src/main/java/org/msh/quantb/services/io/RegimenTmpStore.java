package org.msh.quantb.services.io;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jdesktop.observablecollections.ObservableCollections;

/**
 * This class represent buffer storage for the selected regimen
 * @author alexey
 *
 */
public class RegimenTmpStore extends AbstractUIAdapter {
	private RegimenUIAdapter regimen;
	private List<DisplayMedication> displayMedications;

	public RegimenTmpStore(RegimenUIAdapter _regimen){
		regimen = _regimen;

	}

	/**
	 * @return the regimen
	 */
	public RegimenUIAdapter getRegimen() {
		return regimen;
	}

	/**
	 * @param regimen the regimen to set
	 */
	public void setRegimen(RegimenUIAdapter regimen) {
		RegimenUIAdapter old = getRegimen();
		List<DisplayMedication> oldMed = null;
		if (old != null){
			oldMed = old.getDisplayMedications();
		}
		this.regimen = regimen;
		firePropertyChange("regimen", old, getRegimen());
		if (this.regimen != null){
			this.displayMedications = this.regimen.getDisplayMedications();
		}else{
			this.displayMedications = ObservableCollections.observableList(new ArrayList<DisplayMedication>());
		}

		firePropertyChange("displayMedications", oldMed, getDisplayMedications());
	}

	/**
	 * @return the displayMedications
	 */
	public List<DisplayMedication> getDisplayMedications() {
		return displayMedications;
	}

	/**
	 * Get the sorted list of the intensive phase medications
	 * set for each medication duration change listener
	 * @return
	 */
	public List<MedicationUIAdapter> getIntensiveMedications(){
		List<MedicationUIAdapter> medUIList = getRegimen().getIntensive().getMedications();
		sortMedicationsInPhase(medUIList);
		addDurationListener(medUIList);
		return medUIList;
	}
	/**
	 * Add duration change listener for all medications
	 * @param medUIList
	 */
	private void addDurationListener(List<MedicationUIAdapter> medUIList) {
		for(MedicationUIAdapter medUI : medUIList){
			medUI.removeAllPropertyChangeListeners("duration");
			medUI.addPropertyChangeListener("duration", new PropertyChangeListener() {
				
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					getRegimen().setConsumption(getRegimen().calcComposition());
					
				}
			});

		}
	}

	/**
	 * Get the sorted list of the continuous phase medications
	 * @return
	 */
	public List<MedicationUIAdapter> getContiMedications(){
		List<MedicationUIAdapter> medUIList = getRegimen().getContinious().getMedications();
		sortMedicationsInPhase(medUIList);
		addDurationListener(medUIList);
		return medUIList;
	}

	/**
	 * Sort the medication list
	 * @param medUIList
	 */
	private void sortMedicationsInPhase(List<MedicationUIAdapter> medUIList) {
		Collections.sort(medUIList, new Comparator<MedicationUIAdapter>(){

			@Override
			public int compare(MedicationUIAdapter o1, MedicationUIAdapter o2) {
				return o1.getMedicine().compareTo(o2.getMedicine());
			}

		});
	}


}
