
package fi.soveltia.liferay.gsearch.morelikethis.portlet.action;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.PortletPreferences;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContextBuilder;
import fi.soveltia.liferay.gsearch.localization.LocalizationHelper;
import fi.soveltia.liferay.gsearch.morelikethis.constants.GSearchMoreLikeThisPortletKeys;
import fi.soveltia.liferay.gsearch.morelikethis.util.AssetHelper;
import fi.soveltia.liferay.gsearch.recommender.api.RecommenderService;

/**
 * Resource command for getting the search results.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	property = {
		"javax.portlet.name=" + GSearchMoreLikeThisPortletKeys.MORE_LIKE_THIS_PORTLET,
		"mvc.command.name=get_search_results"
	}, 
	service = MVCResourceCommand.class
)
public class GetSearchResultsMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {
 
		if (_log.isDebugEnabled()) {
			_log.debug("GetSearchResultsMVCResourceCommand.doServeResource()");
		}

		AssetEntry assetEntry = null;

		try {

			// Try to find an asset entry shown in the Asset Publisher.

			assetEntry = _assetHelper.findAssetEntry(resourceRequest);
			
		}
		catch (Exception e) {

			_log.warn("Assetentry not found.");
		}

		JSONObject responseObject = null;

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay) resourceRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			HttpServletRequest httpServletRequest =
				_portal.getHttpServletRequest(resourceRequest);

			PortletPreferences preferences = resourceRequest.getPreferences();

			// Try to resolve the index document UID.

			QueryContext resolverQueryContext = buildResolverQueryContext(
				httpServletRequest, themeDisplay, preferences);

			String docUID = _recommenderService.resolveDocUIDByAssetEntry(
				resolverQueryContext, assetEntry);

			// Try to get search results.

			if (docUID != null) {

				QueryContext searchQueryContext = buildSearchQueryContext(
					httpServletRequest, themeDisplay, preferences);

				responseObject = _recommenderService.getRecommendationsByDocUID(
					searchQueryContext, new String[] {
						docUID
					});
				
				_localizationHelper.setResultTypeLocalizations(
					themeDisplay.getLocale(), responseObject);
			}

		}
		catch (Exception e) {
			_log.error(e, e);
		}

		// Write response to output stream.

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, responseObject);
	}

	private QueryContext buildResolverQueryContext(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay,
		PortletPreferences preferences)
		throws JSONException, Exception {

		QueryContext queryContext = _queryContextBuilder.buildQueryContext(
			httpServletRequest, themeDisplay.getCompanyId(),
			themeDisplay.getLocale(), null);

		queryContext.setConfiguration(
			ConfigurationKeys.CLAUSE,
			getResolveUIDClauseConfiguration(preferences));
		queryContext.setParameter(
			ParameterNames.USER_ID, themeDisplay.getUserId());

		return queryContext;
	}

	private QueryContext buildSearchQueryContext(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay,
		PortletPreferences preferences)
		throws JSONException, Exception {

		QueryContext queryContext = _queryContextBuilder.buildQueryContext(
			httpServletRequest, getFilterConfiguration(preferences),
			getClauseConfiguration(preferences),
			_configurationHelper.getFacetConfiguration(), null, null);

		queryContext.setParameter(
			ParameterNames.COMPANY_ID, themeDisplay.getCompanyId());
		queryContext.setParameter(
			ParameterNames.LOCALE, themeDisplay.getLocale());
		queryContext.setParameter(
			ParameterNames.USER_ID, themeDisplay.getUserId());
		queryContext.setPageSize(
			GetterUtil.getInteger(preferences.getValue("itemsToShow", "5")));
		queryContext.setStart(0);
		
		String resultLayout = preferences.getValue("resultLayout", "list");

		// Layout options.

		if (resultLayout.equals("thumbnailList") ||
			resultLayout.equals("image")) {
			queryContext.setParameter(ParameterNames.INCLUDE_THUMBNAIL, true);
		}

		if (resultLayout.equals("userImageList")) {

			queryContext.setParameter(
				ParameterNames.INCLUDE_USER_PORTRAIT, true);
		}

		queryContext.setParameter(
			ParameterNames.ASSET_PUBLISHER_URL,
			preferences.getValue("assetPublisherPage", "/viewasset"));

		queryContext.setParameter(
			ParameterNames.VIEW_RESULTS_IN_CONTEXT, GetterUtil.getBoolean(
				preferences.getValue("showResultsInContext", "true")));
	
		return queryContext;

	}

	/**
	 * Get filter configuration from portlet instance preferences.
	 * 
	 * @param preferences
	 * @return
	 * @throws JSONException
	 */
	private String[] getFilterConfiguration(PortletPreferences preferences)
		throws JSONException {

		JSONArray json = JSONFactoryUtil.createJSONArray(
			preferences.getValue("filters", null));

		if (json == null || json.length() == 0) {
			return new String[0];
		}

		String[] configuration = new String[json.length()];

		for (int i = 0; i < json.length(); i++) {
			configuration[i] = json.getString(i);
		}

		return configuration;
	}		

	/**
	 * Getclause configuration from portlet instance preferences.
	 * 
	 * @param preferences
	 * @return
	 * @throws JSONException
	 */
	private String[] getClauseConfiguration(PortletPreferences preferences)
		throws JSONException {

		JSONArray queryConfiguration = JSONFactoryUtil.createJSONArray(
			preferences.getValue("moreLikeThisClauses", null));

		if (queryConfiguration == null || queryConfiguration.length() == 0) {
			return new String[0];
		}

		String[] configuration = new String[queryConfiguration.length()];

		for (int i = 0; i < queryConfiguration.length(); i++) {
			configuration[i] = queryConfiguration.getString(i);
		}

		return configuration;
	}

	/**
	 * Get query clause configuration for resolving document UID.
	 * 
	 * @param preferences
	 * @return
	 * @throws JSONException
	 */
	private String[] getResolveUIDClauseConfiguration(
		PortletPreferences preferences)
		throws JSONException {

		JSONArray queryConfiguration = JSONFactoryUtil.createJSONArray(
			preferences.getValue("resolveUIDClauses", null));

		if (queryConfiguration == null || queryConfiguration.length() == 0) {
			return new String[0];
		}

		String[] configuration = new String[queryConfiguration.length()];

		for (int i = 0; i < queryConfiguration.length(); i++) {
			configuration[i] = queryConfiguration.getString(i);
		}

		return configuration;
	}

	@Reference
	private AssetHelper _assetHelper;

	@Reference
	private ConfigurationHelper _configurationHelper;

	@Reference
	LocalizationHelper _localizationHelper;
	
	@Reference
	private Portal _portal;

	@Reference
	private QueryContextBuilder _queryContextBuilder;

	@Reference
	private RecommenderService _recommenderService;

	private static final Log _log =
		LogFactoryUtil.getLog(GetSearchResultsMVCResourceCommand.class);
	
	
}
