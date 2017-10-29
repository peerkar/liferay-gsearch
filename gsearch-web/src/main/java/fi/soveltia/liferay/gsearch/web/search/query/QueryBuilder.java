
package fi.soveltia.liferay.gsearch.web.search.query;

import com.liferay.portal.kernel.search.Query;

import javax.portlet.PortletRequest;

import fi.soveltia.liferay.gsearch.web.search.internal.queryparams.QueryParams;

/**
 * Main query builder. Implementations of this interface parse the query params
 * object and build query for the backend.
 * 
 * @author Petteri Karttunen
 */
public interface QueryBuilder {

	/**
	 * Build query
	 * 
	 * @param portletRequest
	 * @param queryParams
	 * @return Query object
	 * @throws Exception
	 */
	public Query buildQuery(
		PortletRequest portletRequest, QueryParams queryParams)
		throws Exception;
}
