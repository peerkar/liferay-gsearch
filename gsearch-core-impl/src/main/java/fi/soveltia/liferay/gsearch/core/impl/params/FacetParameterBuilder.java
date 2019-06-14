
package fi.soveltia.liferay.gsearch.core.impl.params;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.ParamUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.constants.ConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.exception.ParameterValidationException;
import fi.soveltia.liferay.gsearch.core.api.facet.FacetProcessor;
import fi.soveltia.liferay.gsearch.core.api.facet.FacetProcessorFactory;
import fi.soveltia.liferay.gsearch.core.api.params.FacetParameter;
import fi.soveltia.liferay.gsearch.core.api.params.ParameterBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.impl.util.GSearchUtil;

/**
 * Parses facet parameters.
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
		QueryContext queryContext, Map<String, Object> parameters)
		throws Exception {

		addFacetParameters(queryContext, null, parameters);

	}

	@Override
	public void addParameter(QueryContext queryContext)
		throws Exception {

		PortletRequest portletRequest =
			GSearchUtil.getPortletRequestFromContext(queryContext);

		addFacetParameters(queryContext, portletRequest, null);

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

	protected void addFacetParameters(
		QueryContext queryContext, PortletRequest portletRequest, Map<String, Object> parameters) throws Exception {

		List<FacetParameter> facetParams = new ArrayList<FacetParameter>();

		String[] configuration =
			queryContext.getConfiguration(ConfigurationKeys.FACET);

		// Loop through configured facets

		String[] fieldValues = null;

		for (int i = 0; i < configuration.length; i++) {

			JSONObject facetConfiguration =
				JSONFactoryUtil.createJSONObject(configuration[i]);

			String fieldParam = facetConfiguration.getString("param_name");

			String processorName =
				facetConfiguration.getString("processor_name");

			fieldValues = null;
			
			if (parameters != null) {
				fieldValues = (String[]) parameters.get(fieldParam);
			} else {
				fieldValues =
					ParamUtil.getStringValues(portletRequest, fieldParam);
			}

			if (fieldValues != null && fieldValues.length > 0) {

				FacetProcessor facetProcessor =
					_facetProcessorFactory.getProcessor(processorName);

				if (facetProcessor == null) {
					facetProcessor =
						_facetProcessorFactory.getProcessor("default");
				}

				facetProcessor.processFacetParameters(
					facetParams, fieldValues, facetConfiguration);
			}
		}

		queryContext.setFacetParameters(facetParams);
	}

	@Reference
	private FacetProcessorFactory _facetProcessorFactory;

}
