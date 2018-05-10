package org.msh.quantb.view.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.msh.quantb.services.io.ForecastingTotal;
import org.msh.quantb.services.io.ForecastingTotalItemUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.CellRendererColor;
import org.msh.quantb.view.DashZeroCellRenderer;
import org.msh.quantb.view.PercentageCellEditor;
import org.msh.quantb.view.TextCellEditor;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import java.math.BigDecimal;
import javax.swing.BoxLayout;
import javax.swing.border.LineBorder;

public class FinalCostPanel extends JPanel {
	private JTableBinding<ForecastingTotalItemUIAdapter, ForecastingTotal, JTable> itemsBinding;
	private static final long serialVersionUID = -8941965219628994510L;
	private JScrollPane scrollTotal;
	private JTable finalCostTable;
	private ForecastingTotal total = null;
	private JPanel grandTotalPanel;
	private JPanel medTotalPanel;
	private JLabel medTotalValLabel;
	private JLabel grandTotalValueLabel;
	private JTextField comment2Fld;
	private JPanel totalPanel;
	private String title;
	private JPanel titlePanel;
	private JLabel titleLbl;
	private JButton addItemButton;
	private JButton removeItemButton;
	private JPanel leftSpace;
	private JPanel finalCostPanel;
	private ForecastingTotal otherTotal;
	private JButton btnCopyValues;


	public FinalCostPanel(String _title) {
		setBorder(null);
		this.title = _title;
		setSize(new Dimension(950, 289));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		titlePanel = new JPanel();
		titlePanel.setBorder(null);
		add(titlePanel);

		titleLbl = new JLabel(""); //$NON-NLS-1$
		titleLbl.setFont(new Font(this.getFont().getName(),Font.BOLD,this.getFont().getSize()+4));
		titlePanel.add(titleLbl);

		totalPanel = new JPanel();
		totalPanel.setPreferredSize(new Dimension(10000, 10));
		totalPanel.setBorder(null);
		add(totalPanel);
		scrollTotal = new JScrollPane();
		scrollTotal.setBorder(null);
		finalCostTable = new JTable();
		scrollTotal.setViewportView(finalCostTable);

		finalCostPanel = new JPanel();
		finalCostPanel.setBorder(null);
		finalCostPanel.setLayout(new BorderLayout(0,0));
		leftSpace = new JPanel();
		leftSpace.setBorder(null);
		leftSpace.setPreferredSize(new Dimension(400, 10));
		finalCostPanel.add(leftSpace,BorderLayout.EAST);
		leftSpace.setLayout(null);

		addItemButton = new JButton(Presenter.getMessage("ForecastingDocumentWindow.order.add"));
		addItemButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.addNewOrderItem(getTotal());
			}
		});

		addItemButton.setBounds(10, 67, 135, 23);
		leftSpace.add(addItemButton);
		finalCostPanel.add(scrollTotal, BorderLayout.CENTER);

		removeItemButton = new JButton(Presenter.getMessage("ForecastingDocumentWindow.order.remove"));
		removeItemButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ForecastingTotalItemUIAdapter fTi = getTotal().getSelectedTotalItem();
				if(fTi != null){
					getTotal().removeItem(fTi);
					getTotal().setSelectedTotalItem(null);
					getTotal().renewAddItems();
				}
			}
		});
		totalPanel.setLayout(new BoxLayout(totalPanel, BoxLayout.Y_AXIS));

		medTotalPanel = new JPanel();
		medTotalPanel.setPreferredSize(new Dimension(950, 50));
		medTotalPanel.setMinimumSize(new Dimension(950, 50));
		totalPanel.add(medTotalPanel);
		medTotalPanel.setLayout(null);

		medTotalValLabel = new JLabel("");
		medTotalValLabel.setBounds(21, 11, 417, 14);
		medTotalValLabel.setFont(new Font(this.getFont().getName(),Font.BOLD,this.getFont().getSize()));
		medTotalPanel.add(medTotalValLabel);
		removeItemButton.setBounds(10, 101, 135, 23);
		leftSpace.add(removeItemButton);



		JLabel comment2Lbl = new JLabel(Messages.getString("ForecastingDocumentWindow.order.commentlbl"));
		comment2Lbl.setBounds(10, 11, 234, 14);
		leftSpace.add(comment2Lbl);

		comment2Fld = new JTextField();
		comment2Fld.setBounds(20, 36, 370, 20);
		leftSpace.add(comment2Fld);
		comment2Fld.setColumns(10);

		btnCopyValues = new JButton(""); 
		btnCopyValues.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Presenter.showWarningString(Messages.getString("ForecastingDocumentWindow.order.copyWarning"))){
					copyAdditionalItems();
				}
			}
		});
		btnCopyValues.setBounds(10, 135, 380, 23);
		leftSpace.add(btnCopyValues);

		totalPanel.add(finalCostPanel);


		grandTotalPanel = new JPanel();
		grandTotalPanel.setBorder(null);
		grandTotalPanel.setPreferredSize(new Dimension(950, 50));
		grandTotalPanel.setMinimumSize(new Dimension(950, 50));
		totalPanel.add(grandTotalPanel);
		grandTotalPanel.setLayout(null);

		grandTotalValueLabel = new JLabel("");
		grandTotalValueLabel.setFont(new Font(this.getFont().getName(),Font.BOLD,this.getFont().getSize()));
		grandTotalValueLabel.setBounds(21, 11, 440, 14);
		grandTotalPanel.add(grandTotalValueLabel);
	}

	/**
	 * Copy additional items from "other"
	 */
	protected void copyAdditionalItems() {
		boolean found = false;
		for(ForecastingTotalItemUIAdapter totalItem : getOtherTotal().getAddItems()){
			found = false;
			for(ForecastingTotalItemUIAdapter thisTotalItem: getTotal().getAddItems()){
				if(thisTotalItem.getItem().equalsIgnoreCase(totalItem.getItem())){
					if(totalItem.getFcItemObj().isIsValue()){
						thisTotalItem.setValue(totalItem.getValue());
						getTotal().recalcByValue(thisTotalItem);
					}else{
						thisTotalItem.setPerCents(totalItem.getPerCents());
						getTotal().recalcByPercents(thisTotalItem);
					}
					found = true;
					break;
				}
			}
			if(!found){
				ForecastingTotalItemUIAdapter newItem = totalItem.clone();
				if(newItem.getFcItemObj().isIsValue()){
					getTotal().recalcByValue(newItem);
				}else{
					getTotal().recalcByPercents(newItem);
				}
				getTotal().getAddItems().add(newItem);
			}
			getTotal().recalcTotal();
		}

	}
	/**
	 * bind to totals
	 * @param _total total for edit and display in this panel
	 * @param otherTotal other total to have possibility to copy parameters to _total
	 */
	public void setAndBind(ForecastingTotal _total, ForecastingTotal otherTotal){
		this.otherTotal = otherTotal;
		this.total = _total;
		initDataBindings();
		adjustFinalCostTable();
		adjustCopyButtons();
	}
	/**
	 * Labels on the "copy values" buttons depends on type of order
	 */
	private void adjustCopyButtons() {
		if(getTotal().isAccelTotal()){
			getBtnCopyValues().setText(Messages.getString("ForecastingDocumentWindow.order.buttons.addRegCopy"));
		}else{
			getBtnCopyValues().setText(Messages.getString("ForecastingDocumentWindow.order.buttons.addAccelCopy"));
		}

	}
	public JButton getBtnCopyValues() {
		return btnCopyValues;
	}
	public void setBtnCopyValues(JButton btnCopyValues) {
		this.btnCopyValues = btnCopyValues;
	}
	public ForecastingTotal getOtherTotal() {
		return otherTotal;
	}
	public void setOtherTotal(ForecastingTotal otherTotal) {
		this.otherTotal = otherTotal;
	}
	public JButton getAddItemButton() {
		return addItemButton;
	}

	public void setAddItemButton(JButton addItemButton) {
		this.addItemButton = addItemButton;
	}

	public JButton getRemoveItemButton() {
		return removeItemButton;
	}

	public void setRemoveItemButton(JButton removeItemButton) {
		this.removeItemButton = removeItemButton;
	}



	public JPanel getFinalCostPanel() {
		return finalCostPanel;
	}

	public void setFinalCostPanel(JPanel finalCostPanel) {
		this.finalCostPanel = finalCostPanel;
	}

	public JPanel getTitlePanel() {
		return titlePanel;
	}

	public void setTitlePanel(JPanel titlePanel) {
		this.titlePanel = titlePanel;
	}

	public JPanel getGrandTotalPanel() {
		return grandTotalPanel;
	}

	public void setGrandTotalPanel(JPanel grandTotalPanel) {
		this.grandTotalPanel = grandTotalPanel;
	}

	public JPanel getMedTotalPanel() {
		return medTotalPanel;
	}

	public void setMedTotalPanel(JPanel medTotalPanel) {
		this.medTotalPanel = medTotalPanel;
	}

	public void setTotal(ForecastingTotal total) {
		this.total = total;
	}


	public JTableBinding<ForecastingTotalItemUIAdapter, ForecastingTotal, JTable> getItemsBinding() {
		return itemsBinding;
	}

	public void setItemsBinding(
			JTableBinding<ForecastingTotalItemUIAdapter, ForecastingTotal, JTable> itemsBinding) {
		this.itemsBinding = itemsBinding;
	}

	public JScrollPane getScrollTotal() {
		return scrollTotal;
	}

	public void setScrollTotal(JScrollPane scrollTotal) {
		this.scrollTotal = scrollTotal;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public JPanel getLeftSpace() {
		return leftSpace;
	}

	public void setLeftSpace(JPanel leftSpace) {
		this.leftSpace = leftSpace;
	}


	public JLabel getGrandTotalValueLabel() {
		return grandTotalValueLabel;
	}

	/**
	 * adjust total table columns and headers
	 */
	private void adjustFinalCostTable() {
		//adjust the table
		finalCostTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		finalCostTable.getTableHeader().setReorderingAllowed(false);
		finalCostTable.getTableHeader().setResizingAllowed(false);
		finalCostTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		finalCostTable.getColumnModel().getColumn(0).setHeaderValue(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.total.item"));
		finalCostTable.getColumnModel().getColumn(0).setMinWidth(250);
		finalCostTable.getColumnModel().getColumn(0).setPreferredWidth(250);
		finalCostTable.getColumnModel().getColumn(0).setCellRenderer(new CellRendererColor(Color.WHITE));
		finalCostTable.getColumnModel().getColumn(0).setCellEditor(new TextCellEditor(new JTextField()));
		finalCostTable.getColumnModel().getColumn(1).setHeaderValue(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.total.percentage"));
		finalCostTable.getColumnModel().getColumn(1).setCellRenderer(new DashZeroCellRenderer(true, Color.WHITE, null));
		finalCostTable.getColumnModel().getColumn(1).setCellEditor(new PercentageCellEditor(new JTextField(), true));
		finalCostTable.getColumnModel().getColumn(2).setHeaderValue(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.total.cost"));
		finalCostTable.getColumnModel().getColumn(2).setCellRenderer(new DashZeroCellRenderer(true, Color.WHITE, null));
		finalCostTable.getColumnModel().getColumn(2).setCellEditor(new PercentageCellEditor(new JTextField(), true));
		finalCostTable.getColumnModel().getColumn(3).setHeaderValue(Presenter.getMessage("ForecastingDocumentWindow.tbSummary.total.method"));
		finalCostTable.getColumnModel().getColumn(3).setMinWidth(320);
		finalCostTable.getColumnModel().getColumn(3).setPreferredWidth(320);
		if(getTotal().isGrandTotal()){
			finalCostTable.removeColumn(finalCostTable.getColumnModel().getColumn(3));
			finalCostTable.removeColumn(finalCostTable.getColumnModel().getColumn(1));
		}
	}

	/**
	 * Get forecasting total
	 * @return
	 */
	public ForecastingTotal getTotal() {
		return this.total;
	}
	protected void initDataBindings() {
		BeanProperty<ForecastingTotal, List<ForecastingTotalItemUIAdapter>> forecastingTotalBeanProperty_2 = BeanProperty.create("addItems");
		itemsBinding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE, total, forecastingTotalBeanProperty_2, finalCostTable);
		//
		BeanProperty<ForecastingTotalItemUIAdapter, String> forecastingTotalItemUIAdapterBeanProperty = BeanProperty.create("item");
		itemsBinding.addColumnBinding(forecastingTotalItemUIAdapterBeanProperty).setColumnName("item");
		//
		BeanProperty<ForecastingTotalItemUIAdapter, BigDecimal> forecastingTotalItemUIAdapterBeanProperty_1 = BeanProperty.create("perCentsOrZero");
		itemsBinding.addColumnBinding(forecastingTotalItemUIAdapterBeanProperty_1).setColumnName("percent");
		//
		BeanProperty<ForecastingTotalItemUIAdapter, BigDecimal> forecastingTotalItemUIAdapterBeanProperty_2 = BeanProperty.create("value");
		itemsBinding.addColumnBinding(forecastingTotalItemUIAdapterBeanProperty_2).setColumnName("value");
		//
		BeanProperty<ForecastingTotalItemUIAdapter, String> forecastingTotalItemUIAdapterBeanProperty_3 = BeanProperty.create("calculationMethod");
		itemsBinding.addColumnBinding(forecastingTotalItemUIAdapterBeanProperty_3).setColumnName("method").setEditable(false);
		//
		itemsBinding.bind();
		//
		BeanProperty<JTable, ForecastingTotalItemUIAdapter> jTableBeanProperty = BeanProperty.create("selectedElement");
		BeanProperty<ForecastingTotal, ForecastingTotalItemUIAdapter> forecastingTotalBeanProperty_3 = BeanProperty.create("selectedTotalItem");
		AutoBinding<JTable, ForecastingTotalItemUIAdapter, ForecastingTotal, ForecastingTotalItemUIAdapter> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, finalCostTable, jTableBeanProperty, total, forecastingTotalBeanProperty_3);
		autoBinding_1.bind();
		//
		BeanProperty<ForecastingTotal, String> forecastingTotalBeanProperty = BeanProperty.create("comment");
		BeanProperty<JTextField, String> jTextFieldBeanProperty = BeanProperty.create("text");
		AutoBinding<ForecastingTotal, String, JTextField, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, total, forecastingTotalBeanProperty, comment2Fld, jTextFieldBeanProperty);
		autoBinding.bind();
		//
		BeanProperty<JLabel, String> jLabelBeanProperty = BeanProperty.create("text");
		AutoBinding<String, String, JLabel, String> autoBinding_4 = Bindings.createAutoBinding(UpdateStrategy.READ, title, titleLbl, jLabelBeanProperty);
		autoBinding_4.bind();
		//
		BeanProperty<ForecastingTotal, String> forecastingTotalBeanProperty_1 = BeanProperty.create("formattedTotal");
		AutoBinding<ForecastingTotal, String, JLabel, String> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ, total, forecastingTotalBeanProperty_1, medTotalValLabel, jLabelBeanProperty);
		autoBinding_2.bind();
		//
		BeanProperty<ForecastingTotal, String> forecastingTotalBeanProperty_4 = BeanProperty.create("formattedGrandTotal");
		AutoBinding<ForecastingTotal, String, JLabel, String> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ, total, forecastingTotalBeanProperty_4, grandTotalValueLabel, jLabelBeanProperty);
		autoBinding_3.bind();
	}
}
