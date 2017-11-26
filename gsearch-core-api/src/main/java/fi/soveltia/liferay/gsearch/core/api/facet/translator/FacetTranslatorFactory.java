
package fi.soveltia.liferay.gsearch.core.api.facet.translator;

/**
 * Facet translator factory. {@see FacetTranslator}
 * 
 * @author Petteri Karttunen
 */
public interface FacetTranslatorFactory {

	/**
	 * Get translator by facet (aggregation) field name.
	 * 
	 * @param fieldName
	 * @return
	 */
	public FacetTranslator getTranslator(String fieldName);
}
