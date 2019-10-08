
package fi.soveltia.liferay.gsearch.core.impl.configuration;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationItemHelper;

/**
 * Facet configuration helper
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = ClauseConfigurationItemHelper.CONFIGURATION_PID, 
	immediate = true,
	property = "config.item=clause",
	service = ConfigurationItemHelper.class
)
public class ClauseConfigurationItemHelper implements ConfigurationItemHelper {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getConfiguration() {

		return _configuration.clauses();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {

		_configuration = ConfigurableUtil.createConfigurable(
			ClauseConfiguration.class, properties);
	}

	private volatile ClauseConfiguration _configuration;

	public static final String CONFIGURATION_PID =
			"fi.soveltia.liferay.gsearch.core.impl.configuration.ClauseConfiguration";
	
}
