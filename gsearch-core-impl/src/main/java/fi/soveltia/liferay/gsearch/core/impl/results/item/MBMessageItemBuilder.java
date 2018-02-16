
package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.wiki.model.WikiPage;

import javax.portlet.WindowState;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * MB message result item builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true
)
public class MBMessageItemBuilder extends BaseResultItemBuilder {

	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	@Override
	public String getImageSrc()
		throws Exception {

		// return _portletRequest.getContextPath() + DEFAULT_IMAGE;
		return null;
	}

	/**
	 * This currently handles links for following MBMessage types (by message
	 * classNameId field) 
	 * com.liferay.wiki.model.WikiPage
	 * com.liferay.journal.model.JournalArticle
	 */
	@Override
	public String getLink()
		throws Exception {

		long classNameId =
			GetterUtil.getLong(_document.get(Field.CLASS_NAME_ID));

		long classPK = GetterUtil.getLong(_document.get(Field.CLASS_PK));

		if (classNameId > 0) {

			String className = PortalUtil.getClassName(classNameId);

			if (JournalArticle.class.getName().equals(className)) {
				return getJournalArticleCommentLink(classPK);
			}
			else if (WikiPage.class.getName().equals(className)) {
				return getWikiPageCommentLink(classPK);
			}
		}
		
		return getAssetRenderer().getURLViewInContext(
			(LiferayPortletRequest) _portletRequest,
			(LiferayPortletResponse) _portletResponse, "");
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
	protected String getJournalArticleCommentLink(long classPK) throws Exception {

		AssetRenderer<?> assetRenderer = 
						getAssetRenderer(JournalArticle.class.getName(), classPK);
		
		String link = assetRenderer.getURLViewInContext(
			(LiferayPortletRequest) _portletRequest,
			(LiferayPortletResponse) _portletResponse, null);

		if (Validator.isNull(link)) {
			
			JournalArticle journalArticle = _journalArticleService.getLatestArticle(classPK);
			link = getNotLayoutBoundJournalArticleUrl(journalArticle);
		}
		
		return link;
	}

	/**
	 * Get wiki comment link.
	 * 
	 * @return
	 * @throws Exception
	 */
	protected String getWikiPageCommentLink(long classPK)
		throws Exception {

		AssetRenderer<?> assetRenderer =
			getAssetRenderer(WikiPage.class.getName(), classPK);

		return assetRenderer.getURLView(
			(LiferayPortletResponse) _portletResponse, WindowState.MAXIMIZED);
	}

	@Reference(unbind = "-")
	protected void setJournalArticleService(
		JournalArticleService journalArticleService) {

		_journalArticleService = journalArticleService;
	}

	public static final String DEFAULT_IMAGE =
					"/o/gsearch-web/images/asset-types/discussion.png";

	private static JournalArticleService _journalArticleService;
	
	private static final Log _log =
					LogFactoryUtil.getLog(MBMessageItemBuilder.class);

}
