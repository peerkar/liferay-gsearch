
package fi.soveltia.liferay.gsearch.core.api.query.postprocessor;

import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Does query processing after the search has been done. Can be used for
 * example for query indexing.
 *
 * @author Petteri Karttunen
 */
public interface QueryPostProcessor {

	/**
	 * Processes query post processor.
	 *  
	 * @param queryContext
	 * @param searchResponse
	 * @return
	 * @throws Exception
	 */
	public boolean process(
			QueryContext queryContext, SearchSearchResponse searchResponse)
		throws Exception;

}