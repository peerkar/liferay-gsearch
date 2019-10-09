
package fi.soveltia.liferay.gsearch.core.impl.suggest;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.suggest.CompletionSuggester;
import com.liferay.portal.kernel.search.suggest.PhraseSuggester;
import com.liferay.portal.kernel.search.suggest.QuerySuggester;
import com.liferay.portal.kernel.search.suggest.Suggester;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search.SuggestSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SuggestSearchResponse;
import com.liferay.portal.search.engine.adapter.search.SuggestSearchResult;
import com.liferay.portal.search.index.IndexNameBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.configuration.CoreConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ConfigurationNames;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.constants.SuggesterConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.SuggesterConfigurationValues;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.suggest.GSearchKeywordSuggester;

/**
 * Keyword suggester implementation.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = GSearchKeywordSuggester.class
)
public class GSearchKeywordSuggesterImpl implements GSearchKeywordSuggester {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONArray getSuggestions(QueryContext queryContext)
		throws Exception {

		String[] suggestions = getSuggestionsAsStringArray(queryContext);

		JSONArray resultsArray = JSONFactoryUtil.createJSONArray();

		if (suggestions != null) {
			for (String s : suggestions) {
				resultsArray.put(s);
			}
		}

		return resultsArray;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getSuggestionsAsStringArray(QueryContext queryContext)
		throws Exception {

		String keywords = queryContext.getKeywords();

		if (Validator.isNull(keywords)) {
			return new String[0];
		}

		List<Suggester> suggesters = createSuggesters(queryContext);
		
		List<String> suggestions = new ArrayList<>();


		// As localized suggestion index fields are template based, it might
		// be that there are no fields yet. This might throw a harmless
		// exception.

		try {

			SuggestSearchResponse response = 
					executeSuggestSearchRequest(queryContext, suggesters);
						
			Collection<SuggestSearchResult> suggesterResults =
				response.getSuggestSearchResults();

			if (suggesterResults != null) {
				for (SuggestSearchResult suggesterResult : suggesterResults) {
					for (SuggestSearchResult.Entry entry :
							suggesterResult.getEntries()) {

						for (SuggestSearchResult.Entry.Option option :
								entry.getOptions()) {

							if (_log.isDebugEnabled()) {
								_log.debug(
									"Adding suggestion:" + option.getText());
							}

							if (!suggestions.contains(option.getText())) {
								suggestions.add(option.getText());
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			_log.warn(e.getMessage(), e);
		}

		return suggestions.stream(
		).toArray(
			String[]::new
		);
	}

	/**
	 * Create name for a suggester.
	 *
	 * @return
	 */
	protected String createSuggesterName() {
		if (_random == null) {
			_random = new Random();
		}

		return "suggester" + _random.nextInt(1000);
	}
	
	/**
	 * Create suggesters based on the configuration.
	 * 
	 * @param queryContext
	 * @return
	 * @throws Exception
	 */
	protected List<Suggester> createSuggesters(QueryContext queryContext) throws Exception {

		String keywords = queryContext.getKeywords();

		// We don't need uppercases in suggestions.

		keywords = keywords.toLowerCase();

		List<Suggester> suggesters = new ArrayList<Suggester>();

		JSONArray configuration = (JSONArray)queryContext.getConfiguration(
			ConfigurationNames.SUGGESTER);

		for (int i = 0; i < configuration.length(); i++) {

			Suggester suggester = null;

			JSONObject item  = configuration.getJSONObject(i);

			boolean enabled = item.getBoolean(SuggesterConfigurationKeys.ENABLED);

			if (!enabled) {
				continue;
			}

			item = _coreConfigurationHelper.parseConfigurationVariables(
			queryContext, configuration.getJSONObject(i));

			String suggesterType = item.getString(SuggesterConfigurationKeys.SUGGESTER_TYPE);

			if (SuggesterConfigurationValues.PHRASE_SUGGESTER.equals(suggesterType)) {
				suggester = createPhraseSuggester(item, queryContext, keywords);
			}
			else if (SuggesterConfigurationValues.COMPLETION_SUGGESTER.equals(suggesterType)) {
				suggester = createCompletionSuggester(
					item, queryContext, keywords);
			}

			if (suggester != null) {
				suggesters.add(suggester);
			}
		}
		
		return suggesters;
	}
	
	/**
	 * Creates a completion suggester
	 *
	 * @param configuration
	 * @param queryContext
	 * @param keywords
	 * @return
	 * @throws Exception
	 */
	protected Suggester createCompletionSuggester(
			JSONObject configuration, QueryContext queryContext,
			String keywords)
		throws Exception {

		String fieldName = configuration.getString(
				SuggesterConfigurationKeys.FIELD_NAME);

		CompletionSuggester suggester = new CompletionSuggester(
			createSuggesterName(), fieldName, keywords);

		int size = configuration.getInt(
				SuggesterConfigurationKeys.NUMBER_OF_SUGGESTIONS, 
				_DEFAULT_NUMBER_OF_SUGGESTIONS);

		suggester.setSize(size);

		if (Validator.isNotNull(configuration.get(SuggesterConfigurationKeys.ANALYZER))) {
			suggester.setAnalyzer(configuration.getString(
					SuggesterConfigurationKeys.ANALYZER));
		}

		return suggester;
	}

	/**
	 * Creates a phrase suggester.
	 *
	 * @param configuration
	 * @param queryContext
	 * @param keywords
	 * @return
	 * @throws Exception
	 */
	protected Suggester createPhraseSuggester(
			JSONObject configuration, QueryContext queryContext,
			String keywords)
		throws Exception {

		String fieldName = configuration.getString(
				SuggesterConfigurationKeys.FIELD_NAME);

		PhraseSuggester suggester = new PhraseSuggester(
			createSuggesterName(), fieldName, keywords);

		int size = configuration.getInt(
				SuggesterConfigurationKeys.NUMBER_OF_SUGGESTIONS, 
				_DEFAULT_NUMBER_OF_SUGGESTIONS);

		suggester.setSize(size);

		if (Validator.isNotNull(configuration.get(SuggesterConfigurationKeys.ANALYZER))) {
			suggester.setAnalyzer(configuration.getString(
					SuggesterConfigurationKeys.ANALYZER));
		}

		if (Validator.isNotNull(configuration.get("confidence"))) {
			suggester.setConfidence(
				GetterUtil.getFloat(configuration.get("confidence")));
		}

		if (Validator.isNotNull(configuration.get("gram_size"))) {
			suggester.setGramSize(configuration.getInt("gram_size"));
		}

		if (Validator.isNotNull(configuration.get("max_errors"))) {
			suggester.setMaxErrors(
				GetterUtil.getFloat(configuration.get("max_errors")));
		}

		if (Validator.isNotNull(configuration.get("pre_highlight_tag"))) {
			suggester.setPreHighlightFilter(
				configuration.getString("pre_highlight_tag"));
		}

		if (Validator.isNotNull(configuration.get("post_highlight_tag"))) {
			suggester.setPreHighlightFilter(
				configuration.getString("post_highlight_tag"));
		}

		if (Validator.isNotNull(
				configuration.get("real_word_error_likelihood"))) {

			suggester.setRealWordErrorLikelihood(
				GetterUtil.getFloat(
					configuration.get("real_word_error_likelihood")));
		}

		return suggester;
	}

	/**
	 * Executes suggester search request.
	 * 
	 * @param queryContext
	 * @param suggesters
	 * @return
	 */
    protected SuggestSearchResponse executeSuggestSearchRequest(
            QueryContext queryContext, List<Suggester> suggesters) throws Exception {
    	
    	long companyId = (long)queryContext.getParameter(ParameterNames.COMPANY_ID);
    	
    	String indexName = "gsearch-query-suggestion-20101"; //"" !_coreConfigurationHelper.getSuggesterIndexName(companyId);
    			
        SuggestSearchRequest suggestSearchRequest =
                new SuggestSearchRequest(indexName);

        for (Suggester suggester : suggesters) {
        	suggestSearchRequest.addSuggester(suggester);
        }

        return _searchEngineAdapter.execute(suggestSearchRequest);
    }
    
	private static final int _DEFAULT_NUMBER_OF_SUGGESTIONS = 5;

	private static final Logger _log = LoggerFactory.getLogger(
		GSearchKeywordSuggesterImpl.class);

	private static Random _random;

	@Reference
	private CoreConfigurationHelper _coreConfigurationHelper;

	@Reference
	private QuerySuggester _querySuggester;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;
	
	@Reference
	private IndexNameBuilder _indexNameBuilder;

}