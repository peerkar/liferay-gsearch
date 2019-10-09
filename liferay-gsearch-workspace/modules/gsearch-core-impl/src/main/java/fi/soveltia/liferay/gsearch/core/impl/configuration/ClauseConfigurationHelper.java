
package fi.soveltia.liferay.gsearch.core.impl.configuration;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * Clause configuration helper / facade.
 *
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = ClauseConfigurationHelper.CONFIGURATION_PID,
	immediate = true, 
	service = ClauseConfigurationHelper.class
)
public class ClauseConfigurationHelper {

	public static final String CONFIGURATION_PID =
		"fi.soveltia.liferay.gsearch.core.impl.configuration.ClauseConfiguration";

	public String[] getClauses() {
		return _configuration.clauses();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_configuration = ConfigurableUtil.createConfigurable(
			ClauseConfiguration.class, properties);
	}

	private volatile ClauseConfiguration _configuration;

}