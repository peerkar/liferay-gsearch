
package fi.soveltia.liferay.gsearch.core.api.params;

import com.liferay.portal.kernel.exception.PortalException;

import javax.portlet.PortletRequest;

/**
 * Query parameters builder. This service builds query params object from
 * request parameters.
 * 
 * @author Petteri Karttunen
 */
public interface QueryParamsBuilder {

	/**
	 * Parse parameters from request and build a query params object.
	 *
	 * @param portletRequest
	 * @return QueryParams object
	 * @throws PortalException
	 */
	public QueryParams buildQueryParams(
		PortletRequest portletRequest)
		throws Exception;
}
