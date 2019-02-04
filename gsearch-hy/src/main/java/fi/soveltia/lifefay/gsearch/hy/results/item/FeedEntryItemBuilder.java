/*
package fi.soveltia.lifefay.gsearch.hy.results.item;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.helsinki.flamma.feed.model.FeedEntry;
import fi.helsinki.flamma.feed.service.FeedEntryLocalService;
import fi.helsinki.flamma.feed.util.FeedUrlService;
import fi.helsinki.flamma.feed.util.ListFeedEntryService;
import fi.helsinki.flamma.feed.util.ListableFeedEntry;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.core.impl.results.item.BaseResultItemBuilder;

@Component(
	immediate = true, 
	service = ResultItemBuilder.class
)
public class FeedEntryItemBuilder extends BaseResultItemBuilder
	implements ResultItemBuilder {

	@Override
	public boolean canBuild(Document document) {

		return NAME.equals(document.get(Field.ENTRY_CLASS_NAME));
	}

	@Override
	public String getLink(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document, QueryContext queryContext)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		String url = "";
		try {
			long feedEntryId = Long.valueOf(document.get(Field.ENTRY_CLASS_PK));
			FeedEntry feedEntry =
				feedEntryLocalService.getFeedEntry(feedEntryId);
			ListableFeedEntry listableFeedEntry =
				listFeedEntryService.getListable(feedEntry, themeDisplay);
			url = feedUrlService.getSingleFeedEntryUrl(
				listableFeedEntry, themeDisplay);
		}
		catch (PortalException e) {
			log.error(
				String.format(
					"Cannot get feed entry for '%s'",
					document.get(Field.ENTRY_CLASS_PK)));
		}
		catch (NumberFormatException e) {
			log.error(
				String.format(
					"Cannot parse '%s' as long.",
					document.get(Field.ENTRY_CLASS_PK)));
		}
		return url;
	}

	@Override
	public String getTitle(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document, boolean isHighlight)
		throws NumberFormatException, PortalException {

		return document.get(Field.USER_NAME);
	}

	private static final Log log =
		LogFactoryUtil.getLog(FeedEntryItemBuilder.class);

	private static final String NAME = FeedEntry.class.getName();

	private static Map<Locale, ResourceBundle> resourceBundles =
		new HashMap<>();

	@Reference
	private FeedUrlService feedUrlService;

	@Reference
	private ListFeedEntryService listFeedEntryService;

	@Reference
	private FeedEntryLocalService feedEntryLocalService;

	@Reference
	private UserLocalService userLocalService;

}
*/