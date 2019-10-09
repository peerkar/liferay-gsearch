
package fi.soveltia.liferay.gsearch.core.impl.query.filter;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.TermQuery;
import com.liferay.portal.search.query.util.BooleanQueryUtilities;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.filter.FilterBuilder;

/**
 * String group filter builder.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = FilterBuilder.class
)
public class StagingGroupFilterBuilder implements FilterBuilder {

	@Override
	public void addFilters(QueryContext queryContext,
			BooleanQuery preFilterQuery, BooleanQuery postFilterQuery) 
					throws Exception {

		// For other asset types than User, do not search staging groups.
		
		BooleanQuery query = _queries.booleanQuery();
		
		TermQuery t1 = _queries.term(
			Field.ENTRY_CLASS_NAME, User.class.getName());

		TermQuery t2 = _queries.term(
			Field.STAGING_GROUP, false);

		query.addShouldQueryClauses(t1, t2);
		
		preFilterQuery.addMustQueryClauses(query);
	}
	
	@Reference
	private Queries _queries;
}	
