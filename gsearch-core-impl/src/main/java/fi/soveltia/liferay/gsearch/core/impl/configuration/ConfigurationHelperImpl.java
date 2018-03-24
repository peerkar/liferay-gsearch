
package fi.soveltia.liferay.gsearch.core.impl.configuration;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;

/**
 * JSON configuration helper service implementation.
 * 
 * @author Petteri
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.core.configuration.GSearchCore", 
	immediate = true,
	service = ConfigurationHelper.class
)
public class ConfigurationHelperImpl implements ConfigurationHelper {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {

		_gSearchConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONArray getAssetTypeOptions(Locale locale)
		throws JSONException {

		String configuration = _gSearchConfiguration.typeConfiguration();

		JSONArray translatedOptions = JSONFactoryUtil.createJSONArray();

		JSONArray options = JSONFactoryUtil.createJSONArray(configuration);

		for (int i = 0; i < options.length(); i++) {

			JSONObject item = options.getJSONObject(i);

			item.put(
				"localization",
				getLocalization(
					"type." + item.getString("entryClassName").toLowerCase(),
					locale));
			translatedOptions.put(item);

		}
		return translatedOptions;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONArray getFacetConfiguration()
		throws JSONException {

		return JSONFactoryUtil.createJSONArray(
			_gSearchConfiguration.facetConfiguration());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONArray getSortOptions(Locale locale)
		throws JSONException {

		String configuration = _gSearchConfiguration.sortFieldConfiguration();

		JSONArray translatedOptions = JSONFactoryUtil.createJSONArray();

		JSONArray options = JSONFactoryUtil.createJSONArray(configuration);

		for (int i = 0; i < options.length(); i++) {

			JSONObject item = options.getJSONObject(i);

			item.put(
				"localization", getLocalization(
					"sort-by-" + item.getString("key").toLowerCase(), locale));

			translatedOptions.put(item);

		}
		return translatedOptions;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String parseConfigurationKey(PortletRequest portletRequest, String fieldName) {
		
		fieldName = fieldName.replace("$language_id", portletRequest.getLocale().toString());		
		
		return fieldName;
	}	
	
	private String getLocalization(String key, Locale locale) {

		if (_resourceBundle == null) {
			_resourceBundle = ResourceBundleUtil.getBundle(
				"content.Language", locale, ConfigurationHelperImpl.class);
		}
		try {
			return _resourceBundle.getString(key);
		}
		catch (Exception e) {
			_log.error(e, e);
		}
		return key;
	}

	
	private volatile ModuleConfiguration _gSearchConfiguration;

	private ResourceBundle _resourceBundle;

	private static final Log _log =
		LogFactoryUtil.getLog(ConfigurationHelperImpl.class);
}
