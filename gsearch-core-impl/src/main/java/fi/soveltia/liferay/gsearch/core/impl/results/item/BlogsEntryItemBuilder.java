
package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.blogs.kernel.service.BlogsEntryService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;

/**
 * Blogs entry result item builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true,
	service = ResultItemBuilder.class
)
public class BlogsEntryItemBuilder extends BaseResultItemBuilder implements ResultItemBuilder {

	@Override
	public boolean canBuild(String name) {
		return NAME.equals(name);
	}

	/**
	 * {@inheritDoc}
	 * @throws Exception 
	 */
	@Override
	public String getImageSrc() throws Exception {
		
		BlogsEntry blogsEntry =  _blogsEntryService.getEntry(_entryClassPK);

		return blogsEntry.getSmallImageURL();
	}
	
	@Reference(unbind = "-")
	protected void setBlogsEntryService(
		BlogsEntryService blogsEntryService) {

		_blogsEntryService = blogsEntryService;
	}

	private static BlogsEntryService _blogsEntryService;
	
	private static final String NAME = BlogsEntry.class.getName();
}
