package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.util.HtmlUtil;
import fi.helsinki.flamma.news.util.NewsURLService;
import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import java.util.List;
import java.util.Locale;

@Component(
    immediate = true,
    service = ResultItemBuilder.class
)
public class NewsJournalArticleItemBuilder extends JournalArticleItemBuilder {

    private static final Log log = LogFactoryUtil.getLog(NewsJournalArticleItemBuilder.class);

    private List<String> DDM_STRUCTURE_KEYS = null;

    @Reference
    private ConfigurationHelper _configurationHelperService;

    @Reference
    private NewsURLService newsUrlService;

    @Override
    public boolean canBuild(Document document) {
        if (DDM_STRUCTURE_KEYS == null) {
            DDM_STRUCTURE_KEYS = _configurationHelperService.getDDMStructureKeys(getType());
        }
        if (log.isDebugEnabled()) {
            String configuredDDMStructureKeys = String.join(",", DDM_STRUCTURE_KEYS);
            log.debug(String.format("Document ddmStructureKey '%s', entryClassName '%s', NAME '%s', ddmStructureKeys '%s'",
                document.get("ddmStructureKey"), document.get(Field.ENTRY_CLASS_NAME), NAME, configuredDDMStructureKeys));
        }
        return NAME.equals(document.get(Field.ENTRY_CLASS_NAME)) && DDM_STRUCTURE_KEYS.contains(document.get("ddmStructureKey"));
    }

    @Override
    public String getImageSrc(PortletRequest portletRequest, long entryClassPK) {
        return "icon-news";
    }

    @Override
    public String getType() {
        return "news";
    }

    @Override
    public String getDescription(PortletRequest portletRequest, PortletResponse portletResponse, Document document, Locale locale)
        throws SearchException {

        return HtmlUtil.stripHtml(getSummary(portletRequest, portletResponse, document).getContent());
    }

    @Override
    public String getLink(PortletRequest portletRequest, PortletResponse portletResponse, Document document, String assetPublisherPageFriendlyURL, long entryClassPK) {
        try {
            JournalArticle article = getJournalArticle(entryClassPK);
            return newsUrlService.getSingleArticleUrl(portletRequest, article);
        } catch (PortalException e) {
            log.error(String.format("Cannot get journal article for entryClassPK %s", entryClassPK));
        }
        return "";
    }
}
