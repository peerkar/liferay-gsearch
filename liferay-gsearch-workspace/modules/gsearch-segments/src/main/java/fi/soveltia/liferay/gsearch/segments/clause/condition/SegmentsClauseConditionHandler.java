
package fi.soveltia.liferay.gsearch.segments.clause.condition;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationValues;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseConditionHandler;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.segments.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.segments.constants.SegmentsClauseConditionProperties;
import fi.soveltia.liferay.gsearch.segments.constants.SegmentsParameterNames;

/**
 * Processes "segments" clause condition.
 *
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.weather.configuration.ModuleConfiguration",
	immediate = true, 
	service = ClauseConditionHandler.class
)
public class SegmentsClauseConditionHandler implements ClauseConditionHandler {

	@Override
	public boolean canProcess(String handlerName) {
		if (handlerName.equals(_HANDLER_NAME) &&
			_moduleConfiguration.isEnabled()) {

			return true;
		}
		return false;
	}

	@Override
	public boolean isTrue(QueryContext queryContext, JSONObject configuration)
		throws Exception {
		
		String matchProperty = configuration.getString(
				ClauseConfigurationKeys.MATCH_PROPERTY);
		
		String matchType = configuration.getString(
				ClauseConfigurationKeys.MATCH_TYPE);
		
		if (Validator.isBlank(matchProperty) || Validator.isBlank(matchType)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Cannot process the clause condition. Match property or type empty.");
			}
		}
		
		matchProperty = matchProperty.toLowerCase();

		switch(matchProperty) {
	
			case SegmentsClauseConditionProperties.USER_SEGMENT_ID: 
				return _checkUserSegmentId(queryContext, configuration);
			default:
				return false;
		}			
	}
	
	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}	
			
	/**
	 * Handles user segment id property.
	 * 
	 * @param queryContext
	 * @param configuration
	 * @return
	 * @throws Exception
	 */
	private boolean _checkUserSegmentId(
			QueryContext queryContext, JSONObject configuration) 
					throws Exception {

		long[] userSegmentIds = (long[])queryContext.getParameter(
				SegmentsParameterNames.USER_SEGMENT_IDS);

		JSONArray matchValues = configuration.getJSONArray(
				ClauseConfigurationKeys.MATCH_VALUES);

		String matchType = configuration.getString(
				ClauseConfigurationKeys.MATCH_TYPE);

		if (userSegmentIds == null || matchValues == null || matchValues.length() == 0 
				|| Validator.isBlank(matchType)) {
			if (_log.isWarnEnabled()) {
				_log.warn("Cannot process the property condition. Check configuration");
				_log.warn("User segment ids in query context: " 
						+ (userSegmentIds != null ? userSegmentIds : 0));
				_log.warn("Matchvalues: " + (matchValues != null ? matchValues.length() : 0));
				_log.warn("Matchtype: " + matchType);
			}
			return false;
		}
	
		if (_log.isDebugEnabled()) {
			_log.debug("Current user has the following user segments:");

			for (long l : userSegmentIds) {
				_log.debug(String.valueOf(l));
			}
		}
			
		int matchCount = 0;

		for (int i = 0; i < matchValues.length(); i++) {
			long matchId = matchValues.getLong(i);

			for (long userSegmentId : userSegmentIds) {
				
				if (userSegmentId == matchId) {
					matchCount++;
				}
			}
		}

		if (matchCount > 0) {
			if (matchType.equalsIgnoreCase(ClauseConfigurationValues.MATCH_ANY)) {
				return true;
			} else if (matchType.equalsIgnoreCase(ClauseConfigurationValues.MATCH_NOT)) {
				return false;
			}
		}
		else if (matchValues.length() > 0 
				&& matchType.equalsIgnoreCase(ClauseConfigurationValues.MATCH_NOT)) {
			return true;
		}
		
		return false;		
	}	
			
	private static final String _HANDLER_NAME = "segments";

	private static final Logger _log = LoggerFactory.getLogger(
		SegmentsClauseConditionHandler.class);

	private volatile ModuleConfiguration _moduleConfiguration;
}