package org.msh.quantb.services.calc;

import java.math.BigDecimal;
import java.util.List;

import org.msh.quantb.services.io.MonthUIAdapter;

/**
 * This class is responsible for calculate delivery schedule for a medicine for optimistic calculation
 * @author Alex Kurasoff
 *@deprecated
 */
public class DeliveryCalculator implements DeliveryCalculatorI {
	/**
	 * Forecasting period begin
	 */
	private MonthUIAdapter begin;
	private ConsumptionMonth prevCons = null;
	private List<ConsumptionMonth> consumptions;
	private BigDecimal orderQuantity;


	public DeliveryCalculator(MonthUIAdapter _begin, List<ConsumptionMonth> _consumptions){
		this.begin = _begin;
		this.consumptions = _consumptions;

	}



	/* (non-Javadoc)
	 * @see org.msh.quantb.services.calc.DeliveryCalculatorI#getBegin()
	 */
	@Override
	public MonthUIAdapter getBegin() {
		return begin;
	}



	/* (non-Javadoc)
	 * @see org.msh.quantb.services.calc.DeliveryCalculatorI#setBegin(org.msh.quantb.services.io.MonthUIAdapter)
	 */
	@Override
	public void setBegin(MonthUIAdapter begin) {
		this.begin = begin;
	}



	/* (non-Javadoc)
	 * @see org.msh.quantb.services.calc.DeliveryCalculatorI#getPrevCons()
	 */
	@Override
	public ConsumptionMonth getPrevCons() {
		return prevCons;
	}



	/* (non-Javadoc)
	 * @see org.msh.quantb.services.calc.DeliveryCalculatorI#setPrevCons(org.msh.quantb.services.calc.ConsumptionMonth)
	 */
	@Override
	public void setPrevCons(ConsumptionMonth prevCons) {
		this.prevCons = prevCons;
	}



	/* (non-Javadoc)
	 * @see org.msh.quantb.services.calc.DeliveryCalculatorI#getConsumptions()
	 */
	@Override
	public List<ConsumptionMonth> getConsumptions() {
		return consumptions;
	}



	/* (non-Javadoc)
	 * @see org.msh.quantb.services.calc.DeliveryCalculatorI#setConsumptions(java.util.List)
	 */
	@Override
	public void setConsumptions(List<ConsumptionMonth> consumptions) {
		this.consumptions = consumptions;
	}



	/* (non-Javadoc)
	 * @see org.msh.quantb.services.calc.DeliveryCalculatorI#getOrderQuantity()
	 */
	@Override
	public BigDecimal getOrderQuantity() {
		return orderQuantity;
	}



	/* (non-Javadoc)
	 * @see org.msh.quantb.services.calc.DeliveryCalculatorI#setOrderQuantity(java.math.BigDecimal)
	 */
	@Override
	public void setOrderQuantity(BigDecimal orderQuantity) {
		this.orderQuantity = orderQuantity;
	}

	/**
	 * Account next month consumption
	 * @param monthCons
	 */
	private void account(ConsumptionMonth monthCons){
		if(monthCons.getMonth().compareTo(getBegin())>=0){
			if(deliveryCondition(monthCons)){
				monthCons.setDelivery(calcDelivery(monthCons));
			}
			calcPStock(monthCons);
		}
	}


	/**
	 * Calculate projected incoming stock, should be called only in ConsumptionMonth iteration
	 * @param monthCons
	 */
	private void calcPStock(ConsumptionMonth monthCons) {
		monthCons.setpStock(calculateIncoming(monthCons));
		setPrevCons(monthCons);
	}

	/**
	 * calculate delivery quantity for old style delivery
	 * @param monthCons 
	 * @return
	 */
	private BigDecimal calcDelivery(ConsumptionMonth monthCons) {
		BigDecimal ret = BigDecimal.ZERO;
		if(getOrderQuantity().compareTo(monthCons.getMaxMissing())>0){
			ret = monthCons.getMaxMissing();	
		}else{
			ret = getOrderQuantity();
		}
		BigDecimal check = calculateIncoming(monthCons).add(ret).subtract(monthCons.getMaxStock());
		if(check.compareTo(BigDecimal.ZERO)>0){ //sometimes it happiness, I do not know why
			ret = ret.subtract(check);
		}
		setOrderQuantity(getOrderQuantity().subtract(ret));
		return ret;
	}



	/**
	 * Does delivery condition occur for old style delivery?
	 * @param monthCons
	 * @return
	 */
	private boolean deliveryCondition(ConsumptionMonth monthCons) {
		BigDecimal incomStock = calculateIncoming(monthCons);
		/*		return (
				incomStock.compareTo(monthCons.getMinStock())<=0 || 	//projected incoming less then minimum
				monthCons.getMissing().compareTo(BigDecimal.ZERO)>0		//this month has missing, delivery is necessary
				)
				&& monthCons.getMinMissing().compareTo(BigDecimal.ZERO)>=0 //missing will be in minimum stock months
				&& getOrderQuantity().compareTo(BigDecimal.ZERO)>0;		   //we can't make delivery if total needs exceed
		 */	
		boolean minStockCond = incomStock.compareTo(monthCons.getMinStock())<=0 && getOrderQuantity().compareTo(BigDecimal.ZERO)>0;
		boolean missingCond = monthCons.getMinStock().compareTo(BigDecimal.ZERO)==0 && monthCons.getMissing().compareTo(BigDecimal.ZERO)>0;
		return minStockCond || missingCond;
	}


	/**
	 * Calculate forecast for incoming stock for this month 
	 * @param monthCons
	 * @return
	 */
	private BigDecimal calculateIncoming(ConsumptionMonth monthCons) {
		if(getPrevCons() == null){
			return monthCons.getOnHand().add(monthCons.getDelivery());
		}else{ 
/*			BigDecimal delta = monthCons.getOnHand().subtract(getPrevCons().getOnHand()).subtract(getPrevCons().getMissing()).add(getPrevCons().getConsAll());
			return getPrevCons().getpStock().subtract(getPrevCons().getConsAll()).add(monthCons.getDelivery()).add(delta);*/
			BigDecimal ret = getPrevCons().getpStock()
					.subtract(getPrevCons().getConsAll())
					.subtract(new BigDecimal(getPrevCons().getExpired()))
					.add(new BigDecimal(getPrevCons().getOrder()));
			if(ret.compareTo(BigDecimal.ZERO)>0){
				return ret.add(monthCons.getDelivery());
			}else{
				return monthCons.getDelivery();
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see org.msh.quantb.services.calc.DeliveryCalculatorI#exec()
	 */
	@Override
	public void exec() {
		setOrderQuantity(BigDecimal.ZERO);
		for(ConsumptionMonth cons : getConsumptions()){
			if(cons.getMonth().compareTo(getBegin())>=0){
				setOrderQuantity(getOrderQuantity().add(cons.getMissing()));
			}
		}
		for(ConsumptionMonth cons : getConsumptions()){
			account(cons);
		}
	}



	@Override
	public void recalcPStocks(List<ConsumptionMonth> consumptionSet) {
		setPrevCons(null);
		for(ConsumptionMonth cM :consumptionSet){
			calcPStock(cM);
		}

	}

}
