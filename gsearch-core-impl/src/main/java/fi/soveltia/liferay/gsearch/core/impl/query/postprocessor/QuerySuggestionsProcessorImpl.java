
package fi.soveltia.liferay.gsearch.core.impl.query.postprocessor;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexSearcherHelper;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.util.Map;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.query.QueryBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.postprocessor.QueryPostProcessor;
import fi.soveltia.liferay.gsearch.core.api.suggest.GSearchKeywordSuggester;
import fi.soveltia.liferay.gsearch.core.impl.configuration.ModuleConfiguration;

/**
 * This class populates hits object with query suggestions and does an
 * alternative search, depending on the configuration.
 * 
 * Original com.liferay.portal.search.internal.hits.QuerySuggestionHitsProcessor
 *
 * @author Michael C. Han
 * @author Josef Sustacek
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.core.configuration.GSearchCore", 
	immediate = true, 
	service = QueryPostProcessor.class
)
public class QuerySuggestionsProcessorImpl
	implements QueryPostProcessor {

	@SuppressWarnings("unchecked")
	@Override
	public boolean process(
		PortletRequest portletRequest, SearchContext searchContext,
		QueryParams queryParams, Hits hits)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("Processing QuerySuggestions");
		}
		
		if (!_moduleConfiguration.enableQuerySuggestions()) {
			return true;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("QuerySuggestions are enabled.");
		}
			
		if (hits.getLength() >= _moduleConfiguration.querySuggestionsHitsThreshold()) {
			
			if (_log.isDebugEnabled()) {
				_log.debug("Hits threshold was exceeded. Returning.");
			}

			return true;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Below threshold. Getting suggestions.");
		}
		
		// Have to put keywords here to searchcontext because
		// suggestKeywordQueries() expects them to be there

		searchContext.setKeywords(queryParams.getKeywords());

		if (_log.isDebugEnabled()) {
			_log.debug("Original keywords: " + queryParams.getKeywords());
		}
		
		// Get suggestions
		
		String[] querySuggestions = _gSearchSuggester.getSuggestionsAsStringArray(portletRequest);

		querySuggestions =
			ArrayUtil.remove(querySuggestions, searchContext.getKeywords());

		if (_log.isDebugEnabled()) {
			_log.debug("Query suggestions size: " + querySuggestions.length);
		}
		
		// Do alternative search based on suggestions (if found)
		
		if (ArrayUtil.isNotEmpty(querySuggestions)) {

			if (_log.isDebugEnabled()) {
				_log.debug("Suggestions found.");
			}
			
			// New keywords is plainly the first in the list.

			queryParams.setOriginalKeywords(queryParams.getKeywords());

			if (_log.isDebugEnabled()) {
				_log.debug("Using querySuggestions[0] for alternative search.");
			}
	
			queryParams.setKeywords(querySuggestions[0]);
			
			Query query = _queryBuilder.buildQuery(portletRequest, queryParams);

			BooleanClause<?> booleanClause = BooleanClauseFactoryUtil.create(
				query, BooleanClauseOccur.MUST.getName());

			searchContext.setBooleanClauses(new BooleanClause[] {
				booleanClause
			});

			Hits alternativeHits =
				_indexSearcherHelper.search(searchContext, query);
			hits.copy(alternativeHits);
		}

		hits.setQuerySuggestions(querySuggestions);

		return true;
	}

	@Activate 
	@Modified
	protected void activate(Map<String, Object> properties) {
		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}	
	
	@Reference(unbind = "-")
	protected void setGSearchKeywordSuggester(
		GSearchKeywordSuggester gSearchSuggester) {

		_gSearchSuggester = gSearchSuggester;
	}

	@Reference(unbind = "-")
	protected void setIndexSearchHelper(
		IndexSearcherHelper indexSearcherHelper) {

		_indexSearcherHelper = indexSearcherHelper;
	}

	@Reference(unbind = "-")
	protected void setQueryBuilder(
		QueryBuilder queryBuilder) {

		_queryBuilder = queryBuilder;
	}

	private volatile ModuleConfiguration _moduleConfiguration;

	private GSearchKeywordSuggester _gSearchSuggester;
	
	private IndexSearcherHelper _indexSearcherHelper;

	private QueryBuilder _queryBuilder;

	private static final Log _log =
					LogFactoryUtil.getLog(QuerySuggestionsProcessorImpl.class);
}
