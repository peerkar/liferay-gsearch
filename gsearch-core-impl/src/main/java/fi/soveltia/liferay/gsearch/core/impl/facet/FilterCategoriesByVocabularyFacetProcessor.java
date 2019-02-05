
package fi.soveltia.liferay.gsearch.core.impl.facet;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.facet.FacetProcessor;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

@Component(
	immediate = true,
	service = FacetProcessor.class
)
public class FilterCategoriesByVocabularyFacetProcessor
	extends BaseFacetProcessor
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
	public JSONObject processFacetResults(
		QueryContext queryContext, Collection<Facet> facets,
		JSONObject facetConfiguration)
		throws Exception {

		JSONObject processorParams =
			facetConfiguration.getJSONObject("processor_params");

		String fieldName = processorParams.getString("field_name");

		Long vocabularyId = processorParams.getLong("vocabulary_id");

		Locale locale =
			(Locale) queryContext.getParameter(ParameterNames.LOCALE);

		FacetCollector facetCollector = getFacetCollector(facets, fieldName);

		if (facetCollector == null) {
			return null;
		}
		
		JSONArray termArray = JSONFactoryUtil.createJSONArray();

		if (vocabularyId > 0) {

			try {
				List<TermCollector> termCollectors =
					facetCollector.getTermCollectors();

				AssetVocabulary assetVocabulary =
					_assetVocabularyLocalService.getAssetVocabulary(
						vocabularyId);

				List<AssetCategory> assetCategories =
					assetVocabulary.getCategories();

				for (AssetCategory assetCategory : assetCategories) {

					for (TermCollector tc : termCollectors) {

						if ((Long.valueOf(
							tc.getTerm()) == assetCategory.getCategoryId())) {

							JSONObject item =
								JSONFactoryUtil.createJSONObject();

							item.put("frequency", tc.getFrequency());
							item.put("name", assetCategory.getTitle(locale));
							item.put("term", tc.getTerm());

							termArray.put(item);
						}
					}
				}

			}
			catch (PortalException e) {

				_log.warn(
					"Asset vocabulary " + vocabularyId + " defined in facets " +
						"configuration was not found.");
			}

		}
		return createResultObject(termArray, fieldName, facetConfiguration);
	}

	private static final Logger _log = LoggerFactory.getLogger(
		FilterCategoriesByVocabularyFacetProcessor.class);

	private static final String NAME = "filter_categories_by_vocabulary";

	@Reference
	AssetVocabularyLocalService _assetVocabularyLocalService;
}
