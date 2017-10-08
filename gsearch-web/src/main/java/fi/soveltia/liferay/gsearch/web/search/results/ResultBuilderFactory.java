
package fi.soveltia.liferay.gsearch.web.search.results;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

/**
 * Result Builder Factory
 * 
 * @author Petteri Karttunen
 */
public class ResultBuilderFactory {

	public static ResultBuilder getResultBuilder(
		PortletRequest portletRequest,
		PortletResponse portletResponse, Document document, String assetPublisherPageFriendlyURL) {

		String entryClassName = document.get(Field.ENTRY_CLASS_NAME);
		
		ResultBuilder resultBuilder;

		if (JournalArticle.class.getName().equals(entryClassName)) {
			resultBuilder = new JournalArticleResultBuilder();
		}
		else if (DLFileEntry.class.getName().equals(entryClassName)) {
			resultBuilder = new DLFileEntryResultBuilder();

		}
		else {
			resultBuilder = new GenericResultBuilder();
		}

		resultBuilder.setProperties(portletRequest,
			portletResponse, document, assetPublisherPageFriendlyURL);
		
		return resultBuilder;
	}
}
