
package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;

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

	@Override
	public String getThumbnail(PortletRequest portletRequest, Document document)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		return getJournalArticle(document).getArticleImageURL(themeDisplay);
	}

	@Override
	public String getLink(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document, QueryContext queryContext)
		throws Exception {

		boolean viewResultsInContext = isViewInContext(queryContext);

		String assetPublisherPageURL = getAssetPublisherPageURL(queryContext);

		StringBundler sb = new StringBundler();

		if (viewResultsInContext) {

			sb.append(
				getAssetRenderer(document).getURLViewInContext(
					(LiferayPortletRequest) portletRequest,
					(LiferayPortletResponse) portletResponse, null));

		}

		if (sb.length() == 0 || sb.toString().equals("null")) {

			// It can happen that there's a string "null".

			sb = new StringBundler();

			sb.append(
				getNotLayoutBoundJournalArticleUrl(
					portletRequest, getJournalArticle(document),
					assetPublisherPageURL));
		}

		return sb.toString();
	}

	/**
	 * Get journal article.
	 * 
	 * @return
	 * @throws PortalException
	 */
	protected JournalArticle getJournalArticle(Document document)
		throws PortalException {

		long entryClassPK = Long.valueOf(document.get(Field.ENTRY_CLASS_PK));

		return _journalArticleService.getLatestArticle(entryClassPK);
	}

	@Reference
	private JournalArticleService _journalArticleService;

	private static final String NAME = JournalArticle.class.getName();

}
