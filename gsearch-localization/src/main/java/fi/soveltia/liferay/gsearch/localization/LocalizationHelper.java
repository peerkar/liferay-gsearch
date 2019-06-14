
package fi.soveltia.liferay.gsearch.localization;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;

import java.util.Locale;

/**
 * Shared localizations helper. 
 * 
 * @author Petteri Karttunen
 */
public interface LocalizationHelper {

	public String getLocalization(Locale locale, String key, Object... objects);

	public JSONArray getSortOptions(
		Locale locale, String[] configuration) throws Exception;

	public void setFacetLocalizations(
		Locale locale, JSONObject responseObject);

	public void setResultTypeLocalizations(
		Locale locale, JSONObject responseObject);
}
