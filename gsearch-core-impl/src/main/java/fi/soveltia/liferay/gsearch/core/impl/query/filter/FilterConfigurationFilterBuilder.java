
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

import javax.portlet.PortletRequest;

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
		PortletRequest portletRequest, BooleanFilter preBooleanfilter,
		BooleanFilter postFilter, QueryContext queryContext)
		throws Exception {

		FilterParameter preFilter =
			queryContext.getFilterParameter("filterConfiguration");

		if (preFilter == null) {
			return;
		}

		BooleanQuery filterQuery = new BooleanQueryImpl();

		List<FilterParameter> filters =
			(List<FilterParameter>) preFilter.getAttribute("filters");

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

				for (String className : values) {

					// Handle journal article separately.

					if (className.equals(JournalArticle.class.getName())) {
						addJournalArticleClassCondition(query);

					}
					else {

						TermQuery condition = new TermQueryImpl(
							Field.ENTRY_CLASS_NAME, className);
						query.add(condition, valueOccur);
					}
				}

			}
			else {

				for (String value : values) {

					TermQuery condition = new TermQueryImpl(fieldName, value);
					query.add(condition, valueOccur);
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
