
package fi.soveltia.liferay.gsearch.web.portlet.action;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.GSearch;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContextBuilder;
import fi.soveltia.liferay.gsearch.localization.LocalizationHelper;
import fi.soveltia.liferay.gsearch.recommender.api.RecommenderService;
import fi.soveltia.liferay.gsearch.web.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.web.constants.GSearchPortletKeys;
import fi.soveltia.liferay.gsearch.web.constants.GSearchResourceKeys;
import fi.soveltia.liferay.gsearch.web.constants.GSearchWebKeys;

/**
 * Resource command for getting the search results.
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.web.configuration.ModuleConfiguration", 
	immediate = true, 
	property = {
		"javax.portlet.name=" + GSearchPortletKeys.GSEARCH_PORTLET,
		"mvc.command.name=" + GSearchResourceKeys.GET_SEARCH_RESULTS
	}, 
	service = MVCResourceCommand.class
)
public class GetSearchResultsMVCResourceCommand extends BaseMVCResourceCommand {

	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) {

		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}

	@Override
	protected void doServeResource(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("GetSearchResultsMVCResourceCommand.doServeResource()");
		}

		// Build query context object.

		QueryContext queryContext = null;

		// Result layout

		String resultLayout = getResultLayoutParam(resourceRequest);

		try {

			HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(resourceRequest);

			queryContext = _queryContextBuilder.buildQueryContext(httpServletRequest,
				null, null, null, null, null);
			
			_queryContextBuilder.parseParameters(queryContext);
			
			// Set other context parameters

			setContextParameters(queryContext, resultLayout, getPageSize(resourceRequest));

		}
		catch (PortalException e) {

			_log.error(e.getMessage(), e);

			return;
		}

		// Try to get search results.

		JSONObject responseObject = null;

		try {
			responseObject = _gSearch.getSearchResults(queryContext);

			// Set active result layout

			responseObject.put("resultsLayout", resultLayout);

			// Set result layout options.

			setResultLayoutOptions(
				resourceRequest, queryContext, responseObject, resultLayout);

			// Localize facets (This is SOY specific and done here because of
			// https://issues.liferay.com/browse/LPS-75141).
			
			ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

			_localizationHelper.setFacetLocalizations(themeDisplay.getLocale(), responseObject);

			// Localize result types (This is SOY specific and done here because
			// of https://issues.liferay.com/browse/LPS-75141).

			_localizationHelper.setResultTypeLocalizations(themeDisplay.getLocale(), responseObject);

		}
		catch (Exception e) {

			_log.error(e.getMessage(), e);

			return;
		}

		// Write response to output stream.

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, responseObject);
	}

	/**
	 * Get and check result layout parameter. Use default if not valid layout
	 * 
	 * @param portletRequest
	 * @throws JSONException
	 */
	protected String getResultLayoutParam(PortletRequest portletRequest)
		throws JSONException {

		String resultsLayoutParam =
			ParamUtil.getString(portletRequest, GSearchWebKeys.RESULTS_LAYOUT, "list");

		String[] configuration = _moduleConfiguration.resultLayouts();

		String defaultResultLayout = null;

		for (int i = 0; i < configuration.length; i++) {

			JSONObject item =
				JSONFactoryUtil.createJSONObject(configuration[i]);


			if (!shouldShowResultLayout(portletRequest, resultsLayoutParam, item)) {
				resultsLayoutParam = "list";
			}

			if (resultsLayoutParam.equals(item.getString("key"))) {
				return resultsLayoutParam;
			}

			if (item.getBoolean("default") == true) {
				defaultResultLayout = item.getString("key");
			}
		}

		return defaultResultLayout;
	}

	/**
	 * Set additional fields to be included in results.
	 * 
	 * @param queryContext
	 */
	protected void setAdditionalResultFields(QueryContext queryContext) {

		// Asset tags.

		Map<String, Class<?>> additionalResultFields =
			new HashMap<String, Class<?>>();

		if (_moduleConfiguration.isAssetTagsVisible()) {
			additionalResultFields.put("assetTagNames", String[].class);
		}

		// Asset categories.
		
		Locale locale = (Locale)queryContext.getParameter(ParameterNames.LOCALE);

		if (_moduleConfiguration.isAssetCategoriesVisible()) {
			additionalResultFields.put(
				"assetCategoryTitles_" + locale.toString(), String[].class);
		}

		// Username.

		if (_moduleConfiguration.isUserNameVisible()) {

			additionalResultFields.put("userName", String.class);
		}

		if (additionalResultFields.size() > 0) {
			queryContext.setParameter(
				ParameterNames.ADDITIONAL_RESULT_FIELDS,
				additionalResultFields);
		}
	}

	private int getPageSize(ResourceRequest resourceRequest) {
		int pageSize = ParamUtil.getInteger(resourceRequest, "pageSize", -1);
		if (pageSize == -1) {
			pageSize = _moduleConfiguration.pageSize();
		}
		return Math.min(pageSize, MAX_PAGE_SIZE); // make sure to return a sensible value
	}

	/**
	 * Set context parameters.
	 * 
	 * @param queryContext
	 * @param resultLayout
	 */
	protected void setContextParameters(
		QueryContext queryContext, String resultLayout, int pageSize) {

		// Page size.
		
		queryContext.setPageSize(pageSize);
		
		// Datepicker format.

		queryContext.setParameter(
			ParameterNames.DATE_FORMAT,
			_moduleConfiguration.datePickerFormat());

		// Asset publisher page.

		queryContext.setParameter(
			ParameterNames.ASSET_PUBLISHER_URL,
			_moduleConfiguration.assetPublisherPage());

		// Show results in context.

		queryContext.setParameter(
			ParameterNames.VIEW_RESULTS_IN_CONTEXT,
			_moduleConfiguration.isViewResultsInContext());

		// Layout specific options.

		if (resultLayout.equals("thumbnailList") ||
			resultLayout.equals("image") ||
			resultLayout.equals("preview")) {
			queryContext.setParameter(ParameterNames.INCLUDE_THUMBNAIL, true);
		}
/*
		if (resultLayout.equals("userImageList") ||
			 resultLayout.equals("maps")) {

			queryContext.setParameter(
				ParameterNames.INCLUDE_USER_PORTRAIT, true);
		}
*/
		// HY
		queryContext.setParameter(
			ParameterNames.INCLUDE_USER_PORTRAIT, true);

		// Set additional fields to include in results.

		setAdditionalResultFields(queryContext);
	}


	/**
	 * Set available result layout options to response object.
	 * 
	 * @param portletRequest
	 * @param queryContext
	 * @param responseObject
	 * @param resultLayout
	 * @throws JSONException
	 */
	protected void setResultLayoutOptions(
		PortletRequest portletRequest, QueryContext queryContext,
		JSONObject responseObject, String resultLayout)
		throws Exception {

		if (responseObject.getJSONArray("items").length() == 0) {
			responseObject.put(
				GSearchWebKeys.RESULTS_LAYOUT_OPTIONS, JSONFactoryUtil.createJSONArray());
			return;
		}		
		
		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		Locale locale = themeDisplay.getLocale();

		String[] configuration = _moduleConfiguration.resultLayouts();

		JSONArray resultLayoutOptions = JSONFactoryUtil.createJSONArray();

		for (int i = 0; i < configuration.length; i++) {

			JSONObject configurationItem =
				JSONFactoryUtil.createJSONObject(configuration[i]);

			// Should we show the layout.
			
			if (!shouldShowResultLayout(portletRequest, resultLayout, configurationItem)) {
				continue;
			}
			configurationItem.put(
				"title", _localizationHelper.getLocalization(
					locale, configurationItem.getString("title")));

			resultLayoutOptions.put(configurationItem);
		}

		responseObject.put(
			GSearchWebKeys.RESULTS_LAYOUT_OPTIONS, resultLayoutOptions);
	}

	protected boolean shouldShowResultLayout(
		PortletRequest portletRequest, String resultLayout, JSONObject configurationItem) {

		// Is enabled

		if (!configurationItem.getBoolean("enabled", true)) {
			return false;
		}

		// Don't show maps layout if Google Maps API key not defined.

		if ("maps".equals(configurationItem.getString("key")) &&
			Validator.isNull(_moduleConfiguration.googleMapsAPIKey())) {
			return false;
		}

		// Process filters

		JSONArray paramFiltersArray =
			configurationItem.getJSONArray("param_filters");

		if (paramFiltersArray == null || paramFiltersArray.length() == 0) {
			return true;
		}

		if ("and".equals(configurationItem.getString("param_filter_operator"))) {
			return isParamFilterMatchWithAND(portletRequest, paramFiltersArray);
		} else {
			return isParamFilterMatchWithOR(portletRequest, paramFiltersArray);
		}
	}

	// must match exactly with all parameters and values
	private boolean isParamFilterMatchWithAND(PortletRequest portletRequest, JSONArray paramFiltersArray) {

		Map<String, List<String>> paramFilters = new HashMap<>();

		for (int i = 0; i < paramFiltersArray.length(); i++) {

			JSONObject filter = paramFiltersArray.getJSONObject(i);

			String matchParameter = filter.getString("parameter");
			String matchValue = filter.getString("value");

			if (paramFilters.containsKey(matchParameter)) {
				paramFilters.get(matchParameter).add(matchValue);
			} else {
				List<String> matchValues = new ArrayList<>();
				matchValues.add(matchValue);
				paramFilters.put(matchParameter, matchValues);
			}
		}

		for (Map.Entry<String, List<String>> entry : paramFilters.entrySet()) {
			String matchParameter = entry.getKey();
			List<String> matchValues = entry.getValue();
			List<String> requestParamValues = Arrays.asList(ParamUtil.getStringValues(portletRequest, matchParameter, new String[]{}));
			if (requestParamValues.size() != matchValues.size()) {
				return false;
			}
			if (!new HashSet<>(requestParamValues).equals(new HashSet<>(paramFilters.get(matchParameter)))) {
				return false;
			}
		}
		return true;
	}

	private boolean isParamFilterMatchWithOR(PortletRequest portletRequest, JSONArray paramFiltersArray) {
		for (int i = 0; i < paramFiltersArray.length(); i++) {

			JSONObject filter = paramFiltersArray.getJSONObject(i);

			String matchParameter = filter.getString("parameter");
			String matchValue = filter.getString("value");

			String[] paramValues = ParamUtil.getStringValues(portletRequest, matchParameter, new String[]{});

			for (String paramValue : paramValues) {
				if (matchValue.equals(paramValue)) {
					return true;
				}
			}
		}
		return false;
	}

	private static final Logger _log =
		LoggerFactory.getLogger(GetSearchResultsMVCResourceCommand.class);

	@Reference
	private GSearch _gSearch;

	@Reference
	private LocalizationHelper _localizationHelper;

	private volatile ModuleConfiguration _moduleConfiguration;

	@Reference 
	private Portal _portal;
	
	@Reference
	private QueryContextBuilder _queryContextBuilder;
	
	@Reference
	private RecommenderService _r;

}
