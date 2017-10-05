
package fi.soveltia.liferay.gsearch.web.search.results;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

/**
 * Result Builder Factory
 * 
 * @author Petteri Karttunen
 */
public class ResultBuilderFactory {

	public static ResultBuilder getResultBuilder(
		ResourceRequest resourceRequest,
		ResourceResponse resourceResponse, Document document, String assetPublisherPageFriendlyURL) {

		String entryClassName = document.get(Field.ENTRY_CLASS_NAME);

		if (JournalArticle.class.getName().equals(entryClassName)) {

			return new JournalArticleResultBuilder(
				resourceRequest, resourceResponse, document, assetPublisherPageFriendlyURL);

		}
		else if (DLFileEntry.class.getName().equals(entryClassName)) {

			return new DLFileEntryResultBuilder(
				resourceRequest, resourceResponse, document);

		}
		else {

			return new BaseResultBuilder(
				resourceRequest, resourceResponse, document);
		}
	}
}
