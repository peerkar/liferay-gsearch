
package fi.soveltia.liferay.gsearch.web.search.query.processor;

import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;

import javax.portlet.PortletRequest;

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;
import fi.soveltia.liferay.gsearch.web.search.internal.queryparams.QueryParams;

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
	 * @param gSearchDisplayConfiguration
	 * @param queryParams
	 * @param hits
	 * @return true/false
	 * @throws Exception
	 */
	public boolean process(
		PortletRequest portletRequest, SearchContext searchContext,
		GSearchDisplayConfiguration gSearchDisplayConfiguration,
		QueryParams queryParams, Hits hits)
		throws Exception;
}
