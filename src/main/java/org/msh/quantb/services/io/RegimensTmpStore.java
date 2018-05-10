package org.msh.quantb.services.io;

import java.util.ArrayList;
import java.util.List;

import org.jdesktop.observablecollections.ObservableCollections;
/**
 * This class represent buffer storage for the selected regimens
 * @author alexey
 *
 */
public class RegimensTmpStore extends AbstractUIAdapter {
	private List<RegimenUIAdapter> regimens;
	
	public RegimensTmpStore(List<RegimenUIAdapter> _regimens){
		regimens = _regimens;
	}
	
	/**
	 * @return the regimens
	 */
	public List<RegimenUIAdapter> getRegimens() {
		return regimens!=null?ObservableCollections.observableList(regimens):new ArrayList<RegimenUIAdapter>();
	}

	/** 
	 * @param regimens 
	 */
	public void setRegimens(List<RegimenUIAdapter> regimens) {
		List<RegimenUIAdapter> old = getRegimens();		
		this.regimens = regimens;
		firePropertyChange("regimens", old, getRegimens());
	}	
}
