
package fi.soveltia.liferay.gsearch.mini.web.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * GSearch Mini Web module configuration.
 *
 * @author Petteri Karttunen
 *
 */
@ExtendedObjectClassDefinition(category = "gsearch")
@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.mini.web.configuration.ModuleConfiguration",
	localization = "content/Language", name = "mini-portlet-configuration"
)
public interface ModuleConfiguration {

	@Meta.AD(
		deflt = "contents", 
		description = "suggest-mode-desc",
		name = "suggest-mode-name", 
		optionLabels = {"Keywords", "Contents"},
		optionValues = {"keywords", "contents"}, 
		required = false
	)
	public String suggestMode();

	@Meta.AD(
		deflt = "7", 
		name = "content-suggestions-count-name", 
		required = false
	)
	public int contentSuggestionsCount();

	@Meta.AD(
		deflt = "150", 
		description = "suggestions-delay-desc",
		name = "suggestions-delay-name", 
		required = false
	)
	public int autoCompleteRequestDelay();

	@Meta.AD(
		deflt = "/search", 
		description = "search-portlet-page-desc",
		name = "search-portlet-page-name", 
		required = false
	)
	public String searchPortletPage();

	@Meta.AD(
		deflt = "/viewasset", 
		description = "asset-publisher-page-desc",
		name = "asset-publisher-page-name", 
		required = false
	)
	public String assetPublisherPage();

	@Meta.AD(
		deflt = "/search", 
		description = "hide-on-pages-desc",
		name = "hide-on-pages-name", 
		required = false
	)
	public String[] hideOnPages();

	@Meta.AD(
		deflt = "true", 
		description = "view-in-context-desc",
		name = "view-in-context-name", 
		required = false
	)
	public boolean isViewResultsInContext();

	@Meta.AD(
		deflt = "true", 
		description = "append-redirect-desc",
		name = "append-redirect-name", 
		required = false
	)
	public boolean isRedirectAppended();

	@Meta.AD(
		deflt = "2", 
		name = "keywords-min-length", 
		required = false)
	public int queryMinLength();

	@Meta.AD(
		deflt = "10000", 
		description = "request-timeout-desc",
		name = "request-timeout-name", 
		required = false
	)
	public int requestTimeout();

}