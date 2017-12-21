
package fi.soveltia.liferay.gsearch.core.api.query.builder;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.WildcardQuery;

import fi.soveltia.liferay.gsearch.core.api.query.QueryParams;

/**
 * Wildcard query builder. 
 * 
 * Implementations of this interface parse a wildcard query
 * from configuration.
 * 
 * @author Petteri Karttunen
 */
public interface WildcardQueryBuilder {

	/**
	 * Build wildcard query.
	 * 
	 * @param configurationObject
	 * @param queryParams
	 * @return WildcardQuery object
	 * @throws Exception
	 */
	public WildcardQuery buildQuery(
		JSONObject configurationObject, QueryParams queryParams)
		throws Exception;

	/**
	 * Build splitted wildcard query.
	 * 
	 * This splits searchphrase to invidual terms and build a query for each.
	 * User for userName field, for example.
	 * 
	 * @param configurationObject
	 * @param queryParams
	 * @return BooleanQuery object
	 * @throws Exception
	 */
	public BooleanQuery buildSplittedQuery(
		JSONObject configurationObject, QueryParams queryParams)
		throws Exception;

}
