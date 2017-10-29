package fi.soveltia.liferay.gsearch.web.search.internal.query.keyword;

import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.message.boards.kernel.model.MBMessage;
import com.liferay.wiki.model.WikiPage;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.web.search.internal.queryparams.QueryParams;
import fi.soveltia.liferay.gsearch.web.search.query.keyword.KeywordQueryBuilder;
import fi.soveltia.liferay.gsearch.web.search.query.keyword.KeywordQueryBuilderFactory;

/**
 * Keyword query builder factory implementation.
 * 
 * @author Petteri Karttunen
 *
 */
@Component(
	immediate = true, 
	service = KeywordQueryBuilderFactory.class
)
public class KeywordQueryBuilderFactoryImpl implements KeywordQueryBuilderFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public KeywordQueryBuilder getKeywordQueryBuilder(String className, 
		QueryParams queryParams) throws UnsupportedOperationException {

		KeywordQueryBuilder keywordQueryBuilder;

		if (BlogsEntry.class.getName().equals(className)) {
			keywordQueryBuilder = new BlogsEntryKeywordQueryBuilder();
		}
		else if (DLFileEntry.class.getName().equals(className)) {
			keywordQueryBuilder = new DLFileEntryKeywordQueryBuilder();
		}
		else if (JournalArticle.class.getName().equals(className)) {
			keywordQueryBuilder = new JournalArticleKeywordQueryBuilder();
		}
		else if (MBMessage.class.getName().equals(className)) {
			keywordQueryBuilder = new MBMessageKeywordQueryBuilder();
		}
		else if (WikiPage.class.getName().equals(className)) {
			keywordQueryBuilder = new WikiPageKeywordQueryBuilder();
		} else {
			throw new UnsupportedOperationException("Keyword query builder not implemented for " + className);
		}

		keywordQueryBuilder.setQueryParams(queryParams);

		return keywordQueryBuilder;
	}
}
