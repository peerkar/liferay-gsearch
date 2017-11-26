package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.portal.kernel.search.SearchException;

import org.osgi.service.component.annotations.Component;

/**
 * Non Liferay result item result builder sample.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true
)
public class NonLiferaySampleItemBuilder extends BaseResultItemBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription()
		throws SearchException {

		return _document.get("description");
	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getImageSrc()
		throws Exception {
		
		return DEFAULT_IMAGE;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLink()
		throws Exception {
		
		return _document.get("treePath");
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getTitle() {
		return _document.get("title");
	}
	
	public static final String DEFAULT_IMAGE = "/o/gsearch-web/images/asset-types/non-liferay-type.png";
}
