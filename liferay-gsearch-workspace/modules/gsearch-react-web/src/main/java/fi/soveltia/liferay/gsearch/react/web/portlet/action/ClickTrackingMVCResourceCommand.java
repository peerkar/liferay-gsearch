
package fi.soveltia.liferay.gsearch.react.web.portlet.action;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.ParamUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.click.tracking.model.Clicks;
import fi.soveltia.liferay.gsearch.click.tracking.service.ClicksLocalService;
import fi.soveltia.liferay.gsearch.core.api.constants.SessionAttributes;
import fi.soveltia.liferay.gsearch.react.web.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.react.web.constants.GSearchReactWebPortletKeys;
import fi.soveltia.liferay.gsearch.react.web.constants.ResourceRequestKeys;

/**
 * Resource command for click tracking.
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.react.web.configuration.ModuleConfiguration", 
	immediate = true, 
	property = {
		"javax.portlet.name=" + GSearchReactWebPortletKeys.GSEARCH_REACT_PORTLET,
		"mvc.command.name=" + ResourceRequestKeys.CLICK_TRACK
	}, 
	service = MVCResourceCommand.class
)
public class ClickTrackingMVCResourceCommand extends BaseMVCResourceCommand {

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
			_log.debug("ClickTrackingMVCResourceCommand.doServeResource()");
		} 

		if (_moduleConfiguration.isClickTrackingEnabled()) {
			trackClick(resourceRequest);
		}
	}
	

	@SuppressWarnings({ "unchecked" })
	protected String getLastKeywords(ResourceRequest resourceRequest) {

		PortletSession session = resourceRequest.getPortletSession();

		return (String)session.getAttribute(
					SessionAttributes.PREVIOUS_SEARCH_PHRASE, 
					PortletSession.APPLICATION_SCOPE);
	}

	/**
	 * Stores the click event.
	 * 
	 * This is a for demonstration and testing purposes only
	 * and by purpose doesn't prevent the same user flooding the click counts.
	 * 
	 * @param resourceRequest
	 */
	protected void trackClick(ResourceRequest resourceRequest) {

		if (_clicksLocalService == null) {
			_log.error("Clicks tracking service is not installed or failed to activate.");
			return;
		}
				
		try {
			
			long entryClassPK = ParamUtil.getLong(resourceRequest, "trackId");
			String lastKeywords = getLastKeywords(resourceRequest);

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
					Clicks.class.getName(), resourceRequest);

			serviceContext.setAttribute("keywords", lastKeywords);
			serviceContext.setAttribute("entryClassPK", entryClassPK);
			
			_clicksLocalService.updateClicks(serviceContext);
			
		} catch (PortalException e) {
			_log.error(e.getMessage(), e);
		}
	}

	
	
	private static final Logger _log =
					LoggerFactory.getLogger(ClickTrackingMVCResourceCommand.class);

	private volatile ModuleConfiguration _moduleConfiguration;
	
	@Reference(
		cardinality=ReferenceCardinality.OPTIONAL
	)
	private ClicksLocalService _clicksLocalService;

}
