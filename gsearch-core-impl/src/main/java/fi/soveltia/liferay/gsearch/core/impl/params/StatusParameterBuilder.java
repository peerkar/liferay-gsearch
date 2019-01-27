
package fi.soveltia.liferay.gsearch.core.impl.params;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.exception.ParameterValidationException;
import fi.soveltia.liferay.gsearch.core.api.params.ParameterBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Status parameter builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ParameterBuilder.class
)
public class StatusParameterBuilder implements ParameterBuilder {

	@Override
	public void addParameter(
		PortletRequest portletRequest, QueryContext queryContext)
		throws Exception {

		int status = ParamUtil.getInteger(
			portletRequest, ParameterNames.STATUS,
			WorkflowConstants.STATUS_APPROVED);

		queryContext.setParameter(ParameterNames.STATUS, status);
	}

	@Override
	public boolean validate(PortletRequest portletRequest)
		throws ParameterValidationException {

		return true;
	}
}
