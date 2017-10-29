
package fi.soveltia.liferay.gsearch.web.search.internal.menuoption;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.ResourceBundle;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;
import fi.soveltia.liferay.gsearch.web.portlet.GsearchWebPortlet;
import fi.soveltia.liferay.gsearch.web.search.menuoption.AssetTypeOptions;

/**
 * Asset type options implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = AssetTypeOptions.class
)
public class AssetTypeOptionsImpl implements AssetTypeOptions {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONArray getOptions(
		PortletRequest portletRequest,
		GSearchDisplayConfiguration gSearchDisplayConfiguration) {

		String[] configurationOptions =
			gSearchDisplayConfiguration.assetTypeOptions();

		JSONArray options = JSONFactoryUtil.createJSONArray();

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", portletRequest.getLocale(),
			GsearchWebPortlet.class);

		for (String option : configurationOptions) {
			String[] parts = option.split(";");
			if (parts.length == 2) {
				JSONObject item = JSONFactoryUtil.createJSONObject();
				item.put("key", parts[0]);
				item.put("facet", parts[1]);
				item.put(
					"name", LanguageUtil.get(resourceBundle, parts[0] + "s"));
				options.put(item);
			}
		}
		return options;
	}
}
