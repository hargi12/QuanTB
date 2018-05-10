package org.msh.quantb.model.mvp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.LocalDate;
import org.msh.quantb.model.errorlog.ErrorLog;
import org.msh.quantb.model.errorlog.LogRecord;
import org.msh.quantb.model.forecast.Forecast;
import org.msh.quantb.model.forecast.ForecastFile;
import org.msh.quantb.model.forecast.ForecastLast5;
import org.msh.quantb.model.forecast.ForecastingBatch;
import org.msh.quantb.model.forecast.ForecastingMedicine;
import org.msh.quantb.model.forecast.ForecastingOrder;
import org.msh.quantb.model.forecast.ForecastingRegimen;
import org.msh.quantb.model.forecast.ForecastingRegimenResult;
import org.msh.quantb.model.forecast.ForecastingResult;
import org.msh.quantb.model.forecast.ForecastingTotalItem;
import org.msh.quantb.model.forecast.MedicineCons;
import org.msh.quantb.model.forecast.Month;
import org.msh.quantb.model.forecast.MonthQuantity;
import org.msh.quantb.model.forecast.PhaseResult;
import org.msh.quantb.model.forecast.PricePack;
import org.msh.quantb.model.forecast.WeekQuantity;
import org.msh.quantb.model.gen.ClassifierTypesEnum;
import org.msh.quantb.model.gen.LocaleName;
import org.msh.quantb.model.gen.Medicine;
import org.msh.quantb.model.gen.MedicineRegimen;
import org.msh.quantb.model.gen.MedicineTypesEnum;
import org.msh.quantb.model.gen.Phase;
import org.msh.quantb.model.gen.PhaseDurationEnum;
import org.msh.quantb.model.gen.Regimen;
import org.msh.quantb.model.gen.RegimenTypesEnum;
import org.msh.quantb.model.gen.SimpleStamp;
import org.msh.quantb.model.locale.LocaleSaved;
import org.msh.quantb.model.medicine.Medicines;
import org.msh.quantb.model.regimen.Regimens;
import org.msh.quantb.services.calc.DateUtils;
import org.msh.quantb.services.excel.TemplateImport;
import org.msh.quantb.services.io.MedicineUIAdapter;
import org.msh.quantb.services.io.MedicinesDicUIAdapter;
import org.msh.quantb.services.io.RegimensDicUIAdapter;

/**
 * This is a factory for all JAXB data types
 * @author alexey
 *
 */
public class ModelFactory {
	public static final String FORECAST_FILE_EXT = ".qtb";
	public static final String MED_DICTIONARY_XML = "/med_dictionary.xml"; //file name
	public static final String REG_DICTIONARY_XML = "/reg_dictionary.xml";
	public static final String LOCALE_CURRENT = "/locale.xml";
	public static final String HISTORY_CURRENT="/history.xml";
	private org.msh.quantb.model.medicine.ObjectFactory medFactory =
		new org.msh.quantb.model.medicine.ObjectFactory();
	private org.msh.quantb.model.gen.ObjectFactory genFactory =
		new org.msh.quantb.model.gen.ObjectFactory();
	private org.msh.quantb.model.regimen.ObjectFactory regFactory =
		new org.msh.quantb.model.regimen.ObjectFactory();
	private org.msh.quantb.model.forecast.ObjectFactory fcFactory = 
		new org.msh.quantb.model.forecast.ObjectFactory();
	private org.msh.quantb.model.errorlog.ObjectFactory errFactory = 
		new org.msh.quantb.model.errorlog.ObjectFactory();
	private org.msh.quantb.model.locale.ObjectFactory locFactory = 
		new org.msh.quantb.model.locale.ObjectFactory();

	//current medicine dictionary
	private Medicines medicineDic = null;
	//path to dictionaries data
	private String pathToData;
	//UI model of the medicines dictionary
	private MedicinesDicUIAdapter medicinesDicUIAdapter;
	//UI model for regimes dictionary
	private RegimensDicUIAdapter regimensDicUIAdapter;
	// current regimens dictionary
	private Regimens regimensDic;
	// all forecasts currently open
	private List<Forecast> allForecasts = new ArrayList<Forecast>();
	private LocaleSaved currentLocale=null;
	private ForecastLast5 forecastLast5;

	public  ModelFactory(String _pathToData){
		this.pathToData = _pathToData;
	}
	/**
	 * Create the medicine
	 * @return
	 */
	public Medicine createMedicine(){
		Medicine med = genFactory.createMedicine();
		med.setAbbrevName("");
		med.setDosage("");
		med.setName("");
		med.setStrength("");
		med.setType(MedicineTypesEnum.UNKNOWN);
		med.setClassifier(ClassifierTypesEnum.UNKNOWN);
		return med;
	}

	/**
	 * Create medicine dictionary document for marshaling
	 * @param medDic data structure for medicine dictionary
	 * @return
	 */
	public JAXBElement<Medicines> createMedDic(Medicines medDic) {
		return medFactory.createMedicines(medDic);
	}
	/**
	 * Get medicine dictionary marshaler
	 * @param doc document for marshaling
	 * @return marshaler or null in case of error
	 */
	@SuppressWarnings("unchecked")
	public Marshaller getMedicineMarshaler(JAXBElement<Medicines> doc){
		Class<Medicines> clazz = (Class<Medicines>) doc.getValue().getClass();
		return createMarshaller(clazz);
	}
	/**
	 * Get forecast marshaler
	 * @param doc document for marshaling
	 * @return marshaler or null in case of error
	 */
	@SuppressWarnings("unchecked")
	private Marshaller getForecastMarshaler(JAXBElement<Forecast> doc){
		Class<Forecast> clazz = (Class<Forecast>) doc.getValue().getClass();
		return createMarshaller(clazz);
	}
	/**
	 * Create structure for name with locale
	 * @return
	 */
	public LocaleName createLocaleName() {
		return genFactory.createLocaleName();
	}
	public SimpleStamp createSimpleStamp() {
		return genFactory.createSimpleStamp();
	}
	/**
	 * Return current data and time in XML Calendar data type
	 * @return now or null
	 */
	public XMLGregorianCalendar getNow() {
		Calendar now = GregorianCalendar.getInstance();
		XMLGregorianCalendar dtXML = getXMLCalendar(now);
		return dtXML;
	}
	/**
	 * Get any calendar as XML calendar
	 * @param cal
	 * @return
	 */
	public XMLGregorianCalendar getXMLCalendar(Calendar cal) {
		XMLGregorianCalendar dtXML = null;
		try {
			dtXML = DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar)cal);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e); // nothing to do
		}
		return dtXML;
	}
	/**
	 * Get unmarshaller for the medicine dictionary
	 * @return unmarshaller or or throw runtime exception if Unmarshaller cannot be created for some reason
	 */
	public Unmarshaller getMedicineUnMarshaler() {
		String packageName = Medicines.class.getPackage().getName();
		return getUnmarshaller(packageName);
	}
	/**
	 * Get unmarshaller for the forecasting
	 * @return unmarshaller or or throw runtime exception if Unmarshaller cannot be created for some reason
	 */
	private Unmarshaller getForecastUnMarshaler() {
		String packageName = Forecast.class.getPackage().getName();
		return getUnmarshaller(packageName);
	}
	/**
	 * General part of get...UnMarshaller
	 * @param packageName package to unmarshall
	 * @return unmarshaller or throw runtime exception if Unmarshaller cannot be created for some reason
	 */
	private Unmarshaller getUnmarshaller(String packageName) {
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance( packageName );
			Unmarshaller u = jc.createUnmarshaller();
			return u;
		} catch (JAXBException e) {
			throw new RuntimeException(e); // nothing to do
		}
	}
	/**
	 * Create regimens data structure
	 * @return 
	 */
	public Regimens createRegimens() {
		return regFactory.createRegimens();
	}
	/**
	 * Create the regimen phase
	 * @return
	 */
	public Phase createPhase() {
		Phase ret = genFactory.createPhase();
		ret.setDuration(0);
		ret.setMeasure(PhaseDurationEnum.MONTHLY);
		return ret;
	}
	/**
	 * Create a medication
	 * @param med medicine
	 * @param duration consumption duration
	 * @param dosage daily dose
	 * @param daysPerWeek days per week to accept
	 * @return
	 */
	public MedicineRegimen createMedication(Medicine med, int duration, int dosage, int daysPerWeek) {
		MedicineRegimen md = genFactory.createMedicineRegimen();
		md.setMedicine(med);
		md.setDuration(duration);
		md.setDosage(dosage);
		md.setDaysPerWeek(daysPerWeek);
		return md;
	}
	/**
	 * Create a regimen without type
	 * @param regName name of regimen
	 * @param formulation consumption formula
	 * @param type regimen type, default Multi Drug
	 * @return regimen
	 */
	public Regimen createRegimen(String regName, String formulation, RegimenTypesEnum type) {
		Regimen r = genFactory.createRegimen();
		r.setName(regName);
		r.setFormulation(formulation);
		if (type == null){
			r.setType(RegimenTypesEnum.MULTI_DRUG);
		}else{
			r.setType(type);
		}
		return r;
	}
	/**
	 * Store medicine dictionary to XML file
	 * @param medDic dictionary object
	 * @return
	 * @throws JAXBException signal to the Presenter about wrong XML
	 * @throws FileNotFoundException signal to Presenter - something wrong with the dictionary file, user may block it or file damaged
	 */
	public boolean storeMedicines(Medicines medDic) throws FileNotFoundException, JAXBException {
		JAXBElement<Medicines> doc = medFactory.createMedicines(medDic);
		Marshaller m = getMedicineMarshaler(doc);
		m.marshal( doc, new FileOutputStream( this.pathToData+ModelFactory.MED_DICTIONARY_XML ) );
		return true;
	}
	/**
	 * Store regimens dictionary to XML file
	 * @param rs regimens
	 * @return Regiments dictionary
	 * @throws JAXBException signal to the Presenter about wrong XML
	 * @throws FileNotFoundException signal to Presenter - something wrong with the dictionary file, user may block it or file damaged
	 */
	public boolean storeRegimens(Regimens rs) throws FileNotFoundException, JAXBException {
		this.regimensDic = rs;
		JAXBElement<Regimens> doc = regFactory.createRegimens(rs);
		Marshaller m = getRegimensMarshaller(doc);
		m.marshal(doc, new FileOutputStream( pathToData+REG_DICTIONARY_XML ));
		return true;

	}
	/**
	 * get regimes marshaler
	 * @param doc regimen object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Marshaller getRegimensMarshaller(JAXBElement<Regimens> doc) {
		Class<Regimens> clazz = (Class<Regimens>) doc.getValue().getClass();
		return createMarshaller(clazz);
	}
	/**
	 * Create marshaller for class given
	 * @param clazz class
	 * @return Marshaller or throw runtime exception
	 */
	@SuppressWarnings("rawtypes")
	private Marshaller createMarshaller(Class clazz) {
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(clazz.getPackage().getName());
			Marshaller m = context.createMarshaller();
			m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			return m;
		} catch (JAXBException e) {
			throw new RuntimeException(e); // nothing to do
		}
	}
	/**
	 * Create a medicine dictionary
	 * @return
	 */
	public Medicines createMedicines() {
		return medFactory.createMedicines();
	}
	/**
	 * Get regimen unMarshaller
	 * @return unMarshaller or null
	 */
	public Unmarshaller getRegimenUnMarshaler() {
		String packageName = Regimens.class.getPackage().getName();
		return getUnmarshaller(packageName);
	}

	/**
	 * Get medicine list from XML
	 * @return list of medicine
	 * @throws JAXBException signal to the Presenter about wrong XML
	 * @throws FileNotFoundException signal to Presenter - something wrong with the dictionary file, user may block it or file damaged
	 */
	@SuppressWarnings("unchecked")
	private Medicines readMedicinesDic() throws FileNotFoundException, JAXBException{
		medicineDic = null;
		medicineDic = ((JAXBElement<Medicines>) getMedicineUnMarshaler().unmarshal(new FileInputStream(pathToData+MED_DICTIONARY_XML))).getValue(); 
		return medicineDic;
	}

	/**
	 * read the medicines dictionary
	 * @return the medicineDic
	 * @throws JAXBException 
	 * @throws FileNotFoundException 
	 */
	public Medicines getMedicineDic() throws FileNotFoundException, JAXBException {
		if (medicineDic == null){
			medicineDic = readMedicinesDic();
		}
		return medicineDic;
	}
	/**
	 * Get MedicinesDic that suit UI binding requirements (Observable etc)
	 * @return
	 * @throws JAXBException bad XML
	 * @throws FileNotFoundException  bad file
	 */
	public MedicinesDicUIAdapter getMedicinesDicUIAdapter() throws FileNotFoundException, JAXBException{
		if (this.medicinesDicUIAdapter == null){
			this.medicinesDicUIAdapter = new MedicinesDicUIAdapter(getMedicineDic());
		}
		return this.medicinesDicUIAdapter;
	}
	/**
	 * Get RegimenDic that suit UI binding requirements (Observable etc)
	 * @return
	 * @throws JAXBException 
	 * @throws FileNotFoundException 
	 */
	public RegimensDicUIAdapter getRegimensDicUIAdapter() throws FileNotFoundException, JAXBException{
		if (this.regimensDicUIAdapter == null){
			this.regimensDicUIAdapter = new RegimensDicUIAdapter(getRegimenDic());
		}
		return this.regimensDicUIAdapter;
	}
	/**
	 * get or load regimen dictionary
	 * @return
	 * @throws JAXBException 
	 * @throws FileNotFoundException 
	 */
	private Regimens getRegimenDic() throws FileNotFoundException, JAXBException {
		if (this.regimensDic == null){
			readRegimenDic();
		}
		return this.regimensDic;
	}
	/**
	 * Read the regimen dictionary from XML file
	 * @throws JAXBException 
	 * @throws FileNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	private Regimens readRegimenDic() throws FileNotFoundException, JAXBException {
		this.regimensDic = null;
		this.regimensDic = ((JAXBElement<Regimens>) getRegimenUnMarshaler().unmarshal(new FileInputStream(pathToData+REG_DICTIONARY_XML))).getValue(); 
		return regimensDic;

	}
	/**
	 * Add new record to the medicines list
	 * @param med
	 * @throws JAXBException  bad dictionary xml
	 * @throws FileNotFoundException no dictionary at all
	 */
	public void addMedDic(Medicine med) throws FileNotFoundException, JAXBException {
		getMedicineDic().getMedicines().add(med);

	}
	/**
	 * set new Medicines dic
	 * @param meds
	 */
	public void setMedicinesDic(Medicines meds) {
		this.medicineDic = meds;

	}
	/**
	 * Sort medicine dic adapter by abbrev
	 * @throws JAXBException 
	 * @throws FileNotFoundException 
	 */
	public void sortMedicinesDic() throws FileNotFoundException, JAXBException {
		Set<MedicineUIAdapter> tmp = new TreeSet<MedicineUIAdapter>();
		tmp.addAll(getMedicinesDicUIAdapter().getMedicinesDic());
		getMedicinesDicUIAdapter().getMedicinesDic().clear();
		getMedicinesDicUIAdapter().getMedicinesDic().addAll(tmp);
	}
	/**
	 * Create new empty forecasting class with given name
	 * Also add one to open forecasting list
	 * @param name forecasting name
	 * @return
	 */
	public Forecast createForecasting(String name) {
		Forecast fc = fcFactory.createForecast();
		fc.setName(name);
		fc.setRegimensType(RegimenTypesEnum.MULTI_DRUG);
		fc.setScenario(true);
		this.getAllForecastings().add(fc);
		return fc;
	}
	/**
	 * Get all open forecasts
	 * @return
	 */
	private List<Forecast> getAllForecastings() {
		return allForecasts;
	}
	/**
	 * create new empty forecasting regimen with given regimen
	 * @param regimen given regimen
	 * @return
	 */
	public ForecastingRegimen createForecastingRegimen(Regimen regimen) {
		ForecastingRegimen fr = fcFactory.createForecastingRegimen();
		fr.setRegimen(regimen);
		return fr;
	}
	/**
	 * create new month quantity with given parameters
	 * @param _year
	 * @param _month
	 * @param _quantity
	 * @return
	 */
	public MonthQuantity createMonthQuantity(int _year, int _month, int _quantity) {
		assert(_month >=0);
		assert(_month<=11);
		assert(_year > 2000);
		MonthQuantity mq = fcFactory.createMonthQuantity();
		mq.setIQuantity(_quantity);
		Month m = fcFactory.createMonth();
		m.setMonth(_month);
		m.setYear(_year);
		mq.setMonth(m);
		return mq;
	}
	/**
	 * create new forecasting medicine based on medicine given
	 * @param med medicine given
	 * @return
	 */
	public ForecastingMedicine createForecastingMedicine(Medicine med) {
		ForecastingMedicine fmed = fcFactory.createForecastingMedicine();
		fmed.setMedicine(med);
		fmed.setConsumptionCases(0);
		fmed.setConsumptionLT(0);
		fmed.setConsumptionNewCases(0);
		fmed.setQuantityExpiredLT(0);
		fmed.setQuantityMissingLT(0);
		fmed.setStockOnHand(0);
		fmed.setStockOnOrderLT(0);
		fmed.setUnitPrice(0);
		fmed.setAjustmentEnrolled(new BigDecimal(100));
		fmed.setAdjustmentExpected(new BigDecimal(100));
		return fmed;
	}
	/**
	 * create a month based on parameters given
	 * @param _year
	 * @param _month
	 * @return
	 */
	public Month createMonth(int _year, int _month) {
		Month month = fcFactory.createMonth();
		month.setMonth(_month);
		month.setYear(_year);
		return month;
	}
	/**
	 * Create month from youda date
	 * @param yoda 
	 * @return
	 */
	public Month createMonth(LocalDate yoda){
		Month month = fcFactory.createMonth();
		month.setMonth(yoda.getMonthOfYear()-1);
		month.setYear(yoda.getYear());
		return month;
	}


	/**
	 * Store forecast as XML document in file system. Only data, without daily results<br>
	 * Also, cleans up excess data
	 * file name will be forecast name.xml
	 * @param forecast forecast
	 * @param path disk directory to store (not end with \)
	 * @throws JAXBException 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public void storeForecast(Forecast forecast, String path) throws JAXBException, IOException, ClassNotFoundException {
		Forecast forecastClone = deepClone(forecast);
		cleanUpForecast(forecastClone);
		storeForecastFull(forecastClone,path);
	}
	
	
	/**
	 * Serialize/de-serialize the forecast. Not very elegant :(
	 * @param forecast
	 * @return deep clone of forecast
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws JAXBException 
	 */
	@SuppressWarnings("unchecked")
	private Forecast deepClone(Forecast forecast) throws IOException, ClassNotFoundException, JAXBException {
		//serialize
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JAXBElement<Forecast> doc = fcFactory.createForecast(forecast);
		Marshaller m = getForecastMarshaler(doc);
		m.marshal(doc, baos);
        baos.close();
        //restore
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        JAXBElement<Forecast> clone = (JAXBElement<Forecast>) getForecastUnMarshaler().unmarshal(bais);
        bais.close();
        baos=null;
        bais=null;		
		return clone.getValue();
	}
	/**
	 * Clean up the forecast before save and after load
	 * @param forecast
	 */
	public void cleanUpForecast(Forecast forecast) {
		for(ForecastingRegimen r : forecast.getRegimes()){
			r.getResults().clear();
			if(forecast.isIsOldPercents()){
				r.getCasesOnTreatment().clear();
			}else{
				r.setPercentCasesOnTreatment(0);
			}
			if(forecast.isIsNewPercents()){
				r.getNewCases().clear();
			}else{
				r.setPercentNewCases(0);
			}
		}
		if(!forecast.isIsOldPercents()){
			forecast.getCasesOnTreatment().clear();
		}
		if(!forecast.isIsNewPercents()){
			forecast.getNewCases().clear();
		}
		for(ForecastingMedicine m : forecast.getMedicines()){
			m.getResults().clear();
		}
	}
	/**
	 * Store forecast with daily calculation results. Very big file<br>
	 * file name will be forecast name.xml
	 * @param forecast
	 * @param path path disk directory to store (not end with \)
	 * @throws JAXBException
	 * @throws IOException 
	 */
	public void storeForecastFull(Forecast forecast, String path) throws JAXBException, IOException{
		forecast.setRecordingDate(getNow());
		JAXBElement<Forecast> doc = fcFactory.createForecast(forecast);
		Marshaller m = getForecastMarshaler(doc);
		String fileName = path + "/" + doc.getValue().getName();
		if (!fileName.endsWith(FORECAST_FILE_EXT)){
			fileName = fileName + FORECAST_FILE_EXT;
		}
		FileOutputStream stream = new FileOutputStream(fileName);
		m.marshal(doc, stream);
		stream.flush();
		stream.close();

	}
	/**
	 * Read forecasting from given file system location<br>
	 * also, add forecasting to the active forecasting list
	 * @param path disk directory to read (not end with \)
	 * @param name forecasting name - file name without extension .XML
	 * @return
	 * @throws JAXBException 
	 * @throws FileNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	public Forecast readForecasting(String path, String name) throws FileNotFoundException, JAXBException {
		Forecast fc = ((JAXBElement<Forecast>) getForecastUnMarshaler().unmarshal(new FileInputStream(path+ "\\"+name))).getValue();
		if (fc.getRegimensType() == null){
			fc.setRegimensType(RegimenTypesEnum.MULTI_DRUG); //default
		}
		return fc;
	}
	/**
	 * Read forecasting from given file system location<br>
	 * also, add forecasting to the active forecasting list
	 * @param name forecasting name - full path with file name without extension .XML
	 * @return
	 * @throws JAXBException 
	 * @throws FileNotFoundException 
	 */
	public Forecast readForecasting(String fullPath) throws FileNotFoundException, JAXBException{
		FileInputStream fi = new FileInputStream(fullPath);
		File file = new File(fullPath);
		Forecast fc = ((JAXBElement<Forecast>) getForecastUnMarshaler().unmarshal(fi)).getValue(); 
		if (fc.getRegimensType() == null){
			fc.setRegimensType(RegimenTypesEnum.MULTI_DRUG); //default
		}
		fc.setName(file.getName()); //to avoid very smart users, that rename forecasting file
		cleanUpForecast(fc);
		return fc;
	}
	/**
	 * remove all medication from phase given with medicine given
	 * @param p phase
	 * @param medicine medicine
	 */
	public void removeMedications(Phase p, Medicine medicine) {
		List<MedicineRegimen> toDel = new ArrayList<MedicineRegimen>();
		for(MedicineRegimen me : p.getMedications()){
			// sorry, only MedicineUIAdapter really comparable
			MedicineUIAdapter ma1 = new MedicineUIAdapter(me.getMedicine());
			MedicineUIAdapter ma2 = new MedicineUIAdapter(medicine);
			if (ma1.equals(ma2)) toDel.add(me);
		}
		for(MedicineRegimen me : toDel) p.getMedications().remove(me);
	}

	/**
	 * Get path to dictionaries data
	 * @return path to dictionaries data
	 */
	public String getPathToData() {
		return pathToData;
	}
	/**
	 * Create new forecasting batch
	 * by default, availability date is in past
	 * @param expireMonth month to expire if null, set very very future
	 * @return empty forecasting batch
	 */
	public ForecastingBatch createForecastingBatch(Month expireMonth) {
		if (expireMonth == null){
			expireMonth = createMonth(9999, 9);
		}
		ForecastingBatch batch  =fcFactory.createForecastingBatch();
		Calendar cal =createCalendarFromMonth(expireMonth);
		// take last day of month
		cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		batch.setExpired(this.getXMLCalendar(cal));
		return defaultBatch(expireMonth, batch);
	}
	/**
	 * Create new forecasting batch with exact expire date
	 * by default, availability date is in past
	 * @param expireDate -date to expire if null, set very very future
	 * @return empty forecasting batch
	 */
	public ForecastingBatch createForecastingBatchExact(Calendar expireDate) {
		ForecastingBatch batch  =fcFactory.createForecastingBatch();
		if(expireDate == null){
			expireDate = GregorianCalendar.getInstance();
			expireDate.set(9999, 9, 9, 9, 9, 9);
		}
		DateUtils.cleanTime(expireDate);
		batch.setExpired(this.getXMLCalendar(expireDate));
		Month em = createMonth(expireDate.get(Calendar.YEAR), expireDate.get(Calendar.MONTH));
		return defaultBatch(em, batch);
	}
	/**
	 * Create calendar from month, first date assumed.
	 * @param month
	 * @return
	 */
	public Calendar createCalendarFromMonth(Month month) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(month.getYear(), month.getMonth(), 1, 0,0, 0);
		DateUtils.cleanTime(cal);
		return cal;
	}
	/**
	 * set batch fields to default values
	 * by default, availability date is in past
	 * @param expireMonth
	 * @param batch
	 * @return 
	 */
	private ForecastingBatch defaultBatch(Month expireMonth,
			ForecastingBatch batch) {
		if (expireMonth == null){
			expireMonth = createMonth(9999, 9);
		}
		batch.setExpiryDate(expireMonth);
		batch.setConsumptionInMonth(BigDecimal.ZERO);
		batch.setQuantity(0);
		batch.setQuantityAvailable(BigDecimal.ZERO);
		batch.setQuantityExpired(0);
		Calendar value = GregorianCalendar.getInstance();
		value.set(Calendar.YEAR, 1900);
		DateUtils.cleanTime(value);
		batch.setAvailFrom(getXMLCalendar(value));
		return batch;
	}

	/**
	 * Create new forecasting order with arrive and forecasting batch expire dates given
	 * @param arrive arrive month
	 * @param expire expire month if null - very very far future
	 * @return
	 */
	public ForecastingOrder createForecastingOrder(
			Month arrive, Month expire) {
		ForecastingOrder order = fcFactory.createForecastingOrder();
		ForecastingBatch batch = createForecastingBatch(expire);
		order.setArrivalDate(arrive);
		if(expire == null){
			expire = createMonth(9999, 9);
		}
		Calendar calE =createCalendarFromMonth(expire);
		Calendar calA =createCalendarFromMonth(arrive);
		order.setArrived(this.getXMLCalendar(calA));
		batch.setExpired(this.getXMLCalendar(calE));
		order.setBatch(batch);
		return order;
	}
	/**
	 * Create new forecasting order with exact dates
	 * @param _arrive exact date of arrive
	 * @param _expire exact date of expire, if null, mean very late date
	 * @return
	 */
	public ForecastingOrder createForecastingOrder(Calendar _arrive, Calendar _expire){
		ForecastingOrder order = fcFactory.createForecastingOrder();
		Month arrive = this.createMonth(_arrive.get(Calendar.YEAR), _arrive.get(Calendar.MONTH));
		order.setArrived(this.getXMLCalendar(_arrive));
		order.setArrivalDate(arrive);
		ForecastingBatch batch = createForecastingBatchExact(_expire);
		order.setBatch(batch);
		return order;
	}
	/**
	 * Create empty regimen result object for month given
	 * by default full month (fromDay and toDay both zero)
	 * @param monthObj
	 */
	public ForecastingRegimenResult createRegimenResult(Month monthObj) {
		ForecastingRegimenResult ret = fcFactory.createForecastingRegimenResult();
		ret.setMonth(monthObj);
		ret.setFromDay(0);
		ret.setToDay(0);
		PhaseResult inten = fcFactory.createPhaseResult();
		inten.setNewCases(new BigDecimal(0.00));
		inten.setOldCases(new BigDecimal(0.00));
		ret.setIntensive(inten);
		PhaseResult conti = fcFactory.createPhaseResult();
		conti.setNewCases(new BigDecimal(0.00));
		conti.setOldCases(new BigDecimal(0.00));
		ret.setContinious(conti);
		return ret;

	}

	/**
	 * Dispose currently edited regimen dictionary
	 */
	public void disposeRegimenDic() {
		this.regimensDic = null;
		this.regimensDicUIAdapter = null;

	}
	/**
	 * Create phase result
	 * @param newC new cases quantity
	 * @param oldC old cases quantity
	 * @return
	 */
	public PhaseResult createPhaseResult(BigDecimal newC, BigDecimal oldC) {
		PhaseResult ret = fcFactory.createPhaseResult();
		ret.setNewCases(newC);
		ret.setOldCases(oldC);
		return ret;
	}
	/**
	 * Create medicine consumption result
	 * @param medicine
	 * @return
	 */
	public MedicineCons createMedicineCons(Medicine medicine) {
		MedicineCons ret = fcFactory.createMedicineCons();
		ret.setMedicine(medicine);
		ret.setConsContiNew(BigDecimal.ZERO);
		ret.setConsContiOld(BigDecimal.ZERO);
		ret.setConsIntensiveNew(BigDecimal.ZERO);
		ret.setConsIntensiveOld(BigDecimal.ZERO);
		return ret;
	}
	/**
	 * Create forecasting result
	 * @param monthObj
	 * @return
	 */
	public ForecastingResult createForecastingResult(Month monthObj) {
		ForecastingResult res = fcFactory.createForecastingResult();
		res.setMonth(monthObj);
		res.setConsNew(BigDecimal.ZERO);
		res.setConsOld(BigDecimal.ZERO);
		res.setNewCases(new BigDecimal(0.00));
		res.setOldCases(new BigDecimal(0.00));
		res.setMissing(BigDecimal.ZERO);
		return res;
	}
	/**
	 * Create simple log record
	 * @param created
	 * @param stack
	 * @return
	 */
	public ErrorLog createSimpleLogRecord(Calendar created, String stack){
		LogRecord rec = errFactory.createLogRecord();
		rec.setDate(getXMLCalendar(created));
		rec.setMessage("unhandled error");
		rec.setUser("");
		rec.setStack(stack);
		ErrorLog log = errFactory.createErrorLog();
		log.getRecords().add(rec);
		return log;
	}
	/**
	 * Save error log file will be saved under data\log with name given in log object
	 * @param log log 
	 * @throws FileNotFoundException
	 * @throws JAXBException
	 */
	public void storeErrorLog(ErrorLog log) throws FileNotFoundException, JAXBException{
		JAXBElement<ErrorLog> doc = errFactory.createErrorLog(log);
		Marshaller m = getErrorLogMarshaler(doc);
		String logPath = this.pathToData + "/log";
		File f = new File(logPath);
		if(!f.exists()){
			f.mkdir();
		}
		m.marshal(doc, new FileOutputStream(logPath + "/" + doc.getValue().getName() + FORECAST_FILE_EXT));
	}
	/**
	 * Create error log marchaller
	 * @param doc error log document
	 * @return
	 */
	private Marshaller getErrorLogMarshaler(JAXBElement<ErrorLog> doc){
		Class<ErrorLog> clazz = (Class<ErrorLog>) doc.getValue().getClass();
		return createMarshaller(clazz);
	}
	/**
	 * Create price pack for forecasting medicine
	 * @param adjust adjustment coefficient 
	 * @param packSize size of medicine package
	 * @param packPrice price of entire package
	 * @return price pack to add to ForecastingMedicine
	 */
	public PricePack createPricePack(BigDecimal adjust, int packSize, BigDecimal packPrice){
		PricePack res = fcFactory.createPricePack();
		res.setAdjust(adjust);
		res.setAdjustAccel(adjust);
		res.setPack(packSize);
		res.setPackAccel(packSize);
		res.setPackPrice(packPrice);
		res.setPackPriceAccel(packPrice);
		return res;

	}
	/**
	 * Create forecasting total item
	 * @param item item name
	 * @param perCents additional expenses in perCents of total sum
	 * @return
	 */
	public ForecastingTotalItem createForecastingTotalItem(String item, BigDecimal perCents){
		ForecastingTotalItem res = fcFactory.createForecastingTotalItem();
		res.setItem(item);
		res.setPerCents(perCents);
		return res;
	}
	/**
	 * Read current locale if exist
	 * If not exist - return default locale
	 * @return
	 */
	public LocaleSaved getLocale(){
		if (this.currentLocale == null){
			try {
				this.currentLocale = ((JAXBElement<LocaleSaved>) getLocaleUnMarshaler().unmarshal(new FileInputStream(pathToData+LOCALE_CURRENT))).getValue();
			} catch (FileNotFoundException e) {
				this.currentLocale = createDefaultLocale();
			} catch (JAXBException e) {
				this.currentLocale = createDefaultLocale();
			} 
		}
		return this.currentLocale;
	}
	/**
	 * Create default locale
	 * @return
	 */
	private LocaleSaved createDefaultLocale() {
		LocaleSaved loc = locFactory.createLocaleSaved();
		loc.setCountry("");
		loc.setLang("");
		return loc;
	}
	/**
	 * Save the current locale
	 * @return
	 * @throws FileNotFoundException
	 * @throws JAXBException
	 */
	public boolean storeCurrentLocale() throws FileNotFoundException, JAXBException {
		JAXBElement<LocaleSaved> doc = locFactory.createCurrentLocale(this.currentLocale);
		Marshaller m = getLocaleMarshaller(doc);
		m.marshal(doc, new FileOutputStream( pathToData+LOCALE_CURRENT ));
		return true;

	}
	/**
	 * get locale marshaller
	 * @param doc
	 * @return
	 */
	private Marshaller getLocaleMarshaller(JAXBElement<LocaleSaved> doc) {
		Class<LocaleSaved> clazz = (Class<LocaleSaved>) doc.getValue().getClass();
		return createMarshaller(clazz);
	}
	/**
	 * Get locale unmarshaller
	 * @return
	 */
	public Unmarshaller getLocaleUnMarshaler() {
		String packageName = LocaleSaved.class.getPackage().getName();
		return getUnmarshaller(packageName);
	}
	
	/**
	 * Get or read list of 5 last opened forecasts
	 * @return
	 */
	public ForecastLast5 getForecastLast5(){
		if (this.forecastLast5 == null){
			try {
				this.forecastLast5 = ((JAXBElement<ForecastLast5>) getForecastLast5UnMarshaler().unmarshal(new FileInputStream(pathToData+HISTORY_CURRENT))).getValue();
			} catch (FileNotFoundException e) {
				this.forecastLast5 = fcFactory.createForecastLast5();
			} catch (JAXBException e) {
				this.forecastLast5 = fcFactory.createForecastLast5();
			}
		}
		return this.forecastLast5;
	}
	/**
	 * Get forecasting open history unmarchaller
	 * @return
	 */
	private Unmarshaller getForecastLast5UnMarshaler() {
		String packageName = ForecastLast5.class.getPackage().getName();
		return getUnmarshaller(packageName);
	}
	/**
	 * Store list of last 5 opened forecasts
	 * @throws JAXBException 
	 * @throws FileNotFoundException 
	 */
	public void storeForecastLast5() throws FileNotFoundException, JAXBException{
		JAXBElement<ForecastLast5> doc = fcFactory.createForecastLast5(this.getForecastLast5());
		Marshaller m = getForecastLast5Marshaller(doc);
		m.marshal(doc, new FileOutputStream( pathToData+HISTORY_CURRENT ));
	}
	/**
	 * Create single forecast file object
	 * @return
	 */
	public ForecastFile createForecastFile(){
		return fcFactory.createForecastFile();
	}
	
	/**
	 * Get forecasting open history marshaller to store
	 * @param doc forecastLast5Histrory in JAXB skin
	 * @return
	 */
	private Marshaller getForecastLast5Marshaller(JAXBElement<ForecastLast5> doc) {
		Class<ForecastLast5> clazz = (Class<ForecastLast5>) doc.getValue().getClass();
		return createMarshaller(clazz);
	}
	
	
	/**
	 * Create new week quantity for regimen
	 * @return
	 */
	public WeekQuantity createWeekQuantity() {
		WeekQuantity wq = fcFactory.createWeekQuantity();
		wq.setWeekNo(0);
		wq.setQuantity(0);
		wq.setNewQuantity(0);
		return wq;
	}
	/**
	 * Clean regimen dictionary, so will be rewritten from the file
	 */
	public void removeRegimenDicUI() {
		this.regimensDicUIAdapter = null;
		this.regimensDic = null;
	}
	/**
	 * Get Excel Stock import template object for future processing
	 * @return
	 */
	public TemplateImport fetchImportTemplate() {
		TemplateImport template = new TemplateImport(getPathToData()+"/StockTemplate.xlsx");
		return template;
	}
	
}
