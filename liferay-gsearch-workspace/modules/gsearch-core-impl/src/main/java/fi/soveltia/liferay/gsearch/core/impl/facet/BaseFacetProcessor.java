
package fi.soveltia.liferay.gsearch.core.impl.facet;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.search.aggregation.AggregationResult;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.TermsAggregationResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import fi.soveltia.liferay.gsearch.core.api.constants.FacetConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.FacetConfigurationValues;
import fi.soveltia.liferay.gsearch.core.api.facet.FacetProcessor;
import fi.soveltia.liferay.gsearch.core.api.params.FacetParameter;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Abstract base facet processor class.
 *
 * @author Petteri Karttunen
 */
public abstract class BaseFacetProcessor implements FacetProcessor {

	public abstract String getName();

	public String getParamName(JSONObject facetConfiguration) {
		return facetConfiguration.getString(FacetConfigurationKeys.PARAM_NAME);
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

		// Add facet parameter.

		FacetParameter facetParam = new FacetParameter(
			fieldName, Arrays.asList(parameterValues), allowMultipleValues,
			multiValueOperator, filterMode);

		facetParameters.add(facetParam);
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
		
		JSONArray termArray = JSONFactoryUtil.createJSONArray();

        for (Bucket bucket : facetResult.getBuckets()) {
 
    		JSONObject item = JSONFactoryUtil.createJSONObject();

			item.put(FacetConfigurationKeys.FREQUENCY,bucket.getDocCount());
			item.put(FacetConfigurationKeys.NAME, bucket.getKey());
			item.put(FacetConfigurationKeys.VALUE, bucket.getKey());

			termArray.put(item);
		}

		return createResultObject(termArray, getParamName(facetConfiguration), facetConfiguration);
	}

	/**
	 * Creates a single facet result object.
	 * 
	 * @param termArray
	 * @param fieldName
	 * @param facetConfiguration
	 * @return
	 */
	protected JSONObject createResultObject(
		JSONArray termArray, String fieldName, JSONObject facetConfiguration) {

		if (termArray.length() == 0) {
			return null;
		}
		
		JSONObject resultItem = JSONFactoryUtil.createJSONObject();

		resultItem.put(FacetConfigurationKeys.PARAM_NAME, 
				facetConfiguration.get(FacetConfigurationKeys.PARAM_NAME));
		
		resultItem.put(FacetConfigurationKeys.VALUES, termArray);

		return resultItem;
	}

	/***
	 * Gets collector for the given facet.
	 * 
	 * @param facets
	 * @param fieldName
	 * @return
	 */
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