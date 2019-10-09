package fi.soveltia.liferay.gsearch.morelikethis.util;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.wiki.model.WikiPage;

import java.util.List;

import javax.portlet.ResourceRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Asset helper implementation.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = AssetHelper.class
)
public class AssetHelperImpl implements AssetHelper {

	/**
	 * Tries to find the Asset Entry for the contents currently shown on the Asset
	 * publisher portlet.
	 *
	 * @param resourceRequest
	 * @return
	 * @throws PortalException
	 * @throws NumberFormatException
	 */
	@Override
	public AssetEntry findAssetEntry(ResourceRequest resourceRequest)
		throws NumberFormatException, PortalException {

		AssetEntry assetEntry = null;

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String layoutURL = themeDisplay.getLayout(
		).getFriendlyURL();

		String currentFriendlyURL = resourceRequest.getParameter("currentURL");

		String assetUrlOrId = null;
		
		if (currentFriendlyURL.indexOf(layoutURL) < 0) {

			int startPos = currentFriendlyURL.indexOf("/-/") + 3;

			if (currentFriendlyURL.indexOf("/-/") > 0) {
				
				int endPos = currentFriendlyURL.indexOf("/", startPos);
				if (endPos < 0) {
					endPos = currentFriendlyURL.indexOf("?", startPos);
				}
				
				if (endPos < 0) {
					assetUrlOrId = currentFriendlyURL.substring(startPos);
				} else {
					assetUrlOrId = currentFriendlyURL.substring(startPos, endPos);
				}
				
				JournalArticle journalArticle = findJournalArticle(
					assetUrlOrId);

				assetEntry = _assetEntryLocalService.getEntry(
					JournalArticle.class.getName(),
					journalArticle.getResourcePrimKey());
			}
		}
		else if (currentFriendlyURL.indexOf("/-/asset_publisher") > 0) {

			if (currentFriendlyURL.indexOf(JOURNAL_KEY_ID) > 0) {

				assetUrlOrId = getAssetUrlOrId(currentFriendlyURL, JOURNAL_KEY_ID);

				assetEntry = _assetEntryLocalService.getAssetEntry(
						Long.valueOf(assetUrlOrId));

			} else if (currentFriendlyURL.indexOf(JOURNAL_KEY) > 0) {

				assetUrlOrId = getAssetUrlOrId(currentFriendlyURL, JOURNAL_KEY);

				JournalArticle journalArticle = findJournalArticle(assetUrlOrId);
			
				assetEntry = _assetEntryLocalService.getEntry(
					JournalArticle.class.getName(),
					journalArticle.getResourcePrimKey());
			
			}
			else if (currentFriendlyURL.indexOf(DL_KEY) > 0) {
				assetUrlOrId = getAssetUrlOrId(currentFriendlyURL, DL_KEY);

				assetEntry = _assetEntryLocalService.getAssetEntry(
					Long.valueOf(assetUrlOrId));
			}
			else if (currentFriendlyURL.indexOf(WIKI_KEY) > 0) {
				assetUrlOrId = getAssetUrlOrId(currentFriendlyURL, WIKI_KEY);

				assetEntry = _assetEntryLocalService.getAssetEntry(
					Long.valueOf(assetUrlOrId));
			}
			else if (currentFriendlyURL.indexOf(BLOG_KEY) > 0) {
				assetUrlOrId = getAssetUrlOrId(currentFriendlyURL, BLOG_KEY);

				BlogsEntry blogsEntry = findBlogsEntry(assetUrlOrId);

				assetEntry = _assetEntryLocalService.getEntry(
					BlogsEntry.class.getName(), blogsEntry.getPrimaryKey());
			}
		}
		else {

			// Try to get wiki page resourcePrimKey.

			String[] urlParts = currentFriendlyURL.split("&");

			for (String s : urlParts) {
				if (s.startsWith(
						"_com_liferay_wiki_web_portlet_WikiPortlet_pageResourcePrimKey")) {

					String[] valueParts = s.split("=");
					assetEntry = _assetEntryLocalService.getEntry(
						WikiPage.class.getName(), Long.valueOf(valueParts[1]));

					break;
				}
			}
		}

		return assetEntry;
	}

	/**
	 * Tries to find a Blogs Entry by urlTitle.
	 * 
	 * @param urlTitle
	 * @return
	 */
	private BlogsEntry findBlogsEntry(String urlTitle) {
		DynamicQuery dynamicQuery = _blogsEntryLocalService.dynamicQuery(
		).add(
			RestrictionsFactoryUtil.eq("urlTitle", urlTitle)
		).add(
			RestrictionsFactoryUtil.eq("status", 0)
		);

		List<BlogsEntry> entries = _blogsEntryLocalService.dynamicQuery(
			dynamicQuery);

		if ((entries != null) && (entries.size() > 0)) {
			return entries.get(0);
		}

		return null;
	}

	/**
	 * Tries to find a Journal Article by urlTitle.
	 *
	 * @param urlTitle
	 * @return
	 */
	private JournalArticle findJournalArticle(String urlTitle) {
		DynamicQuery dynamicQuery = _journalArticleLocalService.dynamicQuery(
		).add(
			RestrictionsFactoryUtil.eq("urlTitle", urlTitle)
		).add(
			RestrictionsFactoryUtil.eq("status", 0)
		);

		List<JournalArticle> entries =
			JournalArticleLocalServiceUtil.dynamicQuery(dynamicQuery);

		if ((entries != null) && (entries.size() > 0)) {
			return entries.get(0);
		}

		return null;
	}

	/**
	 * Tries to parse an asset id or content friendly url from url.
	 *
	 * @param currentFriendlyURL
	 * @param key
	 * @return
	 */
	private String getAssetUrlOrId(String currentFriendlyURL, String key) {
		
		int start = currentFriendlyURL.indexOf(key) + key.length();

		int stop = currentFriendlyURL.indexOf("?");

		if (stop < 0) {
			return currentFriendlyURL.substring(start);
		}
		else if ((start > 0) && (stop > 0) && (start < stop)) {
			return currentFriendlyURL.substring(start, stop);
		}

		return null;
	}

	private static final String BLOG_KEY = "/blog/";

	private static final String DL_KEY = "/document/id/";

	private static final String JOURNAL_KEY_ID = "/content/id/";

	private static final String JOURNAL_KEY = "/content/";

	private static final String WIKI_KEY = "/wiki/id/";

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

}