
package fi.soveltia.liferay.gsearch.core.api.query.builder;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.generic.MatchQuery;

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
	 * Build localized matchquery.
	 * 
	 * @param configurationObject
	 * @param queryParams
	 * @return BooleanQuery object
	 * @throws Exception
	 */
	public BooleanQuery buildLocalizedQuery(
		JSONObject configurationObject, QueryParams queryParams)
		throws Exception;

	/**
	 * Build matchquery.
	 * 
	 * @param configurationObject
	 * @param queryParams
	 * @return MatchQuery object
	 * @throws Exception
	 */
	public MatchQuery buildQuery(
		JSONObject configurationObject, QueryParams queryParams)
		throws Exception;
}
