package org.msh.quantb.services.calc;

import java.math.BigDecimal;
import java.util.List;

import org.msh.quantb.services.io.ForecastingTotalMedicine;
import org.msh.quantb.services.io.MonthUIAdapter;

public interface DeliveryCalculatorI {

	MonthUIAdapter getBegin();

	void setBegin(MonthUIAdapter begin);

	ConsumptionMonth getPrevCons();

	void setPrevCons(ConsumptionMonth prevCons);

	List<ConsumptionMonth> getConsumptions();

	void setConsumptions(List<ConsumptionMonth> consumptions);

	BigDecimal getOrderQuantity();

	void setOrderQuantity(BigDecimal orderQuantity);

	/**
	 * Execute calculations
	 */
	void exec();
	/**
	 * Recalc projected stock for set of consumption given
	 * @param consumptionSet
	 */
	void recalcPStocks(List<ConsumptionMonth> consumptionSet);

}