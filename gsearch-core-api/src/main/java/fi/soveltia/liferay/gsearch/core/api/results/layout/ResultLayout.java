package fi.soveltia.liferay.gsearch.core.api.results.layout;

import java.util.Map;

/**
 * Result layout.
 * 
 * Creating a new result layout:
 * 
 * 1) Create the new result layout component
 * 2) Implement the UI (see examples in gsearch-web)
 * 3) Add configuration entry to GSearch portlet configuration
 * 
 * @author Petteri Karttunen
 *
 */
public interface ResultLayout {

	/**
	 * Get parameter key.
	 * 
	 * @return
	 */
	public String getKey();
	
	/**
	 * Get OR filters for showing the layout.
	 * 
	 * @return
	 */
	public Map<String, String>getParamFiltersAND();

	/**
	 * Get AND filters for showing the layout.
	 * 
	 * @return
	 */
	public Map<String, String>getParamFiltersOR();

}
