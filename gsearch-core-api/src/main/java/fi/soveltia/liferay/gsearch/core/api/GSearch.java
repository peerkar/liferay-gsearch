
package fi.soveltia.liferay.gsearch.core.api;

import com.liferay.portal.kernel.json.JSONArray;
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
	 * @param queryParams
	 * @return search results JSON object
	 * @throws Exception
	 */
	public JSONObject getSearchResults(
		PortletRequest portletRequest, PortletResponse portletResponse,
		QueryParams queryParams)
		throws Exception;
	
	/**
	 * Get search results.
	 * 
	 * @param portletRequest
	 * @param portletResponse
	 * @param queryParams
	 * @param queryConfiguration
	 * @param executeQueryPostProcessors
	 * @param processQueryContributors
	 * @return search results JSON object
	 * @throws Exception
	 */
	public JSONObject getSearchResults(
		PortletRequest portletRequest, PortletResponse portletResponse,
		QueryParams queryParams, JSONArray queryConfiguration, 
		boolean executeQuerypostProcessors, boolean processQueryContributors)
		throws Exception;
	
}
