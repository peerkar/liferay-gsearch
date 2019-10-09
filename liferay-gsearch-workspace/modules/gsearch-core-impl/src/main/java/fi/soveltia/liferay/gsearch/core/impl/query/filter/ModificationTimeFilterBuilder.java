
package fi.soveltia.liferay.gsearch.core.impl.query.filter;

import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.RangeTermQuery;
import com.liferay.portal.search.query.util.BooleanQueryUtilities;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.params.FilterParameter;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.filter.FilterBuilder;

/**
 * Modification time filter builder.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = FilterBuilder.class
)
public class ModificationTimeFilterBuilder implements FilterBuilder {

	@Override
	public void addFilters(QueryContext queryContext,
			BooleanQuery preFilterQuery, BooleanQuery postFilterQuery) 
					throws Exception {

		FilterParameter filter = queryContext.getFilterParameter(
			ParameterNames.TIME);

		if (filter == null) {
			return;
		}

		Date from = (Date)filter.getAttribute("timeFrom");
		Date to = (Date)filter.getAttribute("timeTo");

		if ((from != null) && (to != null)) {
			
			RangeTermQuery rangeTermQuery = 
					_queries.rangeTerm("modified_sortable", true, true, 
							from.getTime(), to.getTime());

			preFilterQuery.addMustQueryClauses(rangeTermQuery);
		}
		else {
			if (from != null) {

				RangeTermQuery rangeTermQuery = 
						_queries.rangeTerm("modified_sortable", true, true, 
								from.getTime(), Long.MAX_VALUE);

				preFilterQuery.addMustQueryClauses(rangeTermQuery);
			}

			if (to != null) {

				RangeTermQuery rangeTermQuery = 
						_queries.rangeTerm("modified_sortable", true, true, 
								Long.MIN_VALUE, to.getTime());

				preFilterQuery.addMustQueryClauses(rangeTermQuery);
			}
		}
	}
	
	@Reference
	private Queries _queries;
	
}