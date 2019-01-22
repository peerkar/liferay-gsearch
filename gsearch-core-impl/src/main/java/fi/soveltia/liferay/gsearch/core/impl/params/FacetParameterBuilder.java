
package fi.soveltia.liferay.gsearch.core.impl.params;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.constants.ConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.exception.ParameterValidationException;
import fi.soveltia.liferay.gsearch.core.api.facet.FacetTranslator;
import fi.soveltia.liferay.gsearch.core.api.facet.FacetTranslatorFactory;
import fi.soveltia.liferay.gsearch.core.api.params.FacetParameter;
import fi.soveltia.liferay.gsearch.core.api.params.ParameterBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Facet parameter builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ParameterBuilder.class
)
public class FacetParameterBuilder implements ParameterBuilder {

	@Override
	public void addParameter(
		PortletRequest portletRequest, QueryContext queryContext)
		throws Exception {

		List<FacetParameter> facetParams = new ArrayList<FacetParameter>();

		String fieldParam;
		String fieldName;
		String[] fieldValues;

		String translatorName;
		String filterMode;
		boolean allowMultipleValues;
		String multiValueOperator;

		String[] configuration =
			queryContext.getConfiguration(ConfigurationKeys.FACET);

		for (int i = 0; i < configuration.length; i++) {

			JSONObject facetConfiguration =
				JSONFactoryUtil.createJSONObject(configuration[i]);

			fieldParam = facetConfiguration.getString("param_name");
			fieldName = facetConfiguration.getString("field_name");
			filterMode = GetterUtil.getString(
				facetConfiguration.getString("filter_mode"), "pre");
			multiValueOperator = GetterUtil.getString(
				facetConfiguration.getString("multi_value_operator"), "or");
			allowMultipleValues = GetterUtil.getBoolean(
				facetConfiguration.getString("allow_multiple_values"), false);
			translatorName = facetConfiguration.getString("translator_name");

			fieldValues = ParamUtil.getStringValues(portletRequest, fieldParam);

			if (Validator.isNotNull(fieldValues) && fieldValues.length > 0) {

				FacetTranslator translator =
					_facetTranslatorFactory.getTranslator(translatorName);
				
				if (translator != null) {

					List<String> values = new ArrayList<String>();
					
					for (String fieldValue : fieldValues) {
						
						String[] translatedValues = translator.toQuery(
							fieldValue, facetConfiguration);
						
						Collections.addAll(values, translatedValues);
					}

					FacetParameter facetParam = new FacetParameter(
						fieldName, values, allowMultipleValues, 
						multiValueOperator, filterMode);

					facetParams.add(facetParam);

				}
				else {
					FacetParameter facetParam = new FacetParameter(
						fieldName, Arrays.asList(fieldValues), allowMultipleValues,
						multiValueOperator, filterMode);

					facetParams.add(facetParam);
				}
			}
		}
		queryContext.setParameter(ParameterNames.FACETS, facetParams);
	}

	@Override
	public boolean validate(PortletRequest portletRequest)
		throws ParameterValidationException {

		return true;
	}

	@Reference
	private FacetTranslatorFactory _facetTranslatorFactory;
}
