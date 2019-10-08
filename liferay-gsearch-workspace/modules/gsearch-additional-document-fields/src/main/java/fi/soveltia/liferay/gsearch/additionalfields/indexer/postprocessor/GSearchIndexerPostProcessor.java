
package fi.soveltia.liferay.gsearch.additionalfields.indexer.postprocessor;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.BaseIndexerPostProcessor;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.wiki.model.WikiPage;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.additionalfields.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.additionalfields.constants.FieldNames;

/**
 * Adds additional document fields for improving search relevancy.
 * 
 * See currently supported asset types in the component properties.
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

		if (!_moduleConfiguration.isEnabled()) {
			return;
		}

		_addNumericVersion(obj, document);

		_addContentLength(obj, document);

	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {

		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}

	/**
	 * Add content length field, for every available locale.
	 * 
	 * As this is a postprocessor, the content fields exist in 
	 * the incoming document already.
	 * 
	 * @param object
	 * @param document
	 */
	private void _addContentLength(Object object, Document document) {

		try {

			long groupId = Long.valueOf(document.get(Field.GROUP_ID));
			
			for (Locale locale : _language.getAvailableLocales(groupId)) {

				int length = 0;

				String contentFieldName = Field.CONTENT.concat(
						StringPool.UNDERLINE).concat(locale.toString());
				
				String lengthFieldName = FieldNames.CONTENT_LENGTH.concat(
						StringPool.UNDERLINE).concat(locale.toString());
				
				if (document.get(contentFieldName) != null) {
					length = document.get(contentFieldName).length();
				}

				if (length > 0) {
					
					if (_log.isDebugEnabled()) {
						
						StringBundler message = new StringBundler();
						message.append("Adding content length ");
						message.append(length);
						message.append(" for ");
						message.append(document.get(Field.ENTRY_CLASS_PK));
						message.append( "(");
						message.append(locale.toString());
						message.append(").");
						
						_log.debug(message.toString());
					}
					
					document.addNumber(lengthFieldName, length);
				}
			}

		}
		catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
		
	}
	
	/**
	 * Adds a numeric version field to document.
	 * 
	 * By default version numbers are stored string
	 * as keywords and cannot be used, for example
	 * for function score queries.
	 * 
	 * @param object
	 * @param document
	 */
	private void _addNumericVersion(Object object, Document document) {

		try {
			
			// Simple name: the object classname here will be the implementation class name.
			
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

				if (_log.isDebugEnabled()) {
					
					StringBundler message = new StringBundler();
					message.append("Adding version ");
					message.append(version);
					message.append(" for ");
					message.append(document.get(Field.ENTRY_CLASS_PK));
					
					_log.debug(message.toString());
				}
				document.addNumber(
					FieldNames.VERSION_COUNT, version);
			}

		}
		catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
	}
	
	private volatile ModuleConfiguration _moduleConfiguration;

	private static final Logger _log =
		LoggerFactory.getLogger(GSearchIndexerPostProcessor.class);

	@Reference
	Language _language;
	
}
