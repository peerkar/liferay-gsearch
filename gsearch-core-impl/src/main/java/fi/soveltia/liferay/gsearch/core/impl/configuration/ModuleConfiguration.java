
package fi.soveltia.liferay.gsearch.core.impl.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;


@ExtendedObjectClassDefinition(
	category = "GSearch"
)
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.core.impl.configuration.ModuleConfiguration",
	localization = "content/Language",
	name = "GSearch Core"
)
public interface ModuleConfiguration {
	
	@Meta.AD(
		deflt = "50", 
		description = "highlight-fragment-size-desc",
	    name = "highlight-fragment-size-name",
		required = false
	)
	public int highlightFragmentSize();	

	@Meta.AD(
		deflt = "200", 
		description = "snippet-size-desc",
	    name = "snippet-size-name",
		required = false
	)
	public int snippetSize();	

	@Meta.AD(
		deflt = "20", 
		description = "max-facet-terms-desc",
	    name = "max-facet-terms-name",
		required = false
	)
	public int maxFacetTerms();	
}
