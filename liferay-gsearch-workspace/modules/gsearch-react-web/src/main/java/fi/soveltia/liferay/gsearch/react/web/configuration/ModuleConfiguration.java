
package fi.soveltia.liferay.gsearch.react.web.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;

/**
 * Liferay GSearch Web module configuration.
 * 
 * @author Petteri Karttunen
 *
 */
@ExtendedObjectClassDefinition(
	category = "gsearch"
)
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.react.web.configuration.ModuleConfiguration",
	localization = "content/Language",
	name = "GSearch React Portlet"
)
public interface ModuleConfiguration {

	@Meta.AD(
		deflt = "/viewasset", 
	    name = "asset-publisher-page-name",
	    description = "asset-publisher-page-desc",
		required = false
	)
	public String assetPublisherPage();	
	
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
		deflt = "10", 
		description = "page-size-desc",
		name = "page-size-name",
		required = false
	)
	public int pageSize();	
	
	@Meta.AD(
		deflt = "10000", 
		description = "request-timeout-desc",
	    name = "request-timeout-name",
		required = false
	)
	public int requestTimeout();
	
	@Meta.AD(
		deflt = "yyyy-MM-dd", 
		description = "datepicker-format-desc",
	    name = "datepicker-format-name",
		required = false
	)
	public String datePickerFormat();
	
	@Meta.AD(
		deflt = "3", 
	    name = "keywords-min-length",
		required = false
	)
	public int queryMinLength();


	@Meta.AD(
		deflt = "search", 
	    description = "keywords-placeholder-desc",
	    name = "keywords-placeholder-name",
		required = false
	)
	public String keywordsPlaceholder();
	
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
	    description = "show-scope-filter-desc",
	    name = "show-scope-filter-name",
		required = false
	)
	public boolean isScopeFilterVisible();	
	
	@Meta.AD(
		deflt = "true", 
	    description = "show-time-filter-name",
	    name = "show-time-filter-name",
		required = false
	)
	public boolean isTimeFilterVisible();
	
	@Meta.AD(
		deflt = "true", 
	    description = "show-type-desc",
	    name = "show-type-name",
		required = false
	)
	public boolean isTypeVisible();

	@Meta.AD(
		deflt = "true", 
	    description = "show-link-desc",
	    name = "show-link-name",
		required = false
	)
	public boolean isLinkVisible();

	@Meta.AD(
		deflt = "false", 
	    description = "show-username-desc",
	    name = "show-username-name",
		required = false
	)
	public boolean isUserNameVisible();
	
	@Meta.AD(
		deflt = "true", 
	    description = "show-asset-tags-desc",
	    name = "show-asset-tags-name",
		required = false
	)
	public boolean isAssetTagsVisible();


	@Meta.AD(
		deflt = "false", 
	    description = "show-asset-categories-desc",
	    name = "show-asset-categories-name",
		required = false
	)
	public boolean isAssetCategoriesVisible();
	
	@Meta.AD(
		deflt = "", 
		description = "google-maps-api-key-desc",
	    name = "google-maps-api-key-name",
		required = false
	)
	public String googleMapsAPIKey();
	
	@Meta.AD(
		deflt = "51.522525", 
		description = "google-maps-default-center-lat-desc",
	    name = "google-maps-default-center-lat-name",
		required = false
	)
	public float googleMapsDefaultCenterLat();
	
	@Meta.AD(
		deflt = "-0.130456", 
		description = "google-maps-default-center-lng-desc",
	    name = "google-maps-default-center-lng-name",
		required = false
	)
	public float googleMapsDefaultCenterLng();

	@Meta.AD(
		deflt = "true", 
	    description = "click-tracking-desc",
	    name = "click-tracking-name",
		required = false
	)
	public boolean isClickTrackingEnabled();

	@Meta.AD(
		deflt = "", 
		description = "result-layouts-desc",
	    name = "result-layouts-name",
		required = false
	)
	public String[] resultLayouts();
}

