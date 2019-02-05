
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
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

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
import fi.soveltia.liferay.gsearch.web.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.web.constants.GSearchPortletKeys;
import fi.soveltia.liferay.gsearch.web.constants.GSearchResourceKeys;
import fi.soveltia.liferay.gsearch.web.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.web.portlet.util.LocalizationHelper;

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

			// Build basic params.

			queryContext = _queryContextBuilder.buildQueryContext(
				resourceRequest, _moduleConfiguration.pageSize());

			// Set other context parameters

			setContextParameters(queryContext, resultLayout);

		}
		catch (PortalException e) {

			_log.error(e.getMessage(), e);

			return;
		}

		// Try to get search results.

		JSONObject responseObject = null;

		try {
			responseObject = _gSearch.getSearchResults(
				resourceRequest, resourceResponse, queryContext);

			// Set active result layout

			responseObject.put("resultsLayout", resultLayout);

			// Set result layout options.

			setResultLayoutOptions(
				resourceRequest, queryContext, responseObject, resultLayout);

			// Localize facets (This is SOY specific and done here because of
			// https://issues.liferay.com/browse/LPS-75141).

			setFacetLocalizations(resourceRequest, responseObject);

			// Localize result types (This is SOY specific and done here because
			// of https://issues.liferay.com/browse/LPS-75141).

			setResultTypeLocalizations(resourceRequest, responseObject);

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
			ParamUtil.getString(portletRequest, GSearchWebKeys.RESULTS_LAYOUT);

		String[] configuration = _moduleConfiguration.resultLayouts();

		String defaultResultLayout = null;

		for (int i = 0; i < configuration.length; i++) {

			JSONObject item =
				JSONFactoryUtil.createJSONObject(configuration[i]);

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

		if (_moduleConfiguration.isAssetCategoriesVisible()) {
			additionalResultFields.put("assetCategoryTitles", String[].class);
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

	/**
	 * Set context parameters.
	 * 
	 * @param queryContext
	 * @param resultLayout
	 */
	protected void setContextParameters(
		QueryContext queryContext, String resultLayout) {

		// Datepicker format

		queryContext.setParameter(
			ParameterNames.DATEPICKER_FORMAT,
			_moduleConfiguration.datePickerFormat());

		// Asset publisher page.

		queryContext.setParameter(
			ParameterNames.ASSET_PUBLISHER_URL,
			_moduleConfiguration.assetPublisherPage());

		// Show results in context.

		queryContext.setParameter(
			ParameterNames.VIEW_RESULTS_IN_CONTEXT,
			_moduleConfiguration.isViewResultsInContext());

		// Layout specific options

		if (resultLayout.equals("thumbnailList") ||
			resultLayout.equals("image")) {
			queryContext.setParameter(ParameterNames.INCLUDE_THUMBNAIL, true);
		}

		if (resultLayout.equals("userImageList") ||
			 resultLayout.equals("maps")) {

			queryContext.setParameter(
				ParameterNames.INCLUDE_USER_PORTRAIT, true);
		}
		
		// Set additional fields to include in results.

		setAdditionalResultFields(queryContext);
	}

	/**
	 * Add localizations to facets. This is not in the templates because of
	 * https://issues.liferay.com/browse/LPS-75141
	 * 
	 * @param portletRequest
	 * @param responseObject
	 */
	protected void setFacetLocalizations(
		PortletRequest portletRequest, JSONObject responseObject) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		Locale locale = themeDisplay.getLocale();

		JSONArray facets = responseObject.getJSONArray("facets");

		if (facets == null || facets.length() == 0) {
			return;
		}

		for (int i = 0; i < facets.length(); i++) {

			JSONObject resultItem = facets.getJSONObject(i);

			resultItem.put(
				"anyOption",
				_localizationHelper.getLocalization(
					locale,
					"any-" + resultItem.getString("param_name").toLowerCase()));

			resultItem.put(
				"multipleOption",
				_localizationHelper.getLocalization(
					locale, "multiple-" +
						resultItem.getString("param_name").toLowerCase()));

			JSONArray values = resultItem.getJSONArray("values");

			for (int j = 0; j < values.length(); j++) {

				JSONObject value = values.getJSONObject(j);

				value.put(
					"name", _localizationHelper.getLocalization(
						locale, value.getString("name").toLowerCase()));
			}
		}
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

	/**
	 * Localize result types This is not in the templates because of
	 * https://issues.liferay.com/browse/LPS-75141
	 * 
	 * @param portletRequest
	 * @param responseObject
	 */
	protected void setResultTypeLocalizations(
		PortletRequest portletRequest, JSONObject responseObject) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		Locale locale = themeDisplay.getLocale();

		JSONArray items = responseObject.getJSONArray("items");

		if (items == null || items.length() == 0) {
			return;
		}

		for (int i = 0; i < items.length(); i++) {

			JSONObject resultItem = items.getJSONObject(i);

			resultItem.put(
				"type", _localizationHelper.getLocalization(
					locale, resultItem.getString("type").toLowerCase()));

		}
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

		String paramFilterOperator =
			"and".equals(configurationItem.getString("param_filter_operator"))
				? "and" : "or";

		if (paramFiltersArray == null || paramFiltersArray.length() == 0) {
			return true;
		}

		for (int i = 0; i < paramFiltersArray.length(); i++) {

			JSONObject filter = paramFiltersArray.getJSONObject(i);

			String matchParameter = filter.getString("parameter");
			String matchValue = filter.getString("value");

			String paramValue =
				ParamUtil.getString(portletRequest, matchParameter, null);

			if (matchValue.equals(paramValue)) {

				if (paramFilterOperator.equals("or")) {
					return true;
				}

				if (i == paramFiltersArray.length() - 1) {
					return true;
				}

			}
			else {

				if (paramFilterOperator.equals("and")) {
					return false;
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
	private QueryContextBuilder _queryContextBuilder;

}
