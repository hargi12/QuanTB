package org.msh.quantb.view.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.msh.quantb.services.mvp.Messages;
import org.msh.quantb.services.mvp.Presenter;

public class HelpDocumentPanel extends JPanel {

	private static final long serialVersionUID = 4450245997690429576L;
	private JEditorPane editorPane;
	private JRadioButton toggleStartHelp;
	private JScrollPane scrollPane;
	private JPanel panel;

	public HelpDocumentPanel() {
		setBackground(Color.WHITE);
		setMaximumSize(new Dimension(1000, 500));
		setPreferredSize(new Dimension(1000, 552));
		setMinimumSize(new Dimension(1000, 500));
		setLayout(new BorderLayout(10, 10));
		setSize(1100, 546);
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(1000, 550));
		panel.setBackground(Color.WHITE);
		panel.setBorder(null);
		panel.setMaximumSize(new Dimension(1000, 550));
		panel.setMinimumSize(new Dimension(1000, 550));
		add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(10, 10));

		JPanel controlPane = new JPanel();
		controlPane.setPreferredSize(new Dimension(1000, 30));
		controlPane.setSize(new Dimension(1000, 30));
		controlPane.setMinimumSize(new Dimension(1000, 30));
		controlPane.setBackground(Color.WHITE);
		controlPane.setBorder(null);
		controlPane.setMaximumSize(new Dimension(2000, 30));
		panel.add(controlPane, BorderLayout.CENTER);
		controlPane.setLayout(new BorderLayout(10, 10));


		toggleStartHelp = new JRadioButton("");
		toggleStartHelp.setAlignmentX(Component.RIGHT_ALIGNMENT);
		toggleStartHelp.setVerticalAlignment(SwingConstants.TOP);
		toggleStartHelp.setPreferredSize(new Dimension(1000, 23));
		toggleStartHelp.setSize(new Dimension(1000, 23));
		toggleStartHelp.setBorder(new EmptyBorder(0, 10, 0, 0));
		toggleStartHelp.setFocusable(false);
		toggleStartHelp.setBackground(Color.WHITE);
		toggleStartHelp.setMinimumSize(new Dimension(1000, 23));
		toggleStartHelp.setMaximumSize(new Dimension(1000, 23));
		toggleStartHelp.setSelected(Presenter.isHelpVisible());
		toggleStartHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				java.awt.EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						boolean bo = toggleStartHelp.isSelected();
						Presenter.storeCustHelp(!bo);
						repaintHelp();
					}

				});
			}
		});

		controlPane.add(toggleStartHelp, BorderLayout.WEST);
		repaintHelp();

	}

	public void repaintHelp() {
		if(Presenter.isHelpVisible()){
			scrollPane = new JScrollPane();
			scrollPane.setSize(new Dimension(1000, 460));
			scrollPane.setMinimumSize(new Dimension(1000, 480));
			scrollPane.setMaximumSize(new Dimension(1000, 480));
			panel.add(scrollPane, BorderLayout.NORTH);
			scrollPane.setPreferredSize(new Dimension(1000, 480));
			scrollPane.setBorder(null);
			toggleStartHelp.setHorizontalAlignment(SwingConstants.CENTER);
			editorPane = new JEditorPane();
			editorPane.setMinimumSize(new Dimension(1000, 470));
			editorPane.setMaximumSize(new Dimension(1000, 470));
			editorPane.setPreferredSize(new Dimension(1000, 470));
			editorPane.setBackground(Color.WHITE);
			editorPane.setEditable(false);
			editorPane.setContentType("text/html;charset=UTF-8");
			scrollPane.setViewportView(editorPane);
			setContent();
		}else{
			if(scrollPane != null){
				scrollPane.removeAll();
				panel.remove(scrollPane);
			}
			editorPane=null;
			scrollPane=null;
			toggleStartHelp.setHorizontalAlignment(SwingConstants.LEFT);
		}
		// toggle label
		String radioLabel = Messages.getString("MainWindow.showquickhelp.show");
		/*		if(Presenter.isHelpVisible()){
			radioLabel = Messages.getString("MainWindow.showquickhelp.skip");
		}*/
		toggleStartHelp.setText(radioLabel);
		//repaint
		panel.revalidate();

	}

	/**
	 * Set the content
	 */
	private void setContent() {
		HTMLEditorKit kit = new HTMLEditorKit();
		StyleSheet style = kit.getStyleSheet();
		style.addRule("h1 {color: #969636;}");
		style.addRule("h2 {color: #969636;}");
		style.addRule("body {font-family: sans-serif}");
		editorPane.setEditorKit(kit);
		URL helpURL=this.getClass().getResource(Messages.getString("Application.help"));
		if (helpURL != null) {
			try {
				editorPane.setPage(helpURL);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
		editorPane.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent hle) {
				if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
					String address = Messages.getString(hle.getURL().getHost());      //it is a some trick should be in help document, see help_eng.html
					URL url;
					try {
						url = new URL(address);
						showUserGuide(url);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			}

		});
	}
	/**
	 * Common method to get User Guide from Internet
	 */
	public static void showUserGuide(URL url) {
		/*		Desktop desktop = Desktop.getDesktop();
		try {
			URI uri = url.toURI();
			desktop.browse(uri);
		} catch (Exception ex) {
			ex.printStackTrace();
		}*/
		Presenter.showUserGuide();
	}
}
