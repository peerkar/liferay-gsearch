package fi.soveltia.liferay.gsearch.recommender.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;

@ExtendedObjectClassDefinition(
	category = "GSearch"
)
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.recommender.configuration.ModuleConfiguration",
	localization = "content/Language",
	name = "recommender-configuration"
)
public interface ModuleConfiguration {

	@Meta.AD(
		deflt = "",
		description = "clause-configuration-desc",
	    name = "clause-configuration-name",
		required = false
	)
	public String[] recommendationClauses();
	
	@Meta.AD(
		deflt = "",
		description = "uid-clause-configuration-desc",
	    name = "uid-clause-configuration-name",
		required = false
	)
	public String[] uidResolverClauses();	
}
