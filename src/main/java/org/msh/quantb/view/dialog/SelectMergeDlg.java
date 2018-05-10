package org.msh.quantb.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.msh.quantb.services.io.ForecastUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;

public class SelectMergeDlg extends JDialog {
	private static final long serialVersionUID = -2933762496242052735L;
	private JTable table;
	private List<ForecastUIAdapter> fcList;
	
	public SelectMergeDlg(List<ForecastUIAdapter> fcList, JFrame mainWindow){
		super(mainWindow);
		this.fcList = fcList;
		BorderLayout borderLayout = (BorderLayout) getContentPane().getLayout();
		borderLayout.setVgap(5);
		borderLayout.setHgap(5);
		setTitle(Messages.getString("Forecasting.merge.dlgTitle"));
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(10, 40));
		getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(null);
		
		JButton okButton = new JButton(Messages.getString("DlgConfirm.okButton"));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.mergeChecked(getFcList());
				dispose();
			}
		});
		okButton.setPreferredSize(new Dimension(89, 23));
		okButton.setBounds(10, 11, 89, 23);
		panel.add(okButton);
		
		JButton cancelButton = new JButton(Messages.getString("DlgConfirm.cancelButton"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 dispose();
			}
		});
		cancelButton.setBounds(444, 11, 89, 23);
		panel.add(cancelButton);
		
		adjustDlg();
		initDataBindings();
		adjustTable();
	}

	
	public List<ForecastUIAdapter> getFcList() {
		return fcList;
	}


	public void setFcList(List<ForecastUIAdapter> fcList) {
		this.fcList = fcList;
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
	
	/**
	 * Adjust table appearance
	 */
	private void adjustTable() {
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.setRowSelectionAllowed(false);
		table.setRowHeight(20);
		table.getColumnModel().getColumn(0).setPreferredWidth(45);
		table.getColumnModel().getColumn(0).setHeaderValue("");
		table.getColumnModel().getColumn(1).setPreferredWidth(400);
		table.getColumnModel().getColumn(1).setHeaderValue(Messages.getString("Application.forecast"));


	}
	protected void initDataBindings() {
		JTableBinding<ForecastUIAdapter, List<ForecastUIAdapter>, JTable> jTableBinding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE, fcList, table);
		//
		BeanProperty<ForecastUIAdapter, Boolean> forecastUIAdapterBeanProperty = BeanProperty.create("checked");
		jTableBinding.addColumnBinding(forecastUIAdapterBeanProperty).setColumnName("New Column").setColumnClass(Boolean.class);
		//
		BeanProperty<ForecastUIAdapter, String> forecastUIAdapterBeanProperty_1 = BeanProperty.create("name");
		jTableBinding.addColumnBinding(forecastUIAdapterBeanProperty_1).setColumnName("New Column").setEditable(false);
		//
		jTableBinding.bind();
	}
}
