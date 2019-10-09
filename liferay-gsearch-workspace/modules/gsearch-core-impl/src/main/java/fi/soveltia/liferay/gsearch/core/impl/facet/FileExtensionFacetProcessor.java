
package fi.soveltia.liferay.gsearch.core.impl.facet;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.aggregation.AggregationResult;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.TermsAggregationResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.constants.FacetConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.FacetConfigurationValues;
import fi.soveltia.liferay.gsearch.core.api.facet.FacetProcessor;
import fi.soveltia.liferay.gsearch.core.api.params.FacetParameter;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Facet processor for document extension.
 *
 * @author Petteri Karttunen
 */
@Component(immediate = true, service = FacetProcessor.class)
public class FileExtensionFacetProcessor
	extends BaseFacetProcessor implements FacetProcessor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return _FIELD_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processFacetParameters(
			List<FacetParameter> facetParameters, String[] parameterValues,
			JSONObject facetConfiguration)
		throws Exception {		
		
		JSONObject processorParams = facetConfiguration.getJSONObject(
				FacetConfigurationKeys.PROCESSOR_PARAMS);

		String fieldName = facetConfiguration.getString(
				FacetConfigurationKeys.FIELD_NAME);
		
		String filterMode = processorParams.getString(
				FacetConfigurationKeys.FILTER_MODE, 
				FacetConfigurationValues.FILTER_MODE_PRE);
		
		String multiValueOperator = processorParams.getString(
				FacetConfigurationKeys.MULTI_VALUE_OPERATOR,
				FacetConfigurationValues.OPERATOR_OR);
		
		boolean allowMultipleValues = processorParams.getBoolean(
				FacetConfigurationKeys.ALLOW_MULTIPLE_VALUES, true);
		
		List<String> values = new ArrayList<>();

		JSONArray aggregations = processorParams.getJSONArray(FacetConfigurationKeys.AGGREGATIONS);

		for (String parameterValue : parameterValues) {
			String[] valueArray = null;

			for (int i = 0; i < aggregations.length(); i++) {
				JSONObject aggregation = aggregations.getJSONObject(i);

				if (aggregation.getString(FacetConfigurationKeys.AGGREGATION_KEY).equals(parameterValue)) {
					valueArray = aggregation.getString(
							FacetConfigurationKeys.AGGREGATION_VALUES
					).split(
						","
					);

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
			QueryContext queryContext, AggregationResult aggregationResult,
			JSONObject facetConfiguration)
		throws Exception {

		TermsAggregationResult facetResult = (TermsAggregationResult)aggregationResult;

		JSONObject processorParams = facetConfiguration.getJSONObject(
				FacetConfigurationKeys.PROCESSOR_PARAMS);
	
		JSONArray aggregations = 
				processorParams.getJSONArray(
						FacetConfigurationKeys.AGGREGATIONS);

		Map<String, Integer> termMap = new HashMap<>();

		// First aggregate frequency counts

		boolean mappingFound = false;

        for (Bucket bucket : facetResult.getBuckets()) {
        	
			// Check for empty term value!

			if (Validator.isNull(bucket.getKey())) {
				continue;
			}

			mappingFound = false;

			for (int i = 0; i < aggregations.length(); i++) {
				
				JSONObject aggregation = aggregations.getJSONObject(i);

				String key = aggregation.getString(
						FacetConfigurationKeys.AGGREGATION_KEY);
				String[] values = aggregation.getString(
					FacetConfigurationKeys.AGGREGATION_VALUES
				).split(
					","
				);

				for (int j = 0; j < values.length; j++) {
					
					if (values[j].equals(bucket.getKey())) {

						if (termMap.get(key) != null) {
							int newValue = termMap.get(key) + (int)bucket.getDocCount();
							termMap.put(key, newValue);
						}
						else {
							termMap.put(key, (int)bucket.getDocCount());
						}

						mappingFound = true;
					}
				}
			}

			if (!mappingFound) {
				termMap.put(bucket.getKey(), (int)bucket.getDocCount());
			}
		}

		// Sort terms
		
        Map<String, Integer> termMapOrdered = termMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
		
		// Then build JSON array.

		JSONArray termArray = JSONFactoryUtil.createJSONArray();

		for (Map.Entry<String, Integer> entry : termMapOrdered.entrySet()) {
			
			JSONObject item = JSONFactoryUtil.createJSONObject();
			
			item.put(FacetConfigurationKeys.FREQUENCY, entry.getValue());
			item.put(FacetConfigurationKeys.NAME, entry.getKey());
			item.put(FacetConfigurationKeys.VALUE, entry.getKey());

			termArray.put(item);
		}

		return createResultObject(termArray, getParamName(facetConfiguration), facetConfiguration);
	}

	private static final String _FIELD_NAME = "extension";

}