
package fi.soveltia.liferay.gsearch.core.api.query.filter;

import com.liferay.portal.search.query.BooleanQuery;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Single filter builder.
 *
 * Notice that if you use BooleanQuery type for filter
 * conditions, they get translated intto 3 subqueries: match, phrase, and
 * phrase_prefix. Prefer explicit TermQuery for filtering.
 *
 * @author Petteri Karttunen
 */
public interface FilterBuilder {

	/**
	 * Adds filters to the respective queries.
	 * 
	 * @param queryContext
	 * @param preFilterQuery
	 * @param postFilterQuery
	 * @throws Exception
	 */
	public void addFilters(
			QueryContext queryContext, BooleanQuery preFilterQuery,
			BooleanQuery postFilterQuery)
		throws Exception;

}