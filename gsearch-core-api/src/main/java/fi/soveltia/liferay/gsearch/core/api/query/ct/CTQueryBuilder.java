
package fi.soveltia.liferay.gsearch.core.api.query.ct;

import com.liferay.portal.kernel.search.BooleanQuery;

import javax.portlet.PortletRequest;

/**
 * Audience (Content) targeting query builder.
 * 
 * @author Petteri Karttunen
 */
public interface CTQueryBuilder {

	/**
	 * Build query.
	 * 
	 * @param portletRequest
	 * @return BooleanQuery
	 * @throws Exception
	 */
	public BooleanQuery buildCTQuery(PortletRequest portletRequest)
		throws Exception;

	/**
	 * Is builder enabled
	 * 
	 * @return
	 */
	public boolean isEnabled();	
}
