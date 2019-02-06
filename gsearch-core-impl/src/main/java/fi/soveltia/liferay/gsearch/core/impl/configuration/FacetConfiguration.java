package fi.soveltia.liferay.gsearch.core.impl.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;


@ExtendedObjectClassDefinition(
	category = "GSearch"
)
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.core.impl.configuration.FacetConfiguration",
	localization = "content/Language",
	name = "facet-configuration"
)
public interface FacetConfiguration {

	@Meta.AD(
		deflt = "",
		description = "facet-configuration-desc",
	    name = "facet-configuration-name",
		required = false
	)
	public String[] facets();
}
