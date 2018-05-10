package org.msh.quantb.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.msh.quantb.model.gen.RegimenTypesEnum;
import org.msh.quantb.services.io.MedicationUIAdapter;
import org.msh.quantb.services.io.MedicineUIAdapter;
import org.msh.quantb.services.io.PhaseUIAdapter;
import org.msh.quantb.services.io.RegimenTmpStore;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;

public class RegimensDlg extends JDialog implements ISingleMedSelection  {
	private static final long serialVersionUID = -5079877437055637643L;
	private RegimenTmpStore selected;
	protected boolean wasEdited;
	private JTextField nameFld;
	private JTextField compFld;
	private JLabel typeLbl;
	private JPanel phasesDataPnl;
	private JTabbedPane mainTabPane;
	private boolean isFromRegimen;
	private boolean isChanged = false; // was this regimen is edited?
	private JButton copyPhaseBtn;
	private JButton addPhaseBtn;
	private JButton cancelBtn;
	private JButton saveBtn;
	private JPanel phasesCntrlPnl;
	private boolean displayOnly = false;
	private JLabel compLbl;
	private JPanel generalInfoPnl;

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
	 * Create the dialog for edit - new
	 * @wbp.parser.constructor 
	 * @param owner
	 * @param isFromRegimen
	 *            does it call from regimenListDlg
	 * @param isEdited
	 *            does RegimenTmpStore edited
	 *            TODO remove extra parameters
	 */
	public RegimensDlg(final RegimenTmpStore _selected, Dialog owner, final boolean _isFromRegimen, final boolean isEdited) {
		super(owner);
		setDisplayOnly(false);
		addCloseListener();
		if (isEdited){
			setTitle(Messages.getString("Regimen.oldTitle"));
		}else{
			setTitle(Messages.getString("Regimen.newTitle"));
		}
		this.wasEdited = isEdited;
		setChanged(false);
		this.isFromRegimen = _isFromRegimen;
		this.selected = _selected;
		paintDialog();

	}

	public RegimensDlg(RegimenTmpStore _selected){
		super(Presenter.getView().getMainWindow());
		this.selected = _selected;
		setTitle(Messages.getString("Regimen.title.display"));
		paintDialog();
		deactivate();
	}
	/**
	 * Deactivate all active elements - display only!
	 */
	private void deactivate() {
		setDisplayOnly(true);
		getNameFld().setEditable(false);
		if(getCopyPhaseBtn() != null){
			getCopyPhaseBtn().setVisible(false);
		}
		getCompLbl().setForeground(Color.BLACK);
		if (getCompLbl().getMouseListeners().length>0){
			getCompLbl().removeMouseListener(getCompLbl().getMouseListeners()[0]);
		}
		if(getAddPhaseBtn() != null){
			getAddPhaseBtn().setVisible(false);
		}
		getCompFld().setEditable(false);
		getSaveBtn().setVisible(false);
		getPhasesCntrlPnl().setVisible(false);
		for(Component comp: getMainTabPane().getComponents()){
			PhaseComponent pComp = (PhaseComponent) comp;
			pComp.deactivate();
		}


	}

	public boolean isDisplayOnly() {
		return displayOnly;
	}
	public void setDisplayOnly(boolean displayOnly) {
		this.displayOnly = displayOnly;
	}
	public JTextField getNameFld() {
		return nameFld;
	}

	public JButton getCopyPhaseBtn() {
		return copyPhaseBtn;
	}

	public JButton getAddPhaseBtn() {
		return addPhaseBtn;
	}


	public JTextField getCompFld() {
		return compFld;
	}


	public JButton getSaveBtn() {
		return saveBtn;
	}



	public JPanel getPhasesCntrlPnl() {
		return phasesCntrlPnl;
	}


	public JTabbedPane getMainTabPane() {
		return mainTabPane;
	}

	public JLabel getCompLbl() {
		return compLbl;
	}
	public void setCompLbl(JLabel compLbl) {
		this.compLbl = compLbl;
	}


	public JPanel getGeneralInfoPnl() {
		return generalInfoPnl;
	}
	/**
	 * Paint the dialog
	 */
	private void paintDialog() {
		setSize(new Dimension(893, 534));
		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		int wdwLeft = screenSize.width / 2 - getWidth() / 2;
		int wdwTop = screenSize.height / 2 - getHeight() / 2;
		setLocation(wdwLeft, wdwTop);
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);


		JPanel headPnl = new JPanel();
		getContentPane().add(headPnl, BorderLayout.NORTH);
		headPnl.setLayout(new BorderLayout(0, 0));

		generalInfoPnl = new JPanel();
		generalInfoPnl.setPreferredSize(new Dimension(10, 70));
		headPnl.add(generalInfoPnl, BorderLayout.NORTH);
		generalInfoPnl.setLayout(null);

		JLabel nameLbl = new JLabel(Messages.getString("Regimen.name"));
		nameLbl.setBounds(10, 11, 115, 14);
		generalInfoPnl.add(nameLbl);

		nameFld = new JTextField();
		nameFld.setBounds(141, 8, 519, 20);
		generalInfoPnl.add(nameFld);
		nameFld.setColumns(10);
		final JDialog self = this;
		compLbl = new JLabel(Messages.getString("Regimen.composition"));
		compLbl.addMouseListener(new MouseAdapter() {
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
		compLbl.setForeground(Color.BLUE);
		compLbl.setBounds(10, 36, 115, 14);
		generalInfoPnl.add(compLbl);

		compFld = new JTextField();
		compFld.setEditable(false);
		compFld.setBounds(141, 39, 519, 20);
		generalInfoPnl.add(compFld);
		compFld.setColumns(10);

		typeLbl = new JLabel(Messages.getString("Regimen.types." + selected.getRegimen().getType().toString()));
		typeLbl.setHorizontalAlignment(SwingConstants.CENTER);
		typeLbl.setBorder(new LineBorder(new Color(0, 0, 0)));
		typeLbl.setBounds(691, 11, 176, 14);
		generalInfoPnl.add(typeLbl);

		JPanel phasesPnl = new JPanel();
		getContentPane().add(phasesPnl, BorderLayout.CENTER);
		phasesPnl.setLayout(new BorderLayout(0, 0));

		phasesCntrlPnl = new JPanel();
		phasesPnl.add(phasesCntrlPnl, BorderLayout.NORTH);
		phasesCntrlPnl.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		phasesCntrlPnl.add(panel, BorderLayout.EAST);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		if (getSelected().getRegimen().getType().equals(RegimenTypesEnum.MULTI_DRUG)){
			addPhaseBtn = new JButton(Messages.getString("Regimen.btn.addPhase"));
			panel.add(addPhaseBtn);
			addPhaseBtn.setHorizontalAlignment(SwingConstants.RIGHT);
			addPhaseBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					addPhase();
					addPhaseBtn.setEnabled(mainTabPane.getTabCount()<10);
				}
			});

			copyPhaseBtn = new JButton(Messages.getString("Regimen.btn.copyMedicines"));
			copyPhaseBtn.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					copyPreviousPhase();

				}
			});
			panel.add(copyPhaseBtn);
			copyPhaseBtn.setEnabled(false);



		}else{
			addPhaseBtn = new JButton(Messages.getString("Regimen.btn.addPhase"));
			panel.add(addPhaseBtn);
			addPhaseBtn.setHorizontalAlignment(SwingConstants.RIGHT);
			addPhaseBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					addPhase();
					addPhaseBtn.setEnabled(mainTabPane.getTabCount()<10);
					if(mainTabPane.getSelectedIndex()>0){
						copyPreviousPhase();
					}
				}
			});
		}
		JButton delPhaseBtn = new JButton(Messages.getString("Regimen.btn.delPhase")); 
		delPhaseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (Presenter.askDelete()){
					delPhase();
				}
			}
		});
		panel.add(delPhaseBtn);

		phasesDataPnl = new JPanel();
		phasesPnl.add(phasesDataPnl, BorderLayout.CENTER);
		phasesDataPnl.setLayout(new BorderLayout(0, 0));

		JPanel savePnl = new JPanel();
		getContentPane().add(savePnl, BorderLayout.SOUTH);
		savePnl.setLayout(new BorderLayout(10, 0));

		cancelBtn = new JButton(Messages.getString("DlgConfirm.cancelButton")); //$NON-NLS-1$
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(isDisplayOnly()){
					dispose();
				}else{
					Presenter.closeRegimenDlg(true, true);
				}
			}
		});
		savePnl.add(cancelBtn, BorderLayout.EAST);

		saveBtn = new JButton(Messages.getString("DlgEditMedicine.btnSave.text")); //$NON-NLS-1$
		saveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (generalCheck()){
					if (phasesCheck()){
						if (checkComposition()){
							Presenter.saveRegimenDic(getSelected(), wasEdited);
							Presenter.adjustRegimen(getSelected());
							dispose();
						}else{
							Presenter.closeRegimenDlg(true, true);
						}
					}
				}
			}
		});
		savePnl.add(saveBtn, BorderLayout.WEST);
		initDataBindings();
		paintPhases();
		addPropertyChangeListeners();
	}
	/**
	 * Copy all medications from the previous phase
	 */
	protected void copyPreviousPhase() {
		// take the current phase
		PhaseUIAdapter current = ((PhaseComponent)mainTabPane.getSelectedComponent()).getPhase();
		// take the previous phase
		int cInd = mainTabPane.getSelectedIndex();
		PhaseUIAdapter prev = ((PhaseComponent)mainTabPane.getComponent(cInd-1)).getPhase();
		//clean up the current and copy from the previous
		current.getPhase().getMedications().clear();
		for(MedicationUIAdapter mUi : prev.getMedications()){
			MedicationUIAdapter clone = mUi.createClone();
			current.getPhase().getMedications().add(clone.getMedication());
		}
		paintPhases();
		mainTabPane.setSelectedIndex(cInd);
		//TODO CHECK IT!!!
	}
	/**
	 * General close listener
	 */
	private void addCloseListener() {
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Presenter.closeRegimenDlg(true, true);
			}
		});


	}
	/**
	 * Add listener to listen some changes
	 */
	private void addPropertyChangeListeners() {
		// this listener listen all changes in the regimen
		getSelected().getRegimen().addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				setChanged(true);
			}
		});


	}
	/**
	 * Check all phases
	 */
	protected boolean phasesCheck() {
		Integer wrongTab = this.getSelected().getRegimen().checkPhases();
		if (wrongTab != null && mainTabPane.getTabCount() >= wrongTab+1){
			mainTabPane.setSelectedIndex(wrongTab);
			showPhaseError(wrongTab);
			return false;
		}else{
			return true;
		}

	}
	/**
	 * Check only general regimen issues
	 * if the composition is not defined - calc by default
	 */
	protected boolean generalCheck() {
		if (this.getSelected().getRegimen().getName().length() == 0){
			Presenter.showError(Messages.getString("Error.Validation.RegimenSave.FieldBlanks"));
			return false;
		}
		if (this.getSelected().getRegimen().getCompositions().length() == 0){
			this.getSelected().getRegimen().calcComposition();
		}
		return true;
	}

	/**
	 * Check the composition
	 */
	private boolean checkComposition() {
		String currentComp = selected.getRegimen().getConsumption();
		String calcComp = selected.getRegimen().calcComposition();
		if (!currentComp.equalsIgnoreCase(calcComp)){
			String mess = "<html><b>"+Presenter.getMessage("Warning.regimen.composition")+"</b><br>"+
					currentComp + "<br><b>"+
					Presenter.getMessage("Warning.regimen.composition.entered") + "</b><br>"+
					calcComp + "<br><b>" +
					Presenter.getMessage("Warning.regimen.composition.question");
			if (Presenter.showWarningString(mess)){
				selected.getRegimen().setConsumption(selected.getRegimen().calcComposition());
				return true;
			}else{
				return false;
			}
		}
		return true;
	}

	/**
	 * Delete the current phase - it is currently active tab
	 */
	protected void delPhase() {
		PhaseComponent current = (PhaseComponent) mainTabPane.getSelectedComponent();
		selected.getRegimen().shiftPhases(current.getPhase().getOrder());
		paintPhases();
	}
	/**
	 * Add a phase. Rules:
	 * <ul>
	 * <li> check previous tabs for consistency, it is not allowed to add new phase if at least one phase is inconsistent
	 * <li> add new phase on the new tab
	 * </ul>
	 */
	protected void addPhase() {
		Integer wrongTab = this.getSelected().getRegimen().checkPhases();
		if ( wrongTab!= null && mainTabPane.getTabCount()>=(wrongTab.intValue()+1)){
			mainTabPane.setSelectedIndex(wrongTab);
			showPhaseError(wrongTab);
		}else{
			PhaseUIAdapter pUi=null;
			if(mainTabPane.getTabCount() == 1){
				pUi = this.getSelected().getRegimen().getContinious();
			}else{
				pUi = this.getSelected().getRegimen().addPhase();
			}
			addTab(pUi, Messages.getString("Regimen.phase") + " " + (pUi.getOrder()));
		}
	}

	/**
	 * Show the phase error
	 * @param wrongTab
	 */
	private void showPhaseError(Integer wrongTab) {
		PhaseComponent pComp = (PhaseComponent) mainTabPane.getComponentAt(wrongTab);
		PhaseUIAdapter pUi = pComp.getPhase();
		Presenter.showError(pUi.validate());
	}

	/**
	 * Paint data for all phases, if no phases was defined, then no to paint
	 * 
	 */
	private void paintPhases() {
		//clean up the tabbed pane
		if (mainTabPane != null){
			mainTabPane.removeAll();
		}else{
			mainTabPane = new JTabbedPane(JTabbedPane.TOP);
		}
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				if (copyPhaseBtn != null){
					if (mainTabPane.getSelectedIndex()>0){
						copyPhaseBtn.setEnabled(true);
					}else{
						copyPhaseBtn.setEnabled(false);
					}
					if(addPhaseBtn != null){
						addPhaseBtn.setEnabled(mainTabPane.getTabCount()<10);
					}
				}
			}
		};
		mainTabPane.addChangeListener(changeListener);
		//paint intensive phase is mandatory
		PhaseUIAdapter intPhase = selected.getRegimen().getIntensive();
		intPhase.setOrder(1);
		String intHeader = Messages.getString("Regimen.phase") + " 1";
		/*		if(selected.getRegimen().isSingleDrug()){						20150428 not needed for multi phases single drug
			intHeader = Messages.getString("Regimen.phase.single");
		}*/
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
		//20150428 }
		phasesDataPnl.add(mainTabPane, BorderLayout.CENTER);
		mainTabPane.setSelectedIndex(0);
	}

	/**
	 * Add new tab on the phases tabbed pane
	 * @param phase phase to add
	 * @param header label will be placed on the new tab
	 */
	private void addTab(PhaseUIAdapter phase, String header) {
		Component comp = mainTabPane.add(header, new PhaseComponent(phase, selected.getRegimen().isSingleDrug(), this));
		mainTabPane.setSelectedComponent(comp);
	}

	///////////////////////   void methods for backward compatibility  ///////////////////////////////////////////
	public boolean isIntensive() {
		// TODO Auto-generated method stub
		return false;
	}

	public RegimenTmpStore getSelected() {
		return this.selected;
	}


	protected void initDataBindings() {
		BeanProperty<RegimenTmpStore, String> regimenTmpStoreBeanProperty = BeanProperty.create("regimen.name");
		BeanProperty<JTextField, String> jTextFieldBeanProperty = BeanProperty.create("text");
		AutoBinding<RegimenTmpStore, String, JTextField, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, selected, regimenTmpStoreBeanProperty, nameFld, jTextFieldBeanProperty);
		autoBinding.bind();
		//
		BeanProperty<RegimenTmpStore, String> regimenTmpStoreBeanProperty_1 = BeanProperty.create("regimen.consumption");
		BeanProperty<JTextField, String> jTextFieldBeanProperty_1 = BeanProperty.create("text");
		AutoBinding<RegimenTmpStore, String, JTextField, String> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, selected, regimenTmpStoreBeanProperty_1, compFld, jTextFieldBeanProperty_1);
		autoBinding_1.bind();
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



}
