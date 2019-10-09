package fi.soveltia.liferay.gsearch.core.impl.query.context.contributor;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.constants.ConfigurationVariables;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.constants.QueryContextContributorNames;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.context.contributor.QueryContextContributor;

/**
 * Adds current user properties to query context.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = QueryContextContributor.class
)
public class UserQueryContextContributor implements QueryContextContributor {

	public void contribute(QueryContext queryContext) throws Exception {

		_contribute(queryContext);
	}
	
	@Override
	public String getName() {
		return _NAME;
	}

	/**
	 * Adds configuration variables and parameters to query context.
	 * 
	 * User parameters don't need to be put in the context as
	 * they can be directly accessed from the User object.
	 * Some are still put there for the convenience and better performance.
	 * 
	 * @param queryContext
	 * @throws Exception
	 */
	private void _contribute(QueryContext queryContext) throws Exception {
		
		User user = (User)queryContext.getParameter(ParameterNames.USER);
		
		int age = _getAge(user.getBirthday());
		String gender = _getGender(user);
		
		// Configuration variables.
		
		queryContext.addConfigurationVariable(ConfigurationVariables.USER_AGE, String.valueOf(age));
		
		queryContext.addConfigurationVariable(ConfigurationVariables.USER_EMAIL,
				user.getEmailAddress());
		
		queryContext.addConfigurationVariable(ConfigurationVariables.USER_GENDER, gender);

		queryContext.addConfigurationVariable(ConfigurationVariables.USER_GROUP_IDS,
				_getGroupIds(user));
		
		queryContext.addConfigurationVariable(ConfigurationVariables.USER_JOB_TITLE,
				user.getJobTitle());

		queryContext.addConfigurationVariable(ConfigurationVariables.USER_LANGUAGE_ID,
				user.getLanguageId());

		queryContext.addConfigurationVariable(ConfigurationVariables.USER_ROLE_IDS,
				_getRoleIds(user));

		// Context parameters.
		
		queryContext.setParameter(ParameterNames.USER_AGE, age);
		queryContext.setParameter(ParameterNames.USER_GENDER, gender);
	}
		
	/**
	 * Gets user age.
	 * 
	 * @param birthday
	 * @return
	 */
	private int _getAge(Date birthday) {
		
		Date now = new Date();
		
	    DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
	    int d1 = Integer.parseInt(formatter.format(birthday));
	    int d2 = Integer.parseInt(formatter.format(now));
	    int age = (d2-d1)/10000;
	    
	    return age;
	}
	
	/**
	 * Gets user gender.
	 * 
	 * @param user
	 * @return
	 * @throws PortalException
	 */
	private String _getGender(User user) throws PortalException {
		
		if (user.isFemale()) {
			return "female";
		} else if (user.isMale()) {
			return "male";
		} else {
			return "other";
		}
	}

	/**
	 * Get user group ids.
	 * 
	 * @param user
	 * @return
	 */
	private String _getGroupIds(User user)  {

		return  Arrays.stream(user.getGroupIds())
		            .mapToObj(String::valueOf) 
		                 .collect(Collectors.joining(" "));
	}
	
	/**
	 * Gets user role ids.
	 * 
	 * @param user
	 * @return
	 */
	private String _getRoleIds(User user)  {
		
		return  Arrays.stream(user.getRoleIds())
	            .mapToObj(String::valueOf) 
	                 .collect(Collectors.joining(" "));
		
	}
	
	private static final String _NAME = 
			QueryContextContributorNames.USER;

}
