
package fi.soveltia.liferay.gsearch.core.impl.configuration;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * Filter configuration helper / facade.
 *
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = FilterConfigurationHelper.CONFIGURATION_PID,
	immediate = true, 
	service = FilterConfigurationHelper.class
)
public class FilterConfigurationHelper {

	public static final String CONFIGURATION_PID =
		"fi.soveltia.liferay.gsearch.core.impl.configuration.FilterConfiguration";

	public String[] getFilters() {
		return _configuration.filters();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_configuration = ConfigurableUtil.createConfigurable(
			FilterConfiguration.class, properties);
	}

	private volatile FilterConfiguration _configuration;

}