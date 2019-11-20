package fi.soveltia.liferay.gsearch.weather.clause.condition;

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
import fi.soveltia.liferay.gsearch.weather.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.weather.constants.WeatherClauseConditionProperties;
import fi.soveltia.liferay.gsearch.weather.constants.WeatherParameterNames;

/**
 * Processes "weather" clause condition.
 *
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.weather.configuration.ModuleConfiguration",
	immediate = true, 
	service = ClauseConditionHandler.class
)
public class WeatherClauseConditionHandler implements ClauseConditionHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canProcess(String handlerName) {
		if (handlerName.equals(_HANDLER_NAME) &&
			_moduleConfiguration.isEnabled()) {

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

		String matchProperty = configuration.getString(
				ClauseConfigurationKeys.MATCH_PROPERTY);
		
		String matchType = configuration.getString(
				ClauseConfigurationKeys.MATCH_TYPE);
		
		if (Validator.isBlank(matchProperty) || Validator.isBlank(matchType)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
						"Cannot process the clause condition.");
				_log.warn("Match property:" + matchProperty);
				_log.warn("Match type:" + matchType);
			}
		}
		
		matchProperty = matchProperty.toLowerCase();

		switch(matchProperty) {
	
			case WeatherClauseConditionProperties.TEMPERATURE: 
				return _checkTemperature(queryContext, configuration);
			case WeatherClauseConditionProperties.WEATHER: 
				return _checkWeather(queryContext, configuration);
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
	 * Check temperature condition.
	 * 
	 * @param queryContext
	 * @param configuration
	 * @return
	 */
	private boolean _checkTemperature(QueryContext queryContext, JSONObject configuration) {

		Double temperature = (Double)queryContext.getParameter(
				WeatherParameterNames.TEMPERATURE);

		Double matchValue = configuration.getDouble(ClauseConfigurationKeys.MATCH_VALUE);

		String matchType = configuration.getString(
				ClauseConfigurationKeys.MATCH_TYPE);

		if (temperature == null || matchValue == null) {

			if (_log.isWarnEnabled()) {
				_log.warn("Cannot process the property condition. Check configuration");
				_log.warn("Temperature in query context: " + temperature);
				_log.warn("Matchvalues: " + matchValue);
				_log.warn("Matchtype: " + matchType);
			}
			return false;
		}

		if (_log.isDebugEnabled()) {
			_log.warn("Checking temperature.");
			_log.warn("Matchvalue: " + matchValue);
			_log.warn("Temperature: " + temperature);
		}

		switch(matchType) {

			case ClauseConfigurationValues.MATCH_EQ: 
				return temperature == matchValue;
			case ClauseConfigurationValues.MATCH_LT: 
				return temperature < matchValue;
			case ClauseConfigurationValues.MATCH_GT: 
				return temperature > matchValue;
			case ClauseConfigurationValues.MATCH_NOT: 
				return temperature != matchValue;
			default:
				return false;
		}
	}
	
	/**
	 * Checks "weather" condition.
	 * 
	 * @param queryContext
	 * @param configuration
	 * @return
	 */
	private boolean _checkWeather(QueryContext queryContext, JSONObject configuration) {

		String weather = (String)queryContext.getParameter(
				WeatherParameterNames.WEATHER);
		
		JSONArray matchValues = configuration.getJSONArray(
				ClauseConfigurationKeys.MATCH_VALUES);

		String matchType = configuration.getString(
				ClauseConfigurationKeys.MATCH_TYPE);

		if (Validator.isBlank(weather) || matchValues == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("Cannot process the weather property condition. Check configuration");
				_log.warn("Weather in query context: " + weather);
				_log.warn("Matchvalues: " + (matchValues != null ? matchValues.length() : 0));
				_log.warn("Matchtype: " + matchType);
			}
			return false;
		}
		
		int matchCount = 0;

		for (int i = 0; i < matchValues.length(); i++) {
			String matchValue = matchValues.getString(i);

			if (weather.equalsIgnoreCase(matchValue)) {
				matchCount++;
			}
		}

		if (matchCount > 0) {
			if (matchType.equalsIgnoreCase(ClauseConfigurationValues.MATCH_ANY)) {
				return true;
			} else if (matchType.equalsIgnoreCase( ClauseConfigurationValues.MATCH_NOT)) {
				return false;
			}
		}
		else if (matchValues.length() > 0 
				&& matchType.equalsIgnoreCase(ClauseConfigurationValues.MATCH_NOT)) {
			return true;
		}
		
		return false;		
	}
	
	private static final String _HANDLER_NAME = "weather";

	private static final Logger _log = LoggerFactory.getLogger(
		WeatherClauseConditionHandler.class);

	private volatile ModuleConfiguration _moduleConfiguration;

}