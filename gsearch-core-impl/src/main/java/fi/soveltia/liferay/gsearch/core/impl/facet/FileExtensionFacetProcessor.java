
package fi.soveltia.liferay.gsearch.core.impl.facet;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.facet.FacetProcessor;
import fi.soveltia.liferay.gsearch.core.api.params.FacetParameter;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Facet processor for document extension.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true,
	service = FacetProcessor.class
)
public class FileExtensionFacetProcessor extends BaseFacetProcessor
	implements FacetProcessor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {

		return NAME;
	}

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

		List<String> values = new ArrayList<String>();

		JSONArray aggregations = processorParams.getJSONArray("aggregations");

		for (String parameterValue : parameterValues) {

			String[] valueArray = null;

			for (int i = 0; i < aggregations.length(); i++) {

				JSONObject aggregation = aggregations.getJSONObject(i);

				if (aggregation.getString("key").equals(parameterValue)) {
					valueArray = aggregation.getString("values").split(",");
					break;
				}
			}

			if (valueArray != null) {
				Collections.addAll(values, valueArray);
			}
			else {
				values.add(parameterValue);
			}
		}

		facetParameters.add(
			new FacetParameter(
				fieldName, values, allowMultipleValues, multiValueOperator,
				filterMode));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject processFacetResults(
		QueryContext queryContext, Collection<Facet> facets,
		JSONObject facetConfiguration)
		throws Exception {

		JSONObject processorParams =
			facetConfiguration.getJSONObject("processor_params");

		String fieldName = processorParams.getString("field_name");

		JSONArray aggregations = processorParams.getJSONArray("aggregations");

		FacetCollector facetCollector = getFacetCollector(facets, fieldName);

		if (facetCollector == null) {
			return null;
		}

		Map<String, Integer> termMap = new HashMap<String, Integer>();

		// First aggregate frequency counts

		boolean mappingFound = false;

		for (TermCollector tc : facetCollector.getTermCollectors()) {

			// Check for empty term value!

			if (Validator.isNull(tc.getTerm())) {
				continue;
			}

			mappingFound = false;

			for (int i = 0; i < aggregations.length(); i++) {

				JSONObject aggregation = aggregations.getJSONObject(i);

				String key = aggregation.getString("key");
				String[] values = aggregation.getString("values").split(",");

				for (int j = 0; j < values.length; j++) {
					if (values[j].equals(tc.getTerm())) {
						if (termMap.get(key) != null) {
							int newValue = termMap.get(key) + tc.getFrequency();
							termMap.put(key, newValue);
						}
						else {
							termMap.put(key, tc.getFrequency());;
						}
						mappingFound = true;
						continue;
					}
				}
			}

			if (!mappingFound) {
				termMap.put(tc.getTerm(), tc.getFrequency());;
			}
		}

		// Then build JSON array

		JSONArray termArray = JSONFactoryUtil.createJSONArray();

		for (Entry<String, Integer> entry : termMap.entrySet()) {

			JSONObject item = JSONFactoryUtil.createJSONObject();

			item.put("frequency", entry.getValue());
			item.put("name", entry.getKey());
			item.put("term", entry.getKey());

			termArray.put(item);
		}

		return createResultObject(termArray, fieldName, facetConfiguration);
	}

	private static final String NAME = "file_extension";
}
