
package fi.soveltia.liferay.gsearch.core.api.query.builder;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Query;

import fi.soveltia.liferay.gsearch.core.api.query.QueryParams;

/**
 * Match query builder. 
 * 
 * Implementations of this interface parse a matchquery
 * from configuration.
 * 
 * @author Petteri Karttunen
 */
public interface MatchQueryBuilder {

	/**
	 * Build matchquery.
	 * 
	 * @param configurationObject
	 * @param queryParams
	 * @return Query object
	 * @throws Exception
	 */
	public Query buildQuery(
		JSONObject configurationObject, QueryParams queryParams)
		throws Exception;
}
