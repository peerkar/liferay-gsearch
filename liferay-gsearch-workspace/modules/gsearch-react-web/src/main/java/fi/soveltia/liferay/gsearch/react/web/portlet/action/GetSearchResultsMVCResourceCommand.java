
package fi.soveltia.liferay.gsearch.react.web.portlet.action;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.search.Field;
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
import fi.soveltia.liferay.gsearch.core.api.constants.FacetConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.constants.ResponseKeys;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContextBuilder;
import fi.soveltia.liferay.gsearch.localization.api.LocalizationHelper;
import fi.soveltia.liferay.gsearch.react.web.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.react.web.constants.GSearchReactWebPortletKeys;
import fi.soveltia.liferay.gsearch.react.web.constants.ResourceRequestKeys;
import fi.soveltia.liferay.gsearch.react.web.constants.WebResponseKeys;

/**
 * Resource command for getting the search results.
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.react.web.configuration.ModuleConfiguration", 
	immediate = true, 
	property = {
		"javax.portlet.name=" + GSearchReactWebPortletKeys.GSEARCH_REACT_PORTLET,
		"mvc.command.name=" + ResourceRequestKeys.GET_SEARCH_RESULTS
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

		// Paese requested result layout value.

		String resultLayout = getResultLayoutParam(resourceRequest);

		try {

			ThemeDisplay themeDisplay = 
					(ThemeDisplay)resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
			
			HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(resourceRequest);

			String keywords = ParamUtil.getString(resourceRequest, ParameterNames.KEYWORDS);
			
			// Create query context
			
			queryContext = _queryContextBuilder.buildQueryContext(
				httpServletRequest, themeDisplay.getLocale(),
					null, null, null, null, null, null, keywords);

			// Process query context contributors.
			
			_queryContextBuilder.processQueryContextContributors(queryContext);

			// Parse request parameters.
			
			_queryContextBuilder.parseParameters(queryContext);

			// Set this apps specific parameters.

			setQueryContextParameters(resourceRequest, queryContext, resultLayout);
			
			// Set additional result fields to be included.

			setAdditionalResultFields(queryContext, resultLayout);


		}
		catch (PortalException e) {

			_log.error(e.getMessage(), e);

			return;
		}

		// Try to get search results.

		JSONObject responseObject = null;

		try {
			
			ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			responseObject = _gSearch.getSearchResults(queryContext);

			// Set active result layout

			responseObject.put(WebResponseKeys.RESULT_LAYOUT, resultLayout);
			
			// Set result layout options.
			
			if (responseObject.getJSONArray(ResponseKeys.ITEMS).length() > 0) {

				// Set result layout options.
				
				responseObject.put(
					WebResponseKeys.RESULTS_LAYOUT_OPTIONS, getResultLayoutOptions(resourceRequest));

				// Localize facets.

				processResultFacets(themeDisplay.getLocale(), responseObject);

				// Localize items.
				
				processResultItems(themeDisplay.getLocale(), responseObject);

			}
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
	 * Gets available result layout options for the response.
	 * 
	 * @param portletRequest
	 * @return
	 * @throws JSONException
	 */
	protected JSONArray getResultLayoutOptions(
		PortletRequest portletRequest) throws JSONException {

		ThemeDisplay themeDisplay =
				(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		String[] configuration = _moduleConfiguration.resultLayouts();

		JSONArray resultLayoutOptions = JSONFactoryUtil.createJSONArray();
		
		for (int i = 0; i < configuration.length; i++) {

			JSONObject configurationItem =
				JSONFactoryUtil.createJSONObject(configuration[i]);

			// Should we show the layout.
			
			if (!shouldShowResultLayout(portletRequest, configurationItem)) {
				continue;
			}

			JSONObject responseItem =
					JSONFactoryUtil.createJSONObject();

			responseItem.put(
					"key", configurationItem.getString("value"));

			responseItem.put(
					"text", _localizationHelper.getLocalization(
							themeDisplay.getLocale(), configurationItem.getString("text")));

			responseItem.put(
					"value", configurationItem.getString("value"));


			responseItem.put(
					"icon", configurationItem.getString("icon"));

			resultLayoutOptions.put(responseItem);
		}
		
		return resultLayoutOptions;
	}	

	/**
	 * Get and check result layout parameter. 
	 * Use the default if the requested value is not a valid layout.
	 * 
	 * @param portletRequest
	 * @throws JSONException
	 */
	protected String getResultLayoutParam(PortletRequest portletRequest)
		throws JSONException {

		String resultsLayoutParam =
			ParamUtil.getString(portletRequest, WebResponseKeys.RESULT_LAYOUT);

		String[] configuration = _moduleConfiguration.resultLayouts();

		String defaultResultLayout = null;

		for (int i = 0; i < configuration.length; i++) {

			JSONObject item =
				JSONFactoryUtil.createJSONObject(configuration[i]);

			if (resultsLayoutParam.equals(item.getString("value"))) {
				return resultsLayoutParam;
			}

			if (item.getBoolean("default") == true) {
				defaultResultLayout = item.getString("value");
			}
		}

		return defaultResultLayout;
	}

	/**
	 * Processes response facets for the UI.
	 * 
	 * @param locale
	 * @param responseObject
	 */
	protected void processResultFacets(
		Locale locale, JSONObject responseObject) {
		
		JSONArray facets = responseObject.getJSONArray(ResponseKeys.FACETS);

		if (facets == null || facets.length() == 0) {
			return;
		}
		
		for (int i = 0; i < facets.length(); i++) {

			JSONObject resultItem = facets.getJSONObject(i);
			
			resultItem.put(
				"anyOption", _localizationHelper.
				getLocalization(locale, "any-" + resultItem.getString(
						FacetConfigurationKeys.PARAM_NAME).toLowerCase()));
			
			
			JSONArray values = resultItem.getJSONArray(FacetConfigurationKeys.VALUES);

			for (int j = 0; j < values.length(); j++) {

				JSONObject value = values.getJSONObject(j);

				value.put(
				"text", _localizationHelper.getLocalization(
					locale, value.getString(FacetConfigurationKeys.NAME).toLowerCase()) + 
					" (" + value.getString(FacetConfigurationKeys.FREQUENCY) + ")");

				value.put("text_",_localizationHelper.getLocalization(
						locale, value.getString(FacetConfigurationKeys.NAME).toLowerCase()));
			}
		}
	}
	
	/**
	 * Processes response result items for the UI.
	 * 
	 * @param locale
	 * @param responseObject
	 */
	protected void processResultItems (
		Locale locale, JSONObject responseObject) {

		JSONArray items = responseObject.getJSONArray(ResponseKeys.ITEMS);

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
	
	
	/**
	 * Set additional fields to be included in results.
	 * 
	 * For the performance reasons, these fields are not included by default.
	 * 
	 * @param queryContext
	 * @param resultLayout
	 */
	protected void setAdditionalResultFields(QueryContext queryContext, String resultLayout) {

		Map<String, Class<?>> additionalResultFields =
			new HashMap<String, Class<?>>();

		// Asset tags.

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

		if (_moduleConfiguration.isUserNameVisible() || resultLayout.equals("card")) {

			additionalResultFields.put(Field.USER_NAME, String.class);
		}

		// Click tracking
		
		if (_moduleConfiguration.isClickTrackingEnabled()) {
			additionalResultFields.put(Field.ENTRY_CLASS_PK, Long.class);
		}
		
		if (additionalResultFields.size() > 0) {
			queryContext.setParameter(
				ParameterNames.ADDITIONAL_RESULT_FIELDS,
				additionalResultFields);
		}
	}

	/**
	 * Set query context parameters.
	 * 
	 * @param resourceRequest
	 * @param queryContext
	 * @param resultLayout
	 */
	protected void setQueryContextParameters(ResourceRequest resourceRequest,
		QueryContext queryContext, String resultLayout) {

		// Page size and start.
		
		queryContext.setPageSize(_moduleConfiguration.pageSize());
		
		queryContext.setStart(_getStart(resourceRequest, queryContext));
		
		// Asset publisher page.

		queryContext.setParameter(
			ParameterNames.ASSET_PUBLISHER_URL,
			_moduleConfiguration.assetPublisherPage());

		// Datepicker format.

		queryContext.setParameter(
			ParameterNames.DATE_FORMAT,
			_moduleConfiguration.datePickerFormat());

		// Show results in context.

		queryContext.setParameter(
			ParameterNames.VIEW_RESULTS_IN_CONTEXT,
			_moduleConfiguration.isViewResultsInContext());

		// Layout specific options.

		if (resultLayout.equals("thumbnail") ||
			resultLayout.equals("image")) {
			queryContext.setParameter(ParameterNames.INCLUDE_THUMBNAIL, true);
		}

		if (resultLayout.equals("userImage") ||
			 resultLayout.equals("maps")) {

			queryContext.setParameter(
				ParameterNames.INCLUDE_USER_PORTRAIT, true);
		}
		
		// Raw document layout.
		
		if (resultLayout.equals("document")) {
			queryContext.setParameter(ParameterNames.INCLUDE_RAW_DOCUMENT, true);
		}
		
		if (resultLayout.equals("explain")) {
			queryContext.setParameter(ParameterNames.SET_EXPLAIN, true);
		}
	}
	
	/**
	 * Finds out whether the requested result layout can be shown.
	 * 
	 * @param portletRequest
	 * @param configurationItem
	 * @return
	 */
	protected boolean shouldShowResultLayout(
		PortletRequest portletRequest, JSONObject configurationItem) {

		// Is enabled.
		
		if (!configurationItem.getBoolean("enabled", true)) {
			return false;
		}
		
		// Don't show maps layout if Google Maps API key not defined.

		if ("maps".equals(configurationItem.getString("key")) && 
				Validator.isBlank(_moduleConfiguration.googleMapsAPIKey())) {
			return false;
		}

		// Process filters.

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

	/**
	 * Get search start.
	 * 
	 * We are relying here on Semanticweb pagination component,
	 * but on the core level, still using the "start" param,
	 * which is better suitable for headless access.
	 * 
	 * @param resourceRequest
	 * @param queryContext
	 * @return
	 */
	private int _getStart(ResourceRequest resourceRequest, QueryContext queryContext) {

		int page = ParamUtil.getInteger(
				resourceRequest, ParameterNames.PAGE, 1);		

		if (page == 1) {
			return 0;
		} else {
			return (((page-1) * _moduleConfiguration.pageSize()) + 1);
		}
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

}
