package fi.soveltia.liferay.gsearch.core.impl.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;

@ExtendedObjectClassDefinition(
	category = "GSearch"
)
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.core.impl.configuration.ClauseConfiguration",
	localization = "content/Language",
	name = "clause-configuration"
)
public interface ClauseConfiguration {

	@Meta.AD(
		deflt = "",
		description = "clause-configuration-desc",
	    name = "clause-configuration-name",
		required = false
	)
	public String[] clauses();
}
