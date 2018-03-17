package fi.soveltia.liferay.gsearch.querycontributor.audiencetargeting.configuration;

import aQute.bnd.annotation.metatype.Meta;

/**
 * GSearch Audience Targeting module configuration.
 * 
 * @author Petteri Karttunen
 *
 */
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.querycontributor.audiencetargeting.configuration.GSearchAudienceTargeting",
	localization = "content/Language",
	name = "GSearch Audience Targeting Contributor"
)
public interface ModuleConfiguration {
	
	@Meta.AD(
		deflt = "false", 
	    description = "enable-audience-targeting-desc",
	    name = "enable-audience-targeting-name",
		required = false
	)
	public boolean enableAudienceTargeting();
	
	@Meta.AD(
		deflt = "1.0f", 
	    description = "audience-targeting-boost-desc",
	    name= "audience-targeting-boost-name",
		required = false
	)
	public float audienceTargetingBoost();

}
