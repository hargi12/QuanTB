package org.msh.quantb.services.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jdesktop.observablecollections.ObservableCollections;
import org.msh.quantb.model.gen.ClassifierTypesEnum;
import org.msh.quantb.model.gen.Medicine;
import org.msh.quantb.model.gen.MedicineTypesEnum;
import org.msh.quantb.model.medicine.Medicines;

/**
 * Observable medicines
 * Intends for use with UI elements
 * @author alexey
 *
 */
public class MedicinesDicUIAdapter extends AbstractUIAdapter {
	private Medicines medicinesDictionary;
	private ClassifierTypesEnum filter = null;
	private List<MedicineUIAdapter> dicSrc;
	private List<MedicineUIAdapter> exceptList=null;
	/**
	 * This comparator intends only for compare by the abbreviated name
	 * @author alexey
	 *
	 */
	private class NameComparator implements Comparator<MedicineUIAdapter>{
		@Override
		public int compare(MedicineUIAdapter o1, MedicineUIAdapter o2) {
			if(o1==null && o2 == null){
				return 0;
			}
			if (o2 == null){
				return -1;
			}
			if(o1 == null){
				return 1;
			}
			if (o1.getAbbrevName() == null)
				return -1;
			if (o2.getAbbrevName() == null)
				return 1;
			if (o1.equals(o2)){
				return 0;
			}
			int sort = o1.getAbbrevName().compareTo(o2.getAbbrevName());
			if (sort == 0){
				return -1;
			}else{
				return sort;
			}
		}
	}

	public MedicinesDicUIAdapter(Medicines _medicinesDic){
		assert(_medicinesDic != null);
		medicinesDictionary = _medicinesDic;
		List<MedicineUIAdapter> ml = new ArrayList<MedicineUIAdapter>();		
		for(Medicine m: medicinesDictionary.getMedicines()){
			ml.add(new MedicineUIAdapter(m));
		}
		Collections.sort(ml);
		dicSrc = ObservableCollections.observableList(ml);	
	}

	/**
	 * Get the medicines list sorted by the medicine UI adapter order - injectables first
	 * @return the medicinesDic
	 */
	public List<MedicineUIAdapter> getMedicinesDic() {
		List<MedicineUIAdapter> ml = createTheList();
		Collections.sort(ml);
		return ObservableCollections.observableList(ml);	
	}

	/**
	 * Get the medicines list sorted by the medicine abbr name
	 * @return the medicinesDic
	 */
	public List<MedicineUIAdapter> getMedicinesDicByName() {
		List<MedicineUIAdapter> ml = createTheList();
		Collections.sort(ml, new NameComparator());
		return ObservableCollections.observableList(ml);	
	}
	/**
	 * Create the medicins list
	 * @return
	 */
	private List<MedicineUIAdapter> createTheList() {
		List<MedicineUIAdapter> ml = new ArrayList<MedicineUIAdapter>();		
		for(MedicineUIAdapter mUi: dicSrc){
			if(this.exceptList != null){
				if (!this.exceptList.contains(mUi)){
					addToList(ml, mUi);
				}
			}else{
				addToList(ml, mUi);
			}
		}
		return ml;
	}
	/**
	 * Add element to the list
	 * @param ml
	 * @param mUi
	 */
	private void addToList(List<MedicineUIAdapter> ml, MedicineUIAdapter mUi) {
		if(filter != null ){
			if(mUi.getClassifier() == filter || filter==ClassifierTypesEnum.UNKNOWN){
				ml.add(mUi);
			}
		}else{
			ml.add(mUi);
		}
	}

	/**
	 * @return the filter
	 */
	public ClassifierTypesEnum getFilter() {
		return filter;
	}

	/**
	 * @param filter the filter to set
	 */
	public void setFilter(ClassifierTypesEnum filter) {
		ClassifierTypesEnum oldValue = getFilter();
		this.filter = filter;
		firePropertyChange("filter", oldValue, getFilter());
		firePropertyChange("medicinesDic",null , getMedicinesDic());
		firePropertyChange("medicinesDicByName",null , getMedicinesDic());
	}
	/**
	 * Set list of exceptions, medicines from this list do not will be returned
	 * @param _exceptList
	 */
	public void setExeptionList(List<MedicineUIAdapter> _exceptList) {
		this.exceptList = _exceptList;

	}
	/**
	 * Get all medicines from the dictionary, without apply filters and exceptions
	 * @return
	 */
	public List<MedicineUIAdapter> getMedicinesDicAll() {
		return dicSrc;
	}

	/**
	 * Add medicine if not existed in dictionary
	 * @param medicine
	 * @return true, if medicine has been added
	 */
	public boolean addIfNotExist(MedicineUIAdapter medicine) {
		if (!getMedicinesDicAll().contains(medicine)){
			getMedicinesDicAll().add(medicine);
			return true;
		}else{
			return false;
		}

	}



}
