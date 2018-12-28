package fi.soveltia.liferay.gsearch.core.impl.results.item;

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
import com.liferay.portal.kernel.util.HtmlUtil;
import fi.helsinki.flamma.common.group.FlammaGroupService;
import fi.helsinki.flamma.common.url.ViewNewsURLService;
import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
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
)
public class NewsJournalArticleItemBuilder extends JournalArticleItemBuilder {

    private static final Log log = LogFactoryUtil.getLog(NewsJournalArticleItemBuilder.class);

    private List<String> DDM_STRUCTURE_KEYS = null;
    private List<Long> DDM_STRUCTURE_IDS = new ArrayList<>();

    @Reference
    private ConfigurationHelper _configurationHelperService;

    @Reference
    private ViewNewsURLService viewNewsUrlService;

    @Reference
    private DDMStructureLocalService ddmStructureLocalService;

    @Reference
    private FlammaGroupService flammaGroupService;

    @Reference
    private ClassNameLocalService classNameLocalService;

    @Override
    public boolean canBuild(Document document) {
        if (DDM_STRUCTURE_KEYS == null) {
            DDM_STRUCTURE_KEYS = _configurationHelperService.getDDMStructureKeys(getType());
            long classNameId = classNameLocalService.getClassName(JournalArticle.class.getName()).getClassNameId();
            DDM_STRUCTURE_IDS = DDM_STRUCTURE_KEYS
                .stream()
                .map(key -> {
                    try {
                        return ddmStructureLocalService.getStructure(flammaGroupService.getFlammaGroupId(), classNameId, key).getStructureId();
                    } catch (PortalException e) {
                        log.error(String.format("Cannot get DDM structure for classNameId %s with key %s", classNameId, key));
                    }
                    return null;
                })
                .collect(Collectors.toList());
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
    public String getLink(PortletRequest portletRequest, PortletResponse portletResponse, Document document, String assetPublisherPageFriendlyURL, long entryClassPK) {
        try {
            JournalArticle article = getJournalArticle(entryClassPK);
            return viewNewsUrlService.getSingleArticleUrl(portletRequest, article);
        } catch (PortalException e) {
            log.error(String.format("Cannot get journal article for entryClassPK %s", entryClassPK));
        }
        return "";
    }

    @Override
    public String getDescription(PortletRequest portletRequest, PortletResponse portletResponse, Document document, Locale locale) throws SearchException {

        String languageId = locale.toString();

        for (Long ddmStructureId : DDM_STRUCTURE_IDS) {
            String ingress = document.get(String.format("ddm__text__%s__ingress_%s", ddmStructureId, languageId));
            if ((ingress != null) && !ingress.isEmpty()) {
                return HtmlUtil.stripHtml(ingress);
            }
        }

        return super.getDescription(portletRequest, portletResponse, document, locale);
    }

}
