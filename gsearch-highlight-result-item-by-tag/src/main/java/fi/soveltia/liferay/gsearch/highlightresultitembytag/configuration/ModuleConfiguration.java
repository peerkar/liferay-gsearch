package fi.soveltia.liferay.gsearch.highlightresultitembytag.configuration;

import aQute.bnd.annotation.metatype.Meta;

@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.highlightresultitembytag.GSearchHighlightResultItemByTag",
	localization = "content/Language",
	name = "GSearch Highlight Result Item by Tag"
)
public interface ModuleConfiguration {
		
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
