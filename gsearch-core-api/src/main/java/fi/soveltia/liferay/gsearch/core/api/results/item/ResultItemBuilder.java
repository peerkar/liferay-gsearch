
package fi.soveltia.liferay.gsearch.core.api.results.item;

import com.liferay.portal.kernel.search.Document;

import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Builds a single result item. 
 * 
 * @author Petteri Karttunen
 */
public interface ResultItemBuilder {

	/**
	 * Checks whether this builder can build the result item 
	 * for the index document. 
	 * 
	 * This is usually based on asset type but can be any condition.
	 * 
	 * 
	 * @param document
	 * @return
	 */
	public boolean canBuild(Document document);

	/**
	 * Gets item date. 
	 * 
	 * @param portletRequest
	 * @param document
	 * @return string representation of item date
	 * @throws Exception
	 */
	public String getDate(PortletRequest portletRequest, Document document)
		throws Exception;

	/**
	 * Gets item description.
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
	 * Gets item link.
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
	 * Gets item additional metadata.
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
	 * Gets thumbnail (src) for a result item.
	 * 
	 * @param portletRequest
	 * @param document
	 * @return thumbnail src
	 * @throws Exception
	 */
	public String getThumbnail(PortletRequest portletRequest, Document document)
		throws Exception;

	/**
	 * Gets item title.
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
	 * Gets item asset type.
	 * 
	 * @param document
	 * @return name of the item asset type
	 * @throws Exception
	 */
	public String getType(Document document)
		throws Exception;
}
