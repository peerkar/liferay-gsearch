
package fi.soveltia.liferay.gsearch.core.api.results.item;

import com.liferay.portal.kernel.search.Document;
import fi.soveltia.liferay.gsearch.core.api.results.SearchResultCategory;

import java.util.Locale;
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
	public String getDate(Document document, Locale locale)
		throws Exception;

	/**
	 * Get item description.
	 *
	 * @return item description
	 */
	public String getDescription(PortletRequest portletRequest, PortletResponse portletResponse, Document document, Locale locale)
		throws Exception;

	/**
	 * Get item image src i.e. src attribute for img tag.
	 *
	 * @return item description
	 */
	public String getImageSrc(PortletRequest portletRequest, long entryClassPK)
		throws Exception;

	/**
	 * Get item link.
	 *
	 * @return item link
	 * @throws Exception
	 */
	public String getLink(PortletRequest portletRequest, PortletResponse portletResponse, Document document, String assetPublisherPageFriendlyURL, long entryClassPK)
		throws Exception;

	/**
	 * Get item additional metadata.
	 *
	 * @return item metadata
	 * @throws Exception
	 */
	public Map<String, String> getMetadata(Document document, Locale locale, long companyId)
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
	 * @return item title
	 */
	public String getTitle(PortletRequest portletRequest, PortletResponse portletResponse, Document document, Locale locale, long entryClassPK)
		throws Exception;

	/**
	 * Get item type
	 *
	 * @return name of the item asset type
	 */
	public String getType()
		throws Exception;

	public String getBreadcrumbs(PortletRequest portletRequest, PortletResponse portletResponse, Document document, String assetPublisherPageFriendlyURL, long entryClassPK) throws Exception;

	public SearchResultCategory[] getCategories(Document document, Locale locale) throws Exception;

}
