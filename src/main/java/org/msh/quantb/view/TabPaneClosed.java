package org.msh.quantb.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.msh.quantb.services.mvp.Presenter;
import org.msh.quantb.view.panel.ForecastingDocumentPanel;

/**
 * Tab with close button
 *
 */
public class TabPaneClosed extends JPanel {
	private static final long serialVersionUID = -3191069079023895879L;
	private ForecastingDocumentPanel panel;

	/**
	 * Create tab header
	 * @param panel owner
	 * @param title header title
	 */
	public TabPaneClosed(ForecastingDocumentPanel panel, String title) {
		this.panel = panel;		
		setOpaque(false);
		setLayout(new BorderLayout(7, 0));
		JLabel titleLbl = new JLabel(title);		
		add(titleLbl);

		JButton btnNewButton = new JButton();
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Presenter.closeForecastingDocument(TabPaneClosed.this.panel);
			}
		});
		btnNewButton.setIcon(new ImageIcon(TabPaneClosed.class.getResource("/org/msh/quantb/view/images/close.png")));
		btnNewButton.setMargin(new Insets(0, 0, 0, 0));
		btnNewButton.setPreferredSize(new Dimension(15, 15));
		add(btnNewButton, BorderLayout.EAST);
	}
}
