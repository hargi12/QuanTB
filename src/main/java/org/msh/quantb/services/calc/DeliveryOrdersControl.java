package org.msh.quantb.services.calc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.msh.quantb.model.gen.DeliveryScheduleEnum;
import org.msh.quantb.services.io.DeliveryOrderItemUI;
import org.msh.quantb.services.io.DeliveryOrderUI;
import org.msh.quantb.services.io.ForecastingTotal;
import org.msh.quantb.services.io.ForecastingTotalItemUIAdapter;
import org.msh.quantb.services.io.ForecastingTotalMedicine;
import org.msh.quantb.services.io.MedicineUIAdapter;
import org.msh.quantb.services.io.MonthUIAdapter;
import org.msh.quantb.services.mvp.Presenter;

/**
 * Control class to build all deliveries for medicines
 * @author Alex Kurasoff
 *
 */
public class DeliveryOrdersControl {

	private List<DeliveryOrderUI> regular = new ArrayList<DeliveryOrderUI>();
	private List<DeliveryOrderUI> accelerated = new ArrayList<DeliveryOrderUI>();
	private List<MonthUIAdapter> accDates = new ArrayList<MonthUIAdapter>();
	private List<MonthUIAdapter> regDates = new ArrayList<MonthUIAdapter>();
	private Integer leadTime;
	private ForecastingTotal regularTotal;
	private ForecastingTotal acceleratedTotal;
	private List<MedicineConsumption> consumptions = new ArrayList<MedicineConsumption>();
	private ForecastingTotal total;



	/**
	 * Forecast should be full calculated before!
	 * @param total 
	 * @param forecast
	 */
	public DeliveryOrdersControl(Integer leadTime,	ForecastingTotal regular, ForecastingTotal accelerated, ForecastingTotal total) {
		super();
		this.leadTime = leadTime;
		this.regularTotal = regular;
		this.acceleratedTotal = accelerated;
		this.total = total;
	}

	public List<MedicineConsumption> getConsumptions() {
		return consumptions;
	}
	/**
	 * Set medicines consumptions
	 * These consumptions should already contain lists with calculated deliveries
	 * @param consumptions
	 */
	public void setConsumptions(List<MedicineConsumption> consumptions) {
		this.consumptions = consumptions;
	}

	public List<DeliveryOrderUI> getRegular() {
		Collections.sort(regular);
		return regular;
	}



	public List<DeliveryOrderUI> getAccelerated() {
		Collections.sort(accelerated);
		return accelerated;
	}

	public ForecastingTotal getTotal() {
		return total;
	}

	public Integer getLeadTime() {
		return leadTime;
	}

	public ForecastingTotal getRegularTotal() {
		return regularTotal;
	}



	public ForecastingTotal getAcceleratedTotal() {
		return acceleratedTotal;
	}



	public List<MonthUIAdapter> getAccDates() {
		return accDates;
	}

	public void setAccDates(List<MonthUIAdapter> accDates) {
		this.accDates = accDates;
	}

	public List<MonthUIAdapter> getRegDates() {
		return regDates;
	}

	public void setRegDates(List<MonthUIAdapter> regDates) {
		this.regDates = regDates;
	}

	/**
	 * Build delivery orders based on consumptions, exactly by current schedule (monthly, quarterly etc)!
	 */
	public void buildAllExact(){
		getAccelerated().clear();
		getRegular().clear();
		buildDeliveryDates();
		for(MedicineConsumption mc : getConsumptions()){
			for(ConsumptionMonth cM : mc.getRegularDeliveries()){
				if(cM.hasDelivery()){
					ForecastingTotalMedicine med = getRegularTotal().fetchMedicineTotal(mc.getMed());
					List<DeliveryOrderUI> deliveries = getRegular();
					accountToDeliveries(cM, med, deliveries, true);
				}
			}
			for(ConsumptionMonth cM : mc.getAccelDeliveries()){
				if(cM.hasDelivery()){
					ForecastingTotalMedicine med = getAcceleratedTotal().fetchMedicineTotal(mc.getMed());
					List<DeliveryOrderUI> deliveries = getAccelerated();
					accountToDeliveries(cM, med, deliveries, false);
				}
			}
		}

		//calculate additional cost regular orders
		for(DeliveryOrderUI delivery: getRegular()){
			calcAddCost(delivery, getRegularTotal());
		}
		//calculate additional cost for accelerated orders
		for(DeliveryOrderUI delivery: getAccelerated()){
			calcAddCost(delivery, getAcceleratedTotal());
		}
		buildGrandTotals();

	}


	/**
	 * We have to build "exact" deliveries dates before calculations for both acc and reg order
	 */
	private void buildDeliveryDates() {
		getAccDates().clear();
		getRegDates().clear();
		for(MedicineConsumption mc : getConsumptions()){
			for(ConsumptionMonth cM : mc.getAccelDeliveries()){//mc.getCons()){
				if(cM.hasDelivery()){
					if(!getAccDates().contains(cM.getMonth())){
						getAccDates().add(cM.getMonth());
					}
				}
			}
			for(ConsumptionMonth cM : mc.getRegularDeliveries()){//mc.getCons()){
				if(cM.hasDelivery()){
					if(!getRegDates().contains(cM.getMonth())){
						getRegDates().add(cM.getMonth());
					}
				}
			}
		}
		Collections.sort(getAccDates());
		Collections.sort(getRegDates());
	}

	/**
	 * Calculate additional cost of the delivery based on additional cost items placed in the total
	 * @param delivery
	 * @param total
	 */
	private void calcAddCost(DeliveryOrderUI delivery, ForecastingTotal total) {
		BigDecimal medCost = delivery.getMedCost();
		BigDecimal addCost = BigDecimal.ZERO;
		addCost = addCost.setScale(4, RoundingMode.HALF_UP);
		for(ForecastingTotalItemUIAdapter item : total.getAddItems()){
			if(item.getFcItemObj().isIsValue()){
				addCost = addCost.add(item.getValue());
			}else{
				addCost = addCost.add(total.calcPersItem(item, medCost));
			}
		}
		delivery.setAddCost(addCost.setScale(2, RoundingMode.HALF_UP));
	}



	/**
	 * Account consumption to deliveries
	 * @param cM consumption
	 * @param medFromTotal data about medicine such are pack size, pack price etc
	 * @param deliveries regular or accelerated deliveries
	 * @param isRegular take total data from the regular order
	 */
	private void accountToDeliveries(ConsumptionMonth cM, ForecastingTotalMedicine medFromTotal,
			List<DeliveryOrderUI> deliveries, boolean isRegular) {
		if(medFromTotal!=null){
			DeliveryOrderItemUI item = new DeliveryOrderItemUI(medFromTotal, cM.getDelivery(), isRegular);
			addToDeliveries(cM.getMonth(),item, deliveries, isRegular);
		}
	}

	/**
	 * Add item to the deliveries given
	 * @param itemDeliveryMonth month of item
	 * @param item 
	 * @param deliveries list of regular or accelerated deliveries
	 * @param isRegular is it regular deliveries?
	 */
	private void addToDeliveries(MonthUIAdapter itemDeliveryMonth, DeliveryOrderItemUI item,
			List<DeliveryOrderUI> deliveries, boolean isRegular) {
		List<MonthUIAdapter> deliveryDates = fetchDeliveryDates(isRegular);
		if(true){//item.getCost().compareTo(BigDecimal.ZERO)>0){
			for(DeliveryOrderUI delivery : deliveries){
				if(inDelivery(itemDeliveryMonth, delivery, deliveryDates, isRegular)){
					delivery.addItem(item);
					return;
				}
			}
			//create the new delivery
			MonthUIAdapter deliveryMonth = calcDeliveryMonth(itemDeliveryMonth, isRegular);
			MonthUIAdapter orderDate = deliveryMonth.incrementClone(Presenter.getFactory(), getLeadTime()*-1);
			DeliveryOrderUI delivery = new DeliveryOrderUI(orderDate, deliveryMonth, getRegularTotal().getForecastUI().getFirstFCDate());
			delivery.addItem(item);
			deliveries.add(delivery);
		}
	}


	/**
	 * Calculate month to delivery. This month depends on user's choice
	 * @param itemDeliveryMonth
	 * @param isRegular is it regular delivery?
	 * @return
	 */
	private MonthUIAdapter calcDeliveryMonth(MonthUIAdapter itemDeliveryMonth, boolean isRegular) {
		DeliveryScheduleEnum sched = calcSchedule(isRegular);
		List<MonthUIAdapter> deliveryDates = fetchDeliveryDates(isRegular);
		if(sched == DeliveryScheduleEnum.EXACT){
			return itemDeliveryMonth;
		}
		if(sched == DeliveryScheduleEnum.QUARTER){
			return getQuarterBegin(itemDeliveryMonth, deliveryDates);
		}
		if(sched == DeliveryScheduleEnum.HALF){
			return getSixBegin(itemDeliveryMonth, deliveryDates);
		}
		if(sched == DeliveryScheduleEnum.YEAR){
			return getYearBegin(itemDeliveryMonth,deliveryDates);
		}
		//all at once!
		return deliveryDates.get(0).incrementClone(Presenter.getFactory(), 0);
	}

	private List<MonthUIAdapter> fetchDeliveryDates(boolean isRegular) {
		List<MonthUIAdapter> deliveryDates = null;
		if(isRegular){
			deliveryDates = getRegDates();
		}else{
			deliveryDates=getAccDates();
		}
		return deliveryDates;
	}
	/**
	 * get very first delivery in a year of the delivery 
	 * @param itemDeliveryMonth
	 * @param deliveryDates 
	 * @return 
	 */
	private MonthUIAdapter getYearBegin(MonthUIAdapter itemDeliveryMonth, List<MonthUIAdapter> deliveryDates) {
		for(MonthUIAdapter mUi : deliveryDates){
			if(mUi.getYear().intValue() == itemDeliveryMonth.getYear().intValue()){
				return mUi.incrementClone(Presenter.getFactory(), 0);
			}
		}
		return null; //impossible
	}
	/**
	 * Get half of the year begin
	 * @param itemDeliveryMonth
	 * @param deliveryDates possible delivery dates
	 * @return
	 */
	private MonthUIAdapter getSixBegin(MonthUIAdapter itemDeliveryMonth, List<MonthUIAdapter> deliveryDates) {
		if(itemDeliveryMonth.getMonth() <=5){
			return getFirstInQuarter(1,itemDeliveryMonth.getYear(), deliveryDates);
		}else{
			return getFirstInQuarter(3,itemDeliveryMonth.getYear(), deliveryDates);
		}
	}

	/**
	 * Is this item in delivery. Delivery may be exact month, quarter, half of year, year or all at once
	 * @param deliveryMonth
	 * @param delivery
	 * @param deliveryDates 
	 * @param isRegular is it regular order?
	 * @return
	 */
	private boolean inDelivery(MonthUIAdapter deliveryMonth, DeliveryOrderUI delivery, List<MonthUIAdapter> deliveryDates, boolean isRegular) {
		DeliveryScheduleEnum sched = calcSchedule(isRegular);
		if(sched == DeliveryScheduleEnum.EXACT){
			return delivery.getDeliveryDate().compareTo(deliveryMonth) == 0;
		}
		if(sched == DeliveryScheduleEnum.QUARTER){
			return delivery.getDeliveryDate().compareTo(getQuarterBegin(deliveryMonth, deliveryDates))==0;
		}
		if(sched == DeliveryScheduleEnum.HALF){
			return delivery.getDeliveryDate().compareTo(getSixBegin(deliveryMonth, deliveryDates))==0;
		}
		if(sched == DeliveryScheduleEnum.YEAR){
			return delivery.getDeliveryDate().compareTo(getYearBegin(deliveryMonth, deliveryDates))==0;
		}
		return true;  // all at once
	}

	private DeliveryScheduleEnum calcSchedule(boolean isRegular) {
		/*		DeliveryScheduleEnum sched = isRegular ? getRegularTotal().getForecastUI().getDeliverySchedule():
			getRegularTotal().getForecastUI().getAcceleratedSchedule();
		return sched;*/
		return getRegularTotal().getForecastUI().getDeliverySchedule(); // since 2016.05.16
	}


	/**
	 * Get first month of delivery for a quarter that include the deliveryMonth
	 * It should be first delivery in a quarter
	 * @param deliveryMonth
	 * @param deliveryDates 
	 * @return
	 */
	private MonthUIAdapter getQuarterBegin(MonthUIAdapter deliveryMonth, List<MonthUIAdapter> deliveryDates) {
		int month = deliveryMonth.getMonth();

		if(month>=0 && month<=2){
			return getFirstInQuarter(1, deliveryMonth.getYear(), deliveryDates);
		}
		if(month>=3 && month<=5){
			return getFirstInQuarter(2, deliveryMonth.getYear(), deliveryDates);
		}
		if(month>=6 && month<=8){
			return getFirstInQuarter(3, deliveryMonth.getYear(), deliveryDates);
		}
		return getFirstInQuarter(4, deliveryMonth.getYear(), deliveryDates);
	}

	/**
	 * Get first delivery for the quarter
	 * Assume that getDeliveryDates() is sorted ASC
	 * @param quarter
	 * @param year
	 * @param deliveryDates list of possible delivery dates
	 * @return the first delivery for quarter or null if not delivery
	 */
	private MonthUIAdapter getFirstInQuarter(int quarter, Integer year, List<MonthUIAdapter> deliveryDates) {
		int lMonth = 9;
		if(quarter == 1){
			lMonth=0;
		}
		if(quarter==2){
			lMonth=3;
		}
		if(quarter==3){
			lMonth=6;
		}
		for(MonthUIAdapter mUi : deliveryDates){
			if(mUi.getYear().intValue() == year.intValue()){
				if(mUi.getMonth().intValue()>=lMonth){
					return mUi.incrementClone(Presenter.getFactory(), 0);
				}
			}
		}
		return null;
	}
	/**
	 * Fetch delivery value for medicine and month given
	 * @param med
	 * @param month
	 * @return
	 */
	public BigDecimal fetchDelivery(MedicineUIAdapter med, MonthUIAdapter month) {
		BigDecimal ret = BigDecimal.ZERO;
		ret = searchForDelivery(getAccelerated(), med, month);
		if(ret.compareTo(BigDecimal.ZERO)==0){
			ret = searchForDelivery(getRegular(), med, month);
		}
		return ret;
	}
	/**
	 * Search for delivery medicine in month in the list of deliveries given
	 * @param deliveries list of deliveries
	 * @param med medicine
	 * @param month month
	 * @return Zero if no delivery, or delivery value
	 */
	private BigDecimal searchForDelivery(List<DeliveryOrderUI> deliveries, MedicineUIAdapter med,
			MonthUIAdapter month) {
		BigDecimal ret = BigDecimal.ZERO;
		for(DeliveryOrderUI order : deliveries){
			if(order.getDeliveryDate().compareTo(month)==0){
				for(DeliveryOrderItemUI item: order.getItems()){
					if(item.getMedicine().compareTo(med)==0){
						ret = item.getQuantity();
					}
				}
			}
		}
		return ret;
	}
	/**
	 * Build grand totals for accelerated and regular deliveries
	 */
	public void buildGrandTotals() {
		BigDecimal total = BigDecimal.ZERO;
		// recalculate summary orders
		for(ForecastingTotalMedicine totMed : getAcceleratedTotal().getMedItems()){
			reCalcTotalOrders(totMed, false);
		}
		for(ForecastingTotalMedicine totMed : getRegularTotal().getMedItems()){
			reCalcTotalOrders(totMed,true);
		}
		//accel
		for(DeliveryOrderUI dUi: getAccelerated()){
			total = total.add(dUi.getAddCost());
			total = total.add(dUi.getMedCost());
		}
		getAcceleratedTotal().setDeliveriesQuantity(getAccelerated().size());
		getAcceleratedTotal().setOrderGrandTotal(total);
		//regular
		total = BigDecimal.ZERO;
		for(DeliveryOrderUI dUi: getRegular()){
			for(DeliveryOrderItemUI item: dUi.getItems()){
				Integer toAdd = new Integer(item.getPacks().intValue());
				//item.getMedFromTotal().addAdjustedRegularPackFromDelivery(toAdd);
			}
			total = total.add(dUi.getAddCost());
			total = total.add(dUi.getMedCost());
		}
		getRegularTotal().setDeliveriesQuantity(getRegular().size());
		getRegularTotal().setOrderGrandTotal(total);
/*		for(ForecastingTotalMedicine totMed : getRegularTotal().getMedItems()){
			totMed.setAdjustedRegularPack(totMed.getAdjustedRegularPackFromDelivery());
		}*/
		//all orders
		total = getAcceleratedTotal().getOrderGrandTotal();
		total = total.add(getRegularTotal().getOrderGrandTotal());
		if(getTotal() != null){
			getTotal().setOrderGrandTotal(total);
		}

	}
	/**
	 * Calculate total packs in all deliveries for medicine given
	 * @param totMed medicine given
	 * @param isRegular
	 */
	private void reCalcTotalOrders(ForecastingTotalMedicine totMed, boolean isRegular) {
		Integer packs = 0;
		BigDecimal quantity = BigDecimal.ZERO;
		List<DeliveryOrderUI> orders = null;
		if(isRegular){
			orders = getRegular();
		}else{
			orders = getAccelerated();
		}
		if(orders != null){
			for(DeliveryOrderUI deliv :orders){
				for(DeliveryOrderItemUI item : deliv.getItems()){
					if(item.getMedicine().compareTo(totMed.getMedicine())==0){
						packs = packs+item.getPacks().intValue();
						quantity = quantity.add(item.getQuantity());
					}
				}
			}
		}
		if(isRegular){
			//totMed.setRegularQuantFast(quantity.intValue());
			totMed.setAdjustedRegularPack(packs);
			totMed.setAdjustedRegular(quantity.intValue());
		}else{
			//TODO totMed.setAccelQuantFast(quantity.intValue());
			totMed.setAdjustedAccelPack(packs);
			totMed.setAdjustAccel(quantity.intValue());
		}
	}

}
