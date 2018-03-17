
package fi.soveltia.liferay.gsearch.core.api.query;

import com.liferay.portal.kernel.search.Query;

import javax.portlet.PortletRequest;

import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;

/**
 * Main query builder interface. Implementations of this interface parse the query
 * params object and build the main query to be sent to the search backend.
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
