
package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.constants.GSearchWebKeys;
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
	public String getImageSrc(PortletRequest portletRequest, Document document)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(
			GSearchWebKeys.THEME_DISPLAY);

		return getJournalArticle(document).getArticleImageURL(themeDisplay);
	}

	@Override
	public String getLink(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document, String assetPublisherPageFriendlyURL)
		throws Exception {

		String link = null;

		link = getAssetRenderer(document).getURLViewInContext(
			(LiferayPortletRequest) portletRequest,
			(LiferayPortletResponse) portletResponse, null);

		if (Validator.isNull(link)) {
			link = getNotLayoutBoundJournalArticleUrl(
				portletRequest, getJournalArticle(document),
				assetPublisherPageFriendlyURL);
		}

		return link;
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

	@Reference(unbind = "-")
	protected void setJournalArticleService(
		JournalArticleService journalArticleService) {

		_journalArticleService = journalArticleService;
	}

	private static JournalArticleService _journalArticleService;

	private static final String NAME = JournalArticle.class.getName();

}
