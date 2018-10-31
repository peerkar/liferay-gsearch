
package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;

/**
 * KBArticle result item builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true,
	service = ResultItemBuilder.class
)
public class KBArticleItemBuilder extends BaseResultItemBuilder
	implements ResultItemBuilder {

	@Override
	public boolean canBuild(Document document) {

		return NAME.equals(document.get(Field.ENTRY_CLASS_NAME));
	}

	@Override
	public String getLink(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document, String assetPublisherPageFriendlyURL)
		throws Exception {

		return getAssetRenderer(document).getURLViewInContext(
			(LiferayPortletRequest) portletRequest,
			(LiferayPortletResponse) portletResponse, null);
	}

	// Avoiding KB dependency in gradle.build

	private static final String NAME =
		"com.liferay.knowledge.base.model.KBArticle";
}
