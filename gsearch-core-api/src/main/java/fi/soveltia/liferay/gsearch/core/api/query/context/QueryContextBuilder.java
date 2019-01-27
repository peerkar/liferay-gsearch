
package fi.soveltia.liferay.gsearch.core.api.query.context;

import javax.portlet.PortletRequest;

/**
 * Builds query context object from and parses request parameters.
 * 
 * @author Petteri Karttunen
 */
public interface QueryContextBuilder {

	/**
	 * Parse parameters from request and build a query context object.
	 * 
	 * @param portletRequest
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public QueryContext buildQueryContext(
		PortletRequest portletRequest, int pageSize)
		throws Exception;

	/**
	 * Parse parameters from request and build a query context object.
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
	public QueryContext buildQueryContext(
		PortletRequest portletRequest, String[] assetTypeConfiguration,
		String[] clauseConfiguration, String[] facetConfiguration,
		String[] sortConfiguration, int pageSize)
		throws Exception;
}
