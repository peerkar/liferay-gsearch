
package fi.soveltia.liferay.gsearch.web.portlet.action;

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
import com.liferay.portal.kernel.util.ParamUtil;
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
import fi.soveltia.liferay.gsearch.core.api.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.params.QueryParamsBuilder;
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

		// Build query parameters object.

		QueryParams queryParams = null;

		// Result layout

		String resultLayout = getResultLayoutParam(resourceRequest);

		try {

			// Build basic params.

			queryParams = _queryParamsBuilder.buildQueryParams(
				resourceRequest, _moduleConfiguration.pageSize());

			// Asset publisher page.

			queryParams.setAssetPublisherPageURL(
				_moduleConfiguration.assetPublisherPage());

			// Show results in context.

			queryParams.setViewResultsInContext(
				_moduleConfiguration.isViewResultsInContext());

			// Set additional fields to include in results.

			setAdditionalResultFields(queryParams);

			// Set extra params.

			setExtraParams(resourceRequest, queryParams, resultLayout);

		}
		catch (PortalException e) {

			_log.error(e.getMessage(), e);

			return;
		}

		// Try to get search results.

		JSONObject responseObject = null;

		try {
			responseObject = _gSearch.getSearchResults(
				resourceRequest, resourceResponse, queryParams);

			// Set active result layout

			responseObject.put("resultsLayout", resultLayout);

			// Set result layout options.

			setResultLayoutOptions(
				resourceRequest, queryParams, responseObject, resultLayout);

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
	 * @param queryParams
	 */
	protected void setAdditionalResultFields(QueryParams queryParams) {

		// Asset tags

		if (_moduleConfiguration.isAssetTagsVisible()) {
			queryParams.addAdditionalResultField("assetTagNames", String[].class);
		}
	}

	/**
	 * Set query extra parameters.
	 * 
	 * @param portletRequest
	 * @param queryParams
	 */
	protected void setExtraParams(
		PortletRequest portletRequest, QueryParams queryParams,
		String resultLayout) {

		// Include thumbnail?

		if (resultLayout.equals("thumbnailList") ||
			resultLayout.equals("image")) {

			queryParams.addExtraParam("includeThumbnail", true);
		}

		// Include user initials?

		if (resultLayout.equals("userImageList") ||
			resultLayout.equals("image")) {

			queryParams.addExtraParam("includeUserPortrait", true);
		}
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

		Locale locale = portletRequest.getLocale();

		JSONArray facets = responseObject.getJSONArray("facets");

		if (facets == null || facets.length() == 0) {
			return;
		}

		for (int i = 0; i < facets.length(); i++) {

			JSONObject resultItem = facets.getJSONObject(i);

			resultItem.put(
				"anyOption",
				getLocalization(
					"any-" + resultItem.getString("paramName").toLowerCase(),
					locale));

			resultItem.put(
				"multipleOption",
				getLocalization(
					"multiple-" +
						resultItem.getString("paramName").toLowerCase(),
					locale));

		}
	}

	/**
	 * Set available result layout options to response object.
	 * 
	 * @param portletRequest
	 * @param queryParams
	 * @param responseObject
	 * @param resultLayout
	 * @throws JSONException
	 */
	protected void setResultLayoutOptions(
		PortletRequest portletRequest, QueryParams queryParams,
		JSONObject responseObject, String resultLayout)
		throws Exception {

		Locale locale = portletRequest.getLocale();

		String[] configuration = _moduleConfiguration.resultLayouts();

		JSONArray resultLayoutOptions = JSONFactoryUtil.createJSONArray();

		for (int i = 0; i < configuration.length; i++) {

			JSONObject configurationItem =
				JSONFactoryUtil.createJSONObject(configuration[i]);

			if (!shouldShowResultLayout(portletRequest, configurationItem)) {
				continue;
			}
			configurationItem.put(
				"title",
				getLocalization(configurationItem.getString("title"), locale));

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

		Locale locale = portletRequest.getLocale();

		JSONArray items = responseObject.getJSONArray("items");

		if (items == null || items.length() == 0) {
			return;
		}

		for (int i = 0; i < items.length(); i++) {

			JSONObject resultItem = items.getJSONObject(i);

			resultItem.put(
				"type", getLocalization(
					resultItem.getString("type").toLowerCase(), locale));
		}
	}

	protected boolean shouldShowResultLayout(
		PortletRequest portletRequest, JSONObject configurationItem) {

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
	protected ConfigurationHelper _configurationHelper;

	@Reference
	private GSearch _gSearch;

	@Reference
	private Language _language;

	private volatile ModuleConfiguration _moduleConfiguration;

	@Reference
	private QueryParamsBuilder _queryParamsBuilder;

	private ResourceBundle _resourceBundle;

	@Reference(target = "(bundle.symbolic.name=fi.soveltia.liferay.gsearch.web)", unbind = "-")
	private ResourceBundleLoader _resourceBundleLoader;
}
