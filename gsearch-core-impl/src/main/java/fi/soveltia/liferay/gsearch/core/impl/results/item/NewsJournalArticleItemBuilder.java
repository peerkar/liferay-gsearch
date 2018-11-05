package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.PortletRequest;
import java.util.List;

@Component(
    immediate = true,
    service = ResultItemBuilder.class
)
public class NewsJournalArticleItemBuilder extends JournalArticleItemBuilder {

    private static final Log log = LogFactoryUtil.getLog(NewsJournalArticleItemBuilder.class);

    private List<String> DDM_STRUCTURE_KEYS = null;

    @Reference
    private ConfigurationHelper _configurationHelperService;

    @Override
    public boolean canBuild(Document document) {
        if (DDM_STRUCTURE_KEYS == null) {
            DDM_STRUCTURE_KEYS = _configurationHelperService.getDDMStructureKeys(getType());
        }
        return NAME.equals(document.get(Field.ENTRY_CLASS_NAME)) && DDM_STRUCTURE_KEYS.contains(document.get("ddmStructureKey"));
    }

    @Override
    public String getImageSrc(PortletRequest portletRequest, long entryClassPK) {
        return "icon-file-text";
    } // TODO add correct icon

    @Override
    public String getType() {
        return "news";
    }

}
