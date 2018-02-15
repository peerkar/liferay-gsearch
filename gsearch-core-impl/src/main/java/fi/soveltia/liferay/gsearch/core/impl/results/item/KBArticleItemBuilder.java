package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * KBArticle result item builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true
)
public class KBArticleItemBuilder extends BaseResultItemBuilder {

	@Override
	public String getImageSrc()
		throws Exception {
		
		// No small image.
		
		return null;
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
}
