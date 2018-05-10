package org.msh.quantb.services.io;

import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.joda.time.LocalDate;
import org.msh.quantb.model.forecast.ForecastingBatch;
import org.msh.quantb.model.forecast.ForecastingMedicine;
import org.msh.quantb.model.forecast.ForecastingOrder;
import org.msh.quantb.model.forecast.ForecastingResult;
import org.msh.quantb.model.forecast.PricePack;
import org.msh.quantb.model.mvp.ModelFactory;
import org.msh.quantb.services.calc.DateUtils;
import org.msh.quantb.services.mvp.Presenter;

/**
 * Object ForecastingMedicine adopted for UI
 * @author alexey
 *
 */
/**
 * @author User
 *
 */
@SuppressWarnings("rawtypes")
public class ForecastingMedicineUIAdapter extends AbstractUIAdapter implements Comparable {
	@Override
	public String toString() {
		return "ForecastingMedicineUIAdapter [getStockOnOrderInt()="
				+ getStockOnOrderInt() + ", getBatchesToExpireInt()="
				+ getBatchesToExpireInt() + ", getMedicine()=" + getMedicine()
				+ "]";
	}
	private ForecastingMedicine fcMedicineObj;
	private MedicineUIAdapter medicineUI;
	//private List<ForecastingBatchUIAdapter> batchesToExpire;
	//private List<ForecastingOrderUIAdapter> orders;
	private List<ForecastingResultUIAdapter> results;
	private String propertyName;
	private PropertyChangeListener listener;
	private PropertyChangeListener ordersInclExcListener;
	private PropertyChangeListener batchInclExclListener;
	/**
	 * Only valid constructor
	 * @param fcMedicineObj
	 */
	public ForecastingMedicineUIAdapter(ForecastingMedicine fcMedicineObj) {
		super();
		this.fcMedicineObj = fcMedicineObj;
	}
	/**
	 * @return the fcMedicineObj
	 */
	public ForecastingMedicine getFcMedicineObj() {
		return fcMedicineObj;
	}
	/**
	 * @param fcMedicineObj the fcMedicineObj to set
	 */
	public void setFcMedicineObj(ForecastingMedicine fcMedicineObj) {
		ForecastingMedicine oldValue = getFcMedicineObj();
		this.fcMedicineObj = fcMedicineObj;
		firePropertyChange("fcMEdicineObj", oldValue, getFcMedicineObj());
	}

	/**
	 * Get sum of quantities of all orders 
	 * @return sum of quantities of all orders 
	 */
	public Integer getStockOnOrderInt(){
		int sum = 0;
		if (fcMedicineObj==null || fcMedicineObj.getOrders()==null){
			return null;
		}
		for (ForecastingOrderUIAdapter fo : getOrders()){
			sum+=(fo!=null && fo.getBatch()!=null)?fo.getBatch().getQuantity():0;
		}
		return sum;
	}


	/**
	 * Get sum of quantities of all expiried batches 
	 * @return sum of quantities of all expiried batches 
	 */
	public Integer getBatchesToExpireInt(){
		int sum = 0;
		if (fcMedicineObj==null || fcMedicineObj.getBatchesToExpire()==null){
			return null;
		}
		for (ForecastingBatchUIAdapter fb : getBatchesToExpire()){
			sum+=(fb!=null)?fb.getQuantity():0;
		}
		return sum;
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingMedicine#getMedicine()
	 */
	public MedicineUIAdapter getMedicine() {
		if (medicineUI == null) medicineUI = new MedicineUIAdapter(fcMedicineObj.getMedicine());
		return medicineUI;
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingMedicine#setMedicine(org.msh.quantb.model.gen.Medicine)
	 */
	public void setMedicine(MedicineUIAdapter value) {
		MedicineUIAdapter oldValue = getMedicine();
		fcMedicineObj.setMedicine(value.getMedicine());
		firePropertyChange("medicine", oldValue, getMedicine());
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingMedicine#getStockOnOrderLT()
	 */
	public Integer getStockOnOrderLT() {
		return fcMedicineObj.getStockOnOrderLT();
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingMedicine#setStockOnOrderLT(int)
	 */
	public void setStockOnOrderLT(Integer value) {
		Integer old = getStockOnOrderLT();
		fcMedicineObj.setStockOnOrderLT(value);
		firePropertyChange("stockOnOrderLT", old, getStockOnOrderLT());
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingMedicine#getConsumptionLT()
	 */
	public Integer getConsumptionLT() {
		return fcMedicineObj.getConsumptionLT();
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingMedicine#setConsumptionLT(int)
	 */
	public void setConsumptionLT(Integer value) {
		Integer old = getConsumptionLT();
		fcMedicineObj.setConsumptionLT(value);
		firePropertyChange("consumptionLT", old, getConsumptionLT());
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingMedicine#getQuantityMissingLT()
	 */
	public Integer getQuantityMissingLT() {
		return fcMedicineObj.getQuantityMissingLT();
	} 
	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingMedicine#setQuantityMissingLT(int)
	 */
	public void setQuantityMissingLT(Integer value) {
		Integer old = getQuantityMissingLT();
		fcMedicineObj.setQuantityMissingLT(value);
		firePropertyChange("quantityMissingLT", old, getQuantityMissingLT());
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingMedicine#getQuantityExpiredLT()
	 */
	public Integer getQuantityExpiredLT() {
		return fcMedicineObj.getQuantityExpiredLT();
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingMedicine#setQuantityExpiredLT(int)
	 */
	public void setQuantityExpiredLT(Integer value) {
		Integer oldValue = getQuantityExpiredLT();
		fcMedicineObj.setQuantityExpiredLT(value);
		firePropertyChange("quantityExpiredLT", oldValue, getQuantityExpiredLT());
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingMedicine#getConsumptionCases()
	 */
	public Integer getConsumptionCases() {
		return fcMedicineObj.getConsumptionCases();
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingMedicine#setConsumptionCases(int)
	 */
	public void setConsumptionCases(Integer value) {
		Integer oldValue = getConsumptionCases();
		fcMedicineObj.setConsumptionCases(value);
		firePropertyChange("consumptionCases", oldValue, getConsumptionCases());
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingMedicine#getConsumptionNewCases()
	 */
	public Integer getConsumptionNewCases() {
		return fcMedicineObj.getConsumptionNewCases();
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingMedicine#setConsumptionNewCases(int)
	 */
	public void setConsumptionNewCases(Integer value) {
		Integer oldValue = getConsumptionNewCases();
		fcMedicineObj.setConsumptionNewCases(value);
		firePropertyChange("consumptionNewCases", oldValue, getConsumptionNewCases());
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingMedicine#getUnitPrice()
	 */
	public Integer getUnitPrice() {
		return fcMedicineObj.getUnitPrice();
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingMedicine#setUnitPrice(int)
	 */
	public void setUnitPrice(Integer value) {
		Integer oldValue = getUnitPrice();
		fcMedicineObj.setUnitPrice(value);
		firePropertyChange("unitPrice", oldValue, getUnitPrice());
	}
	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingMedicine#getStockOnHand()
	 */
	public Integer getStockOnHand() {
		return fcMedicineObj.getStockOnHand();
	}
	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.ForecastingMedicine#setStockOnHand(int)
	 */
	public void setStockOnHand(Integer value) {
		Integer oldValue = getStockOnHand();
		fcMedicineObj.setStockOnHand(value);
		firePropertyChange("stockOnHand", oldValue, getStockOnHand());
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingMedicine#getBatchesToExpire()
	 */
	public List<ForecastingBatchUIAdapter> getBatchesToExpire() {
		ArrayList<ForecastingBatchUIAdapter> bas = new ArrayList<ForecastingBatchUIAdapter>();
		for(ForecastingBatch b : fcMedicineObj.getBatchesToExpire()){
			ForecastingBatchUIAdapter ba = new ForecastingBatchUIAdapter(b);
			if (this.listener!=null && this.propertyName!=null) ba.addPropertyChangeListener(propertyName, listener);
			ba.addPropertyChangeListener("include",getBatchInclExclListener());
			bas.add(ba);
		}
		ObservableList<ForecastingBatchUIAdapter> batchesToExpire = ObservableCollections.observableList(bas);
		Collections.sort(batchesToExpire);
		return batchesToExpire;        
	}    

	/**
	 * Get orders by natural (expire date) order, it's need for calculations
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingMedicine#getOrders()
	 */
	public List<ForecastingOrderUIAdapter> getOrders() {
		List<ForecastingOrderUIAdapter> ret = getUnsortedOrders();
		Collections.sort(ret);
		return ret;
	}

	/**
	 * Get orders by the arrival date, it's need for display only
	 * @return
	 */
	public List<ForecastingOrderUIAdapter> getOrdersByArrival(){
		List<ForecastingOrderUIAdapter> ret = getUnsortedOrders();
		Collections.sort(ret, new Comparator<ForecastingOrderUIAdapter>() {

			@Override
			public int compare(ForecastingOrderUIAdapter o1,
					ForecastingOrderUIAdapter o2) {
				if (o1 == null){
					return 1;
				}
				if (o2 == null){
					return -1;
				}
				int comp = o1.getArrived().compareTo(o2.getArrived());
				if (comp != 0){
					return comp;
				}else{
					return o1.compareTo(o2);
				}
			}
		});
		return ret;
	}
	/**
	 * Get orders unsorted, sort order will be determined later
	 * @return
	 */
	private List<ForecastingOrderUIAdapter> getUnsortedOrders() {
		ArrayList<ForecastingOrderUIAdapter> oas = new ArrayList<ForecastingOrderUIAdapter>();
		for(ForecastingOrder o : fcMedicineObj.getOrders()){
			ForecastingOrderUIAdapter oa = new ForecastingOrderUIAdapter(o);
			//TODO temporary fix bad edit bug
			o.getArrivalDate().setMonth(oa.getArrived().get(Calendar.MONTH));
			o.getArrivalDate().setYear(oa.getArrived().get(Calendar.YEAR));
			//end temp fix
			if (this.listener!=null && this.propertyName!=null) oa.setProgertyChangeListener(propertyName, listener);
			oa.addPropertyChangeListener("batchInclude", getOrdersInclExclListener());
			oas.add(oa);
		}

		//this.orders = ObservableCollections.observableList(oas);
		//return this.orders;  
		List<ForecastingOrderUIAdapter> ret = ObservableCollections.observableList(oas);
		return ret;
	}

	/**
	 * Set property change listener to current ForecastingMedicineUiAdapter
	 * @param propertyName property name
	 * @param listener property change listener
	 */
	public void setProgertyChangeListener(String propertyName, PropertyChangeListener listener){
		this.listener = listener;
		this.propertyName = propertyName;    	
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.ForecastingMedicine#getResults()
	 */
	public List<ForecastingResultUIAdapter> getResults() {
		ArrayList<ForecastingResultUIAdapter> or = new ArrayList<ForecastingResultUIAdapter>();
		for(ForecastingResult r : fcMedicineObj.getResults()){
			ForecastingResultUIAdapter ra = new ForecastingResultUIAdapter(r);
			or.add(ra);
		}
		this.results = ObservableCollections.observableList(or);
		Collections.sort(this.results);
		return this.results;           
	}

	/**
	 * Count object as equal if medicine equal<br>
	 * Certainly, it is impossible two equal medicines in one forecasting
	 */
	@Override
	public boolean equals(Object _another){
		if (super.equals(_another)){
			ForecastingMedicineUIAdapter another = (ForecastingMedicineUIAdapter) _another;
			return this.getMedicine().equals(another.getMedicine());
		}else return false;
	}

	@Override
	public int compareTo(Object o) {
		if (!o.getClass().equals(this.getClass())) return -1;
		ForecastingMedicineUIAdapter another = (ForecastingMedicineUIAdapter) o;
		if (getMedicine() == null || getMedicine().getAbbrevName()==null) return -1;
		if (another==null || another.getMedicine()==null || another.getMedicine().getAbbrevName() == null) return 1;
		//return getMedicine().getAbbrevName().compareTo(another.getMedicine().getAbbrevName());
		return getMedicine().compareTo(another.getMedicine());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getMedicine() == null || getMedicine().getAbbrevName() == null) ? 0 : getMedicine().getAbbrevName().hashCode());
		return result;
	}
	/**
	 * clean previous and create new forecasting results for each day
	 * Based on existent regimen results !!!!
	 * @param modelFactory
	 * @param forecastUI forecasting
	 */
	public void createResults(ModelFactory modelFactory,
			ForecastUIAdapter forecastUI) {
		this.getFcMedicineObj().getResults().clear();
		boolean account = false;
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		List<ForecastingRegimenResultUIAdapter> resAd = forecastUI.getRegimes().get(0).getResults();
		LocalDate begin = new LocalDate(forecastUI.getReferenceDate()); //20160825 seems as appropriate, because "after" below
		for(ForecastingRegimenResultUIAdapter res : resAd){
			//AK to avoid loss of the calc result because of time zone diff/ account = account | fmt.format(res.getFromDate().getTime()).equals(fmt.format(forecastUI.getReferenceDt()));
			//account = account | (fmt.format(res.getFromDate().getTime()).compareTo(fmt.format(forecastUI.getReferenceDt()))>=0);
			LocalDate resDate= new LocalDate(res.getFromDate());    //AK 20160822 shift right
			account= account | resDate.isAfter(begin);				//AK 20160822 shift right
			if (account){
				ForecastingResult frr = modelFactory.createForecastingResult(res.getMonth().getMonthObj());
				frr.setFromDay(res.getFromDay());
				frr.setToDay(res.getToDay());
				this.getFcMedicineObj().getResults().add(frr);
			}

		}

	}
	/**
	 * Refreshe totals for stock on order
	 */
	public void refreshStockOnOrderInt() {
		firePropertyChange("stockOnOrderInt", -1, getStockOnOrderInt());		
	}
	/**
	 * Refresh totals for batches to expire
	 */
	public void refreshBatchesToExpireInt() {
		firePropertyChange("batchesToExpireInt", -1, getBatchesToExpireInt());		
	}
	/**
	 * Pack in the order
	 * @param pack
	 */
	public void setPackOrder(PricePack pack){
		PricePack oldValue = getPackOrder(null);
		this.getFcMedicineObj().setPackOrder(pack);
		firePropertyChange("packOrder", oldValue, getPackOrder(null));
	}
	/**
	 * Get Pack in the order
	 * @param modelFactory 
	 * @return always return pack, but if no pack and modelFactory is null, return null
	 */
	public PricePack getPackOrder(ModelFactory modelFactory){
		PricePack pack = this.getFcMedicineObj().getPackOrder();
		if (pack == null){
			if (modelFactory != null){
				pack = modelFactory.createPricePack(new BigDecimal(100), 0, new BigDecimal(0));
				this.getFcMedicineObj().setPackOrder(pack);
			}else{
				return null;
			}
		}
		return pack;
	}

	/**
	 * Set the adjustment coefficient for enrolled cases
	 * @param value
	 */
	public void setAdjustmentEnrolled(BigDecimal value){
		BigDecimal oldValue = this.getAdjustmentEnrolled();
		this.getFcMedicineObj().setAjustmentEnrolled(value);
		firePropertyChange("adjustmentEnrolled", oldValue, getAdjustmentEnrolled());
	}

	/**
	 * get the adjustment coefficient for enrolled cases
	 * @return
	 */
	public BigDecimal getAdjustmentEnrolled() {
		BigDecimal ret = this.getFcMedicineObj().getAjustmentEnrolled();
		if (ret == null){
			ret = new BigDecimal(100);
		}
		return ret; 
	}

	/**
	 * Set the adjustment coefficient for expected cases
	 * @param value
	 */
	public void setAdjustmentExpected(BigDecimal value){
		BigDecimal oldValue = this.getAdjustmentExpected();
		this.getFcMedicineObj().setAdjustmentExpected(value);
		firePropertyChange("adjustmentExpected", oldValue, getAdjustmentExpected());
	}

	/**
	 * get the adjustment coefficient for expected cases
	 * @return
	 */
	public BigDecimal getAdjustmentExpected() {
		BigDecimal ret =  this.getFcMedicineObj().getAdjustmentExpected();
		if (ret == null){
			ret = new BigDecimal(100);
		}
		return ret;
	}
	/**
	 * Remove all existing batches
	 */
	public void removeBatches() {
		fcMedicineObj.getBatchesToExpire().clear();
		firePropertyChange("batchesToExpireInt", -1, getBatchesToExpireInt());	

	}
	/**
	 * Add batch, mainly for import batches
	 * @param batch
	 */
	public void addBatch(ForecastingBatch batch) {
		fcMedicineObj.getBatchesToExpire().add(batch);
		firePropertyChange("batchesToExpireInt", null, getBatchesToExpireInt());
		firePropertyChange("batchesToExpire", null, getBatchesToExpire());
	}

	/**
	 * add batch if one not exists of merge quantity with existing batch
	 * @param fBuI
	 */
	public void addOrMergeBatch(ForecastingBatchUIAdapter fBuI) {
		for(ForecastingBatchUIAdapter myBatch : getBatchesToExpire()){
			if(myBatch.equalsForMerge(fBuI)){ //merge
				myBatch.setQuantity(fBuI.getQuantity() + myBatch.getQuantity());
				myBatch.setQuantityAvailable(fBuI.getQuantityAvailable().add(myBatch.getQuantityAvailable()));
				// merge coments
				mergeCommentsBatch(fBuI, myBatch);
				return;
			}
		}
		//nothing to merge, so add a new batch
		ForecastingBatch fb = Presenter.getFactory().createForecastingBatchExact(fBuI.getExpired());
		fb.setAvailFrom(fBuI.getForecastingBatchObj().getAvailFrom());
		fb.setComment(fBuI.getForecastingBatchObj().getComment());
		fb.setQuantity(fBuI.getForecastingBatchObj().getQuantity());
		fb.setQuantityAvailable(fBuI.getForecastingBatchObj().getQuantityAvailable().add(BigDecimal.ZERO)); //to avoid refs
		addBatch(fb);

	}
	
	private void mergeCommentsBatch(ForecastingBatchUIAdapter fcUi, ForecastingBatchUIAdapter fcMerged) {
		String s = fcMerged.getComment();
		if(s == null) s = "";
		
		String s1 = fcUi.getComment();
		if(s1 == null) s1 = "";
		
		if(s.length() > 0){
			if(!s.contains(s1))
				s = s + "; " + s1;
		}else
			s = s1;
		
		if(s.length() > 36)
			s = s.substring(0, 36);
		
		fcMerged.setComment(s);
	}
	/**
	 * add order if one not exist or merge with existing order if batch is same
	 * @param fOuI
	 */
	public void addOrMergeOrder(ForecastingOrderUIAdapter fOuI) {
		for(ForecastingOrderUIAdapter myOrder : getOrders()){
			if(DateUtils.compareDates(myOrder.getArrived(), fOuI.getArrived())==0){
				if(DateUtils.compareDates(myOrder.getBatch().getExpired(), fOuI.getBatch().getExpired())==0){ //merge
					myOrder.getBatch().setQuantity(fOuI.getBatch().getQuantity()+myOrder.getBatch().getQuantity());
					myOrder.getBatch().setQuantityAvailable(fOuI.getBatch().getQuantityAvailable().add(myOrder.getBatch().getQuantityAvailable()));
					// merge coments
					mergeCommentsOrder(fOuI, myOrder);
					return;
				}
			}
		}
		// nothing to merge, so add a new order
		ForecastingOrderUIAdapter newOrder = fOuI.makeClone(Presenter.getFactory());
		this.getFcMedicineObj().getOrders().add(newOrder.getForecastingOrderObj());

	}

	private void mergeCommentsOrder(ForecastingOrderUIAdapter fcUi, ForecastingOrderUIAdapter fcMerged) {
		String s = fcMerged.getComment();
		if(s == null) s = "";
		
		String s1 = fcUi.getComment();
		if(s1 == null) s1 = "";
		
		if(s.length() > 0){
			if(!s.contains(s1))
				s = s + "; " + s1;
		}else
			s = s1;
		
		if(s.length() > 63)
			s = s.substring(0, 63);
		
		fcMerged.setComment(s);
	}
	
	/**
	 * Has excluded batches?
	 * @return
	 */
	public boolean hasExcludedBatchesToExpire() {
		for(ForecastingBatch b : getFcMedicineObj().getBatchesToExpire()){
			if(b.isExclude()){
				return true;
			}
		}
		return false;
	}

	/**
	 * Has excluded batches in orders
	 * @return
	 */
	public boolean hasExcludedOrderBatchesToExpire() {
		for(ForecastingOrder o :getFcMedicineObj().getOrders()){
			if(o.getBatch().isExclude()){
				return true;
			}
		}
		return false;
	}


	public PropertyChangeListener getOrdersInclExclListener() {
		return ordersInclExcListener;
	}

	/**
	 * Any order should has this processor to process changes of BatchInclude property of the order
	 * @see getUnsortedOrders
	 * @param propertyChangeListener
	 */
	public void setOrdersIncExclListener(
			PropertyChangeListener propertyChangeListener) {
		this.ordersInclExcListener = propertyChangeListener;

	}

	/**
	 * Any batch should has this processor to process changes of include property of the batch 
	 * @see getBatchesToExpire
	 * @param propertyChangeListener
	 */
	public void setBatchInclExclListener(
			PropertyChangeListener propertyChangeListener) {
		this.batchInclExclListener = propertyChangeListener;

	}
	public PropertyChangeListener getBatchInclExclListener() {
		return batchInclExclListener;
	}
	/**
	 * remove all orders
	 */
	public void removeOrders() {
		getFcMedicineObj().getOrders().clear();
		firePropertyChange("stockOnOrderInt", -1, getStockOnOrderInt());
		
		
	}
	public void addOrder(ForecastingOrder order) {
		fcMedicineObj.getOrders().add(order);
		firePropertyChange("stockOnOrderInt", null, getStockOnOrderInt());
		firePropertyChange("orders", null, getOrders());
		
	}

}
