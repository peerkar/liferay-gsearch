
package fi.soveltia.liferay.gsearch.web.portlet.action;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.ResourceBundle;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.web.constants.GSearchResourceKeys;
import fi.soveltia.liferay.gsearch.web.constants.GSearchPortletKeys;
import fi.soveltia.liferay.gsearch.web.portlet.GSearchPortlet;

/**
 * Resource command for getting the help text.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	property = {
		"javax.portlet.name=" + GSearchPortletKeys.GSEARCH_PORTLET,
		"mvc.command.name=" + GSearchResourceKeys.GET_HELP_TEXT
	}, 
	service = MVCResourceCommand.class
)
public class GetHelpTextMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("GetHelpTextMVCResourceCommand.doServeResource()");
		}

		JSONObject helpObject = JSONFactoryUtil.createJSONObject();

		helpObject.put(GSearchWebKeys.HELP_TEXT, getHelpText(resourceRequest));

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, helpObject);
	}

	/**
	 * Get help text. 
	 *  
	 * @return String help test
	 */
	protected String getHelpText(ResourceRequest resourceRequest) {

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", resourceRequest.getLocale(),
			GSearchPortlet.class);

		return LanguageUtil.get(resourceBundle, "helptext");
	}

	private static final Log _log =
		LogFactoryUtil.getLog(GetHelpTextMVCResourceCommand.class);
}
