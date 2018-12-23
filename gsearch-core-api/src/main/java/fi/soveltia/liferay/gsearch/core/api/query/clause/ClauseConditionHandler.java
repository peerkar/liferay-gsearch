
package fi.soveltia.liferay.gsearch.core.api.query.clause;

import com.liferay.portal.kernel.json.JSONObject;

import javax.portlet.PortletRequest;

import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;

/**
 * Handle clause condition interface. 
 * 
 * Implementations of this interface check
 * conditions: whether a clause should be applied.
 * 
 * @author Petteri Karttunen
 * 
 */
public interface ClauseConditionHandler {

	/**
	 * Is this condition true.
	 * 
	 * @param portletRequest
	 * @param queryParams
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	public boolean isTrue(
		PortletRequest portletRequest, QueryParams queryParams,
		JSONObject parameters)
		throws Exception;

	/**
	 * Check if this handler can process the requested type.
	 * 
	 * @param handlerName
	 * @return
	 */
	public boolean canHandle(String handlerName);
}
