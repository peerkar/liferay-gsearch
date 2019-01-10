package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
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

    @Reference
    private UserLocalService userLocalService;

    @Override
    public boolean canBuild(Document document) {
        return NAME.equals(document.get(Field.ENTRY_CLASS_NAME));
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getImageSrc(PortletRequest portletRequest, long entryClassPK)
        throws Exception {

        String userPortraitUrl = "";
        try {
            FeedEntry feedEntry = feedEntryLocalService.getFeedEntry(entryClassPK);
            long userId = feedEntry.getUserId();
            ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
            userPortraitUrl = getUserPortraitUrl(userId, themeDisplay);
        } catch (PortalException e) {
            log.error(String.format("Cannot get feed entry for '%s'", entryClassPK));
        } catch (NumberFormatException e) {
            log.error(String.format("Cannot parse '%s' as long.", entryClassPK));
        }
        return userPortraitUrl;
    }

    @Override
    public Map<String, String> getMetadata(Document document, Locale locale, long companyId) {
        String initials = "";
        try {
            long entryClassPK = Long.valueOf(document.get(Field.ENTRY_CLASS_PK));
            FeedEntry feedEntry = feedEntryLocalService.getFeedEntry(entryClassPK);
            long userId = feedEntry.getUserId();
            User user = getUser(userId);
            if (user != null) {
                initials = user.getInitials();
            }
        } catch (PortalException e) {
            log.error(String.format("Cannot get feed entry for '%s'", document.get(Field.ENTRY_CLASS_PK)));
        } catch (NumberFormatException e) {
            log.error(String.format("Cannot parse '%s' as long.", document.get(Field.ENTRY_CLASS_PK)));
        }
        Map<String, String> metadata = new HashMap<>();
        metadata.put("initials", initials);
        return metadata;
    }

    @Override
    public String getBreadcrumbs(PortletRequest portletRequest, PortletResponse portletResponse, Document document, String assetPublisherPageFriendlyURL, long entryClassPK) throws Exception {
        try {
            FeedEntry feedEntry = feedEntryLocalService.getFeedEntry(entryClassPK);
            User user = getUser(feedEntry.getUserId());
            if (user != null) {
                return String.format("%s / %s.%s / %s", "VIRTA", user.getFirstName().toUpperCase(), user.getLastName().toUpperCase(), entryClassPK);
            }
        } catch (PortalException e) {
            log.error(String.format("Cannot get feed entry for '%s'", entryClassPK));
        } catch (NumberFormatException e) {
            log.error(String.format("Cannot parse '%s' as long.", entryClassPK));
        }
        return "";
    }

    @Override
    public String getDescription(PortletRequest portletRequest, PortletResponse portletResponse, Document document, Locale locale) throws SearchException {
        return super.getDescription(portletRequest, portletResponse, document, locale);
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
        return document.get(Field.USER_NAME);
    }

    private User getUser(long userId) {
        try {
            User user = userLocalService.getUser(userId);
            if (user.getStatus() == WorkflowConstants.STATUS_APPROVED) {
                return user;
            }
        } catch (PortalException e) {
            log.error(String.format("Cannot get user with userId %s", userId));
        }
        return null;

    }

    private String getUserPortraitUrl(long userId, ThemeDisplay themeDisplay) {
        String url = "";
        User user = getUser(userId);
        if (user != null) {
            if (user.getPortraitId() != 0) {
                try {
                    url = user.getPortraitURL(themeDisplay);
                    if (url == null) {
                        url = "";
                    }
                } catch (PortalException e) {
                    log.error(String.format("Cannot get user portrait url for user %s", userId));
                }
            }
        }

        return url;
    }

}
