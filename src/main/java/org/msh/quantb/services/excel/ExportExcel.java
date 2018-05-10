package org.msh.quantb.services.excel;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.msh.quantb.model.mvp.ModelFactory;
import org.msh.quantb.services.calc.ConsumptionMonth;
import org.msh.quantb.services.calc.DateParser;
import org.msh.quantb.services.calc.MedicineConsumption;
import org.msh.quantb.services.calc.MedicineResume;
import org.msh.quantb.services.calc.PeriodResume;
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

/**
 * This class intends to export quantification reports to Excel
 * 
 * @author alexey
 * 
 */
public class ExportExcel extends JXLProcessor {
	private static final int CONTENT_OFFSET = 2;

	/**
	 * Medicines and cases report
	 * 
	 * @param cons
	 * @param factory
	 * @param details details information
	 * @throws IOException
	 * @throws WriteException
	 */
	public void createMedicinesOnTreatment(List<MedicineConsumption> cons, ModelFactory factory, String details) throws WriteException, IOException {
		WritableSheet casesMed = this.getWorkbook().createSheet(cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbCasesReport.titleMed")), 4);
		createCasesMedHeader(casesMed, cons.get(0), factory, details);
		int blockRow = CONTENT_OFFSET;
		Map<Integer, String> grandTotal = new HashMap<Integer, String>(); //grand totals formulas by columns
		//content
		for (MedicineConsumption r : cons) {
			addSubChapter(casesMed, 0, blockRow, r.getMed().getNameForDisplay());
			casesMed.mergeCells(0, blockRow, 0, blockRow + 2);
			addSubChapter(casesMed, 1, blockRow, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbCasesReport.column.PrevCases")));
			addSubChapter(casesMed, 1, blockRow + 1, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbCasesReport.column.NewCases")));
			addSubChapter(casesMed, 1, blockRow + 2, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbCasesReport.column.Total")));
			int i = 2;
			for (ConsumptionMonth rr : r.getCons()) {
				addDecimal(casesMed, i, blockRow, rr.getOldCases());
				addDecimal(casesMed, i, blockRow + 1, rr.getNewCases());
				addSumFormula(casesMed, i, blockRow, blockRow + 1, grandTotal);
				i++;
			}
			blockRow = blockRow + 3;
		}
		//grand total excluded in accordance with Issue # 901
		/*		addSubChapter(casesMed, 0, blockRow, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbCasesReport.column.Total")));
		casesMed.mergeCells(0, blockRow, 1, blockRow);
		for (Integer j : grandTotal.keySet()) {
			String s = grandTotal.get(j);
			s = s.substring(0, s.length() - 1);
			addFormula(casesMed, j, blockRow, s, true);
		}*/
	}

	/**
	 * cases by medicines header
	 * 
	 * @param sheet
	 * @param medicineConsumption
	 * @param factory
	 * @param details details information
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	private void createCasesMedHeader(WritableSheet sheet, MedicineConsumption medicineConsumption, ModelFactory factory, String details) throws RowsExceededException, WriteException {
		addLabel(sheet, 0, 0, details != null ? details : "");
		sheet.mergeCells(0, 0, medicineConsumption.getCons().size() + 1, 0);
		addCaption(sheet, 0, 1, Presenter.getMessage("ForecastingDocumentWindow.tbDetailedReport.medicine"), 30);
		sheet.mergeCells(0, 1, 1, 1);
		int i = 2;
		for (ConsumptionMonth mc : medicineConsumption.getCons()) {
			addCaption(sheet, i, 1, mc.getMonth().toString(), 0);
			i++;
		}

	}

	/**
	 * Regimens on treatment report
	 * 
	 * @param factory model factory
	 * @param referenceDate reference date
	 * @param details details information
	 * @param rres
	 * @throws IOException
	 * @throws WriteException
	 */
	public void createRegimensOnTreatment(List<ForecastingRegimenUIAdapter> reg, ModelFactory factory, Calendar referenceDate, String details) throws WriteException, IOException {
		WritableSheet regimens = this.getWorkbook().createSheet(cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbCasesReport.title.excel")), 3);
		createRegimensHeader(regimens, reg.get(0).getMonthsResults(referenceDate, factory), details);
		int blockRow = CONTENT_OFFSET;
		Map<Integer, String> grandTotal = new HashMap<Integer, String>(); //grand totals formulas by columns
		//content
		for (ForecastingRegimenUIAdapter r : reg) {
			addSubChapter(regimens, 0, blockRow, r.getRegimen().getName());
			regimens.mergeCells(0, blockRow, 0, blockRow + 2);
			addSubChapter(regimens, 1, blockRow, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbCasesReport.column.PrevCases")));
			addSubChapter(regimens, 1, blockRow + 1, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbCasesReport.column.NewCases")));
			addSubChapter(regimens, 1, blockRow + 2, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbCasesReport.column.Total")));
			int i = 2;
			for (ForecastingRegimenResultUIAdapter rr : r.getMonthsResults(referenceDate, factory)) {
				addDecimal(regimens, i, blockRow, rr.getEnrolled());
				addDecimal(regimens, i, blockRow + 1, rr.getExpected());
				addSumFormula(regimens, i, blockRow, blockRow + 1, grandTotal);
				i++;
			}
			blockRow = blockRow + 3;
		}
		//grand total
		addSubChapter(regimens, 0, blockRow, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbCasesReport.column.GrandTotal")));
		regimens.mergeCells(0, blockRow, 1, blockRow);
		for (Integer j : grandTotal.keySet()) {
			String s = grandTotal.get(j);
			s = s.substring(0, s.length() - 1);
			addFormula(regimens, j, blockRow, s, true);
		}

	}

	/**
	 * add sum formula to sheet
	 * 
	 * @param sheet worksheet
	 * @param col column index
	 * @param row1 begin row
	 * @param row2 end row
	 * @param grandTotal map contains grand totals formulas
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	private void addSumFormula(WritableSheet sheet, int col, int row1, int row2, Map<Integer, String> grandTotal) throws RowsExceededException, WriteException {
		String formula = "SUM(" + ALPHA[col] + (row1 + 1) + ":" + ALPHA[col] + (row2 + 1) + ")";
		int formulaRow = row2 + 1;
		addFormula(sheet, col, formulaRow, formula, false);
		String s = grandTotal.get(col);
		if (s == null) {
			s = "";
		}
		grandTotal.put(col, s + ALPHA[col] + (formulaRow + 1) + "+");
	}

	/**
	 * Create header for regimes on treatment table
	 * 
	 * @param sheet worksheet
	 * @param rres data list
	 * @param details details information
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	private void createRegimensHeader(WritableSheet sheet, List<ForecastingRegimenResultUIAdapter> rres, String details) throws RowsExceededException, WriteException {
		addLabel(sheet, 0, 0, details != null ? details : "");
		sheet.mergeCells(0, 0, rres.size() + 1, 0);
		addCaption(sheet, 0, 1, Presenter.getMessage("ForecastingDocumentWindow.tbCasesReport.column.TreatmentRegimen"), 30);
		sheet.mergeCells(0, 1, 1, 1);
		int i = 2;
		for (ForecastingRegimenResultUIAdapter rrU : rres) {
			addCaption(sheet, i, 1, rrU.getMonth().toString(), 0);
			i++;
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
	public void createConsumption(List<MedicineConsumption> cons, String details) throws WriteException, IOException {
		WritableSheet consumption = this.getWorkbook().createSheet(Presenter.getMessage("ForecastingDocumentWindow.tbMedicinesReport.title"), 2);
		int blockRow = CONTENT_OFFSET;
		createConsHeader(cons, consumption, details);
		for (MedicineConsumption mc : cons) {
			addSubChapter(consumption, 0, blockRow, mc.getMed().getNameForDisplay());
			consumption.mergeCells(0, blockRow, mc.getCons().size(), blockRow);
			addLabel(consumption, 0, blockRow + 1, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbMedicinesReport.column.StockOnHand")));
			addLabel(consumption, 0, blockRow + 2, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbMedicinesReport.column.QuantityMissing")));
			addLabel(consumption, 0, blockRow + 3, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbMedicinesReport.column.QuantityLostDue")));
			addLabel(consumption, 0, blockRow + 4, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbMedicinesReport.column.StockOnOrder")));
			addLabel(consumption, 0, blockRow + 5, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbMedicinesReport.column.ConsumptionPrev")));
			addLabel(consumption, 0, blockRow + 6, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbMedicinesReport.column.ConsumptionNew")));
			addLabel(consumption, 0, blockRow + 7, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbMedicinesReport.column.total")));
			int j = 1;
			for (ConsumptionMonth cm : mc.getCons()) {
				addInteger(consumption, j, blockRow + 1, cm.getOnHand().intValue());
				addInteger(consumption, j, blockRow + 2, cm.getMissing().intValue());
				addInteger(consumption, j, blockRow + 3, cm.getExpired());
				addInteger(consumption, j, blockRow + 4, cm.getOrder());
				addInteger(consumption, j, blockRow + 5, cm.getConsOld().intValue());
				addInteger(consumption, j, blockRow + 6, cm.getConsNew().intValue());
				addInteger(consumption, j, blockRow + 7, cm.getConsNew().add(cm.getConsOld()).intValue());
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
	private void createConsHeader(List<MedicineConsumption> cons, WritableSheet sheet, String details) throws RowsExceededException, WriteException {
		addLabel(sheet, 0, 0, details != null ? details : "");
		sheet.mergeCells(0, 0, cons.get(0).getCons().size(), 0);
		addCaption(sheet, 0, 1, Presenter.getMessage("ForecastingDocumentWindow.tbDetailedReport.medicine"), 30);
		int i = 1;
		MedicineConsumption mc = cons.get(0);
		for (ConsumptionMonth cm : mc.getCons()) {
			addCaption(sheet, i, 1, cm.getMonth().toString(), 0);
			i++;
		}

	}

	/**
	 * Create summary page on workbook
	 * 
	 * @param res list of medicines resume
	 * @param fcU forecasting
	 * @throws IOException
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	public void createSummary(List<MedicineResume> res, ForecastUIAdapter fcU) throws IOException, RowsExceededException, WriteException {
		// get or create the workbook
		WritableSheet summary = this.getWorkbook().createSheet(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.title"), 1);
		createSummaryHeader(summary, res, fcU);
		createSummaryContent(summary, res, fcU);
	}

	/**
	 * Create content in summary worksheet
	 * 
	 * @param summary worksheet
	 * @param res results
	 * @param fcU forecasting
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	private void createSummaryContent(WritableSheet summary, List<MedicineResume> res, ForecastUIAdapter fcU) throws RowsExceededException, WriteException {
		if (res != null) {
			int i = 3;
			int j=0;
			for (MedicineResume mr : res) {
				addLabel(summary, 0, i, mr.getMedicine().getNameForDisplay());
				addInteger(summary, 1, i, mr.getLeadPeriod().getIncomingBalance());
				Object[][] data = Presenter.getView().getActiveForecastingPanel().getSummaryPanel().getData();
				addInteger(summary, 2, i, toIntegerOrZero(((String) data[j][2]).replace(".COLOR","")));
				addCenterLabel(summary, 3, i, ((String) data[j][3]).replace(".COLOR",""));
				addInteger(summary, 4, i, mr.getLeadPeriod().getTransit());
				addInteger(summary, 5, i, mr.getLeadPeriod().getDispensedInt());
				addInteger(summary, 6, i, mr.getLeadPeriod().getExpired());
				addInteger(summary, 7, i, mr.getReviewPeriod().getIncomingBalance());
				addInteger(summary, 8, i, mr.getReviewPeriod().getTransit());
				addInteger(summary, 9, i, mr.getReviewPeriod().getExpired());
				addInteger(summary, 10, i, mr.getReviewPeriod().getConsumedOld().intValue());
				addInteger(summary, 11, i, mr.getReviewPeriod().getConsumedNew().intValue());
				addInteger(summary, 12, i, mr.getQuantityToProcured().intValue());
				addInteger(summary, 13, i, mr.getLeadPeriod().getMissing().intValue());
				addInteger(summary, 14, i, mr.getQuantityToProcured().add(mr.getLeadPeriod().getMissing()).intValue());

				/*
				 * addDecimal(summary, 12, i,0.000f); //don't know let user make
				 * input addCostFormula(summary, i, 11, 12, 13);
				 */
				i++;
				j++;
			}
		}

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
	 * create summary table header like in web version consume two lines at
	 * sheet top
	 * 
	 * @param summary
	 * @param resume medicine resume
	 * @param fcU forecasting
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	private void createSummaryHeader(WritableSheet summary, List<MedicineResume> resume, ForecastUIAdapter fcU) throws RowsExceededException, WriteException {
		//set height of first and second caption rows
		summary.setRowView(1, 700);
		summary.setRowView(2, 1500);
		addLabel(summary, 0, 0, fcU != null ? fcU.getDetailsInformationHTML() : "");
		summary.mergeCells(0, 0, 12, 0);


		addCaption(summary, 0, 1, Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.Medicine"), 35);
		summary.mergeCells(0, 1, 0, 2);

		String s = cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.StockOnHand"));
		addCaption(summary, 1, 1, s, 12);
		summary.mergeCells(1, 1, 1, 2);

		//TODO add new column
		s = cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.monthsOfStock"));
		addCaption(summary, 2, 1, s, 12);
		summary.mergeCells(2, 1, 2, 2);


		s = cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.nextProcurementDate"));
		addCaption(summary, 3, 1, s, 12);
		summary.mergeCells(3, 1, 3, 2);

		s = getLeadCaption(resume);
		addCaption(summary, 4, 1, s, 12);
		summary.mergeCells(4, 1, 6, 1);

		s = cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.StockOnOrder.plain"));
		addCaption(summary, 4, 2, s, 12);
		s = cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.DispensingQuantity"));
		addCaption(summary, 5, 2, s, 12);
		s = cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.QuantityLost"));
		addCaption(summary, 6, 2, s, 12);
		//review
		s = getReviewCaption(fcU);
		addCaption(summary, 7, 1, s, 11);
		summary.mergeCells(7, 1, 11, 1);

		s = cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.StockOnHandAfter"));
		addCaption(summary, 7, 2, s, 12);
		s = cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.StockOnOrder.plain"));
		addCaption(summary, 8, 2, s, 12);
		s = cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.QuantityLost"));
		addCaption(summary, 9, 2, s, 12);
		s = cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.EstimatedConsumptionPrev.plain"));
		addCaption(summary, 10, 2, s, 12);
		s = cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.EstimatedConsumptionNew"));
		addCaption(summary, 11, 2, s, 12);

		s= cleanStr(Messages.getString("ForecastingDocumentWindow.tbSummary.column.group.total"));
		addCaption(summary, 12, 1, s, 14);
		summary.mergeCells(12, 1, 14, 1);
		s = cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.column.EstimatedQuantity"));
		addCaption(summary, 12, 2, s, 12);
		s = cleanStr(Messages.getString("ForecastingDocumentWindow.tbSummary.column.accQuantity.plain"));
		addCaption(summary, 13, 2, s, 12);
		s = cleanStr(Messages.getString("ForecastingDocumentWindow.tbSummary.column.total"));
		addCaption(summary, 14, 2, s, 12);
		/*
		 * s = cleanStr(Presenter.getMessage(
		 * "ForecastingDocumentWindow.tbSummary.column.UnitPrice"));
		 * addCaption(summary,12,1,s, 0); s = cleanStr(Presenter.getMessage(
		 * "ForecastingDocumentWindow.tbSummary.column.TotalPrice"));
		 * addCaption(summary,13,1,s, 0);
		 */
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
			cap = cap + Messages.getString("ForecastingDocumentWindow.tbSummary.column.group.reviewPeriod") + " " + fcU.getStartReviewPeriodTxt() + "..." + fcU.getEndReviewPeriodTxt() + " " + fcU.getDurationOfReviewPeriodInDays();
		}
		String s = cleanStr(cap);
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
			cap = cap + " " + mr.getLeadPeriod().getFromTxt() + "..." + mr.getLeadPeriod().getToTxt() + " " + mr.getLeadPeriod().getDaysBetweenPeriodTxt();
		}
		String s = cleanStr(cap);
		return s;
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
	 * Create the first excel report page - general info
	 * 
	 * @param fcU
	 * @param isEnrolledPercentage TODO
	 * @param isExpectedPercentage TODO
	 * @throws IOException
	 * @throws WriteException
	 */
	public void createGeneral(ForecastUIAdapter fcU, boolean isEnrolledPercentage, boolean isExpectedPercentage) throws WriteException, IOException {
		WritableSheet general = this.getWorkbook().createSheet(Presenter.getMessage("ForecastingDocumentWindow.tbParameters.title"), 0);
		general.setColumnView(0, 30);
		int startRow = 0;
		startRow = generalUserInfo(fcU, general, startRow);
		startRow = generalForecastInfo(fcU, general, startRow);
		if (isEnrolledPercentage) {			
			startRow = generalCases(fcU, general, startRow, false);
			startRow = generalRegPerc(fcU, general, startRow, false);
		} else {
			startRow = generalCasesOnTreatment(fcU, general, startRow, false);
			startRow++;
		}
		if (isExpectedPercentage) {
			startRow = generalCases(fcU, general, startRow, true);
			startRow = generalRegPerc(fcU, general, startRow, true);
		} else {
			startRow = generalCasesOnTreatment(fcU, general, startRow, true);
			startRow++;
		}		
		startRow = generalBatches(fcU, general, startRow);
		startRow = generalOrders(fcU, general, startRow);

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
	private int generalOrders(ForecastUIAdapter fcU, WritableSheet general, int startRow) throws RowsExceededException, WriteException {
		addCaption(general, 0, startRow, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.stockOnOrder.plain"), 0);
		general.mergeCells(0, startRow, 9, startRow);
		addCaption(general, 0, startRow + 1, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.medicines.plain"), 0);
		addCaption(general, 1, startRow + 1, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.receivingDate.plain"), 0);
		addCaption(general, 2, startRow + 1, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.expirationDate"), 0);
		addCaption(general, 3, startRow + 1, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.quantity"), 0);
		general.mergeCells(4, startRow+1, 9, startRow+1);
		addCaption(general, 4, startRow + 1, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.comment"), 0);
		int i = startRow + 2;
		int totalOrd = 0;
		for (ForecastingMedicineUIAdapter med : fcU.getMedicines()) {
			addSubChapter(general, 0, i, med.getMedicine().getNameForDisplay());
			//general.mergeCells(0, i, 2, i);
			int medStockOrd = med.getStockOnOrderInt();
			//addInteger(general, 3, i, medStockOrd);
			addTotal(general, 3, i, medStockOrd);
			totalOrd = totalOrd + medStockOrd;
			i++;
			for (ForecastingOrderUIAdapter ord : med.getOrders()) {
				addDate(general, 1, i, ord.getArrivedDt());
				Date exp = ord.getBatch().getExpiredDtEdit();
				if (exp != null) {
					addDate(general, 2, i, exp);
				} else {
					addLabel(general, 2, i, "");
				}
				addInteger(general, 3, i, ord.getBatch().getQuantity());
				general.mergeCells(4, i, 9, i);
				addLabel(general, 4, i, ord.getComment());
				i++;
			}
		}
		/*addSubChapter(general, 0, i, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.excel.totalorder"));
		general.mergeCells(0, i, 2, i);
		addTotal(general, 3, i, totalOrd);
		i++;*/
		return i + 1;
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
	private int generalBatches(ForecastUIAdapter fcU, WritableSheet general, int startRow) throws RowsExceededException, WriteException {
		addCaption(general, 0, startRow, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.stockOnHand.plain"), 0);
		general.mergeCells(0, startRow, 8, startRow);
		addCaption(general, 0, startRow + 1, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.medicines.plain"), 0);
		addCaption(general, 1, startRow + 1, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.expirationDate"), 0);
		addCaption(general, 2, startRow + 1, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.quantity"), 0);
		general.mergeCells(3, startRow+1, 8, startRow+1);
		addCaption(general, 3, startRow + 1, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.comment"), 0);
		int i = startRow + 2;
		int totalBatches = 0;
		for (ForecastingMedicineUIAdapter med : fcU.getMedicines()) {
			addSubChapter(general, 0, i, med.getMedicine().getNameForDisplay());
			//general.mergeCells(0, i, 1, i);
			int totalBatchesMed = med.getBatchesToExpireInt();
			addTotal(general, 2, i, totalBatchesMed);
			totalBatches = totalBatches + totalBatchesMed;
			i++;
			for (ForecastingBatchUIAdapter bt : med.getBatchesToExpire()) {
				addDate(general, 1, i, bt.getExpiredDtEdit());
				addInteger(general, 2, i, bt.getQuantity());
				general.mergeCells(3, i, 8, i);
				addLabel(general, 3, i, bt.getComment());
				i++;
			}
		}
		/*addSubChapter(general, 0, i, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.excel.totalbatches"));
		general.mergeCells(0, i, 1, i);
		addTotal(general, 2, i, totalBatches);
		i++;*/
		return i + 1;
	}

	/**
	 * out regimen - percents table
	 * 
	 * @param fcU
	 * @param general
	 * @param startRow
	 * @param isNewCases TODO
	 * @return
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	private int generalRegPerc(ForecastUIAdapter fcU, WritableSheet general, int startRow, boolean isNewCases) throws RowsExceededException, WriteException {
		addCaption(general, 0, startRow, Presenter.getMessage("Regimen.clmn.Regimen"), 0);
		addCaption(general, 1, startRow, Presenter.getMessage(isNewCases?"ForecastingDocumentWindow.tbParameters.SubTab.NewCases.percentClmn":"ForecastingDocumentWindow.tbParameters.SubTab.CasesOnTreatment.percentClmn"), 0);
		int i = startRow + 1;
		for (ForecastingRegimenUIAdapter fr : fcU.getRegimes()) {
			addLabel(general, 0, i, fr.getRegimen().getNameWithForDisplay());
			String string = isNewCases?fr.getPercentNewCases().toString():fr.getPercentCasesOnTreatment().toString();
			addDecimal(general, 1, i, new BigDecimal(string));
			i++;
		}
		return i + 1;
	}

	/**
	 * new cases subtable
	 * 
	 * @param fcU
	 * @param general
	 * @param startRow
	 * @param isNewCases TODO
	 * @return
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	private int generalCases(ForecastUIAdapter fcU, WritableSheet general, int startRow, boolean isNewCases) throws RowsExceededException, WriteException {
		addCaption(general, 0, startRow, Presenter.getMessage(isNewCases?"ForecastingDocumentWindow.tbParameters.SubTab.NewCases.title":"ForecastingDocumentWindow.tbParameters.SubTab.CasesOnTreatment.title"), 0);
		general.mergeCells(0, startRow, 10, startRow);
		int j = 1;
		addSubChapter(general, 0, startRow + 1, "");
		addSubChapter(general, 0, startRow + 2, "");
		for (MonthQuantityUIAdapter mq : isNewCases?fcU.getNewCases():fcU.getCasesOnTreatment()) {
			addDateMY(general, j, startRow + 1, mq.getMonth().getAnyDate(1).getTime());
			addInteger(general, j, startRow + 2, mq.getIQuantity());
			j++;
			if (j > 10) {
				startRow = startRow + 2;
				addSubChapter(general, 0, startRow + 1, "");
				addSubChapter(general, 0, startRow + 2, "");
				j = 1;
			}
		}
		return startRow + 4;
	}

	/**
	 * cases on treatment table
	 * 
	 * @param fcU
	 * @param general
	 * @param startRow
	 * @param isNewCases TODO
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	private int generalCasesOnTreatment(ForecastUIAdapter fcU, WritableSheet general, int startRow, boolean isNewCases) throws RowsExceededException, WriteException {
		addCaption(general, 0, startRow, Presenter.getMessage(isNewCases?"ForecastingDocumentWindow.tbParameters.SubTab.NewCases.title":"ForecastingDocumentWindow.tbParameters.SubTab.CasesOnTreatment.title"), 0);
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
		general.mergeCells(0, startRow, monthCount, startRow);
		List<MonthQuantityUIAdapter> cases = isNewCases?fR.getNewCases():fR.getCasesOnTreatment();
		for (MonthQuantityUIAdapter mq : cases) {
			addDateMY(general, j, i + 1, mq.getMonth().getAnyDate(1).getTime());
			j++;
		}
		for (ForecastingRegimenUIAdapter fr : fcU.getRegimes()) {
			addSubChapter(general, 0, i + 2, fr.getRegimen().getNameWithForDisplay());
			//j = isNewCases?1:(monthCount + 1 - fr.getRegimen().getContinious().getDurationInMonths() - fr.getRegimen().getIntensive().getDurationInMonths());
			j = isNewCases?1:(monthCount + 1 - fr.getFcRegimenObj().getCasesOnTreatment().size());//.getRegimen().getContinious().getDurationInMonths() - fr.getRegimen().getIntensive().getDurationInMonths());
			for (MonthQuantityUIAdapter mq : isNewCases?fr.getNewCases():fr.getCasesOnTreatment()) {
				addInteger(general, j, i + 2, mq.getIQuantity());
				j++;
			}
			i++;
		}
		return i + 2;
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
	private int generalForecastInfo(ForecastUIAdapter fcU, WritableSheet general, int startRow) throws RowsExceededException, WriteException {
		addSubChapter(general, 0, startRow, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.referenceDate"));
		addDate(general, 1, startRow, fcU.getReferenceDt());  //20160825 Appropriate
		general.mergeCells(1, startRow, 3, startRow);
		addSubChapter(general, 0, startRow + 1, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.leadTime"));
		addInteger(general, 1, startRow + 1, fcU.getLeadTime());
		general.mergeCells(2, startRow + 1, 3, startRow + 1);
		addCenterLabel(general, 2, startRow + 1, DateParser.getMonthLabel(fcU.getLeadTime()));
		addSubChapter(general, 0, startRow + 2, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.reviewPeriod"));
		addDate(general, 1, startRow + 2, fcU.getIniDt());
		addCenterLabel(general, 2, startRow + 2, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.until"));
		addDate(general, 3, startRow + 2, fcU.getEndDt());
		/*addSubChapter(general, 0, startRow + 3, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.bufferStock"));
		addInteger(general, 1, startRow + 3, fcU.getBufferStockTime()); 20160905 no more buffer stock*/
		/*general.mergeCells(2, startRow + 3, 3, startRow + 3);
		addCenterLabel(general, 2, startRow + 3, DateParser.getMonthLabel(fcU.getBufferStockTime()));*/

		addSubChapter(general, 0, startRow + 3, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.minstock"));
		this.addInteger(general, 1,  startRow + 3, fcU.getMinStock());
		general.mergeCells(2, startRow + 3, 3, startRow + 4);
		addCenterLabel(general, 2, startRow + 4, DateParser.getMonthLabel(fcU.getMinStock()));

		addSubChapter(general, 0, startRow + 4, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.maxstock"));
		this.addInteger(general, 1,  startRow + 4, fcU.getMaxStock());
		general.mergeCells(2, startRow + 4, 3, startRow + 4);
		addCenterLabel(general, 2, startRow + 4, DateParser.getMonthLabel(fcU.getMaxStock()));
		return startRow + 6;
	}

	/**
	 * User info
	 * 
	 * @param fcU
	 * @param general
	 * @param startRow
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	private int generalUserInfo(ForecastUIAdapter fcU, WritableSheet general, int startRow) throws RowsExceededException, WriteException {
		addSubChapter(general, 0, startRow, Presenter.getMessage("Forecasting.name"));
		addLabel(general, 1, startRow, fcU.getName());
		general.mergeCells(1, startRow, 3, startRow);
		addSubChapter(general, 4, startRow, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.comment"));
		general.mergeCells(4, startRow, 5, startRow);
		addLabel(general, 6, startRow, fcU.getComment());
		general.mergeCells(6, startRow, 13, startRow);
		addSubChapter(general, 0, startRow + 1, Presenter.getMessage("DlgForecastingWizard.address.label"));
		addLabel(general, 1, startRow + 1, fcU.getAddress());
		general.mergeCells(1, startRow + 1, 3, startRow + 1);
		addSubChapter(general, 0, startRow + 2, Presenter.getMessage("Forecasting.general.saved"));
		if (fcU.getForecastObj().getRecordingDate() != null) {
			addDate(general, 1, startRow + 2, fcU.getForecastObj().getRecordingDate().toGregorianCalendar().getTime());
		}
		general.mergeCells(1, startRow + 2, 3, startRow + 2);
		addSubChapter(general, 0, startRow + 3, Presenter.getMessage("ForecastingDocumentWindow.tbParameters.calculator"));
		addLabel(general, 1, startRow + 3, fcU.getCalculator());
		general.mergeCells(1, startRow + 3, 3, startRow + 3);
		return startRow + 4;
	}

	/**
	 * Create one medicine resume
	 * 
	 * @param med medicine
	 * @param res list of period resume
	 * @param i sequence number of result to correctly calculate Excel tab
	 *        number
	 * @param details details information
	 * @throws IOException
	 * @throws WriteException
	 */
	public void createMedicinesResume(MedicineUIAdapter med, List<PeriodResume> res, int i, String details) throws WriteException, IOException {
		String medicineNameWithEllipsis = (med.getNameForDisplayWithAbbrev().length() > 31) ? (med.getNameForDisplayWithAbbrev().substring(0, 28).concat("...")) : med.getNameForDisplayWithAbbrev();
		medicineNameWithEllipsis = medicineNameWithEllipsis.replaceAll("/", " ");
		WritableSheet resume = this.getWorkbook().createSheet(medicineNameWithEllipsis, i + 5);
		addLabel(resume, 0, 0, details != null ? details : "");
		resume.mergeCells(0, 0, 7, 0);
		addCaption(resume, 0, 1, med.getNameForDisplayWithAbbrev(), 0);
		resume.mergeCells(0, 1, 7, 1);
		int offset = 2;
		addCaption(resume, 0, offset, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbDetailedReport.column.period")), 40);
		addCaption(resume, 1, offset, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.stockOnHand.plain")), 0);
		addCaption(resume, 2, offset, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbDetailedReport.column.EstimatedConsumptionPrev")), 20);
		addCaption(resume, 3, offset, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbDetailedReport.column.EstimatedConsumptionNew")), 20);
		addCaption(resume, 4, offset, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbDetailedReport.column.EstimatedConsumptionTotal")), 20);
		addCaption(resume, 5, offset, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.stockOnOrder.plain")), 20);
		addCaption(resume, 6, offset, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbDetailedReport.column.QuantityLost")), 20);
		addCaption(resume, 7, offset, cleanStr(Presenter.getMessage("ForecastingDocumentWindow.tbDetailedReport.column.QuantityMissing")), 20);

		int j = offset + 1;
		for (PeriodResume pr : res) {
			addLabel(resume, 0, j, pr.getFromTxt() + "..." + pr.getToTxt() + " " + pr.getDaysBetweenPeriodTxt());
			addInteger(resume, 1, j, pr.getIncomingBalance());
			addInteger(resume, 2, j, pr.getConsumedOld().intValue());
			addInteger(resume, 3, j, pr.getConsumedNew().intValue());
			addInteger(resume, 4, j, pr.getConsumedNew().intValue() + pr.getConsumedOld().intValue());
			addInteger(resume, 5, j, pr.getTransit());
			addInteger(resume, 6, j, pr.getExpired());
			addInteger(resume, 7, j, pr.getMissing().intValue());
			j++;
		}
	}

	/**
	 * Create the forecasting order
	 * 
	 * @param total information for order
	 * @param totalR TODO
	 * @param totalA TODO
	 * @param index sheet number
	 * @throws IOException
	 * @throws WriteException
	 */
	public void createOrder(ForecastingTotal total, ForecastingTotal totalR, ForecastingTotal totalA, int index) throws WriteException, IOException {
		WritableSheet order = this.getWorkbook().createSheet(Presenter.getMessage("ForecastingDocumentWindow.order.titleorder.excelsheet"), index);
		createOrderData(total, order);
		if (total.getMedTotal().compareTo(new BigDecimal(0)) != 0){
			WritableSheet orderTotal = this.getWorkbook().createSheet(Presenter.getMessage("ForecastingDocumentWindow.order.totalTabName"), index + 1);
			WritableSheet orderTotalR = this.getWorkbook().createSheet(Presenter.getMessage("ForecastingDocumentWindow.order.regularTabName"), index + 2);
			WritableSheet orderTotalA = this.getWorkbook().createSheet(Presenter.getMessage("ForecastingDocumentWindow.order.accelTabName"), index + 3);
			createOrderTotal(total, orderTotal);
			createOrderTotal(totalR, orderTotalR);
			createOrderTotal(totalA, orderTotalA);
		}
	}

	/**
	 * Create order total page
	 * 
	 * @param total data to display
	 * @param orderTotal page to place data
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	private void createOrderTotal(ForecastingTotal total, WritableSheet orderTotal) throws RowsExceededException, WriteException {
		int currentRow = 1;
		addSubChapter(orderTotal, 0, currentRow, Presenter.getMessage("ForecastingDocumentWindow.order.submedtotal"));
		addDecimalTotal(orderTotal, 1, currentRow, total.getMedTotal());
		currentRow = currentRow + 3;
		int col =0;
		addCaption(orderTotal, col++, currentRow, Presenter.getMessage("ForecastingDocumentWindow.tbSummary.total.item.plain"), 60);
		if(!total.isGrandTotal()){
			addCaption(orderTotal, col++, currentRow, Presenter.getMessage("ForecastingDocumentWindow.tbSummary.total.percentage.plain"), 15);
		}
		addCaption(orderTotal, col, currentRow, Presenter.getMessage("ForecastingDocumentWindow.tbSummary.total.cost.plain"), 15);
		currentRow++;
		for (ForecastingTotalItemUIAdapter item : total.getAddItems()) {
			col=0;
			addLabel(orderTotal, col++, currentRow, item.getItem());
			if(!total.isGrandTotal()){
				addDecimalNoZero(orderTotal, col++, currentRow, item.getPerCents());
			}
			addDecimalNoZero(orderTotal, col, currentRow, item.getValue());
			currentRow++;
		}
		currentRow = currentRow + 3;
		String s = Presenter.getMessage("ForecastingDocumentWindow.order.subordertotal");
		if(total.isGrandTotal()){
			s = Presenter.getMessage("ForecastingDocumentWindow.order.grandtotal");
		}
		addSubChapter(orderTotal, 0, currentRow, s );
		addDecimalTotal(orderTotal, 1, currentRow, total.getCostOrderTotal());

		int commentRow = currentRow + 5;
		addSubChapter(orderTotal, 0, commentRow, Presenter.getMessage("ForecastingDocumentWindow.order.commentlbl"));
		addLabel(orderTotal, 0, commentRow + 1, total.getForecastUI().getTotalComment2());
	}

	/**
	 * Create order data page
	 * 
	 * @param total data to display
	 * @param order page to place data
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	private void createOrderData(ForecastingTotal total, WritableSheet order) throws RowsExceededException, WriteException {
		int i = createRegularOrderData(total, order);
		i = createAccelOrderData(total, order,i+1);
		i = createTotalOrderData(total, order, i+1);
		int totalRow = i + 3;
		addSubChapter(order, 0, totalRow, Presenter.getMessage("ForecastingDocumentWindow.order.grandtotal"));
		addDecimalTotal(order, 1, totalRow, total.getMedTotal());
		int commentRow = i + 5;
		addSubChapter(order, 0, commentRow, Presenter.getMessage("ForecastingDocumentWindow.order.commentlbl"));
		addLabel(order, 0, commentRow + 1, total.getForecastUI().getTotalComment1());
	}


	/**
	 * Create Excel data for the total order
	 * @param total data for display
	 * @param order writable sheet for data
	 * @param startRow
	 * @return
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	private int createTotalOrderData(ForecastingTotal total,
			WritableSheet order, int startRow) throws RowsExceededException, WriteException {
		addSubChapter(order, 0, startRow, Presenter.getMessage("ForecastingDocumentWindow.order.total"));
		int headRow = startRow  +1;
		addCaption(order, 0, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.medicines"), 60);
		addCaption(order, 1, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.quantityneeded"), 15);
		addCaption(order, 2, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.quantityadjusted"), 15);
		/*addCaption(order, 3, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.packajusted"), 15); */
		if (total.getMedTotal().compareTo(BigDecimal.ZERO) > 0){
			addCaption(order, 3, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.totalcost"), 15);
		}

		int i = startRow+2;
		for (ForecastingTotalMedicine tmed : total.getMedItems()) {
			addLabel(order, 0, i, tmed.getMedicine().getNameForDisplayWithAbbrev());
			addNoZero(order, 1, i, tmed.getBruttoQuant());
			addNoZero(order, 2, i, tmed.getTotal());
			/*addNoZero(order, 3, i, tmed.getTotalPack()); */
			if (total.getMedTotal().compareTo(BigDecimal.ZERO) > 0){
				addDecimalNoZero(order, 3, i, tmed.getTotalCost());
			}
			i++;
		}
		return i;
	}

	/**
	 * Create Excel data for the accelerated order
	 * @param total data to display
	 * @param order writable sheet for data
	 * @param startRow row "from" on the writable sheet
	 * @return
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	private int createAccelOrderData(ForecastingTotal total,
			WritableSheet order, int startRow) throws RowsExceededException, WriteException {
		addSubChapter(order, 0, startRow, Presenter.getMessage("ForecastingDocumentWindow.order.accel"));
		int headRow = startRow  +1;
		addCaption(order, 0, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.medicines"), 60);
		addCaption(order, 1, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.quantityneeded"), 15);
		addCaption(order, 2, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.adjcoef"), 15);
		addCaption(order, 3, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.packsize"), 15);
		addCaption(order, 4, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.packprice"), 15);
		addCaption(order, 5, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.quantityadjusted"), 15);
		addCaption(order, 6, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.packajusted"), 15);
		addCaption(order, 7, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.accordcost"), 15);
		addCaption(order, 8, headRow, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.accorddate"), 15);

		int i = startRow+2;
		for (ForecastingTotalMedicine tmed : total.getMedItems()) {
			addLabel(order, 0, i, tmed.getMedicine().getNameForDisplayWithAbbrev());
			addNoZero(order, 1, i, tmed.getAccelQuant());
			addDecimalNoZero(order, 2, i, tmed.getAdjustItAccel());
			addNoZero(order, 3, i, tmed.getPackSizeAccel());
			addDecimalNoZero(order, 4, i, tmed.getPackPriceAccel());
			addNoZero(order, 5, i, tmed.getAdjustAccel());
			addNoZero(order, 6, i, tmed.getAdjustedAccelPack());
			addDecimalNoZero(order, 7, i, tmed.getAccelCost());
			addDateNoZero(order, 8, i, tmed.getAccelDate());
			i++;
		}
		return i;
	}

	/**
	 * Create Excel data for the Regular order
	 * @param total
	 * @param order
	 * @return
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	private int createRegularOrderData(ForecastingTotal total,
			WritableSheet order) throws RowsExceededException, WriteException {
		addSubChapter(order, 0, 0, Presenter.getMessage("ForecastingDocumentWindow.order.regular"));
		addCaption(order, 0, 1, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.medicines"), 60);
		addCaption(order, 1, 1, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.quantityneeded"), 15);
		addCaption(order, 2, 1, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.adjcoef"), 15);
		addCaption(order, 3, 1, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.packsize"), 15);
		addCaption(order, 4, 1, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.packprice"), 15);
		addCaption(order, 5, 1, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.quantityadjusted"), 15);
		addCaption(order, 6, 1, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.packajusted"), 15);
		addCaption(order, 7, 1, Presenter.getMessage("ForecastingDocumentWindow.order.ordcolumns.regordcost"), 15);
		int i = 2;
		for (ForecastingTotalMedicine tmed : total.getMedItems()) {
			addLabel(order, 0, i, tmed.getMedicine().getNameForDisplayWithAbbrev());
			addNoZero(order, 1, i, tmed.getRegularQuant());
			addDecimalNoZero(order, 2, i, tmed.getAdjustIt());
			addNoZero(order, 3, i, tmed.getPackSize());
			addDecimalNoZero(order, 4, i, tmed.getPackPrice());
			addNoZero(order, 5, i, tmed.getAdjustedRegular());
			addNoZero(order, 6, i, tmed.getAdjustedRegularPack());
			addDecimalNoZero(order, 7, i, tmed.getRegularCost());
			/*
			addNoZero(order, 13, i, tmed.getTotal());
			addNoZero(order, 14, i, tmed.getTotalPack());
			addDecimalNoZero(order, 15, i, tmed.getTotalCost());*/
			i++;
		}
		return i;
	}

	/**
	 * Write date to cell. If date is null, write dash
	 * 
	 * @param sheet
	 * @param col
	 * @param row
	 * @param dt
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	private void addDateNoZero(WritableSheet sheet, int col, int row, Date dt) throws RowsExceededException, WriteException {
		if (dt == null) {
			addCenterLabel(sheet, col, row, "-");
		} else {
			addDate(sheet, col, row, dt);
		}

	}

	/**
	 * write decimal to cell. If decimal zero, write dash
	 * 
	 * @param order
	 * @param col
	 * @param row
	 * @param fnum
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	private void addDecimalNoZero(WritableSheet order, int col, int row, BigDecimal fnum) throws RowsExceededException, WriteException {
		if (fnum.equals(new BigDecimal(0))) {
			addCenterLabel(order, col, row, "-");
		} else {
			addDecimal(order, col, row, fnum);
		}
	}

	/**
	 * write Integer to cell. If Integer zero, write dash
	 * 
	 * @param order sheet to place
	 * @param col column
	 * @param row
	 * @param regularQuant
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	private void addNoZero(WritableSheet order, int col, int row, Integer regularQuant) throws RowsExceededException, WriteException {
		if (regularQuant == 0) {
			addCenterLabel(order, col, row, "-");
		} else {
			addInteger(order, col, row, regularQuant);
		}
	}
}
