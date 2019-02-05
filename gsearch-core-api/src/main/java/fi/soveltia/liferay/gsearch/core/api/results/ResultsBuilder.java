
package fi.soveltia.liferay.gsearch.core.api.results;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Builds the search results object.
 * 
 * @author Petteri Karttunen
 */
public interface ResultsBuilder {

	/**
	 * Builds search results.
	 * 
	 * @param portletRequest
	 * @param portletResponse
	 * @param queryContext
	 * @param searchContext
	 * @param hits
	 * @return
	 */
	public JSONObject buildResults(
		PortletRequest portletRequest, PortletResponse portletResponse,
		QueryContext queryContext, SearchContext searchContext, Hits hits);
}
