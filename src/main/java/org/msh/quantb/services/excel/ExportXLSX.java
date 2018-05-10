package org.msh.quantb.services.excel;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.msh.quantb.model.mvp.ModelFactory;
import org.msh.quantb.services.calc.ConsumptionMonth;
import org.msh.quantb.services.calc.DateParser;
import org.msh.quantb.services.calc.DateUtils;
import org.msh.quantb.services.calc.DeliveryOrdersControl;
import org.msh.quantb.services.calc.MedicineConsumption;
import org.msh.quantb.services.calc.MedicineResume;
import org.msh.quantb.services.calc.OrderCalculator;
import org.msh.quantb.services.calc.PeriodResume;
import org.msh.quantb.services.io.DeliveryOrderItemUI;
import org.msh.quantb.services.io.DeliveryOrderUI;
import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.ForecastingBatchUIAdapter;
import org.msh.quantb.services.io.ForecastingMedicineUIAdapter;
import org.msh.quantb.services.io.ForecastingOrderUIAdapter;
import org.msh.quantb.services.io.ForecastingRegimenResultUIAdapter;
import org.msh.quantb.services.io.ForecastingRegimenUIAdapter;
import org.msh.quantb.services.io.ForecastingTotal;
import org.msh.quantb.services.io.ForecastingTotalItemUIAdapter;
import org.msh.quantb.services.io.ForecastingTotalMedicine;
import org.msh.quantb.services.io.MedicineUIAdapter;
import org.msh.quantb.services.io.MonthQuantityUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.panel.SummaryDataModelHelper;

/**
 * Import FC result to the Excel XLSX
 * @author Alexey Kurasov
 *
 */
public class ExportXLSX {
	private static final int CONTENT_OFFSET = 2;
	private POIProcessor processor;

	public ExportXLSX(File excelFile) {
		processor = new POIProcessor(excelFile);
	}

	/**
	 * Create the first excel report page - general info
	 * 
	 * @param fcU the forecast
	 */
	public void createGeneral(ForecastUIAdapter fcU){
		processor.createSheet(Presenter.getMessage("ForecastingDocumentWindow.tbParameters.title"), 0);
		processor.setColumnView(0, 30);
		int startRow = 0;
		startRow = generalUserInfo(fcU, startRow);
		startRow = generalForecastInfo(fcU, startRow);
		if (fcU.isEnrolledCasesPercents()) {			
			startRow = generalCases(fcU, startRow, false);
			startRow = generalRegPerc(fcU, startRow, false);
		} else {
			startRow = generalCasesOnTreatment(fcU, startRow, false);
			startRow++;
		}
		if (fcU.isExpectedCasesPercents()) {
			startRow = generalCases(fcU, startRow, true);
			startRow = generalRegPerc(fcU, startRow, true);
		} else {
			startRow = generalCasesOnTreatment(fcU, startRow, true);
			startRow++;
		}		
		//startRow = generalBatches(fcU, startRow);
		//startRow = generalOrders(fcU, startRow);
		processor.createSheet(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.title"), 1);
		createBatchesAndOrders(fcU);

	}
	/**
	 * Create current stock and orders data on separate sheet
	 * Future, user will have possibility to copy these data if needed
	 * @param fcU 
	 */
	private void createBatchesAndOrders(ForecastUIAdapter fcU) {
		XSSFSheet sheet = processor.getCurrentSheet();
		int row=0;
		//paint header with right styles
		processor.addCaption(0, row, Messages.getString("ImportStock.columns.medicine"), 60);
		processor.addCaption(1, row, Messages.getString("ImportStock.columns.quantity"), 0);
		processor.addCaption(2, row, Messages.getString("ImportStock.columns.expiry"), 0);
		processor.addCaption(3, row, Messages.getString("ImportStock.columns.ordquantity"), 0);
		processor.addCaption(4, row, Messages.getString("ImportStock.columns.orddelivery"), 0);
		processor.addCaption(5, row, Messages.getString("ImportStock.columns.ordexpiry"), 0);
		processor.addCaption(6, row, Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.comment"), 40);
		row++;
		List<ForecastingMedicineUIAdapter> medicines = fcU.getMedicines();
		for(ForecastingMedicineUIAdapter medicine : medicines){
			processor.addLabel(0, row, medicine.getMedicine().getNameForDisplayWithAbbrev());
			List<ForecastingBatchUIAdapter> batches = medicine.getBatchesToExpire();
			for(ForecastingBatchUIAdapter batch : batches){
				if(!batch.isExclude()){
					processor.addInteger(1, row, batch.getQuantity());
					processor.addDate(2, row, batch.getExpiredDt());
					processor.addLabel(6, row, batch.getComment());
					//paint borders
					processor.addLabel(3, row, "");
					processor.addLabel(4, row, "");
					processor.addLabel(5, row, "");
					row++;
				}
			}
			List<ForecastingOrderUIAdapter> orders = medicine.getOrders();
			for(ForecastingOrderUIAdapter order : orders){
				if(order.getBatchInclude()){
					processor.addInteger(3, row, order.getBatchQuantity());
					processor.addDate(4, row, order.getArrivedDt());
					Date expired = order.getBatch().getExpiredDt();
					if(expired != null){
						if(expired.getYear()< 3000){
							processor.addDate(5, row, expired);
						}
					}
					processor.addLabel(6, row, order.getBatch().getComment());
					//paint borders
					processor.addLabel(1, row, "");
					processor.addLabel(2, row, "");
					row++;
				}
			}
		}


	}

	private int generalUserInfo(ForecastUIAdapter fcU, int startRow) {
		processor.addSubChapter(0, startRow, Presenter.getMessage("Forecasting.name"));
		processor.addLabel(1, startRow, fcU.getName());
		processor.mergeCells(1, startRow, 3, startRow);
		processor.addSubChapter(4, startRow, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.comment"));
		processor.mergeCells(4, startRow, 5, startRow);
		processor.addLabel(6, startRow, fcU.getComment());
		processor.mergeCells(6, startRow, 13, startRow+4);
		processor.addSubChapter(0, startRow + 1, Presenter.getMessage("DlgForecastingWizard.address.label"));
		processor.addLabel(1, startRow + 1, fcU.getAddress());
		processor.mergeCells(1, startRow + 1, 3, startRow + 1);
		processor.addSubChapter(0, startRow + 2, Presenter.getMessage("Forecasting.general.saved"));
		if (fcU.getForecastObj().getRecordingDate() != null) {
			processor.addDate(1, startRow + 2, fcU.getForecastObj().getRecordingDate().toGregorianCalendar().getTime());
		}
		processor.mergeCells(1, startRow + 2, 3, startRow + 2);
		processor.addSubChapter(0, startRow + 3, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.calculator"));
		processor.addLabel(1, startRow + 3, fcU.getCalculator());
		processor.mergeCells(1, startRow + 3, 3, startRow + 3);
		return startRow + 4;
	}

	/**
	 * General forecast info
	 * 
	 * @param fcU
	 * @param general
	 * @param startRow
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	private int generalForecastInfo(ForecastUIAdapter fcU,  int startRow){
		processor.addSubChapter(0, startRow, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.referenceDate"));
		processor.addDate(1, startRow, fcU.getReferenceDt()); //20160825 appropriate
		processor.mergeCells(1, startRow, 3, startRow);
		
		processor.addSubChapter(0, startRow + 1, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.leadTime"));
		processor.addInteger(1, startRow + 1, fcU.getLeadTime());
		processor.addCenterLabel(2, startRow + 1, DateParser.getMonthLabel(fcU.getLeadTime()));
		processor.mergeCells(2, startRow + 1, 3, startRow + 1);
		
		processor.addSubChapter(0, startRow + 2, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.until"));
		processor.addDate(1, startRow + 2, fcU.getEndDt());
		processor.mergeCells(1, startRow+2, 3, startRow+2);
		
		processor.addSubChapter(0, startRow+3, Messages.getString("ForecastingDocumentWindow.tbParameters.quantperiod"));
		String dur = Presenter.getView().getActiveForecastingPanel().getDurationOfPeriod();
		processor.addCenterLabel(1, startRow + 3, dur);
		processor.mergeCells(1, startRow + 3, 3, startRow + 3);
		
		processor.addSubChapter(0, startRow + 4, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.minstock"));
		processor.addInteger(1,  startRow + 4, fcU.getMinStock());
		processor.addCenterLabel(2, startRow + 4, DateParser.getMonthLabel(fcU.getMinStock()));
		processor.mergeCells(2, startRow + 4, 3, startRow + 4);
		
		processor.addSubChapter(0, startRow + 5, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.maxstock"));
		processor.addInteger(1,  startRow + 5, fcU.getMaxStock());
		processor.addCenterLabel(2, startRow + 5, DateParser.getMonthLabel(fcU.getMaxStock()));
		processor.mergeCells(2, startRow + 5, 3, startRow + 5);
		
		return startRow + 7;
	}
	/**
	 * Cases table for percentage style. Suit for both enrolled and expected cases
	 * @param fcU
	 * @param startRow
	 * @param isNewCases
	 * @return
	 */
	private int generalCases(ForecastUIAdapter fcU, int startRow, boolean isNewCases){
		processor.addCaption(0, startRow, Presenter.getMessage(isNewCases?"ForecastingDocumentWindow.tbParameters.SubTab.NewCases.title":"ForecastingDocumentWindow.tbParameters.SubTab.CasesOnTreatment.title"), 0);
		processor.mergeCells(0, startRow, 10, startRow);
		int j = 1;
		processor.addSubChapter( 0, startRow + 1, "");
		processor.addSubChapter( 0, startRow + 2, "");
		for (MonthQuantityUIAdapter mq : isNewCases?fcU.getNewCases():fcU.getCasesOnTreatment()) {
			processor.addDateMY( j, startRow + 1, mq.getMonth().getAnyDate(1).getTime());
			processor.addInteger( j, startRow + 2, mq.getIQuantity());
			j++;
			if (j > 10) {
				startRow = startRow + 2;
				processor.addSubChapter( 0, startRow + 1, "");
				processor.addSubChapter( 0, startRow + 2, "");
				j = 1;
			}
		}
		return startRow + 4;
	}
	/**
	 * Monthly quantities for percentage style calculation. Suit for oth enrolled and expected cases
	 * @param fcU
	 * @param startRow
	 * @param isNewCases
	 * @return
	 */
	private int generalRegPerc(ForecastUIAdapter fcU, int startRow, boolean isNewCases){
		processor.addCaption( 0, startRow, Presenter.getMessage("Regimen.clmn.Regimen"), 0);
		processor.addCaption( 1, startRow, Presenter.getMessage(isNewCases?"ForecastingDocumentWindow.tbParameters.SubTab.NewCases.percentClmn":"ForecastingDocumentWindow.tbParameters.SubTab.CasesOnTreatment.percentClmn"), 0);
		int i = startRow + 1;
		for (ForecastingRegimenUIAdapter fr : fcU.getRegimes()) {
			processor.addLabel( 0, i, fr.getRegimen().getNameWithForDisplay());
			String string = isNewCases?fr.getPercentNewCases().toString():fr.getPercentCasesOnTreatment().toString();
			processor.addDecimal( 1, i, new BigDecimal(string));
			i++;
		}
		return i + 1;
	}

	/**
	 * cases on treatment table
	 * 
	 * @param fcU
	 * @param general
	 * @param startRow
	 * @param isNewCases
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	private int generalCasesOnTreatment(ForecastUIAdapter fcU,  int startRow, boolean isNewCases){
		processor.addCaption(0, startRow, Presenter.getMessage(isNewCases?"ForecastingDocumentWindow.tbParameters.SubTab.NewCases.title":"ForecastingDocumentWindow.tbParameters.SubTab.CasesOnTreatment.title"), 0);
		int i = startRow;
		int monthCount = 0;
		ForecastingRegimenUIAdapter fR = null;
		for (ForecastingRegimenUIAdapter fr : fcU.getRegimes()) {
			List<MonthQuantityUIAdapter> cases = isNewCases?fr.getNewCases():fr.getCasesOnTreatment();
			if (cases.size() > monthCount) {
				fR = fr;
				monthCount = cases.size();
			}
		}
		int j = 1;
		processor.mergeCells(0, startRow, monthCount, startRow);
		List<MonthQuantityUIAdapter> cases = isNewCases?fR.getNewCases():fR.getCasesOnTreatment();
		for (MonthQuantityUIAdapter mq : cases) {
			processor.addDateMY(j, i + 1, mq.getMonth().getAnyDate(1).getTime());
			j++;
		}
		for (ForecastingRegimenUIAdapter fr : fcU.getRegimes()) {
			processor.addSubChapter(0, i + 2, fr.getRegimen().getNameWithForDisplay());
			j = isNewCases?1:(monthCount + 1 - fr.getFcRegimenObj().getCasesOnTreatment().size());
			for (MonthQuantityUIAdapter mq : isNewCases?fr.getNewCases():fr.getCasesOnTreatment()) {
				processor.addInteger(j, i + 2, mq.getIQuantity());
				j++;
			}
			i++;
		}
		return i + 2;
	}

	/**
	 * All the batches
	 * 
	 * @param fcU
	 * @param general
	 * @param startRow
	 * @return
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	private int generalBatches(ForecastUIAdapter fcU, int startRow) {
		processor.addCaption(0, startRow, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.stockOnHand.plain"), 0);
		processor.mergeCells(0, startRow, 8, startRow);
		processor.addCaption(0, startRow + 1, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.medicines.plain"), 0);
		processor.addCaption(1, startRow + 1, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.expirationDate"), 0);
		processor.addCaption(2, startRow + 1, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.quantity"), 0);
		processor.addCaption(3, startRow + 1, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.comment"), 0);
		processor.mergeCells(3, startRow+1, 8, startRow+1);
		int i = startRow + 2;
		int totalBatches = 0;
		for (ForecastingMedicineUIAdapter med : fcU.getMedicines()) {
			processor.addSubChapter(0, i, med.getMedicine().getNameForDisplay());
			int totalBatchesMed = med.getBatchesToExpireInt();
			processor.addTotal(2, i, totalBatchesMed);
			totalBatches = totalBatches + totalBatchesMed;
			i++;
			for (ForecastingBatchUIAdapter bt : med.getBatchesToExpire()) {
				processor.addDate(1, i, bt.getExpiredDtEdit());
				processor.addInteger(2, i, bt.getQuantity());
				processor.addLabel(3, i, bt.getComment());
				processor.mergeCells(3, i, 8, i);
				i++;
			}
		}
		return i + 1;
	}

	/**
	 * all the orders (like a batches)
	 * 
	 * @param fcU
	 * @param general
	 * @param startRow
	 * @return
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	private int generalOrders(ForecastUIAdapter fcU, int startRow){
		processor.addCaption(0, startRow, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.stockOnOrder.plain"), 0);
		processor.mergeCells(0, startRow, 9, startRow);
		processor.addCaption(0, startRow + 1, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.medicines.plain"), 0);
		processor.addCaption(1, startRow + 1, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.receivingDate.plain"), 0);
		processor.addCaption(2, startRow + 1, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.expirationDate"), 0);
		processor.addCaption(3, startRow + 1, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.quantity"), 0);
		processor.addCaption(4, startRow + 1, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.comment"), 0);
		processor.mergeCells(4, startRow+1, 9, startRow+1);
		int i = startRow + 2;
		int totalOrd = 0;
		for (ForecastingMedicineUIAdapter med : fcU.getMedicines()) {
			int medStockOrd = med.getStockOnOrderInt();
			if(medStockOrd != 0){
				processor.addSubChapter(0, i, med.getMedicine().getNameForDisplay());
				processor.addTotal(3, i, medStockOrd);
				totalOrd = totalOrd + medStockOrd;
				i++;
				for (ForecastingOrderUIAdapter ord : med.getOrders()) {
					processor.addDate(1, i, ord.getArrivedDt());
					Date exp = ord.getBatch().getExpiredDtEdit();
					if (exp != null) {
						processor.addDate(2, i, exp);
					} else {
						processor.addLabel(2, i, "");
					}
					processor.addInteger(3, i, ord.getBatch().getQuantity());
					processor.addLabel(4, i, ord.getComment());
					processor.mergeCells(4, i, 9, i);
					i++;
				}
			}
		}
		return i + 1;
	}

	/**
	 * Create summary page on the workbook
	 * @param res medicines resume
	 * @param cons medicines consumptions
	 * @param fcU the forecast
	 * @param orderCalculator order's calculator
	 */
	public void createSummary(List<MedicineResume> res, List<MedicineConsumption> cons, ForecastUIAdapter fcU, OrderCalculator orderCalculator){
		processor.createSheet(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.title"), 100);
		createSummaryHeader(res, fcU);
		createSummaryContent(res, cons, fcU, orderCalculator);
	}

	/**
	 * create summary table header like in web version consume two lines at
	 * sheet top
	 * 
	 * @param summary
	 * @param resume medicine resume
	 * @param fcU forecasting
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	private void createSummaryHeader(List<MedicineResume> resume, ForecastUIAdapter fcU) {
		//set height of first and second caption rows
		processor.setRowView(1, 700);
		processor.setRowView(2, 1500);
		processor.addLabel(0, 0, fcU != null ? fcU.getDetailsInformationTxt() : "");
		processor.mergeCells(0, 0, 12, 0);
		processor.makeBoldLabel(0,0,": ");


		processor.addCaption(0, 1, Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.Medicine"), 35);
		processor.mergeCells(0, 1, 0, 2);
		
		String s = getInventoryCaption(fcU);
		processor.addCaption(1, 1, s, 12);
		processor.mergeCells(1, 1, 2, 1);
		
		s = cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.StockOnHand"));
		processor.addCaption(1, 2, s, 12);
		//processor.mergeCells(1, 1, 1, 2);

		s = cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.monthsOfStock"));
		processor.addCaption(2, 2, s, 12);
		//processor.mergeCells(2, 1, 2, 2);


/*		s = cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.nextProcurementDate"));
		processor.addCaption(3, 1, s, 12);
		processor.mergeCells(3, 1, 3, 2);*/

		s = getLeadCaption(resume);
		processor.addCaption(3, 1, s, 12);
		processor.mergeCells(3, 1, 5, 1);

		s = cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.StockOnOrder"));
		processor.addCaption(3, 2, s, 12);
		s = cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.DispensingQuantity"));
		processor.addCaption(4, 2, s, 12);
		s = cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.QuantityLost"));
		processor.addCaption(5, 2, s, 12);
		//review
		s = getReviewCaption(fcU);
		processor.addCaption(6, 1, s, 11);
		processor.mergeCells(6, 1, 10, 1);
		s = cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.StockOnHandAfter"));
		processor.addCaption(6, 2, s, 12);
		s = cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.StockOnOrder"));
		processor.addCaption( 7, 2, s, 12);
		s = cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.QuantityLost"));
		processor.addCaption(8, 2, s, 12);
		s = cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.EstimatedConsumptionPrev"));
		processor.addCaption(9, 2, s, 12);
		s = cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.EstimatedConsumptionNew"));
		processor.addCaption(10, 2, s, 12);
		//s= cleanStr(Messages.getString("ForecastingDocumentWindow.tbSummary.column.group.total"))
		s=getTotalCaption(fcU, resume);
		processor.addCaption(11, 1, s, 13);
		processor.mergeCells(11, 1, 13, 1);
		s = cleanStr(Messages.getString("ForecastingDocumentWindow.tbSummary.column.accQuantity"));
		processor.addCaption(11, 2, s, 12);
		s = cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.EstimatedQuantity"));
		processor.addCaption(12, 2, s, 12);
		s = cleanStr(Messages.getString("ForecastingDocumentWindow.tbSummary.column.total"));
		processor.addCaption(13, 2, s, 12);
	}

	private String getInventoryCaption(ForecastUIAdapter fcU) {
		String ret = "";
		ret = Messages.getString("ForecastingDocumentWindow.tbSummary.column.group.RD")+ " "+
				DateUtils.formatMedium(fcU.getReferenceDate());
		return ret;
	}

	private String getTotalCaption(ForecastUIAdapter fui, List<MedicineResume> resume) {
		String cap = Messages.getString("ForecastingDocumentWindow.tbSummary.column.group.total");
		if (resume != null) {
			MedicineResume mr = resume.get(0);
			cap = cap + " " + mr.getLeadPeriod().getFromTxt() + " - " + fui.getEndReviewPeriodTxt() + " "+
					fui.getForecastingDurationDays();
		}
		String s = cleanStr(cap);
		return s;
	}

	/**
	 * Create content in summary worksheet
	 * 
	 * @param summary worksheet
	 * @param res results
	 * @param cons consumptions
	 * @param fcU forecasting
	 * @param orderCalculator 
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	private void createSummaryContent(List<MedicineResume> res, List<MedicineConsumption> cons, ForecastUIAdapter fcU, OrderCalculator orderCalculator) {
		if (res != null) {
			int i = 3;
			int j=0;
			SummaryDataModelHelper model = new SummaryDataModelHelper(fcU, res, cons,orderCalculator);
			Object[][] data = model.getData();
			for (MedicineResume mr : res) {
				processor.addLabel(0, i, (String) data[j][0]);
				processor.addInteger(1, i, (Integer) data[j][1]);
				processor.addInteger(2, i, toIntegerOrZero(((String) data[j][2]).replace(".COLOR","")));
				//processor.addCenterLabel(3, i, ((String) data[j][3]).replace(".COLOR",""));
				processor.addInteger(3, i, (Integer) data[j][3]);
				processor.addInteger(4, i, (Integer) data[j][4]);
				processor.addInteger(5, i, (Integer) data[j][5]);
				processor.addInteger(6, i, (Integer) data[j][6]);
				processor.addInteger(7, i, (Integer) data[j][7]);
				processor.addInteger(8, i, (Integer) data[j][8]);
				processor.addInteger(9, i, (Integer) data[j][9]);
				processor.addInteger(10, i, (Integer) data[j][10]);
				processor.addInteger(11, i, (Integer) data[j][11]); //accel forward 20161114
				processor.addInteger(12, i, (Integer) data[j][12]);
				processor.addInteger(13, i, (Integer) data[j][13]);
				
				
				
/*				processor.addInteger(3, i, mr.getLeadPeriod().getTransit());
				processor.addInteger(4, i, mr.getLeadPeriod().getDispensedInt());
				processor.addInteger(5, i, mr.getLeadPeriod().getExpired());
				processor.addInteger(6, i, mr.getReviewPeriod().getIncomingBalance());
				processor.addInteger(7, i, mr.getReviewPeriod().getTransit());
				processor.addInteger(8, i, mr.getReviewPeriod().getExpired());
				processor.addInteger(9, i, mr.getReviewPeriod().getConsumedOld().intValue());
				processor.addInteger(10, i, mr.getReviewPeriod().getConsumedNew().intValue());
				processor.addInteger(11, i, mr.getQuantityToProcured().intValue());
				processor.addInteger(12, i, mr.getLeadPeriod().getMissing().intValue());
				processor.addInteger(13, i, mr.getQuantityToProcured().add(mr.getLeadPeriod().getMissing()).intValue());*/
				i++;
				j++;
			}
		}
	}

	/**
	 * Create report of medicine consumption
	 * 
	 * @param cons medicine consumption
	 * @param details details information
	 * @throws IOException
	 * @throws WriteException
	 */
	public void createConsumption(List<MedicineConsumption> cons, String details) {
		processor.createSheet(Presenter.getMessage("ForecastingDocumentWindow.tbMedicinesReport.title"), 100);
		int blockRow = CONTENT_OFFSET;
		createConsHeader(cons, details);
		for (MedicineConsumption mc : cons) {
			processor.addSubChapter(0, blockRow, mc.getMed().getNameForDisplay());
			processor.mergeCells(0, blockRow, mc.getCons().size(), blockRow);
			processor.addLabel(0, blockRow + 1, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbMedicinesReport.column.StockOnHand")));
			processor.addLabel(0, blockRow + 2, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbMedicinesReport.column.QuantityMissing")));
			processor.addLabel(0, blockRow + 3, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbMedicinesReport.column.QuantityLostDue")));
			processor.addLabel(0, blockRow + 4, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbMedicinesReport.column.StockOnOrder")));
			processor.addLabel(0, blockRow + 5, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbMedicinesReport.column.ConsumptionPrev")));
			processor.addLabel(0, blockRow + 6, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbMedicinesReport.column.ConsumptionNew")));
			processor.addLabel(0, blockRow + 7, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbMedicinesReport.column.total")));
			int j = 1;
			for (ConsumptionMonth cm : mc.getCons()) {
				processor.addInteger(j, blockRow + 1, cm.getOnHand().intValue());
				processor.addInteger(j, blockRow + 2, cm.getMissing().intValue());
				processor.addInteger(j, blockRow + 3, cm.getExpired());
				processor.addInteger(j, blockRow + 4, cm.getOrder());
				processor.addInteger(j, blockRow + 5, cm.getConsOld().intValue());
				processor.addInteger(j, blockRow + 6, cm.getConsNew().intValue());
				processor.addInteger(j, blockRow + 7, cm.getConsNew().add(cm.getConsOld()).intValue());
				j++;
			}
			blockRow = blockRow + 8;
		}
	}


	/**
	 * create table header
	 * 
	 * @param cons - month list need
	 * @param sheet worksheet for report
	 * @param details details information
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	private void createConsHeader(List<MedicineConsumption> cons, String details){
		processor.addLabel(0, 0, details != null ? details : "");
		processor.mergeCells(0, 0, cons.get(0).getCons().size(), 0);
		processor.addCaption(0, 1, Presenter.getMessage("ForecastingDocumentWindow.tbDetailedReport.medicine"), 30);
		int i = 1;
		MedicineConsumption mc = cons.get(0);
		for (ConsumptionMonth cm : mc.getCons()) {
			processor.addCaption(i, 1, cm.getMonth().toString(), 0);
			i++;
		}

	}
	/**
	 * Regimes on treatment report only for multi drug
	 * @param reg regimen
	 * @param startForecasting 
	 * @param factory
	 * @param details commentary to place on the top
	 */
	public void createRegimensOnTreatment(
			List<ForecastingRegimenUIAdapter> reg, Calendar startForecasting,
			ModelFactory factory, String details) {
		processor.createSheet(Presenter.getMessage("ForecastingDocumentWindow.tbCasesReport.title.excel"), 100);
		createRegimensHeader(reg.get(0).getMonthsResults(startForecasting, factory), details);
		int blockRow = CONTENT_OFFSET;
		Map<Integer, String> grandTotal = new HashMap<Integer, String>(); //grand totals formulas by columns
		//content
		for (ForecastingRegimenUIAdapter r : reg) {
			processor.addSubChapter(0, blockRow, r.getRegimen().getName());
			processor.mergeCells(0, blockRow, 0, blockRow + 2);
			processor.addSubChapter(1, blockRow, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbCasesReport.column.PrevCases")));
			processor.addSubChapter(1, blockRow + 1, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbCasesReport.column.NewCases")));
			processor.addSubChapter(1, blockRow + 2, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbCasesReport.column.Total")));
			int i = 2;
			for (ForecastingRegimenResultUIAdapter rr : r.getMonthsResults(startForecasting, factory)) {
				processor.addDecimal(i, blockRow, rr.getEnrolled());
				processor.addDecimal(i, blockRow + 1, rr.getExpected());
				processor.addSumFormula(i, blockRow, blockRow + 1, grandTotal,false);
				i++;
			}
			blockRow = blockRow + 3;
		}
		//grand total
		processor.addSubChapter(0, blockRow, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbCasesReport.column.GrandTotal")));
		processor.mergeCells(0, blockRow, 1, blockRow);
		for (Integer j : grandTotal.keySet()) {
			String s = grandTotal.get(j);
			s = s.substring(0, s.length() - 1);
			processor.addFormula(j, blockRow, s, true,false);
		}
	}

	private void createRegimensHeader(List<ForecastingRegimenResultUIAdapter> rres, String details){
		processor.addLabel(0, 0, details != null ? details : "");
		processor.mergeCells(0, 0, rres.size() + 1, 0);
		processor.addCaption(0, 1, Presenter.getMessage("ForecastingDocumentWindow.tbCasesReport.column.TreatmentRegimen"), 30);
		processor.mergeCells(0, 1, 1, 1);
		int i = 2;
		for (ForecastingRegimenResultUIAdapter rrU : rres) {
			processor.addCaption(i, 1, rrU.getMonth().toString(), 0);
			i++;
		}
	}

	/**
	 * Medicines (single drug) on treatment
	 * @param cons
	 * @param factory
	 * @param details
	 */
	public void createMedicinesOnTreatment(List<MedicineConsumption> cons, ModelFactory factory, String details){
		processor.createSheet(Presenter.getMessage("ForecastingDocumentWindow.tbCasesReport.titleMed"), 100);
		createCasesMedHeader(cons.get(0), details);
		int blockRow = CONTENT_OFFSET;
		Map<Integer, String> grandTotal = new HashMap<Integer, String>(); //grand totals formulas by columns
		//content
		for (MedicineConsumption r : cons) {
			processor.addSubChapter(0, blockRow, r.getMed().getNameForDisplay());
			processor.mergeCells(0, blockRow, 0, blockRow + 2);
			processor.addSubChapter(1, blockRow, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbCasesReport.column.PrevCases")));
			processor.addSubChapter(1, blockRow + 1, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbCasesReport.column.NewCases")));
			processor.addSubChapter(1, blockRow + 2, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbCasesReport.column.Total")));
			int i = 2;
			for (ConsumptionMonth rr : r.getCons()) {
				processor.addDecimal(i, blockRow, rr.getOldCases());
				processor.addDecimal(i, blockRow + 1, rr.getNewCases());
				processor.addSumFormula(i, blockRow, blockRow + 1, grandTotal, true);
				i++;
			}
			blockRow = blockRow + 3;
		}
	}


	/**
	 * paint medicine consumption header
	 * @param medicineConsumption
	 * @param details
	 */
	private void createCasesMedHeader(MedicineConsumption medicineConsumption, String details){
		processor.addLabel(0, 0, details != null ? details : "");
		processor.mergeCells(0, 0, medicineConsumption.getCons().size() + 1, 0);
		processor.makeBoldLabel(0,0,": ");
		processor.addCaption(0, 1, Presenter.getMessage("ForecastingDocumentWindow.tbDetailedReport.medicine"), 30);
		processor.mergeCells(0, 1, 1, 1);
		int i = 2;
		for (ConsumptionMonth mc : medicineConsumption.getCons()) {
			processor.addCaption(i, 1, mc.getMonth().toString(), 0);
			i++;
		}
	}

	public void createMedicinesResume(MedicineUIAdapter med, List<PeriodResume> res,  String details) {
		String medicineNameWithEllipsis = (med.getNameForDisplayWithAbbrev().length() > 31) ? (med.getNameForDisplayWithAbbrev().substring(0, 28).concat("...")) : med.getNameForDisplayWithAbbrev();
		medicineNameWithEllipsis = medicineNameWithEllipsis.replaceAll("/", " ");
		processor.createSheet(medicineNameWithEllipsis, 100);
		processor.addLabel(0, 0, details != null ? details : "");
		processor.mergeCells(0, 0, 7, 0);
		processor.addCaption(0, 1, med.getNameForDisplayWithAbbrev(), 0);
		processor.mergeCells(0, 1, 7, 1);
		int offset = 2;
		processor.addCaption(0, offset, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbDetailedReport.column.period")), 40);
		processor.addCaption(1, offset, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.stockOnHand.plain")), 0);
		processor.addCaption( 2, offset, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbDetailedReport.column.EstimatedConsumptionPrev")), 20);
		processor.addCaption( 3, offset, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbDetailedReport.column.EstimatedConsumptionNew")), 20);
		processor.addCaption( 4, offset, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbDetailedReport.column.EstimatedConsumptionTotal")), 20);
		processor.addCaption( 5, offset, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.stockOnOrder.plain")), 20);
		processor.addCaption( 6, offset, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbDetailedReport.column.QuantityLost")), 20);
		processor.addCaption( 7, offset, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbDetailedReport.column.QuantityMissing")), 20);

		int j = offset + 1;
		for (PeriodResume pr : res) {
			processor.addLabel( 0, j, pr.getFromTxt() + "..." + pr.getToTxt() + " " + pr.getDaysBetweenPeriodTxt());
			processor.addInteger( 1, j, pr.getIncomingBalance());
			processor.addInteger( 2, j, pr.getConsumedOld().intValue());
			processor.addInteger( 3, j, pr.getConsumedNew().intValue());
			processor.addInteger( 4, j, pr.getConsumedNew().intValue() + pr.getConsumedOld().intValue());
			processor.addInteger( 5, j, pr.getTransit());
			processor.addInteger( 6, j, pr.getExpired());
			processor.addInteger( 7, j, pr.getMissing().intValue());
			j++;
		}
	}

	/**
	 * Paint order on two sheets<br>
	 * First sheet is for regular, accelerated and total only medicines costs
	 * Second sheet is for additional costs for regular and accelerated
	 * @param total
	 * @param totalR
	 * @param totalA
	 */
	public void createOrder(ForecastingTotal total, ForecastingTotal totalR, ForecastingTotal totalA){
		processor.createSheet(Presenter.getMessage("ForecastingDocumentWindow.order.titleorder"), 100);
		processor.addLabel(0, 0, total.getForecastUI().getTotalComment1() != null ?
				Messages.getString("ForecastingDocumentWindow.tbParameters.comment")+" "+total.getForecastUI().getTotalComment1() : "");
		processor.mergeCells(0, 0, 12, 0);
		processor.makeBoldLabel(0,0,": ");
		addDeliveryScheduleData(total, 1, 0);
		createOrderMedicinesData(totalR, totalA, total);
		processor.createSheet(Presenter.getMessage("ForecastingDocumentWindow.order.totalTabName"), 100);
		addDeliveryScheduleData(total, 0, 0);
		int nextRow = createOrderTotal(totalR,1);
		nextRow = createOrderTotal(totalA, ++nextRow);
		//grand total structure
		nextRow++;
		processor.addSubChapter(0, nextRow, Messages.getString("ForecastingDocumentWindow.order.grandmedtotal"));
		processor.enlargeFont(0,nextRow);
		processor.addDecimalSubChapter(1, nextRow, total.getMedTotal());
		nextRow++;
		processor.addSubChapter(0, nextRow, Messages.getString("ForecastingDocumentWindow.order.grandTotalOther"));
		processor.enlargeFont(0,nextRow);
		processor.addDecimalSubChapter(1, nextRow, total.getOrderGrandTotal().subtract(total.getMedTotal()));
		nextRow++;
		processor.addSubChapter(0, nextRow, total.getGrandTotalLabel());
		processor.enlargeFont(0,nextRow);
		processor.addDecimalSubChapter(1, nextRow, total.getOrderGrandTotal());
	}
	/**
	 * Add delivery schedule data to the sheet row, col
	 * @param total to get
	 * @param row 
	 * @param col 
	 */
	public void addDeliveryScheduleData(ForecastingTotal total, int row, int col) {
		processor.addLabel(col, row, Messages.getString("DeliveryOrder.labels.schedule")
													+ " " 
													+ Messages.getString("DeliveryOrder.enum." + total.getForecastUI().getDeliverySchedule().toString()));
		processor.makeBoldLabel(row,col,": ");
	}

	/**
	 * Create a first sheet - only medicines
	 * @param totalR regular order data
	 * @param totalA accelerated order data
	 * @param total total data
	 */
	private void createOrderMedicinesData(ForecastingTotal totalR, ForecastingTotal totalA, ForecastingTotal total){
		//TODO comment
		int i = createRegularOrderMedicinesData(totalR);
		processor.addSubChapter(0, i, Messages.getString("ForecastingDocumentWindow.order.submedtotal"));
		processor.mergeCells(0, i, 6, i);
		processor.addDecimalSubChapter(7, i, totalR.getMedTotal());
		i++;
		i = createAccelOrderMedicinesData(totalA,i+1);
		processor.addSubChapter(0, i, Messages.getString("ForecastingDocumentWindow.order.submedtotal"));
		processor.mergeCells(0, i, 6, i);
		processor.addDecimalSubChapter(7, i, totalA.getMedTotal());
		i++;
		i = createTotalOrderData(total,i+1);
		int totalRow = i;
		processor.addSubChapter(0, totalRow, Presenter.getMessage("ForecastingDocumentWindow.order.grandmedtotal"));
		processor.mergeCells(0, i, 2, i);
		processor.addDecimalSubChapter(3, totalRow, total.getMedTotal());
/*		int commentRow = i + 2;
		processor.addSubChapter(0, commentRow, Presenter.getMessage("ForecastingDocumentWindow.order.commentlbl"));
		processor.addLabel(0, commentRow + 1, total.getForecastUI().getTotalComment1());*/
	}

	/**
	 * Paint a regular - medicines only order
	 * @param total data
	 * @return last row that will be used to position the accelerated order
	 */
	private int createRegularOrderMedicinesData(ForecastingTotal total) {
		int row = 2;
		processor.addSubChapter( 0, row, Presenter.getMessage("ForecastingDocumentWindow.order.regular"));
		processor.addCaption( 0, row+1, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.medicines"), 60);
		processor.addCaption( 1, row+1, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.quantityneeded"), 15);
		processor.addCaption( 2, row+1, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.adjcoef"), 15);
		processor.addCaption( 3, row+1, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.packsize"), 15);
		processor.addCaption( 4, row+1, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.packprice"), 15);
		processor.addCaption( 5, row+1, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.quantityadjusted"), 15);
		processor.addCaption( 6, row+1, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.packajusted"), 15);
		processor.addCaption( 7, row+1, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.regordcost"), 15);
		int i = row + 2;
		for (ForecastingTotalMedicine tmed : total.getMedItems()) {
			processor.addLabel( 0, i, tmed.getMedicine().getNameForDisplayWithAbbrev());
			addNoZero( 1, i, tmed.getRegularQuant());
			addDecimalNoZero( 2, i, tmed.getAdjustIt());
			addNoZero( 3, i, tmed.getPackSize());
			addDecimalNoZero( 4, i, tmed.getPackPrice());
			addNoZero( 5, i, tmed.getAdjustedRegular());
			addNoZero( 6, i, tmed.getAdjustedRegularPack());
			addDecimalNoZero( 7, i, tmed.getRegularCost());
			i++;
		}
		return i;
	}

	/**
	 * Paint an accelerated medicines only order
	 * @param total order data
	 * @param startRow
	 * @return
	 */
	private int createAccelOrderMedicinesData(ForecastingTotal total, int startRow){
		processor.addSubChapter( 0, startRow, Presenter.getMessage("ForecastingDocumentWindow.order.accel"));
		int headRow = startRow  +1;
		processor.addCaption( 0, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.medicines"), 60);
		processor.addCaption( 1, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.quantityneeded"), 15);
		processor.addCaption( 2, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.adjcoef"), 15);
		processor.addCaption( 3, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.packsize"), 15);
		processor.addCaption( 4, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.packprice"), 15);
		processor.addCaption( 5, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.quantityadjusted"), 15);
		processor.addCaption( 6, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.packajusted"), 15);
		processor.addCaption( 7, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.accordcost"), 15);
		//processor.addCaption( 8, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.accorddate"), 15);

		int i = startRow+2;
		for (ForecastingTotalMedicine tmed : total.getMedItems()) {
			processor.addLabel( 0, i, tmed.getMedicine().getNameForDisplayWithAbbrev());
			addNoZero( 1, i, tmed.getAccelQuant());
			addDecimalNoZero( 2, i, tmed.getAdjustItAccel());
			addNoZero( 3, i, tmed.getPackSizeAccel());
			addDecimalNoZero( 4, i, tmed.getPackPriceAccel());
			addNoZero( 5, i, tmed.getAdjustAccel());
			addNoZero( 6, i, tmed.getAdjustedAccelPack());
			addDecimalNoZero( 7, i, tmed.getAccelCost());
			//addDateNoZero( 8, i, tmed.getAccelDate());
			i++;
		}
		return i;
	}
	/**
	 * Paint a total for a regular and an accelerated orders
	 * @param total
	 * @param startRow
	 * @return
	 */
	private int createTotalOrderData(ForecastingTotal total,int startRow) {
		processor.addSubChapter( 0, startRow, Presenter.getMessage("ForecastingDocumentWindow.order.total"));
		processor.enlargeFont(0,startRow);
		int headRow = startRow  +1;
		processor.addCaption( 0, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.medicines"), 60);
		processor.addCaption( 1, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.quantityneeded"), 15);
		processor.addCaption( 2, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.quantityadjusted"), 15);
		//if (total.getMedTotal().compareTo(BigDecimal.ZERO) > 0){
		processor.addCaption( 3, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.totalcost"), 15);
		//}

		int i = startRow+2;
		for (ForecastingTotalMedicine tmed : total.getMedItems()) {
			processor.addLabel( 0, i, tmed.getMedicine().getNameForDisplayWithAbbrev());
			addNoZero( 1, i, tmed.getBruttoQuant());
			addNoZero( 2, i, tmed.getTotal());
			//if (total.getMedTotal().compareTo(BigDecimal.ZERO) > 0){
			addDecimalNoZero( 3, i, tmed.getTotalCost());
			//}
			i++;
		}
		return i;
	}
	/**
	 * Paint a order section on an additional costs sheet
	 * @param total
	 * @param currentRow from this row
	 * @return next row number
	 */
	private int createOrderTotal(ForecastingTotal total, int currentRow) {
		int col =0;
		processor.addCaption(0, currentRow, total.getTitle(), 60);
		processor.mergeCells(0, currentRow, 1, currentRow);
		processor.enlargeFont(0,currentRow);
		currentRow++;
		processor.addCaption( col++, currentRow, Presenter.getMessage("ForecastingDocumentWindow.order.item.title"), 0);
		processor.addCaption( col, currentRow, Presenter.getMessage("ForecastingDocumentWindow.tbSummary.total.cost.plain"), 15);
		currentRow++;
		for (ForecastingTotalItemUIAdapter item : total.getAddItems()) {
			col=0;
			processor.addLabel( col++, currentRow, item.getItem());
			BigDecimal val = item.getValue();
			if(item.getFcItemObj().isIsValue()){
				val = val.multiply(new BigDecimal(total.getDeliveriesQuantity()));
			}
			addDecimalNoZero( col, currentRow, val);
			currentRow++;
		}
		processor.addSubChapter(0, currentRow, Messages.getString("ForecastingDocumentWindow.order.submedtotal"));
		processor.addDecimalSubChapter(1, currentRow, total.getMedTotal());
		currentRow++;
		processor.addSubChapter(0, currentRow, Messages.getString("DeliveryOrder.labels.additionalCost"));
		processor.addDecimalSubChapter(1, currentRow, total.getOrderGrandTotal().subtract(total.getMedTotal()));
		currentRow++;
		currentRow++;
		processor.addSubChapter( 0, currentRow, total.getGrandTotalLabel() );
		processor.addDecimalSubChapter( 1, currentRow, total.getOrderGrandTotal());
		if (total.getComment() != null && total.getComment().length()>0){
			currentRow++;
			currentRow++;
			processor.addSubChapter(0, currentRow, Messages.getString("ForecastingDocumentWindow.tbParameters.comment") +" "+total.getComment());
			processor.mergeCells(0, currentRow, 1, currentRow);
		}
		return ++currentRow;
	}

	/**
	 * Create sheet with delivery schedule
	 * @param control
	 */
	public void createSchedule(DeliveryOrdersControl control) {
		processor.createSheet(Presenter.getMessage("ForecastingDocumentWindow.order.schedTabName"), 100);
		//paint orders
		int row=0;
		int col = 0;
		int accNo=1;
		int regNo=1;
		int maxRows=0;
		int thisRow = 1;
		addDeliveryScheduleData(control.getTotal(), 0, 0);
		for(DeliveryOrderUI order : control.getAccelerated()){
			row = paintDeliveryOrder(order, thisRow, col, accNo, false);
			if(row>maxRows){
				maxRows=row;
			}
			if(col>0){
				col=0;
				thisRow = maxRows;
			}else{
				col=5;
			}
			accNo++;
		}
		for(DeliveryOrderUI order : control.getRegular()){
			row = paintDeliveryOrder(order, thisRow, col, regNo, true);
			if(row>maxRows){
				maxRows=row;
			}
			if(col>0){
				col=0;
				thisRow = maxRows;
			}else{
				col=5;
			}
			regNo++;
		}

	}
	/**
	 * Paint a delivery order number ordNo from row in col regular or accelerated
	 * @param order
	 * @param row
	 * @param col
	 * @param ordNo 
	 * @param isRegular
	 * @return row for a next order
	 */
	private int paintDeliveryOrder(DeliveryOrderUI order, int row, int col, int ordNo, boolean isRegular) {
		String[] columnNames = new String[] { 
				Messages.getString("ForecastingDocumentWindow.order.ordcolumns.medicines"),
				Messages.getString("ForecastingDocumentWindow.order.ordcolumns.quantityadjusted"),
				Messages.getString("ForecastingDocumentWindow.order.ordcolumns.packajusted"),
				Messages.getString("ForecastingDocumentWindow.order.ordcolumns.regordcost")
		};

		//header
		String ordType = isRegular? Messages.getString("DeliveryOrder.labels.regularNo") : Messages.getString("DeliveryOrder.labels.accOrdNo");
		processor.addCaption(col, row, ordType + " " + ordNo, 40);
		processor.addCaption(col+1, row,Messages.getString("DeliveryOrder.labels.ordDate") , 20);
		processor.addCaption(col+2, row,order.getOrderDateStr(), 0);
		processor.addCaption(col+1, row+1,Messages.getString("DeliveryOrder.labels.deliveryDate"), 20);
		processor.addCaption(col+2, row+1,order.getDeliveryDateStr() , 0);
		//merge
		processor.mergeCells(col, row, col, row+1);
		processor.mergeCells(col+2, row, col+3, row);
		processor.mergeCells(col+2, row+1, col+3, row+1);
		processor.setRowView(row, 750);
		processor.setRowView(row+1, 750);
		//columns
		processor.addCaption(col, row+2, columnNames[0], 0);
		processor.addCaption(col+1, row+2, columnNames[1], 20);
		processor.addCaption(col+2, row+2, columnNames[2], 0);
		processor.addCaption(col+3, row+2, columnNames[3], 20);
		//data
		int rowInc=1;
		for(DeliveryOrderItemUI item : order.getItems()){
			processor.addLabel(col, row+2+rowInc, item.getMedicine().getNameForDisplayWithAbbrev());
			processor.addDecimal(col+1, row+2+rowInc,item.getUnits());
			processor.addDecimal(col+2, row+2+rowInc, item.getPacks());
			processor.addDecimal(col+3, row+2+rowInc, item.getCost());
			rowInc++;
		}
		//footer
		processor.addSubChapter(col, row+2+rowInc, Messages.getString("ForecastingDocumentWindow.order.submedtotal"));
		processor.mergeCells(col, row+2+rowInc, col+2, row+2+rowInc);
		processor.addDecimalSubChapter(col+3, row+2+rowInc, order.getMedCost());
		processor.addSubChapter(col, row+2+rowInc+1, Messages.getString("DeliveryOrder.labels.additionalCost"));
		processor.mergeCells(col, row+2+rowInc+1, col+2, row+2+rowInc+1);
		processor.addDecimalSubChapter(col+3, row+2+rowInc+1, order.getAddCost());
		processor.addSubChapter(col, row+2+rowInc+2, Messages.getString("DeliveryOrder.labels.ordMedCost")+":");
		processor.mergeCells(col, row+2+rowInc+2, col+2, row+2+rowInc+2);
		processor.addDecimalSubChapter(col+3, row+2+rowInc+2, order.calcTotal());
/*		if(col==0){
			return row;
		}else{
			return row+4+rowInc+3;
		}*/
		return row+4+rowInc+3;
	}

	/**
	 * Save result
	 * @throws IOException 
	 */
	public void save() throws IOException {
		processor.save();
	}

	/**
	 * Clean message string from bad characters
	 * 
	 * @param s
	 * @return
	 */
	private String cleanStr(String s) {
		s = s.replaceAll("\\r", " ");
		s = s.replaceAll("\\n", " ");
		s = s.trim();
		return s;
	}

	/**
	 * Get caption for lead period
	 * 
	 * @param resume medicine resume
	 * @return
	 */
	private String getLeadCaption(List<MedicineResume> resume) {
		String cap = Messages.getString("ForecastingDocumentWindow.tbSummary.column.group.leadTime");
		if (resume != null) {
			MedicineResume mr = resume.get(0);
			cap = cap + " " + mr.getLeadPeriod().getFromTxt() + " - " + mr.getLeadPeriod().getToTxt() + " " + mr.getLeadPeriod().getDaysBetweenPeriodTxt();
		}
		String s = cleanStr(cap);
		return s;
	}

	/**
	 * get review period caption
	 * 
	 * @param fcU medicine resume
	 * @return
	 */
	private String getReviewCaption(ForecastUIAdapter fcU) {
		String cap = "";
		if (fcU != null) {
			cap = cap + Messages.getString("ForecastingDocumentWindow.tbSummary.column.group.reviewPeriod") + " " + fcU.getStartReviewPeriodTxt() + " - " + fcU.getEndReviewPeriodTxt() + " " + fcU.getDurationOfReviewPeriodInDays();
		}
		String s = cleanStr(cap);
		return s;
	}

	/**
	 * Convert parameter to the Integer
	 * @param maybeInt
	 * @return converted integer or zero, if conversion impossible
	 */
	private Integer toIntegerOrZero(String maybeInt) {
		Integer ret = 0;
		try {
			ret = new Integer(maybeInt);
		} catch (NumberFormatException e) {
			ret = 0;
		}
		return ret;
	}

	/**
	 * Add decimal to col, row or dash if decimal is zero
	 * @param col
	 * @param row
	 * @param value
	 */
	private void addDecimalNoZero(int col, int row, BigDecimal value){
		if (value.compareTo(BigDecimal.ZERO)==0) {
			processor.addCenterLabel(col, row, "-");
		} else {
			processor.addDecimal(col, row, value);
		}
	}
	/**
	 * add integer to col, row or dash if integer is zero
	 * @param col
	 * @param row
	 * @param value
	 */
	private void addNoZero(int col, int row, Integer value){
		if (value == 0) {
			processor.addCenterLabel(col, row, "-");
		} else {
			processor.addInteger(col, row, value);
		}
	}

	/**
	 * add date to col, row or dash if date is undefined
	 * @param col
	 * @param row
	 * @param value
	 */
	private void addDateNoZero(int col, int row, Date value){
		if (value == null) {
			processor.addCenterLabel(col, row, "-");
		} else {
			processor.addDate(col, row, value);
		}

	}


}
