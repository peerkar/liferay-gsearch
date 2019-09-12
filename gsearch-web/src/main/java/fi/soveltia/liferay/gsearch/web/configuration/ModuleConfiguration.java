
package fi.soveltia.liferay.gsearch.web.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;

/**
 * Liferay GSearch Web module configuration.
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
	public boolean isJSDebuggingEnabled();	

	@Meta.AD(
		deflt = "/viewasset", 
	    name = "asset-publisher-page-name",
	    description = "asset-publisher-page-desc",
		required = false
	)
	public String assetPublisherPage();	
	
	@Meta.AD(
		deflt = "3", 
	    name = "keywords-min-length",
		required = false
	)
	public int queryMinLength();
	
	@Meta.AD(
		deflt = "10", 
		description = "page-size-desc",
		name = "page-size-name",
		required = false
	)
	public int pageSize();	
	
	@Meta.AD(
		deflt = "true", 
	    name = "enable-keyword-suggester-name",
	    description = "enable-keyword-suggester-desc",
		required = false
	)
	public boolean isKeywordSuggesterEnabled();

	@Meta.AD(
		deflt = "150", 
	    description = "keyword-suggester-delay-desc",
	    name = "keyword-suggester-delay-name",
		required = false
	)
	public int keywordSuggesterRequestDelay();	

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
	    description = "show-asset-tags-desc",
	    name = "show-asset-tags-name",
		required = false
	)
	public boolean isAssetTagsVisible();

	@Meta.AD(
		deflt = "false", 
	    description = "show-username-desc",
	    name = "show-username-name",
		required = false
	)
	public boolean isUserNameVisible();
	
	@Meta.AD(
		deflt = "false", 
	    description = "show-asset-categories-desc",
	    name = "show-asset-categories-name",
		required = false
	)
	public boolean isAssetCategoriesVisible();
	
	@Meta.AD(
		deflt = "", 
		description = "result-layouts-desc",
	    name = "result-layouts-name",
		required = false
	)
	public String[] resultLayouts();
	
	@Meta.AD(
		deflt = "false", 
		description = "view-in-context-desc",
	    name = "view-in-context-name",
		required = false
	)
	public boolean isViewResultsInContext();

	@Meta.AD(
		deflt = "false", 
		description = "append-redirect-desc",
	    name = "append-redirect-name",
		required = false
	)
	public boolean isRedirectAppended();

	
	@Meta.AD(
		deflt = "", 
		description = "google-maps-api-key-desc",
	    name = "google-maps-api-key-name",
		required = false
	)
	public String googleMapsAPIKey();
	
	@Meta.AD(
		deflt = "yyyy-MM-dd", 
		description = "datepicker-format-desc",
	    name = "datepicker-format-name",
		required = false
	)
	public String datePickerFormat();

	@Meta.AD(
		deflt = "false",
		description = "past-searches-desc",
		name = "past-searches-name",
		required = false
	)
	public boolean isPastSearchesEnabled();
}

