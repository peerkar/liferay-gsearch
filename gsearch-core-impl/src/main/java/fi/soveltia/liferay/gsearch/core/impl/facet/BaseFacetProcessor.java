
package fi.soveltia.liferay.gsearch.core.impl.facet;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import fi.soveltia.liferay.gsearch.core.api.facet.FacetProcessor;
import fi.soveltia.liferay.gsearch.core.api.params.FacetParameter;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Abstract base facet processor class.
 * 
 * @author liferay
 */
public abstract class BaseFacetProcessor implements FacetProcessor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processFacetParameters(
		List<FacetParameter> facetParameters, String[] parameterValues,
		JSONObject facetConfiguration)
		throws Exception {

		JSONObject processorParams =
			facetConfiguration.getJSONObject("processor_params");

		String fieldName = processorParams.getString("field_name");
		String filterMode = processorParams.getString("filter_mode", "pre");
		String multiValueOperator =
			processorParams.getString("multi_value_operator", "or");
		boolean allowMultipleValues =
			processorParams.getBoolean("allow_multiple_values", true);

		// Add facet parameter.

		FacetParameter facetParam = new FacetParameter(
			fieldName, Arrays.asList(parameterValues), allowMultipleValues,
			multiValueOperator, filterMode);

		facetParameters.add(facetParam);
	}

	@Override
	public JSONObject processFacetResults(
		QueryContext queryContext, Collection<Facet> facets,
		JSONObject facetConfiguration)
		throws Exception {

		JSONObject processorParams =
			facetConfiguration.getJSONObject("processor_params");

		String fieldName = processorParams.getString("field_name");

		FacetCollector facetCollector = getFacetCollector(facets, fieldName);

		if (facetCollector == null) {
			return null;
		}

		// Build term array.

		JSONArray termArray = JSONFactoryUtil.createJSONArray();

		List<TermCollector> termCollectors = facetCollector.getTermCollectors();

		for (TermCollector tc : termCollectors) {

			JSONObject item = JSONFactoryUtil.createJSONObject();

			item.put("frequency", tc.getFrequency());
			item.put("name", tc.getTerm());
			item.put("term", tc.getTerm());

			termArray.put(item);
		}

		return createResultObject(termArray, fieldName, facetConfiguration);

	}

	protected JSONObject createResultObject(
		JSONArray termArray, String fieldName, JSONObject facetConfiguration) {

		if (termArray.length() == 0) {
			return null;
		}

		JSONObject resultItem = JSONFactoryUtil.createJSONObject();

		resultItem.put("field_name", fieldName);
		resultItem.put("param_name", facetConfiguration.get("param_name"));
		resultItem.put("icon", facetConfiguration.get("icon"));
		resultItem.put("hide", facetConfiguration.getBoolean("hide", false));
		resultItem.put("values", termArray);

		return resultItem;
	}

	protected FacetCollector getFacetCollector(
		Collection<Facet> facets, String fieldName) {

		for (Facet facet : facets) {

			if (facet.isStatic()) {
				continue;
			}

			if (facet.getFieldName().equals(fieldName)) {
				return facet.getFacetCollector();
			}
		}
		return null;
	}
}
