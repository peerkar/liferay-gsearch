
package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.webserver.WebServerServletTokenUtil;
import com.liferay.portal.search.document.Document;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.core.impl.util.GSearchUtil;

/**
 * JournalArticle item type result builder.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ResultItemBuilder.class
)
public class JournalArticleItemBuilder
	extends BaseResultItemBuilder implements ResultItemBuilder {

	public static final String _NAME = JournalArticle.class.getName();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canBuild(Document document) {
		return _NAME.equals(document.getString(Field.ENTRY_CLASS_NAME));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLink(QueryContext queryContext, Document document)
		throws Exception {

		PortletRequest portletRequest =
			GSearchUtil.getPortletRequestFromContext(queryContext);

		if (portletRequest == null) {
			return null;
		}

		PortletResponse portletResponse =
			GSearchUtil.getPortletResponseFromContext(queryContext);

		boolean viewResultsInContext = isViewInContext(queryContext);

		String assetPublisherPageURL = getAssetPublisherPageURL(queryContext);

		if (assetPublisherPageURL == null) {
			return null;
		}

		StringBundler sb = new StringBundler();

		if (viewResultsInContext) {
			sb.append(
				getAssetRenderer(
					document
				).getURLViewInContext(
					(LiferayPortletRequest)portletRequest,
					(LiferayPortletResponse)portletResponse, null
				));
		}

		if ((sb.length() == 0) || sb.toString().equals("null")) {

			// It can happen that there's a string "null".

			sb = new StringBundler();

			sb.append(
				getNotLayoutBoundJournalArticleUrl(
					portletRequest, getJournalArticle(document),
					assetPublisherPageURL));
		}

		return sb.toString();
	}

	@Override
	public String getThumbnail(QueryContext queryContext, Document document)
		throws Exception {

		PortletRequest portletRequest =
			GSearchUtil.getPortletRequestFromContext(queryContext);

		if (portletRequest != null) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			return getJournalArticle(
				document
			).getArticleImageURL(
				themeDisplay
			);
		}

		// Headless access.

		long smallImageId = getJournalArticle(
			document
		).getSmallImageId();

		return StringBundler.concat(
			queryContext.getParameter(ParameterNames.PATH_IMAGE) +
				"/journal/article?img_id=",
			String.valueOf(smallImageId), "&t=",
			WebServerServletTokenUtil.getToken(smallImageId));
	}

	/**
	 * Gets journal article.
	 *
	 * @return
	 * @throws PortalException
	 */
	protected JournalArticle getJournalArticle(Document document)
		throws PortalException {

		long entryClassPK = document.getLong(Field.ENTRY_CLASS_PK);

		return _journalArticleService.getLatestArticle(entryClassPK);
	}

	@Reference
	private JournalArticleService _journalArticleService;

}