package org.msh.quantb.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.msh.quantb.services.excel.ImportExcel;
import org.msh.quantb.services.io.ForecastingMedicineUIAdapter;
import org.msh.quantb.services.io.MedicinesDecoder;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.ToolTipCellRenderer;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;
/**
 * General dialog to establish link between a forecasting medicines and imported medicines
 * Use for import medicines stock 
 * @author Alexey Kurasov
 *
 */
public class MedicinesDecodeDlg extends JDialog {
	
	private static final long serialVersionUID = -3866763563572740837L;
	private JTable table;
	private List<MedicinesDecoder> decoder = new ArrayList<MedicinesDecoder>();
	private DefaultComboBoxModel<String> comboModel = null;
	private ImportExcel result;
	
	/**
	 * Create the "establish link" dialog
	 * @param _fcMedicine medicines in the forecasting
	 * @param ie medicines exported, also acted as result
	 * @param owner main frame
	 */
	public MedicinesDecodeDlg(List<ForecastingMedicineUIAdapter> _fcMedicine, ImportExcel ie, Frame owner) {
		super(owner);
		
		setTitle(Messages.getString("Application.importExcel.title"));
		initDialog();
		
		BorderLayout borderLayout = (BorderLayout) getContentPane().getLayout();
		borderLayout.setVgap(5);
		borderLayout.setHgap(5);
		this.result = ie;
		initDecoder(_fcMedicine);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		
		table = new JTable();
		table.setBounds(0, 0, 1, 1);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		panel.setLayout(new BorderLayout(0, 0));
		
		
		JScrollPane scrollPane = new JScrollPane(table);
		panel.add(scrollPane, BorderLayout.CENTER);
		
		JPanel btnPanel = new JPanel();
		panel.add(btnPanel, BorderLayout.SOUTH);
		btnPanel.setLayout(new BorderLayout(5, 5));
		
		JButton saveButton = new JButton(Messages.getString("DlgEditMedicine.btnSave.text"));
		btnPanel.add(saveButton, BorderLayout.WEST);
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.setBatchesFromExcel(getResult(), getDecoder());
				setVisible(false);
				dispose();
			}
		});
		
		JButton cancelButton = new JButton(Messages.getString("DlgConfirm.cancelButton"));
		btnPanel.add(cancelButton, BorderLayout.EAST);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		initDataBindings();
		adjustTable();
	}
	/**
	 * Assign column names and renderer for the second column
	 */
	private void adjustTable() {
		this.getTable().getColumnModel().getColumn(0).setHeaderValue(Messages.getString("Application.importExcel.dlg.col0"));
		this.getTable().getColumnModel().getColumn(0).setCellRenderer(new ToolTipCellRenderer());
		this.getTable().getColumnModel().getColumn(0).setPreferredWidth(120);
		this.getTable().getColumnModel().getColumn(1).setHeaderValue(Messages.getString("Application.importExcel.dlg.col1"));
		JComboBox<String> combo=new JComboBox<String>(getComboModel());
		combo.setEditable(true);
		this.getTable().getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(combo));
		this.getTable().getColumnModel().getColumn(1).setPreferredWidth(120);
	}

	/**
	 * Initialize dimension and modality of the dialog and position at center of
	 * the screen.
	 */
	private void initDialog() {
		setSize(new Dimension(676, 302));
		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		int wdwLeft = screenSize.width / 2 - getWidth() / 2;
		int wdwTop = screenSize.height / 2 - getHeight() / 2;
		setLocation(wdwLeft, wdwTop);
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
	}
	/**
	 * Init the decoder list from the forecasting medicines
	 * Also, prepare the exported medicines model
	 * @param _fcMedicine
	 */
	private void initDecoder(List<ForecastingMedicineUIAdapter> _fcMedicine) {
		for(ForecastingMedicineUIAdapter fMuI : _fcMedicine){
			getDecoder().add(new MedicinesDecoder(fMuI));
		}
		int i = 1;
		setComboModel(new DefaultComboBoxModel<String>());
		getComboModel().addElement("");
		for(String eMed : result.getUniqueMeds()){
			getComboModel().addElement(eMed);
			i++;
		}
	}
	
	

	public DefaultComboBoxModel<String> getComboModel() {
		return comboModel;
	}
	public void setComboModel(DefaultComboBoxModel<String> defaultComboBoxModel) {
		this.comboModel = defaultComboBoxModel;
	}
	public ImportExcel getResult() {
		return result;
	}
	public void setResult(ImportExcel result) {
		this.result = result;
	}
	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		this.table = table;
	}

	public List<MedicinesDecoder> getDecoder() {
		return decoder;
	}

	public void setDecoder(List<MedicinesDecoder> decoder) {
		this.decoder = decoder;
	}
	protected void initDataBindings() {
		JTableBinding<MedicinesDecoder, List<MedicinesDecoder>, JTable> jTableBinding = SwingBindings.createJTableBinding(UpdateStrategy.READ, decoder, table);
		//
		BeanProperty<MedicinesDecoder, String> decoderBeanProperty = BeanProperty.create("medicineQ.medicine.nameForDisplay");
		jTableBinding.addColumnBinding(decoderBeanProperty).setColumnName("New Column");
		//
		BeanProperty<MedicinesDecoder, String> decoderBeanProperty_1 = BeanProperty.create("medicineE");
		jTableBinding.addColumnBinding(decoderBeanProperty_1).setColumnName("New Column");
		//
		jTableBinding.bind();
	}
}
