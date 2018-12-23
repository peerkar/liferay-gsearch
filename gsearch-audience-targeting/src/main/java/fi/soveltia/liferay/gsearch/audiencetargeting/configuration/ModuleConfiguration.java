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
	name = "Audience Targeting"
)
public interface ModuleConfiguration {
	
	@Meta.AD(
		deflt = "false", 
	    description = "enable-query-contributor-desc",
	    name = "enable-query-contributor-name",
		required = false
	)
	public boolean enableQueryContributor();
	
	@Meta.AD(
		deflt = "2.0f", 
	    description = "query-contributor-boost-desc",
	    name= "query-contributor-boost-name",
		required = false
	)
	public float queryContributorBoost();

}
