
package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.wiki.model.WikiPage;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.WindowState;

import org.apache.commons.lang3.StringEscapeUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.core.impl.util.GSearchUtil;

/**
 * MB message result item builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true,
	service = ResultItemBuilder.class
)
public class MBMessageItemBuilder extends BaseResultItemBuilder
	implements ResultItemBuilder {

	@Override
	public boolean canBuild(Document document) {

		return NAME.equals(document.get(Field.ENTRY_CLASS_NAME));
	}

	/**
	 * This currently handles links for following MBMessage types (by message
	 * classNameId field) com.liferay.wiki.model.WikiPage
	 * com.liferay.journal.model.JournalArticle
	 */
	@Override
	public String getLink(
		QueryContext queryContext,
		Document document)
		throws Exception {

		PortletRequest portletRequest =
						GSearchUtil.getPortletRequestFromContext(queryContext);

		if (portletRequest == null) {
			return null;
		}

		PortletResponse portletResponse =
			GSearchUtil.getPortletResponseFromContext(queryContext);

		long classNameId =
			GetterUtil.getLong(document.get(Field.CLASS_NAME_ID));

		long classPK = GetterUtil.getLong(document.get(Field.CLASS_PK));

		if (classNameId > 0) {
			
			boolean viewResultsInContext = isViewInContext(queryContext);

			String assetPublisherPageURL = getAssetPublisherPageURL(queryContext);

			String className = _portal.getClassName(classNameId);

			if (JournalArticle.class.getName().equals(className)) {
				return getJournalArticleCommentLink(
					portletRequest, portletResponse, queryContext,
					assetPublisherPageURL, classPK, viewResultsInContext);
			}
			else if (WikiPage.class.getName().equals(className)) {
				return getWikiPageCommentLink(
					portletRequest, portletResponse, queryContext, classPK, viewResultsInContext);
			}
		}

		return super.getLink(
			queryContext, document);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle(
		QueryContext queryContext,
		Document document, boolean isHighlight)
		throws NumberFormatException, PortalException {

		long classNameId =
			GetterUtil.getLong(document.get(Field.CLASS_NAME_ID));

		if (classNameId > 0) {

			String className = _portal.getClassName(classNameId);

			if (JournalArticle.class.getName().equals(className) ||
				WikiPage.class.getName().equals(className)) {

				String title = document.get(Field.CONTENT);

				if (title.length() > TITLE_MAXLENGTH) {
					title = title.substring(0, TITLE_MAXLENGTH) + "...";
				}

				// Using Apache commons as it works better than HTMLUtils

				title = StringEscapeUtils.unescapeHtml4(title);
				title = HtmlUtil.stripHtml(title);

				return title;
			}
		}
		return super.getTitle(
			queryContext, document, isHighlight);
	}

	protected String getDLFileEntryCommentLink() {

		return null;
	}

	/**
	 * Get journal article link
	 * 
	 * @param classPK
	 * @return
	 * @throws Exception
	 */
	protected String getJournalArticleCommentLink(
		PortletRequest portletRequest, PortletResponse portletResponse,
		QueryContext queryContext, String assetPublisherPageFriendlyURL,
		long classPK, boolean viewResultsInContext)
		throws Exception {

		AssetRenderer<?> assetRenderer =
			getAssetRenderer(JournalArticle.class.getName(), classPK);

		String link = null;

		if (viewResultsInContext || assetPublisherPageFriendlyURL == null) {

			link = assetRenderer.getURLViewInContext(
				(LiferayPortletRequest) portletRequest,
				(LiferayPortletResponse) portletResponse, null);
		}

		if (Validator.isNull(link)) {

			JournalArticle journalArticle =
				_journalArticleService.getLatestArticle(classPK);
			link = getNotLayoutBoundJournalArticleUrl(
				portletRequest, journalArticle, assetPublisherPageFriendlyURL);
		}

		return link;
	}

	/**
	 * Get wiki comment link.
	 * 
	 * @return
	 * @throws Exception
	 */
	protected String getWikiPageCommentLink(
		PortletRequest portletRequest, PortletResponse portletResponse,
		QueryContext queryContext, long classPK, boolean viewResultsInContext)
		throws Exception {

		AssetRenderer<?> assetRenderer =
			getAssetRenderer(WikiPage.class.getName(), classPK);

		if (viewResultsInContext) {

			return assetRenderer.getURLViewInContext(
				(LiferayPortletRequest) portletRequest,
				(LiferayPortletResponse) portletResponse, "");

		}
		else {
			return assetRenderer.getURLView(
				(LiferayPortletResponse) portletResponse,
				WindowState.MAXIMIZED);
		}
	}

	private static final String NAME = MBMessage.class.getName();

	private static final int TITLE_MAXLENGTH = 80;

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private Portal _portal;
}
