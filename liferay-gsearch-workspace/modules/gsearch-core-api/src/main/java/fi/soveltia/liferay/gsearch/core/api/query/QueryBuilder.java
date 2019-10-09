
package fi.soveltia.liferay.gsearch.core.api.query;

import com.liferay.portal.search.query.BooleanQuery;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Query builder interface builds the main query to be sent to the search
 * backend.
 * 
 * @author Petteri Karttunen
 */
public interface QueryBuilder {

	/**
	 * Builds the rescore query.
	 *
	 * @param queryContext
	 * @return rescore query
	 * @throws Exception
	 */
	public BooleanQuery buildRescoreQuery(QueryContext queryContext) 
			throws Exception;

	/**
	 * Builds the search query.
	 *
	 * @param queryContext
	 * @return query
	 * @throws Exception
	 */
	public BooleanQuery buildSearchQuery(QueryContext queryContext) 
			throws Exception;

}