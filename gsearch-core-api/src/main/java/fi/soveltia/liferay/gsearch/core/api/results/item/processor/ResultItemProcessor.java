
package fi.soveltia.liferay.gsearch.core.api.results.item.processor;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Document;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;

/**
 * (Post) processes single result item. 
 * 
 * It can, for example, be used to add any additional 
 * properties to the result item.
 * 
 * @author Petteri Karttunen
 */
public interface ResultItemProcessor {

	/**
	 * Checks whether this processor is enabled.
	 * 
	 * @return
	 */
	public boolean isEnabled();

	/**
	 * Processes the item.
	 * 
	 * @param portletRequest
	 * @param portletResponse
	 * @param queryParams
	 * @param document
	 * @param resultItemBuilder
	 * @param resultItem
	 */
	public void process(
		PortletRequest portletRequest, PortletResponse portletResponse,
		QueryContext queryParams, Document document,
		ResultItemBuilder resultItemBuilder, JSONObject resultItem)
		throws Exception;
}
