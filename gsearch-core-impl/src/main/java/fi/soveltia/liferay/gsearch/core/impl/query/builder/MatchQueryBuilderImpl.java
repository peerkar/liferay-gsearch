
package fi.soveltia.liferay.gsearch.core.impl.query.builder;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.MatchQuery;
import com.liferay.portal.kernel.search.generic.MatchQuery.Operator;
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
@Component(
	immediate = true, 
	service = MatchQueryBuilder.class
)
public class MatchQueryBuilderImpl implements MatchQueryBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MatchQuery buildQuery(
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
		
		MatchQuery matchQuery =
			new MatchQuery(fieldName, value);
		
		float boost =
			GetterUtil.getFloat(configurationObject.get("boost"), 1.0f);
		matchQuery.setBoost(boost);

		String operator =
						GetterUtil.getString(configurationObject.get("operator"), "and");

		if ("and".equals(operator)) {
			matchQuery.setOperator(Operator.AND);
		}

		if (configurationObject.get("fuzziness") != null && configurationObject.get("fuzziness") != "") {
			float fuzziness = GetterUtil.getFloat(configurationObject.get("fuzziness"), 0.0f);
			matchQuery.setFuzziness(fuzziness);
		}
		
		return matchQuery;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public BooleanQuery buildLocalizedQuery(
		JSONObject configurationObject, QueryParams queryParams)
		throws Exception {
		
		BooleanQuery booleanQuery = new BooleanQueryImpl();

		String fieldName = configurationObject.getString("fieldName");

		String queryType = configurationObject.getString("queryType");

		if (fieldName == null || queryType == null) {
			return null;
		}
		
		// If there's a predefined value in the configuration, use that

		String value = configurationObject.getString("value");

		if (Validator.isNull(value)) {
			value = queryParams.getKeywords();
		}

		MatchQuery matchQuery =
			new MatchQuery(fieldName, value);

		// Boost
		
		float boost = GetterUtil.getFloat(configurationObject.get("boost"), 1.0f);

		matchQuery.setBoost(boost);

		// Operator
		
		String operator = GetterUtil.getString(configurationObject.get("operator"), "and");

		if (operator.equals("or")) {
			matchQuery.setOperator(Operator.OR);
		}
		else {
			matchQuery.setOperator(Operator.AND);
		}
		
		// Fuzziness
		
		Float fuzziness = null;
		
		if (configurationObject.get("fuzziness") != null && configurationObject.get("fuzziness") != "") {
			fuzziness = GetterUtil.getFloat(configurationObject.get("fuzziness"), 0.0f);
			matchQuery.setFuzziness(fuzziness);
		}
		
		// Localized query
		
		String localizedFieldName =
				fieldName + "_" + queryParams.getLocale().toString();

		float localizedBoost = GetterUtil.getFloat(
				configurationObject.getString("boostForLocalizedVersion"), 1f);

		MatchQuery localizedQuery =
				new MatchQuery(localizedFieldName, value);

		localizedQuery.setBoost(localizedBoost);

		if ("and".equals(operator)) {
			matchQuery.setOperator(Operator.AND);
		}
		
		if (fuzziness != null) {
			localizedQuery.setFuzziness(fuzziness);
		}

		// Add subqueries to the query
		
		booleanQuery.add(matchQuery, BooleanClauseOccur.SHOULD);
		booleanQuery.add(localizedQuery, BooleanClauseOccur.SHOULD);

		return booleanQuery;
	}
}
