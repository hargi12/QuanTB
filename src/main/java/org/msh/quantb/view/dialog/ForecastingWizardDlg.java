package org.msh.quantb.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.joda.time.LocalDate;
import org.msh.quantb.model.gen.RegimenTypesEnum;
import org.msh.quantb.services.calc.DateParser;
import org.msh.quantb.services.calc.DateUtils;
import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.ForecastUIVerify;
import org.msh.quantb.services.io.RegimenUIAdapter;
import org.msh.quantb.services.io.RegimensDicUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.DateLabel;
import org.msh.quantb.view.EnumListRenderer;
import org.msh.quantb.view.ToolTipCellRenderer;

import com.toedter.calendar.DateUtil;
import com.toedter.calendar.JDateChooser;

/**
 * Wizard for creation new forecasting document
 * 
 * @author User
 * 
 */
public class ForecastingWizardDlg extends JDialog {

	private static final long serialVersionUID = -4004921574449007313L;
	private ForecastUIAdapter forecast;
	private RegimensDicUIAdapter regimensDic;
	private JDateChooser referenceDateDc;
	private JSpinner leadTimeSp;
	private JDateChooser endDateDc;
	private JPanel cards;
	private JTable regimensTable;
	private JButton finishBtn;
	private JButton cancelBtn;
	private List<RegimenUIAdapter> selected = new ArrayList<RegimenUIAdapter>();
	private JLabel totalDuration;
	private JTextField calculator;
	private JTextField addrField;
	private JLabel label_1;
	private JComboBox typeBox;
	private JLabel minStockLbl;
	private JSpinner minStockSp;
	private JLabel minMonthsLbl;
	private JLabel maxStockLbl;
	private JSpinner maxStockSp;
	private JLabel maxMonthsLbl;
	private JPanel caseTypePnl;
	private enum CaseType{BY_NUMBER, BY_PERCENTAGE};
	/**
	 * Create the dialog.
	 */
	public ForecastingWizardDlg(ForecastUIAdapter _forecast, final RegimensDicUIAdapter regimensDic, Frame owner) {
		super(owner);
		initDialog();
		this.forecast = _forecast;
		this.regimensDic = regimensDic;
		setTitle(Messages.getString("DlgForecastingWizard.title"));
		getContentPane().setLayout(new BorderLayout());
		cards = new JPanel();
		cards.setLayout(null);
		getContentPane().add(cards, BorderLayout.CENTER);
		getContentPane().add(createControlButtonsPanel(), BorderLayout.PAGE_END);
		regimensTable = new JTable();
		regimensTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		regimensTable.getTableHeader().setReorderingAllowed(false);
		regimensTable.getTableHeader().setResizingAllowed(false);

		{
			JPanel panel = new JPanel();
			panel.setBounds(0, 6, 677, 283);
			cards.add(panel);
			panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
			panel.setLayout(null);
			{
				JLabel label = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.referenceDate"));
				label.setBounds(8, 72, 125, 14);
				panel.add(label);
			}
			{
				referenceDateDc = new JDateChooser();
				referenceDateDc.getDateEditor().setEnabled(false);
				referenceDateDc.setLocale(new Locale(Messages.getLanguage(), Messages.getCountry()));
				referenceDateDc.getJCalendar().setWeekOfYearVisible(false);
				referenceDateDc.setBounds(173, 70, 118, 20);
				panel.add(referenceDateDc);
			}
			{
				JLabel label = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.leadTime"));
				label.setBounds(7, 101, 157, 14);
				panel.add(label);
			}
			{
				leadTimeSp = new JSpinner();
				leadTimeSp.setBounds(174, 98, 47, 20);
				panel.add(leadTimeSp);
				leadTimeSp.setModel(new SpinnerNumberModel(1, 0, 30, 1));
				leadTimeSp.addChangeListener(new ChangeListener() {					
					@Override
					public void stateChanged(ChangeEvent e) {						
						int value = ((SpinnerNumberModel)leadTimeSp.getModel()).getNumber().intValue();
						label_1.setText("("+DateParser.getMonthLabel(value)+")");
					}
				});
				if (leadTimeSp.getEditor() instanceof JSpinner.DefaultEditor) {
					JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) leadTimeSp.getEditor();
					editor.getTextField().setEnabled(true);
					editor.getTextField().setEditable(false);
				}
			}
			{
				label_1 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.month"));
				label_1.setBounds(234, 101, 80, 14);
				panel.add(label_1);
			}
			{
				//addInitDateHelper(panel);
			}
			{
				JLabel label = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.until"));
				label.setHorizontalAlignment(SwingConstants.LEFT);
				label.setBounds(8, 135, 156, 14);
				panel.add(label);
			}
			{
				endDateDc = new JDateChooser();
				endDateDc.getDateEditor().setEnabled(false);
				endDateDc.setLocale(new Locale(Messages.getLanguage(), Messages.getCountry()));
				endDateDc.getJCalendar().setWeekOfYearVisible(false);
				endDateDc.setBounds(173, 129, 118, 20);
				panel.add(endDateDc);
			}

			totalDuration = new JLabel();
			totalDuration.setBounds(301, 135, 312, 14);
			totalDuration.setText(getDurationOfPeriod());
			panel.add(totalDuration);
			{
				calculator = new JTextField();
				calculator.setBounds(382, 40, 261, 20);
				panel.add(calculator);
				calculator.setColumns(10);
			}
			{
				JLabel lblNewLabel_3 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.calculator")); //$NON-NLS-1$
				lblNewLabel_3.setBounds(8, 43, 373, 14);
				panel.add(lblNewLabel_3);
			}

			addrField = new JTextField();
			addrField.setBounds(381, 11, 261, 20);
			panel.add(addrField);
			addrField.setColumns(10);

			JLabel lblNewLabel_1 = new JLabel(Messages.getString("DlgForecastingWizard.address.label")); //$NON-NLS-1$
			lblNewLabel_1.setBounds(8, 11, 353, 14);
			panel.add(lblNewLabel_1);

			JLabel lblNewLabel_2 = new JLabel(Messages.getString("DlgForecastingWizard.regtype")); //$NON-NLS-1$
			lblNewLabel_2.setBounds(8, 234, 196, 14);
			panel.add(lblNewLabel_2);

			typeBox = new JComboBox();
			typeBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JComboBox cb = (JComboBox)e.getSource();
					RegimenTypesEnum filter = (RegimenTypesEnum)cb.getSelectedItem();
					regimensDic.setFilter(filter);
					boolean isMultiSelect = filter.equals(RegimenTypesEnum.MULTI_DRUG);
					caseTypePnl.setVisible(isMultiSelect);
				}
			});
			typeBox.setModel(new DefaultComboBoxModel(RegimenTypesEnum.values()));
			typeBox.setBounds(205, 228, 180, 20);
			typeBox.setRenderer(new EnumListRenderer("Regimen.types")); 
			panel.add(typeBox);

			minStockLbl = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.minstock"));
			minStockLbl.setBounds(8, 171, 196, 14);
			panel.add(minStockLbl);

			minStockSp = new JSpinner();
			minStockSp.setModel(new SpinnerNumberModel(1, 0, 30, 1));
			minStockSp.setBounds(205, 165, 41, 20);
			panel.add(minStockSp);
			minStockSp.addChangeListener(new ChangeListener() {					
				@Override
				public void stateChanged(ChangeEvent e) {
					ajustLabels();
				}
			});
			if (minStockSp.getEditor() instanceof JSpinner.DefaultEditor) {
				JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) minStockSp.getEditor();
				editor.getTextField().setEnabled(true);
				editor.getTextField().setEditable(false);
			}

			minMonthsLbl = new JLabel("ForecastingDocumentWindow.tbParameters.month");
			minMonthsLbl.setBounds(251, 171, 63, 14);
			panel.add(minMonthsLbl);

			maxStockLbl = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.maxstock"));
			maxStockLbl.setHorizontalAlignment(SwingConstants.LEFT);
			maxStockLbl.setBounds(8, 196, 180, 14);
			panel.add(maxStockLbl);

			maxStockSp = new JSpinner();
			maxStockSp.setModel(new SpinnerNumberModel(1, 0, 30, 1));
			maxStockSp.setBounds(205, 196, 41, 20);
			panel.add(maxStockSp);
			maxStockSp.addChangeListener(new ChangeListener() {					
				@Override
				public void stateChanged(ChangeEvent e) {
					ajustLabels();
				}
			});
			if (maxStockSp.getEditor() instanceof JSpinner.DefaultEditor) {
				JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) maxStockSp.getEditor();
				editor.getTextField().setEnabled(true);
				editor.getTextField().setEditable(false);
			}

			maxMonthsLbl = new JLabel("ForecastingDocumentWindow.tbParameters.month");
			maxMonthsLbl.setBounds(251, 196, 58, 14);
			panel.add(maxMonthsLbl);

			caseTypePnl = new JPanel();
			caseTypePnl.setBounds(2, 257, 611, 24);
			panel.add(caseTypePnl);
			caseTypePnl.setLayout(null);

			JLabel enrolledCasesLbl = new JLabel(Messages.getString("ForecastingDocumentWindow.tbCasesReport.column.PrevCases")+":"); //$NON-NLS-1$
			enrolledCasesLbl.setBounds(7, 3, 180, 14);
			caseTypePnl.add(enrolledCasesLbl);

			JComboBox enrolledTypeCmbBox = new JComboBox();
			enrolledTypeCmbBox.setBounds(204, 0, 116, 20);
			caseTypePnl.add(enrolledTypeCmbBox);
			enrolledTypeCmbBox.setModel(new DefaultComboBoxModel(CaseType.values()));
			enrolledTypeCmbBox.setSelectedItem(CaseType.BY_NUMBER);
			enrolledTypeCmbBox.setRenderer(new EnumListRenderer("DlgForecastingWizard.caseType"));
			enrolledTypeCmbBox.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					JComboBox cb = (JComboBox)e.getSource();
					CaseType type = (CaseType)cb.getSelectedItem();
					boolean value = type.equals(CaseType.BY_PERCENTAGE);
					forecast.getForecastObj().setIsOldPercents(value);	
				}
			});

			JLabel expectedCasesLbl = new JLabel(Messages.getString("ForecastingDocumentWindow.tbCasesReport.column.NewCases") + ":"); //$NON-NLS-1$
			expectedCasesLbl.setHorizontalAlignment(SwingConstants.RIGHT);
			expectedCasesLbl.setBounds(351, 3, 126, 14);
			caseTypePnl.add(expectedCasesLbl);

			JComboBox expectedTypeCmbBox = new JComboBox();
			expectedTypeCmbBox.setBounds(488, 0, 116, 20);
			expectedTypeCmbBox.setModel(new DefaultComboBoxModel(CaseType.values()));
			expectedTypeCmbBox.setSelectedItem(CaseType.BY_NUMBER);
			expectedTypeCmbBox.setRenderer(new EnumListRenderer("DlgForecastingWizard.caseType"));
			expectedTypeCmbBox.addActionListener(new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					JComboBox cb = (JComboBox)e.getSource();
					CaseType type = (CaseType)cb.getSelectedItem();
					boolean value = type.equals(CaseType.BY_PERCENTAGE);
					forecast.getForecastObj().setIsNewPercents(value);
				}
			});
			caseTypePnl.add(expectedTypeCmbBox);
		}

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(0, 285, 677, 200);
		cards.add(panel_1);
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_1.setLayout(null);

		JLabel lblNewLabel = new JLabel(Messages.getString("DlgForecastingWizard.selectRegimens"));
		lblNewLabel.setBounds(10, 11, 294, 14);
		panel_1.add(lblNewLabel);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(8, 36, 659, 164);
		panel_1.add(scrollPane);
		scrollPane.setViewportView(regimensTable);

		initDataBindings();
		regimensTable.getColumnModel().getColumn(0).setPreferredWidth(10);
		regimensTable.getColumnModel().getColumn(1).setPreferredWidth(150);
		regimensTable.getColumnModel().getColumn(1).setCellRenderer(new ToolTipCellRenderer());
		regimensTable.getColumnModel().getColumn(2).setPreferredWidth(280);
		regimensTable.getColumnModel().getColumn(2).setCellRenderer(new ToolTipCellRenderer());
		regimensTable.getColumnModel().getColumn(0).setHeaderValue("");
		regimensTable.getColumnModel().getColumn(1).setHeaderValue(Messages.getString("Regimen.clmn.Regimen"));
		regimensTable.getColumnModel().getColumn(2).setHeaderValue(Messages.getString("Regimen.clmn.Composition"));
		ajustLabels();
		addListeners();
	}
	/**
	 * Add listeners to the forecasting
	 */
	private void addListeners() {

		referenceDateDc.addPropertyChangeListener("date",new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String err = ForecastUIVerify.checkInventoryDate(evt.getNewValue(), getForecast());
				if (err.length()>0){
					referenceDateDc.setDate((Date) evt.getOldValue());
					Presenter.showError(err);
				}
				totalDuration.setText(getDurationOfPeriod());
			}
		});

		endDateDc.addPropertyChangeListener("date", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String err = ForecastUIVerify.checkEndDate(evt.getNewValue(), getForecast());
				if (err.length()>0){
					endDateDc.setDate((Date) evt.getOldValue());
					Presenter.showError(err);
				}
				totalDuration.setText(getDurationOfPeriod());
			}
		});
	}

	/**
	 * Get the current forecast
	 * @return
	 */
	protected ForecastUIAdapter getForecast() {
		return this.forecast;
	}

	/**
	 * Make some labels adjustment, really need for not-English languages
	 */
	private void ajustLabels() {
		int value = ((SpinnerNumberModel)minStockSp.getModel()).getNumber().intValue();
		minMonthsLbl.setText("("+DateParser.getMonthLabel(value)+")");
		value = ((SpinnerNumberModel)maxStockSp.getModel()).getNumber().intValue();
		maxMonthsLbl.setText("("+DateParser.getMonthLabel(value)+")");
		totalDuration.setText(getDurationOfPeriod());
	}

	/**
	 * Initialize dimension and modality of the dialog and position at center of the screen.
	 */
	private void initDialog() {
		setSize(new Dimension(683, 549));
		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		int wdwLeft = screenSize.width / 2 - getWidth() / 2;
		int wdwTop = screenSize.height / 2 - getHeight() / 2;
		setLocation(wdwLeft, wdwTop);
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
	}

	/**
	 * Get duration of review period
	 * 
	 * @return number of months of the duration of review period
	 */
	private String getDurationOfPeriod() {		
		String s = "";
		if(getForecast().getFirstFCDate() != null && getForecast().getEndDate() != null){
			if(getForecast().getEndDate().compareTo(getForecast().getFirstFCDate())>0){
				return DateParser.getDurationOfPeriod(getForecast().getFirstFCDate().getTime(), getForecast().getEndDt());
			}
		}
		return "(" + s + ")";
	}

	/**
	 * Create panel with contol buttons
	 * 
	 * @return control panell
	 */
	private JPanel createControlButtonsPanel() {
		JPanel buttonPane = new JPanel();
		FlowLayout fl_buttonPane = new FlowLayout(FlowLayout.LEFT);
		fl_buttonPane.setHgap(20);
		buttonPane.setLayout(fl_buttonPane);
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		finishBtn = new JButton(Messages.getString("DlgEditMedicine.btnSave.text")); //$NON-NLS-1$
		finishBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selected.clear();
				for (RegimenUIAdapter rui : regimensDic.getRegimens()) {
					if (rui.getChecked()) {
						selected.add(rui);
					}
				}
				if (isValidFields()) {
					if (!selected.isEmpty()) {
						Presenter.addForecasting(forecast, selected);
						dispose();
					} else {
						Presenter.showError(Messages.getString("Error.forecasting.regimenSelectionEmpty"));
					}
				}
			}
		});
		buttonPane.add(finishBtn);

		cancelBtn = new JButton(Messages.getString("DlgForecastingWizard.cancelBtn.text"));
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		buttonPane.add(cancelBtn);

		return buttonPane;
	}

	/**
	 * Check initial and final date (initial date can't be equals or after the final date), reference date (can't be after the initial date) and buffer stok and lead time are
	 * integer values that must be between the ranges.
	 * Lead time zero should produce warning
	 * 
	 * @return true - if valid, false - another.
	 */
	private boolean isValidFields() {
		if (calculator.getText().isEmpty() || addrField.getText().isEmpty()) {
			Presenter.showError(Messages.getString("Error.forecasting.validation_1"));
			return false;
		}
		String s = forecast.verifyParameters();
		if(s.length()>0){
			Presenter.showError(s);
			return false;
		}
		return true;
	}
	protected void initDataBindings() {
		BeanProperty<ForecastUIAdapter, Integer> forecastUIAdapterBeanProperty_1 = BeanProperty.create("leadTime");
		BeanProperty<JSpinner, Object> jSpinnerBeanProperty = BeanProperty.create("value");
		AutoBinding<ForecastUIAdapter, Integer, JSpinner, Object> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, forecast, forecastUIAdapterBeanProperty_1, leadTimeSp, jSpinnerBeanProperty);
		autoBinding_1.bind();
		//
		BeanProperty<RegimensDicUIAdapter, List<RegimenUIAdapter>> regimensDicUIAdapterBeanProperty = BeanProperty.create("regimens");
		JTableBinding<RegimenUIAdapter, RegimensDicUIAdapter, JTable> jTableBinding = SwingBindings.createJTableBinding(UpdateStrategy.READ, regimensDic, regimensDicUIAdapterBeanProperty, regimensTable);
		//
		BeanProperty<RegimenUIAdapter, Boolean> regimenUIAdapterBeanProperty = BeanProperty.create("checked");
		jTableBinding.addColumnBinding(regimenUIAdapterBeanProperty).setColumnClass(Boolean.class);
		//
		BeanProperty<RegimenUIAdapter, String> regimenUIAdapterBeanProperty_1 = BeanProperty.create("name");
		jTableBinding.addColumnBinding(regimenUIAdapterBeanProperty_1).setColumnName("regimen").setEditable(false);
		//
		BeanProperty<RegimenUIAdapter, String> regimenUIAdapterBeanProperty_2 = BeanProperty.create("consumption");
		jTableBinding.addColumnBinding(regimenUIAdapterBeanProperty_2).setColumnName("consumption").setEditable(false);
		//
		jTableBinding.bind();
		//
		BeanProperty<ForecastUIAdapter, Date> forecastUIAdapterBeanProperty_3 = BeanProperty.create("referenceDt");
		BeanProperty<JDateChooser, Date> jDateChooserBeanProperty = BeanProperty.create("date");
		AutoBinding<ForecastUIAdapter, Date, JDateChooser, Date> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, forecast, forecastUIAdapterBeanProperty_3, referenceDateDc, jDateChooserBeanProperty);
		autoBinding_3.bind();
		//
		BeanProperty<ForecastUIAdapter, Date> forecastUIAdapterBeanProperty_2 = BeanProperty.create("endDt");
		AutoBinding<ForecastUIAdapter, Date, JDateChooser, Date> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, forecast, forecastUIAdapterBeanProperty_2, endDateDc, jDateChooserBeanProperty);
		autoBinding_2.bind();
		//
		BeanProperty<ForecastUIAdapter, String> forecastUIAdapterBeanProperty_7 = BeanProperty.create("calculator");
		BeanProperty<JTextField, String> jTextFieldBeanProperty_2 = BeanProperty.create("text");
		AutoBinding<ForecastUIAdapter, String, JTextField, String> autoBinding_7 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, forecast, forecastUIAdapterBeanProperty_7, calculator, jTextFieldBeanProperty_2);
		autoBinding_7.bind();
		//
		BeanProperty<ForecastUIAdapter, String> forecastUIAdapterBeanProperty_5 = BeanProperty.create("address");
		BeanProperty<JTextField, String> jTextFieldBeanProperty = BeanProperty.create("text");
		AutoBinding<ForecastUIAdapter, String, JTextField, String> autoBinding_5 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, forecast, forecastUIAdapterBeanProperty_5, addrField, jTextFieldBeanProperty);
		autoBinding_5.bind();
		//
		BeanProperty<ForecastUIAdapter, RegimenTypesEnum> forecastUIAdapterBeanProperty_6 = BeanProperty.create("regimensType");
		BeanProperty<JComboBox, Object> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
		AutoBinding<ForecastUIAdapter, RegimenTypesEnum, JComboBox, Object> autoBinding_6 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, forecast, forecastUIAdapterBeanProperty_6, typeBox, jComboBoxBeanProperty);
		autoBinding_6.bind();
		//
		BeanProperty<ForecastUIAdapter, Integer> forecastUIAdapterBeanProperty_8 = BeanProperty.create("minStock");
		AutoBinding<ForecastUIAdapter, Integer, JSpinner, Object> autoBinding_8 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, forecast, forecastUIAdapterBeanProperty_8, minStockSp, jSpinnerBeanProperty);
		autoBinding_8.bind();
		//
		BeanProperty<ForecastUIAdapter, Integer> forecastUIAdapterBeanProperty_9 = BeanProperty.create("maxStock");
		AutoBinding<ForecastUIAdapter, Integer, JSpinner, Object> autoBinding_9 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, forecast, forecastUIAdapterBeanProperty_9, maxStockSp, jSpinnerBeanProperty);
		autoBinding_9.bind();
	}
}
