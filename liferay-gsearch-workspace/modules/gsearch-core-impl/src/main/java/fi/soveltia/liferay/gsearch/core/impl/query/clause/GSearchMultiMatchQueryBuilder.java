
package fi.soveltia.liferay.gsearch.core.impl.query.clause;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.query.MultiMatchQuery;
import com.liferay.portal.search.query.Operator;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.Query;

import fi.soveltia.liferay.gsearch.core.api.configuration.CoreConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationValues;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.query.GSearchMultiMatchQuery;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Match all query builder.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ClauseBuilder.class
)
public class GSearchMultiMatchQueryBuilder implements ClauseBuilder {

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

		Locale locale = (Locale)queryContext.getParameter(
				ParameterNames.LOCALE);

		// Fields.

		JSONArray fields = configuration.getJSONArray(ClauseConfigurationKeys.FIELDS);

		Map<String, Float> fieldsWithBoosts = new HashMap<String, Float>();
		
		for (int i = 0; i < fields.length(); i++) {

			JSONObject field = fields.getJSONObject(i);

			// Add non translated version.

			String fieldName = field.getString(ClauseConfigurationKeys.FIELD_NAME);

			float boost = GetterUtil.getFloat(field.getString(ClauseConfigurationKeys.BOOST), 1.0F);

			fieldsWithBoosts.put(fieldName, boost);

			// Add translated version

			boolean localized = GetterUtil.getBoolean(
				field.get("localized"), false);

			if (localized) {
				String localizedFieldName = fieldName + "_" + locale.toString();
				float localizedBoost = GetterUtil.getFloat(
					field.getString("boost_localized_version"), 1F);

				fieldsWithBoosts.put(localizedFieldName, localizedBoost);
			}
		}

		GSearchMultiMatchQuery multiMatchQuery = new GSearchMultiMatchQuery(
				keywords, fieldsWithBoosts);


		// Analyzer

		if (Validator.isNotNull(configuration.get(ClauseConfigurationKeys.ANALYZER))) {
			multiMatchQuery.setAnalyzer(configuration.getString(ClauseConfigurationKeys.ANALYZER));
		}

		// Boost

		if (Validator.isNotNull(configuration.get(ClauseConfigurationKeys.BOOST))) {
			multiMatchQuery.setBoost(
				GetterUtil.getFloat(configuration.get(ClauseConfigurationKeys.BOOST)));
		}

		// Cut off frequency

		if (Validator.isNotNull(configuration.get("cut_off_frequency"))) {
			multiMatchQuery.setCutOffFrequency(
				GetterUtil.getFloat(configuration.get("cut_off_frequency")));
		}

		// Fuzziness

		if (Validator.isNotNull(configuration.get("fuzziness"))) {
			multiMatchQuery.setFuzziness(configuration.getString("fuzziness"));
		}

		// Lenient

		if (Validator.isNotNull(configuration.get("lenient"))) {
			multiMatchQuery.setLenient(configuration.getBoolean("lenient"));
		}

		// Max expansions

		if (Validator.isNotNull(configuration.get("max_expansions"))) {
			multiMatchQuery.setMaxExpansions(
				configuration.getInt("max_expansions"));
		}

		// Minimum should match expansions

		if (Validator.isNotNull(configuration.get("minimum_should_match"))) {
			multiMatchQuery.setMinShouldMatch(
				configuration.getString("minimum_should_match"));
		}

		// MatchQuery.Operator

		if (Validator.isNotNull(configuration.get(ClauseConfigurationKeys.OPERATOR))) {
			if (ClauseConfigurationValues.OPERATOR_OR.equalsIgnoreCase(
					configuration.getString(ClauseConfigurationKeys.OPERATOR))) {
				multiMatchQuery.setOperator(Operator.OR);
			}
			else {
				multiMatchQuery.setOperator(Operator.AND);
			}
		}

		// Slop

		if (Validator.isNotNull(configuration.get("slop"))) {
			multiMatchQuery.setSlop(configuration.getInt("slop"));
		}

		// Type

		if (Validator.isNotNull(configuration.get("type"))) {
			String type = configuration.getString("type");

			if (MultiMatchQuery.Type.BEST_FIELDS.name().equalsIgnoreCase(
					type)) {

				multiMatchQuery.setType(MultiMatchQuery.Type.BEST_FIELDS);
			}
			else if (MultiMatchQuery.Type.CROSS_FIELDS.name().equalsIgnoreCase(
						type)) {

				multiMatchQuery.setType(MultiMatchQuery.Type.CROSS_FIELDS);
			}
			else if (MultiMatchQuery.Type.MOST_FIELDS.name().equalsIgnoreCase(
						type)) {

				multiMatchQuery.setType(MultiMatchQuery.Type.MOST_FIELDS);
			}
			else if (MultiMatchQuery.Type.PHRASE.name().equalsIgnoreCase(
						type)) {

				multiMatchQuery.setType(MultiMatchQuery.Type.PHRASE);
			}
			else if (MultiMatchQuery.Type.PHRASE_PREFIX.name().equalsIgnoreCase(
						type)) {

				multiMatchQuery.setType(MultiMatchQuery.Type.PHRASE_PREFIX);
			}
		}

		return multiMatchQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canBuild(String querytype) {
		return querytype.equals(_QUERY_TYPE);
	}

	private static final String _QUERY_TYPE = 
			ClauseConfigurationValues.QUERY_TYPE_GSEARCH_MULTIMATCH;

	@Reference
	private CoreConfigurationHelper _coreConfigurationHelper;

	@Reference
	private Queries _queries;

}