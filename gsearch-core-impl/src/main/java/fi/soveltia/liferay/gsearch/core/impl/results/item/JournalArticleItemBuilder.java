package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.constants.GSearchWebKeys;

/**
 * JournalArticle item type result builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true
)
public class JournalArticleItemBuilder extends BaseResultItemBuilder {

	/**
	 * {@inheritDoc}
	 * @throws Exception 
	 */
	@Override
	public String getImageSrc() throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)_portletRequest.getAttribute(GSearchWebKeys.THEME_DISPLAY);

		return getJournalArticle().getArticleImageURL(themeDisplay);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLink()
		throws Exception {

		String link = null;

		link = getAssetRenderer().getURLViewInContext(
			(LiferayPortletRequest) _portletRequest,
			(LiferayPortletResponse) _portletResponse, null);

		if (Validator.isNull(link)) {
			link = getNotLayoutBoundJournalArticleUrl(getJournalArticle());
		}

		return link;
	}

	/**
	 * Get journal article.
	 * 
	 * @return
	 * @throws PortalException 
	 */
	protected JournalArticle getJournalArticle() throws PortalException {

		if (_journalArticle == null) {
			_journalArticle = _journalArticleService.getLatestArticle(_entryClassPK);
		}
		return _journalArticle;
	}

	@Reference(unbind = "-")
	protected void setJournalArticleService(
		JournalArticleService journalArticleService) {

		_journalArticleService = journalArticleService;
	}

	protected JournalArticle _journalArticle;
	
	private static JournalArticleService _journalArticleService;
}
