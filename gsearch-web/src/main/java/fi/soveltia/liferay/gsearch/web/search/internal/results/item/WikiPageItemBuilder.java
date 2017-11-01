
package fi.soveltia.liferay.gsearch.web.search.internal.results.item;

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
	
	public static final String DEFAULT_IMAGE = "/images/wiki.png";

}
