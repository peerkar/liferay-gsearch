
package fi.soveltia.liferay.gsearch.web.search.internal;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexSearcherHelper;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.MultiValueFacet;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManager;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;
import fi.soveltia.liferay.gsearch.web.search.GSearch;
import fi.soveltia.liferay.gsearch.web.search.internal.queryparams.QueryParams;
import fi.soveltia.liferay.gsearch.web.search.query.QueryBuilder;
import fi.soveltia.liferay.gsearch.web.search.query.processor.QueryIndexerProcessor;
import fi.soveltia.liferay.gsearch.web.search.query.processor.QuerySuggestionsProcessor;
import fi.soveltia.liferay.gsearch.web.search.results.ResultsBuilder;

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
		QueryParams queryParams,
		GSearchDisplayConfiguration gSearchDisplayConfiguration)
		throws Exception {

		_portletRequest = portletRequest;
		_portletResponse = portletResponse;
		_queryParams = queryParams;
		_gSearchDisplayConfiguration = gSearchDisplayConfiguration;

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
		
		_queryIndexerProcessor.process(searchContext, _gSearchDisplayConfiguration, _queryParams, hits);

		// Process query suggestions and alternative search.
		
		_querySuggestionsProcessor.process(_portletRequest, searchContext, _gSearchDisplayConfiguration, _queryParams, hits);

		// Build results JSON object.

		JSONObject resultsObject = _resultsBuilder.buildResults(
			_portletRequest, _portletResponse, _queryParams, searchContext, hits,
			_gSearchDisplayConfiguration);

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

		_log.info("Executing search with query: " + query.toString());

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
	 */
	protected SearchContext getSearchContext() {

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

		setFacets(searchContext);

		return searchContext;
	}

	/**
	 * Set facets to request for from the backend.
	 * 
	 * @param searchContext
	 */
	protected void setFacets(SearchContext searchContext) {

		// Add entry class name facet.

		Facet assetCategoryTitlesFacet = new MultiValueFacet(searchContext);
		assetCategoryTitlesFacet.setFieldName(Field.ENTRY_CLASS_NAME);
		assetCategoryTitlesFacet.setStatic(false);
		searchContext.addFacet(assetCategoryTitlesFacet);
	}
	
	@Reference(unbind = "-")
	protected void setFacetedSearcherManager(
		FacetedSearcherManager facetedSearcherManager) {

		_facetedSearcherManager = facetedSearcherManager;
	}

	@Reference(unbind = "-")
	protected void setIndexSearchHelper(IndexSearcherHelper indexSearcherHelper) {

		_indexSearcherHelper = indexSearcherHelper;
	}

	@Reference
	protected FacetedSearcherManager _facetedSearcherManager;

	@Reference
	protected IndexSearcherHelper _indexSearcherHelper;
	
	@Reference
	protected QueryBuilder _queryBuilder;

	@Reference
	protected QueryIndexerProcessor _queryIndexerProcessor;

	@Reference
	protected QuerySuggestionsProcessor _querySuggestionsProcessor;

	@Reference
	protected ResultsBuilder _resultsBuilder;

	private GSearchDisplayConfiguration _gSearchDisplayConfiguration;
	private QueryParams _queryParams;
	private PortletRequest _portletRequest;
	private PortletResponse _portletResponse;

	private static final Log _log = LogFactoryUtil.getLog(GSearchImpl.class);
}
