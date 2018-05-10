package org.msh.quantb.services.io;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;

import org.jdesktop.observablecollections.ObservableCollections;
import org.msh.quantb.model.gen.MedicineRegimen;
import org.msh.quantb.model.gen.Phase;
import org.msh.quantb.model.gen.PhaseDurationEnum;
import org.msh.quantb.model.gen.Regimen;
import org.msh.quantb.model.gen.RegimenTypesEnum;
import org.msh.quantb.services.calc.DateUtils;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;

/**
 * Regimen object adapted for UI operations
 * @author alexey
 *
 */
public class RegimenUIAdapter extends AbstractUIAdapter implements Comparable<RegimenUIAdapter> {
	private Regimen regimen;
	private List<DisplayMedication> displayMedications;

	public RegimenUIAdapter(Regimen _regimen){
		this.regimen = _regimen;
	}

	@Override
	public String toString() {
		return "RegimenUIAdapter [getConsumption()=" + getConsumption() + "]";
	}

	/**
	 * @return the regimen
	 */
	public Regimen getRegimen() {
		return regimen;
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.gen.Regimen#getName()
	 */
	public String getName() {
		return regimen.getName();
	}

	/**
	 * Return name of regimen and composition
	 * @return
	 */
	public String getNameWithForDisplay(){
		return getName() + "  " + getConsumption();
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.gen.Regimen#getConsumption()
	 */
	public String getConsumption() {
		return regimen.getFormulation();
	}

	/**
	 * @param formula
	 * @see org.msh.quantb.model.gen.Regimen#setConsumption(java.lang.String)
	 */
	public void setConsumption(String formula) {
		String old = getConsumption();
		regimen.setFormulation(formula);
		firePropertyChange("consumption", old, getConsumption());
	}

	/**
	 * @param name
	 * @see org.msh.quantb.model.gen.Regimen#setName(java.lang.String)
	 */
	public void setName(String name) {
		String old = getName();
		regimen.setName(name);
		firePropertyChange("name", old, getName());

	}

	/**
	 * @return
	 * @see org.msh.quantb.model.gen.Regimen#getIntensive()
	 */
	public PhaseUIAdapter getIntensive() {
		Phase p = regimen.getIntensive();
		return new PhaseUIAdapter(p);
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.gen.Regimen#setIntensive(org.msh.quantb.model.gen.Phase)
	 */
	public void setIntensive(PhaseUIAdapter value) {
		PhaseUIAdapter old = getIntensive();
		regimen.setIntensive(value.getPhase());
		firePropertyChange("intensive",old, getIntensive());
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.gen.Regimen#getContinious()
	 */
	public PhaseUIAdapter getContinious() {
		Phase p = regimen.getContinious();
		p.setOrder(2);
		return new PhaseUIAdapter(p);
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.gen.Regimen#setContinious(org.msh.quantb.model.gen.Phase)
	 */
	public void setContinious(PhaseUIAdapter value) {
		PhaseUIAdapter old = getIntensive();
		regimen.setContinious(value.getPhase());
		firePropertyChange("continious",old, getContinious());
	}


	/**
	 * @return the displayMedications
	 */
	public List<DisplayMedication> getDisplayMedications() {
		//prepare list by quasi- relation operation between intensive and continuous phases
		Set<DisplayMedication> tmp = new TreeSet<DisplayMedication>();
		//determine real phases quantity
		int phases = 0;
		if (getIntensive().getPhase().getMedications().size()>0){
			phases++;
		}
		if (getContinious().getPhase().getMedications().size()>0){
			phases++;
		}
		for(Phase ph : this.getRegimen().getAddPhases()){
			if (ph.getMedications().size()>0){
				phases++;
			}
		}
		//
		// Intensive
		for(MedicationUIAdapter m : getIntensive().getMedications()){
			m.setDuration(getIntensive().getDurationValue());
			addDisplatMedication(tmp, m, 1, phases, getIntensive().getMeasure()==PhaseDurationEnum.MONTHLY);
		}
		//Continuous
		for(MedicationUIAdapter m : getContinious().getMedications()){
			m.setDuration(getContinious().getDurationValue());
			addDisplatMedication(tmp, m,2, phases, getContinious().getMeasure()==PhaseDurationEnum.MONTHLY);
		}
		//Additional
		int phase = 3;
		for(PhaseUIAdapter pUi : getAddPhases()){
			for(MedicationUIAdapter m : pUi.getMedications()){
				m.setDuration(pUi.getDurationValue());
				addDisplatMedication(tmp, m,phase, phases, pUi.getMeasure()==PhaseDurationEnum.MONTHLY);
			}
			phase++;
		}
		List<DisplayMedication> tmpl = new ArrayList<DisplayMedication>(tmp);
		Collections.sort(tmpl);
		this.displayMedications = ObservableCollections.observableList(tmpl);
		return displayMedications;
	}
	
	/**
	 * Add medication information to the list for display in the Regimen List dialog
	 * @param medSet - Display medication  set to build
	 * @param medication a medication data
	 * @param phase phase number, begins from 1
	 * @param phases real phases quantity
	 * @param isMonth medication duration is in months, false - in weeks
	 */
	private void addDisplatMedication(Set<DisplayMedication> medSet,
			MedicationUIAdapter medication, int phase, int phases, boolean isMonth) {
		DisplayMedication dm = new DisplayMedication(medication.getMedicine(), phases);
		if (!medSet.add(dm)){
			for(DisplayMedication dmE : medSet){
				if (dmE.getMedicineName().equals(dm.getMedicineName())){
					setDisplayParameters(dmE, medication, phase, isMonth);
				}
			}
		}else{
			setDisplayParameters(dm, medication, phase, isMonth);
		}
	}

	/**
	 * set parameters for DisplayMedication 
	 * @param dmE DisplayMedication object
	 * @param medication medication record
	 * @param phaseNo phase number
	 * @param isMonth is duration in months
	 */
	private void setDisplayParameters(DisplayMedication dmE,
			MedicationUIAdapter medication, int phaseNo, boolean isMonth) {
		dmE.setDose(medication.getDosage(), phaseNo);
		dmE.setDuration(medication.getDuration(), isMonth, phaseNo);
		dmE.setFrequency(medication.getDaysPerWeek(), phaseNo);
		
	}

	@Override
	public int hashCode(){
		return getNameWithForDisplay().hashCode();
	}
	
	/**
	 * Count regiment equal if equals names and all medications
	 */
	@Override
	public boolean equals(Object _another){
		if (super.equals(_another)){
			RegimenUIAdapter another = (RegimenUIAdapter) _another;
			if(another == null) return false;
			return this.getName().equals(another.getName()) &&
					this.getCompositions().equals(another.getCompositions());
			/*			if (another.getConsumption() == null && this.getConsumption() == null){
				return this.getName().equals(another.getName());
			}else{
				if (this.getConsumption() == null || another.getConsumption() == null){
					return false;
				}else{
					return this.getName().equals(another.getName()) &&
							this.getConsumption().equals(another.getConsumption());
				}
			}*/
		}else return false;
	}

	/**
	 * Get concatenated medications as compositions for compare purpose
	 * @return
	 */
	public String getCompositions() {
		return this.getDisplayMedications().toString();
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.gen.Regimen#getType()
	 */
	public RegimenTypesEnum getType() {
		return regimen.getType();
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.gen.Regimen#setType(org.msh.quantb.model.gen.RegimenTypesEnum)
	 */
	public void setType(RegimenTypesEnum value) {
		RegimenTypesEnum oldValue = getType();
		regimen.setType(value);
		firePropertyChange("type", oldValue, getType());
	}

	/**
	 * Is it single drug regimen?
	 * @return
	 */
	public boolean isSingleDrug(){
		return getType().equals(RegimenTypesEnum.SINGLE_DRUG);
	}

	/**
	 * Regimen duration in weeks
	 * @return
	 * @see org.msh.quantb.model.gen.Regimen#getDuration()
	 */
	public Integer getDuration() {
		return regimen.getDuration();
	}

	/**
	 * Regimen duration in weeks
	 * @param value
	 * @see org.msh.quantb.model.gen.Regimen#setDuration(int)
	 */
	public void setDuration(Integer value) {
		Integer oldValue = getDuration();
		regimen.setDuration(value);
		firePropertyChange("duration", oldValue, getDuration());
	}

	/**
	 * Set duration from the text field
	 * @param _value
	 */
	public void setDurationText(String _value){
		if (_value.length() == 0){
			_value = "0";
		}
		Integer value=0;
		try {
			value = new Integer(_value);
		} catch (NumberFormatException e) {
			setDuration(value);
		}
		setDuration(value);
	}

	public String getDurationText(){
		return getDuration().toString();
	}


	/**
	 * Calculate the regimen composition in accordance with WHO rules<br>
	 * First is Intensive Phase, then is Continuous Phase
	 * @return regimen composition string based on WHO rules or an empty string
	 */
	public String calcComposition(){
		String ret = "";
		String tmp = "";
		PhaseUIAdapter phase = this.getIntensive();
		tmp = calcPhaseComposition(phase);
		if (tmp.length()>0){
			ret = tmp;
		}
		if (this.getType().equals(RegimenTypesEnum.MULTI_DRUG)){
			phase = this.getContinious();
			tmp = calcPhaseComposition(phase);
			if(tmp.length()>0){
				ret = ret +"/"+ tmp;
			}
			for(PhaseUIAdapter phUi : this.getAddPhases()){
				tmp = calcPhaseComposition(phUi);
				if(tmp.length()>0){
					ret = ret +"/"+ tmp;
				}
			}
		}
		return ret;
	}

	/**
	 * Calculate composition for the phase given
	 * @param phase
	 * @return composition or empty string
	 */
	private String calcPhaseComposition(PhaseUIAdapter phase) {
		String ret = phase.getDurationInMonths().toString();
		if (phase != null){
			if (phase.getMedications().size() > 0){
				List<MedicationUIAdapter> pMed = phase.getMedications();
				Collections.sort(pMed, new Comparator<MedicationUIAdapter>(){
					@Override
					public int compare(MedicationUIAdapter o1,
							MedicationUIAdapter o2) {
						return o1.getMedicine().compareTo(o2.getMedicine());
					}
				});
				for (MedicationUIAdapter medUI : pMed){
					ret = ret+medUI.getMedicine().getOnlyAbbrevName();
				}
			}
		}
		return ret;
	}
	
	/**
	 * Get list of the additional regimen phases Sorted by order nums
	 * @return empty list if no add phases or list of the additional phases
	 */
	public List<PhaseUIAdapter> getAddPhases() {
		List<PhaseUIAdapter> tmp = new ArrayList<PhaseUIAdapter>();
		for(Phase ph : this.getRegimen().getAddPhases()){
			PhaseUIAdapter phUI = new PhaseUIAdapter(ph);
			tmp.add(phUI);
		}
		Collections.sort(tmp);
		return ObservableCollections.observableList(tmp);
	}

	/**
	 * Add new phase to the additional phases list
	 * @return new created phase
	 */
	public PhaseUIAdapter addPhase() {
		int maxOrder = this.getRegimen().getAddPhases().size()+3;
		Phase phase = Presenter.getFactory().createPhase();
		phase.setOrder(maxOrder);
		this.getRegimen().getAddPhases().add(phase);
		return new PhaseUIAdapter(phase);
	}

	/**
	 * Check phases completeness
	 * @return the wrong phase number (from 0), or null if all phases OK
	 */
	public Integer checkPhases() {
		if (getIntensive().validate().length() > 0){
			return 0;
		}
		if (getContinious().validate().length()>0){
			return 1;
		}
		int i = 2;
		for(PhaseUIAdapter pUi : this.getAddPhases()){
			if (pUi.validate().length() > 0){
				return i;
			}
			i++;
		}
		return null;
	}
	/**
	 * Remove one phases and shift rests
	 * @param order number of phase to remove, begins from 1
	 */
	public void shiftPhases(Integer order) {
		List<Phase> phases = new ArrayList<Phase>();
		//build and remove
		if (this.getRegimen().getIntensive() != null){
			phases.add(this.getRegimen().getIntensive());
		}
		if (this.getRegimen().getContinious() != null){
			phases.add(this.getRegimen().getContinious());
		}
		phases.addAll(this.getRegimen().getAddPhases());
		if (order.intValue() >= 1 && order.intValue() <= phases.size()){
			phases.remove(order.intValue()-1);
		}
		// shift order
		int i = 1;
		for(Phase ph : phases){
			ph.setOrder(i++);
		}

		// restore intensive and continuous
		if (phases.size() >=1){
			this.getRegimen().setIntensive(phases.get(0));
			phases.remove(0);
		}else{
			Phase inten = Presenter.getFactory().createPhase();
			inten.setOrder(1);
			this.getRegimen().setIntensive(inten);
		}
		if (phases.size()>=1){
			this.getRegimen().setContinious(phases.get(0));
			phases.remove(0);
		}else{
			Phase conti = Presenter.getFactory().createPhase();
			conti.setOrder(2);
			this.getRegimen().setContinious(conti);
		}
		this.getRegimen().getAddPhases().clear();
		this.getRegimen().getAddPhases().addAll(phases);
	}
	/**
	 * Get real phases quantity. Phase is real, when it has at least one medication
	 * @return
	 */
	public int getPhasesQuantity() {
		int ret = 0;
		Phase ph = this.getRegimen().getIntensive();
		ret = calcMedications(ret, ph);
		ph = this.getRegimen().getContinious();
		ret = calcMedications(ret, ph);
		for(Phase phA : this.getRegimen().getAddPhases()){
			ret = calcMedications(ret, phA);
		}
		//System.out.println(ret);
		return ret;
	}

	private int calcMedications(int ret, Phase ph) {
		if (ph.getMedications().size() > 0){
			ret++;
		}
		return ret;
	}
	/**
	 * Get begin date of the regimen based on the end date
	 * @param lastDate last date of the regimen
	 * @return
	 */
	public Calendar getBeginDate(Calendar lastDate) {
		Calendar ret = GregorianCalendar.getInstance();
		DateUtils.cleanTime(ret);
		DateUtils.cleanTime(lastDate);
		ret.setTime(lastDate.getTime());
		List<PhaseUIAdapter> others = this.getAddPhases();
		ListIterator<PhaseUIAdapter> li = others.listIterator(others.size());
		while(li.hasPrevious()){
			PhaseUIAdapter phUi = li.previous();
			ret = phUi.getPeriod().calcReverse(ret);
			ret.add(Calendar.DAY_OF_MONTH, -1); // previous regimen will be end at the previous day
			//System.out.println(DateUtils.formatDate(ret.getTime(), "dd-MM-yyyy"));
		}
		PhaseUIAdapter conti = this.getContinious();
		ret = conti.getPeriod().calcReverse(ret);
		ret.add(Calendar.DAY_OF_MONTH, -1); // previous regimen will be end at the previous day
		//System.out.println(DateUtils.formatDate(ret.getTime(), "dd-MM-yyyy"));
		PhaseUIAdapter inte = this.getIntensive();
		ret = inte.getPeriod().calcReverse(ret);
		return ret;
	}
	/**
	 * Get end date of the regimen based on begin date
	 * @param beg begin date
	 * @return
	 */
	public Calendar getEndDate(Calendar beg) {
		Calendar ret = GregorianCalendar.getInstance();
		DateUtils.cleanTime(ret);
		DateUtils.cleanTime(beg);
		ret.setTime(beg.getTime());
		PhaseUIAdapter inte = this.getIntensive();
		ret = inte.getPeriod().calcDirect(ret);
		ret.add(Calendar.DAY_OF_MONTH, 1);
		PhaseUIAdapter conti = this.getContinious();
		ret = conti.getPeriod().calcDirect(ret);
		ret.add(Calendar.DAY_OF_MONTH, 1);
		for(PhaseUIAdapter phUi : this.getAddPhases()){
			ret = phUi.getPeriod().calcDirect(ret);
			ret.add(Calendar.DAY_OF_MONTH, 1);
		}
		ret.add(Calendar.DAY_OF_MONTH, -1); //correction for real begin
		return ret;
	}
	/**
	 * Make a clone of the current regimen
	 * @return
	 */
	public RegimenUIAdapter makeClone() {
		//create new regimen
		Regimen regObj = Presenter.getFactory().createRegimen(this.getName()+" - " + Messages.getString("Regimen.copy"),
				this.getConsumption(), this.getType());
		//clone phases
		regObj.setIntensive(this.getIntensive().makeClone().getPhase());
		regObj.setContinious(this.getContinious().makeClone().getPhase());
		for(PhaseUIAdapter addPh : this.getAddPhases()){
			regObj.getAddPhases().add(addPh.makeClone().getPhase());
		}
		return new RegimenUIAdapter(regObj);
	}

	@Override
	public int compareTo(RegimenUIAdapter o) {
		if(o==null){
			return 1;
		}
		if(this.equals(o)){
			return 0;
		}
		return getNameWithForDisplay().compareTo(o.getNameWithForDisplay());
	}


}
