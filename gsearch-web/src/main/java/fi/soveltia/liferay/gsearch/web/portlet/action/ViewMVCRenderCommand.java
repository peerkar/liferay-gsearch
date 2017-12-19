
package fi.soveltia.liferay.gsearch.web.portlet.action;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceURL;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.configuration.JSONConfigurationHelperService;
import fi.soveltia.liferay.gsearch.core.api.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.core.configuration.GSearchConfiguration;
import fi.soveltia.liferay.gsearch.web.constants.GSearchResourceKeys;
import fi.soveltia.liferay.gsearch.web.constants.GSearchPortletKeys;

/**
 * View render command. Primary/default view.
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.core.configuration.GSearchConfiguration",
	immediate = true, 
	property = {
		"javax.portlet.name=" + GSearchPortletKeys.GSEARCH_PORTLET,
		"mvc.command.name=/"
	}, 
	service = MVCRenderCommand.class
)
public class ViewMVCRenderCommand implements MVCRenderCommand{

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		if (_log.isDebugEnabled()) {
			_log.debug("ViewMVCRenderCommand.render()");
		}
			
		Template template =
			(Template) renderRequest.getAttribute(WebKeys.TEMPLATE);

		// Set namespace (a convenience alias for $id).

		String portletNamespace = renderResponse.getNamespace();
		template.put(GSearchWebKeys.PORTLET_NAMESPACE, portletNamespace);

		// Autocomplete on/off.
		
		template.put(
			GSearchWebKeys.AUTO_COMPLETE_ENABLED, 
			_gSearchConfiguration.enableAutoComplete());

		// Autocomplete request delay.
		
		template.put(
			GSearchWebKeys.AUTO_COMPLETE_REQUEST_DELAY, 
			_gSearchConfiguration.autoCompleteRequestDelay());
		
		// Set help text resource url.
		
		template.put(
			GSearchWebKeys.HELP_TEXT_URL, 
			createResourceURL(renderResponse, GSearchResourceKeys.GET_HELP_TEXT));
		
		// Set search results resource url.

		template.put(
			GSearchWebKeys.SEARCH_RESULTS_URL,
			createResourceURL(renderResponse, GSearchResourceKeys.GET_SEARCH_RESULTS));

		// Set autocomplete/suggestions resource url.

		template.put(
			GSearchWebKeys.SUGGESTIONS_URL,
			createResourceURL(renderResponse, GSearchResourceKeys.GET_SUGGESTIONS));

		try {
		
			// Set supported asset type options.
			
			template.put(
				GSearchWebKeys.ASSET_TYPE_OPTIONS,
				_jsonConfigurationHelperService.getAssetTypeOptions(renderRequest.getLocale()));

			template.put(
				GSearchWebKeys.SORT_OPTIONS,
				_jsonConfigurationHelperService.getSortOptions(renderRequest.getLocale()));
			
		} catch (Exception e) {
			_log.error(e, e);
		}
		
		// Set request timeout.
		
		template.put(
			GSearchWebKeys.REQUEST_TIMEOUT,
			_gSearchConfiguration.requestTimeout());
		
		// Set query min length.
		
		template.put(
			GSearchWebKeys.QUERY_MIN_LENGTH,
			_gSearchConfiguration.queryMinLength());
				
		// Enable / disable JS console logging messages.
		
		template.put(
			GSearchWebKeys.JS_DEBUG_ENABLED,
			_gSearchConfiguration.jsDebuggingEnabled());
		
		// Get/set parameters from url

		setInitialParameters(renderRequest, template);

		return "View";
	}
	
	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) {
		_gSearchConfiguration = ConfigurableUtil.createConfigurable(
			GSearchConfiguration.class, properties);
	}
	
	/**
	 * Create resource URL for a resourceId
	 * 
	 * @param renderResponse
	 * @param resourceId
	 * 
	 * @return url string
	 */
	protected String createResourceURL(RenderResponse renderResponse, String resourceId) {

		ResourceURL portletURL = renderResponse.createResourceURL();

		portletURL.setResourceID(resourceId);

		return portletURL.toString();
	}	
	
	/**
	 * Set initial parameters for the templates.
	 * 
	 * This is used to make search bookmarkable and linkable.
	 * 
	 * @param renderRequest
	 * @param template
	 */
	protected void setInitialParameters(RenderRequest renderRequest, Template template) {

		HttpServletRequest httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		HttpServletRequest request = PortalUtil.getOriginalServletRequest(httpServletRequest);
		
		String keywords = ParamUtil.getString(request, GSearchWebKeys.KEYWORDS);
		
		String scopeFilter = ParamUtil.getString(request, GSearchWebKeys.FILTER_SCOPE);
		String timeFilter = ParamUtil.getString(request, GSearchWebKeys.FILTER_TIME);
		String typeFilter = ParamUtil.getString(request, GSearchWebKeys.FILTER_TYPE);

		String facetAssetCategoryTitles = ParamUtil.getString(request, GSearchWebKeys.FACET_CATEGORIES);
		String facetAssetTagNames = ParamUtil.getString(request, GSearchWebKeys.FACET_TAGS);
		String facetDocumentType = ParamUtil.getString(request, GSearchWebKeys.FACET_DOCUMENT_TYPE);
		String facetExtension = ParamUtil.getString(request, GSearchWebKeys.FACET_EXTENSION);
		String facetUsername = ParamUtil.getString(request, GSearchWebKeys.FACET_USERNAME);
		String facetWebContentStructure = ParamUtil.getString(request, GSearchWebKeys.FACET_WEB_CONTENT_STRUCTURE);

		String sortField = ParamUtil.getString(request, GSearchWebKeys.SORT_FIELD);
		String sortDirection = ParamUtil.getString(request, GSearchWebKeys.SORT_DIRECTION);
		String start = ParamUtil.getString(request, GSearchWebKeys.START);
		
		String resultsLayout = ParamUtil.getString(request, GSearchWebKeys.RESULTS_LAYOUT);
		
		Map<String, String>initialParameters = new HashMap<String, String>();
				
		if (Validator.isNotNull(keywords)) {
			initialParameters.put(GSearchWebKeys.KEYWORDS, keywords);
		}

		if (Validator.isNotNull(facetWebContentStructure)) {
			initialParameters.put(GSearchWebKeys.FACET_WEB_CONTENT_STRUCTURE, facetWebContentStructure);
		}
		
		if (Validator.isNotNull(scopeFilter)) {
			initialParameters.put(GSearchWebKeys.FILTER_SCOPE, scopeFilter);
		}

		if (Validator.isNotNull(timeFilter)) {
			initialParameters.put(GSearchWebKeys.FILTER_TIME, timeFilter);
		}

		if (Validator.isNotNull(typeFilter)) {
			initialParameters.put(GSearchWebKeys.FILTER_TYPE, typeFilter);
		}
		
		if (Validator.isNotNull(sortDirection)) {
			initialParameters.put(GSearchWebKeys.SORT_DIRECTION, sortDirection);
		}

		if (Validator.isNotNull(sortField)) {
			initialParameters.put(GSearchWebKeys.SORT_FIELD, sortField);
		}

		if (Validator.isNotNull(start)) {
			initialParameters.put(GSearchWebKeys.START, start);
		}
		
		// Facets
		
		if (Validator.isNotNull(facetAssetCategoryTitles)) {
			initialParameters.put(GSearchWebKeys.FACET_CATEGORIES, facetAssetCategoryTitles);
		}

		if (Validator.isNotNull(facetAssetTagNames)) {
			initialParameters.put(GSearchWebKeys.FACET_TAGS, facetAssetTagNames);
		}

		if (Validator.isNotNull(facetDocumentType)) {
			initialParameters.put(GSearchWebKeys.FACET_DOCUMENT_TYPE, facetDocumentType);
		}
		
		if (Validator.isNotNull(facetExtension)) {
			initialParameters.put(GSearchWebKeys.FACET_EXTENSION, facetExtension);
		}

		if (Validator.isNotNull(facetUsername)) {
			initialParameters.put(GSearchWebKeys.FACET_USERNAME, facetUsername);
		}

		if (Validator.isNotNull(resultsLayout)) {
			initialParameters.put(GSearchWebKeys.RESULTS_LAYOUT, resultsLayout);
		}
		
		template.put(GSearchWebKeys.INITIAL_QUERY_PARAMETERS, initialParameters);
	}

	@Reference(unbind = "-")
	protected void setJSONConfigurationHelperService(JSONConfigurationHelperService jsonConfigurationHelperService) {

		_jsonConfigurationHelperService = jsonConfigurationHelperService;
	}
	
	@Reference
	protected JSONConfigurationHelperService _jsonConfigurationHelperService;

	private volatile GSearchConfiguration _gSearchConfiguration;

	private static final Log _log = LogFactoryUtil.getLog(
		ViewMVCRenderCommand.class);
}
