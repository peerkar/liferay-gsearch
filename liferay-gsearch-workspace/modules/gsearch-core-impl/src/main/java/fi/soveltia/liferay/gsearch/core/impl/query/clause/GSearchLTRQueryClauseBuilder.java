package fi.soveltia.liferay.gsearch.core.impl.query.clause;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.query.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationValues;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.query.GSearchLTRQuery;

/**
 * LTR query builder.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ClauseBuilder.class
)
public class GSearchLTRQueryClauseBuilder implements ClauseBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query buildClause(
			QueryContext queryContext, JSONObject configuration)
		throws Exception {

		String keywords = (String)configuration.get(ClauseConfigurationKeys.QUERY);
		String model = configuration.getString("model");
		
		if (Validator.isBlank(keywords)) {
			keywords = queryContext.getKeywords();
		}

		if (Validator.isBlank(keywords) || Validator.isBlank(model)) {
			return null;
		}
		
		// Params
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		JSONArray paramConfiguration = configuration.getJSONArray("params");
		
		if (paramConfiguration != null && paramConfiguration.length() > 0) {

			for (int i = 0; i < paramConfiguration.length(); i++) {
				
				JSONObject param = paramConfiguration.getJSONObject(i);
			
				String key = param.getString("key");
				Object value = param.getString("value");
				
				params.put(key, value);
			}
		}

		// Active features
		
		List<String> activeFeatures = new ArrayList<String>();

		JSONArray featureConfiguration = configuration.getJSONArray("active_features");
		
		if (featureConfiguration != null && featureConfiguration.length() > 0) {

			for (int i = 0; i < featureConfiguration.length(); i++) {
				
				String feature = featureConfiguration.getString(i);
				
				activeFeatures.add(feature);
			}
		}

		GSearchLTRQuery ltrQuery = new GSearchLTRQuery(
				keywords, params, model, activeFeatures);

		return ltrQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canBuild(String querytype) {
		return querytype.equals(_QUERY_TYPE);
	}

	public static final String _QUERY_TYPE =
			ClauseConfigurationValues.QUERY_TYPE_LTR;
}