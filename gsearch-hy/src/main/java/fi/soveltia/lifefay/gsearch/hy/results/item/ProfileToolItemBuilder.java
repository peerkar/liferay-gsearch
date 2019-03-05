package fi.soveltia.lifefay.gsearch.hy.results.item;

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import fi.helsinki.flamma.profile.tools.model.ProfileTool;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.core.impl.results.item.BaseResultItemBuilder;
import org.osgi.service.component.annotations.Component;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

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
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document, QueryContext queryContext) {
		return document.get(Field.URL);
	}

	@Override
	public String getTitle(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document, boolean isHighlight)
		throws NumberFormatException {
		return document.get(Field.TITLE);
	}

    @Override
    public String getDescription(PortletRequest portletRequest, PortletResponse portletResponse, Document document) {
        return document.get(portletRequest.getLocale(), Field.CONTENT);
    }

	private static final String NAME = ProfileTool.class.getName();


}