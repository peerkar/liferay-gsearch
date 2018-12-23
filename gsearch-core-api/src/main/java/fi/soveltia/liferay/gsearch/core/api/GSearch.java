
package fi.soveltia.liferay.gsearch.core.api;

import com.liferay.portal.kernel.json.JSONObject;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;

/**
 * GSearch service.
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
	 * This overload gives a possibility to override 
	 * the system wide core configuration.
	 * 
	 * @param portletRequest
	 * @param portletResponse
	 * @param queryParams
	 * @param executeQueryPostProcessors
	 * @param processQueryContributors
	 * @return
	 * @throws Exception
	 */
	public JSONObject getSearchResults(
		PortletRequest portletRequest, PortletResponse portletResponse,
		QueryParams queryParams, boolean executeQueryPostProcessors, 
		boolean processQueryContributors)
		throws Exception;
}
