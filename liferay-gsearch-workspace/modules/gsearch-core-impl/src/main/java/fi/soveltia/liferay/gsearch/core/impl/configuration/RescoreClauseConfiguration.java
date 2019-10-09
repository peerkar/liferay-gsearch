package fi.soveltia.liferay.gsearch.core.impl.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * Rescore clause configuration.
 *
 * @author Petteri Karttunen
 */
@ExtendedObjectClassDefinition(category = "gsearch")
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.core.impl.configuration.RescoreClauseConfiguration",
	localization = "content/Language",
	name = "rescore-clause-configuration"
)
public interface RescoreClauseConfiguration {

	@Meta.AD(
		deflt = "", 
		description = "rescore-clause-configuration-desc",
		name = "rescore-clause-configuration-name", 
		required = false
	)
	public String[] rescoreClauses();

}