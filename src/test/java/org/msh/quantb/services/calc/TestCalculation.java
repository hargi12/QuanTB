package org.msh.quantb.services.calc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import junit.framework.TestCase;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.msh.quantb.model.forecast.Forecast;
import org.msh.quantb.model.forecast.ForecastingRegimen;
import org.msh.quantb.model.forecast.ForecastingRegimenResult;
import org.msh.quantb.model.forecast.Month;
import org.msh.quantb.model.forecast.MonthQuantity;
import org.msh.quantb.model.mvp.ModelFactory;
import org.msh.quantb.services.io.DeliveryOrderUI;
import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.ForecastingMedicineUIAdapter;
import org.msh.quantb.services.io.ForecastingRegimenResultUIAdapter;
import org.msh.quantb.services.io.ForecastingRegimenUIAdapter;
import org.msh.quantb.services.io.ForecastingResultUIAdapter;
import org.msh.quantb.services.io.ForecastingTotal;
import org.msh.quantb.services.io.MedicineConsUIAdapter;
import org.msh.quantb.services.io.MonthQuantityUIAdapter;
import org.msh.quantb.services.io.MonthUIAdapter;
import org.msh.quantb.services.io.RegimenUIAdapter;
import org.msh.quantb.services.io.RegimensDicUIAdapter;
import org.msh.quantb.services.io.SelectableDates;
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.CanShowMessages;

public class TestCalculation extends TestCase {

	private static final String TEST_SLICE_QUANTITY = "TestSliceByQuantity.qtb";
	private static final String TEST_REGIMEN_NAME = "TestCasesQuantityRegimen";
	private static final String TEST_FORECASTING_NAME ="TestCasesQuantity.qtb";
	private static final String TEST_SPLIT_FORECASTING_NAME ="TestCasesQuantity_Split.qtb";
	private static final String TEST_FORECASTING_NAME_PERS ="TestCasesPercents.qtb";
	private static final String TEST_SPLIT_FORECASTING_NAME_PERS ="TestCasesPercents_Split.qtb";
	private static final String TEST_MERGE_1 ="TestByMerge_1.qtb";
	private static final String TEST_MERGE_2 ="TestByMerge_2.qtb";
	private static final String TEST_CHECK_MED ="TestByCheckMed.qtb";
	private static final String TEST_PERS_CHECK_MED ="TestPercentsByCheckMed.qtb";

	private static final String TEST_1 = "PercentByMerge1.qtb";
	private static final String TEST_2 = "PercentByMerge2.qtb";
	private static final String TEST_3 = "PercentByMerge3.qtb";

	public static final String testDocPath = "src/test/resources/doc/byJUnit/";
	public static final String testPath = "src/test/resources";
	private static ModelFactory model = new ModelFactory(testPath);

	/**
	 * load the test regimen by quantity
	 * @return
	 */
	private RegimenUIAdapter loadTestRegimen(){
		try {
			//load the test regimen
			RegimensDicUIAdapter regs = model.getRegimensDicUIAdapter();
			RegimenUIAdapter testReg = null;
			for(RegimenUIAdapter regUi : regs.getAllRegimens()){
				if (regUi.getName().equals(TEST_REGIMEN_NAME)){
					testReg = regUi;
					break;
				};
			}
			return testReg;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
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

	public void testIgnoreTimezone(){
		ForecastUIAdapter fUi = loadTestFile(TEST_FORECASTING_NAME);
		XMLGregorianCalendar xCal = fUi.getForecastObj().getIniDate();
		System.out.println(xCal);
		xCal.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		System.out.println(xCal);
		Calendar cal = xCal.toGregorianCalendar();
		System.out.println(cal.getTimeZone());
	}

	/**
	 * This test based on test regimen named TestQuantityRegimen
	 * and test result data that calculated manually in a Excel file named CasesNoTest.xlsx
	 * Class to test is CasesCalculator
	 */
	public void testCasesQuantitySimple(){
		RegimenUIAdapter regUi = loadTestRegimen();
		Calendar rd = DateUtils.getCleanCalendar(2014, 4, 17);//ref DAte
		//check interval, from, to and no extra!
		Calendar beg = regUi.getBeginDate(rd);// режим должен заканчиватсья в rd // regimen must end at RD
		Calendar end = DateUtils.getCleanCalendar(2014, 7, 26);
		Calendar realEnd = DateUtils.getCleanCalendar(2014, 7, end.getActualMaximum(Calendar.DAY_OF_MONTH));
		CasesCalculator calc = new CasesCalculator(beg, end, regUi);
		assertEquals(realEnd, calc.getEnd());

		ForecastingRegimenResult res1 = calc.createResult(model, 0);
		assertEquals(2013, res1.getMonth().getYear());
		assertEquals(10, res1.getMonth().getMonth());
		assertEquals(21, res1.getFromDay());
		ForecastingRegimenResult resLast = calc.createResult(model, 283);
		assertEquals(2014, resLast.getMonth().getYear());
		assertEquals(7, resLast.getMonth().getMonth());
		assertEquals(31, resLast.getFromDay());
		ForecastingRegimenResult resAfter = calc.createResult(model, 284);
		assertNull(resAfter);
		// test getIndex - it's very intermediary test solely for one function
		assertEquals(0, calc.getIndex(beg).intValue());
		assertEquals(283, calc.getIndex(end).intValue());
		Calendar middle = DateUtils.getCleanCalendar(2014, 4, 7);
		assertEquals(167, calc.getIndex(middle).intValue());
		// finally! test quantity calculations
		Calendar from1 = DateUtils.getCleanCalendar(2013, 10, 21);
		Calendar from2 = DateUtils.getCleanCalendar(2013, 11, 1);
		Calendar from3 = DateUtils.getCleanCalendar(2014, 0, 1);
		Calendar from4 = DateUtils.getCleanCalendar(2014, 1, 1);
		Calendar from5 = DateUtils.getCleanCalendar(2014, 2, 1);
		calc.add(from1, BigDecimal.valueOf(5));
		calc.add(from2, BigDecimal.valueOf(2));
		calc.add(from3, BigDecimal.valueOf(4));
		calc.add(from4, BigDecimal.valueOf(7));
		calc.add(from5, BigDecimal.valueOf(3));
		checkQuantities(calc);
	}

	/**
	 * Private method to check some cases quantities
	 * @param calc
	 */
	private void checkQuantities(CasesCalculator calc) {
		Calendar testPoint = DateUtils.getCleanCalendar(2014, 4, 2);
		BigDecimal quan = calc.getPhaseQByDate(1, testPoint);
		assertEquals(3, quan.intValue());
		quan = calc.getPhaseQByDate(2, testPoint);
		assertEquals(7, quan.intValue());
		quan = calc.getPhaseQByDate(3, testPoint);
		assertEquals(4, quan.intValue());
		quan = calc.getPhaseQByDate(4, testPoint);
		assertEquals(7, quan.intValue());
	}

	private ForecastingCalculation testCheckMed_base(){
		ForecastUIAdapter fUi = loadTestFile(TEST_CHECK_MED);
		Presenter.setFactory(model);
		ForecastingCalculation fc = new ForecastingCalculation(fUi.getForecastObj(), model);
		// imitate execute method
		fc.clearResults();
		fc.calcCasesOnTreatment();
		fc.calcNewCases();
		fc.calcMedicinesRegimes();
		fc.calcMedicines();
		fc.calcMedicinesResults();
		List<MedicineResume> res = fc.getResume();

		ForecastingRegimenUIAdapter ui = fc.getForecastUI().getRegimes().get(0);
		List<ForecastingRegimenResultUIAdapter> fRes = ui.getResults();
		//check result quantity
		assertEquals(679, fRes.size());

		Calendar endDate = DateUtils.getCleanCalendar(2014, 5, 1);
		int i = 0;
		for(ForecastingMedicineUIAdapter mUi : fUi.getMedicines()){
			BigDecimal cases = BigDecimal.ZERO;
			int dispensed = 0; 
			for (ForecastingResultUIAdapter frUi : mUi.getResults()){
				if (frUi.getFrom().before(endDate)){
					cases = cases.add(frUi.getOldCases());
					cases = cases.add(frUi.getNewCases());
					dispensed += frUi.getMedicineConsInt();
				}else
					break;
			}

			if (mUi.getMedicine().getAbbrevName().contains("Am(500)")){
				assertTrue(cases.compareTo(new BigDecimal(120)) == 0);
				assertEquals(240, dispensed);
				assertEquals(1966, res.get(0).getLeadPeriod().getDispensed().intValue());
				assertEquals(2000, res.get(0).getLeadPeriod().getIncomingBalance().intValue());
				i++;
			}
			if(mUi.getMedicine().getAbbrevName().contains("Am(500/2)")){
				assertTrue(cases.compareTo(new BigDecimal(0)) == 0);
				assertEquals(0, dispensed);
				assertEquals(70, res.get(1).getLeadPeriod().getDispensed().intValue());
				assertEquals(8000, res.get(1).getLeadPeriod().getIncomingBalance().intValue());
				i++;
			}
			if (mUi.getMedicine().getAbbrevName().contains("Cm(1000)")){
				assertTrue(cases.compareTo(new BigDecimal(110)) == 0);
				assertEquals(110, dispensed);
				assertEquals(425, res.get(2).getLeadPeriod().getDispensed().intValue());
				assertEquals(300, res.get(2).getLeadPeriod().getIncomingBalance().intValue());
				assertEquals(1000, res.get(2).getLeadPeriod().getTransit().intValue());

				assertEquals(875, res.get(2).getReviewPeriod().getIncomingBalance().intValue());
				assertEquals(45, res.get(2).getReviewPeriod().getConsumedOldInt().intValue());
				assertEquals(966, res.get(2).getReviewPeriod().getConsumedNewInt().intValue());
				i++;
			}
			if (mUi.getMedicine().getAbbrevName().contains("Imi/Cls(500/500)")){
				assertTrue(cases.compareTo(new BigDecimal(37)) == 0);
				assertEquals(37, dispensed);
				assertEquals(269, res.get(3).getLeadPeriod().getDispensed().intValue());
				assertEquals(350, res.get(3).getLeadPeriod().getIncomingBalance().intValue());

				assertEquals(81, res.get(3).getReviewPeriod().getIncomingBalance().intValue());
				assertEquals(155, res.get(3).getReviewPeriod().getConsumedOldInt().intValue());
				assertEquals(702, res.get(3).getReviewPeriod().getConsumedNewInt().intValue());
				i++;
			}
		}
		assertEquals(4,	i);
		return fc;
	}
	/**
	 * stock on hand
	 */
	public void testCheckMed_stockOnHand(){
		ForecastingCalculation fc = testCheckMed_base();

		for(ForecastingMedicineUIAdapter m : fc.getForecastUI().getMedicines()){
			if(m.getMedicine().getAbbrevName().contains("Imi/Cls(500/500)")){
				m.getBatchesToExpire().get(1).setExclude(true);
				break;
			}
		}
		// пересчитаем
		fc.clearResults();
		fc.calcCasesOnTreatment();
		fc.calcNewCases();
		fc.calcMedicinesRegimes();
		fc.calcMedicines();
		fc.calcMedicinesResults();

		List<MedicineResume> res = fc.getResume();
		assertEquals(679, fc.getForecastUI().getRegimes().get(0).getResults().size());

		Calendar endDate = DateUtils.getCleanCalendar(2014, 5, 1);
		for(ForecastingMedicineUIAdapter mUi : fc.getForecastUI().getMedicines()){
			BigDecimal cases = BigDecimal.ZERO;
			int dispensed = 0;
			if (mUi.getMedicine().getAbbrevName().contains("Imi/Cls(500/500)")){
				for(int j = 0; j < mUi.getBatchesToExpire().size(); j++){
					if(mUi.getBatchesToExpire().get(j).getInclude()){
						for (ForecastingResultUIAdapter frUi : mUi.getResults()){
							if (frUi.getFrom().before(endDate)){
								cases = cases.add(frUi.getOldCases());
								cases = cases.add(frUi.getNewCases());
								dispensed += frUi.getMedicineConsInt();
							}else
								break;
						}

						assertTrue(cases.compareTo(new BigDecimal(37)) == 0);
						assertEquals(37, dispensed);
						assertEquals(50, res.get(3).getLeadPeriod().getDispensed().intValue());
						assertEquals(50, res.get(3).getLeadPeriod().getIncomingBalance().intValue());
						assertEquals(219, res.get(3).getLeadPeriod().getMissing().intValue());

						assertEquals(0, res.get(3).getReviewPeriod().getIncomingBalance().intValue());
						assertEquals(155, res.get(3).getReviewPeriod().getConsumedOldInt().intValue());
						assertEquals(702, res.get(3).getReviewPeriod().getConsumedNewInt().intValue());
					}
				}
			}
		}
	}

	/**
	 * stock on order
	 */
	public void testCheckMed_stockOrder(){
		ForecastingCalculation fc = testCheckMed_base();

		for(ForecastingMedicineUIAdapter m : fc.getForecastUI().getMedicines()){
			if(m.getMedicine().getAbbrevName().contains("Cm(1000)")){
				m.getOrders().get(0).getBatch().setExclude(true);
				break;
			}
		}
		// пересчитаем
		fc.clearResults();
		fc.calcCasesOnTreatment();
		fc.calcNewCases();
		fc.calcMedicinesRegimes();
		fc.calcMedicines();
		fc.calcMedicinesResults();

		List<MedicineResume> res = fc.getResume();
		assertEquals(679, fc.getForecastUI().getRegimes().get(0).getResults().size());

		Calendar endDate = DateUtils.getCleanCalendar(2014, 5, 1);
		for(ForecastingMedicineUIAdapter mUi : fc.getForecastUI().getMedicines()){
			BigDecimal cases = BigDecimal.ZERO;
			int dispensed = 0;
			if (mUi.getMedicine().getAbbrevName().contains("Cm(1000)")){
				for(int j = 0; j < mUi.getOrders().size(); j++){
					if(mUi.getOrders().get(j).getBatchInclude()){
						for (ForecastingResultUIAdapter frUi : mUi.getResults()){
							if (frUi.getFrom().before(endDate)){
								cases = cases.add(frUi.getOldCases());
								cases = cases.add(frUi.getNewCases());
								dispensed += frUi.getMedicineConsInt();
							}else
								break;
						}

						assertTrue(cases.compareTo(new BigDecimal(110)) == 0);
						assertEquals(110, dispensed);
						assertEquals(425, res.get(2).getLeadPeriod().getDispensed().intValue());
						assertEquals(300, res.get(2).getLeadPeriod().getIncomingBalance().intValue());
						assertEquals(0, res.get(2).getLeadPeriod().getMissing().intValue());
						assertEquals(300, res.get(2).getLeadPeriod().getTransit().intValue());

						assertEquals(175, res.get(2).getReviewPeriod().getIncomingBalance().intValue());
						assertEquals(45, res.get(2).getReviewPeriod().getConsumedOldInt().intValue());
						assertEquals(966, res.get(2).getReviewPeriod().getConsumedNewInt().intValue());
					}
				}
			}
		}
	}

	/**
	 * exclude regimen in the treatment of cases
	 */
	public void testCheckRegim_treatment(){
		ForecastingCalculation fc = testCheckMed_base();

		fc.getForecastUI().getRegimes().get(0).setExcludeCasesOnTreatment(true);

		// пересчитаем
		fc.clearResults();
		fc.calcCasesOnTreatment();
		fc.calcNewCases();
		fc.calcMedicinesRegimes();
		fc.calcMedicines();
		fc.calcMedicinesResults();

		List<MedicineResume> res = fc.getResume();
		assertEquals(679, fc.getForecastUI().getRegimes().get(0).getResults().size());

		Calendar endDate = DateUtils.getCleanCalendar(2014, 5, 1);
		for(ForecastingMedicineUIAdapter mUi : fc.getForecastUI().getMedicines()){
			BigDecimal cases = BigDecimal.ZERO;
			int dispensed = 0;
			if (mUi.getMedicine().getAbbrevName().contains("Imi/Cls(500/500)")){
				for (ForecastingResultUIAdapter frUi : mUi.getResults()){
					if (frUi.getFrom().before(endDate)){
						cases = cases.add(frUi.getOldCases());
						cases = cases.add(frUi.getNewCases());
						dispensed += frUi.getMedicineConsInt();
					}else
						break;
				}

				assertTrue(cases.compareTo(new BigDecimal(0)) == 0);
				assertEquals(0, dispensed);
				assertEquals(0, res.get(3).getLeadPeriod().getDispensed().intValue());// 0
				assertEquals(350, res.get(3).getLeadPeriod().getIncomingBalance().intValue());
				assertEquals(0, res.get(3).getLeadPeriod().getMissing().intValue());

				assertEquals(350, res.get(3).getReviewPeriod().getIncomingBalance().intValue());// 350
				assertEquals(0, res.get(3).getReviewPeriod().getConsumedOldInt().intValue());
				assertEquals(702, res.get(3).getReviewPeriod().getConsumedNewInt().intValue());
			}
		}
	}

	/**
	 * exclude regimen in cases expected
	 */
	public void testCheckRegim_new(){
		ForecastingCalculation fc = testCheckMed_base();
		fc.getForecastUI().getRegimes().get(0).setExcludeNewCases(true);

		// пересчитаем
		fc.clearResults();
		fc.calcCasesOnTreatment();
		fc.calcNewCases();
		fc.calcMedicinesRegimes();
		fc.calcMedicines();
		fc.calcMedicinesResults();

		List<MedicineResume> res = fc.getResume();
		assertEquals(679, fc.getForecastUI().getRegimes().get(0).getResults().size());

		Calendar endDate = DateUtils.getCleanCalendar(2014, 5, 1);
		int i = 0;
		for(ForecastingMedicineUIAdapter mUi : fc.getForecastUI().getMedicines()){
			BigDecimal cases = BigDecimal.ZERO;
			int dispensed = 0, disp = 0;
			for (ForecastingResultUIAdapter frUi : mUi.getResults()){
				if (frUi.getFrom().before(endDate)){
					cases = cases.add(frUi.getOldCases());
					cases = cases.add(frUi.getNewCases());
					disp += frUi.getDispensing().intValue();
					dispensed += frUi.getMedicineConsInt();
				}else
					break;
			}
			if (mUi.getMedicine().getAbbrevName().contains("Am(500)")){
				assertTrue(cases.compareTo(new BigDecimal(75)) == 0);
				assertEquals(150, dispensed);
				assertEquals(450, res.get(0).getLeadPeriod().getDispensed().intValue());
				assertEquals(2000, res.get(0).getLeadPeriod().getIncomingBalance().intValue());
				assertEquals(0, res.get(0).getLeadPeriod().getMissing().intValue());

				assertEquals(1550, res.get(0).getReviewPeriod().getIncomingBalance().intValue());
				assertEquals(0, res.get(0).getReviewPeriod().getConsumedOldInt().intValue());
				assertEquals(0, res.get(0).getReviewPeriod().getConsumedNewInt().intValue());
				i++;
			}
			if(mUi.getMedicine().getAbbrevName().contains("Am(500/2)")){
				assertTrue(cases.compareTo(new BigDecimal(0)) == 0);
				assertEquals(0, dispensed);
				assertEquals(70, res.get(1).getLeadPeriod().getDispensed().intValue());
				assertEquals(8000, res.get(1).getLeadPeriod().getIncomingBalance().intValue());
				assertEquals(0, res.get(1).getLeadPeriod().getMissing().intValue());

				assertEquals(9030, res.get(1).getReviewPeriod().getIncomingBalance().intValue());
				assertEquals(0, res.get(1).getReviewPeriod().getConsumedOldInt().intValue());
				assertEquals(0, res.get(1).getReviewPeriod().getConsumedNewInt().intValue());
				i++;
			}
			if (mUi.getMedicine().getAbbrevName().contains("Cm(1000)")){
				assertTrue(cases.compareTo(new BigDecimal(110)) == 0);
				assertEquals(110, dispensed);
				assertEquals(425, res.get(2).getLeadPeriod().getDispensed().intValue());
				assertEquals(300, res.get(2).getLeadPeriod().getIncomingBalance().intValue());
				assertEquals(0, res.get(2).getLeadPeriod().getMissing().intValue());

				assertEquals(875, res.get(2).getReviewPeriod().getIncomingBalance().intValue());
				assertEquals(45, res.get(2).getReviewPeriod().getConsumedOldInt().intValue());
				assertEquals(0, res.get(2).getReviewPeriod().getConsumedNewInt().intValue());
				i++;
			}
			if (mUi.getMedicine().getAbbrevName().contains("Imi/Cls(500/500)")){
				assertTrue(cases.compareTo(new BigDecimal(37)) == 0);
				assertEquals(37, dispensed);
				assertEquals(269, res.get(3).getLeadPeriod().getDispensed().intValue());
				assertEquals(350, res.get(3).getLeadPeriod().getIncomingBalance().intValue());
				assertEquals(0, res.get(3).getLeadPeriod().getMissing().intValue());

				assertEquals(81, res.get(3).getReviewPeriod().getIncomingBalance().intValue());
				assertEquals(155, res.get(3).getReviewPeriod().getConsumedOldInt().intValue());
				assertEquals(0, res.get(3).getReviewPeriod().getConsumedNewInt().intValue());
				i++;
			}
		}
		assertEquals(4, i);
	}

	private ForecastingCalculation testPersCheckMed_base(){
		ForecastUIAdapter fUi = loadTestFile(TEST_PERS_CHECK_MED);
		Presenter.setFactory(model);
		ForecastingCalculation fc = new ForecastingCalculation(fUi.getForecastObj(), model);
		// imitate execute method
		fc.clearResults();
		fc.calcCasesOnTreatment();
		fc.calcNewCases();
		fc.calcMedicinesRegimes();
		fc.calcMedicines();
		fc.calcMedicinesResults();
		List<MedicineResume> res = fc.getResume();

		ForecastingRegimenUIAdapter ui = fc.getForecastUI().getRegimes().get(0);
		List<ForecastingRegimenResultUIAdapter> fRes = ui.getResults();

		Calendar endDate = DateUtils.getCleanCalendar(2014, 6, 1);
		int i = 0;
		for(ForecastingMedicineUIAdapter mUi : fUi.getMedicines()){
			BigDecimal casesOld = BigDecimal.ZERO;
			BigDecimal casesNew = BigDecimal.ZERO;
			BigDecimal dispensing = BigDecimal.ZERO;
			for (ForecastingResultUIAdapter frUi : mUi.getResults()){
				if (frUi.getFrom().before(endDate)){
					casesOld = casesOld.add(frUi.getOldCases());
					casesNew = casesNew.add(frUi.getNewCases());
					dispensing = dispensing.add(frUi.getDispensing());
				}else
					break;
			}
			System.out.println(mUi.getMedicine().getAbbrevName());

			BigDecimal cOld = casesOld.round(new MathContext(2, RoundingMode.CEILING));
			BigDecimal cNew = casesNew.round(new MathContext(1, RoundingMode.CEILING));
			int cases = cOld.intValue() + cNew.intValue();
			BigDecimal val = BigDecimal.ZERO;

			if (mUi.getMedicine().getAbbrevName().contains("Am(500/2)")){
				assertEquals(20, cases);
				assertEquals(500.0, res.get(0).getLeadPeriod().getIncomingBalance().doubleValue());

				val = res.get(0).getLeadPeriod().getDispensed().round(new MathContext(2, RoundingMode.CEILING));
				assertEquals(53, val.intValue());

				assertEquals(448.0, res.get(0).getReviewPeriod().getIncomingBalance().doubleValue());

				val = res.get(0).getReviewPeriod().getConsumedOld().round(new MathContext(3, RoundingMode.CEILING));
				assertEquals(213, val.intValue());
				val = res.get(0).getReviewPeriod().getConsumedNew().round(new MathContext(4, RoundingMode.CEILING));
				assertEquals(1557, val.intValue());

				val = res.get(0).getQuantityToProcured().round(new MathContext(4, RoundingMode.CEILING));
				assertEquals(1322, val.intValue());

				i++;
			}
			if(mUi.getMedicine().getAbbrevName().contains("Cm(1000)")){
				assertEquals(57, cases);
				assertEquals(500.0, res.get(1).getLeadPeriod().getIncomingBalance().doubleValue());

				val = res.get(1).getLeadPeriod().getDispensed().round(new MathContext(3, RoundingMode.CEILING));
				assertEquals(159, val.intValue());

				assertEquals(342.0, res.get(1).getReviewPeriod().getIncomingBalance().doubleValue());

				val = res.get(1).getReviewPeriod().getConsumedOld().round(new MathContext(3, RoundingMode.CEILING));
				assertEquals(618, val.intValue());
				val = res.get(1).getReviewPeriod().getConsumedNew().round(new MathContext(4, RoundingMode.CEILING));
				assertEquals(4539, val.intValue());

				val = res.get(1).getQuantityToProcured().round(new MathContext(4, RoundingMode.CEILING));
				assertEquals(4815, val.intValue());

				i++;
			}
			if (mUi.getMedicine().getAbbrevName().contains("Cs(250)")){
				val = dispensing.round(new MathContext(2, RoundingMode.CEILING));
				assertEquals(30, val.intValue());
				assertEquals(1000.0, res.get(2).getLeadPeriod().getIncomingBalance().doubleValue());

				val = res.get(2).getLeadPeriod().getDispensed().round(new MathContext(2, RoundingMode.CEILING));
				assertEquals(82, val.intValue());

				assertEquals(919.0, res.get(2).getReviewPeriod().getIncomingBalance().doubleValue());

				val = res.get(2).getReviewPeriod().getConsumedOld().round(new MathContext(3, RoundingMode.CEILING));
				assertEquals(905, val.intValue());
				val = res.get(2).getReviewPeriod().getConsumedNew().round(new MathContext(4, RoundingMode.CEILING));
				assertEquals(2502, val.intValue());

				val = res.get(2).getQuantityToProcured().round(new MathContext(4, RoundingMode.CEILING));
				assertEquals(2488, val.intValue());

				i++;
			}
			if (mUi.getMedicine().getAbbrevName().contains("Eto(250)")){
				assertEquals(0.0, res.get(3).getLeadPeriod().getIncomingBalance().doubleValue());
				assertEquals(1000.0, res.get(3).getLeadPeriod().getTransit().doubleValue());

				val = res.get(3).getLeadPeriod().getDispensed().round(new MathContext(3, RoundingMode.CEILING));
				assertEquals(813, val.intValue());

				val = res.get(3).getLeadPeriod().getMissing().round(new MathContext(3, RoundingMode.CEILING));
				assertEquals(460, val.intValue());

				assertEquals(188.0, res.get(3).getReviewPeriod().getIncomingBalance().doubleValue());

				val = res.get(3).getReviewPeriod().getConsumedOld().round(new MathContext(5, RoundingMode.CEILING));
				assertEquals(14168, val.intValue());
				val = res.get(3).getReviewPeriod().getConsumedNew().round(new MathContext(5, RoundingMode.CEILING));
				assertEquals(39196, val.intValue());

				val = res.get(3).getQuantityToProcured().round(new MathContext(5, RoundingMode.CEILING));
				assertEquals(53176, val.intValue());

				i++;
			}
		}
		assertEquals(4,	i);
		return fc;
	}

	/**
	 * stock on hand
	 */
	public void testPersCheckMed_stockOnHand(){
		ForecastingCalculation fc = testPersCheckMed_base();

		for(ForecastingMedicineUIAdapter m : fc.getForecastUI().getMedicines()){
			if(m.getMedicine().getAbbrevName().contains("Cm(1000)")){
				m.getBatchesToExpire().get(0).setExclude(true);
				break;
			}
		}
		// пересчитаем
		fc.clearResults();
		fc.calcCasesOnTreatment();
		fc.calcNewCases();
		fc.calcMedicinesRegimes();
		fc.calcMedicines();
		fc.calcMedicinesResults();

		List<MedicineResume> res = fc.getResume();

		Calendar endDate = DateUtils.getCleanCalendar(2014, 6, 1);
		for(ForecastingMedicineUIAdapter mUi : fc.getForecastUI().getMedicines()){
			BigDecimal casesOld = BigDecimal.ZERO;
			BigDecimal casesNew = BigDecimal.ZERO;
			if (mUi.getMedicine().getAbbrevName().contains("Cm(1000)")){
				for(int j = 0; j < mUi.getBatchesToExpire().size(); j++){
					if(mUi.getBatchesToExpire().get(j).getInclude()){
						for (ForecastingResultUIAdapter frUi : mUi.getResults()){
							if (frUi.getFrom().before(endDate)){
								casesOld = casesOld.add(frUi.getOldCases());
								casesNew = casesNew.add(frUi.getNewCases());
							}else
								break;
						}

						BigDecimal cOld = casesOld.round(new MathContext(2, RoundingMode.CEILING));
						BigDecimal cNew = casesNew.round(new MathContext(1, RoundingMode.CEILING));
						int cases = cOld.intValue() + cNew.intValue();
						BigDecimal val = BigDecimal.ZERO;

						assertEquals(57, cases);
						assertEquals(200.0, res.get(1).getLeadPeriod().getIncomingBalance().doubleValue());

						val = res.get(1).getLeadPeriod().getDispensed().round(new MathContext(3, RoundingMode.CEILING));
						assertEquals(159, val.intValue());

						val = res.get(1).getLeadPeriod().getMissing().round(new MathContext(3, RoundingMode.CEILING));
						assertEquals(0, val.intValue());

						assertEquals(42.0, res.get(1).getReviewPeriod().getIncomingBalance().doubleValue());

						val = res.get(1).getReviewPeriod().getConsumedOld().round(new MathContext(3, RoundingMode.CEILING));
						assertEquals(618, val.intValue());
						val = res.get(1).getReviewPeriod().getConsumedNew().round(new MathContext(4, RoundingMode.CEILING));
						assertEquals(4539, val.intValue());

						val = res.get(1).getQuantityToProcured().round(new MathContext(4, RoundingMode.CEILING));
						assertEquals(5115, val.intValue());
					}
				}
			}
		}
	}

	/**
	 * stock on hand
	 */
	public void testPersCheckMed_stockOrder(){
		ForecastingCalculation fc = testPersCheckMed_base();

		for(ForecastingMedicineUIAdapter m : fc.getForecastUI().getMedicines()){
			if(m.getMedicine().getAbbrevName().contains("Eto(250)")){
				m.getOrders().get(0).getBatch().setExclude(true);
				break;
			}
		}
		// пересчитаем
		fc.clearResults();
		fc.calcCasesOnTreatment();
		fc.calcNewCases();
		fc.calcMedicinesRegimes();
		fc.calcMedicines();
		fc.calcMedicinesResults();

		List<MedicineResume> res = fc.getResume();

		Calendar endDate = DateUtils.getCleanCalendar(2014, 6, 1);
		for(ForecastingMedicineUIAdapter mUi : fc.getForecastUI().getMedicines()){
			BigDecimal casesOld = BigDecimal.ZERO;
			BigDecimal casesNew = BigDecimal.ZERO;
			if (mUi.getMedicine().getAbbrevName().contains("Eto(250)")){
				for(int j = 0; j < mUi.getOrders().size(); j++){
					for (ForecastingResultUIAdapter frUi : mUi.getResults()){
						if (frUi.getFrom().before(endDate)){
							casesOld = casesOld.add(frUi.getOldCases());
							casesNew = casesNew.add(frUi.getNewCases());
						}else
							break;
					}

					BigDecimal cOld = casesOld.round(new MathContext(3, RoundingMode.CEILING));
					BigDecimal cNew = casesNew.round(new MathContext(1, RoundingMode.CEILING));
					int cases = cOld.intValue() + cNew.intValue();
					BigDecimal val = BigDecimal.ZERO;

					assertEquals(154, cases);
					assertEquals(0.0, res.get(3).getLeadPeriod().getIncomingBalance().doubleValue());

					val = res.get(3).getLeadPeriod().getDispensed().round(new MathContext(1, RoundingMode.CEILING));
					assertEquals(0, val.intValue());

					val = res.get(3).getLeadPeriod().getMissing().round(new MathContext(4, RoundingMode.CEILING));
					assertEquals(1272, val.intValue());

					assertEquals(0.0, res.get(3).getReviewPeriod().getIncomingBalance().doubleValue());

					val = res.get(3).getReviewPeriod().getConsumedOld().round(new MathContext(5, RoundingMode.CEILING));
					assertEquals(14168, val.intValue());
					val = res.get(3).getReviewPeriod().getConsumedNew().round(new MathContext(5, RoundingMode.CEILING));
					assertEquals(39196, val.intValue());

					val = res.get(3).getQuantityToProcured().round(new MathContext(5, RoundingMode.CEILING));
					assertEquals(53363, val.intValue());
				}
			}
		}
	}

	/**
	 * exclude regimen in the treatment of cases
	 */
	public void testPersCheckRegim_treatment(){
		ForecastingCalculation fc = testPersCheckMed_base();

		fc.getForecastUI().getRegimes().get(0).setExcludeCasesOnTreatment(true);

		// пересчитаем
		fc.clearResults();
		fc.calcCasesOnTreatment();
		fc.calcNewCases();
		fc.calcMedicinesRegimes();
		fc.calcMedicines();
		fc.calcMedicinesResults();

		List<MedicineResume> res = fc.getResume();

		Calendar endDate = DateUtils.getCleanCalendar(2014, 6, 1);
		for(ForecastingMedicineUIAdapter mUi : fc.getForecastUI().getMedicines()){
			BigDecimal casesOld = BigDecimal.ZERO;
			BigDecimal casesNew = BigDecimal.ZERO;
			if (mUi.getMedicine().getAbbrevName().contains("Am(500/2)")){
				for (ForecastingResultUIAdapter frUi : mUi.getResults()){
					if (frUi.getFrom().before(endDate)){
						casesOld = casesOld.add(frUi.getOldCases());
						casesNew = casesNew.add(frUi.getNewCases());
					}else
						break;
				}

				BigDecimal cOld = casesOld.round(new MathContext(2, RoundingMode.CEILING));
				BigDecimal cNew = casesNew.round(new MathContext(1, RoundingMode.CEILING));
				int cases = cOld.intValue() + cNew.intValue();
				BigDecimal val = BigDecimal.ZERO;

				assertEquals(2, cases);
				assertEquals(500.0, res.get(0).getLeadPeriod().getIncomingBalance().doubleValue());

				val = res.get(0).getLeadPeriod().getDispensed().round(new MathContext(1, RoundingMode.CEILING));
				assertEquals(5, val.intValue());

				assertEquals(0.0, res.get(0).getLeadPeriod().getMissing().doubleValue());

				assertEquals(496.0, res.get(0).getReviewPeriod().getIncomingBalance().doubleValue());

				assertEquals(0.0, res.get(0).getReviewPeriod().getConsumedOld().doubleValue());
				val = res.get(0).getReviewPeriod().getConsumedNew().round(new MathContext(4, RoundingMode.CEILING));
				assertEquals(1557, val.intValue());

				val = res.get(0).getQuantityToProcured().round(new MathContext(4, RoundingMode.CEILING));
				assertEquals(1062, val.intValue());
			}
		}
	}

	/**
	 * exclude regimen in new cases
	 */
	public void testPersCheckRegim_new(){
		ForecastingCalculation fc = testPersCheckMed_base();

		fc.getForecastUI().getRegimes().get(0).setExcludeNewCases(true);

		// пересчитаем
		fc.clearResults();
		fc.calcCasesOnTreatment();
		fc.calcNewCases();
		fc.calcMedicinesRegimes();
		fc.calcMedicines();
		fc.calcMedicinesResults();

		List<MedicineResume> res = fc.getResume();

		Calendar endDate = DateUtils.getCleanCalendar(2014, 6, 1);
		for(ForecastingMedicineUIAdapter mUi : fc.getForecastUI().getMedicines()){
			BigDecimal casesOld = BigDecimal.ZERO;
			BigDecimal casesNew = BigDecimal.ZERO;
			if (mUi.getMedicine().getAbbrevName().contains("Am(500/2)")){
				for (ForecastingResultUIAdapter frUi : mUi.getResults()){
					if (frUi.getFrom().before(endDate)){
						casesOld = casesOld.add(frUi.getOldCases());
						casesNew = casesNew.add(frUi.getNewCases());
					}else
						break;
				}

				BigDecimal cOld = casesOld.round(new MathContext(2, RoundingMode.CEILING));
				BigDecimal cNew = casesNew.round(new MathContext(1, RoundingMode.CEILING));
				int cases = cOld.intValue() + cNew.intValue();
				BigDecimal val = BigDecimal.ZERO;

				assertEquals(18, cases);
				assertEquals(500.0, res.get(0).getLeadPeriod().getIncomingBalance().doubleValue());

				val = res.get(0).getLeadPeriod().getDispensed().round(new MathContext(2, RoundingMode.CEILING));
				assertEquals(48, val.intValue());

				assertEquals(0.0, res.get(0).getLeadPeriod().getMissing().doubleValue());

				assertEquals(452.0, res.get(0).getReviewPeriod().getIncomingBalance().doubleValue());

				val = res.get(0).getReviewPeriod().getConsumedOld().round(new MathContext(3, RoundingMode.CEILING));
				assertEquals(213, val.intValue());

				assertEquals(0.0, res.get(0).getReviewPeriod().getConsumedNew().doubleValue());
				assertEquals(0.0, res.get(0).getQuantityToProcured().doubleValue());
			}
		}
	}

	/**
	 * Test enrolled and expected cases quantities
	 */
	public void testCalcCasesQuantities(){
		ForecastUIAdapter fUi = loadTestFile(TEST_FORECASTING_NAME);
		Presenter.setFactory(model);
		ForecastingCalculation fc = new ForecastingCalculation(fUi.getForecastObj(), model);
		// imitate execute method
		fc.clearResults();
		fc.calcCasesOnTreatment();
		fc.calcNewCases();
		fc.calcMedicinesRegimes();
		fc.calcMedicines();
		fc.calcMedicinesResults();
		List<MedicineResume> res = fc.getResume();

		List<ForecastingRegimenUIAdapter> list = fc.getForecastUI().getRegimes();
		ForecastingRegimenUIAdapter ui = list.get(0);
		List<ForecastingRegimenResultUIAdapter> fRes = ui.getResults();
		//check result quantity
		assertEquals(679, fRes.size());
		//First random point
		ForecastingRegimenResultUIAdapter rC = fRes.get(179);
		System.out.println("First random " + DateUtils.formatDate(rC.getFromDate().getTime(), "dd.MM.yyyy"));
		assertEquals(3,rC.getIntensive().getOldCases().intValue());
		assertEquals(0,rC.getContinious().getOldCases().intValue());
		assertEquals(11, rC.getAdditionalPhases().get(0).getOldCases().intValue());
		assertEquals(2, rC.getAdditionalPhases().get(1).getOldCases().intValue());
		//Second random point
		rC = fRes.get(357);
		System.out.println("Second random " + DateUtils.formatDate(rC.getFromDate().getTime(), "dd.MM.yyyy"));
		assertEquals(3,rC.getIntensive().getNewCases().intValue());
		assertEquals(7,rC.getContinious().getNewCases().intValue());
		assertEquals(4, rC.getAdditionalPhases().get(0).getNewCases().intValue());
		assertEquals(2, rC.getAdditionalPhases().get(1).getNewCases().intValue());
		// third random point
		rC = fRes.get(246); 
		System.out.println("Third random " + DateUtils.formatDate(rC.getFromDate().getTime(), "dd.MM.yyyy"));
		assertEquals(0,rC.getIntensive().getOldCases().intValue());
		assertEquals(0,rC.getContinious().getOldCases().intValue());
		assertEquals(3, rC.getAdditionalPhases().get(0).getOldCases().intValue());
		assertEquals(7, rC.getAdditionalPhases().get(1).getOldCases().intValue());
		assertEquals(11,rC.getIntensive().getNewCases().intValue());
		assertEquals(0,rC.getContinious().getNewCases().intValue());
		assertEquals(0, rC.getAdditionalPhases().get(0).getNewCases().intValue());
		assertEquals(0, rC.getAdditionalPhases().get(1).getNewCases().intValue());

		//check regimen - medicines
		int i = 0;
		for(ForecastingMedicineUIAdapter mUi : fUi.getMedicines()){
			MedicineConsUIAdapter mcUi = rC.getMedConsunption(mUi.getMedicine());
			if (mcUi != null){
				System.out.println(mcUi);
				if (mcUi.getMedicine().getAbbrevName().contains("Am(500)")){
					assertEquals(22, mcUi.getConsIntensiveNew().intValue());
					assertEquals(22, mcUi.getAllConsumption().intValue());
					i++;
				}
				if(mcUi.getMedicine().getAbbrevName().contains("Am(500/2)")){
					assertEquals(0, mcUi.getAllConsumption().intValue());
					i++;
				}
				if(mcUi.getMedicine().getAbbrevName().contains("Cm(1000)")){
					assertEquals(3, mcUi.getConsOtherOld().get(0).intValue());
					assertEquals(3, mcUi.getAllConsumption().intValue());
					i++;
				}
				if(mcUi.getMedicine().getAbbrevName().contains("Imi/Cls(500/500)")){
					assertEquals(7, mcUi.getConsOtherOld().get(1).intValue());
					assertEquals(7, mcUi.getAllConsumption().intValue());
					i++;
				}
			}
			//check medicines only - The Result! Third random point. 69, because all results are from th Reference Date
			ForecastingResultUIAdapter fRu = mUi.getResults().get(69);
			if (mUi.getMedicine().getAbbrevName().contains("Am(500)")){
				assertEquals(22, fRu.getConsNew().intValue());
				assertEquals(11, fRu.getNewCases().intValue());
				assertEquals(0, fRu.getConsOld().intValue());
				assertEquals(0, fRu.getOldCases().intValue());
				i++;
			}
			if (mUi.getMedicine().getAbbrevName().contains("Am(500/2)")){
				int res1 = fRu.getConsOld().intValue() + fRu.getConsNew().intValue()
						+ fRu.getNewCases().intValue() + fRu.getOldCases().intValue();
				assertEquals(0, res1);
				i++;
			}
			if (mUi.getMedicine().getAbbrevName().contains("Cm(1000)")){
				assertEquals(0, fRu.getConsNew().intValue());
				assertEquals(0, fRu.getNewCases().intValue());
				assertEquals(3, fRu.getConsOld().intValue());
				assertEquals(3, fRu.getOldCases().intValue());
				i++;
			}

			if (mUi.getMedicine().getAbbrevName().contains("Imi/Cls(500/500)")){
				assertEquals(0, fRu.getConsNew().intValue());
				assertEquals(0, fRu.getNewCases().intValue());
				assertEquals(7, fRu.getConsOld().intValue());
				assertEquals(7, fRu.getOldCases().intValue());
				i++;
			}
		}
		assertEquals(8, i);	// have all tests been passed?

		//check medicine consumption and Resume (see page Medicines in table casesNoTest.xlsx)
		Calendar endDate = DateUtils.getCleanCalendar(2014, 5, 1);
		i = 0;
		for(ForecastingMedicineUIAdapter mUi : fUi.getMedicines()){
			BigDecimal cases = BigDecimal.ZERO;
			int dispensed = 0; 
			for (ForecastingResultUIAdapter frUi : mUi.getResults()){
				if (frUi.getFrom().before(endDate)){
					//System.out.println(DateUtils.formatDate(frUi.getFrom().getTime(), "dd.MM.yyyy"));
					cases = cases.add(frUi.getOldCases());
					cases = cases.add(frUi.getNewCases());
					dispensed = dispensed + frUi.getMedicineConsInt();
				}else{
					break;
				}
			}

			if (mUi.getMedicine().getAbbrevName().contains("Am(500)")){
				assertTrue(cases.compareTo(new BigDecimal(120)) == 0);
				assertEquals(240, dispensed);
				assertEquals(1918,res.get(0).getLeadPeriod().getDispensed().intValue());
				i++;
			}
			if(mUi.getMedicine().getAbbrevName().contains("Am(500/2)")){
				assertTrue(cases.compareTo(new BigDecimal(0)) == 0);
				assertEquals(0, dispensed);
				assertEquals(42,res.get(1).getLeadPeriod().getDispensed().intValue());
				i++;
			}
			if (mUi.getMedicine().getAbbrevName().contains("Cm(1000)")){
				assertTrue(cases.compareTo(new BigDecimal(145)) == 0);
				assertEquals(145, dispensed);
				assertEquals(446,res.get(2).getLeadPeriod().getDispensed().intValue());
				i++;
			}
			if (mUi.getMedicine().getAbbrevName().contains("Imi/Cls(500/500)")){
				assertTrue(cases.compareTo(new BigDecimal(43)) == 0);
				assertEquals(43, dispensed);
				assertEquals(420,res.get(3).getLeadPeriod().getDispensed().intValue());
				i++;
			}
		}
		assertEquals(4, i);
		//test monthly cases every fRuI is first date of a month - Cases report - Treatment regimes
		List<ForecastingRegimenResultUIAdapter> fRuI = fUi.getRegimes().get(0).getMonthsResults(fUi.getFirstFCDate(),model);
		i=0;
		int firstDate =17;
		for(ForecastingRegimenResultUIAdapter frr : fRuI){
			assertEquals(firstDate, frr.getFromDay().intValue());
			firstDate = 1;
			if (frr.getMonth().getMonth().intValue() == 4 && frr.getMonth().getYear().intValue()==2014){
				assertEquals(21, frr.getEnrolled().intValue());
				assertEquals(5, frr.getExpected().intValue());
				i++;
			}
			if (frr.getMonth().getMonth().intValue() == 7 && frr.getMonth().getYear().intValue()==2014){
				assertEquals(3, frr.getEnrolled().intValue());
				assertEquals(18, frr.getExpected().intValue());
				i++;
			}
			if (frr.getMonth().getMonth().intValue() == 1 && frr.getMonth().getYear().intValue()==2015){
				assertEquals(0, frr.getEnrolled().intValue());
				assertEquals(3, frr.getExpected().intValue());
				i++;
			}
			if (frr.getMonth().getMonth().intValue() == 2 && frr.getMonth().getYear().intValue()==2015){
				assertEquals(0, frr.getEnrolled().intValue());
				assertEquals(0, frr.getExpected().intValue());
				i++;
			}
		}
		assertEquals(4, i);
		//test medicine consumption Cases Report - Medicine
		List<MedicineConsumption> mCons = fc.getMedicineConsumption();
		//first point at RD 17.05.2014
		int newCases = mCons.get(0).getCons().get(0).getNewCases().intValue();
		int oldCases = mCons.get(0).getCons().get(0).getOldCases().intValue();
		assertEquals(3, oldCases);
		assertEquals(5, newCases);

		//second point at 01.07.2014 and 4-th phase
		newCases = mCons.get(3).getCons().get(2).getNewCases().intValue();
		oldCases = mCons.get(3).getCons().get(2).getOldCases().intValue();
		assertEquals(7, oldCases);
		assertEquals(0, newCases);

		//third point at 17.08.2014 and 2-nd phase
		newCases = mCons.get(1).getCons().get(3).getNewCases().intValue();
		oldCases = mCons.get(1).getCons().get(3).getOldCases().intValue();
		assertEquals(0, oldCases);
		assertEquals(5, newCases);

		//test detailed report - 4 random points
		ForecastingMedicineUIAdapter med = fc.getForecastUI().getMedicines().get(0);
		List<PeriodResume> periods = fc.calcMedicineResume(med.getMedicine());
		assertEquals(90, periods.get(0).getConsumedOld().intValue());
		assertEquals(150, periods.get(0).getConsumedNew().intValue());
		assertEquals(0, periods.get(4).getConsumedOld().intValue());
		assertEquals(840, periods.get(4).getConsumedNew().intValue());

		med = fc.getForecastUI().getMedicines().get(2);
		periods = fc.calcMedicineResume(med.getMedicine());
		assertEquals(223, periods.get(1).getConsumedOld().intValue());
		assertEquals(0, periods.get(1).getConsumedNew().intValue());
		assertEquals(0, periods.get(6).getConsumedOld().intValue());
		assertEquals(212, periods.get(6).getConsumedNew().intValue());
	}

	/**
	 * 
	 */
	public void testCalcCasesQuantities_Split(){
		// 1/
		ForecastUIAdapter fUi = loadTestFile(TEST_FORECASTING_NAME);
		Presenter.setFactory(model);
		ForecastingCalculation fc = new ForecastingCalculation(fUi.getForecastObj(), model);
		// imitate execute method
		fc.clearResults();
		fc.calcCasesOnTreatment();
		fc.calcNewCases();
		fc.calcMedicinesRegimes();
		fc.calcMedicines();
		fc.calcMedicinesResults();

		List<MedicineResume> res = fc.getResume();
		List<ForecastingRegimenResultUIAdapter> fRes = fc.getForecastUI().getRegimes().get(0).getResults();
		//check result quantity
		assertEquals(679, fRes.size());

		// 2
		ForecastUIAdapter split_fUi = loadTestFile(TEST_SPLIT_FORECASTING_NAME);
		ForecastingCalculation split_fc = new ForecastingCalculation(split_fUi.getForecastObj(), model);
		// imitate execute method
		split_fc.clearResults();
		split_fc.calcCasesOnTreatment();
		split_fc.calcNewCases();
		split_fc.calcMedicinesRegimes();
		split_fc.calcMedicines();
		split_fc.calcMedicinesResults();

		List<MedicineResume> split_res = fc.getResume();
		List<ForecastingRegimenResultUIAdapter> split_fRes = split_fc.getForecastUI().getRegimes().get(0).getResults();
		//check result quantity
		assertEquals(585, split_fRes.size());

		ForecastingRegimenUIAdapter split_fres = split_fc.getForecastUI().getRegimes().get(0);

		List<MonthQuantityUIAdapter> split_month = split_fres.getCasesOnTreatment();
		List<MonthQuantityUIAdapter> split_month_new = split_fres.getNewCases();

		// сверим February, March, April, May:new=old
		System.out.println("CasesOnTreatment: ");
		assertEquals(7, split_month.get(0).getIQuantity().intValue());
		assertEquals(3, split_month.get(1).getIQuantity().intValue());
		assertEquals(0, split_month.get(2).getIQuantity().intValue());
		assertEquals(5, split_month.get(3).getIQuantity().intValue());
		assertEquals(2, split_month.get(4).getIQuantity().intValue());
		assertEquals(4, split_month.get(5).getIQuantity().intValue());
		System.out.println("ok");

		System.out.println("NewCases: ");
		assertEquals(7, split_month_new.get(0).getIQuantity().intValue());
		assertEquals(3, split_month_new.get(1).getIQuantity().intValue());
		System.out.println("ok");

		// текущий запас
		for(ForecastingMedicineUIAdapter m : split_fc.getForecastUI().getMedicines()){
			if(m.getMedicine().getAbbrevName().contains("Am(500)"))
				assertEquals(82, m.getBatchesToExpire().get(0).getQuantity().intValue());
			if(m.getMedicine().getAbbrevName().contains("Am(500/2)"))
				assertEquals(9958, m.getBatchesToExpire().get(0).getQuantity().intValue());
			if(m.getMedicine().getAbbrevName().contains("Cm(1000)"))
				assertEquals(9554, m.getBatchesToExpire().get(0).getQuantity().intValue());
			if(m.getMedicine().getAbbrevName().contains("Imi/Cls(500/500)"))
				assertEquals(9580, m.getBatchesToExpire().get(0).getQuantity().intValue());
		}
	}

	/**
	 * Class by System.out.println warning or errors 
	 * in test ForecastMergeControl
	 * @author Irina
	 *
	 */
	private class SysOut implements CanShowMessages {
		public SysOut() {}
		@Override
		public void showInformation(String message, String title) {
			System.out.println("Information:");
			System.out.println(message);
		}

		@Override
		public void showError(String mess) {
			System.out.println("ERROR:");
			System.out.println(mess);
		}

		@Override
		public boolean showSimpleWarningString(String message) {
			System.out.println(message);
			return true;
		}

	}

	/**
	 * test validate merge two file forecasting
	 */
	public void testValidateMergeForecastings(){
		ForecastUIAdapter f_m1Ui = loadTestFile(TEST_MERGE_1);
		ForecastUIAdapter f_m2Ui = loadTestFile(TEST_MERGE_2);
		Presenter.setFactory(model);

		ForecastingCalculation fc_1 = new ForecastingCalculation(f_m1Ui.getForecastObj(), model);
		ForecastingCalculation fc_2 = new ForecastingCalculation(f_m2Ui.getForecastObj(), model);

		List<ForecastUIAdapter> fcToMerge = new ArrayList<ForecastUIAdapter>();
		fcToMerge.add(fc_1.getForecastUI());
		fcToMerge.add(fc_2.getForecastUI());
		SysOut printing = new SysOut();
		ForecastMergeControl controler = new ForecastMergeControl(fcToMerge, printing);
		if (controler.validate()){
			System.out.println("ForecastMergeControl validate OK");
		}
	}

	/**
	 * test merge two file forecasting
	 */
	public void testMergeForecastings(){
		ForecastUIAdapter f_m1Ui = loadTestFile(TEST_MERGE_1);
		ForecastUIAdapter f_m2Ui = loadTestFile(TEST_MERGE_2);
		Presenter.setFactory(model);

		ForecastingCalculation fc_1 = new ForecastingCalculation(f_m1Ui.getForecastObj(), model);
		ForecastingCalculation fc_2 = new ForecastingCalculation(f_m2Ui.getForecastObj(), model);

		List<ForecastUIAdapter> fcToMerge = new ArrayList<ForecastUIAdapter>();
		fcToMerge.add(fc_1.getForecastUI());
		fcToMerge.add(fc_2.getForecastUI());
		SysOut printing = new SysOut();
		ForecastMergeControl controler = new ForecastMergeControl(fcToMerge, printing);
		if (controler.validate()){
			ForecastUIAdapter merged = controler.merge();
			if (merged != null){
				System.out.println("merged ok");
				ForecastingCalculation fc_merge = new ForecastingCalculation(merged.getForecastObj(), model);
				fc_merge.clearResults();
				fc_merge.calcCasesOnTreatment();
				fc_merge.calcNewCases();
				fc_merge.calcMedicinesRegimes();
				fc_merge.calcMedicines();
				fc_merge.calcMedicinesResults();

				List<MedicineResume> res = fc_merge.getResume();
				List<ForecastingRegimenResultUIAdapter> mergRes = merged.getRegimes().get(0).getResults();
				//check result quantity
				assertEquals(679, mergRes.size());

				ForecastingRegimenUIAdapter forReg = merged.getRegimes().get(0);

				List<MonthQuantityUIAdapter> split_month = forReg.getCasesOnTreatment();
				List<MonthQuantityUIAdapter> split_month_new = forReg.getNewCases();

				// сверим February, March, April, May:new=old
				System.out.println("CasesOnTreatment: ");
				assertEquals(5, split_month.get(0).getIQuantity().intValue());
				assertEquals(5, split_month.get(1).getIQuantity().intValue());
				assertEquals(6, split_month.get(2).getIQuantity().intValue());
				assertEquals(13, split_month.get(3).getIQuantity().intValue());
				assertEquals(3, split_month.get(4).getIQuantity().intValue());
				assertEquals(5, split_month.get(5).getIQuantity().intValue());
				System.out.println("ok");

				System.out.println("NewCases: ");
				assertEquals(8, split_month_new.get(0).getIQuantity().intValue());
				assertEquals(6, split_month_new.get(1).getIQuantity().intValue());
				assertEquals(6, split_month_new.get(2).getIQuantity().intValue());
				assertEquals(12, split_month_new.get(3).getIQuantity().intValue());
				assertEquals(4, split_month_new.get(4).getIQuantity().intValue());
				assertEquals(3, split_month_new.get(5).getIQuantity().intValue());
				assertEquals(5, split_month_new.get(6).getIQuantity().intValue());
				System.out.println("ok");

				// текущий запас
				System.out.println("Stock on hand and stock on order: ");
				for(ForecastingMedicineUIAdapter m : merged.getMedicines()){
					if(m.getMedicine().getAbbrevName().contains("Am(500)")){
						assertEquals(2000, m.getBatchesToExpire().get(0).getQuantity().intValue());
						assertEquals(2000, m.getBatchesToExpire().get(1).getQuantity().intValue());
					}
					if(m.getMedicine().getAbbrevName().contains("Am(500/2)")){
						assertEquals(18000, m.getBatchesToExpire().get(0).getQuantity().intValue());

						assertEquals(3000, m.getOrders().get(0).getBatchQuantity().intValue());
						assertEquals(2000, m.getOrders().get(1).getBatchQuantity().intValue());
					}
					if(m.getMedicine().getAbbrevName().contains("Cm(1000)")){
						assertEquals(10000, m.getBatchesToExpire().get(0).getQuantity().intValue());
						assertEquals(15000, m.getBatchesToExpire().get(1).getQuantity().intValue());
					}
					if(m.getMedicine().getAbbrevName().contains("Imi/Cls(500/500)")){
						assertEquals(12000, m.getBatchesToExpire().get(0).getQuantity().intValue());
						assertEquals(8000, m.getBatchesToExpire().get(1).getQuantity().intValue());
					}
				}
				System.out.println("ok");
			}
		}
	}

	//Test percentage calculation
	public void testPercentage(){
		ForecastUIAdapter fUi = loadTestFile(TEST_FORECASTING_NAME_PERS);
		Presenter.setFactory(model);
		ForecastingCalculation fc = new ForecastingCalculation(fUi.getForecastObj(), model);
		// imitate execute method
		fc.clearResults();
		fc.calcCasesOnTreatment();
		fc.calcNewCases();
		fc.calcMedicinesRegimes();
		fc.calcMedicines();
		fc.calcMedicinesResults();

		List<MedicineResume> res = fc.getResume();
		List<MedicineConsumption> mCons = fc.getMedicineConsumption();
		//check the cases quantities
		/*for(int j = 0; j < 15; j++){
			for(MedicineConsumption cons:mCons){// препарат - месяц
				if(cons.getMed().getMedicine().getAbbrevName().contains("Km(1000)"))
					System.out.println(cons.getCons().get(j).getNewCases().doubleValue());
			}
		}*/
		int m4 = 4; //01.10.2014
		int m9 = 9; //01.03.2015
		for(MedicineConsumption cons:mCons){// препарат - месяц
			String abbrev = cons.getMed().getMedicine().getAbbrevName();
			System.out.println(abbrev);

			if(abbrev.contains("Am(500/2)")){
				assertEquals(new Double(1.56), cons.getCons().get(m4).getNewCases().doubleValue());
				assertEquals(new Double(8.28), cons.getCons().get(m9).getNewCases().doubleValue());
			}
			if(abbrev.contains("Cm(1000)")){
				assertEquals(new Double(3.9), cons.getCons().get(m4).getNewCases().doubleValue());
				assertEquals(new Double(20.7), cons.getCons().get(m9).getNewCases().doubleValue());
			}
			if(abbrev.contains("Cs(250)")){
				assertEquals(new Double(0.78), cons.getCons().get(m4).getNewCases().doubleValue());
				assertEquals(new Double(4.26), cons.getCons().get(m9).getNewCases().doubleValue());
			}
			if(abbrev.contains("Eto(250)")){
				assertEquals(new Double(12.22), cons.getCons().get(m4).getNewCases().doubleValue());
				assertEquals(new Double(66.74), cons.getCons().get(m9).getNewCases().doubleValue());
			}
			if(abbrev.contains("Km(1000)")){
				assertEquals(new Double(12.61), cons.getCons().get(m4).getNewCases().doubleValue());
				assertEquals(new Double(66.93), cons.getCons().get(m9).getNewCases().doubleValue());
			}
			if(abbrev.contains("Lfx(250)")){
				assertEquals(new Double(12.35), cons.getCons().get(m4).getNewCases().doubleValue());
				assertEquals(new Double(67.45), cons.getCons().get(m9).getNewCases().doubleValue());
			}
			if(abbrev.contains("PAS(Na)")){
				assertEquals(new Double(12.22), cons.getCons().get(m4).getNewCases().doubleValue());
				assertEquals(new Double(66.74), cons.getCons().get(m9).getNewCases().doubleValue());
			}
			if(abbrev.contains("Pto(250)")){
				assertEquals(new Double(12.74), cons.getCons().get(m4).getNewCases().doubleValue());
				assertEquals(new Double(69.58), cons.getCons().get(m9).getNewCases().doubleValue());
			}
			if(abbrev.contains("Z(500)")){
				assertEquals(new Double(0.65), cons.getCons().get(m4).getNewCases().doubleValue());
				assertEquals(new Double(3.55), cons.getCons().get(m9).getNewCases().doubleValue());
			}
		}

		List<MonthQuantityUIAdapter> newCases = fUi.getNewCases();
		int i = 0;
		BigDecimal q = BigDecimal.ZERO;
		for(MonthQuantityUIAdapter mUi : newCases){
			BigDecimal pers = fUi.calcPercents(mUi.getIQuantity(), 12.00f);
			q = q.add(pers);
			assertTrue(q.compareTo(mCons.get(0).getCons().get(i).getNewCases()) == 0);
			i++;
			if (i>8){
				break; // 8 month first medicine
			}
		}
	}

	//Test percentage calculation
	public void testPercentage_split(){
		ForecastUIAdapter fUi = loadTestFile(TEST_SPLIT_FORECASTING_NAME_PERS);
		Presenter.setFactory(model);
		ForecastingCalculation fc = new ForecastingCalculation(fUi.getForecastObj(), model);
		fUi.shiftNewCasesPercents();
		fUi.shiftNewCasesRegimens();
		fUi.shiftOldCasesPercents();
		fUi.shiftOldCasesRegimens();
		// imitate execute method
		fc.clearResults();
		fc.calcCasesOnTreatment();
		fc.calcNewCases();
		fc.calcMedicinesRegimes();
		fc.calcMedicines();
		fc.calcMedicinesResults();

		List<ForecastingRegimenUIAdapter> regimens = fUi.getRegimes();
		System.out.println("Check percentage of exoected cases per medicine:");
		for(ForecastingRegimenUIAdapter regim : regimens){
			String abbrev = regim.getRegimen().getIntensive().getMedicines().get(0).getAbbrevName();
			System.out.println(abbrev);

			if(abbrev.contains("Am(500/2)")){
				assertEquals(new Float(12), regim.getPercentNewCases());
			}
			if(abbrev.contains("Cm(1000)")){
				assertEquals(new Float(30), regim.getPercentNewCases());
			}
			if(abbrev.contains("Cs(250)")){
				assertEquals(new Float(6), regim.getPercentNewCases());
			}
			if(abbrev.contains("Eto(250)")){
				assertEquals(new Float(94), regim.getPercentNewCases());
			}
			if(abbrev.contains("Km(1000)")){
				assertEquals(new Float(97), regim.getPercentNewCases());
			}
			if(abbrev.contains("Lfx(250)")){
				assertEquals(new Float(95), regim.getPercentNewCases());
			}
			if(abbrev.contains("PAS(Na)")){
				assertEquals(new Float(94), regim.getPercentNewCases());
			}
			if(abbrev.contains("Pto(250)")){
				assertEquals(new Float(98), regim.getPercentNewCases());
			}
			if(abbrev.contains("Z(500)")){
				assertEquals(new Float(5), regim.getPercentNewCases());
			}

		}

		List<MonthQuantityUIAdapter> newCases = fUi.getNewCases();
		for(MonthQuantityUIAdapter m:newCases){
			checkMonth(m);
		}

		List<MedicineResume> res = fc.getResume();
		List<MedicineConsumption> mCons = fc.getMedicineConsumption();
		MedicineConsumption med_am = null;
		MedicineConsumption med_eto = null;
		for(MedicineConsumption m:mCons){
			if(m.getMed().getAbbrevName().equals("Am(500/2)"))
				med_am = m;
			if(m.getMed().getAbbrevName().equals("Eto(250)"))
				med_eto = m;
		}

		int i = 0 , j = 0;
		BigDecimal q = BigDecimal.ZERO;
		if(med_am != null){
			for(MonthQuantityUIAdapter mUi : newCases){
				BigDecimal pers = fUi.calcPercents(mUi.getIQuantity(), 12.00f);

				q = q.add(pers);
				assertTrue(q.compareTo(med_am.getCons().get(i).getNewCases()) == 0);
				i++;

				if (i > 8){
					break; // 8 month first medicine
				}
			}
		}
		q = BigDecimal.ZERO;
		if(med_eto != null){
			for(MonthQuantityUIAdapter mUi : newCases){
				BigDecimal pers = fUi.calcPercents(mUi.getIQuantity(), 94.00f);

				q = q.add(pers);
				assertTrue(q.compareTo(med_eto.getCons().get(j).getNewCases()) == 0);
				j++;
			}
		}
	}

	private void checkMonth(MonthQuantityUIAdapter mon){
		int year = mon.getMonth().getYear();
		switch (mon.getMonth().getMonth()) {
		case 0:
			if(year == 2015)
				assertEquals(16, mon.getIQuantity().intValue());
			break;
		case 1:
			if(year == 2015)
				assertEquals(11, mon.getIQuantity().intValue());
			break;
		case 2:
			if(year == 2015)
				assertEquals(20, mon.getIQuantity().intValue());
			break;
		case 3:
			if(year == 2015)
				assertEquals(7, mon.getIQuantity().intValue());
			break;
		case 4:
			if(year == 2015)
				assertEquals(3, mon.getIQuantity().intValue());
			break;
		case 5:
			if(year == 2015)
				assertEquals(7, mon.getIQuantity().intValue());
			break;
		case 6:
			if(year == 2014)
				assertEquals(1, mon.getIQuantity().intValue());
			if(year == 2015)
				assertEquals(7, mon.getIQuantity().intValue());
			break;
		case 7:
			if(year == 2014)
				assertEquals(2, mon.getIQuantity().intValue());
			if(year == 2015)
				assertEquals(4, mon.getIQuantity().intValue());
			break;
		case 8:
			if(year == 2014)
				assertEquals(2, mon.getIQuantity().intValue());
			break;
		case 9:
			if(year == 2014)
				assertEquals(7, mon.getIQuantity().intValue());
			break;
		case 10:
			if(year == 2014)
				assertEquals(3, mon.getIQuantity().intValue());
			break;
		case 11:
			if(year == 2014)
				assertEquals(8, mon.getIQuantity().intValue());
			break;

		default:
			break;
		}
	}
	/**
	 * Find the corresponding quantity in the source forecasting
	 * Rules:
	 * <ul>
	 * <li>if isEnrolled try to search in source enrolled first, if won't found then try to search in expected
	 * </ul>
	 * @param mq month and quantity
	 * @param forecast the source FC
	 * @return quantity or 0 if not found
	 */
	private int findQuantity(MonthQuantity mq,
			List<MonthQuantity> list) {
		int ret = 0;
		for(MonthQuantity mqSrc :list){
			if(mqSrc.getMonth().getYear() == mq.getMonth().getYear() && 
					mqSrc.getMonth().getMonth()== mq.getMonth().getMonth()){
				ret = mqSrc.getIQuantity();
			}
		}
		return ret;
	}

	public void testPercentage_MergeValidate(){
		List<ForecastUIAdapter> listFUi = new ArrayList<ForecastUIAdapter>();
		ForecastUIAdapter f1 = loadTestFile(TEST_1);
		listFUi.add(f1);
		ForecastUIAdapter f2 = loadTestFile(TEST_2);
		listFUi.add(f2);
		ForecastUIAdapter f3 = loadTestFile(TEST_3);
		listFUi.add(f3);
		Presenter.setFactory(model);
		ForecastMergeControl controler = new ForecastMergeControl(listFUi,  new SysOut());
		if(controler.validate())
			System.out.println("ForecastMergeControl validate OK");
	}

	public void testPercentage_Merge(){
		List<ForecastUIAdapter> listFUi = new ArrayList<ForecastUIAdapter>();
		ForecastUIAdapter f1 = loadTestFile(TEST_1);
		listFUi.add(f1);
		ForecastUIAdapter f2 = loadTestFile(TEST_2);
		listFUi.add(f2);
		ForecastUIAdapter f3 = loadTestFile(TEST_3);
		listFUi.add(f3);
		Presenter.setFactory(model);

		ForecastMergeControl controler = new ForecastMergeControl(listFUi,  new SysOut());
		if(controler.validate()){
			ForecastUIAdapter merged = controler.merge();
			if (merged != null){
				System.out.println("ForecastMergeControl merge ok");
				ForecastingCalculation fc_merge = new ForecastingCalculation(merged.getForecastObj(), model);
				fc_merge.clearResults();
				fc_merge.calcCasesOnTreatment();
				fc_merge.calcNewCases();
				fc_merge.calcMedicinesRegimes();
				fc_merge.calcMedicines();
				fc_merge.calcMedicinesResults();

				List<MedicineResume> res = fc_merge.getResume();
				List<ForecastingRegimenUIAdapter> regimens = merged.getRegimes();

				for(ForecastingRegimenUIAdapter regim : regimens){
					String abbrev = regim.getRegimen().getIntensive().getMedicines().get(0).getAbbrevName();
					System.out.println(abbrev);

					if(abbrev.contains("Am(500/2)")){
						assertEquals(new Float(12), regim.getPercentCasesOnTreatment());
						assertEquals(new Float(12), regim.getPercentNewCases());
					}
					if(abbrev.contains("Cm(1000)")){
						assertEquals(new Float(30), regim.getPercentCasesOnTreatment());
						assertEquals(new Float(30), regim.getPercentNewCases());
					}
					if(abbrev.contains("Cs(250)")){
						assertEquals(new Float(6), regim.getPercentCasesOnTreatment());
						assertEquals(new Float(6), regim.getPercentNewCases());
					}
					if(abbrev.contains("Eto(250)")){
						assertEquals(new Float(94), regim.getPercentCasesOnTreatment());
						assertEquals(new Float(94), regim.getPercentNewCases());
					}
					if(abbrev.contains("Lfx(250)")){
						assertEquals(new Float(95), regim.getPercentCasesOnTreatment());
						assertEquals(new Float(0), regim.getPercentNewCases());
					}
					if(abbrev.contains("Z(500)")){
						assertEquals(new Float(25), regim.getPercentCasesOnTreatment());
						assertEquals(new Float(0), regim.getPercentNewCases());
					}
				}

				int i = 0;
				for(MonthQuantityUIAdapter mon:merged.getCasesOnTreatment()){
					MonthUIAdapter month = mon.getMonth();
					int quant = mon.getIQuantity().intValue();
					if(month.getYear() == 2012){
						if(month.getMonth() == 7 || month.getMonth() == 8)
							assertEquals(3, quant);
						else
							assertEquals(0, quant);
						i++;
					}else if(month.getYear() == 2013){
						assertEquals(0, quant);
						i++;
					}else if(month.getYear() == 2014){
						switch (month.getMonth()) {
						case 0:
							assertEquals(6, quant);
							break;
						case 1:
							assertEquals(3, quant);
							break;
						case 2:
							assertEquals(9, quant);
							break;
						case 3:
							assertEquals(15, quant);
							break;
						case 4:
							assertEquals(6, quant);
							break;
						case 5:
							assertEquals(9, quant);
							break;

						default:
							assertEquals(0, quant);
							break;
						}
						i++;
					}
				}
				assertEquals(i, 23);

				i = 0;
				for(MonthQuantityUIAdapter mon:merged.getNewCases()){
					MonthUIAdapter month = mon.getMonth();
					int quant = mon.getIQuantity().intValue();
					if(month.getYear() == 2014){
						if(month.getMonth() == 5 || month.getMonth() == 6)
							assertEquals(3, quant);
						else if(month.getMonth() == 7 || month.getMonth() == 8)
							assertEquals(6, quant);
						else if(month.getMonth() == 9)
							assertEquals(21, quant);
						else if(month.getMonth() == 10)
							assertEquals(9, quant);
						else if(month.getMonth() == 11)
							assertEquals(24, quant);
						i++;
					}else if(month.getYear() == 2015){
						switch (month.getMonth()) {
						case 0:
							assertEquals(48, quant);
							break;
						case 1:
							assertEquals(33, quant);
							break;
						case 2:
							assertEquals(60, quant);
							break;
						case 3:
							assertEquals(21, quant);
							break;
						case 4:
							assertEquals(9, quant);
							break;
						case 5:
							assertEquals(21, quant);
							break;
						case 6:
							assertEquals(21, quant);
							break;
						case 7:
							assertEquals(12, quant);
							break;

						default:
							assertEquals(0, quant);
							break;
						}
						i++;
					}
				}
				assertEquals(i, 14);
			}
		}
	}

	public void testExpectedToPers(){
		Presenter.setFactory(model);
		ForecastUIAdapter fUi = loadTestFile("Fictitia 1-1-2013 fast.qtb");
		assertEquals(0,fUi.getForecastObj().getCasesOnTreatment().size());
		assertEquals(0,fUi.getForecastObj().getNewCases().size());
		assertFalse(fUi.isExpectedCasesPercents());

		assertTrue(fUi.expectedToPers());

		assertTrue(fUi.isExpectedCasesPercents());
		assertEquals(0,fUi.getForecastObj().getCasesOnTreatment().size());
		assertEquals(100,fUi.getNewCases().get(2).getIQuantity().intValue());
		fUi.setName("testExpectedToPers.qtb");
		try {
			model.storeForecast(fUi.getForecastObj(), testDocPath);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testExpectedQuan(){
		Presenter.setFactory(model);
		ForecastUIAdapter fUi = loadTestFile("testExpectedToPers.qtb");
		assertTrue(fUi.isExpectedCasesPercents());
		assertTrue(fUi.hasExpectedPersQuantities());

		fUi.getForecastObj().getRegimes().get(0).setPercentNewCases(50);
		assertTrue(fUi.expectedToQuantity());

		assertFalse(fUi.isExpectedCasesPercents());
		assertEquals(70,fUi.getRegimes().get(0).getNewCases().get(3).getIQuantity().intValue());
		fUi.setName("testExpectedToQuan.qtb");
		try {
			model.storeForecast(fUi.getForecastObj(), testDocPath);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testEnrolledToPers(){
		Presenter.setFactory(model);
		ForecastUIAdapter fUi = loadTestFile("Fictitia 1-1-2013 fast.qtb");
		assertFalse(fUi.isEnrolledCasesPercents());

		assertTrue(fUi.enrolledToPers());

		assertEquals(72,fUi.getCasesOnTreatment().get(2).getIQuantity().intValue());
		assertTrue(fUi.isEnrolledCasesPercents());
		fUi.setName("testEnrolledToPers.qtb");
		try {
			model.storeForecast(fUi.getForecastObj(), testDocPath);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testEnrolledToQuan(){
		Presenter.setFactory(model);
		ForecastUIAdapter fUi = loadTestFile("testEnrolledToPers.qtb");
		assertTrue(fUi.isEnrolledCasesPercents());

		fUi.getForecastObj().getRegimes().get(0).setPercentCasesOnTreatment(50);
		assertTrue(fUi.enrolledToQuantity());

		assertEquals(5,fUi.getRegimes().get(0).getCasesOnTreatment().get(1).getIQuantity().intValue());
		assertFalse(fUi.isEnrolledCasesPercents());
		fUi.setName("testEnrolledToQuan.qtb");
		try {
			model.storeForecast(fUi.getForecastObj(), testDocPath);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * It is not real test
	 * Only to prepare excel file for chart demo purpose
	 */
	public void testDeliveriesForChart(){
		Presenter.setFactory(model);
		XSSFWorkbook workbook = new XSSFWorkbook();
		CreationHelper createHelper = workbook.getCreationHelper();
		XSSFCellStyle dateMYStyle= workbook.createCellStyle();
		dateMYStyle.setDataFormat(createHelper.createDataFormat().getFormat("mmm-yyyy"));
		dateMYStyle.setAlignment(HorizontalAlignment.CENTER);

		//ForecastUIAdapter fUi = loadTestFile("Fictitia_2015.qtb");
		ForecastUIAdapter fUi = loadTestFile("Fictitia 1-1-2013MinZero.qtb");
		fUi.setScenario(false);


		ForecastingCalculation fCalc = new ForecastingCalculation(fUi.getForecastObj(), model);
		fCalc.clearResults();
		fCalc.calcCasesOnTreatment();
		fCalc.calcNewCases();
		fCalc.calcMedicinesRegimes();
		fCalc.calcMedicines();
		fCalc.calcMedicinesResults();
		fCalc.getResume();
		OrderCalculator oC = new OrderCalculator(fCalc);
		oC.execute();
		for(MedicineConsumption mc : fCalc.getMedicineConsumption()){
			XSSFSheet sheet = workbook.createSheet(mc.getMed().getAbbrevName().replace("/", " "));
			int rowNo = 5;
			paintExcelHead(sheet, rowNo);
			for(ConsumptionMonth cons : mc.getAccelDeliveries()){
				paintExcelRow(dateMYStyle, sheet, rowNo, cons);
				rowNo++;
			}
			rowNo=rowNo+4;
			paintExcelHead(sheet, rowNo);
			for(ConsumptionMonth cons : mc.getRegularDeliveries()){
				paintExcelRow(dateMYStyle, sheet, rowNo, cons);
				rowNo++;
			}
		}
		try {
			File outFile = new File(testPath + "/newChart.xlsx");
			FileOutputStream outStream;
			outStream = new FileOutputStream(outFile);
			workbook.write(outStream);
			outStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * Test the pessimistic scenario 
	 * Creates excel file named pessimist.xlsx
	 */
	public void testPessimisticScenario(){
		Presenter.setFactory(model);
		XSSFWorkbook workbook = new XSSFWorkbook();
		CreationHelper createHelper = workbook.getCreationHelper();
		XSSFCellStyle dateMYStyle= workbook.createCellStyle();
		dateMYStyle.setDataFormat(createHelper.createDataFormat().getFormat("mmm-yyyy"));
		dateMYStyle.setAlignment(HorizontalAlignment.CENTER);

		ForecastUIAdapter fUi = loadTestFile("Fictitia 1-1-2013MaxZero.qtb");
		//ForecastUIAdapter fUi = loadTestFile("TestMiddleMonth.qtb");

		//fUi.setMinStock(1);
		//fUi.setMaxStock(4);
		fUi.setScenario(true); //pessimistic!
		ForecastingCalculation fCalc = new ForecastingCalculation(fUi.getForecastObj(), model);
		fCalc.clearResults();
		fCalc.calcCasesOnTreatment();
		fCalc.calcNewCases();
		fCalc.calcMedicinesRegimes();
		fCalc.calcMedicines();
		fCalc.calcMedicinesResults();
		fCalc.getResume();
		OrderCalculator calc = new OrderCalculator(fCalc);
		calc.execute();
		for(MedicineConsumption mc : fCalc.getMedicineConsumption()){
			XSSFSheet sheet = workbook.createSheet(mc.getMed().getAbbrevName().replace("/", " "));
			int rowNo = 5;
			paintPessimisticHead(sheet, rowNo);
			for(ConsumptionMonth cons : mc.getAccelDeliveries()){
				paintPessimisticRow(dateMYStyle, sheet, rowNo, cons);
				rowNo++;
			}
			rowNo=rowNo+2;
			/* paintPessimisticHead(sheet, rowNo);*/
			for(ConsumptionMonth cons : mc.getRegularDeliveries()){
				paintPessimisticRow(dateMYStyle, sheet, rowNo, cons);
				rowNo++;
			}
		}
		try {
			File outFile = new File(testPath + "/pessimist.xlsx");
			FileOutputStream outStream;
			outStream = new FileOutputStream(outFile);
			workbook.write(outStream);
			outStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * Paint row for a "pessimistic" sheet
	 * @param dateMYStyle
	 * @param sheet
	 * @param rowNo
	 * @param cons
	 */
	private void paintPessimisticRow(XSSFCellStyle dateMYStyle, XSSFSheet sheet, int rowNo, ConsumptionMonth cons) {
		getCell(sheet, rowNo,0).setCellValue(cons.getMonth().getAnyDate(1).getTime());
		getCell(sheet, rowNo,0).setCellStyle(dateMYStyle);
		getCell(sheet, rowNo,1).setCellValue(cons.getMinStock().intValue());
		getCell(sheet, rowNo,2).setCellValue(cons.getMaxStock().intValue());
		getCell(sheet, rowNo,3).setCellValue(cons.getOnHandInt());
		getCell(sheet, rowNo,4).setCellValue(cons.getMissingInt());
		getCell(sheet, rowNo,5).setCellValue(cons.getOrder());
		getCell(sheet, rowNo,6).setCellValue(cons.getExpired());
		getCell(sheet, rowNo,7).setCellValue(cons.getpStock().intValue());
		getCell(sheet, rowNo,8).setCellValue(cons.getConsAllInt()*-1);
		getCell(sheet, rowNo,9).setCellValue(cons.getDelivery().intValue());
	}
	/**
	 * Paint header for a "pessimistic" sheet
	 * @param sheet
	 * @param rowNo
	 */
	private void paintPessimisticHead(XSSFSheet sheet, int rowNo) {
		getCell(sheet,rowNo-1,0).setCellValue("Date");
		getCell(sheet,rowNo-1,1).setCellValue("MinStock");
		getCell(sheet,rowNo-1,2).setCellValue("MaxStock");
		getCell(sheet,rowNo-1,3).setCellValue("On hand");
		getCell(sheet,rowNo-1,4).setCellValue("Needed");
		getCell(sheet,rowNo-1,5).setCellValue("Prev order");
		getCell(sheet,rowNo-1,6).setCellValue("Expired");
		getCell(sheet,rowNo-1,7).setCellValue("Stock");
		getCell(sheet,rowNo-1,8).setCellValue("Consum");
		getCell(sheet,rowNo-1,9).setCellValue("To order");

	}

	private void paintExcelRow(XSSFCellStyle dateMYStyle, XSSFSheet sheet, int rowNo, ConsumptionMonth cons) {
		getCell(sheet, rowNo,0).setCellValue(cons.getMonth().getAnyDate(1).getTime());
		getCell(sheet, rowNo,0).setCellStyle(dateMYStyle);
		getCell(sheet, rowNo,1).setCellValue(cons.getMinStock().intValue());
		getCell(sheet, rowNo,2).setCellValue(cons.getMaxStock().intValue());
		getCell(sheet, rowNo,3).setCellValue(cons.getMinFullStock().intValue());
		getCell(sheet, rowNo,4).setCellValue(cons.getMaxFullStock().intValue());
		getCell(sheet, rowNo,5).setCellValue(cons.getMissingInt());
		getCell(sheet, rowNo,6).setCellValue(cons.getpStock().intValue());
		getCell(sheet, rowNo,7).setCellValue(cons.getConsAllInt()*-1);
		getCell(sheet, rowNo,8).setCellValue(cons.getDelivery().intValue());
	}

	private void paintExcelHead(XSSFSheet sheet, int rowNo) {
		getCell(sheet,rowNo-1,0).setCellValue("Date");
		getCell(sheet,rowNo-1,1).setCellValue("MinStock");
		getCell(sheet,rowNo-1,2).setCellValue("MaxStock");
		getCell(sheet,rowNo-1,3).setCellValue("MinChartStock");
		getCell(sheet,rowNo-1,4).setCellValue("MaxChartStock");
		getCell(sheet,rowNo-1,5).setCellValue("Missing");
		getCell(sheet,rowNo-1,6).setCellValue("Stock");
		getCell(sheet,rowNo-1,7).setCellValue("Consum");
		getCell(sheet,rowNo-1,8).setCellValue("Delivery");
	}

	private XSSFRow getRow(XSSFSheet sheet, int row) {
		XSSFRow res = sheet.getRow(row);
		if (res == null){
			res = sheet.createRow(row);
		}
		return res;
	}
	private XSSFCell getCell(XSSFSheet sheet, int rowNo, int col) {
		XSSFRow sRow = getRow(sheet,rowNo);
		XSSFCell res = sRow.getCell(col);
		if(res==null){
			res = sRow.createCell(col);
		}
		return res;
	}


	/**
	 * Test delivery calculator
	 */
	public void testDeliveryExactCalculatorPessimist(){
		Presenter.setFactory(model);
		//very traditional
		ForecastUIAdapter fUi = loadTestFile("TestMiddleMonth.qtb");
		fUi.setScenario(true);
		checkPessimistDeliveries(fUi);
		//traditional buffer stock
		fUi = loadTestFile("Fictitia 1-1-2013MinZero.qtb");
		fUi.setScenario(true);
		checkPessimistDeliveries(fUi);
		// buffer stock is 0, min stock is > 0
		fUi = loadTestFile("Fictitia 1-1-2013 slow.qtb");
		fUi.setScenario(true);
		checkPessimistDeliveries(fUi);
		// buffer stock is 0, min stockis > 0, max stock is 0 (infinity)
		fUi = loadTestFile("Fictitia 1-1-2013MaxZero.qtb");
		fUi.setScenario(true);
		checkPessimistDeliveries(fUi);
		//buffer stock is >0, min stock iz zero, max stock is zero (infinity)
		fUi = loadTestFile("Fictitia 1-1-2013MinMaxZero.qtb");
		fUi.setScenario(true);
		checkPessimistDeliveries(fUi);
		//Completely different
		fUi = loadTestFile("Fictitia_2015-2.qtb");
		fUi.setScenario(true);
		checkPessimistDeliveries(fUi);
	}
	/**
	 * Check pessimist deliveries, see criteria in comments
	 */
	private void checkPessimistDeliveries(ForecastUIAdapter fUi) {
		ForecastingCalculation fCalc = new ForecastingCalculation(fUi.getForecastObj(), model);
		fCalc.clearResults();
		fCalc.calcCasesOnTreatment();
		fCalc.calcNewCases();
		fCalc.calcMedicinesRegimes();
		fCalc.calcMedicines();
		fCalc.calcMedicinesResults();
		fCalc.getResume();
		OrderCalculator oC = new OrderCalculator(fCalc);
		oC.execute();
		for (MedicineConsumption mCons :fCalc.getMedicineConsumption()){
			//calculate control numbers and check special cases
			BigDecimal missing = BigDecimal.ZERO;
			BigDecimal deliveries = BigDecimal.ZERO;
			for(ConsumptionMonth cM : mCons.getCons()){
				missing = missing.add(cM.getMissing());
				deliveries = deliveries.add(cM.getDelivery());
				String mess = cM.getMonth() +" "+ fUi.getName() +" "+ mCons.getMed().getAbbrevName();
/*				if(cM.getMaxStock().compareTo(BigDecimal.ZERO)>0){
					if(cM.getDelivery().compareTo(BigDecimal.ZERO)>0){
						if(cM.getMaxStock().compareTo(cM.getpStock())<0 ){
							mess = mess + " maximum overflow at delivery month";
							assertFalse(mess, true);
						}
					}
					It is possible since 2017-05-23
				}*/
				if(cM.getMinStock().compareTo(cM.getpStock())>0){
					mess = mess + " minimum violation";
					assertFalse(mess, true);
				}
				if(cM.getMinStock().compareTo(BigDecimal.ZERO)==0){
					//special case
					if(cM.getMissing().compareTo(cM.getpStock())>0){
						mess = mess + " stock after delivery do not cover missing this month";
						assertFalse(mess, true);
					}
				}
				assertTrue(mCons.getMed().getAbbrevName() + " missing not covered by deliveries",
						deliveries.compareTo(missing)>=0);
			}

		}

	}

	/**
	 * Test delivery orders builder
	 * Both TCs should be at once! Because cost, not quantity
	 */
	public void testDeliveryOrdersControl(){
		//build consumptions
		Presenter.setFactory(model);
		//traditional
		ForecastUIAdapter fUi = loadTestFile("Fictitia 1-1-2013 fast.qtb");
		checkDeliveryOrders(fUi);
		//from middle
		fUi = loadTestFile("TestMiddleMonth.qtb");
		checkDeliveryOrders(fUi);
	}
	/**
	 * Check orders that built on deliveries
	 * @param fUi
	 */
	private void checkDeliveryOrders(ForecastUIAdapter fUi) {
		ForecastingCalculation fCalc = new ForecastingCalculation(fUi.getForecastObj(), model);
		fCalc.clearResults();
		fCalc.calcCasesOnTreatment();
		fCalc.calcNewCases();
		fCalc.calcMedicinesRegimes();
		fCalc.calcMedicines();
		fCalc.calcMedicinesResults();
		fCalc.getResume();
		OrderCalculator oC = new OrderCalculator(fCalc);
		oC.execute();
		ForecastingTotal totalR = new ForecastingTotal(fUi, oC.getMedicineTotals(), ForecastingTotal.REGULAR_TOTAL);
		ForecastingTotal totalA = new ForecastingTotal(fUi, oC.getMedicineTotals(), ForecastingTotal.ACCEL_TOTAL);
		DeliveryOrdersControl control = new DeliveryOrdersControl(fUi.getLeadTime(),
				totalR, totalA,null);
		control.setConsumptions(fCalc.getMedicineConsumption());
		control.buildAllExact();
		System.out.println("Accelerated");
		BigDecimal res = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
		for(DeliveryOrderUI order : control.getAccelerated()){
			res = res.add(order.getMedCost());
			System.out.println(order);
		}
		System.out.println(res +  " ---- " + totalA.getMedTotal());
		assertTrue(totalA.getMedTotal().compareTo(res)==0);

		System.out.println("Regular");
		res = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
		for(DeliveryOrderUI order : control.getRegular()){
			res = res.add(order.getMedCost());
			System.out.println(order);
		}
		System.out.println(res +  " ---- " + totalR.getMedTotal());
		assertTrue(totalR.getMedTotal().compareTo(res)==0);
	}

}
