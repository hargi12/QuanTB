package org.msh.quantb.services.io;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.msh.quantb.model.forecast.ForecastFile;
import org.msh.quantb.model.forecast.ForecastLast5;
import org.msh.quantb.services.mvp.Presenter;

/**
 * Open file history business object
 * @author alexey
 *
 */
public class ForecastLast5UI extends AbstractUIAdapter {
	private ForecastLast5 forecastLast5;
	private List<ForecastFileUI> fileList;

	/**
	 * Only valid constructor
	 * @param _fcLast5 object with history
	 */
	public ForecastLast5UI(ForecastLast5 _fcLast5){
		this.forecastLast5 = _fcLast5;
		if(fileList != null){
			fileList.clear();
		}
		List<ForecastFileUI> res = new ArrayList<ForecastFileUI>();
		for(ForecastFile ff : forecastLast5.getForecastFile()){
			res.add(new ForecastFileUI(ff));
		}
		this.fileList = ObservableCollections.observableList(res);
		
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastLast5#getForecastFile()
	 */
	public List<ForecastFileUI> getForecastFiles() {
		return this.fileList;
	}
	//only one opened file may be added, max 5 elements may be
	public void add(ForecastFileUI _ffUi){
		List<ForecastFileUI> oldValue = getForecastFiles();
		if (oldValue.indexOf(_ffUi)== -1){
			oldValue.add(_ffUi);
		}
		while(oldValue.size()>5){
			oldValue.remove(0);
		}
		this.forecastLast5.getForecastFile().clear();
		for(ForecastFileUI ffui : oldValue){
			this.forecastLast5.getForecastFile().add(ffui.getObject());
		}
		firePropertyChange("forecastFiles", oldValue, getForecastFiles());
	}
	
	
	
}
