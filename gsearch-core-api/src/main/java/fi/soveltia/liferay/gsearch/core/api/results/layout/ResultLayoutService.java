package fi.soveltia.liferay.gsearch.core.api.results.layout;

import java.util.List;

import javax.portlet.PortletRequest;

/**
 * Result layout service.
 * 
 * @author Petteri Karttunen
 *
 */
public interface ResultLayoutService {

	/**
	 * Get list of all available result layouts.
	 * 
	 * @return
	 */
	public List<ResultLayout>getResultLayouts();
	
	/**
	 * Get list of result layouts available for the query. 
	 * 
	 * @param portletRequest
	 * @return
	 */
	public List<ResultLayout>getAvailableResultLayouts(PortletRequest portletRequest);

	/**
	 * Get default result layout key.
	 * 
	 * @return
	 */
	public String getDefaultResultLayoutKey();
	
	/**
	 * Check if there's a registered result layout for the key.
	 * 
	 * @param key
	 * @return
	 */
	public boolean isValidResultLayoutKey(PortletRequest portletRequest, String key);
}
