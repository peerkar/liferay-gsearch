
package fi.soveltia.liferay.gsearch.core.api.params;

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
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public QueryParams buildQueryParams(
		PortletRequest portletRequest, int pageSize)
		throws Exception;

	/**
	 * Parse parameters from request and build a query params object.
	 *
	 * @param portletRequest
	 * @param assetTypeConfiguration
	 * @param clauseConfiguration
	 * @param facetConfiguration
	 * @param sortConfiguration
	 * @param pagesize
	 * @return
	 * @throws Exception
	 */
	public QueryParams buildQueryParams(
		PortletRequest portletRequest, String[] assetTypeConfiguration,
		String[] clauseConfiguration, String[] facetConfiguration,
		String[] sortConfiguration, int pageSize)
		throws Exception;
}
