
package fi.soveltia.liferay.gsearch.web.portlet.action;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;

import java.util.Map;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;
import fi.soveltia.liferay.gsearch.web.constants.GSearchResourceKeys;
import fi.soveltia.liferay.gsearch.web.constants.GsearchWebPortletKeys;

/**
 * Get Help Text Resource COmmand
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration",
	immediate = true, property = {
	"javax.portlet.name=" + GsearchWebPortletKeys.SEARCH_PORTLET,
	"mvc.command.name=" + GSearchResourceKeys.GET_HELP_TEXT
	}, 
	service = MVCResourceCommand.class
)
public class GetHelpTextMVCResourceCommand extends BaseMVCResourceCommand {
	
	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) {
		_gSearchDisplayConfiguration = ConfigurableUtil.createConfigurable(
			GSearchDisplayConfiguration.class, properties);
	}

	@Override
	protected void doServeResource(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		if(_log.isDebugEnabled()) {
			_log.debug("GetHelpTextMVCResourceCommand.doServeResource()");
		}
		
		JSONObject helpObject = JSONFactoryUtil.createJSONObject();

		helpObject.put("text", getHelpText());

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, helpObject);
	}

	/**
	 * Get help text.
	 * 
	 * Using portlet preferences here but could easily be
	 * modified for example, to use a JournalArticle from CMS
	 * 
	 * @return String help test
	 */
	protected String getHelpText() {
	      return _gSearchDisplayConfiguration.helpText();
	}

	private volatile GSearchDisplayConfiguration _gSearchDisplayConfiguration;

	private static final Log _log =
		LogFactoryUtil.getLog(GetHelpTextMVCResourceCommand.class);

}
