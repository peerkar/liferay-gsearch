
package fi.soveltia.liferay.gsearch.web.search.queryparams;

import com.liferay.portal.kernel.exception.PortalException;

import javax.portlet.PortletRequest;

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;
import fi.soveltia.liferay.gsearch.web.search.internal.queryparams.QueryParams;

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
	 * @param configuration
	 *            portlet configuration object
	 * @return QueryParams object
	 * @throws PortalException
	 */
	public QueryParams buildQueryParams(
		PortletRequest portletRequest,
		GSearchDisplayConfiguration configuration)
		throws Exception;
}
