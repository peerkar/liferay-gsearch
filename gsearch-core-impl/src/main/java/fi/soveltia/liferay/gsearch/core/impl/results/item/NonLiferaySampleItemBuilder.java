package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.portal.kernel.search.SearchException;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;

/**
 * Non Liferay result item result builder sample.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true,
	service = ResultItemBuilder.class
)
public class NonLiferaySampleItemBuilder extends BaseResultItemBuilder implements ResultItemBuilder {

	@Override
	public boolean canBuild(String name) {
		return NAME.equals(name);
	}

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
	
	private static final String DEFAULT_IMAGE = "/o/gsearch-web/images/asset-types/non-liferay-type.png";
	
	private static final String NAME = "non-liferay-type";

}
