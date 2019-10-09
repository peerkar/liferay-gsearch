package fi.soveltia.liferay.gsearch.core.impl.query.context.contributor;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.constants.ConfigurationVariables;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.constants.QueryContextContributorNames;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.context.contributor.QueryContextContributor;
import fi.soveltia.liferay.gsearch.core.impl.util.GSearchUtil;

@Component(
	immediate = true, 
	service = QueryContextContributor.class
)
public class ContextQueryContextContributor implements QueryContextContributor {

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
	 * @param queryContext
	 * @throws Exception
	 */
	private void _contribute(QueryContext queryContext) throws Exception {

		User user = (User)queryContext.getParameter(ParameterNames.USER);

		String timeOfTheDay = _getTimeOfTheDay(user.getTimeZone());
		
		Locale locale = (Locale)queryContext.getParameter(ParameterNames.LOCALE);
		
		// Set configuration variables.
		
		queryContext.addConfigurationVariable(ConfigurationVariables.CONTEXT_COMPANY_ID,
				queryContext.getParameter(ParameterNames.COMPANY_ID).toString());

		queryContext.addConfigurationVariable(ConfigurationVariables.CONTEXT_KEYWORDS, 
				queryContext.getKeywords());

		queryContext.addConfigurationVariable(ConfigurationVariables.CONTEXT_TIME_OF_THE_DAY, 
				timeOfTheDay);
		
		queryContext.addConfigurationVariable(ConfigurationVariables.CONTEXT_LANGUAGE_ID,
				locale.toString());

		queryContext.addConfigurationVariable(ConfigurationVariables.CONTEXT_NOW_YYYY_MM_DD, 
				_NOW_YYYY_MM_DD.format(new Date()));

		// Set context parameters.
		
		queryContext.setParameter(ParameterNames.TIME_OF_THE_DAY,
				_getTimeOfTheDay(user.getTimeZone()));

		// Following variables are not present in headless request

		PortletRequest portletRequest = GSearchUtil.getPortletRequestFromContext(queryContext);

		if (portletRequest != null) {

			ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

			queryContext.addConfigurationVariable(ConfigurationVariables.CONTEXT_SCOPE_GROUP_ID,
					String.valueOf(themeDisplay.getScopeGroupId()));
			
			queryContext.setParameter(ParameterNames.SCOPE_GROUP_ID,
					String.valueOf(themeDisplay.getScopeGroupId()));
		}
	}
	
	/**
	 * Gets time of the day.
	 * 
	 * Possible values:
	 * 	-
	 * 
	 * @param timeZone
	 * @return
	 */
	private String _getTimeOfTheDay(TimeZone timeZone) {

		LocalTime now = LocalTime.now(timeZone.toZoneId());
		
		if (_isTimebetween(now, _MORNING, _AFTER_NOON)) {
			return "morning";
		} else if (_isTimebetween(now, _AFTER_NOON, _EVENING)) {
			return "afternoon";
		} else if (_isTimebetween(now, _EVENING, _NIGHT)) {
			return "evening";
		} else {
			return "night";
		}
	}

	/**
	 * Checks if time is between an interval.
	 * 
	 * @param time
	 * @param start
	 * @param end
	 * @return
	 */
	private boolean _isTimebetween(LocalTime time, LocalTime start, LocalTime end) {
		return (!time.isBefore(start)) && time.isBefore(end);
	}

	private static final LocalTime _MORNING = LocalTime.of(0, 0, 0);
	private static final LocalTime _AFTER_NOON = LocalTime.of(12, 0, 0);
	private static final LocalTime _EVENING = LocalTime.of(16, 0, 0);
	private static final LocalTime _NIGHT = LocalTime.of(21, 0, 0);

	private static final DateFormat _NOW_YYYY_MM_DD = new SimpleDateFormat(
			"yyyy-MM-dd");

	private static final String _NAME = 
			QueryContextContributorNames.CONTEXT;
	
}
