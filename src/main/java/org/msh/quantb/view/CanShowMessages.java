package org.msh.quantb.view;

/**
 * Since ver 3
 * Classes that implement this interface can show info, error and warning messages
 * @author Alexey Kurasov
 *
 */
public interface CanShowMessages {
	/**
	 * Show general information for user 
	 * @param message message to show
	 * @param title title of dialog box or some add information
	 */
	void showInformation(String message, String title);
	/**
	 * Show error message
	 * @param mess
	 */
	void showError(String mess);
	/**
	 * Show very simple warning without yes or no question. It's supposed that user always agree
	 * @param message to display
	 * @return always true
	 */
	public boolean showSimpleWarningString(String message);

}
