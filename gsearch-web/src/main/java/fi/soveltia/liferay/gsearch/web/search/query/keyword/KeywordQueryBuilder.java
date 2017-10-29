
package fi.soveltia.liferay.gsearch.web.search.query.keyword;

import com.liferay.portal.kernel.search.Query;

import java.util.List;

import fi.soveltia.liferay.gsearch.web.search.internal.query.keyword.KeywordFieldParam;
import fi.soveltia.liferay.gsearch.web.search.internal.queryparams.QueryParams;

/**
 * Asset type specific keyword query builder. Implementations of this interface
 * build asset type specific query conditions.
 * 
 * @author Petteri Karttunen
 */
public interface KeywordQueryBuilder {

	/**
	 * Build keyword query.
	 * 
	 * @param portletRequest
	 * @param queryParams
	 * @return Query object
	 * @throws Exception
	 */
	public Query buildKeywordQuery()
		throws Exception;

	/**
	 * Get list of keyword fields i.e. fields to target search to.
	 * 
	 * @return
	 */
	public List<KeywordFieldParam> getKeywordFieldParams();

	/**
	 * Set query parameters.
	 * 
	 * @param queryParams
	 */
	public void setQueryParams(QueryParams queryParams);
}
