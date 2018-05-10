package org.msh.quantb.services.calc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.msh.quantb.services.io.ForecastingResultUIAdapter;
import org.msh.quantb.services.io.MedicineUIAdapter;

/**
 * Medicine consumption for report
 * @author alexey
 *
 */
public class MedicineConsumption implements Comparable {
	private MedicineUIAdapter med;
	private List<ConsumptionMonth> cons = new ArrayList<ConsumptionMonth>();
	//exact consumption last month of lead time
	private ConsumptionMonth exactLastLead;
	//exact consumption of first month of forecasting period
	private ConsumptionMonth exactFirstForecast;
	private List<ConsumptionMonth> accelDeliveries;
	private List<ConsumptionMonth> regularDeliveries;

	/**
	 * Only valid constructor
	 * @param medicine
	 */
	public MedicineConsumption(MedicineUIAdapter medicine) {
		med = medicine;
	}
	/**
	 * @return the med
	 */
	public MedicineUIAdapter getMed() {
		return med;
	}
	/**
	 * @return the cons
	 */
	public List<ConsumptionMonth> getCons() {
		Collections.sort(cons);
		return cons;
	}
	
	
	
	public ConsumptionMonth getExactLastLead() {
		return exactLastLead;
	}
	public void setExactLastLead(ConsumptionMonth exactLastLead) {
		this.exactLastLead = exactLastLead;
	}
	public ConsumptionMonth getExactFirstForecast() {
		return exactFirstForecast;
	}
	public void setExactFirstForecast(ConsumptionMonth exactFirstForecast) {
		this.exactFirstForecast = exactFirstForecast;
	}
	/**
	 * account daily result to medicine consumption 
	 * @param fr
	 */
	public void account(ForecastingResultUIAdapter fr) {
		ConsumptionMonth tmp = null;
		boolean isNew = true;
		if (this.getCons().size() == 0){
			tmp = new ConsumptionMonth(fr.getMonth());
			tmp.setOnHand(fr.getAllAvailable().subtract(new BigDecimal(fr.getInOrders()))); //begin of period orders not accounted
		}else{
			tmp = this.getCons().get(cons.size()-1);
			isNew = false;
		}
		if (tmp.getMonth().compareTo(fr.getMonth()) < 0){
			isNew = true;
			tmp = new ConsumptionMonth(fr.getMonth());
			tmp.setOnHand(fr.getAllAvailable().subtract(new BigDecimal(fr.getInOrders()))); //begin of period, orders not accounted
		}
		//daily parameters
		tmp.addConsNew(fr.getConsNew());
		tmp.addConsOld(fr.getConsOld());
		tmp.addOrder(fr.getInOrders());
		if (fr.getExpired() > 0){
			tmp.addExpired(fr.getExpired());
		}
		if (fr.getOrderExpired()>0){
			tmp.addOrderExpired(fr.getOrderExpired());
		}
		tmp.addMissing(fr.getMissing());
		// determine max cases quantity 
		if (tmp.getOldCases().compareTo(fr.getOldCases())<0){
			tmp.setOldCases(fr.getOldCases());
		}
		if (tmp.getNewCases().compareTo(fr.getNewCases())<0){
			tmp.setNewCases(fr.getNewCases());
		}
		if (isNew){
/*			if (fr.getConsNew().compareTo(BigDecimal.ZERO) > 0 && tmp.getNewCases().compareTo(BigDecimal.ZERO) == 0){
				tmp.setNewCases(fr.getNewCases());
			}
			if(fr.getConsOld().compareTo(BigDecimal.ZERO)>0 && tmp.getOldCases().compareTo(BigDecimal.ZERO) == 0){
				tmp.setOldCases(fr.getOldCases());
			}*/
			//add new to list

			//System.out.println(med +" " + tmp.getMonth() + " old "+ tmp.getOldCases() +" new " + tmp.getNewCases());
			this.getCons().add(tmp);
		}

	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cons == null) ? 0 : cons.hashCode());
		result = prime * result + ((med == null) ? 0 : med.hashCode());
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
		MedicineConsumption other = (MedicineConsumption) obj;
		if (cons == null) {
			if (other.cons != null) {
				return false;
			}
		} else if (!cons.equals(other.cons)) {
			return false;
		}
		if (med == null) {
			if (other.med != null) {
				return false;
			}
		} else if (!med.equals(other.med)) {
			return false;
		}
		return true;
	}
	@Override
	public int compareTo(Object o) {
		if (this.equals(o)) return 0;
		if (o instanceof MedicineConsumption){
			return med.compareTo(((MedicineConsumption) o).getMed());
		}else{
			return 1;
		}
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MedicineConsumption [med=" + med + ", cons=" + cons + "]";
	}
	
	public Integer getConsInt() {
		return null;
	}

	/**
	 * Consumptions for accelerated order deliveries
	 * @param cons
	 */
	public void setAccelDeliveries(List<ConsumptionMonth> cons) {
		this.accelDeliveries=cons;
	}
	/**
	 * Consumptions for accelerated order deliveries
	 * @param accCons
	 */
	public List<ConsumptionMonth> getAccelDeliveries() {
		return accelDeliveries;
	}
	/**
	 * Consumptions for regular order deliveries
	 * @param cons
	 */
	public void setRegularDeliveries(List<ConsumptionMonth> cons) {
		this.regularDeliveries=cons;
	}
	public List<ConsumptionMonth> getRegularDeliveries() {
		return regularDeliveries;
	}
	/**
	 * return total quantity of all accelerated ideal deliveries
	 * @return
	 */
	public Integer getAccelDeliveriesTotal() {
		Integer ret = 0;
		for(ConsumptionMonth cM : getAccelDeliveries()){
			ret = ret + cM.getDelivery().intValue();
		}
		return ret;
	}
	/**
	 * Return total quantity of all regular ideal deliveries
	 * @return
	 */
	public Integer getRegularDeliveriesTotal() {
		Integer ret = 0;
		for(ConsumptionMonth cM : getRegularDeliveries()){
			ret = ret + cM.getDelivery().intValue();
		}
		return ret;
	}
	/**
	 * Clean order related values
	 */
	public void cleanMinMaxDeliv() {
		for(ConsumptionMonth cons : getCons()){
			cons.cleanMinMaxDeliv();
		}
		
	}
	/**
	 * Return total packs of accelerated deliveries
	 * @return
	 */
	public Integer getAccelPacksTotal() {
		Integer ret = 0;
		for(ConsumptionMonth cM : getAccelDeliveries()){
			ret = ret + cM.getPacks().intValue();
		}
		return ret;
	}
	/**
	 * Return total packs of regular deliveries
	 * @return
	 */
	public Integer getRegularPacksTotal() {
		Integer ret = 0;
		for(ConsumptionMonth cM : getRegularDeliveries()){
			ret = ret + cM.getPacks().intValue();
		}
		return ret;
	}
	/**
	 * Calculate "Quantity need" for accelerated order
	 * @return
	 */
	public Integer getAccelNeedTotal() {
		Integer ret = 0;
		for(ConsumptionMonth cM : getAccelDeliveries()){
			ret = ret + cM.getIdealDelivery().intValue();
		}
		return ret;
	}

	/**
	 * Calculate "Quantity need" for Regular order
	 * @return
	 */
	public Integer getRegularNeedTotal() {
		Integer ret = 0;
		for(ConsumptionMonth cM : getRegularDeliveries()){
			ret = ret + cM.getIdealDelivery().intValue();
		}
		return ret;
	}
	
}
