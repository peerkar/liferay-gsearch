
package fi.soveltia.liferay.gsearch.core.impl.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

@ExtendedObjectClassDefinition(category = "gsearch")
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.core.impl.configuration.KeywordSuggesterConfiguration",
	localization = "content/Language", 
	name = "keyword-suggester-configuration"
)
public interface KeywordSuggesterConfiguration {

	@Meta.AD(
		deflt = "false", 
		description = "enable-query-suggestions-name-desc",
		name = "enable-query-suggestions-name", 
		required = false
	)
	public boolean isQuerySuggestionsEnabled();

	@Meta.AD(
		deflt = "2", 
		description = "query-indexing-threshold-desc",
		name = "query-indexing-threshold-name", 
		required = false
	)
	public int queryIndexingThreshold();

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
		deflt = "", 
		description = "keyword-suggester-configuration-desc",
		name = "keyword-suggester-configuration-name", 
		required = false
	)
	public String[] keywordSuggesters();

	@Meta.AD(
		deflt = "", 
		description = "filter-splitter-desc",
		name = "filter-splitter-name", 
		required = false
	)
	public String filterSplitter();

	@Meta.AD(
		deflt = "", 
		description = "excluded-words-desc",
		name = "excluded-words-name", 
		required = false
	)
	public String[] excludedWords();

}