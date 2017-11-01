package fi.soveltia.liferay.gsearch.web.configuration;

import aQute.bnd.annotation.metatype.Meta;

@Meta.OCD(
	id = "fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration"
)
public interface GSearchDisplayConfiguration {

	@Meta.AD(
		deflt = "3", 
	    name = "Search keywords minimum length, in characters",
		required = false
	)
	public int queryMinLength();

	@Meta.AD(
		deflt = "10", 
		description = "Number of results to show on one page",
		name = "Search results page size",
		required = false
	)
	public int pageSize();

	@Meta.AD(
		deflt = "10000", 
		description = "Timeout, after which the search request stalls in UI",
	    name = "Search request timeout in ms",
		required = false
	)
	public int requestTimeout();
	
	@Meta.AD(
		deflt = "/viewasset", 
	    name = "Asset Publisher page friendly URL",
	    description = "Friendly URL to a page to show search results if they cannot be shown in context.",
		required = false
	)
	public String assetPublisherPage();

	@Meta.AD(
		deflt = "false", 
		description = "Show content small images in the results.",
	    name = "Show result small images.",
		required = false
	)
	public boolean showSmallImages();

	@Meta.AD(
		deflt = "false", 
		description = "When this is enabled and you're doing a image search, results are shown as images (Google like).",
	    name = "Enable image search layout.",
		required = false
	)
	public boolean enableImageSearchLayout();
	
	@Meta.AD(
		deflt = "true", 
	    name = "Enable autocompletion",
	    description = "Enable autocompletion of searchfield",
		required = false
	)
	public boolean enableAutoComplete();

	@Meta.AD(
		deflt = "5", 
	    name = "Number of autocomplete suggestions to show",
		required = false
	)
	public int keywordSuggestionsMax();

	@Meta.AD(
		deflt = "0.95", 
		description = "The likelihood of a term being a misspelled even if the term exists in the dictionary. The default is 0.85 corresponding to 15% of the real words are misspelled. Please see https://www.elastic.co/guide/en/elasticsearch/reference/5.6/search-suggesters-phrase.html",
	    name = "Keyword suggestions real world error likelihood.",
		required = false
	)
	public float keywordSuggestionsRealWordErrorLikelihood();
	
	@Meta.AD(
		deflt = "2.0", 
		description = "The maximum percentage of the terms that at most considered to be misspellings in order to form a correction. Please see https://www.elastic.co/guide/en/elasticsearch/reference/5.6/search-suggesters-phrase.html",
	    name = "Number of autocomplete suggestions to show",
		required = false
	)
	public float keywordSuggestionsMaxErrors();

	@Meta.AD(
		deflt = "0.01f", 
	    description = "The confidence level defines a factor applied to the input phrases score which is used as a threshold for other suggest candidates. Only candidates that score higher than the threshold will be included in the result. Please see https://www.elastic.co/guide/en/elasticsearch/reference/5.6/search-suggesters-phrase.html",
	    name= "Keyword suggestion confidence level",
		required = false
	)
	public float keywordSuggestionsConfidence();
		
	@Meta.AD(
		deflt = "true", 
	    name = "Enable query suggestion",
	    description = "Enable query suqqestions which are shown if there are results than the defined threshold (below).",
		required = false
	)
	public boolean enableQuerySuggestions();

	@Meta.AD(
		deflt = "1", 
		description = "If hits count is below this number, show query suggestions ",
	    name = "Query Suggestion hits threshold.",
		required = false
	)
	public int querySuggestionsHitsThreshold();

	@Meta.AD(
		deflt = "1", 
	    name = "Number of query suggestions to show at maximum",
		required = false
	)
	public int querySuggestionsMax();

	@Meta.AD(
		deflt = "2", 
		description = "Notice that either autocompletion or query suggestions has to be enabled in order to index queries.",
	    name = "Number of results there has to be to index the query",
		required = false
	)
	public int queryIndexingThreshold();

	@Meta.AD(
		deflt = "web-content;com.liferay.journal.model.JournalArticle|file;com.liferay.document.library.kernel.model.DLFileEntry|" +
			"discussion;com.liferay.message.boards.kernel.model.MBMessage|blog;com.liferay.blogs.kernel.model.BlogsEntry|" +
			"wikipage;com.liferay.wiki.model.WikiPage",
	    name = "Supported asset type option",
	    description = "Syntax: translation_key;classname_of_supported_asset_type",
	    required = false
	)
	public String[]assetTypeOptions();	
	
	@Meta.AD(
		deflt = "pdf;type.pdf;pdf|word;type.word;doc_docx|excel;type.excel;xls_xlsx|powerpoint;type.powerpoint;ppt_pptx|image;type.image;png_jpg_jpeg_gif_tif_tiff|video;type.video;mp4_avi",
	    name = "Document Extension Filter Option",
	    description = "Syntax: filter_key;translation_key_for_ui;underscore_separated_extensions_list",
	    required = false
	)
	public String[]documentExtensionOptions();
	
}
