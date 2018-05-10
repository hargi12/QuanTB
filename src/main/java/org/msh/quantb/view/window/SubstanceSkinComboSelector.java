package org.msh.quantb.view.window;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.SwingUtilities;

import org.msh.quantb.services.mvp.Presenter;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultComboBoxRenderer;
import org.pushingpixels.substance.api.skin.SkinInfo;
/**
 * Substance skins combo selector
 * @author Kirill Grouchnikov
 *
 */
public class SubstanceSkinComboSelector extends JComboBox {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6383091768133710432L;
	
	 public SubstanceSkinComboSelector() {
		    // populate the combobox
		    super(new ArrayList<SkinInfo>(SubstanceLookAndFeel.getAllSkins()
		        .values()).toArray());
		    // set the current skin as the selected item
		    SubstanceSkin currentSkin = SubstanceLookAndFeel.getCurrentSkin();
		    for (SkinInfo skinInfo : SubstanceLookAndFeel.getAllSkins().values()) {
		      if (skinInfo.getDisplayName().compareTo(
		          currentSkin.getDisplayName()) == 0) {
		        this.setSelectedItem(skinInfo);
		        break;
		      }
		    }
		    // set custom renderer to show the skin display name
		    this.setRenderer(new SubstanceDefaultComboBoxRenderer(this) {
		      /**
				 * 
				 */
				private static final long serialVersionUID = 8039212977643221350L;

			@Override
		      public Component getListCellRendererComponent(JList list,
		          Object value, int index, boolean isSelected,
		          boolean cellHasFocus) {
		        return super.getListCellRendererComponent(list,
		            ((SkinInfo) value).getDisplayName(), index, isSelected,
		            cellHasFocus);
		      }
		    });
		    // add an action listener to change skin based on user selection
		    this.addActionListener(new ActionListener() {
		      @Override
		      public void actionPerformed(ActionEvent e) {
		        SwingUtilities.invokeLater(new Runnable() {
		          @Override
		          public void run() {
		            SubstanceLookAndFeel
		                .setSkin(((SkinInfo) SubstanceSkinComboSelector.this
		                    .getSelectedItem()).getClassName());
		            Presenter.runForecastingCalculation();
		          }
		        });
		      }
		    });
		  }


}
