package org.msh.quantb.view.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Enumeration;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.msh.quantb.services.io.DeliveryOrderItemUI;
import org.msh.quantb.services.io.DeliveryOrderUI;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.view.BigDecimalCellRenderer;
import org.msh.quantb.view.DashZeroCellRenderer;
import org.msh.quantb.view.ToolTipCellRenderer;
import org.msh.quantb.view.tableExt.DivSimpleHeaderRenderer;

public class DeliveryOrderPanel extends JPanel {
	private static final long serialVersionUID = -1326671769292328172L;
	private JScrollPane scrollPane;
	private JTable table=null;
	private Object[] columnNames = new Object[] { 
			Messages.getString("ForecastingDocumentWindow.order.ordcolumns.medicines"),
			Messages.getString("ForecastingDocumentWindow.order.ordcolumns.quantityadjusted"),
			Messages.getString("ForecastingDocumentWindow.order.ordcolumns.packajusted"),
			Messages.getString("ForecastingDocumentWindow.order.ordcolumns.regordcost")
	};
	private JLabel headerLbl;
	private JLabel ordDateTxt;
	private JLabel ordDateDate;
	private JLabel deliveryDateTxt;
	private JLabel deliveryDateDate;
	private JLabel totalMedLbl;
	private JLabel addCostLbl;
	private JLabel deliveryTotLbl;
	private String orderTitle;
	private JPanel panel_3;
	private JPanel panel_4;

	/**
	 * Build a delivery schedule order
	 * @param _orderTitle title of order - Regular or Accelerated
	 */
	public DeliveryOrderPanel(String _orderTitle) {
		super();
		setSize(new Dimension(400, 300));
		setMaximumSize(new Dimension(400, 300));
		setMinimumSize(new Dimension(400, 300));
		orderTitle = _orderTitle;
		setBorder(null);
		setPreferredSize(new Dimension(400, 300));
		setLayout(new BorderLayout(0, 0));
		scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(10, 0));
		
		headerLbl = new JLabel("Order #");
		headerLbl.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel.add(headerLbl, BorderLayout.WEST);
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		panel_1.setLayout(new BorderLayout(5, 0));
		
		panel_3 = new JPanel();
		panel_1.add(panel_3, BorderLayout.WEST);
		panel_3.setLayout(new GridLayout(2, 1, 0, 0));
		
		ordDateTxt = new JLabel(Messages.getString("DeliveryOrder.labels.ordDate"));
		panel_3.add(ordDateTxt);
		
		deliveryDateTxt = new JLabel(Messages.getString("DeliveryOrder.labels.deliveryDate"));
		panel_3.add(deliveryDateTxt);
		
		panel_4 = new JPanel();
		panel_1.add(panel_4, BorderLayout.CENTER);
		panel_4.setLayout(new GridLayout(2, 1, 0, 0));
		
		ordDateDate = new JLabel("");
		panel_4.add(ordDateDate);
		ordDateDate.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		deliveryDateDate = new JLabel("");
		panel_4.add(deliveryDateDate);
		deliveryDateDate.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new LineBorder(new Color(0, 0, 0)));
		add(panel_2, BorderLayout.SOUTH);
		panel_2.setLayout(new GridLayout(0, 2, 5, 5));
		JLabel lblNewLabel = new JLabel(Messages.getString("ForecastingDocumentWindow.order.submedtotal")); //$NON-NLS-1$
		panel_2.add(lblNewLabel);
		lblNewLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		totalMedLbl = new JLabel(""); //$NON-NLS-1$
		totalMedLbl.setHorizontalAlignment(SwingConstants.TRAILING);
		totalMedLbl.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_2.add(totalMedLbl);
		JLabel lblNewLabel_1 = new JLabel(Messages.getString("DeliveryOrder.labels.additionalCost")); //$NON-NLS-1$
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.TRAILING);
		panel_2.add(lblNewLabel_1);
		addCostLbl = new JLabel(""); //$NON-NLS-1$
		addCostLbl.setHorizontalAlignment(SwingConstants.TRAILING);
		addCostLbl.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_2.add(addCostLbl);
		JLabel lblNewLabel_2 = new JLabel(Messages.getString("DeliveryOrder.labels.ordMedCost")+":");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.TRAILING);
		panel_2.add(lblNewLabel_2);
		deliveryTotLbl = new JLabel(""); //$NON-NLS-1$
		deliveryTotLbl.setHorizontalAlignment(SwingConstants.TRAILING);
		deliveryTotLbl.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_2.add(deliveryTotLbl);
		
	}


	public String getOrderTitle() {
		return orderTitle;
	}


	public JScrollPane getScrollPane() {
		return scrollPane;
	}


	public void setScrollPane(JScrollPane scrollPane) {
		this.scrollPane = scrollPane;
	}


	public JTable getTable() {
		return table;
	}


	public void setTable(JTable table) {
		this.table = table;
	}


	public Object[] getColumnNames() {
		return columnNames;
	}


	public void setColumnNames(Object[] columnNames) {
		this.columnNames = columnNames;
	}


	public JLabel getHeaderLbl() {
		return headerLbl;
	}

	public JLabel getOrdDateTxt() {
		return ordDateTxt;
	}

	public JLabel getOrdDateDate() {
		return ordDateDate;
	}


	public void setOrdDateDate(JLabel ordDateDate) {
		this.ordDateDate = ordDateDate;
	}


	public JLabel getDeliveryDateTxt() {
		return deliveryDateTxt;
	}

	public JLabel getDeliveryDateDate() {
		return deliveryDateDate;
	}


	public JLabel getTotalMedLbl() {
		return totalMedLbl;
	}


	public JLabel getAddCostLbl() {
		return addCostLbl;
	}


	public JLabel getDeliveryTotLbl() {
		return deliveryTotLbl;
	}


	/**
	 * Rebuild the table
	 * @param deliveryNo 
	 */
	public void rebuildTable(DeliveryOrderUI order, int deliveryNo) {
		getHeaderLbl().setText(getOrderTitle() +" " + deliveryNo);
		getOrdDateDate().setText(order.getOrderDateStr());
		getDeliveryDateDate().setText(order.getDeliveryDateStr());
		getTotalMedLbl().setText(order.getFormattedMedCost());
		getAddCostLbl().setText(order.getFormattedAddCost());
		getDeliveryTotLbl().setText(order.getFormattedDelivery());
		Object[][] data = new Object[order.getItems().size()][4];
		int index=0;
		for(DeliveryOrderItemUI item : order.getItems()){
			data[index][0] = item.getMedicine().getNameForDisplayWithAbbrev();
			data[index][1] = item.getUnits();
			data[index][2] = item.getPacks();
			data[index][3] = item.getCost();
			index++;
		}
		if(getTable() != null){
			getScrollPane().remove(getTable());
		}
		DefaultTableModel model = new DefaultTableModel(data, getColumnNames()){
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		setTable(new JTable(model));
		getTable().setEnabled(true);
		getTable().setRowSelectionAllowed(false);
		getTable().setCellSelectionEnabled(false);
		getTable().setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		getTable().getTableHeader().setReorderingAllowed(false); 
		//cells
		getTable().getColumnModel().getColumn(0).setPreferredWidth(300);
		getTable().getColumnModel().getColumn(0).setCellRenderer(new ToolTipCellRenderer());
		getTable().getColumnModel().getColumn(1).setPreferredWidth(90);
		getTable().getColumnModel().getColumn(1).setCellRenderer(new BigDecimalCellRenderer(false, null, null));
		getTable().getColumnModel().getColumn(2).setPreferredWidth(90);
		getTable().getColumnModel().getColumn(2).setCellRenderer(new BigDecimalCellRenderer(false, null, null));
		getTable().getColumnModel().getColumn(3).setCellRenderer(new DashZeroCellRenderer(true, null, null));
		//headers
		DivSimpleHeaderRenderer renderer = new DivSimpleHeaderRenderer();
		renderer.setBackground(new Color(221,224,229));
		Enumeration<TableColumn> en = getTable().getColumnModel().getColumns();
		while (en.hasMoreElements()) {
			((TableColumn) en.nextElement()).setHeaderRenderer(renderer);
		}


		getScrollPane().setViewportView(getTable());


	}



}
