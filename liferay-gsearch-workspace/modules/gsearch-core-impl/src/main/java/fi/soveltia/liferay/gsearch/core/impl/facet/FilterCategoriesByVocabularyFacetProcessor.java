
package fi.soveltia.liferay.gsearch.core.impl.facet;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.aggregation.AggregationResult;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.TermsAggregationResult;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.constants.FacetConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.facet.FacetProcessor;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Facet processor for vocabulary categories.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = FacetProcessor.class
)
public class FilterCategoriesByVocabularyFacetProcessor
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
	public JSONObject processFacetResults(
			QueryContext queryContext, AggregationResult aggregationResult,
			JSONObject facetConfiguration)
		throws Exception {

		TermsAggregationResult facetResult = (TermsAggregationResult)aggregationResult;
		
		JSONArray termArray = JSONFactoryUtil.createJSONArray();

		JSONObject processorParams = facetConfiguration.getJSONObject(
				FacetConfigurationKeys.PROCESSOR_PARAMS);

		Long vocabularyId = processorParams.getLong("vocabulary_id");

		if (vocabularyId > 0) {
			
			try {

				AssetVocabulary assetVocabulary =
					_assetVocabularyLocalService.getAssetVocabulary(
						vocabularyId);

				// Not matching vocabulary. Return.
				
				if (assetVocabulary.getVocabularyId() != vocabularyId) {
					return null;
				}
				
				Locale locale = (Locale)queryContext.getParameter(
						ParameterNames.LOCALE);

				List<AssetCategory> assetCategories =
					assetVocabulary.getCategories();

				for (AssetCategory assetCategory : assetCategories) {
					
			        for (Bucket bucket : facetResult.getBuckets()) {
						if (Long.valueOf(bucket.getKey()) ==
								assetCategory.getCategoryId()) {

				    		JSONObject item = JSONFactoryUtil.createJSONObject();
	
							item.put(FacetConfigurationKeys.FREQUENCY, 
									bucket.getDocCount());
							item.put(FacetConfigurationKeys.NAME, 
									assetCategory.getTitle(locale));
							item.put(FacetConfigurationKeys.VALUE, 
									bucket.getKey());
							termArray.put(item);
						}
					}
				}
			}
			catch (PortalException pe) {
				_log.warn(
					"Asset vocabulary " + vocabularyId + " defined in facets " +
						"configuration was not found.");
			}
		}

		return createResultObject(termArray, getParamName(facetConfiguration), facetConfiguration);
	}
	
	private static final String _FIELD_NAME = Field.ASSET_CATEGORY_IDS;

	private static final Logger _log = LoggerFactory.getLogger(
		FilterCategoriesByVocabularyFacetProcessor.class);

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;
}