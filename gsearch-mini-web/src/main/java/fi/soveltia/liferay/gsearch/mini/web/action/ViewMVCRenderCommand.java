package fi.soveltia.liferay.gsearch.mini.web.action;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceURL;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.mini.web.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.mini.web.constants.GSearchMiniPortletKeys;
import fi.soveltia.liferay.gsearch.mini.web.constants.GSearchMiniResourceKeys;
import fi.soveltia.liferay.gsearch.mini.web.constants.GSearchMiniWebKeys;

/**
 * View render command. Primary/default view.
 * 
 * @author Petteri Karttunen
 */

@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.mini.web.configuration.ModuleConfiguration",
	immediate = true, 
	property = {
		"javax.portlet.name=" + GSearchMiniPortletKeys.GSEARCH_MINIPORTLET,
		"mvc.command.name=MiniView",
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
			
		// Hide portlet if we are on the search page

		if (getCurrentFriendlyURL(renderRequest).equals(_moduleConfiguration.searchPortletPage())) {
			renderRequest.setAttribute(WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, false);
		}
		
		Template template =
			(Template) renderRequest.getAttribute(WebKeys.TEMPLATE);

		// Set namespace (a convenience alias for $id).

		String portletNamespace = renderResponse.getNamespace();
		template.put(GSearchMiniWebKeys.PORTLET_NAMESPACE, portletNamespace);

		// Autocomplete on/off.
		
		template.put(
			GSearchMiniWebKeys.AUTO_COMPLETE_ENABLED, 
			_moduleConfiguration.enableAutoComplete());

		// Autocomplete request delay.
		
		template.put(
			GSearchMiniWebKeys.AUTO_COMPLETE_REQUEST_DELAY, 
			_moduleConfiguration.autoCompleteRequestDelay());
		
		// Set request timeout.
		
		template.put(
			GSearchMiniWebKeys.REQUEST_TIMEOUT,
			_moduleConfiguration.requestTimeout());
		
		// Set query min length.
		
		template.put(
			GSearchMiniWebKeys.QUERY_MIN_LENGTH,
			_moduleConfiguration.queryMinLength());
				
		// Set search page url.

		template.put(
			GSearchMiniWebKeys.SEARCHPAGE_URL, _portal.getPortalURL(renderRequest) +
			  _moduleConfiguration.searchPortletPage());
				
		// Set autocomplete/suggestions resource url.

		template.put(
			GSearchMiniWebKeys.SUGGESTIONS_URL,
			createResourceURL(renderResponse, GSearchMiniResourceKeys.GET_SUGGESTIONS));
		
		return "MiniView";
	}
	
	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) {
		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
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

	protected String getCurrentFriendlyURL(RenderRequest renderRequest) {
		
		String url = _portal.getCurrentURL(renderRequest);

		if (url.length() > 0 && url.indexOf("?") > 0) {
			url = url.split("\\?")[0];
		}
		return url;
	}
	
	@Reference(unbind = "-")
	protected void setPortal(Portal portal) {
		_portal = portal;
	}
	
	private volatile ModuleConfiguration _moduleConfiguration;

	private Portal _portal;
	
	private static final Log _log = LogFactoryUtil.getLog(
		ViewMVCRenderCommand.class);
}
