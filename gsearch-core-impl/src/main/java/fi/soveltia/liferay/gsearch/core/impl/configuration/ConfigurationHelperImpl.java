
package fi.soveltia.liferay.gsearch.core.impl.configuration;

import com.liferay.portal.kernel.util.StringBundler;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.portlet.PortletRequest;

import org.apache.felix.cm.file.ConfigurationHandler;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationItemHelper;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * JSON configuration helper service implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ConfigurationHelper.class
)
public class ConfigurationHelperImpl implements ConfigurationHelper {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getClauseConfiguration() {

		String[] config = _clauseConfigurationHelper.getConfiguration();

		if (config == null || config.length == 0 || config[0].length() == 0) {
			setDefaultConfiguration(
				ClauseConfigurationItemHelper.CONFIGURATION_PID);
		}

		return _clauseConfigurationHelper.getConfiguration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getFacetConfiguration() {

		String[] config = _facetConfigurationHelper.getConfiguration();

		if (config == null || config.length == 0 || config[0].length() == 0) {
			setDefaultConfiguration(
				FacetConfigurationItemHelper.CONFIGURATION_PID);
		}

		return _facetConfigurationHelper.getConfiguration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getKeywordSuggesterConfiguration() {

		String[] config =
			_keywordSuggestertConfigurationHelper.getConfiguration();

		if (config == null || config.length == 0 || config[0].length() == 0) {
			setDefaultConfiguration(
				KeywordSuggesterItemHelper.CONFIGURATION_PID);
		}
		
		return _keywordSuggestertConfigurationHelper.getConfiguration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getFilterConfiguration() {
		
		String[] config = _filterConfigurationHelper.getConfiguration();

		if (config == null || config.length == 0 || config[0].length() == 0) {
			setDefaultConfiguration(
				FilterConfigurationItemHelper.CONFIGURATION_PID);
		}

		return _filterConfigurationHelper.getConfiguration();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getSortConfiguration() {

		String[] config = _sortConfigurationHelper.getConfiguration();

		if (config == null || config.length == 0 || config[0].length() == 0) {
			setDefaultConfiguration(
				SortConfigurationItemHelper.CONFIGURATION_PID);
		}

		return _sortConfigurationHelper.getConfiguration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String parseConfigurationVariables(
		PortletRequest portletRequest, QueryContext queryParams, String input) {

		input = input.replace(
			"$_now_yyyy-mm-dd_$", NOW_YYYY_MM_DD.format(new Date()));

		if (queryParams != null && queryParams.getKeywords() != null) {

			input = input.replace("$_keywords_$", queryParams.getKeywords());
		}

		input = input.replace(
			"$_language_id_$", portletRequest.getLocale().toString());

		return input;
	}

	@SuppressWarnings("unchecked")
	private void setDefaultConfiguration(String configurationName) {

		_log.info("Setting default configuration for: " + configurationName);

		InputStream inputStream = null;

		try {

			Configuration configuration =
				_configurationAdmin.getConfiguration(configurationName);

			StringBundler sb = new StringBundler();
			sb.append("configs/").append(configurationName).append(".config");

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

	private static final Logger _log =
		LoggerFactory.getLogger(ConfigurationHelperImpl.class);

	private static final DateFormat NOW_YYYY_MM_DD =
		new SimpleDateFormat("yyyy-MM-dd");

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference(target = "(config.item=filter)")
	private ConfigurationItemHelper _filterConfigurationHelper;

	@Reference(target = "(config.item=clause)")
	private ConfigurationItemHelper _clauseConfigurationHelper;

	@Reference(target = "(config.item=facet)")
	private ConfigurationItemHelper _facetConfigurationHelper;

	@Reference(target = "(config.item=keyword)")
	private ConfigurationItemHelper _keywordSuggestertConfigurationHelper;

	@Reference(target = "(config.item=sort)")
	private ConfigurationItemHelper _sortConfigurationHelper;

}
