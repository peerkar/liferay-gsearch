
package fi.soveltia.liferay.gsearch.web.search;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.MultiValueFacet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcher;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManager;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;
import fi.soveltia.liferay.gsearch.web.search.query.QueryBuilder;
import fi.soveltia.liferay.gsearch.web.search.query.QueryParams;
import fi.soveltia.liferay.gsearch.web.search.results.ResultsBuilder;

/**
 * GSearch
 * 
 * @author Petteri Karttunen
 */
@Component(immediate = true, service = GSearch.class)
public class GSearchImpl implements GSearch {

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

	protected JSONObject getResults()
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) _portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		Query query = _queryBuilder.buildQuery(_portletRequest, _queryParams);

		// Set searchcontext

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(themeDisplay.getCompanyId());
		searchContext.setIncludeStagingGroups(false);
		searchContext.setKeywords(_queryParams.getKeywords());
		searchContext.setStart(_queryParams.getStart());
		searchContext.setEnd(_queryParams.getEnd());
		searchContext.setSorts(_queryParams.getSorts());

		// Queryconfig

		QueryConfig queryConfig = new QueryConfig();
		queryConfig.setHighlightEnabled(true);
		searchContext.setQueryConfig(queryConfig);

		// Set facets

		setFacets(searchContext);

		Hits hits = execute(searchContext, query);

		// Build results object

		JSONObject resultsObject = JSONFactoryUtil.createJSONObject();

		long startTime = System.currentTimeMillis();

		resultsObject.put(
			"items",
			_resultsBuilder.getItemsArray(
				_portletRequest, _portletResponse, hits,
				_gSearchDisplayConfiguration));

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Results processing took: " +
					(System.currentTimeMillis() - startTime));
		}

		resultsObject.put("meta", createMetaObject(hits));
		resultsObject.put("paging", createPagingObject(hits));
		resultsObject.put("facets", createFacetsArray(searchContext));

		return resultsObject;
	}

	/**
	 * Execute search
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

		FacetedSearcher facetedSearcher =
			facetedSearcherManager.createFacetedSearcher();

		Hits hits = facetedSearcher.search(searchContext);

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
	 * Get facets from response
	 * 
	 * @param searchContext
	 * @return JSONArray
	 */
	protected JSONArray createFacetsArray(SearchContext searchContext) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		// Get facets

		Map<String, Facet> facets = searchContext.getFacets();
		List<Facet> facetsList = ListUtil.fromCollection(facets.values());

		for (Facet facet : facetsList) {
			if (facet.isStatic()) {
				continue;
			}
			FacetCollector facetCollector = facet.getFacetCollector();
			List<TermCollector> termCollectors =
				facetCollector.getTermCollectors();
			for (TermCollector tc : termCollectors) {

				JSONObject item = JSONFactoryUtil.createJSONObject();
				item.put("term", tc.getTerm());
				item.put("frequency", tc.getFrequency());
				jsonArray.put(item);

				if (_log.isDebugEnabled()) {
					_log.debug(tc.getTerm() + ":" + tc.getFrequency());
				}
			}
		}
		return jsonArray;
	}

	/**
	 * Create results meta information object.
	 * 
	 * @param hits
	 * @return JSONObject
	 */
	protected JSONObject createMetaObject(Hits hits) {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		String[] queryTerms = _queryParams.getKeywords().split(" ");

		jsonObject.put("queryTerms", Arrays.asList(queryTerms));

		jsonObject.put(
			"executionTime", String.format("%.3f", hits.getSearchTime()));
		jsonObject.put("querySuggestions", hits.getQuerySuggestions().length);
		jsonObject.put("start", _queryParams.getStart());

		int pageCount = (int) Math.ceil(
			hits.getLength() * 1.0 / _queryParams.getPageSize());
		jsonObject.put("totalPages", pageCount);

		jsonObject.put("totalHits", hits.getLength());

		return jsonObject;
	}

	/**
	 * Create paging object.
	 * 
	 * @param hits
	 * @return JSONObject
	 */
	protected JSONObject createPagingObject(Hits hits) {

		JSONObject pagingObject = JSONFactoryUtil.createJSONObject();

		// Count of pages to show at once in the paging bar.

		int pagesToShow = 10;
		int pageSize = _queryParams.getPageSize();
		int totalHits = hits.getLength();
		int start = _queryParams.getStart();

		if (totalHits == 0) {
			return pagingObject;
		}

		// Number of pages total.

		int pageCount = (int) Math.ceil(totalHits * 1.0 / pageSize);

		// Page number to start from.

		int currentPage = ((int) Math.floor((start + 1) / pageSize)) + 1;
		pagingObject.put("currentPage", currentPage);

		// Page number to start to loop from

		int loopStart = 1;

		// Page number to loop to

		int loopEnd = pagesToShow;

		if (currentPage > pagesToShow) {
			loopStart = currentPage - (pagesToShow / 2);
			loopEnd = currentPage + (pagesToShow / 2);
		}

		if (loopEnd > pageCount) {
			loopEnd = pageCount;
		}

		// Previous and next buttons

		int prevStart = -1;

		if (currentPage > pagesToShow) {
			prevStart = (loopStart - 2) * pageSize;
			pagingObject.put("prevStart", prevStart);
		}

		int nextStart = -1;

		if (pageCount > loopEnd) {
			nextStart = loopEnd * pageSize;
			pagingObject.put("nextStart", nextStart);
		}

		// Create paging set

		JSONArray pageArray = JSONFactoryUtil.createJSONArray();

		for (int i = loopStart; i <= loopEnd; i++) {

			JSONObject pageObject = JSONFactoryUtil.createJSONObject();

			pageObject.put("number", i);
			pageObject.put("start", (i - 1) * pageSize);

			if (i == currentPage) {
				pageObject.put("selected", true);
			}
			pageArray.put(pageObject);
		}

		pagingObject.put("pages", pageArray);

		return pagingObject;
	}

	/**
	 * Set facets to request for
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

	@Reference
	protected FacetedSearcherManager facetedSearcherManager;

	@Reference
	protected QueryBuilder _queryBuilder;

	@Reference
	protected ResultsBuilder _resultsBuilder;

	private GSearchDisplayConfiguration _gSearchDisplayConfiguration;
	private QueryParams _queryParams;
	private PortletRequest _portletRequest;
	private PortletResponse _portletResponse;

	private static final Log _log = LogFactoryUtil.getLog(GSearchImpl.class);
}
