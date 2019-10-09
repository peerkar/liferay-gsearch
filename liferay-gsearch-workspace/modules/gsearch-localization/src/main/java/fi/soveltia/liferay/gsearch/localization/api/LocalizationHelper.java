
package fi.soveltia.liferay.gsearch.localization.api;

import com.liferay.portal.kernel.json.JSONObject;

import java.util.Locale;

/**
 * Shared localizations helper. 
 * 
 * @author Petteri Karttunen
 */
public interface LocalizationHelper {

	public String getLocalization(Locale locale, String key, Object... objects);

	/**
	 * Localizes result types in the search result items.
	 * 
	 * @param locale
	 * @param responseObject
	 * @return
	 */
	public void setResultTypeLocalizations(
		Locale locale, JSONObject responseObject);
}
