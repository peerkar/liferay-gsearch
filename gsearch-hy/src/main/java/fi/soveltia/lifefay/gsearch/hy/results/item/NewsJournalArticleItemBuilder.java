/*
package fi.soveltia.lifefay.gsearch.hy.results.item;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.WebKeys;

import fi.helsinki.flamma.common.group.FlammaGroupService;
import fi.helsinki.flamma.common.url.ViewNewsURLService;
import fi.soveltia.lifefay.gsearch.hy.util.HYDDMUtil;
import fi.soveltia.lifefay.gsearch.hy.util.HYUtil;
import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.core.impl.results.item.JournalArticleItemBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component(
	immediate = true, 
	service = ResultItemBuilder.class
  	property = {
     "service.ranking:Integer=100"	
	}
)
public class NewsJournalArticleItemBuilder extends JournalArticleItemBuilder {

	@Override
	public boolean canBuild(Document document) {
		
		// FIXME. This shouldn't really be take from global configuration.
		
		String[] facetConfiguration = _configurationHelper.getFacetConfiguration();
		
		List<String> newsKeys = HYDDMUtil.getHYNewsDDMStructureKeys(facetConfiguration);
		
		return newsKeys.contains(document.get("ddmStructureKey"));
		
	}

	@Override
	public String getLink(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document, QueryContext queryContext)
		throws Exception {

		long entryClassPK = Long.valueOf(document.get(Field.ENTRY_CLASS_PK));

		try {
			JournalArticle article = getJournalArticle(document));
			return viewNewsUrlService.getSingleArticleUrl(
				portletRequest, article);
		}
		catch (PortalException e) {
			log.error(
				String.format(
					"Cannot get journal article for entryClassPK %s",
					entryClassPK));
		}
		return "";
	}

	@Override
	public String getDescription(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document)
		throws SearchException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		Locale locale = themeDisplay.getLocale();

		String languageId = locale.toString();

		for (Long ddmStructureId : DDM_STRUCTURE_IDS) {
			String ingress = document.get(
				String.format(
					"ddm__text__%s__ingress_%s", ddmStructureId, languageId));
			if ((ingress != null) && !ingress.isEmpty()) {
				return HtmlUtil.stripHtml(ingress);
			}
		}

		return super.getDescription(portletRequest, portletResponse, document);
	}

	private static final Log log =
		LogFactoryUtil.getLog(NewsJournalArticleItemBuilder.class);

	private List<String> DDM_STRUCTURE_KEYS = null;

	private List<Long> DDM_STRUCTURE_IDS = new ArrayList<>();

	@Reference
	private ConfigurationHelper _configurationHelper;

	@Reference
	private ViewNewsURLService viewNewsUrlService;

	@Reference
	private DDMStructureLocalService ddmStructureLocalService;

	@Reference
	private FlammaGroupService flammaGroupService;

	@Reference
	private ClassNameLocalService classNameLocalService;

}

*/
