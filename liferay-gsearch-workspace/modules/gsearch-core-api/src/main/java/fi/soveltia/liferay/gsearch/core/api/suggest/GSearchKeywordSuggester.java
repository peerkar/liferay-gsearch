
package fi.soveltia.liferay.gsearch.core.api.suggest;

import com.liferay.portal.kernel.json.JSONArray;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Keyword suggester service.
 *
 * @author Petteri Karttunen
 */
public interface GSearchKeywordSuggester {

	/**
	 * Gets keyword suggestions as JSON array.
	 *
	 * @param queryContext
	 * @return suggestions JSON array
	 * @throws Exception
	 */
	public JSONArray getSuggestions(QueryContext queryContext) throws Exception;

	/**
	 * Gets keyword suggestions as string array.
	 *
	 * @param queryContext
	 * @return suggestions JSON array
	 * @throws Exception
	 */
	public String[] getSuggestionsAsStringArray(QueryContext queryContext)
		throws Exception;

}