
package fi.soveltia.liferay.gsearch.core.api.query.clause;

import com.liferay.portal.kernel.json.JSONObject;

import javax.portlet.PortletRequest;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Checks clause's conditions.
 * 
 * @author Petteri Karttunen
 */
public interface ClauseConditionHandler {

	/**
	 * Checks if this handler can process the requested type.
	 * 
	 * @param handlerName
	 * @return
	 */
	public boolean canProcess(String handlerName);

	/**
	 * Is this condition true.
	 * 
	 * @param portletRequest
	 * @param queryContext
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	public boolean isTrue(
		PortletRequest portletRequest, QueryContext queryContext,
		JSONObject parameters)
		throws Exception;
}
