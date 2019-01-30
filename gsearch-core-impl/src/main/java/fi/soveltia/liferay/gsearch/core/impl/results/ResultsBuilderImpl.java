
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

import fi.soveltia.liferay.gsearch.core.api.constants.ConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.facet.FacetTranslator;
import fi.soveltia.liferay.gsearch.core.api.facet.FacetTranslatorFactory;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.results.ResultsBuilder;
import fi.soveltia.liferay.gsearch.core.api.results.facet.processor.FacetProcessor;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilderFactory;
import fi.soveltia.liferay.gsearch.core.api.results.item.processor.ResultItemProcessor;
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
		PortletRequest portletRequest, PortletResponse portletResponse,
		QueryContext queryContext, SearchContext searchContext, Hits hits) {

		JSONObject resultsObject = JSONFactoryUtil.createJSONObject();

		long startTime = System.currentTimeMillis();

		// Create items array

		resultsObject.put(
			"items", createItemsArray(
				portletRequest, portletResponse, queryContext, hits));

		// Create meta info array

		resultsObject.put("meta", createMetaObject(queryContext, hits));

		// Paging object

		resultsObject.put("paging", createPagingObject(queryContext, hits));

		// Create facets

		try {
			resultsObject.put(
				"facets", createFacetsArray(
					searchContext, queryContext, hits));
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
	 * Add facet processor.
	 * 
	 * @param facetProcessor
	 */
	protected void addFacetProcessor(
		FacetProcessor facetProcessor) {

		if (_facetProcessors == null) {
			_facetProcessors = new ArrayList<FacetProcessor>();
		}
		_facetProcessors.add(facetProcessor);
	}
	
	/**
	 * Add result item processor.
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
	 * @param queryContext
	 * @param facetConfiguration
	 * @return
	 * @throws Exception
	 */
	protected JSONArray createFacetsArray(
		SearchContext searchContext, QueryContext queryContext, Hits hits)
		throws Exception {

		if (hits.getLength() == 0) {
			return JSONFactoryUtil.createJSONArray();
		}
		
		Map<String, Facet> facets = searchContext.getFacets();

		if (facets == null || facets.size() == 0) {
			return JSONFactoryUtil.createJSONArray();
		}
		
		String[] facetConfiguration = queryContext.getConfiguration(ConfigurationKeys.FACET);
		
		// Get facets.

		JSONArray resultArray = JSONFactoryUtil.createJSONArray();

		List<Facet> facetsList = sortFacetList(
			ListUtil.fromCollection(facets.values()), facetConfiguration);
		
		// Get a configured facet.

		for (int i = 0; i < facetConfiguration.length; i++) {

			JSONObject configuration = null;
			Facet facet = null;

			JSONObject configurationItem =
				JSONFactoryUtil.createJSONObject(facetConfiguration[i]);

			for (Facet f : facetsList) {

				if (f.isStatic()) {
					continue;
				}
				
			
				if (f.getFieldName().equals(
					configurationItem.get("field_name"))) {
	
					configuration = configurationItem;
					facet = f;
	
					break;
				}
			}
			
			if (configuration == null) {
				
				continue;

			} else {

				FacetCollector facetCollector = facet.getFacetCollector();
	
				JSONArray termArray = JSONFactoryUtil.createJSONArray();
	
				// Process facets
	
				String facetTranslatorName = (String)configuration.get("translator_name");
				
				if (facetTranslatorName != null) {
				
					FacetTranslator translator =
						_facetTranslatorFactory.getTranslator(facetTranslatorName);

					if (translator != null) {
						termArray = translator.fromResults(
							queryContext, facetCollector, configuration);
					}
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
	
				// Put terms to results, if found.
	
				if (termArray.length() > 0) {
					
					JSONObject resultItem = JSONFactoryUtil.createJSONObject();
	
					resultItem.put(
						"field_name", configuration.get("field_name"));
					resultItem.put(
						"param_name", configuration.get("param_name"));
					resultItem.put("icon", configuration.get("icon"));
					
					if (configuration.get("hide") != null) {
						resultItem.put("hide", configuration.get("hide"));
	
					}
					resultItem.put("values", termArray);
	
					resultArray.put(resultItem);
				}
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
		QueryContext queryContext, Hits hits) {

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

				String link = resultItemBuilder.getLink(
					portletRequest, portletResponse, document,
					queryContext);
				
				jsonObject.put(
					"link", link);
				
				// Redirect
				
				jsonObject.put("redirect", GSearchUtil.getRedirect(portletRequest, link));

				// Additional metadata.

				jsonObject.put(
					"metadata",
					resultItemBuilder.getMetadata(portletRequest, document));
				
				// Execute result item processors

				executeResultItemProcessors(
					portletRequest, queryContext, document, resultItemBuilder,
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
	protected JSONObject createMetaObject(QueryContext queryContext, Hits hits) {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		// If this parameter is populated, there was an alternate search.

		String originalQueryTerms = queryContext.getOriginalKeywords();

		if (originalQueryTerms != null) {
			jsonObject.put("originalQueryTerms", originalQueryTerms);
		}

		jsonObject.put("queryTerms", queryContext.getKeywords());

		jsonObject.put(
			"executionTime", String.format("%.3f", hits.getSearchTime()));

		jsonObject.put("querySuggestions", hits.getQuerySuggestions());

		jsonObject.put("start", getStart(queryContext, hits));

		jsonObject.put("totalPages", getPageCount(queryContext, hits));

		jsonObject.put("totalHits", hits.getLength());

		return jsonObject;
	}

	/**
	 * Create paging object.
	 * 
	 * @return paging JSON object
	 */
	protected JSONObject createPagingObject(
		QueryContext queryContext, Hits hits) {

		JSONObject pagingObject = JSONFactoryUtil.createJSONObject();

		int totalHits = hits.getLength();

		if (totalHits == 0) {
			return pagingObject;
		}

		// Count of pages to show at once in the paging bar.

		int pagesToShow = 10;
		int pageSize = queryContext.getPageSize();
		int start = getStart(queryContext, hits);
		int pageCount = getPageCount(queryContext, hits);

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
	 * @param queryContext
	 * @param document
	 * @param resultItemBuilder
	 * @param resultItem
	 */
	protected void executeResultItemProcessors(
		PortletRequest portletRequest, QueryContext queryContext,
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
						portletRequest, queryContext, document,
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
	 * Remove facet processor.
	 * 
	 * @param facetProcessor
	 */
	protected void remoceFacetProcessor(
		FacetProcessor facetProcessor) {

		_facetProcessors.remove(facetProcessor);
	}	
	
	/**
	 * Remove a result item processor.
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
	private int getPageCount(QueryContext queryContext, Hits hits) {

		return (int) Math.ceil(
			hits.getLength() * 1.0 / queryContext.getPageSize());
	}

	/**
	 * Check start parameter. We might get a start parameter higher than hits
	 * total. In that case the last page is returned and start has to be
	 * adjusted.
	 * 
	 * @return
	 */
	private int getStart(QueryContext queryContext, Hits hits) {

		int pageSize = queryContext.getPageSize();
		int totalHits = hits.getLength();
		int start = queryContext.getStart();

		if (totalHits < start) {

			start = (getPageCount(queryContext, hits) - 1) * pageSize;

			if (start < 0) {
				start = 0;
			}
		}

		return start;
	}

	private static final Logger _log =
		LoggerFactory.getLogger(ResultsBuilderImpl.class);

	@Reference(
		bind = "addFacetProcessor", 
		cardinality = ReferenceCardinality.MULTIPLE, 
		policy = ReferencePolicy.DYNAMIC, 
		service = FacetProcessor.class,
		unbind = "remoceFacetProcessor"
	)
	private volatile List<FacetProcessor> _facetProcessors;

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
