package fi.soveltia.liferay.gsearch.geolocation.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;
import fi.soveltia.liferay.gsearch.geolocation.query.contributor.DecayFunction;

/**
 * Geolocation Query Contributor configuration.
 * 
 * @author Petteri Karttunen
 *
 */
@ExtendedObjectClassDefinition(
	category = "GSearch"
)
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.geolocation.configuration.ModuleConfiguration",
	localization = "content/Language",
	name = "geolocation-configuration"
)
public interface ModuleConfiguration {

	@Meta.AD(
		deflt = "false", 
	    description = "enable-querycontributor-desc",
	    name = "enable-querycontributor-name",
		required = false
	)
	public boolean enableQueryContributor();

	@Meta.AD(
		deflt = "false", 
	    description = "enable-indexerpostprocessor-desc",
	    name = "enable-indexerpostprocessor-name",
		required = false
	)
	public boolean enableIndexerPostProcessor();
	
	@Meta.AD(
		deflt = "2000km", 
		description = "geolocation-scale-desc",
	    name = "geolocation-scale-name",
		required = false
	)
	public String scale();

	@Meta.AD(
		deflt = "1000km", 
		description = "geolocation-offset-desc",
	    name = "geolocation-offset-name",
		required = false
	)
	public String offset();
	
	@Meta.AD(
		deflt = "0.75", 
	    description = "geolocation-decay-desc",
	    name= "geolocation-decay-name",
		required = false
	)
	public double decay();

	@Meta.AD(
		deflt = "gauss", 
		description = "geolocation-decay-function-desc",
	    name = "geolocation-decay-function-name",
		required = false
	)
	public DecayFunction functionType();

	@Meta.AD(
		deflt = "2", 
	    description = "geolocation-boost-desc",
	    name= "geolocation-boost-name",
		required = false
	)
	public float boost();

	@Meta.AD(
		deflt = "2", 
	    description = "geolocation-score-weight-desc",
	    name= "geolocation-score-weight-name",
		required = false
	)
	public float weight();

	@Meta.AD(
		deflt = "gsearch_geolocation", 
		description = "geolocation-field-desc",
	    name = "geolocation-field-name",
		required = false
	)
	public String indexField();

	@Meta.AD(
		deflt = "", 
		description = "test-ip-desc",
	    name = "test-ip-name",
		required = false
	)
	public String testIpAddress();
	
	@Meta.AD(
		deflt = "", 
		description = "ipstack-api-key-desc",
	    name = "ipstack-api-key-name",
		required = false
	)
	public String IPStackApiKey();
}