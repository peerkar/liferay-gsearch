
package fi.soveltia.liferay.gsearch.core.impl.query.filter;

import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.model.User;
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

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;

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
	public void addFilters(
		PortletRequest portletRequest, BooleanFilter preBooleanfilter,
		BooleanFilter postFilter, QueryContext queryContext)
		throws Exception {

		// For other asset types than User, do not search staging groups.
		
		BooleanQuery filterQuery = new BooleanQueryImpl();
		
		filterQuery.addTerm(Field.ENTRY_CLASS_NAME, User.class.getName(), false, BooleanClauseOccur.SHOULD);
		filterQuery.addTerm(Field.STAGING_GROUP, "false", false, BooleanClauseOccur.SHOULD);

		QueryFilter queryFilter = new QueryFilter(filterQuery);
		preBooleanfilter.add(queryFilter, BooleanClauseOccur.MUST);
	}
}
