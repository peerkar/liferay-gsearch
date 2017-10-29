
package fi.soveltia.liferay.gsearch.web.search.internal.query.processor;

import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexSearcherHelper;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.ArrayUtil;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;
import fi.soveltia.liferay.gsearch.web.search.internal.queryparams.QueryParams;
import fi.soveltia.liferay.gsearch.web.search.query.QueryBuilder;
import fi.soveltia.liferay.gsearch.web.search.query.processor.QuerySuggestionsProcessor;

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
	immediate = true, 
	service = QuerySuggestionsProcessor.class
)
public class QuerySuggestionsProcessorImpl
	implements QuerySuggestionsProcessor {

	@SuppressWarnings("unchecked")
	@Override
	public boolean process(
		PortletRequest portletRequest, SearchContext searchContext,
		GSearchDisplayConfiguration gSearchDisplayConfiguration,
		QueryParams queryParams, Hits hits)
		throws Exception {

		if (!gSearchDisplayConfiguration.enableQuerySuggestions()) {
			return true;
		}

		if (hits.getLength() >= gSearchDisplayConfiguration.querySuggestionsHitsThreshold()) {
			return true;
		}

		// Have to put keywords here to searchcontext because
		// suggestKeywordQueries() expects them to be there

		searchContext.setKeywords(queryParams.getKeywords());

		// Get suggestions

		String[] querySuggestions = _indexSearcherHelper.suggestKeywordQueries(
			searchContext, gSearchDisplayConfiguration.querySuggestionsMax());

		querySuggestions =
			ArrayUtil.remove(querySuggestions, searchContext.getKeywords());

		// Do alternative search based on suggestions (if found)

		if (ArrayUtil.isNotEmpty(querySuggestions)) {

			// New keywords is plainly the first in the list.

			queryParams.setOriginalKeywords(queryParams.getKeywords());

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

	@Reference(unbind = "-")
	protected void setIndexSearchHelper(
		IndexSearcherHelper indexSearcherHelper) {

		_indexSearcherHelper = indexSearcherHelper;
	}

	@Reference
	protected IndexSearcherHelper _indexSearcherHelper;

	@Reference
	protected QueryBuilder _queryBuilder;
}
