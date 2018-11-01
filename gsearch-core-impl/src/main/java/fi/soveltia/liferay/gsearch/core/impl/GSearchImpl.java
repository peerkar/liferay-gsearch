
package fi.soveltia.liferay.gsearch.core.impl;

import com.liferay.portal.kernel.json.JSONArray;
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
		
		return getSearchResults(portletRequest, portletResponse, queryParams, null, true, true);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject getSearchResults(
		PortletRequest portletRequest, PortletResponse portletResponse,
		QueryParams queryParams, JSONArray queryConfiguration, boolean executeQueryPostProcessors, boolean processQueryContributors) 
		throws Exception {
			
		Query query = _queryBuilder.buildQuery(portletRequest, queryParams, queryConfiguration, processQueryContributors);

		// Create SearchContext.

		SearchContext searchContext = getSearchContext(portletRequest, queryParams);
		
		// Set query config.
		
		setQueryConfig(searchContext, queryParams, query);

		// Execute search.
		
		Hits hits = execute(searchContext, query);
		
		// Execute query post processors.

		if (executeQueryPostProcessors) {
		
			executeQueryPostProcessors(portletRequest, searchContext, queryParams, hits);
		}
		
		// Build results JSON object.

		JSONObject resultsObject = _resultsBuilder.buildResults(
			portletRequest, portletResponse, queryParams, searchContext,
			hits);

		return resultsObject;		
	}
	
	/**
	 * Add query post processor to the list.
	 * 
	 * @param clauseBuilder
	 */
	protected void addQueryPostProcessor(
		QueryPostProcessor queryPostProcessor) {

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
	protected void executeQueryPostProcessors(
		PortletRequest portletRequest, SearchContext searchContext, QueryParams queryParams, Hits hits) {

		if (_log.isDebugEnabled()) {
			_log.debug("Executing query post processors.");
		}

		if (_queryPostProcessors == null) {
			return;
		}

		for (QueryPostProcessor queryPostProcessor : _queryPostProcessors) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Executing " + queryPostProcessor.getClass().getName());
			}

			try {
				queryPostProcessor.process(
					portletRequest, searchContext, queryParams, hits);
			}
			catch (Exception e) {
				_log.error(e, e);
			}
		}
	}

	/**
	 * Get searchcontext.
	 * 
	 * @return searchcontext object
	 * @throws Exception
	 */
	protected SearchContext getSearchContext(PortletRequest portletRequest, QueryParams queryParams)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		SearchContext searchContext = new SearchContext();
		searchContext.setCompanyId(themeDisplay.getCompanyId());
		searchContext.setStart(queryParams.getStart());
		searchContext.setEnd(queryParams.getEnd());
		searchContext.setSorts(queryParams.getSorts());

		// Set facets.

		_facetsBuilder.setFacets(searchContext);

		return searchContext;
	}
	
	/**
	 * Set query config
	 * 
	 * @param searchContext
	 * @param queryParams
	 */
	protected void setQueryConfig(SearchContext searchContext, QueryParams queryParams, Query query) {

		// Create Queryconfig.
		
		QueryConfig queryConfig = searchContext.getQueryConfig();
		
		queryConfig.setHighlightEnabled(true);
			
		// Set highlighted fields
		
		String contentFieldLocalized =
						Field.CONTENT + "_" + queryParams.getLocale().toString();
		
		String titleFieldLocalized =
						Field.TITLE + "_" + queryParams.getLocale().toString();

		queryConfig.setHighlightFieldNames(new String[]{Field.CONTENT, contentFieldLocalized, Field.TITLE, titleFieldLocalized});
		queryConfig.setHighlightFragmentSize(SNIPPET_CONTENT_SIZE);
	
		// TODO: Of some reason queryconfig has to be set on both searchcontext and query.
		
		query.setQueryConfig(queryConfig);		
	}	

	/**
	 * Remove a query post processor from list.
	 * 
	 * @param clauseBuilder
	 */
	protected void removeQueryPostProcessor(
		QueryPostProcessor queryPostProcessor) {

		_queryPostProcessors.remove(queryPostProcessor);
	}

	@Reference(unbind = "-")
	protected void setFacetsBuilder(FacetsBuilder facetsBuilder) {

		_facetsBuilder = facetsBuilder;
	}

	@Reference(unbind = "-")
	protected void setIndexSearchHelper(
		IndexSearcherHelper indexSearcherHelper) {

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

	private ResultsBuilder _resultsBuilder;

	@Reference(
		bind = "addQueryPostProcessor", 
		cardinality = ReferenceCardinality.MULTIPLE, 
		policy = ReferencePolicy.DYNAMIC, 
		service = QueryPostProcessor.class,
		unbind = "removeQueryPostProcessor"
	)
	private volatile List<QueryPostProcessor> _queryPostProcessors = null;

	private static final int SNIPPET_CONTENT_SIZE = 50;
	
	private static final Log _log = LogFactoryUtil.getLog(GSearchImpl.class);
}
