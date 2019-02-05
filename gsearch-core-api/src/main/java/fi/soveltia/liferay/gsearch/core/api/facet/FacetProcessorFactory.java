
package fi.soveltia.liferay.gsearch.core.api.facet;

/**
 * Facet processor factory.
 * 
 * @author Petteri Karttunen
 */
public interface FacetProcessorFactory {

	/**
	 * Get processor by name.
	 * 
	 * @param processorName
	 * @return
	 */
	public FacetProcessor getProcessor(String processorName);
}
