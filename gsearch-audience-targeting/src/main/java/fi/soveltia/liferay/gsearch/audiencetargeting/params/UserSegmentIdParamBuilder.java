
package fi.soveltia.liferay.gsearch.audiencetargeting.params;

import java.util.Map;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.audiencetargeting.constants.GSearchAudienceTargetingConstants;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.exception.ParameterValidationException;
import fi.soveltia.liferay.gsearch.core.api.params.ParameterBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * User segment ID parameter builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ParameterBuilder.class
)
public class UserSegmentIdParamBuilder implements ParameterBuilder {

	@Override
	public void addParameter(QueryContext queryContext)
		throws Exception {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest) queryContext.getParameter(
				ParameterNames.HTTP_SERVLET_REQUEST);

		PortletRequest portletRequest =
			(PortletRequest) httpServletRequest.getAttribute(
				"javax.portlet.request");

		queryContext.setParameter(
			GSearchAudienceTargetingConstants.USER_SEGMENT_ID_PARAM,
			portletRequest.getAttribute(
				GSearchAudienceTargetingConstants.USER_SEGMENT_ID_PARAM));
	}

	@Override
	public void addParameter(
		QueryContext queryContext, Map<String, Object> parameters)
		throws Exception {

		queryContext.setParameter(
			GSearchAudienceTargetingConstants.USER_SEGMENT_ID_PARAM,
			(long[]) parameters.get(GSearchAudienceTargetingConstants.USER_SEGMENT_ID_PARAM));
	}

	@Override
	public boolean validate(QueryContext queryContext)
		throws ParameterValidationException {

		return true;
	}

	@Override
	public boolean validate(
		QueryContext queryContext, Map<String, Object> parameters)
		throws ParameterValidationException {

		return true;
	}
}
