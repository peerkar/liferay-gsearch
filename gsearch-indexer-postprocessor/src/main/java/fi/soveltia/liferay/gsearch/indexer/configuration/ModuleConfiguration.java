
package fi.soveltia.liferay.gsearch.indexer.configuration;

import aQute.bnd.annotation.metatype.Meta;

/**
 * Indexer postprocessor configuration.
 * 
 * @author Petteri Karttunen
 *
 */
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.indexer.postprocessor.configuration.GSearchIndexerPostprocessor",
	localization = "content/Language",
	name = "GSearch Indexer Postprocessor"
)
public interface ModuleConfiguration {

	@Meta.AD(
		deflt = "gsearch_geolocation", 
		description = "geolocation-field-desc",
	    name = "geolocation-field-name",
		required = false
	)
	public String fieldName();

	@Meta.AD(
		deflt = "", 
		description = "test-ip-desc",
	    name = "test-ip-name",
		required = false
	)
	public String testIpAddress();
}