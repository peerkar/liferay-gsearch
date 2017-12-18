
package fi.soveltia.liferay.gsearch.core.impl.results.item;

import org.osgi.service.component.annotations.Component;

/**
 * Wiki page result item builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true
)
public class WikiPageItemBuilder extends BaseResultItemBuilder {

	/**
	 * {@inheritDoc}
	 * @throws Exception 
	 */
	@Override
	public String getImageSrc() throws Exception {
		
		// return _portletRequest.getContextPath() + DEFAULT_IMAGE;

		return null;
	}
	
	public static final String DEFAULT_IMAGE = "/o/gsearch-web/images/asset-types/wiki.png";
}
