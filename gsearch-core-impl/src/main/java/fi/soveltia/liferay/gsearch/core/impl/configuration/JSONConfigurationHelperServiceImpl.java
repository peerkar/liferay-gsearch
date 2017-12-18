package fi.soveltia.liferay.gsearch.core.impl.configuration;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import fi.soveltia.liferay.gsearch.core.api.configuration.JSONConfigurationHelperService;
import fi.soveltia.liferay.gsearch.core.configuration.GSearchConfiguration;

/**
 * JSON configuration helper service implementation.
 * 
 * @author Petteri
 *
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.core.configuration.GSearchConfiguration", 
	immediate = true,
	service = JSONConfigurationHelperService.class
)
public class JSONConfigurationHelperServiceImpl implements JSONConfigurationHelperService {

	@Activate 
	@Modified
	protected void activate(Map<String, Object> properties) {
		_gSearchConfiguration = ConfigurableUtil.createConfigurable(
			GSearchConfiguration.class, properties);
	}	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONArray getAssetTypeOptions(Locale locale) throws JSONException {

		String configuration = _gSearchConfiguration.typeConfiguration();

		JSONArray translatedOptions = JSONFactoryUtil.createJSONArray();
		
		JSONArray options = JSONFactoryUtil.createJSONArray(configuration);

		for (int i = 0; i < options.length(); i++) {

			JSONObject item = options.getJSONObject(i);
			
			item.put("localization", getLocalization("type." + item.getString("entryClassName").toLowerCase(), locale));
			translatedOptions.put(item);
			
		}
		return translatedOptions;
	}	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONArray getSortOptions(Locale locale) throws JSONException {
		
		String configuration = _gSearchConfiguration.sortFieldConfiguration();

		JSONArray translatedOptions = JSONFactoryUtil.createJSONArray();
			
		JSONArray options = JSONFactoryUtil.createJSONArray(configuration);
			
		for (int i = 0; i < options.length(); i++) {

			JSONObject item = options.getJSONObject(i);

			item.put("localization", getLocalization("sort-by-" + item.getString("key").toLowerCase(), locale));

			translatedOptions.put(item);
			
		}
		return translatedOptions;
	}

	private String getLocalization(String key, Locale locale) {
		if (_resourceBundle == null) {
			_resourceBundle = ResourceBundleUtil.getBundle(
				"content.Language", locale, JSONConfigurationHelperServiceImpl.class);
		}
		return _resourceBundle.getString(key);
	}
	
	private volatile GSearchConfiguration _gSearchConfiguration;

	private ResourceBundle _resourceBundle;
}