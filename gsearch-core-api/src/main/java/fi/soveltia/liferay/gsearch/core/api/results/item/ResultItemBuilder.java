
package fi.soveltia.liferay.gsearch.core.api.results.item;

import com.liferay.portal.kernel.search.Document;

import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Asset type specific single result item builder. 
 * 
 * @author Petteri Karttunen
 */
public interface ResultItemBuilder {

	/**
	 * Check if this builder can build the result item for the document.
	 * 
	 * @param document
	 * @return
	 */
	public boolean canBuild(Document document);

	/**
	 * Get item date. 
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
	 * Get item link.
	 * 
	 * @param portletRequest
	 * @param portletResponse
	 * @param document
	 * @param queryContext
	 * @return item url
	 * @throws Exception
	 */
	public String getLink(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document, QueryContext queryContext)
		throws Exception;

	/**
	 * Get item additional metadata.
	 * 
	 * @param portletRequest
	 * @param document
	 * @return item metadata
	 * @throws Exception
	 */
	public Map<String, String> getMetadata(
		PortletRequest portletRequest, Document document)
		throws Exception;

	/**
	 * Get thumbnail (src) for a result item.
	 * 
	 * @param portletRequest
	 * @param document
	 * @return thumbnail src
	 * @throws Exception
	 */
	public String getThumbnail(PortletRequest portletRequest, Document document)
		throws Exception;

	/**
	 * Get item title.
	 * 
	 * @param portletRequest
	 * @param portletResponse
	 * @param document
	 * @param highlight
	 * @return item title
	 * @throws Exception
	 */
	public String getTitle(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document, boolean highlight)
		throws Exception;

	/**
	 * Get item asset type.
	 * 
	 * @param document
	 * @return name of the item asset type
	 * @throws Exception
	 */
	public String getType(Document document)
		throws Exception;
}
