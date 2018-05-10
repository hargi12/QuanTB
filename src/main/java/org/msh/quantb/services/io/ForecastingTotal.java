package org.msh.quantb.services.io;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.msh.quantb.model.forecast.ForecastingTotalItem;
import org.msh.quantb.services.mvp.Messages;

/**
 * This class responsible for forecasting order totals for all "final cost" tabs
 *  These tabs displays totals for total, regular and accelerated orders
 * @author alexey
 *
 */
public class ForecastingTotal extends AbstractUIAdapter {
	/**
	 * calculate whole total
	 */
	public final static int ALL_TOTAL = 0;
	/**
	 * calculate only regular order total
	 */
	public final static int REGULAR_TOTAL = 1;
	/**
	 * calculate only accelerated order total
	 */
	public final static int ACCEL_TOTAL = 2;

	private List<ForecastingTotalMedicine> medItems = null;
	private List<ForecastingTotalItemUIAdapter> addItems = null;
	private BigDecimal costOrderTotal = new BigDecimal(0);
	private BigDecimal medTotal = new BigDecimal(0);
	private ForecastUIAdapter forecastUI = null;
	private ForecastingTotalItemUIAdapter selectedTotalItem = null;
	private boolean lockLocalListener;
	private int orderTotal=0;
	private List<ForecastingTotal> orders = new ArrayList<ForecastingTotal>();
	private BigDecimal orderGrandTotal=BigDecimal.ZERO;
	private Integer deliveriesQuantity=0;

	/**
	 * Only valid constructor
	 * @param _forecastUI forecasting
	 * @param _medItems total medicine items
	 * @param orderTotal ALL_TOTAL, or REGULAR_TOTAL or ACCEL_TOTAL
	 */
	public ForecastingTotal(ForecastUIAdapter _forecastUI, List<ForecastingTotalMedicine> _medItems, int orderTotal){
		this.orderTotal = orderTotal;
		this.forecastUI = _forecastUI;
		this.medItems = _medItems;
		switch(this.orderTotal){
		case ALL_TOTAL:
			this.addItems = _forecastUI.getTotalOrderItems();
			break;
		case REGULAR_TOTAL:
			this.addItems = _forecastUI.getRegOrderItems();
			break;
		case ACCEL_TOTAL:
			this.addItems = _forecastUI.getAccOrderItems();
			break;
		default:
			this.addItems = _forecastUI.getTotalOrderItems();
		}
		recalcMedTotal();
		recalcAddItems();
		recalcTotal();
		for(ForecastingTotalMedicine ftm : this.medItems){
			ftm.addPropertyChangeListener("totalCost", new PropertyChangeListener(){
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					recalcTotal();
					for(ForecastingTotalItemUIAdapter ftiU : getAddItems()){
						recalcTotalItem(ftiU);
					}
				}
			});
		}
	}


	/**
	 * CleanUp grand total items
	 */
	public void cleanUpGrand() {
		getForecastUI().getForecastObj().getTotal().clear();
		getAddItems().clear();
	}


	public List<ForecastingTotal> getOrders() {
		return orders;
	}


	public void setOrders(List<ForecastingTotal> orders) {
		this.orders = orders;
	}


	/**
	 * For ALL_TOTAL items should be taken from other orders - accelerated and 
	 * @return
	 */
	public void addOrder(ForecastingTotal order){
		this.orders.add(order);
		order.addPropertyChangeListener("costOrderTotal", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				recalcItemsFromOrders();

			}
		});
	}


	/**
	 * Recalc current items from the suborders item
	 * currently valid only for total order
	 */
	public void recalcItemsFromOrders() {
		if(getOrderTotal() == ALL_TOTAL){
			cleanUpGrand();
			for(ForecastingTotal ord : getOrders()){
				addOrSum(getForecastUI().getForecastObj().getTotal(), ord.getAddItems());
			}
			this.addItems = getForecastUI().getTotalOrderItems();
			recalcTotal();
			firePropertyChange("addItems", null, getAddItems());
		}

	}



	/**
	 * Add items to target - if item exists, then add sum only, else add item
	 * @param target
	 * @param items
	 */
	private void addOrSum(List<ForecastingTotalItem> target,
			List<ForecastingTotalItemUIAdapter> items) {
		boolean found = false;
		for(ForecastingTotalItemUIAdapter item  :items){
			found = false;
			for(ForecastingTotalItem trgIt : target){
				if(trgIt.getItem().equalsIgnoreCase(item.getItem())){
					trgIt.setValue(trgIt.getValue().add(item.getValue()));
					found = true;
				}
			}
			if(!found){
				ForecastingTotalItemUIAdapter totUi = item.clone();
				totUi.getFcItemObj().setIsValue(true);  // for grand total - only by value
				if(item.clone().getValue().compareTo(BigDecimal.ZERO) != 0){
					target.add(totUi.getFcItemObj());
					//System.out.println("added " + item.getItem());
				}
			}
		}

	}


	/**
	 * @return the forecastUI
	 */
	public ForecastUIAdapter getForecastUI() {
		return forecastUI;
	}



	/**
	 * @param forecastUI the forecastUI to set
	 */
	public void setForecastUI(ForecastUIAdapter forecastUI) {
		this.forecastUI = forecastUI;
	}



	/**
	 * Recalculate add items and add change listeners
	 */
	private void recalcAddItems() {
		for(ForecastingTotalItemUIAdapter ftiU : getAddItems()){
			ftiU.addPropertyChangeListener("perCents", new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (!lockLocalListener){
						recalcByPercents(selectedTotalItem);
						if(selectedTotalItem != null){
							firePropertyChange("itemValue", null, selectedTotalItem.getValue());
						}
					}
				}
			});
			ftiU.addPropertyChangeListener("value", new PropertyChangeListener(){
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (!lockLocalListener){
						recalcByValue(selectedTotalItem);
						if(selectedTotalItem != null){
							firePropertyChange("itemValue", null, selectedTotalItem.getValue());
						}
					}
				}
			});		
			recalcTotalItem(ftiU);
		}
	}

	/**
	 * @return the selectedTotalItem
	 */
	public ForecastingTotalItemUIAdapter getSelectedTotalItem() {
		return selectedTotalItem;
	}

	/**
	 * Set selected total item and manage buttons
	 * @param selectedTotalItem the selectedTotalItem to set
	 */
	public void setSelectedTotalItem(ForecastingTotalItemUIAdapter selectedTotalItem) {
		this.selectedTotalItem = selectedTotalItem;
	}

	/**
	 * Recalculate total. From version 4 is using only to check changes in additional prices and/or medicines prices
	 * Use setOrderGrandTotal, getOrderGrandTotal instead 
	 * 
	 */
	public void recalcTotal() {
		BigDecimal oldValue = getCostOrderTotal();
		recalcMedTotal();
		BigDecimal ret = new BigDecimal(getMedTotal().toString());
		ret = ret.setScale(2,RoundingMode.HALF_UP);
		for(ForecastingTotalItemUIAdapter fti : this.addItems){
			BigDecimal bd = new BigDecimal(fti.getValue().toString());
			bd = bd.setScale(2,RoundingMode.HALF_UP);
			ret = ret.add(bd);
		}
		ret = ret.setScale(2,RoundingMode.HALF_UP);
		this.costOrderTotal = ret;
		firePropertyChange("costOrderTotal", oldValue, getCostOrderTotal());
		//firePropertyChange("formattedGrandTotal", oldValue, getFormattedGrandTotal());

	}
	/**
	 * In use from version 4. Calls from DeliveryOrdersControl
	 * @return
	 */
	public BigDecimal getOrderGrandTotal(){
		return orderGrandTotal;
	}
	/**
	 * In use from version 4. Calls from DeliveryOrdersControl
	 * @return
	 */
	public void setOrderGrandTotal(BigDecimal _orderGrandTotal){
		BigDecimal oldValue = getOrderGrandTotal();
		orderGrandTotal = _orderGrandTotal;
		firePropertyChange("orderGrandTotal", oldValue, getOrderGrandTotal());
		firePropertyChange("formattedGrandTotal", oldValue, getFormattedGrandTotal());
	}


	/**
	 * Quantity of deliveries for this total
	 * @return
	 */
	public Integer getDeliveriesQuantity() {
		return deliveriesQuantity;
	}
	/**
	 * Quantity of deliveries for this total
	 */
	public void setDeliveriesQuantity(Integer deliveriesQuantity) {
		Integer oldValue = getDeliveriesQuantity();
		this.deliveriesQuantity = deliveriesQuantity;
		firePropertyChange("deliveriesQuantity", oldValue, getDeliveriesQuantity());
	}

	/**
	 * recalculate only medicines total
	 */
	protected void recalcMedTotal(){
		BigDecimal oldValue = getCostOrderTotal();
		String oldStr = getFormattedTotal();
		String oldStr1 = getFormattedGrandMedTotal();
		BigDecimal res = new BigDecimal(0);
		//if (checkInputData()){
		for(ForecastingTotalMedicine ftm : this.medItems){
			switch(this.orderTotal){
			case ALL_TOTAL:
				res = res.add(ftm.getTotalCost());
				break;
			case REGULAR_TOTAL:
				res = res.add(ftm.getRegularCost());
				break;
			case ACCEL_TOTAL:
				res = res.add(ftm.getAccelCost());
				break;
			default:
				res = res.add(ftm.getTotalCost());
			}
		}
		//}
		this.medTotal = res.setScale(2, RoundingMode.HALF_UP);
		firePropertyChange("medTotal", oldValue, getMedTotal());
		firePropertyChange("formattedTotal", oldStr, getFormattedTotal());
		firePropertyChange("formattedGrandMedTotal", oldStr1, getFormattedGrandMedTotal());
	}


	/**
	 * Total may be calculated only when user fills all data for reg and acc orders
	 * @return
	 */
	public boolean checkInputData() { //TODO remove it!!
		boolean res = true;
		for(ForecastingTotalMedicine ftm : this.medItems){
			if (ftm.getRegularQuant() > 0){
				if (ftm.getRegularCost().compareTo(BigDecimal.ZERO)<=0){
					res = false;
				}
			}
			if(ftm.getAccelQuant() > 0){
				if (ftm.getAccelCost().compareTo(BigDecimal.ZERO)<=0){
					res = false;
				}
			}
			if (!res){
				break;
			}
		}
		return res;
	}



	public int getOrderTotal() {
		return orderTotal;
	}



	public void setOrderTotal(int orderTotal) {
		this.orderTotal = orderTotal;
	}



	/**
	 * @return the medTotal
	 */
	public BigDecimal getMedTotal() {
		return medTotal;
	}
	/**
	 * @return the total
	 */
	public BigDecimal getCostOrderTotal() {
		return costOrderTotal;
	}
	/**
	 * Get total formatted to output
	 * @return
	 */
	public String getFormattedTotal(){
		DecimalFormat formatter = new DecimalFormat("###,###,###,##0.00");
		return Messages.getString("ForecastingDocumentWindow.order.submedtotal") +" "+ formatter.format(getMedTotal())+" (USD/$)";
	}

	/**
	 * Get total formatted to output
	 * @return
	 */
	public String getFormattedGrandMedTotal(){
		DecimalFormat formatter = new DecimalFormat("###,###,###,##0.00");
		return Messages.getString("ForecastingDocumentWindow.order.grandmedtotal") +" "+ formatter.format(getMedTotal())+" (USD/$)";
	}

	/**
	 * Get total formatted to output
	 * @return
	 */
	public String getFormattedGrandTotal(){
		DecimalFormat formatter = new DecimalFormat("###,###,###,##0.00");
		String s = getGrandTotalLabel();
		return  s+" "+ formatter.format(getOrderGrandTotal())+" (USD/$)";
	}
	/**
	 * Get label of grand total
	 * @return
	 */
	public String getGrandTotalLabel() {
		String s = Messages.getString("ForecastingDocumentWindow.order.subordertotal");
		if (isGrandTotal()){
			s = Messages.getString("ForecastingDocumentWindow.order.grandtotal");
		}else if(isAccelTotal()){
			s=Messages.getString("ForecastingDocumentWindow.order.subordertotalacc");
		}
		return s;
	}


	/**
	 * Re-read 
	 */
	public void renewAddItems() {
		List<ForecastingTotalItemUIAdapter> oldValue = getAddItems();
		switch(this.orderTotal){
		case ALL_TOTAL:
			this.addItems = this.forecastUI.getTotalOrderItems();
			break;
		case REGULAR_TOTAL:
			this.addItems = this.forecastUI.getRegOrderItems();
			break;
		case ACCEL_TOTAL:
			this.addItems = this.forecastUI.getAccOrderItems();
			break;
		default:
			this.addItems = this.forecastUI.getTotalOrderItems();
		}
		recalcAddItems();
		recalcTotal();
		firePropertyChange("addItems", oldValue, getAddItems());	
	}
	/**
	 * Get additional price items
	 * @return
	 */
	public List<ForecastingTotalItemUIAdapter> getAddItems() {
		if(this.addItems == null){
			renewAddItems();
		}
		return this.addItems;
	}
	/**
	 * @return the medItems
	 */
	public List<ForecastingTotalMedicine> getMedItems() {
		return medItems;
	}


	/**
	 * Recalculate total item based on item value and medicines total
	 * @param item item to recalculate
	 * 
	 */
	private void recalcTotalItem(ForecastingTotalItemUIAdapter item) {
		if (item.getFcItemObj().isIsValue()){
			recalcByValue(item);
		}else{
			recalcByPercents(item);
		}
	}

	/**
	 * Recalculate item percent by value
	 * @param item
	 */
	public void recalcByValue(ForecastingTotalItemUIAdapter item) {
		if(item == null) return;
		if(getMedTotal().compareTo(new BigDecimal(0)) == 0){
			item.setPerCents(new BigDecimal(0));
		}else{
			BigDecimal totBD = new BigDecimal(getMedTotal().toString());
			totBD = totBD.setScale(4, RoundingMode.HALF_UP);
			BigDecimal valBD = new BigDecimal(item.getValue().toString());
			valBD = valBD.setScale(4,RoundingMode.HALF_UP);
			BigDecimal hundred = getHundred();
			BigDecimal res = new BigDecimal(0);
			res = res.setScale(4, RoundingMode.HALF_UP);
			res = valBD.multiply(hundred);
			res = res.divide(totBD, RoundingMode.HALF_UP);
			res = res.setScale(2, RoundingMode.HALF_UP);
			lockLocalListener(true);
			item.setPerCents(res);
			lockLocalListener(false);
			item.setIsValue(true); // recalculate by value
		}
		recalcTotal();
	}
	/**
	 * Set flag to not run local listener on change percentage or value
	 * @param b
	 */
	private void lockLocalListener(boolean b) {
		this.lockLocalListener = b;

	}



	/**
	 * Recalculate item value by percents
	 * @param item
	 */
	public void recalcByPercents(ForecastingTotalItemUIAdapter item) {
		if (item == null) return;;
		BigDecimal totBD = new BigDecimal(getMedTotal().toString());
		BigDecimal res = calcPersItem(item, totBD);
		lockLocalListener(true);
		item.setValue(res);
		lockLocalListener(false);
		item.setIsValue(false); // recalculate by percents
		recalcTotal();
	}
	/**
	 * Rule to calculate additional cost for medCost based on data in item, if item in percents
	 * @param item
	 * @param medCost
	 * @return
	 */
	public BigDecimal calcPersItem(ForecastingTotalItemUIAdapter item, BigDecimal medCost) {
		medCost = medCost.setScale(4, RoundingMode.HALF_UP);
		BigDecimal persBD = new BigDecimal(item.getPerCents().toString());
		persBD = persBD.setScale(4, RoundingMode.HALF_UP);
		BigDecimal hundred = getHundred();
		BigDecimal res = new BigDecimal(0);
		res = res.setScale(4, RoundingMode.HALF_UP);
		res = medCost.multiply(persBD);
		res = res.divide(hundred, RoundingMode.HALF_UP);
		res = res.setScale(2, RoundingMode.HALF_UP);
		return res;
	}


	/**
	 * Simply get 100.0000 to the BigDecimal object
	 * @return
	 */
	private BigDecimal getHundred() {
		BigDecimal hundred = new BigDecimal("100");
		hundred = hundred.setScale(4, RoundingMode.HALF_UP);
		return hundred;
	}


	/**
	 * Add a new forecasting total item
	 * @param item item to add
	 */
	public void addItem(ForecastingTotalItem item) {
		switch(this.orderTotal){
		case ALL_TOTAL:
			getForecastUI().getForecastObj().getTotal().add(item);
			break;
		case REGULAR_TOTAL:
			getForecastUI().getForecastObj().getTotalR().add(item);
			break;
		case ACCEL_TOTAL:
			getForecastUI().getForecastObj().getTotalA().add(item);
			break;
		default:
			getForecastUI().getForecastObj().getTotal().add(item);
		}

	}
	/**
	 * Remove an item
	 * @param fTi item to remove
	 */
	public void removeItem(ForecastingTotalItemUIAdapter fTi) {
		int index = getAddItems().indexOf(fTi);
		if (index > -1){
			switch(this.orderTotal){
			case ALL_TOTAL:
				getForecastUI().getForecastObj().getTotal().remove(index);
				break;
			case REGULAR_TOTAL:
				getForecastUI().getForecastObj().getTotalR().remove(index);
				break;
			case ACCEL_TOTAL:
				getForecastUI().getForecastObj().getTotalA().remove(index);
				break;
			default:
				getForecastUI().getForecastObj().getTotal().remove(index);
			}
		}

	}
	/**
	 * get a commentary for the current total
	 * @return
	 */
	public String getComment(){
		switch(getOrderTotal()){
		case ALL_TOTAL:
			return getForecastUI().getForecastObj().getTotalComment2();
		case REGULAR_TOTAL:
			return getForecastUI().getForecastObj().getTotalComment3();
		case ACCEL_TOTAL:
			return getForecastUI().getForecastObj().getTotalComment4();
		default:
			return getForecastUI().getForecastObj().getTotalComment2();
		}
	}

	public void setComment(String comment){
		String oldValue = getComment();
		switch(getOrderTotal()){
		case ALL_TOTAL:
			getForecastUI().setTotalComment2(comment);
			break;
		case REGULAR_TOTAL:
			getForecastUI().setTotalComment3(comment);
			break;
		case ACCEL_TOTAL:
			getForecastUI().setTotalComment4(comment);
			break;
		default:
			getForecastUI().setTotalComment2(comment);
		}
		firePropertyChange("comment", oldValue, getComment());
	}


	/**
	 * Is it panel for grand total order
	 * @return
	 */
	public boolean isGrandTotal() {
		return getOrderTotal() == ALL_TOTAL;
	}

	public boolean isAccelTotal(){
		return getOrderTotal() == ACCEL_TOTAL;
	}
	/**
	 * Copy all pack sizes from accel to regular
	 * zero values will not copy
	 */
	public void copyPackSizesFromAccel() {
		for(ForecastingTotalMedicine med : getMedItems()){
			if(med.getPackSizeAccel()>0){
				med.setPackSize(med.getPackSizeAccel());
			}
		}
	}

	/**
	 * copy all pack prices from accel to regular
	 */
	public void copyPackPricesFromAccel() {
		for(ForecastingTotalMedicine med : getMedItems()){
			if(med.getPackPriceAccel().compareTo(BigDecimal.ZERO)>0){
				med.setPackPrice(med.getPackPriceAccel());
			}
		}
	}

	/**
	 * copy all pack prices from regular to accel
	 */
	public void copyPackPricesFromRegular() {
		for(ForecastingTotalMedicine med : getMedItems()){
			if(med.getPackPrice().compareTo(BigDecimal.ZERO)>0){
				med.setPackPriceAccel(med.getPackPrice());
			}
		}
	}
	/**
	 * copy all pack sizes from the accel to regular
	 */
	public void copyPackSizesFromRegular() {
		for(ForecastingTotalMedicine med : getMedItems()){
			if(med.getPackSize()>0){
				med.setPackSizeAccel(med.getPackSize());
			}
		}

	}

	/**
	 * Get title of the order
	 * @return
	 */
	public String getTitle(){
		switch(getOrderTotal()){
		case ALL_TOTAL:
			return Messages.getString("ForecastingDocumentWindow.order.total");
		case REGULAR_TOTAL:
			return Messages.getString("ForecastingDocumentWindow.order.regular");
		case ACCEL_TOTAL:
			return Messages.getString("ForecastingDocumentWindow.order.accel");
		default:
			return Messages.getString("ForecastingDocumentWindow.order.total");
		}
	}
	/**
	 * Fetch medicine item
	 * @param medicine
	 * @return medicine item or null if not found
	 */
	public ForecastingTotalMedicine fetchMedicineTotal(MedicineUIAdapter medicine) {
		for(ForecastingTotalMedicine medItem : getMedItems()){
			if(medItem.getMedicine().equals(medicine)){
				return medItem;
			}
		}
		return null;
	}


}
