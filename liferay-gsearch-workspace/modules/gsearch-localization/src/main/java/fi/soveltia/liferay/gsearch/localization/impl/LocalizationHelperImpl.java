package fi.soveltia.liferay.gsearch.localization.impl;

import com.liferay.portal.kernel.json.JSONArray;
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

import fi.soveltia.liferay.gsearch.localization.api.LocalizationHelper;

/**
 * Liferay GSearch shared localizations helper impl.
 *
 * @author Petteri Karttunen
 */
@Component(immediate = true, service = LocalizationHelper.class)
public class LocalizationHelperImpl implements LocalizationHelper {

	@Override
	public String getLocalization(
		Locale locale, String key, Object... objects) {

		if (!_resourceBundles.containsKey(locale)) {
			_resourceBundles.put(
				locale, _resourceBundleLoader.loadResourceBundle(locale));
		}

		String value = ResourceBundleUtil.getString(
			_resourceBundles.get(locale), key, objects);

		if (value == null) {
			return _language.format(locale, key, objects);
		}

		return value;
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
