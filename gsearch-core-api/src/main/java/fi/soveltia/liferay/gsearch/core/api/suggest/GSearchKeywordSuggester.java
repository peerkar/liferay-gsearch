
package fi.soveltia.liferay.gsearch.core.api.suggest;

import com.liferay.portal.kernel.json.JSONArray;

import javax.portlet.PortletRequest;

/**
 * Keyword suggester service.
 * 
 * @author Petteri Karttunen
 */
public interface GSearchKeywordSuggester {

	/**
	 * Gets keyword suggestions as JSON array.
	 * 
	 * @param portletRequest
	 * @return suggestions JSON array
	 * @throws Exception
	 */
	public JSONArray getSuggestions(PortletRequest portletRequest)
		throws Exception;

	/**
	 * Gets keyword suggestions as string array.
	 * 
	 * @param portletRequest
	 * @return suggestions JSON array
	 * @throws Exception
	 */
	public String[] getSuggestionsAsStringArray(PortletRequest portletRequest)
		throws Exception;
}
