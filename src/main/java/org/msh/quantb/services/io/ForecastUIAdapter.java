package org.msh.quantb.services.io;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jdesktop.observablecollections.ObservableCollections;
import org.joda.time.LocalDate;
import org.msh.quantb.model.forecast.Forecast;
import org.msh.quantb.model.forecast.ForecastingMedicine;
import org.msh.quantb.model.forecast.ForecastingRegimen;
import org.msh.quantb.model.forecast.ForecastingRegimenResult;
import org.msh.quantb.model.forecast.ForecastingTotalItem;
import org.msh.quantb.model.forecast.Month;
import org.msh.quantb.model.forecast.MonthQuantity;
import org.msh.quantb.model.gen.DeliveryScheduleEnum;
import org.msh.quantb.model.gen.MedicineRegimen;
import org.msh.quantb.model.gen.Phase;
import org.msh.quantb.model.gen.RegimenTypesEnum;
import org.msh.quantb.model.mvp.ModelFactory;
import org.msh.quantb.services.calc.CalendarQuantity;
import org.msh.quantb.services.calc.DateParser;
import org.msh.quantb.services.calc.DateUtils;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.ForecastingRegimensNewCasesModel;
import org.msh.quantb.view.ForecastingRegimensTableModel;
import org.msh.quantb.view.panel.OrderScenarioEnum;

/**
 * Object Forecast adapted for UI
 * 
 * @author User
 * 
 */
public class ForecastUIAdapter extends AbstractUIAdapter {
	private Forecast forecastObj;
	private List<ForecastingMedicineUIAdapter> medicines;
	private List<ForecastingRegimenUIAdapter> regimes;
	private List<MonthQuantityUIAdapter> newCases;
	private List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();
	private List<String> propertyNames = new ArrayList<String>();
	private PropertyChangeListener regimenQuantityListener = null; //TODO implement getter!!!

	private PropertyChangeListener enrollQtyListener = null;
	private PropertyChangeListener regimeNewQtyListener;
	private PropertyChangeListener newQtyListener;
	private ForecastUIVerify forecastUIVerify;
	private Boolean dirty=true; //need to recalculate!!!

	/**
	 * Only valid constructor
	 * 
	 * @param forecast_
	 */
	public ForecastUIAdapter(Forecast forecast_) {
		forecastObj = forecast_;
		forecastUIVerify = new ForecastUIVerify(this);
		addLogic();
	}

	/**
	 * Add some logic for parameter change
	 */
	private void addLogic() {
		minStockBufStockLogic();

	}
	/**
	 * if min > 0, then BS set to 0 and hide it
	 * if min == 0, then unhide BS for user input
	 */
	private void minStockBufStockLogic() {
		//min stock and buffer stock
		if(getMinStock() > 0 && getBufferStockTime()>0){
			setBufferStockTime(0);
		}
		this.addPropertyChangeListener("minStock", new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if(getMinStock() > 0 && getBufferStockTime()>0){
					setBufferStockTime(0);
				}

			}
		});
	}

	/**
	 * @return the forecastObj
	 */
	public Forecast getForecastObj() {
		return forecastObj;
	}

	/**
	 * @param forecast_ the foracastObj to set
	 */
	public void setForecastObj(Forecast forecast_) {
		Forecast oldValue = getForecastObj();
		this.forecastObj = forecast_;
		firePropertyChange("forecastObj", oldValue, getForecastObj());
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.Forecast#getName()
	 */
	public String getName() {
		return forecastObj.getName();
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.Forecast#setName(String)
	 */
	public void setName(String value) {
		String old = getName();
		forecastObj.setName(value);
		firePropertyChange("name", old, getName());
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.Forecast#getReferenceDate()
	 */
	public Date getReferenceDt() {
		Calendar cal = forecastObj.getReferenceDate().toGregorianCalendar();
		DateUtils.cleanTime(cal);
		return forecastObj.getReferenceDate().toGregorianCalendar().getTime();
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.Forecast#setReferenceDate(XMLGregorianCalendar)
	 */
	public void setReferenceDt(Date value) {
		Calendar tmp = new GregorianCalendar();
		tmp.setTime(DateUtils.getcleanDate(value));
		XMLGregorianCalendar dtXML = null;
		try {
			dtXML = DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) tmp);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
		Date old = getReferenceDt(); //20160825 appropriate
		Calendar oldValue = getReferenceDate(); //20160825 appropriate
		forecastObj.setReferenceDate(dtXML);
		calcIniDate();
		firePropertyChange("referenceDt", old, getReferenceDt()); //20160825 appropriate
		firePropertyChange("referenceDate", oldValue, getReferenceDate()); //20160825 appropriate
	}
	/**
	 * Calculate ini date in accordance with current rules
	 */
	public void calcIniDate(){
			Calendar cal = getFirstFCDate();
			cal.add(Calendar.MONTH, getLeadTime());
			setIniDate(cal);

	}
	

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.Forecast#getReferenceDate()
	 */
	public Calendar getReferenceDate() {
		if (forecastObj.getReferenceDate() != null) {			
			Calendar tmp = GregorianCalendar.getInstance();
			XMLGregorianCalendar xCal = forecastObj.getReferenceDate();
			xCal.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
			tmp.setTime(DateUtils.getcleanDate(xCal.toGregorianCalendar()));
			return tmp;
		} else {
			return null;
		}
	}


	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.Forecast#setReferenceDate(XMLGregorianCalendar)
	 */
	public void setReferenceDate(Calendar value) {
		value.setTime(DateUtils.getcleanDate(value));
		XMLGregorianCalendar dtXML = null;
		try {
			dtXML = DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) value);
			dtXML.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
		Calendar old = getReferenceDate(); //20160825 appropriate
		forecastObj.setReferenceDate(dtXML);
		firePropertyChange("referenceDate", old, getReferenceDate().getTime()); //20160825 appropriate
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.Forecast#getIniDate()
	 */
	public Calendar getIniDate() {
		XMLGregorianCalendar xcal = forecastObj.getIniDate();
		if (xcal != null) {
			xcal.setTimezone(DatatypeConstants.FIELD_UNDEFINED );
			Calendar tmp = xcal.toGregorianCalendar();
			tmp.setTime(DateUtils.getcleanDate(tmp));
			return tmp;
		} else {
			return null;
		}
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.Forecast#setIniDate(XMLGregorianCalendar)
	 */
	public void setIniDate(Calendar value) {
		value.setTime(DateUtils.getcleanDate(value));
		XMLGregorianCalendar dtXML = null;
		try {
			dtXML = DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) value);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
		Calendar old = getIniDate();
		Date oldDt = getIniDt();
		dtXML.setTimezone(DatatypeConstants.FIELD_UNDEFINED );
		forecastObj.setIniDate(dtXML);
		firePropertyChange("iniDate", old, getIniDate());
		firePropertyChange("iniDt", oldDt, getIniDt());
	}


	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.Forecast#getIniDate()
	 */
	public Date getIniDt() {
		if (forecastObj.getIniDate() != null) {
			XMLGregorianCalendar xcal = forecastObj.getIniDate();
			xcal.setTimezone(DatatypeConstants.FIELD_UNDEFINED );
			Calendar cal = xcal.toGregorianCalendar();
			DateUtils.cleanTime(cal);
			return cal.getTime();
		} else {
			return null;
		}
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.Forecast#setIniDate(XMLGregorianCalendar)
	 */
	public void setIniDt(Date value) {
		XMLGregorianCalendar dtXML = null;
		if (value != null) {
			Calendar tmp = new GregorianCalendar();
			tmp.setTime(DateUtils.getcleanDate(value));
			try {
				dtXML = DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) tmp);
			} catch (DatatypeConfigurationException e) {
				throw new RuntimeException(e);
			}
		}
		Calendar oldCal = getIniDate();
		Date old = getIniDt();
		dtXML.setTimezone(DatatypeConstants.FIELD_UNDEFINED );
		forecastObj.setIniDate(dtXML);
		firePropertyChange("iniDt", old, getIniDt());
		firePropertyChange("iniDate", oldCal, getIniDate());
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.Forecast#getEndDate()
	 */
	public Calendar getEndDate() {
		if (forecastObj.getEndDate() != null) {
			XMLGregorianCalendar xcal = forecastObj.getEndDate();
			xcal.setTimezone(DatatypeConstants.FIELD_UNDEFINED );
			Calendar cal =xcal.toGregorianCalendar();
			cal.setTime(DateUtils.getcleanDate(cal));
			return cal;
		} else {
			return null;
		}
	}


	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.Forecast#setEndDate(XMLGregorianCalendar)
	 */
	public void setEndDate(Calendar value) {
		value.setTime(DateUtils.getcleanDate(value));
		XMLGregorianCalendar dtXML = null;
		try {
			dtXML = DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) value);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
		Calendar old = getEndDate();
		forecastObj.setEndDate(dtXML);
		firePropertyChange("endDate", old, getEndDate());
	}

	/**
	 * Get the last date of forecasting period, before the buffer stock
	 * @return
	 * @see org.msh.quantb.model.forecast.Forecast#getIniDate()
	 */
	public Date getEndDt() {
		if (forecastObj.getEndDate() != null) {
			XMLGregorianCalendar xCal = forecastObj.getEndDate();
			xCal.setTimezone(DatatypeConstants.FIELD_UNDEFINED );
			Calendar cal = xCal.toGregorianCalendar();
			DateUtils.cleanTime(cal);
			return cal.getTime();
		} else {
			return null;
		}
	}

	/**
	 * Set the last date of forecasting period, before the buffer stock
	 * @param value
	 * @see org.msh.quantb.model.forecast.Forecast#setIniDate(XMLGregorianCalendar)
	 */
	public void setEndDt(Date value) {
		Calendar tmp = new GregorianCalendar();
		tmp.setTime(DateUtils.getcleanDate(value));
		XMLGregorianCalendar dtXML = null;
		try {
			dtXML = DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) tmp);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
		Date old = getEndDt();
		dtXML.setTimezone(DatatypeConstants.FIELD_UNDEFINED );
		forecastObj.setEndDate(dtXML);
		firePropertyChange("endDt", old, getEndDt());
		firePropertyChange("endDate", old, getEndDate());
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.Forecast#getBufferStockTime()
	 */
	public Integer getBufferStockTime() {
		return forecastObj.getBufferStockTime();
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.Forecast#setBufferStockTime(int)
	 */
	public void setBufferStockTime(Integer value) {
		Integer old = getBufferStockTime();
		forecastObj.setBufferStockTime(value);
		firePropertyChange("bufferStockTime", old, getBufferStockTime());
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.Forecast#getLeadTime()
	 */
	public Integer getLeadTime() {
		return forecastObj.getLeadTime();
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.Forecast#setLeadTime(int)
	 */
	public void setLeadTime(Integer value) {
		Integer old = getLeadTime();
		forecastObj.setLeadTime(value);
		calcIniDate();
		firePropertyChange("leadTime", old, getLeadTime());
	}

	/**
	 * Get alphabetical sorted list of unigue medications
	 * 
	 * @return
	 * @see org.msh.quantb.model.forecast.Forecast#getMedicines()
	 */
	public List<ForecastingMedicineUIAdapter> getMedicines() {
		Set<ForecastingMedicineUIAdapter> tmp = new HashSet<ForecastingMedicineUIAdapter>();
		if (medicines == null) {
			for (ForecastingMedicine fm : getForecastObj().getMedicines()) {
				ForecastingMedicineUIAdapter fmUI = new ForecastingMedicineUIAdapter(fm);
				tmp.add(fmUI);
			}
		} else {
			tmp.addAll(medicines);
		}
		for (ForecastingRegimen fr : getForecastObj().getRegimes()) {
			for (MedicineRegimen fri : fr.getRegimen().getIntensive().getMedications()) {
				addMedicineFromPhase(tmp, fri);
			}
			for (MedicineRegimen frc : fr.getRegimen().getContinious().getMedications()) {
				addMedicineFromPhase(tmp, frc);
			}
			for(Phase ph :fr.getRegimen().getAddPhases()){
				for(MedicineRegimen fra : ph.getMedications()){
					addMedicineFromPhase(tmp, fra);
				}
			}
		}
		for (ForecastingMedicine fm : getForecastObj().getMedicines()) {
			ForecastingMedicineUIAdapter fmUI = new ForecastingMedicineUIAdapter(fm);
			tmp.add(fmUI);
		}
		this.medicines = ObservableCollections.observableList(new ArrayList<ForecastingMedicineUIAdapter>(tmp));
		//return modified to forecasting object
		this.getForecastObj().getMedicines().clear();
		for (ForecastingMedicineUIAdapter mu : this.medicines) {
			this.getForecastObj().getMedicines().add(mu.getFcMedicineObj());
		}
		Collections.sort(this.medicines);
		return this.medicines;
	}

	private void addMedicineFromPhase(Set<ForecastingMedicineUIAdapter> tmp,
			MedicineRegimen fri) {
		ForecastingMedicine fm = new ForecastingMedicine();
		fm.setMedicine(fri.getMedicine());
		ForecastingMedicineUIAdapter fmUI = new ForecastingMedicineUIAdapter(fm);
		tmp.add(fmUI);
	}



	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.Forecast#getRegion()
	 */
	public String getRegion() {
		return forecastObj.getRegion();
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.Forecast#setRegion(java.lang.String)
	 */
	public void setRegion(String value) {
		String oldValue = getRegion();
		forecastObj.setRegion(value);
		firePropertyChange("region", oldValue, getRegion());
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.Forecast#getComment()
	 */
	public String getComment() {
		String ret = forecastObj.getComment();
		if (ret == null) {
			return "";
		} else {
			return ret;
		}
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.Forecast#setComment(java.lang.String)
	 */
	public void setComment(String value) {
		//String oldValue = getComment();
		forecastObj.setComment(value);
		//firePropertyChange("comment", oldValue, getComment());
	}

	/**
	 * Set new values for forecasting medicines
	 * 
	 * @param value new forecasting medicines
	 */
	public void setMedicines(List<ForecastingMedicineUIAdapter> medicines) {
		List<ForecastingMedicineUIAdapter> oldValue = getMedicines();
		getForecastObj().getMedicines().clear();
		for (ForecastingMedicineUIAdapter fma : medicines) {
			getForecastObj().getMedicines().add(fma.getFcMedicineObj());
		}
		this.medicines = null; //init build new list anyway!
		firePropertyChange("medicines", oldValue, getMedicines());
	}

	/**
	 * Get regimens list and add property change listeners to each element
	 * 
	 * @return
	 * @see org.msh.quantb.model.forecast.Forecast#getRegimes()
	 */
	@SuppressWarnings("unchecked")
	public List<ForecastingRegimenUIAdapter> getRegimes() {
		List<ForecastingRegimenUIAdapter> tmp = new ArrayList<ForecastingRegimenUIAdapter>();
		for (ForecastingRegimen fr : getForecastObj().getRegimes()) {
			ForecastingRegimenUIAdapter frUI = new ForecastingRegimenUIAdapter(fr);
			if (fr.getCasesOnTreatment().isEmpty()) {
				createCasesOnTreatment(frUI);
			}

			int i = 0;
			//setup listeners
			for (String prp : this.propertyNames) {
				frUI.addPropertyChangeListener(prp, listeners.get(i));
				i++;
			}
			frUI.setRegimensOldCasesQtyListener(getRegimenQuantityListener());
			frUI.setRegimensNewCasesQtyListener(getRegimenNewQtyListener());
			tmp.add(frUI);
		}
		this.regimes = ObservableCollections.observableList(tmp);
		Collections.sort(this.regimes);
		return this.regimes;
	}

	/**
	 * create cases on treatment and assign 0 for quantities
	 * 
	 * @param frui - regimen
	 */
	public void createCasesOnTreatment(ForecastingRegimenUIAdapter frui) {
		ModelFactory modelFactory = Presenter.getFactory();
		MonthUIAdapter refMonth = this.getFirstFCMonth(modelFactory);
		Calendar begin = frui.getRegimen().getBeginDate(getFirstFCDate());
		MonthUIAdapter month = new MonthUIAdapter(modelFactory.createMonth(begin.get(Calendar.YEAR), begin.get(Calendar.MONTH)));
		while(month.compareTo(refMonth)<=0) {
			MonthQuantity monthQuantity = new MonthQuantity();
			monthQuantity.setIQuantity(0);
			monthQuantity.setMonth(month.getMonthObj());
			frui.getFcRegimenObj().getOriginal().getCasesOnTreatment().add(monthQuantity);
			month = month.incrementClone(modelFactory, 1);
		}
	}

	/**
	 * create new cases and assign 0 for quantities
	 * 
	 * @param frui - regimen for which new cases will be created
	 */
	private void createNewCasesRegimens(ForecastingRegimenUIAdapter frui) {
		frui.getFcRegimenObj().getOriginal().getNewCases().clear(); //20180206 add getOriginal AK
		ModelFactory modelFactory = Presenter.getFactory();
		/*Month mon = modelFactory.createMonth(getReferenceDate().get(GregorianCalendar.YEAR), getReferenceDate().get(GregorianCalendar.MONTH));
		MonthUIAdapter begin = new MonthUIAdapter(mon);*/
		MonthUIAdapter begin = getRefPeriodMonth(modelFactory); //20160817 next day!
		Month mend = modelFactory.createMonth(getLastDate().get(GregorianCalendar.YEAR), getLastDate().get(GregorianCalendar.MONTH));
		MonthUIAdapter end = new MonthUIAdapter(mend);
		MonthUIAdapter month = begin.incrementClone(modelFactory, 0);
		List<MonthQuantity> mqList = frui.getFcRegimenObj().getOriginal().getNewCases();
		while (month.compareTo(end) <= 0) {
			MonthQuantity monthQuantity = new MonthQuantity();
			monthQuantity.setIQuantity(0);
			monthQuantity.setMonth(month.getMonthObj());
			mqList.add(monthQuantity);
			month = month.incrementClone(modelFactory, 1);
		}
	}

	/**
	 * For each regimen set property listener. <b>Attention!</b> property name
	 * must be from ForecastingRegimenUIAdapter
	 * 
	 * @param propertyName name of property for which event will fire
	 * @param listener property change listener
	 */
	public void addRegimensListener(String propertyName, PropertyChangeListener listener) {
		this.propertyNames.add(propertyName);
		this.listeners.add(listener);
	}

	/**
	 * Set new values for forecasting regimens adjust medicines list in
	 * accordance
	 * 
	 * @param value new forecasting regimens
	 */
	public void setRegimes(List<ForecastingRegimenUIAdapter> value) {
		List<ForecastingRegimenUIAdapter> oldValue = getRegimes();
		getForecastObj().getRegimes().clear();
		for (ForecastingRegimenUIAdapter fra : value) {
			getForecastObj().getRegimes().add(fra.getFcRegimenObj().getOriginal());
		}
		firePropertyChange("regimes", oldValue, getRegimes());
	}

	/**
	 * Get total percentage of new cases per all treatment regimen
	 * 
	 * @return total percentage
	 */
	public BigDecimal getTotalPercentage() {
		BigDecimal total = new BigDecimal(0);
		total = total.setScale(2);
		for (ForecastingRegimen fr : getForecastObj().getRegimes()) {
			if (fr != null) {
				BigDecimal perc = BigDecimal.ZERO;
				try {
					perc = new BigDecimal(fr.getPercentNewCases());
				} catch (Exception e) {
					//do nothing
				}
				perc = perc.setScale(2, RoundingMode.HALF_UP);
				total = total.add(perc);
			}
		}
		return total;
	}

	/**
	 * Get total percentage of enrolled cases
	 * 
	 * @return
	 */
	public BigDecimal getTotalPercentageOld() {
		BigDecimal total = new BigDecimal(0);
		total = total.setScale(2);
		for (ForecastingRegimen fr : getForecastObj().getRegimes()) {
			if (fr != null) {
				BigDecimal perc = BigDecimal.ZERO;
				try {
					perc = new BigDecimal(fr.getPercentCasesOnTreatment());
				} catch (Exception e) {
					//do nothing!
				}
				perc = perc.setScale(2, RoundingMode.HALF_UP);
				total = total.add(perc);
			}
		}
		return total;
	}

	/**
	 * Get first case of treatment month
	 * 
	 * @param factory
	 * @return very first month or null if any
	 */
	public MonthUIAdapter getVeryFirstMonth(ModelFactory factory) {
		Calendar cal = getVeryFirstDate();
		return new MonthUIAdapter(factory.createMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)));
	}

	/**
	 * Get new cases assigned for the whole forecasting
	 * @return
	 * @see org.msh.quantb.model.forecast.Forecast#getNewCases()
	 */
	public List<MonthQuantityUIAdapter> getNewCases() {
		List<MonthQuantityUIAdapter> tmp = new ArrayList<MonthQuantityUIAdapter>();
		if (getForecastObj().getNewCases() == null || getForecastObj().getNewCases().isEmpty()) {
			// get clean quantities, but right interval
			this.getForecastObj().getNewCases().clear();
			ModelFactory modelFactory = Presenter.getFactory();
			MonthUIAdapter lastMonth =new MonthUIAdapter(modelFactory.createMonth(getLastDate().get(GregorianCalendar.YEAR), getLastDate().get(GregorianCalendar.MONTH)));
			createEmptyNewCases(modelFactory, getRefPeriodMonth(modelFactory), lastMonth);
		}
		for (MonthQuantity mq : getForecastObj().getNewCases()) {
			MonthQuantityUIAdapter mqUI = new MonthQuantityUIAdapter(mq);
			mqUI.addPropertyChangeListener("iQuantity", getNewQtyListener());
			tmp.add(mqUI);
		}
		this.newCases = ObservableCollections.observableList(tmp);
		Collections.sort(this.newCases);
		return this.newCases;
	}

	/**
	 * Get or create list of all cases on treatment
	 * 
	 * @return
	 * @see org.msh.quantb.model.forecast.Forecast#getCasesOnTreatment()
	 */
	public List<MonthQuantityUIAdapter> getCasesOnTreatment() {
		List<MonthQuantityUIAdapter> tmp = new ArrayList<MonthQuantityUIAdapter>();
		List<MonthQuantity> cases = getForecastObj().getCasesOnTreatment();
		if (cases != null && !cases.isEmpty() && getForecastObj().isIsOldPercents()) {
			//return existent if not need to recalc by quantity
			for (MonthQuantity mq : cases) {
				MonthQuantityUIAdapter mqUI = new MonthQuantityUIAdapter(mq);
				if (enrollQtyListener != null) {
					mqUI.addPropertyChangeListener(enrollQtyListener);
				}
				tmp.add(mqUI);
			}
		} else {
			// create empty slots
			this.getForecastObj().getCasesOnTreatment().clear();
			ModelFactory factory = Presenter.getFactory();
			MonthUIAdapter firstMonth = getVeryFirstMonth(factory);
			MonthUIAdapter workMonth = firstMonth.incrementClone(factory, 0);
			MonthUIAdapter lastMonth = getFirstFCMonth(factory);
			while (workMonth.compareTo(lastMonth) <= 0) {
				//int monthTotal = getTotalOldCasesMonth(workMonth);
				int monthTotal = 0;
				MonthQuantity mq = factory.createMonthQuantity(workMonth.getYear(), workMonth.getMonth(), monthTotal);
				this.getForecastObj().getCasesOnTreatment().add(mq);
				MonthQuantityUIAdapter mqU = new MonthQuantityUIAdapter(mq);
				if (enrollQtyListener != null) {
					mqU.addPropertyChangeListener(enrollQtyListener);
				}
				tmp.add(mqU);
				workMonth.incrementMonth(1);
			}
		}
		List<MonthQuantityUIAdapter> ret = ObservableCollections.observableList(tmp);
		Collections.sort(ret);
		return ret;
	}

	/**
	 * @param newCases the newCases to set
	 */
	public void setNewCases(List<MonthQuantityUIAdapter> newCases) {
		List<MonthQuantityUIAdapter> oldValue = getNewCases();
		getForecastObj().getNewCases().clear();
		for (MonthQuantityUIAdapter mqui : newCases) {
			getForecastObj().getNewCases().add(mqui.getMonthQuantityObj());
		}
		firePropertyChange("newCases", oldValue, getNewCases());
	}

	/**
	 * set cases on treatment to Forecasting object
	 * 
	 * @param casesOnTreatment
	 */
	public void setCasesOnTreatment(List<MonthQuantityUIAdapter> casesOnTreatment) {
		List<MonthQuantityUIAdapter> oldValue = getCasesOnTreatment();
		getForecastObj().getCasesOnTreatment().clear();
		for (MonthQuantityUIAdapter mqui : casesOnTreatment) {
			getForecastObj().getCasesOnTreatment().add(mqui.getMonthQuantityObj());
		}
		firePropertyChange("casesOnTreatment", oldValue, getCasesOnTreatment());
	}

	/**
	 * Create empty new cases for forecasting itself, from reference date to
	 * date of end review period
	 * 
	 * @param modelFactory model factory
	 * @param refMonth month of referance date
	 * @param endMonth month of date of end review period
	 */
	private void createEmptyNewCases(ModelFactory modelFactory, MonthUIAdapter refMonth, MonthUIAdapter endMonth) {
		MonthUIAdapter month = new MonthUIAdapter(refMonth.getMonthObj());
		while (month.compareTo(endMonth) != 1) {
			MonthQuantity mq = new MonthQuantity();
			mq.setIQuantity(0);
			mq.setMonth(month.getMonthObj());
			forecastObj.getNewCases().add(mq);
			month = month.incrementClone(modelFactory, 1);
		}
	}


	/**
	 * Correct fetch first forecasting date month
	 * @param factory
	 * @return
	 */
	public MonthUIAdapter getFirstFCMonth(ModelFactory factory){
		//Month m = factory.createMonth(this.getFirstFCDate().get(Calendar.YEAR), this.getFirstFCDate().get(Calendar.MONTH));
		Month m = factory.createMonth(this.getReferenceDate().get(Calendar.YEAR), this.getReferenceDate().get(Calendar.MONTH));
		return new MonthUIAdapter(m);
	}

	/**
	 * Get end forecasting period
	 * 
	 * @param modelFactory
	 * @return
	 */
	public MonthUIAdapter getEndMonth(ModelFactory modelFactory) {
		Calendar endDate = this.getEndDate();
		MonthUIAdapter m = new MonthUIAdapter(modelFactory.createMonth(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH)));
		return m;
	}

	/**
	 * Return all regimen result in particular month
	 * 
	 * @param month
	 * @return list of results or empty list
	 */
	public List<ForecastingRegimenResultUIAdapter> getAllRRByMonth(MonthUIAdapter month) {
		List<ForecastingRegimenResultUIAdapter> res = new ArrayList<ForecastingRegimenResultUIAdapter>();
		for (ForecastingRegimenUIAdapter reg : this.getRegimes()) {
			for (ForecastingRegimenResultUIAdapter fRes : reg.getResults()) {
				if (fRes.getMonth().equals(month)) {
					res.add(fRes);
				}
			}
		}
		return res;
	}

	/**
	 * Get consumption for given medicine in given days interval for all regimes
	 * in given month
	 * 
	 * @param month month
	 * @param fromDay from day in month
	 * @param toDay to day in month
	 * @param medicine
	 * @return consumption or 0 if not found any consumption or wrong interval
	 */
	public BigDecimal getRegimenCons(MonthUIAdapter month, int fromDay, int toDay, MedicineUIAdapter medicine) {
		BigDecimal res = BigDecimal.ZERO;
		if (fromDay <= toDay) {
			for (ForecastingRegimenResultUIAdapter rr : this.getAllRRByDays(month, fromDay, toDay)) {
				MedicineConsUIAdapter mcons = rr.getMedConsunption(medicine);
				if (mcons != null) {
					res = res.add(mcons.getAllConsumption());
				}
			}
		}
		return res;
	}

	/**
	 * Get forecasting result in particular interval within month
	 * 
	 * @param month month
	 * @param fromDay begin interval
	 * @param toDay end interval
	 * @return empty list if nothing to do or wrong interval
	 */
	public List<ForecastingRegimenResultUIAdapter> getAllRRByDays(MonthUIAdapter month, Integer fromDay, Integer toDay) {
		List<ForecastingRegimenResultUIAdapter> res = new ArrayList<ForecastingRegimenResultUIAdapter>();
		if (fromDay <= toDay) {
			/*
			 * for(ForecastingRegimenUIAdapter reg : this.getRegimes()){
			 * for(ForecastingRegimenResultUIAdapter fRes : reg.getResults()){
			 * if(fRes.getMonth().equals(month)){ int rTo = fRes.getToDay(); int
			 * rFrom = fRes.getFromDay(); if ((rFrom >= fromDay) && (rTo <=
			 * toDay)){ res.add(fRes); } } } }
			 */
			List<ForecastingRegimen> regList = this.getForecastObj().getRegimes();
			int resSize = (toDay - fromDay + 1) * regList.size();
			int m2 = month.getMonth();
			int y2 = month.getYear();
			for (ForecastingRegimen reg : regList) {
				List<ForecastingRegimenResult> fResList = reg.getResults();
				for (ForecastingRegimenResult fRes : fResList) {
					Month m = fRes.getMonth();
					int m1 = m.getMonth();
					int y1 = m.getYear();
					if (m1 == m2 && y1 == y2) {
						int rTo = fRes.getToDay();
						int rFrom = fRes.getFromDay();
						if ((rFrom >= fromDay) && (rTo <= toDay)) {
							res.add(new ForecastingRegimenResultUIAdapter(fRes));
						}
						if (res.size() == resSize) return res; //TODO speedup experimental!!!
					}
				}
			}
		}
		return res;
	}

	/**
	 * Get lead time end
	 * 
	 * @return
	 */
	public Calendar getLeadTimeEnd() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(getIniDt());
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return cal;
	}

	/**
	 * Get review period end buffer months included
	 * 
	 * @return
	 */
	public Calendar getReviewEnd() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(getEndDt());
		cal.add(Calendar.MONTH, this.getBufferStockTime());
		return cal;
	}

	/**
	 * Get real review period end without buffer months
	 * 
	 * @return
	 */
	public Calendar getRealReviewEnd() {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(getEndDt());
		DateUtils.cleanTime(cal);
		return cal;
	}

	/**
	 * get initial medicine quantity for medicine given
	 * 
	 * @param medU medicine
	 * @return initial quantity based on all batches or 0 if none
	 */
	public Integer getInitial(MedicineUIAdapter medU) {
		Integer res = 0;
		for (ForecastingMedicineUIAdapter med : this.getMedicines()) {
			if (med.getMedicine().equals(medU)) {
				res = med.getBatchesToExpireInt();
			}
		}
		return res;
	}

	/**
	 * Get orders in transit by medicine given. Take only orders before end of
	 * review period
	 * 
	 * @param medicine
	 * @return 0 in nothing
	 */
	public int getAllTransit(MedicineUIAdapter medicine) {
		int res = 0;
		for (ForecastingMedicineUIAdapter meds : getMedicines()) {
			if (meds.getMedicine().equals(medicine)) {
				for (ForecastingOrderUIAdapter ord : meds.getOrders()) {
					if (ord.getArrived().before(this.getReviewEnd())) {
						res = res + ord.getBatch().getQuantity();
					}
				}
			}
		}
		return res;
	}

	/**
	 * Get start of lead time period
	 * 
	 * @return
	 */
	public String getStartLeadPeriodTxt() {
		return DateUtils.FormatDateTime("MMM dd, yyyy", getIniDt());
	}

	/**
	 * Get end of lead time period
	 * 
	 * @return
	 */
	public String getEndLeadPeriodTxt() {
		return DateUtils.FormatDateTime("MMM dd, yyyy", getEndDt());
	}

	/**
	 * Get start of review period
	 * 
	 * @return
	 */
	public String getStartReviewPeriodTxt() {
		return DateUtils.FormatDateTime("MMM dd, yyyy", getIniDt());
	}

	/**
	 * Get end of review period
	 * 
	 * @return
	 */
	public String getEndReviewPeriodTxt() {
		return DateUtils.FormatDateTime("MMM dd, yyyy", getReviewEnd().getTime());
	}

	/**
	 * Get text representation of duration of lead period in days
	 * 
	 * @return
	 */
	public String getDurationOfLeadPeriodInDays() {
		return "(" + DateUtils.daysBetween(getIniDt(), getEndDt()) + ")";
	}

	/**
	 * Get text representation of duration of review period in days
	 * 
	 * @return
	 */
	public String getDurationOfReviewPeriodInDays() {
		return "(" + (DateUtils.daysBetween(getIniDt(), getReviewEnd().getTime())+1) + " " + Messages.getString("ForecastingDocumentWindow.tbSummary.days") + ")";
	}

	/**
	 * Get forecasting medicine given
	 * 
	 * @param med
	 * @return null if not found
	 */
	public ForecastingMedicineUIAdapter getMedicine(MedicineUIAdapter med) {
		ForecastingMedicineUIAdapter res = null;
		for (ForecastingMedicineUIAdapter fm : this.getMedicines()) {
			if (fm.getMedicine().equals(med)) {
				res = fm;
			}
		}
		return res;
	}

	/**
	 * get new cases list only for regimen given
	 * 
	 * @param rU
	 * @param factory factory to create MonthQuantity
	 * @return empty list if nothing
	 * @deprecated old version
	 */
	public List<MonthQuantityUIAdapter> getNewCases(ForecastingRegimenUIAdapter rU, ModelFactory factory) {
		List<MonthQuantityUIAdapter> res = new ArrayList<MonthQuantityUIAdapter>();
		if (getForecastObj().isIsNewPercents()) {
			for (MonthQuantityUIAdapter mQ : this.getNewCases()) {
				BigDecimal iQ = calcPercents(mQ.getIQuantity(), rU.getPercentNewCases());
				MonthQuantityUIAdapter mq = mQ.getClone(factory);
				mq.getMonthQuantityObj().setIQuantity(iQ.intValue());
				mq.addPropertyChangeListener("iQuantity", getNewQtyListener());
				res.add(mq);
			}
		}else{
			res.addAll(rU.getNewCases());
		}
		Collections.sort(res);
		return res;
	}

	/**
	 * get old cases list only for regimen given
	 * 
	 * @param rU forecasting regimen
	 * @param factory factory to create MonthQuantity
	 * @return empty list if nothing
	 * @deprecated from the previous version
	 */
	public List<MonthQuantityUIAdapter> getOldCases(ForecastingRegimenUIAdapter rU, ModelFactory factory){
		List<MonthQuantityUIAdapter> res = new ArrayList<MonthQuantityUIAdapter>();
		if (getForecastObj().isIsOldPercents()){
			for (MonthQuantityUIAdapter mQ: this.getCasesOnTreatment()){
				BigDecimal iQ = calcPercents(mQ.getIQuantity(), rU.getPercentCasesOnTreatment());
				MonthQuantityUIAdapter mq = mQ.getClone(factory);
				mq.getMonthQuantityObj().setIQuantity(iQ.intValue());
				mq.addPropertyChangeListener("iQuantity", getNewQtyListener());
				res.add(mq);
			}
		}else{
			res.addAll(rU.getCasesOnTreatment());
		}
		return res;
	}

	/**
	 * get end of review + buffer stock or min stock (2016-02-23)
	 * 
	 * @return
	 */
	public Calendar getLastDate() {
		Calendar ret = this.getEndDate();
		if(getMinStock() > 0){
			//ret.add(Calendar.MONTH, this.getMinStock());
		}else{
			ret.add(Calendar.MONTH, this.getBufferStockTime());
		}
		return ret;
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.Forecast#getCountry()
	 */
	public String getCountry() {
		return forecastObj.getCountry();
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.Forecast#setCountry(java.lang.String)
	 */
	public void setCountry(String value) {
		String oldValue = getCountry();
		forecastObj.setCountry(value);
		firePropertyChange("country", oldValue, getCountry());
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.Forecast#getInstitution()
	 */
	public String getInstitution() {
		return forecastObj.getInstitution();
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.Forecast#setInstitution(java.lang.String)
	 */
	public void setInstitution(String value) {
		String oldValue = getInstitution();
		forecastObj.setInstitution(value);
		firePropertyChange("institution", oldValue, getInstitution());
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.Forecast#getCalculator()
	 */
	public String getCalculator() {
		String ret = forecastObj.getCalculator();
		if (ret == null) {
			return "";
		} else {
			return ret;
		}
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.Forecast#setCalculator(java.lang.String)
	 */
	public void setCalculator(String value) {
		String oldValue = getCalculator();
		forecastObj.setCalculator(value);
		firePropertyChange("calculator", oldValue, getCalculator());
	}

	/**
	 * Get Country/Region/Facility/Person and comment informaion in HTML
	 * 
	 * @return
	 */
	public String getDetailsInformationHTML() {
		return "<html>"+getAddress() + "/" + getCalculator() + "/ <b>" + Messages.getString("ForecastingDocumentWindow.order.commentlbl")+"</b> "+ getComment() + "</html>";
	}
	
	/**
	 * Get Country/Region/Facility/Person and comment informaion in txt
	 * 
	 * @return
	 */
	public String getDetailsInformationTxt() {
		return getAddress() + "/" + getCalculator() + "/ " + Messages.getString("ForecastingDocumentWindow.order.commentlbl")+" "+ getComment();
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.Forecast#getAddress()
	 */
	public String getAddress() {
		String ret = forecastObj.getAddress();
		if (ret == null) {
			return "";
		} else {
			return ret;
		}
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.Forecast#setAddress(java.lang.String)
	 */
	public void setAddress(String value) {
		String oldValue = getAddress();
		forecastObj.setAddress(value);
		firePropertyChange("address", oldValue, getAddress());
	}

	/**
	 * Get additional expenses list for a total order
	 * 
	 * @return
	 */
	public List<ForecastingTotalItemUIAdapter> getTotalOrderItems() {
		List<ForecastingTotalItemUIAdapter> tmp = prepareTotalItems(forecastObj.getTotal());
		return ObservableCollections.observableList(tmp);
	}

	/**
	 * Get additional expenses list for an accelerated order
	 * 
	 * @return
	 */
	public List<ForecastingTotalItemUIAdapter> getAccOrderItems() {
		List<ForecastingTotalItemUIAdapter> tmp = prepareTotalItems(forecastObj.getTotalA());
		return ObservableCollections.observableList(tmp);
	}

	/**
	 * Get additional expenses list for a regular order
	 * 
	 * @return
	 */
	public List<ForecastingTotalItemUIAdapter> getRegOrderItems() {
		List<ForecastingTotalItemUIAdapter> tmp = prepareTotalItems(forecastObj.getTotalR());
		return ObservableCollections.observableList(tmp);
	}

	/**
	 * Prepare total items for all totals
	 * @param fcTotItems TODO
	 * @return prepared total items
	 */
	private List<ForecastingTotalItemUIAdapter> prepareTotalItems(List<ForecastingTotalItem> fcTotItems) {
		List<ForecastingTotalItemUIAdapter> tmp = new ArrayList<ForecastingTotalItemUIAdapter>();
		if (fcTotItems.isEmpty()) { //create predefined
			fcTotItems.add(Presenter.getFactory().createForecastingTotalItem(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.items.freight"), new BigDecimal(0)));
			fcTotItems.add(Presenter.getFactory().createForecastingTotalItem(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.items.insurance"), new BigDecimal(0)));
			fcTotItems.add(Presenter.getFactory().createForecastingTotalItem(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.items.PSI"), new BigDecimal(0)));
			fcTotItems.add(Presenter.getFactory().createForecastingTotalItem(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.items.agent"), new BigDecimal(0)));
			fcTotItems.add(Presenter.getFactory().createForecastingTotalItem(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.items.custom"), new BigDecimal(0)));
		}
		//build list
		for (ForecastingTotalItem it : fcTotItems) {
			ForecastingTotalItemUIAdapter itU = new ForecastingTotalItemUIAdapter(it);
			tmp.add(itU);
		}
		return tmp;
	}

	/**
	 * Set totals for this forecasting
	 * 
	 * @param items
	 */
	public void setTotalItems(List<ForecastingTotalItemUIAdapter> items) {
		List<ForecastingTotalItemUIAdapter> oldValue = getTotalOrderItems();
		persistTotalItems(items, forecastObj.getTotal());
		firePropertyChange("totalItems", oldValue, getTotalOrderItems());
	}

	/**
	 * Set accelerated order totals for this forecasting
	 * 
	 * @param items
	 */
	public void setTotalAItems(List<ForecastingTotalItemUIAdapter> items) {
		List<ForecastingTotalItemUIAdapter> oldValue = getAccOrderItems();
		persistTotalItems(items, forecastObj.getTotalA());
		firePropertyChange("totalAItems", oldValue, getAccOrderItems());
	}

	/**
	 * Set regular order totals for this forecasting
	 * 
	 * @param items
	 */
	public void setTotalRItems(List<ForecastingTotalItemUIAdapter> items) {
		List<ForecastingTotalItemUIAdapter> oldValue = getRegOrderItems();
		persistTotalItems(items, forecastObj.getTotalR());
		firePropertyChange("totalAItems", oldValue, getRegOrderItems());
	}

	/**
	 * Save totals to the Forecasting object
	 * @param items items to save
	 * @param source forecasting object items
	 */
	private void persistTotalItems(List<ForecastingTotalItemUIAdapter> items, List<ForecastingTotalItem> source) {
		source.clear();
		for (ForecastingTotalItemUIAdapter it : items) {
			source.add(it.getFcItemObj());
		}
	}

	/**
	 * For forecasting that have at least two medications in any regimen,
	 * allowed total 100% new cases for all regimens<br>
	 * More or less then 100% allowed only for forecasting that have only one
	 * medication in any regimen. This intends for<br>
	 * provide possibility to calculate forecasting for medicines only
	 * 
	 * @return
	 */
	public boolean isOnly100Allowed() {
		return this.getForecastObj().getRegimensType() != RegimenTypesEnum.SINGLE_DRUG;
	}

	/**
	 * If reference date changed, we are need to "shift" old cases in
	 * Forecasting
	 */
	public void shiftOldCasesPercents() {
		// take list of previous quantities
		List<MonthQuantityUIAdapter> oldValue = this.getCasesOnTreatment();
		// get clean quantities, but right interval
		this.getForecastObj().getCasesOnTreatment().clear();
		List<MonthQuantityUIAdapter> newValue = this.getCasesOnTreatment();
		for (MonthQuantityUIAdapter mqui : newValue) {
			MonthQuantityUIAdapter found = getMQFromList(mqui, oldValue);
			if (found != null) {
				mqui.getMonthQuantityObj().setIQuantity(found.getMonthQuantityObj().getIQuantity());
			}
		}
	}

	/**
	 * if reference date changed , we are need to "shift" old cases for all
	 * regimes
	 */
	public void shiftOldCasesRegimens() {
		for (ForecastingRegimenUIAdapter frui : getRegimes()) {
			List<MonthQuantityUIAdapter> oldValue = frui.getCasesOnTreatment();
			frui.getFcRegimenObj().getOriginal().getCasesOnTreatment().clear();
			createCasesOnTreatment(frui);
			List<MonthQuantityUIAdapter> newValue = frui.getCasesOnTreatment();
			for (MonthQuantityUIAdapter mqui : newValue) {
				MonthQuantityUIAdapter found = getMQFromList(mqui, oldValue);
				if (found != null) {
					mqui.getMonthQuantityObj().setIQuantity(found.getMonthQuantityObj().getIQuantity());
				}
			}
		}

	}

	/**
	 * Get from list month quantity with same month as in mq
	 * 
	 * @param mq month Quantity
	 * @param mqList list of month quantity
	 * @return month quantity UI or null, if not found
	 */
	private MonthQuantityUIAdapter getMQFromList(MonthQuantityUIAdapter mq, List<MonthQuantityUIAdapter> mqList) {
		if (mqList != null) {
			for (MonthQuantityUIAdapter mqui : mqList) {
				if (mq.getMonth().equals(mqui.getMonth())) { return mqui; }
			}
		}
		return null;
	}

	/**
	 * @return the regimenQuantityListener
	 */
	public PropertyChangeListener getRegimenQuantityListener() {
		return regimenQuantityListener;
	}

	/**
	 * Set listener for change any enrolled cases quantity for any regimen
	 * 
	 * @param regimenQuantityListener the regimenQuantityListener to set
	 */
	public void setRegimenEnrollQtyListener(PropertyChangeListener regimenQuantityListener) {
		this.regimenQuantityListener = regimenQuantityListener;
	}

	/**
	 * Set property change listener to table of enrolled cases used in
	 * percentage calculations
	 * 
	 * @param propertyChangeListener
	 */
	public void setEnrollQtyListener(PropertyChangeListener propertyChangeListener) {
		this.enrollQtyListener = propertyChangeListener;

	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.Forecast#getRegimensType()
	 */
	public RegimenTypesEnum getRegimensType() {
		return forecastObj.getRegimensType();
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.Forecast#setRegimensType(org.msh.quantb.model.gen.RegimenTypesEnum)
	 */
	public void setRegimensType(RegimenTypesEnum value) {
		RegimenTypesEnum oldValue = getRegimensType();
		forecastObj.setRegimensType(value);
		firePropertyChange("regimensType", oldValue, getRegimensType());
	}

	/**
	 * Calculate percents from quantity
	 * 
	 * @param quant
	 * @param percents
	 * @return
	 */
	public BigDecimal calcPercents(Integer quant, Float percents) {
		BigDecimal newQ = new BigDecimal(quant);
		newQ = newQ.setScale(4);
		BigDecimal perc=BigDecimal.ZERO;
		try {
			perc = new BigDecimal(percents.toString());
		} catch (Exception e) {
			//nothing to do, zero is good in this case
		}
		perc = perc.setScale(4, RoundingMode.HALF_UP);
		BigDecimal hundred = new BigDecimal(100);
		hundred = hundred.setScale(4);
		BigDecimal res = new BigDecimal(0);
		res = res.setScale(2);
		res = newQ.multiply(perc);
		res = res.divide(hundred, RoundingMode.HALF_UP);
		res = res.setScale(2, RoundingMode.HALF_UP);
		return res;
	}

	/**
	 * listen new cases quantity changes in all regimens
	 * 
	 * @param propertyChangeListener
	 */
	public void setRegimenNewQtyListener(PropertyChangeListener propertyChangeListener) {
		this.regimeNewQtyListener = propertyChangeListener;

	}

	/**
	 * get new
	 * 
	 * @return the newQtyListener
	 */
	public PropertyChangeListener getRegimenNewQtyListener() {
		return regimeNewQtyListener;
	}

	/**
	 * listen for changes in months total quantities for new cases
	 * 
	 * @param propertyChangeListener
	 */
	public void setNewQtyListener(PropertyChangeListener propertyChangeListener) {
		this.newQtyListener = propertyChangeListener;

	}

	/**
	 * @return the newQtyListener
	 */
	public PropertyChangeListener getNewQtyListener() {
		return newQtyListener;
	}

	/**
	 * Shift new cases quantities for all regimen in accordance with Reference
	 * Date, End Date or Lead Time change
	 */
	public void shiftNewCasesRegimens() {
		for (ForecastingRegimenUIAdapter frui : getRegimes()) {
			List<MonthQuantityUIAdapter> oldValue = frui.getNewCases();
			createNewCasesRegimens(frui);
			List<MonthQuantityUIAdapter> newValue = frui.getNewCases();
			for (MonthQuantityUIAdapter mqui : newValue) {
				MonthQuantityUIAdapter found = getMQFromList(mqui, oldValue);
				if (found != null) {
					mqui.getMonthQuantityObj().setIQuantity(found.getMonthQuantityObj().getIQuantity());
				}
			}
		}

	}

	/**
	 * Shift new cases quantities for months totals in accordance with Reference
	 * Date, End Date or Lead Time change
	 */
	public void shiftNewCasesPercents() {
		ModelFactory modelFactory = Presenter.getFactory();
		// take list of previous quantities
		List<MonthQuantityUIAdapter> oldValue = this.getNewCases();
		// get clean quantities, but right interval
		this.getForecastObj().getNewCases().clear();
		MonthUIAdapter lastMonth =new MonthUIAdapter(modelFactory.createMonth(getLastDate().get(GregorianCalendar.YEAR), getLastDate().get(GregorianCalendar.MONTH)));
		createEmptyNewCases(modelFactory, getRefPeriodMonth(modelFactory), lastMonth);
		List<MonthQuantityUIAdapter> newValue = this.getNewCases();
		for (MonthQuantityUIAdapter mqui : newValue) {
			MonthQuantityUIAdapter found = getMQFromList(mqui, oldValue);
			if (found != null) {
				mqui.getMonthQuantityObj().setIQuantity(found.getMonthQuantityObj().getIQuantity());
			}
		}

	}
	/**
	 * Reference period for expected cases should begin on the next day after Reference Date
	 * @param modelFactory
	 * @return
	 */
	private MonthUIAdapter getRefPeriodMonth(ModelFactory modelFactory) {
		MonthUIAdapter ret = new MonthUIAdapter(modelFactory.createMonth(getFirstFCDate().get(Calendar.YEAR), getFirstFCDate().get(Calendar.MONTH)));
		return ret;
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.Forecast#getMinStock()
	 */
	public Integer getMinStock() {
		return forecastObj.getMinStock();
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.Forecast#setMinStock(int)
	 */
	public void setMinStock(Integer value) {
		Integer oldValue = getMinStock();
		forecastObj.setMinStock(value);
		firePropertyChange("minStock", oldValue, getMinStock());
	}

	/**
	 * @return
	 * @see org.msh.quantb.model.forecast.Forecast#getMaxStock()
	 */
	public Integer getMaxStock() {
		return forecastObj.getMaxStock();
	}

	/**
	 * @param value
	 * @see org.msh.quantb.model.forecast.Forecast#setMaxStock(int)
	 */
	public void setMaxStock(Integer value) {
		Integer oldValue = getMaxStock();
		forecastObj.setMaxStock(value);
		firePropertyChange("maxStock", oldValue, getMaxStock());
	}

	/**
	 * Get comment for order page 1
	 * 
	 * @return
	 * @see org.msh.quantb.model.forecast.Forecast#getTotalComment1()
	 */
	public String getTotalComment1() {
		return forecastObj.getTotalComment1();
	}

	/**
	 * Set comment for order page 1
	 * 
	 * @param value
	 * @see org.msh.quantb.model.forecast.Forecast#setTotalComment1(java.lang.String)
	 */
	public void setTotalComment1(String value) {
		String oldValue = getTotalComment1();
		forecastObj.setTotalComment1(value);
		firePropertyChange("totalComment1", oldValue, getTotalComment1());
	}

	/**
	 * Get comment for order page 2
	 * 
	 * @return
	 * @see org.msh.quantb.model.forecast.Forecast#getTotalComment2()
	 */
	public String getTotalComment2() {
		return forecastObj.getTotalComment2();
	}

	/**
	 * Set comment for order page 2
	 * 
	 * @param value
	 * @see org.msh.quantb.model.forecast.Forecast#setTotalComment2(java.lang.String)
	 */
	public void setTotalComment2(String value) {
		String oldValue = getTotalComment2();
		forecastObj.setTotalComment2(value);
		firePropertyChange("totalComment2", oldValue, getTotalComment2());
	}

	/**
	 * @return the forecastUIVerify
	 */
	public ForecastUIVerify getForecastUIVerify() {
		return forecastUIVerify;
	}

	/**
	 * verify forecasting parameters
	 * 
	 * @return
	 */
	public String verifyParameters() {
		return getForecastUIVerify().verifyUI();
	}
	/**
	 * Get total quantity of enrolled cases
	 * @param fru regimen in forecasting
	 * @param modelFactory
	 * @return
	 */
	public int getOldCasesQuantity(ForecastingRegimenUIAdapter fru,
			ModelFactory modelFactory) {
		int res = 0;
		for(MonthQuantityUIAdapter mqui : this.getOldCases(fru, modelFactory)){
			res = res + mqui.getIQuantity();
		}
		return res;
	}
	/**
	 * Get total quantity of expected cases
	 * @param fru
	 * @param modelFactory
	 * @return
	 */
	public int getNewCasesQuantity(ForecastingRegimenUIAdapter fru,
			ModelFactory modelFactory) {
		int res = 0;
		for(MonthQuantityUIAdapter mqui : this.getNewCases(fru, modelFactory)){
			res = res + mqui.getIQuantity();
		}
		return res;
	}
	/**
	 * Get total enrolled cases if quantity style - by regimen, if percent style - simply total
	 * @return
	 */
	public int getTotalEnrolled() {
		int ret = 0;
		if(!this.isEnrolledCasesPercents()){
			for(ForecastingRegimenUIAdapter frui : this.getRegimes()){
				ret = ret + this.getOldCasesQuantity(frui, Presenter.getFactory());
			}
		}else{
			if(this.getTotalPercentageOld().compareTo(new BigDecimal(0))> 0){ //make sense only if percentage > 0
				for(MonthQuantityUIAdapter mqUi : this.getCasesOnTreatment()){
					ret = ret + mqUi.getIQuantity();
				}
			}
		}
		return ret;
	}
	/**
	 * Get total expected cases if quantity style - by regimen, if percent style - simply total
	 * @return
	 */
	public int getTotalExpected() {
		int ret = 0;
		if (!this.isExpectedCasesPercents()){
			for(ForecastingRegimenUIAdapter frui : this.getRegimes()){
				ret = ret + this.getNewCasesQuantity(frui, Presenter.getFactory());
			}
		}else{
			if (this.getTotalPercentage().compareTo(new BigDecimal(0))> 0){ //make sense only if percentage > 0
				for(MonthQuantityUIAdapter mqUi : this.getNewCases()){
					ret = ret + mqUi.getIQuantity();
				}
			}
		}
		return ret;
	}
	/**
	 * Is enrolled cases by percents
	 * @return
	 */
	public boolean isEnrolledCasesPercents() {
		return this.getForecastObj().isIsOldPercents();
	}
	/**
	 * Is expected cases by percents
	 * @return
	 */
	public boolean isExpectedCasesPercents() {
		return this.getForecastObj().isIsNewPercents();
	}
	/**
	 * Change enrolled cases data - Quantity mode
	 * @param data this data never be null or empty
	 * @return resolved error message or empty string if all OK
	 */
	public String changeEnrolledData(Integer[][] data) {
		// check regimens quantity
		List<ForecastingRegimenUIAdapter> regs = getRegimes();
		if (regs.size() >= data.length){
			ForecastingRegimensTableModel model = new ForecastingRegimensTableModel(this,null);
			int columns = model.getColumnCount()-1;
			if ( columns == data[0].length){
				for(int row=0; row<data.length; row++){
					List<MonthQuantityUIAdapter> onTreatment = regs.get(row).getCasesOnTreatment();
					int column = data[row].length-1;
					for(int j= onTreatment.size()-1; j>=0; j--){
						onTreatment.get(j).setIQuantity(data[row][column]);
						column--;
					}
				}
				return "";
			}else{
				return Messages.getString("Error.forecasting.paste.invalidcolumns") + " " + 
						columns + " "+
						Messages.getString("Error.forecasting.paste.columns") ;
			}
		}else{
			return Messages.getString("Error.forecasting.paste.toomanyrows") + " " + regs.size();
		}
	}

	/**
	 * Change expected cases data - Quantity mode
	 * @param data
	 * @return
	 */
	public String changeExpectedData(Integer[][] data) {
		// check regimens quantity
		List<ForecastingRegimenUIAdapter> regs = getRegimes();
		if (regs.size() >= data.length){
			ForecastingRegimensNewCasesModel model = new ForecastingRegimensNewCasesModel(this, null);
			int columns = model.getColumnCount()-1;
			if ( columns == data[0].length){
				for(int row=0; row<data.length; row++){
					List<MonthQuantityUIAdapter> expected = regs.get(row).getNewCases();
					int column = data[row].length-1;
					for(int j= expected.size()-1; j>=0; j--){
						expected.get(j).setIQuantity(data[row][column]);
						column--;
					}
				}
				return "";

			}else{
				return Messages.getString("Error.forecasting.paste.invalidcolumns") + " " + 
						columns + " "+
						Messages.getString("Error.forecasting.paste.columns") ;
			}
		}else{
			return Messages.getString("Error.forecasting.paste.toomanyrows") + " " + regs.size();
		}
	}


	/**
	 * Change enrolled cases data - Percent mode
	 * @param data this data never be null or empty
	 * @return resolved error message or empty string if all OK
	 */
	public String changeEnrolledPersQtyData(Integer[][] data) {
		if (data[0].length == 1){
			List<MonthQuantityUIAdapter> enrolled = this.getCasesOnTreatment();
			if (enrolled.size() >= data.length){
				for(int i=0; i<data.length;i++){
					enrolled.get(i).setIQuantity(data[i][0]);
				}
				return "";
			}else{
				return Messages.getString("Error.forecasting.paste.toomanyrows") + " " + enrolled.size();
			}
		}else{
			return Messages.getString("Error.forecasting.paste.invalidcolumns") + " 1";
		}
	}
	/**
	 * Change expected cases data - Percent mode
	 * @param data this data never be null or empty
	 * @return resolved error message or empty string if all OK
	 */
	public String changeExpectedPersQtyData(Integer[][] data) {
		if (data[0].length == 1){
			List<MonthQuantityUIAdapter> expected = this.getNewCases();
			if (expected.size() >= data.length){
				for(int i=0; i<data.length;i++){
					expected.get(i).setIQuantity(data[i][0]);
				}
				return "";
			}else{
				return Messages.getString("Error.forecasting.paste.toomanyrows") + " " + expected.size();
			}
		}else{
			return Messages.getString("Error.forecasting.paste.invalidcolumns") + " 1";
		}
	}

	/**
	 * Get begin date of the calculation
	 * Condition - last day of treatment of the longest regimen must be on the reference date
	 * @return
	 */
	public Calendar getVeryFirstDate() {
		Calendar ret = getFirstFCDate();
		Calendar rd = getFirstFCDate();
		for(ForecastingRegimenUIAdapter fRegUi : this.getRegimes()){
			RegimenUIAdapter regUi = fRegUi.getRegimen();
			Calendar cal = regUi.getBeginDate(rd);
			if (cal.compareTo(ret) == -1){
				ret.setTime(cal.getTime());
			}
		}
		return ret;
	}
	/**
	 * Get calendar of the enrolled cases for a forecasting regimen
	 * assume all raw data existed
	 * We should translate on screen MonthQuantityUIAdapter to CalendarQuantity suit for calculations
	 * @param fr regimen in forecasting
	 * @return list of cases that where enrolled for defined dates
	 */
	public List<CalendarQuantity> getCalendarOldCases(ForecastingRegimenUIAdapter fr) {
		//First of all, calc first date of the regimen
		Calendar first = fr.getRegimen().getBeginDate(getFirstFCDate());
		List<CalendarQuantity> res = new ArrayList<CalendarQuantity>();
		boolean isFirst = true; // first day is 
		if (getForecastObj().isIsOldPercents()){ // quantities directly in the forecasting, percents in the regimen
			for (MonthQuantity mq : this.getForecastObj().getCasesOnTreatment()){
				BigDecimal qBD = calcPercents(mq.getIQuantity(), fr.getPercentCasesOnTreatment());
				Month m = mq.getMonth();
				CalendarQuantity cQ = createCalendarQuantity(first, isFirst, qBD, m);
				res.add(cQ);
				isFirst = false;
			}
		}else{ //quantities directly in the regimen
			for(MonthQuantityUIAdapter mq : fr.getCasesOnTreatment()){
				Month m = mq.getMonthQuantityObj().getMonth();
				CalendarQuantity cQ =createCalendarQuantity(first, isFirst, mq.getBDQuantity(), m);
				res.add(cQ);
				isFirst = false;
			}
		}
		Collections.sort(res);
		return res;
	}

	/**
	 * Get first date of forecasting calculation
	 * @return
	 */
	public Calendar getFirstFCDate() {
		LocalDate tmp = calcFirstDate(getReferenceDate());
		return tmp.toDateTimeAtStartOfDay().toCalendar(Locale.getDefault());
	}

	public static LocalDate calcFirstDate(Calendar referenceDate) {
		LocalDate tmp = new LocalDate(referenceDate); //20180625 really appropriate
		tmp = tmp.plusDays(1);
		return tmp;
	}

	/**
	 * Get calendar of the expected cases for forecasting regimen
	 * @param fr
	 * @return
	 */
	public List<CalendarQuantity> getCalendarNewCases(ForecastingRegimenUIAdapter fr) {
		List<CalendarQuantity> res = new ArrayList<CalendarQuantity>();
		Calendar first = getFirstFCDate();
		boolean isFirst = true;
		if (getForecastObj().isIsNewPercents()){
			// calculate values based on forecasting quantities and regimen percent
			for(MonthQuantityUIAdapter mq: this.getNewCases()){
				BigDecimal qBD = calcPercents(mq.getIQuantity(), fr.getPercentNewCases());
				Month m = mq.getMonth().getMonthObj();
				CalendarQuantity cQ = createCalendarQuantity(first, isFirst,
						qBD, m);
				res.add(cQ);
				isFirst = false;
			}

		}else{
			//take values from the regimen
			for(MonthQuantityUIAdapter mq : fr.getNewCases()){
				Month m = mq.getMonthQuantityObj().getMonth();

				CalendarQuantity cQ = createCalendarQuantity(first, isFirst,
						mq.getBDQuantity(), m);
				res.add(cQ);
				isFirst = false;
			}
		}
		Collections.sort(res);
		return res;
	}

	/**
	 * Create the calendar quantity in uniform way
	 * @param first first date
	 * @param isFirst is it first date
	 * @param qBD quantity
	 * @param m year and month
	 * @return
	 */
	private CalendarQuantity createCalendarQuantity(Calendar first,
			boolean isFirst, BigDecimal qBD, Month m) {
		int day = 1; //obvious
		if (isFirst){ 
			//the exception
			day = first.get(Calendar.DAY_OF_MONTH);
		}
		CalendarQuantity cQ = 
				new CalendarQuantity(DateUtils.getCleanCalendar(m.getYear(), m.getMonth(), day),qBD);
		return cQ;
	}

	public void setTotalComment3(String comment) {
		String oldValue = getTotalComment3();
		this.getForecastObj().setTotalComment3(comment);
		firePropertyChange("totalComment3", oldValue, getTotalComment3());
	}

	/**
	 * Total comment for a regular order
	 * @return
	 */
	private String getTotalComment3() {
		return this.getForecastObj().getTotalComment3();
	}

	public void setTotalComment4(String comment) {
		String oldValue = getTotalComment4();
		this.getForecastObj().setTotalComment4(comment);
		firePropertyChange("totalComment3", oldValue, getTotalComment3());
	}

	/**
	 * Total comment for an accelerated order
	 * @return
	 */
	private String getTotalComment4() {
		return this.getForecastObj().getTotalComment4();
	}

	/**
	 * Format is x.x.x.x-yyyyMMdd
	 * @return
	 */
	public String getQtbVersion(){
		return getForecastObj().getQtbVersion();
	}

	public void setQtbVersion(String version){
		String oldValue = getForecastObj().getQtbVersion();
		getForecastObj().setQtbVersion(version);
		firePropertyChange("qtbVersion", oldValue, getQtbVersion());
	}

	public boolean isVersionSuit(String programVersion){
		//programVersion = "3.0.0.2-20150612";
		if(getQtbVersion() == null){
			return true; // oldest
		}
		if(getQtbVersion().length() == 0){
			return true; // oldest
		}
		String[] progVersion = programVersion.split("-");
		String[] fcVersion = getQtbVersion().split("-");
		if(progVersion.length == 2 && fcVersion.length == 2){
			String[] progVersComp = progVersion[0].split("\\.");
			String[] fcVersionComp = fcVersion[0].split("\\.");
			if (progVersComp.length == 4 && fcVersionComp.length == 4){
				try {
					int progVi = Integer.parseInt(progVersComp[0]+progVersComp[1]);
					int fcVi = Integer.parseInt(fcVersionComp[0]+fcVersionComp[1]);
					return progVi>=fcVi;
				} catch (NumberFormatException e) {
					return true; //something undefined suspect oldest
				}
			}else{
				return true; //something undefined suspect oldest
			}
		}else{
			return true; //development mode or something unresolved
		}
	}

	/**
	 * Create new forecasting and copy general parameters to it
	 * @return new forecast
	 */
	public ForecastUIAdapter copyParameters(){
		ForecastUIAdapter fcUi = new ForecastUIAdapter(Presenter.getFactory().createForecasting(getName()));
		fcUi.setReferenceDate(getReferenceDate()); //20160825 appropriate
		fcUi.setLeadTime(getLeadTime());
		fcUi.setBufferStockTime(getBufferStockTime());
		fcUi.setMaxStock(getMaxStock());
		fcUi.setMinStock(getMinStock());
		fcUi.setIniDate(getIniDate());
		fcUi.setEndDate(getEndDate());
		fcUi.setCalculator(getCalculator());
		fcUi.setInstitution(getInstitution());
		fcUi.setAddress(getAddress());
		fcUi.setComment(getComment());
		fcUi.setRegimensType(getRegimensType());
		fcUi.getForecastObj().setIsOldPercents(isEnrolledCasesPercents());
		fcUi.getForecastObj().setIsNewPercents(isExpectedCasesPercents());
		fcUi.setQtbVersion(Presenter.getVersion());
		return fcUi;
	}

	public List<String> getWarnings() {
		return getForecastUIVerify().warningsAsList();
	}

	/**
	 * Returns list of properties names 
	 * @return
	 */
	public static String[] getParamenters() {
		String[] ret= {
				"bufferStockTime", "endDate", "iniDate", "leadTime", "referenceDate", "maxStock", "minStock"
				, "casesOnTreatment", "newCases"
		};
		return ret;
	}
	/**
	 * Convert expected cases from quantity style calculation to percent style
	 * All quantity data will be removed
	 * @return true if it is possible
	 */
	public boolean expectedToPers() {
		if(!isExpectedCasesPercents()){
			if(!hasExpectedPersQuantities()){
				//create slot for results
				List<MonthQuantityUIAdapter> result = getNewCases();
				//nullify results
				for(MonthQuantityUIAdapter mq : result){
					mq.getMonthQuantityObj().setIQuantity(0);
				}
				valuesToPers(result, false);
			}
			getForecastObj().setIsNewPercents(true);
			shiftNewCasesPercents();
			return true;
		}else{
			return false;
		}
	}
	/**
	 * convert expected cases from percent style calculation to quantity style
	 * All percent data will be removed
	 * @return true if it possible
	 */
	public boolean expectedToQuantity() {
		if(isExpectedCasesPercents()){
			if(!hasExpectedQuantities()){
				for(ForecastingRegimenUIAdapter frUi : getRegimes()){
					frUi.getFcRegimenObj().getNewCases().clear();
					createNewCasesRegimens(frUi);
					for(int i=0; i<getForecastObj().getNewCases().size();i++){
						BigDecimal qBD = calcPercents(getForecastObj().getNewCases().get(i).getIQuantity(), frUi.getPercentNewCases());
						int quant = qBD.setScale(0, RoundingMode.HALF_UP).intValue();
						frUi.getFcRegimenObj().getNewCases().get(i).setIQuantity(quant);
					}
				}
			}
			getForecastObj().setIsNewPercents(false);
			return true;
		}else{
			return false;
		}
	}
	/**
	 * Convert enrolled cases from quantity style calculation to percent style
	 * @return true if it is possible
	 */
	public boolean enrolledToPers() {
		if(!isEnrolledCasesPercents()){
			if(!hasEnrolledPersQuantities()){
				//create slot for results
				List<MonthQuantityUIAdapter> result = getCasesOnTreatment();
				//nullify results
				for(MonthQuantityUIAdapter mq : result){
					mq.getMonthQuantityObj().setIQuantity(0);
				}
				valuesToPers(result, true);
			}
			getForecastObj().setIsOldPercents(true);
			shiftOldCasesPercents();
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Convert quantities to percents
	 * suit for  enrolled and expected
	 * @param monthlyQuantities - list of monthly quantities for enrolled cases, initially should be zero for each month
	 * @param isOld 
	 */
	private void valuesToPers(List<MonthQuantityUIAdapter> monthlyQuantities, boolean isOld) {
		//summ cases quantity for all regimes
		Integer sumTot=0; //total cases
		for(ForecastingRegimenUIAdapter frUi : getRegimes()){
			Integer sumReg = 0; //regime's cases
			List<MonthQuantityUIAdapter> frCases = isOld ? frUi.getCasesOnTreatment() : frUi.getNewCases();
			for(MonthQuantityUIAdapter mQ: frCases){
				sumReg=sumReg+mQ.getIQuantity();
				sumTot=sumTot + mQ.getIQuantity();
				addQuantityToList(monthlyQuantities, mQ);
			}
			//store sum, it isn't real percents!!!!
			if(isOld){
				frUi.getFcRegimenObj().setPercentCasesOnTreatment(sumReg);
			}else{
				frUi.getFcRegimenObj().setPercentNewCases(sumReg);
			}
		}
		//rogue calculate percents
		ForecastingRegimenUIAdapter lastFr=null;
		float sumF = 0.0000f;	//total sum
		float sum=0.0000f;		//regimen sum
		for(ForecastingRegimenUIAdapter frUi : getRegimes()){
			sum= isOld ? frUi.getFcRegimenObj().getPercentCasesOnTreatment()/sumTot : frUi.getFcRegimenObj().getPercentNewCases()/sumTot;
			sumF = sumF+sum;
			if(isOld){
				frUi.getFcRegimenObj().setPercentCasesOnTreatment(sum*100);
			}else{
				frUi.getFcRegimenObj().setPercentNewCases(sum*100);
			}
			lastFr = frUi;
		}
		if(isOnly100Allowed()){
			float correction = 100.00f-sumF*100.00f;
			if(isOld){
				lastFr.getFcRegimenObj().setPercentCasesOnTreatment(lastFr.getPercentCasesOnTreatment()+correction);
			}else{
				lastFr.getFcRegimenObj().setPercentNewCases(lastFr.getPercentNewCases()+correction);
			}
		}
	}


	/**
	 * Add quantity mQ to the corresponding month in result
	 * @param result
	 * @param mQ
	 */
	private void addQuantityToList(List<MonthQuantityUIAdapter> result, MonthQuantityUIAdapter mQ) {
		for(MonthQuantityUIAdapter rMq : result){
			if(rMq.getMonth().compareTo(mQ.getMonth()) == 0){
				int value= rMq.getMonthQuantityObj().getIQuantity() + mQ.getIQuantity();
				rMq.getMonthQuantityObj().setIQuantity(value);
				break;
			}
		}

	}

	/**
	 * Convert enrolled cases from percentage style calculation to quantity style
	 * All percentage data will be removed
	 * @return true, if it is possible
	 */
	public boolean enrolledToQuantity() {
		if(isEnrolledCasesPercents()){
			if(!hasEnrolledQuantyties()){
				//all percents for regimen will be zero and create new by quantity
				for(ForecastingRegimenUIAdapter frUi : getRegimes()){
					frUi.getFcRegimenObj().getCasesOnTreatment().clear();
					createCasesOnTreatment(frUi);
					int maxI = getForecastObj().getCasesOnTreatment().size()-1;
					int index=frUi.getFcRegimenObj().getCasesOnTreatment().size()-1;
					for(int i=0; i<getForecastObj().getCasesOnTreatment().size();i++){
						BigDecimal qBD = calcPercents(getForecastObj().getCasesOnTreatment().get(maxI-i).getIQuantity(), frUi.getPercentCasesOnTreatment());
						int quant = qBD.setScale(0, RoundingMode.HALF_UP).intValue();
						//System.out.println(">>>>" + frUi.getRegimen().getName()+ " index "+ index+ " quant " + quant);
						if(index-i>=0){
							frUi.getFcRegimenObj().getCasesOnTreatment().get(index-i).setIQuantity(quant);
							//System.out.println(frUi.getRegimen().getName()+ " index "+ index+ " quant " + quant);
						}
					}
				}
			}
			getForecastObj().setIsOldPercents(false);
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Does this forecast has data for each regimen by quantity for enrolled cases.
	 * Possible when switch to was applied
	 * @return
	 */
	public boolean hasEnrolledQuantyties(){
		int retI = 0;
		for(ForecastingRegimen fR : getForecastObj().getRegimes()){
			for(MonthQuantity mq : fR.getCasesOnTreatment()){
				retI=retI+mq.getIQuantity();
			}
		}
		return retI>0;
	}
	/**
	 * Does this forecast has quantities to calculate percentage for enrolled cases
	 * @return
	 */
	public boolean hasEnrolledPersQuantities() {
		int retI = 0;
		for(MonthQuantity mq : getForecastObj().getCasesOnTreatment()){
			retI = retI + mq.getIQuantity();
		}
		return retI>0;
	}
	/**
	 * Does this forecast has quantities to calculate percentage for expected cases
	 * @return
	 */
	public boolean hasExpectedPersQuantities() {
		int retI = 0;
		for(MonthQuantity mq : getForecastObj().getNewCases()){
			retI = retI + mq.getIQuantity();
		}
		return retI>0;
	}
	/**
	 * Does this forecast has data for each regimen by quantity for expected cases.
	 * @return
	 */
	public boolean hasExpectedQuantities() {
		int retI = 0;
		for(ForecastingRegimen fR : getForecastObj().getRegimes()){
			for(MonthQuantity mq : fR.getNewCases()){
				retI=retI+mq.getIQuantity();
			}
		}
		return retI>0;
	}
	/**
	 * How to calculate delivery schedule - at once, yearly, quarterly or at exact months
	 * @return
	 */
	public DeliveryScheduleEnum getDeliverySchedule(){
		if(getForecastObj().getDeliverySchedule() == null){
			getForecastObj().setDeliverySchedule(DeliveryScheduleEnum.EXACT);
		}
		return getForecastObj().getDeliverySchedule();
	}
	/**
	 * How to calculate delivery schedule - at once, yearly, quarterly or at exact months
	 */
	public void setDeliverySchedule(DeliveryScheduleEnum schedule){
		DeliveryScheduleEnum oldValue = getDeliverySchedule();
		getForecastObj().setDeliverySchedule(schedule);
		firePropertyChange("deliverySchedule", oldValue, getDeliverySchedule());
	}

	/**
	 * How to calculate accelerated order schedule - at once, yearly, quarterly or at exact months
	 * @return
	 */
	public DeliveryScheduleEnum getAcceleratedSchedule(){
		if(getForecastObj().getAcceleratedSchedule() == null){
			getForecastObj().setAcceleratedSchedule(DeliveryScheduleEnum.EXACT);
		}
		return getForecastObj().getAcceleratedSchedule();
	}
	/**
	 * How to calculate accelerated order schedule - at once, yearly, quarterly or at exact months
	 */
	public void setAcceleratedSchedule(DeliveryScheduleEnum schedule){
		DeliveryScheduleEnum oldValue = getAcceleratedSchedule();
		getForecastObj().setAcceleratedSchedule(schedule);
		firePropertyChange("acceleratedSchedule", oldValue, getAcceleratedSchedule());
	}



	/**
	 * Get max stock as is if not zero, or infinity if zero
	 * @return
	 */
	public int getSmartMaxStock() {
		int ret = getMaxStock();
		if (ret == 0){
			ret = Integer.MAX_VALUE;
		}
		return ret;
	}

	public Boolean getDirty() {
		return dirty;
	}

	public void setDirty(Boolean dirty) {
		this.dirty = dirty;
	}
	/**
	 * Get forecasting period duration for summary tab
	 * @return
	 */
	public String getForecastingDurationDays() {
		int days = DateUtils.daysBetween(getFirstFCDate().getTime(), getEndDt())+1;
		String s =String.valueOf(days);
		return "("+s +" "+DateParser.getDaysLabel(days)+")";
	}

	public Boolean getScenario(){
		return true; //pessimist only! 20160902
	}

	public void setScenario(Boolean scenario){
		scenario = true; //pessimist only 20160902
		Boolean oldValue = getScenario();
		getForecastObj().setScenario(scenario);
		firePropertyChange("scenario", oldValue, getScenario());
	}
	/**
	 * Get init month of forecasting period
	 * @return
	 */
	public MonthUIAdapter getIniMonth() {
		return new MonthUIAdapter(Presenter.getFactory().createMonth(getIniDate().get(Calendar.YEAR), getIniDate().get(Calendar.MONTH)));
	}
	/**
	 * Get month of the end of lead time
	 * @return
	 */
	public MonthUIAdapter getLeadTimeEndMonth() {
		return new MonthUIAdapter(Presenter.getFactory().createMonth(getLeadTimeEnd().get(Calendar.YEAR),
				getLeadTimeEnd().get(Calendar.MONTH)));
	}
	/**
	 * It is maybe convenient to use scenario as ENUM, not true, false
	 * @return
	 */
	public OrderScenarioEnum getScenarioEnum(){
		if(getScenario()){
			return OrderScenarioEnum.PESSIMIST;
		}else{
			return OrderScenarioEnum.OPTIMIST;
		}
	}

	/**
	 * It is maybe convenient to use scenario as ENUM, not true, false
	 * @param scenario
	 */
	public void setScenarioEnum(OrderScenarioEnum scenario){
		OrderScenarioEnum oldValue = getScenarioEnum();
		setScenario(scenario==OrderScenarioEnum.PESSIMIST);
		firePropertyChange("scenarioEnum", oldValue, scenario);
	}
	/**
	 * Is it new forecasting?
	 * @return
	 */
	public boolean isNew() {
		return this.getName().contains(Messages.getString("Forecasting.newForecasting.name"));
	}

}
