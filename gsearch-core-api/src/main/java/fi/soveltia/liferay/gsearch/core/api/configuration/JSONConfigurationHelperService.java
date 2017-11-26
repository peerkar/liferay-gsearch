
package fi.soveltia.liferay.gsearch.core.api.configuration;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;

import java.util.Locale;

/**
 * This service is a temporary helper service for localizing configuration values
 * because of https://issues.liferay.com/browse/LPS-75141
 * 
 * @author Petteri Karttunen
 */
public interface JSONConfigurationHelperService {

	/**
	 * Get asset type options localized Should be deprecated when
	 * https://issues.liferay.com/browse/LPS-75141 is solved.
	 *
	 * @param locale
	 * @return
	 * @throws JSONException
	 */
	public JSONArray getAssetTypeOptions(Locale locale)
		throws JSONException;
	
	/**
	 * Get sort options localized Should be deprecated when
	 * https://issues.liferay.com/browse/LPS-75141 is solved.
	 * 
	 * @param locale
	 * @return
	 * @throws JSONException
	 */
	public JSONArray getSortOptions(Locale locale)
		throws JSONException;

}
