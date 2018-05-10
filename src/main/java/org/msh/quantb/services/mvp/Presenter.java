package org.msh.quantb.services.mvp;

import java.awt.Cursor;
import java.awt.Desktop;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.msh.quantb.model.errorlog.ErrorLog;
import org.msh.quantb.model.forecast.Forecast;
import org.msh.quantb.model.forecast.ForecastFile;
import org.msh.quantb.model.forecast.ForecastingBatch;
import org.msh.quantb.model.forecast.ForecastingMedicine;
import org.msh.quantb.model.forecast.ForecastingOrder;
import org.msh.quantb.model.forecast.ForecastingRegimen;
import org.msh.quantb.model.forecast.ForecastingTotalItem;
import org.msh.quantb.model.forecast.Month;
import org.msh.quantb.model.gen.Medicine;
import org.msh.quantb.model.gen.MedicineRegimen;
import org.msh.quantb.model.gen.MedicineTypesEnum;
import org.msh.quantb.model.gen.Phase;
import org.msh.quantb.model.gen.Regimen;
import org.msh.quantb.model.gen.RegimenTypesEnum;
import org.msh.quantb.model.gen.SimpleStamp;
import org.msh.quantb.model.locale.LocaleSaved;
import org.msh.quantb.model.medicine.Medicines;
import org.msh.quantb.model.mvp.ModelFactory;
import org.msh.quantb.model.regimen.Regimens;
import org.msh.quantb.services.calc.DateUtils;
import org.msh.quantb.services.calc.DeliveryOrdersControl;
import org.msh.quantb.services.calc.ForecastMergeControl;
import org.msh.quantb.services.calc.ForecastingCalculation;
import org.msh.quantb.services.calc.ForecastingError;
import org.msh.quantb.services.calc.IdaysCalculator;
import org.msh.quantb.services.calc.MedicineConsumption;
import org.msh.quantb.services.calc.MedicineResume;
import org.msh.quantb.services.calc.OrderCalculator;
import org.msh.quantb.services.calc.PeriodResume;
import org.msh.quantb.services.calc.WeeklyFrequency;
import org.msh.quantb.services.excel.ClipBoard;
import org.msh.quantb.services.excel.ExportExcelWHO;
import org.msh.quantb.services.excel.ExportXLSX;
import org.msh.quantb.services.excel.ImportExcel;
import org.msh.quantb.services.excel.ImportExcelDTO;
import org.msh.quantb.services.excel.TemplateImport;
import org.msh.quantb.services.io.ForecastFileUI;
import org.msh.quantb.services.io.ForecastLast5UI;
import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.ForecastingBatchTmpStore;
import org.msh.quantb.services.io.ForecastingBatchUIAdapter;
import org.msh.quantb.services.io.ForecastingMedicineTmpStore;
import org.msh.quantb.services.io.ForecastingMedicineUIAdapter;
import org.msh.quantb.services.io.ForecastingOrderTmpStore;
import org.msh.quantb.services.io.ForecastingOrderUIAdapter;
import org.msh.quantb.services.io.ForecastingRegimenUIAdapter;
import org.msh.quantb.services.io.ForecastingRegimensUIAdapter;
import org.msh.quantb.services.io.ForecastingTotal;
import org.msh.quantb.services.io.ForecastingTotalItemUIAdapter;
import org.msh.quantb.services.io.ForecastingTotalMedicine;
import org.msh.quantb.services.io.MedicationUIAdapter;
import org.msh.quantb.services.io.MedicineTmpStore;
import org.msh.quantb.services.io.MedicineUIAdapter;
import org.msh.quantb.services.io.MedicinesDecoder;
import org.msh.quantb.services.io.MedicinesDicUIAdapter;
import org.msh.quantb.services.io.MonthUIAdapter;
import org.msh.quantb.services.io.PhaseUIAdapter;
import org.msh.quantb.services.io.RegimenTmpStore;
import org.msh.quantb.services.io.RegimenUIAdapter;
import org.msh.quantb.services.io.RegimensDicUIAdapter;
import org.msh.quantb.services.io.SliceFCUIAdapter;
import org.msh.quantb.view.ForecastingRegimensNewCasesModel;
import org.msh.quantb.view.ForecastingRegimensTableModel;
import org.msh.quantb.view.ISelectableMedicine;
import org.msh.quantb.view.dialog.IMultiMedSelection;
import org.msh.quantb.view.dialog.MedicineSelectDlg;
import org.msh.quantb.view.dialog.MedicineSelectMulDlg;
import org.msh.quantb.view.dialog.MedicinesDecodeDlg;
import org.msh.quantb.view.mvp.ViewFactory;
import org.msh.quantb.view.panel.ForecastingDocumentPanel;
import org.msh.quantb.view.panel.ForecastingTotalPanel;
import org.msh.quantb.view.panel.StockGraphPanel;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * This class acts as Presenter in common MVP model for Quantification
 * application
 * 
 * @author alexey
 * 
 */
public class Presenter {

	private static final String USER = "USER";
	private static final String EMPTY_ABBR = "";
	private static ViewFactory view;
	private static ModelFactory modelFactory;

	/**
	 * Entry point of Quantification tool
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		view = new ViewFactory();
		modelFactory = new ModelFactory(getXMLPath());
		LocaleSaved localeSaved = modelFactory.getLocale();
		setLocale(localeSaved.getLang(), localeSaved.getCountry());
		view.showMainWindow(false);
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				if(e.toString().contains("0>=0") || e.toString().contains("0 >=0") ||
						e.toString().contains("0>= 0") || e.toString().contains("0 >= 0")){
					return;  //AK 20151014 to get round error from the JDateChooser
				}
				e.printStackTrace();
				Writer result = new StringWriter();
				PrintWriter printWriter = new PrintWriter(result);
				e.printStackTrace(printWriter);
				view.showError(Messages.getString("Error.runtime.text") + e.toString());
				Calendar cal = GregorianCalendar.getInstance();
				ErrorLog log = modelFactory.createSimpleLogRecord(cal, result.toString());
				String logName = DateUtils.getLogFileName(cal) + "err";
				log.setName(logName);
				try {
					modelFactory.storeErrorLog(log);
					setLocale(modelFactory.getLocale().getLang(), modelFactory.getLocale().getCountry());
				} catch (FileNotFoundException exp) {
					e.printStackTrace();
					setLocale(modelFactory.getLocale().getLang(), modelFactory.getLocale().getCountry());
				} catch (JAXBException exp) {
					e.printStackTrace();
					setLocale(modelFactory.getLocale().getLang(), modelFactory.getLocale().getCountry());
				}
			}
		});
	}

	/**
	 * Ask User about exit Quantb
	 */
	public static void askStop() {
		JTabbedPane fcast = view.getMainWindow().getPanelDocuments();
		if (fcast != null) {
			if (fcast.getTabCount() > 0) {
				int res = view.showExitConfirmDlg();
				switch (res) {
				case JOptionPane.YES_OPTION:
					saveAllForecasts();
				case JOptionPane.NO_OPTION:
					stop();
				}
			} else {
				if (view.showSimpleExitConfirmation()) {
					stop();
				}
			}
		} else {
			stop();
		}
	}

	/**
	 * Ask User about delete data
	 * 
	 * @return true - confirm, false - denied
	 */
	public static boolean askDelete() {
		return view.showDeleteConfitmDlg();
	}
	/**
	 * Ask file rewrite
	 * @param file
	 * @return
	 */
	private static boolean askFileRewrite(File file) {
		return view.showFileRewriteDialog(file.getName());
	}

	/**
	 * Ask user about save data
	 * @param forecastUIAdapter 
	 * 
	 * @return true - confirm, false - deny
	 */
	public static boolean askSave(ForecastUIAdapter forecastUIAdapter) {
		String message = Messages.getString("Application.saveDocument.message");
		if(forecastUIAdapter.isNew()){
			message = Messages.getString("Application.saveDocument.saveas");
		}
		return view.showSaveDocumentConfirmDlg(message);
	}

	/**
	 * Load medicines list and show in dialog form
	 * 
	 * @param selected
	 */
	public static void showMedicineListDialog(MedicineUIAdapter selected) {
		try {
			MedicinesDicUIAdapter medicinesDic = modelFactory.getMedicinesDicUIAdapter();
			if (medicinesDic != null) {
				view.showMedicineListDlg(medicinesDic, selected);
			}
		} catch (FileNotFoundException e) {
			if (!Presenter.showWarningString(getMessage("Error.medicines.file"))){
				saveActiveForecasting(false);
				askStop();
			};
		} catch (JAXBException e) {
			if (!Presenter.showWarningString(getMessage("Error.medicines.xml"))){
				saveActiveForecasting(false);
				askStop();
			};
		}

	}


	/**
	 * get path to the dictionary XML
	 * 
	 * @return
	 */
	private static String getXMLPath() {
		String ret = System.getProperty("quantb.data.path", "src/test/resources");
		return ret;
	}

	/**
	 * get path to the default document folder
	 * 
	 * @return
	 */
	public static String getDocDefaultPath() {
		//TODO
		String ret = getFactory().getLocale().getPathToFiles();
		if(ret == null || ret.equals(""))
			ret = getHomeDocPath();
		return ret;
	}

	public static String getHomeDocPath(){
		return System.getProperty("quantb.doc.path", "src/test/resources/doc");
	}

	/**
	 * Get version number
	 * 
	 * @return
	 */
	public static String getVersion() {
		String ret = System.getProperty("quantb.version", "Development mode");
		return ret;
	}

	/**
	 * Save all changes and exit all views
	 */
	public static void stop() {
		view.disposeMainWindow();
		System.exit(0);
	}

	/**
	 * Translate a key into a system messages using the current locale
	 * 
	 * @param key
	 * @return
	 */
	public static String getMessage(String key) {
		return Messages.getString(key);
	}

	/**
	 * Create new medicine and show it in new medicine dialog
	 */
	public static void createNewMedicine() {
		if (getActiveForecasting() != null){
			if (!showWarningAdvanced(getMessage("Warning.forecasting.open"), 
					getMessage("DlgMedicines.warning.title"))){
				return;
			}

		}
		MedicineUIAdapter med = new MedicineUIAdapter(modelFactory.createMedicine());
		med.setAbbrevName(EMPTY_ABBR);
		med.setName(EMPTY_ABBR);
		med.setDosage(EMPTY_ABBR);
		med.setStrength(EMPTY_ABBR);
		view.showNewMedicineDialog(med, false);
	}
	/**
	 * Show warning message with custom title and predefined choice No
	 * All . will be replaced to . and line feed
	 * All | will be replaced to two line feeds
	 * @param message
	 * @param title
	 * @return
	 */
	private static boolean showWarningAdvanced(String message, String title) {
		return getView().showCommonWarning(message, title, true);
	}

	/**
	 * Edit selected medicine
	 * 
	 * @param medicine selected medicine
	 */
	public static void editMedicine(MedicineUIAdapter medicine) {
		if (getActiveForecasting() != null){
			if (!showWarningAdvanced(getMessage("Warning.forecasting.open"), 
					getMessage("DlgMedicines.warning.title"))){
				return;
			}

		}
		view.showNewMedicineDialog(medicine.createClone(modelFactory), true);
	}

	/**
	 * Save the medicines dictionary to XML formatted file
	 */
	public static void saveMedicinesDic() {
		//prepare data
		try {
			// sort records
			modelFactory.sortMedicinesDic();
			// remove empty records
			Medicines meds = modelFactory.createMedicines();
			MedicinesDicUIAdapter medDic = modelFactory.getMedicinesDicUIAdapter();
			for (MedicineUIAdapter med : medDic.getMedicinesDicAll()) {
				if (!med.getName().equalsIgnoreCase(EMPTY_ABBR)) {
					meds.getMedicines().add(med.getMedicine());
				}
			}
			// put stamp
			SimpleStamp stamp = modelFactory.createSimpleStamp();
			stamp.setName(USER);
			stamp.setLastUpdated(modelFactory.getNow());
			meds.setStamp(stamp);
			// save
			modelFactory.setMedicinesDic(meds);
			modelFactory.storeMedicines(meds);
		} catch (FileNotFoundException e) {
			view.showError(getMessage("Error.medicines.file"));
		} catch (JAXBException e) {
			view.showError(getMessage("Error.medicines.xml"));
		}

	}

	/**
	 * Delete the medicine record by index
	 * 
	 * @param object
	 */
	public static void deleteMedicine(MedicineUIAdapter object) {
		if (getActiveForecasting() != null){
			if (!showWarningAdvanced(getMessage("Warning.forecasting.open"), 
					getMessage("DlgMedicines.warning.title"))){
				return;
			}

		}
		try {
			if (askDelete()) {
				modelFactory.getMedicinesDicUIAdapter().getMedicinesDicAll().remove(object);
				saveMedicinesDic();
			}
		} catch (FileNotFoundException e) {
			view.showError(getMessage("Error.medicines.file"));
		} catch (JAXBException e) {
			view.showError(getMessage("Error.medicines.xml"));
		}

	}

	/**
	 * Show regimens list to user
	 */
	public static void showRegimensDialog(RegimenTypesEnum types) {
		try {
			RegimensDicUIAdapter data = modelFactory.getRegimensDicUIAdapter();
			data.setFilter(types);
			view.showRegimenListDialog(data);
		} catch (FileNotFoundException e) {
			if(!Presenter.showWarningString(getMessage("Error.regimens.file"))){
				saveActiveForecasting(false);
				askStop();
			};
		} catch (JAXBException e) {
			if(!Presenter.showWarningString(getMessage("Error.regimens.xml"))){
				saveActiveForecasting(false);
				askStop();
			};
		}

	}

	/**
	 * Show error as service :)
	 * 
	 * @param string
	 */
	public static void showError(String string) {
		view.showError(string);

	}

	/**
	 * Show select medicine dialog
	 * 
	 * @param selected - list medicines do not include to selection
	 * @param isSingle is single selection of medications
	 * 
	 */
	public static void selectMedicine(List<MedicineUIAdapter> selected, boolean isSingle) {
		try {
			MedicinesDicUIAdapter medicinesDic = modelFactory.getMedicinesDicUIAdapter();
			if (medicinesDic != null) {
				ISelectableMedicine dlg = (ISelectableMedicine) view.getRegimensDlg();
				Medicines mdic = modelFactory.getMedicineDic();
				MedicinesDicUIAdapter medi = new MedicinesDicUIAdapter(mdic);
				medi.setExeptionList(selected);
				view.showSelectedMedicineDlg(medi, dlg, isSingle);
			}
		} catch (FileNotFoundException e) {
			if(!Presenter.showWarningString(getMessage("Error.medicines.file"))){
				askStop();
			};
		} catch (JAXBException e) {
			if(!Presenter.showWarningString(getMessage("Error.medicines.xml"))){
				askStop();
			};
		}
	}

	/**
	 * Save all regimen dictionary
	 * 
	 * @param isEdited true if edit existing, false add new medicine
	 */
	public static void saveRegimenDic(RegimenTmpStore selected, boolean isEdited) {
		//prepare empty document
		Regimens regimes = modelFactory.createRegimens();
		// fill empty document
		try {
			//Add new regimen to the dictionary
			if (selected != null && !isEdited)
				modelFactory.getRegimensDicUIAdapter().getSavedRegimens().add(selected.getRegimen());
			for (RegimenUIAdapter ra : modelFactory.getRegimensDicUIAdapter().getSavedRegimens()) {
				Regimen r = modelFactory.createRegimen(ra.getName(), ra.getConsumption(), ra.getType());
				Phase phaseInt = modelFactory.createPhase();
				phaseInt.setDuration(ra.getIntensive().getDurationValue());
				phaseInt.setMeasure(ra.getIntensive().getMeasure());
				phaseInt.setOrder(1);
				for (MedicationUIAdapter ma : ra.getIntensive().getMedications()) {
					MedicineRegimen medi = modelFactory.createMedication(ma.getMedicine().getMedicine(), ra.getIntensive().getDurationValue(), ma.getDosage(), ma.getDaysPerWeek());
					phaseInt.getMedications().add(medi);
				}
				r.setIntensive(phaseInt);
				Phase phaseCont = modelFactory.createPhase();
				phaseCont.setDuration(ra.getContinious().getDurationValue());
				phaseCont.setMeasure(ra.getContinious().getMeasure());
				phaseCont.setOrder(2);
				for (MedicationUIAdapter ma : ra.getContinious().getMedications()) {
					MedicineRegimen medi = modelFactory.createMedication(ma.getMedicine().getMedicine(), ra.getContinious().getDurationValue(), ma.getDosage(), ma.getDaysPerWeek());
					phaseCont.getMedications().add(medi);
				}
				r.setContinious(phaseCont);
				// store additional phases
				for (PhaseUIAdapter phUi : ra.getAddPhases()){
					Phase ph = modelFactory.createPhase();
					ph.setDuration(phUi.getDurationValue());
					ph.setMeasure(phUi.getMeasure());
					ph.setOrder(phUi.getOrder());
					for(MedicationUIAdapter ma : phUi.getMedications()){
						MedicineRegimen medi = modelFactory.createMedication(ma.getMedicine().getMedicine(), phUi.getDurationValue(), ma.getDosage(), ma.getDaysPerWeek());
						ph.getMedications().add(medi);
					}
					r.getAddPhases().add(ph);
				}
				regimes.getRegimen().add(r);
			}
			saveRegimens(regimes);
		} catch (FileNotFoundException e) {
			view.showError(getMessage("Error.regimens.file"));
		} catch (JAXBException e) {
			view.showError(getMessage("Error.regimens.xml"));
		}
	}

	private static void saveRegimens(Regimens regimes)
			throws FileNotFoundException, JAXBException {
		// put stamp
		SimpleStamp stamp = modelFactory.createSimpleStamp();
		stamp.setName(USER);
		stamp.setLastUpdated(modelFactory.getNow());
		regimes.setStamp(stamp);
		// really save
		modelFactory.storeRegimens(regimes);

		//inform all about changes
		modelFactory.getRegimensDicUIAdapter().firePropertyChange("regimens", null, modelFactory.getRegimensDicUIAdapter().getRegimens());
	}

	/**
	 * Adjust regimen list view
	 * 
	 * @param selected
	 */
	public static void adjustRegimen(RegimenTmpStore selected) {
		if (selected != null) {
			if (view.getRegimenListDlg() != null){
				view.getRegimenListDlg().setRow(selected);
				view.getRegimenListDlg().setType(selected.getRegimen().getType());
			}
		}
	}

	/**
	 * Delete regimen number i
	 * 
	 * @param i
	 */
	public static void deleteRegimen(int i) {
		if (getActiveForecasting() != null){
			if (!showWarningAdvanced(getMessage("Warning.forecasting.open"), 
					getMessage("DlgMedicines.warning.title"))){
				return;
			}

		}
		try {
			if (askDelete()) {
				List<RegimenUIAdapter> regFilteredList = modelFactory.getRegimensDicUIAdapter().getRegimens();
				RegimenUIAdapter regU = regFilteredList.get(i);
				List<RegimenUIAdapter> regList = modelFactory.getRegimensDicUIAdapter().getSavedRegimens();
				regList.remove(regU);
				saveRegimenDic(null, false);
			}
		} catch (FileNotFoundException e) {
			view.showError(getMessage("Error.regimens.file"));
		} catch (JAXBException e) {
			view.showError(getMessage("Error.regimens.xml"));
		}

	}

	/**
	 * Edit particular regimen
	 * 
	 * @param selected - storage for particular regimen
	 * @param isFromRegimen does the edit regimen will work with regimen list
	 *        dialog box
	 * @param isEdit does RegimenTmpStore edit
	 */
	public static void editRegimen(RegimenTmpStore selected, boolean isFromRegimen, boolean isEdit) {
		if (getActiveForecasting() != null){
			if (!showWarningAdvanced(getMessage("Warning.forecasting.open"), 
					getMessage("DlgMedicines.warning.title"))){
				return;
			}

		}
		// close regimen list			
		editRegimenPrivate(selected, isFromRegimen, isEdit);
	}

	/**
	 * Really edit regimen This method is only to bypass the fuser
	 * @param selected
	 * @param isFromRegimen
	 * @param isEdit
	 */
	private static void editRegimenPrivate(RegimenTmpStore selected,
			boolean isFromRegimen, boolean isEdit) {
		if (view.getRegimenListDlg() != null) view.getRegimenListDlg().clearSelection();
		view.showRegimensDlg(selected, isFromRegimen, isEdit);
	}

	/**
	 * Add medicine to intensive phase of given regimen
	 * 
	 * @param selRegimen
	 * @param intensive intensive phase
	 * 
	 */
	public static void addMedicineToPhase(RegimenTmpStore selRegimen, boolean intensive) {
		// selected will be installed via binding automatically
		PhaseUIAdapter pUI = intensive ? selRegimen.getRegimen().getIntensive() : selRegimen.getRegimen().getContinious();
		List<MedicineUIAdapter> notSelect = pUI.getMedicines();
		selectMedicine(notSelect, selRegimen.getRegimen().getType() == RegimenTypesEnum.SINGLE_DRUG);
	}

	/**
	 * Create new empty medication
	 * 
	 * @return
	 */
	public static MedicineRegimen createEmptyMedication() {
		Medicine med = modelFactory.createMedicine();
		med.setName("");
		med.setAbbrevName("");
		MedicineRegimen medi = new MedicineRegimen();
		medi.setMedicine(med);
		medi.setDaysPerWeek(0);
		medi.setDosage(0);
		medi.setDuration(0);
		return medi;
	}

	/**
	 * delete the medication by medicine. All medication with medicine given
	 * will be deleted
	 * 
	 * @param selRegimen regimen
	 * @param selMedicine medicine
	 * @param intensive true - from the intensive phase, false - from the
	 *        continuous phase
	 * 
	 */
	public static void deleteMedication(RegimenTmpStore selRegimen, MedicineTmpStore selMedicine, boolean intensive) {
		Phase p = null;
		if (intensive) p = selRegimen.getRegimen().getIntensive().getPhase();
		else
			p = selRegimen.getRegimen().getContinious().getPhase();
		if (askDelete()) {
			modelFactory.removeMedications(p, selMedicine.getMedicine().getMedicine());
			// new UI phase definition must replace the old one and select first medicine
			if (intensive) {
				selRegimen.getRegimen().setIntensive(selRegimen.getRegimen().getIntensive());
			} else {
				selRegimen.getRegimen().setContinious(selRegimen.getRegimen().getContinious());
			}
		}
	}

	/**
	 * add new regimen to the regimes dictionary from the Regimen list
	 * 
	 * @param regimensDic current regimes dictionary
	 */
	public static void addNewRegimen(RegimensDicUIAdapter regimensDic) {
		if (getActiveForecasting() != null){
			if (!showWarningAdvanced(getMessage("Warning.forecasting.open"), 
					getMessage("DlgMedicines.warning.title"))){
				return;
			}

		}
		RegimenUIAdapter regUIAdapter = createNewRegimen(regimensDic);
		editRegimenPrivate(new RegimenTmpStore(regUIAdapter), true, false);
	}
	/**
	 * Create the new regimen
	 * @param regimensDic
	 * @return
	 */
	private static RegimenUIAdapter createNewRegimen(
			RegimensDicUIAdapter regimensDic) {
		Regimen reg = modelFactory.createRegimen("", "", regimensDic.getFilter());
		Phase pI = modelFactory.createPhase();
		Phase pC = modelFactory.createPhase();
		;
		reg.setIntensive(pI);
		reg.setContinious(pC);
		RegimenUIAdapter regUIAdapter = new RegimenUIAdapter(reg);
		return regUIAdapter;
	}

	/**
	 * Add new regimen to the regimen dictionary from the forecasting wizard
	 * @param regimensDic
	 */
	public static void addNewRegimenFromFC(RegimensDicUIAdapter regimensDic) {
		RegimenUIAdapter regUIAdapter = createNewRegimen(regimensDic);
		editRegimen(new RegimenTmpStore(regUIAdapter), false, false);
	}

	/**
	 * Close Regimen dialog and select custom regimen in RegimenListDlg
	 * 
	 * @param isFromRegimen does the edit regimen dialog called from regimen
	 *        list dialog
	 * @param wasEdited - true if not new
	 * 
	 */
	public static void closeRegimenDlg(boolean isFromRegimen, boolean wasEdited) {
		if (wasEdited && !Presenter.askCancel()){
			return;
		}
		RegimenTypesEnum type = view.getRegimensDlg().getSelected().getRegimen().getType();
		view.getRegimensDlg().dispose();
		if (isFromRegimen) {
			modelFactory.disposeRegimenDic();
			if (view.getRegimenListDlg() != null) {
				view.getRegimenListDlg().dispose();
			}
			showRegimensDialog(type);
		}
	}

	/**
	 * Open forecasting xml document
	 */
	public static void openForecastingDocument() {
		String currentDirectory = getDocDefaultPath();
		File selectedFile = view.getForecastingFile(currentDirectory);
		openForecastingDocument(selectedFile);
	}

	/**
	 * Open selected forecasting document
	 * 
	 * @param file forecasting
	 */
	public static void openForecastingDocument(File file) {
		if (file != null) {
			try {
				Forecast forecast = modelFactory.readForecasting(file.getAbsolutePath());
				boolean allowOpen = true;
				//buffer stock only for old versions
				if(forecast.getBufferStockTime()>0){
					String s = String.format(Messages.getString("Forecasting.warning.bufferStock4"),
							forecast.getBufferStockTime(),
							forecast.getMinStock(),
							forecast.getMaxStock());
					allowOpen=showWarningString(s);
					forecast.setMinStock(0);
					forecast.setMaxStock(0);
				}
				ForecastUIAdapter forecastObj = new ForecastUIAdapter(forecast);
				forecastObj.calcIniDate();
				if(allowOpen){
					forecast.setBufferStockTime(0);
					registerInHistory(file);
					if (!forecastObj.isVersionSuit(Presenter.getVersion())){
						showWrongVersionDialog();
						return;
					}
					forecastObj.shiftOldCasesPercents();
					forecastObj.shiftOldCasesRegimens();
					forecastObj.shiftNewCasesPercents();
					forecastObj.shiftNewCasesRegimens();
					view.addForecastingDocument(forecastObj, file);
					//only for backward compatibility
					ForecastUIAdapter fcUI = view.getActiveForecastingPanel().getForecast();
					fcUI.getForecastObj().setIsOldPercents(forecast.isIsOldPercents());
					fcUI.getForecastObj().setIsNewPercents(forecast.isIsNewPercents());				
					if (runForecastingCalculation()){
						addChangeParametersLogic();
					}
				}
			} catch (FileNotFoundException e) {
				view.showError(getMessage("Error.forecasting.file"));		
			} catch (JAXBException e) {
				view.showError(getMessage("Error.forecasting.xml"));
			}
		}
	}
	/**
	 * Hide results tab FC parameters became changed
	 */
	private static void addChangeParametersLogic() {
		for(String pS : ForecastUIAdapter.getParamenters())
			getActiveForecasting().addPropertyChangeListener(pS, new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					getView().getActiveForecastingPanel().setVisibleCalculationDetailsTabs(false);
				}
			});

	}

	/**
	 * Register open file in open file history
	 * 
	 * @param file
	 */
	private static void registerInHistory(File file) {
		//adjust name for new
		String fileName = file.getName();
		if (!fileName.endsWith(ModelFactory.FORECAST_FILE_EXT)){
			fileName = fileName + ModelFactory.FORECAST_FILE_EXT;
		}
		//generate
		ForecastFile fcFile = modelFactory.createForecastFile();
		fcFile.setName(fileName);
		fcFile.setPath(file.getParent());
		ForecastFileUI ffU = new ForecastFileUI(fcFile);
		//add	
		ForecastLast5UI fcHistory = new ForecastLast5UI(modelFactory.getForecastLast5());
		fcHistory.add(ffU);
		try {
			modelFactory.storeForecastLast5();
		} catch (FileNotFoundException e) {
			view.showError(getMessage("Error.forecasting.file"));
		} catch (JAXBException e) {
			view.showError(getMessage("Error.forecasting.xml"));	
		}
	}


	/**
	 * Run new forecasting wizard to create new forecast
	 */
	public static void createForecasting() {
		try {
			RegimensDicUIAdapter regimensDicUIAdapter = modelFactory.getRegimensDicUIAdapter();
			for (RegimenUIAdapter rui : regimensDicUIAdapter.getRegimens()) {
				rui.setChecked(false);
			}
			Forecast forecast = modelFactory.createForecasting(Messages.getString("Forecasting.newForecasting.name"));
			Calendar now = GregorianCalendar.getInstance();
			Calendar nowDate = DateUtils.getCleanCalendar(now.get(Calendar.YEAR),
					now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
			ForecastUIAdapter forecastUIAdapter = new ForecastUIAdapter(forecast);
			forecastUIAdapter.setReferenceDate(nowDate);
			forecastUIAdapter.setLeadTime(1);
			forecast.setBufferStockTime(0);
			forecast.setMaxStock(0);
			forecast.setMinStock(0);
			forecast.setCalculator("");
			forecast.setInstitution("");
			forecast.setCountry("");
			forecast.setIsOldPercents(false);
			forecast.setIsNewPercents(false);
			forecastUIAdapter.getScenario();
			forecastUIAdapter.setQtbVersion(Presenter.getVersion());
			//regimensDicUIAdapter.selectAll();
			view.showNewForecastingWizard(forecastUIAdapter, regimensDicUIAdapter);
		} catch (FileNotFoundException e) {
			if(!Presenter.showWarningString(getMessage("Error.regimens.file"))){
				askStop();
			};
		} catch (JAXBException e) {
			if (!Presenter.showWarningString(getMessage("Error.regimens.xml"))){
				askStop();
			};
		}
	}

	/**
	 * Add new forecasting document to main window
	 * 
	 * @param forecast forecasting document
	 * @param selected selected regimens
	 */
	public static void addForecasting(ForecastUIAdapter forecast, List<RegimenUIAdapter> selected) {
		if (selected != null && !selected.isEmpty()) {
			for (RegimenUIAdapter r : selected) {
				forecast.getForecastObj().getRegimes().add(modelFactory.createForecastingRegimen(r.getRegimen()));
			}
		}
		addForecastingTab(forecast);
	}

	/**
	 * Add real tab for the new forecasting
	 * @param forecast
	 */
	public static void addForecastingTab(ForecastUIAdapter forecast) {
		forecast.shiftNewCasesPercents();
		forecast.shiftNewCasesRegimens();
		view.addForecastingDocument(forecast, null);
	}

	/**
	 * Add new medicine to Medicines Dictionary;
	 * 
	 * @param medicine
	 */
	public static void addMedicineToDic(MedicineUIAdapter medicine) {
		try {
			modelFactory.getMedicinesDicUIAdapter().getMedicinesDicAll().add(medicine);
			saveMedicinesDic();
		} catch (FileNotFoundException e) {
			view.showError(getMessage("Error.medicines.file"));
		} catch (JAXBException e) {
			view.showError(getMessage("Error.medicines.xml"));
		}
	}

	/**
	 * Save edited medicine in medicine dictionary
	 * 
	 * @param medicine edited medicine
	 */
	public static void saveMedicineDic(MedicineUIAdapter medicine) {
		try {
			List<MedicineUIAdapter> medicineDic = modelFactory.getMedicinesDicUIAdapter().getMedicinesDic();
			medicineDic.get(medicineDic.indexOf(view.getMedicinesDlg().getSelected().getMedicine())).setMedicine(medicine.getMedicine());
			saveMedicinesDic();
		} catch (FileNotFoundException e) {
			view.showError(getMessage("Error.medicines.file"));
		} catch (JAXBException e) {
			view.showError(getMessage("Error.medicines.xml"));
		}
	}

	/**
	 * Add selected in selectedMedicineDlg medications to selected phase
	 * 
	 * @param selectedList selected in selectedMedicineDlg medications
	 */
	public static void addSelectedMedicinesToPhase(List<MedicineUIAdapter> selectedList) {
		if (selectedList != null && selectedList != null && !selectedList.isEmpty()) {
			if (!selectedList.isEmpty()) {
				if (view.getRegimensDlg().isIntensive()) {
					if (view.getRegimensDlg().getSelected().getRegimen().getIntensive().getPhase().getMedications().size() == 1 && view.getRegimensDlg().getSelected().getRegimen().getIntensive().getPhase().getMedications().get(0).getMedicine().getAbbrevName().isEmpty()) {
						view.getRegimensDlg().getSelected().getRegimen().getIntensive().getPhase().getMedications().clear();
					}
				} else {
					if (view.getRegimensDlg().getSelected().getRegimen().getContinious().getPhase().getMedications().size() == 1
							&& view.getRegimensDlg().getSelected().getRegimen().getContinious().getPhase().getMedications().get(0).getMedicine().getAbbrevName().isEmpty()) {
						view.getRegimensDlg().getSelected().getRegimen().getContinious().getPhase().getMedications().clear();
					}
				}
			}
			for (MedicineUIAdapter m : selectedList) {
				MedicineRegimen medi = createEmptyMedication();
				medi.setMedicine(m.getMedicine());
				boolean isExist = false;
				if (view.getRegimensDlg().isIntensive()) {
					for (MedicationUIAdapter mUI : view.getRegimensDlg().getSelected().getRegimen().getIntensive().getMedications()) {
						if (m.getAbbrevName().equals(mUI.getMedicine().getAbbrevName()) && m.getName().equals(mUI.getMedicine().getName())) {
							isExist = true;
							break;
						}
					}
					if (!isExist) {
						view.getRegimensDlg().getSelected().getRegimen().getIntensive().getPhase().getMedications().add(medi);
						// new UI phase definition must replace the old one
						view.getRegimensDlg().getSelected().getRegimen().setIntensive(view.getRegimensDlg().getSelected().getRegimen().getIntensive());
					}
				} else {
					for (MedicationUIAdapter mUI : view.getRegimensDlg().getSelected().getRegimen().getContinious().getMedications()) {
						if (m.getAbbrevName().equals(mUI.getMedicine().getAbbrevName()) && m.getName().equals(mUI.getMedicine().getName())) {
							isExist = true;
							break;
						}
					}
					if (!isExist) {
						view.getRegimensDlg().getSelected().getRegimen().getContinious().getPhase().getMedications().add(medi);
						// new UI phase definition must replace the old one
						view.getRegimensDlg().getSelected().getRegimen().setContinious(view.getRegimensDlg().getSelected().getRegimen().getContinious());
					}
				}
			}
		}

	}

	/**
	 * Select regimens for forecasting document
	 * 
	 * @param forecastingDocumentPanel .getForecast() selected regimens
	 */
	@SuppressWarnings("unchecked")
	public static void selectRegimens(ForecastingDocumentPanel forecastingDocumentPanel) {
		Set<ForecastingRegimenUIAdapter> totalSet = new HashSet<ForecastingRegimenUIAdapter>();
		view.setActiveForecastingPanel(forecastingDocumentPanel);
		try {
			if (view.getActiveForecastingPanel().getForecast() != null && view.getActiveForecastingPanel().getForecast().getRegimes() != null) {
				totalSet.addAll(view.getActiveForecastingPanel().getForecast().getRegimes());
				for (Iterator<ForecastingRegimenUIAdapter> iterator = totalSet.iterator(); iterator.hasNext();) {
					ForecastingRegimenUIAdapter forecastingRegimenUIAdapter = iterator.next();
					forecastingRegimenUIAdapter.setChecked(true);
				}
			}
			modelFactory.getRegimensDicUIAdapter().setFilter(view.getActiveForecastingPanel().getForecast().getRegimensType());
			List<RegimenUIAdapter> regimensDic = modelFactory.getRegimensDicUIAdapter().getRegimens();
			for (RegimenUIAdapter ra : regimensDic) {
				ForecastingRegimen fcRegimenObj = new ForecastingRegimen();
				fcRegimenObj.setRegimen(ra.getRegimen());
				fcRegimenObj.setPercentNewCases(0);
				ForecastingRegimenUIAdapter fra = new ForecastingRegimenUIAdapter(fcRegimenObj);
				totalSet.add(fra);
			}
			List<ForecastingRegimenUIAdapter> regimensList = new ArrayList<ForecastingRegimenUIAdapter>(totalSet);
			Collections.sort(regimensList);
			view.showRegimenSelectDlg(new ForecastingRegimensUIAdapter(regimensList));
		} catch (FileNotFoundException e) {
			if(!Presenter.showWarningString(getMessage("Error.regimens.file"))){
				saveActiveForecasting(false);
				askStop();
			};
		} catch (JAXBException e) {
			if(!Presenter.showWarningString(getMessage("Error.regimens.xml"))){
				saveActiveForecasting(false);
				askStop();
			};
		}
	}

	/**
	 * Add selected in RegimenSelectDlg forecasting regimens to
	 * forecstingDocument
	 * 
	 * @param selectedList
	 */
	public static void addSelectedRegimensToForecast(List<ForecastingRegimenUIAdapter> selectedList) {
		view.getActiveForecastingPanel().setRegimes(selectedList);
		onReferenceDateChange();
		refreshMedicinesInFc(view.getActiveForecastingPanel().getForecast());
		ForecastUIAdapter fcUi = view.getActiveForecastingPanel().getForecast();
		fcUi.firePropertyChange("medicines", null, fcUi.getMedicines());
	}

	/**
	 * refresh medicines in forecasting, add new one and delete exceed
	 * 
	 * @param forecast
	 */
	public static void refreshMedicinesInFc(ForecastUIAdapter forecast) {
		Set<MedicineUIAdapter> medicinesInReg = new HashSet<MedicineUIAdapter>();
		Set<MedicineUIAdapter> medicinesInFc = new HashSet<MedicineUIAdapter>();
		Set<MedicineUIAdapter> medicinesToAdd = new HashSet<MedicineUIAdapter>();
		Set<MedicineUIAdapter> medicinesToDel = new HashSet<MedicineUIAdapter>();
		// build list of medicines based on the regimens only
		for (ForecastingRegimenUIAdapter r : forecast.getRegimes()) {
			for (MedicationUIAdapter m : r.getRegimen().getIntensive().getMedications()) {
				medicinesInReg.add(m.getMedicine());
			}
			for (MedicationUIAdapter m : r.getRegimen().getContinious().getMedications()) {
				medicinesInReg.add(m.getMedicine());
			}
			for(PhaseUIAdapter ph : r.getRegimen().getAddPhases()){
				for(MedicationUIAdapter m : ph.getMedications()){
					medicinesInReg.add(m.getMedicine());
				}
			}
		}
		// get current list of medicines
		for (ForecastingMedicineUIAdapter fm : forecast.getMedicines()) {
			medicinesInFc.add(fm.getMedicine());
		}
		// add new medicines
		for (MedicineUIAdapter m : medicinesInReg) {
			if (!medicinesInFc.contains(m)) {
				medicinesToAdd.add(m);
			}
		}
		// search for excess medicines
		for (MedicineUIAdapter m : medicinesInFc) {
			if (!medicinesInReg.contains(m)) {
				medicinesToDel.add(m);
			}
		}
		//reformat medicines in forecasting list
		List<ForecastingMedicineUIAdapter> tmp = new ArrayList<ForecastingMedicineUIAdapter>();
		//delete
		for (ForecastingMedicine fm : forecast.getForecastObj().getMedicines()) {
			ForecastingMedicineUIAdapter fmu = new ForecastingMedicineUIAdapter(fm);
			if (!medicinesToDel.contains(fmu.getMedicine())) {
				tmp.add(fmu);
			}
		}
		//add
		for (MedicineUIAdapter m : medicinesToAdd) {
			ForecastingMedicine fm = modelFactory.createForecastingMedicine(m.getMedicine());
			ForecastingMedicineUIAdapter fmu = new ForecastingMedicineUIAdapter(fm);
			tmp.add(fmu);
		}
		forecast.setMedicines(tmp);

	}

	/**
	 * Process reference date change event.
	 */
	public static void onReferenceDateChange() {
		view.getActiveForecastingPanel().getForecast().shiftOldCasesRegimens();
		view.getActiveForecastingPanel().getForecast().shiftOldCasesPercents();
		view.getActiveForecastingPanel().getForecast().shiftNewCasesRegimens();
		view.getActiveForecastingPanel().getForecast().shiftNewCasesPercents();
		view.getActiveForecastingPanel().redrawTables();
	}

	/**
	 * Add new batches to expire or stock on order to selected forecasting
	 * medicine
	 * 
	 * @param selectedMedicine selected forecasting medicine
	 * @param selectedBatchType if 0 - then add to stock on order, if 1 - then
	 *        add to batches to expire, -1 - none.
	 */
	public static void addBatchToForecastingMedicine(ForecastingMedicineTmpStore selectedMedicine, int selectedBatchType) {
		if (selectedBatchType == 0 || selectedBatchType == 1) {
			Calendar cal = GregorianCalendar.getInstance();
			Month month = modelFactory.createMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
			ForecastingBatch batch = modelFactory.createForecastingBatch(month);
			batch.setExpired(modelFactory.getXMLCalendar(cal));
			ForecastingOrder forecastingOrder = modelFactory.createForecastingOrder(month, null);
			forecastingOrder.setArrived(modelFactory.getXMLCalendar(cal));
			for (ForecastingMedicineUIAdapter fmui : view.getActiveForecastingPanel().getForecast().getMedicines()) {
				if (fmui.equals(selectedMedicine.getFcMedicine())) {
					if (selectedBatchType == 1) {
						view.showForecastingOrderDlg(new ForecastingOrderUIAdapter(forecastingOrder), view.getActiveForecastingPanel().getForecast().getFirstFCDate(), false);
					} else {
						view.showForecastingBatchDlg(new ForecastingBatchUIAdapter(batch), view.getActiveForecastingPanel().getForecast().getReferenceDate(), false); //20160825 appropriate for batch
					}
					break;
				}
			}
			view.getActiveForecastingPanel().refreshBatchTables();
		} else {
			// 
		}
	}

	/**
	 * Delete selected batches to expire or stock on order in consider of
	 * selectedBatchType.
	 * 
	 * @param selectedMedicine - selected forecasting medicine.
	 * @param selectedBatchType - if 0 - selected stock on order, if 1 -
	 *        selected batches to expire, another - incorect
	 * @param selectedOrder - selected stock on order
	 * @param selectedBatch - selected batches to expire
	 */
	public static void deleteSelectedBatches(ForecastingMedicineTmpStore selectedMedicine, int selectedBatchType, ForecastingOrderTmpStore selectedOrder, ForecastingBatchTmpStore selectedBatch) {
		if (selectedBatchType == 0 || selectedBatchType == 1) {
			for (ForecastingMedicineUIAdapter fmui : view.getActiveForecastingPanel().getForecast().getMedicines()) {
				if (fmui.equals(selectedMedicine.getFcMedicine())) {
					if (askDelete()) {
						if (selectedBatchType == 1) {
							if(selectedOrder.getForecastingOrder() != null){
								fmui.getFcMedicineObj().getOrders().remove(selectedOrder.getForecastingOrder().getForecastingOrderObj());
							}
						} else {
							if(selectedBatch.getForecastingBatch() != null){
								fmui.getFcMedicineObj().getBatchesToExpire().remove(selectedBatch.getForecastingBatch().getForecastingBatchObj());
							}
						}
						view.getActiveForecastingPanel().setVisibleCalculationDetailsTabs(false);
					}
					break;
				}
			}
			view.getActiveForecastingPanel().setMedisinesAndRefresh(new ArrayList<ForecastingMedicineUIAdapter>(view.getActiveForecastingPanel().getForecast().getMedicines()));
			view.getActiveForecastingPanel().refreshBatchTables();
		} else {
			// 
		}
	}

	/**
	 * Days in month strategy factory
	 * 
	 * @return
	 */
	public static IdaysCalculator getDIMStrategy() {
		return new WeeklyFrequency();
	}

	/**
	 * Set active forecasting document
	 * 
	 * @param selected forecasting document panel
	 */
	public static void setActiveForecastingDocument(ForecastingDocumentPanel selected) {
		view.setActiveForecastingPanel(selected);
	}

	/**
	 * Save active forecasting document (selected forecasting document tab) into
	 * xml file.
	 * 
	 * @param isSaveAs does need to resave (change name of working file) active
	 *        document
	 */
	public static void saveActiveForecasting(boolean isSaveAs) {
		if (view.getActiveForecastingPanel() != null) {
			boolean confirm = isSaveAs;
			if (!confirm) {
				confirm = askSave(new ForecastUIAdapter(view.getActiveForecastingPanel().getForecast().getForecastObj()));
			}
			if (confirm) {
				Forecast forecast = view.getActiveForecastingPanel().getForecast().getForecastObj();
				//validate before save
				ForecastingCalculation fc = new ForecastingCalculation(forecast, getFactory());
				fc.validate();
				List<ForecastingError> err = fc.getError();
				if (err.size() > 0) {
					processForecastingError(err);
					view.getActiveForecastingPanel().setVisibleCalculationDetailsTabs(false);
					return;
				} 
				//Does warnings exists?
				List<ForecastingError> war = fc.getWarning();
				if (war.size() > 0) {
					if (!processForecastingWarning(war)) {
						return;
					}
				}
				String path = getDocDefaultPath();
				String fileName = Messages.getString("Forecasting.newForecasting.name");
				File file = view.getActiveForecastingPanel().getWorkingFile();
				if (isSaveAs){
					if (file != null){
						path = file.getParent();
						fileName = file.getName().replace(".qtb", "");
					}else{
						fileName = Messages.getString("Forecasting.newForecasting.name");
					}
				}
				if (file != null && !isSaveAs) {
					path = file.getParent();
				} else {
					String currentDirectory = path;
					file = view.saveForecastingDocument(currentDirectory,fileName);
					if(file==null){
						return;
					}
					//this file may be already opened
					String newTabName = stripExtension(file);
					if (view.getMainWindow().isForecastingOpen(newTabName)){
						showError(file.getName()+" " +Messages.getString("Application.ask.fileSaveAs.alreadyOpened") + " "  + file.getName());
						return;
					}
					String checkName = file.getName();
					if (!checkName.endsWith(ModelFactory.FORECAST_FILE_EXT)){
						checkName = checkName + ModelFactory.FORECAST_FILE_EXT;
					}
					File chkFile = new File(file.getParent(),checkName);
					if(chkFile.exists()){
						if (!askFileRewrite(file)){
							showWarningString(file.getName()+" " +Messages.getString("Application.ask.fileSaveAs.notSaved"));
							return;
						}
					}

					path = file.getParent();
					forecast.setName(file.getName());
				}
				try {
					saveOrderParameters(forecast);
					modelFactory.storeForecast(forecast, path);
					view.getActiveForecastingPanel().setWorkingFile(file);
					registerInHistory(file);
					String newTabName = stripExtension(file);
					view.getMainWindow().setTitleAt(newTabName);
				} catch (FileNotFoundException e) {
					showError(Messages.getString("Error.forecasting.file"));
				} catch (JAXBException e) {
					showError(Messages.getString("Error.forecasting.xml"));
				} catch (IOException e) {
					e.printStackTrace();
					showError(Messages.getString("Error.forecasting.file"));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					showError(Messages.getString("Error.forecasting.file"));
				}
			}
		}
	}

	private static String stripExtension(File file) {
		int index = file.getName().lastIndexOf(ModelFactory.FORECAST_FILE_EXT);
		String newTabName = (index != -1) ? file.getName().substring(0, index) : file.getName();
		return newTabName;
	}


	/**
	 * Save parameters typed by user in forecasting order
	 * 
	 * @param forecast
	 */
	private static void saveOrderParameters(Forecast forecast) {
		ForecastingTotalPanel totPan = view.getActiveForecastingPanel().getTotalPnl();
		if (totPan != null) {
			ForecastingTotal total = view.getActiveForecastingPanel().getTotalPnl().getTotal();
			ForecastUIAdapter fcU = view.getActiveForecastingPanel().getForecast();
			for (ForecastingTotalMedicine mtot : total.getMedItems()) {
				ForecastingMedicineUIAdapter med = fcU.getMedicine(mtot.getMedicine());
				if (med != null){
					med.getPackOrder(modelFactory).setAdjust(mtot.getAdjustIt());
					med.getPackOrder(modelFactory).setAdjustAccel(mtot.getAdjustItAccel());
					med.getPackOrder(modelFactory).setPack(mtot.getPackSize());
					med.getPackOrder(modelFactory).setPackAccel(mtot.getPackSizeAccel());
					med.getPackOrder(modelFactory).setPackPrice(mtot.getPackPrice());
					med.getPackOrder(modelFactory).setPackPriceAccel(mtot.getPackPriceAccel());
				}
			}
		}
	}

	/**
	 * Close active forecasting document panel
	 */
	public static void closeForecastingDocument(ForecastingDocumentPanel panel) {
		int res = view.showCloseForecastingConfirmDlg();
		switch (res) {
		case JOptionPane.YES_OPTION:
			saveActiveForecasting(false);
		case JOptionPane.NO_OPTION:
			view.getMainWindow().closeActiveForecastingTab(panel != null ? panel : view.getActiveForecastingPanel());
		}

	}

	/**
	 * Run about dialog box
	 */
	public static void showAboutDialog() {
		view.showAboutDialog();
	}

	/**
	 * Execute forecasting calculation
	 */
	public static boolean runForecastingCalculation() {
		view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		if (view.getActiveForecastingPanel() != null && view.getActiveForecastingPanel().getForecast() != null) {
			Forecast fObj = view.getActiveForecastingPanel().getForecast().getForecastObj();
			getFactory().cleanUpForecast(fObj);
			ForecastingCalculation calc = prepareForecastingCalculator(view.getActiveForecastingPanel().getForecast().getForecastObj());
			view.getActiveForecastingPanel().setCalculator(calc);
			List<ForecastingError> err = calc.execute();
			if (err.size() > 0) {
				processForecastingError(err);
				view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				view.getActiveForecastingPanel().setVisibleCalculationDetailsTabs(false);
				return false;
			} else {
				//Does warnings exists?
				List<ForecastingError> war = calc.getWarning();
				if (war.size() > 0) {
					if (!processForecastingWarning(war)) {
						view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						return false;
					}
				}
				// calculate resume and 
				List<MedicineResume> res = calc.getResume();
				if (res != null){  //First of all, check resume (Data for the summary tab) for negative numbers (possible overflow)
					//Show calculation details tabs
					view.getActiveForecastingPanel().showCalculationDetailsTabs();
					ForecastUIAdapter fcU = calc.getForecastUI();
					//Data for medicine consumption tab
					List<MedicineConsumption> mCons = calc.getMedicineConsumption();
					OrderCalculator oCalc = new OrderCalculator(calc);
					oCalc.execute();
					view.getActiveForecastingPanel().getSummaryPanel().setData(fcU, res, mCons, oCalc);
					//Data for medicine consumption tab
					view.getActiveForecastingPanel().getMedicineConsumptionPanel().setData(fcU, mCons);

					//Data for cases on treatment tab
					List<ForecastingRegimenUIAdapter> resReg = fcU.getRegimes();
					view.getActiveForecastingPanel().getCasesOnTreatmentPnl().setDataForRegimen(fcU.getFirstFCDate(), resReg, modelFactory);
					view.getActiveForecastingPanel().getCasesOnTreatmentPnl().setDataForMedicine(mCons);
					if (fcU.getRegimensType() == RegimenTypesEnum.SINGLE_DRUG){
						view.getActiveForecastingPanel().getCasesOnTreatmentPnl().disableRegimenTab();
					}
					view.getActiveForecastingPanel().getCasesOnTreatmentPnl().setForecastingDetails(fcU.getDetailsInformationHTML());
					view.getActiveForecastingPanel().getDashBoardPanel().setData(fcU,calc.getMedicineConsumption());
					//prepareOrdersAndGraphs(calc, fcU);
					
					//init total panel
					view.getActiveForecastingPanel().getTotalPnl().setAndBind(oCalc);
					view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					//init graph
					initStockGraph(oCalc);
					
					//Data for details 
					view.getActiveForecastingPanel().getDetailsPnl().setCalc(calc, res);
					
					/*	view.getActiveForecastingPanel().getDetailsPnl().setMedicines(mCons);
					view.getActiveForecastingPanel().getDetailsPnl().setSummaryResume(res);*/
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							if(getActiveForecasting().isNew()){
								saveForecastingPanelWithoutConfirmDialog(getView().getActiveForecastingPanel());
							}
						}
					});
					return true;
				}else{
					view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					view.getActiveForecastingPanel().setVisibleCalculationDetailsTabs(false);
					String mess = Messages.getString("Forecasting.error.overflow")+ "<br>"+
							calc.getError().get(0).getMessage()+".<br>"+
							Messages.getString("Forecasting.error.tryagain");

					showError("<html>" + mess+ "</html>");
					return false;
				}
			}
		} else {
			view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return false;
		}
	}
	/**
	 * Init stock graph
	 * @param oCalc
	 */
	public static void initStockGraph(OrderCalculator oCalc) {
		view.getActiveForecastingPanel().getStockGraphPnl().setData(oCalc);
	}

	/**
	 * Prepare the forecasting calculator, but do not execute it!
	 * @param forecast 
	 * @return
	 */
	public static ForecastingCalculation prepareForecastingCalculator(Forecast forecast) {
		ForecastingCalculation calc = new ForecastingCalculation(forecast, getFactory());
		saveOrderParameters(calc.getForecastUI().getForecastObj());
		return calc;
	}

	/**
	 * Process all forecasting warnings, if user do not want to continue, switch
	 * to appropriate tab and end process, otherwise display next warning
	 * 
	 * @param war list
	 * @return true, if user agree to continue
	 */
	private static boolean processForecastingWarning(List<ForecastingError> war) {
		for (ForecastingError w : war) {
			switchToErrorPlace(w);
			if (!showWarning(w)) {
				return false;
			}
		}
		return true;
	}

	private static boolean showWarning(ForecastingError w) {
		return getView().showCommonWarning(w.getMessage(), Messages.getString("Forecasting.warning.continue"), false);
	}
	/**
	 * Show the warning messsage
	 * @param message
	 * @return true, if user agree
	 */
	public static boolean showWarningString(String message) {
		return getView().showCommonWarning(message, Messages.getString("Forecasting.warning.continue"), false);
	}

	/**
	 * Process forecasting error - switch to tab, if need and possible, then
	 * show error to user
	 * 
	 * @param err
	 */
	private static void processForecastingError(List<ForecastingError> err) {
		String errorToDisplay = "";
		for (ForecastingError s : err) {
			switchToErrorPlace(s);
			errorToDisplay += s.getMessage() + "\r\n";
			break;
		}
		showError(errorToDisplay);
		view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * Focus interface to error or warning place
	 * 
	 * @param s
	 */
	private static void switchToErrorPlace(ForecastingError s) {
		if (s.getPlace().equals(ForecastingError.ENROLLED_CASES)) {
			switchSubTab(0);
		}
		if (s.getPlace().equals(ForecastingError.NEW_CASES)) {
			switchSubTab(1);
		}
		if (s.getPlace().equals(ForecastingError.MEDICINES)) {
			switchSubTab(2);
		}
	}

	/**
	 * Switch to subtab on Forecasting parameters panel
	 * 
	 * @param i subtab number
	 */
	private static void switchSubTab(int i) {
		if (view != null) {
			ForecastingDocumentPanel fdpanel = view.getActiveForecastingPanel();
			if (fdpanel != null) {
				JTabbedPane sub = fdpanel.getSubTabPane();
				if (sub != null) {
					sub.setSelectedIndex(i);
				}
			}
		}
	}

	/**
	 * Calculate forecasting medicine resume
	 * 
	 * @param med selected medicine
	 * @deprecated forever
	 */
	public static void calculateMedicineResume(MedicineUIAdapter med) {
		if(view.getActiveForecastingPanel() != null){
			ForecastUIAdapter forecast = view.getActiveForecastingPanel().getForecast();
			if(forecast != null){
				ForecastingCalculation calc = new ForecastingCalculation(forecast.getForecastObj(), modelFactory);
				view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				List<ForecastingError> err = calc.execute();
				view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				if (err.size() > 0) {
					processForecastingError(err);
				} else {
					view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					List<PeriodResume> res = calc.calcMedicineResume(med);
					view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					view.getActiveForecastingPanel().getDetailsPnl().setData(res, med);
					view.getActiveForecastingPanel().getDetailsPnl().setForecastingDetails(view.getActiveForecastingPanel().getForecast().getDetailsInformationHTML());
				}
			}
		}
		if (view.getActiveForecastingPanel() != null && view.getActiveForecastingPanel().getForecast() != null) {
			view.getActiveForecastingPanel().getForecast();

		}
	}

	/**
	 * Open and edit selected forecasting order
	 */
	public static void openForEditForecastingOrder() {
		if (view.getActiveForecastingPanel() != null && view.getActiveForecastingPanel().getSelectedOrder() != null) {
			ForecastingOrderUIAdapter selected = view.getActiveForecastingPanel().getSelectedOrder().getForecastingOrder();
			ForecastingOrder forecastingOrder = modelFactory.createForecastingOrder(selected.getArrived(), selected.getBatch().getExpired());
			forecastingOrder.setBatch(selected.getBatch().makeClone(modelFactory).getForecastingBatchObj().getOriginal());
			forecastingOrder.getBatch().setComment(selected.getBatch().getComment());
			view.showForecastingOrderDlg(new ForecastingOrderUIAdapter(forecastingOrder), view.getActiveForecastingPanel().getForecast().getFirstFCDate(), true);
		}
	}

	/**
	 * Edit selected forecasting order
	 * 
	 * @param order new forecasting order
	 */
	public static void editSelectedOrder(ForecastingOrderUIAdapter order) {
		if (view.getActiveForecastingPanel() != null && view.getActiveForecastingPanel().getSelectedOrder() != null) {
			ForecastingOrder selectedTrue = view.getActiveForecastingPanel().getSelectedOrder().getForecastingOrder().getForecastingOrderObj();
			selectedTrue.setArrived(modelFactory.getXMLCalendar(order.getArrived()));
			selectedTrue.setBatch(order.getBatch().getForecastingBatchObj().getOriginal());
			view.getActiveForecastingPanel().refreshBatchTables();
		}
	}

	/**
	 * Create new forecasting order
	 * 
	 * @param order
	 */
	public static void createOrderAndAddToExists(ForecastingOrderUIAdapter order) {
		view.getActiveForecastingPanel().getSelectedMedicine().getFcMedicine().getFcMedicineObj().getOrders().add(order.getForecastingOrderObj());
		view.getActiveForecastingPanel().refreshBatchTables();
	}



	/**
	 * Export to excel active forecasting calculation
	 * Should be already calculated
	 */
	public static void exportToExcel() {
		ForecastUIAdapter fcU = getActiveForecasting();
		ForecastingCalculation calc = view.getActiveForecastingPanel().getCalculator();

		if(fcU != null && calc != null && !fcU.getDirty()){
			String currentDirectory = getDocDefaultPath();
			JComboBox<String> typeBox = view.createExportTypeBox();
			File file = view.exportForecastingCalculation(currentDirectory, typeBox);

			String path = "";
			String filePath = null;
			ExportXLSX report = null;
			if (file != null) {
				path = file.getParent();
				saveCurrentPath(path);
				// try to export excel
				filePath = path + "/" + file.getName();
				if (!filePath.endsWith(".xlsx")) {
					filePath = filePath + ".xlsx";
				}
				if (checkAskFileExist(filePath)){
					report = new ExportXLSX(new File(filePath));
				}else{
					return;
				}
			} else {
				return;
			}
			view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			saveOrderParameters(fcU.getForecastObj());
			List<MedicineResume> res = calc.getMedicineSummary();//calc.getResume(); 20170608
			List<MedicineConsumption> cons = calc.getMedicineConsumption();
			String detailsInformation = fcU.getDetailsInformationTxt();
			try {
				report.createGeneral(fcU);
				DeliveryOrdersControl control = view.getActiveForecastingPanel().getTotalPnl().getDeliveries();
				report.createSummary(res, cons, fcU, view.getActiveForecastingPanel().getTotalPnl().getOrderCalculator());
				report.createConsumption(cons, detailsInformation);
				if (fcU.getRegimensType() == RegimenTypesEnum.MULTI_DRUG){
					report.createRegimensOnTreatment(fcU.getRegimes(), fcU.getFirstFCDate(), modelFactory, detailsInformation);
				}
				report.createMedicinesOnTreatment(cons, modelFactory, detailsInformation);
				ForecastingTotal total = view.getActiveForecastingPanel().getTotalPnl().getTotal();
				ForecastingTotal totalR = view.getActiveForecastingPanel().getTotalPnl().getTotalR();
				ForecastingTotal totalA = view.getActiveForecastingPanel().getTotalPnl().getTotalA();
				report.createOrder(total, totalR, totalA);
				report.createSchedule(control);

				if(view.isFullType(typeBox)){
					for (ForecastingMedicineUIAdapter med : fcU.getMedicines()) {
						List<PeriodResume> pr = calc.calcMedicineResume(med.getMedicine());
						report.createMedicinesResume(med.getMedicine(), pr, detailsInformation);
					}
				}
				
				report.save();
				view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				setLocale(Messages.getLanguage(), Messages.getCountry());
				int answer = JOptionPane.showConfirmDialog(view.getMainWindow(), Messages.getString("Application.exportExcel.askOpen"), Messages.getString("Application.exportExcel.title"), JOptionPane.YES_NO_OPTION);
				if (answer == JOptionPane.YES_OPTION) {
					File fileToOpen = new File(filePath);
					Desktop.getDesktop().open(fileToOpen);
				}
			} catch (IOException e) {
				view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				showError(file.getName()+" " +Messages.getString("Application.ask.fileSaveAs.alreadyOpened") + " "  + file.getName());
			} 

		}
		/*
		if (fcU != null) {
			String currentDirectory = getDocDefaultPath();
			JComboBox<String> typeBox = view.createExportTypeBox();
			File file = view.exportForecastingCalculation(currentDirectory, typeBox);

			String path = "";
			String filePath = null;
			ExportXLSX report = null;
			if (file != null) {
				path = file.getParent();
				saveCurrentPath(path);
				// try to export excel
				filePath = path + "/" + file.getName();
				if (!filePath.endsWith(".xlsx")) {
					filePath = filePath + ".xlsx";
				}
				if (checkAskFileExist(filePath)){
					report = new ExportXLSX(new File(filePath));
				}else{
					return;
				}
			} else {
				return;
			}
			view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			saveOrderParameters(fcU.getForecastObj());
			if (runForecastingCalculation()) {
				view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				ForecastingCalculation calc = new ForecastingCalculation(fcU.getForecastObj(), modelFactory);
				view.getActiveForecastingPanel().showCalculationDetailsTabs();

				List<MedicineResume> res = calc.getResume();
				List<MedicineConsumption> cons = calc.getMedicineConsumption();
				//fill or redraw tabs
				//Data for summary tab
				//Data for medicine consumption tab
				//List<MedicineConsumption> mCons = calc.getMedicineConsumption();
				view.getActiveForecastingPanel().getSummaryPanel().setData(fcU, res, cons);
				view.getActiveForecastingPanel().getMedicineConsumptionPanel().setData(fcU, cons);
				//Data for details tab
				view.getActiveForecastingPanel().getDetailsPnl().setMedicines(cons);
				//Data for cases on treatment tab
				List<ForecastingRegimenUIAdapter> resReg = fcU.getRegimes();
				view.getActiveForecastingPanel().getCasesOnTreatmentPnl().setDataForRegimen(fcU.getReferenceDate(), resReg, modelFactory);
				view.getActiveForecastingPanel().getCasesOnTreatmentPnl().setDataForMedicine(cons);
				view.getActiveForecastingPanel().getDashBoardPanel().setData(fcU, cons);
				String detailsInformation = fcU.getDetailsInformationTxt();
				try {
					report.createGeneral(fcU);
					report.createSummary(res, cons, fcU);
					report.createConsumption(cons, detailsInformation);
					if (fcU.getRegimensType() == RegimenTypesEnum.MULTI_DRUG){
						report.createRegimensOnTreatment(fcU.getRegimes(), fcU.getReferenceDate(), modelFactory, detailsInformation);
					}
					report.createMedicinesOnTreatment(cons, modelFactory, detailsInformation);
					ForecastingTotal total = view.getActiveForecastingPanel().getTotalPnl().getTotal();
					ForecastingTotal totalR = view.getActiveForecastingPanel().getTotalPnl().getTotalR();
					ForecastingTotal totalA = view.getActiveForecastingPanel().getTotalPnl().getTotalA();
					report.createOrder(total, totalR, totalA);
					OrderCalculator oCalc = new OrderCalculator(calc);
					oCalc.execute();
					if(view.isFullType(typeBox)){
						for (ForecastingMedicineUIAdapter med : fcU.getMedicines()) {
							List<PeriodResume> pr = calc.calcMedicineResume(med.getMedicine());
							report.createMedicinesResume(med.getMedicine(), pr, detailsInformation);
						}
					}

					report.save();
					view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					setLocale(Messages.getLanguage(), Messages.getCountry());
					int answer = JOptionPane.showConfirmDialog(view.getMainWindow(), Messages.getString("Application.exportExcel.askOpen"), Messages.getString("Application.exportExcel.title"), JOptionPane.YES_NO_OPTION);
					if (answer == JOptionPane.YES_OPTION) {
						File fileToOpen = new File(filePath);
						Desktop.getDesktop().open(fileToOpen);
					}
				} catch (IOException e) {
					view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					showError(file.getName()+" " +Messages.getString("Application.ask.fileSaveAs.alreadyOpened") + " "  + file.getName());
				} 

			}
		}*/
	}
	/**
	 * Does file exist, if so ask for rewrite
	 * @param filePath
	 * @return true if either file not exist or file exist, but user allow rewrite
	 */
	private static boolean checkAskFileExist(String filePath) {
		File f = new File(filePath);
		if(f.exists() && !f.isDirectory()){
			if (askFileRewrite(f)){
				return true;
			}else{
				return false;
			}
		}else{
			return true;
		}
	}

	/**
	 * Open and edit selected forecasting batch
	 */
	public static void openForEditForecastingButch() {
		if (view.getActiveForecastingPanel() != null && view.getActiveForecastingPanel().getSelectedOrder() != null
				&& view.getActiveForecastingPanel().getSelectedBatch()!=null) {
			ForecastingBatchUIAdapter selected = view.getActiveForecastingPanel().getSelectedBatch().getForecastingBatch();
			if(selected != null){
				ForecastingBatch forecastingBatch = modelFactory.createForecastingBatch(selected.getExpiryDate().getMonthObj());
				forecastingBatch.setQuantity(selected.getQuantity());
				forecastingBatch.setExpired(modelFactory.getXMLCalendar(selected.getExpired()));
				forecastingBatch.setComment(selected.getComment());
				view.showForecastingBatchDlg(new ForecastingBatchUIAdapter(forecastingBatch), view.getActiveForecastingPanel().getForecast().getReferenceDate(), true); //20160825 appropriate for batch
			}
		}
	}

	/**
	 * Edit selected forecasting batch
	 * 
	 * @param batch new forecasting batch
	 */
	public static void editSelectedBatch(ForecastingBatchUIAdapter batch) {
		if (view.getActiveForecastingPanel() != null && view.getActiveForecastingPanel().getSelectedOrder() != null
				&& view.getActiveForecastingPanel().getSelectedBatch()!=null) {
			ForecastingBatch selectedTrue = view.getActiveForecastingPanel().getSelectedBatch().getForecastingBatch().getForecastingBatchObj().getOriginal();
			if(selectedTrue != null){
				selectedTrue.setExpired(modelFactory.getXMLCalendar(batch.getExpired()));
				selectedTrue.setQuantity(batch.getQuantity());
				selectedTrue.setComment(batch.getComment());
				view.getActiveForecastingPanel().refreshBatchTables();
			}
		}
	}

	/**
	 * Create new batch
	 * 
	 * @param batch
	 */
	public static void createBatchAndAddToExists(ForecastingBatchUIAdapter batch) {
		view.getActiveForecastingPanel().getSelectedMedicine().getFcMedicine().getFcMedicineObj().getBatchesToExpire().add(batch.getForecastingBatchObj().getOriginal());
		view.getActiveForecastingPanel().refreshBatchTables();
	}

	/**
	 * Get current active forecasting returns null if no
	 */
	public static ForecastUIAdapter getActiveForecasting() {
		ForecastingDocumentPanel fpanel = view.getActiveForecastingPanel();
		if (fpanel != null) {
			return fpanel.getForecast();
		} else {
			return null;
		}

	}

	/**
	 * Does this month belongs to buffer period buffer period must begin at next
	 * day after Review (sorry Forecasting) period
	 * 
	 * @param adapter
	 * @return
	 */
	public static boolean monthInBuffer(MonthUIAdapter adapter) {
		ForecastUIAdapter fcU = getActiveForecasting();
		return monthInBuffer(adapter, fcU);
	}

	/**
	 * Doues this month belongs to buffer period for forecasting given
	 * 
	 * @param adapter
	 * @param fcU
	 * @return
	 */
	public static boolean monthInBuffer(MonthUIAdapter adapter, ForecastUIAdapter fcU) {
		return false; //20161219 AK
/*		if (fcU != null) {
			Calendar cal = fcU.getRealReviewEnd();
			DateUtils.cleanTime(cal);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			MonthUIAdapter bMonth = new MonthUIAdapter(modelFactory.createMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)));
			return (adapter.compareTo(bMonth) >= 0);
		} else {
			return false;
		}*/
	}

	/**
	 * Does this period completely belongs to buffer period
	 * 
	 * @param adapter
	 * @return
	 */
	public static boolean periodInBuffer(PeriodResume adapter) {
		MonthUIAdapter beg = new MonthUIAdapter(modelFactory.createMonth(adapter.getFrom().get(Calendar.YEAR), adapter.getFrom().get(Calendar.MONTH)));
		MonthUIAdapter end = new MonthUIAdapter(modelFactory.createMonth(adapter.getTo().get(Calendar.YEAR), adapter.getTo().get(Calendar.MONTH)));
		return monthInBuffer(beg) && monthInBuffer(end);
	}

	/**
	 * Change interface language
	 * 
	 * @param language
	 * @param country
	 */
	public static void changeLocale(String language, String country) {
		setLocale(language, country);
		List<File> savedFiles = saveAllForecasts();
		view.getMainWindow().dispose();
		view.reloadMainWindow(savedFiles);
	}

	/**
	 * Reopen existing forecasting files
	 * 
	 * @param files forecastings
	 */
	public static void reopenExistingForecastings(List<File> files) {
		for (File f : files) {
			openForecastingDocument(f);
		}
	}

	/**
	 * Save all currently open forecastings. This method must be started before
	 * locale switch!
	 * 
	 * @return list of currently open forecasing files
	 */
	private static List<File> saveAllForecasts() {
		List<File> savedForecastings = new ArrayList<File>();
		JTabbedPane fcast = view.getMainWindow().getPanelDocuments();
		if (fcast != null) {
			for (int i = 0; i < fcast.getTabCount(); i++) {
				ForecastingDocumentPanel docPane = (ForecastingDocumentPanel) fcast.getComponentAt(i);
				saveForecastingPanelWithoutConfirmDialog(docPane);
				savedForecastings.add(docPane.getWorkingFile());
			}
		}
		return savedForecastings;
	}

	/**
	 * Save forecasting document panel without confirmation dialog. If
	 * forecasting document panel contains new forecasting, show "Save as"
	 * dialog. Another, save without confirmation.
	 * 
	 * @param docPanel forecasting document panel
	 */
	private static void saveForecastingPanelWithoutConfirmDialog(ForecastingDocumentPanel docPanel) {
		Forecast forecast = docPanel.getForecast().getForecastObj();
		File file = docPanel.getWorkingFile();
		String path = null;
		if (file != null) {
			path = file.getParent();
		} else {
			String currentDirectory = getDocDefaultPath();
			file = view.saveForecastingDocument(currentDirectory, Messages.getString("Forecasting.newForecasting.name"));
			if (file != null) {
				path = file.getParent();
				forecast.setName(file.getName());
				String newTabName = stripExtension(file);
				view.getMainWindow().setTitleAt(newTabName);
			} else {
				return;
			}
		}
		try {
			saveOrderParameters(forecast);
			modelFactory.storeForecast(forecast, path);
			String fileName = file.getAbsolutePath();
			if (file.getName().lastIndexOf(ModelFactory.FORECAST_FILE_EXT) == -1) fileName += ModelFactory.FORECAST_FILE_EXT;
			docPanel.setWorkingFile(new File(fileName));
			registerInHistory(file);
		} catch (FileNotFoundException e) {
			showError(Messages.getString("Error.forecasting.file"));
		} catch (JAXBException e) {
			showError(Messages.getString("Error.forecasting.xml"));
		} catch (IOException e) {
			e.printStackTrace();
			showError(Messages.getString("Error.forecasting.file"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			showError(Messages.getString("Error.forecasting.file"));
		}
	}

	/**
	 * Set interface language
	 * 
	 * @param language - supported locale
	 * @param country - country name
	 */
	private static void setLocale(String language, String country) {
		//get locale
		LocaleSaved locSaved = modelFactory.getLocale();
		locSaved.setCountry(country);
		locSaved.setLang(language);
		//set messages bundle
		Messages.setCountry(country);
		Messages.setLanguage(language);
		Locale.setDefault(new Locale(language, country));
		view.setLocale();
		Messages.reloadBundle();
		//save locale
		try {
			modelFactory.storeCurrentLocale();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ask user default locale
	 * 
	 * @return chosen locale
	 */
	public static Locale askLocale() {
		Object selectedValue = view.askLocale();
		Locale locale = new Locale(selectedValue != null ? selectedValue.toString() : "", selectedValue != null ? selectedValue.toString().toUpperCase() : "");
		return locale;
	}

	/**
	 * Get model factory
	 * 
	 * @return
	 */
	public static ModelFactory getFactory() {
		return modelFactory;
	}	

	/**
	 * For testing only
	 * 
	 * @param factory
	 */
	public static void setFactory(ModelFactory factory) {
		modelFactory = factory;

	}

	/**
	 * Add new forecast order item (total)
	 * @param forecastingTotal 
	 */
	public static void addNewOrderItem(ForecastingTotal forecastingTotal) {
		String s = (String) JOptionPane.showInputDialog(view.getMainWindow(), null, Messages.getString("ForecastingDocumentWindow.order.item.title"), JOptionPane.PLAIN_MESSAGE, null, null, null);
		if ((s != null) && (s.length() > 0)) {
			ForecastingTotalItem item = modelFactory.createForecastingTotalItem(s, new BigDecimal(0));
			forecastingTotal.addItem(item);
			forecastingTotal.renewAddItems();
		}
	}

	/**
	 * Edit an order item
	 * 
	 * @param selectedTotalItem order item for edit
	 */
	public static void editOrderItem(ForecastingTotalItemUIAdapter selectedTotalItem) {
		String s = (String) JOptionPane.showInputDialog(view.getMainWindow(), null, Messages.getString("ForecastingDocumentWindow.order.item.title"), JOptionPane.PLAIN_MESSAGE, null, null, selectedTotalItem.getItem());
		if ((s != null) && (s.length() > 0)) {
			selectedTotalItem.setItem(s);
		}
	}

	/**
	 * Select medicines for regimen dialog new style!!!
	 * 
	 * @param actioner
	 */
	public static void selectMedicine(IMultiMedSelection actioner) {
		try {
			MedicinesDicUIAdapter mdicUI = new MedicinesDicUIAdapter(modelFactory.getMedicineDic());
			view.showMedicineSelectWeekDlg(mdicUI, actioner);
		} catch (FileNotFoundException e) {
			view.showError(getMessage("Error.medicines.file"));
		} catch (JAXBException e) {
			view.showError(getMessage("Error.medicines.xml"));
		}

	}

	/**
	 * Create new default medication instance
	 * 
	 * @param duration regimen duration in weeks
	 * @param medUi medicine
	 * @return
	 */
	public static MedicineRegimen getMedicationInstance(Integer duration, MedicineUIAdapter medUi) {
		MedicineRegimen ret = modelFactory.createMedication(medUi.getMedicine(), 0, 0, 0);
		ret.setStartWeek(1);
		ret.setEndWeek(duration);
		return ret;
	}

	/**
	 * Delete the medication from regimen
	 * 
	 * @param regimen regimen
	 * @param toDelete medication
	 */
	public static void deleteMedication(RegimenUIAdapter regimen, MedicationUIAdapter toDelete) {
		regimen.getRegimen().getMedications().remove(toDelete.getMedication());

	}

	/**
	 * Refresh the medicines list dialog
	 * 
	 * @param medicine medicine to select
	 */
	public static void refreshMedicinesList(MedicineUIAdapter medicine) {
		view.getMedicinesDlg().dispose();
		showMedicineListDialog(medicine);

	}

	/**
	 * Propose to open last 5 files
	 */
	public static void last5FilesPropose() {
		ForecastLast5UI fl5Ui = new ForecastLast5UI(modelFactory.getForecastLast5());
		view.showLast5Dlg(fl5Ui);
	}

	/**
	 * Is last 5 docs menu enabled
	 * 
	 * @return
	 */
	public static boolean isLast5Enabled() {
		return modelFactory.getForecastLast5().getForecastFile().size() > 0;
	}

	/**
	 * @return the view
	 */
	public static ViewFactory getView() {
		return view;
	}
	/**
	 * Does this regimen unique or will be unique
	 * @param _selected
	 * @param isEdited
	 * @return
	 */
	public static boolean checkRegimen(RegimenTmpStore _selected,
			boolean isEdited) {
		List<RegimenUIAdapter> rUis = getRegimensListForCheck();
		if (rUis != null){
			// calculate quantity
			int quantity = 0;
			for(RegimenUIAdapter rUi: rUis){
				if (rUi.equals(_selected.getRegimen())){
					quantity ++;
				}
			}
			if (isEdited){
				return quantity <= 1;
			}else{
				return quantity == 0;
			}
		}else{
			return false;
		}
	}
	/**
	 * Does this medicine unique or will be unique
	 * @param medicine
	 * @param isEdit
	 * @return
	 */
	public static boolean checkMedicine(MedicineUIAdapter medicine,
			boolean isEdit) {
		List<MedicineUIAdapter> medicineDic = null;
		try {
			medicineDic = modelFactory.getMedicinesDicUIAdapter().getMedicinesDic();
		} catch (FileNotFoundException e) {
			view.showError(getMessage("Error.medicines.file"));
			return false;
		} catch (JAXBException e) {
			view.showError(getMessage("Error.medicines.xml"));
			return false;
		}
		if (medicineDic != null){
			int index = medicineDic.indexOf(medicine);
			if (index <0){
				return true;  //no such medicine
			}else{ //compare do not take to mind the classifier!
				MedicineUIAdapter oldMed = medicineDic.get(index);
				return oldMed.getClassifier().compareTo(medicine.getClassifier()) != 0;
			}
			
		}else{
			return false;
		}
	}
	/**
	 * Check does regimen with same name already exist
	 * @param _selected
	 * @param isEdited
	 * @return
	 */
	public static boolean checkRegimenName(RegimenTmpStore _selected,
			boolean isEdited) {
		boolean res = false;
		List<RegimenUIAdapter> rUis = getRegimensListForCheck();
		if (rUis != null){
			// calculate quantity
			int quantity = 0;
			for(RegimenUIAdapter rUi: rUis){
				if (rUi.getNameWithForDisplay().equalsIgnoreCase(_selected.getRegimen().getNameWithForDisplay())){
					quantity ++;
				}
			}
			if (isEdited){
				res = quantity <= 1;
			}else{
				res = quantity == 0;
			}
			if (!res){
				return showWarningString(Messages.getString("Warning.regimen.sameName"));
			}else{
				return true;
			}
		}else{
			return false;
		}
	}

	public static boolean checkRegimenMedications(RegimenTmpStore _selected,
			boolean isEdited) {
		boolean res = false;
		List<RegimenUIAdapter> rUis = getRegimensListForCheck();
		if (rUis != null){
			// calculate quantity
			int quantity = 0;
			for(RegimenUIAdapter rUi: rUis){
				if (rUi.getCompositions().equalsIgnoreCase(_selected.getRegimen().getCompositions())){
					quantity ++;
				}
			}
			if (isEdited){
				res = quantity <= 1;
			}else{
				res = quantity == 0;
			}
			if (!res){
				return showWarningString(Messages.getString("Warning.regimen.sameMed"));
			}else{
				return true;
			}
		}else{
			return false;
		}
	}


	/**
	 * Get regimens list only for check purpose
	 * @return regimens list or null if something went wrong
	 */
	private static List<RegimenUIAdapter> getRegimensListForCheck() {
		List<RegimenUIAdapter> rUis = null;
		try {
			rUis = modelFactory.getRegimensDicUIAdapter().getSavedRegimens();
		} catch (FileNotFoundException e) {
			view.showError(getMessage("Error.regimens.file"));
			return null;
		} catch (JAXBException e) {
			view.showError(getMessage("Error.regimens.xml"));
			return null;
		}
		return rUis;
	}
	/**
	 * Try to paste enrolled cases from the clipboard - Quantity mode
	 * Assume, data already copied from the excel sheet before this operation
	 * @param forecast forecast
	 * @param casesOnTreatmentTable table with selected area
	 * @return 
	 */
	public static boolean pasteEnrolled(ForecastUIAdapter forecast, JTable casesOnTreatmentTable) {
		// get the clipboard
		Integer[][] data = ClipBoard.getQuantities();
		if (data != null){
			// determine selected
			int[] cols = casesOnTreatmentTable.getSelectedColumns();
			int[] rows = casesOnTreatmentTable.getSelectedRows();
			ForecastingRegimensTableModel model = (ForecastingRegimensTableModel) casesOnTreatmentTable.getModel();
			if (model.isRowsEditable(rows)){
				getView().getActiveForecastingPanel().setVisibleCalculationDetailsTabs(false);
				// determine the branch or an error
				int branch = getPasteBranch(data, cols, rows, casesOnTreatmentTable.getRowCount(), casesOnTreatmentTable.getColumnCount());
				int casesIndx = 0;
				switch(branch){
				case 1:
					casesIndx = getCaseIndex(forecast, casesOnTreatmentTable, 0, 0);
					if (casesIndx >=0){
						forecast.getRegimes().get(rows[0]).getCasesOnTreatment().get(casesIndx).setIQuantity(data[0][0]);
						return true;
					}else{
						return false;
					}
				case 2:
					for(int i=0; i<rows.length; i++){
						for(int j=0; j<cols.length;j++){
							casesIndx = getCaseIndex(forecast, casesOnTreatmentTable, i, j);
							if (casesIndx >=0){
								forecast.getRegimes().get(rows[i]).getCasesOnTreatment().get(casesIndx).setIQuantity(data[0][0]);
							}
						}
					}
					return true;
				case 3:
					for(int i=0; i<data.length; i++){
						for(int j=0; j<data[i].length;j++){
							casesIndx = getCaseIndex(forecast, casesOnTreatmentTable, i, j);
							if (casesIndx >=0){
								forecast.getRegimes().get(rows[0]+i).getCasesOnTreatment().get(casesIndx).setIQuantity(data[i][j]);
							}
						}
					}
					return true;
				case 4:
					for(int i=0; i<rows.length; i++){
						for(int j=0; j<cols.length;j++){
							casesIndx = getCaseIndex(forecast, casesOnTreatmentTable, i, j);
							if (casesIndx >=0){
								forecast.getRegimes().get(rows[i]).getCasesOnTreatment().get(casesIndx).setIQuantity(data[i][j]);
							}
						}
					}

					return true;
				}
				return false;
			}else{
				showError(Messages.getString("Error.forecasting.paste.noteditablerows"));
				return false;
			}
		}else{
			showError(Messages.getString("Error.forecasting.paste"));
			return false;
		}

	}

	/**
	 * It's possible different duration for regimen in a forecasting.
	 * But the cases on treatment table always as wide as the longest regimen
	 * So, column index not always equial case index. Common adjustment is need.
	 * @param forecast forecasting
	 * @param casesOnTreatmentTable cases on treatment table
	 * @param rowIndex current row number (is index)
	 * @param colIndex current column number (not index!!!)
	 * @return index of the cases quantity for regimen or -1 in case of violation
	 */
	private static int getCaseIndex(ForecastUIAdapter forecast, JTable casesOnTreatmentTable, int rowIndex, int colIndex){
		int[] cols = casesOnTreatmentTable.getSelectedColumns();
		int[] rows = casesOnTreatmentTable.getSelectedRows();
		int casesSize = forecast.getRegimes().get(rows[0]+rowIndex).getCasesOnTreatment().size();
		int casesIndx = cols[0] - (casesOnTreatmentTable.getColumnCount()-casesSize)  +colIndex;
		if (casesIndx >=0 && casesIndx<casesSize){
			return casesIndx;
		}else{
			return -1;
		}
	}

	/**
	 * Determine paste algorithm, or show error if data is inappropriate
	 * @param data data in rows and columns from the clipboard, validated before
	 * @param cols array of cols number selected
	 * @param rows array of rows number selected
	 * @param colCount - count of columns
	 * @param rowCount - count of rows
	 * @return 0 if error or branch number otherwise
	 */
	private static int getPasteBranch(Integer[][] data, int[] cols, int[] rows, int rowCount, int colCount) {
		if(isClipboardSimple(data) && isSelectionSimple(rows, cols)){
			return 1;
		}
		if(isClipboardSimple(data) && isSelectionRange(rows, cols)){
			return 2;
		}
		if(isClipboardRange(data) && isSelectionSimple(rows, cols)){
			//check does data fit the rest of table from cols[0] & rows[0]
			if (data.length<= rowCount - rows[0] ){
				if(data[0].length <= colCount-cols[0]){
					return 3;
				}else{
					showError(Messages.getString("Error.forecasting.paste.toomanycolumns") +" " + (colCount-cols[0]) + ". "+
							Messages.getString("Error.forecasting.paste.recopy"));
				}
			}else{
				showError(Messages.getString("Error.forecasting.paste.toomanyrows") +" " + (rowCount - rows[0]) + ". "+
						Messages.getString("Error.forecasting.paste.recopy"));
			}
		}
		if (isClipboardRange(data) && isSelectionRange(rows, cols)){
			//range is exact selection - additional check!
			if (data.length == rows.length && data[0].length == cols.length){
				return 4;
			}else{
				showError(Messages.getString("Error.forecasting.paste.invalidselection") + " " + rows.length+
						" " + Messages.getString("Error.forecasting.paste.invalidselection.rows") + " and " +
						cols.length +" "+ Messages.getString("Error.forecasting.paste.invalidselection.cols") +". "+
						Messages.getString("Error.forecasting.paste.recopy"));
			}
		}

		return 0;
	}
	/**
	 * is clipboard range - not single number
	 * @param data
	 * @return
	 */
	private static boolean isClipboardRange(Integer[][] data) {
		return data.length>1 || (data.length==1 && data[0].length>1);
	}
	/**
	 * User selected a range
	 * @param rows selection cols
	 * @param cols selection rows
	 * @return
	 */
	private static boolean isSelectionRange(int[] rows, int[] cols) {
		return cols.length>1 || rows.length>1;
	}
	/**
	 * is clipboard single number
	 * @param data
	 * @return
	 */
	public static boolean isClipboardSimple(Integer[][] data) {
		if(data == null){
			return false;
		}
		return data.length == 1 && data[0].length==1;
	}
	/**
	 * Is only a cell selected
	 * @param rows selection rows
	 * @param cols selection cols
	 * @return
	 */
	private static boolean isSelectionSimple(int[] rows, int[] cols){
		return cols.length==1 && rows.length == 1;
	}


	/**
	 * Try to paste expected cases from the clipboard - Quantity mode
	 * Assume, data already copied from the excel sheet before this operation
	 * @param forecast forecasting
	 * @param newCasesTable new cases table
	 * @return
	 */
	public static boolean pasteExpected(ForecastUIAdapter forecast, JTable newCasesTable) {
		Integer[][] data = ClipBoard.getQuantities();
		if (data != null){
			// determine selected
			int[] cols = newCasesTable.getSelectedColumns();
			int[] rows = newCasesTable.getSelectedRows();
			ForecastingRegimensNewCasesModel model = (ForecastingRegimensNewCasesModel) newCasesTable.getModel();
			if (model.isRowsEditable(rows)){
				getView().getActiveForecastingPanel().setVisibleCalculationDetailsTabs(false);
				// determine the branch or an error
				int branch = getPasteBranch(data, cols, rows, newCasesTable.getRowCount(), newCasesTable.getColumnCount());
				switch(branch){
				case 1:
					forecast.getRegimes().get(rows[0]).getNewCases().get(cols[0]).setIQuantity(data[0][0]);
					return true;
				case 2:
					for(int i=0; i<rows.length; i++){
						for(int j=0; j<cols.length;j++){
							forecast.getRegimes().get(rows[i]).getNewCases().get(cols[j]).setIQuantity(data[0][0]);
						}
					}
					return true;
				case 3:
					for(int i=0; i<data.length; i++){
						for(int j=0; j<data[i].length;j++){
							forecast.getRegimes().get(rows[0]+i).getNewCases().get(cols[0]+j).setIQuantity(data[i][j]);
						}
					}
					return true;
				case 4:
					for(int i=0; i<rows.length; i++){
						for(int j=0; j<cols.length;j++){
							forecast.getRegimes().get(rows[i]).getNewCases().get(cols[j]).setIQuantity(data[i][j]);
						}
					}
					return true;
				}
				return false;
			}
			else{
				showError(Messages.getString("Error.forecasting.paste.noteditablerows"));
				return false;
			}
		}else{
			showError(Messages.getString("Error.forecasting.paste"));
			return false;
		}
	}
	/**
	 * Try to paste enrolled cases from the clipboard - Percentage mode
	 * @param forecast
	 * @param numCasesOntreatmentTable 
	 */
	public static boolean enrolledPastePersQty(ForecastUIAdapter forecast, JTable numCasesOntreatmentTable) {
		Integer[][] data = ClipBoard.getQuantities();
		if (data != null){
			// determine selected
			int[] cols = numCasesOntreatmentTable.getSelectedColumns();
			int[] rows = numCasesOntreatmentTable.getSelectedRows();
			// determine the branch or an error
			int branch = getPasteBranch(data, cols, rows, numCasesOntreatmentTable.getRowCount(), numCasesOntreatmentTable.getColumnCount());
			getView().getActiveForecastingPanel().setVisibleCalculationDetailsTabs(false);
			switch(branch){
			case 1:
				forecast.getCasesOnTreatment().get(rows[0]).setIQuantity(data[0][0]);
				return true;
			case 2:
				for(int i=0; i<rows.length; i++){
					forecast.getCasesOnTreatment().get(rows[i]).setIQuantity(data[0][0]);
				}
				return true;
			case 3:
				for(int i=0; i<data.length; i++){
					forecast.getCasesOnTreatment().get(rows[0]+i).setIQuantity(data[i][0]);
				}
				return true;
			case 4:
				for(int i=0; i<rows.length; i++){
					for(int j=0; j<cols.length;j++){
						forecast.getCasesOnTreatment().get(rows[i]).setIQuantity(data[i][0]);
					}
				}
				getView().getActiveForecastingPanel().setVisibleCalculationDetailsTabs(false);
				return true;
			}
			return false;
		}else{
			showError(Messages.getString("Error.forecasting.paste"));
			return false;
		}

	}
	/**
	 * Try to paste expected cases quantity from the clipboard
	 * @param forecast
	 * @param estimatedNumberTable 
	 * @return
	 */
	public static boolean expectedPastePersQty(ForecastUIAdapter forecast, JTable estimatedNumberTable) {
		Integer[][] data = ClipBoard.getQuantities();
		if (data != null){
			// determine selected
			int[] cols = estimatedNumberTable.getSelectedColumns();
			int[] rows = estimatedNumberTable.getSelectedRows();
			// determine the branch or an error
			int branch = getPasteBranch(data, cols, rows, estimatedNumberTable.getRowCount(), estimatedNumberTable.getColumnCount());
			getView().getActiveForecastingPanel().setVisibleCalculationDetailsTabs(false);
			switch(branch){
			case 1:
				forecast.getNewCases().get(rows[0]).setIQuantity(data[0][0]);
				return true;
			case 2:
				for(int i=0; i<rows.length; i++){
					forecast.getNewCases().get(rows[i]).setIQuantity(data[0][0]);
				}
				return true;
			case 3:
				for(int i=0; i<data.length; i++){
					forecast.getNewCases().get(rows[0]+i).setIQuantity(data[i][0]);
				}
				return true;
			case 4:
				for(int i=0; i<rows.length; i++){
					for(int j=0; j<cols.length;j++){
						forecast.getNewCases().get(rows[i]).setIQuantity(data[i][0]);
					}
				}
				return true;
			}
			return false;
		}else{
			showError(Messages.getString("Error.forecasting.paste"));
			return false;
		}


	}

	/**
	 * Export to Excel WHO formatted file
	 */
	public static void exportToExcelWHO() {
		ForecastUIAdapter fcU = getActiveForecasting();
		if (fcU != null) {
			saveOrderParameters(fcU.getForecastObj());
			if (runForecastingCalculation()) {
				ForecastingCalculation calc = new ForecastingCalculation(fcU.getForecastObj(), modelFactory);
				view.getActiveForecastingPanel().showCalculationDetailsTabs();
				String currentDirectory = getDocDefaultPath();
				File file = view.exportForecastingCalculation(currentDirectory, view.createExportTypeBox());
				String path = "";
				if (file != null) {
					path = file.getParent();
				} else {
					return;
				}
				try{
					// try to export excel
					ExportExcelWHO report = new ExportExcelWHO();
					String filePath = path + "/" + file.getName();
					if (!filePath.endsWith(".xls")) {
						filePath = filePath + ".xls";
					}
					report.setOutputFile(filePath);
					report.createReport(calc);
					report.save();
					view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					setLocale(Messages.getLanguage(), Messages.getCountry());
					int answer = JOptionPane.showConfirmDialog(view.getMainWindow(), Messages.getString("Application.exportExcel.askOpen"), Messages.getString("Application.exportExcel.title"), JOptionPane.YES_NO_OPTION);
					if (answer == JOptionPane.YES_OPTION) {
						File fileToOpen = new File(filePath);
						Desktop.getDesktop().open(fileToOpen);
					}
				} catch (IOException e) {
					view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					showError(e.getMessage());
				} catch (RowsExceededException e) {
					view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					showError(e.getMessage());
				} catch (WriteException e) {
					view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					showError(e.getMessage());
				}

			}
		}

	}
	/**
	 * Add medication to the additional phase
	 * It will be never uses for single drug regimen
	 * @param phase additional phase
	 * @param phaseDataTable table to refresh when medication will be added
	 * @param isSingle multi or single medicine dialog box needed
	 */
	public static void selectMedicineForAddPhase(PhaseUIAdapter phase,
			JTable phaseDataTable, boolean isSingle) {
		Medicines mdic;
		try {
			mdic = modelFactory.getMedicineDic();
			MedicinesDicUIAdapter medi = new MedicinesDicUIAdapter(mdic);
			medi.setExeptionList(phase.getMedicines());
			if (isSingle){
				MedicineSelectDlg dlg = view.createMedicineSelectDlg(medi, phase, phaseDataTable);
				dlg.setVisible(true);
			}else{
				MedicineSelectMulDlg dlg = view.createMedicineSelectMulDlg(medi, phase, phaseDataTable);
				dlg.setVisible(true);
			}
		} catch (FileNotFoundException e) {
			if(!Presenter.showWarningString(getMessage("Error.medicines.file"))){
				askStop();
			};
		} catch (JAXBException e) {
			if(!Presenter.showWarningString(getMessage("Error.medicines.xml"))){
				askStop();
			};
		}


	}
	/**
	 * Add medication to the phase
	 * @param selectedList medicines list
	 * @param phase phase to add medications
	 * @param phaseDataTable on screen table for refresh
	 */
	public static void addSelectedMedicineToAddPhase(
			List<MedicineUIAdapter> selectedList, PhaseUIAdapter phase,
			JTable phaseDataTable) {
		for (MedicineUIAdapter m : selectedList) {
			MedicineRegimen medi = createEmptyMedication();
			medi.setMedicine(m.getMedicine());
			phase.getPhase().getMedications().add(medi);
		}
		AbstractTableModel model = (AbstractTableModel) phaseDataTable.getModel();
		model.fireTableDataChanged();
		phaseDataTable.repaint();
	}

	/**
	 * Ask about cancel edit
	 * @return
	 */
	public static boolean askCancel() {
		return view.showCancelConfirmDlg();
	}
	/**
	 * Ask reopen fc or no
	 * @return
	 */
	public static boolean askReopen() {
		return view.showAskFileReopen();
	}
	/**
	 * Create clone of regimen and add it to the dictionary
	 * @param regimen regimen for cloning
	 */
	public static void doubleRegimen(RegimenUIAdapter regimen) {
		if (getActiveForecasting() != null){
			if (!showWarningAdvanced(getMessage("Warning.forecasting.open"), 
					getMessage("DlgMedicines.warning.title"))){
				return;
			}

		}
		RegimenUIAdapter clone = regimen.makeClone();
		saveRegimenDic(new RegimenTmpStore(clone), false);
	}
	/**
	 * Export regimes and, possible, medicines from the active forecating to the dictionaries
	 */
	public static void exportFcToDic() {
		ForecastUIAdapter fcUi = getActiveForecasting();
		String protocol = "<html>";
		// first, add medicines
		List<ForecastingMedicineUIAdapter> fcMuis = fcUi.getMedicines();
		try {
			boolean wasAdded=false;
			MedicinesDicUIAdapter mUiDic = getFactory().getMedicinesDicUIAdapter();
			protocol = protocol + "<br><b>" + Messages.getString("Application.added.medicine") + "</b><ul>";
			for(ForecastingMedicineUIAdapter fcMui : fcMuis){
				if (mUiDic.addIfNotExist(fcMui.getMedicine())){
					protocol = protocol + "<li>" + fcMui.getMedicine().getNameForDisplayWithAbbrev() +
							"("+
							fcMui.getMedicine().getTypeAsString()+")</li>";
					wasAdded=true;
				}
			}
			if(!wasAdded){
				protocol = protocol + "<li>" + Messages.getString("Application.added.not") + "</li>";
			}
			protocol = protocol + "</ul>";
			saveMedicinesDic();
			// second, add regimes
			getFactory().removeRegimenDicUI();
			RegimensDicUIAdapter regUiDic = getFactory().getRegimensDicUIAdapter();
			protocol = protocol + "<br><b>" + Messages.getString("Application.added.regimen") + "</b><ul>";
			List<ForecastingRegimenUIAdapter> fcRegUis = fcUi.getRegimes();
			wasAdded=false;
			for(ForecastingRegimenUIAdapter fcRegUi : fcRegUis){
				if (regUiDic.addIfNotExist(fcRegUi.getRegimen())){
					protocol = protocol + "<li>" + fcRegUi.getRegimen().getNameWithForDisplay() +"</li>";
					wasAdded=true;
				}
			}
			if(!wasAdded){
				protocol = protocol + "<li>" + Messages.getString("Application.added.not") + "</li>";
			}
			saveRegimens(regUiDic.getRegimensObj());
			getFactory().removeRegimenDicUI();
			protocol = protocol + "</ul></html>";
			getView().showInformation(protocol, Messages.getString("MainWindow.menuMedReg.addFromFC").replace("...", ""));
		} catch (FileNotFoundException e) {
			// nothing to do
			e.printStackTrace();
		} catch (JAXBException e) {
			// nothing to do
			e.printStackTrace();
		}


	}
	/**
	 * Import stock of medicines from the Excel
	 */
	public static void importMedStock() {
		File selectedFile = view.getExcelForImport(getDocDefaultPath());
		if (selectedFile != null){
			ImportExcel ie = new ImportExcel(selectedFile);
			String err = ie.importMedStock();
			if(err.length()==0){
				if(!ie.hasStockData()){
					if(!showWarningString(Messages.getString("Application.importExcel.warning.stockonhand"))){
						return;
					}
				}
				if(!ie.hasOrderData()){
					if(!showWarningString(Messages.getString("Application.importExcel.warning.stockinorder"))){
						return;
					}
				}
				MedicinesDecodeDlg dlg = view.createMedDecoderDialog(getActiveForecasting().getMedicines(),ie);
				dlg.setVisible(true);
			}else{
				view.showError(err);
			}
		}
	}
	/**
	 * Set medicines batches and/or orders from the Excel file
	 * @param result medicines batches from the Excel file
	 * @param decoder decoder medicines from Excel - medicines from QuanTB
	 */
	public static void setBatchesFromExcel(ImportExcel result,
			List<MedicinesDecoder> decoder) {
		for(MedicinesDecoder mDec : decoder){
			if (mDec.getMedicineE() != null && mDec.getMedicineE().length()>0){
				List<ImportExcelDTO> dtos = result.getMedicineResult(mDec.getMedicineE());
				//conditionally clean up batches or/and orders if at least one exists in import
				for(ImportExcelDTO dto : dtos ){
					if(dto.hasStock()){
						mDec.getMedicineQ().removeBatches();
					}
					if(dto.hasOrder()){
						mDec.getMedicineQ().removeOrders();
					}
				}
				for(ImportExcelDTO dto : dtos ){
					Integer quantity =  dto.hasStock() ? dto.getQuantity() : dto.getOrderQuantity();			
					Date dt = dto.hasStock() ? dto.getExpDate() : dto.getOrderExpDate();
					Date aDt = dto.getArrive();
					// we should create batch in any case
					Calendar expireDate = null;
					if(dt != null){
						expireDate = GregorianCalendar.getInstance();
						expireDate.setTime(DateUtils.getcleanDate(dt));
					}
					ForecastingBatch batch = modelFactory.createForecastingBatchExact(expireDate);
					batch.setQuantity(quantity);
					if(dto.hasStock()){
						mDec.getMedicineQ().addBatch(batch);
					}else{
						//create an order
						Calendar arrDate = GregorianCalendar.getInstance();
						arrDate.setTime(DateUtils.getcleanDate(dto.getArrive()));
						ForecastingOrder order = modelFactory.createForecastingOrder(arrDate, expireDate);
						order.setBatch(batch);
						ForecastingOrderUIAdapter orderUI = new ForecastingOrderUIAdapter(order);
						String err = orderUI.validate(getActiveForecasting().getFirstFCDate());
						if(err.length() == 0){
							mDec.getMedicineQ().addOrder(order);
						}else{
							showError(mDec.getMedicineQ().getMedicine().getNameForDisplay() + ": " + err); //TODO check it!!
						}
					}
				}
			}
		}
		java.awt.EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				getView().getActiveForecastingPanel().refreshMedicinesTable();

			}
		});

	}


	/**
	 * Get a slice from the current forecasting
	 */
	public static void sliceCurrentFC() {
		ForecastUIAdapter fc = getActiveForecasting();
		boolean goAhead = !fc.getDirty();
		if(!goAhead){
			goAhead = runForecastingCalculation();
		}
		if (goAhead){
			SliceFCUIAdapter sUi = new SliceFCUIAdapter(getActiveForecasting(), view);
			getView().showSliceDialog(sUi);
		}

	}
	/**
	 * Show slice on a tab
	 * @param slice
	 */
	public static void showSlice(SliceFCUIAdapter slice) {
		Presenter.addForecastingTab(slice.prepareSliceForecasting());
	}
	/**
	 * Store current flag to show or not to show the customization help
	 * @param value control visual help appearance
	 */
	public static void storeCustHelp(boolean value) {
		LocaleSaved locSaved = modelFactory.getLocale();
		locSaved.setDoNotShowHelp(value);
		try {
			modelFactory.storeCurrentLocale();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	/**
	 * GEt current country
	 */
	public static String getLocaleCountry() {
		LocaleSaved locSaved = modelFactory.getLocale();
		return locSaved.getCountry();
	}

	/**
	 * save the last user-selected path to the files
	 * @param value path to files
	 */
	public static void saveCurrentPath(String value) {
		LocaleSaved locSaved = modelFactory.getLocale();
		locSaved.setPathToFiles(value);
		try {
			modelFactory.storeCurrentLocale();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Merge opened forecastings
	 */
	public static void mergeAskFC() {
		// get all opened
		List<ForecastUIAdapter> allFC = new ArrayList<ForecastUIAdapter>();
		JTabbedPane fcast = view.getMainWindow().getPanelDocuments();
		if (fcast != null) {
			for (int i = 0; i < fcast.getTabCount(); i++) {
				ForecastingDocumentPanel docPane = (ForecastingDocumentPanel) fcast.getComponentAt(i);
				ForecastUIAdapter forecast = docPane.getForecast();
				allFC.add(forecast);
			}
		}
		// allow the user to select
		getView().showSelectMergeDialog(allFC);
	}
	/**
	 * Merge selected forecastings
	 * Do nothing if at least one fc  has an error
	 * @param fcList all forecastings
	 */
	public static void mergeChecked(List<ForecastUIAdapter> fcList) {
		List<ForecastUIAdapter> toMerge = new ArrayList<ForecastUIAdapter>();
		for(ForecastUIAdapter fcUi :fcList){
			if(fcUi.getChecked()){
				ForecastingCalculation calc = prepareForecastingCalculator(fcUi.getForecastObj());
				calc.validate();
				List<ForecastingError> errors = calc.getError();
				if (errors.isEmpty()){
					toMerge.add(fcUi);
				}else{
					String errorToDisplay=fcUi.getName() + "\r\n";
					for (ForecastingError s : errors) {
						errorToDisplay += s.getMessage() + "\r\n";
						break;
					}
					showError(errorToDisplay);
					return;
				}
			}
		}

		ForecastMergeControl control = new ForecastMergeControl(toMerge, getView());
		if (control.validate()){
			ForecastUIAdapter merged = control.merge();
			if (merged != null){
				Presenter.addForecastingTab(merged);
			}
		}
	}
	/**
	 * Determine help visibility from the locale
	 * @return
	 */
	public static boolean isHelpVisible() {
		return !getFactory().getLocale().isDoNotShowHelp();
	}
	/**
	 * Open the User Guide in Adobe Acrobat
	 */
	public static void showUserGuide() {
		String filePath = getXMLPath()+"/"+Messages.getString("MainWindow.file.userguide");
		File fileToOpen = new File(filePath);
		try {
			Desktop.getDesktop().open(fileToOpen);
		} catch (IOException e) {
			showError(Messages.getString("Error.userguide"));
		}
	}
	/**
	 * Show message about wrong version of QTB
	 */
	public static void showWrongVersionDialog() {
		getView().showWrongVersionDialog();

	}
	/**
	 * Show warning with No as default choice
	 * @param message
	 * @return
	 */
	public static boolean showWarningStringStrict(String message) {
		return getView().showCommonWarning(message, Messages.getString("Forecasting.warning.continue"), true);
	}
	/**
	 * For expected cases convert quantities style calculations to percents style
	 * @return
	 */
	public static boolean convertExpectedToPers() {
		ForecastUIAdapter fUi = getActiveForecasting();
		if(fUi != null){
			String s = Messages.getString("Forecasting.warning.casesWillBeAdded");
			if(fUi.hasExpectedPersQuantities()){
				s = Messages.getString("Forecasting.warning.casesWillBeRetriviedPers");
			}
			if(showWarningStringStrict(s)){
				return fUi.expectedToPers();
			}else{
				return false;
			}
		}
		return false;
	}
	/**
	 * For expected cases convert percents style calculations to quantity style
	 * @return
	 */
	public static boolean convertExpectedToQuan() {
		ForecastUIAdapter fUi = getActiveForecasting();
		if(fUi != null){
			String s = Messages.getString("Forecasting.warning.casesWillBeDistributed");
			if(fUi.hasExpectedQuantities()){
				s = Messages.getString("Forecasting.warning.casesWillBeRetrivied");
			}
			if(showWarningStringStrict(s)){
				return fUi.expectedToQuantity();
			}else{
				return false;
			}
		}
		return false;
	}
	/**
	 * For enrolled cases convert quantity style calculations to percentage style
	 * @return
	 */
	public static boolean convertEnrolledToPers() {
		ForecastUIAdapter fUi = getActiveForecasting();
		if(fUi != null){
			String s = Messages.getString("Forecasting.warning.casesWillBeAdded");
			if(fUi.hasEnrolledPersQuantities()){
				s = Messages.getString("Forecasting.warning.casesWillBeRetriviedPers");
			}
			if(showWarningStringStrict(s)){
				return fUi.enrolledToPers();
			}else{
				return false;
			}
		}
		return false;
	}

	/**
	 * For enrolled cases convert percentage style calculation to quantity style
	 * @return
	 */
	public static boolean convertEnrolledToQuan() {
		ForecastUIAdapter fUi = getActiveForecasting();
		if(fUi != null){
			String s = Messages.getString("Forecasting.warning.casesWillBeDistributed");
			if (fUi.hasEnrolledQuantyties()){
				s = Messages.getString("Forecasting.warning.casesWillBeRetrivied");
			}
			if(showWarningStringStrict(s)){
				return fUi.enrolledToQuantity();
			}else{
				return false;
			}
		}
		return false;
	}
	/**
	 * Create Excel template to fill data for import stock omn hand and stock on orders
	 */
	public static void createImportTemplate() {
		view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		TemplateImport template=getFactory().fetchImportTemplate();
		try {
			XSSFWorkbook workbook = template.create(getActiveForecasting());
			String currentDirectory = getDocDefaultPath();
			File file = getView().askImportTemplateFile(currentDirectory);
			String path="";
			String filePath="";
			if(file!=null){
				path = file.getParent();
				saveCurrentPath(path);
				filePath = path + "/" + file.getName();
				if (!filePath.endsWith(".xlsx")) {
					filePath = filePath + ".xlsx";
				}
				if (checkAskFileExist(filePath)){
					try {
						template.saveWorkBook(workbook, filePath);
						setLocale(Messages.getLanguage(), Messages.getCountry());
						int answer = JOptionPane.showConfirmDialog(view.getMainWindow(), Messages.getString("Application.exportExcel.askOpen"), Messages.getString("Application.exportExcel.title"), JOptionPane.YES_NO_OPTION);
						if (answer == JOptionPane.YES_OPTION) {
							File fileToOpen = new File(filePath);
							view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							Desktop.getDesktop().open(fileToOpen);
						}
					} catch (IOException e) {
						view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						showError(file.getName()+" " +Messages.getString("Application.ask.fileSaveAs.alreadyOpened") + " "  + file.getName());
					}
				}else{
					view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					return;
				}
			} else {
				view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				return;
			}
		} catch (Exception e) {
			view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			showError(Messages.getString("Error.template.file"));
		}
	}
	/**
	 * Repaint the stock graph on current consumptions
	 */
	public static void repaintStockGraph() {
		view.getActiveForecastingPanel().getStockGraphPnl().recalculate();
	}
	
	public static void paintDeliveries() {
		view.getActiveForecastingPanel().getTotalPnl().paintDeliveries();

	}
	/**
	 * Recalculate order scenario, then repaint order tab and graph
	 * @param orderCalculator
	 */
	public static void applyOrderScenario(OrderCalculator orderCalculator) {
		view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		orderCalculator.reExecute();
		view.getActiveForecastingPanel().getTotalPnl().setAndBind(null);
		view.getActiveForecastingPanel().getStockGraphPnl().recalculate();
		view.getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	/**
	 * Copy selected cells from JTable
	 * @param table the JTable
	 * @param offset it is possible split this table to fixed columns and scrolled columns it is is offset or fixed columns quantity
	 */
	public static void copyJTableCells(JTable table, int offset) {
		if (table.getSelectedColumnCount() + table.getSelectedRowCount()>0){
			ClipBoard.copy(table,offset);
		}else{
			Presenter.showError(Messages.getString("ForecastingDocumentWindow.tbParameters.excel.selectFirst"));
		}
		
	}
	/**
	 * Clean Stock graph panel
	 */
	public static void cleanStockGraph() {
		StockGraphPanel sg =view.getActiveForecastingPanel().getStockGraphPnl();
		if(sg!=null){
			sg.cleanGraph();
		}
		
	}
	/**
	 * Change total order for medicine given
	 * @param medOrd order for a medicine given
	 */
	public static void changeTotalOrderOnSummary(ForecastingTotalMedicine medOrd) {
		getView().getActiveForecastingPanel().getSummaryPanel().changeOrderTotal(medOrd.getMedicine(), 
				medOrd.getAdjustAccel(), medOrd.getAdjustedRegular(),medOrd.getTotal());
	}



}
