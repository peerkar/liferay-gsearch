
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

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;
import fi.soveltia.liferay.gsearch.web.constants.GSearchResourceKeys;
import fi.soveltia.liferay.gsearch.web.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.web.constants.GsearchWebPortletKeys;
import fi.soveltia.liferay.gsearch.web.search.menuoption.AssetTypeOptions;
import fi.soveltia.liferay.gsearch.web.search.menuoption.DocumentExtensionOptions;
import fi.soveltia.liferay.gsearch.web.search.menuoption.DocumentTypeOptions;
import fi.soveltia.liferay.gsearch.web.search.menuoption.WebContentStructureOptions;

/**
 * View render command. Primary/default view.
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration",
	immediate = true, 
	property = {
		"javax.portlet.name=" + GsearchWebPortletKeys.SEARCH_PORTLET,
		"mvc.command.name=/", 
		"mvc.command.name=View"
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
			_gSearchDisplayConfiguration.enableAutoComplete());
		
		// Set help text resource url.
		
		template.put(
			GSearchWebKeys.HELP_TEXT_URL, 
			createResourceURL(renderResponse, GSearchResourceKeys.GET_HELP_TEXT));

		// Enable image search layout.
		
		template.put(
			GSearchWebKeys.IMAGE_SEARCH_LAYOUT_ENABLED, 
			_gSearchDisplayConfiguration.enableImageSearchLayout());

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
				_assetTypeOptions.getOptions(renderRequest, _gSearchDisplayConfiguration));
			
			// Set document extension options.
	
			template.put(
				GSearchWebKeys.DOCUMENT_EXTENSION_OPTIONS,
				_documentExtensionOptions.getOptions(renderRequest, _gSearchDisplayConfiguration));
			
			// Set document type options.
	
			template.put(
				GSearchWebKeys.DOCUMENT_TYPE_OPTIONS,
				_documentTypeOptions.getOptions(renderRequest, _gSearchDisplayConfiguration));
	
			// Set web content structure options.
	
			template.put(
				GSearchWebKeys.WEB_CONTENT_STRUCTURE_OPTIONS,
				_webContentStructureOptions.getOptions(renderRequest, _gSearchDisplayConfiguration));

		} catch (Exception e) {
			_log.error(e, e);
		}
		
		// Set request timeout.
		
		template.put(
			GSearchWebKeys.REQUEST_TIMEOUT,
			_gSearchDisplayConfiguration.requestTimeout());
		
		// Set query min length.
		
		template.put(
			GSearchWebKeys.QUERY_MIN_LENGTH,
			_gSearchDisplayConfiguration.queryMinLength());
				
		// Get/set parameters from url
		
		setInitialParameters(renderRequest, template);

		return "View";
	}
	
	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) {
		_gSearchDisplayConfiguration = ConfigurableUtil.createConfigurable(
			GSearchDisplayConfiguration.class, properties);
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

		String documentExtensionFilter = ParamUtil.getString(request, GSearchWebKeys.DOCUMENT_EXTENSION_FILTER);
		String documentTypeFilter = ParamUtil.getString(request, GSearchWebKeys.DOCUMENT_TYPE_FILTER);
		String scopeFilter = ParamUtil.getString(request, GSearchWebKeys.SCOPE_FILTER);
		String timeFilter = ParamUtil.getString(request, GSearchWebKeys.TIME_FILTER);
		String typeFilter = ParamUtil.getString(request, GSearchWebKeys.TYPE_FILTER);
		String webContentStructureFilter = ParamUtil.getString(request, GSearchWebKeys.WEB_CONTENT_STRUCTURE_FILTER);

		String sortField = ParamUtil.getString(request, GSearchWebKeys.SORT_FIELD);
		String sortDirection = ParamUtil.getString(request, GSearchWebKeys.SORT_DIRECTION);
		String start = ParamUtil.getString(request, GSearchWebKeys.START);
		
		Map<String, String>initialParameters = new HashMap<String, String>();
				
		if (Validator.isNotNull(keywords)) {
			initialParameters.put(GSearchWebKeys.KEYWORDS, keywords);
		}

		if (Validator.isNotNull(documentExtensionFilter)) {
			initialParameters.put(GSearchWebKeys.DOCUMENT_EXTENSION_FILTER, documentExtensionFilter);
		}

		if (Validator.isNotNull(documentTypeFilter)) {
			initialParameters.put(GSearchWebKeys.DOCUMENT_TYPE_FILTER, documentTypeFilter);
		}

		if (Validator.isNotNull(scopeFilter)) {
			initialParameters.put(GSearchWebKeys.SCOPE_FILTER, scopeFilter);
		}

		if (Validator.isNotNull(timeFilter)) {
			initialParameters.put(GSearchWebKeys.TIME_FILTER, timeFilter);
		}

		if (Validator.isNotNull(typeFilter)) {
			initialParameters.put(GSearchWebKeys.TYPE_FILTER, typeFilter);
		}

		if (Validator.isNotNull(webContentStructureFilter)) {
			initialParameters.put(GSearchWebKeys.WEB_CONTENT_STRUCTURE_FILTER, webContentStructureFilter);
		}
		
		if (Validator.isNotNull(keywords)) {
			initialParameters.put(GSearchWebKeys.SORT_DIRECTION, sortDirection);
		}

		if (Validator.isNotNull(sortField)) {
			initialParameters.put(GSearchWebKeys.SORT_FIELD, sortField);
		}

		if (Validator.isNotNull(start)) {
			initialParameters.put(GSearchWebKeys.START, start);
		}
		
		if (initialParameters.size() > 0) {
			template.put(GSearchWebKeys.INITIAL_URL_PARAMETERS, initialParameters);
		}
	}

	@Reference
	protected AssetTypeOptions _assetTypeOptions;

	@Reference
	protected DocumentExtensionOptions _documentExtensionOptions;

	@Reference
	protected DocumentTypeOptions _documentTypeOptions;
	
	@Reference
	protected WebContentStructureOptions _webContentStructureOptions;

	private volatile GSearchDisplayConfiguration _gSearchDisplayConfiguration;

	private static final Log _log = LogFactoryUtil.getLog(
		ViewMVCRenderCommand.class);
}
