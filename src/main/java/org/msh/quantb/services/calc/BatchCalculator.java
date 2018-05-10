package org.msh.quantb.services.calc;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import org.msh.quantb.model.forecast.ForecastingResult;
import org.msh.quantb.model.mvp.ModelFactory;
import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.ForecastingBatchUIAdapter;
import org.msh.quantb.services.io.ForecastingMedicineUIAdapter;
import org.msh.quantb.services.io.ForecastingOrderUIAdapter;
import org.msh.quantb.services.io.ForecastingResultUIAdapter;
import org.msh.quantb.services.io.MedicineUIAdapter;


/**
 * This class implements all medicines batch manipulations
 * @author alexey
 *
 */
public class BatchCalculator {
	private ForecastUIAdapter forecastUI;
	private ModelFactory factory;
	/**
	 * Only valid constructor
	 * @param _forecastU
	 * @param minMaxValues 
	 */
	public BatchCalculator(ForecastUIAdapter _forecastU, ModelFactory _factory){
		this.forecastUI = _forecastU;
		this.factory = _factory;
	}
	
	

	public ForecastUIAdapter getForecastUI() {
		return forecastUI;
	}



	public void setForecastUI(ForecastUIAdapter forecastUI) {
		this.forecastUI = forecastUI;
	}



	public ModelFactory getFactory() {
		return factory;
	}



	public void setFactory(ModelFactory factory) {
		this.factory = factory;
	}

	/**
	 * set available field depends of day and month in result, f.e. not available medicine after batch expired date 
	 * @param batch 
	 * @param result
	 */
	private void determineAvailable(ForecastingBatchUIAdapter batch,
			ForecastingResultUIAdapter result) {
		//maybe need to open
		int year = result.getMonth().getYear();
		int month = result.getMonth().getMonth();
		int day = result.getFromDay();
		Calendar cal = batch.getAvailFrom();
		if ((cal.get(Calendar.YEAR) == year) && (cal.get(Calendar.MONTH) == month) && (cal.get(Calendar.DAY_OF_MONTH) == day)){
			batch.getForecastingBatchObj().setQuantityAvailable(new BigDecimal(batch.getQuantity()));
		}
	}


	/**
	 * consume medicines for each day<br>
	 * <ul>
	 * <li>Consume from appropriate batch or order
	 * <li>if no batch, add to missing
	 * <ul>
	 */
	public void consume(){
		List<ForecastingMedicineUIAdapter> fmL = forecastUI.getMedicines();
		for(ForecastingMedicineUIAdapter fm : fmL){
			List<ForecastingBatchUIAdapter> previous = null;
			int i =0;
			for (ForecastingResult frr : fm.getFcMedicineObj().getResults()){
				ForecastingResultUIAdapter fr = new ForecastingResultUIAdapter(frr);
				if (previous == null){
/*					if(fm.getMedicine().getNameForDisplayWithAbbrev().contains("penem")){
						System.out.println(fr);
					}*/
					initBatches(fm, fr);
/*					if(fm.getMedicine().getNameForDisplayWithAbbrev().contains("penem")){
						System.out.println(fr);
					}*/
				}else{
					createBatches(fr,previous);
				}
				BigDecimal rest = consumeBatches(fm,fr);
				if (rest.compareTo(BigDecimal.ZERO) > 0){
					frr.setMissing(rest);
				}
				previous = fr.getBatches();
				i++;
			}
		}
	}




	/**
	 * create batches from previous ones, recalculate available and add to result
	 * @param fr result
	 * @param previous previous batches
	 */
	private void createBatches(ForecastingResultUIAdapter fr,
			List<ForecastingBatchUIAdapter> previous) {
		fr.getBatches().clear();
		for(ForecastingBatchUIAdapter fbu : previous){
			ForecastingBatchUIAdapter clone = fbu.makeClone(factory);
			if (clone.getQuantityAvailable().compareTo(BigDecimal.ZERO) > 0){
				clone.setQuantityAvailable(clone.getQuantityAvailable().subtract(clone.getConsumptionInMonth()));
			}
			clone.setConsumptionInMonth(BigDecimal.ZERO);
			determineAvailable(clone, fr);
			clone.setQuantityExpired(0);  // account expiration only just in time
			fr.getForecastingResult().getBatches().add(clone.getForecastingBatchObj().getOriginal());
		}
	}

	/**
	 * Clone all batches from medicine and add to given regimen
	 * @param fm medicine
	 * @param fr regimen
	 */
	private void initBatches(
			ForecastingMedicineUIAdapter fm, ForecastingResultUIAdapter fr) {
		//cleanup
		fr.getBatches().clear();
		//batches
		for(ForecastingBatchUIAdapter b : fm.getBatchesToExpire()){
			ForecastingBatchUIAdapter clone = b.makeClone(factory);
			//very important, for some reasons calc results becomes stored!!!
			clone.setQuantityAvailable(new BigDecimal(clone.getQuantity()));
			clone.setQuantityExpired(0);
			clone.setConsumptionInMonth(BigDecimal.ZERO);	
			determineAvailable(clone, fr);
			fr.getForecastingResult().getBatches().add(clone.getForecastingBatchObj().getOriginal());
		}
		//orders
		for(ForecastingOrderUIAdapter o : fm.getOrders()){
			ForecastingBatchUIAdapter clone = o.getBatch().makeClone(factory);
			clone.setAvailFrom(o.getArrived());
			clone.getForecastingBatchObj().setQuantityAvailable(BigDecimal.ZERO);
			determineAvailable(clone, fr);
			fr.getForecastingResult().getBatches().add(clone.getForecastingBatchObj().getOriginal());
		}
	}

	/**
	 * try to consume medicine from batches placed in result<br>
	 * bathes as batches and extracted from orders
	 * if batch expire - expire it!
	 * @param fm medicine
	 * @param fr result with batches
	 * @param resNo seq number of this result
	 * @return quantity to replenish or zero if we have enough
	 */
	private BigDecimal consumeBatches(ForecastingMedicineUIAdapter fm,
			ForecastingResultUIAdapter fr) {
		BigDecimal consume = fr.getConsNew().add(fr.getConsOld());  //account consumption for both enrolled and expected
/*		BigDecimal consNew = fr.getConsNew().setScale(0, BigDecimal.ROUND_UP);
		BigDecimal consOld = fr.getConsOld().setScale(0, BigDecimal.ROUND_UP);
		BigDecimal consume = consNew.add(consOld);*/
/*		int i=0;
		if(fm.getMedicine().getNameForDisplay().contains("Eth") && i<11){
			System.out.println("'"+fr.getConsNew());
			i++;
		}*/
		List<ForecastingBatchUIAdapter> frBUI = fr.getBatches();    
		for(ForecastingBatchUIAdapter bt : frBUI){ //assume sorted by expire date
			//check expire condition
			if (bt.justExpired(fr.getMonth(),fr.getFromDay())){
				bt.expire();
			}else{
				BigDecimal avail = bt.getQuantityAvailable();
				if (avail.compareTo(BigDecimal.ZERO) > 0){ 			//we have something
					if (consume.compareTo(BigDecimal.ZERO) != 0){	//we still need consume
						if (consume.compareTo(avail)<=0){
							bt.getForecastingBatchObj().setConsumptionInMonth(consume); //all consumed from the batch!
							consume = BigDecimal.ZERO;				
						}else{
							bt.getForecastingBatchObj().setConsumptionInMonth(avail); //only avail consumed from this batch
							consume = consume.subtract(avail);	//maybe rest consumption will be from other batches, or not
						}
					}
				}
			}
		}

		return consume; //will be >0 if we need replenish!
	}

}
