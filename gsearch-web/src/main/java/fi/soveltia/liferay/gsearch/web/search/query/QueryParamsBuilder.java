
package fi.soveltia.liferay.gsearch.web.search.query;

import com.liferay.portal.kernel.exception.PortalException;

import javax.portlet.PortletRequest;

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;

/**
 * QueryParamsBuilder interface. This service builds query params object from
 * request parameters.
 * 
 * @author Petteri Karttunen
 */
public interface QueryParamsBuilder {

	/**
	 * Parse parameters from request and build a QueryParam object.
	 *
	 * @param portletRequest
	 * @param configuration
	 * @return
	 * @throws PortalException
	 */
	public QueryParams buildQueryParams(
		PortletRequest portletRequest,
		GSearchDisplayConfiguration configuration)
		throws PortalException;
}
