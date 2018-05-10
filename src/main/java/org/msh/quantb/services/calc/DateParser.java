package org.msh.quantb.services.calc;

import java.util.Date;

import org.msh.quantb.services.mvp.Messages;

/**
 * Work with dates and convert them to spoken representation
 * 
 * @author Andrey
 * 
 */
public class DateParser {

	/**
	 * Get spoken presentation of day (Only label)
	 * @param days days
	 * @return spoken presentation of day
	 */
	public static String getDaysLabel(int days) {
		String s = "";
		if (days >= 0) {
			int digit = Integer.valueOf(String.valueOf(days).substring(String.valueOf(days).length() - 1, String.valueOf(days).length()));
			if ((digit == 1 && days > 20) || (days == 1)) {
				String language = Messages.getLanguage();
				s = Messages.getString((language.isEmpty() && days == 1) || !language.isEmpty() ? "ForecastingDocumentWindow.tbSummary.onedays" : "ForecastingDocumentWindow.tbSummary.days");
			} else if (digit > 1 && digit < 5 && (days > 20 || days < 10)) {
				s = Messages.getString("ForecastingDocumentWindow.tbSummary.severaldays");
			} else if (digit == 0 || digit > 4 || days > 10) {
				s = Messages.getString("ForecastingDocumentWindow.tbSummary.days");
			}
		}
		return s;
	}
	
	/**
	 * Get spoken presentation of day (Only label)
	 * @param days days
	 * @return spoken presentation of day
	 */
	public static String getMonthLabel(int month) {
		String s = "";
		if (month >= 0) {
			int digit = Integer.valueOf(String.valueOf(month).substring(String.valueOf(month).length() - 1, String.valueOf(month).length()));
			if ((digit == 1 && month > 20) || (month == 1)) {
				String language = Messages.getLanguage();
				s = Messages.getString((language.isEmpty() && month == 1) || !language.isEmpty() ? "ForecastingDocumentWindow.tbParameters.onemonths" : "ForecastingDocumentWindow.tbParameters.months");
			} else if (digit > 1 && digit < 5 && (month > 20 || month < 10)) {
				s = Messages.getString("ForecastingDocumentWindow.tbParameters.severalmonths");
			} else if (digit == 0 || digit > 4 || month > 10) {
				s = Messages.getString("ForecastingDocumentWindow.tbParameters.months");
			}
		}
		return s;
	}

	/**
	 * Get spoken presentation of month
	 * 
	 * @param month month
	 * @return spoken representation of month
	 */
	public static String getMonths(int month) {
		String s = "";
		if (month >= 0) {
			int digit = Integer.valueOf(String.valueOf(month).substring(String.valueOf(month).length() - 1, String.valueOf(month).length()));
			if ((digit == 1 && month > 20) || (month == 1)) {
				String language = Messages.getLanguage();
				s = s + month + " " + Messages.getString((language.isEmpty() && month == 1) || !language.isEmpty() ? "ForecastingDocumentWindow.tbParameters.onemonths" : "ForecastingDocumentWindow.tbParameters.months");
			} else if (digit > 1 && digit < 5 && (month > 20 || month < 10)) {
				s = s + month + " " + Messages.getString("ForecastingDocumentWindow.tbParameters.severalmonths");
			} else if (digit == 0 || digit > 4 || month > 10) {
				s = s + month + " " + Messages.getString("ForecastingDocumentWindow.tbParameters.months");
			}
		}
		return s;
	}

	/**
	 * Get spoken presentation of day
	 * 
	 * @param day day
	 * @return spoketn representation of day
	 */
	public static String getDays(int day) {
		String s = "";
		if (day >= 0) {
			int digit = Integer.valueOf(String.valueOf(day).substring(String.valueOf(day).length() - 1, String.valueOf(day).length()));
			if ((digit == 1 && day > 20) || (day == 1)) {
				String language = Messages.getLanguage();
				s = s + day + " " + Messages.getString((language.isEmpty() && day == 1) || !language.isEmpty() ? "ForecastingDocumentWindow.tbSummary.onedays" : "ForecastingDocumentWindow.tbSummary.days");
			} else if (digit > 1 && digit < 5 && (day > 20 || day < 10)) {
				s = s + day + " " + Messages.getString("ForecastingDocumentWindow.tbSummary.severaldays");
			} else if (digit == 0 || digit > 4 || day > 10) {
				s = s + day + " " + Messages.getString("ForecastingDocumentWindow.tbSummary.days");
			}
		}
		return s;
	}

	/**
	 * Get duration of period betweem two dates
	 * 
	 * @param from from date
	 * @param to end date
	 * @return spoken representation of duration of period
	 */
	public static String getDurationOfPeriod(Date from, Date to) {
		Date iniDate = from;
		Date endDate = to;
		//endDate = DateUtils.incDays(endDate, 1);
		int months = DateUtils.monthsBetween(iniDate, endDate);
		String language = Messages.getLanguage();
		String s = "";
		if (months > 0) {
			if (!s.isEmpty()) s = s + ", ";
			int digit = Integer.valueOf(String.valueOf(months).substring(String.valueOf(months).length() - 1, String.valueOf(months).length()));
			if ((digit == 1 && months > 20) || (months == 1)) {
				s = s + months + " " + Messages.getString((language.isEmpty() && months == 1) || !language.isEmpty() ? "ForecastingDocumentWindow.tbParameters.onemonths" : "ForecastingDocumentWindow.tbParameters.months");
			} else if (digit > 1 && digit < 5 && (months > 20 || months < 10)) {
				s = s + months + " " + Messages.getString("ForecastingDocumentWindow.tbParameters.severalmonths");
			} else if (digit == 0 || digit > 4 || months > 10) {
				s = s + months + " " + Messages.getString("ForecastingDocumentWindow.tbParameters.months");
			}
			iniDate = DateUtils.incMonths(iniDate, months);
		}

		int days = DateUtils.daysBetween(iniDate, endDate);
		if (days >= 1) {
			//days = days - 1;
			int digit = Integer.valueOf(String.valueOf(days).substring(String.valueOf(days).length() - 1, String.valueOf(days).length()));
			if (!s.isEmpty()) s = s + ", ";
			if ((digit == 1 && days > 20) || (days == 1)) {
				s = s + days + " " + Messages.getString((language.isEmpty() && days == 1) || !language.isEmpty() ? "ForecastingDocumentWindow.tbSummary.onedays" : "ForecastingDocumentWindow.tbSummary.days");
			} else if (digit > 1 && digit < 5 && (days > 20 || days < 10)) {
				s = s + days + " " + Messages.getString("ForecastingDocumentWindow.tbSummary.severaldays");
			} else if (digit == 0 || digit > 4 || days > 10) {
				s = s + days + " " + Messages.getString("ForecastingDocumentWindow.tbSummary.days");
			}
		}
		return (s.isEmpty() ? "-" : s);
	}
}
