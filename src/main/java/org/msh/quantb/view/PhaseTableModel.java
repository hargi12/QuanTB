package org.msh.quantb.view;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.msh.quantb.services.io.MedicationUIAdapter;
import org.msh.quantb.services.io.PhaseUIAdapter;

public class PhaseTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4495052085168108525L;
	private PhaseUIAdapter phase = null;
	List<MedicationUIAdapter> ml = null;
	boolean editable = true;

	/**
	 * only valid constructor
	 * @param _phase phase with medications
	 */
	public PhaseTableModel(PhaseUIAdapter _phase){
		this.phase = _phase;
		this.editable = true;
		if (this.phase != null){
			this.ml = this.phase.getMedications();
		}
	}

	public PhaseUIAdapter getPhase() {
		return phase;
	}

	public boolean isEditable() {
		return editable;
	}
	
	

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public int getRowCount() {
		if (this.ml != null){
			return ml.size();
		}else{
			return 0;
		}
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		MedicationUIAdapter mUi = this.ml.get(arg0);
		switch(arg1){
		case 0:
			return mUi.getMedicine().getNameForDisplay();
		case 1:
			return mUi.getDosage();
		case 2:
			return mUi.getDaysPerWeek();
		}
		return null;
	}
	
	 public void setValueAt(Object value, int row, int col) {
		 MedicationUIAdapter mUi = this.ml.get(row);
		 if(mUi != null){
			 switch(col){
			 case 1:
				 mUi.setDosage((Integer) value);
				 break;
			 case 2:
				 mUi.setDaysPerWeek((Integer) value);
				 break;
			 }
		 }
	        fireTableCellUpdated(row, col);
	    }

	
	@Override
	public boolean isCellEditable(int rowIndex,
            int columnIndex){
		return columnIndex>0 && isEditable();
	}
	
	@Override
	/**
	 * data change event
	 * before fire real data change event, data change should be performed
	 */
	public void fireTableDataChanged(){
		this.ml = this.phase.getMedications();
		super.fireTableDataChanged();
	}
	/**
	 * Delete the medication by index
	 * @param selectedRow index
	 */
	public void deleteMedication(int selectedRow) {
		if (selectedRow > -1){
			this.phase.deleteMedication(selectedRow);
		}
		
	}
	/**
	 * All cells are not editable
	 */
	public void deactivate() {
		setEditable(false);
	}

}
