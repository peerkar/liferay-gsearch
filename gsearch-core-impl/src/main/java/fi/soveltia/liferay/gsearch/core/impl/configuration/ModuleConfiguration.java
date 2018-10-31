
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
		deflt = "10", 
		description = "page-size-desc",
		name = "page-size-name",
		required = false
	)
	public int pageSize();

	@Meta.AD(
		deflt = "/viewasset", 
	    name = "asset-publisher-page-name",
	    description = "asset-publisher-page-desc",
		required = false
	)
	public String assetPublisherPage();
		
	@Meta.AD(
		deflt = "[Please get the default configuration from project README page.",
		description = "suggest-configuration-desc",
	    name = "suggest-configuration-name",
		required = false
	)
	public String suggestConfiguration();

	@Meta.AD(
		deflt = "true", 
	    description = "enable-query-suggestions-name-desc",
	    name = "enable-query-suggestions-name",
		required = false
	)
	public boolean enableQuerySuggestions();

	@Meta.AD(
		deflt = "1", 
		description = "query-suggestions-hits-threshold-desc",
	    name = "Query Suggestion hits threshold.",
		required = false
	)
	public int querySuggestionsHitsThreshold();

	@Meta.AD(
		deflt = "1", 
	    name = "query-suggestions-max",
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
		deflt = "list",
	    description = "default-result-layout-desc",
	    name = "default-result-layout-name",
		required = false
	)
	public String defaultResultLayout();
		
	@Meta.AD(
		deflt = "[Please get the default configuration from project README page.",
		description = "type-configuration-desc",
	    name = "type-configuration-name",
		required = false
	)
	public String typeConfiguration();
	
	@Meta.AD(
		deflt = "[Please get the default configuration from project README page.",
	    description = "facet-configuration-desc",
	    name = "facet-configuration-name",
		required = false
	)
	public String facetConfiguration();

	@Meta.AD(
		deflt = "[Please get the default configuration from project README page.",
		description = "query-configuration-desc",
	    name = "query-configuration-name",
		required = false
	)
	public String queryConfiguration();

	@Meta.AD(
		deflt = "[Please get the default configuration from project README page.",
	    description = "sortfield-configuration-desc",
	    name = "sortfield-configuration-name",
		required = false
	)
	public String sortFieldConfiguration();
}
