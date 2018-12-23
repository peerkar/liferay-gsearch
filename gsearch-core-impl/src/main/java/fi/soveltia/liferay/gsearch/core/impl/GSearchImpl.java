
package fi.soveltia.liferay.gsearch.core.impl;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONObject;
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
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.GSearch;
import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.facet.FacetsBuilder;
import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.query.QueryBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.postprocessor.QueryPostProcessor;
import fi.soveltia.liferay.gsearch.core.api.results.ResultsBuilder;
import fi.soveltia.liferay.gsearch.core.impl.configuration.ModuleConfiguration;

/**
 * GSearch service implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.core.impl.configuration.ModuleConfiguration", 
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

		return getSearchResults(
			portletRequest, portletResponse, queryParams, true, true);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject getSearchResults(
		PortletRequest portletRequest, PortletResponse portletResponse,
		QueryParams queryParams, boolean executeQueryPostProcessors, 
		boolean processQueryContributors)
		throws Exception {

		// Build query
		
		Query query = _queryBuilder.buildQuery(
			portletRequest, queryParams,
			processQueryContributors);

		// Create SearchContext.

		SearchContext searchContext =
			getSearchContext(portletRequest, queryParams);

		// Set facets.

		_facetsBuilder.setFacets(searchContext, queryParams);
		 
		// Set query config.

		setQueryConfig(searchContext, queryParams, query);

		// Execute search.

		Hits hits = execute(searchContext, query);

		// Execute query post processors.

		if (executeQueryPostProcessors) {

			executeQueryPostProcessors(
				portletRequest, searchContext, queryParams, hits);
		}

		// Build results JSON object.

		JSONObject resultsObject = _resultsBuilder.buildResults(
			portletRequest, portletResponse, queryParams, searchContext, hits);

		return resultsObject;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {

		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
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

		searchContext.setBooleanClauses(
			new BooleanClause[] {
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
		PortletRequest portletRequest, SearchContext searchContext,
		QueryParams queryParams, Hits hits) {

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
				_log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Get searchcontext.
	 * 
	 * @return searchcontext object
	 * @throws Exception
	 */
	protected SearchContext getSearchContext(
		PortletRequest portletRequest, QueryParams queryParams)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		SearchContext searchContext = new SearchContext();
		
		searchContext.setCompanyId(themeDisplay.getCompanyId());
		searchContext.setStart(queryParams.getStart());
		searchContext.setEnd(queryParams.getEnd());
		searchContext.setSorts(queryParams.getSorts());

		return searchContext;
	}

	/**
	 * Set query config
	 * 
	 * @param searchContext
	 * @param queryParams
	 */
	protected void setQueryConfig(
		SearchContext searchContext, QueryParams queryParams, Query query) {

		// Create Queryconfig.

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(true);

		// Set highlighted fields

		String contentFieldLocalized =
			Field.CONTENT + "_" + queryParams.getLocale().toString();

		String titleFieldLocalized =
			Field.TITLE + "_" + queryParams.getLocale().toString();

		queryConfig.setHighlightFieldNames(
			new String[] {
				Field.CONTENT, contentFieldLocalized, Field.TITLE,
				titleFieldLocalized
			});
		queryConfig.setHighlightFragmentSize(_moduleConfiguration.highlightFragmentSize());

		// TODO: Of some reason queryconfig has to be set on both searchcontext
		// and query.

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

	private static final Logger _log =
					LoggerFactory.getLogger(GSearchImpl.class);

	@Reference
	private ConfigurationHelper _configurationHelper;

	@Reference
	private FacetsBuilder _facetsBuilder;

	@Reference
	private IndexSearcherHelper _indexSearcherHelper;

	@Reference
	private QueryBuilder _queryBuilder;

	@Reference
	private ResultsBuilder _resultsBuilder;

	@Reference(
		bind = "addQueryPostProcessor", 
		cardinality = ReferenceCardinality.MULTIPLE, 
		policy = ReferencePolicy.DYNAMIC, 
		service = QueryPostProcessor.class, 
		unbind = "removeQueryPostProcessor"
	)
	private volatile List<QueryPostProcessor> _queryPostProcessors = null;
	
	private volatile ModuleConfiguration _moduleConfiguration;
}
