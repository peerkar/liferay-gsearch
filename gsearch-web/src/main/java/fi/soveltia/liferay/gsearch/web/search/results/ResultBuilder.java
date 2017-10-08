
package fi.soveltia.liferay.gsearch.web.search.results;

import com.liferay.portal.kernel.search.Document;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

/**
 * Result Builder Interface
 * 
 * @author Petteri Karttunen
 */
public interface ResultBuilder {

	/**
	 * Get hit date
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getDate()
		throws Exception;

	/**
	 * Get description
	 * 
	 * @return
	 */
	public String getDescription()
		throws Exception;

	/**
	 * Get Link
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getLink()
		throws Exception;

	/**
	 * Get Title
	 * 
	 * @return
	 */
	public String getTitle()
		throws Exception;

	/**
	 * Get Type
	 * 
	 * @return
	 */
	public String getType()
		throws Exception;

	/**
	 * Set builder properties
	 * 
	 * @param portletRequest
	 * @param portletResponse
	 * @param document
	 * @param assetPublisherFriendlyURL
	 */
	public void setProperties(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document, String assetPublisherFriendlyURL);

}
