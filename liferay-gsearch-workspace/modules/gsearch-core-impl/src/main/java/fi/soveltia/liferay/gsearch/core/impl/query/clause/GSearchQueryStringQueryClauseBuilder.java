
package fi.soveltia.liferay.gsearch.core.impl.query.clause;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.query.Operator;
import com.liferay.portal.search.query.Query;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.configuration.CoreConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationValues;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.query.GSearchQueryStringQuery;

/**
 * QueryStringQuery clause builder service implementation.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ClauseBuilder.class
)
public class GSearchQueryStringQueryClauseBuilder implements ClauseBuilder {

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
		
		GSearchQueryStringQuery queryStringQuery = new GSearchQueryStringQuery(keywords);

		JSONArray fields = configuration.getJSONArray(ClauseConfigurationKeys.FIELDS);

		Locale locale = (Locale)queryContext.getParameter(
				ParameterNames.LOCALE);

		for (int i = 0; i < fields.length(); i++) {
			
			JSONObject field = fields.getJSONObject(i);

			// Add non translated version

			String fieldName = field.getString(ClauseConfigurationKeys.FIELD_NAME);

			float boost = GetterUtil.getFloat(field.getString(ClauseConfigurationKeys.BOOST), 1.0F);

			queryStringQuery.addField(fieldName, boost);

			// Add translated version

			boolean localized = GetterUtil.getBoolean(
				field.get("localized"), false);

			if (localized) {
				String localizedFieldName = fieldName + "_" + locale.toString();
				float localizedBoost = GetterUtil.getFloat(
					field.getString("boost_localized_version"), 1.0F);

				queryStringQuery.addField(localizedFieldName, localizedBoost);
			}
		}

		// Allow leading wildcard

		if (Validator.isNotNull(configuration.get("allow_leading_wildcard"))) {
			queryStringQuery.setAllowLeadingWildcard(
				configuration.getBoolean("allow_leading_wildcard"));
		}

		// Analyze wildcard

		if (Validator.isNotNull(configuration.get("analyze_wildcard"))) {
			queryStringQuery.setAnalyzeWildcard(
				configuration.getBoolean("analyze_wildcard"));
		}

		// Analyzer

		if (Validator.isNotNull(configuration.get(ClauseConfigurationKeys.ANALYZER))) {
			queryStringQuery.setAnalyzer(configuration.getString(ClauseConfigurationKeys.ANALYZER));
		}

		// Boost

		if (Validator.isNotNull(configuration.get(ClauseConfigurationKeys.BOOST))) {
			queryStringQuery.setBoost(
				GetterUtil.getFloat(configuration.get(ClauseConfigurationKeys.BOOST)));
		}

		// Default operator

		if (Validator.isNotNull(configuration.get(ClauseConfigurationKeys.DEFAULT_OPERATOR))) {
			String operator = GetterUtil.getString(
				configuration.get(ClauseConfigurationKeys.DEFAULT_OPERATOR), 
				ClauseConfigurationValues.OPERATOR_AND);

			if (operator.equalsIgnoreCase(ClauseConfigurationValues.OPERATOR_OR)) {
				queryStringQuery.setDefaultOperator(Operator.OR);
			}
			else {
				queryStringQuery.setDefaultOperator(Operator.AND);
			}
		}

		// Enable position increments

		if (Validator.isNotNull(
				configuration.get("enable_position_increments"))) {

			queryStringQuery.setEnablePositionIncrements(
				configuration.getBoolean("enable_position_increments"));
		}

		// Fuzziness

		if (Validator.isNotNull(configuration.get("fuzziness"))) {
			queryStringQuery.setFuzziness(
				GetterUtil.getFloat(configuration.get("fuzziness")));
		}

		// Fuzzy max expansions

		if (Validator.isNotNull(configuration.get("fuzzy_max_expansions"))) {
			queryStringQuery.setFuzzyMaxExpansions(
				configuration.getInt("fuzzy_max_expansions"));
		}

		// Fuzzy prefix length

		if (Validator.isNotNull(configuration.get("fuzzy_prefix_length"))) {
			queryStringQuery.setFuzzyPrefixLength(
				configuration.getInt("fuzzy_prefix_length"));
		}

		// Lenient

		if (Validator.isNotNull(configuration.get("lenient"))) {
			queryStringQuery.setLenient(configuration.getBoolean("lenient"));
		}

		// Max determined states

		if (Validator.isNotNull(configuration.get("max_determined_states"))) {
			queryStringQuery.setMaxDeterminedStates(
				configuration.getInt("max_determined_states"));
		}

		// Minimum should match expansions

		if (Validator.isNotNull(configuration.get("minimum_should_match"))) {
			queryStringQuery.setMinimumShouldMatch(
				configuration.getString("minimum_should_match"));
		}

		// Phrase slop

		if (Validator.isNotNull(configuration.get("phrase_slop"))) {
			queryStringQuery.setPhraseSlop(configuration.getInt("phrase_slop"));
		}

		// Quote analyzer

		if (Validator.isNotNull(configuration.get("quote_analyzer"))) {
			queryStringQuery.setQuoteAnalyzer(
				configuration.getString("quote_analyzer"));
		}

		// Quote analyzer

		if (Validator.isNotNull(configuration.get("quote_field_suffix"))) {
			queryStringQuery.setQuoteFieldSuffix(
				configuration.getString("quote_field_suffix"));
		}

		// Tie breaker

		if (Validator.isNotNull(configuration.get("tie_breaker"))) {
			queryStringQuery.setTieBreaker(
				GetterUtil.getFloat(configuration.getString("tie_breaker")));
		}

		return queryStringQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canBuild(String querytype) {
		return querytype.equals(_QUERY_TYPE);
	}

	private static final String _QUERY_TYPE =
			ClauseConfigurationValues.QUERY_TYPE_GSEARCH_QUERY_STRING;

	@Reference
	private CoreConfigurationHelper _coreConfigurationHelper;
}