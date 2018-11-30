package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import fi.helsinki.flamma.feed.model.FeedEntry;
import fi.helsinki.flamma.feed.service.FeedEntryLocalService;
import fi.helsinki.flamma.feed.util.FeedUrlService;
import fi.helsinki.flamma.feed.util.ListFeedEntryService;
import fi.helsinki.flamma.feed.util.ListableFeedEntry;
import fi.soveltia.liferay.gsearch.core.api.results.SearchResultCategory;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@Component(
    immediate = true,
    service = ResultItemBuilder.class
)
public class FeedEntryItemBuilder extends BaseResultItemBuilder implements ResultItemBuilder {

    private static final Log log = LogFactoryUtil.getLog(FeedEntryItemBuilder.class);

    private static final String NAME = FeedEntry.class.getName();
    private static final String TYPE = "feed-entry";

    private static Map<Locale, ResourceBundle> resourceBundles = new HashMap<>();

    @Reference
    private FeedUrlService feedUrlService;

    @Reference
    private ListFeedEntryService listFeedEntryService;

    @Reference
    private FeedEntryLocalService feedEntryLocalService;

    @Reference
    private ResultItemCommonService resultItemCommonService;

    @Override
    public boolean canBuild(Document document) {
        return NAME.equals(document.get(Field.ENTRY_CLASS_NAME));
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getLink(PortletRequest portletRequest, PortletResponse portletResponse, Document document, String assetPublisherPageFriendlyURL, long entryClassPK) {
        ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
        String url = "";
        try {
            long feedEntryId = Long.valueOf(document.get(Field.ENTRY_CLASS_PK));
            FeedEntry feedEntry = feedEntryLocalService.getFeedEntry(feedEntryId);
            ListableFeedEntry listableFeedEntry = listFeedEntryService.getListable(feedEntry, themeDisplay);
            url = feedUrlService.getSingleFeedEntryUrl(listableFeedEntry, themeDisplay);
        } catch (PortalException e) {
            log.error(String.format("Cannot get feed entry for '%s'", document.get(Field.ENTRY_CLASS_PK)));
        } catch (NumberFormatException e) {
            log.error(String.format("Cannot parse '%s' as long.", document.get(Field.ENTRY_CLASS_PK)));
        }
        return url;
    }

    @Override
    public SearchResultCategory[] getCategories(Document document, Locale locale) {
        return resultItemCommonService.getCategories(document, locale);
    }

    @Override
    public String getTitle(PortletRequest portletRequest, PortletResponse portletResponse, Document document, Locale locale, long entryClassPK) throws NumberFormatException, PortalException {
        String userName = document.get(Field.USER_NAME);
        String titlePrefix = getLocalization(locale, "feed-entry-title");
        return String.format("%s: %s", titlePrefix, userName);
    }

    private String getLocalization(Locale locale, String key) {
        if (resourceBundles.get(locale) == null) {
            resourceBundles.put(locale, ResourceBundleUtil.getBundle("content.Language", locale, FeedEntryItemBuilder.class));
        }
        return resourceBundles.get(locale).getString(key);
    }
}
