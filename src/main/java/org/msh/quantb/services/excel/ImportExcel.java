package org.msh.quantb.services.excel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFName;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.msh.quantb.services.mvp.Messages;

/**
 * This class responsible for Excel import
 * getResult is only source for the result. 
 * Other getters are for debug only!
 * @author Alexey Kurasov
 *
 */
public class ImportExcel {


	public static final String QUANTITIES_RANGE = "Q";
	public static final String DATES_RANGE = "D";
	public static final String MED_RANGE = "M";
	public static final String ARRIVE_RANGE = "DE";
	public static final String QUANTORDER_RANGE="QO";
	public static final String ORDEXPDATE_RANGE="DO";
	private File excelFile = null;
	private XSSFWorkbook workbook = null;
	private List<String> meds = new ArrayList<String>();
	private List<Date> dats = new ArrayList<Date>();
	private List<Date> arrs = new ArrayList<Date>();
	private List<Integer> quants = new ArrayList<Integer>();
	private List<Integer> ord_quants = new ArrayList<Integer>();
	private List<Date> ord_expiry = new ArrayList<Date>();
	private List<ImportExcelDTO> result = new ArrayList<ImportExcelDTO>();
	private Boolean stockData=false; // does stock data exist
	private Boolean orderData=false; // does order data exist

	/**
	 * Only valid constructor
	 * @param _excelFile
	 */
	public ImportExcel(File _excelFile) {
		this.excelFile = _excelFile;
	}



	private File getExcelFile() {
		return excelFile;
	}


	private XSSFWorkbook getWorkbook() throws InvalidFormatException, IOException {
		if (workbook == null){
			if (getExcelFile() != null){
				workbook = new XSSFWorkbook(getExcelFile());
			}
		}
		return workbook;
	}


	public List<String> getMeds() {
		return meds;
	}



	public void setMeds(List<String> meds) {
		this.meds = meds;
	}



	public List<Date> getDats() {
		return dats;
	}



	public void setDats(List<Date> dats) {
		this.dats = dats;
	}



	public List<Date> getArrs() {
		return arrs;
	}



	public void setArrs(List<Date> arrs) {
		this.arrs = arrs;
	}



	public List<Integer> getQuants() {
		return quants;
	}



	public void setQuants(List<Integer> quants) {
		this.quants = quants;
	}



	public List<ImportExcelDTO> getResult() {
		return result;
	}



	public void setResult(List<ImportExcelDTO> result) {
		this.result = result;
	}



	public List<Integer> getOrd_quants() {
		return ord_quants;
	}



	public void setOrd_quants(List<Integer> ord_quants) {
		this.ord_quants = ord_quants;
	}



	public List<Date> getOrd_expiry() {
		return ord_expiry;
	}



	public void setOrd_expiry(List<Date> ord_expiry) {
		this.ord_expiry = ord_expiry;
	}



	public Boolean getStockData() {
		return stockData;
	}



	public void setStockData(Boolean stockData) {
		this.stockData = stockData;
	}



	public Boolean getOrderData() {
		return orderData;
	}



	public void setOrderData(Boolean orderData) {
		this.orderData = orderData;
	}



	/**
	 * Import data as is
	 * @return error message or empty string if all OK
	 */
	public String importMedStock() {
		try {
			if (getWorkbook() != null){
				XSSFName med = getWorkbook().getName(MED_RANGE);
				XSSFName dat = getWorkbook().getName(DATES_RANGE);
				XSSFName quan = getWorkbook().getName(QUANTITIES_RANGE);
				XSSFName arrive = getWorkbook().getName(ARRIVE_RANGE);
				XSSFName ord_quant = getWorkbook().getName(QUANTORDER_RANGE);
				XSSFName ord_exp = getWorkbook().getName(ORDEXPDATE_RANGE);
				String err = checkRanges(med, dat, quan, arrive, ord_quant, ord_exp);
				if(err.length()==0){
					//we must read list of medicines anyway
					getMeds().clear();
					readRange(med);
					err = verifyMedRange();
					if(err.length()==0){

						if(getStockData()){
							getDats().clear();
							readRange(dat);
							getQuants().clear();
							readRange(quan);
						}
						if(getOrderData()){
							getOrd_quants().clear();
							readRange(ord_quant);
							getArrs().clear();
							readRange(arrive);
							getOrd_expiry().clear();
							readRange(ord_exp);
						}
						buildResult();
					}else{
						return err;
					}
				}else{
					return err;
				}
			}else{
				return Messages.getString("Application.importExcel.error.noFile");
			}
		} catch (InvalidFormatException e) {
			return Messages.getString("Application.importExcel.error.badFormat");
		} catch (IOException e) {
			return Messages.getString("Application.importExcel.error.noFile");
		} catch (IllegalStateException e){
			return Messages.getString("Application.importExcel.error.noFile");
		} catch (InvalidOperationException e){
			return Messages.getString("Application.importExcel.error.badFormat");
		}

		finally {
			if(this.workbook != null){
				try {
					this.workbook.close();
				} catch (IOException e) {
					//nothing to do!!!
				}
			}
		}
		return verifyResult();
	}




	/**
	 * Very complex rules to check ranges
	 * @param med medicines range
	 * @param dat stock expiration dates
	 * @param quan stock quantity dates
	 * @param arrive order arrive dates
	 * @param ord_exp order expiration dates
	 * @param ord_quant order quantity dates
	 * @return empty string if all OK or error message
	 * @throws IOException 
	 * @throws IllegalStateException 
	 * @throws InvalidFormatException 
	 * @throws InvalidOperationException 
	 */
	private String checkRanges(XSSFName med, XSSFName dat, XSSFName quan, XSSFName arrive,
			XSSFName ord_quant, XSSFName ord_exp) throws InvalidOperationException, InvalidFormatException, IllegalStateException, IOException {
		//first of all filter files without any range
		if(hasAnyRange(med, dat, quan, arrive, ord_quant, ord_exp)){
			//range (M)edicines is mandatory and must be valid.
			if(doesRangeExist(med)){
				if(!isRangeWrong(med)){
					// 								stock or order or both ranges must be
					//STOCK DATA
					if(hasAnyRange(dat, quan)){ 
						if(hasAllRanges(dat, quan)){
							if(isRangeWrong(dat)){
								return String.format(Messages.getString("Application.importExcel.error.wrongrange"), DATES_RANGE); 
							}
							if(isRangeWrong(quan)){
								return String.format(Messages.getString("Application.importExcel.error.wrongrange"), QUANTITIES_RANGE); 
							}
							setStockData(true);
						}else{
							if(!doesRangeExist(dat)){
								return String.format(Messages.getString("Application.importExcel.error.stock.expiration"),DATES_RANGE);
							}
							if(!doesRangeExist(quan)){
								return String.format(Messages.getString("Application.importExcel.error.stock.quantity"),QUANTITIES_RANGE) ;
							}
						}
					}else{
						setStockData(false);
					}

					//ORDER DATA
					if(hasAnyRange(arrive, ord_quant, ord_exp)){
						if(hasAllRanges(arrive, ord_quant, ord_exp)){ //ord_exp is optional data, but mandatory range 
							if(isRangeWrong(ord_quant)){
								return String.format(Messages.getString("Application.importExcel.error.wrongrange"), QUANTORDER_RANGE); 
							}
							if(isRangeWrong(arrive)){
								return String.format(Messages.getString("Application.importExcel.error.wrongrange"), ARRIVE_RANGE); 
							}
							if(isRangeWrong(ord_exp)){
								return String.format(Messages.getString("Application.importExcel.error.wrongrange"), ORDEXPDATE_RANGE); 
							}
							setOrderData(true);
						}else{
							if(!doesRangeExist(arrive)){
								return String.format(Messages.getString("Application.importExcel.error.order.available"),ARRIVE_RANGE) ;
							}
							if(!doesRangeExist(ord_quant)){
								return String.format(Messages.getString("Application.importExcel.error.order.quantity"),QUANTORDER_RANGE) ;
							}
							if(!doesRangeExist(ord_exp)){
								return String.format(Messages.getString("Application.importExcel.error.order.expiration"),ORDEXPDATE_RANGE) ;
							}
						}
					}else{
						setOrderData(false);
					}
					//SAME ROW
					List<String> ranges = new ArrayList<String>();
					ranges.add(med.getRefersToFormula());
					if(getStockData()){
						ranges.add(quan.getRefersToFormula());
						ranges.add(dat.getRefersToFormula());
					}
					if(getOrderData()){
						ranges.add(arrive.getRefersToFormula());
						ranges.add(ord_exp.getRefersToFormula());
						ranges.add(ord_quant.getRefersToFormula());
					}
					if(checkSameRow(ranges)){
						return "";
					}else{
						return(Messages.getString("Application.importExcel.error.samerow"));
					}
				}else{
					return String.format(Messages.getString("Application.importExcel.error.wrongrange"), MED_RANGE); 
				}
			}else{
				return Messages.getString("Application.importExcel.error.medRange"); 	
			}
		}else{
			return Messages.getString("Application.importExcel.error.badfile");
		}
	}

	/**
	 * Check all ranges are existing
	 * @param ranges - var args any number of ranges
	 * @return
	 */
	private boolean hasAllRanges(XSSFName... ranges) {
		boolean ret = true;
		for(XSSFName range  :ranges){
			ret = ret && doesRangeExist(range);
		}
		return ret;
	}



	/**
	 * Check at least one range is existing
	 * @param ranges - var args any number of ranges
	 * @return
	 */
	private boolean hasAnyRange(XSSFName... ranges) {
		boolean ret = false;
		for(XSSFName range  :ranges){
			ret = ret || doesRangeExist(range);
		}
		return ret;
	}



	/**
	 * Does this range exist?
	 * @param range
	 * @return
	 */
	public static boolean doesRangeExist(XSSFName range) {
		return range!=null && range.getRefersToFormula()!=null;
	}

	/**
	 * Sometimes it is possible to define range as whole column or row or empty
	 * @param range
	 * @return
	 * @throws IOException 
	 * @throws IllegalStateException 
	 * @throws InvalidFormatException 
	 * @throws InvalidOperationException 
	 */
	private boolean isRangeWrong(XSSFName range) throws InvalidFormatException, IOException {
		AreaReference aref;
		try {
			aref = new AreaReference(range.getRefersToFormula());
		} catch (Exception e1) {
			return true;
		}
		CellReference[] crefs = aref.getAllReferencedCells();
		if(crefs.length>=1 || crefs.length<1000){
			if(crefs[0].getCol() != crefs[crefs.length-1].getCol()){
				return true; // more then 1 column
			}
			int entrs = 0; //valid entries quantity
			for(int i=0; i<crefs.length;i++){
				XSSFSheet s = getWorkbook().getSheet(crefs[i].getSheetName());
				XSSFRow r = s.getRow(crefs[i].getRow());
				if(r!=null){
					XSSFCell cell = r.getCell(crefs[i].getCol());
					if (cell!=null){
						int ctype= cell.getCellType();
						if(ctype != Cell.CELL_TYPE_BLANK){ //allowable for all!
							if(range.getNameName().equalsIgnoreCase(MED_RANGE)){
								if(ctype == Cell.CELL_TYPE_STRING){
									entrs++;
								}else{
									return true;
								}
							}
							if(range.getNameName().equalsIgnoreCase(DATES_RANGE) || range.getNameName().equalsIgnoreCase(ARRIVE_RANGE)
									|| range.getNameName().equalsIgnoreCase(ORDEXPDATE_RANGE)){ 
								try {
									Date d = cell.getDateCellValue(); //only blank and date
									entrs++;
								} catch (Exception e) {
									//it is possible blank text
									String sD = cell.getStringCellValue().trim();
									if(sD.length()>0){
										return true;
									}else{
										entrs++;
									}
								}
							}
							if(range.getNameName().equalsIgnoreCase(QUANTITIES_RANGE) || range.getNameName().equalsIgnoreCase(QUANTORDER_RANGE)){
								if(ctype == Cell.CELL_TYPE_NUMERIC || ctype == Cell.CELL_TYPE_FORMULA ){
									entrs++;
								}else{
									if(cell.getStringCellValue().length() == 0){
										entrs++;
									}else{
										return true;
									}
								}
							}
						}else{
							entrs++;
						}
					}
				}
			}
			return entrs==0; //no useful info in this range
		}else{
			return true;
		}
	}


	/**
	 * All ranges starts from the same row
	 * @param ranges list of ranges
	 * @return
	 */
	private boolean checkSameRow(List<String> ranges) {
		if(ranges.size()>1){
			AreaReference first = new AreaReference(ranges.get(0));
			int trueRow = first.getFirstCell().getRow();
			boolean ret = true;
			for(String range: ranges){
				AreaReference ref = new AreaReference(range);
				ret =ret && (ref.getFirstCell().getRow() == trueRow);
			}
			return ret;
		}else{
			return true; // impossible!
		}
	}



	/**
	 * medicines range is a list of medicines names as these names defined in Excel
	 * The equal names, if exist, should follow each other
	 * @return
	 */
	private String verifyMedRange() {
		String medName = getMeds().get(0);
		Set<String> unic = new HashSet<String>();
		unic.add(medName);
		for(String name : getMeds()){
			if(name != null){
				if(!name.equalsIgnoreCase(medName)){
					medName = name;
					if (!unic.add(medName)){
						return medName +" " + Messages.getString("Application.importExcel.error.medicinetwice");
					}
				}
			}
		}
		return "";
	}



	/**
	 * Verify the result. For any medicine at least one batch should be defined
	 * Result should contain at least one record
	 * @return error message or empty string if verify is successful
	 */
	private String verifyResult() {
		if(getResult().size()>0){
			boolean found = false;
			for(String med : getUniqueMeds()){
				found = false;
				for(ImportExcelDTO dto :getResult()){
					found = found | dto.getMedicine().equals(med);
				}
				if(!found){
					return med + " " + Messages.getString("Application.importExcel.error.nobatches");
				}
			}
		}else{
			return Messages.getString("Application.importExcel.error.emptyresult");
		}
		return "";
	}



	/**
	 * Build a list of the Medicines Stock
	 */
	private void buildResult() {
		getResult().clear();
		String med = getFirstMedicine();
		while (med != null){
			addToResult(med);
			med = getNextMedicine(med);
		}


	}
	/**
	 * Add to the result all batches by defined medicine result separate for stock and orders!
	 * Allows "raw" and "boot" topologies
	 * @param med
	 */
	private void addToResult(String med) {
		// determine first and end index
		int fI = meds.indexOf(med);
		int eI = fI;
		int nextMi = getIndexOfNextMed(med);
		if (nextMi == -1){
			eI = 10000; // means infinity
		} else{
			eI = nextMi-1;
		}
		for(int i=fI; i<=eI; i++){
			if(getStockData()){
				if (i< getDats().size() && i<getQuants().size()){
					if((getDats().get(i) != null && getQuants().get(i) != null)){ // the "boot" topology
						getResult().add(new ImportExcelDTO(med, getDats().get(i), getQuants().get(i), null, null,null));
					}
				}
			}
			if(getOrderData()){
				if(i<getOrd_quants().size() && i<getOrd_expiry().size() && i< getArrs().size()){
					if(getOrd_quants().get(i) != null && getArrs().get(i)!=null){
						getResult().add(new ImportExcelDTO(med, null, null, getArrs().get(i), getOrd_quants().get(i),getOrd_expiry().get(i)));
					}
				}
			}
		}

	}



	/**
	 * Get next medicine
	 * @param med
	 * @return next medicine or null if no
	 */
	private String getNextMedicine(String med) {
		int nextInd = getIndexOfNextMed(med);
		if (nextInd >-1){
			return meds.get(nextInd);
		}
		return null;
	}


	/**
	 * Get index of next medicine
	 * @param med this medicine
	 * @return index of next medicine or -1 if no next medicine
	 */
	private int getIndexOfNextMed(String med) {
		int medInd = meds.indexOf(med);
		for(int i=medInd+1; i<meds.size();i++){
			if(meds.get(i) != null && !meds.get(i).equals(med)){
				return i;
			}
		}
		return -1;
	}



	/**
	 * Get the first medicine in medicines result? if no - returns null
	 * @return
	 */
	private String getFirstMedicine() {
		for(String med : meds){
			if(med != null){
				return med;
			}
		}
		return null;
	}



	/**
	 * Read the range to list, only for single column ranges!!!
	 * @param range ready to use range
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	private void readRange(XSSFName range) throws InvalidFormatException,
	IOException {
		AreaReference aref = new AreaReference(range.getRefersToFormula());
		CellReference[] crefs = aref.getAllReferencedCells();
		for (int i=0; i<crefs.length; i++) {
			Sheet s = getWorkbook().getSheet(crefs[i].getSheetName());
			Row r = s.getRow(crefs[i].getRow());
			Cell c = null;
			if(r!=null){
				c = r.getCell(crefs[i].getCol());
			}
			// extract the cell contents based on cell type etc.
			if(range.getNameName().equalsIgnoreCase(MED_RANGE)){
				addString(c, getMeds());
			}
			if(range.getNameName().equalsIgnoreCase(DATES_RANGE)){
				addToDates(c, getDats());
			}
			if(range.getNameName().equalsIgnoreCase(ORDEXPDATE_RANGE)){
				addToDates(c,getOrd_expiry());
			}
			if(range.getNameName().equalsIgnoreCase(ARRIVE_RANGE)){
				addToDates(c,getArrs());
			}
			if(range.getNameName().equalsIgnoreCase(QUANTITIES_RANGE)){
				addToQuants(c, getQuants());
			}
			if(range.getNameName().equalsIgnoreCase(QUANTORDER_RANGE)){
				addToQuants(c, getOrd_quants());
			}

		}
	}

	/**
	 * Add value from cell c to quantity list
	 * @param c
	 * @param list 
	 */
	private void addToQuants(Cell c, List<Integer> list) {
		if(c!= null){
			if (c.getCellType() == Cell.CELL_TYPE_NUMERIC || c.getCellType() == Cell.CELL_TYPE_FORMULA ){
				if (!DateUtil.isCellDateFormatted(c)) {
					Double dvalue = Double.valueOf(c.getNumericCellValue());
					list.add(dvalue.intValue());
				} else {
					list.add(null);
				}
			}else{
				list.add(null);
			}
		}
		else{
			list.add(null);
		}

	}



	/**
	 * Add value from cell c to dates list
	 * @param c
	 * @param list 
	 */
	private void addToDates(Cell c, List<Date> list) {
		if(c!=null){
			if (c.getCellType() == Cell.CELL_TYPE_NUMERIC || c.getCellType() == Cell.CELL_TYPE_FORMULA ){
				if (DateUtil.isCellDateFormatted(c)) {
					list.add(c.getDateCellValue());
				} else {
					list.add(null);
				}
			}else{
				list.add(null);
			}
		}
		else{
			list.add(null);
		}

	}



	/**
	 * Add string value of cell c to list
	 * @param c cell
	 * @param list list to add value
	 */
	private void addString(Cell c, List<String> list) {
		if(c!=null){
			if (c.getCellType() == Cell.CELL_TYPE_STRING || c.getCellType() == Cell.CELL_TYPE_FORMULA){
				list.add(c.getRichStringCellValue().getString());
			}else{
				list.add(null);
			}
		}else{
			list.add(null);
		}

	}


	/**
	 * Get list of the unique medicines names
	 * @return
	 */
	public List<String> getUniqueMeds() {
		List<String> res = new ArrayList<String>();
		String prevMed = "not existed medicine";
		for(String med : getMeds()){
			if(med != null){
				if(!med.equalsIgnoreCase(prevMed)){
					res.add(med);
					prevMed=med;
				}
			}

		}
		return res;
	}


	/**
	 * Get all DTOs with 
	 * @param medicineE
	 * @return list of DTO or empty list if not found
	 */
	public List<ImportExcelDTO> getMedicineResult(String medicineE) {
		List<ImportExcelDTO> res = new ArrayList<ImportExcelDTO>();
		for(ImportExcelDTO dto : getResult()){
			if (dto.getMedicine().equalsIgnoreCase(medicineE)){
				res.add(dto);
			}
		}
		return res;
	}


	/**
	 * really has stock data
	 * @return
	 */
	public boolean hasStockData() {
		boolean ret = false;
		for(Integer quant : getQuants()){
			ret = ret || quant != null;
		}
		return ret;
	}


	/**
	 * Really has order data
	 * @return
	 */
	public boolean hasOrderData() {
		boolean ret = false;
		for(Integer quant : getOrd_quants()){
			ret = ret || quant != null;
		}
		return ret;
	}



}
