
package fi.soveltia.liferay.gsearch.core.impl.query.postprocessor;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.suggest.SuggestionConstants;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.postprocessor.QueryPostProcessor;
import fi.soveltia.liferay.gsearch.core.impl.configuration.KeywordSuggesterConfiguration;

/**
 * Query indexer processor. 
 * 
 * Originally com.liferay.portal.search.internal.hits.QueryIndexingHitsProcessor
 *
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.core.impl.configuration.KeywordSuggesterConfiguration",
	immediate = true, 
	service = QueryPostProcessor.class
)
public class QueryIndexerProcessorImpl implements QueryPostProcessor {

	@Override
	public boolean process(
			QueryContext queryContext, SearchSearchResponse searchResponse) 
					throws Exception {		
		
		if (_log.isDebugEnabled()) {
			_log.debug("Processing QueryIndexer");
		}

		if (!_keywordSuggesterConfiguration.isQuerySuggestionsEnabled()) {
			return true;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("QueryIndexer is enabled");
		}

		Hits hits = searchResponse.getHits();
		
		// Use the raw keywords from the initial search only
		// i.e. not from an alternate search.
		
		String keywords = 
				(String)queryContext.getParameter(ParameterNames.RAW_KEYWORDS);
				
		if (Validator.isNotNull(keywords) &&
			(hits.getLength() >=
				_keywordSuggesterConfiguration.queryIndexingThreshold())) {

			// Filter words.

			String filteredWords = filterKeywords(keywords);

			if (filteredWords.length() == 0) {
				return true;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					"QueryIndexing threshold exceeded. Indexing keywords: " +
							filteredWords);
			}
			
			addDocument(
				(long)queryContext.getParameter(ParameterNames.COMPANY_ID),
				filteredWords,
				(Locale)queryContext.getParameter(ParameterNames.LOCALE));
		}
		else {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"QueryIndexing threshold wasn't exceeded or no keywords. Not indexing.");
			}
		}

		return true;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_keywordSuggesterConfiguration = ConfigurableUtil.createConfigurable(
			KeywordSuggesterConfiguration.class, properties);
	}

	protected void addDocument(long companyId, String keywords, Locale locale)
		throws SearchException {

		_indexWriterHelper.indexKeyword(
			companyId, keywords, 0, SuggestionConstants.TYPE_QUERY_SUGGESTION,
			locale);
	}

	/**
	 * Strips not wanted keywords.
	 *
	 * @param keywords
	 * @return
	 */
	protected String filterKeywords(String keywords) throws Exception {
		String[] excludedWords = _keywordSuggesterConfiguration.excludedWords();

		String splitter = _keywordSuggesterConfiguration.filterSplitter();

		if ((excludedWords == null) || (excludedWords.length == 0) ||
			Validator.isNull(splitter)) {

			return keywords;
		}

		String[] keywordArray = keywords.split(splitter);

		for (String keyword : keywordArray) {
			for (String exclude : excludedWords) {
				if (exclude.endsWith("*")) {
					exclude = exclude.substring(0, exclude.length() - 1);

					if (keyword.startsWith(exclude)) {
						if (_log.isDebugEnabled()) {
							_log.debug("Excluding keyword by stem: " + keyword);
						}

						keywords = keywords.replace(keyword, "");
					}
				}
				else if (keyword.equals(exclude)) {
					if (_log.isDebugEnabled()) {
						_log.debug("Excluding keyword: " + keyword);
					}

					keywords = keywords.replace(keyword, "");
				}
			}
		}

		return keywords;
	}

	private static final Logger _log = LoggerFactory.getLogger(
		QueryIndexerProcessorImpl.class);

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	private volatile KeywordSuggesterConfiguration
		_keywordSuggesterConfiguration;

}