package org.msh.quantb.services.calc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.msh.quantb.model.forecast.PricePack;
import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.ForecastingMedicineUIAdapter;
import org.msh.quantb.services.io.ForecastingTotalMedicine;
import org.msh.quantb.services.io.MonthUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
/**
 * Delivery calculator for a medicine given
 * @author Alex Kurasoff
 *
 */
public class DeliveryCalculatorP implements DeliveryCalculatorI {
	//monthly consumptions
	private List<ConsumptionMonth> consumptions;
	ConsumptionMonth prevCons = null;
	int lastLeadTimeDelivery = -1;
	int lastDelivery = -1;
	private MonthUIAdapter endLead;
	private ForecastingMedicineUIAdapter forecastMedicineUI;

	/**
	 * "Pessimistic" calculator for a medicine
	 * @param forecastingMedicineUI the forecast medicine
	 * @param _consumptions monthly consumption data for the medicine
	 * @param endLead end of lead time month
	 */
	public DeliveryCalculatorP(ForecastingMedicineUIAdapter forecastingMedicineUI, List<ConsumptionMonth> _consumptions, MonthUIAdapter endLead){
		this.forecastMedicineUI = forecastingMedicineUI;
		this.consumptions = _consumptions;
		this.endLead = endLead;
	}

	public ForecastingMedicineUIAdapter getForecastMedicineUI() {
		return forecastMedicineUI;
	}

	public void setForecastMedicineUI(ForecastingMedicineUIAdapter forecastMedicineUI) {
		this.forecastMedicineUI = forecastMedicineUI;
	}

	protected int getLastLeadTimeDelivery() {
		return lastLeadTimeDelivery;
	}


	protected void setLastLeadTimeDelivery(int lastLeadTimeDelivery) {
		this.lastLeadTimeDelivery = lastLeadTimeDelivery;
	}


	protected int getLastDelivery() {
		return lastDelivery;
	}


	protected void setLastDelivery(int lastDelivery) {
		this.lastDelivery = lastDelivery;
	}


	protected MonthUIAdapter getEndLead() {
		return endLead;
	}


	protected void setEndLead(MonthUIAdapter endLead) {
		this.endLead = endLead;
	}


	@Override
	public MonthUIAdapter getBegin() {
		if(getConsumptions() != null && getConsumptions().size()>0){
			return getConsumptions().get(0).getMonth();
		}else{
			return null;
		}
	}

	@Override
	public void setBegin(MonthUIAdapter begin) {
		throw new IllegalArgumentException(Messages.getString("Begin of period is read only"));
	}

	@Override
	public ConsumptionMonth getPrevCons() {
		return this.prevCons;
	}

	@Override
	public void setPrevCons(ConsumptionMonth prevCons) {
		this.prevCons=prevCons;

	}

	@Override
	public List<ConsumptionMonth> getConsumptions() {
		return consumptions;
	}

	@Override
	public void setConsumptions(List<ConsumptionMonth> consumptions) {
		this.consumptions = consumptions;

	}

	@Override
	public BigDecimal getOrderQuantity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOrderQuantity(BigDecimal orderQuantity) {
		// TODO Auto-generated method stub

	}
	/**
	 * Execute:
	 * <ul>
	 * <li>Ideal calculation, it assumes pack size is 1 unit
	 * <li>Store result of ideal calculation
	 * <li>Real calculation, it assumes real pack sizes
	 * <li>Make adjustments in accordance with the order data
	 * </ul>
	 */
	@Override
	public void exec() {
		execIt(true);
		saveIdealAndClean();
		execIt(false);
		adjustFromOrder(getForecastMedicineUI().getFcMedicineObj().getPackOrder());

	}

	/**
	 * Order may contains adjustment coefficient
	 * We will apply it to quantity in units and recalculate quantity in packs
	 * @param pricePack
	 */
	private void adjustFromOrder(PricePack pricePack) {
		BigDecimal delivery = BigDecimal.ZERO;
		if(pricePack != null){
			for(ConsumptionMonth cM : getConsumptions()){
				if(cM.hasDelivery()){
					BigDecimal pers=null;
					int pack = 1;
					if(isLeadTime(cM)){
						pers=pricePack.getAdjustAccel();
						pack=pricePack.getPackAccel();
					}else{
						pers=pricePack.getAdjust();
						pack=pricePack.getPack();
					}
					delivery = ForecastingCalculation.calcPercents(cM.getDelivery(),pers);
					cM.setPacks(DeliveryCalculatorP.calckPacks(delivery, pack));
					cM.setDelivery(cM.getPacks().multiply(new BigDecimal(pack)));
				}
			}
		}
	}

	/**
	 * Save results of ideal calculations and clean calc result
	 */
	private void saveIdealAndClean() {
		for(ConsumptionMonth cM : getConsumptions()){
			cM.setIdealDelivery(cM.getDelivery());
			cM.setDelivery(BigDecimal.ZERO);
			cM.setpStock(BigDecimal.ZERO);
			setPrevCons(null);
			setLastDelivery(-1);
			setLastLeadTimeDelivery(-1);
		}
	}

	/**
	 * Execute ideal or real delivery calculation
	 * @param ideal true id ideal
	 */
	public void execIt(boolean ideal) {
		int i=0;
		for(ConsumptionMonth cM : getConsumptions()){
			account(cM, isLeadTime(cM), ideal);
			if(getEndLead() != null){
				if(isLeadTime(cM)){
					if(cM.getDelivery().compareTo(BigDecimal.ZERO)>0){
						setLastLeadTimeDelivery(i);
					}
				}
			}
			if(cM.getDelivery().compareTo(BigDecimal.ZERO)>0){
				setLastDelivery(i);
			}
			i++;
		}
	}
	/**
	 * Is this consumption in lead time
	 * @param cM
	 * @return
	 */
	public boolean isLeadTime(ConsumptionMonth cM) {
		return cM.getMonth().compareTo(getEndLead())<=0;
	}


	/**
	 * Calculate prognosis stock and check delivery condition
	 * @param cM
	 * @param isLeadTime 
	 * @param ideal true without pack constraints, false with pack constraints
	 */
	private void account(ConsumptionMonth cM, boolean isLeadTime, boolean ideal) {
		if(deliveryCondition(cM)){
			if(ideal){
				cM.setDelivery(calcDelivery(cM));
			}else{
				cM.setDelivery(calcPackDelivery(cM));
			}
		}
		calcPStock(cM);

	}

	/**
	 * Adjust delivery to whole packs, because delivery in partial packs is impossible
	 * If pack size is undefined, assume pack size is 1
	 * @param cM consumption in month
	 * @return 
	 */
	public BigDecimal calcPackDelivery(ConsumptionMonth cM){
		BigDecimal exactDelivery = calcDelivery(cM);
		int packSize=1;
		if(getForecastMedicineUI().getFcMedicineObj().getPackOrder() != null){
			packSize = getForecastMedicineUI().getFcMedicineObj().getPackOrder().getPack();
		}
		if (isLeadTime(cM)){
			if(getForecastMedicineUI().getFcMedicineObj().getPackOrder() != null){
				packSize = getForecastMedicineUI().getFcMedicineObj().getPackOrder().getPackAccel();
			}
		}
		if(packSize==0){
			packSize=1;
		}
		BigDecimal packs = calckPacks(exactDelivery, packSize);
		return packs.multiply(new BigDecimal(packSize));
	}

	/**
	 * Calc packs for units
	 * @param quantity in units
	 * @param packSize pack size in units
	 * @return
	 */
	public static BigDecimal calckPacks(BigDecimal quantity, int packSize){
		if(packSize == 0){
			packSize=1;   //brave assumption to avoid divide on zero
		}
		return quantity.divide(new BigDecimal(packSize),0, RoundingMode.UP);
	}

	/**
	 * Calculate projected incoming stock for consumption given
	 * @param cM
	 */
	private void calcPStock(ConsumptionMonth cM) {
		cM.setpStock(calculateIncoming(cM));
		setPrevCons(cM);
	}

	/**
	 * Calculate delivery quantity if delivery condition has occurred
	 * @param cM
	 * @return
	 */
	private BigDecimal calcDelivery(ConsumptionMonth cM) {
		BigDecimal incoming = calculateIncoming(cM);
		BigDecimal minStock = cM.getMinStock().subtract(incoming);
		BigDecimal missing = cM.getConsAll().add(new BigDecimal(cM.getExpired()).subtract(incoming));
		if(minStockCondition(cM) && !missingCondition(cM)){
			return minStock;
		}
		if(!minStockCondition(cM) && missingCondition(cM)){
			return missing;
		}
		if(minStockCondition(cM) && missingCondition(cM)){
			if(minStock.compareTo(missing)>0){
				return minStock;
			}else{
				return missing;
			}
		}
		return BigDecimal.ZERO;
	}

	/**
	 * Determine delivery condition
	 * @param cM
	 * @return
	 */
	private boolean deliveryCondition(ConsumptionMonth cM) {
		return minStockCondition(cM) || missingCondition(cM);
	}

	private boolean minStockCondition(ConsumptionMonth cM){
		BigDecimal incomStock = calculateIncoming(cM);
		return incomStock.compareTo(cM.getMinStock())<=0;
	}

	private boolean missingCondition(ConsumptionMonth cM){
		BigDecimal incomStock = calculateIncoming(cM);
		return incomStock.compareTo(cM.getConsAll().add(new BigDecimal(cM.getExpired())))<=0;
	}

	/**
	 * Calculate incoming stock based on previous incoming and previous stock motions
	 * @param cM for this incoming stock
	 * @return
	 */
	public BigDecimal calculateIncoming(ConsumptionMonth cM) {
		if(getPrevCons() == null){
			return cM.getOnHand().add(cM.getDelivery());
		}else{ 
			BigDecimal ret = getPrevCons().getpStock()
					.subtract(getPrevCons().getConsAll())
					.subtract(new BigDecimal(getPrevCons().getExpired()))
					.add(new BigDecimal(getPrevCons().getOrder()));
			if(ret.compareTo(BigDecimal.ZERO)>0){
				return ret.add(cM.getDelivery());
			}else{
				return cM.getDelivery();
			}
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
