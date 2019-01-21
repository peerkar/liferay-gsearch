
package fi.soveltia.liferay.gsearch.geolocation.service;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.SingleVMPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;
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

import fi.soveltia.liferay.gsearch.geolocation.configuration.ModuleConfiguration;

/**
 * Geolocation service implementation using IPStack https://ipstack.com/
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.geolocation.configuration.ModuleConfiguration", 
	immediate = true, 
	service = GeoLocationService.class
)
public class GeoLocationServiceImpl implements GeoLocationService {

	/**
	 * {@inheritDoc}
	 * 
	 * @throws JSONException
	 */
	@Override
	public Float[] getCoordinates(String ipAddress)
		throws IOException, JSONException {

		String apiKey = _moduleConfiguration.IPStackApiKey();

		if (Validator.isNull(apiKey)) {
			_log.warn("IPStack API key not provided.");
		}

		// Don't try to resolve private addresses.
		
		if (!isPublicAddress(ipAddress) ) {
			
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Ipaddress " + ipAddress +
						" seems to be a local address. Returning null.");
			}

			return null;
		}
		
		// Try to get value from cache first.

		Float[] coordinates = _portalCache.get(ipAddress);

		if (coordinates != null) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Ipaddress " + ipAddress +
						" found from cache.");
			}
			return coordinates;
		}

		// Resolve using IPStack.
		
		StringBundler sb = new StringBundler();
		
		sb.append(IPSTACK_API_URL).append("/").append(ipAddress).append(
			"?access_key=").append(apiKey);

		String locationData = HttpUtil.URLtoString(sb.toString());

		if (locationData == null) {
			return null;
		}

		JSONObject json = JSONFactoryUtil.createJSONObject(locationData);

		if (json.get("latitude") == null) {
			_log.warn("There seems to be a problem with IPStack API key.");
			_log.warn("Calling URL: " + sb.toString());
			_log.warn("Response:" + locationData);
			return null;
		}

		coordinates = new Float[2];
		coordinates[0] = Float.valueOf(json.getString("latitude"));
		coordinates[1] = Float.valueOf(json.getString("longitude"));

		// Put to cache

		if (coordinates != null) {
			_portalCache.put(ipAddress, coordinates, CACHE_TIMEOUT);
		}

		return coordinates;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {

		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);

	}

	/**
	 * Check if the provided IP is a public one so that we won't make
	 * unnecessary calls to the resolver service. Thanks to
	 * https://codereview.stackexchange.com/questions/65071/test-if-given-ip-is-a-public-one
	 * 
	 * @param ipAddress
	 * @return
	 */
	private boolean isPublicAddress(String ipAddress) {

		Inet4Address address;
		try {
			address = (Inet4Address) InetAddress.getByName(ipAddress);
		}
		catch (UnknownHostException exception) {
			return false;
		}

	    return !(address.isSiteLocalAddress() || 
	             address.isAnyLocalAddress()  || 
	             address.isLinkLocalAddress() || 
	             address.isLoopbackAddress() || 
	             address.isMulticastAddress());		
	}

	@SuppressWarnings("unchecked")
	@Reference(unbind = "-")
	private void setSingleVMPool(SingleVMPool singleVMPool) {

		_portalCache =
			(PortalCache<String, Float[]>) singleVMPool.getPortalCache(
				GeoLocationServiceImpl.class.getName());
	}

	private static final String IPSTACK_API_URL = "http://api.ipstack.com/";

	// Default cache timeout in seconds. Could be practically eternal. 
	// Set to 1 week.

	private static final int CACHE_TIMEOUT = 604800;

	protected volatile ModuleConfiguration _moduleConfiguration;

	private static final Logger _log =
		LoggerFactory.getLogger(GeoLocationServiceImpl.class);

	private PortalCache<String, Float[]> _portalCache;
}
