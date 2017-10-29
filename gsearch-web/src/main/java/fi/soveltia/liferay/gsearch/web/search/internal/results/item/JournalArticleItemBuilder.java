package fi.soveltia.liferay.gsearch.web.search.internal.results.item;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.publisher.web.constants.AssetPublisherPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.web.search.internal.util.GSearchUtil;

/**
 * JournalArticle item type result builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true
)
public class JournalArticleItemBuilder extends BaseResultItemBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLink()
		throws Exception {

		String link = null;

		link = getAssetRenderer().getURLViewInContext(
			(LiferayPortletRequest) _portletRequest,
			(LiferayPortletResponse) _portletResponse, null);

		if (Validator.isNull(link)) {
			link = getNotLayoutBoundJournalArticleUrl();
		}

		return link;
	}

	/**
	 * Get a view url for an article which is not bound to a layout or has a
	 * default view page.
	 * 
	 * @return url string
	 * @throws PortalException
	 */
	protected String getNotLayoutBoundJournalArticleUrl()
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) _portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		Layout layout = GSearchUtil.getLayoutByFriendlyURL(
			_portletRequest, _assetPublisherPageFriendlyURL);

		String assetPublisherInstanceId =
			GSearchUtil.findDefaultAssetPublisherInstanceId(layout);

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			JournalArticle.class.getName(), getAssetRenderer().getClassPK());

		JournalArticle journalArticle =
			_journalArticleLocalService.getLatestArticle(
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

	@Reference(unbind = "-")
	protected void setAssetEntryLocalService(
		AssetEntryLocalService assetEntryLocalService) {

		_assetEntryLocalService = assetEntryLocalService;
	}

	@Reference(unbind = "-")
	protected void setJournalArticleLocalService(
		JournalArticleLocalService journalArticleLocalService) {

		_journalArticleLocalService = journalArticleLocalService;
	}

	private static AssetEntryLocalService _assetEntryLocalService;
	private static JournalArticleLocalService _journalArticleLocalService;
}
