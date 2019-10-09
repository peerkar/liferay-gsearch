
package fi.soveltia.liferay.gsearch.core.impl.params;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.constants.ConfigurationNames;
import fi.soveltia.liferay.gsearch.core.api.constants.FilterConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.FilterConfigurationValues;
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
	property = "param.name=filters",
	service = ParameterBuilder.class
)
public class FilterParameterBuilder implements ParameterBuilder {

	@Override
	public void addParameter(QueryContext queryContext) throws Exception {
		_addFilters(queryContext);
	}

	@Override
	public void addParameterHeadless(
			QueryContext queryContext, Map<String, Object> parameters)
		throws Exception {

		_addFilters(queryContext);
	}

	@Override
	public boolean validate(QueryContext queryContext)
		throws ParameterValidationException {

		return true;
	}

	@Override
	public boolean validateHeadless(
			QueryContext queryContext, Map<String, Object> parameters)
		throws ParameterValidationException {

		return true;
	}

	/**
	 * Add filters from the configuration.
	 *
	 * @param queryContext
	 * @throws Exception
	 */
	private void _addFilters(QueryContext queryContext) throws Exception {
		
		JSONArray configuration = (JSONArray)queryContext.getConfiguration(
			ConfigurationNames.FILTER);

		List<FilterParameter> filterParameters = new ArrayList<>();

		for (int i = 0; i < configuration.length(); i++) {

			JSONObject item = configuration.getJSONObject(i);

			boolean enabled = item.getBoolean(FilterConfigurationKeys.ENABLED, true);
			
			// Avoid breaking on an empty config item.

			if (!enabled || item.length() == 0) {
				continue;
			}

			String filterOccur = item.getString(FilterConfigurationKeys.OCCUR, 
					FilterConfigurationValues.OCCUR_MUST);

			JSONObject filterConfiguration = item.getJSONObject(
					FilterConfigurationKeys.CONFIGURATION);

			String fieldName = filterConfiguration.getString(
					FilterConfigurationKeys.FIELD_NAME);
			String valueOccur = filterConfiguration.getString(
					FilterConfigurationKeys.OCCUR,
					FilterConfigurationValues.OCCUR_MUST);
			JSONArray valueArray = filterConfiguration.getJSONArray(
					FilterConfigurationKeys.VALUES);

			List<String> values = new ArrayList<>();

			for (int j = 0; j < valueArray.length(); j++) {
				values.add(valueArray.getString(j));
			}

			// Create a new filter parameter from a configuration object.
			
			FilterParameter filter = new FilterParameter(fieldName);

			filter.setAttribute("filterOccur", filterOccur);
			filter.setAttribute("valueOccur", valueOccur);
			filter.setAttribute("values", values);

			filterParameters.add(filter);
		}

		if (!filterParameters.isEmpty()) {
			FilterParameter filter = new FilterParameter("filterConfiguration");

			filter.setAttribute("filters", filterParameters);

			queryContext.addFilterParameter("filterConfiguration", filter);
		}
	}

}