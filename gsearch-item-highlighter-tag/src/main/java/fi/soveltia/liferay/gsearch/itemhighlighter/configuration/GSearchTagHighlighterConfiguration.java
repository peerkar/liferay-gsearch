package fi.soveltia.liferay.gsearch.itemhighlighter.configuration;

import aQute.bnd.annotation.metatype.Meta;

@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.itemhighlighter.tag.GSearchTagHighlighterConfiguration",
	localization = "content/Language"
)
public interface GSearchTagHighlighterConfiguration {
		
	@Meta.AD(
		deflt = "false", 
	    description = "enable-tag-highlighter-desc",
	    name = "enable-tag-highlighter-name",
		required = false
	)
	public boolean enableTagHighlighter();
	
	@Meta.AD(
		deflt = "official", 
	    description = "highlight-tag-desc",
	    name= "highlight-tag-name",
		required = false
	)
	public String tagName();
}
