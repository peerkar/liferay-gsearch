package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.SAXReader;
import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import java.util.List;

@Component(
    immediate = true,
    service = ResultItemBuilder.class
)
public class BasicJournalArticleItemBuilder extends JournalArticleItemBuilder implements ResultItemBuilder{

    private static final Log log = LogFactoryUtil.getLog(BasicJournalArticleItemBuilder.class);

    private List<String> DDM_STRUCTURE_KEYS;


    @Reference
    private ConfigurationHelper _configurationHelperService;

    @Activate
    protected void activate() {
        DDM_STRUCTURE_KEYS = _configurationHelperService.getDDMStructureKeys(getType());
    }

    @Override
    public boolean canBuild(Document document) {
        return NAME.equals(document.get(Field.ENTRY_CLASS_NAME)) && DDM_STRUCTURE_KEYS.contains(document.get("ddmStructureKey"));
    }

//    @Override
//    public String getTitle()
//        throws NumberFormatException, PortalException {
//
//        String title = getHeadline();
//        if ((title == null) || title.isEmpty()) {
//            try {
//                title = getLayoutTitle();
//            } catch (Exception e) {
//                log.error(String.format("Cannot get layout title for journalArticle %s", getJournalArticle().getArticleId()), e);
//                return super.getTitle();
//            }
//        }
//        return HtmlUtil.stripHtml(title);
//    }
//
//    private String getHeadline() throws PortalException {
//        String headline = null;
//        try {
//            com.liferay.portal.kernel.xml.Document docXml = saxReader.read(getJournalArticle().getContentByLocale(_locale.getLanguage()));
//            headline = docXml.valueOf("//dynamic-element[@name='headline']/dynamic-content/text()");
//        } catch (DocumentException e) {
//            log.error(String.format("Cannot read content xml of journalArticle %s", getJournalArticle().getArticleId()));
//        }
//
//        return headline;
//    }
//
//    private String getLayoutTitle() throws Exception {
//
//        long groupId = getJournalArticle().getGroupId();
//
//        Layout layout = getJournalArticleLayout(groupId);
//        if (layout != null) {
//            return layout.getName(_locale);
//        }
//
//        return null;
//    }

    @Override
    public String getType() {
        return "content";
    }

}
