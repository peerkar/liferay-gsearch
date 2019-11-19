package fi.soveltia.liferay.gsearch.geolocation.clause.condition;

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
import fi.soveltia.liferay.gsearch.geolocation.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.geolocation.constants.GeolocationParameterNames;

/**
 * Processes "geolocation" clause condition.
 *
 * Tries to resolve the give property from the location JSON.
 * Currently supports only first level, equalilty comparison only and singular values. 
 * 
 * If both 'match_value' and 'match_values' are given in the condition,
 * the singular match value takes precendence.
 *
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.geolocation.configuration.ModuleConfiguration",
	immediate = true, 
	service = ClauseConditionHandler.class
)
public class GeolocationClauseConditionHandler implements ClauseConditionHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canProcess(String handlerName) {
		return handlerName.equals(_HANDLER_NAME) &&
				_moduleConfiguration.isEnabled();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isTrue(QueryContext queryContext, JSONObject configuration)
		throws Exception {

		String matchProperty = configuration.getString(ClauseConfigurationKeys.MATCH_PROPERTY);
		String matchType = configuration.getString(ClauseConfigurationKeys.MATCH_TYPE);
		Object matchValue = configuration.get(ClauseConfigurationKeys.MATCH_VALUE);
		JSONArray matchValues = configuration.getJSONArray(ClauseConfigurationKeys.MATCH_VALUES);	
				
		if (Validator.isBlank(matchProperty) || Validator.isBlank(matchType) || 
				(matchValue == null & matchValues == null)) {

			if (_log.isWarnEnabled()) {
				_log.warn(
						"Cannot process the clause condition.");
				_log.warn("Match property:" + matchProperty);
				_log.warn("Match type:" + matchType);
				_log.warn("Match value:" + matchValue);
				_log.warn("Match values:" + matchValues);
			}
		}

		JSONObject locationData = (JSONObject)queryContext.getParameter(
				GeolocationParameterNames.GEOLOCATION_DATA);
		
		if (locationData == null) {
			_log.warn("Geolocation data is null. Cannot process clause condition.");
			return false;
		}
		
		if (_log.isDebugEnabled()) {
			_log.debug("Current user has the geolocation data: " + locationData.toString());
		}

		
		Object dataValue = locationData.get(matchProperty);		
		
		if (dataValue == null) {
			
			if (_log.isWarnEnabled()) {
				_log.warn("The match property " + matchProperty + 
						" is not present in the location data.");
			}
			
			return false;
		}
		
		if (matchValue != null) {
			return _processSingleMatchValue(matchValue, dataValue, matchType);
		} else {
			return _processMultiMatchValue(matchValues, dataValue, matchType);
		}
	}
	
	/**
	 * Processes multi match values comparison.
	 * 
	 * @param matchValue
	 * @param dataValue
	 * @param matchType
	 * @return
	 */
	private boolean _processMultiMatchValue(JSONArray matchValues, 
			Object dataValue, String matchType) {
		
		int matchCount = 0;

		for (Object obj : matchValues) {
			
			if (dataValue instanceof String) {
				
				String data = (String) dataValue;
				String match = (String) obj;
				
				if (data.equalsIgnoreCase(match)) {
					matchCount++;
					continue;
				}
				
			} else if (double.class.isAssignableFrom(dataValue.getClass()) || 
					Double.class.isAssignableFrom(dataValue.getClass())) {
				
				Double data = (Double) dataValue;
				Double match = (Double) obj;

				if (data == match) {
					matchCount++;
					continue;
				}
				
			} else if (boolean.class.isAssignableFrom(dataValue.getClass()) || 
					Boolean.class.isAssignableFrom(dataValue.getClass())) {

				Boolean data = (Boolean) dataValue;
				Boolean match = (Boolean) obj;
				
				if (data == match) {
					matchCount++;
					continue;
				}
			}
		}

		if (matchCount > 0) {
			if (matchType.equalsIgnoreCase( ClauseConfigurationValues.MATCH_ANY)) {
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
	
	/**
	 * Processes single match value comparison.
	 * 
	 * @param matchValue
	 * @param dataValue
	 * @param matchType
	 * @return
	 */
	private boolean _processSingleMatchValue(Object matchValue, Object dataValue, String matchType) {
		
		if (dataValue instanceof String) {
			
			String data = (String) dataValue;
			String match = (String) matchValue;
			
			if (match.equalsIgnoreCase(data)) {
				return true;
			}
			
		} else if (double.class.isAssignableFrom(dataValue.getClass()) || 
				Double.class.isAssignableFrom(dataValue.getClass())) {
			
			Double data = (Double) dataValue;
			Double match = (Double) matchValue;
			
			switch(matchType) {

				case ClauseConfigurationValues.MATCH_EQ: 
					return match == data;
				case ClauseConfigurationValues.MATCH_LT: 
					return match < data;
				case ClauseConfigurationValues.MATCH_GT: 
					return match > data;
				case ClauseConfigurationValues.MATCH_NOT: 
					return match !=  data;
				default:
					return false;
			}
			
		} else if (boolean.class.isAssignableFrom(dataValue.getClass()) || 
				Boolean.class.isAssignableFrom(dataValue.getClass())) {

			Boolean data = (Boolean) dataValue;
			Boolean match = (Boolean) matchValue;
			
			if (match == data) {
				return true;
			}
		}
		return false;
	}
	
	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}
	
	private static final String _HANDLER_NAME = "geolocation";

	private static final Logger _log = LoggerFactory.getLogger(
			GeolocationClauseConditionHandler.class);

	private volatile ModuleConfiguration _moduleConfiguration;
}