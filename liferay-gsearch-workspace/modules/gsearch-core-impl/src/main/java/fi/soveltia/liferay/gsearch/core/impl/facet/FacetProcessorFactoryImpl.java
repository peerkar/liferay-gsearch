
package fi.soveltia.liferay.gsearch.core.impl.facet;

import fi.soveltia.liferay.gsearch.core.api.facet.FacetProcessor;
import fi.soveltia.liferay.gsearch.core.api.facet.FacetProcessorFactory;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Facet processor factory implementation.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = FacetProcessorFactory.class
)
public class FacetProcessorFactoryImpl implements FacetProcessorFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<FacetProcessor> getProcessors(String fieldName) {
		
		if (_facetProcessors == null) {
			_log.error("No facet processors found.");

			return null;
		}
		
		List<FacetProcessor>processors = new ArrayList<FacetProcessor>();

		FacetProcessor defaultProcessor = null;
		
		for (FacetProcessor facetProcessor : _facetProcessors) {
			if (facetProcessor.getName().equals(fieldName)) {
				processors.add(facetProcessor);
			}
			
			if (facetProcessor.getName().equals(
					DEFAULT_FACET_PROCESSOR_NAME)) {
				defaultProcessor = facetProcessor;
			}
		}
		
		if (processors.size() == 0) {
			processors.add(defaultProcessor);
		}

		return processors;
	}

	protected void addFacetProcessor(FacetProcessor facetProcessor) {
		
		if (_facetProcessors == null) {
			_facetProcessors = new ArrayList<>();
		}

		_facetProcessors.add(facetProcessor);
	}

	protected void removeFacetProcessor(FacetProcessor facetProcessor) {
		_facetProcessors.remove(facetProcessor);
	}

	public static final String DEFAULT_FACET_PROCESSOR_NAME = "_default";
	
	private static final Logger _log = LoggerFactory.getLogger(
		FacetProcessorFactoryImpl.class);

	@Reference(
		bind = "addFacetProcessor", 
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC, 
		service = FacetProcessor.class,
		unbind = "removeFacetProcessor"
	)
	private volatile List<FacetProcessor> _facetProcessors;

}