
package fi.soveltia.liferay.gsearch.geolocation.service.impl;

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
import fi.soveltia.liferay.gsearch.geolocation.service.api.GeoLocationService;

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
	 */
	@Override
	public JSONObject getLocationData(String ipAddress)
		throws IOException, JSONException {

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
				_log.debug("Ipaddress " + ipAddress + " found from cache.");
			}

			return data;
		}

		// Resolve using IPStack.

		StringBundler sb = new StringBundler();

		sb.append(apiUrl);
		sb.append("/");
		sb.append(ipAddress);
		sb.append("?access_key=");
		sb.append(apiKey);

		String rawData = HttpUtil.URLtoString(sb.toString());

		if (rawData == null) {
			return null;
		}

		data = JSONFactoryUtil.createJSONObject(rawData);

		if (data.get("latitude") == null) {
			_log.warn("There was an error in fetching the location data. Check the configuration.");
			_log.warn("Calling URL: " + sb.toString());
			_log.warn("Response:" + data);

			return null;
		}

		// Put to cache.

		if (data != null) {
			_portalCache.put(
				ipAddress, data,
				_moduleConfiguration.ipResolverCacheTimeout());
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
	 * Checks if the provided IP is a public one so that we won't make
	 * unnecessary calls to the resolver service. Thanks to
	 * https://codereview.stackexchange.com/questions/65071/test-if-given-ip-is-a-public-one
	 *
	 * @param ipAddress
	 * @return
	 */
	private boolean _isPublicAddress(String ipAddress) {

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

	@Reference(unbind="-")
	@SuppressWarnings("unchecked")
	private void setSingleVMPool(SingleVMPool singleVMPool) {
		_portalCache =
			(PortalCache<String, JSONObject>)singleVMPool.getPortalCache(
				GeoLocationServiceImpl.class.getName());
	}

	private static final Logger _log = LoggerFactory.getLogger(
		GeoLocationServiceImpl.class);

	private volatile ModuleConfiguration _moduleConfiguration;
	private PortalCache<String, JSONObject> _portalCache;

}