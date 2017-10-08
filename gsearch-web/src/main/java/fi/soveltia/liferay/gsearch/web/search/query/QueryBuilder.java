
package fi.soveltia.liferay.gsearch.web.search.query;

import com.liferay.portal.kernel.search.Query;

import javax.portlet.PortletRequest;

/**
 * QueryBuilder interface. This service builds the query for the backend.
 * 
 * @author Petteri Karttunen
 */
public interface QueryBuilder {

	/**
	 * Build query
	 * 
	 * @param portletRequest
	 * @param queryParams
	 * @return
	 * @throws Exception
	 */
	public Query buildQuery(
		PortletRequest portletRequest, QueryParams queryParams)
		throws Exception;
}
