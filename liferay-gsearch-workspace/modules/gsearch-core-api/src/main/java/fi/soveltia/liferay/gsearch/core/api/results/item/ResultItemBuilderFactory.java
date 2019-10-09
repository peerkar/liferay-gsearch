
package fi.soveltia.liferay.gsearch.core.api.results.item;

import com.liferay.portal.search.document.Document;

/**
 * Returns an asset type specific result item builder.
 *
 * @author Petteri Karttunen
 */
public interface ResultItemBuilderFactory {

	/**
	 * Gets result builder.
	 *
	 * @param document
	 * @return ResultItemBuilder
	 */
	public ResultItemBuilder getResultBuilder(Document document);

}