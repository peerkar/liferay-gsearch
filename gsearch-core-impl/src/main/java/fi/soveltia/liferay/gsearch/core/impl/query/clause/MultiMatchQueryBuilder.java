
package fi.soveltia.liferay.gsearch.core.impl.query.clause;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.generic.MatchQuery.Operator;
import com.liferay.portal.kernel.search.generic.MultiMatchQuery;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.query.GSearchMultiMatchQuery;

/**
 * Match all query builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ClauseBuilder.class
)
public class MultiMatchQueryBuilder implements ClauseBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query buildClause(
		QueryContext queryContext, JSONObject configuration)
		throws Exception {

		Locale locale = (Locale)queryContext.getParameter(ParameterNames.LOCALE);

		// Keywords

		String keywords = null;

		if (Validator.isNotNull(configuration.get("query"))) {

			keywords = configuration.getString("query");

			keywords = _configurationHelper.parseConfigurationVariables(
				queryContext, keywords);
		}

		if (Validator.isNull(keywords)) {
			keywords = queryContext.getKeywords();
		}

		GSearchMultiMatchQuery multiMatchQuery =
			new GSearchMultiMatchQuery(keywords);

		// Fields

		JSONArray fields = configuration.getJSONArray("fields");

		for (int i = 0; i < fields.length(); i++) {

			JSONObject field = fields.getJSONObject(i);

			// Add non translated version

			String fieldName = field.getString("field_name");

			float boost = GetterUtil.getFloat(field.getString("boost"), 1.0f);

			multiMatchQuery.addField(fieldName, boost);

			// Add translated version

			boolean isLocalized =
				GetterUtil.getBoolean(field.get("localized"), false);

			if (isLocalized) {

				String localizedFieldName =
					fieldName + "_" + locale.toString();
				float localizedBoost = GetterUtil.getFloat(
					field.getString("boost_localized_version"), 1f);

				multiMatchQuery.addField(localizedFieldName, localizedBoost);
			}
		}

		// Analyzer

		if (Validator.isNotNull(configuration.get("analyzer"))) {
			multiMatchQuery.setAnalyzer(configuration.getString("analyzer"));
		}

		// Boost

		if (Validator.isNotNull(configuration.get("boost"))) {
			multiMatchQuery.setBoost(
				GetterUtil.getFloat(configuration.get("boost")));
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
			multiMatchQuery.setLenient(configuration.getBoolean(("lenient")));
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

		// Operator

		if (Validator.isNotNull(configuration.get("operator"))) {

			if ("or".equalsIgnoreCase(configuration.getString("operator"))) {
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

		return (querytype.equals(QUERY_TYPE));
	}

	private static final String QUERY_TYPE = "multi_match";

	@Reference
	private ConfigurationHelper _configurationHelper;
}
