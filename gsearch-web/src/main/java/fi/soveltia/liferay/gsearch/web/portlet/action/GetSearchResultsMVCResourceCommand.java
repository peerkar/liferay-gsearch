
package fi.soveltia.liferay.gsearch.web.portlet.action;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.GSearch;
import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.params.QueryParamsBuilder;
import fi.soveltia.liferay.gsearch.core.api.results.layout.ResultLayout;
import fi.soveltia.liferay.gsearch.core.api.results.layout.ResultLayoutService;
import fi.soveltia.liferay.gsearch.web.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.web.constants.GSearchPortletKeys;
import fi.soveltia.liferay.gsearch.web.constants.GSearchResourceKeys;

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

		JSONObject responseObject = null;

		// Build query parameters object.

		QueryParams queryParams = null;

		try {
			queryParams = _queryParamsBuilder.buildQueryParams(
				resourceRequest);
		}
		catch (PortalException e) {

			_log.error(e, e);

			return;
		}

		// Try to get search results.
		
		try {
			responseObject = _gSearch.getSearchResults(
				resourceRequest, resourceResponse, queryParams);
			
			// Result layouts

			responseObject.put("resultLayoutOptions", getResultLayoutOptions(resourceRequest));
			
		}
		catch (Exception e) {

			_log.error(e, e);

			return;
		}

		// Write response to output stream.

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, responseObject);
	}

	/**
	 * Get localization
	 * 
	 * @param key
	 * @param locale
	 * @param objects 
	 * @return
	 */
	protected String getLocalization(String key, Locale locale, Object...objects)  {
		
		if (_resourceBundle == null) {
			_resourceBundle = _resourceBundleLoader.loadResourceBundle(locale);
		}

		String value = ResourceBundleUtil.getString(_resourceBundle, key, objects);
			
		return value == null ? _language.format(locale, key, objects) : value;
	}			

	/**
	 * Get available result layout options. Merge information from portlet configuration.
	 * 
	 * @param resourceRequest
	 * @return
	 * @throws JSONException
	 */
	protected JSONArray getResultLayoutOptions(ResourceRequest resourceRequest) throws JSONException {
	
		Locale locale = resourceRequest.getLocale();
		
		String defaultResultLayoutKey = _resultLayoutService.getDefaultResultLayoutKey();
		
		JSONArray configurationArray = JSONFactoryUtil.createJSONArray(
			_moduleConfiguration.resultLayouts());

		JSONArray resultLayoutOptions = JSONFactoryUtil.createJSONArray();

		// Get the order (and existence) of layouts from configuration
		// Not configured layouts are not shown
		
		for (int i = 0; i < configurationArray.length(); i++) {

			JSONObject configurationItem = configurationArray.getJSONObject(i);

			for (ResultLayout resultLayout : _resultLayoutService.getAvailableResultLayouts(resourceRequest)) {
			
				JSONObject layoutItem = JSONFactoryUtil.createJSONObject();

				if (resultLayout.getKey().equals(defaultResultLayoutKey)) {
					layoutItem.put("default", true);
				}
				
				if (configurationItem.getString("key").equals(resultLayout.getKey())) {
					layoutItem.put("title", getLocalization(configurationItem.getString("title"), locale));
					layoutItem.put("cssClasses", configurationItem.getString("cssClasses"));
					layoutItem.put("icon", configurationItem.getString("icon"));
					layoutItem.put("key", configurationItem.getString("key"));
					
					resultLayoutOptions.put(layoutItem);
					
					break;
				}
			}
		}
		return resultLayoutOptions;			
	}		
	
	@Reference
	protected ConfigurationHelper _configurationHelperService;

	@Reference
	private GSearch _gSearch;

	@Reference
	private Language _language;

	private volatile ModuleConfiguration _moduleConfiguration;

	@Reference
	private QueryParamsBuilder _queryParamsBuilder;

	private ResourceBundle _resourceBundle;

	@Reference(
		target = "(bundle.symbolic.name=fi.soveltia.liferay.gsearch.web)", 
		unbind = "-"
	)
	private ResourceBundleLoader _resourceBundleLoader;

	@Reference
	private ResultLayoutService _resultLayoutService;

	private static final Log _log =
		LogFactoryUtil.getLog(GetSearchResultsMVCResourceCommand.class);
}
