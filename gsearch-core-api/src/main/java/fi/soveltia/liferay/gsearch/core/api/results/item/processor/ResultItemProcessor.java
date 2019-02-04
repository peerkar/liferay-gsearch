
package fi.soveltia.liferay.gsearch.core.api.results.item.processor;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Document;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;

/**
 * Processes single result item. It can be used for example to "highlight" a
 * single result item based on its' tag.
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
