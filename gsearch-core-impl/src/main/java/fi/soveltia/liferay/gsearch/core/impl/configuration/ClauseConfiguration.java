package fi.soveltia.liferay.gsearch.core.impl.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;


@ExtendedObjectClassDefinition(
	category = "GSearch"
)
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.core.impl.configuration.ClauseConfiguration",
	localization = "content/Language",
	name = "Clause Configuration"
)
public interface ClauseConfiguration {

	@Meta.AD(
		deflt = "[Please get the default configuration from the project GitHub page.",
		description = "clause-configuration-desc",
	    name = "clause-configuration-name",
		required = false
	)
	public String[] clauses();
}
