
package fi.soveltia.liferay.gsearch.core.impl.query.filter;

import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.QueryFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.params.FilterParameter;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.filter.FilterBuilder;

/**
 * Group filter builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = FilterBuilder.class
)
public class GroupFilterBuilder implements FilterBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addFilters(
		QueryContext queryContext, BooleanFilter preBooleanfilter,
		BooleanFilter postFilter)
		throws Exception {

		FilterParameter filter =
			queryContext.getFilterParameter(ParameterNames.GROUP_ID);

		if (filter == null) {
			return;
		}

		long[] groupIds = (long[]) filter.getAttribute("values");

		if (groupIds != null && groupIds.length > 0) {

			BooleanQueryImpl query = new BooleanQueryImpl();

			for (long l : groupIds) {
				TermQuery condition =
					new TermQueryImpl(Field.SCOPE_GROUP_ID, String.valueOf(l));
				query.add(condition, BooleanClauseOccur.SHOULD);
			}

			QueryFilter queryFilter = new QueryFilter(query);

			preBooleanfilter.add(queryFilter, BooleanClauseOccur.MUST);
		}
	}
}
