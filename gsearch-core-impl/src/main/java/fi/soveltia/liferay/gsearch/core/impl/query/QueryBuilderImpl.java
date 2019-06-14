
package fi.soveltia.liferay.gsearch.core.impl.query;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.QueryFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.util.Validator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.constants.ConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.query.QueryBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseBuilderFactory;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseConditionHandler;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseConditionHandlerFactory;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.contributor.QueryContributor;
import fi.soveltia.liferay.gsearch.core.api.query.filter.FilterBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.filter.PermissionFilterQueryBuilder;

/**
 * Query builder implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = QueryBuilder.class
)
public class QueryBuilderImpl implements QueryBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query buildQuery(QueryContext queryContext)
		throws Exception {

		// Build query.

		BooleanQuery query =
			constructQuery(queryContext);

		// Process query contributors.

		if (queryContext.isQueryContributorsEnabled()) {

			processQueryContributors(queryContext, query);
		}

		// Add filters.
		
		addFilters(queryContext, query);
						
		return query;
	}

	/**
	 * Add filters to query.
	 * 
	 * @param portletRequest
	 * @param queryContext
	 * @param query
	 * @throws Exception
	 */
	protected void addFilters(QueryContext queryContext, Query query) throws Exception {

		BooleanFilter preFilter = new BooleanFilter();

		BooleanFilter postFilter = new BooleanFilter();

		for (FilterBuilder f : _filterBuilders) {

			f.addFilters(queryContext, preFilter, postFilter);
		}
		
		// Add permission clauses.
		
		Query permissionQuery = _permissionFilterQueryBuilder.buildPermissionQuery(
			queryContext);
		
		if (permissionQuery != null) {
			QueryFilter permissionFilter = new QueryFilter(permissionQuery);
			preFilter.add(permissionFilter, BooleanClauseOccur.MUST);
		}
		
		query.setPreBooleanFilter(preFilter);
		
		if (postFilter.hasClauses()) {
			query.setPostFilter(postFilter);
		}
	}

	protected void addFilterBuilder(
		FilterBuilder filterBuilder) {

		
		if (_filterBuilders == null) {
			_filterBuilders = new ArrayList<FilterBuilder>();
		}
		_filterBuilders.add(filterBuilder);
	}

	protected void addPermissionFilterQueryBuilder(
		PermissionFilterQueryBuilder permissionFilterQueryBuilder) {

		_permissionFilterQueryBuilder = permissionFilterQueryBuilder;
	}

	protected void addQueryContributor(QueryContributor queryContributor) {

		if (_queryContributors == null) {
			_queryContributors = new ArrayList<QueryContributor>();
		}
		_queryContributors.add(queryContributor);
	}

	protected boolean checkConditions(
		QueryContext queryContext,
		JSONArray conditionsArray)
		throws Exception {

		if (conditionsArray == null || conditionsArray.length() == 0) {
			return true;
		}

		ClauseConditionHandler clauseConditionHandler;

		boolean isValid = false;

		for (int i = 0; i < conditionsArray.length(); i++) {

			JSONObject condition = conditionsArray.getJSONObject(i);

			String handlerName = condition.getString("handler_name");

			String occur = condition.getString("occur");

			// Try to get a clause builder for the query type.

			clauseConditionHandler =
				_clauseConditionHandlerFactory.getHandler(handlerName);

			// Check if condition is valid.
			// Return false if no handler is found.

			if (clauseConditionHandler != null) {

				JSONObject handlerParameters =
					condition.getJSONObject("handler_parameters");

				if (clauseConditionHandler.isTrue(
					queryContext, handlerParameters)) {

					isValid = true;

				}
				else {

					if ("must".equals(occur)) {
						return false;
					}
				}

			}
			else {

				return false;
			}
		}
		return isValid;
	}

	/**
	 * Construct query. Please note that QueryStringQuery type is an extension
	 * of Liferay StringQuery. Thus, if you don't want to use the custom search
	 * adapter, this falls silently to the default StringQuery. Remember however
	 * that with standard adapter you loose the possibility to define target
	 * fields or boosts (configuration) - or, they just don't get applied.
	 * 
	 * @param portletRequest
	 * @param queryParams
	 * @return
	 * @throws Exception
	 */
	protected BooleanQuery constructQuery(QueryContext queryContext)
		throws Exception {

		BooleanQuery query = new BooleanQueryImpl();

		// Build query

		ClauseBuilder clauseBuilder;

		Query clause;

		String[] configuration = queryContext.getConfiguration(
			ConfigurationKeys.CLAUSE);

		for (int i = 0; i < configuration.length; i++) {

			JSONObject clauseObject =
				JSONFactoryUtil.createJSONObject(configuration[i]);

			// Check if this clause is enabled
				
			if (!clauseObject.getBoolean("enabled", true)) {
				continue;
			}
				
			// Process conditions.
			// Conditions are error prone for editing.
			// Just log the error to be able to recover.

			JSONArray conditionsArray = clauseObject.getJSONArray("conditions");

			boolean applyClauses = false;

			try {

				applyClauses = checkConditions(
					queryContext, conditionsArray);

			}
			catch (Exception e) {

				_log.error(e.getMessage(), e);

				continue;
			}

			if (applyClauses) {

				JSONArray clauseArray = clauseObject.getJSONArray("clauses");

				for (int j = 0; j < clauseArray.length(); j++) {

					JSONObject clauseItem = clauseArray.getJSONObject(j);

					String queryType = clauseItem.getString("query_type");

					if (Validator.isNull(queryType)) {
						continue;
					}
					else {
						queryType = queryType.toLowerCase();
					}

					String occurString = clauseItem.getString("occur");

					if (Validator.isNotNull(occurString)) {
						occurString = occurString.toLowerCase();
					}

					BooleanClauseOccur occur;
					if ("must".equalsIgnoreCase(occurString)) {
						occur = BooleanClauseOccur.MUST;
					}
					else if ("must_not".equalsIgnoreCase(occurString)) {
						occur = BooleanClauseOccur.MUST_NOT;
					}
					else {
						occur = BooleanClauseOccur.SHOULD;
					}

					// Try to get a clause builder for the query type.

					clauseBuilder =
						_clauseBuilderFactory.getClauseBuilder(queryType);

					if (clauseBuilder != null) {

						clause = clauseBuilder.buildClause(
							queryContext,
							clauseItem.getJSONObject("query_configuration")
							);

						if (clause != null) {
							query.add(clause, occur);
						}
					}
				}
			}
		}
		
		return query;
	}

	/**
	 * Process registered query contributors.
	 * 
	 * @param portletRequest
	 * @param query
	 */
	protected void processQueryContributors(
		QueryContext queryContext, BooleanQuery query) {

		if (_log.isDebugEnabled()) {
			_log.debug("Processing query contributors.");
		}

		if (_queryContributors == null) {
			return;
		}

		for (QueryContributor queryContributor : _queryContributors) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Processing " + queryContributor.getClass().getName());

				if (!queryContributor.isEnabled()) {
					_log.debug(
						queryContributor.getClass().getName() +
							" is disabled.");
				}
			}

			try {
				Query contributorQuery =
					queryContributor.buildQuery(queryContext);

				if (contributorQuery != null) {
					query.add(contributorQuery, queryContributor.getOccur());
				}

			}
			catch (Exception e) {
				_log.error(e.getMessage(), e);
			}
		}
	}
	

	protected void removeFilterBuilder(FilterBuilder filterBuilder) {

		_filterBuilders.remove(filterBuilder);
	}

	protected void removePermissionFilterQueryBuilder(
		PermissionFilterQueryBuilder permissionFilterQueryBuilder) {

		_permissionFilterQueryBuilder = null;
	}

	protected void removeQueryContributor(QueryContributor queryContributor) {

		_queryContributors.remove(queryContributor);
	}

	@Reference
	private ClauseBuilderFactory _clauseBuilderFactory;

	@Reference
	private ClauseConditionHandlerFactory _clauseConditionHandlerFactory;

	@Reference(
		bind = "addFilterBuilder", 
		cardinality = ReferenceCardinality.MULTIPLE, 
		policy = ReferencePolicy.DYNAMIC, 
		service = FilterBuilder.class, 
		unbind = "removeFilterBuilder"
	)
	private volatile List<FilterBuilder> _filterBuilders = null;

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE, 
		policy = ReferencePolicy.DYNAMIC, 
		service = QueryContributor.class, 
		unbind = "removeQueryContributor"
	)
	private volatile List<QueryContributor> _queryContributors = null;

	@Reference(
		bind = "addPermissionFilterQueryBuilder", 
		policy = ReferencePolicy.STATIC, 
		policyOption = ReferencePolicyOption.GREEDY, 
		service = PermissionFilterQueryBuilder.class, 
		unbind = "removePermissionFilterQueryBuilder"
	)
	private volatile PermissionFilterQueryBuilder _permissionFilterQueryBuilder;
	
	public static final DateFormat INDEX_DATE_FORMAT =
			new SimpleDateFormat("yyyyMMddHHmmss");

	private static final Logger _log =
		LoggerFactory.getLogger(QueryBuilderImpl.class);
	
}
