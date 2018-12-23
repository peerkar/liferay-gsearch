
package fi.soveltia.liferay.gsearch.mini.web.action;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

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
import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.params.QueryParamsBuilder;
import fi.soveltia.liferay.gsearch.mini.web.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.mini.web.constants.GSearchMiniPortletKeys;
import fi.soveltia.liferay.gsearch.mini.web.constants.GSearchMiniResourceKeys;

/**
 * Resource command for getting suggestions (autocomplete).
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.mini.web.configuration.ModuleConfiguration",
	immediate = true, 
	property = {
		"javax.portlet.name=" + GSearchMiniPortletKeys.GSEARCH_MINIPORTLET,
		"mvc.command.name=" + GSearchMiniResourceKeys.GET_CONTENT_SUGGESTIONS
	}, 
	service = MVCResourceCommand.class
)
public class GetContentSuggestionsMVCResourceCommand
	extends BaseMVCResourceCommand {

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
		
		// Build query parameters object.

		QueryParams queryParams = null;
		
		try {

			// Build basic params.
						
			queryParams = _queryParamsBuilder.buildQueryParams(resourceRequest,
				_moduleConfiguration.contentSuggestionsCount());

			// Asset publisher page.  
			
			queryParams.setAssetPublisherPageURL(_moduleConfiguration.assetPublisherPage());
			
			// Show results in context.
			
			queryParams.setViewResultsInContext(_moduleConfiguration.isViewResultsInContext());
				
		} catch (PortalException e) {

			_log.error(e.getMessage(), e);

			return;
		}

		// Try to get search results.
		
		JSONObject responseObject = null;

		try {
			responseObject = _gSearch.getSearchResults(
				resourceRequest, resourceResponse, queryParams);	
			
			// Localize result types.
			
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
	 * Get localization.
	 * 
	 * @param key
	 * @param locale
	 * @param objects
	 * @return
	 */
	protected String getLocalization(
		String key, Locale locale, Object... objects) {

		if (_resourceBundle == null) {
			_resourceBundle = _resourceBundleLoader.loadResourceBundle(locale);
		}

		String value =
			ResourceBundleUtil.getString(_resourceBundle, key, objects);

		return value == null ? _language.format(locale, key, objects) : value;
	}
		
	/**
	 * Localize result types
	 * 
	 * This is not in the templates because of https://issues.liferay.com/browse/LPS-75141
	 * 
	 * @param portletRequest
	 * @param responseObject
	 * @throws JSONException 
	 */
	protected void setResultTypeLocalizations(PortletRequest portletRequest, JSONObject responseObject) throws JSONException {

		
		String[]configuration = _configurationHelper.getAssetTypeConfiguration();
		
		Locale locale = portletRequest.getLocale();
		
		JSONArray items = responseObject.getJSONArray("items");
		
		if (items == null || items.length() == 0) {
			return;
		}
		
		for (int i = 0; i < items.length(); i++) {
			
			JSONObject resultItem = items.getJSONObject(i);

			System.out.println(resultItem.getString("type"));

			for (int j = 0; j < configuration.length; j++) {
				
				JSONObject configurationItem = JSONFactoryUtil.createJSONObject(configuration[j]);

				if (resultItem.getString("type").equalsIgnoreCase(configurationItem.getString("entry_class_name"))) {
					resultItem.put("key", configurationItem.getString("key"));
					break;
				}
			}
			
			resultItem.put(
				"type", getLocalization(
					resultItem.getString("type").toLowerCase(), locale));
			

		}
	}		
	
	private static final Logger _log =
		LoggerFactory.getLogger(GetContentSuggestionsMVCResourceCommand.class);
	
	@Reference
	private ConfigurationHelper _configurationHelper;

	@Reference
	private GSearch _gSearch;

	@Reference
	private Language _language;

	private volatile ModuleConfiguration _moduleConfiguration;

	@Reference
	private QueryParamsBuilder _queryParamsBuilder;
	
	private ResourceBundle _resourceBundle;

	@Reference(
		target = "(bundle.symbolic.name=fi.soveltia.liferay.gsearch.mini.web)", 
		unbind = "-"
	)
	private ResourceBundleLoader _resourceBundleLoader;
}
