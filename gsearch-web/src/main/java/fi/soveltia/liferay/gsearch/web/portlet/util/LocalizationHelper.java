
package fi.soveltia.liferay.gsearch.web.portlet.util;

import java.util.Locale;

/**
 * Localization helper util.
 * 
 * @author Petteri Karttunen
 */
public interface LocalizationHelper {

	public String getLocalization(Locale locale, String key, Object... objects);
}
