package org.msh.quantb.services.io;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * This class represents one item in delivery
 * Medicine - quantity - cost
 * @author Alex Kurasoff
 *
 */
public class DeliveryOrderItemUI extends AbstractUIAdapter {
	
	private ForecastingTotalMedicine medFromTotal;
	private BigDecimal quantity;
	private boolean regular;

	/**
	 * Create a delivery item
	 * @param medFromTotal Reference data for packs and cost calculations
	 * @param quantity quantity in units, not in packs!
	 * @param _isRegular take pack size, pack price etc from the regular order data, otherwise - accelerated
	 */
	public DeliveryOrderItemUI(ForecastingTotalMedicine medFromTotal, BigDecimal quantity, boolean _isRegular) {
		super();
		this.medFromTotal = medFromTotal;
		this.quantity = quantity;
		this.regular = _isRegular;
	}
	
	/**
	 * Reference data for packs and cost calculations
	 * @return
	 */
	public ForecastingTotalMedicine getMedFromTotal() {
		return medFromTotal;
	}

	public void setMedFromTotal(ForecastingTotalMedicine medFromTotal) {
		ForecastingTotalMedicine oldValue = getMedFromTotal();
		this.medFromTotal = medFromTotal;
		firePropertyChange("medFromTotal", oldValue, getMedFromTotal());
	}
	
	
	public boolean isRegular() {
		return regular;
	}

	/**
	 * <b>Before 20170601</b> - Quantity in units. It is an original quantity! Not adjusted! not rounded to pack size<br>
	 * <b>After 20170601</b> - Adjusted quantity rounded to pack size
	 * @return
	 */
	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		BigDecimal oldValue = getQuantity();
		this.quantity = quantity;
		firePropertyChange("quantity", oldValue, getQuantity());
	}

	public MedicineUIAdapter getMedicine() {
		return getMedFromTotal().getMedicine();
	}

	public BigDecimal getPacks() {
		return calculatePacks();
	}
	/**
	 * We should calculate packs quantity based on the units quantity and reference data
	 * @return
	 */
	private BigDecimal calculatePacks() {
		Integer packSize = getMedFromTotal().getPackSize();
		if (!isRegular()){
			packSize = getMedFromTotal().getPackSizeAccel();
		}
		Integer adjPacks = getMedFromTotal().divide(calcAdjQuantity(), packSize);
		if(adjPacks>=0){
			return new BigDecimal(adjPacks);
		}else{
			return null; //overflow, it's impossible, but...
		}
	}

	public BigDecimal getCost() {
		return calcDeliveryCost();
	}
	/**
	 * Calculate cost of delivery
	 * @return cost
	 */
	private BigDecimal calcDeliveryCost() {
		BigDecimal packPrice = getMedFromTotal().getPackPrice();
		if(!isRegular()){
			packPrice = getMedFromTotal().getPackPriceAccel();
		}
		return getMedFromTotal().multiply(packPrice, getPacks());
	}

	public Integer getPackSize() {
		return getMedFromTotal().getPackSize();
	}
	/**
	 * Get adjusted quantity, but not rounded to pack size
	 * @return
	 */
	public BigDecimal getUnits(){
		return new BigDecimal(calcAdjQuantity());
	}
	
	/**
	 * From 20170601 quantity is already adjusted quantity rounded to packs
	 * @return
	 */
	private Integer calcAdjQuantity(){
		return getQuantity().intValue();
/*		BigDecimal adj = getMedFromTotal().getAdjustIt();
		if(!isRegular()){
			adj = getMedFromTotal().getAdjustItAccel();
		}
		return getMedFromTotal().calcPercents(getQuantity().intValue(), adj);*/
	}

	@Override
	public String toString() {
		return "DeliveryOrderItemUI [getMedicine()=" + getMedicine() + ", calcAdjQuantity()=" + calcAdjQuantity() + "]";
	}



	
}
