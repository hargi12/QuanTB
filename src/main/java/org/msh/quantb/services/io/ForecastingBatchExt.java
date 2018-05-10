package org.msh.quantb.services.io;

import java.math.BigDecimal;

import javax.xml.datatype.XMLGregorianCalendar;

import org.msh.quantb.model.forecast.ForecastingBatch;
import org.msh.quantb.model.forecast.Month;

public class ForecastingBatchExt  {

	ForecastingBatch original;


	public ForecastingBatchExt(ForecastingBatch original) {
		super();
		this.original = original;
	}


	public ForecastingBatch getOriginal() {
		return original;
	}


	public void setOriginal(ForecastingBatch original) {
		this.original = original;
	}

	/**
	 * Get quantity if not excluded
	 * otherwise 0
	 * @return
	 */
	public int getQuantity() {
		if(isExclude()){
			return 0;
		}else{
			return getOriginal().getQuantity();
		}
	}

	/**
	 * get available quantity if not excluded
	 * otherwise 0
	 * @return
	 */
	public BigDecimal getQuantityAvailable() {
		if (isExclude()){
			return BigDecimal.ZERO;
		}else{
			return getOriginal().getQuantityAvailable();
		}
	}


	public int hashCode() {
		return original.hashCode();
	}


	public XMLGregorianCalendar getExpired() {
		return original.getExpired();
	}


	public void setExpired(XMLGregorianCalendar value) {
		original.setExpired(value);
	}


	public Month getExpiryDate() {
		return original.getExpiryDate();
	}


	public boolean equals(Object obj) {
		return original.equals(obj);
	}


	public void setExpiryDate(Month value) {
		original.setExpiryDate(value);
	}


	public void setQuantity(int value) {
		original.setQuantity(value);
	}


	public String getComment() {
		return original.getComment();
	}


	public void setComment(String value) {
		original.setComment(value);
	}


	public void setQuantityAvailable(BigDecimal value) {
		original.setQuantityAvailable(value);
	}


	public int getQuantityExpired() {
		if(isExclude()){
			return 0;
		}
		else{
			return original.getQuantityExpired();
		}
	}


	public void setQuantityExpired(int value) {
		original.setQuantityExpired(value);
	}


	public BigDecimal getConsumptionInMonth() {
		if(isExclude()){
			return BigDecimal.ZERO;
		}else{
			return original.getConsumptionInMonth();
		}
	}


	public void setConsumptionInMonth(BigDecimal value) {
		original.setConsumptionInMonth(value);
	}


	public XMLGregorianCalendar getAvailFrom() {
		return original.getAvailFrom();
	}


	public void setAvailFrom(XMLGregorianCalendar value) {
		original.setAvailFrom(value);
	}


	public String toString() {
		return original.toString();
	}


	public boolean isExclude() {
		return original.isExclude();
	}


	public void setExclude(boolean value) {
		original.setExclude(value);
	}


}
