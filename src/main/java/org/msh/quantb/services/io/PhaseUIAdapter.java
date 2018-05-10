package org.msh.quantb.services.io;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jdesktop.observablecollections.ObservableCollections;
import org.msh.quantb.model.gen.MedicineRegimen;
import org.msh.quantb.model.gen.Phase;
import org.msh.quantb.model.gen.PhaseDurationEnum;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;

/**
 * Cure phase object adapted for UI operations<br>
 * Remember! All medications for given phase must be rewritten from the medication attribute
 * @author alexey
 *
 */
public class PhaseUIAdapter extends AbstractUIAdapter implements Comparable<PhaseUIAdapter> {
	private Phase phase;
	private List<MedicationUIAdapter> medications;
	/**
	 * Only valid constructor
	 * @param _phase
	 */
	public PhaseUIAdapter(Phase _phase){
		this.phase = _phase;
		if (this.phase.getMeasure() == null){
			this.phase.setMeasure(PhaseDurationEnum.MONTHLY);
		}
	}
	/**
	 * @return the phase
	 */
	public Phase getPhase() {
		return phase;
	}
	/**
	 * Get duration in whole months
	 * Duration value may be in months or in weeks
	 * If duration is in weeks, then it will be recalculated in months, assumptions:
	 * <ul>
	 * <li> there are 4 weeks in month
	 * <li> the result will be round to the upper month value, i.e. 1-4 weeks is 1 month, 5-8 weeks is 2 months, etc
	 * </ul>
	 * @return 0 if no duration defined, duration in months otherwise
	 */
	public Integer getDurationInMonths() {	
		if(phase == null){
			return 0;
		}
		Integer div = 1;
		if (this.getMeasure().equals(PhaseDurationEnum.WEEKLY)){
			div = 4; // assume 4 weeks per any month
		}
		Integer dur = this.getDurationValue();
		Double dRet = Math.ceil(dur.doubleValue()/div.doubleValue());
		return dRet.intValue();
		
	}
	
	
	/**
	 * Since version 3, duration is value and measurement
	 * This method get only duration value, but, for backward compatibility scan medications for max duration, if native duration field is zero
	 * @return
	 */
	public Integer getDurationValue(){
		if (phase != null){
			if (phase.getDuration() == 0){ // try to scan medications
				int maxDur = 0;
				for(MedicineRegimen mr : phase.getMedications()){
					if (mr.getDuration() > maxDur){
						maxDur = mr.getDuration();
					}
				}
				phase.setDuration(maxDur);
			}
			return phase.getDuration();
		}else{
			return null;
		}
	}
	
	/**
	 * Since version 3, duration is value and measurement
	 * This method set only duration value
	 * @param duration
	 */
	public void setDurationValue(Integer duration){
		Integer oldValue = getDurationValue();
		Integer oldDur = getDurationInMonths();
		if(phase != null && duration != null){
			phase.setDuration(duration.intValue());
			for(MedicineRegimen mr : phase.getMedications()){ //for backward compatibility only
				mr.setDuration(duration);
			}
		}
		firePropertyChange("durationValue", oldValue, getDurationValue());
		firePropertyChange("duration", oldDur, getDurationInMonths());
	}



	/**
	 * The "old style" duration calculation - max duration from the all medications
	 * @deprecated
	 * @return
	 */
	private int calcDurationByMedications() {
		return (phase.getMedications()!=null && !phase.getMedications().isEmpty())?Collections.max(phase.getMedications(), new Comparator<MedicineRegimen>() {
			@Override
			public int compare(MedicineRegimen m1, MedicineRegimen m2) {
				if (m1==null && m2!=null){
					return -1;
				}else if (m2==null && m1!=null){
					return 1;
				}else if (m1!=null && m2!=null){
					return new Integer(m1.getDuration()).compareTo(new Integer(m2.getDuration()));
				}
				return 0;
			}
		}).getDuration():new Integer(0);
	}	
	/**
	 * @return
	 * @see org.msh.quantb.model.gen.Phase#getMedications()
	 */
	public List<MedicationUIAdapter> getMedications() {
		List<MedicationUIAdapter> ml = new ArrayList<MedicationUIAdapter>();
		if (phase != null){
			for(MedicineRegimen med : phase.getMedications()){
				MedicationUIAdapter mu = new MedicationUIAdapter(med);
				ml.add(mu);
			}
		}
		medications = ObservableCollections.observableList(ml);
		return medications;
	}

	/**
	 * Append medication to the list without rewriting
	 * @param medui
	 */
	public void appendMedication(MedicationUIAdapter medui) {
		if (medications != null){
			this.medications.add(medui);
			this.phase.getMedications().add(medui.getMedication());
		}

	}
	/**
	 * Get all medicines from the phase (acted as helper)
	 * @return
	 */
	public List<MedicineUIAdapter> getMedicines() {
		List<MedicineUIAdapter> ret = new ArrayList<MedicineUIAdapter>();
		for(MedicineRegimen m : getPhase().getMedications()){
			ret.add(new MedicineUIAdapter(m.getMedicine()));
		}
		return ret;
	}
	/**
	 * Delete the medication by index
	 * DOESN't rise a propertyChjange event !!!!
	 * @param selectedRow
	 */
	public void deleteMedication(int selectedRow) {
		this.getPhase().getMedications().remove(selectedRow);

	}

	/**
	 * For list of additional phases order is valuable
	 * Not used for the traditional intensive and continuation phases
	 * @param newOrder
	 */
	public void setOrder(Integer newOrder){
		Integer oldValue = this.getPhase().getOrder();
		this.getPhase().setOrder(newOrder);
		firePropertyChange("order", oldValue, getOrder());
	}

	/**
	 * Order number for the additional phases
	 * @return
	 */
	public Integer getOrder() {
		return this.getPhase().getOrder();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + phase.getDuration();
		result = prime * result + ((phase.getMeasure() == null) ? 0 : phase.getMeasure().hashCode());
		result = prime * result
				+ ((medications == null) ? 0 : medications.hashCode());
		result = prime * result + phase.getOrder();
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
		Phase other = ((PhaseUIAdapter)obj).getPhase();
		if (phase.getDuration() != other.getDuration()) {
			return false;
		}
		if (this.getMeasure() != other.getMeasure()) {
			return false;
		}
		if (phase.getMedications() == null) {
			if (other.getMedications() != null) {
				return false;
			}
		} else if (!phase.getMedications().equals(other.getMedications())) {
			return false;
		}
		if (phase.getOrder() != other.getOrder()) {
			return false;
		}
		return true;
	}
	/**
	 * Mainly by order
	 */
	@Override
	public int compareTo(PhaseUIAdapter o) {
		if (this.equals(o)) return 0;
		if (this.getOrder().equals(o.getOrder())) return 1;
		return this.getOrder().compareTo(o.getOrder());
	}
	/**
	 * Validate the phase completeness
	 * @return empty string, if OK, detail error message, if something wrong
	 */
	public String validate() {
		if (this.getDurationValue() == 0){
			return Messages.getString("Error.Validation.phase.duration");
		}
		String ret = "";
		if (this.getMedications().size() > 0){
			for(MedicationUIAdapter mUi : this.getMedications()){
				ret = mUi.validate();
				if (ret.length()>0){
					return ret;
				}
			}
		}else{
			ret = Messages.getString("Error.Validation.phase.medicine");
		}
		return ret;
	}

	/**
	 * Measurement of the duration
	 * @return
	 */
	public PhaseDurationEnum getMeasure(){
		PhaseDurationEnum tmp = this.getPhase().getMeasure();
		if (tmp == null){
			this.getPhase().setMeasure(PhaseDurationEnum.MONTHLY);
		}
		return this.getPhase().getMeasure();
	}
	/**
	 * Measure of the duration, by default - months
	 * @param measure
	 */
	public void setMeasure(PhaseDurationEnum measure){
		PhaseDurationEnum oldValue = getMeasure();
		this.getPhase().setMeasure(measure);
		firePropertyChange("measure", oldValue, getMeasure());
	}
	
	/**
	 * Get the phase period
	 * @return
	 */
	public RegimenPeriod getPeriod() {
		RegimenPeriod ret = new RegimenPeriod();
		if (phase.getMeasure().equals(PhaseDurationEnum.MONTHLY)){
			ret.setMonths(getDurationValue());
			ret.setWeeks(0);
		}else{
			ret.setWeeks(getDurationValue());
			ret.setMonths(0);
		}
		return ret;
	}
	/**
	 * Make clone of this
	 * @return
	 */
	public PhaseUIAdapter makeClone() {
		// create a clone
		Phase phaseObj = Presenter.getFactory().createPhase();
		phaseObj.setDuration(this.getDurationValue());
		phaseObj.setMeasure(this.getMeasure());
		phaseObj.setOrder(this.getOrder());
		//add medication's clones
		for(MedicationUIAdapter mediUi: getMedications()){
			phaseObj.getMedications().add(mediUi.createClone().getMedication());
		}
		
		return new PhaseUIAdapter(phaseObj);
	}


}
