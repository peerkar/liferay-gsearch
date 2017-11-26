
package fi.soveltia.liferay.gsearch.core.api.query.ct;

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
	 * @return string
	 * @throws Exception
	 */
	public String buildCTQuery(PortletRequest portletRequest)
		throws Exception;
}
