
package fi.soveltia.liferay.gsearch.core.api.results.item;

import com.liferay.portal.kernel.search.Document;

/**
 * Result item builder factory interface. Implementations of this service
 * returns an asset type specific result item builder.
 * 
 * @author Petteri Karttunen
 */
public interface ResultItemBuilderFactory {

	/**
	 * Get result builder.
	 * 
	 * @param document Searchresult document
	 * @return ResultItemBuilder
	 */
	public ResultItemBuilder getResultBuilder(Document document);
}
