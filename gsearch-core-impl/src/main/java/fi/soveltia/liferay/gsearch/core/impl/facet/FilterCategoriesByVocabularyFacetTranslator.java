package fi.soveltia.liferay.gsearch.core.impl.facet;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.facet.FacetTranslator;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.impl.results.ResultsBuilderImpl;

@Component(
	immediate = true,
	service = FacetTranslator.class
)
public class FilterCategoriesByVocabularyFacetTranslator implements FacetTranslator  {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canProcess(String translatorName) {
		return NAME.equals(translatorName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] toQuery(String value, JSONObject configuration) {

		return new String[] {
			value
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONArray fromResults(
		QueryContext queryContext, FacetCollector facetCollector,
		JSONObject configuration)
		throws Exception {

		Locale locale = (Locale)queryContext.getParameter(ParameterNames.LOCALE);
		
		JSONObject translatorConfiguration = configuration.getJSONObject("translator_params");
	
		Long vocabularyId = translatorConfiguration.getLong("vocabulary_id");
		
		JSONArray termArray = JSONFactoryUtil.createJSONArray();
		
		if (vocabularyId > 0) {
			
			try {
				List<TermCollector> termCollectors =
								facetCollector.getTermCollectors();

				AssetVocabulary assetVocabulary = _assetVocabularyLocalService.
								getAssetVocabulary(vocabularyId);
				
				List<AssetCategory> assetCategories = assetVocabulary.getCategories();

				for (AssetCategory assetCategory : assetCategories) {

					for (TermCollector tc : termCollectors) {

						if ((Long.valueOf(tc.getTerm()) == assetCategory.getCategoryId())) {
							
							JSONObject item = JSONFactoryUtil.createJSONObject();

							item.put("frequency", tc.getFrequency());
							item.put("name", assetCategory.getTitle(locale));
							item.put("term", tc.getTerm());

							termArray.put(item);
						}
					}
				}

			} catch (Exception e) {
				
				_log.error(e.getMessage(), e);
			}
			
		}
		return termArray;
	}

	private static final Logger _log =
					LoggerFactory.getLogger(ResultsBuilderImpl.class);
	
	private static final String NAME = "filter_categories_by_vocabulary";
	
	@Reference
	AssetVocabularyLocalService _assetVocabularyLocalService;
}
