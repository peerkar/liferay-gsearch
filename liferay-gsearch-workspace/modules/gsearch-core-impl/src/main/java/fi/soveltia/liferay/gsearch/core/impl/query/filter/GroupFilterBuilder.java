
package fi.soveltia.liferay.gsearch.core.impl.query.filter;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.TermQuery;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.constants.FilterConfigurationKeys;
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
	public void addFilters(QueryContext queryContext,
			BooleanQuery preFilterQuery, BooleanQuery postFilterQuery) 
					throws Exception {

		FilterParameter filter = queryContext.getFilterParameter(
			ParameterNames.GROUP_ID);

		if (filter == null) {
			return;
		}

		long[] groupIds = (long[])filter.getAttribute(FilterConfigurationKeys.VALUES);

		if ((groupIds != null) && (groupIds.length > 0)) {
			
			BooleanQuery query = _queries.booleanQuery();

			for (long groupId : groupIds) {
				TermQuery condition = _queries.term(
					Field.SCOPE_GROUP_ID, groupId);

				query.addShouldQueryClauses(condition);
			}
		}
	}

	@Reference
	private Queries _queries;	
}