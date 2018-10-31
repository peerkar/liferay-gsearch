
package fi.soveltia.liferay.gsearch.core.impl.query;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.util.Validator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.query.QueryBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseBuilderFactory;
import fi.soveltia.liferay.gsearch.core.api.query.contributor.QueryContributor;
import fi.soveltia.liferay.gsearch.core.api.query.filter.QueryFilterBuilder;
import fi.soveltia.liferay.gsearch.core.impl.configuration.ModuleConfiguration;

/**
 * Query builder implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.core.impl.configuration.ModuleConfiguration", 
	immediate = true, 
	service = QueryBuilder.class
)
public class QueryBuilderImpl implements QueryBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BooleanQuery buildQuery(
		PortletRequest portletRequest, QueryParams queryParams, JSONArray queryConfiguration, boolean processQueryContributors)
		throws Exception {

		if (queryConfiguration == null) {
			queryConfiguration = JSONFactoryUtil.createJSONArray(_moduleConfiguration.queryConfiguration());
		}
		
		// Build query

		BooleanQuery query = constructQuery(portletRequest, queryParams, queryConfiguration);

		// Add query contributors

		if (processQueryContributors) {
		
			processQueryContributors(portletRequest, query);
		}
		
		// Add filters

		BooleanFilter preBooleanFilter =
			_queryFilterBuilder.buildQueryFilter(portletRequest, queryParams);

		query.setPreBooleanFilter(preBooleanFilter);

		// Set query config

		setQueryConfig(query, queryParams);

		return query;
	}
	
	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {

		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}

	/**
	 * Add query contributor to the list.
	 * 
	 * @param queryContributor
	 */
	protected void addQueryContributor(QueryContributor queryContributor) {

		if (_queryContributors == null) {
			_queryContributors = new ArrayList<QueryContributor>();
		}
		_queryContributors.add(queryContributor);
	}

	/**
	 * Process registered query contributors.
	 * 
	 * @param portletRequest
	 * @param query
	 */
	protected void processQueryContributors(
		PortletRequest portletRequest, BooleanQuery query) {

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
					queryContributor.buildQuery(portletRequest);

				if (contributorQuery != null) {
					query.add(contributorQuery, queryContributor.getOccur());
				}

			}
			catch (Exception e) {
				_log.error(e, e);
			}
		}
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
	protected BooleanQuery constructQuery(
		PortletRequest portletRequest, QueryParams queryParams, JSONArray queryConfiguration)
		throws Exception {
		
		BooleanQuery query = new BooleanQueryImpl();

		// Build query

		ClauseBuilder clauseBuilder;

		Query clause;

		for (int i = 0; i < queryConfiguration.length(); i++) {

			JSONObject queryItem = queryConfiguration.getJSONObject(i);

			String queryType = queryItem.getString("queryType");

			if (Validator.isNull(queryType)) {
				continue;
			}
			else {
				queryType = queryType.toLowerCase();
			}

			String occurString = queryItem.getString("occur");

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

			// Try to get a clause builder for the query type

			clauseBuilder = _clauseBuilderFactory.getClauseBuilder(queryType);

			if (clauseBuilder != null) {

				clause = clauseBuilder.buildClause(queryItem, queryParams);

				if (clause != null) {
					query.add(clause, occur);
				}
			}
		}

		return query;
	}

	@Reference(unbind = "-")
	protected void setClauseBuilderFactory(
		ClauseBuilderFactory clauseBuilderFactory) {

		_clauseBuilderFactory = clauseBuilderFactory;
	}

	/**
	 * Remove a query contributor from list.
	 * 
	 * @param clauseBuilder
	 */
	protected void removeQueryContributor(QueryContributor queryContributor) {

		_queryContributors.remove(queryContributor);
	}

	@Reference(unbind = "-")
	protected void setQueryFilterBuilder(
		QueryFilterBuilder queryFilterBuilder) {

		_queryFilterBuilder = queryFilterBuilder;
	}

	/**
	 * Set queryconfig.
	 * 
	 * @param query
	 */
	protected void setQueryConfig(BooleanQuery query, QueryParams queryParams) {

		// Create Queryconfig.
		
		QueryConfig queryConfig = new QueryConfig();
		queryConfig.setHighlightEnabled(true);
		
		// Set highlighted fields
		
		String contentFieldLocalized =
						Field.CONTENT + "_" + queryParams.getLocale().toString();
		
		String titleFieldLocalized =
						Field.TITLE + "_" + queryParams.getLocale().toString();

		queryConfig.setHighlightFieldNames(new String[]{Field.CONTENT, contentFieldLocalized, Field.TITLE, titleFieldLocalized});
		query.setQueryConfig(queryConfig);
	}
	
	public static final DateFormat INDEX_DATE_FORMAT =
		new SimpleDateFormat("yyyyMMddHHmmss");

	protected volatile ModuleConfiguration _moduleConfiguration;

	private ClauseBuilderFactory _clauseBuilderFactory;

	private QueryFilterBuilder _queryFilterBuilder;

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC, 
		service = QueryContributor.class, 
		unbind = "removeQueryContributor"
	)
	private volatile List<QueryContributor> _queryContributors = null;

	private static final Log _log =
		LogFactoryUtil.getLog(QueryBuilderImpl.class);
}
