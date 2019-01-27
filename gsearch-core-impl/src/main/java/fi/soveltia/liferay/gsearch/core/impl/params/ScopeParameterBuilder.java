
package fi.soveltia.liferay.gsearch.core.impl.params;

import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.exception.ParameterValidationException;
import fi.soveltia.liferay.gsearch.core.api.params.ParameterBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Group parameter builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ParameterBuilder.class
)
public class ScopeParameterBuilder implements ParameterBuilder {

	@Override
	public void addParameter(
		PortletRequest portletRequest, QueryContext queryContext)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		String scopeFilter =
			ParamUtil.getString(portletRequest, ParameterNames.SCOPE);

		long[] groupIds;

		if ("this-site".equals(scopeFilter)) {
			groupIds = new long[] {
				themeDisplay.getScopeGroupId()
			};
		}
		else {
			groupIds = new long[0];
		}

		queryContext.setParameter(ParameterNames.GROUP_ID, groupIds);
	}

	@Override
	public boolean validate(PortletRequest portletRequest)
		throws ParameterValidationException {

		return true;
	}
}
