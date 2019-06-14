
package fi.soveltia.liferay.gsearch.web.menuoption;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.localization.LocalizationHelper;
import fi.soveltia.liferay.gsearch.web.constants.GSearchWebKeys;

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

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.
						getAttribute(WebKeys.THEME_DISPLAY);

		Template template =
			(Template) portletRequest.getAttribute(WebKeys.TEMPLATE);

		String[] configuration =
			_coreConfigurationHelper.getSortConfiguration();

		JSONArray options =
			_localizationHelper.getSortOptions(themeDisplay.getLocale(), configuration);

		template.put(GSearchWebKeys.SORT_OPTIONS, options);
	}

	@Reference
	protected ConfigurationHelper _coreConfigurationHelper;

	@Reference
	LocalizationHelper _localizationHelper;
}
