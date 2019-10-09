
package fi.soveltia.liferay.gsearch.core.api.facet;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.search.aggregation.AggregationResult;

import java.util.List;

import fi.soveltia.liferay.gsearch.core.api.params.FacetParameter;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Processes facets from parameters to query and from results to UI.
 *
 * For example, file extension facet processor translates both "doc" and "docx"
 * facet terms and frequencies to a single aggregation called "MS Word". During
 * query time it translates MS Word back to query params "doc" and "docx".
 *
 * @author Petteri Karttunen
 */
public interface FacetProcessor {

	/**
	 * Gets processor name.
	 *
	 * @return
	 */
	public String getName();

	/**
	 * Processes / translates facet request parameter values
	 * to facetparameter filters.
	 *
	 * @param facetParameters
	 * @param parameterValues
	 * @param facetConfiguration
	 * @return
	 */
	public void processFacetParameters(
			List<FacetParameter> facetParameters, String[] parameterValues,
			JSONObject facetConfiguration)
		throws Exception;

	/**
	 * Processes facet values from search results for UI.
	 *
	 * @param queryContext
	 * @param aggregationResult
	 * @return
	 * @throws Exception
	 */
	public JSONObject processFacetResults(
			QueryContext queryContext, AggregationResult aggregationResult,
			JSONObject facetConfiguration)
		throws Exception;

}