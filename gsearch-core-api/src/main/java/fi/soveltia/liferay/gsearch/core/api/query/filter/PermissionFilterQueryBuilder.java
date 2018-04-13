package fi.soveltia.liferay.gsearch.core.api.query.filter;

import com.liferay.portal.kernel.search.Query;

import javax.portlet.PortletRequest;

import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;

/**
 * Permission filter query builder.
 * 
 * 
 * @author Petteri Karttunen
 */
public interface PermissionFilterQueryBuilder {


	/**
	 * Build permission query filter
	 * 
	 * @param portletRequest
	 * @param queryParams
	 * @return Query object
	 * @throws Exception
	 */
	public Query buildPermissionQuery(
		PortletRequest portletRequest, QueryParams queryParams)
		throws Exception;
}
