package fi.soveltia.liferay.gsearch.localization;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	immediate = true, 
	service = LocalizationHelper.class
)
public class LocalizationHelperImpl implements LocalizationHelper {

	@Override
	public String getLocalization(
		Locale locale, String key, Object... objects) {

		if (!_resourceBundles.containsKey(locale)) {
			_resourceBundles.put(
				locale,
				_resourceBundleLoader.loadResourceBundle(locale.toString()));
		}

		String value = ResourceBundleUtil.getString(
			_resourceBundles.get(locale), key, objects);

		return value == null ? _language.format(locale, key, objects) : value;
	}

	@Override
	public JSONArray getSortOptions(Locale locale, String[]configuration)
		throws Exception {

		JSONArray options = JSONFactoryUtil.createJSONArray();

		for (int i = 0; i < configuration.length; i++) {

			JSONObject item =
				JSONFactoryUtil.createJSONObject(configuration[i]);

			item.put(
				"localization", getLocalization(
					locale, "sort-by-" + item.getString("key").toLowerCase()));

			options.put(item);
		}

		return options;
	}
	
	@Override
	public void setFacetLocalizations(
		Locale locale, JSONObject responseObject) {

		JSONArray facets = responseObject.getJSONArray("facets");

		if (facets == null || facets.length() == 0) {
			return;
		}
	
		for (int i = 0; i < facets.length(); i++) {

			JSONObject resultItem = facets.getJSONObject(i);

			resultItem.put(
				"anyOption",
				getLocalization(
					locale,
					"any-" + resultItem.getString("param_name").toLowerCase()));

			resultItem.put(
				"multipleOption",
				getLocalization(
					locale, "multiple-" +
						resultItem.getString("param_name").toLowerCase()));

			JSONArray values = resultItem.getJSONArray("values");

			for (int j = 0; j < values.length(); j++) {

				JSONObject value = values.getJSONObject(j);

				value.put(
					"name", getLocalization(
						locale, value.getString("name").toLowerCase()));
			}
		}
	}
	
	@Override
	public void setResultTypeLocalizations(
		Locale locale, JSONObject responseObject) {

		JSONArray items = responseObject.getJSONArray("items");

		if (items == null || items.length() == 0) {
			return;
		}

		for (int i = 0; i < items.length(); i++) {

			JSONObject resultItem = items.getJSONObject(i);

			resultItem.put(
				"type", getLocalization(
					locale, resultItem.getString("type").toLowerCase()));
		}
	}

	@Override
	public void setGroupingLocalizations(
		Locale locale, JSONObject responseObject) {

		JSONArray items = responseObject.getJSONArray("items");

		if (items == null || items.length() == 0) {
			return;
		}

		for (int i = 0; i < items.length(); i++) {

			JSONObject resultItem = items.getJSONObject(i);

			resultItem.put(
				"group_localized", getLocalization(locale, "suggestion.group." +
					resultItem.getString("group").toLowerCase()));
		}
	}

	
	@Reference
	private Language _language;

	@Reference(
		target = "(bundle.symbolic.name=fi.soveltia.liferay.gsearch.localization)", 
		unbind = "-"
	)
	private ResourceBundleLoader _resourceBundleLoader;	
	
	private Map<Locale, ResourceBundle> _resourceBundles =
					new ConcurrentHashMap<>();


}
