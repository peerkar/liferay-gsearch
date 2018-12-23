
package fi.soveltia.liferay.gsearch.web.portlet.action;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.web.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.web.constants.GSearchPortletKeys;
import fi.soveltia.liferay.gsearch.web.constants.GSearchResourceKeys;
import fi.soveltia.liferay.gsearch.web.portlet.GSearchPortlet;

/**
 * Resource command for getting the help text.
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.web.configuration.ModuleConfiguration", 
	immediate = true, 
	property = {
		"javax.portlet.name=" + GSearchPortletKeys.GSEARCH_PORTLET,
		"mvc.command.name=" + GSearchResourceKeys.GET_HELP_TEXT
	}, 
	service = MVCResourceCommand.class
)
public class GetHelpTextMVCResourceCommand extends BaseMVCResourceCommand {

	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) {

		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);

		_helpText = null;
	}

	@Override
	protected void doServeResource(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("GetHelpTextMVCResourceCommand.doServeResource()");
		}

		JSONObject helpObject = JSONFactoryUtil.createJSONObject();

		helpObject.put(
			GSearchWebKeys.HELP_TEXT,
			getHelpText(resourceRequest, resourceResponse));

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, helpObject);
	}

	/**
	 * Get help text.
	 * 
	 * @param resourceRequest
	 * @param resourceResponse
	 * @return
	 */
	protected String getHelpText(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse) {

		if (_helpText != null) {
			return _helpText;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(
			GSearchWebKeys.THEME_DISPLAY);

		String articleId = _moduleConfiguration.helpTextArticleId();

		long groupId =
			GetterUtil.getLong(_moduleConfiguration.helpTextGroupId());

		if (articleId != null && groupId > 0) {

			try {

				JournalArticle journalArticle =
					_journalArticleService.getLatestArticle(
						groupId, articleId, WorkflowConstants.STATUS_APPROVED);

				_helpText = _journalArticleService.getArticleContent(
					groupId, articleId, journalArticle.getVersion(),
					themeDisplay.getLanguageId(),
					new PortletRequestModel(resourceRequest, resourceResponse),
					themeDisplay);
			}
			catch (Exception e) {
				_log.error(e.getMessage(), e);
			}
		}

		// Fall back to default help text from localization file.

		if (_helpText == null) {

			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				"content.Language", resourceRequest.getLocale(),
				GSearchPortlet.class);

			_helpText = LanguageUtil.get(resourceBundle, "helptext");
		}

		return _helpText;
	}

	private static final Logger _log =
					LoggerFactory.getLogger(GetHelpTextMVCResourceCommand.class);

	private volatile ModuleConfiguration _moduleConfiguration;

	private String _helpText;

	private JournalArticleService _journalArticleService;

}
