
package fi.soveltia.liferay.gsearch.web.search.internal.results.item;

import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.message.boards.kernel.model.MBMessage;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.wiki.model.WikiPage;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.web.search.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.web.search.results.item.ResultItemBuilderFactory;

/**
 * Single result builder factory implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ResultItemBuilderFactory.class
)
public class ResultItemBuilderFactoryImpl implements ResultItemBuilderFactory {

	/**
	 * {@inheritDoc}
	 */
	public ResultItemBuilder getResultBuilder(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Document document, String assetPublisherPageFriendlyURL) {

		String entryClassName = document.get(Field.ENTRY_CLASS_NAME);

		ResultItemBuilder resultItemBuilder;

		if (BlogsEntry.class.getName().equals(entryClassName)) {
			resultItemBuilder = new BlogsEntryItemBuilder();
		}
		else if (DLFileEntry.class.getName().equals(entryClassName)) {
			resultItemBuilder = new DLFileEntryItemBuilder();
		}
		else if (JournalArticle.class.getName().equals(entryClassName)) {
			resultItemBuilder = new JournalArticleItemBuilder();
		}
		else if (MBMessage.class.getName().equals(entryClassName)) {
			resultItemBuilder = new MBMessageItemBuilder();
		}
		else if (WikiPage.class.getName().equals(entryClassName)) {
			resultItemBuilder = new WikiPageItemBuilder();
		} else {
			throw new UnsupportedOperationException("Result item builder not implemented for " + entryClassName);
		}

		resultItemBuilder.setProperties(
			portletRequest, portletResponse, document,
			assetPublisherPageFriendlyURL);

		return resultItemBuilder;
	}
}
