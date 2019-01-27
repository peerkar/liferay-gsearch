package fi.soveltia.liferay.gsearch.core.impl.params;

import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.exception.ParameterValidationException;
import fi.soveltia.liferay.gsearch.core.api.params.ParameterBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Locale parameter builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ParameterBuilder.class
)
public class LocaleParameterBuilder implements ParameterBuilder {

	@Override
	public void addParameter(
		PortletRequest portletRequest, QueryContext queryContext)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		queryContext.setParameter(
			ParameterNames.LOCALE, themeDisplay.getLocale());
	}

	@Override
	public boolean validate(PortletRequest portletRequest)
		throws ParameterValidationException {

		return true;
	}
}
