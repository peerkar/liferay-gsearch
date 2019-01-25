
package fi.soveltia.liferay.gsearch.core.impl.query.clause;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.query.Operator;
import fi.soveltia.liferay.gsearch.query.QueryStringQuery;

/**
 * QueryStringQuery clause builder service implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ClauseBuilder.class
)
public class QueryStringQueryClauseBuilder implements ClauseBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query buildClause(
		PortletRequest portletRequest, JSONObject configuration,
		QueryContext queryContext)
		throws Exception {

		Locale locale = (Locale)queryContext.getParameter(ParameterNames.LOCALE);

		String keywords = null;

		if (Validator.isNotNull(configuration.get("query"))) {

			keywords = configuration.getString("query");

			keywords = _configurationHelper.parseConfigurationVariables(
				portletRequest, queryContext, keywords);
		}

		if (Validator.isNull(keywords)) {
			keywords = queryContext.getKeywords();
		}

		QueryStringQuery queryStringQuery = new QueryStringQuery(keywords);
		
		JSONArray fields = configuration.getJSONArray("fields");

		for (int i = 0; i < fields.length(); i++) {

			JSONObject field = fields.getJSONObject(i);

			// Add non translated version

			String fieldName = field.getString("field_name");

			float boost = GetterUtil.getFloat(field.getString("boost"), 1.0f);

			queryStringQuery.addField(fieldName, boost);
			
			// Add translated version

			boolean isLocalized =
				GetterUtil.getBoolean(field.get("localized"), false);

			if (isLocalized) {

				String localizedFieldName =
					fieldName + "_" + locale.toString();
				float localizedBoost = GetterUtil.getFloat(
					field.getString("boost_localized_version"), 1.0f);

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

		if (Validator.isNotNull(configuration.get("analyzer"))) {
			queryStringQuery.setAnalyzer(configuration.getString("analyzer"));
		}

		// Boost

		if (Validator.isNotNull(configuration.get("boost"))) {
			queryStringQuery.setBoost(
				GetterUtil.getFloat(configuration.get("boost")));
		}

		// Default operator

		if (Validator.isNotNull(configuration.get("default_operator"))) {

			String operator =
				GetterUtil.getString(configuration.get("default_operator"), "and");

			if (operator.equalsIgnoreCase("or")) {
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

		if (Validator.isNotNull(configuration.get("max_determinized_states"))) {
			queryStringQuery.setMaxDeterminizedStates(
				configuration.getInt("max_determinized_states"));
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

		return (querytype.equals(QUERY_TYPE));
	}

	private static final String QUERY_TYPE = "query_string";

	@Reference
	private ConfigurationHelper _configurationHelper;
}
