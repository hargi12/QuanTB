package org.msh.quantb.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.msh.quantb.services.io.MedicineUIAdapter;
import org.msh.quantb.services.io.MedicinesDicUIAdapter;
import org.msh.quantb.services.io.PhaseUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.EnumListRenderer;
import org.msh.quantb.view.ToolTipCellRenderer;
import org.msh.quantb.model.gen.ClassifierTypesEnum;
import org.msh.quantb.model.gen.MedicineTypesEnum;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.Bindings;
import javax.swing.JLabel;

/**
 * select multiply medicine from the all medicines list
 * use checkboxes
 * @author alexey
 *
 */
public class MedicineSelectMulDlg extends JDialog {

	private static final long serialVersionUID = -5079877437055637643L;
	private final JPanel panelContent = new JPanel();	
	private JTable medicinesDicTable;
	private MedicinesDicUIAdapter medicinesDic;
	private List<MedicineUIAdapter> selectedList = new ArrayList<MedicineUIAdapter>();
	private MedicineSelectMulDlg _self;
	private JComboBox typeBox;
	private PhaseUIAdapter phase = null;
	private JTable phaseDataTable;
	/**
	 * Create the dialog.
	 * @param owner frame owner I don't know why
	 */
	public MedicineSelectMulDlg(MedicinesDicUIAdapter medicines, Frame owner) {
		super(owner);
		_self=this;
		this.medicinesDic = medicines;
		setSize(new Dimension(1107, 419));
		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());        
		int wdwLeft = screenSize.width / 2 - getWidth()/ 2;
		int wdwTop = screenSize.height / 2 - getHeight()/ 2;
		setLocation(wdwLeft, wdwTop);        
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setModal(true);
		setTitle(Messages.getString("DlgMedicineSelect.title")); //$NON-NLS-1$
		getContentPane().setLayout(null);
		{
			panelContent.setBounds(1, 35, 1090, 303);

			panelContent.setBorder(new EmptyBorder(5, 5, 5, 5));
			getContentPane().add(panelContent);
			panelContent.setLayout(new BorderLayout(0, 0));			
			medicinesDicTable = new JTable();
			medicinesDicTable.getTableHeader().setReorderingAllowed(false);
			medicinesDicTable.getTableHeader().setResizingAllowed(false);
			medicinesDicTable.setRowSelectionAllowed(false);			
			medicinesDicTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);	


			JScrollPane scrollPane = new JScrollPane(medicinesDicTable);				
			panelContent.add(scrollPane, BorderLayout.CENTER);
			{
				JPanel panelBottom = new JPanel();
				panelBottom.setBounds(0, 349, 354, 33);
				getContentPane().add(panelBottom);
				{
					JButton btnClose = new JButton(Messages.getString("DlgMedicineSelect.btnCancel.text")); //$NON-NLS-1$
					btnClose.setBounds(119, 5, 99, 23);
					btnClose.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							dispose();
						}
					});
					panelBottom.setLayout(null);
					btnClose.setActionCommand("Cancel");
					panelBottom.add(btnClose);
				}
				{
					JButton button = new JButton(Messages.getString("DlgEditMedicine.btnSave.text")); //$NON-NLS-1$
					button.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							selectedList.clear();
							for(MedicineUIAdapter m : medicinesDic.getMedicinesDicAll()){
								if (m.getChecked()){
									selectedList.add(m);
								}
							}
							if(!selectedList.isEmpty()){
								if (phase != null){
									Presenter.addSelectedMedicineToAddPhase(selectedList, phase, phaseDataTable);
								}else{
									Presenter.addSelectedMedicinesToPhase(selectedList);
								}
								dispose();
							}else{
								Presenter.showError(Messages.getString("Regimen.errors.mednotselected"));
							}
						}
					});
					button.setActionCommand("OK");
					button.setBounds(10, 5, 99, 23);
					panelBottom.add(button);
				}
			}			
		}
		typeBox = new JComboBox();
		typeBox.setModel(new DefaultComboBoxModel(ClassifierTypesEnum.values()));
		typeBox.setBounds(783, 11, 201, 20);
		typeBox.setRenderer(new EnumListRenderer("Medicine.classifier"));
		getContentPane().add(typeBox);
		{
			JLabel lblNewLabel = new JLabel(Messages.getString("Medicine.type") + ":"); //$NON-NLS-1$
			lblNewLabel.setBounds(621, 14, 152, 14);
			getContentPane().add(lblNewLabel);
		}
		initDataBindings();
		medicinesDicTable.getColumnModel().getColumn(0).setPreferredWidth(20);
		medicinesDicTable.getColumnModel().getColumn(0).setMaxWidth(20);
		medicinesDicTable.getColumnModel().getColumn(0).setHeaderValue(Messages.getString("DlgMedicines.clmnCheck.text"));
		medicinesDicTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
		medicinesDicTable.getColumnModel().getColumn(1).setPreferredWidth(180);
		medicinesDicTable.getColumnModel().getColumn(1).setMaxWidth(180);
		medicinesDicTable.getColumnModel().getColumn(1).setHeaderValue(Messages.getString("DlgMedicines.clmnAbbr.text"));
		medicinesDicTable.getColumnModel().getColumn(1).setCellRenderer(new ToolTipCellRenderer());
		medicinesDicTable.getColumnModel().getColumn(2).setCellRenderer(new ToolTipCellRenderer());
		medicinesDicTable.getColumnModel().getColumn(2).setHeaderValue(Messages.getString("DlgMedicines.clmnName.text"));
		medicinesDicTable.getColumnModel().getColumn(2).setCellRenderer(new ToolTipCellRenderer());
		medicinesDicTable.getColumnModel().getColumn(3).setPreferredWidth(200);
		medicinesDicTable.getColumnModel().getColumn(3).setMaxWidth(200);
		medicinesDicTable.getColumnModel().getColumn(3).setHeaderValue(Messages.getString("DlgMedicines.clmnStrength.text"));
		medicinesDicTable.getColumnModel().getColumn(3).setCellRenderer(new ToolTipCellRenderer());
		medicinesDicTable.getColumnModel().getColumn(4).setHeaderValue(Messages.getString("DlgMedicines.clmnDosage.textWithOutNL"));		
		medicinesDicTable.getColumnModel().getColumn(4).setCellRenderer(new ToolTipCellRenderer());
		medicinesDicTable.getColumnModel().getColumn(4).setPreferredWidth(140);
		medicinesDicTable.getColumnModel().getColumn(4).setMaxWidth(140);
		medicinesDicTable.getColumnModel().getColumn(5).setHeaderValue(Messages.getString("Medicine.type"));
		//medicinesDicTable.setRowHeight(medicinesDicTable.getRowHeight()*2);
		medicinesDicTable.repaint();
	}

	/**
	 * Set the phase if this dialog used for add medication to the phase
	 * @param phase
	 * @param phaseDataTable 
	 */
	public void setPhase(PhaseUIAdapter phase, JTable phaseDataTable) {
		this.phase = phase;
		this.phaseDataTable = phaseDataTable;

	}
	protected void initDataBindings() {
		BeanProperty<MedicinesDicUIAdapter, List<MedicineUIAdapter>> medicinesDicUIAdapterBeanProperty = BeanProperty.create("medicinesDicByName");
		JTableBinding<MedicineUIAdapter, MedicinesDicUIAdapter, JTable> jTableBinding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE, medicinesDic, medicinesDicUIAdapterBeanProperty, medicinesDicTable);
		//
		BeanProperty<MedicineUIAdapter, Boolean> medicineUIAdapterBeanProperty = BeanProperty.create("checked");
		jTableBinding.addColumnBinding(medicineUIAdapterBeanProperty).setColumnClass(Boolean.class);
		//
		BeanProperty<MedicineUIAdapter, String> medicineUIAdapterBeanProperty_1 = BeanProperty.create("abbrevName");
		jTableBinding.addColumnBinding(medicineUIAdapterBeanProperty_1).setColumnName("New Column");
		//
		BeanProperty<MedicineUIAdapter, String> medicineUIAdapterBeanProperty_2 = BeanProperty.create("name");
		jTableBinding.addColumnBinding(medicineUIAdapterBeanProperty_2).setColumnName("abbrev").setEditable(false);
		//
		BeanProperty<MedicineUIAdapter, String> medicineUIAdapterBeanProperty_3 = BeanProperty.create("strength");
		jTableBinding.addColumnBinding(medicineUIAdapterBeanProperty_3).setColumnName("name").setEditable(false);
		//
		BeanProperty<MedicineUIAdapter, String> medicineUIAdapterBeanProperty_4 = BeanProperty.create("formDosage");
		jTableBinding.addColumnBinding(medicineUIAdapterBeanProperty_4).setColumnName("Strength").setEditable(false);
		//
		BeanProperty<MedicineUIAdapter, String> medicineUIAdapterBeanProperty_5 = BeanProperty.create("classifierAsString");
		jTableBinding.addColumnBinding(medicineUIAdapterBeanProperty_5).setColumnName("Dosage").setEditable(false);
		//
		jTableBinding.bind();
		//
		BeanProperty<MedicinesDicUIAdapter, ClassifierTypesEnum> medicinesDicUIAdapterBeanProperty_1 = BeanProperty.create("filter");
		BeanProperty<JComboBox, Object> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
		AutoBinding<MedicinesDicUIAdapter, ClassifierTypesEnum, JComboBox, Object> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, medicinesDic, medicinesDicUIAdapterBeanProperty_1, typeBox, jComboBoxBeanProperty);
		autoBinding.bind();
	}
}
