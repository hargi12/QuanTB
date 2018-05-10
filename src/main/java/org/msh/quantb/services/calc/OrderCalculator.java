package org.msh.quantb.services.calc;

import java.util.ArrayList;
import java.util.List;

import org.msh.quantb.model.forecast.PricePack;
import org.msh.quantb.model.gen.DeliveryScheduleEnum;
import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.ForecastingMedicineUIAdapter;
import org.msh.quantb.services.io.ForecastingTotal;
import org.msh.quantb.services.io.ForecastingTotalMedicine;
import org.msh.quantb.services.io.MedicineUIAdapter;
import org.msh.quantb.services.mvp.Presenter;

/**
 * This class is responsible to calculate, recalculate and store orders
 * Consume forecasting calculation result
 * Control order panel and order graph
 * @author Alex Kurasoff
 *
 */
public class OrderCalculator {

	private ForecastingCalculation forecastCalculator=null;
	private ForecastingTotal total;
	private ForecastingTotal totalR;
	private ForecastingTotal totalA;
	private DeliveryOrdersControl control;
	private LogisticCalculatorI logisticCalculator;
	private List<ForecastingTotalMedicine> medicineTotals = new ArrayList<ForecastingTotalMedicine>();
	/**
	 * Constructor
	 */
	public OrderCalculator(ForecastingCalculation fCalc){
		this.forecastCalculator = fCalc;
	}

	public ForecastUIAdapter getForecast() {
		return getForecastCalculator().getForecastUI();
	}

	public ForecastingCalculation getForecastCalculator() {
		return forecastCalculator;
	}

	public void setForecastCalculator(ForecastingCalculation forecastCalculator) {
		this.forecastCalculator = forecastCalculator;
	}

	public ForecastingTotal getTotal() {
		return total;
	}

	public void setTotal(ForecastingTotal total) {
		this.total = total;
	}

	public ForecastingTotal getTotalR() {
		return totalR;
	}

	public void setTotalR(ForecastingTotal totalR) {
		this.totalR = totalR;
	}

	public ForecastingTotal getTotalA() {
		return totalA;
	}

	public void setTotalA(ForecastingTotal totalA) {
		this.totalA = totalA;
	}

	public DeliveryOrdersControl getControl() {
		return control;
	}

	public void setControl(DeliveryOrdersControl control) {
		this.control = control;
	}

	public LogisticCalculatorI getLogisticCalculator() {
		return logisticCalculator;
	}

	public void setLogisticCalculator(LogisticCalculatorI logisticCalculator) {
		this.logisticCalculator = logisticCalculator;
	}

	public List<ForecastingTotalMedicine> getMedicineTotals() {
		return medicineTotals;
	}

	public void setMedicineTotals(List<ForecastingTotalMedicine> medicineTotals) {
		this.medicineTotals = medicineTotals;
	}

	/**
	 * Execute the calculator first time for the actual forecasting results
	 */
	public void execute(){
		//calculate deliveries
		logisticCalculator = LogisticCalculatorsFactory.getLolgisticCalculator(getForecast());
		logisticCalculator.exec(getForecastCalculator().getMedicineConsumption());
		//calculate totals for each medicine
		calcMedTotals();
		//collect totals for each medicine to Regular, Accelerated and Total orders
		calcOrders();
	}
	/**
	 * RE- execute calculation for a medicine given
	 * @param medTot
	 */
	public void reExecuteForMedicine(ForecastingTotalMedicine medTot){
		MedicineConsumption cons = null;
		//find it!
		for(MedicineConsumption mCons : getForecastCalculator().getMedicineConsumption()){
			if(mCons.getMed().compareTo(medTot.getMedicine())==0){
				cons=mCons;
			}
		}
		if(cons != null){
			List<MedicineConsumption> consList = new ArrayList<MedicineConsumption>();
			ForecastingMedicineUIAdapter med = getForecast().getMedicine(medTot.getMedicine());
			if(med != null){
				//make changes to ForecastingMedicine's PricePack (order's parameters) before calculations
				PricePack order = med.getFcMedicineObj().getPackOrder();
				order.setAdjust(medTot.getAdjustIt());
				order.setAdjustAccel(medTot.getAdjustItAccel());
				order.setPack(medTot.getPackSize());
				order.setPackAccel(medTot.getPackSizeAccel());
				order.setPackPrice(medTot.getPackPrice());
				order.setPackPriceAccel(medTot.getPackPriceAccel());
				consList.add(cons);
				logisticCalculator = LogisticCalculatorsFactory.getLolgisticCalculator(getForecast());
				logisticCalculator.exec(consList);
				//put results to existed med totals
				recalcQuantityAndCost(cons, medTot);
				calcOrders();
				//make changes to quantity and cost only for this medicine
			}
		}
	}

	/**
	 * Calculate orders based on medicine totals
	 */
	public void calcOrders() {
		total = new ForecastingTotal(getForecast(), getMedicineTotals(), ForecastingTotal.ALL_TOTAL);
		totalR = new ForecastingTotal(getForecast(),getMedicineTotals(), ForecastingTotal.REGULAR_TOTAL);
		totalA = new ForecastingTotal(getForecast(),getMedicineTotals(), ForecastingTotal.ACCEL_TOTAL);
		//take and set the schedule constrain
		DeliveryScheduleEnum sched = getForecast().getForecastObj().getDeliverySchedule();
		getForecast().getForecastObj().setAcceleratedSchedule(sched); //2016-04-04 IMPERATIVE NOW!!!!
		control = new DeliveryOrdersControl(getForecast().getLeadTime(),
				totalR, totalA, total);
		control.setConsumptions(getForecastCalculator().getMedicineConsumption());
	}

	/**
	 * Re execute calculation, because calculation scenario has been changed by user!
	 */
	public void reExecute() {
		//recalculate min max deliveries
		logisticCalculator = LogisticCalculatorsFactory.getLolgisticCalculator(getForecast());
		logisticCalculator.exec(getForecastCalculator().getMedicineConsumption());
		//put results to existed med totals
		reCalcMedTotals();
		calcOrders();
	}

	/**
	 * Put result to the existing med totals
	 */
	private void reCalcMedTotals() {
		cleanUpPreviousQuantities();
		for(MedicineConsumption mC :getForecastCalculator().getMedicineConsumption()){
			ForecastingTotalMedicine medOrd = findMedOrd(mC.getMed());
			recalcQuantityAndCost(mC, medOrd);
		}

	}
	/**
	 * Recalc quantity and cost line for medicine given
	 * @param mC
	 * @param medOrd
	 */
	public void recalcQuantityAndCost(MedicineConsumption mC, ForecastingTotalMedicine medOrd) {
		medOrd.setAdjustAccel(mC.getAccelDeliveriesTotal());
		medOrd.setAdjustedRegular(mC.getRegularDeliveriesTotal());
		medOrd.setAdjustedAccelPack(mC.getAccelPacksTotal());
		medOrd.setAdjustedRegularPack(mC.getRegularPacksTotal());
		medOrd.setAccelQuant(mC.getAccelNeedTotal());
		medOrd.setRegularQuant(mC.getRegularNeedTotal());
	}

	/**
	 * find existing med order by medicine
	 * @param med medicine
	 * @return
	 */
	private ForecastingTotalMedicine findMedOrd(MedicineUIAdapter med) {
		for(ForecastingTotalMedicine medOrd : getMedicineTotals()){
			if(medOrd.getMedicine().compareTo(med)==0){
				return medOrd;
			}
		}
		return null;
	}

	/**
	 * Clean up previous regular and accelerated quantities from the totals
	 */
	private void cleanUpPreviousQuantities() {
		for(ForecastingTotalMedicine medOrd : getMedicineTotals()){
			medOrd.setAccelQuantFast(0);
			medOrd.setRegularQuantFast(0);
		}

	}

	/**
	 * Calculate medicines totals for orders. This calculation will based solely on deliveries calculated above
	 */
	private void calcMedTotals() {
		getMedicineTotals().clear();
		for(MedicineConsumption mC :getForecastCalculator().getMedicineConsumption()){
			ForecastingMedicineUIAdapter fmu = getForecast().getMedicine(mC.getMed());
			ForecastingTotalMedicine medOrd = new ForecastingTotalMedicine(mC.getMed());
			getMedicineTotals().add(medOrd);
			recalcQuantityAndCost(mC, medOrd);
			medOrd.setAdjustIt(fmu.getPackOrder(Presenter.getFactory()).getAdjust());
			medOrd.setAdjustItAccel(fmu.getPackOrder(Presenter.getFactory()).getAdjustAccel());
			medOrd.setPackPrice(fmu.getPackOrder(Presenter.getFactory()).getPackPrice());
			medOrd.setPackPriceAccel(fmu.getPackOrder(Presenter.getFactory()).getPackPriceAccel());
			medOrd.setPackSize(fmu.getPackOrder(Presenter.getFactory()).getPack());
			medOrd.setPackSizeAccel(fmu.getPackOrder(Presenter.getFactory()).getPackAccel());
			medOrd.setAdjustAccel(mC.getAccelDeliveriesTotal());
			medOrd.setAdjustedRegular(mC.getRegularDeliveriesTotal());
		}

	}


}
