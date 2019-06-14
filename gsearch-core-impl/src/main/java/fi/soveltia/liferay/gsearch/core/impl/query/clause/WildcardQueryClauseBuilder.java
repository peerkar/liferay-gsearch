
package fi.soveltia.liferay.gsearch.core.impl.query.clause;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.WildcardQuery;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.WildcardQueryImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
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

		String keywords = null;

		if (Validator.isNotNull(configuration.get("query"))) {

			keywords = configuration.getString("query");

			keywords = _configurationHelper.parseConfigurationVariables(
				queryContext, keywords);
		}

		if (Validator.isNull(keywords)) {
			keywords = queryContext.getKeywords();
		}

		// Splitter?

		String keywordSplitter =
			configuration.getString("keyword_splitter_regexp");

		if (keywordSplitter != null && keywordSplitter.length() > 0) {

			BooleanQuery query = new BooleanQueryImpl();

			String[] keywordArray = keywords.split(keywordSplitter);

			for (String keyword : keywordArray) {

				WildcardQuery q = buildClause(configuration, keyword);

				query.add(q, BooleanClauseOccur.SHOULD);
			}

			// Boost

			if (Validator.isNotNull(configuration.get("boost"))) {
				query.setBoost(GetterUtil.getFloat(configuration.get("boost")));
			}

			return query;

		}
		else {

			WildcardQuery wildcardQuery = buildClause(configuration, keywords);

			// Boost

			if (Validator.isNotNull(configuration.get("boost"))) {
				wildcardQuery.setBoost(
					GetterUtil.getFloat(configuration.get("boost")));
			}

			return wildcardQuery;
		}
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

		String fieldName = configuration.getString("field_name");

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

		WildcardQuery wildcardQuery =
			new WildcardQueryImpl(fieldName, sb.toString());

		return wildcardQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canBuild(String querytype) {

		return (querytype.equals(QUERY_TYPE));
	}

	private static final String QUERY_TYPE = "wildcard";

	@Reference
	private ConfigurationHelper _configurationHelper;
}
