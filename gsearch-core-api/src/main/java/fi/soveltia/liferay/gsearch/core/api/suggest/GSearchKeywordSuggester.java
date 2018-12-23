
package fi.soveltia.liferay.gsearch.core.api.suggest;

import com.liferay.portal.kernel.json.JSONArray;

import javax.portlet.PortletRequest;

/**
 * Keywords suggester / autocompletion interface.
 * 
 * @author Petteri Karttunen
 */
public interface GSearchKeywordSuggester {

	/**
	 * Get keyword suggestions as JSON array.
	 * 
	 * @param portletRequest
	 * @return suggestions JSON array
	 * @throws Exception
	 */
	public JSONArray getSuggestions(
		PortletRequest portletRequest)
		throws Exception;

	/**
	 * Get keyword suggestions as string array.
	 * 
	 * @param portletRequest
	 * @return suggestions JSON array
	 * @throws Exception
	 */
	public String[] getSuggestionsAsStringArray(
		PortletRequest portletRequest)
		throws Exception;
}
