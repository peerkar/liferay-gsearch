
package fi.soveltia.liferay.gsearch.core.api.query.postprocessor;

import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;

import javax.portlet.PortletRequest;

import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;

/**
 * Query postprocessor service. Implementations of this service can do query
 * processing after the search has been done.
 * 
 * Can be used for example to query indexing.
 * 
 * @author Petteri Karttunen
 */
public interface QueryPostProcessor {

	/**
	 * Index query.
	 * 
	 * @param portletRequest
	 * @param searchContext
	 * @param queryParams
	 * @param hits
	 * @return true / false
	 * @throws Exception
	 */
	public boolean process(
		PortletRequest portletRequest, SearchContext searchContext,
		QueryParams queryParams, Hits hits)
		throws Exception;
}
