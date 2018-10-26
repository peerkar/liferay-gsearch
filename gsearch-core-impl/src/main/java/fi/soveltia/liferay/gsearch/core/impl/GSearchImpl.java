
package fi.soveltia.liferay.gsearch.core.impl;

import com.liferay.portal.kernel.exception.PortalException;
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

import fi.soveltia.liferay.gsearch.core.api.params.QueryParamsBuilder;
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
		PortletRequest portletRequest, PortletResponse portletResponse)
		throws Exception {

		JSONObject responseObject = null;
		JSONObject unfilteredResponseObject = null;

		// Build query parameters object.

		QueryParams queryParams = null;
		QueryParams unfilteredQueryParams = null;

		try {
			queryParams = _queryParamsBuilder.buildQueryParams(portletRequest);
			unfilteredQueryParams = _queryParamsBuilder.buildUnfilteredQueryParams(portletRequest);
		} catch (PortalException e) {

			_log.error(e, e);

			return null;
		}

		// Try to get search results.

		try {
			responseObject = getResults(portletRequest, portletResponse, queryParams);

			// fetch unfiltered results only if there is a filter applied
			if (!queryParams.equals(unfilteredQueryParams)) {
				unfilteredResponseObject = getResults(portletRequest, portletResponse, unfilteredQueryParams);
			}

			// overwrite meta.typeCounts in responseObject if necessary
			if (unfilteredResponseObject != null) {
				updateTypeCountsToResponse(responseObject, unfilteredResponseObject);
			}

		} catch (Exception e) {

			_log.error(e, e);

			return null;
		}

		return responseObject;
	}

	private void updateTypeCountsToResponse(JSONObject responseObject, JSONObject responseObjectWithTypeCounts) {

		JSONObject typeCounts = null;
		if (responseObjectWithTypeCounts.has("meta") &&
			responseObjectWithTypeCounts.getJSONObject("meta") != null) {
			JSONObject meta = responseObjectWithTypeCounts.getJSONObject("meta");
			if (meta.has("typeCounts") && (meta.getJSONObject("typeCounts") != null)) {
				typeCounts = meta.getJSONObject("typeCounts");
			}
		}

		if (responseObject.has("meta") && (responseObject.getJSONObject("meta") != null) && (typeCounts != null)) {
			responseObject.getJSONObject("meta").put("typeCounts", typeCounts);
		}
	}

	/**
	 * Add query post processor to the list.
	 *
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
			if (query.getPreBooleanFilter() != null) {
				_log.debug("Pre boolean filters: " + query.getPreBooleanFilter().toString());
			}
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
		SearchContext searchContext, Hits hits, PortletRequest portletRequest, QueryParams queryParams) {

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
	 * Get results object.
	 *
	 * @return results as a JSON object
	 * @throws Exception
	 */
	protected JSONObject getResults(PortletRequest portletRequest, PortletResponse portletResponse, QueryParams queryParams)
		throws Exception {

		Query query = _queryBuilder.buildQuery(portletRequest, queryParams);

		// Create SearchContext.

		SearchContext searchContext = getSearchContext(portletRequest, queryParams);

		// Execute search.

		Hits hits = execute(searchContext, query);

		// Executre query post processors.

		executeQueryPostProcessors(searchContext, hits, portletRequest, queryParams);

		// Build results JSON object.

		JSONObject resultsObject = _resultsBuilder.buildResults(
			portletRequest, portletResponse, queryParams, searchContext,
			hits);

		return resultsObject;
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
	 * Remove a query post processor from list.
	 *
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

	@Reference(unbind = "-")
	protected void setQueryParamsBuilder(QueryParamsBuilder queryParamsBuilder) {

		_queryParamsBuilder = queryParamsBuilder;
	}

	@Reference
	protected QueryParamsBuilder _queryParamsBuilder;

	@Reference(
		bind = "addQueryPostProcessor",
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		service = QueryPostProcessor.class,
		unbind = "removeQueryPostProcessor"
	)
	private volatile List<QueryPostProcessor> _queryPostProcessors = null;

	private static final Log _log = LogFactoryUtil.getLog(GSearchImpl.class);
}
