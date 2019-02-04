
package fi.soveltia.liferay.gsearch.web.menuoption;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.Locale;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.web.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.web.portlet.util.LocalizationHelper;

@Component(
	immediate = true, 
	property = {
		"menu.name=sort-options"
	}, 
	service = MenuOptionProvider.class
)
public class SortOptionsMenuProvider implements MenuOptionProvider {

	@Override
	public void setOptions(PortletRequest portletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(
			GSearchWebKeys.THEME_DISPLAY);

		Template template =
			(Template) portletRequest.getAttribute(WebKeys.TEMPLATE);

		Locale locale = themeDisplay.getLocale();

		String[] configuration =
			_configurationHelper.getSortConfiguration();

		JSONArray options = JSONFactoryUtil.createJSONArray();

		for (int i = 0; i < configuration.length; i++) {

			JSONObject item =
				JSONFactoryUtil.createJSONObject(configuration[i]);

			item.put(
				"localization", _localizationHelper.getLocalization(
					locale, "sort-by-" + item.getString("key").toLowerCase()));

			options.put(item);
		}

		template.put(GSearchWebKeys.SORT_OPTIONS, options);
	}
	
	@Reference
	protected ConfigurationHelper _configurationHelper;

	@Reference
	LocalizationHelper _localizationHelper;
}
