package org.msh.quantb.services.excel;

import org.msh.quantb.services.io.ForecastingRegimenResultUIAdapter;
import org.msh.quantb.services.io.ForecastingRegimenUIAdapter;

/**
 * we are need results with regimen
 * @author alexey
 *
 */
public class RegimenMonthResult implements Comparable<RegimenMonthResult> {
	private ForecastingRegimenUIAdapter regimen;
	private ForecastingRegimenResultUIAdapter result;
	
	public RegimenMonthResult(ForecastingRegimenUIAdapter _regimen, ForecastingRegimenResultUIAdapter _result){
		regimen = _regimen;
		result = _result;
	}

	/**
	 * @return the regimen
	 */
	public ForecastingRegimenUIAdapter getRegimen() {
		return regimen;
	}

	/**
	 * @param regimen the regimen to set
	 */
	public void setRegimen(ForecastingRegimenUIAdapter regimen) {
		this.regimen = regimen;
	}

	/**
	 * @return the result
	 */
	public ForecastingRegimenResultUIAdapter getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(ForecastingRegimenResultUIAdapter result) {
		this.result = result;
	}

	/**
	 * compare by month, if months equals, by regimen
	 */
	@Override
	public int compareTo(RegimenMonthResult o) {
		int res = this.getResult().getMonth().compareTo(o.getResult().getMonth());
		if (res == 0){
			return this.getRegimen().compareTo(o.getRegimen());
		}else{
			return res;
		}
	}
}
