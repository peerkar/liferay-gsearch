
package fi.soveltia.liferay.gsearch.core.impl.query.filter;

import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.QueryFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;

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
	public void addFilters(
		QueryContext queryContext, BooleanFilter preBooleanfilter,
		BooleanFilter postFilter)
		throws Exception {

		FilterParameter preFilter =
			queryContext.getFilterParameter("filterConfiguration");

		if (preFilter == null) {
			return;
		}

		List<FilterParameter> filters =
			(List<FilterParameter>) preFilter.getAttribute("filters");

		BooleanQuery filterQuery = new BooleanQueryImpl();

		for (FilterParameter filter : filters) {			

			String fieldName = filter.getFieldName();
			
			BooleanClauseOccur filterOccur =
				translateOccur((String) filter.getAttribute("filterOccur"));
			BooleanClauseOccur valueOccur =
				translateOccur((String) filter.getAttribute("valueOccur"));
			List<String> values = (List<String>) filter.getAttribute("values");

			BooleanQuery query = new BooleanQueryImpl();

			// Special treatment for entryClassNames.

			if (fieldName.equals("entryClassName")) {

				for (String value : values) {

					// Handle journal article separately.

					if (value.equals(JournalArticle.class.getName())) {
						addJournalArticleClassCondition(query);
					}
					else {
						query.addTerm(fieldName, value, false, valueOccur);
					}
				}

			}
			else {

				for (String value : values) {
					query.addTerm(fieldName, value, false, valueOccur);
				}
			}

			filterQuery.add(query, filterOccur);
		}

		QueryFilter queryFilter = new QueryFilter(filterQuery);
		preBooleanfilter.add(queryFilter, BooleanClauseOccur.MUST);
	}

	/**
	 * Add journal article class condition.
	 * 
	 * @param query
	 * @throws ParseException
	 */
	protected void addJournalArticleClassCondition(BooleanQuery query)
		throws ParseException {

		BooleanQuery journalArticleQuery = new BooleanQueryImpl();

		// Classname condition.

		TermQuery classNamecondition = new TermQueryImpl(
			Field.ENTRY_CLASS_NAME, JournalArticle.class.getName());
		journalArticleQuery.add(classNamecondition, BooleanClauseOccur.MUST);

		// Add display date limitation.

		Date now = new Date();

		journalArticleQuery.addRangeTerm(
			"displayDate_sortable", Long.MIN_VALUE, now.getTime());

		journalArticleQuery.addRangeTerm(
			"expirationDate_sortable", now.getTime(), Long.MAX_VALUE);

		// Add version limitation.

		TermQuery versionQuery =
			new TermQueryImpl("head", Boolean.TRUE.toString());
		journalArticleQuery.add(versionQuery, BooleanClauseOccur.MUST);

		query.add(journalArticleQuery, BooleanClauseOccur.SHOULD);
	}

	private BooleanClauseOccur translateOccur(String occurString) {

		BooleanClauseOccur occur;

		if ("should".equals(occurString)) {
			occur = BooleanClauseOccur.SHOULD;
		}
		else if ("must_not".equals(occurString)) {
			occur = BooleanClauseOccur.MUST_NOT;
		}
		else {
			occur = BooleanClauseOccur.MUST;
		}

		return occur;

	}
}
