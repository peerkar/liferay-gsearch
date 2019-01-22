
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
import fi.soveltia.liferay.gsearch.query.DecayFunctionScoreQuery;

/**
 * Decay function score query builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ClauseBuilder.class
)
public class DecayFunctionScoreQueryBuilderImpl implements ClauseBuilder {

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

		DecayFunctionScoreQuery functionScoreQuery =
			new DecayFunctionScoreQuery(null);

		// Boost

		if (Validator.isNotNull(configuration.get("boost"))) {
			functionScoreQuery.setBoost(
				GetterUtil.getFloat(configuration.get("boost")));
		}

		// Boostmode

		if (Validator.isNotNull(configuration.get("boost_mode"))) {
			functionScoreQuery.setBoostMode(
				GetterUtil.getString(configuration.get("boost_mode")));
		}

		// Decay

		if (Validator.isNotNull(configuration.get("decay"))) {
			functionScoreQuery.setDecay(
				GetterUtil.getDouble(configuration.get("decay")));
		}

		// Field name

		functionScoreQuery.setFieldName(fieldName);

		// Function type (gauss, linear, exp)

		if (Validator.isNotNull(configuration.get("function_type"))) {
			functionScoreQuery.setFunctionType(
				configuration.getString("function_type"));
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

		// Multivalue mode

		if (Validator.isNotNull(configuration.get("multi_value_mode"))) {
			functionScoreQuery.setMultiValueMode(
				configuration.getString("multi_value_mode"));
		}

		// Offset

		if (Validator.isNotNull(configuration.get("offset"))) {
			functionScoreQuery.setOffset(
				configuration.getString("offset"));
		}

		// Origin
		
		if (Validator.isNotNull(configuration.get("origin"))) {
			

			if (configuration.getString("origin_type").equals("date")) {
				functionScoreQuery.setOrigin(
					_configurationHelper.parseConfigurationVariables(
						portletRequest, queryContext, configuration.getString("origin")));
			}
		}

		// Scale

		if (Validator.isNotNull(configuration.get("scale"))) {
			functionScoreQuery.setScale(configuration.getString("scale"));
		}

		// Score mode

		if (Validator.isNotNull(configuration.get("score_mode"))) {
			functionScoreQuery.setScoreMode(configuration.getString("score_mode"));
		}

		// Weight

		if (Validator.isNotNull(configuration.get("weight"))) {
			functionScoreQuery.setWeight(
				GetterUtil.getFloat(configuration.get("weight")));
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

	private static final String QUERY_TYPE = "decay_function_score";

	@Reference
	private ConfigurationHelper _configurationHelper;
}
