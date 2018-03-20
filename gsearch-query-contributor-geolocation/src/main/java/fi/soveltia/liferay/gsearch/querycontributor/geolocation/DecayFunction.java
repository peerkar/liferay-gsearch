
package fi.soveltia.liferay.gsearch.querycontributor.geolocation;

/**
 * Decay function type
 * 
 * See https://www.elastic.co/guide/en/elasticsearch/guide/current/decay-functions.html
 * 
 * @author Petteri Karttuen
 *
 */
public enum DecayFunction {
	gauss, linear, exp
}
