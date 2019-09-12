package fi.soveltia.lifefay.gsearch.hy.results.item;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchException;
import fi.helsinki.flamma.common.url.ExpertSearchProfileURLService;
import fi.helsinki.flamma.expert.search.model.model.ExpertSearchContact;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.core.impl.results.item.BaseResultItemBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

@Component(
	immediate = true,
	service = ResultItemBuilder.class
)
public class ExpertSearchContactItemBuilder extends BaseResultItemBuilder
	implements ResultItemBuilder {

	@Override
	public boolean canBuild(Document document) {

		return NAME.equals(document.get(Field.ENTRY_CLASS_NAME));
	}

	@Override
	public String getLink(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document, QueryContext queryContext) {

	    try {
            long userId = Long.valueOf(document.get(Field.USER_ID));
            return expertSearchProfileURLService.getProfileUrl(portletRequest, userId);
        } catch (NumberFormatException e) {
	        return "";
        }
	}

	@Override
	public String getTitle(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document, boolean isHighlight)
		throws NumberFormatException {
		return document.get(Field.USER_NAME);
	}

    @Override
    public String getDescription(PortletRequest portletRequest, PortletResponse portletResponse, Document document) throws SearchException {
        return document.get(portletRequest.getLocale(), Field.DESCRIPTION);
    }

	@Reference
    private ExpertSearchProfileURLService expertSearchProfileURLService;

	private static final String NAME = ExpertSearchContact.class.getName();


}