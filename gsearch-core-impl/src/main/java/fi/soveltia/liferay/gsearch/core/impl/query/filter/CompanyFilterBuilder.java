
package fi.soveltia.liferay.gsearch.core.impl.query.filter;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.filter.FilterBuilder;

/**
 * Company filter builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = FilterBuilder.class
)
public class CompanyFilterBuilder implements FilterBuilder {

	@Override
	public void addFilters(
		PortletRequest portletRequest, BooleanFilter preBooleanfilter,
		BooleanFilter postFilter, QueryContext queryContext)
		throws Exception {

		// Search only this portal instance.
		// Create a parameter builder if you want to control
		// this earlier in the pipeline.

		long companyId;

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		if (themeDisplay != null) {
			companyId = themeDisplay.getCompanyId();
		}
		else {
			companyId = _portal.getDefaultCompanyId();
		}

		preBooleanfilter.addRequiredTerm(Field.COMPANY_ID, companyId);
	}

	@Reference
	Portal _portal;
}
