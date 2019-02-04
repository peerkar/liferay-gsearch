
package fi.soveltia.lifefay.gsearch.hy.results.item.processor;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryProperty;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetCategoryPropertyLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.lifefay.gsearch.hy.util.HYDDMUtil;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.core.api.results.item.processor.ResultItemProcessor;

@Component(
	immediate = true, 
	service = ResultItemProcessor.class
)
public class HYResultItemProcessor implements ResultItemProcessor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {

		return true;
	}

	@Override
	public void process(
		PortletRequest portletRequest, PortletResponse portletResponse,
		QueryContext queryContext, Document document,
		ResultItemBuilder resultItemBuilder, JSONObject resultItem)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		// Set categories to result item.

		setCategories(document, themeDisplay.getLocale(), resultItem);;

		// Set type, icon and breadcrumbs.

		setAdditionalProperties(
			portletRequest, portletResponse, queryContext, document,
			resultItemBuilder, resultItem);
	}

	private Layout getAssetLayout(long groupId, String link)
		throws Exception {

		if (link != null) {
			String regex = ".*https?://[\\w.-]+(/.*?)(?:/-/.*|)";

			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(link);

			if (matcher.matches()) {
				String fullPath = matcher.group(1);

				String friendlyURL = "";
				if (fullPath.startsWith("/group/")) {
					List<String> path = Arrays.asList(fullPath.split("/"));
					friendlyURL =
						"/" + String.join("/", path.subList(3, path.size()));
				}
				else {
					friendlyURL = fullPath;
				}

				try {
					return _layoutLocalService.getFriendlyURLLayout(
						groupId, true, friendlyURL);
				}
				catch (NoSuchLayoutException e) {
					// do nothing
				}
			}
			else {
				_log.warn(
					String.format(
						"Link '%s' does not match with regex '%s", link,
						regex));
			}
		}
		return null;
	}

	/**
	 * Get asset renderer.
	 * 
	 * @param entryClassName
	 * @param entryClassPK
	 * @return
	 * @throws PortalException
	 */
	private AssetRenderer<?> getAssetRenderer(
		String entryClassName, long entryClassPK)
		throws PortalException {

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				entryClassName);

		return assetRendererFactory.getAssetRenderer(entryClassPK);
	}

	/**
	 * Get breadcrumbs for DLFileEntry.
	 * 
	 * @param portletRequest
	 * @param portletResponse
	 * @param queryContext
	 * @param document
	 * @param resultItemBuilder
	 * @return
	 * @throws Exception
	 */
	private String getDLFileEntryBreadcrumbs(
		PortletRequest portletRequest, PortletResponse portletResponse,
		QueryContext queryContext, Document document,
		ResultItemBuilder resultItemBuilder)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		
		long entryClassPK = Long.valueOf(document.get(Field.ENTRY_CLASS_PK));

		long groupId = getAssetRenderer(
			DLFileEntry.class.getName(), entryClassPK).getGroupId();

		Locale locale = themeDisplay.getLocale();

		final List<String> breadcrumbs = new ArrayList<>();

		String link = resultItemBuilder.getLink(
			portletRequest, portletResponse, document, queryContext);

		Layout layout = getAssetLayout(groupId, link);

		if (layout != null) {
			breadcrumbs.add(layout.getName(locale));
			List<Layout> ancestors = layout.getAncestors();
			ancestors.forEach(a -> breadcrumbs.add(a.getName(locale)));
		}
		breadcrumbs.add(getGroupName(groupId, locale));

		Collections.reverse(breadcrumbs);

		return String.join(" / ", breadcrumbs);
	}

	// FIXME

	private String getFeedBreadcrumbs(
		PortletRequest portletRequest, PortletResponse portletResponse,
		QueryContext queryContext, Document document,
		ResultItemBuilder resultItemBuilder)
		throws Exception {

    	/*
        long entryClassPK = Long.valueOf(document.get(Field.ENTRY_CLASS_PK));
    	
    	try {
            FeedEntry feedEntry = feedEntryLocalService.getFeedEntry(entryClassPK);
            User user = getUser(feedEntry.getUserId());
            if (user != null) {
                return String.format("%s / %s.%s / %s", "VIRTA", user.getFirstName().toUpperCase(), user.getLastName().toUpperCase(), entryClassPK);
            }
        } catch (PortalException e) {
            _log.error(String.format("Cannot get feed entry for '%s'", entryClassPK));
        } catch (NumberFormatException e) {
            _log.error(String.format("Cannot parse '%s' as long.", entryClassPK));
        }
        */

		return "";
	}

	/**
	 * Get group name.
	 * 
	 * @param groupId
	 * @param locale
	 * @return
	 */
	private String getGroupName(long groupId, Locale locale) {

		String groupName = "";

		try {
			Group group = _groupLocalService.getGroup(groupId);
			groupName = group.getDescriptiveName(locale);
		}
		catch (PortalException e) {

			_log.warn(String.format("Group with id %s not found", groupId));
		}
		return groupName;
	}

	/**
	 * Get journal article breadcrumbs.
	 * 
	 * @param portletRequest
	 * @param portletResponse
	 * @param document
	 * @param assetPublisherPageFriendlyURL
	 * @param entryClassPK
	 * @return
	 * @throws Exception
	 */
	private String getJournalArticleBreadcrumbs(
		PortletRequest portletRequest, PortletResponse portletResponse,
		QueryContext queryContext, Document document,
		ResultItemBuilder resultItemBuilder)
		throws Exception {

		long groupId = Long.valueOf(document.get(Field.GROUP_ID));

		Locale locale = portletRequest.getLocale();

		final List<String> breadcrumbs = new ArrayList<>();

		String link = resultItemBuilder.getLink(
			portletRequest, portletResponse, document, queryContext);
		Layout layout = getAssetLayout(groupId, link);

		if (layout != null) {
			breadcrumbs.add(layout.getName(locale));
			List<Layout> ancestors = layout.getAncestors();
			ancestors.forEach(a -> breadcrumbs.add(a.getName(locale)));
		}
		breadcrumbs.add(getGroupName(groupId, locale));

		Collections.reverse(breadcrumbs);

		return String.join(" / ", breadcrumbs);
	}

	/**
	 * Check whether current item is a "News" content. Checked from HY facet
	 * params.
	 * 
	 * @param queryContext
	 * @param document
	 * @return
	 */
	private boolean isNewsArticle(
		QueryContext queryContext, Document document) {

		String ddmStructureKey = document.get("ddmStructureKey");

		List<String> newsStructureKeys =
			HYDDMUtil.getHYNewsDDMStructureKeys(queryContext);

		return newsStructureKeys.contains(ddmStructureKey);
	}

	/**
	 * Set type key.
	 * 
	 * @param document
	 * @param resultItem
	 */
	private void setAdditionalProperties(
		PortletRequest portletRequest, PortletResponse portletResponse,
		QueryContext queryContext, Document document,
		ResultItemBuilder resultItemBuilder, JSONObject resultItem) {

		String entryClassName = document.get(Field.ENTRY_CLASS_NAME);

		try {

			if (DLFileEntry.class.getName().equals(entryClassName)) {

				resultItem.put("typeKey", "file");
				resultItem.put("icon", "icon-file-text");
				resultItem.put(
					"breadcrumbs",
					getDLFileEntryBreadcrumbs(
						portletRequest, portletResponse, queryContext, document,
						resultItemBuilder));
			}
			else if (FEED_ENTRY_CLASS.equals(entryClassName)) {

				resultItem.put("typeKey", "news");
				resultItem.put("icon", "icon-news");
				resultItem.put(
					"breadcrumbs",
					getFeedBreadcrumbs(
						portletRequest, portletResponse, queryContext, document,
						resultItemBuilder));

			}
			else if (isNewsArticle(queryContext, document)) {

				resultItem.put("typeKey", "news");
				resultItem.put("icon", "icon-news");
				resultItem.put(
					"breadcrumbs",
					getJournalArticleBreadcrumbs(
						portletRequest, portletResponse, queryContext, document,
						resultItemBuilder));

			}
			else if (JournalArticle.class.getName().equals(entryClassName)) {

				resultItem.put("typeKey", "content");
				resultItem.put("icon", "");
				resultItem.put(
					"breadcrumbs",
					getJournalArticleBreadcrumbs(
						portletRequest, portletResponse, queryContext, document,
						resultItemBuilder));
			}
		}
		catch (Exception e) {

			_log.error(e.getMessage(), e);
		}
	}

	/**
	 * Set categories.
	 * 
	 * @param document
	 * @param locale
	 * @param resultItem
	 */
	private void setCategories(
		Document document, Locale locale, JSONObject resultItem) {

		String[] categoryIds = document.getValues(Field.ASSET_CATEGORY_IDS);

		List<Map<String, String>> categories =
			new ArrayList<Map<String, String>>();

		if (categoryIds != null) {

			for (String id : categoryIds) {

				try {
					AssetCategory category =
						_assetCategoryLocalService.getCategory(
							Long.valueOf(id));
					List<AssetCategoryProperty> properties =
						_assetCategoryPropertyLocalService.getCategoryProperties(
							category.getCategoryId());

					String name = "";
					String colorCode = "";

					for (AssetCategoryProperty property : properties) {

						if (property.getKey().equals("abbreviation")) {
							name = property.getValue();

						}
						else if (property.getKey().equals("colorCode")) {
							colorCode = property.getValue();
						}
					}

					if (name.isEmpty()) {
						name = category.getTitle(locale);
					}

					Map<String, String> item = new HashMap<String, String>();

					item.put("name", name);
					item.put("colorCode", "#" + colorCode);

					categories.add(item);

				}
				catch (PortalException e) {

					_log.error(
						String.format(
							"Cannot get asset category for id %s", id));

				}
				catch (NumberFormatException e) {

					// do nothing
				}
			}

		}

		// Set to result item.

		if (categories != null && categories.size() > 0) {

			resultItem.put("categories", categories);
		}
	}

	private static final Logger _log =
		LoggerFactory.getLogger(HYResultItemProcessor.class);

	private static final String FEED_ENTRY_CLASS =
		"fi.helsinki.flamma.feed.model.FeedEntry";

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetCategoryPropertyLocalService _assetCategoryPropertyLocalService;

}
