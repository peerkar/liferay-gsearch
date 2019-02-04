
package fi.soveltia.liferay.gsearch.core.api.configuration;

import javax.portlet.PortletRequest;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Configuration helper interface.
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
	 * @param portletRequest
	 * @param queryParams
	 * @param input
	 * @return
	 */
	public String parseConfigurationVariables(
		PortletRequest portletRequest, QueryContext queryParams, String input);
}