package org.msh.quantb.view;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

/**
 * Cell editor, as text field, which will accept only positive numbers
 * @author User
 *
 */
public class NumericCellEditor extends DefaultCellEditor {
	private static final long serialVersionUID = 178314337782245473L;
	private JTextField txtField;
	DecimalFormat formatter = new DecimalFormat("#################0");
		
	/**
	 * Set text field as default editor
	 * @param txtField text field
	 */
	public NumericCellEditor(JTextField _txtField) {
		super(_txtField);		
		this.txtField = _txtField;	

		PlainDocument doc = (PlainDocument)txtField.getDocument();
		doc.setDocumentFilter(new NumericFilter());
		txtField.addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent e) {
				JTextField tmp = (JTextField) e.getComponent();
				tmp.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		

	}
	@Override
	public Component getTableCellEditorComponent(JTable table, final Object value, boolean isSelected, int row, int column) {
		txtField.setText(value.toString());
		txtField.setCaretPosition(0);
		txtField.selectAll();
		return txtField;
	}	

}
