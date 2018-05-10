package org.msh.quantb.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.msh.quantb.services.io.MedicineUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.EnumListRenderer;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import org.msh.quantb.model.gen.MedicineTypesEnum;
import org.msh.quantb.model.gen.ClassifierTypesEnum;
import org.msh.quantb.model.gen.MedicineFormEnum;
import java.awt.Color;

/**
 * New medicine dialog.
 * 
 * @author User
 * 
 */
public class MedicineDlg extends JDialog {

	private static final long serialVersionUID = -7120876886940395626L;
	private final JPanel contentPanel = new JPanel();
	private JTextField txtName;
	private JTextField txtAbbr;
	private MedicineUIAdapter medicine;
	private JTextField txtStrength;
	private JTextField txtDosage;
	private boolean isEdit;
	private JComboBox medFormBox;
	private boolean isChanged = false;
	private JComboBox classifierBox;

	/**
	 * Create the dialog.
	 */
	public MedicineDlg(MedicineUIAdapter _medicine, Dialog owner, boolean _isEdit) {
		super(owner);
		addCloseListener();
		setChanged(false);
		if (_isEdit){
			setTitle(Messages.getString("Medicine.oldTitle"));
		}else{
			setTitle(Messages.getString("Medicine.newTitle"));
		}
		this.medicine = _medicine;
		this.isEdit = _isEdit;
		setTitle(_isEdit ? Messages.getString("DlgEditMedicine.EditTitle") : Messages.getString("DlgEditMedicine.NewTitle")); //$NON-NLS-1$
		initDialog();		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblMedicineName = new JLabel(Messages.getString("DlgEditMedicine.lblName.text"));
			lblMedicineName.setBounds(10, 11, 385, 14);
			contentPanel.add(lblMedicineName);
		}
		{
			JLabel lblAbbreviatedName = new JLabel(Messages.getString("DlgEditMedicine.lblAbbr.text"));
			lblAbbreviatedName.setBounds(10, 55, 167, 14);
			contentPanel.add(lblAbbreviatedName);
		}
		{
			txtName = new JTextField();
			txtName.setBounds(20, 29, 422, 22);
			contentPanel.add(txtName);
			txtName.setColumns(10);
		}
		{
			txtAbbr = new JTextField();
			txtAbbr.setBounds(170, 56, 272, 22);
			contentPanel.add(txtAbbr);
			txtAbbr.setColumns(10);
		}
		{
			JLabel lblStrength = new JLabel(Messages.getString("DlgEditMedicine.lblStrength.text")); //$NON-NLS-1$
			lblStrength.setBounds(10, 85, 153, 14);
			contentPanel.add(lblStrength);
		}
		{
			JLabel lblDosage = new JLabel(Messages.getString("DlgEditMedicine.lblDosage.text")); //$NON-NLS-1$
			lblDosage.setBounds(10, 123, 165, 14);
			contentPanel.add(lblDosage);
		}
		{
			txtStrength = new JTextField();
			txtStrength.setBounds(170, 85, 272, 22);
			contentPanel.add(txtStrength);
			txtStrength.setColumns(10);
		}
		{
			txtDosage = new JTextField();
			txtDosage.setBounds(28, 148, 414, 20);
			contentPanel.add(txtDosage);
			txtDosage.setColumns(10);
		}

		JLabel lblNewLabel = new JLabel(Messages.getString("DlgEditMedicine.lblType"));
		lblNewLabel.setBounds(10, 179, 153, 14);
		contentPanel.add(lblNewLabel);
		List<MedicineTypesEnum> values = Arrays.asList(MedicineTypesEnum.values());
		EnumListRenderer renderer = new EnumListRenderer("Medicine.types");

		medFormBox = new JComboBox();
		medFormBox.setModel(new DefaultComboBoxModel(MedicineFormEnum.values()));
		EnumListRenderer formRenderer = new EnumListRenderer("Medicine.forms");
		medFormBox.setRenderer(formRenderer);
		medFormBox.setBounds(170, 120, 272, 20);
		contentPanel.add(medFormBox);
		

		classifierBox = new JComboBox();
		classifierBox.setModel(new DefaultComboBoxModel(ClassifierTypesEnum.values()));
		EnumListRenderer classiRenderer = new EnumListRenderer("Medicine.classifier");
		classifierBox.setRenderer(classiRenderer);
		classifierBox.setBounds(170, 176, 272, 20);
		contentPanel.add(classifierBox);
		
		JLabel oldTypeLbl1 = new JLabel(Messages.getString("Medicine.oldType"));
		oldTypeLbl1.setForeground(Color.LIGHT_GRAY);
		oldTypeLbl1.setBounds(10, 209, 153, 14);
		contentPanel.add(oldTypeLbl1);
		oldTypeLbl1.setVisible(medicine.getType()!=MedicineTypesEnum.UNKNOWN);
		JLabel oldTypeLbl2 = new JLabel(medicine.getTypeAsString()); //$NON-NLS-1$
		oldTypeLbl2.setForeground(Color.LIGHT_GRAY);
		oldTypeLbl2.setBounds(170, 209, 272, 14);
		contentPanel.add(oldTypeLbl2);
		oldTypeLbl2.setVisible(medicine.getType()!=MedicineTypesEnum.UNKNOWN);

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.LEFT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton(Messages.getString("DlgEditMedicine.btnSave.text")); //$NON-NLS-1$
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(medicine.getType() == null){
							Presenter.showError(Messages.getString("Error.newMedicine.badType"));
						}else{
							if (validateFlds()) {
								if (Presenter.checkMedicine(medicine, isEdit)){
									if (isEdit) {
										Presenter.saveMedicineDic(medicine);
									} else {
										Presenter.addMedicineToDic(medicine);
									}
									dispose();
									Presenter.refreshMedicinesList(medicine);
								}else{
									if (isEdit){
										Presenter.showError(Messages.getString("Error.medicines.exist_nothing"));
										dispose();  //silently
									}else{
										Presenter.showError(Messages.getString("Error.medicines.exist"));
									}
								}
							} else {
								Presenter.showError(Messages.getString("Error.newMedicine.validFields"));
							}
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton(Messages.getString("DlgEditMedicine.btnCancel.text")); //$NON-NLS-1$
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onCancel();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		hideOrShowDosage();
		addListeners();
		initDataBindings();
	}

	/**
	 * Process system close button - X in the left upper
	 */
	private void addCloseListener() {
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

	}


	/**
	 * @return the isChanged
	 */
	public boolean isChanged() {
		return isChanged;
	}


	/**
	 * @param isChanged the isChanged to set
	 */
	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}


	/**
	 * Set additional listeners
	 */
	private void addListeners() {
		this.medicine.addPropertyChangeListener("form", new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				hideOrShowDosage();
			}
		});
		this.medicine.addPropertyChangeListener(new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				setChanged(true);
			}

		});
	}

	/**
	 * hide or show the dosage field
	 */
	protected void hideOrShowDosage() {
		if (medicine.getType() != null){
			if (medicine.getForm() == MedicineFormEnum.OTHER){
				txtDosage.setVisible(true);
			}else{
				txtDosage.setVisible(false);
				medicine.setDosage("");
			}
		}else{
			txtDosage.setVisible(false);
			medicine.setDosage("");
		}

	}
	/**
	 * validate fields before save
	 * @return
	 */
	private boolean validateFlds() {
		boolean ret = !medicine.getName().isEmpty()
				&& !medicine.getAbbrevName().isEmpty() && 
				!medicine.getStrength().isEmpty() && 
				!(medicine.getForm() == null);
		if (needDosageFld()){
			ret = ret && this.medicine.getDosage().length()>0;
		}
		return ret;
	}
	/**
	 * Does dosage field need
	 * @return
	 */
	private boolean needDosageFld() {
		return (medicine.getForm() == MedicineFormEnum.OTHER)
				&& medicine.getClassifier()!=ClassifierTypesEnum.UNKNOWN && medicine.getClassifier()!=ClassifierTypesEnum.OTHER_SUPPLIERS_MEDICINES; //AK 20161222
	}

	/**
	 * Initialize dimension and modality of the dialog and position at center of
	 * the screen.
	 */
	private void initDialog() {
		setSize(new Dimension(458, 297));
		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		int wdwLeft = screenSize.width / 2 - getWidth() / 2;
		int wdwTop = screenSize.height / 2 - getHeight() / 2;
		setLocation(wdwLeft, wdwTop);
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
	}

	private void onCancel() {
		if (isChanged){
			if (Presenter.askCancel()){
				dispose();
			}
		}else{
			dispose();
		}
	}
	protected void initDataBindings() {
		BeanProperty<MedicineUIAdapter, String> medicineUIAdapterBeanProperty = BeanProperty.create("name");
		BeanProperty<JTextField, String> jTextFieldBeanProperty = BeanProperty.create("text");
		AutoBinding<MedicineUIAdapter, String, JTextField, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, medicine, medicineUIAdapterBeanProperty, txtName, jTextFieldBeanProperty);
		autoBinding.bind();
		//
		BeanProperty<MedicineUIAdapter, String> medicineUIAdapterBeanProperty_1 = BeanProperty.create("abbrevName");
		BeanProperty<JTextField, String> jTextFieldBeanProperty_1 = BeanProperty.create("text");
		AutoBinding<MedicineUIAdapter, String, JTextField, String> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, medicine, medicineUIAdapterBeanProperty_1, txtAbbr, jTextFieldBeanProperty_1);
		autoBinding_1.bind();
		//
		BeanProperty<MedicineUIAdapter, String> medicineUIAdapterBeanProperty_2 = BeanProperty.create("strength");
		BeanProperty<JTextField, String> jTextFieldBeanProperty_2 = BeanProperty.create("text");
		AutoBinding<MedicineUIAdapter, String, JTextField, String> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, medicine, medicineUIAdapterBeanProperty_2, txtStrength, jTextFieldBeanProperty_2);
		autoBinding_2.bind();
		//
		BeanProperty<MedicineUIAdapter, String> medicineUIAdapterBeanProperty_3 = BeanProperty.create("dosage");
		BeanProperty<JTextField, String> jTextFieldBeanProperty_3 = BeanProperty.create("text");
		AutoBinding<MedicineUIAdapter, String, JTextField, String> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, medicine, medicineUIAdapterBeanProperty_3, txtDosage, jTextFieldBeanProperty_3);
		autoBinding_3.bind();
		//
		BeanProperty<MedicineUIAdapter, MedicineFormEnum> medicineUIAdapterBeanProperty_5 = BeanProperty.create("form");
		BeanProperty<JComboBox, Object> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
		AutoBinding<MedicineUIAdapter, MedicineFormEnum, JComboBox, Object> autoBinding_5 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, medicine, medicineUIAdapterBeanProperty_5, medFormBox, jComboBoxBeanProperty);
		autoBinding_5.bind();
		//
		BeanProperty<MedicineUIAdapter, ClassifierTypesEnum> medicineUIAdapterBeanProperty_6 = BeanProperty.create("classifier");
		AutoBinding<MedicineUIAdapter, ClassifierTypesEnum, JComboBox, Object> autoBinding_6 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, medicine, medicineUIAdapterBeanProperty_6, classifierBox, jComboBoxBeanProperty);
		autoBinding_6.bind();
	}
}
