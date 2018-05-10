package org.msh.quantb.services.calc;

import java.math.BigDecimal;

import org.msh.quantb.services.io.MonthUIAdapter;

/**
 * Single line in medicine consumption report
 * @author alexey
 *
 */
public class ConsumptionMonth implements Comparable {
	private MonthUIAdapter month;
	private BigDecimal onHand = BigDecimal.ZERO;
	private BigDecimal missing = BigDecimal.ZERO;
	private int expired=0;
	private int order=0;
	private BigDecimal consOld =BigDecimal.ZERO;
	private BigDecimal consNew =BigDecimal.ZERO;
	private BigDecimal newCases= new BigDecimal(0.00);
	private BigDecimal oldCases= new BigDecimal(0.00);
	private int orderExpired=0;
	/////////// DELIVERY ACCOUNT VARIABLES SEE DeliveryCalculator ///////////////////////
	
	//// First is for old style calculation, we will keep them for some reason
	//consumption in a future - min stock months and max stock months
	private BigDecimal minStock = BigDecimal.ZERO;
	private BigDecimal maxStock = BigDecimal.ZERO;
	//missing in a future - min stock months and max stock months
	private BigDecimal maxMissing = BigDecimal.ZERO;
	private BigDecimal minMissing = BigDecimal.ZERO;
	//projected stock on hand - delivery had accounted
	private BigDecimal pStock = BigDecimal.ZERO;
	//real delivery this month. The "whole pack" constraint is applied
	private BigDecimal delivery = BigDecimal.ZERO;
	//ideal delivery this month. No "whole pack" constraint
	private BigDecimal idealDelivery = BigDecimal.ZERO;
	// packs in real delivery
	private BigDecimal packs=BigDecimal.ZERO;
	
	//// Second is for new style calculations
	private BigDecimal minFullStock = BigDecimal.ZERO;
	private BigDecimal maxFullStock = BigDecimal.ZERO;

	
	/**
	 * Constructor based only on month
	 * Creates empty cons based on month Future sets are required
	 * @param _month
	 */
	public ConsumptionMonth(MonthUIAdapter _month){
		this.month = _month;
	}
	
	/**
	 * This constructor will create consumption with data based on period data
	 * This period should be exact a month
	 * WARNING it is impossible to get cases quantities from period, will be set to ZERO
	 * @param period
	 * @param month 
	 */
	public ConsumptionMonth(PeriodResume period, MonthUIAdapter month) {
		setConsNew(period.getConsumedNew());
		setConsOld(period.getConsumedOld());
		setExpired(period.getExpired());
		setMissing(period.getMissing());
		setMonth(month);
		setNewCases(BigDecimal.ZERO);
		setOldCases(BigDecimal.ZERO);
		setOnHand(new BigDecimal(period.getIncomingBalance()));
		setOrder(period.getTransit());
	}





	public BigDecimal getDelivery() {
		return delivery;
	}





	public void setDelivery(BigDecimal delivery) {
		this.delivery = delivery;
	}





	public BigDecimal getIdealDelivery() {
		return idealDelivery;
	}

	public void setIdealDelivery(BigDecimal idealDelivery) {
		this.idealDelivery = idealDelivery;
	}

	public BigDecimal getpStock() {
		return pStock;
	}


	public void setpStock(BigDecimal pStock) {
		this.pStock = pStock;
	}





	public BigDecimal getMaxMissing() {
		return maxMissing;
	}





	public void setMaxMissing(BigDecimal maxMissing) {
		this.maxMissing = maxMissing;
	}





	public BigDecimal getMinMissing() {
		return minMissing;
	}





	public void setMinMissing(BigDecimal minMissing) {
		this.minMissing = minMissing;
	}





	public BigDecimal getMinStock() {
		return minStock;
	}



	public void setMinStock(BigDecimal minStock) {
		this.minStock = minStock;
	}



	public BigDecimal getMaxStock() {
		return maxStock;
	}



	public void setMaxStock(BigDecimal maxStock) {
		this.maxStock = maxStock;
	}



	public BigDecimal getMinFullStock() {
		return minFullStock;
	}

	public void setMinFullStock(BigDecimal minProjStock) {
		this.minFullStock = minProjStock;
	}

	public BigDecimal getMaxFullStock() {
		return maxFullStock;
	}

	public void setMaxFullStock(BigDecimal maxProjStock) {
		this.maxFullStock = maxProjStock;
	}

	public void setMissing(BigDecimal missing) {
		this.missing = missing;
	}



	public void setExpired(int expired) {
		this.expired = expired;
	}



	public void setOrder(int order) {
		this.order = order;
	}



	public void setConsOld(BigDecimal consOld) {
		this.consOld = consOld;
	}



	public void setConsNew(BigDecimal consNew) {
		this.consNew = consNew;
	}



	public void setOrderExpired(int orderExpired) {
		this.orderExpired = orderExpired;
	}



	/**
	 * @return the newCases
	 */
	public BigDecimal getNewCases() {
		return newCases;
	}

	/**
	 * @param bigDecimal the newCases to set
	 */
	public void setNewCases(BigDecimal bigDecimal) {
		this.newCases = bigDecimal;
	}

	/**
	 * @return the oldCases
	 */
	public BigDecimal getOldCases() {
		return oldCases;
	}

	/**
	 * @param oldCases the oldCases to set
	 */
	public void setOldCases(BigDecimal oldCases) {
		this.oldCases = oldCases;
	}

	/**
	 * @return the month
	 */
	public MonthUIAdapter getMonth() {
		return month;
	}

	/**
	 * @param month the month to set
	 */
	public void setMonth(MonthUIAdapter month) {
		this.month = month;
	}

	/**
	 * @return the onHand
	 */
	public BigDecimal getOnHand() {
		return onHand;
	}


	/**
	 * @param onHand the onHand to set
	 */
	public void setOnHand(BigDecimal onHand) {
		this.onHand = onHand;
	}

	/**
	 * @return the missing
	 */
	public BigDecimal getMissing() {
		return missing;
	}

	/**
	 * @param missing the missing to add
	 */
	public void addMissing(BigDecimal value) {
		this.missing = missing.add(value);
	}

	/**
	 * @return the expired
	 */
	public int getExpired() {
		return expired;
	}
	
	/**
	 * @param expired the expired to add
	 */
	public void addExpired(int value) {
		this.expired = expired + value;
	}

	/**
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}
	

	/**
	 * @param order the order to add
	 */
	public void addOrder(int value) {
		this.order = order + value;
	}

	/**
	 * @return the consOld
	 */
	public BigDecimal getConsOld() {
		return consOld;
	}
	/**
	 * add to consumption old cases
	 * @param value
	 */
	public void addConsOld(BigDecimal value){
		consOld = consOld.add(value);
	}

	/**
	 * @return the consNew
	 */
	public BigDecimal getConsNew() {
		return consNew;
	}
	
	public void addConsNew(BigDecimal value){
		consNew = consNew.add(value);
	}

	@Override
	public int compareTo(Object o) {
		if (o == null) return 1;
		if (o instanceof ConsumptionMonth){
			return this.getMonth().compareTo(((ConsumptionMonth) o).getMonth());
		}else{
			return 1;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((month == null) ? 0 : month.hashCode());
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
		ConsumptionMonth other = (ConsumptionMonth) obj;
		if (month == null) {
			if (other.month != null) {
				return false;
			}
		} else if (!month.equals(other.month)) {
			return false;
		}
		return true;
	}
	/**
	 * get all consumption
	 * @return
	 */
	public BigDecimal getConsAll() {
		return this.getConsNew().add(this.getConsOld());
	}


	@Override
	public String toString() {
		return month + ", onHand=" + onHand + ", missing=" + missing + ", minStock="
				+ minStock + ", maxStock=" + maxStock + ", maxMissing=" + maxMissing + ", minMissing=" + minMissing
				+ ", pStock=" + pStock + ", delivery=" + delivery;
	}





	/**
	 * Get right integer onHand
	 * @return
	 */
	public int getOnHandInt() {
		return getOnHand().setScale(0, BigDecimal.ROUND_UP).intValueExact();
	}

	public int getConsAllInt() {
		return getConsAll().setScale(0, BigDecimal.ROUND_UP).intValueExact();
	}

	public int getMissingInt() {
		return getMissing().setScale(0, BigDecimal.ROUND_UP).intValueExact();
	}

	public Integer getConsOldInt() {
		return getConsOld().setScale(0, BigDecimal.ROUND_UP).intValueExact();
	}

	public Integer getConsNewInt() {
		return getConsNew().setScale(0, BigDecimal.ROUND_UP).intValueExact();
	}
	/**
	 * Expired in order batches
	 * @param orderExpired
	 */
	public void addOrderExpired(Integer orderExpired) {
		this.orderExpired +=orderExpired;
		
	}

	/**
	 * @return the orderExpired
	 */
	public int getOrderExpired() {
		return orderExpired;
	}


	/**
	 * add to min stock
	 * @param value
	 */
	public void addMinStock(BigDecimal value) {
		setMinStock(minStock.add(value));	
	}

	/**
	 * Add to max stock
	 * @param value
	 */
	public void addMaxStock(BigDecimal value) {
		setMaxStock(maxStock.add(value));
		
	}
	
	/**
	 * add to projected min stock
	 * @param value
	 */
	public void addProjMinStock(BigDecimal value) {
		setMinFullStock(minFullStock.add(value));	
	}

	/**
	 * Add to projected max stock
	 * @param value
	 */
	public void addProjMaxStock(BigDecimal value) {
		setMaxFullStock(maxFullStock.add(value));
		
	}
	
	
	/**
	 * add to min missing
	 * @param value
	 */
	public void addMinMissing(BigDecimal value) {
		setMinMissing(minMissing.add(value));	
	}

	/**
	 * Add to max missing
	 * @param value
	 */
	public void addMaxMissing(BigDecimal value) {
		setMaxMissing(maxMissing.add(value));
		
	}

	/**
	 * Has delivery in this month
	 * @return
	 */
	public boolean hasDelivery() {
		return getDelivery().compareTo(BigDecimal.ZERO)>0;
	}
	
	
	
	public BigDecimal getPacks() {
		return packs;
	}

	public void setPacks(BigDecimal packs) {
		this.packs = packs;
	}

	/**
	 * Create a clone of this object
	 * @return
	 */
	public ConsumptionMonth getClone() {
		ConsumptionMonth ret = new ConsumptionMonth(this.getMonth());
		ret.setConsNew(this.getConsNew());
		ret.setConsOld(this.getConsOld());
		ret.setDelivery(this.getDelivery());
		ret.setIdealDelivery(this.getIdealDelivery());
		ret.setExpired(this.getExpired());
		ret.setMaxFullStock(this.getMaxFullStock());
		ret.setMaxMissing(this.getMaxMissing());
		ret.setMaxStock(this.getMaxStock());
		ret.setMinFullStock(this.getMinFullStock());
		ret.setMinMissing(this.getMinMissing());
		ret.setMinStock(this.getMinStock());
		ret.setMissing(this.getMissing());
		ret.setNewCases(this.getNewCases());
		ret.setOldCases(this.getOldCases());
		ret.setOnHand(this.getOnHand());
		ret.setOrder(this.getOrder());
		ret.setOrderExpired(this.getOrderExpired());
		ret.setpStock(this.getpStock());
		ret.setPacks(this.getPacks());
		return ret;
	}
	/**
	 * Clean min max and delivery values
	 */
	public void cleanMinMaxDeliv() {
	//// First is for old style calculation, we will keep them for some reason
		//consumption in a future - min stock months and max stock months
		minStock = BigDecimal.ZERO;
		maxStock = BigDecimal.ZERO;
		//missing in a future - min stock months and max stock months
		maxMissing = BigDecimal.ZERO;
		minMissing = BigDecimal.ZERO;
		//projected stock on hand - delivery had accounted
		pStock = BigDecimal.ZERO;
		//delivery this month
		delivery = BigDecimal.ZERO;
		//projected min amd max, only for charting and early warning!
		
		//// Second is for new style calculations
		minFullStock = BigDecimal.ZERO;
		maxFullStock = BigDecimal.ZERO;
		
	}
	
	
}
