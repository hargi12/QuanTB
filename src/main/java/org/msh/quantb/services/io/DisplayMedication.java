package org.msh.quantb.services.io;

import java.util.ArrayList;
import java.util.List;

import org.msh.quantb.services.mvp.Messages;

/**
 * Special synthetic class to display medication table in regimen view dialog<br>
 * Comparable an so on
 * @author alexey
 *
 */
public class DisplayMedication implements Comparable<DisplayMedication> {
	private List<Integer> dose = new ArrayList<Integer>();
	private List<Integer> duration = new ArrayList<Integer>();
	private List<Boolean> isMonths = new ArrayList<Boolean>(); // true - months, false - weeks
	private List<Integer> frequency = new ArrayList<Integer>();
	private MedicineUIAdapter medicine = null;	

	/**
	 * Create an object for defined medicine
	 * @param mUi medication
	 * @param phases total phases quantity
	 */
	public DisplayMedication(MedicineUIAdapter mUi, int phases){
		super();
		initLists(phases);
		this.medicine = mUi;
	}
	/**
	 * Init all lists
	 * @param phases
	 */
	private void initLists(int phases) {
		for(int i=0; i<phases; i++){
			dose.add(0);
			duration.add(0);
			isMonths.add(true);
			frequency.add(0);
		}
		
	}

	/**
	 * @return the medicineName
	 */
	public String getMedicineName() {
		return this.medicine.getNameForDisplay();
	}

	/**
	 * @return the medicine for display
	 */
	public String getMedicineForDisplay(){
		return this.medicine.getNameForDisplayWithAbbrev();
	}

	/**
	 * Return duration of the phase i or null if phaseNo is wrong
	 * @param phaseNo phase no begins from 1
	 * @return
	 */
	private Integer getDuration(int phaseNo) {
		if (duration.size()>= phaseNo && phaseNo>=1){
			return duration.get(phaseNo-1);
		}else{
			return null;
		}
	}
	/**
	 * set the duration
	 * @param dur duration value
	 * @param _isMonth duration measure
	 * @param phaseNo phase no, begins from 1
	 */
	public void setDuration(Integer dur, Boolean _isMonth, int phaseNo) {
		if (duration.size()>= phaseNo && phaseNo>=1){
			duration.set(phaseNo-1, dur);
			isMonths.set(phaseNo-1, _isMonth);
		}

	}


	/**
	 * Return dose of the phase phaseNo or null if phaseNo is wrong
	 * @param phaseNo phase no begins from 1
	 * @return
	 */
	private Integer getDose(int phaseNo) {
		if (dose.size()>= phaseNo && phaseNo>=1){
			return dose.get(phaseNo-1);
		}else{
			return null;
		}
	}
	/**
	 * set the daily dose
	 * @param dos daily consumption value
	 * @param phaseNo phase no, begins from 1
	 */
	public void setDose(Integer dos, int phaseNo) {
		if (dose.size()>= phaseNo && phaseNo>=1){
			dose.set(phaseNo-1, dos);
		}

	}

	/**
	 * Return weekly frequency of the phase phaseNo or null if phaseNo is wrong
	 * @param phaseNo phase no begins from 1
	 * @return
	 */
	private Integer getFrequency(int phaseNo) {
		if (frequency.size()>= phaseNo && phaseNo>=1){
			return frequency.get(phaseNo-1);
		}else{
			return null;
		}
	}
	/**
	 * set weekly frequency of the phase phaseNo
	 * @param fre weekly frequency value
	 * @param phaseNo phase no, begins from 1
	 */
	public void setFrequency(Integer fre, int phaseNo) {
		if (frequency.size()>= phaseNo && phaseNo>=1){
			frequency.set(phaseNo-1, fre);
		}

	}
	/**
	 * Main method - get display string for this medicine
	 * @return
	 */
	public String getDisplayStr(){
		String ret = "";
		for(int i=1; i<=dose.size(); i++){
			ret = ret + Messages.getString("Regimen.phase")+ i;
			if (this.getDose(i)>0){
				ret = ret + " "
					+ getElement(i) + "<br>";
			}else{
				ret = ret + " - <br>";
			}
		}
		
		return "<html>" + ret.trim() + "</html>";
	}
	
	/**
	 * Get an element Dose - Frequency - Duration Measure 
	 * @param phaseNo
	 * @return
	 */
	private String getElement(int phaseNo){
		String ret = "-";
		if (this.getDose(phaseNo) != null && this.getDose(phaseNo) > 0){
		ret = this.getDose(phaseNo) + " - " + this.getFrequency(phaseNo) + " - "
				+ this.getDuration(phaseNo) +" " + this.getMeasure(phaseNo);
		}
		return ret;
	}
	
	/**
	 * get measure - week or month
	 * @param phaseNo phase no
	 * @return
	 */
	private String getMeasure(int phaseNo) {
		if (isMonths.size()>= phaseNo && phaseNo>=1){
			String ret = Messages.getString("Regimen.phase.durtypes.MONTHLY");
			if (!isMonths.get(phaseNo-1)){
				ret = Messages.getString("Regimen.phase.durtypes.WEEKLY");
			}
			return ret;
		}else{
			return null;
		}
	}
	@Override
	public int compareTo(DisplayMedication o) {
		if (o == null) return -1;
		if (this.medicine != null && o.getMedicine() != null){
			return this.getMedicine().compareTo(o.getMedicine());
		}
		return this.getMedicineForDisplay().compareTo(o.getMedicineForDisplay());
	}
	
	
	private MedicationUIAdapter getMedicine() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Get info for phase 1
	 * @return
	 */
	public String getPhase1(){
		String ret = getElement(1);
		if (ret == null){
			ret = "-";
		}
		return ret;
	}
	/**
	 * Get info for phase 2
	 * @return
	 */
	public String getPhase2(){
		String ret = getElement(2);
		if (ret == null){
			ret = "-";
		}
		return ret;
	}
	/**
	 * Get info for phase 3
	 * @return
	 */
	public String getPhase3(){
		String ret = getElement(3);
		if (ret == null){
			ret = "-";
		}
		return ret;
	}
	
	/**
	 * Get info for phase 4
	 * @return
	 */
	public String getPhase4(){
		String ret = getElement(4);
		if (ret == null){
			ret = "-";
		}
		return ret;
	}
	/**
	 * Get info for phase 5
	 * @return
	 */
	public String getPhase5(){
		String ret = getElement(5);
		if (ret == null){
			ret = "-";
		}
		return ret;
	}
	
	/**
	 * Get info for phase 6
	 * @return
	 */
	public String getPhase6(){
		String ret = getElement(6);
		if (ret == null){
			ret = "-";
		}
		return ret;
	}
	
	/**
	 * Get info for phase 7
	 * @return
	 */
	public String getPhase7(){
		String ret = getElement(7);
		if (ret == null){
			ret = "-";
		}
		return ret;
	}
	
	/**
	 * Get info for phase 5
	 * @return
	 */
	public String getPhase8(){
		String ret = getElement(8);
		if (ret == null){
			ret = "-";
		}
		return ret;
	}
	/**
	 * Get info for phase 9
	 * @return
	 */
	public String getPhase9(){
		String ret = getElement(9);
		if (ret == null){
			ret = "-";
		}
		return ret;
	}
	
	/**
	 * Get info for phase 10
	 * @return
	 */
	public String getPhase10(){
		String ret = getElement(10);
		if (ret == null){
			ret = "-";
		}
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dose == null) ? 0 : dose.hashCode());
		result = prime * result
				+ ((duration == null) ? 0 : duration.hashCode());
		result = prime * result
				+ ((frequency == null) ? 0 : frequency.hashCode());
		result = prime * result
				+ ((isMonths == null) ? 0 : isMonths.hashCode());
		result = prime * result
				+ ((medicine == null) ? 0 : medicine.hashCode());
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
		DisplayMedication other = (DisplayMedication) obj;
		if (dose == null) {
			if (other.dose != null) {
				return false;
			}
		} else if (!dose.equals(other.dose)) {
			return false;
		}
		if (duration == null) {
			if (other.duration != null) {
				return false;
			}
		} else if (!duration.equals(other.duration)) {
			return false;
		}
		if (frequency == null) {
			if (other.frequency != null) {
				return false;
			}
		} else if (!frequency.equals(other.frequency)) {
			return false;
		}
		if (isMonths == null) {
			if (other.isMonths != null) {
				return false;
			}
		} else if (!isMonths.equals(other.isMonths)) {
			return false;
		}
		if (medicine == null) {
			if (other.medicine != null) {
				return false;
			}
		} else if (!medicine.equals(other.medicine)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Get number of phases
	 * @return
	 */
	public int getPhasesNo() {
		return dose.size();
	}
	
	

}
