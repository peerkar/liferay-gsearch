package fi.soveltia.liferay.gsearch.core.impl.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * Clause configuration.
 *
 * @author Petteri Karttunen
 */
@ExtendedObjectClassDefinition(category = "gsearch")
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