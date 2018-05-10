package org.msh.quantb.services.io;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.jdesktop.observablecollections.ObservableCollections;
import org.msh.quantb.model.forecast.ForecastingRegimen;
import org.msh.quantb.model.forecast.ForecastingRegimenResult;
import org.msh.quantb.model.forecast.Month;
import org.msh.quantb.model.forecast.MonthQuantity;
import org.msh.quantb.model.mvp.ModelFactory;

/**
 * ForecastingRegimen object adapted to UI
 * @author alexey
 *
 */
@SuppressWarnings("rawtypes")
public class ForecastingRegimenUIAdapter extends AbstractUIAdapter implements Comparable{
	private ForecastingRegimenExt fcRegimenObj;
	private RegimenUIAdapter regimenUI=null;
	private List<MonthQuantityUIAdapter> casesOnTreatment;
	// listen quantity changes
	private PropertyChangeListener quantityChangeListener=null;
	private List<MonthQuantityUIAdapter> newCases;
	private PropertyChangeListener quantityNewChangeListener;
	/**
	 * Only valid constructor
	 * @param fcRegimenObj
	 */
	public ForecastingRegimenUIAdapter(ForecastingRegimen fcRegimenObj) {
		super();
		this.fcRegimenObj = new ForecastingRegimenExt(fcRegimenObj);
	}
	

	@Override
	public String toString() {
		return "ForecastingRegimenUIAdapter [getRegimen()=" + getRegimen()
				+ ", getCasesOnTreatment()=" + getCasesOnTreatment()
				+ ", getNewCases()=" + getNewCases() + "]";
	}


	/**
	 * @return the fcRegimenObj
	 */
	public ForecastingRegimenExt getFcRegimenObj() {
		return fcRegimenObj;
	}
	/**
	 * @param fcRegimenObj the fcRegimenObj to set
	 */
	public void setFcRegimenObj(ForecastingRegimen fcRegimenObj) {
		ForecastingRegimenExt oldValue = getFcRegimenObj();
		this.fcRegimenObj = new ForecastingRegimenExt(fcRegimenObj);
		firePropertyChange("fcRegimenObj", oldValue, getFcRegimenObj());
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingRegimen#getPersentNewCases()
	 */
	public Float getPercentNewCases() {
		return fcRegimenObj.getPercentNewCases();
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingRegimen#setPersentNewCases(float)
	 */
	public void setPercentNewCases(Float value) {
		if (value == null) return;
		Float oldValue = getPercentNewCases();
		fcRegimenObj.setPercentNewCases(value);
		firePropertyChange("percentNewCases", oldValue, getPercentNewCases());
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingRegimen#getRegimen()
	 */
	public RegimenUIAdapter getRegimen() {
		if (this.regimenUI == null) regimenUI = new RegimenUIAdapter(getFcRegimenObj().getRegimen());
		return regimenUI;
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingRegimen#setRegimen(org.msh.quantb.model.gen.Regimen)
	 */
	public void setRegimen(RegimenUIAdapter value) {
		RegimenUIAdapter oldValue = getRegimen();
		fcRegimenObj.setRegimen(value.getRegimen());
		firePropertyChange("regimen", oldValue, getRegimen());
	}
	/**
	 * Get list cases on treatment
	 * @return
	 */
	public List<MonthQuantityUIAdapter> getCasesOnTreatment(){
		List<MonthQuantityUIAdapter> tmp = new ArrayList<MonthQuantityUIAdapter>();
		for(MonthQuantity m : fcRegimenObj.getCasesOnTreatment()){
			MonthQuantityUIAdapter mqui = new MonthQuantityUIAdapter(m);
			if (getQuantityChangeListener() != null){
				mqui.addPropertyChangeListener("iQuantity", getQuantityChangeListener());
			}
			tmp.add(mqui);
		}
		this.casesOnTreatment = ObservableCollections.observableList(tmp);
		Collections.sort(this.casesOnTreatment);
		return this.casesOnTreatment;
	}
	
	/**
	 * Get new cases quantities
	 * @return
	 */
	public List<MonthQuantityUIAdapter> getNewCases(){
		List<MonthQuantityUIAdapter> tmp = new ArrayList<MonthQuantityUIAdapter>();
		List<MonthQuantity> arr = fcRegimenObj.getNewCases();
		for(MonthQuantity m : arr){
			MonthQuantityUIAdapter mui = new MonthQuantityUIAdapter(m);
			mui.addPropertyChangeListener("iQuantity", getQuantityNewChangeListener());
			tmp.add(mui);
		}
		newCases = ObservableCollections.observableList(tmp);
		Collections.sort(newCases);
		return newCases;
	}

	/**
	 * Get quantity from list for month given
	 * @param mqList
	 * @param month
	 * @return quantity or 0 if not found
	 */
	public float getMonthQuantity(List<MonthQuantityUIAdapter> mqList, MonthUIAdapter month) {
		int res = 0;
		for(MonthQuantityUIAdapter mq : mqList){
			if(mq.equals(month)){
				return mq.getIQuantity();
			}
		}
		return res;
	}


	/**
	 * Get list of results
	 * @return list of results or empty list
	 */
	@SuppressWarnings("unchecked")
	public List<ForecastingRegimenResultUIAdapter> getResults(){
		List<ForecastingRegimenResultUIAdapter> res = new ArrayList<ForecastingRegimenResultUIAdapter>();
		for(ForecastingRegimenResult r : this.getFcRegimenObj().getResults()){
			res.add(new ForecastingRegimenResultUIAdapter(r));
		}
		Collections.sort(res);
		return res;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
		+ ((getRegimen() == null || getRegimen().getName() == null) ? 0 : getRegimen().getName().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object _another){
		if (super.equals(_another)){
			ForecastingRegimenUIAdapter another = (ForecastingRegimenUIAdapter) _another;
			return this.getRegimen().getName().equals(another.getRegimen().getName());
		}else return false;
	}
	@Override
	public int compareTo(Object o) {
		if (!o.getClass().equals(this.getClass())) return -1;
		ForecastingRegimenUIAdapter another = (ForecastingRegimenUIAdapter) o;
		if (getRegimen() == null || getRegimen().getName()==null) return -1;
		if (another==null || another.getRegimen()==null || another.getRegimen().getName() == null) return 1;
		return getRegimen().getName().compareTo(another.getRegimen().getName());
	}
	/**
	 * Get full regimen length
	 * @deprecated
	 * @return
	 */
	public int getFullLength() {
		int ret = 0;
		ret = this.getRegimen().getIntensive().getDurationInMonths() + 
		this.getRegimen().getContinious().getDurationInMonths();
		for(PhaseUIAdapter addPh : this.getRegimen().getAddPhases()){
			ret = ret + addPh.getDurationInMonths();
		}
		return ret;
	}
	/**
	 * find medication by medicine for phase
	 * @param medicine
	 * @param intens - true, search in intensive phase
	 * @return null, if not found, otherwise medication (not clone!!!!)
	 */
	public MedicationUIAdapter findMedication(MedicineUIAdapter medicine, boolean intens) {
		PhaseUIAdapter p = intens? this.getRegimen().getIntensive() : 
			this.getRegimen().getContinious();
		for(MedicationUIAdapter medU : p.getMedications()){
			if(medU.getMedicine().equals(medicine)) return medU;
		}
		return null;
	}
	/**
	 * Get Forecasting regimes results from RD for each 1 of month
	 * This method returns results split by month 
	 * @param referenceDate 
	 * @param factory TODO
	 * @return months regimes totals or empty list
	 */
	public List<ForecastingRegimenResultUIAdapter> getMonthsResults(Calendar referenceDate, ModelFactory factory) {
		List<ForecastingRegimenResultUIAdapter> tmp = new ArrayList<ForecastingRegimenResultUIAdapter>();
		Month m = factory.createMonth(referenceDate.get(Calendar.YEAR), referenceDate.get(Calendar.MONTH));
		MonthUIAdapter rdM = new MonthUIAdapter(m);
		for(ForecastingRegimenResultUIAdapter fr : this.getResults()){
			if (fr.getMonth().compareTo(rdM)==0){
				if (fr.getFromDay() == referenceDate.get(Calendar.DAY_OF_MONTH)){
					ForecastingRegimenResultUIAdapter first = fr.makeClone(factory);
					tmp.add(first);
				}
			}
			if(fr.getMonth().compareTo(rdM)>0){
				if (fr.getFromDay() == 1){
					ForecastingRegimenResultUIAdapter next = fr.makeClone(factory);
					tmp.add(next);
				}
			}
		}
		List<ForecastingRegimenResultUIAdapter> res = ObservableCollections.observableList(tmp);
		Collections.sort(res);
		return res;
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingRegimen#getPercentCasesOnTreatment()
	 */
	public Float getPercentCasesOnTreatment() {
		return fcRegimenObj.getPercentCasesOnTreatment();
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingRegimen#setPercentCasesOnTreatment(float)
	 */
	public void setPercentCasesOnTreatment(Float value) {
		if(value == null) return;
		Float oldValue = getPercentCasesOnTreatment();
		if (oldValue != null){
			fcRegimenObj.setPercentCasesOnTreatment(value);
			firePropertyChange("percentCasesOnTreatment", oldValue, getPercentCasesOnTreatment());
		}
	}

	/**
	 * sometimes we are need to listen enrolled cases quantity changes
	 * @param regimenQuantityListener
	 */
	public void setRegimensOldCasesQtyListener(
			PropertyChangeListener regimenQuantityListener) {
		this.quantityChangeListener = regimenQuantityListener;

	}
	/**
	 * @return the quantityChangeListener
	 */
	public PropertyChangeListener getQuantityChangeListener() {
		return quantityChangeListener;
	}
	/**
	 * @param quantityChangeListener the quantityChangeListener to set
	 */
	public void setQuantityChangeListener(
			PropertyChangeListener quantityChangeListener) {
		this.quantityChangeListener = quantityChangeListener;
	}
	/**
	 * listen new cases quantity changes
	 * @param newQtyListener
	 */
	public void setRegimensNewCasesQtyListener(
			PropertyChangeListener newQtyListener) {
		this.quantityNewChangeListener = newQtyListener;
		
	}
	/**
	 * @return the quantityNewChangeListener
	 */
	public PropertyChangeListener getQuantityNewChangeListener() {
		return quantityNewChangeListener;
	}
	/**
	 * Get total enrolled cases quantity
	 * @return
	 */
	public int getOldCasesQuantity() {
		int res = 0;
		for(MonthQuantity mq : this.getFcRegimenObj().getCasesOnTreatment()){
			res = res + mq.getIQuantity();
		}
		return res;
	}
	/**
	 * Get total new cases quantity
	 * @return
	 */
	public int getNewCasesQuantity() {
		int res = 0;
		for(MonthQuantity mq : this.getFcRegimenObj().getNewCases()){
			res = res + mq.getIQuantity();
		}
		return res;
	}
	
	public Boolean isExcludeNewCases() {
		return fcRegimenObj.isExcludeNewCases();
	}
	
	public void setExcludeNewCases(Boolean value) {
		Boolean oldValue = isExcludeNewCases();
		fcRegimenObj.setExcludeNewCases(value);
		firePropertyChange("excludeNewCases", oldValue, isExcludeNewCases());
	}
	
	public boolean isExcludeCasesOnTreatment(){
		return fcRegimenObj.isExcludeCasesOnTreatment();
	}
	
	public void setExcludeCasesOnTreatment(Boolean value){
		Boolean oldValue = isExcludeCasesOnTreatment();
		fcRegimenObj.setExcludeCasesOnTreatment(value);
		firePropertyChange("excludeCasesOnTreatment", oldValue, isExcludeCasesOnTreatment());
	}
}
