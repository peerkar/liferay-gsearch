
package fi.soveltia.liferay.gsearch.core.impl.results;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.aggregation.AggregationResult;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.Field;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.highlight.HighlightField;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.constants.ConfigurationNames;
import fi.soveltia.liferay.gsearch.core.api.constants.FacetConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.constants.ResponseKeys;
import fi.soveltia.liferay.gsearch.core.api.facet.FacetProcessor;
import fi.soveltia.liferay.gsearch.core.api.facet.FacetProcessorFactory;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.results.ResultsBuilder;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilderFactory;
import fi.soveltia.liferay.gsearch.core.api.results.item.processor.ResultItemProcessor;
import fi.soveltia.liferay.gsearch.core.impl.facet.FacetProcessorFactoryImpl;
import fi.soveltia.liferay.gsearch.core.impl.util.GSearchUtil;

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
		QueryContext queryContext, SearchSearchResponse searchResponse) {

		JSONObject resultsObject = JSONFactoryUtil.createJSONObject();

		long startTime = System.currentTimeMillis();
		
		// Create items array.

		resultsObject.put(
				ResponseKeys.ITEMS, createItemsArray(
						queryContext, searchResponse));

		// Create meta info array.

		try {
			resultsObject.put(
					ResponseKeys.META, createMetaObject(
							queryContext, searchResponse));
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
		
		// Pagination.

		try {
			resultsObject.put(
					ResponseKeys.PAGINATION, createPagingObject(
							queryContext, searchResponse));
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}

		// Query suggestions.
		
		JSONArray querySuggestions = createQuerySuggestionsObject(searchResponse);
		if (querySuggestions != null) {
			resultsObject.put(
					ResponseKeys.QUERY_SUGGESTIONS, 
					querySuggestions);
		}
		
		// Create facets

		try {
			resultsObject.put(
					ResponseKeys.FACETS, createFacetsArray(
							searchResponse, queryContext));
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

	protected void addResultItemProcessor(
		ResultItemProcessor resultItemProcessor) {

		if (_resultItemProcessors == null) {
			_resultItemProcessors = new ArrayList<>();
		}

		_resultItemProcessors.add(resultItemProcessor);
	}

	/**
	 * Creates facets array for the results.
	 * 
	 * @param searchResponse
	 * @param queryContext
	 * @return
	 * @throws Exception
	 */
	protected JSONArray createFacetsArray(
			SearchSearchResponse searchResponse, QueryContext queryContext)
		throws Exception {

		JSONArray configuration = (JSONArray)queryContext.getConfiguration(
				ConfigurationNames.FACET);

		Map<String, AggregationResult> aggregations = searchResponse.getAggregationResultsMap();

		JSONArray resultArray = JSONFactoryUtil.createJSONArray();

		if (aggregations == null || configuration == null || 
				configuration.length() == 0) {
			return resultArray;
		}

		// Looping this way to keep the order of configuration.
		
		for (int i = 0; i < configuration.length(); i++) {
			
			JSONObject facetConfiguration = configuration.getJSONObject(i);

			String facetFieldName = 
					facetConfiguration.getString(FacetConfigurationKeys.FIELD_NAME);

			for (Entry<String, AggregationResult> entry : aggregations.entrySet()) {
	
				String fieldName = entry.getKey();
				
				if (!fieldName.equalsIgnoreCase(facetFieldName)) {
					continue;
				}

				List<FacetProcessor> facetProcessors = 
						_facetProcessorFactory.getProcessors(fieldName);

				if (facetProcessors.size() == 0) {
					facetProcessors = 
							_facetProcessorFactory.getProcessors(
									FacetProcessorFactoryImpl.DEFAULT_FACET_PROCESSOR_NAME);
				}

				for (FacetProcessor f : facetProcessors) {
					JSONObject resultObject = f.processFacetResults(
							queryContext, entry.getValue(), facetConfiguration);

					if (resultObject != null) {
						resultArray.put(resultObject);
					}
				}
			}
		}
		return resultArray;
	}

	/**
	 * Creates array of result items as JSON array.
	 *
	 * @return JSON array of result items
	 */
	protected JSONArray createItemsArray(QueryContext queryContext,
			SearchSearchResponse searchResponse) {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		SearchHits searchHits = searchResponse.getSearchHits();

		List<SearchHit> items = searchHits.getSearchHits();

		if ((items == null) || (items.size() == 0)) {
			return jsonArray;
		}
		
		
		int descriptionMaxLength = GetterUtil.getInteger(
				(Integer)queryContext.getConfiguration(
						ConfigurationNames.RESULT_DESCRIPTION_MAX_LENGTH), 700);

		for (SearchHit item : items) {
			Document document = item.getDocument();

			try {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"##############################################");

					_log.debug("Score: " + item.getScore());

					for (Map.Entry<String, Field> e :
							document.getFields().entrySet()) {

						_log.debug(
							e.getKey() + ":" +
								e.getValue(
								).getValue());
					}
				}

				JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

				// Get item type specific item result builder.

				ResultItemBuilder resultItemBuilder =
					_resultsBuilderFactory.getResultBuilder(document);

				// Title highlighted & raw .

				jsonObject.put(
					"title",
					resultItemBuilder.getTitle(queryContext, document));

				// Date.

				jsonObject.put(
					"date", resultItemBuilder.getDate(queryContext, document));

				// Description.

				jsonObject.put(
					"description",
					resultItemBuilder.getDescription(queryContext, document));

				// Type.

				jsonObject.put(
					"type",
					resultItemBuilder.getType(document).toLowerCase());

				// Link.

				String link = resultItemBuilder.getLink(queryContext, document);

				jsonObject.put("link", link);

				// Redirect
				// In headless access we might not have the link at all.

				if (link != null) {
					jsonObject.put(
						"redirect",
						GSearchUtil.getRedirect(queryContext, link));
				}

				// Additional metadata.

				jsonObject.put(
					"metadata",
					resultItemBuilder.getMetadata(queryContext, document));

				// Execute result item processors

				executeResultItemProcessors(
					queryContext, document, resultItemBuilder, jsonObject);

				// Include explain
				
				if (GetterUtil.getBoolean(
						queryContext.getParameter(ParameterNames.SET_EXPLAIN))) {
					jsonObject.put("explain", item.getExplanation());
				}
				
				// Set higlight fields
				
				setHightlightFields(item, jsonObject, descriptionMaxLength);
				
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
	 * Creates meta information object for the results.
	 *
	 * @return meta information JSON object
	 */
	protected JSONObject createMetaObject(
		QueryContext queryContext, SearchSearchResponse searchResponse) 
				throws Exception {

		// 7.2 new SearchHits.getSearchTime() still doesn't work in FP1.
		// Using the hits object.
		
		Hits hits = searchResponse.getHits();
		
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		// If this parameter is populated, there was an alternate search.

		String originalQueryTerms = queryContext.getOriginalKeywords();

		if (originalQueryTerms != null) {
			jsonObject.put("originalQueryTerms", originalQueryTerms);
		}

		jsonObject.put("queryTerms", queryContext.getKeywords());

		jsonObject.put(
			"executionTime", String.format("%.3f", hits.getSearchTime()));

		jsonObject.put("start", _getStart(queryContext, searchResponse.getSearchHits()));

		jsonObject.put("totalHits", hits.getLength());

		return jsonObject;
	}

	/**
	 * Creates paging object.
	 *
	 * @return paging JSON object
	 * @throws Exception 
	 */
	protected JSONObject createPagingObject(
		QueryContext queryContext, 
		SearchSearchResponse searchResponse) throws Exception {

		SearchHits searchHits = searchResponse.getSearchHits();

		JSONObject pagingObject = JSONFactoryUtil.createJSONObject();

		// ... long ...
		
		int totalHits = Math.toIntExact(searchHits.getTotalHits());

		if (totalHits == 0) {
			return pagingObject;
		}

		int pageSize = queryContext.getPageSize();
		int start = _getStart(queryContext, searchHits);
		int pageCount = _getPageCount(queryContext, totalHits);

		// Page number to start from.

		int currentPage = (int)Math.floor((start + 1) / pageSize) + 1;
		pagingObject.put("defaultActivePage", currentPage);
		pagingObject.put("totalPages", pageCount);

		return pagingObject;
	}

	/**
	 * Creates query suggestions object.
	 * 
	 * @param hits
	 * @return
	 */
	protected JSONArray createQuerySuggestionsObject(
			SearchSearchResponse searchResponse) {
		
		Hits hits = searchResponse.getHits();
		
		String[] querySuggestions = hits.getQuerySuggestions();
		
		if (querySuggestions != null && querySuggestions.length > 0) {
			return  JSONFactoryUtil.createJSONArray(querySuggestions);
		}
		
		return null;
	}
	
	/**
	 * Executes result item processors.
	 *
	 * @param portletRequest
	 * @param queryContext
	 * @param document
	 * @param resultItemBuilder
	 * @param resultItem
	 */
	protected void executeResultItemProcessors(
		QueryContext queryContext, Document document,
		ResultItemBuilder resultItemBuilder, JSONObject resultItem) {

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
						queryContext, document, resultItemBuilder, resultItem);
				}
				catch (Exception e) {
					_log.error(e.getMessage(), e);
				}
			}
			else {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Processor " +
							r.getClass(
							).getName() + " is disabled");
				}
			}
		}
	}

	protected void removeResultItemProcessor(
		ResultItemProcessor resultItemProcessor) {

		_resultItemProcessors.remove(resultItemProcessor);
	}
	
	/**
	 * Adds highlight fields to result item.
	 * 
	 * @param item
	 * @param resultObject
	 * @param maxLength
	 * @throws Exception
	 */
	protected void setHightlightFields(SearchHit item, JSONObject resultObject, int maxLength) 
			throws Exception {
		
		if (item.getHighlightFieldsMap() == null) {
			return;
		}
		
		for (Entry<String, HighlightField> entry : item.getHighlightFieldsMap().entrySet()) {
			
			StringBundler sb = new StringBundler();
			
			int i = 0;
			for (String s : entry.getValue().getFragments()) {
				
				if (i > 0) {
					sb.append("...");
				}
				
				sb.append(s);	
				i++;
			}
			
			// Use the field stem as the key. 

			String key = entry.getKey();
					
			if (key.contains("_")) {
				key = key.substring(0, key.indexOf("_"));
			}
			
			String cleanedText = GSearchUtil.stripHTML(sb.toString(), maxLength);
			
			resultObject.put(key + "_highlight", cleanedText);
		}
	}

	/**
	 * Gets page count.
	 * 
	 * @param queryContext
	 * @param searchHits
	 * @return
	 */
	private int _getPageCount(QueryContext queryContext, int totalHits) {
		
		return (int)Math.ceil(
			totalHits  * 1.0 / queryContext.getPageSize());
	}

	/**
	 * Check start parameter. 
	 * 
	 * We might get a start parameter higher than hits
	 * total. In that case the last page is returned and start has to be
	 * adjusted.
	 * 
	 * @param queryContext
	 * @param searchHits
	 * @return
	 */
	private int _getStart(QueryContext queryContext, SearchHits searchHits) 
		throws Exception {
		int pageSize = queryContext.getPageSize();
		
		// Total hits in SearchHits API is long.
		// We don't probably have more than 2,147,483,647 documents here but
		// if we do, throw an exception.

		int totalHits = Math.toIntExact(searchHits.getTotalHits());

		int start = queryContext.getStart();

		if (totalHits < start) {
			
			start = (_getPageCount(queryContext, totalHits) - 1) * pageSize;

			if (start < 0) {
				start = 0;
			}
		}

		return start;
	}

	private static final Logger _log = LoggerFactory.getLogger(
		ResultsBuilderImpl.class);

	@Reference
	private FacetProcessorFactory _facetProcessorFactory;

	@Reference(
		bind = "addResultItemProcessor",
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC, service = ResultItemProcessor.class,
		unbind = "removeResultItemProcessor"
	)
	private volatile List<ResultItemProcessor> _resultItemProcessors;

	@Reference
	private ResultItemBuilderFactory _resultsBuilderFactory;

}