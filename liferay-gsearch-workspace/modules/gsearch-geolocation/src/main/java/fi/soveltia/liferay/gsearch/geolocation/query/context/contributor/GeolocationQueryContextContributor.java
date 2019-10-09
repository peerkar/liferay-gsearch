package fi.soveltia.liferay.gsearch.geolocation.query.context.contributor;

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
import fi.soveltia.liferay.gsearch.geolocation.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.geolocation.constants.GeolocationConfigurationVariables;
import fi.soveltia.liferay.gsearch.geolocation.constants.GeolocationParameterNames;
import fi.soveltia.liferay.gsearch.geolocation.service.api.GeoLocationService;

/**
 * Adds user's location data to querycontext.
 *
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.geolocation.configuration.ModuleConfiguration",
	immediate = true, 
	service = QueryContextContributor.class
)
public class GeolocationQueryContextContributor
	implements QueryContextContributor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void contribute(QueryContext queryContext) throws Exception {

		if (!_moduleConfiguration.isEnabled()) {
			return;
		}

		String ipAddress = _getIpAddress(queryContext);

		JSONObject locationData = _geoLocationService.getLocationData(
			ipAddress);
		
		if (locationData == null) {
			_log.warn("User's location data could not be resolved.");

			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Current user's ip: " + ipAddress);
			_log.debug("Latitude " + locationData.getString("latitude"));
			_log.debug("Longitude " + locationData.getString("longitude"));
		}

		_addConfigurationVariables(queryContext, locationData);
		
		_addContextParameters(queryContext, locationData);
		
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
	 * Adds configuration variables. 
	 * 
	 * @param queryContext
	 * @param locationData
	 */
	private void _addConfigurationVariables(QueryContext queryContext, 
			JSONObject locationData) {

		queryContext.addConfigurationVariable(
			GeolocationConfigurationVariables.GEOLOCATION_CITY, 
			locationData.getString("city").toLowerCase());
		queryContext.addConfigurationVariable(
			GeolocationConfigurationVariables.GEOLOCATION_CONTINENT_NAME,
			locationData.getString("continent_name").toLowerCase());
		queryContext.addConfigurationVariable(
			GeolocationConfigurationVariables.GEOLOCATION_COUNTRY_NAME, 
			locationData.getString("country_name").toLowerCase());
		queryContext.addConfigurationVariable(
			GeolocationConfigurationVariables.GEOLOCATION_LATITUDE, 
			locationData.getString("latitude").toLowerCase());
		queryContext.addConfigurationVariable(
			GeolocationConfigurationVariables.GEOLOCATION_LONGITUDE, 
			locationData.getString("longitude").toLowerCase());
		
	}
	
	/**
	 * Add parameters to query context.
	 * 
	 * @param queryContext
	 * @param locationData
	 */
	private void _addContextParameters(QueryContext queryContext, JSONObject locationData) {

		queryContext.setParameter(
			GeolocationParameterNames.GEOLOCATION_DATA, locationData);

	}

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

	private static final Logger _log = LoggerFactory.getLogger(
			GeolocationQueryContextContributor.class);
	
	private static final String _NAME = "geolocation";

	@Reference
	private GeoLocationService _geoLocationService;

	private volatile ModuleConfiguration _moduleConfiguration;

}