package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;

public class BasicJournalArticleItemBuilder extends JournalArticleItemBuilder {

    @Override
    public boolean canBuild(Document document) {
        return NAME.equals(document.get(Field.ENTRY_CLASS_NAME)) && (document.get(Field.));
    }

    @Override
    public String getType() {
        return "web-content-basic";
    }

}
