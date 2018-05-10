package org.msh.quantb.view.dialog;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.msh.quantb.services.mvp.Messages;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;
import javax.swing.border.LineBorder;
import java.awt.SystemColor;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class WrongVersionDlg extends JDialog {

	private static final long serialVersionUID = -2083518934789534897L;
	private JEditorPane editorPane;
	private JPanel panel_1;

	public WrongVersionDlg(Frame owner) {
		super(owner);
		getContentPane().setBounds(new Rectangle(5, 5, 0, 0));
		setBackground(SystemColor.inactiveCaptionBorder);
		getContentPane().setBackground(SystemColor.inactiveCaptionBorder);
		initDialog();
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		setTitle(Messages.getString("Error"));
		
		panel_1 = new JPanel();
		panel_1.setOpaque(false);
		panel_1.setBorder(null);
		panel_1.setBackground(SystemColor.inactiveCaptionBorder);
		getContentPane().add(panel_1);
		Icon icon = UIManager.getIcon("OptionPane.errorIcon");
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));
		JLabel lblNewLabel = new JLabel(icon); //$NON-NLS-1$
		lblNewLabel.setBorder(new LineBorder(SystemColor.inactiveCaptionBorder, 10));
		lblNewLabel.setBackground(SystemColor.inactiveCaptionBorder);
		lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
		panel_1.add(lblNewLabel);
		editorPane = new JEditorPane();
		editorPane.setBorder(new LineBorder(new Color(244, 247, 252), 10));
		editorPane.setOpaque(false);
		panel_1.add(editorPane);
		editorPane.setBackground(SystemColor.inactiveCaptionBorder);
		editorPane.setContentType("text/html");
		editorPane.setEditable(false);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel);
		
		JButton btnNewButton = new JButton(Messages.getString("DlgConfirm.okButton"));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		panel.add(btnNewButton);
		setContent();
	}
	/**
	 * Initialize dimension and modality of the dialog and position at center of the screen.
	 */
	private void initDialog() {
		setResizable(false);
		setSize(new Dimension(675, 244));
		Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		int wdwLeft = screenSize.width / 2 - getWidth() / 2;
		int wdwTop = screenSize.height / 2 - getHeight() / 2;
		setLocation(wdwLeft, wdwTop);
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
	}
	private void setContent() {
		
		HTMLEditorKit kit = new HTMLEditorKit();
		StyleSheet style = kit.getStyleSheet();
		style.addRule("h1 {color: #969636;}");
		style.addRule("h2 {color: #969636;}");
		editorPane.setEditorKit(kit);
		String content = "<html><body>"+
				Messages.getString("Application.wrongversion")+
				"</body></html>";
		editorPane.setText(content);
		editorPane.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent hle) {
				if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
					URL url = hle.getURL();
					goToTheLatestVersion(url);
				}
			}

		});
		
	}
	
	/**
	 * Common method to get User Guide from Internet
	 */
	public static void goToTheLatestVersion(URL url) {
		Desktop desktop = Desktop.getDesktop();
		try {
			URI uri = url.toURI();
			desktop.browse(uri);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
