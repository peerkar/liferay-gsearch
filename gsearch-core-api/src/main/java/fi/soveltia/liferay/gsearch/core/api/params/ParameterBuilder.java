
package fi.soveltia.liferay.gsearch.core.api.params;

import javax.portlet.PortletRequest;

import fi.soveltia.liferay.gsearch.core.api.exception.ParameterValidationException;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Takes care of receiving request parameter, validating and putting it to query
 * context.
 * 
 * @author Petteri Karttunen
 */
public interface ParameterBuilder {

	/**
	 * Process parameter and add it to query context.
	 * 
	 * @param portletRequest
	 * @param filter
	 * @param queryContext
	 * @throws Exception
	 */
	public void addParameter(
		PortletRequest portletRequest, QueryContext queryContext)
		throws Exception;

	/**
	 * Validate parameter
	 * 
	 * @return
	 */
	public boolean validate(PortletRequest portletRequest)
		throws ParameterValidationException;
}
