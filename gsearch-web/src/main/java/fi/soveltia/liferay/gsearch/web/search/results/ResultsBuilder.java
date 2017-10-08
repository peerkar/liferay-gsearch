
package fi.soveltia.liferay.gsearch.web.search.results;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.search.Hits;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;

/**
 * ResultsBuilder interface. This service builds the search result items.
 * 
 * @author Petteri Karttunen
 */
public interface ResultsBuilder {

	public JSONArray getItemsArray(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Hits hits, GSearchDisplayConfiguration gSearchDisplayConfiguration);
}
