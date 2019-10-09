package fi.soveltia.liferay.gsearch.resultitemtagger.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

@ExtendedObjectClassDefinition(
	category = "gsearch"
)
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.resultitemtagger.configuration.ModuleConfiguration",
	localization = "content/Language", 
	name = "result-item-tagger"
)
public interface ModuleConfiguration {

	@Meta.AD(
		deflt = "false", 
		description = "is-enabled-desc",
		name = "is-enabled-name", 
		required = false
	)
	public boolean isEnabled();

	@Meta.AD(
		deflt = "", 
		description = "rule-configuration-desc",
		name = "rule-configuration-name", 
		required = false
	)
	public String[] rules();

}