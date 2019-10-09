package fi.soveltia.liferay.gsearch.react.web.portlet.action;

import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolver;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceURL;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.configuration.CoreConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.FacetConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.SortConfigurationKeys;
import fi.soveltia.liferay.gsearch.localization.api.LocalizationHelper;
import fi.soveltia.liferay.gsearch.react.web.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.react.web.constants.WebConfigurationKeys;
import fi.soveltia.liferay.gsearch.react.web.constants.GSearchReactWebPortletKeys;
import fi.soveltia.liferay.gsearch.react.web.constants.ResourceRequestKeys;

/**
 * View render command.
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.react.web.configuration.ModuleConfiguration",
	immediate = true, 
	property = {
		"javax.portlet.name=" + GSearchReactWebPortletKeys.GSEARCH_REACT_PORTLET,
		"mvc.command.name=/"
	}, 
	service = MVCRenderCommand.class
)
public class ViewMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		if (_log.isDebugEnabled()) {
			_log.debug("ViewMVCRenderCommand.render()");
		}

		renderRequest.setAttribute(
				"mainRequire", _npmResolver.resolveModuleName(NPM_MODULE_NAME) + " as main");

		try {
			renderRequest.setAttribute(
					"configuration",  getConfiguration(renderRequest, renderResponse));
		} catch (Exception e) {
			_log.error("UI configuration object could not be set.");
			throw new RuntimeException(e);
		}

		return "/view.jsp";
	}
	
	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) {
		_moduleConfiguration = ConfigurableUtil.createConfigurable(
				ModuleConfiguration.class, properties);
	}

	/**
	 * Creates resource URL for a resourceId.
	 * 
	 * @param renderResponse
	 * @param resourceId
	 * @return url string
	 */
	protected String createResourceURL(RenderResponse renderResponse, String resourceId) {

		ResourceURL portletURL = renderResponse.createResourceURL();

		portletURL.setResourceID(resourceId);

		return portletURL.toString();
	}

	/**
	 * Gets the UI configuration object.
	 * 
	 * @param renderRequest
	 * @throws Exception 
	 */
	protected JSONObject getConfiguration(RenderRequest renderRequest, 
			RenderResponse renderResponse) 
			throws Exception {

		JSONObject configuration = JSONFactoryUtil.createJSONObject();

		// Click tracking.
		
		configuration.put(WebConfigurationKeys.CLICK_TRACKING_CONFIG, getClickTrackingConfig());		
		
		// Facet config.

		configuration.put(WebConfigurationKeys.FACET_CONFIG, getFacetConfig());

		// Filter config.

		configuration.put(WebConfigurationKeys.FILTER_CONFIG, getFilterConfig());
		
		// Google Maps config.
		
		configuration.put(WebConfigurationKeys.GOOGLE_MAPS_CONFIG, getGoogleMapsConfig());


		// Result item config.

		configuration.put(WebConfigurationKeys.RESULT_ITEM_CONFIG, getResultItemConfig());

		// Search field config.
		
		configuration.put(WebConfigurationKeys.SEARCHFIELD_CONFIG, getSearchFieldConfig());
		
		// Sort config.

		configuration.put(WebConfigurationKeys.SORT_CONFIG, getSortConfig(renderRequest));

		// Url config.
		
		configuration.put(WebConfigurationKeys.URL_CONFIG, getURLConfig(renderResponse));
		
		return configuration;
	}
	
	/**
	 * Gets click tracking config.
	 * 
	 * @return
	 */
	protected JSONObject getClickTrackingConfig() {

		JSONObject config = JSONFactoryUtil.createJSONObject();

		config.put("enabled", _moduleConfiguration.isClickTrackingEnabled());
		
		return config;
	}
	
	/**
	 * Gets facets config.
	 * 
	 * Example:
	 *  {
	 *  	entryClassName: {
	 *  		color: "grey",
	 *  		icon: "edit icon-edit"
	 *  	}, ...
	 *  }
	 *  
	 *  @return
	 * @throws JSONException 
	 */
	protected JSONObject getFacetConfig() throws JSONException {
		
		JSONObject config = JSONFactoryUtil.createJSONObject();

		JSONArray configuration = _coreConfigurationHelper.getFacets();

		for (int i = 0; i < configuration.length(); i++) {

			JSONObject sourceItem = configuration.getJSONObject(i);
			
			if (!sourceItem.getBoolean(FacetConfigurationKeys.ENABLED, false)) {
				continue;
			}
			
			JSONObject facetItem = JSONFactoryUtil.createJSONObject();

			facetItem.put(FacetConfigurationKeys.COLOR, 
					sourceItem.get(FacetConfigurationKeys.COLOR));
			facetItem.put(FacetConfigurationKeys.ICON, 
					sourceItem.get(FacetConfigurationKeys.ICON));

			config.put(sourceItem.getString(
					FacetConfigurationKeys.PARAM_NAME), facetItem);
		}		 
		 
		return config;
	}
	
	/**
	 * Gets filters config.
	 * 
	 * @return
	 */
	protected JSONObject getFilterConfig() {

		JSONObject config = JSONFactoryUtil.createJSONObject();

		config.put(WebConfigurationKeys.SHOW_SCOPE_FILTER, 
				_moduleConfiguration.isScopeFilterVisible());
		config.put(WebConfigurationKeys.SHOW_TIME_FILTER,
				_moduleConfiguration.isTimeFilterVisible());
		config.put(WebConfigurationKeys.DATE_PICKER_DATE_FORMAT,
				_moduleConfiguration.datePickerFormat());		
		
		return config;
	}
	
	/**
	 * Gets Google Maps config.
	 * 
	 * @return
	 */
	protected JSONObject getGoogleMapsConfig() {
		
		JSONObject config = JSONFactoryUtil.createJSONObject();
	
		config.put(WebConfigurationKeys.GMAP_API_KEY, 
				_moduleConfiguration.googleMapsAPIKey());
	
		JSONObject center = JSONFactoryUtil.createJSONObject();
		center.put("lat", _moduleConfiguration.googleMapsDefaultCenterLat());
		center.put("lng", _moduleConfiguration.googleMapsDefaultCenterLng());
		config.put(WebConfigurationKeys.GMAP_DEFAULT_CENTER, center);
	
		return config;
	}
	
	/**
	 * Gets the result item config.
	 * 
	 * This sets the fields visible on the UI. 
	 * 
	 * @return
	 */
	protected JSONObject getResultItemConfig() {
		
		JSONObject config = JSONFactoryUtil.createJSONObject();
				
		config.put(WebConfigurationKeys.APPEND_REDIRECT, 
				_moduleConfiguration.isRedirectAppended());
		config.put(WebConfigurationKeys.SHOW_ASSET_CATEGORIES, 
				_moduleConfiguration.isAssetCategoriesVisible());
		config.put(WebConfigurationKeys.SHOW_ASSET_TAGS, 
				_moduleConfiguration.isAssetTagsVisible());
		config.put(WebConfigurationKeys.SHOW_AUTHOR, 
				_moduleConfiguration.isUserNameVisible());
		config.put(WebConfigurationKeys.SHOW_LINK, 
				_moduleConfiguration.isLinkVisible());
		config.put(WebConfigurationKeys.SHOW_TYPE, 
				_moduleConfiguration.isTypeVisible());

		return config;
	}

	/**
	 * Gets the searchfield config.
	 * 
	 * @return
	 */
	protected JSONObject getSearchFieldConfig() {

		JSONObject config = JSONFactoryUtil.createJSONObject();

		config.put(WebConfigurationKeys.QUERY_MIN_LENGTH, 
				_moduleConfiguration.queryMinLength());
		config.put(WebConfigurationKeys.KEYWORDS_PLACEHOLDER, 
				_moduleConfiguration.keywordsPlaceholder());
		config.put(WebConfigurationKeys.KEYWORD_SUGGESTER_ENABLED, 
				_moduleConfiguration.isKeywordSuggesterEnabled());
		config.put(WebConfigurationKeys.KEYWORD_SUGGESTER_REQUEST_DELAY,
				_moduleConfiguration.keywordSuggesterRequestDelay());
		
		return config;
	}

	/**
	 * Gets the sort config. 
	 * 
	 * Example:
	 *  {
	 *  	key: score,
	 *  	text: "Relevancy,
	 *  	value: score
	 *  }
	 * 
	 * @param portletRequest
	 * @return
	 * @throws JSONException
	 */
	protected JSONObject getSortConfig(PortletRequest portletRequest) 
			throws JSONException {
		
		JSONObject config = JSONFactoryUtil.createJSONObject();
		
		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.
				getAttribute(WebKeys.THEME_DISPLAY);

		JSONArray configuration =
				_coreConfigurationHelper.getSorts();

		String defaultValue = "";
		
		// Create options array.
		
		JSONArray options = JSONFactoryUtil.createJSONArray();

		for (int i = 0; i < configuration.length(); i++) {

			JSONObject item = configuration.getJSONObject(i);

			if (item.getBoolean(SortConfigurationKeys.DEFAULT, false)) {
				defaultValue = item.getString(SortConfigurationKeys.PARAM_NAME);
			}

			item.put("value", item.getString(SortConfigurationKeys.PARAM_NAME));
			item.put("text", _localizationHelper.getLocalization(
					themeDisplay.getLocale(), "sort-by-" + item.getString(
							SortConfigurationKeys.PARAM_NAME).toLowerCase()));

			// Remove not needed fields

			item.remove(SortConfigurationKeys.DEFAULT);
			item.remove(SortConfigurationKeys.FIELD_NAME);
			item.remove(SortConfigurationKeys.FIELD_TYPE);
			item.remove(SortConfigurationKeys.PARAM_NAME);
			
			options.put(item);
		}

		config.put(WebConfigurationKeys.OPTIONS, options);
		config.put(WebConfigurationKeys.DEFAULT_VALUE, defaultValue);
		
		return config;
	}
	
	/**
	 * Gets resource request URL config.
	 * 
	 * @param renderResponse
	 * @return
	 */
	protected JSONObject getURLConfig(RenderResponse renderResponse) {
		
		JSONObject config = JSONFactoryUtil.createJSONObject();

		config.put(WebConfigurationKeys.HELP_TEXT_URL, 
				createResourceURL(renderResponse, 
						ResourceRequestKeys.GET_HELP_TEXT));

		if (_moduleConfiguration.isClickTrackingEnabled()) {
			config.put(WebConfigurationKeys.CLICK_TRACKING_URL,
					createResourceURL(renderResponse, 
							ResourceRequestKeys.CLICK_TRACK));
		}
		
		config.put(WebConfigurationKeys.SEARCH_RESULTS_URL,
				createResourceURL(renderResponse, 
						ResourceRequestKeys.GET_SEARCH_RESULTS));

		config.put(WebConfigurationKeys.SUGGESTIONS_URL,
				createResourceURL(renderResponse, 
						ResourceRequestKeys.GET_SUGGESTIONS));

		return config;
	}
	
	@Reference
	private CoreConfigurationHelper _coreConfigurationHelper;

	@Reference
	private LocalizationHelper _localizationHelper;

	private volatile ModuleConfiguration _moduleConfiguration;

	@Reference
	private NPMResolver _npmResolver;	

	private static final Logger _log =
		LoggerFactory.getLogger(ViewMVCRenderCommand.class);

	private static final String NPM_MODULE_NAME = "gsearch-react-web";
}