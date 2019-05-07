
package fi.soveltia.lifefay.gsearch.hy.results.item;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.journal.service.JournalContentSearchLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.WebKeys;
import fi.helsinki.flamma.common.group.FlammaGroupService;
import fi.soveltia.lifefay.gsearch.hy.util.HYDDMUtil;
import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.core.impl.results.item.JournalArticleItemBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import java.util.List;

@Component(
	immediate = true,
	service = ResultItemBuilder.class,
	property = {
		"service.ranking:Integer=100"
	}
)
public class HYJournalArticleItemBuilder extends JournalArticleItemBuilder {

	@Override
	public boolean canBuild(Document document) {

		String[] facetConfiguration = _configurationHelper.getFacetConfiguration();

		List<String> newsKeys = HYDDMUtil.getHYNewsDDMStructureKeys(facetConfiguration);

		return NAME.equals(document.get(Field.ENTRY_CLASS_NAME)) && !newsKeys.contains(document.get("ddmStructureKey"));
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

			// search article from other groups' layouts
			String articleId = document.get(Field.ARTICLE_ID);
			List<Group> groups = getSiteGroups();
			for (Group group : groups) {
				List<Long> groupLayoutIds = JournalContentSearchLocalServiceUtil.getLayoutIds(group.getGroupId(), true, articleId);
				if ((groupLayoutIds != null) && !groupLayoutIds.isEmpty()) {
					long layoutId = groupLayoutIds.get(0);
					Layout layout = layoutLocalService.getLayout(group.getGroupId(), true, layoutId);
					ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
					sb.append(PortalUtil.getLayoutFriendlyURL(layout, themeDisplay));
					break;
				}
			}

			if (sb.length() == 0) {
				sb.append(
					getNotLayoutBoundJournalArticleUrl(
						portletRequest, getJournalArticle(document),
						assetPublisherPageURL));

			}
		}

		return sb.toString();
	}

	private List<Group> getSiteGroups() {
		return flammaGroupService
			.getFlammaGroup()
			.getDescendants(true);
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
	private ConfigurationHelper _configurationHelper;

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private LayoutLocalService layoutLocalService;
	@Reference
	private FlammaGroupService flammaGroupService;

	public static final String NAME = JournalArticle.class.getName();

}
