
package fi.soveltia.liferay.gsearch.core.impl.params;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.ParamUtil;

import java.util.ArrayList;
import java.util.List;

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
		PortletRequest portletRequest, QueryContext queryContext)
		throws Exception {

		List<FacetParameter> facetParams = new ArrayList<FacetParameter>();

		String[] configuration =
			queryContext.getConfiguration(ConfigurationKeys.FACET);

		// Loop through configured facets
		
		for (int i = 0; i < configuration.length; i++) {

			JSONObject facetConfiguration =
				JSONFactoryUtil.createJSONObject(configuration[i]);

			String fieldParam = facetConfiguration.getString("param_name");

			String processorName = facetConfiguration.getString("processor_name");

			String[] fieldValues = ParamUtil.getStringValues(portletRequest, fieldParam);
			
			if (fieldValues.length > 0) {

				FacetProcessor facetProcessor =
					_facetProcessorFactory.getProcessor(processorName);
				
				if (facetProcessor == null ) {
					facetProcessor = _facetProcessorFactory.getProcessor("default");
				}
				
				facetProcessor.processFacetParameters(
						facetParams, fieldValues, facetConfiguration);
			}
		}
		
		queryContext.setFacetParameters(facetParams);
	}

	@Override
	public boolean validate(PortletRequest portletRequest)
		throws ParameterValidationException {

		return true;
	}

	@Reference
	private FacetProcessorFactory _facetProcessorFactory;
}
