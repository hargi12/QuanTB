package org.msh.quantb.services.calc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.msh.quantb.services.io.MedicineUIAdapter;

/**
 * Helper class for medicine calculation
 * @author alexey
 *
 */
public class MedicineBuffer {

	Map<MedicineUIAdapter,BigDecimal> buffer = new HashMap<MedicineUIAdapter,BigDecimal>();
	/**
	 * add quantity to medicine
	 * @param med medicine
	 * @param quantity
	 */
	public void add(MedicineUIAdapter med, BigDecimal quantity){
		BigDecimal oldQ = this.get(med);
		buffer.put(med, oldQ.add(quantity));
	}


	
	/**
	 * get quantity by medicine
	 * @param med medicine
	 * @return quantity or 0, if no medicine
	 */
	public BigDecimal get(MedicineUIAdapter med){
		BigDecimal ret = buffer.get(med);
		if (ret == null) ret =BigDecimal.ZERO;
		return ret;	
	}
	/**
	 * Get set of medicines from the buffer
	 * @return
	 */
	public Set<MedicineUIAdapter> getMeds() {
		return buffer.keySet();
	}
	
}
