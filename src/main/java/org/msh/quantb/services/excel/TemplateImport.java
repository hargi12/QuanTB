package org.msh.quantb.services.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFName;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.msh.quantb.services.calc.DateUtils;
import org.msh.quantb.services.calc.MedicineBuffer;
import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.ForecastingBatchUIAdapter;
import org.msh.quantb.services.io.ForecastingMedicineUIAdapter;
import org.msh.quantb.services.io.ForecastingOrderUIAdapter;
import org.msh.quantb.services.mvp.Messages;

public class TemplateImport {

	private String templatePath;
	private XSSFWorkbook workbook=null;
	private ForecastUIAdapter forecast;
	private int colMed = 0;
	private int colQuant = 0;
	private int colExp = 0;
	private int colQuantOrd=0;
	private int colArrOrd=0;
	private int colExpOrd=0;

	public TemplateImport(String _path){
		this.templatePath=_path;
	}



	public String getTemplatePath() {
		return templatePath;
	}



	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}



	public XSSFWorkbook getWorkbook(){
		return workbook;
	}



	public void setWorkbook(XSSFWorkbook workbook) {
		this.workbook = workbook;
	}


	public ForecastUIAdapter getForecast() {
		return forecast;
	}



	public void setForecast(ForecastUIAdapter forecast) {
		this.forecast = forecast;
	}



	public int getColMed() {
		return colMed;
	}



	public void setColMed(int colMed) {
		this.colMed = colMed;
	}



	public int getColQuant() {
		return colQuant;
	}



	public void setColQuant(int colQuant) {
		this.colQuant = colQuant;
	}



	public int getColExp() {
		return colExp;
	}



	public void setColExp(int colExp) {
		this.colExp = colExp;
	}



	public int getColQuantOrd() {
		return colQuantOrd;
	}



	public void setColQuantOrd(int colQuantOrd) {
		this.colQuantOrd = colQuantOrd;
	}



	public int getColArrOrd() {
		return colArrOrd;
	}



	public void setColArrOrd(int colArrOrd) {
		this.colArrOrd = colArrOrd;
	}



	public int getColExpOrd() {
		return colExpOrd;
	}



	public void setColExpOrd(int colExpOrd) {
		this.colExpOrd = colExpOrd;
	}



	/**
	 * Prepare workbook for current language and dates
	 * @param _forecast to fill current data
	 * @return
	 * @throws IOException
	 */
	public XSSFWorkbook create(ForecastUIAdapter _forecast) throws IOException, IllegalStateException{
		this.forecast = _forecast;
		FileInputStream file = new FileInputStream(getTemplatePath());
		setWorkbook(new XSSFWorkbook(file));
		file.close();
		if (parseIt()){
			XSSFSheet reference = getWorkbook().getSheetAt(1);
			LocalDate minDate = new LocalDate(forecast.getFirstFCDate());
			LocalDate maxDate = minDate.plusYears(15);
			String minDateS = minDate.toString(DateTimeFormat.forStyle("M-").withLocale(new Locale(Messages.getLanguage())));
			String maxDateS = maxDate.toString(DateTimeFormat.forStyle("M-").withLocale(new Locale(Messages.getLanguage())));
			XSSFCell dateMin = fetchCell(0,1,reference);
			dateMin.setCellValue(minDate.toDate());
			XSSFCell dateMax = fetchCell(1,1,reference);
			dateMax.setCellValue(maxDate.toDate());
			XSSFCell errorQuant = fetchCell(2,2,reference);
			errorQuant.setCellValue(Messages.getString("Error.template.stockonhand"));
			XSSFCell errorDates = fetchCell(3,2,reference);
			errorDates.setCellValue(Messages.getString("Error.template.stockexpirydate")+ " " + minDateS + " "+
					Messages.getString("Error.template.orafter")+" "+ maxDateS);
			XSSFCell col = fetchCell(4,2,reference);
			col.setCellValue(Messages.getString("Error.template.stockonorder"));
			col = fetchCell(5,2,reference);
			col.setCellValue(Messages.getString("Error.template.expectedreceiving")+ " " + minDateS + " "+
					Messages.getString("Error.template.orafter")+" "+ maxDateS);
			col = fetchCell(6,2,reference);
			col.setCellValue(Messages.getString("Error.template.orderexpirydate"));
			col = fetchCell(7,2,reference);
			col.setCellValue(Messages.getString("ImportStock.columns.medicine"));
			col = fetchCell(8,2,reference);
			col.setCellValue(Messages.getString("ImportStock.columns.quantity"));
			col = fetchCell(9,2,reference);
			col.setCellValue(Messages.getString("ImportStock.columns.expiry"));
			col = fetchCell(10,2,reference);
			col.setCellValue(Messages.getString("ImportStock.columns.orddelivery"));
			col = fetchCell(11,2,reference);
			col.setCellValue(Messages.getString("ImportStock.columns.ordexpiry"));
			col = fetchCell(12,2,reference);
			col.setCellValue(Messages.getString("ImportStock.columns.ordquantity"));
			col = fetchCell(13,2,reference);
			col.setCellValue(Messages.getString("ForecastingDocumentWindow.tbParameters.referenceDate"));
			getWorkbook().setSheetName(0, Messages.getString("ImportStock.Stock"));
			//getWorkbook().setSheetName(1, Messages.getString("ImportStock.current"));
			//createCurrentData(getWorkbook().getSheetAt(1)); 
			getWorkbook().getCreationHelper().createFormulaEvaluator().evaluateAll();
			return getWorkbook();
		}else{
			throw new IllegalStateException(Messages.getString("Application.importExcel.error.illegalTemplate"));
		}
	}
	/**
	 * Check and parse the template
	 * @return true, if template is good
	 */
	private boolean parseIt() {
		XSSFName med = getWorkbook().getName(ImportExcel.MED_RANGE);
		XSSFName dat = getWorkbook().getName(ImportExcel.DATES_RANGE);
		XSSFName quan = getWorkbook().getName(ImportExcel.QUANTITIES_RANGE);
		XSSFName arrive = getWorkbook().getName(ImportExcel.ARRIVE_RANGE);
		XSSFName ord_quant = getWorkbook().getName(ImportExcel.QUANTORDER_RANGE);
		XSSFName ord_exp = getWorkbook().getName(ImportExcel.ORDEXPDATE_RANGE);
		int col = -1;
		if(ImportExcel.doesRangeExist(med)){
			col=calcColumn(med);
			if(col>=0){
				setColMed(calcColumn(med));
			}else{
				return false;
			}
		}else{
			return false;
		}
		if(ImportExcel.doesRangeExist(dat)){
			col=calcColumn(dat);
			if(col>=0){
				setColExp(calcColumn(dat));
			}else{
				return false;
			}
		}else{
			return false;
		}
		if(ImportExcel.doesRangeExist(quan)){
			col=calcColumn(quan);
			if(col>=0){
				setColQuant(calcColumn(quan));
			}else{
				return false;
			}
		}else{
			return false;
		}
		if(ImportExcel.doesRangeExist(arrive)){
			col=calcColumn(arrive);
			if(col>=0){
				setColArrOrd(calcColumn(arrive));
			}else{
				return false;
			}
		}else{
			return false;
		}
		if(ImportExcel.doesRangeExist(ord_quant)){
			col=calcColumn(ord_quant);
			if(col>=0){
				setColQuantOrd(calcColumn(ord_quant));
			}else{
				return false;
			}
		}else{
			return false;
		}
		if(ImportExcel.doesRangeExist(ord_exp)){
			col=calcColumn(ord_exp);
			if(col>=0){
				setColExpOrd(calcColumn(ord_exp));
			}else{
				return false;
			}
		}else{
			return false;
		}
		//passed!
		return true;
	}


	/**
	 * Calc column number for range
	 * @param range
	 * @return
	 */
	private int calcColumn(XSSFName range) {
		AreaReference aref;
		try {
			aref = new AreaReference(range.getRefersToFormula());
			CellReference[] crefs = aref.getAllReferencedCells();
			if(crefs.length>0){
				return crefs[0].getCol();
			}else{
				return -1;
			}
		} catch (Exception e1) {
			return -1;
		}
	}







	/**
	 * Get cell, or create if is null
	 * @param row
	 * @param colnum
	 * @return
	 */
	protected XSSFCell fetchCell(int rownum, int colnum, XSSFSheet sheet){
		XSSFRow row = sheet.getRow(rownum);
		if(row == null){
			row = sheet.createRow(rownum);
		}
		XSSFCell cell = row.getCell(colnum);
		if (cell == null)//(row.getRowNum()!=exampleRow)
			cell = row.createCell(colnum);
		return cell;
	}

	/**
	 * Save the workbook to the pathToFile
	 * @param workbook
	 * @param pathToFile
	 * @return true, if workbook successfully saved
	 * @throws IOException 
	 */
	public boolean saveWorkBook(XSSFWorkbook workbook, String pathToFile) throws IOException {
		File outFile = new File(pathToFile);
		FileOutputStream outStream;
		outStream = new FileOutputStream(outFile);
		workbook.write(outStream);
		outStream.close();
		return true;
	}

}
