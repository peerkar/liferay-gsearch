package fi.soveltia.liferay.gsearch.opennlp.query.clause.condition;

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
import fi.soveltia.liferay.gsearch.opennlp.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.opennlp.constants.OpenNlpClauseConditionProperties;
import fi.soveltia.liferay.gsearch.opennlp.constants.OpenNlpParameterNames;

/**
 * Processes "weather" clause condition.
 *
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.opennlp.configuration.ModuleConfiguration",
	immediate = true, 
	service = ClauseConditionHandler.class
)
public class OpenNlpClauseCondition implements ClauseConditionHandler {

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
		
		JSONArray matchValues = configuration.getJSONArray(
				ClauseConfigurationKeys.MATCH_VALUES);

		JSONObject metadata = 
				(JSONObject)queryContext.getParameter(OpenNlpParameterNames.OPEN_NLP_DATA);

		if (Validator.isBlank(matchProperty) || Validator.isBlank(matchType) ||
				metadata == null || matchValues == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Cannot process the clause condition.");
				_log.warn("Match property:" + matchProperty);
				_log.warn("Match type:" + matchType);
				_log.warn("Match values:" + matchValues.length());
				_log.warn("Data:" + metadata);
			}
		}
		
		matchProperty = matchProperty.toLowerCase();

		switch(matchProperty) {
	
			case OpenNlpClauseConditionProperties.DATE: 

				return _checkCondition(metadata, matchValues, "dates", matchType);
				
			case OpenNlpClauseConditionProperties.LOCATION: 

				return _checkCondition(metadata, matchValues, "locations", matchType);

			case OpenNlpClauseConditionProperties.PERSON: 

				return _checkCondition(metadata, matchValues, "persons", matchType);

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

	private boolean _checkCondition (JSONObject metadata, JSONArray matchValues, String key, String matchType) {
		
		try {
			JSONObject entities = _getEntitiesObject(metadata);
		
			if (entities != null) {
				
				JSONArray contextValues = entities.getJSONArray(key);
				
				if (contextValues == null) {
					return false;
				}
				
				return _checkMatch(contextValues, matchValues, matchType);
			}
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
		
		return false;
	}
	
	/**
	 * Check if there's a match.
	 * 
	 * @param contextValues
	 * @param matchValues
	 * @param matchType
	 * @return
	 */
	private boolean _checkMatch(JSONArray contextValues, JSONArray matchValues, String matchType) {
		
		int matchCount = 0;

		for (int i = 0; i < matchValues.length(); i++) {
			
			String contextValue = matchValues.getString(i);

			for (int j = 0; j < contextValues.length(); j++) {
				
				String matchValue = matchValues.getString(j);
				
				if (contextValue.equalsIgnoreCase(matchValue)) {
					matchCount++;
					continue;
				}
			}
		}

		if (matchCount > 0) {
			if (matchType.equalsIgnoreCase(ClauseConfigurationValues.MATCH_ANY)) {
				return true;
			} else if (matchType.equalsIgnoreCase( ClauseConfigurationValues.MATCH_NOT)) {
				return false;
			}
		}
		else if (matchCount == matchValues.length()) {
			return true;
		}

		return false;		
	}
	
	/**
	 * Gets the entities object.
	 * 
	 * @param metadata
	 * @return
	 * @throws Exception
	 */
	private JSONObject _getEntitiesObject(JSONObject metadata) 
			throws Exception {
		
		return metadata.getJSONArray("docs").
				getJSONObject(0).getJSONObject("doc").getJSONObject("_source").
				getJSONObject("entities");
		
	}

	private static final String _HANDLER_NAME = "open_nlp";

	private static final Logger _log = LoggerFactory.getLogger(
		OpenNlpClauseCondition.class);

	private volatile ModuleConfiguration _moduleConfiguration;

}