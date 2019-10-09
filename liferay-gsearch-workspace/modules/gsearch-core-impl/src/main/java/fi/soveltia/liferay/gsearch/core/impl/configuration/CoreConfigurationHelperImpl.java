
package fi.soveltia.liferay.gsearch.core.impl.configuration;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.index.IndexNameBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map.Entry;

import org.apache.felix.cm.file.ConfigurationHandler;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.configuration.CoreConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Core configuration helper.
 * 
 * Provides centralized access to core configurations
 * through facade classes.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = CoreConfigurationHelper.class
)
public class CoreConfigurationHelperImpl implements CoreConfigurationHelper {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONArray getClauses() {
		
		String[] config = _clauseConfigurationHelper.getClauses();

		if ((config == null) || (config.length == 0) ||
			(config[0].length() == 0)) {

			_setDefaultConfiguration(
				ClauseConfigurationHelper.CONFIGURATION_PID);
		}
		
		return stringArrayToJSONArray(
				_clauseConfigurationHelper.getClauses());
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getDescriptionMaxLength() {
		
		return _highlighterConfigurationHelper.getDescriptionMaxLength();
	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONArray getFacets() {
		
		String[] config = _facetConfigurationHelper.getFacets();

		if ((config == null) || (config.length == 0) ||
			(config[0].length() == 0)) {

			_setDefaultConfiguration(
				FacetConfigurationHelper.CONFIGURATION_PID);
		}

		return stringArrayToJSONArray(_facetConfigurationHelper.getFacets());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONArray getFilters() {
		String[] config = _filterConfigurationHelper.getFilters();

		if ((config == null) || (config.length == 0) ||
			(config[0].length() == 0)) {

			_setDefaultConfiguration(
				FilterConfigurationHelper.CONFIGURATION_PID);
		}

		return stringArrayToJSONArray(
				_filterConfigurationHelper.getFilters());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getHightlightFields(Locale locale) {
		return (new String[] {
			Field.CONTENT, 
			Field.CONTENT + StringPool.UNDERLINE + locale.toString(), 
			Field.TITLE, 
			Field.TITLE + StringPool.UNDERLINE + locale.toString()});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getHighlightFragmentSize() {
		return _highlighterConfigurationHelper.getHighlightFragmentSize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getHighlightSnippetSize() {
		return _highlighterConfigurationHelper.getHighlightSnippetSize();
	}	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONArray getKeywordSuggesters() {
		String[] config =
			_keywordSuggestertConfigurationHelper.getKeywordSuggesters();

		if ((config == null) || (config.length == 0) ||
			(config[0].length() == 0)) {

			_setDefaultConfiguration(
				KeywordSuggesterConfigurationHelper.CONFIGURATION_PID);
		}

		return stringArrayToJSONArray(
				_keywordSuggestertConfigurationHelper.getKeywordSuggesters());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLiferayIndexName(long companyId) {
		return _indexNameBuilder.getIndexName(companyId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMaxFacetTerms() {
		
		return _facetConfigurationHelper.getMaxFacetTerms();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONArray getRescoreClauses() {
		
		String[] config = _rescoreClauseConfigurationHelper.getRescoreClauses();

		if ((config == null) || (config.length == 0) ||
			(config[0].length() == 0)) {

			_setDefaultConfiguration(
				RescoreClauseConfigurationHelper.CONFIGURATION_PID);
		}

		return stringArrayToJSONArray(
				_rescoreClauseConfigurationHelper.getRescoreClauses());
	}
	
	@Override
	public String[] getSearchIndexNames(long companyId) {

		String[] config = _indeConfigurationHelper.getSearchIndexes();

		if ((config == null) || (config.length == 0) ||
			(config[0].length() == 0)) {

			return new String[] {_indexNameBuilder.getIndexName(companyId)};
		}

		return config;
	}


	@Override
	public String getSuggesterIndexName(long companyId) {

		String config = _indeConfigurationHelper.getKeywordSuggesterIndex();

		if (Validator.isBlank(config)) {

			return "liferay-" + companyId;
		}

		return config;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONArray getSorts() {
		String[] config = _sortConfigurationHelper.getSorts();

		if ((config == null) || (config.length == 0) ||
			(config[0].length() == 0)) {

			_setDefaultConfiguration(
				SortConfigurationHelper.CONFIGURATION_PID);
		}

		return stringArrayToJSONArray(_sortConfigurationHelper.getSorts());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHighlightEnabled() {
		return _highlighterConfigurationHelper.isHighlightEnabled();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject parseConfigurationVariables(
		QueryContext queryContext, JSONObject configurationItem) throws Exception {

		try {
		
			if (queryContext.getConfigurationVariables() == null ||
					queryContext.getConfigurationVariables().isEmpty()) {
				return configurationItem;
			}
	
			// A "brute force" str replace ...
	
			String str = configurationItem.toString();
	
			boolean changed = false;
			
			for (Entry<String, String>entry : queryContext.getConfigurationVariables().entrySet()) {
	
				if (str.contains(entry.getKey())) {
					str = str.replace(entry.getKey(), entry.getValue());
					changed = true;
				}
			}
			
			if (changed) {
				return JSONFactoryUtil.createJSONObject(str);
			}
			
			return configurationItem;
						
		} catch (Exception e) {
			_log.error("Error in parsing configuration variables for:");
			_log.error(configurationItem.toString());
			_log.error(e.getMessage(), e);
			
			throw new Exception(e);
		}
	}

	/**
	 * Loads and sets a default configuration for the given PID.
	 * 
	 * @param configurationName
	 */
	@SuppressWarnings("unchecked")
	private void _setDefaultConfiguration(String configurationName) {
		_log.info("Setting default configuration for: " + configurationName);

		InputStream inputStream = null;

		try {
			Configuration configuration = _configurationAdmin.getConfiguration(
				configurationName);

			StringBundler sb = new StringBundler();

			sb.append("configs/");
			sb.append(configurationName);
			sb.append(".config");

			inputStream = getClass(
			).getClassLoader(
			).getResourceAsStream(
				sb.toString()
			);

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
				catch (IOException ioe) {
					_log.error(ioe.getMessage(), ioe);
				}
			}
		}
	}
	
	/**
	 * Converts string array to JSON array.
	 * 
	 * @param configuration
	 * @return
	 */
	@Override
	public JSONArray stringArrayToJSONArray(String[]stringArray) {
		
		JSONArray jsonConfig = JSONFactoryUtil.createJSONArray();

		try {
			
			for (String item : stringArray) {
				JSONObject a = JSONFactoryUtil.createJSONObject(item);
				jsonConfig.put(a);
			}
			
			return jsonConfig;
		} catch (JSONException e) {
			_log.error("Error in creating JSONArray configuration:");
			_log.error(stringArray.toString());
			_log.error(e.getMessage(), e);
		}
		return null;
	}
	
	private static final Logger _log = LoggerFactory.getLogger(
		CoreConfigurationHelperImpl.class);

	@Reference
	private ClauseConfigurationHelper _clauseConfigurationHelper;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private FacetConfigurationHelper _facetConfigurationHelper;

	@Reference
	private FilterConfigurationHelper _filterConfigurationHelper;

	@Reference
	private HighlighterConfigurationHelper _highlighterConfigurationHelper;

	@Reference
	private IndexConfigurationHelper _indeConfigurationHelper;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private KeywordSuggesterConfigurationHelper _keywordSuggestertConfigurationHelper;

	@Reference
	private RescoreClauseConfigurationHelper _rescoreClauseConfigurationHelper;
	
	@Reference
	private SortConfigurationHelper _sortConfigurationHelper;
}