package org.msh.quantb.services.excel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.msh.quantb.model.forecast.Forecast;
import org.msh.quantb.model.gen.RegimenTypesEnum;
import org.msh.quantb.model.mvp.ModelFactory;
import org.msh.quantb.services.calc.ForecastingCalculation;
import org.msh.quantb.services.calc.MedicineConsumption;
import org.msh.quantb.services.calc.MedicineResume;
import org.msh.quantb.services.calc.OrderCalculator;
import org.msh.quantb.services.calc.PeriodResume;
import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.ForecastingMedicineUIAdapter;
import org.msh.quantb.services.io.ForecastingTotal;
import org.msh.quantb.services.mvp.Presenter;

/**
 * This class intends to try export to excel tips and tricks
 * Real export tests in TestForecastingCalculation
 * @author alexey
 *
 */
public class TestExcel extends TestCase{
	public static final String testPath = "src/test/resources";

	private static final String TEST_FORECASTING ="TestCasesQuantity.qtb";
	private static final String TEST_EXCEL_NAME ="TestCasesQuantity";
	private static final String TEST_FORECASTING_NAME2 ="verylong.qtb";
	
	private static ModelFactory model = new ModelFactory(testPath);
	public static final String testDocPath = "src\\test\\resources\\doc\\byJUnit\\";
	
	/**
	 * This is a general test for quick found solutions and study purpose
	 */
	public void testGeneral(){
		ExportExcel test = new ExportExcel();
		test.setOutputFile(testPath + "\\general.xls");
		try {
			test.createSummary(null, null);
			test.save();
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Test excel clipboard receiving
	 */
	public void testClipBoard(){
		Integer[][] result = ClipBoard.getQuantities();
		if (result != null){
			for(int i=0 ; i< result.length; i++){
				for(int j=0; j<result[i].length;j++){
					System.out.println("["+i+","+j+"] " + result[i][j]);
				}
			}
		}
	}
	
	/**
	 * load test file by name
	 * @param nameFile - name file
	 * @return ForecastUIAdapter
	 */
	private ForecastUIAdapter loadTestFile(String nameFile) {
		try {
			Forecast fr = model.readForecasting(testDocPath + nameFile);
			return new ForecastUIAdapter(fr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}
/*	public void testXLSX(){
		System.out.println("start " + new Date());
		ForecastUIAdapter fUi = loadTestFile(TEST_FORECASTING_NAME2);
		Presenter.setFactory(model);
		ForecastingCalculation fc = new ForecastingCalculation(fUi.getForecastObj(), model);
		// imitate execute method
		fc.clearResults();
		fc.calcCasesOnTreatment();
		fc.calcNewCases();
		fc.calcMedicinesRegimes();
		fc.adjustResults();
		fc.calcMedicines();
		fc.calcMedicinesResults();
		List<MedicineResume> res = fc.getResume();
		List<MedicineConsumption> cons = fc.getMedicineConsumption();
		OrderCalculator oC = new OrderCalculator(fc);
		oC.execute();
		//Excel
		System.out.println("start excel" + new Date());
		ExportXLSX excel = new ExportXLSX(new File(testPath + "/doc/test.xlsx"));
		excel.createGeneral(fUi);
		//System.out.println("excel general list" + new Date());
		excel.createSummary(res, cons, fUi, oC);
		//System.out.println("excel summary list" + new Date());
		String details = fUi.getDetailsInformationTxt();
		excel.createConsumption(cons, details);
		//System.out.println("excel Consumption list" + new Date());
		if (fUi.getRegimensType() == RegimenTypesEnum.MULTI_DRUG){
			excel.createRegimensOnTreatment(fUi.getRegimes(), fUi.getFirstFCDate(), model, details);
			//System.out.println("excel RegimensOnTreatment list " + new Date());
		}
		excel.createMedicinesOnTreatment(cons, model, details);
		//System.out.println("excel MedicinesOnTreatment list " + new Date());

		ForecastingTotal total = new ForecastingTotal(fUi, oC.getMedicineTotals(), ForecastingTotal.ALL_TOTAL);
		ForecastingTotal totalR = new ForecastingTotal(fUi, oC.getMedicineTotals(), ForecastingTotal.REGULAR_TOTAL);
		ForecastingTotal totalA = new ForecastingTotal(fUi, oC.getMedicineTotals(), ForecastingTotal.ACCEL_TOTAL);
		excel.createOrder(total, totalR, totalA);
		//System.out.println("excel calcMedicineResume list " + new Date());
		for (ForecastingMedicineUIAdapter med : fUi.getMedicines()) {
			List<PeriodResume> pr = fc.calcMedicineResume(med.getMedicine());
			//System.out.println("excel calcMedicineResume " + new Date());
			excel.createMedicinesResume(med.getMedicine(), pr, details);
			//System.out.println("excel createMedicinesResume " + new Date());
		}
		System.out.println("excel calculated " + new Date());
		try {
			excel.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("finish " + new Date());
		
	}*/
	
	public void testWHO(){
		ForecastUIAdapter fUi = loadTestFile(TEST_FORECASTING);
		Presenter.setFactory(model);
		ForecastingCalculation fc = new ForecastingCalculation(fUi.getForecastObj(), model);
		// imitate execute method
		fc.clearResults();
		fc.calcCasesOnTreatment();
		fc.calcNewCases();
		fc.calcMedicinesRegimes();
		fc.adjustResults();
		fc.calcMedicines();
		fc.calcMedicinesResults();

		List<MedicineResume> res = fc.getResume();
		List<MedicineConsumption> cons = fc.getMedicineConsumption();
		
		ExportExcelWHO report = new ExportExcelWHO();
		String filePath = testDocPath + "\\" + TEST_EXCEL_NAME;
		if (!filePath.endsWith(".xls")) {
			filePath = filePath + ".xls";
		}
		report.setOutputFile(filePath);
		try {
			report.createReport(fc);
			report.save();
		} catch (WriteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void testWHO_XLSX(){
		ForecastUIAdapter fUi = loadTestFile(TEST_FORECASTING);
		Presenter.setFactory(model);
		ForecastingCalculation fc = new ForecastingCalculation(fUi.getForecastObj(), model);
		// imitate execute method
		fc.clearResults();
		fc.calcCasesOnTreatment();
		fc.calcNewCases();
		fc.calcMedicinesRegimes();
		fc.adjustResults();
		fc.calcMedicines();
		fc.calcMedicinesResults();

		List<MedicineResume> res = fc.getResume();
		List<MedicineConsumption> cons = fc.getMedicineConsumption();
		
		
		ExportExcelWHO_XLSX excel = new ExportExcelWHO_XLSX(new File( testDocPath + "\\" + TEST_EXCEL_NAME + ".xlsx"));
		excel.createReport(fc);
		try {
			excel.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
