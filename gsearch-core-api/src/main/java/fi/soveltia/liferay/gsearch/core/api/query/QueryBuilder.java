
package fi.soveltia.liferay.gsearch.core.api.query;

import com.liferay.portal.kernel.search.Query;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Query builder interface builds the main query to be sent to the search
 * backend.
 * 
 * @author Petteri Karttunen
 */
public interface QueryBuilder {

	/**
	 * Build query.
	 * 
	 * @param queryContext
	 * @return query
	 * @throws Exception
	 */
	public Query buildQuery(
		QueryContext queryContext)
		throws Exception;
}
