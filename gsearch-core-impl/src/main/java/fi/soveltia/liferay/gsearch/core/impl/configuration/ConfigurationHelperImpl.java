
package fi.soveltia.liferay.gsearch.core.impl.configuration;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.apache.felix.cm.file.ConfigurationHandler;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;

/**
 * JSON configuration helper service implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.core.impl.configuration.ModuleConfiguration", 
	immediate = true,
	service = ConfigurationHelper.class
)
public class ConfigurationHelperImpl implements ConfigurationHelper {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {

		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getAssetTypeConfiguration() {

		try {

			Configuration configuration = _configurationAdmin.getConfiguration(
				ASSET_TYPE_CONFIGURATION_PID);

			if (configuration.getProperties() == null) {
				setDefaultConfiguration(
					configuration, ASSET_TYPE_CONFIGURATION_PID);
			}

			return (String[]) _configurationAdmin.getConfiguration(
				ASSET_TYPE_CONFIGURATION_PID).getProperties().get("assetTypes");

		}
		catch (IOException e) {
			_log.error(e.getMessage(), e);
		}

		_log.warn("Asset type configuration is not set.");

		return new String[] {};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getClauseConfiguration() {

		try {

			Configuration configuration =
				_configurationAdmin.getConfiguration(CLAUSE_CONFIGURATION_PID);

			if (configuration.getProperties() == null) {
				setDefaultConfiguration(
					configuration, CLAUSE_CONFIGURATION_PID);
			}

			return (String[]) _configurationAdmin.getConfiguration(
				CLAUSE_CONFIGURATION_PID).getProperties().get("clauses");
		}
		catch (Exception e) {
			_log.error(e.getMessage(), e);
		}

		_log.warn("Clause configuration is not set.");

		return new String[] {};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getFacetConfiguration() {

		try {

			Configuration configuration =
				_configurationAdmin.getConfiguration(FACET_CONFIGURATION_PID);

			if (configuration.getProperties() == null) {
				setDefaultConfiguration(
					configuration, FACET_CONFIGURATION_PID);
			}

			return (String[]) _configurationAdmin.getConfiguration(
				FACET_CONFIGURATION_PID).getProperties().get("facets");

		}
		catch (IOException e) {
			_log.error(e.getMessage(), e);
		}

		_log.warn("Facets configuration is not set.");

		return new String[] {};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getKeywordSuggesterConfiguration() {

		try {

			Configuration configuration = _configurationAdmin.getConfiguration(
				KEYWORD_SUGGESTER_CONFIGURATION_PID);

			if (configuration.getProperties() == null) {
				setDefaultConfiguration(
					configuration, KEYWORD_SUGGESTER_CONFIGURATION_PID);
			}

			return (String[]) _configurationAdmin.getConfiguration(
				KEYWORD_SUGGESTER_CONFIGURATION_PID).getProperties().get(
					"keywordSuggesters");

		}
		catch (IOException e) {
			_log.error(e.getMessage(), e);
		}

		_log.warn("Keyword suggester configuration is not set.");

		return new String[] {};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getSortConfiguration() {

		try {

			Configuration configuration =
				_configurationAdmin.getConfiguration(SORT_CONFIGURATION_PID);

			if (configuration.getProperties() == null) {
				setDefaultConfiguration(
					configuration, SORT_CONFIGURATION_PID);
			}

			return (String[]) _configurationAdmin.getConfiguration(
				SORT_CONFIGURATION_PID).getProperties().get("sorts");

		}
		catch (IOException e) {
			_log.error(e.getMessage(), e);
		}

		_log.warn("Sort configuration is not set.");

		return new String[] {};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String parseConfigurationVariables(
		PortletRequest portletRequest, QueryParams queryParams, String input) {

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

	private static final String ASSET_TYPE_CONFIGURATION_PID =
		"fi.soveltia.liferay.gsearch.core.impl.configuration.AssetTypeConfiguration";

	private static final String CLAUSE_CONFIGURATION_PID =
		"fi.soveltia.liferay.gsearch.core.impl.configuration.ClauseConfiguration";

	private static final String FACET_CONFIGURATION_PID =
		"fi.soveltia.liferay.gsearch.core.impl.configuration.FacetConfiguration";

	private static final String KEYWORD_SUGGESTER_CONFIGURATION_PID =
		"fi.soveltia.liferay.gsearch.core.impl.configuration.KeywordSuggesterConfiguration";

	private static final String SORT_CONFIGURATION_PID =
		"fi.soveltia.liferay.gsearch.core.impl.configuration.SortConfiguration";

	private static final Logger _log =
		LoggerFactory.getLogger(ConfigurationHelperImpl.class);

	private static final DateFormat NOW_YYYY_MM_DD =
		new SimpleDateFormat("yyyy-MM-dd");

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	private volatile ModuleConfiguration _moduleConfiguration;
}
