
package fi.soveltia.liferay.gsearch.web.search;

import com.liferay.portal.kernel.json.JSONObject;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;
import fi.soveltia.liferay.gsearch.web.search.query.QueryParams;

/**
 * GSearch interface.
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
	 * @param gSearchDisplayConfiguration
	 * @return
	 * @throws Exception
	 */
	public JSONObject getSearchResults(
		PortletRequest portletRequest, PortletResponse portletResponse,
		QueryParams queryParams,
		GSearchDisplayConfiguration gSearchDisplayConfiguration)
		throws Exception;

}
