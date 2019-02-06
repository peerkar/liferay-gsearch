package fi.soveltia.liferay.gsearch.core.impl.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;


@ExtendedObjectClassDefinition(
	category = "GSearch"
)
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.core.impl.configuration.SortConfiguration",
	localization = "content/Language",
	name = "sort-configuration"
)
public interface SortConfiguration {
	
	@Meta.AD(
		deflt = "",
		description = "sort-configuration-desc",
	    name = "sort-configuration-name",
		required = false
	)
	public String[] sorts();
}
