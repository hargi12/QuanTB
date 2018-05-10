package org.msh.quantb.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.msh.quantb.model.gen.ClassifierTypesEnum;
import org.msh.quantb.model.gen.MedicineRegimen;
import org.msh.quantb.model.gen.MedicineTypesEnum;
import org.msh.quantb.services.io.MedicineUIAdapter;
import org.msh.quantb.services.io.MedicinesDicUIAdapter;
import org.msh.quantb.services.io.PhaseUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.EnumListRenderer;
import org.msh.quantb.view.ISelectableMedicine;
import org.msh.quantb.view.ToolTipCellRenderer;
import javax.swing.JLabel;

/**
 * select one medicine from the all medicines list
 * @author alexey
 *
 */
public class MedicineSelectDlg extends JDialog {

	private static final long serialVersionUID = -5079877437055637643L;
	private final JPanel panelContent = new JPanel();	
	private JTable medicinesDicTable;
	private MedicinesDicUIAdapter medicinesDic;

	private MedicineSelectDlg _self;
	private ISelectableMedicine applicant = null;
	private JComboBox typeBox;
	private PhaseUIAdapter phase=null;
	private JTable phaseDataTable=null;

	/**
	 * Create the dialog.
	 * @param _applicant object need to medicine selection
	 * @param owner frame owner I don't know why
	 */
	public MedicineSelectDlg(MedicinesDicUIAdapter medicines, ISelectableMedicine _applicant, Frame owner) {
		super(owner);
		_self=this;
		this.applicant = _applicant;
		this.medicinesDic = medicines;
		//setSelectedIndex(_applicant.getSelectedMedicine());
		setSize(new Dimension(1085, 419));
		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());        
		int wdwLeft = screenSize.width / 2 - getWidth()/ 2;
		int wdwTop = screenSize.height / 2 - getHeight()/ 2;
		setLocation(wdwLeft, wdwTop);        
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setTitle(Messages.getString("DlgMedicineSelect.title")); //$NON-NLS-1$
		getContentPane().setLayout(null);
		{
			panelContent.setBounds(1, 35, 1068, 303);

			panelContent.setBorder(new EmptyBorder(5, 5, 5, 5));
			getContentPane().add(panelContent);
			panelContent.setLayout(new BorderLayout(0, 0));			
			medicinesDicTable = new JTable();			
			medicinesDicTable.getTableHeader().setReorderingAllowed(false);
			medicinesDicTable.getTableHeader().setResizingAllowed(false);
			medicinesDicTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			medicinesDicTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			JScrollPane scrollPane = new JScrollPane(medicinesDicTable);				
			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			panelContent.add(scrollPane, BorderLayout.CENTER);
			{
				JPanel panelBottom = new JPanel();
				panelBottom.setBounds(0, 349, 704, 33);
				getContentPane().add(panelBottom);
				{
					JButton btnClose = new JButton(Messages.getString("DlgMedicineSelect.btnCancel.text")); //$NON-NLS-1$
					btnClose.setBounds(117, 5, 97, 23);
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
							MedicineUIAdapter selected = null;
							int row = medicinesDicTable.getSelectedRow();
							if (row > -1){
								selected = medicinesDic.getMedicinesDicByName().get(row);
							}
							if (applicant != null){
								applicant.setSelectedMedicine(selected);
							}else{
								if (phase != null && selected != null){
									phase.getPhase().getMedications().clear();
									MedicineRegimen mr = Presenter.getFactory().createMedication(selected.getMedicine(), phase.getDurationValue(), 0, 0);
									phase.getPhase().getMedications().add(mr);
									AbstractTableModel model = (AbstractTableModel) phaseDataTable.getModel();
									model.fireTableDataChanged();
									phaseDataTable.revalidate();
									dispose();
								}else{
									if(selected == null){
										Presenter.showError(Messages.getString("Regimen.errors.mednotselected"));
									}
								}
							}
						};
					});
					button.setActionCommand("OK");
					button.setBounds(10, 5, 97, 23);
					panelBottom.add(button);
				}
			}			
		}
		typeBox = new JComboBox();
		typeBox.setModel(new DefaultComboBoxModel(ClassifierTypesEnum.values()));
		typeBox.setBounds(783, 11, 201, 20);
		typeBox.setRenderer(new EnumListRenderer("Medicine.classifier"));
		getContentPane().add(typeBox);
		
		JLabel lblNewLabel = new JLabel(Messages.getString("Medicine.type") + ":"); //$NON-NLS-1$
		lblNewLabel.setBounds(633, 14, 129, 14);
		getContentPane().add(lblNewLabel);
		initDataBindings();				
		medicinesDicTable.getColumnModel().getColumn(0).setPreferredWidth(170);
		medicinesDicTable.getColumnModel().getColumn(0).setMaxWidth(170);
		medicinesDicTable.getColumnModel().getColumn(0).setHeaderValue(Messages.getString("DlgMedicines.clmnAbbr.text"));
		medicinesDicTable.getColumnModel().getColumn(0).setCellRenderer(new ToolTipCellRenderer());
		//medicinesDicTable.getColumnModel().getColumn(1).setPreferredWidth(340);
		medicinesDicTable.getColumnModel().getColumn(1).setCellRenderer(new ToolTipCellRenderer());
		medicinesDicTable.getColumnModel().getColumn(1).setHeaderValue(Messages.getString("DlgMedicines.clmnName.text"));
		medicinesDicTable.getColumnModel().getColumn(2).setPreferredWidth(200);
		medicinesDicTable.getColumnModel().getColumn(2).setMaxWidth(200);
		medicinesDicTable.getColumnModel().getColumn(2).setHeaderValue(Messages.getString("DlgMedicines.clmnStrength.text"));
		medicinesDicTable.getColumnModel().getColumn(2).setCellRenderer(new ToolTipCellRenderer());
		medicinesDicTable.getColumnModel().getColumn(3).setPreferredWidth(200);
		medicinesDicTable.getColumnModel().getColumn(3).setMaxWidth(200);
		medicinesDicTable.getColumnModel().getColumn(3).setHeaderValue(Messages.getString("DlgMedicines.clmnDosage.textWithOutNL"));
		medicinesDicTable.getColumnModel().getColumn(3).setCellRenderer(new ToolTipCellRenderer());
		medicinesDicTable.getColumnModel().getColumn(4).setHeaderValue(Messages.getString("Medicine.type"));
		medicinesDicTable.repaint();
	}

	/**
	 * @return the medicinesDicTable
	 */
	public JTable getMedicinesDicTable() {
		return medicinesDicTable;
	}

	/**
	 * Set the phase related information (new style)
	 * @param phase
	 * @param phaseDataTable
	 */
	public void setPhase(PhaseUIAdapter phase, JTable phaseDataTable) {
		this.phase = phase;
		this.phaseDataTable = phaseDataTable;

	}
	protected void initDataBindings() {
		BeanProperty<MedicinesDicUIAdapter, List<MedicineUIAdapter>> medicinesDicUIAdapterBeanProperty = BeanProperty.create("medicinesDicByName");
		JTableBinding<MedicineUIAdapter, MedicinesDicUIAdapter, JTable> jTableBinding = SwingBindings.createJTableBinding(UpdateStrategy.READ, medicinesDic, medicinesDicUIAdapterBeanProperty, medicinesDicTable);
		//
		BeanProperty<MedicineUIAdapter, String> medicineUIAdapterBeanProperty = BeanProperty.create("abbrevName");
		jTableBinding.addColumnBinding(medicineUIAdapterBeanProperty).setColumnName("abbrev").setEditable(false);
		//
		BeanProperty<MedicineUIAdapter, String> medicineUIAdapterBeanProperty_1 = BeanProperty.create("name");
		jTableBinding.addColumnBinding(medicineUIAdapterBeanProperty_1).setColumnName("name").setEditable(false);
		//
		BeanProperty<MedicineUIAdapter, String> medicineUIAdapterBeanProperty_2 = BeanProperty.create("strength");
		jTableBinding.addColumnBinding(medicineUIAdapterBeanProperty_2).setColumnName("Strength").setEditable(false);
		//
		BeanProperty<MedicineUIAdapter, String> medicineUIAdapterBeanProperty_3 = BeanProperty.create("formDosage");
		jTableBinding.addColumnBinding(medicineUIAdapterBeanProperty_3).setColumnName("Dosage").setEditable(false);
		//
		BeanProperty<MedicineUIAdapter, String> medicineUIAdapterBeanProperty_4 = BeanProperty.create("classifierAsString");
		jTableBinding.addColumnBinding(medicineUIAdapterBeanProperty_4).setColumnName("New Column");
		//
		jTableBinding.setEditable(false);
		jTableBinding.bind();
		//
		BeanProperty<MedicinesDicUIAdapter, ClassifierTypesEnum> medicinesDicUIAdapterBeanProperty_1 = BeanProperty.create("filter");
		BeanProperty<JComboBox, Object> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
		AutoBinding<MedicinesDicUIAdapter, ClassifierTypesEnum, JComboBox, Object> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, medicinesDic, medicinesDicUIAdapterBeanProperty_1, typeBox, jComboBoxBeanProperty);
		autoBinding_1.bind();
	}
}
