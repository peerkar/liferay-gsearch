
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
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import fi.soveltia.liferay.gsearch.core.api.GSearch;
import fi.soveltia.liferay.gsearch.core.api.facet.FacetsBuilder;
import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.query.QueryBuilder;
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
	 * Add query post processor to the list.
	 * 
	 * @param clauseBuilder
	 */
    protected void addQueryPostProcessor(QueryPostProcessor queryPostProcessor) {
        if (_queryPostProcessors == null) {
        	_queryPostProcessors = new ArrayList<QueryPostProcessor>();
        }
        _queryPostProcessors.add(queryPostProcessor);
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
	 * Execute registered query post processors.
	 * 
	 * @param searchContext
	 * @param hits
	 */
	protected void executeQueryPostProcessors(SearchContext searchContext, Hits hits) {

		if (_log.isDebugEnabled()) {
			_log.debug("Executing query post processors.");
		}

		if (_queryPostProcessors == null) {
			return;
		}
		
        for (QueryPostProcessor queryPostProcessor : _queryPostProcessors) {

    		if (_log.isDebugEnabled()) {
    			_log.debug("Executing " + queryPostProcessor.getClass().getName());
    		}
    		
        	try {
        		queryPostProcessor.process(_portletRequest, searchContext, _queryParams, hits);
        	} catch(Exception e) {
        		_log.error(e, e);
        	}
        }
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
		
		// Executre query post processors.
		
		executeQueryPostProcessors(searchContext, hits);
		
		// Build results JSON object.

		JSONObject resultsObject = _resultsBuilder.buildResults(
			_portletRequest, _portletResponse, _queryParams, searchContext, hits);

		return resultsObject;
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

		// Set facets.

		_facetsBuilder.setFacets(searchContext);

		return searchContext;
	}

    /**
     * Remove a query post processor from list.
     * 
     * @param clauseBuilder
     */
    protected void removeQueryPostProcessor(QueryPostProcessor queryPostProcessor) {
    	_queryPostProcessors.remove(queryPostProcessor);
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
	protected void setResultsBuilder(ResultsBuilder resultsBuilder) {

		_resultsBuilder = resultsBuilder;
	}

	private FacetsBuilder _facetsBuilder;
	
	private IndexSearcherHelper _indexSearcherHelper;
	
	private QueryBuilder _queryBuilder;

	private PortletRequest _portletRequest;

	private PortletResponse _portletResponse;

	private ResultsBuilder _resultsBuilder;

	private QueryParams _queryParams;

    @Reference(
    	bind = "addQueryPostProcessor",
    	cardinality = ReferenceCardinality.MULTIPLE, 
    	policy = ReferencePolicy.DYNAMIC,
    	service = QueryPostProcessor.class,
    	unbind = "removeQueryPostProcessor"
    )
    private List<QueryPostProcessor> _queryPostProcessors = null;	

	private static final Log _log = LogFactoryUtil.getLog(GSearchImpl.class);
}
