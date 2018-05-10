package org.msh.quantb.services.io;
/**
 * Establish 1:1 relation between QuanTB medicine and Excel medicine
 * @author Alexey Kurasov
 *
 */
public class MedicinesDecoder extends AbstractUIAdapter {
	ForecastingMedicineUIAdapter medicineQ=null;
	String medicineE="";
	
	
	/**
	 * Only valid constructor
	 * @param medicineQ
	 */
	public MedicinesDecoder(ForecastingMedicineUIAdapter medicineQ) {
		super();
		this.medicineQ = medicineQ;
	}
	public ForecastingMedicineUIAdapter getMedicineQ() {
		return medicineQ;
	}
	public void setMedicineQ(ForecastingMedicineUIAdapter medicineQ) {
		ForecastingMedicineUIAdapter oldValue = getMedicineQ();
		this.medicineQ = medicineQ;
		firePropertyChange("medicineQ", oldValue, getMedicineQ());
	}
	public String getMedicineE() {
		return medicineE;
	}
	public void setMedicineE(String medicineE) {
		String oldValue = getMedicineE();
		this.medicineE = medicineE;
		firePropertyChange("", oldValue, getMedicineE());
	}
	@Override
	public String toString() {
		return "Decoder [medicineQ=" + medicineQ + ", medicineE="
				+ medicineE + "]";
	}
}
