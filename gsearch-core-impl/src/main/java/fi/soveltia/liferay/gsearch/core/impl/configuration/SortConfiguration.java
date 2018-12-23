package fi.soveltia.liferay.gsearch.core.impl.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;


@ExtendedObjectClassDefinition(
	category = "GSearch"
)
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.core.impl.configuration.SortConfiguration",
	localization = "content/Language",
	name = "Sort Configuration"
)
public interface SortConfiguration {

	
	@Meta.AD(
		deflt = "[Please get the default configuration from the project GitHub page.",
		description = "sort-configuration-desc",
	    name = "sort-configuration-name",
		required = false
	)
	public String[] sorts();
}
