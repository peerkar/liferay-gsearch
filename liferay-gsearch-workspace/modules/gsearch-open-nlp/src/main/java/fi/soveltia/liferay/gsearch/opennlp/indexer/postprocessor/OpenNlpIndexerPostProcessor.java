package fi.soveltia.liferay.gsearch.opennlp.indexer.postprocessor;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.BaseIndexerPostProcessor;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.util.StringBundler;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.opennlp.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.opennlp.constants.FieldNames;
import fi.soveltia.liferay.gsearch.opennlp.service.api.OpenNlpService;

/**
 * Adds additional document fields for improving search relevancy.
 * 
 * See currently supported asset types in the component properties.
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.opennlp.configuration.ModuleConfiguration",
	immediate = true,
	property = {
		"indexer.class.name=com.liferay.document.library.kernel.model.DLFileEntry",
		"indexer.class.name=com.liferay.journal.model.JournalArticle",
		"indexer.class.name=com.liferay.wiki.model.WikiPage"
	},
	service = IndexerPostProcessor.class
)
public class OpenNlpIndexerPostProcessor extends BaseIndexerPostProcessor {

	@Override
	public void postProcessDocument(Document document, Object obj)
		throws Exception {
		
		if (!_moduleConfiguration.isEnabled() || !_moduleConfiguration.isIndexerEnabled()) {
			return;
		}

		_addMetadata(obj, document);

	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {

		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}

	/**
	 * Add Open NLP metadata, for every available locale.
	 * 
	 * As this is a postprocessor, the content fields exist in 
	 * the incoming document already.
	 * 
	 * @param object
	 * @param document
	 */
	private void _addMetadata(Object object, Document document) {

		try {

			long groupId = Long.valueOf(document.get(Field.GROUP_ID));
			
			for (Locale locale : _language.getAvailableLocales(groupId)) {
				
				String text = null;

				String contentFieldName = Field.CONTENT.concat(
						StringPool.UNDERLINE).concat(locale.toString());
				
				if (document.get(contentFieldName) != null) {
					text = document.get(contentFieldName);
				}
				
				if (text.length() > 0) {
					
					JSONObject metadata = _openNlpService.extractData(text, true);
					
					if (metadata == null || metadata.getJSONObject("error") != null) {
						continue;
					}
					
					if (_log.isDebugEnabled()) {
						
						StringBundler message = new StringBundler();
						message.append("Extracting metadata ");
						message.append(" for ");
						message.append(document.get(Field.ENTRY_CLASS_PK));
						message.append( "(");
						message.append(locale.toString());
						message.append(").");
						
						_log.debug(metadata.toString());
					}
					
					JSONArray locations = _openNlpService.getMetadata(metadata, "locations");
					if (locations != null) {
						if (_log.isDebugEnabled()) {
							_log.debug("Adding locations: " + locations.toString());
						}
						
						String fieldName = FieldNames.LOCATIONS.concat(
								StringPool.UNDERLINE).concat(locale.toString());
						document.addText(fieldName, valuesToStringArray(locations));
					}
					
					JSONArray persons = _openNlpService.getMetadata(metadata, "persons");
					if (persons != null) {
						if (_log.isDebugEnabled()) {
							_log.debug("Adding persons: " + persons.toString());
						}
						
						String fieldName = FieldNames.PERSONS.concat(
								StringPool.UNDERLINE).concat(locale.toString());

						document.addText(fieldName, valuesToStringArray(locations));
					}
					
					JSONArray dates = _openNlpService.getMetadata(metadata, "dates");
					if (dates != null) {
						if (_log.isDebugEnabled()) {
							_log.debug("Adding dates: " + dates.toString());
						}

						String fieldName = FieldNames.DATES.concat(
								StringPool.UNDERLINE).concat(locale.toString());
						
						document.addText(fieldName, valuesToStringArray(dates));
					}
				}
			}

		}
		catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
		
	}
	
	private String[] valuesToStringArray(JSONArray array) {
		return new String[0];
	}

	private volatile ModuleConfiguration _moduleConfiguration;

	private static final Logger _log =
		LoggerFactory.getLogger(OpenNlpIndexerPostProcessor.class);

	@Reference
	Language _language;

	@Reference
	private OpenNlpService _openNlpService;

}
