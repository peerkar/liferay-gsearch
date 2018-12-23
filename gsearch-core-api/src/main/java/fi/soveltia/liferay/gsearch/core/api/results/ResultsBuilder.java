
package fi.soveltia.liferay.gsearch.core.api.results;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;

/**
 * Results builder interface. 
 * 
 * Implementations of this interface build a search
 * results object.
 * 
 * @author Petteri Karttunen
 */
public interface ResultsBuilder {

	/**
	 * Build search results.
	 * 
	 * @param portletRequest
	 * @param portletResponse
	 * @param queryParams
	 * @param searchContext
	 * @param hits
	 * @return
	 */
	public JSONObject buildResults(
		PortletRequest portletRequest, PortletResponse portletResponse,
		QueryParams queryParams, SearchContext searchContext, Hits hits);
}
