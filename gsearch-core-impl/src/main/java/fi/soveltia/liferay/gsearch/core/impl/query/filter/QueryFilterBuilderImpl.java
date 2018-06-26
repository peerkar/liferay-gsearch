
package fi.soveltia.liferay.gsearch.core.impl.query.filter;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import fi.soveltia.liferay.gsearch.core.api.params.FacetParam;
import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.query.filter.PermissionFilterQueryBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.filter.QueryFilterBuilder;
import fi.soveltia.liferay.gsearch.core.api.results.item.processor.ResultItemProcessor;

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

		_queryParams = queryParams;
		_portletRequest = portletRequest;

		_filter = new BooleanFilter();

		buildClassesCondition();

		buildCompanyCondition();

		buildGroupsCondition();

		buildModificationTimeCondition();

		buildStagingGroupCondition();

		buildStatusCondition();

		buildFacetConditions();

		buildViewPermissionCondition();

		return _filter;
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
	protected void buildClassesCondition()
		throws ParseException {

		List<String> classNames = _queryParams.getClassNames();

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
		addAsQueryFilter(query);
	}

	/**
	 * Add company condition.
	 */
	protected void buildCompanyCondition() {

		_filter.addRequiredTerm(Field.COMPANY_ID, _queryParams.getCompanyId());
	}

	protected void buildFacetConditions() {

		Map<FacetParam, BooleanClauseOccur> facetParams =
			_queryParams.getFacetParams();

		if (facetParams == null) {
			return;
		}

		BooleanQueryImpl facetQuery = new BooleanQueryImpl();

		for (Entry<FacetParam, BooleanClauseOccur> facetParam : facetParams.entrySet()) {

			BooleanQueryImpl query = new BooleanQueryImpl();

			for (int i = 0; i < facetParam.getKey().getValues().length; i++) {

				// Limit max values just in case.

				if (i > MAX_FACET_VALUES) {
					break;
				}

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
		addAsQueryFilter(facetQuery);
	}

	/**
	 * Add groups condition.
	 * 
	 * @throws ParseException
	 */
	protected void buildGroupsCondition()
		throws ParseException {

		long[] groupIds = _queryParams.getGroupIds();

		if (groupIds.length > 0) {

			BooleanQueryImpl query = new BooleanQueryImpl();

			for (long l : groupIds) {
				TermQuery condition =
					new TermQueryImpl(Field.SCOPE_GROUP_ID, String.valueOf(l));
				query.add(condition, BooleanClauseOccur.SHOULD);
			}
			addAsQueryFilter(query);
		}
	}

	/**
	 * Add modification date condition.
	 * 
	 * @throws ParseException
	 */
	protected void buildModificationTimeCondition()
		throws ParseException {

		// Set modified from limit.

		Date from = _queryParams.getTimeFrom();

		if (from != null) {
			BooleanQuery query = new BooleanQueryImpl();
			query.addRangeTerm(
				"modified_sortable", from.getTime(), Long.MAX_VALUE);

			addAsQueryFilter(query);
		}

		// Set modified to limit.

		Date to = _queryParams.getTimeTo();

		if (to != null) {
			BooleanQuery query = new BooleanQueryImpl();
			query.addRangeTerm(
				"modified_sortable", to.getTime(), Long.MAX_VALUE);

			addAsQueryFilter(query);
		}
	}

	/**
	 * Add (no) staging group condition.
	 */
	protected void buildStagingGroupCondition() {

		_filter.addRequiredTerm(Field.STAGING_GROUP, false);
	}

	/**
	 * Add status condition.
	 */
	protected void buildStatusCondition() {

		// Set to approved only

		int status = WorkflowConstants.STATUS_APPROVED;

		_filter.addRequiredTerm(Field.STATUS, status);
	}

	/**
	 * Add view permissions condition.
	 * 
	 * @throws Exception
	 */
	protected void buildViewPermissionCondition()
		throws Exception {

		Query query = _permissionFilterQueryBuilder.buildPermissionQuery(
			_portletRequest, _queryParams);

		if (query != null) {
			addAsQueryFilter(query);
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
	private void addAsQueryFilter(Query query) {

		QueryFilter queryFilter = new QueryFilter(query);

		_filter.add(queryFilter, BooleanClauseOccur.MUST);
	}

	private static final int MAX_FACET_VALUES = 20;

	private BooleanFilter _filter;

	@Reference(
		bind = "setPermissionFilterQueryBuilder", 
		policy = ReferencePolicy.DYNAMIC, 
		policyOption = ReferencePolicyOption.GREEDY, 
		service = PermissionFilterQueryBuilder.class, 
		unbind = "removePermissionFilterQueryBuilder"
	)
	private volatile PermissionFilterQueryBuilder _permissionFilterQueryBuilder;

	private PortletRequest _portletRequest;

	private QueryParams _queryParams;

	private static final Log _log =
		LogFactoryUtil.getLog(QueryFilterBuilderImpl.class);
}
