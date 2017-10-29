
package fi.soveltia.liferay.gsearch.web.search.menuoption;

import com.liferay.portal.kernel.json.JSONArray;

import javax.portlet.PortletRequest;

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;

/**
 * Web content structure options service.
 * 
 * @author Petteri Karttunen
 */
public interface WebContentStructureOptions {

	/**
	 * Get options.
	 * 
	 * @param portletRequest
	 * @param gSearchDisplayConfiguration
	 * @return options JSON array
	 * @throws Exception
	 */
	public JSONArray getOptions(
		PortletRequest portletRequest,
		GSearchDisplayConfiguration gSearchDisplayConfiguration)
		throws Exception;
}
