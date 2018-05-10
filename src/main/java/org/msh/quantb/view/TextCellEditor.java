package org.msh.quantb.view;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.msh.quantb.view.mvp.ViewFactory;

/**
 * Cell editor for percentage, wich will set caret position at the beginning of text field.
 *
 */
public class TextCellEditor extends DefaultCellEditor {
	private static final long serialVersionUID = 1083804644201608959L;
	private JTextField textField;
	DecimalFormat formatter = new DecimalFormat(ViewFactory.DECIMAL_FORMAT);

	public TextCellEditor(JTextField _textField) {
		super(_textField);
		this.textField = _textField;
		this.textField.addFocusListener(new FocusListener(){

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
		String res = String.valueOf(value);
		if (value instanceof Float){
			textField.setText(formatter.format(value));
		}else{
			textField.setText(res);
		}

		textField.setCaretPosition(0);
		textField.selectAll();
		return textField;
	}	

}
