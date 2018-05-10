package org.msh.quantb.view;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.toedter.calendar.JDateChooser;

/**
 * Month (Date) cell editor based on {@linkplain JDateChooser}
 * 
 * @author alexey
 * 
 */
public class MonthCellEditor extends DefaultCellEditor {

	private static final long serialVersionUID = -5220693090648528112L;
	private JDateChooser dateChooser;
	private Date date;
	//private JCalendar calendar;
	//private JDialog dialog;
	
	public MonthCellEditor(int width, int height) {
		super(new JTextField());
		/*calendar = new JCalendar();
		calendar.addPropertyChangeListener("date",new PropertyChangeListener() {			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ((Date)evt.getOldValue()!=null){
					stopCellEditing();
					if (dialog!=null)dialog.dispose();
				}				
			}
		});				
		dialog = new JDialog();
		dialog.add(calendar);		
		dialog.setBounds(this.getComponent().getX(), this.getComponent().getY(), 300, 200);*/
		dateChooser = new JDateChooser();
		dateChooser.getDateEditor().setEnabled(false);
		dateChooser.setSize(width, height);
		//JTextFieldDateEditor dateTextEditor = (JTextFieldDateEditor) dateChooser.getComponents()[1];
		//dateTextEditor.setVisible(false);
		//dateChooser.getCalendarButton().setBounds(0, 0, width, height);
		dateChooser.addPropertyChangeListener("date",new PropertyChangeListener() {			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ((Date)evt.getOldValue()!=null){
					stopCellEditing();
				}				
			}
		});
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		date = (Date) value;
		//calendar.setDate(date);
		dateChooser.setDate(date);		
		return dateChooser;//new JLabel(DateUtils.format(date, "dd-MM-yyyy"));
	}
	
	@Override
	public boolean isCellEditable(EventObject anEvent) {
		//dialog.setVisible(true);
		return true;
	}

	@Override
	public Object getCellEditorValue() {
		return dateChooser.getDate();
	}
}
