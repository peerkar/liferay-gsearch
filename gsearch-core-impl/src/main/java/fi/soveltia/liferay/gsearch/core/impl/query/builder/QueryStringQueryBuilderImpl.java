
package fi.soveltia.liferay.gsearch.core.impl.query.builder;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.GetterUtil;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.query.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.query.builder.QueryStringQueryBuilder;
import fi.soveltia.liferay.gsearch.query.Operator;
import fi.soveltia.liferay.gsearch.query.QueryStringQuery;

/**
 * QueryStringQuery builder service implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = QueryStringQueryBuilder.class
)
public class QueryStringQueryBuilderImpl implements QueryStringQueryBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QueryStringQuery buildQuery(
		JSONObject configurationObject, QueryParams queryParams)
		throws Exception {

		String operator =
			GetterUtil.getString(configurationObject.get("operator"), "and");

		float queryBoost =
			GetterUtil.getFloat(configurationObject.get("boost"), 1.0f);

		QueryStringQuery queryStringQuery = new QueryStringQuery(queryParams.getKeywords());

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

		// Set operator
		
		if (operator.equals("or")) {
			queryStringQuery.setDefaultOperator(Operator.OR);
		}
		else {
			queryStringQuery.setDefaultOperator(Operator.AND);
		}
		
		// Query boost
		
		queryStringQuery.setBoost(queryBoost);

		// Analyzer
		
		String analyzer = configurationObject.getString("analyzer");
		
		if (analyzer != null && analyzer != "") {
			queryStringQuery.setAnalyzer(analyzer);
		}

		return queryStringQuery;
	}
	
}
