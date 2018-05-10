package org.msh.quantb.services.io;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.jdesktop.observablecollections.ObservableCollections;
import org.msh.quantb.model.forecast.ForecastingBatch;
import org.msh.quantb.model.forecast.ForecastingOrder;
import org.msh.quantb.model.forecast.ForecastingResult;
import org.msh.quantb.services.calc.DateUtils;

/**
 * UI adapter for forecasting result
 * @author alexey
 *
 */
public class ForecastingResultUIAdapter extends AbstractUIAdapter implements Comparable<ForecastingResultUIAdapter> {
	private ForecastingResult forecastingResultObj;
	List<ForecastingBatchUIAdapter> batches;
	List<ForecastingOrderUIAdapter> orders;


	/**
	 * Only valid constructor
	 * @param forecastingResult
	 */
	public ForecastingResultUIAdapter(ForecastingResult forecastingResult){
		this.forecastingResultObj = forecastingResult;
	}

	/**
	 * @return forecastingResultObj
	 */
	public ForecastingResult getForecastingResult() {
		return forecastingResultObj;
	}

	/**
	 * @param forecastingResultObj
	 */
	public void setForecastingResult(ForecastingResult forecastingResultObj) {
		//ForecastingResult oldValue = getForecastingResult();
		this.forecastingResultObj = forecastingResultObj;
		//firePropertyChange("forecastingResultObj", oldValue, getForecastingResult());
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingResult#getMonth()
	 */
	public MonthUIAdapter getMonth() {
		return new MonthUIAdapter(forecastingResultObj.getMonth());
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingResult#getOldCases()
	 */
	public BigDecimal getOldCases() {
		return forecastingResultObj.getOldCases();
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingResult#getNewCases()
	 */
	public BigDecimal getNewCases() {
		return forecastingResultObj.getNewCases();
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingResult#getConsOld()
	 */
	public BigDecimal getConsOld() {
		return forecastingResultObj.getConsOld();
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingResult#getConsNew()
	 */
	public BigDecimal getConsNew() {
		return forecastingResultObj.getConsNew();
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingResult#getBatches()
	 */
	public List<ForecastingBatchUIAdapter> getBatches() {
		ArrayList<ForecastingBatchUIAdapter> bas = new ArrayList<ForecastingBatchUIAdapter>();
		for(ForecastingBatch b : forecastingResultObj.getBatches()){
			ForecastingBatchUIAdapter ba = new ForecastingBatchUIAdapter(b);
			bas.add(ba);
		}
		this.batches = ObservableCollections.observableList(bas);
		Collections.sort(batches);
		return this.batches;
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingResult#getOrders()
	 */
	public List<ForecastingOrderUIAdapter> getOrders() {
		ArrayList<ForecastingOrderUIAdapter> bas = new ArrayList<ForecastingOrderUIAdapter>();
		for(ForecastingOrder o : forecastingResultObj.getOrders()){
			ForecastingOrderUIAdapter ba = new ForecastingOrderUIAdapter(o);
			bas.add(ba);
		}
		this.orders = ObservableCollections.observableList(bas);
		Collections.sort(orders);
		return this.orders;
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingResult#setMonth(org.msh.quantb.model.forecast.Month)
	 */
	public void setMonth(MonthUIAdapter value) {
		//MonthUIAdapter oldValue = getMonth();
		forecastingResultObj.setMonth(value.getMonthObj());
		//firePropertyChange("month", oldValue, getMonth());
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingResult#setOldCases(int)
	 */
	public void setOldCases(BigDecimal value) {
		forecastingResultObj.setOldCases(value);
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingResult#setNewCases(int)
	 */
	public void setNewCases(BigDecimal value) {
		//Integer oldValue = getNewCases();
		forecastingResultObj.setNewCases(value);
		//firePropertyChange("newCases", oldValue, getNewCases());
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingResult#setConsOld(int)
	 */
	public void setConsOld(BigDecimal value) {
		//Integer oldValue = getConsOld();
		forecastingResultObj.setConsOld(value);
		//firePropertyChange("consOld", oldValue, getConsOld());
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingResult#setConsNew(int)
	 */
	public void setConsNew(BigDecimal value) {
		//Integer oldValue = getConsNew();
		forecastingResultObj.setConsNew(value);
		///firePropertyChange("consNew", oldValue, getConsNew());
	}

	@Override
	/**
	 * Count results as equal, if months equal
	 */
	public boolean equals(Object _another){
		if (super.equals(_another)){
			ForecastingResultUIAdapter another = (ForecastingResultUIAdapter) _another;
			return this.getMonth().equals(another.getMonth());
		} return false;
	}

	@Override
	public int compareTo(ForecastingResultUIAdapter another) {
		return this.getMonth().compareTo(another.getMonth());
	}
	/**
	 * copy cases quantity and medicine consumptions from all regimes for particular day
	 * @param forecastUI forecasting
	 * @param fm what medicine used
	 */
	public void setCasesAndMedCons(ForecastUIAdapter forecastUI, ForecastingMedicineUIAdapter fm) {
		List<ForecastingRegimenResultUIAdapter> dayRes = forecastUI.getAllRRByDays(this.getMonth(),this.getFromDay(),this.getToDay());
		for(ForecastingRegimenResultUIAdapter frr : dayRes){
			MedicineConsUIAdapter mc = frr.getMedConsunption(fm.getMedicine());
			if (mc != null){
				// calculate medicines consumption
				BigDecimal oldCons = mc.getConsumeOld();
				BigDecimal newCons = mc.getConsumeNew();
				this.addOldCons(oldCons);
				this.addNewCons(newCons);
				//get expected and enrolled cases quantities that really consume this medicine
				BigDecimal oldCases = frr.getOldCasesThatConsume(mc);
				BigDecimal newCases = frr.getNewCasesThatConsume(mc);
				this.addOld(oldCases);
				this.addNew(newCases);
/*				if(fm.getMedicine().getNameForDisplayWithAbbrev().contains("penem") && frr.getFromDay()==17 && frr.getMonth().getYear()==2014 && frr.getMonth().getMonth()==4){
					System.out.println(this);
				}*/
			}
		}
	}
	/**
	 * add quantity to new medicine consumption
	 * @param newCons
	 */
	private void addNewCons(BigDecimal newCons) {
		this.setConsNew(this.getConsNew().add(newCons));
	}

	/**
	 * add quantity to old medicine consumption
	 * @param oldCons
	 */
	private void addOldCons(BigDecimal oldCons) {
		this.setConsOld(this.getConsOld().add(oldCons));
	}

	/**
	 * add quantity to new cases
	 * @param newCases
	 */
	private void addNew(BigDecimal newCases) {
		this.setNewCases(this.getNewCases().add(newCases));
	}
	/**
	 * Add quantity to old cases
	 * @param oldCases
	 */
	private void addOld(BigDecimal oldCases) {
		this.setOldCases(this.getOldCases().add(oldCases));

	}
	/**
	 * Return total medicine consumption
	 * @return
	 */
	public BigDecimal getMedicineCons() {
		return this.getConsNew().add(this.getConsOld());
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingResult#getMissing()
	 */
	public BigDecimal getMissing() {
		return forecastingResultObj.getMissing();
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingResult#setMissing(int)
	 */
	public void setMissing(BigDecimal value) {
		//Integer oldValue = getMissing();
		forecastingResultObj.setMissing(value);
		//firePropertyChange("missing", oldValue, getMissing());
	}
	/**
	 * add missing value to the current
	 * @param value
	 */
	public void addMissing(BigDecimal value){
		this.setMissing(this.getMissing().add(value));
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingResult#getFromDay()
	 */
	public Integer getFromDay() {
		return forecastingResultObj.getFromDay();
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingResult#setFromDay(int)
	 */
	public void setFromDay(Integer value) {
		//Integer oldValue = getFromDay();
		forecastingResultObj.setFromDay(value);
		//firePropertyChange("fromDay", oldValue, getFromDay());
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingResult#getToDay()
	 */
	public Integer getToDay() {
		return forecastingResultObj.getToDay();
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingResult#setToDay(int)
	 */
	public void setToDay(Integer value) {
		//Integer oldValue = getToDay();
		forecastingResultObj.setToDay(value);
		//firePropertyChange("toDay", oldValue, getToDay());
	}
	/**
	 * Get calendar From based on results values
	 * @return
	 */
	public Calendar getFrom() {
		return this.getMonth().getAnyDate(this.getFromDay().intValue());
	}
	/**
	 * Get calendar to based on results values
	 * @return
	 */
	public Calendar getTo() {
		return this.getMonth().getAnyDate(this.getToDay().intValue());
	}
	/**
	 * Sum all available in all batches
	 * @return
	 */
	public BigDecimal getAllAvailable() {
		BigDecimal res = BigDecimal.ZERO;
		for(ForecastingBatch b : this.getForecastingResult().getBatches()){
			res = res.add(b.getQuantityAvailable());
		}
		return res;
	}
	/**
	 * sum all expired in all batches
	 * @return
	 */
	public Integer getExpired() {
		Integer res = 0;
		for(ForecastingBatch b : this.getForecastingResult().getBatches()){
			res = res + b.getQuantityExpired();
		}
		return res;
	}
	
	/**
	 * Get only order's batches expired
	 * @return
	 */
	public Integer getOrderExpired(){
		Integer res = 0;
		for(ForecastingBatch b : this.getForecastingResult().getBatches()){
			if(b.getAvailFrom().getYear()>1900){
				res = res + b.getQuantityExpired();
			}
		}
		return res;
	}

	/**
	 * get all medicines in orders avail in result period<br>
	 * Account them as sum quantities in batches will became available in
	 * any date in given period
	 * @return
	 */
	public int getInOrders() {
		Integer res = 0;
		for(ForecastingBatch b : this.getForecastingResult().getBatches()){
			Calendar availFrom = b.getAvailFrom().toGregorianCalendar();
			DateUtils.cleanTime(availFrom);
			if(matchPeriod(availFrom)){
				res = res + b.getQuantity();
			}
		}
		return res;
	}
	/**
	 * Data given in period
	 * @param availFrom
	 * @return
	 */
	public boolean matchPeriod(Calendar availFrom) {
		Date avail = DateUtils.getcleanDate(availFrom);
		Date from = DateUtils.getcleanDate(getFrom());
		Date to = DateUtils.getcleanDate(getTo());
		return (avail.compareTo(from) >=0) &&
				(avail.compareTo(to)<=0);
	}
	/**
	 * get all dispensing from batches
	 * @return
	 */
	public BigDecimal getDispensing() {
		BigDecimal res = BigDecimal.ZERO;
		for(ForecastingBatch b : this.getForecastingResult().getBatches()){
			res = res.add(b.getConsumptionInMonth());
		}
		return res;
	}

	public int getMedicineConsInt() {
		return getMedicineCons().setScale(2, BigDecimal.ROUND_UP).intValueExact();
	}

	@Override
	public String toString() {
		return "ForecastingResultUIAdapter [getMonth()=" + getMonth()
				+ ", getFromDay()=" + getFromDay() + ", getAllAvailable()="
				+ getAllAvailable() + ", getExpired()=" + getExpired()
				+ ", getDispensing()=" + getDispensing() + "]";
	}


}
