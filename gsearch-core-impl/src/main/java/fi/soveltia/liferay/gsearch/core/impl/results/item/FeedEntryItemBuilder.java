package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import fi.helsinki.flamma.feed.model.FeedEntry;
import fi.helsinki.flamma.feed.service.FeedEntryLocalService;
import fi.helsinki.flamma.feed.util.FeedUrlService;
import fi.helsinki.flamma.feed.util.ListFeedEntryService;
import fi.helsinki.flamma.feed.util.ListableFeedEntry;
import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import java.util.Locale;

@Component(
    immediate = true,
    service = ResultItemBuilder.class
)
public class FeedEntryItemBuilder extends BaseResultItemBuilder implements ResultItemBuilder {

    private static final Log log = LogFactoryUtil.getLog(FeedEntryItemBuilder.class);

    private static final String NAME = "fi.helsinki.flamma.feed.model.FeedEntry";
    private static final String TYPE = "feed-entry";
    private static final int TITLE_SNIPPET_LENGTH = 20;

    @Reference
    private ConfigurationHelper _configurationHelperService;

    @Reference
    private FeedUrlService feedUrlService;

    @Reference
    private ListFeedEntryService listFeedEntryService;

    @Reference
    private FeedEntryLocalService feedEntryLocalService;

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
    public String getTitle(PortletRequest portletRequest, PortletResponse portletResponse, Document document, Locale locale, long entryClassPK) throws NumberFormatException, PortalException {
        String content = document.get(Field.CONTENT);
        String title = "";
        if ((content != null) && !content.isEmpty()) {
            if (content.length() <= TITLE_SNIPPET_LENGTH) {
                title = content;
            } else {
                title = content.substring(0, TITLE_SNIPPET_LENGTH) + "...";
            }
        }
        return title;
    }
}
