package org.msh.quantb.services.calc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.MonthUIAdapter;

/**
 * Logistic constrains calculator
 * Calculate allowable min and max stock as well as deliverables schedule
 * @author Alex Kurasoff
 *@deprecated
 */
public class LogisticCalculator implements LogisticCalculatorI {
	private ForecastUIAdapter forecastUI=null;
	private DeliveryCalculatorI dCalc;

	/* (non-Javadoc)
	 * @see org.msh.quantb.services.calc.LogisticCalculatorI#getForecastUI()
	 */
	@Override
	public ForecastUIAdapter getForecastUI() {
		return forecastUI;
	}

	/* (non-Javadoc)
	 * @see org.msh.quantb.services.calc.LogisticCalculatorI#setForecastUI(org.msh.quantb.services.io.ForecastUIAdapter)
	 */
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

	/* (non-Javadoc)
	 * @see org.msh.quantb.services.calc.LogisticCalculatorI#exec(java.util.List)
	 */
	@Override
	public void exec(List<MedicineConsumption> medConsList){
		if(medConsList.size()>0){
			for(MedicineConsumption mC : medConsList){
				mC.cleanMinMaxDeliv();
			}
			MonthUIAdapter beginFC = medConsList.get(0).getExactFirstForecast().getMonth();
			MonthUIAdapter endLead = medConsList.get(0).getExactLastLead().getMonth();
			for(MedicineConsumption mC : medConsList){
				List<ConsumptionMonth> accCons = new ArrayList<ConsumptionMonth>();
				List<ConsumptionMonth> regCons = new ArrayList<ConsumptionMonth>();
				List<ConsumptionMonth> allCons = mC.getCons(); //assume sorted by dates asc and all months are exist!!!!
				int firstFc=0;
				int lastLt=0;
				int index=0;
				//slice all consumption to accelerated and regular!
				for(ConsumptionMonth cM : allCons){
					if(cM.getMonth().compareTo(beginFC)==0){
						firstFc=index;
					}
					if(cM.getMonth().compareTo(endLead)==0){
						lastLt=index;
					}
					index++;
				}
				if(lastLt>0){
					for(int i=0;i<=lastLt-1;i++){
						accCons.add(allCons.get(i));
					}
					mC.getExactLastLead().cleanMinMaxDeliv();
					accCons.add(lastLt, mC.getExactLastLead());
				}
				mC.getExactFirstForecast().cleanMinMaxDeliv();
				regCons.add(mC.getExactFirstForecast());
				if(allCons.size()>firstFc){
					for(int i=firstFc+1;i<allCons.size();i++){
						regCons.add(allCons.get(i));
					}
				}

				// calculate min and max for accelerated and regular for old style schedule
				for(int i=0; i<accCons.size(); i++){
					setMinMax(accCons, i);
				}
				for(int i=0; i<regCons.size(); i++){
					setMinMax(regCons, i);
				}
				// calculate min and max for all consumptions for new style schedule 
				for(int i=0; i < allCons.size();i++){
					setProjMinMax(allCons,i);
				}
				// correct last accelerated and first regular for chart!
				if(accCons.size()>0){
					mC.getExactLastLead().setMinFullStock(allCons.get(accCons.size()-1).getMinFullStock());
					mC.getExactLastLead().setMaxFullStock(allCons.get(accCons.size()-1).getMaxFullStock());
				}

				mC.getExactFirstForecast().setMinFullStock(allCons.get(accCons.size()).getMinFullStock());
				mC.getExactFirstForecast().setMaxFullStock(allCons.get(accCons.size()).getMaxFullStock());


				//calculate deliveries for old style schedule
				if(accCons.size()>0){
					setdCalc(LogisticCalculatorsFactory.getDeliveryCalculator(false, accCons.get(0).getMonth(), accCons));
					getdCalc().exec();
				}
				if(regCons.size()>0){
					setdCalc(LogisticCalculatorsFactory.getDeliveryCalculator(false, regCons.get(0).getMonth(), regCons));
					if(accCons.size()>0){
						getdCalc().setPrevCons(accCons.get(accCons.size()-1));
					}
					getdCalc().exec();
				}
				//set delivery data for medicine consumption
				mC.setAccelDeliveries(accCons);
				mC.setRegularDeliveries(regCons);
				//recalc all consumptions for chart
				ConsumptionMonth toCorrect = mC.getCons().get(lastLt);
				BigDecimal lastD = mC.getExactLastLead().getDelivery();
				BigDecimal minS = mC.getExactLastLead().getMinStock();
				BigDecimal maxS = mC.getExactLastLead().getMaxStock();
				BigDecimal pStock = mC.getExactLastLead().getpStock();
				toCorrect.setDelivery(lastD);
				toCorrect.setMinStock(minS);
				toCorrect.setMaxStock(maxS);
				toCorrect.setpStock(pStock);
				toCorrect = mC.getCons().get(firstFc);
				lastD = mC.getExactLastLead().getDelivery();
				BigDecimal firstD = mC.getExactFirstForecast().getDelivery();
				minS = mC.getExactFirstForecast().getMinStock();
				maxS = mC.getExactFirstForecast().getMaxStock();
				pStock = mC.getExactFirstForecast().getpStock();
				if(lastLt == firstFc){
					toCorrect.setDelivery(lastD.add(firstD));
				}else{
					toCorrect.setDelivery(firstD);
				}
				toCorrect.setMinStock(minS);
				toCorrect.setMaxStock(maxS);
				toCorrect.setpStock(pStock);



			}
		}
	}

	/**
	 * Set allowable min and max stock for position given
	 * @param consMonthList
	 * @param pos
	 */
	private void setMinMax(List<ConsumptionMonth> consMonthList, int pos) {

		for(int i=pos; i<consMonthList.size();i++){
			ConsumptionMonth cM = consMonthList.get(pos);
			if(i-pos< getForecastUI().getMinStock()){
				cM.addMinMissing(consMonthList.get(i).getMissing());
				cM.addMinStock(consMonthList.get(i).getConsAll());//.add(new BigDecimal(consMonthList.get(i).getExpired())));
			}
			if(i-pos< getForecastUI().getSmartMaxStock()){
				cM.addMaxMissing(consMonthList.get(i).getMissing());
				cM.addMaxStock(consMonthList.get(i).getConsAll());//.add(new BigDecimal(consMonthList.get(i).getExpired())));
			}
		}

	}

	/**
	 * Set projected min and max stock for position given
	 * Projected min and max does not use for calculation delivery, only for the chart
	 * @param consMonthList
	 * @param pos
	 */
	private void setProjMinMax(List<ConsumptionMonth> consMonthList, int pos) {
		int counter=0;
		ConsumptionMonth cM = consMonthList.get(pos);
		BigDecimal lastResult = BigDecimal.ZERO;
		for(int i=pos; i<consMonthList.size();i++){
			counter = i-pos;
			lastResult = consMonthList.get(i).getConsAll();//.add(new BigDecimal(consMonthList.get(i).getExpired()));
			if(counter< getForecastUI().getMinStock()){
				cM.addProjMinStock(lastResult);
			}
			if(counter< getForecastUI().getMaxStock()){
				cM.addProjMaxStock(lastResult);
			}
		}
		//to approximate projected minimum/maximum, add last month consumption necessary times
		for(int i=counter+1; i<getForecastUI().getMinStock();i++){
			cM.addProjMinStock(lastResult);
		}
		for(int i=counter+1; i<getForecastUI().getMaxStock();i++){
			cM.addProjMaxStock(lastResult);
		}

	}

	@Override
	public void recalcPStocks(List<ConsumptionMonth> consumptionSet) {
		//consumption set contains right deliveries
		LogisticCalculatorsFactory.getDeliveryCalculator(false, consumptionSet.get(0).getMonth(), consumptionSet).recalcPStocks(consumptionSet);
	}

}
