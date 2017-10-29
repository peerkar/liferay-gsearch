
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
import fi.soveltia.liferay.gsearch.web.search.menuoption.DocumentExtensionOptions;

/**
 * Document extension (format) options implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = DocumentExtensionOptions.class
)
public class DocumentExtensionOptionsImpl implements DocumentExtensionOptions {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONArray getOptions(
		PortletRequest portletRequest,
		GSearchDisplayConfiguration gSearchDisplayConfiguration)
		throws Exception {
		
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", portletRequest.getLocale(),
			GsearchWebPortlet.class);

		String[] configurationOptions =
			gSearchDisplayConfiguration.documentExtensionOptions();

		JSONArray options = JSONFactoryUtil.createJSONArray();

		for (String extension : configurationOptions) {
			
			// Syntax: filter_key;translation_key_for_ui;underscore_separated_extensions_list
			// Using underscores as commas don't work there.
			
			String[] parts = extension.split(";");

			if (parts.length == 3) {

				JSONObject item = JSONFactoryUtil.createJSONObject();
				item.put("key", parts[0]);
				item.put("name", LanguageUtil.get(resourceBundle, parts[1]));

				options.put(item);
			}
		}
		return options;
	}
}
