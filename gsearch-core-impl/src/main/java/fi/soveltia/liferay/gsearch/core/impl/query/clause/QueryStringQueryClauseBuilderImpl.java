
package fi.soveltia.liferay.gsearch.core.impl.query.clause;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseBuilder;
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
public class QueryStringQueryClauseBuilderImpl implements ClauseBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query buildClause(
		JSONObject configurationObject, QueryParams queryParams)
		throws Exception {

		// If there's a predefined value in the configuration, use that

		String value = configurationObject.getString("value");

		if (Validator.isNull(value)) {

			StringBundler sb = new StringBundler();
			
			String prefix = configurationObject.getString("valuePrefix");
			if (Validator.isNotNull(prefix)) {
				sb.append(prefix);
			}

			sb.append(queryParams.getKeywords());

			String suffix = configurationObject.getString("valueSuffix");
			if (Validator.isNotNull(suffix)) {
				sb.append(suffix);
			}			
			value = sb.toString();
		}
				
		QueryStringQuery queryStringQuery = new QueryStringQuery(value);

		JSONArray fields = configurationObject.getJSONArray("fields");
		
		for (int i = 0; i < fields.length(); i++) {

			JSONObject item = fields.getJSONObject(i);

			// Add non translated version
			
			String fieldName = item.getString("fieldName");
			float boost =  GetterUtil.getFloat(item.getString("boost"), 1f);
			
			queryStringQuery.addField(fieldName, boost);
			
			// Add translated version
			
			boolean isLocalized = GetterUtil.getBoolean(item.get("localized"), false);
			
			if (isLocalized) {
				
				String localizedFieldName = fieldName + "_" + queryParams.getLocale().toString();
				float localizedBoost =  GetterUtil.getFloat(item.getString("boostForLocalizedVersion"), 1f);

				queryStringQuery.addField(localizedFieldName, localizedBoost);
			}			
		}

		// Operator
		
		String operator =
						GetterUtil.getString(configurationObject.get("operator"), "and");

		if (operator.equals("or")) {
			queryStringQuery.setDefaultOperator(Operator.OR);
		}
		else {
			queryStringQuery.setDefaultOperator(Operator.AND);
		}

		// Fuzziness

		if (Validator.isNotNull(configurationObject.get("fuzziness"))) {
			float fuzziness =
				GetterUtil.getFloat(configurationObject.get("fuzziness"), 0.0f);
			queryStringQuery.setFuzziness(fuzziness);
		}
		
		// Boost
		
		float boost =
						GetterUtil.getFloat(configurationObject.get("boost"), 1.0f);
		queryStringQuery.setBoost(boost);

		// Analyzer
		
		String analyzer = configurationObject.getString("analyzer");
		 
		if (Validator.isNotNull(analyzer)) {
			queryStringQuery.setAnalyzer(analyzer);
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

}
