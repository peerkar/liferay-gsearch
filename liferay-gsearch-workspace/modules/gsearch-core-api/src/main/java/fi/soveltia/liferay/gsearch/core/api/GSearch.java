
package fi.soveltia.liferay.gsearch.core.api;

import com.liferay.portal.kernel.json.JSONObject;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * GSearch service.
 *
 * @author Petteri Karttunen
 */
public interface GSearch {

	/**
	 * Gets search results.
	 *
	 * @param queryContext
	 * @return search results as JSON object
	 * @throws Exception
	 */
	public JSONObject getSearchResults(QueryContext queryContext)
		throws Exception;

}