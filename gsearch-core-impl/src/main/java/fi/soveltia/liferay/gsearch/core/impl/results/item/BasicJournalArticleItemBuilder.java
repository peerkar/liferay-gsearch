package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.StringPool;
import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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

    @Override
    public String getDescription() throws SearchException {
        String languageId = _locale.toString();

        Indexer<?> indexer =
            getIndexer(_document.get(Field.ENTRY_CLASS_NAME));

        if (indexer != null) {
            String snippet = _document.get(Field.SNIPPET + StringPool.UNDERLINE + Field.CONTENT + StringPool.UNDERLINE + languageId);

            if ((snippet != null) && !snippet.isEmpty()) {
                return HtmlUtil.stripHtml(snippet);
            }
        }
        return super.getDescription();

    }

    @Override
    public String getType() {
        return "content";
    }

}
