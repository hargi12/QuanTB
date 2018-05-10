package org.msh.quantb.view;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigDecimal;
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
public class PercentageCellEditor extends DefaultCellEditor {
	private static final long serialVersionUID = 1083804644201608959L;
	private JTextField textField;
	private DecimalFormat formatter = new DecimalFormat(ViewFactory.DECIMAL_FORMAT);
	private boolean isBigDecimal;
	
	/**
	 * Cell editor for float and BigDecimal values
	 * @param textField
	 * @param _isBigDecimal if true - for BigDecimal values
	 */
	public PercentageCellEditor(JTextField textField, boolean _isBigDecimal) {
		super(textField);
		init(textField, _isBigDecimal);
	}
	private void init(JTextField textField, boolean _isBigDecimal) {
		this.isBigDecimal = _isBigDecimal;
		this.textField = textField;
		this.textField.addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent arg0) {
				final JTextField tmp = (JTextField) arg0.getComponent();
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						tmp.selectAll();
					}
				});


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
		if (value instanceof Float || value instanceof BigDecimal){
			textField.setText(formatter.format(value));
		}else{
			textField.setText(res);
		}
		//no negative
		String strVal = textField.getText();
		strVal = strVal.replace("-", "");
		textField.setText(strVal);
		
		textField.setCaretPosition(0);
		textField.selectAll();
		return textField;
	}	

	@Override
	public Object getCellEditorValue(){
		//no negative
		String strVal = textField.getText();
		if(strVal.length() == 0){
			strVal = "0";
		}
		strVal = strVal.replace("-", "");
		textField.setText(strVal);
		if (isBigDecimal){
			BigDecimal res=null;
			try {
				Number resO = formatter.parse(textField.getText());
				res = new BigDecimal(resO.toString());
			} catch (ParseException e) {
				res=null;
			}
			return res;
		}else{
		Float res = null;
		try {
			Number resO = formatter.parse(textField.getText());
			res = resO.floatValue();
		} catch (ParseException e) {
			res = null;
		}
		return res;
		}
	}
}
