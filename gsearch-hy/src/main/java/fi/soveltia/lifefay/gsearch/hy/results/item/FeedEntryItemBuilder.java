package fi.soveltia.lifefay.gsearch.hy.results.item;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.UserLocalService;

import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.impl.util.GSearchUtil;
import fi.soveltia.liferay.gsearch.localization.LocalizationHelper;
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

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

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
		QueryContext queryContext, Document document)
		throws Exception {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest) queryContext.getParameter(
				ParameterNames.HTTP_SERVLET_REQUEST);

		PortletRequest portletRequest =
			GSearchUtil.getPortletRequest(httpServletRequest);

		ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

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
	public String getTitle(QueryContext queryContext,
		Document document, boolean isHighlight)
		throws NumberFormatException, PortalException {

		String prefix = _localizationHelper.getLocalization(queryContext.getLocale(), "title-prefix-feed");

		return String.format("(%s) %s", prefix, document.get(Field.USER_NAME));
	}


	private static final Log log =
		LogFactoryUtil.getLog(FeedEntryItemBuilder.class);

	private static final String NAME = FeedEntry.class.getName();

	@Reference
	private FeedUrlService feedUrlService;

	@Reference
	private ListFeedEntryService listFeedEntryService;

	@Reference
	private FeedEntryLocalService feedEntryLocalService;

	@Reference
	private UserLocalService userLocalService;

	@Reference
	private LocalizationHelper _localizationHelper;

}