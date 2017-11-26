
package fi.soveltia.liferay.gsearch.core.api.query.processor;

import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;

import javax.portlet.PortletRequest;

import fi.soveltia.liferay.gsearch.core.api.query.QueryParams;

/**
 * Query suggestions processor. Implementations of this interface populate the
 * hits object with query suggestions.
 * 
 * @author Petteri Karttunen
 */
public interface QuerySuggestionsProcessor {

	/**
	 * Process suggestions.
	 * 
	 * @param portletRequest
	 * @param searchContext
	 * @param queryParams
	 * @param hits
	 * @return true/false
	 * @throws Exception
	 */
	public boolean process(
		PortletRequest portletRequest, SearchContext searchContext,
		QueryParams queryParams, Hits hits)
		throws Exception;
}
