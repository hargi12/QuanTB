package org.msh.quantb.services.excel;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.DateFormat;
import jxl.write.DateFormats;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.NumberFormats;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
/**
 * Helper class need to make export codes clean from JXL specific things
 * @author alexey
 *
 */
public class JXLProcessor {

	private WritableCellFormat captionFormat;
	private WritableCellFormat labelFormat;
	protected WritableCellFormat integerFormat;
	private WritableCellFormat decimalFormat;
	private WritableCellFormat dateFormatMY;
	private WritableCellFormat subChapterFormat;
	private WritableCellFormat totalFormat;
	private String inputFile;
	private WritableWorkbook workbook = null;
	protected final String[] ALPHA = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
			"AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ","AK","AL","AM","AN","AO","AP","AQ","AR","AS","AT","AU","AV","AW","AX","AY","AZ",
			"BA","BB","BC","BD","BE","BF","BG","BH","BI","BJ","BK","BL","BM","BN","BO","BP","BQ","BR","BS","BT","BU","BV","BW","BX","BY","BZ",
			"CA","CB","CC","CD","CE","CF","CG","CH","CI","CJ","CK","CL","CM","CN","CO","CP","CQ","CR","CS","CT","CU","CV","CW","CX","CY","CZ",
			"DA","DB","DC","DD","DE","DF","DG","DH","DI","DJ","DK","DL","DM","DN","DO","DP","DQ","DR","DS","DT","DU","DV","DW","DX","DY","DZ",
			"EA","EB","EC","ED","EE","EF","EG","EH","EI","EJ","EK","EL","EM","EN","EO","EP","EQ","ER","ES","ET","EU","EV","EW","EX","EY","EZ"};
	private WritableCellFormat dateFormat;
	private WritableCellFormat labelCenterFormat;
	private WritableCellFormat decimalTotalFormat;

	public JXLProcessor() {
		super();
	}

	public void setOutputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	/**
	 * Add caption to the sheet
	 * @param sheet  
	 * @param column column number from 0
	 * @param row row number from 0
	 * @param s caption text
	 * @param cWidth column width
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	protected void addCaption(WritableSheet sheet, int column, int row,
			String s, int cWidth) throws RowsExceededException,
			WriteException {
				Label label;
				label = new Label(column, row, s, captionFormat);
				if (cWidth != 0){
					sheet.setColumnView(column, cWidth);
				}
				//sheet.setRowView(row, false);
				sheet.addCell(label);
			}
	/**
	 * Add row sub chapter
	 * @param sheet
	 * @param col
	 * @param row
	 * @param string
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	public void addSubChapter(WritableSheet sheet, int column, int row,
			String string) throws RowsExceededException, WriteException {
		Label label;
		label = new Label(column, row, string, subChapterFormat);
		sheet.addCell(label);
	}

	/**
	 * write integer number to any cell
	 * @param sheet
	 * @param column
	 * @param row
	 * @param integer
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	protected void addInteger(WritableSheet sheet, int column, int row,
			Integer integer) throws WriteException, RowsExceededException {
				Number number;
				number = new Number(column, row, integer, integerFormat);
				sheet.addCell(number);
			}
	/**
	 * Add total in integer format
	 * @param sheet
	 * @param column
	 * @param row
	 * @param integer
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	protected void addTotal(WritableSheet sheet, int column, int row,
			Integer integer) throws WriteException, RowsExceededException {
				Number number;
				number = new Number(column, row, integer, totalFormat);
				sheet.addCell(number);
			}
	
	/**
	 * Add total in decimal format
	 * @param sheet
	 * @param col
	 * @param row
	 * @param total
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	protected void addDecimalTotal(WritableSheet sheet, int col, int row,
			BigDecimal total) throws RowsExceededException, WriteException {
		Number number;
		number = new Number(col, row, total.doubleValue(), decimalTotalFormat);
		sheet.addCell(number);
		
	}
	/**
	 * Add simple decimal
	 * @param sheet
	 * @param column
	 * @param row
	 * @param fl number to write
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	protected void addDecimal(WritableSheet sheet, int column, int row,
			BigDecimal fl) throws WriteException, RowsExceededException {
				Number number;
				number = new Number(column, row, fl.doubleValue(), decimalFormat);
				sheet.addCell(number);
			}

	/**
	 * write text in general format to any cell
	 * @param sheet
	 * @param column
	 * @param row
	 * @param s
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	protected void addLabel(WritableSheet sheet, int column, int row,
			String s) throws WriteException, RowsExceededException {
				Label label;
				label = new Label(column, row, s, labelFormat);
				sheet.addCell(label);
			}
	
	/**
	 * write text in general format to any cell. Center align
	 * @param sheet
	 * @param column
	 * @param row
	 * @param s
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	protected void addCenterLabel(WritableSheet sheet, int column, int row,
			String s) throws WriteException, RowsExceededException {
				Label label;
				label = new Label(column, row, s, labelCenterFormat);
				sheet.addCell(label);
			}

	/**
	 * write date in format month year
	 * @param sheet
	 * @param column
	 * @param row
	 * @param date
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	protected void addDateMY(WritableSheet sheet, int column, int row,
			Date date) throws WriteException, RowsExceededException {
				WritableCell cell = new jxl.write.DateTime(column, row, date,dateFormatMY);
				sheet.addCell(cell);
			}
	/**
	 * write date in current locale format
	 * @param sheet
	 * @param column
	 * @param row
	 * @param date
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	protected void addDate(WritableSheet sheet, int column, int row,
			Date date) throws WriteException, RowsExceededException {
				WritableCell cell = new jxl.write.DateTime(column, row, date,dateFormat);
				sheet.addCell(cell);
			}

	/**
	 * get or create the workbook
	 * @return workbook
	 * @throws IOException 
	 * @throws WriteException 
	 */
	protected WritableWorkbook getWorkbook() throws IOException, WriteException {
		if (this.workbook == null){
			File file = new File(inputFile);
			WorkbookSettings wbSettings = new WorkbookSettings();
			wbSettings.setLocale(new Locale("en", "EN"));
			this.workbook = Workbook.createWorkbook(file, wbSettings);
			// Lets create a times font
			WritableFont dataFont = new WritableFont(WritableFont.TIMES, 10);
			WritableFont captionFont = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false,
					UnderlineStyle.NO_UNDERLINE);
	
			// create general text format
			labelFormat = new WritableCellFormat(dataFont);
			labelFormat.setWrap(true);
			labelFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			labelFormat.setWrap(true);
			labelFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
			// create general text format align center
			labelCenterFormat = new WritableCellFormat(dataFont);
			labelCenterFormat.setWrap(true);
			labelCenterFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			labelCenterFormat.setAlignment(Alignment.CENTRE);
			labelCenterFormat.setWrap(true);
			labelCenterFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
	
			// create general integer format
			integerFormat = new WritableCellFormat(dataFont,NumberFormats.THOUSANDS_INTEGER);
			integerFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			integerFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			//total Integer format
			// create general integer format
			totalFormat = new WritableCellFormat(captionFont,NumberFormats.THOUSANDS_INTEGER);
			totalFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
			totalFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			totalFormat.setBackground(Colour.IVORY);
			
			// decimal format with high precision
			//java.text.NumberFormat nf = java.text.DecimalFormat.getInstance();
			//nf.setMaximumFractionDigits(4);
			NumberFormat fivedps = new NumberFormat("###,###,###,###0.00"); 
			decimalFormat = new WritableCellFormat(dataFont,fivedps);
			decimalFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
			decimalFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			decimalTotalFormat = new WritableCellFormat(captionFont,fivedps);
			decimalTotalFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
			decimalTotalFormat.setBackground(Colour.IVORY);
			decimalTotalFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
	
			// create MMYY date format
			DateFormat dt = new DateFormat("MMM yyyy");
			dateFormatMY = new WritableCellFormat(dataFont,dt);
			dateFormatMY.setBorder(Border.ALL, BorderLineStyle.THIN);
			dateFormatMY.setVerticalAlignment(VerticalAlignment.CENTRE);
			
			// create general date format
			dateFormat = new WritableCellFormat(dataFont,DateFormats.DEFAULT);
			dateFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
			dateFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
	
			// Create caption format
			captionFormat = new WritableCellFormat(captionFont);
			captionFormat.setWrap(true);
			captionFormat.setAlignment(Alignment.CENTRE);
			captionFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			captionFormat.setWrap(true);
			captionFormat.setBackground(Colour.IVORY);
			captionFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
			
			//create sub chapter format
			subChapterFormat = new WritableCellFormat(captionFont);
			subChapterFormat.setWrap(true);
			subChapterFormat.setAlignment(Alignment.LEFT);
			subChapterFormat.setWrap(true);
			subChapterFormat.setBackground(Colour.IVORY);
			subChapterFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
			subChapterFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			// add formats
			CellView cv = new CellView();
			cv.setFormat(labelFormat);
			cv.setFormat(labelCenterFormat);
			cv.setFormat(captionFormat);
			cv.setFormat(integerFormat);
			cv.setFormat(dateFormatMY);
			cv.setFormat(subChapterFormat);
			cv.setFormat(dateFormat);
			cv.setFormat(decimalTotalFormat);
			cv.setAutosize(true);
		}
		return this.workbook;
	}

	/**
	 * Save the report
	 * @throws IOException 
	 * @throws WriteException 
	 */
	public void save() throws IOException, WriteException {
		getWorkbook().write();
		getWorkbook().close();
	
	}
	
	/**
	 * Add any prepared formula to cell given 
	 * @param sheet work sheet
	 * @param col column
	 * @param row row in sheet
	 * @param formula formula to place
	 * @param isTotal is total format need?
	 * @throws WriteException 
	 * @throws RowsExceededException 
	 */
	public void addFormula(WritableSheet sheet, int col, int row, String formula, boolean isTotal) throws RowsExceededException, WriteException {
		WritableCellFormat format = integerFormat;
		if (isTotal){
			format = totalFormat;
		}
		Formula f = new Formula(col, row, formula,format);
		sheet.addCell(f);
		
	}

}