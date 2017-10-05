
package fi.soveltia.liferay.gsearch.web.search.results;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.publisher.web.constants.AssetPublisherPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import fi.soveltia.liferay.gsearch.web.search.util.GSearchUtil;

/**
 * JournalArticle Result Builder
 * 
 * @author Petteri Karttunen
 */
public class JournalArticleResultBuilder extends BaseResultBuilder {

	public JournalArticleResultBuilder(
		ResourceRequest resourceRequest,
		ResourceResponse resourceResponse, Document document, String assetPublisherPageFriendlyURL) {
		super(resourceRequest, resourceResponse, document);
		
		_assetPublisherPageFriendlyURL = assetPublisherPageFriendlyURL;
	}

	@Override
	public String getLink()
		throws Exception {

		String link = null;

		link = getAssetRenderer().getURLViewInContext(
			(LiferayPortletRequest) _resourceRequest,
			(LiferayPortletResponse) _resourceResponse, null);

		if (Validator.isNull(link)) {
			link = getNotLinkedJournalArticleUrl();
		}

		return link;
	}

	/**
	 * Get a view url for an article that's not bound to a layout or has a
	 * default view page.
	 * 
	 * @return
	 * @throws PortalException
	 */
	protected String getNotLinkedJournalArticleUrl()
		throws PortalException {

		ThemeDisplay themeDisplay =
						(ThemeDisplay) _resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);

		Layout layout =
			GSearchUtil.getAssetPublisherLayout(_resourceRequest, _assetPublisherPageFriendlyURL);

		String assetPublisherInstanceId =
			GSearchUtil.findDefaultAssetPublisherInstanceId(layout);

		AssetEntry assetEntry = AssetEntryLocalServiceUtil.getEntry(
			JournalArticle.class.getName(), getAssetRenderer().getClassPK());

		JournalArticle journalArticle =
			JournalArticleLocalServiceUtil.getLatestArticle(
				assetEntry.getClassPK());

		StringBundler sb = new StringBundler();
		sb.append(PortalUtil.getLayoutFriendlyURL(layout, themeDisplay));
		sb.append("/-/asset_publisher/");
		sb.append(assetPublisherInstanceId);
		sb.append("/content/");
		sb.append(journalArticle.getUrlTitle());
		sb.append("?_");
		sb.append(AssetPublisherPortletKeys.ASSET_PUBLISHER);
		sb.append("_INSTANCE_");
		sb.append(assetPublisherInstanceId);
		sb.append("_groupId=");
		sb.append(journalArticle.getGroupId());

		return sb.toString();
	}
	
	String _assetPublisherPageFriendlyURL;
}
