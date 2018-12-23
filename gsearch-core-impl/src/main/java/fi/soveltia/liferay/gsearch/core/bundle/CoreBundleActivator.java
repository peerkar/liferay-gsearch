package fi.soveltia.liferay.gsearch.core.bundle;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Reference;

/**
 * Core bundle activator. This sets the default config.
 * 
 * @author Petteri Karttunen
 */
public class CoreBundleActivator implements BundleActivator {

	@Override
	public void start(BundleContext bundleContext)
		throws Exception {
/*
		Configuration clauseConfiguration = _configurationAdmin.getConfiguration(
						CLAUSE_CONFIGURATION_PID);
		
		String[] clauses =  (String[])clauseConfiguration.getProperties().get("clauses");
		
		if (clauses == null || clauses.length == 0) {
			
			Dictionary<String, Object> dictionary = getDefaultConfiguration(CLAUSE_CONFIGURATION_FILE);
			//clauseConfiguration.update(dictionary);
		}
*/
	}

	@Override
	public void stop(BundleContext bundleContext)
		throws Exception {

		// Enable default adapter on bundle stop.

		/*
		ServiceReference<BundleBlacklistManager> serviceReference =
			bundleContext.getServiceReference(BundleBlacklistManager.class);

		try {

			BundleBlacklistManager bundleBlacklistManager =
				bundleContext.getService(serviceReference);

			bundleBlacklistManager.removeFromBlacklistAndInstall(
				DEFAULT_ADAPTER_SYMBOLIC_NAME);
		}
		finally {
			bundleContext.ungetService(serviceReference);
		}
		*/
	}
		

	private Dictionary<String, Object> getDefaultConfiguration(String fileName) throws IOException {
	
		
		InputStream inputStream = new BufferedInputStream(new FileInputStream(fileName));
		
		Dictionary<String, Object> dictionary = new Hashtable<String, Object>();
		
		Properties properties = new Properties();
		properties.load(inputStream);
		
		for (Entry<Object, Object> entry : properties.entrySet()) {
			dictionary.put((String)entry.getKey(), entry.getValue());
		}

		return dictionary;
    }
	    
	private static final String CLAUSE_CONFIGURATION_PID = "fi.soveltia.liferay.gsearch.core.impl.configuration.ClauseConfiguration";

	private static final String CLAUSE_CONFIGURATION_FILE = "configs/fi.soveltia.liferay.gsearch.core.impl.configuration.ClauseConfiguration.config";

	private static final Log _log =
		LogFactoryUtil.getLog(CoreBundleActivator.class);

	@Reference
	private ConfigurationAdmin _configurationAdmin;
}
