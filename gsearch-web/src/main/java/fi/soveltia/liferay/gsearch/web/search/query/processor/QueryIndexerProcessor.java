
package fi.soveltia.liferay.gsearch.web.search.query.processor;

import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;
import fi.soveltia.liferay.gsearch.web.search.internal.queryparams.QueryParams;

/**
 * Query indexer. Implementations of this interface index queries.
 * 
 * @author Petteri Karttunen
 */
public interface QueryIndexerProcessor {

	/**
	 * Index query.
	 * 
	 * @param gSearchDisplayConfiguration
	 * @param queryParams
	 * @param hits
	 * @return true / false
	 * @throws Exception
	 */
	public boolean process(SearchContext searchContext,
		GSearchDisplayConfiguration gSearchDisplayConfiguration,
		QueryParams queryParams, Hits hits)
		throws Exception;
}
