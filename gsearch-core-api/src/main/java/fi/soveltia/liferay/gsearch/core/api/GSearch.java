
package fi.soveltia.liferay.gsearch.core.api;

import com.liferay.portal.kernel.json.JSONObject;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;

/**
 * GSearch service. This service is responsible for getting the search results.
 *
 * @author Petteri Karttunen
 */
public interface GSearch {

	/**
	 * Get search results.
	 *
	 * @param portletRequest
	 * @param portletResponse
	 * @return search results JSON object
	 * @throws Exception
	 */
	public JSONObject getSearchResults(
		PortletRequest portletRequest, PortletResponse portletResponse)
		throws Exception;
}
