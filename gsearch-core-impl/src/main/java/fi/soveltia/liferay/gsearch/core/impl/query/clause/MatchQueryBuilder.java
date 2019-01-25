
package fi.soveltia.liferay.gsearch.core.impl.query.clause;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.MatchQuery;
import com.liferay.portal.kernel.search.generic.MatchQuery.Operator;
import com.liferay.portal.kernel.search.generic.MatchQuery.Type;
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

/**
 * Match query builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ClauseBuilder.class
)
public class MatchQueryBuilderImpl implements ClauseBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query buildClause(
		PortletRequest portletRequest, JSONObject configuration,
		QueryContext queryContext)
		throws Exception {
		
		Locale locale = (Locale)queryContext.getParameter(ParameterNames.LOCALE);

		String fieldName = configuration.getString("field_name");

		if (fieldName == null) {
			return null;
		}

		String keywords = null;

		if (Validator.isNotNull(configuration.get("query"))) {

			keywords = configuration.getString("query");

			keywords = _configurationHelper.parseConfigurationVariables(
				portletRequest, queryContext, keywords);
		}

		if (Validator.isNull(keywords)) {
			keywords = queryContext.getKeywords();
		}

		MatchQuery matchQuery =
			buildClause(configuration, queryContext, fieldName, keywords);

		// Is localized

		boolean isLocalized = configuration.getBoolean("localized");

		if (isLocalized) {
			
			String localizedFieldName =
				fieldName + "_" + locale.toString();

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

			BooleanQuery booleanQuery = new BooleanQueryImpl();

			booleanQuery.add(matchQuery, BooleanClauseOccur.SHOULD);
			booleanQuery.add(localizedQuery, BooleanClauseOccur.SHOULD);

			return booleanQuery;
		}

		return matchQuery;
	}

	protected MatchQuery buildClause(
		JSONObject configuration, QueryContext queryContext, String fieldName,
		String query)
		throws Exception {

		MatchQuery matchQuery = new MatchQuery(fieldName, query);

		// Analyzer

		if (Validator.isNotNull(configuration.get("analyzer"))) {
			matchQuery.setAnalyzer(configuration.getString("analyzer"));
		}

		// Boost

		if (Validator.isNotNull(configuration.get("boost"))) {
			matchQuery.setBoost(
				GetterUtil.getFloat(configuration.get("boost")));
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
			matchQuery.setLenient(configuration.getBoolean(("lenient")));
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

		// Operator

		if (Validator.isNotNull(configuration.get("operator"))) {

			if ("or".equalsIgnoreCase(configuration.getString("operator"))) {
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

		// Type

		if (Validator.isNotNull(configuration.get("type"))) {

			String type = configuration.getString("type");

			if (Type.BOOLEAN.name().equalsIgnoreCase(type)) {
				matchQuery.setType(Type.BOOLEAN);
			}
			else if (Type.PHRASE.name().equalsIgnoreCase(type)) {
				matchQuery.setType(Type.PHRASE);
			}
			else if (Type.PHRASE_PREFIX.name().equalsIgnoreCase(type)) {
				matchQuery.setType(Type.PHRASE_PREFIX);
			}
		}
		return matchQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canBuild(String querytype) {

		return (querytype.equals(QUERY_TYPE));
	}

	private static final String QUERY_TYPE = "match";

	@Reference
	private ConfigurationHelper _configurationHelper;
}
