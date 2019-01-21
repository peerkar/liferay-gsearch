
package fi.soveltia.liferay.gsearch.geolocation.query.contributor;

/**
 * Decay function type.
 * 
 * See https://www.elastic.co/guide/en/elasticsearch/guide/current/decay-functions.html
 * 
 * @author Petteri Karttunen
 *
 */
public enum DecayFunction {
	gauss, linear, exp
}
