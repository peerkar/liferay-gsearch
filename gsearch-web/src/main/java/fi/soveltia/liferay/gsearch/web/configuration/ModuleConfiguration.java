
package fi.soveltia.liferay.gsearch.web.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;

/**
 * GSearch Web module configuration.
 * 
 * @author Petteri Karttunen
 *
 */
@ExtendedObjectClassDefinition(
	category = "GSearch"
)
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.web.configuration.ModuleConfiguration",
	localization = "content/Language",
	name = "GSearch Portlet"
)
public interface ModuleConfiguration {

	@Meta.AD(
		deflt = "false", 
		description = "enable-js-debug-desc",
	    name = "enable-js-debug-name",
		required = false
	)
	public boolean jsDebuggingEnabled();	

	@Meta.AD(
		deflt = "3", 
	    name = "keywords-min-length",
		required = false
	)
	public int queryMinLength();
	
	@Meta.AD(
		deflt = "true", 
	    name = "enable-autocompletion-name",
	    description = "enable-autocompletion-desc",
		required = false
	)
	public boolean enableAutoComplete();

	@Meta.AD(
		deflt = "150", 
	    description = "autocomplete-delay-desc",
	    name = "autocomplete-delay-name",
		required = false
	)
	public int autoCompleteRequestDelay();	

	@Meta.AD(
		deflt = "10000", 
		description = "request-timeout-desc",
	    name = "request-timeout-name",
		required = false
	)
	public int requestTimeout();

	@Meta.AD(
		deflt = "", 
		description = "helptext-article-id-desc",
	    name = "helptext-article-id-name",
		required = false
	)
	public String helpTextArticleId();
	
	@Meta.AD(
		deflt = "0", 
		description = "helptext-group-id-desc",
	    name = "helptext-group-id-name",
		required = false
	)
	public long helpTextGroupId();
	
	@Meta.AD(
		deflt = "true", 
	    description = "show-tags-desc",
	    name = "show-tags-name",
		required = false
	)
	public boolean showTags();
	
	@Meta.AD(
		deflt = "", 
		description = "result-layouts-desc",
	    name = "result-layouts-name",
		required = false
	)
	public String resultLayouts();
	
	@Meta.AD(
		deflt = "", 
		description = "google-maps-api-key-desc",
	    name = "google-maps-api-key-name",
		required = false
	)
	public String googleMapsAPIKey();
	
}

