package fi.soveltia.lifefay.gsearch.hy.results.item;

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import fi.helsinki.flamma.profile.tools.model.ProfileTool;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.core.impl.results.item.BaseResultItemBuilder;
import org.osgi.service.component.annotations.Component;

@Component(
	immediate = true,
	service = ResultItemBuilder.class
)
public class ProfileToolItemBuilder extends BaseResultItemBuilder
	implements ResultItemBuilder {

	@Override
	public boolean canBuild(Document document) {

		return NAME.equals(document.get(Field.ENTRY_CLASS_NAME));
	}

	@Override
	public String getLink(
		QueryContext queryContext, Document document) {
		return document.get(queryContext.getLocale(), Field.URL);
	}

	@Override
	public String getTitle(QueryContext queryContext,
		Document document, boolean isHighlight)
		throws NumberFormatException {
		return document.get(queryContext.getLocale(), Field.TITLE);
	}

    @Override
    public String getDescription(QueryContext queryContext, Document document) {
        return document.get(queryContext.getLocale(), Field.CONTENT);
    }

	private static final String NAME = ProfileTool.class.getName();


}