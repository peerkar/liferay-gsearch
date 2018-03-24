
package fi.soveltia.liferay.gsearch.core.api.configuration;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;

import java.util.Locale;

import javax.portlet.PortletRequest;

/**
 * A configuration helper interface.
 * 
 * @author Petteri Karttunen
 */
public interface ConfigurationHelper {

	/**
	 * Get asset type options localized. Should be deprecated when
	 * https://issues.liferay.com/browse/LPS-75141 is solved.
	 *
	 * @param locale
	 * @return
	 * @throws JSONException
	 */
	public JSONArray getAssetTypeOptions(Locale locale)
		throws JSONException;
	
	/**
	 * Get facets configuration
	 * 
	 * @return JSONArray
	 * @throws Exception
	 */
	public JSONArray getFacetConfiguration() throws JSONException;
	
	/**
	 * Get sort options localized. Should be deprecated when
	 * https://issues.liferay.com/browse/LPS-75141 is solved.
	 * 
	 * @param locale
	 * @return
	 * @throws JSONException
	 */
	public JSONArray getSortOptions(Locale locale)
		throws JSONException;
	
	/**
	 * Parse configuration field name which might have variables in it.
	 * 
	 * @param portletRequest
	 * @param fieldName
	 * @return
	 */
	public String parseConfigurationKey(PortletRequest portletRequest, String fieldName);

}
