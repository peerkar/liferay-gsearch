
package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JournalArticle item type result builder.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true,
	service = ResultItemBuilder.class
)
public class JournalArticleItemBuilder extends BaseResultItemBuilder
	implements ResultItemBuilder {

	@Override
	public boolean canBuild(Document document) {

		return NAME.equals(document.get(Field.ENTRY_CLASS_NAME));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws Exception
	 */
	@Override
	public String getImageSrc()
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay) _portletRequest.getAttribute(
			GSearchWebKeys.THEME_DISPLAY);

		return getJournalArticle().getArticleImageURL(themeDisplay);
	}

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
			link = getNotLayoutBoundJournalArticleUrl(getJournalArticle());
		}

		return link;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getBreadcrumbs() throws Exception {

		final List<String> breadcrumbs = new ArrayList<>();

		long groupId = getJournalArticle().getGroupId();

		String link = getLink();

		if (link != null) {
			String regex = ".*https?://[\\w\\.]+(/.*?)(?:/-/.*|)";

			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(link);

			if (matcher.matches()) {
				String fullPath = matcher.group(1);

				String friendlyURL = "";
				if (fullPath.startsWith("/group/")) {
					List<String> path = Arrays.asList(fullPath.split("/"));
					friendlyURL = "/" + String.join("/", path.subList(3, path.size()));
				} else {
					friendlyURL = fullPath;
				}

				try {
					Layout layout = _layoutLocalService.getFriendlyURLLayout(groupId, true, friendlyURL);
					breadcrumbs.add(layout.getName(_locale));
					List<Layout> ancestors = layout.getAncestors();
					ancestors.forEach(a -> breadcrumbs.add(a.getName(_locale)));
				} catch (NoSuchLayoutException e) {
					// do nothing
				}
			}
		}

		breadcrumbs.add(getGroupName(groupId));

		Collections.reverse(breadcrumbs);

		return String.join(" / ", breadcrumbs);
	}

	@Override
	public String[] getCategories() {
		String[] categoryIds = _document.getValues(Field.ASSET_CATEGORY_IDS);

		List<String> categories = new ArrayList<>();

		if (categoryIds != null) {
			for (String id : categoryIds) {
				try {
					AssetCategory category = _assetCategoryLocalService.getCategory(Long.valueOf(id));
					categories.add(category.getTitle(_locale));
				} catch (PortalException e) {
					log.error(String.format("Cannot get asset category for id %s", id));
				} catch (NumberFormatException e) {
					// do nothing
				}
			}

		}
		return categories.toArray(new String[] {});
	}

	@Override
	public String getDescription() throws SearchException {

		String languageId = _locale.toString();

		Indexer<?> indexer =
			getIndexer(_document.get(Field.ENTRY_CLASS_NAME));

		if (indexer != null) {
			String snippet = _document.get(Field.SNIPPET + StringPool.UNDERLINE + Field.CONTENT + StringPool.UNDERLINE + languageId);

			if ((snippet != null) && !snippet.isEmpty()) {
				return HtmlUtil.stripHtml(snippet);
			}
		}
		return super.getDescription();
	}


	private String getGroupName(long groupId) {
		String groupName = "";
		try {
			Group group = _groupLocalService.getGroup(groupId);
			groupName = group.getDescriptiveName(_locale);
		} catch (PortalException e) {
			log.warn(String.format("Group with id %s not found", groupId));
		}
		return groupName;
	}

	/**
	 * Get journal article.
	 *
	 * @return
	 * @throws PortalException
	 */
	protected JournalArticle getJournalArticle()
		throws PortalException {

		return _journalArticleService.getLatestArticle(_entryClassPK);
	}

	@Reference(unbind = "-")
	protected void setJournalArticleService(
		JournalArticleService journalArticleService) {

		_journalArticleService = journalArticleService;
	}

	@Reference(unbind = "-")
	protected void setLayoutLocalService(
		LayoutLocalService layoutLocalService) {

		_layoutLocalService = layoutLocalService;
	}

	@Reference(unbind = "-")
	protected void setGroupLocalService(
		GroupLocalService groupLocalService) {

		_groupLocalService = groupLocalService;
	}

	@Reference(unbind = "-")
	protected void setAssetCategoryLocalService(
		AssetCategoryLocalService assetCategoryLocalService) {

		_assetCategoryLocalService = assetCategoryLocalService;
	}

	private static JournalArticleService _journalArticleService;

	private static LayoutLocalService _layoutLocalService;

	private static GroupLocalService _groupLocalService;

	private static AssetCategoryLocalService _assetCategoryLocalService;

	private static final String NAME = JournalArticle.class.getName();

	private static final Log log = LogFactoryUtil.getLog(JournalArticleItemBuilder.class);
}
