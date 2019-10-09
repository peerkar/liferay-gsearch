package fi.soveltia.liferay.gsearch.core.impl.query.clause;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.query.Operator;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.query.StringQuery;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.configuration.CoreConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationValues;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * String query clause builder service implementation.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ClauseBuilder.class
)
public class StringQueryClauseBuilder implements ClauseBuilder {

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
		
		StringQuery stringQuery = _queries.string(keywords);

		// Allow leading wildcard

		if (Validator.isNotNull(configuration.get("allow_leading_wildcard"))) {
			stringQuery.setAllowLeadingWildcard(
				configuration.getBoolean("allow_leading_wildcard"));
		}

		// Analyze wildcard

		if (Validator.isNotNull(configuration.get("analyze_wildcard"))) {
			stringQuery.setAnalyzeWildcard(
				configuration.getBoolean("analyze_wildcard"));
		}

		// Analyzer

		if (Validator.isNotNull(configuration.get(ClauseConfigurationKeys.ANALYZER))) {
			stringQuery.setAnalyzer(configuration.getString(ClauseConfigurationKeys.ANALYZER));
		}

		// Boost

		if (Validator.isNotNull(configuration.get(ClauseConfigurationKeys.BOOST))) {
			stringQuery.setBoost(
				GetterUtil.getFloat(configuration.get(ClauseConfigurationKeys.BOOST)));
		}

		// Default operator

		if (Validator.isNotNull(configuration.get(ClauseConfigurationKeys.DEFAULT_OPERATOR))) {
			String operator = GetterUtil.getString(
				configuration.get(ClauseConfigurationKeys.DEFAULT_OPERATOR), 
				ClauseConfigurationValues.OPERATOR_AND);

			if (operator.equalsIgnoreCase(ClauseConfigurationValues.OPERATOR_OR)) {
				stringQuery.setDefaultOperator(Operator.OR);
			}
			else {
				stringQuery.setDefaultOperator(Operator.AND);
			}
		}

		// Enable position increments

		if (Validator.isNotNull(
				configuration.get("enable_position_increments"))) {

			stringQuery.setEnablePositionIncrements(
				configuration.getBoolean("enable_position_increments"));
		}

		// Fuzziness

		if (Validator.isNotNull(configuration.get("fuzziness"))) {
			stringQuery.setFuzziness(
				GetterUtil.getFloat(configuration.get("fuzziness")));
		}

		// Fuzzy max expansions

		if (Validator.isNotNull(configuration.get("fuzzy_max_expansions"))) {
			stringQuery.setFuzzyMaxExpansions(
				configuration.getInt("fuzzy_max_expansions"));
		}

		// Fuzzy prefix length

		if (Validator.isNotNull(configuration.get("fuzzy_prefix_length"))) {
			stringQuery.setFuzzyPrefixLength(
				configuration.getInt("fuzzy_prefix_length"));
		}

		// Lenient

		if (Validator.isNotNull(configuration.get("lenient"))) {
			stringQuery.setLenient(configuration.getBoolean("lenient"));
		}

		// Max determined states

		if (Validator.isNotNull(configuration.get("max_determined_states"))) {
			stringQuery.setMaxDeterminedStates(
				configuration.getInt("max_determined_states"));
		}

		// Phrase slop

		if (Validator.isNotNull(configuration.get("phrase_slop"))) {
			stringQuery.setPhraseSlop(configuration.getInt("phrase_slop"));
		}

		// Quote analyzer

		if (Validator.isNotNull(configuration.get("quote_analyzer"))) {
			stringQuery.setQuoteAnalyzer(
				configuration.getString("quote_analyzer"));
		}

		// Quote analyzer

		if (Validator.isNotNull(configuration.get("quote_field_suffix"))) {
			stringQuery.setQuoteFieldSuffix(
				configuration.getString("quote_field_suffix"));
		}

		return stringQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canBuild(String querytype) {
		return querytype.equals(_QUERY_TYPE);
	}

	private static final String _QUERY_TYPE =
			ClauseConfigurationValues.QUERY_TYPE_STRING_QUERY;

	@Reference
	private CoreConfigurationHelper _coreConfigurationHelper;

	@Reference
	private Queries _queries;
}