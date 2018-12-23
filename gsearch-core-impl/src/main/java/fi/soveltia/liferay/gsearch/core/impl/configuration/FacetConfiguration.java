package fi.soveltia.liferay.gsearch.core.impl.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;


@ExtendedObjectClassDefinition(
	category = "GSearch"
)
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.core.impl.configuration.FacetConfiguration",
	localization = "content/Language",
	name = "Facet Configuration"
)
public interface FacetConfiguration {

	@Meta.AD(
		deflt = "[Please get the default configuration from the project GitHub page.",
		description = "facet-configuration-desc",
	    name = "facet-configuration-name",
		required = false
	)
	public String[] facets();
}
