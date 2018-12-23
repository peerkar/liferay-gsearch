
package fi.soveltia.liferay.gsearch.core.impl.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;


@ExtendedObjectClassDefinition(
	category = "GSearch"
)
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.core.impl.configuration.KeywordSuggesterConfiguration",
	localization = "content/Language",
	name = "Keyword Suggester Configuration"
)
public interface KeywordSuggesterConfiguration {

	@Meta.AD(
		deflt = "[Please get the default configuration from project README page.",
		description = "keyword-suggester-configuration-desc",
	    name = "keyword-suggester-configuration-name",
		required = false
	)
	public String[] keywordSuggesters();
	
}
