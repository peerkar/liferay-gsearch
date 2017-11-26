
package fi.soveltia.liferay.gsearch.core.api.query;

import com.liferay.portal.kernel.search.Query;

import javax.portlet.PortletRequest;

/**
 * Query builder. 
 * 
 * Implementations of this interface parse the query params
 * object and build final query for the backend.
 * 
 * @author Petteri Karttunen
 */
public interface QueryBuilder {

	/**
	 * Build query.
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
