package org.msh.quantb.services.mvp;

import java.beans.Beans;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.UIManager;

public class Messages {
	private static String language = "";//"uk";
	private static String country = "";//"UA";

	////////////////////////////////////////////////////////////////////////////
	//
	// Constructor
	//
	////////////////////////////////////////////////////////////////////////////
	private Messages() {
		// do not instantiate
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Bundle access
	//
	////////////////////////////////////////////////////////////////////////////
	private static final String BUNDLE_NAME = "messages"; //$NON-NLS-1$
	private static ResourceBundle RESOURCE_BUNDLE = loadBundle();

	private static ResourceBundle loadBundle() {
		return ResourceBundle.getBundle(BUNDLE_NAME, new Locale(language, country));
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Strings access
	//
	////////////////////////////////////////////////////////////////////////////
	public static String getString(String key) {
		try {
			ResourceBundle bundle = Beans.isDesignTime() ? loadBundle() : RESOURCE_BUNDLE;
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return "!" + key + "!";
		}
	}
	
	/**
	 * @return the language
	 */
	public static String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public static void setLanguage(String language) {
		Messages.language = language;
	}

	/**
	 * @return the country
	 */
	public static String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public static void setCountry(String country) {
		Messages.country = country;
	}

	/**
	 * Reload the current bundle
	 */
	public static void reloadBundle() {
		ResourceBundle.clearCache();
		RESOURCE_BUNDLE = loadBundle();

	}

}
