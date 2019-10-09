
package fi.soveltia.liferay.gsearch.core.impl.query.clause;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.query.WildcardQuery;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.configuration.CoreConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationValues;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Wildcard query builder.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ClauseBuilder.class
)
public class WildcardQueryClauseBuilder implements ClauseBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query buildClause(
			QueryContext queryContext, JSONObject configuration)
		throws Exception {

		String keywords = (String)configuration.get(ClauseConfigurationKeys.QUERY);
		
		if (Validator.isBlank(keywords)) {
			keywords = queryContext.getKeywords();
		}

		// Splitter?

		String keywordSplitter = configuration.getString(
			"keyword_splitter_regexp");

		if (!Validator.isBlank(keywordSplitter)) {
			BooleanQuery query = _queries.booleanQuery();

			String[] keywordArray = keywords.split(keywordSplitter);

			for (String keyword : keywordArray) {
				WildcardQuery q = buildClause(configuration, keyword);

				query.addShouldQueryClauses(q);
			}

			return query;
		}

		WildcardQuery wildcardQuery = buildClause(configuration, keywords);

		// Boost

		if (Validator.isNotNull(configuration.get(ClauseConfigurationKeys.BOOST))) {
			wildcardQuery.setBoost(
				GetterUtil.getFloat(configuration.get(ClauseConfigurationKeys.BOOST)));
		}

		return wildcardQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canBuild(String querytype) {
		return querytype.equals(_QUERY_TYPE);
	}

	/**
	 * Build single clause
	 *
	 * @param configuration
	 * @param keywords
	 * @return
	 */
	protected WildcardQuery buildClause(
		JSONObject configuration, String keyword) {

		String fieldName = configuration.getString(ClauseConfigurationKeys.FIELD_NAME);

		if (fieldName == null) {
			return null;
		}

		StringBundler sb = new StringBundler();

		String prefix = configuration.getString("value_prefix");

		if (Validator.isNotNull(prefix)) {
			sb.append(prefix);
		}

		sb.append(keyword);

		String suffix = configuration.getString("value_suffix");

		if (Validator.isNotNull(suffix)) {
			sb.append(suffix);
		}

		return _queries.wildcard(
				fieldName, sb.toString());
	}

	private static final String _QUERY_TYPE = 
			ClauseConfigurationValues.QUERY_TYPE_WILDCARD;

	@Reference
	private CoreConfigurationHelper _coreConfigurationHelper;

	@Reference
	private Queries _queries;

}