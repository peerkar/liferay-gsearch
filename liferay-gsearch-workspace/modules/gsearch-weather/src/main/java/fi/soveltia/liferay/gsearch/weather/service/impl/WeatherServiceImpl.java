package fi.soveltia.liferay.gsearch.weather.service.impl;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.SingleVMPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.geolocation.service.api.GeoLocationService;
import fi.soveltia.liferay.gsearch.weather.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.weather.service.api.WeatherService;

/**
 * OpenWeatherMap weather servie implementation.
 *
 * Sample data: https://samples.openweathermap.org/data/2.5/weather?lat=35&lon=139&appid=b6907d289e10d714a6e88b30761fae22
 *
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.weather.configuration.ModuleConfiguration",
	immediate = true, 
	service = WeatherService.class
)
public class WeatherServiceImpl implements WeatherService {

	/**
	 * {@inheritDoc}
	 */
	public JSONObject getWEatherData(String ipAddress) throws Exception {

		if (!_isPublicAddress(ipAddress)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Ipaddress " + ipAddress +
						" seems to be a local address. No data available.");
			}			
			return null;
		}
		
		String apiKey = _moduleConfiguration.apiKey();
		String apiUrl = _moduleConfiguration.apiUrl();

		if (Validator.isBlank(apiKey) || Validator.isBlank(apiUrl)) {
			_log.warn("API key or url not provided.");
		}

		// Try to get data from cache first.

		JSONObject data = _portalCache.get(ipAddress);

		if (data != null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Data for ipaddress " + ipAddress + " found from cache.");
			}

			return data;
		}

		// Fetch location data using the Geolocation module.

		JSONObject locationData = _geoLocationService.getLocationData(
			ipAddress);

		String latitude = locationData.getString("latitude");
		String longitude = locationData.getString("longitude");

		StringBundler sb = new StringBundler();
		
		sb.append(_moduleConfiguration.apiUrl());
		sb.append("?lat=");
		sb.append(latitude);
		sb.append("&lon=");
		sb.append(longitude);
		sb.append("&units=metric");
		sb.append("&format=json");
		sb.append("&APPID=" + apiKey);

		String rawData = HttpUtil.URLtoString(sb.toString());

		if (rawData == null) {
			return null;
		}

		data = JSONFactoryUtil.createJSONObject(rawData);

		if (data.get("weather") == null) {
			_log.warn("There was an error in fetching the weather data.");
			_log.warn("Calling URL: " + sb.toString());
			_log.warn("Response:" + data);

			return null;
		}

		// Put to cache.

		if (data != null) {
			_portalCache.put(
				ipAddress, data, _moduleConfiguration.cacheTimeout());
		}

		return data;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}

	/**
	 * Checks if the provided IP is a public one.
	 *
	 * We won't make unnecessary calls to the resolver service. Thanks to
	 * https://codereview.stackexchange.com/questions/65071/test-if-given-ip-is-a-public-one
	 * 
	 * @param ipAddress
	 * @return
	 * @throws Exception
	 */
	private boolean _isPublicAddress(String ipAddress) throws Exception {
		Inet4Address address;

		try {
			address = (Inet4Address)InetAddress.getByName(ipAddress);
		}
		catch (UnknownHostException uhe) {
			return false;
		}

		if (!(address.isSiteLocalAddress() || address.isAnyLocalAddress() ||
			  address.isLinkLocalAddress() || address.isLoopbackAddress() ||
			  address.isMulticastAddress())) {

			return true;
		}

		return false;
	}	

	@Reference(unbind = "-")
	@SuppressWarnings("unchecked")
	private void setSingleVMPool(SingleVMPool singleVMPool) {
		_portalCache =
			(PortalCache<String, JSONObject>)singleVMPool.getPortalCache(
				WeatherServiceImpl.class.getName());
	}

	private static final Logger _log = LoggerFactory.getLogger(
		WeatherServiceImpl.class);

	private volatile ModuleConfiguration _moduleConfiguration;
	private PortalCache<String, JSONObject> _portalCache;

	@Reference
	private GeoLocationService _geoLocationService;

}