package org.msh.quantb.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.PropertyStateEvent;
import org.jdesktop.beansbinding.PropertyStateListener;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.msh.quantb.model.gen.MedicineRegimen;
import org.msh.quantb.model.gen.RegimenTypesEnum;
import org.msh.quantb.services.io.MedicationUIAdapter;
import org.msh.quantb.services.io.MedicineTmpStore;
import org.msh.quantb.services.io.MedicineUIAdapter;
import org.msh.quantb.services.io.RegimenTmpStore;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.ISelectableMedicine;
import org.msh.quantb.view.SpinnerEditor;

public class RegimensDlgOld extends JDialog implements ISelectableMedicine, PropertyStateListener, PropertyChangeListener, ChangeListener {
	private static final long serialVersionUID = -5079877437055637643L;
	private RegimenTmpStore selected;
	private JTextField txtRegName;
	private JTextField txtRegCons;
	private JTable phaseIntDataTable;
	private MedicineTmpStore selectedIntensiveMedicine = new MedicineTmpStore(null);
	private MedicineTmpStore selectedContiMedicine = new MedicineTmpStore(null);
	private boolean intensive = true;
	private JTable phaseContDataTable;
	private JButton editIntBtn;
	private JButton editContBtn;
	private JButton delIntBtn;
	private JButton delContBtn;
	protected boolean wasEdited;
	private JComboBox typeBox;
	private JPanel phaseContiCmdPanel;
	private JPanel phaseContiDataPanel;
	private JLabel phaseIntensLbl;
	private JPanel phaseIntDataPanel;

	/**
	 * Create the dialog.
	 * 
	 * @param owner
	 * @param isFromRegimen
	 *            does it call from regimenListDlg
	 * @param isEdited
	 *            does RegimenTmpStore edited
	 */
	public RegimensDlgOld(final RegimenTmpStore _selected, Dialog owner, final boolean isFromRegimen, final boolean isEdited) {
		super(owner);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Presenter.closeRegimenDlg(isFromRegimen, wasEdited);
			}
		});
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setSize(new Dimension(893, 534));
		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		int wdwLeft = screenSize.width / 2 - getWidth() / 2;
		int wdwTop = screenSize.height / 2 - getHeight() / 2;
		setLocation(wdwLeft, wdwTop);
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		this.selected = _selected;
		setTitle(Messages.getString(isEdited?"Regimen.title.edit":"Regimen.title.new")); //$NON-NLS-1$
		getContentPane().setLayout(null);
		{

			txtRegName = new JTextField();
			txtRegName.setText("");
			txtRegName.setBounds(140, 23, 547, 20);
			getContentPane().add(txtRegName);
			txtRegName.setColumns(10);

			txtRegCons = new JTextField();
			txtRegCons.setText("");
			txtRegCons.setBounds(140, 54, 547, 20);
			getContentPane().add(txtRegCons);
			//txtRegCons.setColumns(10);
		}

		JPanel phaseICmdPanel = new JPanel();
		phaseICmdPanel.setBounds(11, 94, 866, 24);
		getContentPane().add(phaseICmdPanel);
		phaseICmdPanel.setLayout(null);

		phaseIntensLbl = new JLabel(Messages.getString("Regimen.phase.intensive")); //$NON-NLS-1$
		phaseIntensLbl.setBounds(10, 3, 130, 14);
		phaseIntensLbl.setHorizontalAlignment(SwingConstants.LEFT);
		phaseICmdPanel.add(phaseIntensLbl);

		editIntBtn = new JButton(Messages.getString("Regimen.btn.addMedicine"));
		editIntBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				intensive = true;
				List<MedicationUIAdapter> oldValue = selected.getIntensiveMedications();
				Presenter.addMedicineToPhase(selected, intensive);
				selected.firePropertyChange("intensiveMedications", oldValue, selected.getIntensiveMedications());
				selected.getRegimen().setConsumption(selected.getRegimen().calcComposition());
			}
		});
		editIntBtn.setBounds(680, 0, 176, 23);
		phaseICmdPanel.add(editIntBtn);

		delIntBtn = new JButton(Messages.getString("Regimen.btn.delMedicine"));
		delIntBtn.setEnabled(false);
		delIntBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (phaseIntDataTable.getSelectedRow() != -1) {
					List<MedicationUIAdapter> oldValue = selected.getIntensiveMedications();
					Presenter.deleteMedication(selected, selectedIntensiveMedicine, true);
					selected.firePropertyChange("intensiveMedications", oldValue, selected.getIntensiveMedications());
					selected.getRegimen().setConsumption(selected.getRegimen().calcComposition());
				}
			}
		});
		delIntBtn.setBounds(510, 1, 160, 23);
		phaseICmdPanel.add(delIntBtn);

		phaseIntDataPanel = new JPanel();
		phaseIntDataPanel.setBounds(11, 128, 866, 140);
		getContentPane().add(phaseIntDataPanel);
		phaseIntDataPanel.setLayout(new BorderLayout(0, 0));

		phaseIntDataTable = new JTable();
		phaseIntDataTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		phaseIntDataTable.getTableHeader().setResizingAllowed(false);
		phaseIntDataTable.getTableHeader().setReorderingAllowed(false);
		phaseIntDataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		phaseIntDataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		JScrollPane phaseIntScrollPane = new JScrollPane(phaseIntDataTable);
		phaseIntScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		phaseIntDataPanel.add(phaseIntScrollPane);

		phaseContiCmdPanel = new JPanel();
		phaseContiCmdPanel.setBounds(11, 279, 866, 24);
		getContentPane().add(phaseContiCmdPanel);
		phaseContiCmdPanel.setLayout(null);

		JLabel phaseContiLabel = new JLabel(Messages.getString("Regimen.phase.continious")); //$NON-NLS-1$
		phaseContiLabel.setBounds(10, 0, 129, 19);
		phaseContiCmdPanel.add(phaseContiLabel);

		editContBtn = new JButton(Messages.getString("Regimen.btn.addMedicine"));
		editContBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				intensive = false;
				List<MedicationUIAdapter> oldValue = selected.getContiMedications();
				Presenter.addMedicineToPhase(selected, intensive);
				selected.firePropertyChange("contiMedications", oldValue, selected.getContiMedications());
				selected.getRegimen().setConsumption(selected.getRegimen().calcComposition());
			}
		});
		editContBtn.setBounds(676, 0, 180, 23);
		phaseContiCmdPanel.add(editContBtn);

		delContBtn = new JButton(Messages.getString("Regimen.btn.delMedicine")); //$NON-NLS-1$
		delContBtn.setEnabled(false);
		delContBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (phaseContDataTable.getSelectedRow() != -1) {
					List<MedicationUIAdapter> oldValue = selected.getContiMedications();
					Presenter.deleteMedication(selected, selectedContiMedicine, false);
					selected.firePropertyChange("contiMedications", oldValue, selected.getContiMedications());
					selected.getRegimen().setConsumption(selected.getRegimen().calcComposition());
				}
			}
		});
		delContBtn.setBounds(510, 0, 151, 23);
		phaseContiCmdPanel.add(delContBtn);

		phaseContiDataPanel = new JPanel();
		phaseContiDataPanel.setBounds(11, 316, 866, 140);
		getContentPane().add(phaseContiDataPanel);
		phaseContiDataPanel.setLayout(new BorderLayout(0, 0));

		phaseContDataTable = new JTable();
		phaseContDataTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		phaseContDataTable.getTableHeader().setResizingAllowed(false);
		phaseContDataTable.getTableHeader().setReorderingAllowed(false);
		phaseContDataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane phaseContScrollPane = new JScrollPane(phaseContDataTable);
		phaseContScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		phaseContiDataPanel.add(phaseContScrollPane);
		{
			JPanel panelBottom = new JPanel();
			panelBottom.setBounds(11, 467, 866, 33);
			getContentPane().add(panelBottom);
			{
				JButton btnClose = new JButton(Messages.getString("DlgConfirm.cancelButton")); //$NON-NLS-1$
				btnClose.setBounds(760, 5, 100, 23);
				btnClose.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Presenter.closeRegimenDlg(isFromRegimen, wasEdited);
					}
				});
				panelBottom.setLayout(null);
				btnClose.setActionCommand("Cancel");
				panelBottom.add(btnClose);
			}
			{
				JButton button = new JButton(Messages.getString("DlgEditMedicine.btnSave.text"));
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String currentComp = selected.getRegimen().getConsumption();
						String calcComp = selected.getRegimen().calcComposition();
						if (!currentComp.equalsIgnoreCase(calcComp)){
							String mess = Presenter.getMessage("Warning.regimen.composition")+" "+
									currentComp + " "+
									Presenter.getMessage("Warning.regimen.composition.entered") + " "+
									calcComp + " " +
									Presenter.getMessage("Warning.regimen.composition.question");
							if (!Presenter.showWarningString(mess)){
								selected.getRegimen().setConsumption(selected.getRegimen().calcComposition());
							}
						}
						int type = -1;
						if ((type = isAllowToSave()) == -1) {
							if(Presenter.checkRegimen(_selected, isEdited)){
								if (Presenter.checkRegimenName(_selected, isEdited)){
									if (Presenter.checkRegimenMedications(_selected, isEdited)){
										Presenter.saveRegimenDic(_selected, isEdited);
										Presenter.adjustRegimen(_selected);
										dispose();
									}
								}
							}else{
								Presenter.showError(
										Messages.getString("Error.Validation.RegimenSave.Existed"));
							}
						} else {
							if (type == 0) {
								Presenter.showError(Messages.getString("Error.Validation.RegimenSave.FieldBlanks"));
							} else if (type == 1) {
								Presenter.showError(Messages.getString("Error.editRegimen.intensive.invalidValue"));
							} else if (type == 2) {
								Presenter.showError(Messages.getString("Error.editRegimen.continuous.invalidValue"));
							} else if (type == 3) {
								Presenter.showError(Messages.getString("Error.Validation.RegimenSave.FieldBlanks"));
							}else if (type == 4) {
								Presenter.showError(Messages.getString("Error.Validation.RegimenSave.intensiveEmpty"));
							}else if (type == 5) {
								Presenter.showError(Messages.getString("Error.Validation.RegimenSave.continuousEmpty"));
							}else if(type == 6){
								Presenter.showError(Messages.getString("Error.Validation.RegimenSave.onlyone"));
							}
						}
					}
				});
				button.setActionCommand("OK");
				button.setBounds(10, 5, 100, 23);
				panelBottom.add(button);
			}
		}

		JLabel lblNewLabel = new JLabel(Messages.getString("Regimen.name")); //$NON-NLS-1$
		lblNewLabel.setBounds(26, 26, 104, 14);
		getContentPane().add(lblNewLabel);

		JLabel compositionLbl = new JLabel(Messages.getString("Regimen.composition"));
		final JDialog self = this;
		compositionLbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				selected.getRegimen().setConsumption(selected.getRegimen().calcComposition());
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				self.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				self.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});
		compositionLbl.setForeground(Color.BLUE);
		compositionLbl.setBounds(26, 57, 104, 14);
		
		getContentPane().add(compositionLbl);

		phaseIntDataTable.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (selectedIntensiveMedicine.getMedicine().getAbbrevName().isEmpty() || selectedIntensiveMedicine.getMedicine().getName().isEmpty()) {
					((SpinnerEditor) phaseIntDataTable.getColumnModel().getColumn(1).getCellEditor()).setEditable(false);
					((SpinnerEditor) phaseIntDataTable.getColumnModel().getColumn(2).getCellEditor()).setEditable(false);
					((SpinnerEditor) phaseIntDataTable.getColumnModel().getColumn(3).getCellEditor()).setEditable(false);
				} else {
					((SpinnerEditor) phaseIntDataTable.getColumnModel().getColumn(1).getCellEditor()).setEditable(true);
					((SpinnerEditor) phaseIntDataTable.getColumnModel().getColumn(2).getCellEditor()).setEditable(true);
					((SpinnerEditor) phaseIntDataTable.getColumnModel().getColumn(3).getCellEditor()).setEditable(true);
				}
				editMedicine(e);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				phaseIntDataTable.setCursor(new Cursor(Cursor.HAND_CURSOR));

			}

			@Override
			public void mouseExited(MouseEvent e) {
				phaseIntDataTable.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// nothing to do

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// nothing to do

			}

		});
		phaseContDataTable.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (selectedContiMedicine.getMedicine().getAbbrevName().isEmpty() || selectedContiMedicine.getMedicine().getName().isEmpty()) {
					((SpinnerEditor) phaseContDataTable.getColumnModel().getColumn(1).getCellEditor()).setEditable(false);
					((SpinnerEditor) phaseContDataTable.getColumnModel().getColumn(2).getCellEditor()).setEditable(false);
					((SpinnerEditor) phaseContDataTable.getColumnModel().getColumn(3).getCellEditor()).setEditable(false);
				} else {
					((SpinnerEditor) phaseContDataTable.getColumnModel().getColumn(1).getCellEditor()).setEditable(true);
					((SpinnerEditor) phaseContDataTable.getColumnModel().getColumn(2).getCellEditor()).setEditable(true);
					((SpinnerEditor) phaseContDataTable.getColumnModel().getColumn(3).getCellEditor()).setEditable(true);
				}
				editMedicine(e);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				phaseContDataTable.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				phaseContDataTable.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// nothing to do

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// nothing to do

			}

		});

/*		typeBox = new JComboBox();
		typeBox.setModel(new DefaultComboBoxModel(RegimenTypesEnum.values()));
		typeBox.setBounds(697, 11, 180, 20);
		typeBox.setRenderer(new EnumListRenderer("Regimen.types"));
		getContentPane().add(typeBox);*/
		
		JLabel typeLabel = new JLabel(Presenter.getMessage("Regimen.types." + selected.getRegimen().getType().toString()));
		typeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		typeLabel.setBorder(new LineBorder(new Color(0, 0, 0)));
		typeLabel.setBounds(697, 11, 180, 20);
		getContentPane().add(typeLabel);

		initDataBindings();
		refineDataBindings();
		setAdditionalListeners();
		adjustPhaseTable(phaseIntDataTable);
		adjustPhaseTable(phaseContDataTable);
		singleOrMultiDrugSwitch(selected.getRegimen().getType());

	}
	
	/**
	 * Adjust screen look in accordance with regimen type
	 * @param type
	 */
	private void singleOrMultiDrugSwitch(RegimenTypesEnum type) {
		if (type == RegimenTypesEnum.MULTI_DRUG){
			phaseContiCmdPanel.setVisible(true);
			phaseContiDataPanel.setVisible(true);
			phaseIntensLbl.setVisible(true);
			phaseIntDataPanel.setBounds(11, 128, 866, 140);
			phaseIntDataTable.getColumnModel().getColumn(0).setHeaderValue(Messages.getString("Regimen.clmn.medicinesName"));
			editIntBtn.setText(Messages.getString("Regimen.btn.addMedicine"));
			delIntBtn.setVisible(true);
		}
		if (type == RegimenTypesEnum.SINGLE_DRUG){
			phaseContiCmdPanel.setVisible(false);
			phaseContiDataPanel.setVisible(false);
			phaseIntensLbl.setVisible(false);
			phaseIntDataPanel.setBounds(11, 128, 866, 280);
			phaseIntDataTable.getColumnModel().getColumn(0).setHeaderValue(Messages.getString("Regimen.clmn.medicinesName"));
			editIntBtn.setText(Messages.getString("Regimen.btn.setMedicine"));
			delIntBtn.setVisible(false);

		}
	}

	/**
	 * Set some additional listeners
	 */
	private void setAdditionalListeners() {
		
		//TODO remove	
		this.selected.getRegimen().addPropertyChangeListener("type", new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				RegimenTypesEnum type = (RegimenTypesEnum) evt.getNewValue();
				singleOrMultiDrugSwitch(type);
			}
		});

	}
	/**
	 * Adjust columns in phase table. Damned SWING!!!
	 * @param phaseTable TODO
	 */
	private void adjustPhaseTable(JTable phaseTable) {
		phaseTable.getColumnModel().getColumn(0).setPreferredWidth(390);
		phaseTable.getColumnModel().getColumn(1).setPreferredWidth(60);
		phaseTable.getColumnModel().getColumn(2).setPreferredWidth(70);
		phaseTable.getColumnModel().getColumn(3).setPreferredWidth(70);
		phaseTable.getColumnModel().getColumn(1).setMaxWidth(60);
		phaseTable.getColumnModel().getColumn(2).setMaxWidth(70);
		phaseTable.getColumnModel().getColumn(3).setMaxWidth(70);
		phaseTable.getColumnModel().getColumn(1).setCellEditor(new SpinnerEditor(0, 10, 1, this));		
		phaseTable.getColumnModel().getColumn(2).setCellEditor(new SpinnerEditor(0, 7, 1, this));
		phaseTable.getColumnModel().getColumn(3).setCellEditor(new SpinnerEditor(0, 36, 1, this));
		phaseTable.getColumnModel().getColumn(0).setHeaderValue(Messages.getString("Regimen.clmn.medicinesName"));
		phaseTable.getColumnModel().getColumn(1).setHeaderValue(Messages.getString("Regimen.clmn.dosage"));
		phaseTable.getColumnModel().getColumn(2).setHeaderValue(Messages.getString("Regimen.clmn.DaysPerWeek"));
		phaseTable.getColumnModel().getColumn(3).setHeaderValue(Messages.getString("Regimen.clmn.Duration"));
		phaseTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		phaseTable.repaint();
	}

	/**
	 * Validate current regimen before saving.
	 * this variant allows saving without continuous phase
	 * @return true - is valid, false - another.
	 */
	protected int isAllowToSave(){
		//at least one one medicine must exist in intensive phase
		if ((selected.getRegimen().getIntensive().getPhase().getMedications().size() == 1 && 
				selected.getRegimen().getIntensive().getPhase().getMedications().get(0).getMedicine().getAbbrevName().isEmpty())) {
			return 3;
		}		
		//zero in any parameters not allowed
		for (MedicineRegimen mr : selected.getRegimen().getIntensive().getPhase().getMedications()) {
			if (mr.getDaysPerWeek() == 0 || mr.getDosage() == 0 || mr.getDuration() == 0) {
				return 1;
			}
		}
		
		for (MedicineRegimen mr : selected.getRegimen().getContinious().getPhase().getMedications()) {
			if (mr.getDaysPerWeek() == 0 || mr.getDosage() == 0 || mr.getDuration() == 0) {
				return 2;
			}
		}

		if (selected.getRegimen().getName().isEmpty()) return 0;
		if (selected.getRegimen().getIntensive().getPhase().getMedications().isEmpty()) return 4;

		if (selected.getRegimen().getType() == RegimenTypesEnum.SINGLE_DRUG && 
				selected.getRegimen().getRegimen().getIntensive().getMedications().size() !=1){
			return 6;
		}

		return -1;
	}


	/**
	 * Add some data binding manually<br>
	 * These bindings generally need for fine screen control
	 */
	private void refineDataBindings() {
		// user pick up intensive phase, so disable continious
		BeanProperty<JTable, MedicationUIAdapter> phaseSelected = BeanProperty.create("selectedElement");
		phaseSelected.addPropertyStateListener(phaseIntDataTable, new PropertyStateListener() {
			@Override
			public void propertyStateChanged(PropertyStateEvent pse) {
				//int i = phaseIntDataTable.getSelectedRow(); // because of strange behavior of JTable selection
				//setIntensiveEditable(true);
				RegimensDlgOld.this.intensive = true;
				//phaseIntDataTable.getSelectionModel().setSelectionInterval(i, i); // because of strange behavior of JTable selection
				delIntBtn.setEnabled(phaseIntDataTable.getSelectedRow() != -1);
			}
		});
		// vice versa of above
		phaseSelected.addPropertyStateListener(phaseContDataTable, new PropertyStateListener() {
			@Override
			public void propertyStateChanged(PropertyStateEvent pse) {
				//int i = phaseContDataTable.getSelectedRow(); // because of strange behavior of JTable selection
				//setIntensiveEditable(false);
				RegimensDlgOld.this.intensive = false;
				//phaseContDataTable.getSelectionModel().setSelectionInterval(i, i); // because of strange behavior of JTable selection
				delContBtn.setEnabled(phaseContDataTable.getSelectedRow()!=-1);
			}
		});		
		BeanProperty<JTextField, String> jTextFieldBeanProperty = BeanProperty.create("text");
		jTextFieldBeanProperty.addPropertyStateListener(txtRegName, this);
		//TODO DELETE jTextFieldBeanProperty.addPropertyStateListener(txtRegCons, this);
		selected.getRegimen().addPropertyChangeListener("intensive", this);
		selected.getRegimen().addPropertyChangeListener("continious", this);
	}

	/**
	 * Set intensive phase as editable or vice versa
	 * 
	 * @param enable
	 *            true - enable intensive, disable continuous, false disable intensive, enable continuous
	 */
	protected void setIntensiveEditable(boolean enable) {
		// set current active phase
		this.intensive = enable;
		if (enable) {
			//disable all continious table buttons			
			this.delContBtn.setEnabled(false);
			//enable all intensive btns			
			this.delIntBtn.setEnabled(true);
			//clear selection in conti phase table
			this.phaseContDataTable.getSelectionModel().clearSelection();
		} else {
			// disable all intensive table buttons			
			this.delIntBtn.setEnabled(false);
			// enable all continuous btns			
			this.delContBtn.setEnabled(true);
			// clear selection in intensive phase table
			this.phaseIntDataTable.getSelectionModel().clearSelection();
		}

	}

	@Override
	/**
	 * We need return shallow copy of the object, because has risk to lost original selection
	 */
	public MedicineUIAdapter getSelectedMedicine() {
		if (intensive) {
			return new MedicineUIAdapter(this.selectedIntensiveMedicine.getMedicine().getMedicine());
		} else {
			return new MedicineUIAdapter(this.selectedContiMedicine.getMedicine().getMedicine());
		}
	}
	/**
	 * Only for single drug!
	 */
	@Override
	public void setSelectedMedicine(MedicineUIAdapter _selected) {
		wasEdited = true;
		if (intensive) {
			selected.getRegimen().getRegimen().getIntensive().getMedications().clear();
			MedicineRegimen med = Presenter.createEmptyMedication();
			med.setMedicine(_selected.getMedicine());
			selected.getRegimen().getIntensive().getPhase().getMedications().add(med);
			// new UI phase definition must replace the old one
			selected.getRegimen().setIntensive(selected.getRegimen().getIntensive());
		}

	}

	/**
	 * @return the selected
	 */
	public RegimenTmpStore getSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            the selected to set
	 */
	public void setSelected(RegimenTmpStore selected) {
		this.selected = selected;
	}

	/**
	 * Edit a medicine for the table given
	 */
	private void editMedicine(MouseEvent e) {
		List<MedicineUIAdapter> notInclude = null;
		if (e.getClickCount() > 1) {
			JTable table = null;
			MedicineTmpStore store = null;
			if (this.intensive) {
				table = phaseIntDataTable;
				store = selectedIntensiveMedicine;
				notInclude = this.selected.getRegimen().getIntensive().getMedicines();
			} else {
				table = phaseContDataTable;
				store = selectedContiMedicine;
				notInclude = this.selected.getRegimen().getContinious().getMedicines();
			}
			if (table.getSelectedColumn() == 0) {
				notInclude.add(store.getMedicine());
				Presenter.selectMedicine(notInclude, true);
			}
		}
	}

	/**
	 * select in the current active phase table medication by index given
	 */
	public void selectMedicationByIndex(int index) {
		if (intensive)
			phaseIntDataTable.getSelectionModel().setSelectionInterval(index, index);
		else
			phaseContDataTable.getSelectionModel().setSelectionInterval(index, index);
	}

	/**
	 * Selected phase is intensive or continious.
	 * 
	 * @return true - intensive, false - continious.
	 */
	public boolean isIntensive() {
		return intensive;
	}

	@Override
	public void propertyStateChanged(PropertyStateEvent arg0) {
		if (arg0.getOldValue() != null && arg0.getValueChanged()) {
			wasEdited = true;
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getOldValue() != null && evt.getNewValue() != null) {
			wasEdited = true;
		}

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		wasEdited = true;
	}
	protected void initDataBindings() {
		BeanProperty<RegimenTmpStore, String> regimenTmpStoreBeanProperty = BeanProperty.create("regimen.name");
		BeanProperty<JTextField, String> jTextFieldBeanProperty = BeanProperty.create("text");
		AutoBinding<RegimenTmpStore, String, JTextField, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, selected, regimenTmpStoreBeanProperty, txtRegName, jTextFieldBeanProperty);
		autoBinding.bind();
		//
		BeanProperty<RegimenTmpStore, List<MedicationUIAdapter>> regimenTmpStoreBeanProperty_2 = BeanProperty.create("intensiveMedications");
		JTableBinding<MedicationUIAdapter, RegimenTmpStore, JTable> jTableBinding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE, selected, regimenTmpStoreBeanProperty_2, phaseIntDataTable);
		//
		BeanProperty<MedicationUIAdapter, String> medicationUIAdapterBeanProperty = BeanProperty.create("medicine.nameForDisplay");
		jTableBinding.addColumnBinding(medicationUIAdapterBeanProperty).setColumnName("name").setEditable(false);
		//
		BeanProperty<MedicationUIAdapter, Integer> medicationUIAdapterBeanProperty_1 = BeanProperty.create("dosage");
		jTableBinding.addColumnBinding(medicationUIAdapterBeanProperty_1).setColumnName("dosage");
		//
		BeanProperty<MedicationUIAdapter, Integer> medicationUIAdapterBeanProperty_3 = BeanProperty.create("daysPerWeek");
		jTableBinding.addColumnBinding(medicationUIAdapterBeanProperty_3).setColumnName("dow");
		//
		BeanProperty<MedicationUIAdapter, Integer> medicationUIAdapterBeanProperty_2 = BeanProperty.create("duration");
		jTableBinding.addColumnBinding(medicationUIAdapterBeanProperty_2).setColumnName("duration");
		//
		jTableBinding.bind();
		//
		BeanProperty<RegimenTmpStore, List<MedicationUIAdapter>> regimenTmpStoreBeanProperty_3 = BeanProperty.create("contiMedications");
		JTableBinding<MedicationUIAdapter, RegimenTmpStore, JTable> jTableBinding_1 = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE, selected, regimenTmpStoreBeanProperty_3, phaseContDataTable);
		//
		BeanProperty<MedicationUIAdapter, String> medicationUIAdapterBeanProperty_4 = BeanProperty.create("medicine.nameForDisplay");
		jTableBinding_1.addColumnBinding(medicationUIAdapterBeanProperty_4).setColumnName("name").setEditable(false);
		//
		BeanProperty<MedicationUIAdapter, Integer> medicationUIAdapterBeanProperty_5 = BeanProperty.create("dosage");
		jTableBinding_1.addColumnBinding(medicationUIAdapterBeanProperty_5).setColumnName("dosage");
		//
		BeanProperty<MedicationUIAdapter, Integer> medicationUIAdapterBeanProperty_7 = BeanProperty.create("daysPerWeek");
		jTableBinding_1.addColumnBinding(medicationUIAdapterBeanProperty_7).setColumnName("dow");
		//
		BeanProperty<MedicationUIAdapter, Integer> medicationUIAdapterBeanProperty_6 = BeanProperty.create("duration");
		jTableBinding_1.addColumnBinding(medicationUIAdapterBeanProperty_6).setColumnName("duration");
		//
		jTableBinding_1.bind();
		//
		BeanProperty<JTable, MedicineUIAdapter> jTableBeanProperty = BeanProperty.create("selectedElement.medicine");
		BeanProperty<MedicineTmpStore, MedicineUIAdapter> medicineTmpStoreBeanProperty = BeanProperty.create("medicine");
		AutoBinding<JTable, MedicineUIAdapter, MedicineTmpStore, MedicineUIAdapter> autoBinding_4 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, phaseIntDataTable, jTableBeanProperty, selectedIntensiveMedicine, medicineTmpStoreBeanProperty);
		autoBinding_4.bind();
		//
		AutoBinding<JTable, MedicineUIAdapter, MedicineTmpStore, MedicineUIAdapter> autoBinding_5 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, phaseContDataTable, jTableBeanProperty, selectedContiMedicine, medicineTmpStoreBeanProperty);
		autoBinding_5.bind();
		//
		BeanProperty<RegimenTmpStore, RegimenTypesEnum> regimenTmpStoreBeanProperty_4 = BeanProperty.create("regimen.type");
		BeanProperty<JComboBox, Object> jComboBoxBeanProperty = BeanProperty.create("selectedItem");
		AutoBinding<RegimenTmpStore, RegimenTypesEnum, JComboBox, Object> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, selected, regimenTmpStoreBeanProperty_4, typeBox, jComboBoxBeanProperty);
		autoBinding_2.bind();
		//
		BeanProperty<RegimenTmpStore, String> regimenTmpStoreBeanProperty_1 = BeanProperty.create("regimen.consumption");
		BeanProperty<JTextField, String> jLabelBeanProperty = BeanProperty.create("text");
		AutoBinding<RegimenTmpStore, String, JTextField, String> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, selected, regimenTmpStoreBeanProperty_1, txtRegCons, jLabelBeanProperty);
		autoBinding_1.bind();
	}
}
