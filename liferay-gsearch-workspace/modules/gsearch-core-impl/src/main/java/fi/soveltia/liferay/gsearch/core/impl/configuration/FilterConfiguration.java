package fi.soveltia.liferay.gsearch.core.impl.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * Filter configuration.
 *
 * @author Petteri Karttunen
 */
@ExtendedObjectClassDefinition(category = "gsearch")
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.core.impl.configuration.FilterConfiguration",
	localization = "content/Language", 
	name = "filter-configuration"
)
public interface FilterConfiguration {

	@Meta.AD(
		deflt = "", 
		description = "filter-desc", 
		name = "filter-name",
		required = false
	)
	public String[] filters();

}