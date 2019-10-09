
package fi.soveltia.liferay.gsearch.mini.web.portlet.action;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.HashMap;
import java.util.Map;

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
import fi.soveltia.liferay.gsearch.core.api.configuration.CoreConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContextBuilder;
import fi.soveltia.liferay.gsearch.localization.api.LocalizationHelper;
import fi.soveltia.liferay.gsearch.mini.web.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.mini.web.constants.GSearchMiniPortletKeys;
import fi.soveltia.liferay.gsearch.mini.web.constants.GSearchMiniResourceKeys;

/**
 * Resource command for getting content suggestions.
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

		QueryContext queryContext = null;

		try {
			queryContext = _buildQueryContext(resourceRequest);
		}
		catch (Exception e) {
			
			_log.error(e.getMessage(), e);

			return;
		}

		// Try to get search results.

		JSONObject responseObject = null;

		try {
			responseObject = _gSearch.getSearchResults(queryContext);

			// Localize result types.

			ThemeDisplay themeDisplay = 
					(ThemeDisplay)resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);

			_localizationHelper.setResultTypeLocalizations(
					themeDisplay.getLocale(), responseObject);
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
	 * Builds query context.
	 * 
	 * @param resourceRequest
	 * @return
	 * @throws Exception
	 */
	private QueryContext _buildQueryContext(ResourceRequest resourceRequest) throws Exception {
		
		ThemeDisplay themeDisplay = 
				(ThemeDisplay)resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);

		HttpServletRequest httpServletRequest =
				PortalUtil.getHttpServletRequest(resourceRequest);

		String keywords = ParamUtil.getString(resourceRequest, ParameterNames.KEYWORDS);
		
		QueryContext queryContext = _queryContextBuilder.buildQueryContext(
				httpServletRequest, themeDisplay.getLocale(), null, null, null,
				null, null, null, keywords);

		// Process query context contributors.
		
		_queryContextBuilder.processQueryContextContributors(queryContext);

		// Parse request parameters.
		
		_queryContextBuilder.parseParameters(queryContext);
		
		queryContext.setPageSize(
			_moduleConfiguration.contentSuggestionsCount());

		queryContext.setParameter(
			ParameterNames.ASSET_PUBLISHER_URL,
			_moduleConfiguration.assetPublisherPage());

		queryContext.setParameter(
			ParameterNames.VIEW_RESULTS_IN_CONTEXT,
			_moduleConfiguration.isViewResultsInContext());

		
		// Entryclass name is needed for the "More" links.
		
		Map<String, Class<?>> additionalResultFields = new HashMap<>();

		additionalResultFields.put(Field.ENTRY_CLASS_NAME, String.class);

		queryContext.setParameter(
			ParameterNames.ADDITIONAL_RESULT_FIELDS,
			additionalResultFields);

		// Postprocessors are not needed for suggestions.
		
		queryContext.setQueryPostProcessorsEnabled(false);		
		
		return queryContext;
	}
	
	
	private static final Logger _log = LoggerFactory.getLogger(
		GetContentSuggestionsMVCResourceCommand.class);

	@Reference
	private CoreConfigurationHelper _coreConfigurationHelper;

	@Reference
	private GSearch _gSearch;

	@Reference
	private LocalizationHelper _localizationHelper;

	private volatile ModuleConfiguration _moduleConfiguration;

	@Reference
	private QueryContextBuilder _queryContextBuilder;

}