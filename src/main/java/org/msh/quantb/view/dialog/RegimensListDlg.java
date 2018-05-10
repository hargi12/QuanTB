package org.msh.quantb.view.dialog;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.PropertyStateEvent;
import org.jdesktop.beansbinding.PropertyStateListener;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.msh.quantb.model.gen.RegimenTypesEnum;
import org.msh.quantb.services.io.DisplayMedication;
import org.msh.quantb.services.io.MedicationUIAdapter;
import org.msh.quantb.services.io.MedicineUIAdapter;
import org.msh.quantb.services.io.PhaseUIAdapter;
import org.msh.quantb.services.io.RegimenTmpStore;
import org.msh.quantb.services.io.RegimenUIAdapter;
import org.msh.quantb.services.io.RegimensDicUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.EnumListRenderer;
import org.msh.quantb.view.tableExt.MultiLineCellRenderer;
import javax.swing.JTabbedPane;

public class RegimensListDlg extends JDialog implements ISingleMedSelection{

	private static final int MED_COL_WIDTH = 270;
	private static final long serialVersionUID = -5079877437055637643L;
	private final JPanel panelContent = new JPanel();
	private JTable regimensDicTable;
	private RegimensDicUIAdapter regimensDic;
	private RegimenTmpStore selected = new RegimenTmpStore(null);
	private JButton btnEdit;
	private JButton btnDelete;
	private JComboBox comboBox;
	private JButton btnDbl;
	private JTabbedPane tabbedPane;

	/**
	 * Create the dialog.
	 * 
	 * @param owner
	 */
	public RegimensListDlg(RegimensDicUIAdapter _regimensDic, Frame owner) {
		super(owner);
		setSize(new Dimension(1207, 581));//469
		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		int wdwLeft = screenSize.width / 2 - getWidth() / 2;
		int wdwTop = screenSize.height / 2 - getHeight() / 2;
		setLocation(wdwLeft, wdwTop);
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		this.regimensDic = _regimensDic;
		//regimensDic.setFilter(RegimenTypesEnum.MULTI_DRUG);
		setTitle(Messages.getString("Regimen")); //$NON-NLS-1$
		getContentPane().setLayout(null);
		{
			JPanel panelTop = new JPanel();
			panelTop.setBounds(10, 12, 396, 33);
			getContentPane().add(panelTop);
			panelTop.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			{

				btnEdit = new JButton(Messages.getString("DlgMedicines.btnEdit.text")); //$NON-NLS-1$
				btnEdit.setEnabled(false);
				btnEdit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (selected != null)
							Presenter.editRegimen(new RegimenTmpStore(selected.getRegimen()), true, true);
					}
				});
				JButton btnNew = new JButton(Messages.getString("DlgMedicines.btnNew.text")); //$NON-NLS-1$
				btnNew.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Presenter.addNewRegimen(regimensDic);
					}
				});
				btnDbl = new JButton(Messages.getString("Regimen.btn.double"));
				btnDbl.setEnabled(false);
				btnDbl.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (selected != null)
							Presenter.doubleRegimen(selected.getRegimen());
					}
				});
				panelTop.add(btnNew);
				panelTop.add(btnDbl);
				panelTop.add(btnEdit);
			}
			{
				btnDelete = new JButton(Messages.getString("DlgMedicines.btnDelete.text")); //$NON-NLS-1$
				btnDelete.setEnabled(false);
				btnDelete.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int i = regimensDicTable.getSelectedRow();
						if (i >= 0){
							Presenter.deleteRegimen(i);
						}
					}
				});
				panelTop.add(btnDelete);
			}
			panelContent.setBounds(10, 56, 1170, 440);

			panelContent.setBorder(new EmptyBorder(5, 5, 5, 5));
			getContentPane().add(panelContent);
			panelContent.setLayout(new BorderLayout(0, 0));
		}
		regimensDicTable = new JTable();
		regimensDicTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		regimensDicTable.getTableHeader().setResizingAllowed(false);
		regimensDicTable.getTableHeader().setReorderingAllowed(false);
		regimensDicTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scrollPane = new JScrollPane(regimensDicTable);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panelContent.add(scrollPane, BorderLayout.CENTER);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setPreferredSize(new Dimension(5, 300));
		panelContent.add(tabbedPane, BorderLayout.SOUTH);

		{
			JPanel panelBottom = new JPanel();
			panelBottom.setBounds(10, 507, 1170, 33);
			getContentPane().add(panelBottom);
			{
				JButton btnClose = new JButton(Messages.getString("MainWindow.menuFile.ItemClose.text")); //$NON-NLS-1$
				btnClose.setBounds(1095, 0, 70, 23);
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
		selected.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				btnEdit.setEnabled(selected.getRegimen() != null);
				btnDbl.setEnabled(selected.getRegimen() != null);
				btnDelete.setEnabled(selected.getRegimen() != null);
			}
		});

		JPanel panel = new JPanel();
		panel.setBounds(416, 12, 468, 33);
		getContentPane().add(panel);

		JLabel regTypeLbl = new JLabel(Messages.getString("Regimen.type")+ ":"); //$NON-NLS-1$
		panel.add(regTypeLbl);

		comboBox = new JComboBox();
		panel.add(comboBox);
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
				RegimenTypesEnum filter = (RegimenTypesEnum)cb.getSelectedItem();
				regimensDic.setFilter(filter);
			}
		});
		comboBox.setModel(new DefaultComboBoxModel(RegimenTypesEnum.values()));
		comboBox.setRenderer(new EnumListRenderer("Regimen.types"));
		//
		initDataBindings(); // created automatically, every time rewrites
		//
		addListeners();
		//
		adjustTables();

	}
	@Override
	public void adjustPhases(MedicineUIAdapter med) {
		selected.getRegimen().getIntensive().getPhase().getMedications().get(0).setMedicine(med.getMedicine());
		PhaseUIAdapter contiPh=selected.getRegimen().getContinious();
		setSingleMedicineToPhase(med, contiPh);
		for(PhaseUIAdapter pUi : selected.getRegimen().getAddPhases()){
			setSingleMedicineToPhase(med, pUi);
		}

	}
	/**
	 * Vary carefully set a medicine for phase
	 * Applicable only for Single Drug
	 * @param med
	 * @param phaseUI
	 */
	private void setSingleMedicineToPhase(MedicineUIAdapter med,
			PhaseUIAdapter phaseUI) {
		if(phaseUI != null && phaseUI.getMedications() != null && phaseUI.getMedications().size()>0){
			MedicationUIAdapter mediUi = phaseUI.getMedications().get(0);
			if (mediUi != null){
				MedicineUIAdapter mUi = mediUi.getMedicine();
				if(mUi != null){
					mUi.setMedicine(med.getMedicine());
				}
			}
		}
	}

	/**
	 * Add special listener to change selected
	 */
	private void addListeners() {
		getSelected().addPropertyChangeListener("regimen", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				repaintDetails();
			}
		});
	}


	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}


	public void setTabbedPane(JTabbedPane tabbedPane) {
		this.tabbedPane = tabbedPane;
	}


	/**
	 * Repaint regimen details
	 */
	protected void repaintDetails() {
		if(getTabbedPane() == null){
			setTabbedPane(new JTabbedPane(JTabbedPane.TOP));
		}
		getTabbedPane().removeAll();
		if(selected == null || selected.getRegimen()==null || selected.getRegimen().getIntensive()==null){
			return;
		}
		PhaseUIAdapter intPhase = selected.getRegimen().getIntensive();
		intPhase.setOrder(1);
		String intHeader = Messages.getString("Regimen.phase") + " 1";
		addTab(intPhase, intHeader);
		// rest phases should be painted only if at least one phase with at least one medication is existing
		if (selected.getRegimen().getPhasesQuantity()>1){
			PhaseUIAdapter contPhase = selected.getRegimen().getContinious();
			contPhase.setOrder(2);
			List<PhaseUIAdapter> phases = selected.getRegimen().getAddPhases();
			//20150428 if(!selected.getRegimen().isSingleDrug()){
			addTab(contPhase, Messages.getString("Regimen.phase")		+ " 2");
			for(PhaseUIAdapter addPhase : phases){
				addTab(addPhase, Messages.getString("Regimen.phase") + " " + (addPhase.getOrder()));
			}
		}
		getTabbedPane().setSelectedIndex(0);
	}

	/**
	 * Add new tab on the phases tabbed pane
	 * @param phase phase to add
	 * @param header label will be placed on the new tab
	 */
	private void addTab(PhaseUIAdapter phase, String header) {
		PhaseComponent pComp = new PhaseComponent(phase, selected.getRegimen().isSingleDrug(), this);
		pComp.deactivate();
		Component comp =getTabbedPane().add(header, pComp);
		getTabbedPane().setSelectedComponent(comp);
	}
	/**
	 * Adjust tables - determine headers, add renderer and so forth
	 */
	private void adjustTables() {
		regimensDicTable.getColumnModel().getColumn(0).setPreferredWidth(100);
		regimensDicTable.getColumnModel().getColumn(0).setHeaderValue(Messages.getString("Regimen.clmn.Regimen"));
		regimensDicTable.getColumnModel().getColumn(1).setHeaderValue(Messages.getString("Regimen.clmn.Composition"));
		regimensDicTable.getColumnModel().getColumn(1).setPreferredWidth(400);

		//	

	}

	

	public RegimenTmpStore getSelected() {
		return selected;
	}


	/**
	 * Set selected row in RegimenDicTable to i
	 * 
	 * @param index
	 */
	public void setSelectedRegimen(int index) {
		regimensDicTable.getSelectionModel().setSelectionInterval(index, index);
	}

	/**
	 * Set row in RegimenDicTable by regimen
	 * 
	 * @param regimen
	 *            selected regimen
	 */
	public void setRow(RegimenTmpStore regimen) {
		int index = regimensDic.getRegimens().indexOf(regimen.getRegimen());
		regimensDicTable.getSelectionModel().setSelectionInterval(index < 0 ? 0 : index, index < 0 ? 0 : index);
		regimensDicTable.scrollRectToVisible(regimensDicTable.getCellRect(index < 0 ? 0 : index, index < 0 ? 0 : index, true));
	}

	/**
	 * Set type filter for the regimens list
	 * @param type
	 */
	public void setType(RegimenTypesEnum type) {
		regimensDic.setFilter(type);

	}

	/**
	 * Clear selection in RegimenDicTable
	 */
	public void clearSelection() {
		regimensDicTable.getSelectionModel().clearSelection();
	}
	protected void initDataBindings() {
		BeanProperty<RegimensDicUIAdapter, List<RegimenUIAdapter>> regimensDicUIAdapterBeanProperty = BeanProperty.create("regimens");
		JTableBinding<RegimenUIAdapter, RegimensDicUIAdapter, JTable> jTableBinding = SwingBindings.createJTableBinding(UpdateStrategy.READ, regimensDic, regimensDicUIAdapterBeanProperty, regimensDicTable);
		//
		BeanProperty<RegimenUIAdapter, String> regimenUIAdapterBeanProperty = BeanProperty.create("name");
		jTableBinding.addColumnBinding(regimenUIAdapterBeanProperty).setColumnName("reg").setEditable(false);
		//
		BeanProperty<RegimenUIAdapter, String> regimenUIAdapterBeanProperty_1 = BeanProperty.create("consumption");
		jTableBinding.addColumnBinding(regimenUIAdapterBeanProperty_1).setColumnName("consumption").setEditable(false);
		//
		jTableBinding.setEditable(false);
		jTableBinding.bind();
		//
		BeanProperty<JTable, RegimenUIAdapter> jTableBeanProperty = BeanProperty.create("selectedElement");
		BeanProperty<RegimenTmpStore, RegimenUIAdapter> regimenTmpStoreBeanProperty = BeanProperty.create("regimen");
		AutoBinding<JTable, RegimenUIAdapter, RegimenTmpStore, RegimenUIAdapter> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, regimensDicTable, jTableBeanProperty, selected, regimenTmpStoreBeanProperty);
		autoBinding.bind();
		//
		BeanProperty<RegimensDicUIAdapter, RegimenTypesEnum> regimensDicUIAdapterBeanProperty_1 = BeanProperty.create("filter");
		BeanProperty<JComboBox, Object> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
		AutoBinding<RegimensDicUIAdapter, RegimenTypesEnum, JComboBox, Object> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, regimensDic, regimensDicUIAdapterBeanProperty_1, comboBox, jComboBoxBeanProperty);
		autoBinding_1.bind();
	}

}
