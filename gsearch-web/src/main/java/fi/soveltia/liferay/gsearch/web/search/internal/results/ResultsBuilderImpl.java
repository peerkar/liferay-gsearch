
package fi.soveltia.liferay.gsearch.web.search.internal.results;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;
import fi.soveltia.liferay.gsearch.web.search.internal.queryparams.QueryParams;
import fi.soveltia.liferay.gsearch.web.search.results.ResultsBuilder;
import fi.soveltia.liferay.gsearch.web.search.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.web.search.results.item.ResultItemBuilderFactory;

/**
 * Results builder implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ResultsBuilder.class
)
public class ResultsBuilderImpl implements ResultsBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject buildResults(
		PortletRequest portletRequest, PortletResponse portletResponse,
		QueryParams queryParams, SearchContext searchContext, Hits hits,
		GSearchDisplayConfiguration gSearchDisplayConfiguration) {

		_hits = hits;
		_portletRequest = portletRequest;
		_portletResponse = portletResponse;
		_queryParams = queryParams;
		_gSearchDisplayConfiguration = gSearchDisplayConfiguration;

		JSONObject resultsObject = JSONFactoryUtil.createJSONObject();

		long startTime = System.currentTimeMillis();

		resultsObject.put("items", createItemsArray());
		resultsObject.put("meta", createMetaObject());
		resultsObject.put("paging", createPagingObject());
		resultsObject.put("facets", createFacetsArray(searchContext));

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Results processing took: " +
					(System.currentTimeMillis() - startTime));
		}
		return resultsObject;
	}

	/**
	 * Create facets array for the results.
	 * 
	 * @param searchContext
	 * @return facets as JSON array
	 */
	protected JSONArray createFacetsArray(SearchContext searchContext) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		// Get facets.

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
	 * Create array of result items as JSON array.
	 * 
	 * @return JSON array of result items
	 */
	protected JSONArray createItemsArray() {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		Document[] docs = _hits.getDocs();

		if (_hits == null || docs.length == 0) {
			return jsonArray;
		}
		
		// Show small images beside the result text?
		
		boolean showImages = _gSearchDisplayConfiguration.showSmallImages();


		// Loop through search result documents and create the
		// JSON array of items to be delivered for UI

		for (int i = 0; i < docs.length; i++) {

			Document document = docs[i];

			try {

				if (_log.isDebugEnabled()) {
					_log.debug(
						"##############################################");

					for (Entry<String, Field> e : document.getFields().entrySet()) {
						_log.debug(e.getKey() + ":" + e.getValue().getValue());
					}
				}

				JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

				// Get item type specific item result builder

				ResultItemBuilder resultItemBuilder =
					_resultsBuilderFactory.getResultBuilder(
						_portletRequest, _portletResponse, document,
						_gSearchDisplayConfiguration.assetPublisherPage());

				// Title

				jsonObject.put("title", resultItemBuilder.getTitle());

				// Date

				jsonObject.put("date", resultItemBuilder.getDate());

				// Description

				jsonObject.put("description", resultItemBuilder.getDescription());

				// Image src

				if (showImages) {
					jsonObject.put("imageSrc", resultItemBuilder.getImageSrc());
				}
				 
				// Type

				jsonObject.put("type", resultItemBuilder.getType());

				// Link

				jsonObject.put("link", resultItemBuilder.getLink());

				// Put single item to result array

				jsonArray.put(jsonObject);

			}
			catch (Exception e) {
				_log.error(e, e);
			}
		}

		return jsonArray;
	}

	/**
	 * Create meta information object for the results.
	 * 
	 * @param hits
	 * @return meta information JSON object
	 */
	protected JSONObject createMetaObject() {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		String[] queryTerms = _queryParams.getKeywords().split(" ");

		// If this parameter is populated, there was an alternate search.
		
		String originalQueryTerms = _queryParams.getOriginalKeywords();
		
		if (originalQueryTerms != null) {
			jsonObject.put("originalQueryTerms", originalQueryTerms);
		}
		
		jsonObject.put("queryTerms", Arrays.asList(queryTerms));

		jsonObject.put(
			"executionTime", String.format("%.3f", _hits.getSearchTime()));
		jsonObject.put("querySuggestions", _hits.getQuerySuggestions());
		jsonObject.put("start", _queryParams.getStart());

		int pageCount = (int) Math.ceil(
			_hits.getLength() * 1.0 / _queryParams.getPageSize());
		jsonObject.put("totalPages", pageCount);

		jsonObject.put("totalHits", _hits.getLength());

		return jsonObject;
	}

	/**
	 * Create paging object.
	 * 
	 * @param hits
	 * @return paging JSON object
	 */
	protected JSONObject createPagingObject() {

		JSONObject pagingObject = JSONFactoryUtil.createJSONObject();

		// Count of pages to show at once in the paging bar.

		int pagesToShow = 10;
		int pageSize = _queryParams.getPageSize();
		int totalHits = _hits.getLength();
		int start = _queryParams.getStart();

		// Check start. We cannot get the start from Hits object but
		// it might be that the actual start is not what we 
		// requested (if there are less results than the starting point)
		
		if (totalHits < start) {
			start = 0;
		}
		
		if (totalHits == 0) {
			return pagingObject;
		}

		// Number of pages total.

		int pageCount = (int) Math.ceil(totalHits * 1.0 / pageSize);

		// Page number to start from.

		int currentPage = ((int) Math.floor((start + 1) / pageSize)) + 1;
		pagingObject.put("currentPage", currentPage);

		// Page number to start to loop from.

		int loopStart = 1;

		// Page number to loop to.

		int loopEnd = pagesToShow;

		if (currentPage > pagesToShow) {
			loopStart = currentPage - (pagesToShow / 2);
			loopEnd = currentPage + (pagesToShow / 2);
		}

		if (loopEnd > pageCount) {
			loopEnd = pageCount;
		}

		// Previous and next buttons.

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

		// Create paging set.

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

	@Reference
	protected ResultItemBuilderFactory _resultsBuilderFactory;

	private GSearchDisplayConfiguration _gSearchDisplayConfiguration;
	private Hits _hits;
	private PortletRequest _portletRequest;
	private PortletResponse _portletResponse;
	private QueryParams _queryParams;

	private static final Log _log =
		LogFactoryUtil.getLog(ResultsBuilderImpl.class);
}
