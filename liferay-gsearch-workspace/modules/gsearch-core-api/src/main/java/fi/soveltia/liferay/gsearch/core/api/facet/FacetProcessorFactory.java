
package fi.soveltia.liferay.gsearch.core.api.facet;

import java.util.List;

/**
 * Facet processor factory.
 *
 * @author Petteri Karttunen
 */
public interface FacetProcessorFactory {

	/**
	 * Gets a list of processors for the given field name.
	 *
	 * @param fieldName
	 * @return
	 */
	public List<FacetProcessor> getProcessors(String fieldName);

}