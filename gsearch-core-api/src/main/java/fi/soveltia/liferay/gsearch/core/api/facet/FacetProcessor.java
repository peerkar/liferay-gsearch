
package fi.soveltia.liferay.gsearch.core.api.facet;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Facet translator takes care of both translating and aggregating facet term
 * values from search results to more user friendly form and vice versa. For
 * example, file extension translators translates both "doc" and "docx" facet
 * terms and frequencies to a single aggregation called "MS Word". During query
 * time it translates MS Word back to query params "doc" and "docx".
 * 
 * @author Petteri Karttunen
 */
public interface FacetTranslator {

	/**
	 * Check if this translator can process the facet.
	 * 
	 * @param queryType
	 * @return
	 */
	public boolean canProcess(String translatorName);

	/**
	 * Translate request parameters to query undestandable form.
	 *
	 * @param value
	 * @param configuration
	 * @return
	 */
	public String[] toQuery(String value, JSONObject configuration);

	/**
	 * Translate facet values from search results for UI.
	 * 
	 * @param queryParams
	 * @param facetCollector
	 * @param configuration
	 * @return
	 * @throws Exception
	 */
	public JSONArray fromResults(
		QueryContext queryParams, FacetCollector facetCollector,
		JSONObject configuration)
		throws Exception;

}
