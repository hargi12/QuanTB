package org.msh.quantb.services.io;

import java.math.BigDecimal;

import org.msh.quantb.model.forecast.PhaseResult;

/**
 * UI adapter for forecasting phase result
 * @author alexey
 *
 */
public class PhaseResultUIAdpter extends AbstractUIAdapter{
	private PhaseResult phareResultObj;
	/**
	 * Only valid constructor
	 * @param _phaseResult
	 */
	public PhaseResultUIAdpter(PhaseResult _phaseResult){
		this.phareResultObj = _phaseResult;
	}
	/**
	 * @return the phareResultObj
	 */
	public PhaseResult getPhareResultObj() {
		return phareResultObj;
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.PhaseResult#getNewCases()
	 */
	public BigDecimal getNewCases() {
		return phareResultObj.getNewCases();
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.PhaseResult#setNewCases(float)
	 */
	public void setNewCases(BigDecimal value) {
		BigDecimal oldValue = getNewCases();
		phareResultObj.setNewCases(value);
		firePropertyChange("newCases", oldValue, getNewCases());
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.PhaseResult#getOldCases()
	 */
	public BigDecimal getOldCases() {
		return phareResultObj.getOldCases();
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.PhaseResult#setOldCases(int)
	 */
	public void setOldCases(BigDecimal value) {
		BigDecimal oldValue = getOldCases();
		phareResultObj.setOldCases(value);
		firePropertyChange("oldcases", oldValue, getOldCases());
	}
	@Override
	public boolean equals(Object _another){
		return false;
	}
	/**
	 * 
	 */
	public String toString(){
		return " new cases " + this.getNewCases() + " cases on trea " + this.getOldCases();
	}
	/**
	 * add values from another phase, useful for join
	 * @param phase
	 */
	public void add(PhaseResultUIAdpter phase) {
		this.setNewCases(this.getNewCases().add(phase.getNewCases()));
		this.setOldCases(this.getOldCases().add(phase.getOldCases()));
	}
	
}
