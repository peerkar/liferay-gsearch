package fi.soveltia.liferay.gsearch.core.impl.query.clause;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.query.FieldValueFactorFunctionScoreQuery;

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
public class FieldValueFactorFunctionScoreQueryBuilder implements ClauseBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query buildClause(
		PortletRequest portletRequest, JSONObject configuration,
		QueryContext queryContext)
		throws Exception {

		String fieldName = configuration.getString("field_name");

		if (fieldName == null) {
			return null;
		}

		FieldValueFactorFunctionScoreQuery functionScoreQuery =
			new FieldValueFactorFunctionScoreQuery(null);

		// Boost.

		if (Validator.isNotNull(configuration.get("boost"))) {
			functionScoreQuery.setBoost(
				GetterUtil.getFloat(configuration.get("boost")));
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
			functionScoreQuery.setMissing(
				configuration.getDouble("missing"));
		}

		// Modified.

		if (Validator.isNotNull(configuration.get("modifier"))) {
			functionScoreQuery.setModifier(
				configuration.getString("modifier"));
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
			functionScoreQuery.setScoreMode(configuration.getString("score_mode"));
		}
		
		return functionScoreQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canBuild(String querytype) {

		return (querytype.equals(QUERY_TYPE));
	}

	private static final String QUERY_TYPE = "field_value_factor";

	@Reference
	private ConfigurationHelper _configurationHelper;
}
