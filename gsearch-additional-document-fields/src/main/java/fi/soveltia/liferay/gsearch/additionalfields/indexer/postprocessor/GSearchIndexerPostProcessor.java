
package fi.soveltia.liferay.gsearch.additionalfields.indexer.postprocessor;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexerPostProcessor;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.wiki.model.WikiPage;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import fi.soveltia.liferay.gsearch.additionalfields.configuration.ModuleConfiguration;

/**
 * Adds additional document fields for improving relevancy.
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.additionalfields.configuration.ModuleConfiguration",
	immediate = true,
	property = {
		"indexer.class.name=com.liferay.document.library.kernel.model.DLFileEntry",
		"indexer.class.name=com.liferay.journal.model.JournalArticle",
		"indexer.class.name=com.liferay.wiki.model.WikiPage"
	},
	service = IndexerPostProcessor.class
)
public class GSearchIndexerPostProcessor extends BaseIndexerPostProcessor {

	@Override
	public void postProcessDocument(Document document, Object obj)
		throws Exception {

		if (!_moduleConfiguration.enableFeature()) {
			return;
		}

		addVersionCount(obj, document);

	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {

		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}

	/**
	 * Adds numeric version to document. By default version numbers are stored
	 * as keywords and cannot be used by function score queries.
	 * 
	 * @param object
	 * @param document
	 */
	protected void addVersionCount(Object object, Document document) {

		try {

			String className = object.getClass().getSimpleName();

			Double version = null;

			if (className.startsWith(DLFileEntry.class.getSimpleName())) {
				version = (Double.valueOf(((DLFileEntry) object).getVersion()));
			}
			else if (className.startsWith(
				JournalArticle.class.getSimpleName())) {
				version = ((JournalArticle) object).getVersion();
			}
			else if (className.startsWith(WikiPage.class.getSimpleName())) {
				version = ((WikiPage) object).getVersion();
			}

			if (version != null) {

				document.addNumber(
					_moduleConfiguration.versionField(), version);
			}

		}
		catch (Exception e) {
			_log.error(e, e);
		}
	}

	private volatile ModuleConfiguration _moduleConfiguration;

	private static final Log _log =
		LogFactoryUtil.getLog(GSearchIndexerPostProcessor.class);

}
