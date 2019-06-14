
package fi.soveltia.liferay.gsearch.core.api.results;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;

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
	 * @param queryContext
	 * @param searchContext
	 * @param hits
	 * @return
	 */
	public JSONObject buildResults(
		QueryContext queryContext, SearchContext searchContext, Hits hits);
}
