package org.msh.quantb.services.calc;

import java.util.List;

import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.ForecastingMedicineUIAdapter;
import org.msh.quantb.services.io.ForecastingTotalMedicine;
import org.msh.quantb.services.io.MonthUIAdapter;

/**
 * This is a factory for calculator related to logistic - delivery and logistic parameters.
 * This factory intends to allow user pick up between pessimistic and optimistic scenarios
 * @author Alex Kurasoff
 *
 */
public class LogisticCalculatorsFactory {

	/**
	 * Return ready to use delivery calculator. Currently is only one!!!!
	 * @param isPessimistic sort of scenario true - pessimistic (not in use now)
	 * @param importantMonth this month is necessary for particular implementation
	 * @param consumptions list of medicine consumptions
	 * @param forecastingMedicineUI current forecast
	 * @return calculator for scenario given
	 */
	public static DeliveryCalculatorI getDeliveryCalculator(boolean isPessimistic, MonthUIAdapter importantMonth, List<ConsumptionMonth> consumptions, ForecastingMedicineUIAdapter forecastingMedicineUI){
		return new DeliveryCalculatorP(forecastingMedicineUI, consumptions, importantMonth);
	}
	/**
	 * Return ready to use logistic calculator. Currently is only one!!!!
	 * @param fUi current forecasting
	 * @return calculator for scenario given
	 */
	public static LogisticCalculatorI getLolgisticCalculator(ForecastUIAdapter fUi){
		LogisticCalculatorI ret=null;
		ret = new LogisticCalculatorPessimist();
		ret.setForecastUI(fUi);
		return ret; 
	}
}
