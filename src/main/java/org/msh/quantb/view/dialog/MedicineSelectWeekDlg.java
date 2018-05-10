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
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.view.EnumListRenderer;
import org.msh.quantb.view.ToolTipCellRenderer;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import org.msh.quantb.model.gen.MedicineTypesEnum;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.Bindings;

/**
 * select multiply medicine from the all medicines list
 * Return result to the Consumer
 * Consumer must implement interface IMutiMedSelection
 * use checkboxes
 * @author alexey
 *
 */
public class MedicineSelectWeekDlg extends JDialog {

	private static final long serialVersionUID = -5079877437055637643L;
	private final JPanel panelContent = new JPanel();	
	private JTable medicinesDicTable;
	private MedicinesDicUIAdapter medicinesDic;
	private List<MedicineUIAdapter> selectedList = new ArrayList<MedicineUIAdapter>();
	private MedicineSelectWeekDlg _self;
	private IMultiMedSelection actioner;
	private JComboBox typeBox;
	/**
	 * Only valid constructor
	 * @param owner frame owner
	 * @param medicines medicines dictionary
	 * @param _actioner class, need selected medicines
	 */
	public MedicineSelectWeekDlg(Frame owner,MedicinesDicUIAdapter medicines, IMultiMedSelection _actioner) {
		super(owner);
		_self=this;
		this.medicinesDic = medicines;
		this.actioner = _actioner;
		setSize(new Dimension(885, 419));
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
			panelContent.setBounds(1, 35, 878, 303);

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
					btnClose.setBounds(95, 5, 75, 23);
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
							_self.setVisible(false);
							selectedList.clear();
							for(MedicineUIAdapter m : medicinesDic.getMedicinesDic()){
								if (m.getChecked()){
									selectedList.add(m);
								}
							}
							actioner.addSelected(selectedList);
							_self.setVisible(false);
							_self.dispose();
						}
					});
					button.setActionCommand("OK");
					button.setBounds(10, 5, 75, 23);
					panelBottom.add(button);
				}
			}			
		}	
		typeBox = new JComboBox();
		typeBox.setModel(new DefaultComboBoxModel(MedicineTypesEnum.values()));
		typeBox.setBounds(668, 4, 201, 20);
		typeBox.setRenderer(new EnumListRenderer("Medicine.types"));
		getContentPane().add(typeBox);
		initDataBindings();
		medicinesDicTable.getColumnModel().getColumn(0).setPreferredWidth(45);
		medicinesDicTable.getColumnModel().getColumn(0).setHeaderValue(Messages.getString("DlgMedicines.clmnCheck.text"));
		medicinesDicTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
		medicinesDicTable.getColumnModel().getColumn(1).setPreferredWidth(200);	
		medicinesDicTable.getColumnModel().getColumn(1).setHeaderValue(Messages.getString("DlgMedicines.clmnAbbr.text"));
		medicinesDicTable.getColumnModel().getColumn(2).setPreferredWidth(300);
		medicinesDicTable.getColumnModel().getColumn(2).setCellRenderer(new ToolTipCellRenderer());
		medicinesDicTable.getColumnModel().getColumn(2).setHeaderValue(Messages.getString("DlgMedicines.clmnName.text"));
		medicinesDicTable.getColumnModel().getColumn(3).setPreferredWidth(170);
		medicinesDicTable.getColumnModel().getColumn(3).setHeaderValue(Messages.getString("DlgMedicines.clmnStrength.text"));
		medicinesDicTable.getColumnModel().getColumn(4).setPreferredWidth(170);
		medicinesDicTable.getColumnModel().getColumn(4).setHeaderValue(Messages.getString("DlgMedicines.clmnDosage.text"));		
		medicinesDicTable.setRowHeight(medicinesDicTable.getRowHeight()*2);
		medicinesDicTable.repaint();
	}
	protected void initDataBindings() {
		BeanProperty<MedicinesDicUIAdapter, List<MedicineUIAdapter>> medicinesDicUIAdapterBeanProperty = BeanProperty.create("medicinesDic");
		JTableBinding<MedicineUIAdapter, MedicinesDicUIAdapter, JTable> jTableBinding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE, medicinesDic, medicinesDicUIAdapterBeanProperty, medicinesDicTable);
		//
		BeanProperty<MedicineUIAdapter, Boolean> medicineUIAdapterBeanProperty = BeanProperty.create("checked");
		jTableBinding.addColumnBinding(medicineUIAdapterBeanProperty).setColumnClass(Boolean.class);
		//
		BeanProperty<MedicineUIAdapter, String> medicineUIAdapterBeanProperty_1 = BeanProperty.create("abbrevName");
		jTableBinding.addColumnBinding(medicineUIAdapterBeanProperty_1).setColumnName("abbrev").setEditable(false);
		//
		BeanProperty<MedicineUIAdapter, String> medicineUIAdapterBeanProperty_2 = BeanProperty.create("name");
		jTableBinding.addColumnBinding(medicineUIAdapterBeanProperty_2).setColumnName("name").setEditable(false);
		//
		BeanProperty<MedicineUIAdapter, String> medicineUIAdapterBeanProperty_3 = BeanProperty.create("strength");
		jTableBinding.addColumnBinding(medicineUIAdapterBeanProperty_3).setColumnName("Strength").setEditable(false);
		//
		BeanProperty<MedicineUIAdapter, String> medicineUIAdapterBeanProperty_4 = BeanProperty.create("dosage");
		jTableBinding.addColumnBinding(medicineUIAdapterBeanProperty_4).setColumnName("Dosage").setEditable(false);
		//
		jTableBinding.bind();
		//
		BeanProperty<MedicinesDicUIAdapter, MedicineTypesEnum> medicinesDicUIAdapterBeanProperty_1 = BeanProperty.create("filter");
		BeanProperty<JComboBox, Object> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
		AutoBinding<MedicinesDicUIAdapter, MedicineTypesEnum, JComboBox, Object> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, medicinesDic, medicinesDicUIAdapterBeanProperty_1, typeBox, jComboBoxBeanProperty);
		autoBinding.bind();
	}
}
