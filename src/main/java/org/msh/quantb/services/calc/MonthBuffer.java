package org.msh.quantb.services.calc;

import java.util.Map;
import java.util.TreeMap;

import org.msh.quantb.model.mvp.ModelFactory;
import org.msh.quantb.services.io.MonthUIAdapter;

/**
 * Helper class to store temporary calculation results
 * @author alexey
 *
 */
public class MonthBuffer {
	private Map<MonthUIAdapter, Integer> buffer = new TreeMap<MonthUIAdapter,Integer>();

	public MonthBuffer(ModelFactory _modelFactory, MonthUIAdapter _from, MonthUIAdapter _to){
		this.buffer.clear();
		MonthUIAdapter tmp = _from.incrementClone(_modelFactory,0);
		while(tmp.compareTo(_to) <= 0){
			this.buffer.put(tmp, 0);
			tmp = tmp.incrementClone(_modelFactory,1);
		}
	}
	/**
	 * add increment from month to duration<br>
	 * @param month month, when counter register
	 * @param increment
	 * @param duration
	 * @param modelFactory model factory
	 */
	public void addCases(MonthUIAdapter month, Integer increment, int duration, ModelFactory modelFactory) {
		for (int i=0;i<duration;i++){
			MonthUIAdapter tmp = month.incrementClone(modelFactory, i);
			Integer old = get(tmp);
			buffer.put(tmp, old+increment);
		}

	}

	/**
	 * Simply get
	 * @param month
	 * @return value or 0 is no results
	 */
	public int get(MonthUIAdapter month) {
		Integer res = buffer.get(month);
		if (res==null) return 0;
		else return res.intValue();
	}

}
