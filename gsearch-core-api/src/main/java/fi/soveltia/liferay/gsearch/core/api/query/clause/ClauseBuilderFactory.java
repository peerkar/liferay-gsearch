
package fi.soveltia.liferay.gsearch.core.api.query.clause;

/**
 * Clause builder factory. This service return a clause builder specific for the
 * query type (MatchQuery, QueryStrinQuery etc.)
 * 
 * @author Petteri Karttunen
 */
public interface ClauseBuilderFactory {

	/**
	 * Get clause builder.
	 * 
	 * @param queryType
	 * @return ClauseBuilder
	 */
	public ClauseBuilder getClauseBuilder(String queryType);
}
