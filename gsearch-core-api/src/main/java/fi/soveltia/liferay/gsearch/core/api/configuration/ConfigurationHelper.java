
package fi.soveltia.liferay.gsearch.core.api.configuration;

import javax.portlet.PortletRequest;

import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;

/**
 * A configuration helper interface.
 * 
 * @author Petteri Karttunen
 */
public interface ConfigurationHelper {

	/**
	 * Get asset type configuration.
	 * 
	 * @return String[]
	 */
	public String[] getAssetTypeConfiguration();

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
		PortletRequest portletRequest, QueryParams queryParams, String input);
}