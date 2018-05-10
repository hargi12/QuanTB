package org.msh.quantb.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.msh.quantb.services.io.ForecastingRegimensUIAdapter;
import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;

import javax.swing.JTable;
import javax.swing.JScrollPane;
import org.jdesktop.beansbinding.BeanProperty;

import java.util.ArrayList;
import java.util.List;
import org.msh.quantb.services.io.ForecastingRegimenUIAdapter;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class RegimenSelectDlg extends JDialog {

	private static final long serialVersionUID = 4601801368887557743L;
	private final JPanel contentPanel = new JPanel();
	private ForecastingRegimensUIAdapter regimensUIAdapter;
	private JTable regimensTable;
	private List<ForecastingRegimenUIAdapter> selectedList = new ArrayList<ForecastingRegimenUIAdapter>();
	/**
	 * Create the dialog.
	 */
	public RegimenSelectDlg(ForecastingRegimensUIAdapter _regimensUIAdapter, Frame owner) {
		super(owner);		
		this.regimensUIAdapter = _regimensUIAdapter;		
		initDialog();		
		setTitle(Messages.getString("DlgForecastingRegimen.title"));
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 608, 320);
		contentPanel.add(scrollPane);
		
		regimensTable = new JTable();
		regimensTable.setRowSelectionAllowed(false);
		regimensTable.getTableHeader().setReorderingAllowed(false);
		regimensTable.getTableHeader().setResizingAllowed(false);
		regimensTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		scrollPane.setViewportView(regimensTable);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.LEFT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton(Messages.getString("DlgMedicineSelect.btnOK.text")); //$NON-NLS-1$				
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {						
						selectedList.clear();
						for(ForecastingRegimenUIAdapter f : regimensUIAdapter.getRegimens()){
							if (f.getChecked()){
								f.setChecked(false);
								selectedList.add(f);
							}
						}
						if (selectedList.isEmpty()){
							Presenter.showError(Messages.getString("Error.regimens.selectedRegimensEmpty"));
							return;
						}
						RegimenSelectDlg.this.setVisible(false);
						Presenter.addSelectedRegimensToForecast(selectedList);
					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton(Messages.getString("DlgEditMedicine.btnCancel.text")); //$NON-NLS-1$				
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				buttonPane.add(cancelButton);
			}
		}
		initDataBindings();
		regimensTable.getColumnModel().getColumn(0).setPreferredWidth(30);
		regimensTable.getColumnModel().getColumn(0).setHeaderValue("");
		regimensTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
		regimensTable.getColumnModel().getColumn(1).setPreferredWidth(250);
		regimensTable.getColumnModel().getColumn(1).setHeaderValue(Messages.getString("Regimen.clmn.Regimen"));
		regimensTable.getColumnModel().getColumn(2).setPreferredWidth(200);
		regimensTable.getColumnModel().getColumn(2).setHeaderValue(Messages.getString("Regimen.composition"));
	}	
	/**
	 * Initialize dimension and modality of the dialog and position at center of the screen.
	 */
	private void initDialog() {
		setSize(new Dimension(634, 400));
		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		int wdwLeft = screenSize.width / 2 - getWidth() / 2;
		int wdwTop = screenSize.height / 2 - getHeight() / 2;
		setLocation(wdwLeft, wdwTop);
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
	}
	protected void initDataBindings() {
		BeanProperty<ForecastingRegimensUIAdapter, List<ForecastingRegimenUIAdapter>> forecastingRegimensUIAdapterBeanProperty = BeanProperty.create("regimens");
		JTableBinding<ForecastingRegimenUIAdapter, ForecastingRegimensUIAdapter, JTable> jTableBinding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE, regimensUIAdapter, forecastingRegimensUIAdapterBeanProperty, regimensTable);
		//
		BeanProperty<ForecastingRegimenUIAdapter, Boolean> forecastingRegimenUIAdapterBeanProperty = BeanProperty.create("checked");
		jTableBinding.addColumnBinding(forecastingRegimenUIAdapterBeanProperty).setColumnName("check").setColumnClass(Boolean.class);
		//
		BeanProperty<ForecastingRegimenUIAdapter, String> forecastingRegimenUIAdapterBeanProperty_1 = BeanProperty.create("regimen.name");
		jTableBinding.addColumnBinding(forecastingRegimenUIAdapterBeanProperty_1).setColumnName("regimen").setEditable(false);
		//
		BeanProperty<ForecastingRegimenUIAdapter, String> forecastingRegimenUIAdapterBeanProperty_2 = BeanProperty.create("regimen.consumption");
		jTableBinding.addColumnBinding(forecastingRegimenUIAdapterBeanProperty_2).setColumnName("consumption").setEditable(false);
		//
		jTableBinding.bind();
	}
}
