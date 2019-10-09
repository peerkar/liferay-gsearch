
package fi.soveltia.liferay.gsearch.core.impl.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * Highlighter configuration.
 * 
 * @author Petteri Karttunen
 */
@ExtendedObjectClassDefinition(category = "gsearch")
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.core.impl.configuration.HighlighterConfiguration",
	localization = "content/Language", 
	name = "highlighter-configuration"
)
public interface HighlighterConfiguration {

	@Meta.AD(
		deflt = "true", 
		description = "highlight-enabled-desc",
		name = "highlight-enabled-name", 
		required = false
	)
	public boolean highlightEnabled();

	@Meta.AD(
		deflt = "50", 
		description = "highlight-fragment-size-desc",
		name = "highlight-fragment-size-name", 
		required = false
	)
	public int highlightFragmentSize();

	@Meta.AD(
		deflt = "10", 
		description = "highlight-snippet-size-desc",
		name = "highlight-snippet-size-name", 
		required = false
	)
	public int highlightSnippetSize();

	@Meta.AD(
		deflt = "700", 
		description = "description-max-length-desc",
		name = "description-max-length-name", 
		required = false
	)
	public int descriptionMaxLength();

}