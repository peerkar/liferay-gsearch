
package fi.soveltia.liferay.gsearch.web.search.suggest;

import com.liferay.portal.kernel.json.JSONArray;

import javax.portlet.PortletRequest;

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;

/**
 * GSearch keywords suggester / autocompletion service.
 * 
 * @author Petteri Karttunen
 */
public interface GSearchKeywordSuggester {

	/**
	 * Get keyword suggestions as JSON Array
	 * 
	 * @param portletRequest
	 * @param portletResponse
	 * @param keywords
	 * @param gSearchDisplayConfiguration
	 * @return suggestions JSON array
	 * @throws Exception
	 */
	public JSONArray getSuggestions(
		PortletRequest portletRequest,
		GSearchDisplayConfiguration gSearchDisplayConfiguration)
		throws Exception;

	/**
	 * Get keyword suggestions as string Array
	 * 
	 * @param portletRequest
	 * @param portletResponse
	 * @param keywords
	 * @param gSearchDisplayConfiguration
	 * @return suggestions JSON array
	 * @throws Exception
	 */
	public String[] getSuggestionsAsStringArray(
		PortletRequest portletRequest,
		GSearchDisplayConfiguration gSearchDisplayConfiguration)
		throws Exception;
}
