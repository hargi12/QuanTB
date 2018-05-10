package org.msh.quantb.services.calc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.msh.quantb.model.forecast.ForecastingRegimenResult;
import org.msh.quantb.model.forecast.Month;
import org.msh.quantb.model.forecast.PhaseResult;
import org.msh.quantb.model.mvp.ModelFactory;
import org.msh.quantb.services.io.PhaseUIAdapter;
import org.msh.quantb.services.io.RegimenPeriod;
import org.msh.quantb.services.io.RegimenUIAdapter;

/**
 * This class intends to calculate case numbers for regimen given.
 * Both phases.
 * @author alexey
 *
 */
public class CasesCalculator {
	private List<BigDecimal> intensive = new ArrayList<BigDecimal>();
	private List<BigDecimal> continuous = new ArrayList<BigDecimal>();
	private List<List<BigDecimal>> others = new ArrayList<List<BigDecimal>>();

	private Calendar begin;
	private Calendar end;
	private RegimenUIAdapter regimen;
	private int othersLength;


	/**
	 * only valid constructor. Simply init arrays
	 * @param _regimen the regimen
	 * @param from from date inclusive
	 * @param to to date inclusive
	 */
	public CasesCalculator(Calendar _from, Calendar _to, RegimenUIAdapter _regimen){
		DateUtils.cleanTime(_from);
		DateUtils.cleanTime(_to);
		this.setBegin(_from);
		this.setEnd(_to);
		this.regimen = _regimen;
		othersLength = this.getRegimen().getAddPhases().size();
		for(int j=0; j<othersLength;j++){
			this.others.add(new ArrayList<BigDecimal>());
		}
		int length = DateUtils.daysBetween(this.getEnd().getTime(), this.getBegin().getTime());
		for(int i=0 ; i<=length; i++){
			intensive.add(new BigDecimal(0.00));
			continuous.add(new BigDecimal(0.00));
			for(List<BigDecimal> counters : this.others){
				counters.add(new BigDecimal(0.00));
			}
		}
	}




	/**
	 * @return the regimen
	 */
	public RegimenUIAdapter getRegimen() {
		return regimen;
	}

	/**
	 * @return the intensive
	 */
	public List<BigDecimal> getIntensive() {
		return intensive;
	}

	/**
	 * @param intensive the intensive to set
	 */
	public void setIntensive(List<BigDecimal> intensive) {
		this.intensive = intensive;
	}

	/**
	 * @return the continious
	 */
	public List<BigDecimal> getContinuous() {
		return continuous;
	}

	/**
	 * @param continious the continious to set
	 */
	public void setContinuous(List<BigDecimal> continious) {
		this.continuous = continious;
	}


	/**
	 * @return the others
	 */
	public List<List<BigDecimal>> getOthers() {
		return others;
	}

	/**
	 * @param others the others to set
	 */
	public void setOthers(List<List<BigDecimal>> others) {
		this.others = others;
	}

	/**
	 * @return the begin
	 */
	public Calendar getBegin() {
		return begin;
	}

	/**
	 * This method is private, because based od this value data structures build only in constructor
	 * @param begin the begin to set
	 */
	private void setBegin(Calendar begin) {
		this.begin = begin;
	}

	/**
	 * @return the end
	 */
	public Calendar getEnd() {
		return end;
	}

	/**
	 * This method is private, because based od this value data structures build only in constructor
	 * @param end the end to set
	 */
	private void setEnd(Calendar end) {
		this.end = end;
		// this adjustment is necessary. because last calc date is always day of month
		this.end.set(Calendar.DAY_OF_MONTH, this.end.getActualMaximum(Calendar.DAY_OF_MONTH));
	}

	/**
	 * add enrolled cases quantities to the data given
	 * @param from date when new case start treatment
	 * @param cases - cases quantity
	 */
	public void add(Calendar from, BigDecimal cases){
		//TODO only to the end of list!!!!
		Calendar nextFrom = addToPhase(from, cases, this.getRegimen().getIntensive().getPeriod(), this.getIntensive());
		if(nextFrom != null){
			nextFrom = addToPhase(nextFrom, cases, this.getRegimen().getContinious().getPeriod(), this.getContinuous());
			if (nextFrom != null){
				for(PhaseUIAdapter phUi : this.getRegimen().getAddPhases()){
					List<BigDecimal> counters = this.getOthers().get(phUi.getOrder()-3);
					nextFrom = addToPhase(nextFrom,cases,phUi.getPeriod(),counters);
					if (nextFrom == null){
						break;
					}
				}
			}
		}
	}

	/**
	 * add cases quantities to the counters
	 * @param from date from
	 * @param cases cases quantities
	 * @param period period to calculate length
	 * @param counters list of counters
	 * @return date when next phase will begin or null if this date is out of the calendar
	 */
	private Calendar addToPhase(Calendar from, BigDecimal cases, RegimenPeriod period,
			List<BigDecimal> counters) {
		Calendar to = period.calcDirect(from);
		Integer fromIndex = getIndex(from);
		Integer toIndex = getIndex(to);
		if (fromIndex != null){
			if (toIndex == null){
				toIndex = getIntensive().size()-1; // it's end
			}
			for(int i=fromIndex; i<=toIndex; i++){
				counters.set(i, counters.get(i).add(cases));
			}
			//System.out.println("from " +DateUtils.formatDate(from.getTime(), "dd-MM-yyyy") + " "+ fromIndex + " to " + toIndex);
			to.add(Calendar.DAY_OF_MONTH, 1);
			if (getIndex(to) != null){
				return to;
			}else{
				return null;
			}
		}else{
			return null;
		}
	}

	/**
	 * Get index of the calendar given 
	 * @param from
	 * @return index, if exists, or null otherwise
	 */
	public Integer getIndex(Calendar from) {
		if (DateUtils.compareDates(from, this.getBegin())>=0 && DateUtils.compareDates(from, this.getEnd())<=0){
			int index = DateUtils.daysBetween(from.getTime(), this.getBegin().getTime());
			return index;
		}
		return null;
	}

	/**
	 * get result by day for intensive phase. Returns 0, if not on boundaries
	 * @param cal day
	 * @return
	 */
	public BigDecimal getIntensiveByDay(Calendar cal){
		//System.out.println(DateUtils.formatDate(cal.getTime(), "dd.MM.yyyy"));
		int index = DateUtils.daysBetween(getBegin().getTime(), cal.getTime());
		if (index > 0 && cal.before(getBegin())) return new BigDecimal(0.00);
		if (index >= getIntensive().size()){
			return new BigDecimal(0.00);
		}else{
			return getIntensive().get(index);
		}
	}
	/**
	 * get result by day for continuous phase. Returns 0, if not on boundaries
	 * @param cal day
	 * @return
	 */
	public BigDecimal getContiniousByDay(Calendar cal) {
		int index = DateUtils.daysBetween(getBegin().getTime(), cal.getTime());
		if (index > 0 && cal.before(getBegin())) return new BigDecimal(0.00);
		if (index >= getContinuous().size()){
			return new BigDecimal(0.00);
		}else{
			return getContinuous().get(index);
		}
	}
	/**
	 * Create regimen result by index given
	 * @param modelFactory
	 * @param i index in result list
	 * @return result if index OK or null otherwise
	 */
	public ForecastingRegimenResult createResult(ModelFactory modelFactory,
			int i) {

		if (i< getIntensive().size()){
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(this.getBegin().getTime());
			cal.add(Calendar.DAY_OF_MONTH, i);
			Month month = modelFactory.createMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
			ForecastingRegimenResult res = modelFactory.createRegimenResult(month);
			res.setFromDay(cal.get(Calendar.DAY_OF_MONTH));
			res.setToDay(cal.get(Calendar.DAY_OF_MONTH));
			PhaseResult phResI = modelFactory.createPhaseResult(new BigDecimal(0.00), getIntensive().get(i));
			PhaseResult phResC = modelFactory.createPhaseResult(new BigDecimal(0.00), getContinuous().get(i));
			res.setIntensive(phResI);
			res.setContinious(phResC);
			for(List<BigDecimal> others : getOthers()){   // by add phases
				PhaseResult phResO = modelFactory.createPhaseResult(new BigDecimal(0.00), others.get(i));
				res.getAddPhases().add(phResO);
			}
			return res;
		}else{
			return null;
		}

	}


	/**
	 * Get the cases quantity for particular phase on particular date
	 * @param phaseNo phase number - 1 - intensive, 2 continuous, rest are additional
	 * @param date date
	 * @return cases quantity or null if date is wrong
	 */
	public BigDecimal getPhaseQByDate(int phaseNo, Calendar date) {
		Integer i = getIndex(date);
		if (i != null){
			if (phaseNo == 1){
				return this.intensive.get(i);
			}
			if (phaseNo == 2){
				return this.continuous.get(i);
			}
			return this.others.get(phaseNo-3).get(i);
		}else{
			return null;
		}
	}


}
