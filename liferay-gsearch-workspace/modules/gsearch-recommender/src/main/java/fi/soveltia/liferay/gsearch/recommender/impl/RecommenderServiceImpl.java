
package fi.soveltia.liferay.gsearch.recommender.impl;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.search.query.MoreLikeThisQuery.DocumentIdentifier;
import com.liferay.portal.search.query.Queries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.GSearch;
import fi.soveltia.liferay.gsearch.core.api.configuration.CoreConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationValues;
import fi.soveltia.liferay.gsearch.core.api.constants.ConfigurationNames;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.constants.QueryContextContributorNames;
import fi.soveltia.liferay.gsearch.core.api.constants.ResponseKeys;
import fi.soveltia.liferay.gsearch.core.api.params.ParameterBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContextBuilder;
import fi.soveltia.liferay.gsearch.recommender.api.RecommenderService;
import fi.soveltia.liferay.gsearch.recommender.configuration.ModuleConfiguration;

/**
 * Recommender service.
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.recommender.configuration.ModuleConfiguration", 
	immediate = true, 
	service = RecommenderService.class
)
public class RecommenderServiceImpl implements RecommenderService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject getRecommendations(QueryContext queryContext) throws Exception {

		return getRecommendationsByAssetEntries(null, queryContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject getRecommendationsByAssetEntries(
			List<AssetEntry> assetEntryList, QueryContext queryContext)
					throws Exception {
		
		if (assetEntryList != null && !assetEntryList.isEmpty()) {
		
			HttpServletRequest httpServletRequest = (HttpServletRequest)
					queryContext.getParameter(ParameterNames.HTTP_SERVLET_REQUEST);

			Locale locale = (Locale)queryContext.getParameter(ParameterNames.LOCALE);
			
			// Resolve doc UIDS
			
			int count = queryContext.getPageSize() - queryContext.getStart();
			
			DocumentIdentifier[] documentIdentifiers = 
					_resolveDocUidsByAssetEntry(httpServletRequest, locale, assetEntryList, count);
			
			if (documentIdentifiers != null) {
				queryContext.setParameter(ParameterNames.DOCUMENT_IDENTIFIERS, documentIdentifiers);
			}
		}
		
		_setRecommendationConfigurations(queryContext);
		
		// Parse filter parameters.
		
		_filterParamBuilder.addParameter(queryContext);
		
		// Disable query post processors.
		
		queryContext.setQueryPostProcessorsEnabled(false); 
		
		// Try to get search results.

		JSONObject responseObject = JSONFactoryUtil.createJSONObject();

		try {
			responseObject = _gSearch.getSearchResults(queryContext);
		}
		catch (Exception e) {

			_log.error(e.getMessage(), e);

		}

		return responseObject;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject getRecommendationsByAssetEntryIds(Long[] assetEntryIds, QueryContext queryContext) throws Exception {

		if (assetEntryIds == null ) {
			
			if (_log.isWarnEnabled()) {
				_log.warn("Assetentry array was null.");
			}
			
			return getRecommendationsByAssetEntries(null, queryContext);
		}

		List<AssetEntry>assetEntryList = new ArrayList<AssetEntry>();
		
		for (long id : assetEntryIds) {
			
			AssetEntry assetEntry = _getAssetEntry(id);
			
			if (assetEntry != null ) {
				assetEntryList.add(assetEntry);
			}
		}
		
		return getRecommendationsByAssetEntries(assetEntryList, queryContext);
		
	}
	
	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) {
		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}

	/**
	 * Gets Asset entry.
	 * 
	 * @param assetEntryId
	 * @return
	 * @throws Exception
	 */
	private AssetEntry _getAssetEntry(long assetEntryId)
		throws Exception {

		try {
			return _assetEntryLocalService.getEntry(assetEntryId);
		}
		catch (PortalException e) {

			_log.warn("Asset Entry not found for ID: " + assetEntryId);
		}
		return null;
	}
		
	/**
	 * Gets query clause configuration for resolving document UID.
	 *
	 * @param assetEntryList
	 * @return
	 * @throws JSONException
	 */
	private JSONArray _getResolverClauseConfiguration(List<AssetEntry> assetEntryList)
		throws JSONException {

		// Build query keywords from the asset list.
		// This way we can get all the doc uids in a single query.
		
		StringBundler keywords = new StringBundler();
		
		int i = 0;
		for (AssetEntry assetEntry : assetEntryList) {
			
			if (i > _MAX_DOC_UIDS) {
				break;
			}
	
			keywords.append("(");
			keywords.append(Field.ENTRY_CLASS_PK);
			keywords.append(":");
			keywords.append(assetEntry.getClassPK());
			keywords.append(" AND ");
			keywords.append(Field.GROUP_ID);
			keywords.append(":");
			keywords.append(assetEntry.getGroupId());
			keywords.append(") ");
		}

		JSONObject clause = JSONFactoryUtil.createJSONObject();

		JSONArray clauses = JSONFactoryUtil.createJSONArray();

		JSONObject clause1 = JSONFactoryUtil.createJSONObject();
		clause1.put(ClauseConfigurationKeys.QUERY_TYPE, ClauseConfigurationValues.QUERY_TYPE_STRING_QUERY);
		clause1.put(ClauseConfigurationKeys.OCCUR, ClauseConfigurationValues.OCCUR_MUST);
		
		JSONObject queryConfiguration = JSONFactoryUtil.createJSONObject();
		queryConfiguration.put("query", keywords.toString());

		clause1.put(ClauseConfigurationKeys.CONFIGURATION, queryConfiguration);
		
		clauses.put(clause1);
		
		clause.put(ClauseConfigurationKeys.ENABLED, true);
		clause.put(ClauseConfigurationKeys.CONDITIONS, JSONFactoryUtil.createJSONArray());
		clause.put(ClauseConfigurationKeys.CLAUSES, clauses);
		
		JSONArray configuration = JSONFactoryUtil.createJSONArray();
		configuration.put(clause);
		
		return configuration;
	}

	/**
	 * Tries to resolve the documentd uids 
	 * for the given Asset entry list.
	 * 
	 * @param assetEntryList
	 * @return
	 * @throws Exception
	 */
	private DocumentIdentifier[] _resolveDocUidsByAssetEntry(
			HttpServletRequest httpServletRequest, 
			Locale locale, List<AssetEntry> assetEntryList, int count)
		throws Exception {

		QueryContext queryContext = _queryContextBuilder.buildQueryContext(
				httpServletRequest,
				locale, null);

		queryContext.setConfiguration(
			ConfigurationNames.CLAUSE,
			_getResolverClauseConfiguration(assetEntryList));

		// Process query context contributors.
	
		_queryContextBuilder.processQueryContextContributor(
				queryContext, QueryContextContributorNames.CONTEXT);
		
		queryContext.setStart(0);
		queryContext.setPageSize(count);
		queryContext.setQueryPostProcessorsEnabled(false);

		queryContext.setParameter(
			ParameterNames.VIEW_RESULTS_IN_CONTEXT, false);

		Map<String, Class<?>> additionalResultFields =
			new HashMap<String, Class<?>>();
		additionalResultFields.put(Field.UID, String.class);
		queryContext.setParameter(
			ParameterNames.ADDITIONAL_RESULT_FIELDS, additionalResultFields);
		
		JSONObject responseObject = _gSearch.getSearchResults(queryContext);
		
		JSONArray items = responseObject.getJSONArray(ResponseKeys.ITEMS);

		if (items == null || items.length() == 0) {
			return null;
		}
		
		long companyId = (long)queryContext.getParameter(ParameterNames.COMPANY_ID);
	
		String type = null;

		String index = _coreConfigurationHelper.getLiferayIndexName(companyId);
		
		DocumentIdentifier[] documentIdentifiers =  new DocumentIdentifier[items.length()];

		for (int i = 0; i < items.length(); i ++) {

			JSONObject item = items.getJSONObject(i);

			String id =  item.getString(Field.UID);

			DocumentIdentifier d = _queries.documentIdentifier(index, type, id);

			documentIdentifiers[i] =  d;
		}
		
		return documentIdentifiers;
	}	

	/**
	 * Set configurations.
	 * 
	 * @param queryContext
	 */
	private void _setRecommendationConfigurations(QueryContext queryContext) {

		if (queryContext.getConfiguration(ConfigurationNames.CLAUSE) == null) {

			JSONArray configuration = _coreConfigurationHelper.
					stringArrayToJSONArray(
							_moduleConfiguration.recommendationClauses());
				
			queryContext.setConfiguration(
					ConfigurationNames.CLAUSE, configuration);
		}

		if (queryContext.getConfiguration(ConfigurationNames.FILTER) == null) {
			
			JSONArray configuration = _coreConfigurationHelper.
					stringArrayToJSONArray(
							_moduleConfiguration.recommendationClauses());

			queryContext.setConfiguration(
					ConfigurationNames.FILTER, configuration);
		}
	}
	
	private static final Logger _log =
			LoggerFactory.getLogger(RecommenderServiceImpl.class);

	private static final int _MAX_DOC_UIDS = 15;
	
	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private CoreConfigurationHelper _coreConfigurationHelper;
 
	@Reference
	private ParameterBuilder _filterParamBuilder;

	@Reference
	private GSearch _gSearch;

	private volatile ModuleConfiguration _moduleConfiguration;
	
	@Reference
	private QueryContextBuilder _queryContextBuilder;	
	
	@Reference
	private Queries _queries;
}
