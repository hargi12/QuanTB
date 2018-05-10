package org.msh.quantb.view.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.text.PlainDocument;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.msh.quantb.services.calc.DateParser;
import org.msh.quantb.services.io.SliceFCUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.TextAreaFilter;

import com.toedter.calendar.JDateChooser;
/**
 * Determine slice parameters
 * @author Alexey Kurasov
 *
 */
public class ForecastSliceDlg extends JDialog {
	private static final long serialVersionUID = 1L;
	private JTextField addrField;
	private JTextField calculator;
	private JSpinner leadTimeSp;
	private JDateChooser referenceDateDc;
	private JDateChooser endDateDc;
	private JSpinner minStockSp;
	private JSpinner maxStockSp;
	private SliceFCUIAdapter slice;
	private JLabel totalDuration;
	private JTextArea commentFld;
	
	/**
	 * Only valid constructor
	 * @param slice - slice definition that initially taken from the source forecasting
	 * @param mainWindow main program window
	 */
	public ForecastSliceDlg(SliceFCUIAdapter slice,  JFrame mainWindow) {
		super(mainWindow);
		setTitle(Messages.getString("DlgSplit.title"));
		this.slice = slice;
		setSize(new Dimension(683, 312));
		setResizable(false);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		JLabel label = new JLabel(Messages.getString("DlgForecastingWizard.address.label"));
		label.setBounds(10, 11, 353, 14);
		panel.add(label);
		
		addrField = new JTextField();
		addrField.setColumns(10);
		addrField.setBounds(392, 11, 261, 20);
		panel.add(addrField);
		
		JLabel label_1 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.calculator"));
		label_1.setBounds(10, 47, 373, 14);
		panel.add(label_1);
		
		calculator = new JTextField();
		calculator.setColumns(10);
		calculator.setBounds(392, 42, 261, 20);
		panel.add(calculator);
		
		JLabel label_2 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.referenceDate"));
		label_2.setBounds(10, 140, 125, 14);
		panel.add(label_2);
		
		JLabel label_3 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.leadTime"));
		label_3.setBounds(10, 165, 157, 14);
		panel.add(label_3);
		
		JLabel label_6 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.minstock"));
		label_6.setBounds(12, 254, 209, 14);
		panel.add(label_6);
		
		referenceDateDc = new JDateChooser();
		referenceDateDc.setOpaque(false);
		referenceDateDc.setFocusable(false);
		referenceDateDc.setBorder(null);
		referenceDateDc.setRequestFocusEnabled(false);
		referenceDateDc.getCalendarButton().setVisible(true);
		referenceDateDc.getCalendarButton().setEnabled(true);
		referenceDateDc.setEnabled(true);
		referenceDateDc.setLocale(Locale.ROOT);
		referenceDateDc.setBounds(232, 134, 118, 20);
		panel.add(referenceDateDc);
		
		leadTimeSp = new JSpinner();
		leadTimeSp.setModel(new SpinnerNumberModel(1, 0, 30, 1));
		leadTimeSp.setBounds(232, 162, 47, 20);
		panel.add(leadTimeSp);
		if (leadTimeSp.getEditor() instanceof JSpinner.DefaultEditor) {
			JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) leadTimeSp.getEditor();
			editor.getTextField().setEnabled(true);
			editor.getTextField().setEditable(false);
		}
		
		JLabel label_7 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.month"));
		label_7.setBounds(289, 165, 80, 14);
		panel.add(label_7);
		
		JLabel label_8 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.until"));
		label_8.setHorizontalAlignment(SwingConstants.LEFT);
		label_8.setBounds(10, 190, 157, 14);
		panel.add(label_8);
		
		endDateDc = new JDateChooser();
		endDateDc.setLocale(Locale.ROOT);
		endDateDc.setBounds(232, 193, 118, 20);
		panel.add(endDateDc);
		
		totalDuration = new JLabel();
		totalDuration.setText("()");
		totalDuration.setBounds(370, 199, 152, 14);
		panel.add(totalDuration);

		
		minStockSp = new JSpinner();
		minStockSp.setModel(new SpinnerNumberModel(1, 0, 30, 1));
		minStockSp.setBounds(231, 251, 41, 20);
		panel.add(minStockSp);
		if (minStockSp.getEditor() instanceof JSpinner.DefaultEditor) {
			JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) minStockSp.getEditor();
			editor.getTextField().setEnabled(true);
			editor.getTextField().setEditable(false);
		}
		
		JLabel label_10 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.month"));
		label_10.setBounds(287, 254, 63, 14);
		panel.add(label_10);
		
		JLabel label_11 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.maxstock"));
		label_11.setHorizontalAlignment(SwingConstants.RIGHT);
		label_11.setBounds(324, 254, 203, 14);
		panel.add(label_11);
		
		maxStockSp = new JSpinner();
		maxStockSp.setModel(new SpinnerNumberModel(1, 0, 30, 1));
		maxStockSp.setBounds(541, 248, 47, 20);
		panel.add(maxStockSp);
		if (maxStockSp.getEditor() instanceof JSpinner.DefaultEditor) {
			JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) maxStockSp.getEditor();
			editor.getTextField().setEnabled(true);
			editor.getTextField().setEditable(false);
		}
		
		JLabel label_12 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.month"));
		label_12.setBounds(598, 254, 58, 14);
		panel.add(label_12);
		
		JLabel label_13 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.comment"));
		label_13.setBounds(10, 72, 141, 14);
		panel.add(label_13);
		
		commentFld = new JTextArea();
		TextAreaFilter filter = new TextAreaFilter(177);
		((PlainDocument) commentFld.getDocument()).setDocumentFilter(filter);

		commentFld.setAlignmentX(Component.RIGHT_ALIGNMENT);
		commentFld.setRows(4);
		commentFld.setWrapStyleWord(true);
		commentFld.setLineWrap(true);
		commentFld.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		Border borderTextFld = calculator.getBorder();
		commentFld.setBorder(borderTextFld);
		commentFld.setColumns(10);
/*		
		
		commentFld = new JTextArea();
		commentFld.setRows(3);
		commentFld.setWrapStyleWord(true);
		commentFld.setLineWrap(true);
		commentFld.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		commentFld.setColumns(10);*/
		commentFld.setBounds(161, 72, 492, 51);
		panel.add(commentFld);
		
		JPanel buttonPane = new JPanel();
		FlowLayout flowLayout = (FlowLayout) buttonPane.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		JButton saveBtn = new JButton(Messages.getString("DlgEditMedicine.btnSave.text"));
		saveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				Presenter.showSlice(getSlice());
			}
		});
		buttonPane.add(saveBtn);
		
		JButton cancelBtn = new JButton(Messages.getString("DlgForecastingWizard.cancelBtn.text"));
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		buttonPane.add(cancelBtn);
		initDataBindings();
		adjustDlg();
	}



	public SliceFCUIAdapter getSlice() {
		return slice;
	}



	public void setSlice(SliceFCUIAdapter slice) {
		this.slice = slice;
	}

	/**
	 * Adjust the dialog box appearance
	 */
	private void adjustDlg() {
		setResizable(false);
		setSize(new Dimension(712, 403));
		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		int wdwLeft = screenSize.width / 2 - getWidth() / 2;
		int wdwTop = screenSize.height / 2 - getHeight() / 2;
		setLocation(wdwLeft, wdwTop);
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}


	
	protected void initDataBindings() {
		BeanProperty<SliceFCUIAdapter, String> sliceFCUIAdapterBeanProperty_1 = BeanProperty.create("calculator");
		BeanProperty<JTextField, String> jTextFieldBeanProperty_1 = BeanProperty.create("text");
		AutoBinding<SliceFCUIAdapter, String, JTextField, String> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, slice, sliceFCUIAdapterBeanProperty_1, calculator, jTextFieldBeanProperty_1);
		autoBinding_1.bind();
		//
		BeanProperty<SliceFCUIAdapter, Integer> sliceFCUIAdapterBeanProperty_3 = BeanProperty.create("leadTime");
		BeanProperty<JSpinner, Object> jSpinnerBeanProperty = BeanProperty.create("value");
		AutoBinding<SliceFCUIAdapter, Integer, JSpinner, Object> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, slice, sliceFCUIAdapterBeanProperty_3, leadTimeSp, jSpinnerBeanProperty);
		autoBinding_3.bind();
		//
		BeanProperty<SliceFCUIAdapter, Integer> sliceFCUIAdapterBeanProperty_7 = BeanProperty.create("minStock");
		AutoBinding<SliceFCUIAdapter, Integer, JSpinner, Object> autoBinding_7 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, slice, sliceFCUIAdapterBeanProperty_7, minStockSp, jSpinnerBeanProperty);
		autoBinding_7.bind();
		//
		BeanProperty<SliceFCUIAdapter, Integer> sliceFCUIAdapterBeanProperty_8 = BeanProperty.create("maxStock");
		AutoBinding<SliceFCUIAdapter, Integer, JSpinner, Object> autoBinding_8 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, slice, sliceFCUIAdapterBeanProperty_8, maxStockSp, jSpinnerBeanProperty);
		autoBinding_8.bind();
		//
		BeanProperty<SliceFCUIAdapter, String> sliceFCUIAdapterBeanProperty = BeanProperty.create("address");
		BeanProperty<JTextField, String> jTextFieldBeanProperty = BeanProperty.create("text");
		AutoBinding<SliceFCUIAdapter, String, JTextField, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, slice, sliceFCUIAdapterBeanProperty, addrField, jTextFieldBeanProperty);
		autoBinding.bind();
		//
		BeanProperty<SliceFCUIAdapter, String> sliceFCUIAdapterBeanProperty_9 = BeanProperty.create("periodAsString");
		BeanProperty<JLabel, String> jLabelBeanProperty = BeanProperty.create("text");
		AutoBinding<SliceFCUIAdapter, String, JLabel, String> autoBinding_9 = Bindings.createAutoBinding(UpdateStrategy.READ, slice, sliceFCUIAdapterBeanProperty_9, totalDuration, jLabelBeanProperty);
		autoBinding_9.bind();
		//
		BeanProperty<SliceFCUIAdapter, String> sliceFCUIAdapterBeanProperty_5 = BeanProperty.create("comment");
		BeanProperty<JTextArea, String> jTextAreaBeanProperty = BeanProperty.create("text");
		AutoBinding<SliceFCUIAdapter, String, JTextArea, String> autoBinding_5 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, slice, sliceFCUIAdapterBeanProperty_5, commentFld, jTextAreaBeanProperty);
		autoBinding_5.bind();
		//
		BeanProperty<SliceFCUIAdapter, Date> sliceFCUIAdapterBeanProperty_10 = BeanProperty.create("endDateDt");
		BeanProperty<JDateChooser, Date> jDateChooserBeanProperty = BeanProperty.create("date");
		AutoBinding<SliceFCUIAdapter, Date, JDateChooser, Date> autoBinding_10 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, slice, sliceFCUIAdapterBeanProperty_10, endDateDc, jDateChooserBeanProperty);
		autoBinding_10.bind();
		//
		BeanProperty<SliceFCUIAdapter, Date> sliceFCUIAdapterBeanProperty_2 = BeanProperty.create("referenceDateD");
		AutoBinding<SliceFCUIAdapter, Date, JDateChooser, Date> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, slice, sliceFCUIAdapterBeanProperty_2, referenceDateDc, jDateChooserBeanProperty);
		autoBinding_2.bind();
	}
}
