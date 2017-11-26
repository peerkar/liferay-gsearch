
package fi.soveltia.liferay.gsearch.web.portlet.action;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;

import fi.soveltia.liferay.gsearch.core.configuration.GSearchConfiguration;
import fi.soveltia.liferay.gsearch.web.constants.GsearchWebPortletKeys;

/**
 * Portlet configuration action class.
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.core.configuration.GSearchConfiguration",
	configurationPolicy = ConfigurationPolicy.OPTIONAL, 
	immediate = true,
	property = {
		"javax.portlet.name=" + GsearchWebPortletKeys.SEARCH_PORTLET
	},
	service = ConfigurationAction.class
)
public class GSearchConfigurationAction
	extends DefaultConfigurationAction {

	@Override
	public void include(
		PortletConfig portletConfig, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse)
		throws Exception {

		httpServletRequest.setAttribute(
			GSearchConfiguration.class.getName(),
			_gSearchConfiguration);

		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}

	@Override
	public void processAction(
		PortletConfig portletConfig, ActionRequest actionRequest,
		ActionResponse actionResponse)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("GSearchDisplayConfigurationAction.processAction()");
		}

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) {

		_gSearchConfiguration = ConfigurableUtil.createConfigurable(
			GSearchConfiguration.class, properties);
	}

	private volatile GSearchConfiguration _gSearchConfiguration;
	
	private static final Log _log =
					LogFactoryUtil.getLog(GSearchConfigurationAction.class);
}
