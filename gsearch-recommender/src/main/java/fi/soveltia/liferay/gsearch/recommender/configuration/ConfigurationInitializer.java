
package fi.soveltia.liferay.gsearch.recommender.configuration;

import com.liferay.portal.kernel.util.StringBundler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.felix.cm.file.ConfigurationHandler;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sets default configurations.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true
)
public class ConfigurationInitializer {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {

		checkConfiguration(CONFIGURATION_PID);
	}

	/**
	 * Check configuration.
	 * 
	 * @return
	 */
	protected void checkConfiguration(String pid) {

		_log.info("Checking configuration " + pid);

		try {

			Configuration configuration =
				_configurationAdmin.getConfiguration(pid);

			if (configuration.getProperties() == null) {
				setDefaultConfiguration(configuration, pid);
			}
			else {
				_log.info("Configuration exists.");

			}
		}
		catch (IOException e) {
			_log.error(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private void setDefaultConfiguration(
		Configuration configuration, String fileName) {

		_log.info(
			"Setting default configuration for: " + configuration.getPid());

		InputStream inputStream = null;

		try {

			StringBundler sb = new StringBundler();
			sb.append("configs/").append(fileName).append(".config");

			inputStream = this.getClass().getClassLoader().getResourceAsStream(
				sb.toString());

			configuration.update(ConfigurationHandler.read(inputStream));

			_log.info("Default configuration set.");

		}
		catch (Exception e) {

			_log.error(e.getMessage(), e);
		}
		finally {

			if (inputStream != null) {
				try {
					inputStream.close();
				}
				catch (IOException e) {
					_log.error(e.getMessage(), e);
				}
			}
		}
	}

	private static final String CONFIGURATION_PID =
		"fi.soveltia.liferay.gsearch.recommender.configuration.ModuleConfiguration";

	private static final Logger _log =
		LoggerFactory.getLogger(ConfigurationInitializer.class);

	@Reference
	private ConfigurationAdmin _configurationAdmin;
}
