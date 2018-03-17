
package fi.soveltia.liferay.gsearch.core.api.results.item.processor;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Document;

/**
 * Result item processor interface. 
 * This service could be used for example to "highlight" a
 * single result item based on its' tag.
 * 
 * @author Petteri Karttunen
 */
public interface ResultItemProcessor {

	/**
	 * Is this processor enabled.
	 * 
	 * @return
	 */
	public boolean isEnabled();

	/**
	 * Process.
	 * 
	 * @param document
	 * @param resultItem
	 */
	public void process(Document document, JSONObject resultItem) throws Exception;
}
