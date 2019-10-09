
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
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.portlet.PortletPreferences;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.configuration.CoreConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.constants.ResponseKeys;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContextBuilder;
import fi.soveltia.liferay.gsearch.localization.api.LocalizationHelper;
import fi.soveltia.liferay.gsearch.morelikethis.constants.GSearchMoreLikeThisPortletKeys;
import fi.soveltia.liferay.gsearch.morelikethis.constants.ModuleConfigurationKeys;
import fi.soveltia.liferay.gsearch.morelikethis.constants.ResourceRequestKeys;
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
		"mvc.command.name=" + ResourceRequestKeys.GET_SEARCH_RESULTS
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

			// Tries to find an asset entry shown in the Asset Publisher.

			assetEntry = _assetHelper.findAssetEntry(resourceRequest);
		}
		catch (Exception e) {
			_log.warn("Assetentry not found.");
		}

		JSONObject responseObject = JSONFactoryUtil.createJSONObject();


		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)resourceRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			HttpServletRequest httpServletRequest =
				_portal.getHttpServletRequest(resourceRequest);

			PortletPreferences preferences = resourceRequest.getPreferences();

			// Try to get search results.

			QueryContext queryContext = _buildQueryContext(
					httpServletRequest, themeDisplay, preferences);
			
			// Process query context contributors.
			
			_queryContextBuilder.processQueryContextContributors(queryContext);
				
			if (assetEntry != null) {

				List<AssetEntry>assetEntryList = new ArrayList<AssetEntry>();
				assetEntryList.add(assetEntry);
				responseObject = _recommenderService.
						getRecommendationsByAssetEntries(assetEntryList, queryContext);

			} else {
		
				responseObject = _recommenderService.getRecommendations(
						queryContext);
			}

			_processResultItems(themeDisplay.getLocale(), responseObject);
		}
		
		catch (Exception e) {
			_log.error(e, e);
		}

		// Write response to output stream.

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, responseObject);
	}

	/**
	 * Builds query context.
	 * 
	 * @param httpServletRequest
	 * @param themeDisplay
	 * @param preferences
	 * @return
	 * @throws Exception
	 * @throws JSONException
	 */
	private QueryContext _buildQueryContext(
			HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay,
			PortletPreferences preferences)
		throws Exception, JSONException {

		JSONArray clauseConfiguration = _getClauseConfiguration(preferences);
		JSONArray filterConfiguration = _getFilterConfiguration(preferences);
		
		String keywords = ParamUtil.getString(httpServletRequest, ParameterNames.KEYWORDS);
		
		QueryContext queryContext = _queryContextBuilder.buildQueryContext(
			httpServletRequest, themeDisplay.getLocale(), filterConfiguration,
			clauseConfiguration, _coreConfigurationHelper.getFacets(),
			null, null, null, keywords);

		queryContext.setPageSize(
			GetterUtil.getInteger(preferences.getValue(ModuleConfigurationKeys.ITEMS_TO_SHOW, "5")));
		
		queryContext.setStart(0);

		String resultLayout = preferences.getValue(ModuleConfigurationKeys.RESULT_LAYOUT, "list");

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
			preferences.getValue(ModuleConfigurationKeys.ASSET_PUBLISHER_PAGE, "/viewasset"));

		queryContext.setParameter(
			ParameterNames.VIEW_RESULTS_IN_CONTEXT,
			GetterUtil.getBoolean(
				preferences.getValue(ModuleConfigurationKeys.SHOW_RESULTS_IN_CONTEXT, "true")));

		// Asset tags.
		/*
		Map<String, Class<?>> additionalResultFields = new HashMap<>();

		additionalResultFields.put(Field.ASSET_TAG_NAMES, String[].class);

		queryContext.setParameter(
			ParameterNames.ADDITIONAL_RESULT_FIELDS, additionalResultFields);
		*/
		
		return queryContext;
	}

	/**
	 * Get clause configuration from portlet instance preferences.
	 *
	 * @param preferences
	 * @return
	 * @throws JSONException
	 */
	private JSONArray _getClauseConfiguration(PortletPreferences preferences)
		throws JSONException {

		return  JSONFactoryUtil.createJSONArray(
			preferences.getValue(ModuleConfigurationKeys.RECOMMENDATION_CLAUSES, null));
	}

	/**
	 * Get filter configuration from portlet instance preferences.
	 *
	 * @param preferences
	 * @return
	 * @throws JSONException
	 */
	private JSONArray _getFilterConfiguration(PortletPreferences preferences)
		throws JSONException {

		return  JSONFactoryUtil.createJSONArray(
			preferences.getValue(ModuleConfigurationKeys.FILTER_CLAUSES, null));
	}

	/**
	 * Processes result items for displaying.
	 *
	 * @param locale
	 * @param responseObject
	 */
	private void _processResultItems(Locale locale, JSONObject responseObject) {

		JSONArray items = responseObject.getJSONArray(ResponseKeys.ITEMS);

		if ((items == null) || (items.length() == 0)) {
			return;
		}

		for (int i = 0; i < items.length(); i++) {
			JSONObject resultItem = items.getJSONObject(i);

			resultItem.put(
				"type",
				_localizationHelper.getLocalization(
					locale,
					resultItem.getString(
						"type"
					).toLowerCase()));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetSearchResultsMVCResourceCommand.class);

	@Reference
	private AssetHelper _assetHelper;

	@Reference
	LocalizationHelper _localizationHelper;

	@Reference
	private CoreConfigurationHelper _coreConfigurationHelper;

	@Reference
	private Portal _portal;

	@Reference
	private QueryContextBuilder _queryContextBuilder;

	@Reference
	private RecommenderService _recommenderService;

}