package fi.soveltia.liferay.gsearch.opennlp.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

@ExtendedObjectClassDefinition(category = "gsearch")
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.opennlp.configuration.ModuleConfiguration",
	localization = "content/Language", 
	name = "opennlp-configuration"
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
		deflt = "http://localhost:9200/_ingest/pipeline/opennlp-pipeline/_simulate", 
		description = "engine-url-desc", 
		name = "engine-url-name",
		required = false
	)
	public String engineURL();
}