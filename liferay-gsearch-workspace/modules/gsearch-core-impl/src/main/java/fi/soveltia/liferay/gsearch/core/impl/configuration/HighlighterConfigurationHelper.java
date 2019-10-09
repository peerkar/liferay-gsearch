package fi.soveltia.liferay.gsearch.core.impl.configuration;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * Hihglighter configuration helper / facade.
 *
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = FilterConfigurationHelper.CONFIGURATION_PID,
	immediate = true, 
	service = HighlighterConfigurationHelper.class
)
public class HighlighterConfigurationHelper {

	public static final String CONFIGURATION_PID =
		"fi.soveltia.liferay.gsearch.core.impl.configuration.HighlighterConfiguration";

	public int getDescriptionMaxLength() {
		return _configuration.descriptionMaxLength();
	}

	public int getHighlightFragmentSize() {
		return _configuration.highlightFragmentSize();
	}

	public int getHighlightSnippetSize() {
		return _configuration.highlightSnippetSize();
	}	
	
	public boolean isHighlightEnabled() {
		return _configuration.highlightEnabled();
	}
	
	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_configuration = ConfigurableUtil.createConfigurable(
				HighlighterConfiguration.class, properties);
	}

	private volatile HighlighterConfiguration _configuration;

}