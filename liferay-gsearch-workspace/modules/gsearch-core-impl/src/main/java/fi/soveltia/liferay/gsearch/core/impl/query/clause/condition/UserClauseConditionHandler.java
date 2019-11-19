package fi.soveltia.liferay.gsearch.core.impl.query.clause.condition;


import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationValues;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.constants.UserClauseConditionProperties;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseConditionHandler;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Processes current user clause condition.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ClauseConditionHandler.class
)
public class UserClauseConditionHandler implements ClauseConditionHandler {

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

			case UserClauseConditionProperties.AGE: 
				return _checkAge(queryContext, configuration);
			case UserClauseConditionProperties.EMAIL: 
				return _checkEmail(queryContext, configuration);
			case UserClauseConditionProperties.GENDER: 
				return _checkGender(queryContext, configuration);
			case UserClauseConditionProperties.GROUP_IDS: 
				return _checkGroupIds(queryContext, configuration);
			case UserClauseConditionProperties.JOB_TITLE: 
				return _checkJobTitle(queryContext, configuration);
			case UserClauseConditionProperties.LANGUAGE_ID: 
				return _checkLanguageId(queryContext, configuration);
			case UserClauseConditionProperties.ROLE_IDS: 
				return _checkRoleIds(queryContext, configuration);
			default:
				return false;
		}
	}

	/**
	 * Checks age condition.
	 * 
	 * @param queryContext
	 * @param configuration
	 * @return
	 */
	private boolean _checkAge(QueryContext queryContext, JSONObject configuration) {
		
		Integer age = (Integer)queryContext.getParameter(ParameterNames.USER_AGE);
		
		String matchType = configuration.getString(
				ClauseConfigurationKeys.MATCH_TYPE);

		int matchValue = configuration.getInt(
				ClauseConfigurationKeys.MATCH_VALUE);
		
		if (age == null || matchValue < 0) {
			if (_log.isWarnEnabled()) {
				_log.warn("Cannot process the property condition. Check configuration");
				_log.warn("Matchvalue: " + matchValue);
				_log.warn("Matchtype: " + matchType);
				_log.warn("Age: " + age);
			}
			return false;
		}

		if (_log.isDebugEnabled()) {
			_log.warn("Checking age.");
			_log.warn("Matchvalue: " + matchValue);
			_log.warn("Age: " + age);
		}
				
		switch(matchType) {

			case ClauseConfigurationValues.MATCH_EQ: 
				return age == matchValue ;
			case ClauseConfigurationValues.MATCH_LT: 
				return age < matchValue;
			case ClauseConfigurationValues.MATCH_GT: 
				return age > matchValue;
			case ClauseConfigurationValues.MATCH_NOT: 
				return age != matchValue;
			default:
				return false;
		}
	}
		
	/**
	 * Checks email condition.
	 * 
	 * @param queryContext
	 * @param configuration
	 * @return
	 */
	private boolean _checkEmail(QueryContext queryContext, JSONObject configuration) {
		
		User user = (User)queryContext.getParameter(ParameterNames.USER);

		String email = user.getEmailAddress();
		
		String matchType = configuration.getString(
				ClauseConfigurationKeys.MATCH_TYPE);

		String matchValue = configuration.getString(
				ClauseConfigurationKeys.MATCH_VALUE);
		
		if (Validator.isBlank(matchValue)) {
			if (_log.isWarnEnabled()) {
				_log.warn("Cannot process the property condition. Check configuration");
				_log.warn("Matchvalue: " + matchValue);
				_log.warn("Matchtype: " + matchType);
				_log.warn("Email: " + email);
			}
			return false;
		}
		
		switch(matchType) {

			case ClauseConfigurationValues.MATCH_CONTAINS: 
				return email.contains(matchValue);
			case ClauseConfigurationValues.MATCH_EQ: 
				return email.equalsIgnoreCase(matchValue);
			case ClauseConfigurationValues.MATCH_NOT: 
				return !email.equalsIgnoreCase(matchValue);
			default:
				return false;
		}		
	}
	
	/**
	 * Checks gender condition.
	 * 
	 * @param queryContext
	 * @param configuration
	 * @return
	 */
	private boolean _checkGender(QueryContext queryContext, JSONObject configuration) {
		
		String gender = (String)queryContext.getParameter(ParameterNames.USER_GENDER);

		String matchType = configuration.getString(
				ClauseConfigurationKeys.MATCH_TYPE);

		String matchValue = configuration.getString(
				ClauseConfigurationKeys.MATCH_VALUE);
		
		if (Validator.isBlank(gender) || Validator.isBlank(matchValue)) {
			if (_log.isWarnEnabled()) {
				_log.warn("Cannot process the property condition. Check configuration");
				_log.warn("Matchvalue: " + matchValue);
				_log.warn("Matchtype: " + matchType);
				_log.warn("Gender: " + gender);
			}
			return false;
		}

		if (_log.isDebugEnabled()) {
			_log.warn("Checking gender.");
			_log.warn("Matchvalue: " + matchValue);
			_log.warn("Gender: " + gender);
		}
		
		switch(matchType) {

			case ClauseConfigurationValues.MATCH_EQ: 
				return gender.equalsIgnoreCase(matchValue);
			case ClauseConfigurationValues.MATCH_NOT: 
				return !gender.equalsIgnoreCase(matchValue);
			default:
				return false;
		}		
	}	

	/**
	 * Check group ids condition.
	 * 
	 * @param queryContext
	 * @param configuration
	 * @return
	 */
	private boolean _checkGroupIds(QueryContext queryContext, JSONObject configuration) {

		User user = (User)queryContext.getParameter(ParameterNames.USER);

		long[] groupIds = user.getGroupIds();
		
		String matchType = configuration.getString(
				ClauseConfigurationKeys.MATCH_TYPE);

		JSONArray matchValues = configuration.getJSONArray(
				ClauseConfigurationKeys.MATCH_VALUES);

		if (groupIds == null || matchValues == null || matchValues.length() == 0) {
			if (_log.isWarnEnabled()) {
				_log.warn("Cannot process the property condition. Check configuration");
				_log.warn("Group ids: " + (groupIds != null ? groupIds.length : 0));
				_log.warn("Matchvalues: " + (matchValues != null ? matchValues.length() : 0));
				_log.warn("Matchtype: " + matchType);
			}
			return false;
		}		
				
		int matchCount = 0;

		for (int i = 0; i < matchValues.length(); i++) {
			long matchId = matchValues.getLong(i);

			for (long groupId : groupIds) {
				
				if (matchId == groupId) {
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
		else if (matchCount == matchValues.length()) {
			return true;
		}

		return false;	
	}	

	/**
	 * Checks job title condition.
	 * 
	 * @param queryContext
	 * @param configuration
	 * @return
	 */
	private boolean _checkJobTitle(QueryContext queryContext, JSONObject configuration) {

		User user = (User)queryContext.getParameter(ParameterNames.USER);

		String jobTitle = user.getJobTitle();

		String matchType = configuration.getString(
				ClauseConfigurationKeys.MATCH_TYPE);

		String matchValue = configuration.getString(
				ClauseConfigurationKeys.MATCH_VALUE);
		
		if (Validator.isBlank(jobTitle) || Validator.isBlank(matchValue)) {
			if (_log.isWarnEnabled()) {
				_log.warn("Cannot process the property condition. Check configuration");
				_log.warn("Matchvalue: " + matchValue);
				_log.warn("Matchtype: " + matchType);
				_log.warn("Job title: " + jobTitle);
			}
			return false;
		}
		
		switch(matchType) {

			case ClauseConfigurationValues.MATCH_CONTAINS: 
				return jobTitle.contains(matchValue);
			case ClauseConfigurationValues.MATCH_EQ: 
				return jobTitle.equalsIgnoreCase(matchValue);
			case ClauseConfigurationValues.MATCH_NOT: 
				return !jobTitle.equalsIgnoreCase(matchValue);
			default:
				return false;
		}	}

	/**
	 * Checks language id condition.
	 * 
	 * @param queryContext
	 * @param configuration
	 * @return
	 */
	private boolean _checkLanguageId(QueryContext queryContext, JSONObject configuration) {

		User user = (User)queryContext.getParameter(ParameterNames.USER);

		String languageId = user.getLanguageId();

		String matchType = configuration.getString(
				ClauseConfigurationKeys.MATCH_TYPE);

		String matchValue = configuration.getString(
				ClauseConfigurationKeys.MATCH_VALUE);
		
		if (Validator.isBlank(languageId) || Validator.isBlank(matchValue)) {
			if (_log.isWarnEnabled()) {
				_log.warn("Cannot process the property condition. Check configuration");
				_log.warn("Matchvalue: " + matchValue);
				_log.warn("Matchtype: " + matchType);
				_log.warn("Language ID: " + languageId);
			}
			return false;
		}
		
		switch(matchType) {

			case ClauseConfigurationValues.MATCH_EQ: 
				return languageId.equalsIgnoreCase(matchValue);
			case ClauseConfigurationValues.MATCH_NOT: 
				return !languageId.equalsIgnoreCase(matchValue);
			default:
				return false;
		}
	}

	/**
	 * Checks role ids condition.
	 * 
	 * @param queryContext
	 * @param configuration
	 * @return
	 */
	private boolean _checkRoleIds(QueryContext queryContext, JSONObject configuration) {

		User user = (User)queryContext.getParameter(ParameterNames.USER);

		long[] roleIds = user.getRoleIds();
		
		String matchType = configuration.getString(
				ClauseConfigurationKeys.MATCH_TYPE);

		JSONArray matchValues = configuration.getJSONArray(
				ClauseConfigurationKeys.MATCH_VALUES);

		if (roleIds == null || matchValues == null || matchValues.length() == 0) {
			if (_log.isWarnEnabled()) {
				_log.warn("Cannot process the property condition. Check configuration");
				_log.warn("Group ids: " + (roleIds != null ? roleIds.length : 0));
				_log.warn("Matchvalues: " + (matchValues != null ? matchValues.length() : 0));
				_log.warn("Matchtype: " + matchType);
			}
			return false;
		}		
				
		int matchCount = 0;

		for (int i = 0; i < matchValues.length(); i++) {
			long matchId = matchValues.getLong(i);

			for (long roleId : roleIds) {
				
				if (matchId == roleId) {
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

	private static final String _HANDLER_NAME = "user";

	private static final Logger _log = LoggerFactory.getLogger(
		UserClauseConditionHandler.class);

}