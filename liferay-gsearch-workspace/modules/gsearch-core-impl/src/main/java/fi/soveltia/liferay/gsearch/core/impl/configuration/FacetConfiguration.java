package fi.soveltia.liferay.gsearch.core.impl.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * Facet configuration.
 * 
 * @author Petteri Karttunen
 */
@ExtendedObjectClassDefinition(category = "gsearch")
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.core.impl.configuration.FacetConfiguration",
	localization = "content/Language", 
	name = "facet-configuration"
)
public interface FacetConfiguration {

	@Meta.AD(
		deflt = "100", 
		description = "max-facet-terms-desc",
		name = "max-facet-terms-name", 
		required = false
	)
	public int maxFacetTerms();

	@Meta.AD(
		deflt = "", 
		description = "facet-configuration-desc",
		name = "facet-configuration-name", 
		required = false
	)
	public String[] facets();

}