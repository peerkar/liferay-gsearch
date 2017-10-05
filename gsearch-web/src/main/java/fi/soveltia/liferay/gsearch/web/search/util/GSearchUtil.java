
package fi.soveltia.liferay.gsearch.web.search.util;

import com.liferay.asset.publisher.web.constants.AssetPublisherPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.List;

import javax.portlet.ResourceRequest;

public class GSearchUtil {

	/**
	 * Try to find asset published instance on a layout
	 * 
	 * @param layout
	 * @return
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
		
		throw new PortalException("Couldn't find asset publisher on page " + layout.getFriendlyURL() + ". Please check configuration.");
	}

	/**
	 * Get AssetPublisher Layout 
	 * 
	 * @param resourceRequest
	 * @return
	 * @throws PortalException
	 */
	public static Layout getAssetPublisherLayout(
		ResourceRequest resourceRequest, String layoutFriendlyURL)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);

		if (layoutFriendlyURL != null) {
			return LayoutLocalServiceUtil.getFriendlyURLLayout(
				themeDisplay.getScopeGroupId(),
				themeDisplay.getLayout().isPrivateLayout(), layoutFriendlyURL);
		}

		throw new PortalException("Couldn't find asset publisher layout for " +  layoutFriendlyURL + ". Please check configuration.");
	}

	/**
	 * Get current layout url
	 * 
	 * @return String
	 * @throws PortalException
	 */
	public static String getCurrentLayoutURL(ResourceRequest resourceRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);

		Layout selectedLayout =
			LayoutLocalServiceUtil.getLayout(themeDisplay.getPlid());
		return PortalUtil.getLayoutFriendlyURL(selectedLayout, themeDisplay);
	}
}
