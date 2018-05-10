package org.msh.quantb.services.calc;

import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.msh.quantb.model.forecast.Month;
import org.msh.quantb.model.gen.MedicineRegimen;
import org.msh.quantb.model.mvp.ModelFactory;
import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.MedicationUIAdapter;
import org.msh.quantb.services.io.MonthUIAdapter;

/**
 * Test utilities
 * @author alexey
 *
 */
public class TestUtils extends TestCase {
	public void testWeeklyFreq(){
		ModelFactory factory = new ModelFactory("");
		Month m = factory.createMonth(2013, 2);
		MonthUIAdapter mU = new MonthUIAdapter(m);
		MedicineRegimen med = factory.createMedication(null, 2, 1, 7);
		MedicationUIAdapter medU = new MedicationUIAdapter(med);
		WeeklyFrequency wf = new WeeklyFrequency();
		int days = wf.calculateDays(mU,medU);
		assertEquals(31, days);
		medU.setDaysPerWeek(6);
		days = wf.calculateDays(mU,medU);
		assertEquals(26, days);
		medU.setDaysPerWeek(5);
		days = wf.calculateDays(mU,medU);
		assertEquals(21, days);
		medU.setDaysPerWeek(4);
		days = wf.calculateDays(mU,medU);
		assertEquals(17, days);
		medU.setDaysPerWeek(3);
		days = wf.calculateDays(mU,medU);
		assertEquals(13, days);
		medU.setDaysPerWeek(2);
		days = wf.calculateDays(mU,medU);
		assertEquals(9, days);
		medU.setDaysPerWeek(1);
		days = wf.calculateDays(mU,medU);
		assertEquals(5, days);
		medU.setDaysPerWeek(0);
		days = wf.calculateDays(mU,medU);
		assertEquals(0, days);
	}
	
	public void testMonth(){
		ModelFactory factory = new ModelFactory("");
		Month m = factory.createMonth(2015, 0);
		MonthUIAdapter mU = new MonthUIAdapter(m);
		MedicineRegimen med = factory.createMedication(null, 2, 1, 7);
		MedicationUIAdapter medU = new MedicationUIAdapter(med);
		WeeklyFrequency wf = new WeeklyFrequency();
		int days = wf.calculateDays(mU,medU);
		assertEquals(31, days);
		//printDays(wf);
		medU.setDaysPerWeek(6);
		days = wf.calculateDays(mU,medU);
		assertEquals(26, days);
		//printDays(wf);
		medU.setDaysPerWeek(5);
		days = wf.calculateDays(mU,medU);
		assertEquals(21, days);
		//printDays(wf);
		medU.setDaysPerWeek(4);
		days = wf.calculateDays(mU,medU);
		assertEquals(16, days);
		//printDays(wf);
		medU.setDaysPerWeek(3);
		days = wf.calculateDays(mU,medU);
		assertEquals(12, days);
		//printDays(wf);
		medU.setDaysPerWeek(2);
		days = wf.calculateDays(mU,medU);
		assertEquals(8, days);
		//printDays(wf);
		medU.setDaysPerWeek(1);
		days = wf.calculateDays(mU,medU);
		assertEquals(4, days);
		//printDays(wf);
		medU.setDaysPerWeek(0);
		days = wf.calculateDays(mU,medU);
		assertEquals(0, days);
		//printDays(wf);
	}
	
	private void printDays(WeeklyFrequency wf){
		switch (wf.getDaysInAWeek()) {
		case 1:
			System.out.println("DaysPerWeek=0: ");
			System.out.println("Sunday");
			System.out.println("Monday");
			System.out.println("Tuesday");
			System.out.println("Wednesday");
			System.out.println("Thursday");
			System.out.println("Friday");
			System.out.println("Saturday");
			break;
		case 2:
			System.out.println("DaysPerWeek=1: ");
			System.out.println("Monday");
			System.out.println("Tuesday");
			System.out.println("Wednesday");
			System.out.println("Thursday");
			System.out.println("Friday");
			System.out.println("Saturday");
			break;
		case 3:
			System.out.println("DaysPerWeek=2: ");
			System.out.println("Tuesday");
			System.out.println("Wednesday");
			System.out.println("Thursday");
			System.out.println("Friday");
			System.out.println("Saturday");
			break;
		case 4:
			System.out.println("DaysPerWeek=3: ");
			System.out.println("Wednesday");
			System.out.println("Thursday");
			System.out.println("Friday");
			System.out.println("Saturday");
			break;
		case 5:
			System.out.println("DaysPerWeek=4: ");
			System.out.println("Thursday");
			System.out.println("Friday");
			System.out.println("Saturday");
			break;
		case 6:
			System.out.println("DaysPerWeek=5: ");
			System.out.println("Friday");
			System.out.println("Saturday");
			break;
		case 7:
			System.out.println("DaysPerWeek=6: ");
			System.out.println("Saturday");
			break;

		default:
			break;
		}
	}
	/**
	 * to understand and check DaysBetween in DateUtils class
	 */
	public void testDaysBetween(){
		Calendar cal = GregorianCalendar.getInstance();
		Calendar cal1 = GregorianCalendar.getInstance();
		// if both dates equals, really 1 day interval
		assertEquals(0, DateUtils.daysBetween(cal.getTime(), cal1.getTime()));
		cal1.add(Calendar.DAY_OF_MONTH, 3);
		// both days really included
		assertEquals(3, DateUtils.daysBetween(cal.getTime(), cal1.getTime()));
	}
	/**
	 * calculate period consumption based on default date
	 */
	public void testCalculatePeriod(){
		WeeklyFrequency wf = new WeeklyFrequency();
		ModelFactory factory = new ModelFactory("");
		Month m = factory.createMonth(2013, 2);
		MonthUIAdapter mU = new MonthUIAdapter(m);
		// Friday
		testPeriod(wf, mU, 1,1,7); 
		testPeriod(wf, mU, 1,1,6); 
		testPeriod(wf, mU, 1,0,5); 
		testPeriod(wf, mU, 1,0,4); 
		testPeriod(wf, mU, 1,0,3); 
		testPeriod(wf, mU, 1,0,2); 
		testPeriod(wf, mU, 1,0,1);	
		//Wednesday
		testPeriod(wf, mU, 20,1,7); 
		testPeriod(wf, mU, 20,1,6); 
		testPeriod(wf, mU, 20,1,5); 
		testPeriod(wf, mU, 20,1,4); 
		testPeriod(wf, mU, 20,0,3); 
		testPeriod(wf, mU, 20,0,2); 
		testPeriod(wf, mU, 20,0,1); 	
		
		//Monday
		testPeriod(wf, mU, 18,1,7); 
		testPeriod(wf, mU, 18,1,6); 
		testPeriod(wf, mU, 18,1,5); 
		testPeriod(wf, mU, 18,1,4); 
		testPeriod(wf, mU, 18,1,3); 
		testPeriod(wf, mU, 18,1,2); 
		testPeriod(wf, mU, 18,0,1); //sunday only
		//Sunday all, because begin in default
		testPeriod(wf, mU, 17,1,7); 
		testPeriod(wf, mU, 17,1,6); 
		testPeriod(wf, mU, 17,1,5); 
		testPeriod(wf, mU, 17,1,4); 
		testPeriod(wf, mU, 17,1,3); 
		testPeriod(wf, mU, 17,1,2); 
		testPeriod(wf, mU, 17,1,1);
		
	}
	/**
	 * Test one period
	 * @param wf object to test
	 * @param mU month
	 * @param expect expected
	 * @param dayNo day number
	 * @param freq frequency in week
	 */
	private void testPeriod(WeeklyFrequency wf, MonthUIAdapter mU, int dayNo,  Integer expect, int freq) {
		Integer days = wf.calculatePeriod(mU, dayNo, dayNo, freq);
		assertEquals(expect, days);
	}
	
	public void testCleanTime(){
		Calendar cal = GregorianCalendar.getInstance();
		DateUtils.cleanTime(cal);
		assertEquals(0, cal.get(Calendar.MILLISECOND));
	}
	/**
	 * Determine max Integer because of Bangladesh bug
	 */
	public void testMaxInt(){
		System.out.println(Integer.MAX_VALUE);
		
	}
	
	public void testDaysSpell(){
		System.out.println(DateParser.getDaysLabel(822));
	}
	
}
