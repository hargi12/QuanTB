package org.msh.quantb.view.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.msh.quantb.model.gen.ClassifierTypesEnum;
import org.msh.quantb.model.gen.MedicineTypesEnum;
import org.msh.quantb.services.io.MedicineTmpStore;
import org.msh.quantb.services.io.MedicineUIAdapter;
import org.msh.quantb.services.io.MedicinesDicUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.EnumListRenderer;
import org.msh.quantb.view.ToolTipCellRenderer;
import javax.swing.JLabel;

public class MedicinesDlg extends JDialog {

	private static final long serialVersionUID = -5079877437055637643L;
	private final JPanel panelContent = new JPanel();
	private JTable medicinesTable;
	private MedicinesDicUIAdapter medicinesDic;
	private MedicineTmpStore selected = new MedicineTmpStore(null);
	private JButton btnEdit;
	private JButton btnDelete;	
	private JComboBox typeBox;

	/**
	 * Create the dialog.
	 * @param selectedMed 
	 * 
	 * @param owner
	 */
	public MedicinesDlg(MedicinesDicUIAdapter medicines, MedicineUIAdapter selectedMed, Frame owner) {
		super(owner);
		setSize(new Dimension(1054, 408));
		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		int wdwLeft = screenSize.width / 2 - getWidth() / 2;
		int wdwTop = screenSize.height / 2 - getHeight() / 2;
		setLocation(wdwLeft, wdwTop);
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		this.medicinesDic = medicines;
		setTitle(Messages.getString("DlgMedicines.title.text"));
		getContentPane().setLayout(new BorderLayout(0, 0));
		{
			JPanel panelTop = new JPanel();
			panelTop.setAlignmentX(Component.RIGHT_ALIGNMENT);
			getContentPane().add(panelTop, BorderLayout.NORTH);
			{
				JButton btnNew = new JButton(Messages.getString("DlgMedicines.btnNew.text"));
				btnNew.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Presenter.createNewMedicine();
					}
				});
				panelTop.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
				panelTop.add(btnNew);
			}
			{
				btnEdit = new JButton(Messages.getString("DlgMedicines.btnEdit.text"));
				btnEdit.setEnabled(false);
				btnEdit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (selected.getMedicine() != null) {
							Presenter.editMedicine(selected.getMedicine());
						}
					}
				});
				panelTop.add(btnEdit);
			}
			{
				btnDelete = new JButton(Messages.getString("DlgMedicines.btnDelete.text"));
				btnDelete.setEnabled(false);
				btnDelete.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						MedicineUIAdapter object = new MedicineUIAdapter(selected.getMedicine().getMedicine());
						if (object.getMedicine() != null) {
							Presenter.deleteMedicine(object);
							Presenter.refreshMedicinesList(null);
						} else {
							Presenter.showError(Messages.getString("Error.medicines.deleteWithoutSelection"));
						}
					}
				});
				panelTop.add(btnDelete);
			}
			{
				JLabel medTypeLbl = new JLabel(Messages.getString("Medicine.type") + ":"); //$NON-NLS-1$
				panelTop.add(medTypeLbl);
			}
			{
				typeBox = new JComboBox();
				typeBox.setModel(new DefaultComboBoxModel(ClassifierTypesEnum.values()));
				typeBox.setRenderer(new EnumListRenderer("Medicine.classifier"));
				panelTop.add(typeBox);
			}


			panelContent.setBorder(new EmptyBorder(5, 5, 5, 5));
			getContentPane().add(panelContent, BorderLayout.CENTER);
			panelContent.setLayout(new BorderLayout(0, 0));
			{
				JPanel panelBottom = new JPanel();
				panelBottom.setPreferredSize(new Dimension(10, 23));
				panelBottom.setMinimumSize(new Dimension(10, 23));
				panelBottom.setSize(new Dimension(0, 23));
				getContentPane().add(panelBottom, BorderLayout.SOUTH);
				{
					JButton btnClose = new JButton(Messages.getString("MainWindow.menuFile.ItemClose.text")); //$NON-NLS-1$
					btnClose.setSize(new Dimension(65, 23));
					btnClose.setBounds(874, 0, 70, 23);
					btnClose.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							dispose();
						}
					});
					panelBottom.setLayout(null);
					btnClose.setActionCommand("Cancel");
					panelBottom.add(btnClose);
				}
			}
		}			
		medicinesTable = new JTable();
		medicinesTable.getTableHeader().setReorderingAllowed(false);
		medicinesTable.getTableHeader().setResizingAllowed(false);
		medicinesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		medicinesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		initDataBindings();
		JScrollPane scrollPane = new JScrollPane(medicinesTable);
		panelContent.add(scrollPane, BorderLayout.CENTER);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);		
		medicinesTable.getColumnModel().getColumn(0).setPreferredWidth(170);
		medicinesTable.getColumnModel().getColumn(0).setMaxWidth(170);
		medicinesTable.getColumnModel().getColumn(0).setHeaderValue(Messages.getString("DlgMedicines.clmnAbbrMultiLine.text"));
		medicinesTable.getColumnModel().getColumn(0).setCellRenderer(new ToolTipCellRenderer());
		medicinesTable.getColumnModel().getColumn(1).setHeaderValue(Messages.getString("DlgMedicines.clmnName.textNl"));
		medicinesTable.getColumnModel().getColumn(1).setCellRenderer(new ToolTipCellRenderer());
		medicinesTable.getColumnModel().getColumn(2).setPreferredWidth(200);
		medicinesTable.getColumnModel().getColumn(2).setMaxWidth(200);
		medicinesTable.getColumnModel().getColumn(2).setHeaderValue(Messages.getString("DlgMedicines.clmnStrength.textNl"));
		medicinesTable.getColumnModel().getColumn(2).setCellRenderer(new ToolTipCellRenderer());
		medicinesTable.getColumnModel().getColumn(3).setPreferredWidth(180);
		medicinesTable.getColumnModel().getColumn(3).setMaxWidth(180);
		medicinesTable.getColumnModel().getColumn(3).setHeaderValue(Messages.getString("DlgMedicines.clmnDosage.text"));
		medicinesTable.getColumnModel().getColumn(3).setCellRenderer(new ToolTipCellRenderer());
		medicinesTable.getColumnModel().getColumn(4).setHeaderValue(Messages.getString("Medicine.type"));
		medicinesTable.repaint();		
		selected.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				boolean b = selected.getMedicine() != null;
				btnDelete.setEnabled(b);
				btnEdit.setEnabled(b);
			}
		});	
		setRow(selectedMed);
	}

	/**
	 * @return the medicinesDicTable
	 */
	public JTable getMedicinesDicTable() {
		return medicinesTable;
	}

	/**
	 * Get selected medicine
	 * 
	 * @return selected medicine
	 */
	public MedicineTmpStore getSelected() {
		return selected;
	}

	/**
	 * Set row in MedicineDictionaryTable by medicine
	 * 
	 * @param medicine selected medicine
	 */
	public void setRow(MedicineUIAdapter medicine) {
		int index = -1;
		if(medicine != null){
			index = medicinesDic.getMedicinesDic().indexOf(medicine);
			if (index != -1){
					try {
						medicinesTable.getSelectionModel().setSelectionInterval(index,index);
					} catch (Exception e) {
						// nothing to do
					}
			}
		}
	}
	protected void initDataBindings() {
		BeanProperty<MedicinesDicUIAdapter, List<MedicineUIAdapter>> medicinesDicUIAdapterBeanProperty = BeanProperty.create("medicinesDicByName");
		JTableBinding<MedicineUIAdapter, MedicinesDicUIAdapter, JTable> jTableBinding = SwingBindings.createJTableBinding(UpdateStrategy.READ, medicinesDic, medicinesDicUIAdapterBeanProperty, medicinesTable);
		//
		BeanProperty<MedicineUIAdapter, String> medicineUIAdapterBeanProperty = BeanProperty.create("abbrevName");
		jTableBinding.addColumnBinding(medicineUIAdapterBeanProperty).setColumnName("New Column").setEditable(false);
		//
		BeanProperty<MedicineUIAdapter, String> medicineUIAdapterBeanProperty_1 = BeanProperty.create("name");
		jTableBinding.addColumnBinding(medicineUIAdapterBeanProperty_1).setColumnName("New Column").setEditable(false);
		//
		BeanProperty<MedicineUIAdapter, String> medicineUIAdapterBeanProperty_2 = BeanProperty.create("strength");
		jTableBinding.addColumnBinding(medicineUIAdapterBeanProperty_2).setColumnName("New Column").setEditable(false);
		//
		BeanProperty<MedicineUIAdapter, String> medicineUIAdapterBeanProperty_3 = BeanProperty.create("formDosageNotEmpty");
		jTableBinding.addColumnBinding(medicineUIAdapterBeanProperty_3).setColumnName("New Column").setEditable(false);
		//
		BeanProperty<MedicineUIAdapter, String> medicineUIAdapterBeanProperty_4 = BeanProperty.create("classifierAsString");
		jTableBinding.addColumnBinding(medicineUIAdapterBeanProperty_4).setColumnName("New Column").setEditable(false);
		//
		jTableBinding.setEditable(false);
		jTableBinding.bind();
		//
		BeanProperty<JTable, MedicineUIAdapter> jTableBeanProperty = BeanProperty.create("selectedElement");
		BeanProperty<MedicineTmpStore, MedicineUIAdapter> medicineTmpStoreBeanProperty = BeanProperty.create("medicine");
		AutoBinding<JTable, MedicineUIAdapter, MedicineTmpStore, MedicineUIAdapter> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, medicinesTable, jTableBeanProperty, selected, medicineTmpStoreBeanProperty);
		autoBinding.bind();
		//
		BeanProperty<MedicinesDicUIAdapter, ClassifierTypesEnum> medicinesDicUIAdapterBeanProperty_1 = BeanProperty.create("filter");
		BeanProperty<JComboBox, Object> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
		AutoBinding<MedicinesDicUIAdapter, ClassifierTypesEnum, JComboBox, Object> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, medicinesDic, medicinesDicUIAdapterBeanProperty_1, typeBox, jComboBoxBeanProperty);
		autoBinding_1.bind();
	}
}
