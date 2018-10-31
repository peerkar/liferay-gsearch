
package fi.soveltia.liferay.gsearch.core.api.results.item;

import com.liferay.portal.kernel.search.Document;

import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

/**
 * Asset type specific result item builder. Implementations of this class build
 * a single result item.
 * 
 * @author Petteri Karttunen
 */
public interface ResultItemBuilder {

	/**
	 * Check if this builder can build the requested document.
	 */
	public boolean canBuild(Document document);

	/**
	 * Get item hit date.
	 * 
	 * @param portletRequest
	 * @param document
	 * @return string representation of item date
	 * @throws Exception
	 */
	public String getDate(PortletRequest portletRequest, Document document)
		throws Exception;

	/**
	 * Get item description.
	 * 
	 * @param portletRequest
	 * @param portletResponse
	 * @param document
	 * @return item description
	 * @throws Exception
	 */
	public String getDescription(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document)
		throws Exception;

	/**
	 * Get item image src i.e. src attribute for img tag.
	 * 
	 * @param portletRequest
	 * @param document
	 * @return item description
	 * @throws Exception
	 */
	public String getImageSrc(PortletRequest portletRequest, Document document)
		throws Exception;

	/**
	 * Get item link.
	 * 
	 * @param portletRequest
	 * @param portletResponse
	 * @param document
	 * @param assetPublisherPageFriendlyURL
	 * @return item link
	 * @throws Exception
	 */
	public String getLink(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document, String assetPublisherPageFriendlyURL)
		throws Exception;

	/**
	 * Get item additional metadata.
	 * 
	 * @param portletRequest
	 * @param document
	 * @return item metadata
	 * @throws Exception
	 */
	public Map<String, String> getMetadata(PortletRequest portletRequest, Document document)
		throws Exception;

	/**
	 * Get item tags.
	 * 
	 * @return item tags
	 */
	public String[] getTags(Document document)
		throws Exception;

	/**
	 * Get item title.
	 * 
	 * @param portletRequest
	 * @param portletResponse
	 * @param document
	 * @return item title
	 * @throws Exception
	 */
	public String getTitle(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document)
		throws Exception;

	/**
	 * Get item type
	 * 
	 * @param document
	 * @return name of the item asset type
	 * @throws Exception
	 */
	public String getType(Document document)
		throws Exception;
}
