package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;

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
public class KBArticleItemBuilder extends BaseResultItemBuilder implements ResultItemBuilder {

	@Override
	public boolean canBuild(String name) {
		return NAME.equals(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLink()
		throws Exception {

		return getAssetRenderer().getURLViewInContext(
			(LiferayPortletRequest) _portletRequest,
			(LiferayPortletResponse) _portletResponse, null);
	}
	
	// Avoiding KB dependency in gradle.build
		
	private static final String NAME = "com.liferay.knowledge.base.model.KBArticle";
}
