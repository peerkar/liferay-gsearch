
package fi.soveltia.liferay.gsearch.core.configuration;

import aQute.bnd.annotation.metatype.Meta;

@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.core.configuration.GSearchConfiguration",
	localization = "content/Language"
)
public interface GSearchConfiguration {

	@Meta.AD(
		deflt = "false", 
		description = "enable-js-debug-desc",
	    name = "enable-js-debug-name",
		required = false
	)
	public boolean jsDebuggingEnabled();

	@Meta.AD(
		deflt = "3", 
	    name = "keywords-min-length",
		required = false
	)
	public int queryMinLength();

	@Meta.AD(
		deflt = "10", 
		description = "page-size-desc",
		name = "page-size-name",
		required = false
	)
	public int pageSize();

	@Meta.AD(
		deflt = "10000", 
		description = "request-timeout-desc",
	    name = "request-timeout-name",
		required = false
	)
	public int requestTimeout();
	
	@Meta.AD(
		deflt = "/viewasset", 
	    name = "asset-publisher-page-name",
	    description = "asset-publisher-page-desc",
		required = false
	)
	public String assetPublisherPage();
	
	@Meta.AD(
		deflt = "true", 
	    name = "enable-autocompletion-name",
	    description = "enable-autocompletion-desc",
		required = false
	)
	public boolean enableAutoComplete();

	@Meta.AD(
		deflt = "150", 
	    description = "autocomplete-delay-desc",
	    name = "autocomplete-delay-name",
		required = false
	)
	public int autoCompleteRequestDelay();	
	
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
		deflt = "false", 
	    description = "enable-audience-targeting-desc",
	    name = "enable-audience-targeting-name",
		required = false
	)
	public boolean enableAudienceTargeting();
	
	@Meta.AD(
		deflt = "1.5f", 
	    description = "audience-targeting-boost-desc",
	    name= "audience-targeting-boost-name",
		required = false
	)
	public float audienceTargetingBoost();
	
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
		description = "searchfield-configuration-desc",
	    name = "searchfield-configuration-name",
		required = false
	)
	public String searchFieldConfiguration();

	@Meta.AD(
		deflt = "[Please get the default configuration from project README page.",
	    description = "sortfield-configuration-desc",
	    name = "sortfield-configuration-name",
		required = false
	)
	public String sortFieldConfiguration();
}
