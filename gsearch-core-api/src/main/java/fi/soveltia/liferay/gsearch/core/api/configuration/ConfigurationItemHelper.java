
package fi.soveltia.liferay.gsearch.core.api.configuration;

/**
 * A configuration item helper interface.
 * 
 * This facade is used to sync with Liferay managed configuration
 * as changes done through Control Panel don't necessarily update
 * configuration queried directly from ConfigurationAdmin
 * 
 * @author Petteri Karttunen
 */
public interface ConfigurationItemHelper {

	/**
	 * Get configuration.
	 * 
	 * @return String[]
	 */
	public String[] getConfiguration();
}
