
package fi.soveltia.liferay.gsearch.web.portlet.action;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.ParamUtil;

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

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;
import fi.soveltia.liferay.gsearch.web.constants.GSearchPreferencesKeys;
import fi.soveltia.liferay.gsearch.web.constants.GsearchWebPortletKeys;

/**
 * Configuration action
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration",
	configurationPolicy = ConfigurationPolicy.OPTIONAL, 
	immediate = true,
	property = {
		"javax.portlet.name=" + GsearchWebPortletKeys.SEARCH_PORTLET
	},
	service = ConfigurationAction.class
)
public class GSearchDisplayConfigurationAction
	extends DefaultConfigurationAction {

	@Override
	public void include(
		PortletConfig portletConfig, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse)
		throws Exception {

		httpServletRequest.setAttribute(
			GSearchDisplayConfiguration.class.getName(),
			_gSearchDisplayConfiguration);

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

		int pageSize = ParamUtil.getInteger(
			actionRequest, GSearchPreferencesKeys.PAGE_SIZE, 10);
		int queryMinLength = ParamUtil.getInteger(
			actionRequest, GSearchPreferencesKeys.QUERY_MIN_LENGTH, 3);
		int requestTimeout = ParamUtil.getInteger(
			actionRequest, GSearchPreferencesKeys.REQUEST_TIMEOUT, 10000);
		String helpText = ParamUtil.getString(
			actionRequest, GSearchPreferencesKeys.HELP_TEXT, "N/A");

		String assetPublisherPageFriendlyURL = ParamUtil.getString(
			actionRequest, GSearchPreferencesKeys.ASSET_PUBLISHER_PAGE_FRIENDLY_URL, null);

		if (_log.isDebugEnabled()) {
			_log.debug("Asset Publisher page friendly url:" + assetPublisherPageFriendlyURL);
			_log.debug("Page size:" + pageSize);
			_log.debug("Query min length:" + queryMinLength);
			_log.debug("Request timeout:" + requestTimeout);
			_log.debug("Helptext:" + helpText);
		}

		setPreference(
			actionRequest, GSearchPreferencesKeys.ASSET_PUBLISHER_PAGE_FRIENDLY_URL,
			assetPublisherPageFriendlyURL);
		setPreference(
			actionRequest, GSearchPreferencesKeys.PAGE_SIZE,
			String.valueOf(pageSize));
		setPreference(
			actionRequest, GSearchPreferencesKeys.QUERY_MIN_LENGTH,
			String.valueOf(queryMinLength));
		setPreference(
			actionRequest, GSearchPreferencesKeys.REQUEST_TIMEOUT,
			String.valueOf(requestTimeout));
		setPreference(
			actionRequest, GSearchPreferencesKeys.HELP_TEXT, helpText);

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) {

		_gSearchDisplayConfiguration = ConfigurableUtil.createConfigurable(
			GSearchDisplayConfiguration.class, properties);
	}

	private static final Log _log =
		LogFactoryUtil.getLog(GSearchDisplayConfigurationAction.class);

	private volatile GSearchDisplayConfiguration _gSearchDisplayConfiguration;

}
