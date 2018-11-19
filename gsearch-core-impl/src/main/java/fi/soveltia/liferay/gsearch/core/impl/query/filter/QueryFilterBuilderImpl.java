
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
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

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

		_portletRequest = portletRequest;

		BooleanFilter booleanFilter = new BooleanFilter();
		buildClassesCondition(queryParams, booleanFilter);

		buildCompanyCondition(queryParams, booleanFilter);

		buildGroupsCondition(queryParams, booleanFilter);

		buildModificationTimeCondition(queryParams, booleanFilter);

		buildStagingGroupCondition(booleanFilter);

		buildStatusCondition(booleanFilter);

		buildCategoryConditions(queryParams, booleanFilter);

		buildViewPermissionCondition(queryParams, booleanFilter);

		return booleanFilter;
	}

	/**
	 * Add DLFileEntry class condition
	 *
	 * @param query
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
	protected void addJournalArticleClassCondition(BooleanQuery query, QueryParams queryParams)
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


		List<String> ddmStructureKeys = queryParams.getDdmStructureKeys();
		if (ddmStructureKeys != null) {
			BooleanQuery ddmStructureQuery = new BooleanQueryImpl();
			for (String ddmStructureKey : ddmStructureKeys) {
				TermQuery ddmStructureCondition = new TermQueryImpl("ddmStructureKey", ddmStructureKey);
				ddmStructureQuery.add(ddmStructureCondition, BooleanClauseOccur.SHOULD);
			}
			journalArticleQuery.add(ddmStructureQuery, BooleanClauseOccur.MUST);
		}

		query.add(journalArticleQuery, BooleanClauseOccur.SHOULD);

	}

	/**
	 * Add classes condition.
	 *
	 * @throws ParseException
	 */
	protected void buildClassesCondition(QueryParams queryParams, BooleanFilter booleanFilter)
		throws ParseException {

		List<String> classNames = queryParams.getClassNames();

		BooleanQuery query = new BooleanQueryImpl();

		for (String className : classNames) {

			// Handle journal article separately.

			if (className.equals(JournalArticle.class.getName())) {
				addJournalArticleClassCondition(query, queryParams);
			}
			else {

				TermQuery condition =
					new TermQueryImpl(Field.ENTRY_CLASS_NAME, className);
				query.add(condition, BooleanClauseOccur.SHOULD);
			}
		}
		addAsQueryFilter(query, booleanFilter);
	}

	/**
	 * Add company condition.
	 */
	protected void buildCompanyCondition(QueryParams queryParams, BooleanFilter booleanFilter) {

		booleanFilter.addRequiredTerm(Field.COMPANY_ID, queryParams.getCompanyId());
	}

	protected void buildCategoryConditions(QueryParams queryParams, BooleanFilter booleanFilter) {
		List<Long> categoryIds = queryParams.getCategories();
		if ((categoryIds != null) && !categoryIds.isEmpty()) {

			BooleanQueryImpl query = new BooleanQueryImpl();
			categoryIds.forEach(categoryId -> {
				TermQuery condition = new TermQueryImpl(Field.ASSET_CATEGORY_IDS, String.valueOf(categoryId));
				query.add(condition, BooleanClauseOccur.SHOULD);
			});
			addAsQueryFilter(query, booleanFilter);
		}
	}

	protected void buildFacetConditions(QueryParams queryParams, BooleanFilter booleanFilter) {

		Map<FacetParam, BooleanClauseOccur> facetParams =
			queryParams.getFacetParams();

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
		addAsQueryFilter(facetQuery, booleanFilter);
	}

	/**
	 * Add groups condition.
	 *
	 * @throws ParseException
	 */
	protected void buildGroupsCondition(QueryParams queryParams, BooleanFilter booleanFilter)
		throws ParseException {

		long[] groupIds = queryParams.getGroupIds();

		if (groupIds.length > 0) {

			BooleanQueryImpl query = new BooleanQueryImpl();

			for (long l : groupIds) {
				TermQuery condition =
					new TermQueryImpl(Field.SCOPE_GROUP_ID, String.valueOf(l));
				query.add(condition, BooleanClauseOccur.SHOULD);
			}
			addAsQueryFilter(query, booleanFilter);
		}
	}

	/**
	 * Add modification date condition.
	 *
	 * @throws ParseException
	 */
	protected void buildModificationTimeCondition(QueryParams queryParams, BooleanFilter booleanFilter)
		throws ParseException {

		// Set modified from limit.

		Date fromDate = queryParams.getTimeFrom();
		Date toDate = queryParams.getTimeTo();
		long fromTime = Long.MIN_VALUE;
		long toTime = Long.MAX_VALUE;

		if (fromDate != null) {
			fromTime = fromDate.getTime();
		}

		if (toDate != null) {
			toTime = toDate.getTime();
		}

		// Set modified to limit.

		BooleanQuery query = new BooleanQueryImpl();
		query.addRangeTerm(
			"modified_sortable", fromTime, toTime);

		addAsQueryFilter(query, booleanFilter);
	}

	/**
	 * Add (no) staging group condition.
	 */
	protected void buildStagingGroupCondition(BooleanFilter booleanFilter) {

		booleanFilter.addRequiredTerm(Field.STAGING_GROUP, false);
	}

	/**
	 * Add status condition.
	 */
	protected void buildStatusCondition(BooleanFilter booleanFilter) {

		// Set to approved only

		int status = WorkflowConstants.STATUS_APPROVED;

		booleanFilter.addRequiredTerm(Field.STATUS, status);
	}

	/**
	 * Add view permissions condition.
	 *
	 * @throws Exception
	 */
	protected void buildViewPermissionCondition(QueryParams queryParams, BooleanFilter booleanFilter)
		throws Exception {

		Query query = _permissionFilterQueryBuilder.buildPermissionQuery(
			_portletRequest, queryParams);

		if (query != null) {
			addAsQueryFilter(query, booleanFilter);
		}
	}

	/**
	 * Remove a result item processor from list.
	 *
	 * @param permissionFilterQueryBuilder
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
	private void addAsQueryFilter(Query query, BooleanFilter booleanFilter) {

		QueryFilter queryFilter = new QueryFilter(query);

		booleanFilter.add(queryFilter, BooleanClauseOccur.MUST);
	}

	private static final int MAX_FACET_VALUES = 20;

	@Reference(
		bind = "setPermissionFilterQueryBuilder",
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		service = PermissionFilterQueryBuilder.class,
		unbind = "removePermissionFilterQueryBuilder"
	)
	private volatile PermissionFilterQueryBuilder _permissionFilterQueryBuilder;

	private PortletRequest _portletRequest;

	private static final Log _log =
		LogFactoryUtil.getLog(QueryFilterBuilderImpl.class);
}
