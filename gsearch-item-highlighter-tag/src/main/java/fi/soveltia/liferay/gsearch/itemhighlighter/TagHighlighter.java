package fi.soveltia.liferay.gsearch.itemhighlighter;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;

import java.util.Arrays;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemHighlighter;
import fi.soveltia.liferay.gsearch.itemhighlighter.configuration.GSearchTagHighlighterConfiguration;

/**
 * Results item tag highlighter implementation.
 * 
 * @author Petteri Karttunen
 *
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.itemhighlighter.tag.GSearchTagHighlighterConfiguration", 
	immediate = true, 
	service = ResultItemHighlighter.class
)
public class TagHighlighter implements ResultItemHighlighter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {

		return _gSearchTagHighlighterConfiguration.enableTagHighlighter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHighlightedItem(Document document) {

		String[]tags = getTags(document);
		
		if (tags != null) {
			
			String highlightTag = _gSearchTagHighlighterConfiguration.tagName();
			
			if (Arrays.stream(tags).anyMatch(highlightTag::equals)) {
				return true;
			}
		}
		return false;
	}
	
	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_gSearchTagHighlighterConfiguration = ConfigurableUtil.createConfigurable(
			GSearchTagHighlighterConfiguration.class, properties);
	}		

	protected String[] getTags(Document document) {

		String[] tags = document.getValues(Field.ASSET_TAG_NAMES);

		return tags;
	}
	
	private volatile GSearchTagHighlighterConfiguration _gSearchTagHighlighterConfiguration;

}
