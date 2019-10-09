
package fi.soveltia.liferay.gsearch.core.impl;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.aggregation.Aggregation;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.GSearch;
import fi.soveltia.liferay.gsearch.core.api.aggregation.AggregationsBuilder;
import fi.soveltia.liferay.gsearch.core.api.configuration.CoreConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.QueryBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.postprocessor.QueryPostProcessor;
import fi.soveltia.liferay.gsearch.core.api.results.ResultsBuilder;

/**
 * GSearch service implementation.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = GSearch.class
)
public class GSearchImpl implements GSearch {

	@Reference
	private Queries _queries;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject getSearchResults(QueryContext queryContext)
		throws Exception {

		// Build query.

		BooleanQuery query = _queryBuilder.buildSearchQuery(queryContext);

		// Build rescore query.
		
		BooleanQuery rescoreQuery = _queryBuilder.buildRescoreQuery(queryContext);

		// Get aggregations.
		
		List<Aggregation> aggregations = getAggregations(queryContext);
				
		// Execute search.

		SearchSearchResponse searchResponse = execute(
				queryContext, query, rescoreQuery, aggregations);

		// Execute query post processors.

		if (queryContext.isQueryPostProcessorsEnabled()) {
			executeQueryPostProcessors(queryContext, searchResponse);
		}

		// Build results JSON object.

		JSONObject resultsObject = _resultsBuilder.buildResults(
				queryContext, searchResponse);

		return resultsObject;
	}

	protected void addAggregationBuilder(
		AggregationsBuilder aggregationBuilder) {

		if (_aggregationBuilders == null) {
			_aggregationBuilders = new ArrayList<>();
		}

		_aggregationBuilders.add(aggregationBuilder);
	}	
	
	protected void addQueryPostProcessor(
		QueryPostProcessor queryPostProcessor) {

		if (_queryPostProcessors == null) {
			_queryPostProcessors = new ArrayList<>();
		}

		_queryPostProcessors.add(queryPostProcessor);
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
        
        // Sorts cannot be used with rescorer (See Elasticsearch documentation)
        
        if (!rescoreQuery.hasClauses() && queryContext.getSorts() != null) {
        	searchRequest.addSorts(queryContext.getSorts());
        } else {
    		searchRequest.setRescoreQuery(rescoreQuery);
        }
        
		if (postFilterQuery != null) {
			searchRequest.setPostFilterQuery(postFilterQuery);
		}
        searchRequest.setQuery(query);
        searchRequest.setSize(queryContext.getPageSize());
        searchRequest.setStart(queryContext.getStart());
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
	 * Executes registered query post processors.
	 *
	 * @param queryContext
	 * @param searchResponse
	 */
	protected void executeQueryPostProcessors(
		QueryContext queryContext, SearchSearchResponse searchResponse) {

		if (_log.isDebugEnabled()) {
			_log.debug("Executing query post processors.");
		}

		if (_queryPostProcessors == null) {
			return;
		}

		for (QueryPostProcessor queryPostProcessor : _queryPostProcessors) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Executing " +
						queryPostProcessor.getClass(
						).getName());
			}

			try {
				queryPostProcessor.process(queryContext, searchResponse);
			}
			catch (Exception e) {
				_log.error(e.getMessage(), e);
			}
		}
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
	
	protected void removeQueryPostProcessor(
		QueryPostProcessor queryPostProcessor) {

		_queryPostProcessors.remove(queryPostProcessor);
	}

	private static final Logger _log = LoggerFactory.getLogger(
		GSearchImpl.class);

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
	private QueryBuilder _queryBuilder;

	@Reference(
		bind = "addQueryPostProcessor",
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC, 
		service = QueryPostProcessor.class,
		unbind = "removeQueryPostProcessor"
	)
	private volatile List<QueryPostProcessor> _queryPostProcessors = null;

	@Reference
	private ResultsBuilder _resultsBuilder;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;
}