
package fi.soveltia.liferay.gsearch.recommender.impl;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.GSearch;
import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.params.FilterParameter;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContextBuilder;
import fi.soveltia.liferay.gsearch.recommender.api.RecommenderService;
import fi.soveltia.liferay.gsearch.recommender.configuration.ModuleConfiguration;

/**
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.recommender.configuration.ModuleConfiguration", 
	immediate = true, 
	service = RecommenderService.class
)
public class RecommenderServiceImpl implements RecommenderService {

	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) {

		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}
	
	@Override
	public JSONObject getRecommendationsByDocUID(
		QueryContext queryContext, String[] docUID)
		throws Exception {

		// Set configurations.

		if (queryContext.getConfiguration(ConfigurationKeys.CLAUSE) == null) {
			queryContext.setConfiguration(
				ConfigurationKeys.CLAUSE,
				_moduleConfiguration.recommendationClauses());
		}

		if (queryContext.getConfiguration(ConfigurationKeys.FILTER) == null) {
			queryContext.setConfiguration(
				ConfigurationKeys.FILTER,
				_configurationHelper.getFilterConfiguration());
		}

		if (queryContext.getConfiguration(ConfigurationKeys.FACET) == null) {
			queryContext.setConfiguration(
				ConfigurationKeys.FACET,
				_configurationHelper.getFacetConfiguration());
		}

		queryContext.setParameter(ParameterNames.DOC_UID, docUID);

		queryContext.setQueryPostProcessorsEnabled(false);
		
		// Try to get search results.

		JSONObject responseObject = null;

		try {
			responseObject = _gSearch.getSearchResults(queryContext);
		}
		catch (Exception e) {

			_log.error(e.getMessage(), e);

		}

		return responseObject;
	}

	@Override
	public String resolveDocUIDByAssetEntryId(
		QueryContext queryContext, long assetEntryId)
		throws Exception {

		AssetEntry assetEntry;

		try {
			assetEntry = _assetEntryLocalService.getEntry(assetEntryId);
		}
		catch (PortalException e) {

			_log.warn("Asset Entry not found for ID: " + assetEntryId);
			return null;
		}

		return resolveDocUIDByAssetEntry(queryContext, assetEntry);
	}

	@Override
	public String resolveDocUIDByAssetEntry(
		QueryContext queryContext, AssetEntry assetEntry)
		throws Exception {

		// Use default config if not set.

		if (queryContext.getConfiguration(ConfigurationKeys.CLAUSE) == null) {
			queryContext.setConfiguration(
				ConfigurationKeys.CLAUSE,
				_moduleConfiguration.uidResolverClauses());
		}

		queryContext.setKeywords(String.valueOf(assetEntry.getClassPK()));
		queryContext.setStart(0);
		queryContext.setPageSize(1);
		queryContext.setQueryContributorsEnabled(false);
		queryContext.setQueryPostProcessorsEnabled(false);

		queryContext.setParameter(
			ParameterNames.GROUP_ID, new long[] {
				assetEntry.getGroupId()
			});

		List<String> entryClassNames = new ArrayList<String>();
		entryClassNames.add(assetEntry.getClassName());

		FilterParameter filter = new FilterParameter("entryClassName");
		filter.setAttribute("filterOccur", "must");
		filter.setAttribute("valueOccur", "must");
		filter.setAttribute("values", entryClassNames);
		queryContext.addFilterParameter("entryClassName", filter);
		queryContext.setParameter(
			ParameterNames.VIEW_RESULTS_IN_CONTEXT, false);

		Map<String, Class<?>> additionalResultFields =
			new HashMap<String, Class<?>>();
		additionalResultFields.put("uid", String.class);
		queryContext.setParameter(
			ParameterNames.ADDITIONAL_RESULT_FIELDS, additionalResultFields);
		
		String docUID = null;

		JSONObject responseObject = _gSearch.getSearchResults(queryContext);
		
		if (responseObject != null &&
			responseObject.getJSONArray("items").length() > 0) {

			JSONObject item =
				responseObject.getJSONArray("items").getJSONObject(0);
			docUID = item.getString("uid");
		}

		return docUID;
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private ConfigurationHelper _configurationHelper;

	@Reference
	private GSearch _gSearch;

	private volatile ModuleConfiguration _moduleConfiguration;
	
	@Reference
	private QueryContextBuilder _queryContextBuilder;

	private static final Logger _log =
					LoggerFactory.getLogger(RecommenderServiceImpl.class);
}
