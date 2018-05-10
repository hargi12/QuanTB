package org.msh.quantb.services.calc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.MedicineUIAdapter;
import org.msh.quantb.services.io.MonthUIAdapter;

/**
 * Apply min and max constraints as well as delivery schedule
 * @author Alex Kurasoff
 *
 */
public class LogisticCalculatorPessimist implements LogisticCalculatorI {
	private ForecastUIAdapter forecastUI;
	private DeliveryCalculatorI dCalc;
	@Override
	public ForecastUIAdapter getForecastUI() {
		return this.forecastUI;
	}

	@Override
	public void setForecastUI(ForecastUIAdapter forecastUI) {
		this.forecastUI = forecastUI;

	}

	public DeliveryCalculatorI getdCalc() {
		return dCalc;
	}

	public void setdCalc(DeliveryCalculatorI dCalc) {
		this.dCalc = dCalc;
	}

	@Override
	/**
	 * Calculate base deliveries to accCons, regCons and allCons - ConsumptionMonth for all medicines
	 * @param medConsList
	 */
	public void exec(List<MedicineConsumption> medConsList) {
		if(medConsList.size()>0){
			for(MedicineConsumption mC : medConsList){
				mC.cleanMinMaxDeliv();
			}
			MonthUIAdapter beginFC = medConsList.get(0).getExactFirstForecast().getMonth();
			MonthUIAdapter endLead = medConsList.get(0).getExactLastLead().getMonth();
			int firstFc=-1;
			int lastLt=-1;
			int index=0;
			//slice all consumption to accelerated and regular!
			for(ConsumptionMonth cM : medConsList.get(0).getCons()){
				if(cM.getMonth().compareTo(beginFC)==0){
					firstFc=index;
				}
				if(cM.getMonth().compareTo(endLead)==0){
					lastLt=index;
				}
				index++;
			}
			for(MedicineConsumption mC : medConsList){
				List<ConsumptionMonth> accCons = new ArrayList<ConsumptionMonth>();
				List<ConsumptionMonth> regCons = new ArrayList<ConsumptionMonth>();
				List<ConsumptionMonth> allCons = mC.getCons();
				// calculate min and max for all consumptions 
				for(int i=0; i < allCons.size();i++){
					setMinMax(allCons,i);
				}
				// calculate deliveries
				MonthUIAdapter endLt = null;
				if(lastLt>=0){
					endLt = allCons.get(lastLt).getMonth();
				}
				dCalc = LogisticCalculatorsFactory.getDeliveryCalculator(true, endLt, allCons, getForecastUI().getMedicine(mC.getMed()));
				dCalc.exec();
				//divide all consumptions to accel and regular
				if(lastLt>0){
					for(int i=0;i<=lastLt;i++){
						accCons.add(allCons.get(i));
					}
				}
				if(lastLt == firstFc){
					firstFc++;  // put to lead time splited month
				}
				if(allCons.size()>firstFc){
					for(int i=firstFc;i<allCons.size();i++){
						regCons.add(allCons.get(i));
					}
				}
				//set delivery data for medicine consumption
				mC.setAccelDeliveries(accCons);
				mC.setRegularDeliveries(regCons);
			}
		}
	}

	/**
	 * Set projected min and max stock for position given
	 * Projected min and max does not use for calculation delivery, only for the chart
	 * @param consMonthList
	 * @param pos
	 */
	private void setMinMax(List<ConsumptionMonth> consMonthList, int pos) {
		int counter=0; //it is counter from pos to the end of list
		ConsumptionMonth cM = consMonthList.get(pos);
		BigDecimal lastResult = BigDecimal.ZERO;
		BigDecimal lastMissing = BigDecimal.ZERO;
		for(int i=pos; i<consMonthList.size();i++){
			counter = i-pos;
			lastResult = consMonthList.get(i).getConsAll();
			//BigDecimal expired = new BigDecimal(consMonthList.get(i).getExpired()); 20160902 do not add expired
			lastMissing = consMonthList.get(i).getMissing();
			//lastResult = consMonthList.get(i).getMissing();
			if(counter< getForecastUI().getMinStock()){
				cM.addProjMinStock(lastResult);//.add(expired));
				cM.addMinStock(lastResult);//.add(expired));
				cM.addMinMissing(lastMissing);
			}
			if(counter< getForecastUI().getSmartMaxStock()){
				cM.addProjMaxStock(lastResult);//.add(expired));
				cM.addMaxStock(lastResult);//.add(expired));
				cM.addMaxMissing(lastMissing);
			}
		}
		//to approximate projected minimum/maximum, add last month consumption necessary times
		for(int i=counter+1; i<getForecastUI().getMinStock();i++){
			cM.addProjMinStock(lastResult);
			cM.addMinStock(lastResult);
		}
		Integer border = getForecastUI().getMaxStock();
		if(border == 0){
			border = getForecastUI().getMinStock() * 2; //mul 2 to avoid monthly deliveries!
		}
		for(int i=counter+1; i<border;i++){
			cM.addProjMaxStock(lastResult);
			cM.addMaxStock(lastResult);
		}
	}

	@Override
	public void recalcPStocks(List<ConsumptionMonth> consumptionSet, MedicineUIAdapter med) {
		LogisticCalculatorsFactory.getDeliveryCalculator(true, getForecastUI().getLeadTimeEndMonth(), consumptionSet, getForecastUI().getMedicine(med)).recalcPStocks(consumptionSet);

	}



}
