package org.msh.quantb.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.msh.quantb.services.io.AbstractUIAdapter;
import org.msh.quantb.services.io.ForecastingMedicineUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.CellRendererColor;
import org.msh.quantb.view.PercentageCellEditor;
import org.msh.quantb.view.ToolTipCellRenderer;
import org.msh.quantb.view.tableExt.DivMultiLineHeaderRenderer;

/**
 * New medicine dialog.
 * 
 * @author User
 * 
 */
public class MedicinesAdjustDlg extends JDialog {
	/**
	 * Buffer to have possibility save-cancel
	 */
	private class MedAdjBuffer extends AbstractUIAdapter{
		private String name;
		private BigDecimal enrolled;
		private BigDecimal expected;
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @return the quantEnrolled
		 */
		public BigDecimal getEnrolled() {
			return enrolled;
		}
		/**
		 * @param quantEnrolled the quantEnrolled to set
		 */
		public void setEnrolled(BigDecimal quantEnrolled) {
			if (quantEnrolled == null){
				quantEnrolled = new BigDecimal(100);
			}else{
				if (quantEnrolled.compareTo(new BigDecimal(100))>0){
					quantEnrolled = new BigDecimal(100);
				}
			}
			BigDecimal oldValue = this.getEnrolled();
			this.enrolled = quantEnrolled;
			firePropertyChange("enrolled", oldValue, this.getEnrolled());
		}
		/**
		 * @return the quantExpected
		 */
		public BigDecimal getExpected() {
			return expected;
		}
		/**
		 * @param quantExpected the quantExpected to set
		 */
		public void setExpected(BigDecimal quantExpected) {
			if (quantExpected == null){
				quantExpected = new BigDecimal(100);
			}else{
				if (quantExpected.compareTo(new BigDecimal(100))>0){
					quantExpected = new BigDecimal(100);
				}
			}
			BigDecimal oldValue = this.getExpected();
			this.expected = quantExpected;
			firePropertyChange("expected", oldValue, this.getExpected());
		}

	}
	/**
	 * It's data model for adjustment coefficient table
	 * @author alexey
	 *
	 */
	private class TableModel extends AbstractTableModel{

		/**
		 * 
		 */
		private static final long serialVersionUID = -7593746151980523917L;
		private List<MedAdjBuffer> buffer;

		public TableModel(List<MedAdjBuffer> _buffer){
			this.buffer = _buffer;
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		public String getColumnName(int col) {
			switch (col){
			case 0:
				return Messages.getString("MedicinesAdjustDlg.columns.medicine");
			case 1:
				return Messages.getString("MedicinesAdjustDlg.columns.enrolled");
			case 2:
				return Messages.getString("MedicinesAdjustDlg.columns.expected");
			default:
				return "";
			}
		}


		@Override
		public int getRowCount() {
			return buffer.size();
		}

		@Override
		public Object getValueAt(int arg0, int arg1) {
			MedAdjBuffer buf = this.buffer.get(arg0);
			switch(arg1){
			case 0:
				return buf.getName();
			case 1:
				return buf.getEnrolled();
			case 2:
				return buf.getExpected();
			default:
				return "";

			}
		}

		public boolean isCellEditable(int row, int col) {
			if (col == 0) {
				return false;
			} else {
				return true;
			}
		}
		/**
		 * Only firs and second column will generate a change event
		 */
		public void setValueAt(Object value, int row, int col) {
			MedAdjBuffer buf = this.buffer.get(row);
			switch(col){
			case 1:
				buf.setEnrolled((BigDecimal) value);
				break;
			case 2:
				buf.setExpected((BigDecimal) value);
			default:
				return;
			}
			fireTableCellUpdated(row, col);
		}



	}

	private static final long serialVersionUID = -7120876886940395626L;
	private final JPanel contentPanel = new JPanel();
	private List<ForecastingMedicineUIAdapter> medicines;
	private List<MedAdjBuffer> buffer = new ArrayList<MedAdjBuffer>();
	private JTable table;
	private TableModel model;
	private JButton saveBtn;


	/**
	 * Create the dialog.
	 */
	public MedicinesAdjustDlg(List<ForecastingMedicineUIAdapter> _fcMedicine, Frame owner) {
		super(owner);
		this.medicines = _fcMedicine;
		setTitle(Messages.getString("MedicinesAdjustDlg.title"));
		initDialog();
		
		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setVgap(5);
		borderLayout.setHgap(5);
		getContentPane().setLayout(borderLayout);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		table = new JTable();
		table.setBounds(0, 0, 1, 1);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		

		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		scrollPane.setBounds(10, 11, 432, 224);
		contentPanel.add(scrollPane);

		JPanel buttonPnl = new JPanel();
		getContentPane().add(buttonPnl, BorderLayout.SOUTH);
		buttonPnl.setLayout(new BorderLayout(5, 5));

		JButton cancelBtn = new JButton(Messages.getString("DlgConfirm.cancelButton"));
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		buttonPnl.add(cancelBtn, BorderLayout.EAST);

		saveBtn = new JButton(Messages.getString("DlgEditMedicine.btnSave.text")); //$NON-NLS-1$
		saveBtn.setEnabled(false);
		saveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//copy adjustment percents
				for(int i=0; i<getBuffer().size(); i++){
					getMedicines().get(i).setAdjustmentEnrolled(getBuffer().get(i).getEnrolled());
					getMedicines().get(i).setAdjustmentExpected(getBuffer().get(i).getExpected());
				}
				setVisible(false);
				dispose();
				Presenter.getView().getActiveForecastingPanel().setVisibleCalculationDetailsTabs(false);
			}
		});
		buttonPnl.add(saveBtn, BorderLayout.WEST);
		initModel();

	}

	/**
	 * @return the medicines
	 */
	public List<ForecastingMedicineUIAdapter> getMedicines() {
		return medicines;
	}


	/**
	 * @return the buffer
	 */
	public List<MedAdjBuffer> getBuffer() {
		return buffer;
	}
	
	

	/**
	 * @return the table
	 */
	public JTable getTable() {
		return table;
	}

	/**
	 * @return the model
	 */
	public TableModel getModel() {
		return model;
	}

	/**
	 * Init data for edit
	 */
	private void initModel() {
		//create the buffer
		for(ForecastingMedicineUIAdapter fMu : this.getMedicines()){
			MedAdjBuffer el = new MedAdjBuffer();
			el.setName(fMu.getMedicine().getNameForDisplay());
			el.setEnrolled(fMu.getAdjustmentEnrolled());
			el.setExpected(fMu.getAdjustmentExpected());
			el.addPropertyChangeListener("enrolled", new PropertyChangeListener() {
				
				@Override
				public void propertyChange(PropertyChangeEvent arg0) {
					saveBtn.setEnabled(true);
					
				}
			});
			el.addPropertyChangeListener("expected", new PropertyChangeListener() {
				
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					saveBtn.setEnabled(true);
					
				}
			});
			this.getBuffer().add(el);
		}
		//init the model
		this.model = new TableModel(this.getBuffer());
		this.getTable().setModel(getModel());
		this.getTable().getColumnModel().getColumn(0).setPreferredWidth(200);
		this.getTable().getColumnModel().getColumn(0).setCellRenderer(new ToolTipCellRenderer());
		this.getTable().getColumnModel().getColumn(1).setCellEditor(new PercentageCellEditor(new JTextField(), true));
		this.getTable().getColumnModel().getColumn(1).setCellRenderer(new CellRendererColor(Color.WHITE));
		this.getTable().getColumnModel().getColumn(2).setCellEditor(new PercentageCellEditor(new JTextField(), true));
		this.getTable().getColumnModel().getColumn(2).setCellRenderer(new CellRendererColor(Color.WHITE));
		DivMultiLineHeaderRenderer renderer = new DivMultiLineHeaderRenderer();
		Enumeration<TableColumn> en = table.getColumnModel().getColumns();
		while (en.hasMoreElements()) {
			((TableColumn) en.nextElement()).setHeaderRenderer(renderer);
		}
		this.getTable().repaint();

	}

	/**
	 * Initialize dimension and modality of the dialog and position at center of
	 * the screen.
	 */
	private void initDialog() {
		setSize(new Dimension(458, 302));
		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		int wdwLeft = screenSize.width / 2 - getWidth() / 2;
		int wdwTop = screenSize.height / 2 - getHeight() / 2;
		setLocation(wdwLeft, wdwTop);
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
	}
}
