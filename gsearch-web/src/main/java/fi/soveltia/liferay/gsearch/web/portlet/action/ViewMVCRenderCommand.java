
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

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;
import fi.soveltia.liferay.gsearch.web.constants.GSearchResourceKeys;
import fi.soveltia.liferay.gsearch.web.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.web.constants.GsearchWebPortletKeys;

/**
 * View Render Command
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
public class ViewMVCRenderCommand implements MVCRenderCommand {

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

		// Help text resource url 
		
		template.put(
			GSearchWebKeys.HELP_TEXT_URL, getHelpTextURL(renderResponse));

		// Search results resource url 

		template.put(
			GSearchWebKeys.SEARCH_RESULTS_URL,
			getSearchResultsURL(renderResponse));
				
		// Preferences
		
		template.put(
			GSearchWebKeys.REQUEST_TIMEOUT,
			_gSearchDisplayConfiguration.requestTimeout());
		
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
	 * Get help text URL
	 * 
	 * @param renderResponse
	 * @return
	 */
	protected String getHelpTextURL(RenderResponse renderResponse) {

		ResourceURL portletURL = renderResponse.createResourceURL();

		portletURL.setResourceID(GSearchResourceKeys.GET_HELP_TEXT);

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
		String scopeFilter = ParamUtil.getString(request, GSearchWebKeys.SCOPE_FILTER);
		String timeFilter = ParamUtil.getString(request, GSearchWebKeys.TIME_FILTER);
		String typeFilter = ParamUtil.getString(request, GSearchWebKeys.TYPE_FILTER);
		String sortField = ParamUtil.getString(request, GSearchWebKeys.SORT_FIELD);
		String sortDirection = ParamUtil.getString(request, GSearchWebKeys.SORT_DIRECTION);
		String start = ParamUtil.getString(request, GSearchWebKeys.START);
		
		Map<String, String>initialParameters = new HashMap<String, String>();
				
		if (Validator.isNotNull(keywords)) {
			initialParameters.put(GSearchWebKeys.KEYWORDS, keywords);
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

	/**
	 * Get search results URL
	 * 
	 * @param renderResponse
	 * @return
	 */
	protected String getSearchResultsURL(RenderResponse renderResponse) {

		ResourceURL portletURL = renderResponse.createResourceURL();

		portletURL.setResourceID(GSearchResourceKeys.GET_SEARCH_RESULTS);

		return portletURL.toString();
	}
	
	private volatile GSearchDisplayConfiguration _gSearchDisplayConfiguration;
	
	private static final Log _log = LogFactoryUtil.getLog(
		ViewMVCRenderCommand.class);

}
