
package fi.soveltia.liferay.gsearch.core.impl.params;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.exception.ParameterValidationException;
import fi.soveltia.liferay.gsearch.core.api.params.ParameterBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Start & end parameter builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ParameterBuilder.class
)
public class StartEndParameterBuilder implements ParameterBuilder {

	@Override
	public void addParameter(
		PortletRequest portletRequest, QueryContext queryContext)
		throws Exception {

		int start =
			ParamUtil.getInteger(portletRequest, ParameterNames.START, 0);

		int pageSize = GetterUtil.getInteger(
			queryContext.getPageSize(),
			DEFAULT_PAGE_SIZE);

		queryContext.setStart(start);
		queryContext.setEnd(start + pageSize);
	}

	@Override
	public boolean validate(PortletRequest portletRequest)
		throws ParameterValidationException {

		return true;
	}

	private static final int DEFAULT_PAGE_SIZE = 10;
}
