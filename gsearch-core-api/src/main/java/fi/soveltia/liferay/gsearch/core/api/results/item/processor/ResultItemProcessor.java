
package fi.soveltia.liferay.gsearch.core.api.results.item.processor;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Document;

import javax.portlet.PortletRequest;

import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;

/**
 * Result item processor interface. This service could be used for example to
 * "highlight" a single result item based on its' tag.
 * 
 * @author Petteri Karttunen
 */
public interface ResultItemProcessor {

	/**
	 * Is this processor enabled.
	 * 
	 * @return
	 */
	public boolean isEnabled();

	/**
	 * Process.
	 * 
	 * @param portletRequest
	 * @param queryParams
	 * @param document
	 * @param resultItemBuilder
	 * @param resultItem
	 */
	public void process(
		PortletRequest portletRequest, QueryParams queryParams, Document document, 
		ResultItemBuilder resultItemBuilder, JSONObject resultItem)
		throws Exception;
}
