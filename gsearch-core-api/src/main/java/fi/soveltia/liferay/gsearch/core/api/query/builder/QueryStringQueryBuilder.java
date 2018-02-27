
package fi.soveltia.liferay.gsearch.core.api.query.builder;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Query;

import fi.soveltia.liferay.gsearch.core.api.query.QueryParams;

/**
 * QueryString query builder. 
 * 
 * Implementations of this interface parse a
 * querystring query (falling back silently to stringquery without a custom
 * adapter) from configuration.
 * 
 * @author Petteri Karttunen
 */
public interface QueryStringQueryBuilder {

	/**
	 * Build querystring query.
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
