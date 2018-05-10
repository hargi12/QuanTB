package org.msh.quantb.services.io;

import java.util.ArrayList;
import java.util.List;

import org.jdesktop.observablecollections.ObservableCollections;

/**
 * This class represent buffer storage for the selected forecasting regimens
 * @author alexey
 *
 */
public class ForecastingRegimensUIAdapter extends AbstractUIAdapter {
private List<ForecastingRegimenUIAdapter> regimens;
	
	public ForecastingRegimensUIAdapter(List<ForecastingRegimenUIAdapter> _regimens){
		regimens = _regimens;
	}
	
	/**
	 * @return the regimens
	 */
	public List<ForecastingRegimenUIAdapter> getRegimens() {
		return regimens!=null?ObservableCollections.observableList(regimens):new ArrayList<ForecastingRegimenUIAdapter>();
	}

	/** 
	 * @param regimens 
	 */
	public void setRegimens(List<ForecastingRegimenUIAdapter> regimens) {
		List<ForecastingRegimenUIAdapter> old = getRegimens();		
		this.regimens = regimens;
		firePropertyChange("forecastingRegimens", old, getRegimens());
	}
	

	/**
	 * Returns list of properties names 
	 * @return
	 */
	public static String[] getParamenters() {
		String[] ret= {
			"percentNewCases", "percentCasesOnTreatment", "casesOnTreatment", "newCases"
			};
		return ret;
	}
}
