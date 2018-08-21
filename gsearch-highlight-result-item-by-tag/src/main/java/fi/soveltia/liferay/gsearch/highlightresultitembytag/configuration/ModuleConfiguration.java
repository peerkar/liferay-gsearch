package fi.soveltia.liferay.gsearch.highlightresultitembytag.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;

@ExtendedObjectClassDefinition(
	category = "GSearch"
)
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.highlightresultitembytag.configuration.ModuleConfiguration",
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
