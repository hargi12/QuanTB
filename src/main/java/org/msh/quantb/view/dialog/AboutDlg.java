package org.msh.quantb.view.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;

/**
 * Dialog window with information about the product, copyright, etc.
 * @author User
 *
 */
public class AboutDlg extends JDialog {
	private static final long serialVersionUID = -8289157059056954233L;
	public AboutDlg(Frame owner) {
		super(owner);
		initDialog();
		setTitle(Messages.getString("DlgAbout.title"));		
		getContentPane().setLayout(null);
		JLabel picLabel = new JLabel(new ImageIcon(AboutDlg.class.getResource("/org/msh/quantb/view/images/logo20.png")));
		picLabel.setBounds(8, 8, 479, 317);
		getContentPane().add(picLabel);
		JButton btnNewButton = new JButton(Messages.getString("DlgMedicines.btnClose.text"));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnNewButton.setBounds(201, 354, 90, 23);
		getContentPane().add(btnNewButton);
		
		JLabel versionlbl = new JLabel(Messages.getString("Application.version") + " "+ Presenter.getVersion());
		versionlbl.setBackground(Color.WHITE);
		versionlbl.setBorder(null);
		versionlbl.setHorizontalAlignment(SwingConstants.CENTER);
		versionlbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
		versionlbl.setBounds(4, 328, 479, 23);
		getContentPane().add(versionlbl);
	}
	
	/**
	 * Initialize dimension and modality of the dialog and position at center of the screen.
	 */
	private void initDialog() {
		setResizable(false);
		setSize(new Dimension(499, 412));
		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		int wdwLeft = screenSize.width / 2 - getWidth() / 2;
		int wdwTop = screenSize.height / 2 - getHeight() / 2;
		setLocation(wdwLeft, wdwTop);
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
	}
}
