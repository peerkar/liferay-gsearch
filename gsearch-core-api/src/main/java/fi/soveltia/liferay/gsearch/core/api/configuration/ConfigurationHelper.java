
package fi.soveltia.liferay.gsearch.core.api.configuration;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Configuration helper service.
 * 
 * @author Petteri Karttunen
 */
public interface ConfigurationHelper {

	/**
	 * Get clause configuration.
	 * 
	 * @return String[]
	 */
	public String[] getClauseConfiguration();

	/**
	 * Get facet configuration.
	 * 
	 * @return String[]
	 * @throws Exception
	 */
	public String[] getFacetConfiguration();

	/**
	 * Get keyword suggester configuration
	 * 
	 * @return
	 */
	public String[] getKeywordSuggesterConfiguration();

	/**
	 * Get filter configuration.
	 * 
	 * @return String[]
	 */
	public String[] getFilterConfiguration();
	
	/**
	 * Get sort configuration.
	 * 
	 * @return String[]
	 * @throws Exception
	 */
	public String[] getSortConfiguration();
	
	/**
	 * Parse known variables in configuration key or value.
	 * 
	 * For example $_language_id_$ could be translated to current language id.
	 *  
	 * @param queryContext
	 * @param input
	 * @return
	 */
	public String parseConfigurationVariables(
		QueryContext queryContext, String input);
}