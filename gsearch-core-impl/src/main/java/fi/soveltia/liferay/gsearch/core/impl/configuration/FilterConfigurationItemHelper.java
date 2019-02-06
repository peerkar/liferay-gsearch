
package fi.soveltia.liferay.gsearch.core.impl.configuration;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationItemHelper;

/**
 * Prefilter configuration helper
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = FilterConfigurationItemHelper.CONFIGURATION_PID, 
	immediate = true,
	property = "config.item=filter",
	service = ConfigurationItemHelper.class
)
public class FilterConfigurationItemHelper implements ConfigurationItemHelper {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getConfiguration() {

		return _configuration.filters();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {

		_configuration = ConfigurableUtil.createConfigurable(
			FilterConfiguration.class, properties);
	}

	private volatile FilterConfiguration _configuration;

	public static final String CONFIGURATION_PID =
		"fi.soveltia.liferay.gsearch.core.impl.configuration.FilterConfiguration";
	
}
