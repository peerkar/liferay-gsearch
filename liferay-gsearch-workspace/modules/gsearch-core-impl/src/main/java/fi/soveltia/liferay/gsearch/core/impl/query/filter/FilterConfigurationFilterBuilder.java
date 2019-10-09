
package fi.soveltia.liferay.gsearch.core.impl.query.filter;

import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.query.RangeTermQuery;
import com.liferay.portal.search.query.TermQuery;
import com.liferay.portal.search.query.util.BooleanQueryUtilities;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.constants.FilterConfigurationValues;
import fi.soveltia.liferay.gsearch.core.api.params.FilterParameter;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.filter.FilterBuilder;

/**
 * Builds filter configuration parameters to filter query.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = FilterBuilder.class
)
public class FilterConfigurationFilterBuilder implements FilterBuilder {

	@SuppressWarnings("unchecked")
	@Override
	public void addFilters(QueryContext queryContext,
			BooleanQuery preFilterQuery, BooleanQuery postFilterQuery) 
					throws Exception {

		FilterParameter preFilter = queryContext.getFilterParameter(
			"filterConfiguration");

		if (preFilter == null) {
			return;
		}

		List<FilterParameter> filters =
			(List<FilterParameter>)preFilter.getAttribute("filters");

		BooleanQuery filterQuery = _queries.booleanQuery();

		for (FilterParameter filter : filters) {
			String fieldName = filter.getFieldName();

			String filterOccur = 
				(String)filter.getAttribute("filterOccur");
			
			String valueOccur = 
				(String)filter.getAttribute("valueOccur");
			
			List<String> values = (List<String>)filter.getAttribute("values");

			BooleanQuery query = _queries.booleanQuery();

			// Special treatment for entryClassNames.

			if (fieldName.equals(Field.ENTRY_CLASS_NAME)) {
				
				for (String value : values) {

					// Handle journal article separately.

					if (value.equals(JournalArticle.class.getName())) {
						_addJournalArticleClassCondition(query);
					}
					else {

						TermQuery entryClassNameQuery = _queries.term(fieldName, value);

						_addChildQueryByOccur(query, entryClassNameQuery, valueOccur);
					}
				}
			}
			else {
				for (String value : values) {
					
					TermQuery termQuery = _queries.term(fieldName, value);

					_addChildQueryByOccur(query, termQuery, valueOccur);
					
				}
			}

			_addChildQueryByOccur(filterQuery, query, filterOccur);
		}

		preFilterQuery.addMustQueryClauses(filterQuery);
	}

	/**
	 * Add journal article class condition.
	 *
	 * @param query
	 */
	private void _addJournalArticleClassCondition(BooleanQuery query) {

		BooleanQuery journalArticleQuery = _queries.booleanQuery();

		// Classname condition.

		TermQuery classNamecondition = _queries.term(
			Field.ENTRY_CLASS_NAME, JournalArticle.class.getName());

		journalArticleQuery.addMustQueryClauses(classNamecondition);

		// Add display date limitation.

		Date now = new Date();
		
		RangeTermQuery fromQuery = 
				_queries.rangeTerm("displayDate_sortable", true, true,
			Long.MIN_VALUE, now.getTime());

		RangeTermQuery toQuery = 
				_queries.rangeTerm("expirationDate_sortable", true, true,
			now.getTime(), Long.MAX_VALUE);

		// Add version limitation.

		TermQuery versionQuery = _queries.term(
			"head", Boolean.TRUE.toString());

		journalArticleQuery.addMustQueryClauses(fromQuery, toQuery, versionQuery);

		query.addShouldQueryClauses(journalArticleQuery);
	}

	/**
	 * Adds a child query by occur value.
	 * 
	 * @param query
	 * @param childQuery
	 * @param occurString
	 */
	private void _addChildQueryByOccur(
			BooleanQuery query, Query childQuery, String occurString) {

		if (FilterConfigurationValues.OCCUR_MUST.equals(occurString)) {
			
			query.addMustQueryClauses(childQuery);
		}
		else if (FilterConfigurationValues.OCCUR_MUST_NOT.equals(occurString)) {
		
			query.addMustNotQueryClauses(childQuery);
		}
		else {
			query.addShouldQueryClauses(childQuery);
		}
	}

	@Reference
	private BooleanQueryUtilities _booleanQueryUtilities;
	
	@Reference
	private Queries _queries;	
}