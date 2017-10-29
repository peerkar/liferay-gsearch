
package fi.soveltia.liferay.gsearch.web.search.query.keyword;

import fi.soveltia.liferay.gsearch.web.search.internal.queryparams.QueryParams;

/**
 * Asset type specific keyword query builder factory.
 * 
 * @author Petteri Karttunen
 */
public interface KeywordQueryBuilderFactory {

	/**
	 * Get query builder.
	 * 
	 * @param className
	 * @param queryParams
	 * 
	 * @return asset specific keyword query builder
	 */
	public KeywordQueryBuilder getKeywordQueryBuilder(String className,
		QueryParams queryParams) throws UnsupportedOperationException;
}
