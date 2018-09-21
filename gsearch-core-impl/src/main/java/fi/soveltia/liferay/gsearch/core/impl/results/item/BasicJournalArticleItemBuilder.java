package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
    immediate = true,
    service = ResultItemBuilder.class
)
public class BasicJournalArticleItemBuilder extends JournalArticleItemBuilder implements ResultItemBuilder{

    private static final Log log = LogFactoryUtil.getLog(BasicJournalArticleItemBuilder.class);

    private String DDM_STRUCTURE_KEY = "";

    @Reference
    private ConfigurationHelper _configurationHelperService;

//    @Activate
//    protected void activate() {
//        try {
//            DDM_STRUCTURE_KEY = _configurationHelperService.getDDMStructureMapping().getJSONObject("content").getString("ddmStructureKey");
    // todo get from module type conf
//        } catch (JSONException e) {
//            log.error("Cannot get DDM structure key mapping for basic web content");
//        }
//    }

    @Override
    public boolean canBuild(Document document) {
        return NAME.equals(document.get(Field.ENTRY_CLASS_NAME)) && DDM_STRUCTURE_KEY.equals(document.get("ddmStructureKey"));
    }

    @Override
    public String getType() {
        return "basic";
    }

}
