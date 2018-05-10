package org.msh.quantb.view.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.msh.quantb.model.gen.PhaseDurationEnum;
import org.msh.quantb.services.io.MedicineUIAdapter;
import org.msh.quantb.services.io.PhaseUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.EnumListRenderer;
import org.msh.quantb.view.PhaseTableModel;
import org.msh.quantb.view.SpinnerEditor;

/**
 * Input component for treatment phase
 * @author alexey
 *
 */
public class PhaseComponent extends JPanel implements ChangeListener {

	private static final long serialVersionUID = 4856467157537465878L;
	private JPanel cmdPanel;
	private JTable phaseDataTable;
	private PhaseUIAdapter phase;
	private PhaseTableModel model;
	private JButton delBtn;
	private JButton addBtn;
	private boolean singleMode;
	private JPanel dataPnl;
	private JComboBox measureBox;
	private JLabel durationLbl;
	private JSpinner spinner;
	/**
	 * Only valid constructor
	 * @param _phase phase to edit
	 * @param isSingle it is single medicine
	 * @param ISingleMedSelection 
	 */
	public PhaseComponent(PhaseUIAdapter _phase, boolean isSingle, final ISingleMedSelection smCorrector) {
		this.phase = _phase;
		this.singleMode = isSingle;
		setLayout(new BorderLayout(5, 5));

		cmdPanel = new JPanel();
		add(cmdPanel, BorderLayout.NORTH);
		cmdPanel.setLayout(new BorderLayout(5, 5));

		JPanel btnPanel = new JPanel();
		cmdPanel.add(btnPanel, BorderLayout.EAST);
		btnPanel.setLayout(new BorderLayout(5, 5));

		if(!this.singleMode){
			delBtn = new JButton(Messages.getString("Regimen.btn.delMedicine"));
			delBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (Presenter.askDelete()){
						model.deleteMedication(phaseDataTable.getSelectedRow());
						model.fireTableDataChanged();
					}
				}
			});
			btnPanel.add(delBtn, BorderLayout.EAST);
		}


		String addBtnLbl = Messages.getString("Regimen.btn.addMedicine");
		if (singleMode){
			addBtnLbl = Messages.getString("Regimen.btn.setMedicine");
		}
		addBtn = new JButton(addBtnLbl);
		addBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (phase != null){
					Presenter.selectMedicineForAddPhase(phase, phaseDataTable, singleMode);
					if(singleMode){
						if(phase.getMedicines().size()>0){
							MedicineUIAdapter mUi = phase.getMedicines().get(0);
							if(mUi != null){
								smCorrector.adjustPhases(mUi);
							}
						}
					}
				}
			}
		});
		btnPanel.add(addBtn, BorderLayout.WEST);

		dataPnl = new JPanel();
		dataPnl.setAlignmentX(Component.RIGHT_ALIGNMENT);
		cmdPanel.add(dataPnl, BorderLayout.WEST);
		dataPnl.setLayout(new BorderLayout(5, 0));

		durationLbl = new JLabel(Messages.getString("Regimen.phase.duration"));
		dataPnl.add(durationLbl, BorderLayout.WEST);

		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(0, 0, 48, 1));
		dataPnl.add(spinner, BorderLayout.CENTER);

		measureBox = new JComboBox();
		measureBox.setModel(new DefaultComboBoxModel(PhaseDurationEnum.values()));
		EnumListRenderer formRenderer = new EnumListRenderer("Regimen.phase.durtypes");
		measureBox.setRenderer(formRenderer);
		dataPnl.add(measureBox, BorderLayout.EAST);

		phaseDataTable = new JTable();
		phaseDataTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		phaseDataTable.getTableHeader().setResizingAllowed(false);
		phaseDataTable.getTableHeader().setReorderingAllowed(false);
		phaseDataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		phaseDataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		JScrollPane scrollPane = new JScrollPane(phaseDataTable);
		scrollPane.setPreferredSize(new Dimension(866, 140));
		scrollPane.setMinimumSize(new Dimension(866, 140));
		add(scrollPane, BorderLayout.CENTER);

		bindToData();
		drawTableColumns();
		showHideDelBtn();
		setListeners();
		initDataBindings();
	}



	public JPanel getCmdPanel() {
		return cmdPanel;
	}



	public JTable getPhaseDataTable() {
		return phaseDataTable;
	}



	public JButton getDelBtn() {
		return delBtn;
	}



	public JButton getAddBtn() {
		return addBtn;
	}



	public PhaseTableModel getModel() {
		return model;
	}



	/**
	 * Set the necessary additional listeners
	 */
	private void setListeners() {
		model.addTableModelListener(new TableModelListener(){
			@Override
			public void tableChanged(TableModelEvent arg0) {
				showHideDelBtn();
			}
		});
		phaseDataTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				showHideDelBtn();
			}
		});
	}

	/**
	 * Draw columns for the data table
	 */
	private void drawTableColumns() {

		phaseDataTable.getColumnModel().getColumn(0).setPreferredWidth(390);
		phaseDataTable.getColumnModel().getColumn(1).setPreferredWidth(60);
		phaseDataTable.getColumnModel().getColumn(2).setPreferredWidth(60);
		phaseDataTable.getColumnModel().getColumn(1).setMaxWidth(60);
		phaseDataTable.getColumnModel().getColumn(2).setMaxWidth(60);
		phaseDataTable.getColumnModel().getColumn(1).setCellEditor(new SpinnerEditor(0, 10, 1, this));
		phaseDataTable.getColumnModel().getColumn(2).setCellEditor(new SpinnerEditor(0, 7, 1, this));
		phaseDataTable.getColumnModel().getColumn(0).setHeaderValue(Messages.getString("Regimen.clmn.medicinesName"));
		phaseDataTable.getColumnModel().getColumn(1).setHeaderValue(Messages.getString("Regimen.clmn.dosage"));
		phaseDataTable.getColumnModel().getColumn(2).setHeaderValue(Messages.getString("Regimen.clmn.DaysPerWeek"));
		phaseDataTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		phaseDataTable.repaint();

	}

	/**
	 * bind the data table to the phase data
	 */
	private void bindToData() {
		model = new PhaseTableModel(phase);
		phaseDataTable.setModel(model);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub

	}
	private void showHideDelBtn() {
		if (this.singleMode){
			return;
		}
		if (model.getRowCount() == 0){
			delBtn.setEnabled(false);
		}else{
			if (phaseDataTable.getSelectedRow() > -1){
				delBtn.setEnabled(true);
			}else{
				delBtn.setEnabled(false);
			}
		}
	}

	/**
	 * Make this component read only
	 */
	public void deactivate() {
		getMeasureBox().setEnabled(false);
		getSpinner().setEnabled(false);
		if(getAddBtn() != null){
			getAddBtn().setVisible(false);
		}
		if(getDelBtn() != null){
			getDelBtn().setVisible(false);
		}
		getModel().deactivate();
	}


	public JComboBox getMeasureBox() {
		return measureBox;
	}



	public JSpinner getSpinner() {
		return spinner;
	}



	/**
	 * @return the phase
	 */
	public PhaseUIAdapter getPhase() {
		return phase;
	}
	protected void initDataBindings() {
		BeanProperty<PhaseUIAdapter, Integer> phaseUIAdapterBeanProperty = BeanProperty.create("durationValue");
		BeanProperty<JSpinner, Object> jSpinnerBeanProperty = BeanProperty.create("value");
		AutoBinding<PhaseUIAdapter, Integer, JSpinner, Object> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, phase, phaseUIAdapterBeanProperty, spinner, jSpinnerBeanProperty, "durationBnd");
		autoBinding.bind();
		//
		BeanProperty<PhaseUIAdapter, PhaseDurationEnum> phaseUIAdapterBeanProperty_1 = BeanProperty.create("measure");
		BeanProperty<JComboBox, Object> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
		AutoBinding<PhaseUIAdapter, PhaseDurationEnum, JComboBox, Object> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, phase, phaseUIAdapterBeanProperty_1, measureBox, jComboBoxBeanProperty);
		autoBinding_1.bind();
	}

}
