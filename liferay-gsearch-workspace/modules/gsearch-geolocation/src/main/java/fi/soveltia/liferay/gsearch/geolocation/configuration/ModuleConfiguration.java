package fi.soveltia.liferay.gsearch.geolocation.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * Geolocation Query Contributor configuration.
 *
 * @author Petteri Karttunen
 *
 */
@ExtendedObjectClassDefinition(category = "gsearch")
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.geolocation.configuration.ModuleConfiguration",
	localization = "content/Language", 
	name = "geolocation-configuration"
)
public interface ModuleConfiguration {

	@Meta.AD(
		deflt = "false", 
		description = "is-enabled-desc",
		name = "is-enabled-name", 
		required = false
	)
	public boolean isEnabled();

	@Meta.AD(
		deflt = "", 
		description = "result-item-field-desc",
		name = "result-item-field-name", 
		required = false
	)
	public String resultItemField();

	@Meta.AD(
		deflt = "", 
		description = "test-ip-desc", 
		name = "test-ip-name",
		required = false
	)
	public String testIpAddress();

	@Meta.AD(
		deflt = "", 
		description = "api-key-desc",
		name = "api-key-name", 
		required = false
	)
	public String apiKey();

	@Meta.AD(
		deflt = "http://api.ipstack.com/", 
		description = "api-url-desc",
		name = "api-url-name", 
		required = false
	)
	public String apiUrl();

	@Meta.AD(
		deflt = "604800", 
		description = "ip-resolver-cache-desc",
		name = "ip-resolver-cache-name", 
		required = false
	)
	public int ipResolverCacheTimeout();

}