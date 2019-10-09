
package fi.soveltia.liferay.gsearch.geolocation.service.api;

import com.liferay.portal.kernel.json.JSONObject;

/**
 * Geolocation service for resolving user ip and coordinate.
 *
 * @author Petteri Karttunen
 */
public interface GeoLocationService {

	/**
	 * Gets location data for the given IP address.
	 *
	 * @param ipAddress
	 * @return
	 */
	public JSONObject getLocationData(String ipAddress) throws Exception;

}