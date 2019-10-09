package fi.soveltia.liferay.gsearch.weather.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

@ExtendedObjectClassDefinition(category = "gsearch")
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.weather.configuration.ModuleConfiguration",
	localization = "content/Language", 
	name = "weather-configuration"
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
		deflt = "http://api.openweathermap.org/data/2.5/weather",
		description = "api-url-desc", 
		name = "api-url-name", 
		required = false
	)
	public String apiUrl();

	@Meta.AD(
		deflt = "604800", 
		description = "cache-timeout-desc",
		name = "cache-timeout-name", 
		required = false
	)
	public int cacheTimeout();

}