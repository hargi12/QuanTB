package org.msh.quantb.view;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

/**
 * Spinner as cell of JTable.
 * @author 
 *
 */
public class SpinnerEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 8100641428122697335L;
	private JSpinner spinner;
	private JSpinner.DefaultEditor editor;
	private JTextField textField;
	boolean valueSet;
	/**
	 * Initialize JSpinner (as default editor) and JTtextField as cell
	 * @param min minimum value of JSpinner
	 * @param max maximum value of JSpinner
	 * @param step step size for JSpinner
	 */
	public SpinnerEditor(int min, int max, int step) {
		super(new JTextField());//Initialize DefaultCellEditor	
		spinner = new JSpinner(new SpinnerNumberModel(min, min, max, step));		
		editor = ((JSpinner.DefaultEditor) spinner.getEditor()); //set JSpinner as default editor for cell
		textField = editor.getTextField();
	}
	/**
	 * Initialize JSpinner (as default editor) and JTtextField as cell
	 * @param min minimum value of JSpinner
	 * @param max maximum value of JSpinner
	 * @param step step size for JSpinner
	 * @param listener change listener for spinner
	 */
	public SpinnerEditor(int min, int max, int step, ChangeListener listener) {
		super(new JTextField());//Initialize DefaultCellEditor	
		spinner = new JSpinner(new SpinnerNumberModel(min, min, max, step));		
		spinner.addChangeListener(listener);
		editor = ((JSpinner.DefaultEditor) spinner.getEditor()); //set JSpinner as default editor for cell
		textField = editor.getTextField();
	}
	/**
	 * Add change state listener for spinner
	 * @param value listener
	 */
	public void addChangeStateListener(ChangeListener value){
		spinner.addChangeListener(value);
	}
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (!valueSet) {
			spinner.setValue(value);
		}
		textField.requestFocus();
		return spinner;
	}
	@Override
	public boolean isCellEditable(EventObject eo) {
		return true;
	}
	@Override
	public Object getCellEditorValue() {
		return spinner.getValue();
	}
	
	
	/**
	 * Is column editable or not 
	 * @param isEditable flag of edit
	 */
	public void setEditable(boolean isEditable){
	}
}
