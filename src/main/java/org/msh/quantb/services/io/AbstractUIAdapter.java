package org.msh.quantb.services.io;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * This class is common root for all classes (not lists) that must acted as bean, i.e. firePropertyChange
 * @author alexey
 *
 */
public class AbstractUIAdapter {
	/**
	 * all UI will be checkable
	 */
	protected Boolean checked = new Boolean(false);
	
	
	
	/**
	 * @return the checked
	 */
	public Boolean getChecked() {
		return checked;
	}

	/**
	 * @param checked the checked to set
	 */
	public void setChecked(Boolean checked) {
		Boolean oldValue = getChecked();
		this.checked = checked;
		firePropertyChange("checked", oldValue, getChecked());
	}
	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName,
				listener);
	}

	public void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue,
				newValue);
	}
	
	/**
	 * Remove all change listeners for the property given
	 * @param propertyName
	 */
	public void removeAllPropertyChangeListeners(String propertyName) {
		PropertyChangeListener[] listeners = propertyChangeSupport.getPropertyChangeListeners(propertyName);
		for(PropertyChangeListener listener : listeners){
			propertyChangeSupport.removePropertyChangeListener(listener);
		}
		
	}
	
	
	/**
	 * Must be overridden !!!!
	 */
	@Override
	public boolean equals(Object _another){
		if (_another == null) return false;
		if (!_another.getClass().equals(this.getClass())) return false;
		return true;
	}
}
