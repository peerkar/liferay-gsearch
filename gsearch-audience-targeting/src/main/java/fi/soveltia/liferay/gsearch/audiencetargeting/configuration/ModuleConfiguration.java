package fi.soveltia.liferay.gsearch.audiencetargeting.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;

/**
 * GSearch Audience Targeting module configuration.
 * 
 * @author Petteri Karttunen
 *
 */
@ExtendedObjectClassDefinition(
	category = "GSearch"
)
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.audiencetargeting.configuration.ModuleConfiguration",
	localization = "content/Language",
	name = "audience-targeting-configuration"
)
public interface ModuleConfiguration {
	
	@Meta.AD(
		deflt = "false", 
	    description = "enable-feature-desc",
	    name = "enable-feature-name",
		required = false
	)
	public boolean enableFeature();
	
	@Meta.AD(
		deflt = "2.0f", 
	    description = "query-contributor-boost-desc",
	    name= "query-contributor-boost-name",
		required = false
	)
	public float queryContributorBoost();

}
