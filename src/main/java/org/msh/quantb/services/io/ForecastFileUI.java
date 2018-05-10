package org.msh.quantb.services.io;

import org.msh.quantb.model.forecast.ForecastFile;

/**
 * Business object for open file history element
 * @author alexey
 *
 */
public class ForecastFileUI extends AbstractUIAdapter {
	private ForecastFile forecastFile;
	/**
	 * Only valid constructor
	 * @param _fcFile forecasting file object
	 */
	public ForecastFileUI(ForecastFile _fcFile){
		this.forecastFile = _fcFile;
	}
	
	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastFile#getName()
	 */
	public String getName() {
		return forecastFile.getName();
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastFile#setName(java.lang.String)
	 */
	public void setName(String value) {
		String oldValue = getName();
		forecastFile.setName(value);
		firePropertyChange("name", oldValue, getName());
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastFile#getPath()
	 */
	public String getPath() {
		return forecastFile.getPath();
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastFile#setPath(java.lang.String)
	 */
	public void setPath(String value) {
		String oldValue = getPath();
		forecastFile.setPath(value);
		firePropertyChange("path", oldValue, getPath());
	}
	/**
	 * Get forecasting file native object
	 * @return
	 */
	public ForecastFile getObject() {
		return this.forecastFile;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result + ((getPath() == null) ? 0 : getPath().hashCode());
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
		ForecastFileUI other = (ForecastFileUI) obj;
		if (getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!getName().equals(other.getName())) {
			return false;
		}
		if (getPath() == null) {
			if (other.getPath() != null) {
				return false;
			}
		} else if (!getPath().equals(other.getPath())) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString(){
		return this.getPath() +"<br>" + this.getName();
	}
}
