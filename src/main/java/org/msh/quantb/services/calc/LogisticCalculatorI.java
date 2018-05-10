package org.msh.quantb.services.calc;

import java.util.List;

import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.ForecastingTotalMedicine;
import org.msh.quantb.services.io.MedicineUIAdapter;
/**
 * It is common inteface for different order quantity calculation strategies
 * @author Alex Kurasoff
 *
 */
public interface LogisticCalculatorI {

	ForecastUIAdapter getForecastUI();

	void setForecastUI(ForecastUIAdapter forecastUI);
	/**
	 * Execute the calculator
	 * @param medConsList
	 */
	void exec(List<MedicineConsumption> medConsList);
	/**
	 * Recalculate Stock projected for given set of consumptions
	 * @param consumptionSet
	 * @param medicineUIAdapter 
	 */
	void recalcPStocks(List<ConsumptionMonth> consumptionSet, MedicineUIAdapter medicineUIAdapter);

}