
package fi.soveltia.liferay.gsearch.core.api.query.filter;

import com.liferay.portal.kernel.search.filter.BooleanFilter;

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
	 * Process filter and add it to the main filter.
	 * 
	 * @param queryContext
	 * @param preBooleanfilter
	 * @param postFilter
	 * @throws Exception
	 */
	public void addFilters(
		QueryContext queryContext, BooleanFilter preBooleanfilter,
		BooleanFilter postFilter)
		throws Exception;

}
