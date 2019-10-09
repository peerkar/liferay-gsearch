
package fi.soveltia.liferay.gsearch.core.impl.util;

import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * GSearch utility class.
 *
 * @author Petteri Karttunen
 */
public class GSearchUtil {

	/**
	 * Tries to find an asset publisher instance id on the given layout
	 * 
	 * @param layout
	 * @return
	 * @throws PortalException
	 */
	public static String findDefaultAssetPublisherInstanceId(Layout layout)
		throws PortalException {

		LayoutTypePortlet layoutType =
			(LayoutTypePortlet)layout.getLayoutType();

		List<Portlet> portlets = layoutType.getAllPortlets();

		for (Portlet p : portlets) {
			if (AssetPublisherPortletKeys.ASSET_PUBLISHER.equals(
					p.getRootPortletId())) {

				return p.getInstanceId();
			}
		}

		throw new PortalException(
			"Couldn't find asset publisher on page " + layout.getFriendlyURL() +
				". Please check configuration.");
	}

	/**
	 * Gets current layout friendly URL.
	 *
	 * @param portletRequest
	 * @return String friendly URL for the current layout
	 * @throws PortalException
	 */
	public static String getCurrentLayoutFriendlyURL(
			PortletRequest portletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout selectedLayout = LayoutLocalServiceUtil.getLayout(
			themeDisplay.getPlid());

		return PortalUtil.getLayoutFriendlyURL(selectedLayout, themeDisplay);
	}

	/**
	 * Gets layout by friendlyurl. 
	 * 
	 * Possible syntaxes: 
	 * - /group/something/viewasset (full group path) 
	 * - /viewasset (path to current group)
	 *
	 * @param portletRequest
	 * @return layout
	 * @throws PortalException if layout is not found
	 */
	public static Layout getLayoutByFriendlyURL(
			PortletRequest portletRequest, String layoutFriendlyURL)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (layoutFriendlyURL == null) {
			throw new PortalException(
				"Default asset publisher page is not defined. Check Liferay GSearch configuration.");
		}

		if (layoutFriendlyURL.startsWith("/group/") ||
			layoutFriendlyURL.startsWith("/web/")) {

			boolean isPrivate = false;

			if (layoutFriendlyURL.startsWith("/group/")) {
				isPrivate = true;
			}

			int position1 = layoutFriendlyURL.indexOf("/", 1);

			int position2 = layoutFriendlyURL.indexOf("/", position1);

			int position3 = position1 + position2 + 1;

			String groupFriendlyURL = layoutFriendlyURL.substring(
				position1, position3);

			Group group = GroupLocalServiceUtil.getFriendlyURLGroup(
				themeDisplay.getCompanyId(), groupFriendlyURL);

			layoutFriendlyURL = layoutFriendlyURL.substring(position3);

			return LayoutLocalServiceUtil.getFriendlyURLLayout(
				group.getGroupId(), isPrivate, layoutFriendlyURL);
		}

		Layout layout = themeDisplay.getLayout();

		return LayoutLocalServiceUtil.getFriendlyURLLayout(
			themeDisplay.getScopeGroupId(), layout.isPrivateLayout(),
			layoutFriendlyURL);
	}

	/**
	 * Gets PortletRequest from HttpServletRequest.
	 * 
	 * @param httpServletRequest
	 * @return
	 */
	public static PortletRequest getPortletRequest(
		HttpServletRequest httpServletRequest) {

		return (PortletRequest)httpServletRequest.getAttribute(
			"javax.portlet.request");
	}

	/**
	 * Gets PortletRequest from the QueryContext.
	 * 
	 * @param queryContext
	 * @return
	 */
	public static PortletRequest getPortletRequestFromContext(
		QueryContext queryContext) {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)queryContext.getParameter(
				ParameterNames.HTTP_SERVLET_REQUEST);

		if (httpServletRequest != null) {
			return getPortletRequest(httpServletRequest);
		}

		return null;
	}

	/**
	 * Gets PortletResponse from HttpServletResponse.
	 * 
	 * @param httpServletRequest
	 * @return
	 */
	public static PortletResponse getPortletResponse(
		HttpServletRequest httpServletRequest) {

		return (PortletResponse)httpServletRequest.getAttribute(
			"javax.portlet.response");
	}

	/**
	 * Gets PortletResponse from the QueryContext.
	 * 
	 * @param queryContext
	 * @return
	 */
	public static PortletResponse getPortletResponseFromContext(
		QueryContext queryContext) {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)queryContext.getParameter(
				ParameterNames.HTTP_SERVLET_REQUEST);

		if (httpServletRequest != null) {
			return getPortletResponse(httpServletRequest);
		}

		return null;
	}

	/**
	 * Appends redirect to url.
	 *
	 * @param queryContext
	 * @param url
	 * @return
	 * @throws PortalException
	 */
	public static String getRedirect(QueryContext queryContext, String url)
		throws PortalException {

		PortletRequest portletRequest = getPortletRequestFromContext(
			queryContext);

		if (portletRequest == null) {
			return "";
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String currentURL = themeDisplay.getURLCurrent();

		// Try to strip everything else but search parameters.

		StringBundler sb = new StringBundler();

		if (url.contains("?")) {
			sb.append("&redirect=");
		}
		else {
			sb.append("?redirect=");
		}

		if ((currentURL.indexOf("?q=") > 0) ||
			(currentURL.indexOf("&q=") > 0)) {

			sb.append(currentURL.substring(0, currentURL.indexOf("?")));

			String part2;

			if (currentURL.indexOf("?q=") > 0) {
				part2 = currentURL.substring(currentURL.indexOf("?q="));
			}
			else {
				part2 = currentURL.substring(currentURL.indexOf("&q="));
				part2 = part2.replace("&q", "?q");
			}

			sb.append(HtmlUtil.escapeURL(part2));
		}
		else {
			sb.append(HtmlUtil.escapeURL(currentURL));
		}

		return sb.toString();
	}

	/**
	 * Gets group ids available for current user.
	 *
	 * @param themeDisplay
	 * @return array of possible groupIds for a user
	 * @throws PortalException
	 */
	public static long[] getUserAccessibleSiteGroupIds(
			ThemeDisplay themeDisplay)
		throws PortalException {

		List<Long> groupIds = new ArrayList<>();

		// Add global

		groupIds.add(themeDisplay.getCompanyGroupId());

		// For a guest user list all public sites

		for (Group group :
				GroupLocalServiceUtil.getGroups(
					themeDisplay.getCompanyId(), 0, true)) {

			if (group.isActive() && !group.isStagingGroup() &&
				group.hasPublicLayouts()) {

				groupIds.add(group.getGroupId());
			}
		}

		// For a logged in user additionally list all sites he's a member of

		User user = themeDisplay.getUser();

		for (Group group : user.getSiteGroups()) {
			if (!groupIds.contains(group.getGroupId()) && group.isActive() &&
				!group.isStagingGroup()) {

				groupIds.add(group.getGroupId());
			}
		}

		return groupIds.stream(
		).mapToLong(
			l -> l
		).toArray();
	}

	/**
	 * Strips other than highlight HTML.
	 *
	 * (Should be done in the adapter, but still bugs in 7.1 SP10)
	 *
	 * @param html
	 * @param length
	 * @return
	 */
	public static String stripHTML(String string, int length) throws Exception {

		if (Validator.isBlank(string)) {
			return string;
		}
		
		// Replace other than highlight tags.

		string = string.replaceAll("<liferay-hl>", "---LR-HL-START---");
		string = string.replaceAll("</liferay-hl>", "---LR-HL-STOP---");
		string = HtmlUtil.stripHtml(string);
		string = string.replaceAll("---LR-HL-START---", "<liferay-hl>");
		string = string.replaceAll("---LR-HL-STOP---", "</liferay-hl>");

		if ((length > -1) && (string.length() > length)) {
			String temp = string.substring(0, length);

			// Check that we are not breaking the HTML.

			if (temp.lastIndexOf("<") > temp.lastIndexOf(">")) {
				temp = string.substring(
					0, 1 + string.indexOf('>', temp.lastIndexOf('<')));
			}

			string = temp.concat("...");
		}

		return string;
	}
}