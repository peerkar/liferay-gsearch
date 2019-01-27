
package fi.soveltia.liferay.gsearch.core.api.query.filter;

import com.liferay.portal.kernel.search.filter.BooleanFilter;

import javax.portlet.PortletRequest;

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
	 * @param portletRequest
	 * @param filter
	 * @param queryContext
	 * @throws Exception
	 */
	public void addFilters(
		PortletRequest portletRequest, BooleanFilter preBooleanfilter,
		BooleanFilter postFilter, QueryContext queryContext)
		throws Exception;

}
