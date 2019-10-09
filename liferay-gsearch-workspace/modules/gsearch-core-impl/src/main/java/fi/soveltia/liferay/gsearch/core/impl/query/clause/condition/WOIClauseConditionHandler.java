package fi.soveltia.liferay.gsearch.core.impl.query.clause.condition;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationValues;
import fi.soveltia.liferay.gsearch.core.api.constants.SessionAttributes;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseConditionHandler;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.impl.util.GSearchUtil;

/**
 * Processes WOI clause condition. 
 * 
 * Just a simple exists implemented currently.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ClauseConditionHandler.class
)
public class WOIClauseConditionHandler implements ClauseConditionHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canProcess(String handlerName) {
		if (handlerName.equals(_HANDLER_NAME)) {

			return true;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isTrue(QueryContext queryContext, JSONObject configuration)
		throws Exception {

		String woiCondition = configuration.getString("woi_condition");
		
		if (Validator.isBlank(woiCondition)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Cannot process the clause condition. Check the existence of 'woi_condition' property in the configuration.");
			}
		}
		
		woiCondition = woiCondition.toLowerCase();

		PortletRequest portletRequest = 
				GSearchUtil.getPortletRequestFromContext(queryContext);

		PortletSession session = portletRequest.getPortletSession();

		Map<String, Integer> previousKeywords = 
				(Map<String, Integer>)session.getAttribute(
				SessionAttributes.PREVIOUS_KEYWORDS, PortletSession.APPLICATION_SCOPE);
				
		switch(woiCondition) {

			case ClauseConfigurationValues.MATCH_NOT_NULL: 

				return previousKeywords != null;
			default:
				return false;
		}		
	}
	
	private static final String _HANDLER_NAME = "woi";

	private static final Logger _log = LoggerFactory.getLogger(
		WOIClauseConditionHandler.class);
}