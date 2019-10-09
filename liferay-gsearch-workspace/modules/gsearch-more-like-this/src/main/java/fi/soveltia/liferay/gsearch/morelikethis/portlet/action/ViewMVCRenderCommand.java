
package fi.soveltia.liferay.gsearch.morelikethis.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import fi.soveltia.liferay.gsearch.morelikethis.constants.ModuleConfigurationKeys;
import fi.soveltia.liferay.gsearch.morelikethis.constants.GSearchMoreLikeThisPortletKeys;
import fi.soveltia.liferay.gsearch.morelikethis.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.morelikethis.constants.ResourceRequestKeys;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceURL;

import org.osgi.service.component.annotations.Component;

/**
 * Primary/default view.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + GSearchMoreLikeThisPortletKeys.MORE_LIKE_THIS_PORTLET,
		"mvc.command.name=/"
	},
	service = MVCRenderCommand.class
)
public class ViewMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		// Check if configured so we can display a message on UI.

		renderRequest.setAttribute(
			"configured",
			Validator.isNotNull(
				renderRequest.getPreferences(
				).getValue(
					ModuleConfigurationKeys.RECOMMENDATION_CLAUSES, null
				)));

		// Set search results URL.

		renderRequest.setAttribute(
			GSearchWebKeys.SEARCH_RESULTS_URL,
			_createResourceURL(
				renderRequest, renderResponse, ResourceRequestKeys.GET_SEARCH_RESULTS));

		return "/view.jsp";
	}

	/**
	 * Creates a resource URL for a resourceId
	 *
	 * @param renderResponse
	 * @param resourceId
	 * @return url string
	 */
	private String _createResourceURL(
		RenderRequest renderRequest, RenderResponse renderResponse,
		String resourceId) {

		ResourceURL portletURL = renderResponse.createResourceURL();

		portletURL.setResourceID(resourceId);

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		portletURL.setParameter("currentURL", themeDisplay.getURLCurrent());

		return portletURL.toString();
	}

}