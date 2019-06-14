
package fi.soveltia.liferay.gsearch.core.impl.query.filter;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.filter.FilterBuilder;

/**
 * Status filter builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = FilterBuilder.class
)
public class StatusFilterBuilder implements FilterBuilder {

	@Override
	public void addFilters(
		QueryContext queryContext, BooleanFilter preBooleanfilter,
		BooleanFilter postFilter)
		throws Exception {

		// Search only published contents. Fixed for now.

		preBooleanfilter.addRequiredTerm(
			Field.STATUS, WorkflowConstants.STATUS_APPROVED);
	}
}
