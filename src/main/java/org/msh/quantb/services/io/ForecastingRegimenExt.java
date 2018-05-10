package org.msh.quantb.services.io;

import java.util.ArrayList;
import java.util.List;

import org.msh.quantb.model.forecast.ForecastingRegimen;
import org.msh.quantb.model.forecast.ForecastingRegimenResult;
import org.msh.quantb.model.forecast.MonthQuantity;
import org.msh.quantb.model.gen.Regimen;
import org.msh.quantb.services.mvp.Presenter;
/**
 * It's extension of ForecastingRegimen. This class intends to redefine behavior of the ascendant
 * Commentaries are placed only on new behavior methods
 * @author Alexey Kurasov
 *
 */
public class ForecastingRegimenExt{
	private ForecastingRegimen  original;

	/**
	 * Only valid constructor
	 * @param original
	 */
	public ForecastingRegimenExt(ForecastingRegimen original) {
		super();
		this.original = original;
	}



	public ForecastingRegimen getOriginal() {
		return original;
	}



	public void setOriginal(ForecastingRegimen original) {
		this.original = original;
	}


	/**
	 * Get percent of new cases if isExcludedNewCases is false
	 * otherwise return 0
	 * @return
	 */
	public float getPercentNewCases() {
		if(isExcludeNewCases()){
			return 0;
		}else{
			return original.getPercentNewCases();
		}
	}

	public void setPercentNewCases(float value) {
		original.setPercentNewCases(value);
	}

	public float getPercentCasesOnTreatment() {
		if (isExcludeCasesOnTreatment()){
			return 0;
		}else{
			return original.getPercentCasesOnTreatment();
		}
	}

	public void setPercentCasesOnTreatment(float value) {
		original.setPercentCasesOnTreatment(value);
	}

	public Regimen getRegimen() {
		return original.getRegimen();
	}

	public void setRegimen(Regimen value) {
		original.setRegimen(value);
	}

	public List<ForecastingRegimenResult> getResults() {
		return original.getResults();
	}
	/**
	 * Return real enrolled cases list if isExcludeCasesOnTreatment is false
	 * otherwise return list with zero quantitites
	 * @return
	 */
	public List<MonthQuantity> getCasesOnTreatment() {
		if (isExcludeCasesOnTreatment()){
			return createZeroCasesOnTreatment();
		}else{
			return original.getCasesOnTreatment();
		}
	}

	/**
	 * Create temporary list of enrolled cases with zero quantity for each month
	 * @return
	 */
	private List<MonthQuantity> createZeroCasesOnTreatment() {
		List<MonthQuantity> res = new ArrayList<MonthQuantity>();
		for (MonthQuantity mq : getOriginal().getCasesOnTreatment()){
			res.add(Presenter.getFactory().createMonthQuantity(mq.getMonth().getYear(), mq.getMonth().getMonth(), 0));
		}
		return res;
	}



	/**
	 * return real expected cases list if isExcludeNewCases is false
	 * otherwise return list with zero quantities
	 * @return
	 */
	public List<MonthQuantity> getNewCases() {
		if (isExcludeNewCases()){
			return createZeroNewCases();
		}else{
			return original.getNewCases();
		}
	}


	/**
	 * Create temporary list of cases on treatment with zero quantity for each month
	 * @return
	 */
	private List<MonthQuantity> createZeroNewCases() {
		List<MonthQuantity> res = new ArrayList<MonthQuantity>();
		for (MonthQuantity mq : getOriginal().getNewCases()){
			res.add(Presenter.getFactory().createMonthQuantity(mq.getMonth().getYear(), mq.getMonth().getMonth(), 0));
		}
		return res;
	}



	public boolean isExcludeNewCases() {
		return original.isExcludeNewCases();
	}

	public void setExcludeNewCases(boolean value) {
		original.setExcludeNewCases(value);
	}

	public boolean isExcludeCasesOnTreatment(){
		return original.isExcludeCasesOnTreatment();
	}

	public void setExcludeCasesOnTreatment(boolean value){
		original.setExcludeCasesOnTreatment(value);
	}

}
