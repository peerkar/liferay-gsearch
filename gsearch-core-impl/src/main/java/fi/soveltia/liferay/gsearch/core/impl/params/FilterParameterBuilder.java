
package fi.soveltia.liferay.gsearch.core.impl.params;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.constants.ConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.exception.ParameterValidationException;
import fi.soveltia.liferay.gsearch.core.api.params.FilterParameter;
import fi.soveltia.liferay.gsearch.core.api.params.ParameterBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Parses (static) filters from configuration and builds parameters.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	property = {
		"param.name=filters"
	},
	service = ParameterBuilder.class
)
public class FilterParameterBuilder implements ParameterBuilder {

	@Override
	public void addParameter(
		QueryContext queryContext)
		throws Exception {
		
		addFilters(queryContext);
	}
	
	@Override
	public void addParameter(
		QueryContext queryContext, Map<String, Object> parameters)
		throws Exception {
		
		addFilters(queryContext);
	}
		
	/**
	 * Add filters from the configuration.
	 * 
	 * @param queryContext
	 * @throws Exception
	 */
	private void addFilters(
		QueryContext queryContext)
		throws Exception {

		String[] configuration =
			queryContext.getConfiguration(ConfigurationKeys.FILTER);

		List<FilterParameter> filterParameters = new ArrayList<FilterParameter>();
		
		for (int i = 0; i < configuration.length; i++) {

			JSONObject item =
				JSONFactoryUtil.createJSONObject(configuration[i]);

			// Avoid breaking on an empty config item.
			
			if (item.length() == 0) {
				continue;
			}
			
			boolean enabled = item.getBoolean("filter_enabled", true);

			if (!enabled) {
				continue;
			}

			String fieldName = item.getString("field_name");
			String filterOccur = item.getString("filter_occur", "must");
			String valueOperator = item.getString("value_occur", "should");

			JSONArray valueArray = item.getJSONArray("values");

			List<String> values = new ArrayList<String>();

			for (int j = 0; j < valueArray.length(); j++) {
				values.add(valueArray.getString(j));
			}

			FilterParameter filter = new FilterParameter(fieldName);

			filter.setAttribute("filterOccur", filterOccur);
			filter.setAttribute("valueOccur", valueOperator);
			filter.setAttribute("values", values);

			filterParameters.add(filter);
		}
		
		if (filterParameters.size() > 0) {
			
			FilterParameter filter = new FilterParameter("filterConfiguration");
			filter.setAttribute("filters", filterParameters);

			queryContext.addFilterParameter("filterConfiguration", filter);
		}
	}

	@Override
	public boolean validate(QueryContext queryContext)
		throws ParameterValidationException {

		return true;
	}

	@Override
	public boolean validate(
		QueryContext queryContext, Map<String, Object> parameters)
		throws ParameterValidationException {

		return true;
	}
}
