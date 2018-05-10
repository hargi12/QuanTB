package org.msh.quantb.view.mvp;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.msh.quantb.model.mvp.ModelFactory;
import org.msh.quantb.services.excel.ImportExcel;
import org.msh.quantb.services.io.ForecastLast5UI;
import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.io.ForecastingBatchUIAdapter;
import org.msh.quantb.services.io.ForecastingMedicineUIAdapter;
import org.msh.quantb.services.io.ForecastingOrderUIAdapter;
import org.msh.quantb.services.io.ForecastingRegimensUIAdapter;
import org.msh.quantb.services.io.MedicineUIAdapter;
import org.msh.quantb.services.io.MedicinesDicUIAdapter;
import org.msh.quantb.services.io.PhaseUIAdapter;
import org.msh.quantb.services.io.RegimenTmpStore;
import org.msh.quantb.services.io.RegimenUIAdapter;
import org.msh.quantb.services.io.RegimensDicUIAdapter;
import org.msh.quantb.services.io.SliceFCUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.CanShowMessages;
import org.msh.quantb.view.ISelectableMedicine;
import org.msh.quantb.view.dialog.AboutDlg;
import org.msh.quantb.view.dialog.ForecastFileHistoryDlg;
import org.msh.quantb.view.dialog.ForecastSliceDlg;
import org.msh.quantb.view.dialog.ForecastingBatchDlg;
import org.msh.quantb.view.dialog.ForecastingOrderDlg;
import org.msh.quantb.view.dialog.ForecastingWizardDlg;
import org.msh.quantb.view.dialog.IMultiMedSelection;
import org.msh.quantb.view.dialog.MedicineDlg;
import org.msh.quantb.view.dialog.MedicineSelectDlg;
import org.msh.quantb.view.dialog.MedicineSelectMulDlg;
import org.msh.quantb.view.dialog.MedicineSelectWeekDlg;
import org.msh.quantb.view.dialog.MedicinesAdjustDlg;
import org.msh.quantb.view.dialog.MedicinesDecodeDlg;
import org.msh.quantb.view.dialog.MedicinesDlg;
import org.msh.quantb.view.dialog.RegimenSelectDlg;
import org.msh.quantb.view.dialog.RegimensDlg;
import org.msh.quantb.view.dialog.RegimensListDlg;
import org.msh.quantb.view.dialog.SelectMergeDlg;
import org.msh.quantb.view.dialog.WrongVersionDlg;
import org.msh.quantb.view.panel.ForecastingDocumentPanel;
import org.msh.quantb.view.window.MainWindow;
import org.pushingpixels.lafwidget.animation.AnimationConfigurationManager;
import org.pushingpixels.lafwidget.animation.AnimationFacet;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceModerateLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceSaharaLookAndFeel;

/**
 * Responsible for control all windows, and actions in them. Include visual
 * components
 * 
 * @author User
 * 
 */
public class ViewFactory implements CanShowMessages {
	public static final String DECIMAL_FORMAT = "###,###,###,##0.00";
	public static final String BIG_WHOLE_FORMAT = "###,###,###,###";
	private MainWindow mainWindow;
	private MedicinesDlg medicinesDlg;
	// old style private RegimensListDlg regimensListDlg;
	private RegimensListDlg regimensListDlg;
	private MedicineSelectDlg selectedMEdicineDlg;
	private MedicineSelectMulDlg selectedMEdicineMulDlg;
	private RegimenSelectDlg regimenSelectDlg;
	//private RegimensDlg regimensDlg;
	private RegimensDlg regimensDlg;
	private ForecastingWizardDlg forecastingWizardDlg;
	private MedicineDlg newMedicineDlg;
	private ForecastingDocumentPanel activeForecastingPanel;
	private AboutDlg aboutDlg;
	private ForecastingOrderDlg forecastingOrderDlg;
	private ForecastingBatchDlg forecastingBatchDlg;
	private int lastNewIndex = 0;
	private ForecastFileHistoryDlg last5Dlg = null;

	public ViewFactory() {}

	/**
	 * Launch the application main window
	 * 
	 * @param createForecasting true - need to create new forecasting at
	 *        beginning, false - doesn't need.
	 */
	public void showMainWindow(final boolean createForecasting) {

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					UIManager.put(SubstanceLookAndFeel.SHOW_EXTRA_WIDGETS, Boolean.TRUE);
					//UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
					UIManager.setLookAndFeel(new SubstanceSaharaLookAndFeel());
					AnimationConfigurationManager.getInstance().disallowAnimations(AnimationFacet.ROLLOVER);
					mainWindow = new MainWindow();
					if (createForecasting) Presenter.createForecasting();
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace(); //nothing to do, no way to inform user
				}

			}
		});
	}

	/**
	 * Launch the application main window
	 */
	public void reloadMainWindow(final List<File> files) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.put(SubstanceLookAndFeel.SHOW_EXTRA_WIDGETS, Boolean.TRUE);
					//UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
					UIManager.setLookAndFeel(new SubstanceModerateLookAndFeel());
					AnimationConfigurationManager.getInstance().disallowAnimations(AnimationFacet.ROLLOVER);
					mainWindow = new MainWindow();
					Presenter.reopenExistingForecastings(files);
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace(); //nothing to do, no way to inform user
				}

			}
		});
	}

	/**
	 * Show error message to user
	 * 
	 * @param mess error message
	 */
	public void showError(String mess) {
		final String mes1 = mess.replaceAll("\\\\n", "\n");
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				setLocale();
				JOptionPane.showMessageDialog(mainWindow, mes1, Presenter.getMessage("Error"), JOptionPane.ERROR_MESSAGE);
			}
		});
	}

	/**
	 * Show simple warning without yes or no
	 * @param string
	 * @return 
	 */
	public boolean showSimpleWarningString(String message) {
		setLocale();
		//JOptionPane.showConfirmDialog(view.getMainWindow(), message, Messages.getString("Application.ask.fileSaveAs.infoTitle"), JOptionPane.OK_OPTION);
		JOptionPane.showMessageDialog(getMainWindow(), message, Messages.getString("Application.ask.fileSaveAs.infoTitle"),
				JOptionPane.OK_OPTION);
		return true;

	}

	/**
	 * Ask user's confirmation to exit application
	 * 
	 * @return
	 */
	public int showExitConfirmDlg() {
		setLocale();
		Object[] options = {Presenter.getMessage("Application.ask.saveclose"),
				Presenter.getMessage("Application.ask.close"),
				Presenter.getMessage("Application.ask.cancel")};
		int res = JOptionPane.showOptionDialog(getMainWindow(),
				Presenter.getMessage("Application.ask.nextaction"),
				Presenter.getMessage("Application.ask.title"),
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[2]);
		return res;
	}

	/**
	 * Ask user's confirmation to close forecasting
	 * @return JOptionPanel YES NO CANCEL
	 */
	public int showCloseForecastingConfirmDlg() {
		setLocale();
		Object[] options = {Presenter.getMessage("Forecasting.ask.saveclose"),
				Presenter.getMessage("Forecasting.ask.close"),
				Presenter.getMessage("Forecasting.ask.cancel")};
		int res = JOptionPane.showOptionDialog(getMainWindow(),
				Presenter.getMessage("Forecasting.ask.nextaction"),
				Presenter.getMessage("Forecasting.ask.title"),
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[2]);
		return res;
	}

	/**
	 * Ask user's confirmation to delete custom data
	 * 
	 * @return true - answer yes, false - answer no
	 */
	public boolean showDeleteConfitmDlg() {
		setLocale();
		int res = JOptionPane.showConfirmDialog(this.getMainWindow(), Presenter.getMessage("Application.delete.message"), Presenter.getMessage("Application.delete.title"), JOptionPane.YES_NO_OPTION);
		return res == JOptionPane.YES_OPTION;
	}

	public boolean showSaveConfitmDlg() {
		setLocale();
		int res = JOptionPane.showConfirmDialog(this.getMainWindow(), Presenter.getMessage("Application.unsaved.message"), Presenter.getMessage("Application.delete.title"), JOptionPane.YES_NO_OPTION);
		return res == JOptionPane.YES_OPTION;
	}

	/**
	 * Ask user's confirmation to saving custom data.
	 * @param message 
	 * 
	 * @return true - answer yes, false - answer no.
	 */
	public boolean showSaveDocumentConfirmDlg(String message) {
		setLocale();
		int res = JOptionPane.showConfirmDialog(this.getMainWindow(), message, Presenter.getMessage("Application.saveDocument.title"), JOptionPane.YES_NO_OPTION);
		return res == JOptionPane.YES_OPTION;
	}

	public void disposeMainWindow() {
		mainWindow.dispose();
	}

	/**
	 * Show medicine dictionary
	 * @param medicines medicines dictionary
	 * @param selected selected medicine
	 */
	public void showMedicineListDlg(MedicinesDicUIAdapter medicines, MedicineUIAdapter selected) {
		medicinesDlg = new MedicinesDlg(medicines, selected, mainWindow);
		medicinesDlg.setVisible(true);
	}

	/**
	 * Show regimes dictionary in dialog window
	 * 
	 * @param data
	 */
	public void showRegimenListDialog(RegimensDicUIAdapter data) {
		regimensListDlg = new RegimensListDlg(data, mainWindow);
		regimensListDlg.setVisible(true);

	}

	/**
	 * Show selected medicine dialog
	 * 
	 * @param medicinesDic medicines dictionary
	 * @param applicant object need to select medicine
	 * @param isSingle is single selection of medications
	 */
	public void showSelectedMedicineDlg(MedicinesDicUIAdapter medicinesDic, ISelectableMedicine applicant, boolean isSingle) {
		if (isSingle) {
			selectedMEdicineDlg = new MedicineSelectDlg(medicinesDic, applicant, this.mainWindow);
			selectedMEdicineDlg.setVisible(true);
		} else {
			selectedMEdicineMulDlg = new MedicineSelectMulDlg(medicinesDic, this.mainWindow);
			selectedMEdicineMulDlg.setVisible(true);
		}
	}

	public MedicinesDlg getMedicinesDlg() {
		return medicinesDlg;
	}

	/**
	 * Get current regimens list dialog box
	 * 
	 * @return
	 */
	public RegimensListDlg getRegimenListDlg() {
		return this.regimensListDlg;
	}

	/**
	 * Get regimen dialo, must exist!!!
	 * 
	 * @return
	 */
	public RegimensDlg getRegimensDlg() {
		return this.regimensDlg;
	}

	/**
	 * Get new forecasting dialog
	 * 
	 * @return
	 */
	public ForecastingWizardDlg getForecastingWizardDlg() {
		return forecastingWizardDlg;
	}

	/**
	 * Get main window
	 * 
	 * @return the main Window
	 */
	public MainWindow getMainWindow() {
		return mainWindow;
	}

	/**
	 * @return the forecastingOrderDlg
	 */
	public ForecastingOrderDlg getForecastingOrderDlg() {
		return forecastingOrderDlg;
	}

	/**
	 * Open Regimen dialog for selected regimen
	 * 
	 * @param selected
	 * @param isFromRegimen does it call from RegimenListDialog
	 * @param isEdited does RegimenTmpStore edit
	 */
	public void showRegimensDlg(RegimenTmpStore selected, boolean isFromRegimen, boolean isEdited) {
		this.regimensDlg = new RegimensDlg(selected, isFromRegimen ? this.regimensListDlg : this.forecastingWizardDlg, isFromRegimen, isEdited);
		this.regimensDlg.setVisible(true);
	}

	/**
	 * Open FileChooserDialog and return forecasting XML file
	 * 
	 * @param defaultDirectory default directory path
	 * @return selected XML file
	 */
	public File getForecastingFile(String defaultDirectory) {
		setLocale();
		final JFileChooser chooser = getFileChooser(defaultDirectory);
		chooser.setDialogTitle(Messages.getString("Application.openDocument.title"));
		FileNameExtensionFilter fileFilter = new FileNameExtensionFilter(Presenter.getMessage("File.forecating.def"), "qtb");
		chooser.setFileFilter(fileFilter);
		chooser.setAcceptAllFileFilterUsed(false);
		if(JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this.mainWindow) && !chooser.getSelectedFile().isDirectory()){
			File f = chooser.getSelectedFile();
			Presenter.saveCurrentPath(f.getParent());
			return f;
		}
		return null;
	}
	/**
	 * Get file chooser common for all
	 * @param defaultDirectory
	 * @return
	 */
	private JFileChooser getFileChooser(String defaultDirectory) {
		final JFileChooser chooser = new JFileChooser(defaultDirectory);
		JPanel panel = new JPanel();
		JButton button = new JButton(Messages.getString("DlgOpenFile.home"));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooser.setCurrentDirectory(new File(Presenter.getHomeDocPath()));
			}
		});
		panel.add(button);
		chooser.setAccessory(panel);
		return chooser;
	}
	
	/**
	 * Get file chooser for Excel file save
	 * This file chooser has an additional combo to select full or short save operation
	 * @param defaultDirectory
	 * @param typeBox 
	 * @return
	 */
	private JFileChooser getExcelFileChooser(String defaultDirectory, JComboBox<String> comboBox){
		JFileChooser ret = getFileChooser(defaultDirectory);
         JPanel comboPnl = new JPanel();
         comboPnl.setLayout(new BoxLayout(comboPnl, BoxLayout.X_AXIS));
         comboPnl.setBorder(new EmptyBorder(0, 0, 0, 5));
         JLabel label = new JLabel(Messages.getString("DlgExportExcel.typeLabel"));
         label.setBorder(new EmptyBorder(0, 0, 0, 5));
         comboPnl.add(label);
         comboPnl.add(comboBox);
         JPanel panel1 = (JPanel) ret.getComponent(3);
         JPanel panel2 = (JPanel) panel1.getComponent(2);

         Component c1=panel2.getComponent(0);//optional used to add the buttons after combobox
         Component c2=panel2.getComponent(1);//optional used to add the buttons after combobox
         panel2.removeAll();

         panel2.add(comboPnl);
         panel2.add(c1);//optional used to add the buttons after combobox
         panel2.add(c2);//optional used to add the buttons after combobox
         return ret;
	}
	/**
	 * Create the box for export excel type
	 * @return
	 */
	public JComboBox<String> createExportTypeBox() {
		JComboBox<String> comboBox = new JComboBox<String>();
         comboBox.setModel(new DefaultComboBoxModel<String>(new String[] { Messages.getString("DlgExportExcel.fullType"),
        		 Messages.getString("DlgExportExcel.shortType") }));
         comboBox.setRenderer(new DefaultListCellRenderer());
		return comboBox;
	}
	/**
	 * Did user pick full export?
	 * @param typeBox
	 * @return
	 */
	public boolean isFullType(JComboBox<String> typeBox) {
		String s = (String) typeBox.getSelectedItem();
		return s.equalsIgnoreCase(Messages.getString("DlgExportExcel.fullType"));
	}

	/**
	 * Open FileChooserDialog for saving and return forecasting XML file
	 * 
	 * @param defaultDirectory
	 * @param fileName 
	 * @return
	 */
	public File saveForecastingDocument(String defaultDirectory, String fileName) {
		setLocale();
		JFileChooser chooser = getFileChooser(defaultDirectory);
		chooser.setDialogTitle(Messages.getString("Application.saveDocument.title"));
		FileNameExtensionFilter fileFilter = new FileNameExtensionFilter(Presenter.getMessage("File.forecating.def"), "qtb");
		chooser.setFileFilter(fileFilter);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setSelectedFile(new File(fileName));
		if(JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(this.mainWindow) && !chooser.getSelectedFile().isDirectory()){
			File f = chooser.getSelectedFile();
			Presenter.saveCurrentPath(f.getParent());
			return f;
		}

		return null;
	}

	public void setLocale() {
		UIManager.put("FileChooser.acceptAllFileFilterText", Messages.getString("DlgOpenFile.acceptAllFileFilterText"));
		UIManager.put("FileChooser.lookInLabelText", Messages.getString("DlgOpenFile.lookInLabelText"));
		UIManager.put("FileChooser.cancelButtonText", Messages.getString("DlgOpenFile.cancelButtonToolTipText"));
		UIManager.put("FileChooser.cancelButtonToolTipText", Messages.getString("DlgOpenFile.cancelButtonToolTipText"));
		UIManager.put("FileChooser.openButtonText", Messages.getString("DlgOpenFile.openButtonText"));
		UIManager.put("FileChooser.openButtonToolTipText", Messages.getString("DlgOpenFile.openButtonText"));
		UIManager.put("FileChooser.filesOfTypeLabelText", Messages.getString("DlgOpenFile.filesOfTypeLabelText"));
		UIManager.put("FileChooser.fileNameLabelText", Messages.getString("DlgOpenFile.fileNameLabelText"));
		UIManager.put("FileChooser.listViewButtonToolTipText", Messages.getString("DlgOpenFile.listViewButtonToolTipText"));
		UIManager.put("FileChooser.listViewButtonAccessibleName", Messages.getString("DlgOpenFile.listViewButtonToolTipText"));
		UIManager.put("FileChooser.detailsViewButtonToolTipText", Messages.getString("DlgOpenFile.detailsViewButtonToolTipText"));
		UIManager.put("FileChooser.detailsViewButtonAccessibleName", Messages.getString("DlgOpenFile.detailsViewButtonToolTipText"));
		UIManager.put("FileChooser.upFolderToolTipText", Messages.getString("DlgOpenFile.upFolderToolTipText"));
		UIManager.put("FileChooser.upFolderAccessibleName", Messages.getString("DlgOpenFile.upFolderToolTipText"));
		UIManager.put("FileChooser.homeFolderToolTipText", Messages.getString("DlgOpenFile.homeFolderToolTipText"));
		UIManager.put("FileChooser.homeFolderAccessibleName", Messages.getString("DlgOpenFile.homeFolderToolTipText"));
		UIManager.put("FileChooser.fileNameHeaderText", Messages.getString("DlgOpenFile.fileNameLabelText"));
		UIManager.put("FileChooser.fileSizeHeaderText", Messages.getString("DlgOpenFile.fileSizeHeaderText"));
		UIManager.put("FileChooser.fileTypeHeaderText", Messages.getString("DlgOpenFile.fileTypeHeaderText"));
		UIManager.put("FileChooser.saveInLabelText", Messages.getString("DlgOpenFile.saveInLabelText"));
		UIManager.put("FileChooser.saveButtonText", Messages.getString("DlgOpenFile.saveButtonText"));
		UIManager.put("FileChooser.saveButtonToolTipText", Messages.getString("DlgOpenFile.saveButtonText"));
		UIManager.put("OptionPane.cancelButtonText", Messages.getString("DlgConfirm.cancelButton"));
		UIManager.put("OptionPane.noButtonText", Messages.getString("DlgConfirm.noButton"));
		UIManager.put("OptionPane.okButtonText", Messages.getString("DlgConfirm.okButton"));
		UIManager.put("OptionPane.yesButtonText", Messages.getString("DlgConfirm.yesButton"));
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);
	}

	/**
	 * Open FileChooserDialog for saving and return exporting Excel file
	 * 
	 * @param defaultDirectory
	 * @param typeBox 
	 * @return
	 */
	public File exportForecastingCalculation(String defaultDirectory, JComboBox<String> typeBox) {
		setLocale();
		JFileChooser chooser = getExcelFileChooser(defaultDirectory, typeBox);
		while(true){
			chooser.setDialogTitle(Messages.getString("Application.exportExcel.title"));
			FileNameExtensionFilter fileFilter = new FileNameExtensionFilter(Presenter.getMessage("Application.importExcel.file"), "xlsx");
			chooser.setFileFilter(fileFilter);
			chooser.setAcceptAllFileFilterUsed(false);

			File res = (JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(this.mainWindow) && !chooser.getSelectedFile().isDirectory()) ? chooser.getSelectedFile() : null;
			if(res != null){
				if(!res.getName().endsWith(".xlsx")){
					res = new File(res.getAbsoluteFile() + ".xlsx");
				}
				if(res.exists() && !res.isDirectory()){
					if(!res.renameTo(res)){ //trick to determine lock
						if(!Presenter.showWarningStringStrict(res.getAbsoluteFile() +" " +Messages.getString("Application.ask.fileSaveAs.alreadyOpened") + " "  + res.getName())){
							return null;
						}
					}else{
						return res; //no lock
					}
				}else{
					return res;
				}
			}else{
				return res;
			}
		}
	}

	/**
	 * Add new tab in Mainwindow with document of forecasting
	 * 
	 * @param forecastObj document of forecasting
	 * @param file working file
	 */
	public void addForecastingDocument(ForecastUIAdapter forecastObj, File file) {

		String realTabName;
		if (file != null) {
			int index = file.getName().lastIndexOf(ModelFactory.FORECAST_FILE_EXT);
			if (index > 0) {
				realTabName = file.getName().substring(0, index);
			} else {
				realTabName = file.getName();
			}
			//realTabName = (realTabName.length() <= 30) ? realTabName : (realTabName.substring(0, 29).concat("..."));
			if (this.mainWindow.isForecastingOpen(realTabName)) {
				if (Presenter.askReopen()){
					getMainWindow().setForecasting(realTabName);
					getMainWindow().closeActiveForecastingTab(getActiveForecastingPanel());
					//TODO open
				}else {
					getMainWindow().setForecasting(realTabName);
					return;
				}
			}
		} else {
			realTabName = Messages.getString("Forecasting.newForecasting.name") + "-" + this.getLastNewIndex();
			forecastObj.getForecastObj().setName(realTabName);
			this.setLastNewIndex(this.getLastNewIndex() + 1);
		}
		ForecastingDocumentPanel documentPanel = new ForecastingDocumentPanel(forecastObj);
		documentPanel.setWorkingFile(file);
		setActiveForecastingPanel(documentPanel);
		String fileName = "";
		if(file == null){
			fileName = realTabName +".qtb";
		}else{
			fileName = file.getName();
		}
		this.mainWindow.addForecastingTab(documentPanel, realTabName, fileName);
	}

	/**
	 * set last new index
	 * 
	 * @param i
	 */
	private void setLastNewIndex(int i) {
		this.lastNewIndex = i;
	}

	/**
	 * Get last index of new forecasting tab
	 * 
	 * @return
	 */
	private int getLastNewIndex() {
		return this.lastNewIndex;
	}

	/**
	 * Open new forecasting wizard dialog for creating new regimen
	 * 
	 * @param forecastUIAdapter forecasting for UI
	 * @param regimensDicUIAdapter all existing regimens from dictionary for UI
	 */
	public void showNewForecastingWizard(ForecastUIAdapter forecastUIAdapter, RegimensDicUIAdapter regimensDicUIAdapter) {
		forecastingWizardDlg = new ForecastingWizardDlg(forecastUIAdapter, regimensDicUIAdapter, this.mainWindow);
		forecastingWizardDlg.setVisible(true);
	}

	/**
	 * Open new medicine dialog for creating new medicine
	 * 
	 * @param med medicine for UI
	 * @param isEdit new medicine dialog uses for edit medicine or for creating
	 *        new
	 */
	public void showNewMedicineDialog(MedicineUIAdapter med, boolean isEdit) {
		if(newMedicineDlg != null){
			newMedicineDlg.dispose();
		}
		newMedicineDlg = new MedicineDlg(med, medicinesDlg, isEdit);
		newMedicineDlg.setVisible(true);
	}

	/**
	 * Show selected forecasting regimen dialog
	 * 
	 * @param forecastingRegimensUIAdapter UI object for forecasting regimens
	 */
	public void showRegimenSelectDlg(ForecastingRegimensUIAdapter forecastingRegimensUIAdapter) {
		regimenSelectDlg = new RegimenSelectDlg(forecastingRegimensUIAdapter, this.mainWindow);
		regimenSelectDlg.setVisible(true);
	}

	/**
	 * Get active forecasting document panel
	 * 
	 * @return foreacasting panel
	 */
	public ForecastingDocumentPanel getActiveForecastingPanel() {
		return activeForecastingPanel;
	}

	/**
	 * Set active forecasting document panel
	 * 
	 * @param activeForecastPanel foreacasting panel
	 */
	public void setActiveForecastingPanel(ForecastingDocumentPanel activeForecastPanel) {
		this.activeForecastingPanel = activeForecastPanel;
	}

	/**
	 * Show about dialog box
	 */
	public void showAboutDialog() {
		aboutDlg = new AboutDlg(mainWindow);
		aboutDlg.setVisible(true);
	}

	/**
	 * Show forecasting edit order dialog
	 * 
	 * @param selected forecasting order for edit
	 * @param refDate referance date
	 * @param isEdit is batch edit or new
	 */
	public void showForecastingOrderDlg(ForecastingOrderUIAdapter selected, Calendar refDate, boolean isEdit) {
		forecastingOrderDlg = new ForecastingOrderDlg(selected, this.mainWindow, this.getActiveForecastingPanel(), refDate, isEdit);
		forecastingOrderDlg.setVisible(true);
	}

	/**
	 * Show forecasting edit batch dialog
	 * 
	 * @param forecastingBatchUIAdapter batch for edit
	 * @param referenceDate reference date
	 * @param isEdit is batch edit or new
	 */
	public void showForecastingBatchDlg(ForecastingBatchUIAdapter forecastingBatchUIAdapter, Calendar referenceDate, boolean isEdit) {
		forecastingBatchDlg = new ForecastingBatchDlg(forecastingBatchUIAdapter, this.mainWindow, this.getActiveForecastingPanel(), referenceDate, isEdit);
		forecastingBatchDlg.setVisible(true);
	}

	/**
	 * Ask user about default locale
	 * @return selected locale (eng,rus), null - non selection
	 */
	public Object askLocale() {
		Object[] possibleValues = {Messages.getString("DlgLocaleSelect.eng"), Messages.getString("DlgLocaleSelect.rus")};
		return JOptionPane.showInputDialog(mainWindow,Messages.getString("DlgLocaleSelect.captionlbl"), Messages.getString("DlgLocaleSelect.title"),JOptionPane.QUESTION_MESSAGE, null,possibleValues, possibleValues[0]);
	}
	/**
	 * Show simple Yes/No application exit dialog
	 * @return
	 */
	public boolean showSimpleExitConfirmation() {
		setLocale();
		int n = JOptionPane.showConfirmDialog(
				getMainWindow(),
				Presenter.getMessage("Application.exit.message"),
				Presenter.getMessage("Application.exit.title"),
				JOptionPane.YES_NO_OPTION);

		return n==JOptionPane.YES_OPTION;
	}
	/**
	 * Show medicine selection dialog and pass result to actioner
	 * @param medDictionary
	 * @param actioner
	 */
	public void showMedicineSelectWeekDlg(MedicinesDicUIAdapter medDictionary,
			IMultiMedSelection actioner) {
		MedicineSelectWeekDlg dlg = new MedicineSelectWeekDlg(this.mainWindow,medDictionary, actioner);
		dlg.setVisible(true);

	}
	/**
	 * Show last 5 files dialog
	 * @param fl5Ui data model for last 5 files
	 */
	public void showLast5Dlg(ForecastLast5UI fl5Ui) {
		ForecastFileHistoryDlg dlg = new ForecastFileHistoryDlg(fl5Ui, this.getMainWindow());
		dlg.setVisible(true);

	}

	/**
	 * Create medicine selection dialog for the defined phase
	 * @param medi medicines list
	 * @param phase the phase dialog created for
	 * @param phaseDataTable 
	 * @return
	 */
	public MedicineSelectMulDlg createMedicineSelectMulDlg(
			MedicinesDicUIAdapter medi, PhaseUIAdapter phase, JTable phaseDataTable) {
		MedicineSelectMulDlg ret = new MedicineSelectMulDlg(medi, this.getMainWindow());
		ret.setPhase(phase, phaseDataTable);
		return ret;
	}
	/**
	 * Create single medicine selection dialog for defined phase. Replace medicine
	 * @param medi medicines list
	 * @param phase - single medicine phase at witch the new medicine should be replaced
	 * @param phaseDataTable data table for refresh
	 * @return
	 */
	public MedicineSelectDlg createMedicineSelectDlg(
			MedicinesDicUIAdapter medi, PhaseUIAdapter phase,
			JTable phaseDataTable) {
		MedicineSelectDlg ret = new MedicineSelectDlg(medi, null, this.getMainWindow());
		ret.setPhase(phase, phaseDataTable);
		return ret;
	}
	/**
	 * Create the medicine adjustment dialog
	 * @param medi
	 * @return
	 */
	public MedicinesAdjustDlg createMedicinesAjustDlg(List<ForecastingMedicineUIAdapter> medi){
		MedicinesAdjustDlg ret = new MedicinesAdjustDlg(medi, this.getMainWindow());
		return ret;
	}
	/**
	 * Show general cancel confirmation dialog
	 * @return
	 */
	public boolean showCancelConfirmDlg() {
		setLocale();
		int res = JOptionPane.showConfirmDialog(this.getMainWindow(), Presenter.getMessage("Application.cancel.message"), Presenter.getMessage("Application.cancel.title"), JOptionPane.YES_NO_OPTION);
		return res == JOptionPane.YES_OPTION;
	}
	/**
	 * Show file rewrite dialog for save as operation
	 * @param name file name
	 * @return
	 */
	public boolean showFileRewriteDialog(String name) {
		setLocale();
		String[] options = {(String) UIManager.get("OptionPane.yesButtonText"), (String) UIManager.get("OptionPane.noButtonText")};
		int res = JOptionPane.showOptionDialog(this.getMainWindow(), "<html>" + name + " " + Messages.getString("Application.ask.fileSaveAs.message"),
				Messages.getString("Application.ask.fileSaveAs.title"), JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,
				null,options, UIManager.get("OptionPane.noButtonText"));
		return res == JOptionPane.YES_OPTION;
	}
	/**
	 * Ask for reopen a forecasting file
	 * @return
	 */
	public boolean showAskFileReopen() {
		setLocale();
		String[] options = {(String) UIManager.get("OptionPane.yesButtonText"), (String) UIManager.get("OptionPane.noButtonText")};
		int res = JOptionPane.showOptionDialog(this.mainWindow, Messages.getString("Application.reloadFile.message"),
				Messages.getString("Application.reloadFile.title"), JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,
				null,options, UIManager.get("OptionPane.noButtonText"));
		return res == JOptionPane.YES_OPTION;
	}

	/**
	 * Show information for user
	 * @param message
	 * @param title TODO
	 */
	public void showInformation(String message, String title){
		setLocale();
		JOptionPane.showMessageDialog(getMainWindow(), message,
				title, JOptionPane.INFORMATION_MESSAGE);
	}
	/**
	 * Get excel (xlsx) file for all import operation (Medicines Stock etc)
	 * @param docDefaultPath
	 * @return file if picked, otherwise null
	 */
	public File getExcelForImport(String docDefaultPath) {
		setLocale();
		JFileChooser chooser = getFileChooser(docDefaultPath);
		chooser.setDialogTitle(Messages.getString("Application.importExcel.title"));
		FileNameExtensionFilter fileFilter = new FileNameExtensionFilter(Presenter.getMessage("Application.importExcel.file"), "xlsx");
		chooser.setFileFilter(fileFilter);
		chooser.setAcceptAllFileFilterUsed(false);
		if(JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this.mainWindow) && !chooser.getSelectedFile().isDirectory()){
			File f = chooser.getSelectedFile();
			Presenter.saveCurrentPath(f.getParent());
			return f;
		}
		
		return null;
	}
	/**
	 * Set relation between the exported from Excel medicine's stock and the Forecasting medicines stock
	 * @param medicines medicines from the forecasting
	 * @param ie "decoded" Excel
	 */
	public MedicinesDecodeDlg createMedDecoderDialog(
			List<ForecastingMedicineUIAdapter> medicines, ImportExcel ie) {
		setLocale();
		MedicinesDecodeDlg dlg = new MedicinesDecodeDlg(medicines, ie, this.getMainWindow());
		return dlg;

	}


	/**
	 * Show "get slice" dialog for the current forecasting
	 * @param sUi model that the dialog uses
	 */
	public void showSliceDialog(SliceFCUIAdapter sUi) {
		ForecastSliceDlg fSdlg = new ForecastSliceDlg(sUi,getMainWindow());
		fSdlg.setVisible(true);

	}

	public void showSelectMergeDialog(List<ForecastUIAdapter> allFC) {
		SelectMergeDlg dlg = new SelectMergeDlg(allFC, getMainWindow());
		dlg.setVisible(true);

	}
	/**
	 * Show wrong version message
	 */
	public void showWrongVersionDialog() {
		WrongVersionDlg dlg = new WrongVersionDlg(getMainWindow());
		dlg.setVisible(true);
	}
	/**
	 * Show the regimen
	 * Edit is prohibited
	 * @param regimen
	 */
	public void showRegimen(RegimenUIAdapter regimen) {
		RegimensDlg dlg = new RegimensDlg(new RegimenTmpStore(regimen));
		dlg.setVisible(true);
		
	}
	/**
	 * Ask user about Excel template file for stock import
	 * @param currentDirectory proposed directory
	 */
	public File askImportTemplateFile(String currentDirectory) {
		setLocale();
		JFileChooser chooser = getFileChooser(currentDirectory);
		while(true){
			chooser.setDialogTitle(Messages.getString("MainWindow.menuForecasting.ItemImportFromExcel.Template"));
			FileNameExtensionFilter fileFilter = new FileNameExtensionFilter(Presenter.getMessage("Application.importExcel.file"), "xlsx");
			chooser.setFileFilter(fileFilter);
			chooser.setAcceptAllFileFilterUsed(false);
			File res = (JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(this.mainWindow) && !chooser.getSelectedFile().isDirectory()) ? chooser.getSelectedFile() : null;
			if(res != null){
				if(!res.getName().endsWith(".xlsx")){
					res = new File(res.getAbsoluteFile() + ".xlsx");
				}
				if(res.exists() && !res.isDirectory()){
					if(!res.renameTo(res)){ //trick to determine lock
						if(!Presenter.showWarningStringStrict(res.getAbsoluteFile() +" " +Messages.getString("Application.ask.fileSaveAs.alreadyOpened") + " "  + res.getName())){
							return null;
						}
					}else{
						return res; //no lock
					}
				}else{
					return res;
				}
			}else{
				return res;
			}
		}
		
	}
	/**
	 * Implementation for all warning dialogs
	 * @param message message to display in box
	 * @param title message to display in head (dialog title)
	 * @param defaultNo default option is No
	 * @return
	 */
	public boolean showCommonWarning(String message, String title, boolean defaultNo){
		setLocale();
		message = message.replaceAll("\\|", "\n\n");
		final String mes1 = message.replaceAll("\\. ", "\\.\n");
		String[] options = {
				Messages.getString("DlgConfirm.yesButton"), Messages.getString("DlgConfirm.noButton")
		};
		String defaultOption = options[0];
		if(defaultNo){
			defaultOption = options[1];
		}
		int res = JOptionPane.showOptionDialog(getMainWindow(), mes1, title,
				JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,null, options, defaultOption);

		return res == 0;
	}





}
