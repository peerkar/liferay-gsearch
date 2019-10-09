package fi.soveltia.liferay.gsearch.core.impl.configuration;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * Index configuration helper / facade.
 *
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = IndexConfigurationHelper.CONFIGURATION_PID,
	immediate = true, 
	service = IndexConfigurationHelper.class
)
public class IndexConfigurationHelper {

	public static final String CONFIGURATION_PID =
		"fi.soveltia.liferay.gsearch.core.impl.configuration.IndexConfigurationHelper";

	public String getKeywordSuggesterIndex() {
		return _configuration.keywordSuggesterIndex();
	}

	public String[] getSearchIndexes() {
		return _configuration.searchIndexes();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_configuration = ConfigurableUtil.createConfigurable(
				IndexConfiguration.class, properties);
	}

	private volatile IndexConfiguration _configuration;

}