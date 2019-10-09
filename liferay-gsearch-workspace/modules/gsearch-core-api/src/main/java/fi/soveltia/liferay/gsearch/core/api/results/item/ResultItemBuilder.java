
package fi.soveltia.liferay.gsearch.core.api.results.item;

import com.liferay.portal.search.document.Document;

import java.util.Map;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Builds a single result item.
 *
 * @author Petteri Karttunen
 */
public interface ResultItemBuilder {

	/**
	 * Checks whether this builder can build the result item for the index
	 * document. This is usually based on asset type but can be any condition.
	 *
	 * @param document
	 * @return
	 */
	public boolean canBuild(Document document);

	/**
	 * Gets item date.
	 *
	 * @param queryContext
	 * @param document
	 * @return string representation of item date
	 * @throws Exception
	 */
	public String getDate(QueryContext queryContext, Document document)
		throws Exception;

	/**
	 * Gets item description.
	 *
	 * @param queryContext
	 * @param document
	 * @return item description
	 * @throws Exception
	 */
	public String getDescription(QueryContext queryContext, Document document)
		throws Exception;

	/**
	 * Gets item link.
	 *
	 * @param httpServletRequest
	 * @param document
	 * @param queryContext
	 * @return item url
	 * @throws Exception
	 */
	public String getLink(QueryContext queryContext, Document document)
		throws Exception;

	/**
	 * Gets item additional metadata.
	 *
	 * @param queryContext
	 * @param document
	 * @return item metadata
	 * @throws Exception
	 */
	public Map<String, String> getMetadata(
			QueryContext queryContext, Document document)
		throws Exception;

	/**
	 * Gets thumbnail (src) for a result item.
	 *
	 * @param queryContext
	 * @param document
	 * @return thumbnail src
	 * @throws Exception
	 */
	public String getThumbnail(QueryContext queryContext, Document document)
		throws Exception;

	/**
	 * Gets item title.
	 *
	 * @param queryContext
	 * @param document
	 * @return item title
	 * @throws Exception
	 */
	public String getTitle(
			QueryContext queryContext, Document document)
		throws Exception;

	/**
	 * Gets item asset type.
	 *
	 * @param document
	 * @return name of the item asset type
	 * @throws Exception
	 */
	public String getType(Document document) throws Exception;

}