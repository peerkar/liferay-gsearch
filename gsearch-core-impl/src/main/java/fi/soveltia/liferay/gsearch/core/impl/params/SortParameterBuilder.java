
package fi.soveltia.liferay.gsearch.core.impl.params;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.exception.ParameterValidationException;
import fi.soveltia.liferay.gsearch.core.api.params.ParameterBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.impl.util.GSearchUtil;

/**
 * Sort parameter builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ParameterBuilder.class
)
public class SortParameterBuilder implements ParameterBuilder {

	@Override
	public void addParameter(QueryContext queryContext)
		throws Exception {

		PortletRequest portletRequest =
			GSearchUtil.getPortletRequestFromContext(queryContext);

		String sortField =
			ParamUtil.getString(portletRequest, ParameterNames.SORT_FIELD);

		String sortDirection =
			ParamUtil.getString(portletRequest, ParameterNames.SORT_DIRECTION);

		addSortParameter(queryContext, sortField, sortDirection);
	}

	@Override
	public void addParameter(
		QueryContext queryContext, Map<String, Object> parameters)
		throws Exception {

		String sortField =
			GetterUtil.getString(parameters.get(ParameterNames.SORT_FIELD));

		String sortDirection =
			GetterUtil.getString(parameters.get(ParameterNames.SORT_DIRECTION));

		addSortParameter(queryContext, sortField, sortDirection);
	}

	@Override
	public boolean validate(
		QueryContext queryContext)
		throws ParameterValidationException {

		return true;
	}

	@Override
	public boolean validate(
		QueryContext queryContext, Map<String, Object> parameters)
		throws ParameterValidationException {

		return true;
	}

	protected void addSortParameter(
		QueryContext queryContext, String sortField, String sortDirection)
		throws Exception {

		Sort sort1 = null;
		Sort sort2 = null;

		boolean reverse;

		if ("desc".equals(sortDirection)) {
			reverse = true;
		}
		else {
			reverse = false;
		}

		String defaultFieldName = null;
		Integer defaultFieldType = 0;

		String fieldName = null;
		Integer fieldType = null;

		String[] configuration =
			queryContext.getConfiguration(ConfigurationKeys.SORT);

		for (int i = 0; i < configuration.length; i++) {

			JSONObject item =
				JSONFactoryUtil.createJSONObject(configuration[i]);

			if (sortField != null && item.getString("key").equals(sortField)) {

				fieldName = _configurationHelper.parseConfigurationVariables(
					queryContext, item.getString("field_name"));

				fieldType = Integer.valueOf(item.getString("field_type"));

				break;

			}
			else if (item.getBoolean("default")) {

				defaultFieldName =
					_configurationHelper.parseConfigurationVariables(
						queryContext, item.getString("field_name"));

				defaultFieldType =
					Integer.valueOf(item.getString("field_type"));
			}
		}

		if (fieldName == null || fieldType == null) {

			fieldName = defaultFieldName;
			fieldType = defaultFieldType;
		}

		sort1 = new Sort(fieldName, fieldType, reverse);

		// If primary sort is score, use modified as secondary
		// Use score as secondary for other primary sorts

		if (Validator.isNull(fieldName) || "_score".equals(fieldName)) {

			sort2 = new Sort(MODIFIED_SORT_FIELD, Sort.LONG_TYPE, reverse);

		}
		else {

			sort2 = new Sort(null, Sort.SCORE_TYPE, reverse);
		}

		queryContext.setSorts(
			new Sort[] {
				sort1, sort2
			});
	}

	// Modification date field name in the index.

	private static final String MODIFIED_SORT_FIELD = "modified_sortable";

	@Reference
	private ConfigurationHelper _configurationHelper;
}
