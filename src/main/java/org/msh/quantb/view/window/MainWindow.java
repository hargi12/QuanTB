package org.msh.quantb.view.window;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.msh.quantb.model.gen.RegimenTypesEnum;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.TabPaneClosed;
import org.msh.quantb.view.panel.ForecastingDocumentPanel;
import org.msh.quantb.view.panel.HelpDocumentPanel;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;

public class MainWindow extends JFrame {
	private static final String SHOW_HELP = "SHOW_HELP";
	private static final String SHOW_DOCUMENTS = "SHOW_DOCUMENTS";
	private static final long serialVersionUID = -2231356897722312952L;
	private JTabbedPane panelDocuments;
	private JMenuItem mntmClose;
	private JMenuItem mntmSave;
	private JMenuItem mntmSaveAs;
	private JButton btnSave;
	private JButton btnExecute;
	private JButton btnExportToExcel;
	private JMenuItem mntmExportToExcel;
	private JButton btnEnglishLocale;
	private JButton btnRusLocale;
	private JToolBar tbMainToolBar;
	private JMenuBar menuBar;
	private JPanel tbStatusBar;
	private JMenuItem menuLastDocs;
	private JButton btnChLocale;
	private JButton btnFRLocale;
	private JMenuItem mntmExcelWHO;
	private JButton btnESLocale;
	private JButton btnPTLocale;
	private JMenuItem mntmExportToDic;
	private JMenu mntmImportFromExcel;
	private JMenuItem mntmMedStock;
	private JMenuItem mntmSplit;
	private JMenuItem mntmMerge;
	private JPanel helpOrDocPane;
	private HelpDocumentPanel panelHelp;
	private JMenuItem mntmGuide;
	private JMenuItem mntmImportTemplate;

	/**
	 * Create the application.
	 */
	public MainWindow() {
		getContentPane().setBackground(Color.WHITE);
		((JComponent) getContentPane()).putClientProperty(
				SubstanceLookAndFeel.COLORIZATION_FACTOR, new Double(1.0));
		setMinimumSize(new Dimension(1279, 550));
		initialize();
	}

	public void setUIFont(javax.swing.plaf.FontUIResource f)
	{   
		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while(keys.hasMoreElements())
		{
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if(value instanceof javax.swing.plaf.FontUIResource) UIManager.put(key, f);
		}
	}


	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		//int res = Toolkit.getDefaultToolkit().getScreenResolution();
		//Float points = (res/72.0f) * 12.0f;
		setUIFont(new javax.swing.plaf.FontUIResource("Arial MS Unicode",Font.PLAIN,12));
		UIManager.put("FormattedTextField.inactiveForeground", Color.BLACK);
		UIManager.put("TextField.margin", new javax.swing.plaf.InsetsUIResource(-2, 0, -2, 0));// letters tails
		setIcone();		
		setSize(new Dimension(1279, 700));
		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		int wdwLeft = screenSize.width / 2 - getWidth() / 2;
		int wdwTop = screenSize.height / 2 - getHeight() / 2;
		setLocation(wdwLeft, wdwTop);
		setVisible(true);
		getContentPane().setLayout(new BorderLayout(0, 0));
		

		JPanel topPanel = new JPanel();
		topPanel.setBackground(Color.WHITE);
		getContentPane().add(topPanel, BorderLayout.NORTH);
		topPanel.setLayout(new BorderLayout(0, 0));

		JPanel subTopPanel = new JPanel();
		subTopPanel.setBackground(Color.WHITE);
		subTopPanel.putClientProperty(
				SubstanceLookAndFeel.COLORIZATION_FACTOR, new Double(1.0));
		topPanel.add(subTopPanel);
		subTopPanel.setLayout(new BorderLayout(0, 0));

		JPanel toolBarContainer = new JPanel();
		toolBarContainer.setBackground(Color.WHITE);
		toolBarContainer.putClientProperty(
				SubstanceLookAndFeel.COLORIZATION_FACTOR, new Double(1.0));
		subTopPanel.add(toolBarContainer, BorderLayout.WEST);
		toolBarContainer.setLayout(new BorderLayout(0, 0));

		tbMainToolBar = new JToolBar();
		tbMainToolBar.setBackground(Color.WHITE);
		tbMainToolBar.putClientProperty(
				SubstanceLookAndFeel.COLORIZATION_FACTOR, new Double(1.0));
		toolBarContainer.add(tbMainToolBar, BorderLayout.NORTH);
		tbMainToolBar.setFloatable(false);

		JButton btnNew = new JButton(); //$NON-NLS-1$		
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.createForecasting();
			}
		});
		btnNew.setIcon(new ImageIcon(MainWindow.class.getResource("/org/msh/quantb/view/images/btnNewDoc.jpg")));
		btnNew.setToolTipText(Messages.getString("MainWindow.menuFile.ItemNew.text")); //$NON-NLS-1$
		tbMainToolBar.add(btnNew);

		JButton btnOpen = new JButton(); //$NON-NLS-1$
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.openForecastingDocument();
			}
		});
		btnOpen.setIcon(new ImageIcon(MainWindow.class.getResource("/org/msh/quantb/view/images/btnOpenDoc.jpg")));
		btnOpen.setToolTipText(Messages.getString("MainWindow.menuFile.ItemOpen.text")); //$NON-NLS-1$
		tbMainToolBar.add(btnOpen);

		btnSave = new JButton();
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.saveActiveForecasting(false);
			}
		});
		btnSave.setEnabled(false);
		btnSave.setIcon(new ImageIcon(MainWindow.class.getResource("/org/msh/quantb/view/images/btnSaveDoc.jpg")));
		btnSave.setToolTipText(Messages.getString("MainWindow.menuFile.ItemSave.text")); //$NON-NLS-1$
		tbMainToolBar.add(btnSave);

		btnExecute = new JButton();
		btnExecute.setEnabled(false);
		btnExecute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.runForecastingCalculation();
			}
		});
		btnExecute.setIcon(new ImageIcon(MainWindow.class.getResource("/org/msh/quantb/view/images/btnExecute.jpg")));
		btnExecute.setToolTipText(Messages.getString("MainWindow.menuForecasting.ItemExecute.text")); //$NON-NLS-1$
		tbMainToolBar.add(btnExecute);

		btnExportToExcel = new JButton();
		btnExportToExcel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				java.awt.EventQueue.invokeLater ( new Runnable() {
					public void run() {
						Presenter.exportToExcel();
					}
				});
			}
		});
		btnExportToExcel.setEnabled(false);
		btnExportToExcel.setIcon(new ImageIcon(MainWindow.class.getResource("/org/msh/quantb/view/images/btnExcel.jpg")));
		btnExportToExcel.setToolTipText(Messages.getString("MainWindow.menuForecasting.ItemExportToExcel.text")); //$NON-NLS-1$
		tbMainToolBar.add(btnExportToExcel);

		JButton btnSelectmedicines = new JButton();
		btnSelectmedicines.setIcon(new ImageIcon(MainWindow.class.getResource("/org/msh/quantb/view/images/btnMedicine.jpg")));
		btnSelectmedicines.setToolTipText(Messages.getString("MainWindow.menuForecasting.ItemSelectMedicines.text")); //$NON-NLS-1$
		tbMainToolBar.add(btnSelectmedicines);
		btnSelectmedicines.setVisible(false); //TODO add to next version or remove

		JButton btnSelectregimens = new JButton();
		btnSelectregimens.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnSelectregimens.setIcon(new ImageIcon(MainWindow.class.getResource("/org/msh/quantb/view/images/btnRegimen.jpg")));
		btnSelectregimens.setToolTipText(Messages.getString("MainWindow.menuForecasting.ItemSelectRegimens.text")); //$NON-NLS-1$
		tbMainToolBar.add(btnSelectregimens);

		btnEnglishLocale = new JButton("");
		btnEnglishLocale.setToolTipText(Messages.getString("Flags.english"));
		btnEnglishLocale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.changeLocale("","");
				refreshFlags();
			}
		});
		btnEnglishLocale.setFocusable(false);
		btnEnglishLocale.setFocusPainted(false);
		btnEnglishLocale.setIcon(new ImageIcon(MainWindow.class.getResource("/org/msh/quantb/view/images/us.png")));
		tbMainToolBar.add(btnEnglishLocale);

		btnRusLocale = new JButton("");
		btnRusLocale.setToolTipText(Messages.getString("Flags.russian"));
		btnRusLocale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.changeLocale("ru","RU");
			}
		});
		btnRusLocale.setFocusable(false);
		btnRusLocale.setFocusPainted(false);
		btnRusLocale.setIcon(new ImageIcon(MainWindow.class.getResource("/org/msh/quantb/view/images/ru.png")));
		tbMainToolBar.add(btnRusLocale);

		btnChLocale = new JButton("");
		btnChLocale.setToolTipText(Messages.getString("Flags.china"));
		btnChLocale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.changeLocale("zh","CN");
				refreshFlags();
			}
		});
		btnChLocale.setFocusable(false);
		btnChLocale.setFocusPainted(false);
		btnChLocale.setIcon(new ImageIcon(MainWindow.class.getResource("/org/msh/quantb/view/images/cn.png")));
		tbMainToolBar.add(btnChLocale);

		btnFRLocale = new JButton("");
		btnFRLocale.setToolTipText(Messages.getString("Flags.french"));
		btnFRLocale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.changeLocale("fr","FR");
				refreshFlags();
			}
		});
		btnFRLocale.setFocusable(false);
		btnFRLocale.setFocusPainted(false);
		btnFRLocale.setIcon(new ImageIcon(MainWindow.class.getResource("/org/msh/quantb/view/images/fr.png")));
		tbMainToolBar.add(btnFRLocale);

		btnESLocale = new JButton("");
		btnESLocale.setToolTipText(Messages.getString("Flags.spain"));
		btnESLocale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.changeLocale("es","ES");
				refreshFlags();
			}
		});
		btnESLocale.setFocusable(false);
		btnESLocale.setFocusPainted(false);
		btnESLocale.setIcon(new ImageIcon(MainWindow.class.getResource("/org/msh/quantb/view/images/es.png")));
		tbMainToolBar.add(btnESLocale);


		btnPTLocale = new JButton();
		btnPTLocale.setToolTipText(Messages.getString("Flags.porto"));
		btnPTLocale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.changeLocale("pt","PT");
				refreshFlags();
			}
		});
		btnPTLocale.setFocusable(false);
		btnPTLocale.setFocusPainted(false);
		btnPTLocale.setIcon(new ImageIcon(MainWindow.class.getResource("/org/msh/quantb/view/images/pt.png")));
		tbMainToolBar.add(btnPTLocale);

		refreshFlags();


		btnSelectregimens.setVisible(false); //TODO add to next version or remove


		JLabel usaidLogo = new JLabel();
		usaidLogo.setBackground(Color.WHITE);
		usaidLogo.setForeground(Color.WHITE);
		usaidLogo.putClientProperty(
				SubstanceLookAndFeel.COLORIZATION_FACTOR, new Double(1.0));

		subTopPanel.add(usaidLogo, BorderLayout.EAST);
		usaidLogo.setHorizontalAlignment(SwingConstants.RIGHT);
		usaidLogo.setIcon(new ImageIcon(MainWindow.class.getResource("/org/msh/quantb/view/images/USAIDSIAPSminweb.png")));

		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.putClientProperty(
				SubstanceLookAndFeel.COLORIZATION_FACTOR, new Double(1.0));

		subTopPanel.add(panel, BorderLayout.CENTER);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		//textArea = new JTextArea();
		//textArea.setText(Messages.getString("MainWindow.textArea.text")); //$NON-NLS-1$
		//panel.add(textArea);

		JLabel versionLabel = new JLabel(""); //$NON-NLS-1$ //$NON-NLS-1$
		versionLabel.setIcon(new ImageIcon(MainWindow.class.getResource("/org/msh/quantb/view/images/headerQT41.png")));
		//versionLabel.setText("VER 4 PROTOTYPE. For internal MSH use only!");
		versionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(versionLabel);

		/*		JComboBox selectSkin = new SubstanceSkinComboSelector();
		selectSkin.setBounds(10, 11, 247, 20);
		panel.add(selectSkin);*/

		menuBar = new JMenuBar();
		topPanel.add(menuBar, BorderLayout.NORTH);

		JMenu mnFile = new JMenu(Messages.getString("MainWindow.menuFile.text"));
		menuBar.add(mnFile);

		JMenuItem mntmNew = new JMenuItem(Messages.getString("MainWindow.menuFile.ItemNew.text"));
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.createForecasting();
			}
		});
		mnFile.add(mntmNew);

		JMenuItem mntmOpen = new JMenuItem(Messages.getString("MainWindow.menuFile.ItemOpen.text"));
		mntmOpen.setEnabled(true);
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.openForecastingDocument();
			}
		});
		mnFile.add(mntmOpen);

		mntmSave = new JMenuItem();
		mntmSave.setEnabled(false);
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.saveActiveForecasting(false);
			}
		});
		mntmSave.setText(Messages.getString("MainWindow.menuFile.ItemSave.text")); //$NON-NLS-1$
		mnFile.add(mntmSave);

		mntmSaveAs = new JMenuItem();
		mntmSaveAs.setEnabled(false);
		mntmSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.saveActiveForecasting(true);
			}
		});
		mntmSaveAs.setText(Messages.getString("MainWindow.menuFile.ItemSaveAs.text")); //$NON-NLS-1$
		mnFile.add(mntmSaveAs);

		mntmClose = new JMenuItem();
		mntmClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.closeForecastingDocument(null);
			}
		});
		mntmClose.setEnabled(false);
		mntmClose.setText(Messages.getString("MainWindow.menuFile.ItemClose.text")); //$NON-NLS-1$
		mnFile.add(mntmClose);
		
		mnFile.addSeparator();

		menuLastDocs = new JMenuItem();
		menuLastDocs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.last5FilesPropose();
			}
		});
		menuLastDocs.setEnabled(Presenter.isLast5Enabled());
		menuLastDocs.setText(Messages.getString("MainWindow.menuFile.ItemLastDocs.text")); //$NON-NLS-1$
		mnFile.add(menuLastDocs);


		mnFile.addSeparator();

		JMenuItem mntmExit = new JMenuItem();
		mntmExit.setText(Messages.getString("MainWindow.menuFile.ItemExit.text")); //$NON-NLS-1$
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Presenter.askStop();
			}
		});
		mnFile.add(mntmExit);

		JMenu mnMedReg = new JMenu(Messages.getString("MainWindow.menuMedReg"));
		menuBar.add(mnMedReg);

		JMenuItem mntmMedicines = new JMenuItem();
		mntmMedicines.setText(Messages.getString("MainWindow.menuMedReg.editMedicines")); //$NON-NLS-1$
		mntmMedicines.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Presenter.showMedicineListDialog(null);
			}
		});
		mnMedReg.add(mntmMedicines);

		JMenuItem mntmRegimens = new JMenuItem();
		mntmRegimens.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.showRegimensDialog(RegimenTypesEnum.MULTI_DRUG);
			}
		});
		mntmRegimens.setText(Messages.getString("MainWindow.menuMedReg.editRegims")); //$NON-NLS-1$
		mnMedReg.add(mntmRegimens);

		mntmExportToDic = new JMenuItem();
		mntmExportToDic.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Presenter.exportFcToDic();
			}
		});
		mntmExportToDic.setEnabled(false);
		mntmExportToDic.setText(Messages.getString("MainWindow.menuMedReg.addFromFC")); //$NON-NLS-1$
		mnMedReg.add(mntmExportToDic);
		
		JMenu mnImportExport = new JMenu(Messages.getString("MainWindow.menuImportExport"));
		menuBar.add(mnImportExport);
		


		mntmImportFromExcel = new JMenu(Messages.getString("MainWindow.menuForecasting.ItemImportFromExcel"));
		mntmImportFromExcel.setEnabled(false);
		mnImportExport.add(mntmImportFromExcel);
		
		mntmImportTemplate = new JMenuItem(Messages.getString("MainWindow.menuForecasting.ItemImportFromExcel.Template"));
		mntmImportTemplate.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Presenter.createImportTemplate();
			}
		});
		mntmImportFromExcel.add(mntmImportTemplate);

		mntmMedStock = new JMenuItem(Messages.getString("MainWindow.menuForecasting.ItemImportFromExcel.MedStock"));
		mntmMedStock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.importMedStock();
			}
		});
		mntmMedStock.setEnabled(false);
		mntmImportFromExcel.add(mntmMedStock);

		mntmExcelWHO = new JMenuItem();
		mntmExcelWHO.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				java.awt.EventQueue.invokeLater ( new Runnable() {
					public void run() {
						Presenter.exportToExcelWHO();
					}
				});
			}
		});
		
		mntmExportToExcel = new JMenuItem();
		mntmExportToExcel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				java.awt.EventQueue.invokeLater ( new Runnable() {
					public void run() {
						Presenter.exportToExcel();
					}
				});
			}
		});
		mntmExportToExcel.setEnabled(false);
		mntmExportToExcel.setText(Messages.getString("MainWindow.menuForecasting.ItemExportToExcel.text"));
		mnImportExport.add(mntmExportToExcel);
		
		
		mntmExcelWHO.setEnabled(false);
		mntmExcelWHO.setText(Messages.getString("MainWindow.menuForecasting.ItemExportToExcelWHO.text"));
		mnImportExport.add(mntmExcelWHO);
		mntmExcelWHO.setVisible(false); //TODO wait for Ver 3!
		
		JMenu mnDivMerge = new JMenu(Messages.getString("MainWindow.mnDivMerge"));
		menuBar.add(mnDivMerge);

		mntmSplit = new JMenuItem();
		mntmSplit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				java.awt.EventQueue.invokeLater ( new Runnable() {
					public void run() {
						Presenter.sliceCurrentFC();
					}
				});
			}
		});
		mntmSplit.setEnabled(false);
		mntmSplit.setText(Messages.getString("MainWindow.mnDivMerge.divide"));
		mnDivMerge.add(mntmSplit);

		mntmMerge = new JMenuItem();
		mntmMerge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				java.awt.EventQueue.invokeLater ( new Runnable() {
					public void run() {
						Presenter.mergeAskFC();
					}
				});
			}
		});
		mntmMerge.setEnabled(false);
		mntmMerge.setText(Messages.getString("MainWindow.mnDivMerge.merge"));
		mnDivMerge.add(mntmMerge);


		JMenu helpMenu = new JMenu(Messages.getString("MainWindow.menuHelp.text"));
		menuBar.add(helpMenu);		
		mntmGuide = new JMenuItem(Messages.getString("MainWindow.menuHelp.itemGuide")); //$NON-NLS-1$
		mntmGuide.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				URL url;
				try {
					url = new URL(Messages.getString("MainWindow.url.userguide"));
					//HelpDocumentPanel.showUserGuide(url);
					Presenter.showUserGuide();
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		helpMenu.add(mntmGuide);
		JMenuItem mntmAbout = new JMenuItem();
		helpMenu.add(mntmAbout);
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.showAboutDialog();
				//Presenter.showWrongVersionDialog();
			}
		});
		mntmAbout.setText(Messages.getString("MainWindow.menuFile.ItemAbout.text"));
		

		JPanel panelMain = new JPanel();
		getContentPane().add(panelMain, BorderLayout.CENTER);
		panelMain.setBackground(Color.WHITE);
		panelMain.putClientProperty(
				SubstanceLookAndFeel.COLORIZATION_FACTOR, new Double(1.0));
		panelMain.setLayout(new BorderLayout(0, 0));

		helpOrDocPane = new JPanel();
		panelMain.add(helpOrDocPane, BorderLayout.CENTER);
		helpOrDocPane.setLayout(new CardLayout(0, 0));

		panelDocuments = new JTabbedPane(JTabbedPane.TOP);
		helpOrDocPane.add(panelDocuments, SHOW_DOCUMENTS);
		panelDocuments.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		panelDocuments.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				refreshElementsActivity();
				ForecastingDocumentPanel selected = (ForecastingDocumentPanel) panelDocuments.getSelectedComponent();
				Presenter.setActiveForecastingDocument(selected);
				if(selected != null){
					CardLayout cl = (CardLayout) helpOrDocPane.getLayout();
					cl.show(helpOrDocPane,SHOW_DOCUMENTS);
				}else{
					CardLayout cl = (CardLayout) helpOrDocPane.getLayout();
					cl.show(helpOrDocPane,SHOW_HELP);
				}
			}
		});

		panelHelp = new HelpDocumentPanel();
		helpOrDocPane.add(panelHelp, SHOW_HELP);

		CardLayout cl = (CardLayout) helpOrDocPane.getLayout();
		cl.show(helpOrDocPane,SHOW_HELP);

		tbStatusBar = new JPanel();
		tbStatusBar.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		tbStatusBar.setBackground(menuBar.getBackground());
		tbStatusBar.putClientProperty(
				SubstanceLookAndFeel.COLORIZATION_FACTOR, new Double(1.0));
		getContentPane().add(tbStatusBar, BorderLayout.SOUTH);

		JLabel txtStatusBar = new JLabel();
		txtStatusBar.setHorizontalAlignment(SwingConstants.CENTER);
		txtStatusBar.setToolTipText(Messages.getString("MainWindow.txtStatusBar.text_1")); //$NON-NLS-1$
		txtStatusBar.setText(Messages.getString("MainWindow.txtStatusBar.text_1")); //$NON-NLS-1$
		tbStatusBar.add(txtStatusBar);
		setTitle(Messages.getString("MainWindow.Title.text")); //$NON-NLS-1$
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Presenter.askStop();
			}
		});
	}


	/**
	 * Set enable - disable flags on the toolbar
	 */
	private void refreshFlags() {
		String lang = Messages.getLanguage();
		btnEnglishLocale.setEnabled(!lang.isEmpty());
		btnRusLocale.setEnabled(!lang.equalsIgnoreCase("RU"));
		btnChLocale.setEnabled(!lang.equalsIgnoreCase("ZH"));
		btnFRLocale.setEnabled(!lang.equalsIgnoreCase("FR"));
		btnESLocale.setEnabled(!lang.equalsIgnoreCase("ES"));
		btnPTLocale.setEnabled(!lang.equalsIgnoreCase("PT"));
	}

	/**
	 * set window icon
	 */
	private void setIcone() {
		java.net.URL url = ClassLoader.getSystemResource("org/msh/quantb/view/images/green_lungs.png");
		Toolkit kit = Toolkit.getDefaultToolkit();
		Image img = kit.createImage(url);
		this.setIconImage(img);

	}

	/**
	 * Add new tab in forecasting tabPanel with document of forecasting
	 * 
	 * @param forecastPanel
	 *            panel of document of forecasting
	 * @param tabName
	 *            name of the tab
	 * @param fileName name of the file (for tooltip)
	 */
	public void addForecastingTab(ForecastingDocumentPanel forecastPanel, String tabName, String fileName) {
		panelDocuments.addTab(tabName, forecastPanel);
		panelDocuments.setSelectedComponent(forecastPanel);
		int index = panelDocuments.indexOfTab(tabName);
		panelDocuments.setToolTipTextAt(index, fileName);
		panelDocuments.setTabComponentAt(index, new TabPaneClosed(forecastPanel,tabName));		
	}

	/**
	 * Close Forecasting tab
	 */
	public void closeActiveForecastingTab(ForecastingDocumentPanel panel) {
		panelDocuments.remove(panel);
	}

	/**
	 * Set title for selected tab.
	 * 
	 * @param title
	 *            new title
	 */
	public void setTitleAt(String title) {
		if (title != null && panelDocuments.getSelectedIndex() != -1) {
			ForecastingDocumentPanel selected = (ForecastingDocumentPanel) panelDocuments.getSelectedComponent();
			panelDocuments.setTitleAt(panelDocuments.getSelectedIndex(), title);
			panelDocuments.setToolTipTextAt(panelDocuments.getSelectedIndex(), title);
			panelDocuments.setTabComponentAt(panelDocuments.getSelectedIndex(), new TabPaneClosed(selected,title));	
		}
	}

	/**
	 * Refresh activity of control elements
	 */
	private void refreshElementsActivity() {
		boolean isExistDocument = panelDocuments.getTabCount() > 0;
		mntmSave.setEnabled(isExistDocument);
		mntmSaveAs.setEnabled(isExistDocument);
		btnSave.setEnabled(isExistDocument);
		btnExecute.setEnabled(isExistDocument);
		mntmExportToDic.setEnabled(isExistDocument);
		btnExportToExcel.setEnabled(isExistDocument);
		mntmExportToExcel.setEnabled(isExistDocument);
		mntmImportFromExcel.setEnabled(isExistDocument);
		mntmMedStock.setEnabled(isExistDocument);
		mntmExcelWHO.setEnabled(isExistDocument);
		mntmSplit.setEnabled(isExistDocument);
		mntmMerge.setEnabled(panelDocuments.getTabCount() > 1);
		mntmClose.setEnabled(isExistDocument);
		menuLastDocs.setEnabled(Presenter.isLast5Enabled());
	}
	/**
	 * return true, if forecasting tab already opened
	 * @param realTabName name on tab
	 * @return
	 */
	public boolean isForecastingOpen(String realTabName) {
		int index = panelDocuments.indexOfTab(realTabName);
		return index > -1;
	}
	/**
	 * set as active forecasting with tab name given
	 * @param realTabName
	 */
	public void setForecasting(String realTabName) {
		int index = panelDocuments.indexOfTab(realTabName);
		if (index>-1){
			panelDocuments.setSelectedIndex(index);
		}

	}

	/**
	 * @return the panelDocuments
	 */
	public JTabbedPane getPanelDocuments() {
		return panelDocuments;
	}
	/**
	 * Get status bar
	 * @return
	 */
	public JComponent getTbStatusBar() {
		return this.tbStatusBar;
	}



}
