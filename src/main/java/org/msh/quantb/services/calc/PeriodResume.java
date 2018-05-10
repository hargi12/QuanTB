package org.msh.quantb.services.calc;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.joda.time.LocalDate;
import org.msh.quantb.services.io.ForecastingResultUIAdapter;
import org.msh.quantb.services.mvp.Presenter;

/**
 * Medicine consumption resume for any period
 * It is same data for Lead Time or Forecasting periods
 * @author alexey
 *
 */
@SuppressWarnings("rawtypes")
public class PeriodResume implements Comparable {
	private Calendar from;
	private Calendar to;
	private Integer incomingBalance=0;
	private Integer transit=0;
	private BigDecimal consumedOld=BigDecimal.ZERO;
	private BigDecimal consumedNew=BigDecimal.ZERO;
	private Integer expired=0;
	private BigDecimal missing=BigDecimal.ZERO;
	private BigDecimal dispensed=BigDecimal.ZERO;

	/**
	 * Constructor for Calendar parameters
	 * @param _from begin period
	 * @param _to end period
	 */
	public PeriodResume(Calendar _from, Calendar _to){
		DateUtils.cleanTime(_from);
		DateUtils.cleanTime(_to);
		this.from = _from;
		this.to = _to;
	}
	
	public PeriodResume(LocalDate from, LocalDate to){
		this.from=from.toDateTimeAtStartOfDay().toCalendar(Locale.getDefault());
		this.to=to.toDateTimeAtStartOfDay().toCalendar(Locale.getDefault());
	}
	
	/**
	 * @return the incomingBalance
	 */
	public Integer getIncomingBalance() {
		return incomingBalance;
	}
	/**
	 * @param incomingBalance the incomingBalance to set
	 */
	public void setIncomingBalance(Integer incomingBalance) {
		this.incomingBalance = incomingBalance;
	}

	/**
	 * @return the from
	 */
	public Calendar getFrom() {
		return from;
	}
	/**
	 * @param from the from to set
	 */
	public void setFrom(Calendar from) {
		this.from = from;
	}
	/**
	 * @return the to
	 */
	public Calendar getTo() {
		return to;
	}
	/**
	 * @param to the to to set
	 */
	public void setTo(Calendar to) {
		this.to = to;
	}
	/**
	 * Get text representation of start period
	 * @return
	 */
	public String getFromTxt(){
		return DateUtils.formatDate(getFrom().getTime(), "MMM dd, yyyy");
	}
	/**
	 * Get text representation of end period
	 * @return
	 */
	public String getToTxt(){
		if(to.before(from)){
			return getFromTxt();
		}else{
			return DateUtils.formatDate(getTo().getTime(), "MMM dd, yyyy");
		}
	}
	/**
	 * Get text representation of days between start and end period
	 * @return
	 */
	public String getDaysBetweenPeriodTxt(){
		int days = getDaysBetweenPeriod();
		String string = String.valueOf(days) + " " + DateParser.getDaysLabel(days);
		return "(" + string + ")";
	}
	public int getDaysBetweenPeriod() {
		int days=0;
		if(!to.before(from)){
			Date fromD = getFrom().getTime();
			Date toD = getTo().getTime();
			days = DateUtils.daysBetween(fromD, toD)+1;
		}
		return days;
	}
	/**
	 * @return the transit
	 */
	public Integer getTransit() {
		return transit;
	}
	/**
	 * @param transit the transit to set
	 */
	public void setTransit(Integer transit) {
		this.transit = transit;
	}
	/**
	 * @return the consumedOld
	 */
	public BigDecimal getConsumedOld() {
		return consumedOld;
	}
	/**
	 * @param consumedOld the consumedOld to set
	 */
	public void setConsumedOld(BigDecimal consumedOld) {
		this.consumedOld = consumedOld;
	}
	/**
	 * @return the consumedNew
	 */
	public BigDecimal getConsumedNew() {
		return consumedNew;
	}
	/**
	 * @param consumedNew the consumedNew to set
	 */
	public void setConsumedNew(BigDecimal consumedNew) {
		this.consumedNew = consumedNew;
	}
	/**
	 * @return the expired
	 */
	public Integer getExpired() {
		return expired;
	}
	/**
	 * @param expired the expired to set
	 */
	public void setExpired(Integer expired) {
		this.expired = expired;
	}
	/**
	 * @return the missing
	 */
	public BigDecimal getMissing() {
		return missing;
	}
	/**
	 * @param missing the missing to set
	 */
	public void setMissing(BigDecimal missing) {
		this.missing = missing;
	}

	/**
	 * @return the dispensed
	 */
	public BigDecimal getDispensed() {
		return dispensed;
	}
	/**
	 * @param dispensed the dispensed to set
	 */
	public void setDispensed(BigDecimal dispensed) {
		this.dispensed = dispensed;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((consumedNew == null) ? 0 : consumedNew.hashCode());
		result = prime * result
				+ ((consumedOld == null) ? 0 : consumedOld.hashCode());
		result = prime * result + ((expired == null) ? 0 : expired.hashCode());
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result
				+ ((incomingBalance == null) ? 0 : incomingBalance.hashCode());
		result = prime * result + ((missing == null) ? 0 : missing.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		result = prime * result + ((transit == null) ? 0 : transit.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PeriodResume other = (PeriodResume) obj;
		if (consumedNew == null) {
			if (other.consumedNew != null) {
				return false;
			}
		} else if (!consumedNew.equals(other.consumedNew)) {
			return false;
		}
		if (consumedOld == null) {
			if (other.consumedOld != null) {
				return false;
			}
		} else if (!consumedOld.equals(other.consumedOld)) {
			return false;
		}
		if (expired == null) {
			if (other.expired != null) {
				return false;
			}
		} else if (!expired.equals(other.expired)) {
			return false;
		}
		if (from == null) {
			if (other.from != null) {
				return false;
			}
		} else if (!from.equals(other.from)) {
			return false;
		}
		if (incomingBalance == null) {
			if (other.incomingBalance != null) {
				return false;
			}
		} else if (!incomingBalance.equals(other.incomingBalance)) {
			return false;
		}
		if (missing == null) {
			if (other.missing != null) {
				return false;
			}
		} else if (!missing.equals(other.missing)) {
			return false;
		}
		if (to == null) {
			if (other.to != null) {
				return false;
			}
		} else if (!to.equals(other.to)) {
			return false;
		}
		if (transit == null) {
			if (other.transit != null) {
				return false;
			}
		} else if (!transit.equals(other.transit)) {
			return false;
		}
		return true;
	}
	/**
	 * account data in period
	 * @param fr
	 */
	public void account(ForecastingResultUIAdapter fr) {
		if(match(fr)){
			if (DateUtils.daysBetween(this.getFrom().getTime(), fr.getFrom().getTime()) == 0){
				//System.out.println("time " + fr.getFrom().getTime() + " -- " + fr.getAllAvailable());
				//begin period, account input balance
				BigDecimal balDec = fr.getAllAvailable().subtract(new BigDecimal(fr.getInOrders()));
				balDec = balDec.setScale(0, BigDecimal.ROUND_UP);
				this.setIncomingBalance(balDec.intValue()); //orders not accounted
			}
			//calc estimated consumed as sum consumed for all periods
			BigDecimal consNew = this.getConsumedNew().add(fr.getConsNew());
			BigDecimal consOld = this.getConsumedOld().add(fr.getConsOld());
			this.setConsumedNew(consNew);
			this.setConsumedOld(consOld);

			//calc transit as sum all transits
			int inOrder = fr.getInOrders();
			this.setTransit(inOrder + this.getTransit());
			//calc missing and expired as sum all missing and expired
			this.setMissing(fr.getMissing().add(this.getMissing()));
			this.setExpired(fr.getExpired() + this.getExpired());
			//calc dispensing
			BigDecimal disp = fr.getDispensing();
			this.setDispensed(this.getDispensed().add(disp));
		}

	}
	/**
	 * does result match period?
	 * @param fr result
	 * @return
	 */
	public boolean match(ForecastingResultUIAdapter fr) {
		DateUtils.cleanTime(fr.getFrom());
		DateUtils.cleanTime(fr.getTo());
		DateUtils.cleanTime(getTo());
		return (fr.getFrom().compareTo(this.getFrom()) >= 0) &&
				fr.getTo().compareTo(getTo())<=0;
	}

	/**
	 * Native sort order
	 */
	@Override
	public int compareTo(Object o) {
		if (o == null){
			return 1;
		}
		if(o instanceof PeriodResume){
			PeriodResume another = (PeriodResume) o;
			if (this.equals(another)){
				return 0;
			}else{
				return this.getFrom().compareTo(another.getFrom());
			}


		}else{
			return 1;
		}
	}
	/**
	 * For view only
	 * @return
	 */
	public Integer getConsumedOldInt() {
		return getConsumedOld().setScale(0, BigDecimal.ROUND_UP).intValueExact();
	}
	/**
	 * For view only
	 * @return
	 */
	public Integer getConsumedNewInt() {
		return getConsumedNew().setScale(0, BigDecimal.ROUND_UP).intValueExact();
	}

	public Integer getMissingInt() {
		return getMissing().setScale(0, BigDecimal.ROUND_UP).intValueExact();
	}
	/**
	 * Get right integer value of dispensing
	 * @return
	 */
	public int getDispensedInt() {
		BigDecimal ret = this.getDispensed();
		ret = ret.setScale(0, BigDecimal.ROUND_UP);
		return ret.intValue();
	}



}
