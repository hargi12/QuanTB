package org.msh.quantb.services.io;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.msh.quantb.services.calc.DateUtils;
import org.msh.quantb.services.mvp.Presenter;


/**
 * This class represent record for one medicine in QuanTB order
 * @author alexey
 *
 */
public class ForecastingTotalMedicine extends AbstractUIAdapter implements Comparable{
	MedicineUIAdapter medicine;
	Integer regularQuant = 0;
	BigDecimal adjustIt = new BigDecimal(100);
	BigDecimal adjustItAccel = new BigDecimal(100);
	Integer packSize=0;
	Integer packSizeAccel=0;
	BigDecimal packPrice=new BigDecimal(0);
	BigDecimal packPriceAccel=new BigDecimal(0);
	Integer adjustedRegular=0;
	Integer adjustedRegularPack = 0;
	//Integer adjustedRegularPackFromDelivery=0;
	BigDecimal regularCost = new BigDecimal(0);
	Integer accelQuant = 0;
	Integer adjustAccel = 0;
	Integer adjustedAccelPack=0;
	//Integer adjustedAccelPackFromDelivery=0;
	BigDecimal accelCost = new BigDecimal(0);
	Calendar accelDate;
	Integer total=0;
	Integer totalPack=0;
	BigDecimal totalCost = new BigDecimal(0);
	/**
	 * Only valid constructor
	 * @param med
	 */
	public ForecastingTotalMedicine(MedicineUIAdapter med){
		this.medicine = med;
		this.accelDate = GregorianCalendar.getInstance();
		DateUtils.cleanTime(accelDate);
	}




	/**
	 * @return the medicine
	 */
	public MedicineUIAdapter getMedicine() {
		return medicine;
	}




	/**
	 * @param medicine the medicine to set
	 */
	public void setMedicine(MedicineUIAdapter medicine) {
		MedicineUIAdapter oldValue = getMedicine();
		this.medicine = medicine;
		firePropertyChange("medicine", oldValue, getMedicine());
	}

	/**
	 * @return the regularQuant
	 */
	public Integer getRegularQuant() {
		return regularQuant;
	}

	/**
	 * @param regularQuant the regularQuant to set
	 */
	public void setRegularQuant(Integer regularQuant) {
		Integer oldValue = getRegularQuant();
		this.regularQuant = regularQuant;
		firePropertyChange("regularQuant", oldValue, getRegularQuant());
		//setAdjustedRegular();
	}

	/**
	 * @return the adjustIt for regular
	 */
	public BigDecimal getAdjustIt() {
		return adjustIt;
	}

	/**
	 * @return the adjustIt for accelerated
	 */
	public BigDecimal getAdjustItAccel() {
		return adjustItAccel;
	}


	/**
	 * @param adjustIt the adjustIt to set for regular
	 */
	public void setAdjustIt(BigDecimal adjustIt) {
		if (adjustIt == null) return;
		BigDecimal oldValue = getAdjustIt();
		this.adjustIt = adjustIt;
		firePropertyChange("adjustIt", oldValue, getAdjustIt());
	}

	/**
	 * @param adjustIt the adjustIt to set for accelerated
	 */
	public void setAdjustItAccel(BigDecimal adjustItAccel) {
		if (adjustItAccel == null) return;
		if(adjustItAccel.compareTo(new BigDecimal(0))<0){
			return;
		}
		BigDecimal oldValue = getAdjustItAccel();
		this.adjustItAccel = adjustItAccel;
		firePropertyChange("adjustItAccel", oldValue, getAdjustItAccel());
	}




	private void showAdjCoeffError() {
		String mess = Presenter.getMessage("Error.totalOrder.adustReg") + "<br>"
				+ getMedicine().getNameForDisplay()+".<br>"+
				Presenter.getMessage("Error.totalOrder.tryagain");
		Presenter.showError("<html>"+mess+"</html>");
	}


	/**
	 * @return the packSize for regular order
	 */
	public Integer getPackSize() {
		return packSize;
	}

	/**
	 * @return the packSize for accelerated order
	 */
	public Integer getPackSizeAccel() {
		return packSizeAccel;
	}


	/**
	 * @param packSize the packSize to set
	 */
	public void setPackSize(Integer packSize) {
		Integer oldValue = getPackSize();
		if(packSize>=0){
			this.packSize = packSize;
		}
		firePropertyChange("packSize", oldValue, getPackSize());
	}

	/**
	 * @param packSize the packSize to set
	 */
	public void setPackSizeAccel(Integer packSizeAccel) {
		Integer oldValue = getPackSizeAccel();
		if(packSizeAccel>=0){
		this.packSizeAccel = packSizeAccel;
		}
		firePropertyChange("packSizeAccel", oldValue, getPackSizeAccel());
	}


	/**
	 * @return the packPrice - regular
	 */
	public BigDecimal getPackPrice() {
		return packPrice;
	}

	/**
	 * @return the packPrice - accelerated
	 */
	public BigDecimal getPackPriceAccel() {
		return packPriceAccel;
	}


	/**
	 * @param packPrice the packPrice to set - regular
	 */
	public void setPackPrice(BigDecimal packPrice) {
		if(packPrice == null){
			return;
		}
		BigDecimal oldValue = getPackPrice();
		this.packPrice = packPrice;
		firePropertyChange("packPrice", oldValue, getPackPrice());
		setRegularCost();
	}

	/**
	 * @param packPrice the packPrice to set - accelerated
	 */
	public void setPackPriceAccel(BigDecimal packPriceAccel) {
		if(packPriceAccel == null) return;
		BigDecimal oldValue = getPackPriceAccel();
		this.packPriceAccel = packPriceAccel;
		firePropertyChange("packPriceAccel", oldValue, getPackPriceAccel());
		setAccelCost();
	}

	/**
	 * @return the adjustedRegular
	 */
	public Integer getAdjustedRegular() {
		return adjustedRegular;
	}
	
	public void setAdjustedRegular(Integer adjustedRegular) {
		Integer oldValue = getAdjustedRegular();
		this.adjustedRegular = adjustedRegular;
		firePropertyChange("adjustedRegular", oldValue, getAdjustedRegular());
	}




	public void setAdjustAccel(Integer adjustAccel) {
		Integer oldValue = getAdjustAccel();
		this.adjustAccel = adjustAccel;
		firePropertyChange("adjustedAccel", oldValue, getAdjustAccel());
	}




	/**
	 * Calculation percents for the value given
	 * Check the integer overflow
	 * @param value
	 * @param percents
	 * @return value or -1 if overflow
	 */
	public Integer calcPercents(Integer value, BigDecimal percents) {
		if (value == null || percents == null){
			return 0;
		}
		if(value == 0 || percents.compareTo(new BigDecimal(0))==0){
			return 0;
		}
		BigDecimal fres = percents.multiply(new BigDecimal(value));
		fres = fres.setScale(0, RoundingMode.HALF_UP);
		fres = fres.divide(new BigDecimal(100));
		fres = fres.setScale(0, RoundingMode.HALF_UP);
		if (fres.compareTo(new BigDecimal(Integer.MAX_VALUE)) >= 0){
			fres=new BigDecimal(-1);
		}
		return fres.intValue();
	}




	/**
	 * @return the adjustedRegularPack
	 */
	public Integer getAdjustedRegularPack() {
		return adjustedRegularPack;
	}


	public void setAdjustedRegularPack(Integer adjustedRegularPack) {
		Integer oldValue = getAdjustedRegularPack();
		this.adjustedRegularPack = adjustedRegularPack;
		firePropertyChange("adjustedRegularPack", oldValue, getAdjustedRegularPack());
		setRegularCost();
		setTotalPack();
	}

	/**
	 * Divide on Integer to another, return result rounded to next Integer
	 * @param one
	 * @param two
	 * @return 0 if at least one argument is 0, 
	 */
	public Integer divide(Integer one, Integer two) {
		if (one == 0 || two == 0) return 0;
		Integer res = one / two;
		if (res * two == one){
			return res;
		}else{
			return (one / two) + 1;
		}
	}




	/**
	 * @return the regularCost
	 */
	public BigDecimal getRegularCost() {
		return regularCost;
	}




	/**
	 * recalculate the regular cost
	 */
	private void setRegularCost() {
		BigDecimal oldValue = getRegularCost();
		this.regularCost = this.getPackPrice().multiply(new BigDecimal(this.getAdjustedRegularPack()));
		this.regularCost = this.regularCost.setScale(2,RoundingMode.HALF_UP);
		this.regularCost = multiply(this.getPackPrice(), new BigDecimal(this.getAdjustedRegularPack()));
		firePropertyChange("regularCost", oldValue, getRegularCost());
		setTotalCost();
		setTotal();
	}
	
	/**
	 * Multiply rule to calculate cost based on quantity and price
	 * @param price
	 * @param quantity
	 * @return
	 */
	public BigDecimal multiply(BigDecimal price, BigDecimal quantity){
		BigDecimal res = price.multiply(quantity);
		return res.setScale(2, RoundingMode.HALF_UP);
	}
	
	
	/**
	 * @return the accelQuant
	 */
	public Integer getAccelQuant() {
		return accelQuant;
	}

	/**
	 * @param accelQuant the accelQuant to set
	 */
	public void setAccelQuant(Integer accelQuant) {
		Integer oldValue = getAccelQuant();
		this.accelQuant = accelQuant;
		firePropertyChange("accelQuant", oldValue, getAccelQuant());
		//setAdjustAccel();
	}


	/**
	 * @return the adjustAccel
	 */
	public Integer getAdjustAccel() {
		return adjustAccel;
	}

	/**
	 * Recalculate adjusted accelerated quantity
	 */
/*	private boolean setAdjustAccel() {
		Integer oldValue = getAdjustAccel();
		Date oldDate = getAccelDate();
		if(getPackPriceAccel().compareTo(BigDecimal.ZERO)==0 || getPackSizeAccel()==0){
			this.adjustAccel = 0;
		}else{
			if (this.getAccelQuant()<0){
				oldValue=0;
				this.accelQuant=0;
			}
			this.adjustAccel = calcPercents(this.getAccelQuant(), getAdjustItAccel());
			if (this.adjustAccel < 0){
				return false;
			}
		}
		firePropertyChange("adjustAccel", oldValue, getAdjustAccel());
		//setAdjustedAccelPack();
		setTotal();
		firePropertyChange("accelDate", oldDate, getAccelDate());
		return true;
	}*/

	/**
	 * @return the adjustedAccelPack
	 */
	public Integer getAdjustedAccelPack() {
		return adjustedAccelPack;
	}

	
	/**
	 * Since ver 4.1
	 * @param adjustedAccelPack
	 */
	public void setAdjustedAccelPack(Integer adjustedAccelPack) {
		Integer oldValue = getAdjustedAccelPack();
		this.adjustedAccelPack = adjustedAccelPack;
		firePropertyChange("adjustedAccelPack", oldValue, getAdjustedAccelPack());
		setAccelCost();
		setTotalPack();
	}
	

	/**
	 * @return the accelCost
	 */
	public BigDecimal getAccelCost() {
		return accelCost;
	}

	/**
	 */
	private void setAccelCost() {
		BigDecimal oldValue = getAccelCost();
		this.accelCost = this.getPackPriceAccel().multiply(new BigDecimal(this.getAdjustedAccelPack()));
		this.accelCost = this.getAccelCost().setScale(2,RoundingMode.HALF_UP);
		firePropertyChange("accelCost", oldValue, getAccelCost());
		setTotalCost();
		setTotal();
	}

	/**
	 * A little complexity took place
	 * @return the accelDate
	 */
	public Date getAccelDate() {
		if (this.accelDate == null) {
			return null;
		}
		if (this.getAdjustAccel() == 0){
			return null;
		}
		return accelDate.getTime();
	}

	/**
	 * @return the accelDate
	 */
	public Calendar getAccelCalendar() {
		return accelDate;
	}

	/**
	 * @param accelDate the accelDate to set
	 */
	public void setAccelDate(Calendar accelDate) {
		Calendar oldValue = getAccelCalendar();
		this.accelDate = accelDate;
		firePropertyChange("accelDate", oldValue, getAccelCalendar());
	}

	//
	// ----------------------------------------  TOTALS ---------------------------------------------------------------
	//
	/**
	 * Get brutto quantity, without adjustment
	 * @return
	 */
	public Integer getBruttoQuant(){
		return getRegularQuant() + getAccelQuant();
	}

	/**
	 * @return the total (means adjusted)
	 */
	public Integer getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	private void setTotal() {
		Integer oldValue = getTotal();
		Integer adjReg = 0;
		if(this.getRegularCost().compareTo(BigDecimal.ZERO) != 0){
			adjReg = this.getAdjustedRegular();
		}
		Integer adjAcc = 0;
		if(this.getAccelCost().compareTo(BigDecimal.ZERO) != 0){
			adjAcc = this.getAdjustAccel();
		}
		this.total = adjReg+adjAcc;
		firePropertyChange("total", oldValue, getTotal());
	}

	/**
	 * @return the totalPack
	 */
	public Integer getTotalPack() {
		return totalPack;
	}

	/**
	 * calculate total packs
	 */
	private void setTotalPack() {
		Integer oldValue = getTotalPack();
		this.totalPack = getAdjustedAccelPack() + getAdjustedRegularPack();
		firePropertyChange("totalPack", oldValue, getTotalPack());

	}

	/**
	 * @return the totalCost
	 */
	public BigDecimal getTotalCost() {
		return totalCost;
	}

	/**
	 * Simply recalculate total cost
	 */
	private void setTotalCost() {
		BigDecimal oldValue = getTotalCost();
		this.totalCost = this.getRegularCost().add(this.getAccelCost());
		firePropertyChange("totalCost", oldValue, getTotalCost());
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((medicine == null) ? 0 : medicine.hashCode());
		return result;
	}
	/**
	 * if Medicine equals
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
		ForecastingTotalMedicine other = (ForecastingTotalMedicine) obj;
		if (medicine == null) {
			if (other.medicine != null) {
				return false;
			}
		} else if (!medicine.equals(other.medicine)) {
			return false;
		}
		return true;
	}
	@Override
	public int compareTo(Object arg0) {
		if (arg0 == null) return -1;
		if (arg0 instanceof ForecastingTotalMedicine){
			ForecastingTotalMedicine another = (ForecastingTotalMedicine) arg0;
			return(this.getMedicine().compareTo(another.getMedicine()));
		}else{
			return -1;
		}
	}


	/**
	 * Add quantity in delivery to the Accelerated order
	 * Do not raise any event!
	 * @param delivery
	 */
	public void addAccelQuant(Integer delivery) {
		setAccelQuantFast(getAccelQuant() + delivery);
	}



	/**
	 * Add quantity in delivery to the Regular order 
	 * Do not raise any event!
	 * @param delivery
	 */
	public void addRegularQuant(Integer delivery) {
		setRegularQuantFast(getRegularQuant()+ delivery);
		
	}

	/**
	 * Set accelerated quantity, but not raise events!
	 * @param quant - quantity
	 */
	public void setAccelQuantFast(Integer quant) {
		this.accelQuant=quant;
		
	}

	/**
	 * Set regular quantity but not raise event
	 * @param quant
	 */
	public void setRegularQuantFast(int quant) {
		this.regularQuant=quant;
	}
}
