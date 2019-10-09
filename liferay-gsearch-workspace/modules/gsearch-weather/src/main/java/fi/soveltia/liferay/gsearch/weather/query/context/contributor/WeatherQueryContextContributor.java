package fi.soveltia.liferay.gsearch.weather.query.context.contributor;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.context.contributor.QueryContextContributor;
import fi.soveltia.liferay.gsearch.weather.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.weather.constants.Weather;
import fi.soveltia.liferay.gsearch.weather.constants.WeatherConfigurationVariables;
import fi.soveltia.liferay.gsearch.weather.constants.WeatherParameterNames;
import fi.soveltia.liferay.gsearch.weather.service.api.WeatherService;

/**
 * Adds weather data to querycontext.
 *
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.weather.configuration.ModuleConfiguration",
	immediate = true, 
	service = QueryContextContributor.class
)
public class WeatherQueryContextContributor implements QueryContextContributor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void contribute(QueryContext queryContext) throws Exception {

		String ipAddress = _getIpAddress(queryContext);

		if (!_moduleConfiguration.isEnabled()) {
			return;
		}
		
		JSONObject weatherData = _weatherService.getWEatherData(ipAddress);
		

		if (weatherData == null) {
			return;
		}

		_contribute(queryContext, weatherData);
	}

	@Override
	public String getName() {
		return _NAME;
	}
	
	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}

	/**
	 * Adds configuration variables and parameters to query context.
	 *
	 * @param queryContext
	 * @param weatherData
	 */
	private void _contribute(
		QueryContext queryContext, JSONObject weatherData) {

		String weather = _getWeather(weatherData);
		
		// Configuration variables.
		
		queryContext.addConfigurationVariable(
			WeatherConfigurationVariables.WEATHER, weather);

		// Context parameters.
		
		queryContext.setParameter(
				WeatherParameterNames.TEMPERATURE, _getTemperature(weatherData));

		queryContext.setParameter(
				WeatherParameterNames.WEATHER, weather);
	}


	/**
	 * Gets IP address of the current user.
	 *
	 * @param queryContext
	 * @return
	 */
	private String _getIpAddress(QueryContext queryContext) {

		// Check if there's a configured test address

		String ipAddress = _moduleConfiguration.testIpAddress();

		if (!Validator.isBlank(ipAddress)) {
			return ipAddress;
		}

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)queryContext.getParameter(
				ParameterNames.HTTP_SERVLET_REQUEST);

		return httpServletRequest.getRemoteAddr();
	}

	/**
	 * Get temperature from weather data.
	 *
	 * @param weatherData
	 * @return
	 */
	private Double _getTemperature(JSONObject weatherData) {

		Double value = weatherData.getJSONObject("main").getDouble("temp");
		
		if (_log.isDebugEnabled()) {
			_log.debug("Temperature: " + value);
		}

		return value;
	}
	
	/**
	 * Get weather from weather data as clear text.
	 *
	 * @param weatherData
	 * @return
	 */
	private String _getWeather(JSONObject weatherData) {

		String value = null;

		int weatherCode = weatherData.getJSONArray(
			"weather"
		).getJSONObject(
			0
		).getInt(
			"id"
		);

		if (_log.isDebugEnabled()) {
			_log.debug("Weathercode: " + weatherCode);
		}
		
		Weather weather = Weather.getWeatherDefinition(weatherCode);

		if (weather != null) {
			value = weather.name().toLowerCase();
		}

		return value;
	}

	private static final Logger _log = LoggerFactory.getLogger(
		WeatherQueryContextContributor.class);

	private static final String _NAME = "weather";

	private volatile ModuleConfiguration _moduleConfiguration;

	@Reference
	private WeatherService _weatherService;

}