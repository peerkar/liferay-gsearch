
package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.blogs.kernel.service.BlogsEntryService;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
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
public class BlogsEntryItemBuilder extends BaseResultItemBuilder
	implements ResultItemBuilder {

	@Override
	public boolean canBuild(Document document) {

		return NAME.equals(document.get(Field.ENTRY_CLASS_NAME));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getThumbnail(QueryContext queryContext, Document document)
		throws Exception {

		long entryClassPK = Long.valueOf(document.get(Field.ENTRY_CLASS_PK));

		BlogsEntry blogsEntry = _blogsEntryService.getEntry(entryClassPK);

		return blogsEntry.getSmallImageURL();
	}

	@Reference
	private BlogsEntryService _blogsEntryService;

	private static final String NAME = BlogsEntry.class.getName();

}
