package org.msh.quantb.services.calc;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.msh.quantb.services.io.ForecastingResultUIAdapter;
import org.msh.quantb.services.io.MedicineUIAdapter;

/**
 * This class need to display one line in forecasting resume table
 * @author alexey
 *
 */
public class MedicineResume implements Comparable{
	private MedicineUIAdapter medicine;
	private Date orderDate =  null;
	private Date firstMissingDate = null;
	private PeriodResume leadPeriod;
	private PeriodResume reviewPeriod;
	private PeriodResume lastLeadTimeMonth;
	private PeriodResume firstReviewMonth;
	
	public MedicineResume(MedicineUIAdapter med){
		this.medicine = med;
	}
	
	/**
	 * @return the medicine
	 */
	public MedicineUIAdapter getMedicine() {
		return medicine;
	}
	/**
	 * @param medicine the medicine to set
	 */
	public void setMedicine(MedicineUIAdapter medicine) {
		this.medicine = medicine;
	}
	/**
	 * @return the orderDate
	 */
	public Date getOrderDate() {
		return orderDate;
	}
	/**
	 * @param orderDate the orderDate to set
	 */
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	
	
	
	/**
	 * @return the firstMissingDate
	 */
	public Date getFirstMissingDate() {
		return firstMissingDate;
	}

	/**
	 * @param firstMissingDate the firstMissingDate to set
	 */
	public void setFirstMissingDate(Date firstMissingDate) {
		this.firstMissingDate = firstMissingDate;
	}

	/**
	 * Get string representation of order date
	 * @return
	 */
	public String getOrderDateTxt(){
		String ret = "";
		if (getOrderDate() != null){
			ret = DateUtils.formatDate(getOrderDate(), "MMM dd, yyyy");
		}
		return ret;
	}
	/**
	 * @return the leadPeriod
	 */
	public PeriodResume getLeadPeriod() {
		return leadPeriod;
	}
	/**
	 * @param leadPeriod the leadPeriod to set
	 */
	public void setLeadPeriod(PeriodResume leadPeriod) {
		this.leadPeriod = leadPeriod;
	}

	/**
	 * @return the reviewPeriod
	 */
	public PeriodResume getReviewPeriod() {
		return reviewPeriod;
	}

	/**
	 * @param reviewPeriod the reviewPeriod to set
	 */
	public void setReviewPeriod(PeriodResume reviewPeriod) {
		this.reviewPeriod = reviewPeriod;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((reviewPeriod == null) ? 0 : reviewPeriod.hashCode());
		result = prime * result
				+ ((leadPeriod == null) ? 0 : leadPeriod.hashCode());
		result = prime * result
				+ ((medicine == null) ? 0 : medicine.hashCode());
		result = prime * result
				+ ((orderDate == null) ? 0 : orderDate.hashCode());
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
		MedicineResume other = (MedicineResume) obj;
		if (reviewPeriod == null) {
			if (other.reviewPeriod != null) {
				return false;
			}
		} else if (!reviewPeriod.equals(other.reviewPeriod)) {
			return false;
		}
		if (leadPeriod == null) {
			if (other.leadPeriod != null) {
				return false;
			}
		} else if (!leadPeriod.equals(other.leadPeriod)) {
			return false;
		}
		if (medicine == null) {
			if (other.medicine != null) {
				return false;
			}
		} else if (!medicine.equals(other.medicine)) {
			return false;
		}
		if (orderDate == null) {
			if (other.orderDate != null) {
				return false;
			}
		} else if (!orderDate.equals(other.orderDate)) {
			return false;
		}
		return true;
	}
	/**
	 * Sort by medicine. Equals if same medicine
	 */
	@Override
	public int compareTo(Object arg0) {
		if (arg0 == null) return -1;
		if (arg0 instanceof MedicineResume){
			MedicineResume another = (MedicineResume) arg0;
			return this.getMedicine().compareTo(another.getMedicine());
		}else return -1;
	}
	/**
	 * Account daily result in summary
	 * @param fr daily result
	 * @param leadTime number of months for order exec
	 */
	public void account(ForecastingResultUIAdapter fr, Integer leadTime) {
		this.getLeadPeriod().account(fr);
		this.getReviewPeriod().account(fr);
		this.getFirstReviewMonth().account(fr);
		this.getLastLeadTimeMonth().account(fr);
		//order date determine as first missing at review period
/*		if(fr.getMissing()>0){
			System.out.println(fr.getMonth());
		}*/
/*		if ((this.getOrderDate() == null) && (fr.getMissing()>0)
				&& (this.getReviewPeriod().match(fr))){*/
/*		//TODO DEBUG
		if(fr.getForecastingResult().getMonth().getYear()==2013 && fr.getForecastingResult().getMonth().getMonth()==10){
			System.out.println(fr.getMonth() + "-" + fr.getFromDay() +": cons "+	(fr.getConsNew()+fr.getConsOld())+" avail:" + fr.getAllAvailable());
		}
		//TODO DEBUG
*/		if ((this.getOrderDate() == null) && (fr.getMissing().compareTo(BigDecimal.ZERO)>0)){
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(fr.getFrom().getTime());
			DateUtils.cleanTime(cal);
			cal.add(Calendar.MONTH, leadTime.intValue() * -1);
			DateUtils.cleanTime(cal);
			this.setOrderDate(cal.getTime());
		}
		// for some reason determine first missing date
		if(this.getFirstMissingDate() == null && (fr.getMissing().compareTo(BigDecimal.ZERO)>0)){
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(fr.getFrom().getTime());
			DateUtils.cleanTime(cal);
			this.setFirstMissingDate(cal.getTime());
		}
	}
	/**
	 * Order quantity is missing without missing in lead time
	 * @return
	 */
	public BigDecimal getQuantityToProcured(){
		return this.getReviewPeriod().getMissing();
	}

	public Integer getQuantityToProcuredInt() {
		return getQuantityToProcured().setScale(0, BigDecimal.ROUND_UP).intValueExact();
	}
	/**
	 * Last lead time month
	 * @param lastLtMonth
	 */
	public void setLastLeadTimeMonth(PeriodResume lastLtMonth) {
		this.lastLeadTimeMonth = lastLtMonth;
	}
	/**
	 * Last lead time month
	 * @param lastLtMonth
	 */
	public PeriodResume getLastLeadTimeMonth() {
		return lastLeadTimeMonth;
	}
	/**
	 * First month of review period
	 * @param firstReviewMonth
	 */
	public void setFirstReviewMonth(PeriodResume firstReviewMonth) {
		this.firstReviewMonth = firstReviewMonth;
	}
	/**
	 * First month of review period
	 * @param firstReviewMonth
	 */
	public PeriodResume getFirstReviewMonth() {
		return firstReviewMonth;
	}
	
	
	
}
