package org.msh.quantb.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.window.MainWindow;

public abstract class GenericDialog extends JDialog {
	private static final long serialVersionUID = 2737579752926005736L;

	private final JPanel contentPanel = new JPanel();
	
	private JButton okButton;
	private JButton cancelButton;
	
	private boolean confirmed;

	public boolean showModal(MainWindow window) {
		setLocationRelativeTo( window );
		setIconImage(window.getIconImage());
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setVisible(true);
		return confirmed;
	}
	
	
	public void commandOk() {
		save();
		confirmed = true;
		setVisible(false);
	}
	
	/**
	 * Cancel opeartion
	 */
	public void commandCancel() {
		cancel();
		confirmed = false;
		setVisible(false);
	}
	
	
	public void hideOkButton() {
		
	}
	
	/**
	 * Save the work done
	 */
	public void save() {
		
	}


	/**
	 * Cancel the operation done
	 */
	public void cancel() {
		
	}

	
	/**
	 * Create the dialog.
	 */
	public GenericDialog() {
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setBounds(100, 100, 500, 350);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBorder(new EmptyBorder(6, 4, 6, 4));
			buttonPane.setBackground(Color.GRAY);
			buttonPane.setLayout(new FlowLayout(FlowLayout.LEFT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton(Presenter.getMessage("form.ok"));
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						commandOk();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton(Presenter.getMessage("form.cancel"));
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						commandCancel();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}


	/**
	 * @return the confirmed
	 */
	public boolean isConfirmed() {
		return confirmed;
	}
	
	public Container getClientContent() {
		return contentPanel;
	}


	/**
	 * @return the okButton
	 */
	public JButton getOkButton() {
		return okButton;
	}


	/**
	 * @param okButton the okButton to set
	 */
	public void setOkButton(JButton okButton) {
		this.okButton = okButton;
	}


	/**
	 * @return the cancelButton
	 */
	public JButton getCancelButton() {
		return cancelButton;
	}


	/**
	 * @param cancelButton the cancelButton to set
	 */
	public void setCancelButton(JButton cancelButton) {
		this.cancelButton = cancelButton;
	}

}
