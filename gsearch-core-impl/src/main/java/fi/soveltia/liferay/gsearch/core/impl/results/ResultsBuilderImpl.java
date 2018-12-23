
package fi.soveltia.liferay.gsearch.core.impl.results;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.facet.translator.FacetTranslator;
import fi.soveltia.liferay.gsearch.core.api.facet.translator.FacetTranslatorFactory;
import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.results.ResultsBuilder;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilderFactory;
import fi.soveltia.liferay.gsearch.core.api.results.item.processor.ResultItemProcessor;

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
		QueryParams queryParams, SearchContext searchContext, Hits hits) {

		JSONObject resultsObject = JSONFactoryUtil.createJSONObject();

		long startTime = System.currentTimeMillis();

		// Create items array

		resultsObject.put(
			"items", createItemsArray(
				portletRequest, portletResponse, queryParams, hits));

		// Create meta info array

		resultsObject.put("meta", createMetaObject(queryParams, hits));

		// Paging object

		resultsObject.put("paging", createPagingObject(queryParams, hits));

		// Create facets

		try {
			resultsObject.put(
				"facets", createFacetsArray(
					searchContext, queryParams));
		}
		catch (Exception e) {
			_log.error(e.getMessage(), e);
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Results processing took: " +
					(System.currentTimeMillis() - startTime));
		}
		return resultsObject;
	}

	/**
	 * Add requested, additional fields to results.
	 * 
	 * @param portletRequest
	 * @param queryParams
	 * @param document
	 * @param resultItemBuilder
	 * @param resultObject
	 */
	protected void addAdditionalFields(
		PortletRequest portletRequest, QueryParams queryParams,
		Document document, ResultItemBuilder resultItemBuilder,
		JSONObject resultItem) {


		if (queryParams.getAdditionalResultFields().isEmpty()) {
			return;
		}

		for (Entry<String, Class<?>>entry : queryParams.getAdditionalResultFields().entrySet()) {
			
			
			if (entry.getValue().isAssignableFrom(String.class)) {

				String value = document.get(entry.getKey());

				if (Validator.isNotNull(value)) {
					resultItem.put(entry.getKey(), value);
				}
				
			} else if (entry.getValue().isAssignableFrom(String[].class)) {
				
				String[]values = document.getValues(entry.getKey());
				
				if (values != null && values.length > 0 && values[0].length() > 0) {
					resultItem.put(entry.getKey(), values);
				}
			}

		}		
	}	
	
	/**
	 * Add result item processor to the list.
	 * 
	 * @param resultItemProcessor
	 */
	protected void addResultItemProcessor(
		ResultItemProcessor resultItemProcessor) {

		if (_resultItemProcessors == null) {
			_resultItemProcessors = new ArrayList<ResultItemProcessor>();
		}
		_resultItemProcessors.add(resultItemProcessor);
	}

	/**
	 * Create facets array for the results.
	 * 
	 * @param searchContext
	 * @param queryParams
	 * @param facetConfiguration
	 * @return
	 * @throws Exception
	 */
	protected JSONArray createFacetsArray(
		SearchContext searchContext, QueryParams queryParams)
		throws Exception {

		String[] configuration = queryParams.getFacetConfiguration();
		
		// Get facets.

		JSONArray resultArray = JSONFactoryUtil.createJSONArray();

		Map<String, Facet> facets = searchContext.getFacets();

		List<Facet> facetsList = sortFacetList(
			ListUtil.fromCollection(facets.values()), configuration);

		for (Facet facet : facetsList) {

			if (facet.isStatic()) {
				continue;
			}

			// Get single facet configuration

			JSONObject facetConfiguration = null;

			for (int i = 0; i < configuration.length; i++) {

				JSONObject facetItem =
					JSONFactoryUtil.createJSONObject(configuration[i]);

				if (facet.getFieldName().equals(

					facetItem.get("field_name"))) {

					facetConfiguration = facetItem;

					break;
				}
			}

			FacetCollector facetCollector = facet.getFacetCollector();

			JSONArray termArray = JSONFactoryUtil.createJSONArray();

			// Process facets

			FacetTranslator translator =
				_facetTranslatorFactory.getTranslator(facet.getFieldName());

			if (translator != null) {

				termArray = translator.translateValues(
					queryParams, facetCollector, facetConfiguration);

			}
			else {

				List<TermCollector> termCollectors =
					facetCollector.getTermCollectors();

				for (TermCollector tc : termCollectors) {

					JSONObject item = JSONFactoryUtil.createJSONObject();

					item.put("frequency", tc.getFrequency());
					item.put("name", tc.getTerm());
					item.put("term", tc.getTerm());

					termArray.put(item);
				}
			}

			// Put item to array (if items found)

			if (termArray.length() > 0) {
				JSONObject resultItem = JSONFactoryUtil.createJSONObject();

				resultItem.put(
					"paramName", facetConfiguration.get("param_name"));
				resultItem.put("icon", facetConfiguration.get("icon"));
				resultItem.put("values", termArray);
				resultItem.put(
					"isMultiValued", facetConfiguration.get("is_multivalued"));

				resultArray.put(resultItem);
			}
		}
		return resultArray;
	}

	/**
	 * Create array of result items as JSON array.
	 * 
	 * @return JSON array of result items
	 */
	protected JSONArray createItemsArray(
		PortletRequest portletRequest, PortletResponse portletResponse,
		QueryParams queryParams, Hits hits) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		Document[] docs = hits.getDocs();

		if (hits == null || docs.length == 0) {
			return jsonArray;
		}

		// Loop through search result documents and create the
		// JSON array of items to be delivered for UI

		for (int i = 0; i < docs.length; i++) {

			Document document = docs[i];

			try {

				if (_log.isDebugEnabled()) {
					_log.debug(
						"##############################################");

					_log.debug("Score: " + hits.getScores()[i]);

					for (Entry<String, Field> e : document.getFields().entrySet()) {
						_log.debug(e.getKey() + ":" + e.getValue().getValue());
					}
				}

				JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

				// Get item type specific item result builder.

				ResultItemBuilder resultItemBuilder =
					_resultsBuilderFactory.getResultBuilder(document);

				// Title highlighted & raw .

				jsonObject.put(
					"title", resultItemBuilder.getTitle(
						portletRequest, portletResponse, document, true));
				jsonObject.put(
					"title_raw", resultItemBuilder.getTitle(
						portletRequest, portletResponse, document, false));

				// Date.

				jsonObject.put(
					"date",
					resultItemBuilder.getDate(portletRequest, document));

				// Description.

				jsonObject.put(
					"description", resultItemBuilder.getDescription(
						portletRequest, portletResponse, document));

				// Type.

				jsonObject.put(
					"type", resultItemBuilder.getType(document).toLowerCase());

				// Link.

				jsonObject.put(
					"link",
					resultItemBuilder.getLink(
						portletRequest, portletResponse, document,
						queryParams));

				// Additional metadata.

				jsonObject.put(
					"metadata",
					resultItemBuilder.getMetadata(portletRequest, document));

				// Add additional fields.
					
				addAdditionalFields(
					portletRequest, queryParams, document, resultItemBuilder,
					jsonObject);
				
				// Execute result item processors

				executeResultItemProcessors(
					portletRequest, queryParams, document, resultItemBuilder,
					jsonObject);

				// Put single item to result array

				jsonArray.put(jsonObject);

			}
			catch (Exception e) {
				_log.error(e.getMessage(), e);
			}
		}

		return jsonArray;
	}

	/**
	 * Create meta information object of the results.
	 * 
	 * @return meta information JSON object
	 */
	protected JSONObject createMetaObject(QueryParams queryParams, Hits hits) {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		// If this parameter is populated, there was an alternate search.

		String originalQueryTerms = queryParams.getOriginalKeywords();

		if (originalQueryTerms != null) {
			jsonObject.put("originalQueryTerms", originalQueryTerms);
		}

		jsonObject.put("queryTerms", queryParams.getKeywords());

		jsonObject.put(
			"executionTime", String.format("%.3f", hits.getSearchTime()));

		jsonObject.put("querySuggestions", hits.getQuerySuggestions());

		jsonObject.put("start", getStart(queryParams, hits));

		jsonObject.put("totalPages", getPageCount(queryParams, hits));

		jsonObject.put("totalHits", hits.getLength());

		return jsonObject;
	}

	/**
	 * Create paging object.
	 * 
	 * @return paging JSON object
	 */
	protected JSONObject createPagingObject(
		QueryParams queryParams, Hits hits) {

		JSONObject pagingObject = JSONFactoryUtil.createJSONObject();

		int totalHits = hits.getLength();

		if (totalHits == 0) {
			return pagingObject;
		}

		// Count of pages to show at once in the paging bar.

		int pagesToShow = 10;
		int pageSize = queryParams.getPageSize();
		int start = getStart(queryParams, hits);
		int pageCount = getPageCount(queryParams, hits);

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

	/**
	 * Execute result item processors.
	 * 
	 * @param portletRequest
	 * @param queryParams
	 * @param document
	 * @param resultItemBuilder
	 * @param resultItem
	 */
	protected void executeResultItemProcessors(
		PortletRequest portletRequest, QueryParams queryParams,
		Document document, ResultItemBuilder resultItemBuilder,
		JSONObject resultItem) {

		if (_log.isDebugEnabled()) {
			_log.debug("Executing result item processors.");
		}

		if (_resultItemProcessors == null) {
			return;
		}

		for (ResultItemProcessor r : _resultItemProcessors) {

			if (r.isEnabled()) {

				try {
					r.process(
						portletRequest, queryParams, document,
						resultItemBuilder, resultItem);
				}
				catch (Exception e) {
					_log.error(e.getMessage(), e);
				}
			}
			else {

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Processor " + r.getClass().getName() + " is disabled");
				}
			}
		}
	}

	/**
	 * Remove a result item processor from list.
	 * 
	 * @param resultItemProcessor
	 */
	protected void removeResultItemProcessor(
		ResultItemProcessor resultItemProcessor) {

		_resultItemProcessors.remove(resultItemProcessor);
	}

	/**
	 * Sort facet list. Use the order of configuration.
	 * 
	 * @param facets
	 * @param configuration
	 * @return sorted facet list
	 * @throws JSONException
	 */
	protected List<Facet> sortFacetList(
		List<Facet> facets, String[] configuration)
		throws JSONException {

		List<Facet> sortedList = new ArrayList<Facet>();

		for (int i = 0; i < configuration.length; i++) {

			JSONObject facetItem =
				JSONFactoryUtil.createJSONObject(configuration[i]);

			String fieldName = facetItem.getString("field_name");

			for (Facet facet : facets) {

				if (fieldName.equals(facet.getFieldName())) {

					sortedList.add(facet);
					break;
				}
			}
		}
		return sortedList;
	}

	/**
	 * Get page count
	 * 
	 * @return
	 */
	private int getPageCount(QueryParams queryParams, Hits hits) {

		return (int) Math.ceil(
			hits.getLength() * 1.0 / queryParams.getPageSize());
	}

	/**
	 * Check start parameter. We might get a start parameter higher than hits
	 * total. In that case the last page is returned and start has to be
	 * adjusted.
	 * 
	 * @return
	 */
	private int getStart(QueryParams queryParams, Hits hits) {

		int pageSize = queryParams.getPageSize();
		int totalHits = hits.getLength();
		int start = queryParams.getStart();

		if (totalHits < start) {

			start = (getPageCount(queryParams, hits) - 1) * pageSize;

			if (start < 0) {
				start = 0;
			}
		}

		return start;
	}

	private static final Logger _log =
		LoggerFactory.getLogger(ResultsBuilderImpl.class);

	@Reference
	private FacetTranslatorFactory _facetTranslatorFactory;

	@Reference
	private ResultItemBuilderFactory _resultsBuilderFactory;

	@Reference(
		bind = "addResultItemProcessor", 
		cardinality = ReferenceCardinality.MULTIPLE, 
		policy = ReferencePolicy.DYNAMIC, 
		service = ResultItemProcessor.class,
		unbind = "removeResultItemProcessor"
	)
	private volatile List<ResultItemProcessor> _resultItemProcessors;
}
