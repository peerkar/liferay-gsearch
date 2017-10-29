package fi.soveltia.liferay.gsearch.web.search.query.filter;

import com.liferay.portal.kernel.search.filter.BooleanFilter;

import javax.portlet.PortletRequest;

import fi.soveltia.liferay.gsearch.web.search.internal.queryparams.QueryParams;

/**
 * Query filter builder. Implementations of this interface build query filter.
 * 
 * @author Petteri Karttunen
 */
public interface QueryFilterBuilder {

	/**
	 * Build query filter
	 * 
	 * @param portletRequest
	 * @param queryParams
	 * @return Filter object
	 * @throws Exception
	 */
	public BooleanFilter buildQueryFilter (
		PortletRequest portletRequest, QueryParams queryParams)
		throws Exception;
}
