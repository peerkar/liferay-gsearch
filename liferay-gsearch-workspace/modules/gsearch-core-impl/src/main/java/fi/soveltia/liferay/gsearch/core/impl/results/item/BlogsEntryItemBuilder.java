
package fi.soveltia.liferay.gsearch.core.impl.results.item;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryService;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.document.Document;

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
public class BlogsEntryItemBuilder
	extends BaseResultItemBuilder implements ResultItemBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canBuild(Document document) {
		return _NAME.equals(document.getString(Field.ENTRY_CLASS_NAME));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getThumbnail(QueryContext queryContext, Document document)
		throws Exception {

		long entryClassPK = Long.valueOf(document.getLong(Field.ENTRY_CLASS_PK));

		BlogsEntry blogsEntry = _blogsEntryService.getEntry(entryClassPK);

		return blogsEntry.getSmallImageURL();
	}

	private static final String _NAME = BlogsEntry.class.getName();

	@Reference
	private BlogsEntryService _blogsEntryService;

}