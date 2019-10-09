package fi.soveltia.liferay.gsearch.weather.service.api;

import com.liferay.portal.kernel.json.JSONObject;

/**
 * Weather service for resolving weather at the given location (IP address).
 *
 * @author Petteri Karttunen
 */
public interface WeatherService {

	/**
	 * Gets weather data for the given IP address.
	 *
	 * @param ipAddress
	 * @return
	 * @throws Exception
	 */
	public JSONObject getWEatherData(String ipAddress) throws Exception;

}