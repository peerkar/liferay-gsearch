
package fi.soveltia.liferay.gsearch.core.api.facet.translator;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;

import fi.soveltia.liferay.gsearch.core.api.query.QueryParams;

/**
 * Single facet translator. 
 * 
 * Implementations of this interface take care of both
 * translating and aggregating facet term values from search results to more
 * user friendly form and vice versa. So for example, file extension translators
 * translates both "doc" and "docx" facet terms and frequencies to a single
 * aggregation called "MS Word". During query time it translates MS Word back to
 * query params "doc" and "docx".
 * 
 * @author Petteri Karttunen
 */
public interface FacetTranslator {

	/**
	 * Set translators facet name.
	 * 
	 * @param facetName
	 */
	public void setFacetName(String facetName);
	
	/**
	 * Translate search result values for UI.
	 * 
	 * @param queryParams
	 * @param facetCollector
	 * @param configuration
	 * @return
	 * @throws Exception
	 */
	public JSONArray translateValues(
		QueryParams queryParams, FacetCollector facetCollector,
		JSONObject configuration)
		throws Exception;

	/**
	 * Translate request parameters to query undestandable form.
	 *
	 * @param value
	 * @param configuration
	 * @return
	 */
	public String[] translateParams(
		String value, JSONObject configuration);
}
