
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
		deflt = "true", 
	    description = "enable-query-suggestions-name-desc",
	    name = "enable-query-suggestions-name",
		required = false
	)
	public boolean isQuerySuggestionsEnabled();

	@Meta.AD(
		deflt = "1", 
		description = "query-suggestions-hits-threshold-desc",
	    name = "query-suggestions-hits-threshold-name",
		required = false
	)
	public int querySuggestionsHitsThreshold();

	@Meta.AD(
		deflt = "1", 
	    name = "query-suggestions-max-name",
		required = false
	)
	public int querySuggestionsMax();

	@Meta.AD(
		deflt = "2", 
		description = "query-indexing-threshold-desc",
	    name = "query-indexing-threshold-name",
		required = false
	)
	public int queryIndexingThreshold();

	@Meta.AD(
		deflt = "50", 
		description = "highlight-fragment-size-desc",
	    name = "highlight-fragment-size-name",
		required = false
	)
	public int highlightFragmentSize();	
	
	@Meta.AD(
		deflt = "20", 
		description = "max-facet-terms-desc",
	    name = "max-facet-terms-name",
		required = false
	)
	public int maxFacetTerms();	
}
