
package fi.soveltia.liferay.gsearch.core.api.queryparams;

import com.liferay.portal.kernel.exception.PortalException;

import javax.portlet.PortletRequest;

import fi.soveltia.liferay.gsearch.core.api.query.QueryParams;

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
