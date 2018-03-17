
package fi.soveltia.liferay.gsearch.core.impl.query.builder;

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

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.query.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.query.builder.MatchQueryBuilder;

/**
 * MatchQuery builder service implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(immediate = true, service = MatchQueryBuilder.class)
public class MatchQueryBuilderImpl implements MatchQueryBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query buildQuery(
		JSONObject configurationObject, QueryParams queryParams)
		throws Exception {

		String fieldName = configurationObject.getString("fieldName");

		if (fieldName == null) {
			return null;
		}
		
		// If there's a predefined value in the configuration, use that

		String value = configurationObject.getString("value");

		if (Validator.isNull(value)) {
			value = queryParams.getKeywords();
		}		
	
		MatchQuery matchQuery = buildQuery(configurationObject, queryParams, fieldName, value);
		
		// Is localized

		boolean isLocalized = configurationObject.getBoolean("localized");

		if (isLocalized) {
			String localizedFieldName =
							fieldName + "_" + queryParams.getLocale().toString();

			MatchQuery localizedQuery = buildQuery(configurationObject, queryParams, localizedFieldName, value);

			// Boost for localized
			
			float localizedBoost = GetterUtil.getFloat(
				configurationObject.getString("boostForLocalizedVersion"), 1f);
			
			localizedQuery.setBoost(localizedBoost);

			// Add subqueries to the query
	
			BooleanQuery booleanQuery = new BooleanQueryImpl();
	
			booleanQuery.add(matchQuery, BooleanClauseOccur.SHOULD);
			booleanQuery.add(localizedQuery, BooleanClauseOccur.SHOULD);
	
			return booleanQuery;
		}
		
		return matchQuery;
	}

	protected MatchQuery buildQuery(
		JSONObject configurationObject, QueryParams queryParams,
		String fieldName, String value)
		throws Exception {

		MatchQuery matchQuery = new MatchQuery(fieldName, value);

		// Type

		String type = configurationObject.getString("type");

		if (Validator.isNotNull(type)) {

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

		// Boost

		float boost =
			GetterUtil.getFloat(configurationObject.get("boost"), 1.0f);
		matchQuery.setBoost(boost);

		// Operator

		String operator =
			GetterUtil.getString(configurationObject.get("operator"), "and");

		if ("or".equals(operator)) {
			matchQuery.setOperator(Operator.AND);
		}
		else {
			matchQuery.setOperator(Operator.AND);
		}

		// Analyzer

		String analyzer =
			GetterUtil.getString(configurationObject.get("analyzer"));

		if (Validator.isNotNull(analyzer)) {
			matchQuery.setAnalyzer(analyzer);
		}

		// Cut off frequency

		if (Validator.isNotNull(configurationObject.get("cutoffFrequency"))) {
			float cutOffFrequency = GetterUtil.getFloat(
				configurationObject.get("cutoffFrequency"), 0.0f);
			matchQuery.setCutOffFrequency(cutOffFrequency);
		}

		// Fuzziness

		if (Validator.isNotNull(configurationObject.get("fuzziness"))) {
			float fuzziness =
				GetterUtil.getFloat(configurationObject.get("fuzziness"), 0.0f);
			matchQuery.setFuzziness(fuzziness);
		}
		
		// Minimum should match

		String minShouldMatch =
						GetterUtil.getString(configurationObject.get("minShouldMatch"));

		if (Validator.isNotNull(minShouldMatch)) {
			matchQuery.setMinShouldMatch(minShouldMatch);
		}
				
		return matchQuery;
	}
}
