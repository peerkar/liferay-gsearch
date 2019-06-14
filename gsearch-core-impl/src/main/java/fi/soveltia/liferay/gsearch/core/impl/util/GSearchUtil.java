
package fi.soveltia.liferay.gsearch.core.impl.util;

import com.liferay.asset.publisher.constants.AssetPublisherPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * GSearch utility class.
 * 
 * @author Petteri Karttunen
 */
public class GSearchUtil {

	/**
	 * Append redirect to url.
	 * 
	 * @param portletRequest
	 * @param url
	 * @return
	 * @throws PortalException
	 */
	public static String getRedirect(QueryContext queryContext, String url)
		throws PortalException {

		PortletRequest portletRequest = getPortletRequestFromContext(queryContext);
		
		if (portletRequest == null) {
			return "";
		}
		
		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		String currentURL = themeDisplay.getURLCurrent();

		// Try to strip everything else but search parameters.

		StringBundler sb = new StringBundler();

		if (url.contains("?")) {
			sb.append("&redirect=");
		}
		else {
			sb.append("?redirect=");
		}

		if (currentURL.indexOf("?q=") > 0 || currentURL.indexOf("&q=") > 0) {

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
	 * Try to find an asset publisher instance id on a layout
	 * 
	 * @param layout
	 * @return portlet instance id
	 * @throws PortalException
	 * @throws SystemException
	 */
	public static String findDefaultAssetPublisherInstanceId(Layout layout)
		throws PortalException, SystemException {

		LayoutTypePortlet layoutType =
			(LayoutTypePortlet) layout.getLayoutType();

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
	 * Get current layout friendly URL
	 * 
	 * @return String friendly URL for the current layout
	 * @throws PortalException
	 */
	public static String getCurrentLayoutFriendlyURL(
		PortletRequest portletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		Layout selectedLayout =
			LayoutLocalServiceUtil.getLayout(themeDisplay.getPlid());
		return PortalUtil.getLayoutFriendlyURL(selectedLayout, themeDisplay);
	}

	/**
	 * Get layout by friendlyurl. Possible syntaxes: -
	 * /group/something/viewasset (full group path) - /viewasset (path to
	 * current group)
	 *
	 * @param resourceRequest
	 * @return layout
	 * @throws PortalException
	 *             if layout is not found
	 */
	public static Layout getLayoutByFriendlyURL(
		PortletRequest portletRequest, String layoutFriendlyURL)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

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

			String groupFriendlyURL =
				layoutFriendlyURL.substring(position1, position3);

			Group group = GroupLocalServiceUtil.getFriendlyURLGroup(
				themeDisplay.getCompanyId(), groupFriendlyURL);

			layoutFriendlyURL = layoutFriendlyURL.substring(position3);

			return LayoutLocalServiceUtil.getFriendlyURLLayout(
				group.getGroupId(), isPrivate, layoutFriendlyURL);

		}
		else {

			return LayoutLocalServiceUtil.getFriendlyURLLayout(
				themeDisplay.getScopeGroupId(),
				themeDisplay.getLayout().isPrivateLayout(), layoutFriendlyURL);
		}
	}

	public static PortletRequest getPortletRequestFromContext(QueryContext queryContext) {
	
		HttpServletRequest httpServletRequest =
						(HttpServletRequest) queryContext.getParameter(
							ParameterNames.HTTP_SERVLET_REQUEST);
		
		if (httpServletRequest != null) {
			return 	GSearchUtil.getPortletRequest(httpServletRequest);
		}
		
		return null;
	}
	
	public static PortletResponse getPortletResponseFromContext(QueryContext queryContext) {
	
		HttpServletRequest httpServletRequest =
						(HttpServletRequest) queryContext.getParameter(
							ParameterNames.HTTP_SERVLET_REQUEST);
		
		if (httpServletRequest != null) {
			return 	GSearchUtil.getPortletResponse(httpServletRequest);
		}
		
		return null;
	}

	public static PortletRequest getPortletRequest(
		HttpServletRequest httpServletRequest) {

		return (PortletRequest) httpServletRequest.getAttribute(
			"javax.portlet.request");
	}

	public static PortletResponse getPortletResponse(
		HttpServletRequest httpServletRequest) {

		return (PortletResponse) httpServletRequest.getAttribute(
			"javax.portlet.response");
	}

	/**
	 * Get group ids available for current user.
	 * 
	 * @param themeDisplay
	 * @return array of possible groupIds for a user
	 * @throws PortalException
	 */
	public static long[] getUserAccessibleSiteGroupIds(
		ThemeDisplay themeDisplay)
		throws PortalException {

		List<Long> groupIds = new ArrayList<Long>();

		// Add global

		groupIds.add(themeDisplay.getCompanyGroupId());

		// For a guest user list all public sites

		for (Group group : GroupLocalServiceUtil.getGroups(
			themeDisplay.getCompanyId(), 0, true)) {

			if (group.isActive() && !group.isStagingGroup() &&
				group.hasPublicLayouts()) {
				groupIds.add(group.getGroupId());
			}
		}

		// For a logged in user additionally list all sites he's a member of

		for (Group group : themeDisplay.getUser().getSiteGroups()) {
			if (!groupIds.contains(group.getGroupId()) && group.isActive() &&
				!group.isStagingGroup()) {
				groupIds.add(group.getGroupId());
			}
		}

		return groupIds.stream().mapToLong(l -> l).toArray();
	}
}
