package fi.soveltia.liferay.gsearch.highlightresultitembytag;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;

import java.util.Arrays;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import fi.soveltia.liferay.gsearch.core.api.results.item.processor.ResultItemProcessor;
import fi.soveltia.liferay.gsearch.highlightresultitembytag.configuration.ModuleConfiguration;

/**
 * Results item tag highlighter implementation.
 * 
 * @author Petteri Karttunen
 *
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.highlightresultitembytag.GSearchHighlightResultItemByTag", 
	immediate = true, 
	service = ResultItemProcessor.class
)
public class HighlightResultItemProcessor implements ResultItemProcessor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {

		return _moduleConfiguration.enableTagHighlighter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process(Document document, JSONObject resultItem) throws Exception {

		if (!isEnabled()) {
			return;
		}
		
		String[]tags = getTags(document);
		
		if (tags != null) {
			
			String highlightTag = _moduleConfiguration.tagName();
			
			if (Arrays.stream(tags).anyMatch(highlightTag::equals)) {
				resultItem.put("highlight", true);
			}
		}
	}
	
	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}		

	protected String[] getTags(Document document) {

		String[] tags = document.getValues(Field.ASSET_TAG_NAMES);

		return tags;
	}
	
	private volatile ModuleConfiguration _moduleConfiguration;

}
