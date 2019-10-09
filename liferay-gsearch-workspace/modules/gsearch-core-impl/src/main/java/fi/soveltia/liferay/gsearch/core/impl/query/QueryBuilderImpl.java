
package fi.soveltia.liferay.gsearch.core.impl.query;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.Query;

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

import fi.soveltia.liferay.gsearch.core.api.configuration.CoreConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationValues;
import fi.soveltia.liferay.gsearch.core.api.constants.ConfigurationNames;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
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

	public static final DateFormat INDEX_DATE_FORMAT = new SimpleDateFormat(
		"yyyyMMddHHmmss");

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BooleanQuery buildRescoreQuery(QueryContext queryContext) 
			throws Exception {

		// Build query.
		
		JSONArray configuration = (JSONArray)queryContext.getConfiguration(
				ConfigurationNames.RESCORE);
		
		return constructQuery(queryContext, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BooleanQuery buildSearchQuery(QueryContext queryContext) throws Exception {

		// Build query.
		
		JSONArray configuration = (JSONArray)queryContext.getConfiguration(
				ConfigurationNames.CLAUSE);

		BooleanQuery query = constructQuery(queryContext, configuration);

		
		// Process query contributors.

		if (queryContext.isQueryContributorsEnabled()) {
			processQueryContributors(queryContext, query);
		}

		// Add filters.

		addFilters(queryContext, query);

		return query;
	}

	protected void addFilterBuilder(FilterBuilder filterBuilder) {
		if (_filterBuilders == null) {
			_filterBuilders = new ArrayList<>();
		}

		_filterBuilders.add(filterBuilder);
	}

	/**
	 * Adds filters to the query.
	 *
	 * @param queryContext
	 * @param query
	 * @throws Exception
	 */
	protected void addFilters(QueryContext queryContext, BooleanQuery query)
		throws Exception {

		BooleanQuery preFilterQuery = _queries.booleanQuery();

		BooleanQuery postFilterQuery = _queries.booleanQuery();

		for (FilterBuilder f : _filterBuilders) {
			f.addFilters(queryContext, preFilterQuery, postFilterQuery);
		}

		// Add permission clauses.

		Query permissionQuery =
			_permissionFilterQueryBuilder.buildPermissionQuery(queryContext);

		if (permissionQuery != null) {
			
			preFilterQuery.addMustQueryClauses(permissionQuery);
		}

		query.addFilterQueryClauses(preFilterQuery);

		// Todo: because of platform API changes, the post filtering has yet to 
		// updated.
		
		if (postFilterQuery.hasClauses()) {
			queryContext.setParameter(
					ParameterNames.POST_FILTER_QUERY, postFilterQuery);
		}
	}

	protected void addPermissionFilterQueryBuilder(
		PermissionFilterQueryBuilder permissionFilterQueryBuilder) {

		_permissionFilterQueryBuilder = permissionFilterQueryBuilder;
	}

	protected void addQueryContributor(QueryContributor queryContributor) {
		if (_queryContributors == null) {
			_queryContributors = new ArrayList<>();
		}

		_queryContributors.add(queryContributor);
	}

	/**
	 * Checks clause conditions
	 * 
	 * @param queryContext
	 * @param conditionsArray
	 * @return
	 * @throws Exception
	 */
	protected boolean checkConditions(
			QueryContext queryContext, JSONArray conditionsArray)
		throws Exception {

		if ((conditionsArray == null) || (conditionsArray.length() == 0)) {
			return true;
		}

		ClauseConditionHandler clauseConditionHandler;

		boolean valid = false;

		for (int i = 0; i < conditionsArray.length(); i++) {
			JSONObject condition = conditionsArray.getJSONObject(i);

			String handlerName = condition.getString(
					ClauseConfigurationKeys.CONDITION_HANDLER);

			String occur = condition.getString(ClauseConfigurationKeys.OCCUR);

			// Try to get a clause builder for the query type.

			clauseConditionHandler = _clauseConditionHandlerFactory.getHandler(
				handlerName);

			// Check if condition is valid.
			// Return false if no handler is found.

			if (clauseConditionHandler != null) {
				JSONObject handlerParameters = condition.getJSONObject(
					ClauseConfigurationKeys.CONFIGURATION);

				if (clauseConditionHandler.isTrue(
						queryContext, handlerParameters)) {

					valid = true;
				}
				else {
					if (ClauseConfigurationValues.OCCUR_MUST.equals(occur)) {
						return false;
					}
				}
			}
			else {
				return false;
			}
		}

		return valid;
	}

	/**
	 * Constructs the query. 
	 * 
	 * @param queryContext
	 * @param configuration
	 * @return
	 * @throws Exception
	 */
	protected BooleanQuery constructQuery(QueryContext queryContext, JSONArray configuration)
		throws Exception {

		BooleanQuery query = _queries.booleanQuery();
		
		if (configuration == null || configuration.length() == 0) {
			return query;
		}

		// Build query

		ClauseBuilder clauseBuilder;

		Query clause;

		for (int i = 0; i < configuration.length(); i++) {
			
			JSONObject clauseObject = configuration.getJSONObject(i);

			// Avoid breaking on an empty config item.

			if (clauseObject.length() == 0) {
				continue;
			}

			// Check if this clause is enabled

			if (!clauseObject.getBoolean(ClauseConfigurationKeys.ENABLED)) {
				continue;
			}

			// Process conditions.
			// Conditions are error prone for editing.
			// Just log the error to be able to recover.

			JSONArray conditionsArray = clauseObject.getJSONArray(
					ClauseConfigurationKeys.CONDITIONS);

			boolean applyClauses = false;

			try {
				applyClauses = checkConditions(queryContext, conditionsArray);
			}
			catch (Exception e) {
				_log.error(e.getMessage(), e);

				continue;
			}

			if (applyClauses) {
				
				JSONArray clauseArray = clauseObject.getJSONArray(ClauseConfigurationKeys.CLAUSES);

				for (int j = 0; j < clauseArray.length(); j++) {
					JSONObject clauseItem = clauseArray.getJSONObject(j);

					String queryType = clauseItem.getString(ClauseConfigurationKeys.QUERY_TYPE);

					if (Validator.isNull(queryType)) {
						continue;
					}

					queryType = queryType.toLowerCase();

					// Try to get a clause builder for the query type.

					clauseBuilder = _clauseBuilderFactory.getClauseBuilder(
						queryType);

					JSONObject clauseConfiguration = 
							_coreConfigurationHelper.parseConfigurationVariables(
							queryContext, clauseItem.getJSONObject(
									ClauseConfigurationKeys.CONFIGURATION));
					
					if (clauseBuilder != null) {
						clause = clauseBuilder.buildClause(
							queryContext, clauseConfiguration);

						if (clause != null) {
							
							String occur = clauseItem.getString(
									ClauseConfigurationKeys.OCCUR);

							if (ClauseConfigurationValues.OCCUR_MUST.
									equalsIgnoreCase(occur)) {
								query.addMustQueryClauses(clause);
							}
							else if (ClauseConfigurationValues.OCCUR_MUST_NOT.
									equalsIgnoreCase(occur)) {
								query.addMustNotQueryClauses(clause);
							}
							else {
								query.addShouldQueryClauses(clause);
							}							
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
					"Processing " +
						queryContributor.getClass(
						).getName());

				if (!queryContributor.isEnabled()) {
					_log.debug(
						queryContributor.getClass(
						).getName() + " is disabled.");
				}
			}

			try {
				Query contributorQuery = queryContributor.buildQuery(
					queryContext);

				if (contributorQuery != null) {
					
					String occur = queryContributor.getOccur();

					if (ClauseConfigurationValues.OCCUR_MUST.
							equalsIgnoreCase(occur)) {
						query.addMustQueryClauses(contributorQuery);
					}
					else if (ClauseConfigurationValues.OCCUR_MUST_NOT.
							equalsIgnoreCase(occur)) {
						query.addMustNotQueryClauses(contributorQuery);
					}
					else {
						query.addShouldQueryClauses(contributorQuery);
					}	
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

	private static final Logger _log = LoggerFactory.getLogger(
		QueryBuilderImpl.class);

	@Reference
	private ClauseBuilderFactory _clauseBuilderFactory;

	@Reference
	private ClauseConditionHandlerFactory _clauseConditionHandlerFactory;

	@Reference
	private CoreConfigurationHelper _coreConfigurationHelper;

	@Reference(
		bind = "addFilterBuilder", cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC, service = FilterBuilder.class,
		unbind = "removeFilterBuilder"
	)
	private volatile List<FilterBuilder> _filterBuilders = null;

	@Reference(
		bind = "addPermissionFilterQueryBuilder",
		policy = ReferencePolicy.STATIC,
		policyOption = ReferencePolicyOption.GREEDY,
		service = PermissionFilterQueryBuilder.class,
		unbind = "removePermissionFilterQueryBuilder"
	)
	private volatile PermissionFilterQueryBuilder _permissionFilterQueryBuilder;

	@Reference
	private Queries _queries;
	
	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC, service = QueryContributor.class,
		unbind = "removeQueryContributor"
	)
	private volatile List<QueryContributor> _queryContributors = null;

}