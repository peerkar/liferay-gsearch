
package fi.soveltia.liferay.gsearch.core.api.query.postprocessor;

import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Does query processing after the search has been done. Ccan be used for
 * example for query indexing.
 * 
 * @author Petteri Karttunen
 */
public interface QueryPostProcessor {

	/**
	 * Index query.
	 * 
	 * @param queryContext
	 * @param searchContext
	 * @param hits
	 * @return true / false
	 * @throws Exception
	 */
	public boolean process(
		QueryContext queryContext, SearchContext searchContext, Hits hits)
		throws Exception;
}
