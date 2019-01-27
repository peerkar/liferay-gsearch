
package fi.soveltia.liferay.gsearch.core.impl.query.filter;

import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.QueryFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;

import java.util.List;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.params.FacetParameter;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.filter.FilterBuilder;
import fi.soveltia.liferay.gsearch.core.impl.query.QueryBuilderImpl;

/**
 * Facet filter builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = FilterBuilder.class
)
public class FacetFilterBuilder implements FilterBuilder {

	@SuppressWarnings("unchecked")
	@Override
	public void addFilters(
		PortletRequest portletRequest, BooleanFilter preBooleanfilter,
		BooleanFilter postFilter, QueryContext queryContext)
		throws Exception {

		List<FacetParameter> facetParameters =
			(List<FacetParameter>) queryContext.getParameter(
				ParameterNames.FACETS);

		if (facetParameters == null) {
			return;
		}

		BooleanQueryImpl preFilterQuery = new BooleanQueryImpl();
		BooleanQueryImpl postFilterQuery = new BooleanQueryImpl();

		for (FacetParameter f : facetParameters) {

			BooleanQueryImpl query = new BooleanQueryImpl();

			for (String value : f.getValues()) {

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Adding facet " + f.getFieldName() + ":" +
							value);
				}

				TermQuery condition;
				condition =
					new TermQueryImpl(f.getFieldName(), value);

				// Check operator if we have multiple values

				BooleanClauseOccur occur;
				
				if (f.getValues().size() > 1) {
					occur = ("or").equals(f.getMultiValueOperator())
						? BooleanClauseOccur.SHOULD : BooleanClauseOccur.MUST;
				}
				else {
					occur = BooleanClauseOccur.MUST;
				}
				
				query.add(condition, occur);

			}

			// Don't add to prefilters if we do post filtering only.

			if (query.hasClauses()) {

				if ("pre".equals(f.getFilterMode())) {
					preFilterQuery.add(query, BooleanClauseOccur.MUST);
				}
				else {
					postFilterQuery.add(query, BooleanClauseOccur.MUST);
				}
			}
		}

		if (preFilterQuery.hasClauses()) {
			QueryFilter pre = new QueryFilter(preFilterQuery);
			preBooleanfilter.add(pre, BooleanClauseOccur.MUST);
		}

		if (postFilterQuery.hasClauses()) {
			QueryFilter post = new QueryFilter(postFilterQuery);
			postFilter.add(post, BooleanClauseOccur.MUST);
		}
	}
	
	private static final Logger _log =
		LoggerFactory.getLogger(QueryBuilderImpl.class);
}
