
package fi.soveltia.liferay.gsearch.core.impl.query.filter;

import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.QueryFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;

import java.util.Date;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
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
	public void addFilters(
		PortletRequest portletRequest, BooleanFilter preBooleanfilter,
		BooleanFilter postFilter, QueryContext queryContext)
		throws Exception {

		Date from = (Date) queryContext.getParameter(ParameterNames.TIME_FROM);
		Date to = (Date) queryContext.getParameter(ParameterNames.TIME_TO);

		if (from != null && to != null) {
			BooleanQuery query = new BooleanQueryImpl();
			query.addRangeTerm(
				"modified_sortable", from.getTime(), to.getTime());

			QueryFilter queryFilter = new QueryFilter(query);
			preBooleanfilter.add(queryFilter, BooleanClauseOccur.MUST);
		
		} else {
		
			if (from != null) {
	
				BooleanQuery query = new BooleanQueryImpl();
				query.addRangeTerm(
					"modified_sortable", from.getTime(), Long.MAX_VALUE);
	
				QueryFilter queryFilter = new QueryFilter(query);
				preBooleanfilter.add(queryFilter, BooleanClauseOccur.MUST);
			}
	
			if (to != null) {
				BooleanQuery query = new BooleanQueryImpl();
				query.addRangeTerm(
					"modified_sortable", to.getTime(), Long.MAX_VALUE);
	
				QueryFilter queryFilter = new QueryFilter(query);
				preBooleanfilter.add(queryFilter, BooleanClauseOccur.MUST);
			}
		}
	}
}
