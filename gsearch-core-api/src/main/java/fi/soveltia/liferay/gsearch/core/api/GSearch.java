
package fi.soveltia.liferay.gsearch.core.api;

import com.liferay.portal.kernel.json.JSONObject;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

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
	 * @param queryContext
	 * @return search results JSON object
	 * @throws Exception
	 */
	public JSONObject getSearchResults(
		PortletRequest portletRequest, PortletResponse portletResponse,
		QueryContext queryContext)
		throws Exception;
}
