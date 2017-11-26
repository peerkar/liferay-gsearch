
package fi.soveltia.liferay.gsearch.core.api.query.processor;

import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;

import fi.soveltia.liferay.gsearch.core.api.query.QueryParams;

/**
 * Query indexer. Implementations of this interface index queries.
 * 
 * @author Petteri Karttunen
 */
public interface QueryIndexerProcessor {

	/**
	 * Index query.
	 * 
	 * @param GSearchConfiguration
	 * @param queryParams
	 * @param hits
	 * @return true / false
	 * @throws Exception
	 */
	public boolean process(
		SearchContext searchContext, QueryParams queryParams, Hits hits)
		throws Exception;
}
