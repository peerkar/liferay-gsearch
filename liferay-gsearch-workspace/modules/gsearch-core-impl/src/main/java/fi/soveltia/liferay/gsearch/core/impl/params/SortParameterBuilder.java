
package fi.soveltia.liferay.gsearch.core.impl.params;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.sort.Sort;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.sort.SortOrder;

import java.util.Map;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.configuration.CoreConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ConfigurationNames;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.constants.SortConfigurationKeys;
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
	public void addParameter(QueryContext queryContext) throws Exception {
		PortletRequest portletRequest =
			GSearchUtil.getPortletRequestFromContext(queryContext);

		String sortField = ParamUtil.getString(
			portletRequest, ParameterNames.SORT_FIELD);

		String sortDirection = ParamUtil.getString(
			portletRequest, ParameterNames.SORT_DIRECTION);

		addSortParameter(queryContext, sortField, sortDirection);
	}

	@Override
	public void addParameterHeadless(
			QueryContext queryContext, Map<String, Object> parameters)
		throws Exception {

		String sortField = GetterUtil.getString(
			parameters.get(ParameterNames.SORT_FIELD));

		String sortDirection = GetterUtil.getString(
			parameters.get(ParameterNames.SORT_DIRECTION));

		addSortParameter(queryContext, sortField, sortDirection);
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

	protected void addSortParameter(
			QueryContext queryContext, String sortField, String sortDirection)
		throws Exception {

		Sort sort1 = null;
		Sort sort2 = null;

		SortOrder sortOrder;

		if ("desc".equals(sortDirection)) {
			sortOrder = SortOrder.DESC;
		}
		else {
			sortOrder = SortOrder.ASC;
		}

		String defaultFieldName = null;

		String fieldName = null;
		
		JSONArray configuration = (JSONArray)queryContext.getConfiguration(
			ConfigurationNames.SORT);
		

		for (int i = 0; i < configuration.length(); i++) {
			
			JSONObject item  = _coreConfigurationHelper.parseConfigurationVariables(
					queryContext, configuration.getJSONObject(i));
			
			if ((sortField != null) &&
				item.getString(SortConfigurationKeys.PARAM_NAME).equals(sortField)) {

				fieldName = item.getString(SortConfigurationKeys.FIELD_NAME);

				//fieldType = Integer.valueOf(item.getString(SortConfigurationKeys.FIELD_TYPE));

				break;
			}
			else if (item.getBoolean(SortConfigurationKeys.DEFAULT)) {
				defaultFieldName = item.getString(SortConfigurationKeys.FIELD_NAME);
			}
		}
		
		if (fieldName == null) {
			fieldName = defaultFieldName;
		}

		if ("_score".equals(fieldName)) {
			
			sort1 = sorts.score();
			sort1.setSortOrder(sortOrder);
		} else {
			sort1 = sorts.field(fieldName, sortOrder);
		}
		
		// If primary sort is score, use modified as secondary
		// Use score as secondary for other primary sorts
		
		if (Validator.isNull(fieldName) || "_score".equals(fieldName)) {
			sort2 = sorts.field(Field.MODIFIED_DATE, sortOrder);
		}
		else {
			sort2 = sorts.score();
			sort2.setSortOrder(sortOrder);
		}

		queryContext.setSorts(new Sort[] {sort1, sort2});
	}

	@Reference
	private CoreConfigurationHelper _coreConfigurationHelper;
	
	@Reference
	Sorts sorts;
}