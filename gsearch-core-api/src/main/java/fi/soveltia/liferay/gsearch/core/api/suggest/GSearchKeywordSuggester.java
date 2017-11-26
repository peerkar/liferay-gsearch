
package fi.soveltia.liferay.gsearch.core.api.suggest;

import com.liferay.portal.kernel.json.JSONArray;

import javax.portlet.PortletRequest;

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
	 * @return suggestions JSON array
	 * @throws Exception
	 */
	public JSONArray getSuggestions(
		PortletRequest portletRequest)
		throws Exception;

	/**
	 * Get keyword suggestions as string Array
	 * 
	 * @param portletRequest
	 * @param portletResponse
	 * @param keywords
	 * @return suggestions JSON array
	 * @throws Exception
	 */
	public String[] getSuggestionsAsStringArray(
		PortletRequest portletRequest)
		throws Exception;
}
