
package fi.soveltia.liferay.gsearch.core.impl.query.clause;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.MatchQuery;
import com.liferay.portal.search.query.Operator;
import com.liferay.portal.search.query.Queries;
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

/**
 * Match query builder.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ClauseBuilder.class
)
public class MatchQueryBuilder implements ClauseBuilder {

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

		String keywords = (String)configuration.get(ClauseConfigurationKeys.QUERY);

		if (Validator.isBlank(keywords)) {
			keywords = queryContext.getKeywords();
		}		

		MatchQuery matchQuery = buildClause(
			configuration, queryContext, fieldName, keywords);

		Locale locale = (Locale)queryContext.getParameter(
				ParameterNames.LOCALE);

		// Is localized

		boolean localized = configuration.getBoolean("localized");

		if (localized) {
			String localizedFieldName = fieldName + "_" + locale.toString();

			MatchQuery localizedQuery = buildClause(
				configuration, queryContext, localizedFieldName, keywords);

			// Boost for localized version

			if (Validator.isNotNull(
					configuration.get("boost_localized_version"))) {

				localizedQuery.setBoost(
					GetterUtil.getFloat(
						configuration.get("boost_localized_version")));
			}

			// Add subqueries to the query

			BooleanQuery booleanQuery = _queries.booleanQuery();

			booleanQuery.addShouldQueryClauses(matchQuery);
			booleanQuery.addShouldQueryClauses(localizedQuery);

			return booleanQuery;
		}

		return matchQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canBuild(String querytype) {
		return querytype.equals(_QUERY_TYPE);
	}

	protected MatchQuery buildClause(
			JSONObject configuration, QueryContext queryContext,
			String fieldName, String query)
		throws Exception {

		MatchQuery matchQuery = _queries.match(fieldName, query);

		// Analyzer

		if (Validator.isNotNull(configuration.get(ClauseConfigurationKeys.ANALYZER))) {
			matchQuery.setAnalyzer(configuration.getString(ClauseConfigurationKeys.ANALYZER));
		}

		// Boost

		if (Validator.isNotNull(configuration.get(ClauseConfigurationKeys.BOOST))) {
			matchQuery.setBoost(
				GetterUtil.getFloat(configuration.get(ClauseConfigurationKeys.BOOST)));
		}
		
		// Cut off frequency

		if (Validator.isNotNull(configuration.get("cut_off_frequency"))) {
			matchQuery.setCutOffFrequency(
				GetterUtil.getFloat(configuration.get("cut_off_frequency")));
		}

		// Fuzziness

		if (Validator.isNotNull(configuration.get("fuzziness"))) {
			matchQuery.setFuzziness(
				GetterUtil.getFloat(configuration.get("fuzziness")));
		}

		// Lenient

		if (Validator.isNotNull(configuration.get("lenient"))) {
			matchQuery.setLenient(configuration.getBoolean("lenient"));
		}

		// Max expansions

		if (Validator.isNotNull(configuration.get("max_expansions"))) {
			matchQuery.setMaxExpansions(configuration.getInt("max_expansions"));
		}

		// Minimum should match expansions

		if (Validator.isNotNull(configuration.get("minimum_should_match"))) {
			matchQuery.setMinShouldMatch(
				configuration.getString("minimum_should_match"));
		}

		// MatchQuery.Operator

		if (Validator.isNotNull(
				configuration.get(ClauseConfigurationKeys.OPERATOR))) {
			
			if (ClauseConfigurationValues.OPERATOR_OR.equalsIgnoreCase(
					configuration.getString(ClauseConfigurationKeys.OPERATOR))) {
				matchQuery.setOperator(Operator.OR);
			}
			else {
				matchQuery.setOperator(Operator.AND);
			}
		}

		// Slop

		if (Validator.isNotNull(configuration.get("slop"))) {
			matchQuery.setSlop(configuration.getInt("slop"));
		}

		// MatchQuery.Type

		if (Validator.isNotNull(configuration.get("type"))) {
			String type = configuration.getString("type");

			if (MatchQuery.Type.BOOLEAN.name().equalsIgnoreCase(type)) {
				matchQuery.setType(MatchQuery.Type.BOOLEAN);
			}
			else if (MatchQuery.Type.PHRASE.name().equalsIgnoreCase(type)) {
				matchQuery.setType(MatchQuery.Type.PHRASE);
			}
			else if (MatchQuery.Type.PHRASE_PREFIX.name().equalsIgnoreCase(
						type)) {

				matchQuery.setType(MatchQuery.Type.PHRASE_PREFIX);
			}
		}

		return matchQuery;
	}

	private static final String _QUERY_TYPE = 
			ClauseConfigurationValues.QUERY_TYPE_MATCH;

	@Reference
	private CoreConfigurationHelper _coreConfigurationHelper;

	@Reference
	private Queries _queries;
}