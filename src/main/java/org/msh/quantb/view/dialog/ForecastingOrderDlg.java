package org.msh.quantb.view.dialog;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

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
import org.msh.quantb.services.calc.DateUtils;
import org.msh.quantb.services.excel.ClipBoard;
import org.msh.quantb.services.io.ForecastingOrderUIAdapter;
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
public class ForecastingOrderDlg extends JDialog {
	private static final long serialVersionUID = -898802626797192164L;
	private ForecastingOrderUIAdapter order;
	private JTextField quantityTxt;
	private JDateChooser arrivedDt;
	private JDateChooser expiriedDt;
	private Calendar refDate;
	protected boolean dateEditAtFirst = true;
	private JTextField commentTxt;
	private JLabel eraseLbl;
	private ForecastingDocumentPanel tabPnl;

	/**
	 * Constructor
	 * 
	 * @param _order
	 *            forecasting order (editable value)
	 * @param owner
	 *            frame owner (need for modality)
	 * @param refDate
	 *            referance date of editable forecasting document
	 */
	public ForecastingOrderDlg(ForecastingOrderUIAdapter _order, Frame owner, ForecastingDocumentPanel tPnl, Calendar refDate, final boolean isEdit) {
		super(owner);
		this.order = _order;
		this.refDate = refDate;
		this.tabPnl = tPnl;
		setTitle(isEdit?Messages.getString("DlgEditOrders.title.edit"):Messages.getString("DlgEditOrders.title.new")); 
		getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.receivingDate.plain")); //$NON-NLS-1$
		lblNewLabel.setBounds(10, 11, 193, 14);
		getContentPane().add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.expirationDate")); //$NON-NLS-1$
		lblNewLabel_1.setBounds(10, 50, 193, 14);
		getContentPane().add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.quantity")); //$NON-NLS-1$
		lblNewLabel_2.setBounds(10, 90, 193, 14);
		getContentPane().add(lblNewLabel_2);

		JButton btnNewButton = new JButton(Messages.getString("DlgEditMedicine.btnSave.text")); //$NON-NLS-1$
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ForecastingOrderDlg.this.isFieldsValid()) {
					ForecastingOrderDlg.this.setVisible(false);
					if (expiriedDt.getDate()==null){
						Calendar dt = new GregorianCalendar(9999, 9, 1);
						order.getBatch().setExpired(dt);
					}
					String err = order.validate(getRefDate());
					if(err.length()==0){
						if (isEdit){
							Presenter.editSelectedOrder(order);
						}else{
							Presenter.createOrderAndAddToExists(order);
						}
						tabPnl.setVisibleCalculationDetailsTabs(false);
					}else{
						String med = 
								Presenter.getView().getActiveForecastingPanel().getSelectedMedicine().getFcMedicine().getMedicine().getNameForDisplay();
						Presenter.showError(med+": "+err);
					}
				}
			}
		});
		btnNewButton.setBounds(10, 180, 91, 23);
		getContentPane().add(btnNewButton);

		JButton btnNewButton_1 = new JButton(Messages.getString("DlgEditMedicine.btnCancel.text")); //$NON-NLS-1$
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnNewButton_1.setBounds(219, 180, 91, 23);
		getContentPane().add(btnNewButton_1);

		arrivedDt = new JDateChooser();
		arrivedDt.getDateEditor().setEnabled(false);
		arrivedDt.setLocale(new Locale(Messages.getLanguage(), Messages.getCountry()));
		arrivedDt.getJCalendar().setWeekOfYearVisible(false);
		arrivedDt.setBounds(213, 11, 171, 20);
		/*		arrivedDt.addPropertyChangeListener("date", new PropertyChangeListener() {			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (!isEdit && dateEditAtFirst  && evt.getOldValue()==null && evt.getNewValue()!=null){
					arrivedDt.setDate(null);
					dateEditAtFirst = false;
				}				
			}
		});*/
		getContentPane().add(arrivedDt);

		expiriedDt = new JDateChooser();
		expiriedDt.getDateEditor().setEnabled(false);
		expiriedDt.setLocale(new Locale(Messages.getLanguage(), Messages.getCountry()));
		expiriedDt.setBounds(213, 50, 171, 20);
		expiriedDt.getJCalendar().setWeekOfYearVisible(false);
		/*		expiriedDt.addPropertyChangeListener("date", new PropertyChangeListener() {			
			@SuppressWarnings("deprecation")
			@Override
			public void propertyChange(PropertyChangeEvent evt) {				
				if (evt.getNewValue()!=null && ((Date)evt.getNewValue()).getYear()==8099 && ((Date)evt.getNewValue()).getMonth()==9){
					expiriedDt.setDate(null);
				}				
			}
		});*/
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
		quantityTxt.setBounds(211, 87, 153, 20);
		getContentPane().add(quantityTxt);

		JLabel label = new JLabel(Messages.getString("ForecastingDocumentWindow.tbParameters.SubTab.SelectedMedicines.comment"));
		label.setBounds(10, 115, 267, 14);
		getContentPane().add(label);

		commentTxt = new JTextField();
		commentTxt.setColumns(10);
		commentTxt.setBounds(10, 140, 391, 20);
		getContentPane().add(commentTxt);
		PlainDocument doc = (PlainDocument) commentTxt.getDocument();
		doc.setDocumentFilter(new TextAreaFilter(63));

		eraseLbl = new JLabel(Messages.getString("DlgEditOrders.erase"));
		eraseLbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Calendar cal = new GregorianCalendar(9999, 9, 1);
				expiriedDt.setDate(null);
			}
		});
		eraseLbl.setFont(new Font("Tahoma", Font.PLAIN, 11));
		eraseLbl.setForeground(Color.BLUE);
		eraseLbl.setBounds(272, 70, 129, 14);
		Font font = eraseLbl.getFont();
		Map  attributes = font.getAttributes();
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		eraseLbl.setFont(font.deriveFont(attributes));
		eraseLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		getContentPane().add(eraseLbl);

		doc = (PlainDocument) quantityTxt.getDocument();
		doc.setDocumentFilter(new NumericFilter());
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

	protected Calendar getRefDate() {
		return this.refDate;
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
	 * Are fields valid
	 * @return
	 */
	protected boolean isFieldsValid() {
		if (arrivedDt.getDate()==null){
			Presenter.showError(Messages.getString("Error.Validation.OrderSave.ReciveDateBlank"));
			return false;
		}
		/*		if (arrivedDt.getDate()!=null && arrivedDt.getCalendar().compareTo(refDate) < 0) {
			Presenter.showError(Messages.getString("Error.Validation.OrderSave.ReciveDate"));
			return false;
		}

		Calendar exp = expiriedDt.getCalendar();
		Calendar arr = arrivedDt.getCalendar();
		if(exp != null){
			DateUtils.cleanTime(exp);
			DateUtils.cleanTime(arr);
			if(exp.compareTo(arr) < 0){
				Presenter.showError(Messages.getString("Error.Validation.OrderSave.ExpireDate"));
				return false;
			}
		}*/

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
		setSize(new Dimension(420, 242));
		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		int wdwLeft = screenSize.width / 2 - getWidth() / 2;
		int wdwTop = screenSize.height / 2 - getHeight() / 2;
		setLocation(wdwLeft, wdwTop);
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
	}
	protected void initDataBindings() {
		BeanProperty<ForecastingOrderUIAdapter, Date> forecastingOrderUIAdapterBeanProperty = BeanProperty.create("arrivedDt");
		BeanProperty<JDateChooser, Date> jDateChooserBeanProperty = BeanProperty.create("date");
		AutoBinding<ForecastingOrderUIAdapter, Date, JDateChooser, Date> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, order, forecastingOrderUIAdapterBeanProperty, arrivedDt, jDateChooserBeanProperty);
		autoBinding.bind();
		//
		BeanProperty<ForecastingOrderUIAdapter, Integer> forecastingOrderUIAdapterBeanProperty_2 = BeanProperty.create("batch.quantity");
		BeanProperty<JTextField, String> jTextFieldBeanProperty = BeanProperty.create("text");
		AutoBinding<ForecastingOrderUIAdapter, Integer, JTextField, String> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, order, forecastingOrderUIAdapterBeanProperty_2, quantityTxt, jTextFieldBeanProperty);
		autoBinding_2.bind();
		//
		BeanProperty<ForecastingOrderUIAdapter, Date> forecastingOrderUIAdapterBeanProperty_3 = BeanProperty.create("batch.expiredDtEdit");
		AutoBinding<ForecastingOrderUIAdapter, Date, JDateChooser, Date> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, order, forecastingOrderUIAdapterBeanProperty_3, expiriedDt, jDateChooserBeanProperty);
		autoBinding_3.bind();
		//
		BeanProperty<ForecastingOrderUIAdapter, String> forecastingOrderUIAdapterBeanProperty_1 = BeanProperty.create("comment");
		BeanProperty<JTextField, String> jTextFieldBeanProperty_1 = BeanProperty.create("text");
		AutoBinding<ForecastingOrderUIAdapter, String, JTextField, String> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, order, forecastingOrderUIAdapterBeanProperty_1, commentTxt, jTextFieldBeanProperty_1);
		autoBinding_1.bind();
	}
}
