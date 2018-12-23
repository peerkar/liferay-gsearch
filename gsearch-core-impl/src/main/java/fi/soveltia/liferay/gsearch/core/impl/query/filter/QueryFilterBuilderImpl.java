
package fi.soveltia.liferay.gsearch.core.impl.query.filter;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.TermQuery;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.QueryFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.params.FacetParam;
import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.query.filter.PermissionFilterQueryBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.filter.QueryFilterBuilder;

/**
 * QueryFilterBuilder implementation. Notice that if you use BooleanQuery type
 * for filter conditions, they get translated to 3 subqueries: match, phrase,
 * and phrase_prefix = use TermQuery for filtering.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = QueryFilterBuilder.class
)
public class QueryFilterBuilderImpl implements QueryFilterBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BooleanFilter buildQueryFilter(
		PortletRequest portletRequest, QueryParams queryParams)
		throws Exception {

		BooleanFilter filter = new BooleanFilter();

		buildClassesCondition(filter, queryParams);

		buildCompanyCondition(filter, queryParams);

		buildGroupsCondition(filter, queryParams);

		buildModificationTimeCondition(filter, queryParams);

		buildStagingGroupCondition(filter);

		buildStatusCondition(filter, queryParams);

		buildFacetConditions(filter, queryParams);

		buildViewPermissionCondition(portletRequest, filter, queryParams);

		return filter;
	}

	/**
	 * Add DLFileEntry class condition
	 * 
	 * @param query
	 * @param dedicatedTypeQuery
	 * @throws ParseException
	 */
	protected void addDLFileEntryClassCondition(BooleanQuery query)
		throws ParseException {

		TermQuery condition = new TermQueryImpl(
			Field.ENTRY_CLASS_NAME, DLFileEntry.class.getName());
		query.add(condition, BooleanClauseOccur.SHOULD);
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

	/**
	 * Add classes condition.
	 * 
	 * @throws ParseException
	 */
	protected void buildClassesCondition(BooleanFilter filter, QueryParams queryParams)
		throws ParseException {

		if (queryParams.getEntryClassNames() == null) {
			return;
		}
		
		List<String> classNames = queryParams.getEntryClassNames();

		BooleanQuery query = new BooleanQueryImpl();

		for (String className : classNames) {

			// Handle journal article separately.

			if (className.equals(JournalArticle.class.getName())) {
				addJournalArticleClassCondition(query);
			}
			else {

				TermQuery condition =
					new TermQueryImpl(Field.ENTRY_CLASS_NAME, className);
				query.add(condition, BooleanClauseOccur.SHOULD);
			}
		}
		addAsQueryFilter(filter, query);
	}

	/**
	 * Add company condition.
	 */
	protected void buildCompanyCondition(BooleanFilter filter, QueryParams queryParams) {

		filter.addRequiredTerm(Field.COMPANY_ID, queryParams.getCompanyId());
	}

	protected void buildFacetConditions(BooleanFilter filter, QueryParams queryParams) {

		Map<FacetParam, BooleanClauseOccur> facetParams =
			queryParams.getFacetParams();

		if (facetParams == null) {
			return;
		}

		BooleanQueryImpl facetQuery = new BooleanQueryImpl();

		for (Entry<FacetParam, BooleanClauseOccur> facetParam : facetParams.entrySet()) {

			BooleanQueryImpl query = new BooleanQueryImpl();

			for (int i = 0; i < facetParam.getKey().getValues().length; i++) {

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Adding facet " + facetParam.getKey().getFieldName() +
							":" + facetParam.getKey().getValues()[i]);
				}

				TermQuery condition;

				condition = new TermQueryImpl(
					facetParam.getKey().getFieldName(),
					facetParam.getKey().getValues()[i]);

				query.add(condition, facetParam.getKey().getOccur());
			}

			facetQuery.add(query, facetParam.getValue());
		}
		addAsQueryFilter(filter, facetQuery);
	}

	/**
	 * Add groups condition.
	 * 
	 * @throws ParseException
	 */
	protected void buildGroupsCondition(BooleanFilter filter, QueryParams queryParams)
		throws ParseException {

		long[] groupIds = queryParams.getGroupIds();

		if (groupIds.length > 0) {

			BooleanQueryImpl query = new BooleanQueryImpl();

			for (long l : groupIds) {
				TermQuery condition =
					new TermQueryImpl(Field.SCOPE_GROUP_ID, String.valueOf(l));
				query.add(condition, BooleanClauseOccur.SHOULD);
			}
			addAsQueryFilter(filter, query);
		}
	}

	/**
	 * Add modification date condition.
	 * 
	 * @throws ParseException
	 */
	protected void buildModificationTimeCondition(BooleanFilter filter, QueryParams queryParams)
		throws ParseException {

		// Set modified from limit.

		Date from = queryParams.getTimeFrom();

		if (from != null) {
			BooleanQuery query = new BooleanQueryImpl();
			query.addRangeTerm(
				"modified_sortable", from.getTime(), Long.MAX_VALUE);

			addAsQueryFilter(filter, query);
		}

		// Set modified to limit.

		Date to = queryParams.getTimeTo();

		if (to != null) {
			BooleanQuery query = new BooleanQueryImpl();
			query.addRangeTerm(
				"modified_sortable", to.getTime(), Long.MAX_VALUE);

			addAsQueryFilter(filter, query);
		}
	}

	/**
	 * Add (no) staging group condition.
	 */
	protected void buildStagingGroupCondition(BooleanFilter filter) {

		filter.addRequiredTerm(Field.STAGING_GROUP, false);
	}

	/**
	 * Add status condition.
	 */
	protected void buildStatusCondition(BooleanFilter filter, QueryParams queryParams) {

		int status = queryParams.getStatus() != null ? 
			queryParams.getStatus() : WorkflowConstants.STATUS_APPROVED;
		
		filter.addRequiredTerm(Field.STATUS, status);
	}

	/**
	 * Add view permissions condition.
	 * 
	 * @throws Exception
	 */
	protected void buildViewPermissionCondition(PortletRequest portletRequest, BooleanFilter filter, QueryParams queryParams)
		throws Exception {

		Query query = _permissionFilterQueryBuilder.buildPermissionQuery(
			portletRequest, queryParams);

		if (query != null) {
			addAsQueryFilter(filter, query);
		}
	}

	/**
	 * Remove a result item processor from list.
	 * 
	 * @param resultItemProcessor
	 */
	protected void removePermissionFilterQueryBuilder(
		PermissionFilterQueryBuilder permissionFilterQueryBuilder) {
		
		_permissionFilterQueryBuilder = null;
	}

	protected void setPermissionFilterQueryBuilder(
		PermissionFilterQueryBuilder permissionFilterQueryBuilder) {
		
		_permissionFilterQueryBuilder = permissionFilterQueryBuilder;
	}

	/**
	 * Add Query object to filters as a QueryFilter.
	 * 
	 * @param query
	 */
	private void addAsQueryFilter(BooleanFilter filter, Query query) {

		QueryFilter queryFilter = new QueryFilter(query);

		filter.add(queryFilter, BooleanClauseOccur.MUST);
	}
	
	private static final Logger _log =
					LoggerFactory.getLogger(QueryFilterBuilderImpl.class);

	@Reference(
		bind = "setPermissionFilterQueryBuilder", 
		policy = ReferencePolicy.STATIC, 
		policyOption = ReferencePolicyOption.GREEDY, 
		service = PermissionFilterQueryBuilder.class, 
		unbind = "removePermissionFilterQueryBuilder"
	)
	private volatile PermissionFilterQueryBuilder _permissionFilterQueryBuilder;

}
