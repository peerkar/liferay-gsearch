
package fi.soveltia.liferay.gsearch.geolocation.service;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

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

		Float[] coordinates = new Float[2];
		coordinates[0] = Float.valueOf(json.getString("latitude"));
		coordinates[1] = Float.valueOf(json.getString("longitude"));

		return coordinates;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {

		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}

	protected volatile ModuleConfiguration _moduleConfiguration;

	private static final String IPSTACK_API_URL = "http://api.ipstack.com/";

	private static final Log _log =
		LogFactoryUtil.getLog(GeoLocationServiceImpl.class);

}
