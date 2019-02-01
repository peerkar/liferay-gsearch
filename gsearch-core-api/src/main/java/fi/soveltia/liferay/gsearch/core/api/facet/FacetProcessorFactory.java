
package fi.soveltia.liferay.gsearch.core.api.facet;

/**
 * Facet translator factory.
 * 
 * @author Petteri Karttunen
 */
public interface FacetTranslatorFactory {

	/**
	 * Get translator by name.
	 * 
	 * @param translatorName
	 * @return
	 */
	public FacetTranslator getTranslator(String translatorName);
}
