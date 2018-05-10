package org.msh.quantb.services.calc;

import org.msh.quantb.services.io.MedicationUIAdapter;
import org.msh.quantb.services.io.MonthUIAdapter;

/**
 * This interface intends to allow implements different strategies to calculate 
 * medication days quantity
 * Concrete realization may be different
 * @author alexey
 *
 */
public interface IdaysCalculator {
	public int calculateDays(MonthUIAdapter month, MedicationUIAdapter med);
	public Integer calculatePeriod(MonthUIAdapter month, int from, int to, int freq);
}
