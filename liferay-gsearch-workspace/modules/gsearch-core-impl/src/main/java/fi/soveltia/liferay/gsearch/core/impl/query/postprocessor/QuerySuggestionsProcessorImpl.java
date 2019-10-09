
package fi.soveltia.liferay.gsearch.core.impl.query.postprocessor;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.search.IndexSearcherHelper;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.aggregation.AggregationResult;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.aggregation.AggregationsBuilder;
import fi.soveltia.liferay.gsearch.core.api.configuration.CoreConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.QueryBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.postprocessor.QueryPostProcessor;
import fi.soveltia.liferay.gsearch.core.api.suggest.GSearchKeywordSuggester;
import fi.soveltia.liferay.gsearch.core.impl.configuration.KeywordSuggesterConfiguration;

/**
 * Query suggestions post processor.
 *
 * This class populates hits object with query suggestions and does an
 * alternative search, depending on the configuration. Original
 * com.liferay.portal.search.internal.hits.QuerySuggestionHitsProcessor
 *
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.core.impl.configuration.KeywordSuggesterConfiguration",
	immediate = true, 
	service = QueryPostProcessor.class
)
public class QuerySuggestionsProcessorImpl implements QueryPostProcessor {

	@Override
	public boolean process(
			QueryContext queryContext, SearchSearchResponse searchResponse) 
					throws Exception {		

		if (_log.isDebugEnabled()) {
			_log.debug("Processing QuerySuggestions");
		}

		if (!_keywordSuggesterConfiguration.isQuerySuggestionsEnabled()) {
			return true;
		}
		
		if (_log.isDebugEnabled()) {
			_log.debug("QuerySuggestions are enabled.");
		}

		SearchHits searchHits = searchResponse.getSearchHits();

		if (searchHits.getSearchHits().size() >=
				_keywordSuggesterConfiguration.
					querySuggestionsHitsThreshold()) {

			if (_log.isDebugEnabled()) {
				_log.debug("Hits threshold was exceeded. Returning.");
			}

			return true;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Below threshold. Getting suggestions.");
		}


		if (_log.isDebugEnabled()) {
			_log.debug("Original keywords: " + queryContext.getKeywords());
		}

		// Get suggestions

		String[] querySuggestions =
			_gSearchKeywordSuggester.getSuggestionsAsStringArray(queryContext);

		querySuggestions = ArrayUtil.remove(
			querySuggestions, queryContext.getKeywords());

		if (_log.isDebugEnabled()) {
			_log.debug("Query suggestions size: " + querySuggestions.length);
		}

		// Do alternative search based on suggestions (if found).

		if (ArrayUtil.isNotEmpty(querySuggestions)) {
			
			if (_log.isDebugEnabled()) {
				_log.debug("Suggestions found.");
			}

			// New keywords is plainly the first in the list.

			queryContext.setOriginalKeywords(queryContext.getKeywords());

			if (_log.isDebugEnabled()) {
				_log.debug("Using querySuggestions[0] for alternative search.");
			}

			queryContext.setKeywords(querySuggestions[0]);
			
			// Remove the new keywords from query suggestions.
			
			if (querySuggestions.length > 0) {
				querySuggestions = ArrayUtil.remove(
					querySuggestions, querySuggestions[0]);
			} else {
				querySuggestions = new String[] {};
			}
			
			SearchSearchResponse newResponse = doNewSearch(queryContext);

			// Copy new values to the response.
			
			if (newResponse.getAggregationResultsMap() != null) {
				for (Entry<String, AggregationResult> entry : 
						newResponse.getAggregationResultsMap().entrySet()) {
					searchResponse.addAggregationResult(entry.getValue());
				}			
			}
			searchResponse.setSearchHits(newResponse.getSearchHits());
			searchResponse.getHits().copy(newResponse.getHits());
		}

		searchResponse.getHits().setQuerySuggestions(querySuggestions);

		return true;
	}

	
	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_keywordSuggesterConfiguration = ConfigurableUtil.createConfigurable(
			KeywordSuggesterConfiguration.class, properties);
	}

	protected void addAggregationBuilder(
		AggregationsBuilder aggregationBuilder) {

		if (_aggregationBuilders == null) {
			_aggregationBuilders = new ArrayList<>();
		}

		_aggregationBuilders.add(aggregationBuilder);
	}	
	
	/**
	 * Does a new search with suggested keywords.
	 * 
	 * @param queryContext
	 * @return
	 * @throws Exception
	 */
	protected SearchSearchResponse doNewSearch(QueryContext queryContext) throws Exception {
		
		BooleanQuery query = _queryBuilder.buildSearchQuery(queryContext);
		
		BooleanQuery rescoreQuery = 
				_queryBuilder.buildRescoreQuery(queryContext);
		
		List<Aggregation> aggregations = getAggregations(queryContext);

		return execute(queryContext, query, rescoreQuery, aggregations);
	}
	
	/**
	 * Executes search.
	 * 
	 * @param queryContext
	 * @param query
	 * @param rescoreQuery
	 * @param aggregations
	 * @return
	 * @throws Exception
	 */
	protected SearchSearchResponse execute(
			QueryContext queryContext, BooleanQuery query,
			BooleanQuery rescoreQuery, List<Aggregation>aggregations)
		throws Exception {
		
		if (_log.isDebugEnabled()) {
			_log.debug("Executing search with query: " + query.toString());
		}
		
		long companyId = (long)queryContext.getParameter(ParameterNames.COMPANY_ID);
		Locale locale = (Locale)queryContext.getParameter(ParameterNames.LOCALE);
		boolean explain = GetterUtil.getBoolean(
				queryContext.getParameter(ParameterNames.SET_EXPLAIN), false);
		Query postFilterQuery =(Query)queryContext.getParameter(ParameterNames.POST_FILTER_QUERY);
		
        SearchSearchRequest searchRequest = new SearchSearchRequest();

        searchRequest.setIndexNames(_coreConfigurationHelper.getSearchIndexNames(companyId));

        if (aggregations != null ) {
        	for (Aggregation aggregation: aggregations) {
        		searchRequest.addAggregation(aggregation);
        	}
        }
		
		if (postFilterQuery != null) {
			searchRequest.setPostFilterQuery(postFilterQuery);
		}
        searchRequest.setQuery(query);
        searchRequest.setSize(queryContext.getPageSize());
        searchRequest.setStart(queryContext.getStart());

        if (!rescoreQuery.hasClauses() && queryContext.getSorts() != null) {
        	searchRequest.addSorts(queryContext.getSorts());
        } else {
    		searchRequest.setRescoreQuery(rescoreQuery);
        }
		searchRequest.setHighlightEnabled(_coreConfigurationHelper.isHighlightEnabled());
		searchRequest.setHighlightFragmentSize(_coreConfigurationHelper.getHighlightFragmentSize());
		searchRequest.setHighlightSnippetSize(_coreConfigurationHelper.getHighlightSnippetSize());
		searchRequest.setHighlightRequireFieldMatch(true);
		searchRequest.setHighlightFieldNames(_coreConfigurationHelper.getHightlightFields(locale));
		searchRequest.setLocale((Locale)queryContext.getParameter(ParameterNames.LOCALE));
		searchRequest.setExplain(explain);
		
		SearchSearchResponse searchSearchResponse = 
				_searchEngineAdapter.execute(searchRequest);
			
		if (_log.isDebugEnabled()) {
			if (searchSearchResponse != null) {
				_log.debug("Request string: " + searchSearchResponse.getSearchRequestString());
				_log.debug("Hits: " + searchSearchResponse.getCount());
				_log.debug("Time:" + searchSearchResponse.getExecutionTime());
			}
		}
		
		return searchSearchResponse;
	}

	
	/**
	 * Gets aggregations for the search request.
	 * 
	 * @param queryContext
	 * @return
	 */
	protected List<Aggregation> getAggregations(QueryContext queryContext) {
		
		if (_log.isDebugEnabled()) {
			_log.debug("Getting aggregations.");
		}

		if (_aggregationBuilders == null) {
			return null;
		}

		List<Aggregation> aggregations = new ArrayList<Aggregation>();
				
		for (AggregationsBuilder aggregationBuilder : _aggregationBuilders) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Executing " +
						aggregationBuilder.getClass().getName());
			}

			try {
				aggregations.addAll(
					aggregationBuilder.buildAggregation(queryContext));
			}
			catch (Exception e) {
				_log.error(e.getMessage(), e);
			}
		}

		return aggregations;
	}
	
	protected void removeAggregationBuilder(
		AggregationsBuilder aggregationBuilder) {

		_aggregationBuilders.remove(aggregationBuilder);
	}	
	
	private static final Logger _log = LoggerFactory.getLogger(
		QuerySuggestionsProcessorImpl.class);

	
	@Reference(
		bind = "addAggregationBuilder",
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC, 
		service = AggregationsBuilder.class,
		unbind = "removeAggregationBuilder"
	)
	private volatile List<AggregationsBuilder> _aggregationBuilders = null;
	
	@Reference
	private CoreConfigurationHelper _coreConfigurationHelper;

	@Reference
	private GSearchKeywordSuggester _gSearchKeywordSuggester;
	
	@Reference
	private IndexSearcherHelper _indexSearcherHelper;

	private volatile KeywordSuggesterConfiguration
		_keywordSuggesterConfiguration;

	@Reference
	private QueryBuilder _queryBuilder;
	
	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

}