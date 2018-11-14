
package fi.soveltia.liferay.gsearch.core.impl.util;

import com.liferay.asset.publisher.web.constants.AssetPublisherPortletKeys;
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
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;

import fi.soveltia.liferay.gsearch.core.api.constants.GSearchWebKeys;

/**
 * GSearch utility class.
 *
 * @author Petteri Karttunen
 */
public class GSearchUtil {

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
	 * Get redirect url.
	 *
	 * @return redirect url
	 * @throws PortalException
	 */
	public static String getRedirectURL(PortletRequest portletRequest)
		throws PortalException {

		StringBundler sb = new StringBundler();

		sb.append(GSearchUtil.getCurrentLayoutFriendlyURL(portletRequest));
		sb.append("?");
		sb.append(GSearchWebKeys.KEYWORDS).append("=").append(
			ParamUtil.getString(portletRequest, GSearchWebKeys.KEYWORDS));
		sb.append("&").append(GSearchWebKeys.FILTER_SCOPE).append("=").append(
			ParamUtil.getString(portletRequest, GSearchWebKeys.FILTER_SCOPE));
		sb.append("&").append(GSearchWebKeys.FILTER_TIME).append("=").append(
			ParamUtil.getString(portletRequest, GSearchWebKeys.FILTER_TIME));
		sb.append("&").append(GSearchWebKeys.FILTER_TYPE).append("=").append(
			ParamUtil.getString(portletRequest, GSearchWebKeys.FILTER_TYPE));
		sb.append("&").append(GSearchWebKeys.SORT_FIELD).append("=").append(
			ParamUtil.getString(portletRequest, GSearchWebKeys.SORT_FIELD));
		sb.append("&").append(GSearchWebKeys.SORT_DIRECTION).append("=").append(
			ParamUtil.getString(portletRequest, GSearchWebKeys.SORT_DIRECTION));
		sb.append("&").append(GSearchWebKeys.START).append("=").append(
			ParamUtil.getString(portletRequest, GSearchWebKeys.START));

		return HtmlUtil.escapeURL(sb.toString());
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
