
package fi.soveltia.liferay.gsearch.web.portlet.action;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceURL;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.web.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.web.constants.GSearchPortletKeys;
import fi.soveltia.liferay.gsearch.web.constants.GSearchResourceKeys;
import fi.soveltia.liferay.gsearch.web.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.web.menuoption.MenuOptionProvider;

/**
 * View render command. Primary/default view.
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.web.configuration.ModuleConfiguration",
	immediate = true, 
	property = {
		"javax.portlet.name=" + GSearchPortletKeys.GSEARCH_PORTLET,
		"mvc.command.name=/",
		"mvc.command.name=GSearch"
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

		// Set template parameters.

		setTemplateParameters(renderRequest, renderResponse);

		// Set menu options.

		setMenuOptions(renderRequest);

		// Set initial parameters and values from url.

		setInitialParameters(renderRequest);

		return "GSearch";
	}

	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) {

		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}

	protected void addMenuOptionProvider(
		MenuOptionProvider menuOptionProvider) {

		if (_menuOptionProviders == null) {
			_menuOptionProviders = new ArrayList<MenuOptionProvider>();
		}
		_menuOptionProviders.add(menuOptionProvider);
	}

	/**
	 * Create resource URL for a resourceId
	 * 
	 * @param renderResponse
	 * @param resourceId
	 * @return url string
	 */
	protected String createResourceURL(
		RenderResponse renderResponse, String resourceId) {

		ResourceURL portletURL = renderResponse.createResourceURL();

		portletURL.setResourceID(resourceId);

		return portletURL.toString();
	}

	protected void removeMenuOptionProvider(
		MenuOptionProvider menuOptionProvider) {

		_menuOptionProviders.remove(menuOptionProvider);
	}

	/**
	 * Set initial parameters for the templates. This is used to make search
	 * bookmarkable and linkable.
	 * 
	 * @param renderRequest
	 * @param template
	 */
	protected void setInitialParameters(RenderRequest renderRequest) {

		Template template =
			(Template) renderRequest.getAttribute(WebKeys.TEMPLATE);

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(renderRequest);
		HttpServletRequest request =
			PortalUtil.getOriginalServletRequest(httpServletRequest);

		// Basic params

		String keywords = ParamUtil.getString(request, ParameterNames.KEYWORDS);

		String scopeFilter = ParamUtil.getString(request, ParameterNames.SCOPE);
		String timeFilter = ParamUtil.getString(request, ParameterNames.TIME);
		String timeFrom =
			ParamUtil.getString(request, ParameterNames.TIME_FROM);
		String timeTo = ParamUtil.getString(request, ParameterNames.TIME_TO);

		String typeFilter =
			ParamUtil.getString(request, ParameterNames.ASSET_TYPE);

		String sortField =
			ParamUtil.getString(request, ParameterNames.SORT_FIELD);
		String sortDirection =
			ParamUtil.getString(request, ParameterNames.SORT_DIRECTION);

		String start = ParamUtil.getString(request, ParameterNames.START);

		String resultsLayout =
			ParamUtil.getString(request, GSearchWebKeys.RESULTS_LAYOUT);

		Map<String, String[]> initialParameters =
			new HashMap<String, String[]>();

		if (Validator.isNotNull(keywords)) {
			initialParameters.put(
				ParameterNames.KEYWORDS, new String[] {
					keywords
				});
		}

		if (Validator.isNotNull(scopeFilter)) {
			initialParameters.put(
				ParameterNames.SCOPE, new String[] {
					scopeFilter
				});
		}

		if (Validator.isNotNull(timeFilter)) {
			initialParameters.put(
				ParameterNames.TIME, new String[] {
					timeFilter
				});
		}

		if (Validator.isNotNull(timeFrom)) {
			initialParameters.put(
				ParameterNames.TIME_FROM, new String[] {
					timeFrom
				});
		}

		if (Validator.isNotNull(timeTo)) {
			initialParameters.put(
				ParameterNames.TIME_TO, new String[] {
					timeTo
				});
		}

		if (Validator.isNotNull(typeFilter)) {
			initialParameters.put(
				ParameterNames.ASSET_TYPE, new String[] {
					typeFilter
				});
		}

		if (Validator.isNotNull(sortDirection)) {
			initialParameters.put(
				ParameterNames.SORT_DIRECTION, new String[] {
					sortDirection
				});
		}

		if (Validator.isNotNull(sortField)) {
			initialParameters.put(
				ParameterNames.SORT_FIELD, new String[] {
					sortField
				});
		}

		if (Validator.isNotNull(start)) {
			initialParameters.put(
				ParameterNames.START, new String[] {
					start
				});
		}

		if (Validator.isNotNull(resultsLayout)) {
			initialParameters.put(
				GSearchWebKeys.RESULTS_LAYOUT, new String[] {
					resultsLayout
				});
		}

		// Facets.

		if (_facetFields != null) {

			for (String facetKey : _facetFields) {

				String[] facetValue =
					ParamUtil.getStringValues(request, facetKey);

				if (Validator.isNotNull(facetValue)) {
					initialParameters.put(facetKey, facetValue);
				}
			}
		}

		template.put(
			GSearchWebKeys.INITIAL_QUERY_PARAMETERS, initialParameters);
	}

	/**
	 * Set menu options
	 * 
	 * @param renderRequest
	 */
	protected void setMenuOptions(RenderRequest renderRequest) {

		if (_log.isDebugEnabled()) {
			_log.debug("Processing menu option providers.");
		}

		if (_menuOptionProviders == null) {
			return;
		}

		for (MenuOptionProvider menuOptionProvider : _menuOptionProviders) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Processing " + menuOptionProvider.getClass().getName());
			}

			try {

				menuOptionProvider.setOptions(renderRequest);

			}
			catch (Exception e) {

				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Set template parameters.
	 * 
	 * @param renderRequest
	 */
	protected void setTemplateParameters(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		Template template =
			(Template) renderRequest.getAttribute(WebKeys.TEMPLATE);

		// Set namespace (a convenience alias for $id).

		String portletNamespace = renderResponse.getNamespace();
		template.put(GSearchWebKeys.PORTLET_NAMESPACE, portletNamespace);

		// Redirect
		
		template.put(
			GSearchWebKeys.APPEND_REDIRECT,
			_moduleConfiguration.isRedirectAppended());
		
		// Autocomplete on/off.

		template.put(
			GSearchWebKeys.AUTO_COMPLETE_ENABLED,
			_moduleConfiguration.isKeywordSuggesterEnabled());

		// Autocomplete request delay.

		template.put(
			GSearchWebKeys.AUTO_COMPLETE_REQUEST_DELAY,
			_moduleConfiguration.keywordSuggesterRequestDelay());

		// Set help text resource url.

		template.put(
			GSearchWebKeys.HELP_TEXT_URL, createResourceURL(
				renderResponse, GSearchResourceKeys.GET_HELP_TEXT));

		// Set search results resource url.

		template.put(
			GSearchWebKeys.SEARCH_RESULTS_URL, createResourceURL(
				renderResponse, GSearchResourceKeys.GET_SEARCH_RESULTS));

		// Set autocomplete/suggestions resource url.

		template.put(
			GSearchWebKeys.SUGGESTIONS_URL, createResourceURL(
				renderResponse, GSearchResourceKeys.GET_SUGGESTIONS));

		// Set request timeout.

		template.put(
			GSearchWebKeys.REQUEST_TIMEOUT,
			_moduleConfiguration.requestTimeout());

		// Set query min length.

		template.put(
			GSearchWebKeys.QUERY_MIN_LENGTH,
			_moduleConfiguration.queryMinLength());

		// Enable / disable JS console logging messages.

		template.put(
			GSearchWebKeys.JS_DEBUG_ENABLED,
			_moduleConfiguration.isJSDebuggingEnabled());

		// Set facet fields

		template.put(GSearchWebKeys.FACET_FIELDS, _facetFields);

		// Tags param name

		template.put(GSearchWebKeys.ASSET_TAG_PARAM, "assetTagNames");

		// Show tags

		template.put(
			GSearchWebKeys.SHOW_ASSET_TAGS,
			_moduleConfiguration.isAssetTagsVisible());

		// Google maps API key

		template.put(
			GSearchWebKeys.GMAPS_API_KEY,
			_moduleConfiguration.googleMapsAPIKey());

		// Datepicker format

		template.put(
			ParameterNames.DATEPICKER_FORMAT,
			_moduleConfiguration.datePickerFormat());
	}

	private static final Logger _log =
		LoggerFactory.getLogger(ViewMVCRenderCommand.class);

	@Reference
	private ConfigurationHelper _configurationHelper;

	@Reference(
		bind = "addMenuOptionProvider", 
		cardinality = ReferenceCardinality.MULTIPLE, 
		policy = ReferencePolicy.DYNAMIC, 
		service = MenuOptionProvider.class, 
		unbind = "removeMenuOptionProvider"
	)
	private volatile List<MenuOptionProvider> _menuOptionProviders = null;

	private volatile ModuleConfiguration _moduleConfiguration;

	private static String[] _facetFields = null;
}
