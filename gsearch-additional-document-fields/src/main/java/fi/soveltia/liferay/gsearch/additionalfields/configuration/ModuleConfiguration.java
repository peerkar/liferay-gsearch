package fi.soveltia.liferay.gsearch.additionalfields.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;

/**
 * Additional document fields configuration.
 * 
 * @author Petteri Karttunen
 *
 */
@ExtendedObjectClassDefinition(
	category = "GSearch"
)
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.additionalfields.configuration.ModuleConfiguration",
	localization = "content/Language",
	name = "additional-fields-configuration"
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
		deflt = "gsearch_version_count", 
	    description = "version-field-desc",
	    name = "version-field-name",
		required = false
	)
	public String versionField();	
}