
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
	 * @return string representation of item date
	 * @throws Exception
	 */
	public String getDate()
		throws Exception;

	/**
	 * Get item description.
	 *
	 * @return item description
	 */
	public String getDescription()
		throws Exception;

	/**
	 * Get item image src i.e. src attribute for img tag.
	 *
	 * @return item description
	 */
	public String getImageSrc()
		throws Exception;

	/**
	 * Get item link.
	 *
	 * @return item link
	 * @throws Exception
	 */
	public String getLink()
		throws Exception;

	/**
	 * Get item additional metadata.
	 *
	 * @return item metadata
	 * @throws Exception
	 */
	public Map<String, String> getMetadata()
		throws Exception;

	/**
	 * Get item tags.
	 *
	 * @return item tags
	 */
	public String[] getTags()
		throws Exception;

	/**
	 * Get item title.
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

	public String getBreadcrumbs() throws Exception;

	public String[] getCategories() throws Exception;

	/**
	 * Set item builder properties.
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
