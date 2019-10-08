package fi.soveltia.liferay.gsearch.additionalfields.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * Additional document fields configuration.
 *
 * @author Petteri Karttunen
 */
@ExtendedObjectClassDefinition(category = "gsearch")
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.additionalfields.configuration.ModuleConfiguration",
	localization = "content/Language", 
	name = "additional-fields-configuration"
)
public interface ModuleConfiguration {

	@Meta.AD(
		deflt = "false", 
		description = "is-enabled-desc",
		name = "is-enabled-name", 
		required = false
	)
	public boolean isEnabled();

}