package fi.soveltia.liferay.gsearch.core.impl.query.clause;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.query.FunctionScoreQuery;
import com.liferay.portal.search.query.Query;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.configuration.CoreConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationValues;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.query.GSearchFieldValueFactorFunctionScoreQuery;

/**
 * Field value factor function score query builder.
 *
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-function-score-query.html
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ClauseBuilder.class
)
public class GSearchFieldValueFactorFunctionScoreQueryBuilder
	implements ClauseBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query buildClause(
			QueryContext queryContext, JSONObject configuration)
		throws Exception {

		String fieldName = configuration.getString(ClauseConfigurationKeys.FIELD_NAME);

		if (fieldName == null) {
			return null;
		}

		GSearchFieldValueFactorFunctionScoreQuery functionScoreQuery =
			new GSearchFieldValueFactorFunctionScoreQuery(null);

		// Boost.

		if (Validator.isNotNull(configuration.get(ClauseConfigurationKeys.BOOST))) {
			functionScoreQuery.setBoost(
				GetterUtil.getFloat(configuration.get(ClauseConfigurationKeys.BOOST)));
		}

		// Boostmode.

		if (Validator.isNotNull(configuration.get("boost_mode"))) {
			functionScoreQuery.setBoostMode(
				GetterUtil.getString(configuration.get("boost_mode")));
		}

		// Factor.

		if (Validator.isNotNull(configuration.get("factor"))) {
			functionScoreQuery.setFactor(
				GetterUtil.getFloat(configuration.get("factor")));
		}

		// Field name

		functionScoreQuery.setFieldName(fieldName);

		// Missing field value.

		if (Validator.isNotNull(configuration.get("missing"))) {
			functionScoreQuery.setMissing(configuration.getDouble("missing"));
		}

		// Modified.

		if (Validator.isNotNull(configuration.get("modifier"))) {
			functionScoreQuery.setModifier(configuration.getString("modifier"));
		}

		// Max boost

		if (Validator.isNotNull(configuration.get("max_boost"))) {
			functionScoreQuery.setMaxBoost(
				GetterUtil.getFloat(configuration.get("max_boost")));
		}

		// Min score

		if (Validator.isNotNull(configuration.get("min_score"))) {
			functionScoreQuery.setMinScore(
				GetterUtil.getFloat(configuration.get("min_score")));
		}

		// Score mode

		if (Validator.isNotNull(configuration.get("score_mode"))) {
			
			String value = configuration.getString("score_mode").toUpperCase();

			FunctionScoreQuery.ScoreMode scoreMode = FunctionScoreQuery.ScoreMode.valueOf(value);

			if (scoreMode != null ) {
				functionScoreQuery.setScoreMode(scoreMode);
			}
		}

		return functionScoreQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canBuild(String querytype) {
		return querytype.equals(_QUERY_TYPE);
	}

	private static final String _QUERY_TYPE = 
			ClauseConfigurationValues.QUERY_TYPE_GSEARCH_FIELD_VALUE_FACTOR_FUNCTION_SCORE;

	@Reference
	private CoreConfigurationHelper _coreConfigurationHelper;
}