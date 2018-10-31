
package fi.soveltia.liferay.gsearch.querycontributor.geolocation;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.query.contributor.QueryContributor;
import fi.soveltia.liferay.gsearch.query.DecayFunctionScoreQuery;
import fi.soveltia.liferay.gsearch.querycontributor.geolocation.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.querycontributor.geolocation.service.GeoLocationService;

/**
 * Geolocation query contributor.
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.querycontributor.geolocation.configuration.ModuleConfiguration", 
	immediate = true, 
	service = QueryContributor.class
)
public class GeolocationQueryContributor implements QueryContributor {

	@Override
	public Query buildQuery(PortletRequest portletRequest)
		throws Exception {

		if (!isEnabled()) {
			return null;
		}

		String ipAddress = getIpAddress(portletRequest);

		Float[]coordinates = _geolocationService.getCoordinates(ipAddress);
		
		if (coordinates == null) {
			return null;
		}

		float latitude = coordinates[0];
		float longitude = coordinates[1];

		if (_log.isDebugEnabled()) {
			_log.debug("Current user's ip: " + ipAddress);
			_log.debug("Latitude " + latitude);
			_log.debug("Longitude " + longitude);
		}

		// Please see fi.soveltia.liferay.gsearch.query.DecayFunctionScoreQuery

		DecayFunctionScoreQuery query = new DecayFunctionScoreQuery(null);

		query.setFieldName(_moduleConfiguration.indexField());
		query.setFunctionType(_moduleConfiguration.functionType().name());
		query.setDecay(new Double(_moduleConfiguration.decay()));
		query.setWeight(_moduleConfiguration.weight()); 
		query.setBoost(_moduleConfiguration.boost());

		// Origin

		Map<String, Object> origin = new HashMap<String, Object>();
		origin.put("lat", latitude);
		origin.put("lon", longitude);
		query.setOrigin(origin);

		query.setScale(_moduleConfiguration.scale());
		query.setOffset(_moduleConfiguration.offset());

		return query;
	}

	@Override
	public BooleanClauseOccur getOccur() {

		return BooleanClauseOccur.SHOULD;
	}

	@Override
	public boolean isEnabled() {

		return _moduleConfiguration.enableGeolocation();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {

		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}

	/**
	 * Get ipaddress to extract the geolocation data from.
	 */
	protected String getIpAddress(PortletRequest portletRequest) {

		// Check if there's a configured test address

		String ipAddress = _moduleConfiguration.testIpAddress();

		if (Validator.isNotNull(ipAddress)) {
			return ipAddress;
		}

		// Get the ip

		return _portal.getHttpServletRequest(portletRequest).getRemoteAddr();

	}

	protected volatile ModuleConfiguration _moduleConfiguration;

	@Reference
	Portal _portal;

	@Reference
	private GeoLocationService _geolocationService;
	
	private static final Log _log =
		LogFactoryUtil.getLog(GeolocationQueryContributor.class);

}
