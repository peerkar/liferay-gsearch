
package fi.soveltia.liferay.gsearch.web.search.results.item;

import com.liferay.portal.kernel.search.Document;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

/**
 * Asset type specific result item builder. Implementations of this class build a single result
 * item.
 * 
 * @author Petteri Karttunen
 */
public interface ResultItemBuilder {

	/**
	 * Get item hit date
	 * 
	 * @return string representation of item date
	 * @throws Exception
	 */
	public String getDate()
		throws Exception;

	/**
	 * Get item description
	 * 
	 * @return item description
	 */
	public String getDescription()
		throws Exception;

	/**
	 * Get item Link
	 * 
	 * @return item link
	 * @throws Exception
	 */
	public String getLink()
		throws Exception;

	/**
	 * Get item Title
	 * 
	 * @return item title
	 */
	public String getTitle()
		throws Exception;

	/**
	 * Get item type
	 * 
	 * @return name of the item asset type
	 */
	public String getType()
		throws Exception;

	/**
	 * Set item builder properties
	 * 
	 * @param portletRequest
	 * @param portletResponse
	 * @param document
	 *            search document
	 * @param assetPublisherFriendlyURL
	 *            friendly url of the page where there is an assetpublisher for
	 *            showing contents without any bound layout
	 */
	public void setProperties(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document, String assetPublisherFriendlyURL);

}
