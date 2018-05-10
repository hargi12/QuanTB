package org.msh.quantb.services.excel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBException;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.msh.quantb.model.forecast.Forecast;
import org.msh.quantb.model.mvp.ModelFactory;
import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.mvp.Messages;

import junit.framework.TestCase;

public class TestImportExcel extends TestCase {
	public static final String testPath = "src/test/resources/doc/byJUnit/ReportStock.xlsx";
	public static final String testPathOrders = "src/test/resources/doc/byJUnit/ReportOrders.xlsx";
	public static final String testPathBoth = "src/test/resources/doc/byJUnit/ReportStockOrders.xlsx";
	public static final String testPathTempl = "src/test/resources/doc/byJUnit/StockFromTempl.xlsx";
	public static final String testPathTemplBoth = "src/test/resources/doc/byJUnit/StockFromTemplBoth.xlsx";
	public static final String testPathTemplOrder = "src/test/resources/doc/byJUnit/StockFromTemplOrder.xlsx";
	public static final String templPath = "src/test/resources/StockTemplate.xlsx";
	public static final String docPath = "src/test/resources/doc/Stocks.xlsx";
	public static final String testDocPath = "src/test/resources/doc/byJUnit/";
	public static final String testResPath = "src/test/resources";
	private static ModelFactory model = new ModelFactory(testResPath);
	
	/**
	 * Test read medicines list
	 */
	public void testOnlyStock(){
		File excelFile = new File(testPath);
		ImportExcel ie = new ImportExcel(excelFile);
		String err = ie.importMedStock();
		if (err.length() == 0){
			List<ImportExcelDTO> result = ie.getResult();
			assertEquals(6, result.size());
			assertEquals(result.get(4).getQuantity().intValue(), 300);
//			System.out.println(result);
		}else{
			System.out.println(err);
			assertTrue(false);
		}
	}
	
	/**
	 * Test read medicines list
	 */
	public void testOnlyOrders(){
		File excelFile = new File(testPathOrders);
		ImportExcel ie = new ImportExcel(excelFile);
		String err = ie.importMedStock();
		if (err.length() == 0){
			List<ImportExcelDTO> result = ie.getResult();
			assertEquals(3, result.size());
			assertEquals(result.get(1).getOrderQuantity().intValue(), 300);
//			System.out.println(result);
		}else{
			System.out.println(err);
			assertTrue(false);
		}
	}
	
	public void testStocksAndOrders(){
		File excelFile = new File(testPathBoth);
		ImportExcel ie = new ImportExcel(excelFile);
		String err = ie.importMedStock();
		if (err.length() == 0){
			List<ImportExcelDTO> result = ie.getResult();
			assertEquals(10, result.size());
			assertEquals(result.get(1).getQuantity().intValue(), 150);
			assertEquals(result.get(7).getOrderQuantity().intValue(), 300);
//			System.out.println(result);
		}else{
			System.out.println(err);
			assertTrue(false);
		}
	}
	
	public void testStockFromTempl(){
		File excelFile = new File(testPathTempl);
		ImportExcel ie = new ImportExcel(excelFile);
		String err = ie.importMedStock();
		if (err.length() == 0){
			List<ImportExcelDTO> result = ie.getResult();
			assertEquals(20, result.size());
			assertEquals(result.get(3).getQuantity().intValue(), 2701440);
//			System.out.println(result);
		}else{
			System.out.println(err);
			assertTrue(false);
		}
	}
	
	public void testStockOrderFromTempl(){
		File excelFile = new File(testPathTemplBoth);
		ImportExcel ie = new ImportExcel(excelFile);
		String err = ie.importMedStock();
		if (err.length() == 0){
			List<ImportExcelDTO> result = ie.getResult();
			assertEquals(22, result.size());
			assertEquals(result.get(8).getQuantity().intValue(), 80028269);
			assertEquals(result.get(14).getOrderQuantity().intValue(), 300);
//			System.out.println(result);
		}else{
			System.out.println(err);
			assertTrue(false);
		}
	}
	
	public void testOrderFromTempl(){
		File excelFile = new File(testPathTemplOrder);
		ImportExcel ie = new ImportExcel(excelFile);
		String err = ie.importMedStock();
		if (err.length() == 0){
			List<ImportExcelDTO> result = ie.getResult();
			assertEquals(4, result.size());
			assertEquals(result.get(2).getOrderQuantity().intValue(), 500);
//			System.out.println(result);
		}else{
			System.out.println(err);
			assertTrue(false);
		}
	}
	
	
	public void testCreateImportTemplate(){
		Locale.setDefault(new Locale("ru", "RU"));
		Messages.setCountry("RU");
		Messages.setLanguage("ru");
		Messages.reloadBundle();
		TemplateImport template = new TemplateImport(templPath);
		try {
			XSSFWorkbook workbook = template.create(loadTestFile("Fictitia 1-1-2013 slow.qtb"));
			template.saveWorkBook(workbook, docPath);
			
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
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
}
