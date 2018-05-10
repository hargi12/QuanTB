package org.msh.quantb.services.io;

import java.math.BigDecimal;
import java.util.List;

import org.msh.quantb.model.forecast.MedicineCons;
import org.msh.quantb.model.mvp.ModelFactory;

/**
 * Medicine consumption object adapted to UI operations
 * @author alexey
 *
 */
public class MedicineConsUIAdapter extends AbstractUIAdapter implements Comparable {
	private MedicineCons medConsObj;

	public MedicineConsUIAdapter(MedicineCons _medCons){
		this.medConsObj = _medCons;
	}

	/**
	 * @return the medicineConsObj
	 */
	public MedicineCons getMedicineConsObj() {
		return medConsObj;
	}

	/**
	 * Count equals if medicine equals. This will work only "under" particular regimen result!!!
	 * @param arg0
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		if (arg0 instanceof MedicineConsUIAdapter){
			MedicineConsUIAdapter another = (MedicineConsUIAdapter) arg0;
			return this.getMedicine().equals(another.getMedicine());
		}else
			return false;
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.MedicineCons#getMedicine()
	 */
	public MedicineUIAdapter getMedicine() {
		return new MedicineUIAdapter(medConsObj.getMedicine());
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.MedicineCons#getConsIntensive()
	 */
	public BigDecimal getConsIntensiveOld() {
		return medConsObj.getConsIntensiveOld();
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.MedicineCons#getConsConti()
	 */
	public BigDecimal getConsContiOld() {
		return medConsObj.getConsContiOld();
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.MedicineCons#getConsIntensiveNew()
	 */
	public BigDecimal getConsIntensiveNew() {
		return medConsObj.getConsIntensiveNew();
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.MedicineCons#setConsIntensiveNew(int)
	 */
	public void setConsIntensiveNew(BigDecimal value) {
		BigDecimal oldValue = getConsIntensiveNew();
		medConsObj.setConsIntensiveNew(value);
		firePropertyChange("consIntensiveNew", oldValue, getConsIntensiveNew());
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.MedicineCons#getConsContiNew()
	 */
	public BigDecimal getConsContiNew() {
		return medConsObj.getConsContiNew();
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.MedicineCons#setConsContiNew(int)
	 */
	public void setConsContiNew(BigDecimal value) {
		BigDecimal oldValue = getConsContiNew();
		medConsObj.setConsContiNew(value);
		firePropertyChange("consContiNew", oldValue, getConsContiNew());
	}

	/**
	 * @return
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + medConsObj.getConsContiOld().intValue();
		result = prime * result + medConsObj.getConsIntensiveOld().intValue();
		result = prime * result + medConsObj.getConsContiNew().intValue();
		result = prime * result + medConsObj.getConsIntensiveNew().intValue();
		result = prime * result
		+ ((getMedicine() == null) ? 0 : getMedicine().hashCode());
		return result;
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.MedicineCons#setMedicine(org.msh.quantb.model.gen.Medicine)
	 */
	public void setMedicine(MedicineUIAdapter value) {
		MedicineUIAdapter oldValue = getMedicine();
		medConsObj.setMedicine(value.getMedicine());
		firePropertyChange("medicine", oldValue, getMedicine());
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.MedicineCons#setConsIntensive(int)
	 */
	public void setConsIntensiveOld(BigDecimal value) {
		BigDecimal oldValue = getConsIntensiveOld();
		medConsObj.setConsIntensiveOld(value);
		firePropertyChange("consIntensiveOld", oldValue, getConsIntensiveOld());
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.MedicineCons#setConsConti(int)
	 */
	public void setConsContiOld(BigDecimal value) {
		BigDecimal oldValue = getConsContiOld();
		medConsObj.setConsContiOld(value);
		firePropertyChange("consContiOld", oldValue, getConsContiOld());
	}

	/**
	 * @return
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s = getMedicine() + " Intensive old - " + getConsIntensiveOld() + " new - " + getConsIntensiveNew();
		s += " Conti old - " + getConsContiOld() + " new - " + getConsContiNew();
		for(int i = 0; i< getConsOtherOld().size(); i++){
			s += " Phase " + (i+3) + " old - " + getConsOtherOld().get(i) + " new - " + getConsOtherNew().get(i);
		}
		
		
		return s;
	}

	@Override
	public int compareTo(Object o) {
		if (o == null) return 1;
		if (o instanceof MedicineConsUIAdapter){
			MedicineConsUIAdapter another = (MedicineConsUIAdapter) o;
			return this.getMedicine().compareTo(another);
		}else
			return 1;
	}
	
	/**
	 * Get original list of the consOtherOld. You can add elements to this list
	 * @return
	 */
	public List<BigDecimal> getConsOtherOld(){
		return this.getMedicineConsObj().getConsOtherOld();
	}
	/**
	 * Get original list of the consOtherNew. You can add elements to this list
	 * @return
	 */
	public List<BigDecimal> getConsOtherNew(){
		return this.getMedicineConsObj().getConsOtherNew();
	}
	

	/**
	 * summ all consumption data
	 * @return
	 */
	public BigDecimal getAllConsumption() {
		return this.getConsumeNew().add(this.getConsumeOld());
	}
	/**
	 * Create deep clone of this object
	 * @return
	 */
	public MedicineConsUIAdapter createClone(ModelFactory factory) {
		MedicineCons ret = factory.createMedicineCons(this.getMedicineConsObj().getMedicine());
		ret.setConsContiNew(this.getConsContiNew());
		ret.setConsContiOld(this.getConsContiOld());
		ret.setConsIntensiveNew(this.getConsIntensiveNew());
		ret.setConsIntensiveOld(this.getConsIntensiveOld());
		for(BigDecimal i : this.getConsOtherOld()){
			ret.getConsOtherOld().add(i);
		}
		for(BigDecimal i : this.getConsOtherNew()){
			ret.getConsOtherNew().add(i);
		}
		return new MedicineConsUIAdapter(ret);
	}
	/**
	 * Get medicine consumption only for enrolled cases in all phases
	 * @return
	 */
	public BigDecimal getConsumeOld() {
		BigDecimal otherCount = BigDecimal.ZERO;
		for(BigDecimal i: this.getConsOtherOld()){
			otherCount = otherCount.add(i);
		}
		otherCount = otherCount.add(this.getConsContiOld());
		otherCount = otherCount.add(this.getConsIntensiveOld());
		return otherCount;
	}
	/**
	 * get medicine consumption only for expected cases in all phases
	 * @return
	 */
	public BigDecimal getConsumeNew() {
		BigDecimal otherCount = BigDecimal.ZERO;
		for(BigDecimal i: this.getConsOtherNew()){
			otherCount=otherCount.add(i);
		}
		otherCount = otherCount.add(this.getConsContiNew());
		otherCount = otherCount.add(this.getConsIntensiveNew());
		return otherCount;
	}


}
