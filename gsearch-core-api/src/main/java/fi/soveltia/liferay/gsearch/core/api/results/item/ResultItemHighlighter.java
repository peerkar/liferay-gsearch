package fi.soveltia.liferay.gsearch.core.api.results.item;

import com.liferay.portal.kernel.search.Document;

/**
 * Result item highlighter. Implementations of this interface process a single result item
 * and decide if the item should be highlighted on the results list.
 * 
 * @author Petteri Karttunen
 */
public interface ResultItemHighlighter {

	/**
	 * Is highlighter enabled
	 * 
	 * @return
	 */
	public boolean isEnabled();

	/**
	 * Is highlighted Item
	 * 
	 * @param document
	 * @return
	 */
	public boolean isHighlightedItem(Document document);
}
