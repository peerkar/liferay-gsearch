
package fi.soveltia.liferay.gsearch.core.api.query.filter;

import com.liferay.portal.kernel.search.Query;

import javax.portlet.PortletRequest;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Builds permission filter query.
 * 
 * This interface is separated from FilterBuilder so
 * that overriding it would be easier.
 * 
 * @author Petteri Karttunen
 */
public interface PermissionFilterQueryBuilder {

	/**
	 * Build permission query filter
	 * 
	 * @param portletRequest
	 * @param queryContext
	 * @return Query object
	 * @throws Exception
	 */
	public Query buildPermissionQuery(
		PortletRequest portletRequest, QueryContext queryContext)
		throws Exception;
}
