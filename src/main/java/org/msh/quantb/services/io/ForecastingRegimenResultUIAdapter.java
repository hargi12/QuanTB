package org.msh.quantb.services.io;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.jdesktop.observablecollections.ObservableCollections;
import org.msh.quantb.model.forecast.ForecastingRegimenResult;
import org.msh.quantb.model.forecast.MedicineCons;
import org.msh.quantb.model.forecast.PhaseResult;
import org.msh.quantb.model.mvp.ModelFactory;

/**
 * UI adapter for ForecatingRegimenResult
 * @author alexey
 *
 */

public class ForecastingRegimenResultUIAdapter extends AbstractUIAdapter implements Comparable {
	private ForecastingRegimenResult resultObj;
	/**
	 * Only valid constructor
	 * @param _result
	 */
	public ForecastingRegimenResultUIAdapter(ForecastingRegimenResult _result){
		this.resultObj = _result;
	}


	/**
	 * @return the resultObj
	 */
	public ForecastingRegimenResult getResultObj() {
		return resultObj;
	}


	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingRegimenResult#getMonth()
	 */
	public MonthUIAdapter getMonth() {
		return new MonthUIAdapter(resultObj.getMonth());
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingRegimenResult#setMonth(org.msh.quantb.model.forecast.Month)
	 */
	public void setMonth(MonthUIAdapter value) {
		MonthUIAdapter oldValue = getMonth();
		resultObj.setMonth(value.getMonthObj());
		firePropertyChange("month", oldValue, getMonth());
	}


	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingRegimenResult#getIntensive()
	 */
	public PhaseResultUIAdpter getIntensive() {
		return new PhaseResultUIAdpter(resultObj.getIntensive());
	}


	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingRegimenResult#setIntensive(org.msh.quantb.model.forecast.PhaseResult)
	 */
	public void setIntensive(PhaseResultUIAdpter value) {
		PhaseResultUIAdpter oldValue = getIntensive();
		resultObj.setIntensive(value.getPhareResultObj());
		firePropertyChange("intensive", oldValue, getIntensive());
	}


	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingRegimenResult#getContinious()
	 */
	public PhaseResultUIAdpter getContinious() {
		return new PhaseResultUIAdpter(resultObj.getContinious());
	}


	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingRegimenResult#setContinious(org.msh.quantb.model.forecast.PhaseResult)
	 */
	public void setContinious(PhaseResultUIAdpter value) {
		PhaseResultUIAdpter oldValue = getContinious();
		resultObj.setContinious(value.getPhareResultObj());
		firePropertyChange("continious", oldValue, getContinious());
	}



	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingRegimenResult#getFromDay()
	 */
	public Integer getFromDay() {
		return resultObj.getFromDay();
	}


	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingRegimenResult#setFromDay(int)
	 */
	public void setFromDay(Integer value) {
		Integer oldValue = getFromDay();
		resultObj.setFromDay(value);
		firePropertyChange("fromDay", oldValue, getFromDay());
	}


	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingRegimenResult#getToDay()
	 */
	public Integer getToDay() {
		return resultObj.getToDay();
	}


	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingRegimenResult#setToDay(int)
	 */
	public void setToDay(Integer value) {
		Integer oldValue = getToDay();
		resultObj.setToDay(value);
		firePropertyChange("toDay", oldValue, getToDay());
	}


	/**
	 * @return medicine consumption result
	 * @see org.msh.quantb.model.forecast.ForecastingRegimenResult#getCons()
	 */
	@SuppressWarnings("unchecked")
	public List<MedicineConsUIAdapter> getCons() {
		List<MedicineConsUIAdapter> tmp = new ArrayList<MedicineConsUIAdapter>();
		for(MedicineCons mc : resultObj.getCons()){
			MedicineConsUIAdapter mcU = new MedicineConsUIAdapter(mc);
			tmp.add(mcU);
		}
		List<MedicineConsUIAdapter> ret = ObservableCollections.observableList(tmp);
		Collections.sort(ret);
		return ret;
	}


	@Override
	public String toString(){
		return this.getMonth().toString() + " intens " + getIntensive().toString()  + " contin " + getContinious().toString();
	}


	@Override
	public int compareTo(Object _another) {
		if (_another == null) return -1;
		if (_another instanceof ForecastingRegimenResultUIAdapter){
			ForecastingRegimenResultUIAdapter another = (ForecastingRegimenResultUIAdapter) _another;
			int res = this.getMonth().compareTo(another.getMonth());
			if (res == 0){
				return this.getFromDay().compareTo(another.getFromDay());
			}else{
				return res;
			}
		}
		return 1;
	}

	/**
	 * get result for particular medicine
	 * @param med medicine
	 * @return result for medicine given or null if not found
	 */
	public MedicineConsUIAdapter getMedConsunption(MedicineUIAdapter med) {
		/*List<MedicineConsUIAdapter> mcUIL = this.getCons();
		for(MedicineConsUIAdapter mc :mcUIL){
			if (mc.getMedicine().equals(med)) return mc;
		}*/
		List<MedicineCons> mcL = this.getResultObj().getCons();
		for(MedicineCons mc : mcL){
			MedicineConsUIAdapter mcUI = new MedicineConsUIAdapter(mc);
			if (mcUI.getMedicine().equals(med)) return mcUI;
		}
		return null;
	}

/*	*//**
	 * Assume cases quantity in particular month already same
	 * Join medicine consumption for two results<br>
	 * Rules:
	 * <ul>
	 * <li>if this contain consumption data for medicine has in other - add values
	 * <li>if other contain consumption for medicine not listed in this - add consumption data to this from other
	 * <li>cannot be used across month boundary. Consumptions will be accounted correctly, cases no.
	 * </ul>
	 * only medicine results must be joined
	 * @param fr
	 *//*
	public void monthJoin(ForecastingRegimenResultUIAdapter other) {
		for(MedicineConsUIAdapter mc : this.getCons()){
			MedicineConsUIAdapter tmp = other.getMedConsunption(mc.getMedicine());
			if (tmp != null){
				mc.setConsContiNew(mc.getConsContiNew() + tmp.getConsContiNew());
				mc.setConsContiOld(mc.getConsContiOld() + tmp.getConsContiOld());
				mc.setConsIntensiveNew(mc.getConsIntensiveNew() + tmp.getConsIntensiveNew());
				mc.setConsIntensiveOld(mc.getConsIntensiveOld() + tmp.getConsIntensiveOld());
			}
		}
		for(MedicineConsUIAdapter mc : other.getCons()){
			MedicineConsUIAdapter tmp = this.getMedConsunption(mc.getMedicine());
			if (tmp == null){
				this.getResultObj().getCons().add(mc.getMedicineConsObj());
			}
		}
	}*/

	/**
	 * make deep clone of this object
	 * @param factory
	 * @return
	 */
	public ForecastingRegimenResultUIAdapter makeClone(ModelFactory factory) {
		MonthUIAdapter m = this.getMonth().incrementClone(factory, 0);
		ForecastingRegimenResult res = factory.createRegimenResult(m.getMonthObj());
		res.getContinious().setNewCases(this.getContinious().getNewCases());
		res.getContinious().setOldCases(this.getContinious().getOldCases());
		res.getIntensive().setNewCases(this.getIntensive().getNewCases());
		res.getIntensive().setOldCases(this.getIntensive().getOldCases());
		res.setFromDay(this.getFromDay());
		res.setToDay(this.getToDay());
		for(MedicineConsUIAdapter mc : this.getCons()){
			res.getCons().add(mc.createClone(factory).getMedicineConsObj());
		}
		for(PhaseResult pHr : this.getResultObj().getAddPhases()){
			PhaseResult tmp = factory.createPhaseResult(pHr.getNewCases(), pHr.getOldCases());
			res.getAddPhases().add(tmp);
		}
		return new ForecastingRegimenResultUIAdapter(res);
	}

	/**
	 * get from date as calendar
	 * @return
	 */
	public Calendar getFromDate() {
		return this.getMonth().getAnyDate(this.getFromDay());
	}
	/**
	 * Get list of additional phases sorted by sequence number
	 * Assume that additional phases are in right sequence!
	 * @return sorted by sequence list or empty list
	 */
	public List<PhaseResultUIAdpter> getAdditionalPhases(){
		List<PhaseResultUIAdpter> ret = new ArrayList<PhaseResultUIAdpter>();
		for(PhaseResult phR: getResultObj().getAddPhases()){
			PhaseResultUIAdpter phUIR = new PhaseResultUIAdpter(phR);
			ret.add(phUIR);
		}
		return ret;
	}

	/**
	 * get total quantity of the enrolled cases that really use medicine from the mc
	 * it is very smart method
	 * @param mc medicine consumption information
	 * @return
	 */
	public BigDecimal getOldCasesThatConsume(MedicineConsUIAdapter mc) {
		BigDecimal ret = new BigDecimal(0.00);
		ret = ret.setScale(2);
		if (mc.getConsIntensiveOld().compareTo(BigDecimal.ZERO) > 0){
			ret =ret.add(this.getIntensive().getOldCases());
		}
		if (mc.getConsContiOld().compareTo(BigDecimal.ZERO) > 0){
			ret = ret.add(this.getContinious().getOldCases());
		}
		int i=0;
		for(BigDecimal q : mc.getConsOtherOld()){
			if (q.compareTo(BigDecimal.ZERO) > 0){
				ret = ret.add(this.getAdditionalPhases().get(i).getOldCases());
			}
			i++;
		}
		return ret;
	}

	/**
	 * get total quantity of the expected cases that really use medicine from mc
	 * it is very smart method
	 * @param mc medicine consumption information
	 * @return
	 */
	public BigDecimal getNewCasesThatConsume(MedicineConsUIAdapter mc) {
		BigDecimal ret = new BigDecimal(0.00);
		ret = ret.setScale(2);
		if (mc.getConsIntensiveNew().compareTo(BigDecimal.ZERO) > 0){
			ret = ret.add(this.getIntensive().getNewCases());
		}
		if (mc.getConsContiNew().compareTo(BigDecimal.ZERO) > 0){
			ret = ret.add(this.getContinious().getNewCases());
		}
		int i=0;
		for(BigDecimal q : mc.getConsOtherNew()){
			if (q.compareTo(BigDecimal.ZERO) > 0){
				ret = ret.add(this.getAdditionalPhases().get(i).getNewCases());
			}
			i++;
		}
		return ret;
	}

	/**
	 * Get all enrolled cases quantities
	 * @return
	 */
	public BigDecimal getEnrolled() {
		BigDecimal ret = new BigDecimal(0.00);
		ret = ret.setScale(2);
		ret = ret.add(this.getIntensive().getOldCases());
		ret = ret.add(this.getContinious().getOldCases());
		for(PhaseResultUIAdpter phRUi : this.getAdditionalPhases()){
			ret = ret.add(phRUi.getOldCases());
		}
		return ret;
	}
	
	/**
	 * Get all expected cases quantities 
	 * @return
	 */
	public BigDecimal getExpected() {
		BigDecimal ret = new BigDecimal(0.00);
		ret = ret.setScale(2);
		ret = ret.add(this.getIntensive().getNewCases());
		ret = ret.add(this.getContinious().getNewCases());
		for(PhaseResultUIAdpter phRUi : this.getAdditionalPhases()){
			ret = ret.add(phRUi.getNewCases());
		}
		return ret;
	}

}
