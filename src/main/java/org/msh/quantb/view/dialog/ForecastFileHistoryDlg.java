package org.msh.quantb.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.ObjectProperty;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.msh.quantb.services.io.ForecastFileUI;
import org.msh.quantb.services.io.ForecastLast5UI;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.tableExt.DivMultiLineCellRenderer;

/**
 * Allow to pick up one or more files from the stored history
 * @author alexey
 *
 */
public class ForecastFileHistoryDlg extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2119605745527252561L;
	private ForecastLast5UI last5;
	private JTable table;
	private JButton btnOK;
	/**
	 * Only valid constructor
	 * @param _last5 last 5 opened forecasts
	 * @param mainWindow main frame
	 */
	public ForecastFileHistoryDlg(ForecastLast5UI _last5, JFrame mainWindow) {
		super(mainWindow);
		setTitle(Messages.getString("DlgLast5.title")); 
		this.last5 = _last5;
		BorderLayout borderLayout = (BorderLayout) getContentPane().getLayout();
		borderLayout.setVgap(5);
		borderLayout.setHgap(5);

		JPanel headerPnl = new JPanel();
		headerPnl.setPreferredSize(new Dimension(10, 40));
		getContentPane().add(headerPnl, BorderLayout.NORTH);
		headerPnl.setLayout(null);

		JLabel lblNewLabel = new JLabel(Messages.getString("DlgLast5.prompt"));
		lblNewLabel.setBounds(10, 11, 355, 14);
		headerPnl.add(lblNewLabel);

		JPanel footerPnl = new JPanel();
		footerPnl.setPreferredSize(new Dimension(10, 40));
		getContentPane().add(footerPnl, BorderLayout.SOUTH);
		footerPnl.setLayout(null);

		JButton btnCancel = new JButton(Messages.getString("DlgLast5.btnCancel"));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnCancel.setBounds(442, 11, 89, 23);
		footerPnl.add(btnCancel);

		btnOK = new JButton(Messages.getString("DlgLast5.btnOK"));
		btnOK.setEnabled(false);
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				for(ForecastFileUI ffUi : last5.getForecastFiles()){
					if(ffUi.getChecked()){
						File file = new File(ffUi.getPath(), ffUi.getName());
						Presenter.openForecastingDocument(file);
					}
				}
				dispose();
			}
		});
		btnOK.setBounds(10, 11, 89, 23);
		footerPnl.add(btnOK);

		JPanel contentPnl = new JPanel();
		contentPnl.setPreferredSize(new Dimension(10, 100));
		contentPnl.setSize(new Dimension(0, 100));
		contentPnl.setMinimumSize(new Dimension(10, 100));
		getContentPane().add(contentPnl, BorderLayout.CENTER);
		contentPnl.setLayout(new BorderLayout(20, 20));
		
				JScrollPane scrollPane = new JScrollPane();
				contentPnl.add(scrollPane);
				
						table = new JTable();
						table.setBounds(0, 0, 1, 1);
						scrollPane.setViewportView(table);

		initDataBindings();
		adjustDlg();
		adjustTable();
		setListeners();
	}
	/**
	 * Set listener to change selection
	 */
	private void setListeners() {
		for(ForecastFileUI ffUi : last5.getForecastFiles()){
			ffUi.addPropertyChangeListener("checked", new PropertyChangeListener(){
				@Override
				public void propertyChange(PropertyChangeEvent arg0) {
					adjustOKButton();
				}
			});
		}
	}
	/**
	 * Show - hide OK button
	 */
	protected void adjustOKButton() {
		boolean enabled = false;
		for(ForecastFileUI ffUi : last5.getForecastFiles()){
			if(ffUi.getChecked()){
				enabled = true;
				break;
			}
		}
		btnOK.setEnabled(enabled);
	}
	/**
	 * Adjust table appearance
	 */
	private void adjustTable() {
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.setRowSelectionAllowed(false);
		table.setRowHeight(50);
		table.getColumnModel().getColumn(0).setPreferredWidth(45);
		table.getColumnModel().getColumn(0).setHeaderValue("");
		table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
		table.getColumnModel().getColumn(1).setPreferredWidth(400);
		table.getColumnModel().getColumn(1).setHeaderValue(Messages.getString("DlgLast5.name"));
		table.getColumnModel().getColumn(1).setCellRenderer(new DivMultiLineCellRenderer(JLabel.LEFT,false));


	}
	/**
	 * Adjust the dialog box appearance
	 */
	private void adjustDlg() {
		setResizable(false);
		setSize(new Dimension(549, 390));
		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		int wdwLeft = screenSize.width / 2 - getWidth() / 2;
		int wdwTop = screenSize.height / 2 - getHeight() / 2;
		setLocation(wdwLeft, wdwTop);
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
	protected void initDataBindings() {
		BeanProperty<ForecastLast5UI, List<ForecastFileUI>> forecastLast5UIBeanProperty = BeanProperty.create("forecastFiles");
		JTableBinding<ForecastFileUI, ForecastLast5UI, JTable> jTableBinding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE, last5, forecastLast5UIBeanProperty, table);
		//
		BeanProperty<ForecastFileUI, Boolean> forecastFileUIBeanProperty = BeanProperty.create("checked");
		jTableBinding.addColumnBinding(forecastFileUIBeanProperty).setColumnName("check").setColumnClass(Boolean.class);
		//
		ObjectProperty<ForecastFileUI> forecastFileUIObjectProperty = ObjectProperty.create();
		jTableBinding.addColumnBinding(forecastFileUIObjectProperty).setColumnName("name").setEditable(false);
		//
		jTableBinding.bind();
	}
}
