package fi.soveltia.liferay.gsearch.resultitemtagger.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;

@ExtendedObjectClassDefinition(
	category = "GSearch"
)
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.resultitemtagger.configuration.ModuleConfiguration",
	localization = "content/Language",
	name = "result-item-tagger"
)
public interface ModuleConfiguration {
		
	@Meta.AD(
		deflt = "false", 
	    description = "enable-feature-desc",
	    name = "enable-feature-name",
		required = false
	)
	public boolean enableFeature();

	@Meta.AD(
		deflt = "assetTagNames", 
	    description = "field-name-desc",
	    name= "field-name-name",
		required = false
	)
	public String fieldName();

	@Meta.AD(
		deflt = "official", 
	    description = "field-value-desc",
	    name= "field-value-name",
		required = false
	)
	public String fieldValue();
	
	@Meta.AD(
		deflt = "official_content", 
	    description = "property-desc",
	    name= "property-name",
		required = false
	)
	public String property();

}