package org.msh.quantb.view.panel;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.jfree.data.category.DefaultCategoryDataset;
import org.msh.quantb.services.calc.ConsumptionMonth;
import org.msh.quantb.services.calc.MedicineConsumption;
import org.msh.quantb.services.io.MonthUIAdapter;
import org.msh.quantb.services.mvp.Messages;

/**
 * This class is responsible for a medicine on the dashboard
 * @author Alexey Kurasov
 *
 */
public class DashBoardRowModel {



	private static final long milissecondsInDay = 0x5265c00L;

	private String key;
	private int[] quantities;
	private int[] days;
	private int[] enrCases;
	private int[] expCases;

	private int color;
	private int slice;
	private int stock; // it is current stock on hand without ordered quantity
	private int initStock;
	private MedicineConsumption medCons;
	private Map<MonthUIAdapter, Integer> orders; //when orders will be arrived - row and month
	private boolean exactStock; //rarely we're having exact stock to cover whole month
	private Map<Integer, Integer> expired; //expired medicines for this row - row and quantity

	private int arrSize;


	/**
	 * Constructor
	 * @param _medCons - months quantity
	 */
	public DashBoardRowModel(MedicineConsumption _medCons){
		this.medCons = _medCons;
		arrSize = _medCons.getCons().size()*4;
		quantities = new int[arrSize];
		days = new int[arrSize];
		enrCases = new int[arrSize];
		expCases = new int[arrSize];
		cleanUp();
		key = medCons.getMed().getName()+"\n"+medCons.getMed().getStrength()+" "+medCons.getMed().getDosage();
		color = DashBoardModel.GRAY;
		slice=0;
		stock = medCons.getCons().iterator().next().getOnHandInt();
		initStock= stock;
		orders = new HashMap<MonthUIAdapter, Integer>();
		exactStock = false;
		expired = new HashMap<Integer, Integer>();
	}


	/**
	 * Clean up all results arrays
	 */
	public void cleanUp() {
		for(int i=0; i<quantities.length; i++){
			quantities[i]=0;
			days[i]=0;
			enrCases[i]=0;
			expCases[i]=0;
		}
	}



	public MedicineConsumption getMedCons() {
		return medCons;
	}



	public void setMedCons(MedicineConsumption medCons) {
		this.medCons = medCons;
	}



	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getColor() {
		return color;
	}

	/**
	 * If color changes, so we'll needed next slice
	 * @param color
	 */
	public void setColor(int color) {
		if (getColor() != color){
			if(!isFirstSliceEmpty()){
				setSlice(getSlice()+1);
			}
		}
		this.color = color;
	}
	/**
	 * Is this slice empty?
	 * @return
	 */
	private boolean isFirstSliceEmpty() {
		if(getCurrentRow()<4){
			return days[0] + days[1] + days[2] + days[3] == 0;  
		}else{
			return false; //it isn't first row
		}
	}


	public int getSlice() {
		return slice;
	}

	public void setSlice(int slice) {
		this.slice = slice;
	}


	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}



	public int getInitStock() {
		return initStock;
	}

	public void setInitStock(int initStock) {
		this.initStock = initStock;
	}



	public int[] getQuantities() {
		return quantities;
	}


	public void setQuantities(int[] quantities) {
		this.quantities = quantities;
	}





	public boolean isExactStock() {
		return exactStock;
	}


	public void setExactStock(boolean exactStock) {
		this.exactStock = exactStock;
	}


	public int[] getEnrCases() {
		return enrCases;
	}


	public void setEnrCases(int[] enrCases) {
		this.enrCases = enrCases;
	}


	public int[] getExpCases() {
		return expCases;
	}


	public void setExpCases(int[] expCases) {
		this.expCases = expCases;
	}


	/**
	 * Add month consumption value
	 * All logic is implemented here
	 * @param cons medicine consumption for a month
	 */
	public void addMonthConsumption(ConsumptionMonth cons){
		// calculate a stock on hand without ordered
		int tmpStock = getStock()-(cons.getConsAllInt() + cons.getExpired());
		if (tmpStock>=0){
			setStock(tmpStock);
			setExactStock(tmpStock==0);
		}else{
			setStock(0);
			setExactStock(false);
		}
		calculateColor(cons);
		addValue(days,cons.getMonth().getDays());
		addValue(quantities,cons.getConsAllInt());
		setMax(enrCases,cons.getOldCases());
		setMax(expCases, cons.getNewCases());
		if(cons.getOrder()>0){
			int orderRow = -1;  // order row is unknown yet
			if (isYellow()){
				orderRow = getCurrentRow();
			}
			this.orders.put(cons.getMonth(),new Integer(orderRow));
		}
		if(cons.getExpired()>0){
			Integer iRow = new Integer(getCurrentRow());
			if (this.expired.get(iRow) == null){
				this.expired.put(iRow,new Integer(cons.getExpired()));
			}else{
				this.expired.put(iRow, this.expired.get(iRow)+cons.getExpired());
			}
		}

	}

	/**
	 * Is this row yellow?
	 * @return
	 */
	private boolean isYellow() {
		return getCurrentRow() % DashBoardModel.YELLOW == 0;
	}


	/**
	 * Set the maximum to the array element in accordance with the color and the slice
	 * @param valArr the array
	 * @param bDValue
	 */
	private void setMax(int[] valArr, BigDecimal bDValue) {
		int intValue = bDValue.intValue();
		if(valArr[getCurrentRow()] < intValue){
			valArr[getCurrentRow()]=intValue;
		}
	}

	/**
	 * Add some value to the array in accordance with the color and the slice
	 * @param valArr array
	 * @param value
	 */
	private void addValue(int[] valArr, int value) {
		valArr[getCurrentRow()] = valArr[getCurrentRow()]+value;
	}

	/**
	 * Get current model row
	 * @return
	 */
	public int getCurrentRow() {
		return getSlice()*4 + getColor();
	}



	/**
	 * Calculate a color for the current bar
	 * @param cons
	 */
	public void calculateColor(ConsumptionMonth cons) {
		// I have nothing, but I don't need any
		if(cons.getMissingInt() == 0 &&
				cons.getConsAllInt()==0 && getStock() == 0 && cons.getOrder() == 0 ){
			setColor(DashBoardModel.GRAY);
		}

		// I have enough for consumption from my stock
		if(cons.getMissingInt()==0 &&
				(getStock()>0 || isExactStock()) && cons.getConsAllInt()>0){
			setColor(DashBoardModel.GREEN);
		}

		// I have enough for consumption but not from the stock, so from the order
		if( cons.getMissingInt() == 0 &&
				getStock()==0 && cons.getConsAllInt()>0 && !isExactStock()){
			setColor(DashBoardModel.YELLOW);
			resolveUnknownOrders();
		}

		// I havn't enough for consumption
		if (cons.getMissingInt() > 0){
			setColor(DashBoardModel.RED);
		}
	}

	/**
	 * Some orders may registered before, so
	 * resolve them as current
	 */
	private void resolveUnknownOrders() {
		for(MonthUIAdapter mUi : this.orders.keySet()){
			Integer iRow = this.orders.get(mUi);
			if (iRow.intValue() <0){
				this.orders.put(mUi, new Integer(getCurrentRow()));
			}
		}
	}


	/**
	 * Add row values to the dataset given
	 * @param dataSet
	 */
	public void addToDataSet(DefaultCategoryDataset dataSet) {
		cleanUp();
		for(ConsumptionMonth cm : getMedCons().getCons()){
			addMonthConsumption(cm);
		}
		for(int i=0; i<days.length; i++){
			String rowKey=new Integer(i).toString();
			switch(i){ // first four should be diff, because of legend
			case 0:
				rowKey=Messages.getString("ForecastingDocumentWindow.dashBoard.legend.gray");
				break;
			case 1:
				rowKey=Messages.getString("ForecastingDocumentWindow.dashBoard.legend.green");
				break;
			case 2:
				rowKey=Messages.getString("ForecastingDocumentWindow.dashBoard.legend.yellow");
				break;
			case 3:
				rowKey=Messages.getString("ForecastingDocumentWindow.dashBoard.legend.red");
				break;
			}
			dataSet.addValue(days[i]*milissecondsInDay,  rowKey, getKey());
		}

	}

	/**
	 * Get a tool tip string for the particular sub bar (row)
	 * @param row
	 * @return
	 */
	public String getToolTip(int row){
		return
				"<html>"+
				getOrdersTip(row)+
				getExpiredTip(row)+
				Messages.getString("ForecastingDocumentWindow.dashBoard.tooltip")+
				" "+
				new DecimalFormat("###,###,###,##0").format(getQuantities()[row]) +
				"<br>"+
				Messages.getString("ForecastingDocumentWindow.dashBoard.tooltip1") +
				" "+
				new DecimalFormat("###,###,###,##0").format(getEnrCases()[row])+"<br>"+
				Messages.getString("ForecastingDocumentWindow.dashBoard.tooltip2") +
				" "+
				new DecimalFormat("###,###,###,##0").format(getExpCases()[row])+ "<br>"+
				Messages.getString("ForecastingDocumentWindow.dashBoard.tooltip3") +
				" "+
				new DecimalFormat("###,###,###,##0").format(getEnrCases()[row]+getExpCases()[row])+
				"</html>";
	}
	/**
	 * 
	 * @param row - Row when medicine became expired
	 * @return
	 */
	private String getExpiredTip(int row) {
		String res = "";
		Integer iExp = this.expired.get(new Integer(row));
		if (iExp != null){
			res = "<b>" + Messages.getString("ForecastingDocumentWindow.dashBoard.expired") +" "+ new DecimalFormat("###,###,###,##0").format(iExp) +"</b><br>";
		}
		return res;
	}


	/**
	 * Get tip for order
	 * @param row row with order consumption
	 * @return
	 */
	private String getOrdersTip(int row) {
		String ret = "";
		for(MonthUIAdapter mUi : this.orders.keySet()){
			Integer iRow = this.orders.get(mUi);
			if (iRow.intValue() == row){
				ret = 	Messages.getString("ForecastingDocumentWindow.dashBoard.column.orderArrived")+
						" "+
						new SimpleDateFormat("MMM yyyy").format(mUi.getFirstDate()) +
						"<br>";
				break; // to avoid two or more orders, only first is valuable
			}
		}
		return ret;
	}

	/**
	 * Return warning for this row, for example "Has Expired"
	 * @param row
	 * @return
	 */
	public String getWarning(int row) {
		String ret = "";
		if (getExpiredTip(row).length()>0){
			//ret = Messages.getString("ForecastingDocumentWindow.dashBoard.warning.hasexpired");
			ret="(!)";
		}
		return ret;
	}



}
