package fi.soveltia.liferay.gsearch.core.impl.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * Index configuration.
 *
 * @author Petteri Karttunen
 */
@ExtendedObjectClassDefinition(category = "gsearch")
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.core.impl.configuration.IndexConfiguration",
	localization = "content/Language", 
	name = "index-configuration"
)
public interface IndexConfiguration {

	@Meta.AD(
		deflt = "", 
		description = "search-index-desc", 
		name = "search-index-name",
		required = false
	)
	public String[] searchIndexes();

	@Meta.AD(
		deflt = "", 
		description = "keyword-suggester-index-desc", 
		name = "keyword-suggester-index-name",
		required = false
	)
	public String keywordSuggesterIndex();

}