
package fi.soveltia.liferay.gsearch.core.impl.query.postprocessor;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.query.QueryBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.postprocessor.QueryPostProcessor;
import fi.soveltia.liferay.gsearch.core.api.suggest.GSearchKeywordSuggester;
import fi.soveltia.liferay.gsearch.core.impl.configuration.KeywordSuggesterConfiguration;

/**
 * Query suggestions post processor.
 * 
 * This class populates hits object with query suggestions and does an
 * alternative search, depending on the configuration. Original
 * com.liferay.portal.search.internal.hits.QuerySuggestionHitsProcessor
 *
 * @author Michael C. Han
 * @author Josef Sustacek
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.core.impl.configuration.KeywordSuggesterConfiguration", 
	immediate = true, 
	service = QueryPostProcessor.class
)
public class QuerySuggestionsProcessorImpl implements QueryPostProcessor {

	@SuppressWarnings("unchecked")
	@Override
	public boolean process(
		PortletRequest portletRequest, SearchContext searchContext,
		QueryContext queryParams, Hits hits)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("Processing QuerySuggestions");
		}

		if (!_keywordSuggesterConfiguration.isQuerySuggestionsEnabled()) {
			return true;
		}
		
		if (_log.isDebugEnabled()) {
			_log.debug("QuerySuggestions are enabled.");
		}

		if (hits.getLength() >= _keywordSuggesterConfiguration.querySuggestionsHitsThreshold()) {

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

		String[] querySuggestions =
			_gSearchKeywordSuggester.getSuggestionsAsStringArray(portletRequest);

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

		_keywordSuggesterConfiguration = ConfigurableUtil.createConfigurable(
			KeywordSuggesterConfiguration.class, properties);
	}

	private static final Logger _log =
		LoggerFactory.getLogger(QuerySuggestionsProcessorImpl.class);
	
	private volatile KeywordSuggesterConfiguration _keywordSuggesterConfiguration;

	@Reference
	private GSearchKeywordSuggester _gSearchKeywordSuggester;

	@Reference
	private IndexSearcherHelper _indexSearcherHelper;

	@Reference
	private QueryBuilder _queryBuilder;
}
