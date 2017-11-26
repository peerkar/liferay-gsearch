
package fi.soveltia.liferay.gsearch.core.impl;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexSearcherHelper;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.GSearch;
import fi.soveltia.liferay.gsearch.core.api.facet.FacetsBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.QueryBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.query.processor.QueryIndexerProcessor;
import fi.soveltia.liferay.gsearch.core.api.query.processor.QuerySuggestionsProcessor;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject getSearchResults(
		PortletRequest portletRequest, PortletResponse portletResponse,
		QueryParams queryParams)
		throws Exception {

		_portletRequest = portletRequest;
		_portletResponse = portletResponse;
		_queryParams = queryParams;

		return getResults();
	}

	/**
	 * Get results object.
	 * 
	 * @return results as a JSON object
	 * @throws Exception
	 */
	protected JSONObject getResults()
		throws Exception {

		Query query = _queryBuilder.buildQuery(_portletRequest, _queryParams);
	
		// Create SearchContext.

		SearchContext searchContext = getSearchContext();

		// Execute search.

		Hits hits = execute(searchContext, query);
		
		// Process query indexing.
		
		_queryIndexerProcessor.process(searchContext, _queryParams, hits);

		// Process query suggestions and alternative search.
		
		_querySuggestionsProcessor.process(_portletRequest, searchContext, _queryParams, hits);

		// Build results JSON object.

		JSONObject resultsObject = _resultsBuilder.buildResults(
			_portletRequest, _portletResponse, _queryParams, searchContext, hits);

		return resultsObject;
	}

	/**
	 * Execute search.
	 * 
	 * @param searchContext
	 * @param query
	 * @return Hits
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected Hits execute(SearchContext searchContext, Query query)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("Executing search with query: " + query.toString());
		}
		
		BooleanClause<?> booleanClause = BooleanClauseFactoryUtil.create(
			query, BooleanClauseOccur.MUST.getName());
		
		searchContext.setBooleanClauses(new BooleanClause[] {
			booleanClause
		});

		Hits hits = _indexSearcherHelper.search(searchContext, query);

		if (_log.isDebugEnabled()) {
			_log.debug("Query: " + hits.getQuery());
			_log.debug("Hits: " + hits.getLength());
			_log.debug("Returned: " + hits.getDocs().length);
			_log.debug("Time:" + hits.getSearchTime());
			_log.debug(
				"Suggestions size: " + hits.getQuerySuggestions().length);
		}
		return hits;
	}

	/**
	 * Get searchcontext.
	 * 
	 * @return searchcontext object
	 * @throws Exception 
	 */
	protected SearchContext getSearchContext() throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) _portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		SearchContext searchContext = new SearchContext();
		searchContext.setCompanyId(themeDisplay.getCompanyId());
		searchContext.setStart(_queryParams.getStart());
		searchContext.setEnd(_queryParams.getEnd());
		searchContext.setSorts(_queryParams.getSorts());

		// Create Queryconfig.

		QueryConfig queryConfig = new QueryConfig();

		// Enable results highlighting

		queryConfig.setHighlightEnabled(true);

		searchContext.setQueryConfig(queryConfig);

		// Set facets.

		_facetsBuilder.setFacets(searchContext);

		return searchContext;
	}

	@Reference(unbind = "-")
	protected void setFacetsBuilder(FacetsBuilder facetsBuilder) {

		_facetsBuilder = facetsBuilder;
	}

	
	@Reference(unbind = "-")
	protected void setIndexSearchHelper(IndexSearcherHelper indexSearcherHelper) {

		_indexSearcherHelper = indexSearcherHelper;
	}

	@Reference(unbind = "-")
	protected void setQueryBuilder(QueryBuilder queryBuilder) {

		_queryBuilder = queryBuilder;
	}

	@Reference(unbind = "-")
	protected void setQueryIndexerProcessor(QueryIndexerProcessor queryIndexerProcessor) {

		_queryIndexerProcessor = queryIndexerProcessor;
	}

	@Reference(unbind = "-")
	protected void setQuerySuggestionsProcessor(QuerySuggestionsProcessor querySuggestionsProcessor) {

		_querySuggestionsProcessor = querySuggestionsProcessor;
	}

	@Reference(unbind = "-")
	protected void setResultsBuilder(ResultsBuilder resultsBuilder) {

		_resultsBuilder = resultsBuilder;
	}
	
	@Reference
	protected IndexSearcherHelper _indexSearcherHelper;
	
	@Reference
	protected QueryBuilder _queryBuilder;

	@Reference
	protected FacetsBuilder _facetsBuilder;
	
	@Reference
	protected QueryIndexerProcessor _queryIndexerProcessor;

	@Reference
	protected QuerySuggestionsProcessor _querySuggestionsProcessor;

	@Reference
	protected ResultsBuilder _resultsBuilder;

	private QueryParams _queryParams;
	private PortletRequest _portletRequest;
	private PortletResponse _portletResponse;

	private static final Log _log = LogFactoryUtil.getLog(GSearchImpl.class);
}
