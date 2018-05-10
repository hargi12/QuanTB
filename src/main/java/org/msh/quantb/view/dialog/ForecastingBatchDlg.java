package org.msh.quantb.view.dialog;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.PlainDocument;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.joda.time.LocalDate;
import org.msh.quantb.services.excel.ClipBoard;
import org.msh.quantb.services.io.ForecastingBatchUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.NumericFilter;
import org.msh.quantb.view.TextAreaFilter;
import org.msh.quantb.view.panel.ForecastingDocumentPanel;
import org.msh.quantb.view.panel.PastePopUp;
import org.msh.quantb.view.panel.PopUpMouseListener;

import com.toedter.calendar.JDateChooser;

/**
 * Edit selected forecasting order
 *
 */
public class ForecastingBatchDlg extends JDialog {
	private static final long serialVersionUID = 1058101100219811105L;
	private ForecastingBatchUIAdapter batch;
	private Calendar refDate;
	private JDateChooser expiriedDt;
	private JTextField quantityTxt;
	private boolean dateEditAtFirst = true;
	private JTextField commentTxt;
	private ForecastingDocumentPanel tabPnl;
	
	public ForecastingBatchDlg(ForecastingBatchUIAdapter _batch, Frame owner, ForecastingDocumentPanel tPnl, Calendar refDate, final boolean isEdit) {
		super(owner);
		this.batch = _batch;
		this.refDate = refDate;
		this.tabPnl = tPnl;
		setTitle(isEdit?Messages.getString("DlgEditBatch.title.edit"):Messages.getString("DlgEditBatch.title.new")); 
		getContentPane().setLayout(null);

		JLabel lblNewLabel_1 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.expirationDate")); //$NON-NLS-1$
		lblNewLabel_1.setBounds(10, 11, 143, 14);
		getContentPane().add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.quantity")); //$NON-NLS-1$
		lblNewLabel_2.setBounds(10, 51, 111, 14);
		getContentPane().add(lblNewLabel_2);

		JButton btnNewButton = new JButton(Messages.getString("DlgEditMedicine.btnSave.text")); //$NON-NLS-1$
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ForecastingBatchDlg.this.isFieldsValid()) {
					ForecastingBatchDlg.this.setVisible(false);					
					if (isEdit){
						Presenter.editSelectedBatch(batch);
					}else{
						Presenter.createBatchAndAddToExists(batch);
					}
					tabPnl.setVisibleCalculationDetailsTabs(false);
				}
			}
		});
		btnNewButton.setBounds(10, 130, 91, 23);
		getContentPane().add(btnNewButton);

		JButton btnNewButton_1 = new JButton(Messages.getString("DlgEditMedicine.btnCancel.text")); //$NON-NLS-1$
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnNewButton_1.setBounds(211, 130, 91, 23);
		getContentPane().add(btnNewButton_1);

		expiriedDt = new JDateChooser();
		expiriedDt.getDateEditor().setEnabled(false);
		expiriedDt.setLocale(new Locale(Messages.getLanguage(), Messages.getCountry()));
		expiriedDt.getJCalendar().setWeekOfYearVisible(false);
		expiriedDt.setBounds(163, 11, 171, 20);
		expiriedDt.addPropertyChangeListener("date", new PropertyChangeListener() {			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (!isEdit && dateEditAtFirst && evt.getOldValue()==null && evt.getNewValue()!=null){
					expiriedDt.setDate(null);
					dateEditAtFirst = false;
				}				
			}
		});
		getContentPane().add(expiriedDt);

		quantityTxt = new JTextField();
		quantityTxt.addFocusListener(new FocusListener() {			
			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				JTextField tmp = (JTextField) e.getComponent();
				tmp.selectAll();				
			}
		});
		
		quantityTxt.setBounds(163, 48, 146, 20);
		getContentPane().add(quantityTxt);
		PlainDocument doc = (PlainDocument) quantityTxt.getDocument();
		doc.setDocumentFilter(new NumericFilter());
		
		JLabel lblNewLabel = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.comment")); //$NON-NLS-1$
		lblNewLabel.setBounds(10, 76, 267, 14);
		getContentPane().add(lblNewLabel);
		
		commentTxt = new JTextField();
		commentTxt.setBounds(10, 101, 324, 20);
		getContentPane().add(commentTxt);
		commentTxt.setColumns(10);
		doc = (PlainDocument) commentTxt.getDocument();
		doc.setDocumentFilter(new TextAreaFilter(36));
		
		//allow the clipboard
		quantityTxt.addMouseListener(new PopUpMouseListener(new PastePopUp(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				pasteQuantity();
			}

		}, null)));
		quantityTxt.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.CTRL_MASK,false),
                "paste");
		quantityTxt.getActionMap().put("paste",
                 new AbstractAction(){
					private static final long serialVersionUID = -657924900066655426L;
					@Override
					public void actionPerformed(ActionEvent e) {
						pasteQuantity();
					}
		});
		initDialog();		
		initDataBindings();
	}
	
	/**
	 * Paste quantity from the Clipboard
	 */
	private void pasteQuantity() {
		Integer[][] data = ClipBoard.getQuantities();
		if (Presenter.isClipboardSimple(data)){
			quantityTxt.setText(data[0][0]+"");
		}else{
			Presenter.showError(Messages.getString("Error.forecasting.paste"));
		}
	}
	
	/**
	 * Are fields valid?
	 * 
	 * @return
	 */
	protected boolean isFieldsValid() {
		if (expiriedDt.getDate()==null){
			Presenter.showError(Messages.getString("Error.Validation.OrderSave.ExpireDateBlank"));
			return false;
		}
		LocalDate expir = new LocalDate(expiriedDt.getDate());
		LocalDate inv = new LocalDate(refDate).plusDays(1);
		
		if (expir.isBefore(inv)) {
			Presenter.showError(Messages.getString("Error.Validation.BatchSave.ExpireDate"));
			return false;
		}				
		if (Integer.parseInt(quantityTxt.getText()) == 0) {
			Presenter.showError(Messages.getString("Error.Validation.OrderSave.Quantity"));
			return false;
		}
		return true;
	}
	/**
	 * Initialize dimension and modality of the dialog and position at center of the screen.
	 */
	private void initDialog() {
		setSize(new Dimension(365, 192));
		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		int wdwLeft = screenSize.width / 2 - getWidth() / 2;
		int wdwTop = screenSize.height / 2 - getHeight() / 2;
		setLocation(wdwLeft, wdwTop);
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
	}
	protected void initDataBindings() {
		BeanProperty<ForecastingBatchUIAdapter, Integer> forecastingBatchUIAdapterBeanProperty_1 = BeanProperty.create("quantity");
		BeanProperty<JTextField, String> jTextFieldBeanProperty = BeanProperty.create("text");
		AutoBinding<ForecastingBatchUIAdapter, Integer, JTextField, String> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, batch, forecastingBatchUIAdapterBeanProperty_1, quantityTxt, jTextFieldBeanProperty);
		autoBinding_1.bind();
		//
		BeanProperty<ForecastingBatchUIAdapter, String> forecastingBatchUIAdapterBeanProperty_2 = BeanProperty.create("comment");
		BeanProperty<JTextField, String> jTextFieldBeanProperty_1 = BeanProperty.create("text");
		AutoBinding<ForecastingBatchUIAdapter, String, JTextField, String> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, batch, forecastingBatchUIAdapterBeanProperty_2, commentTxt, jTextFieldBeanProperty_1);
		autoBinding_2.bind();
		//
		BeanProperty<ForecastingBatchUIAdapter, Date> forecastingBatchUIAdapterBeanProperty = BeanProperty.create("expiredDtEdit");
		BeanProperty<JDateChooser, Date> jDateChooserBeanProperty = BeanProperty.create("date");
		AutoBinding<ForecastingBatchUIAdapter, Date, JDateChooser, Date> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, batch, forecastingBatchUIAdapterBeanProperty, expiriedDt, jDateChooserBeanProperty);
		autoBinding.bind();
	}
}
