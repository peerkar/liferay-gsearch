
package fi.soveltia.liferay.gsearch.core.impl.facet;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.facet.FacetProcessor;
import fi.soveltia.liferay.gsearch.core.api.facet.FacetProcessorFactory;

/**
 * Facet translator factory implementation.
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
	public FacetProcessor getProcessor(String processorName) {

		if (_facetProcessors == null) {

			_log.error("No facet processors found.");

			return null;
		}

		for (FacetProcessor facetProcessor : _facetProcessors) {
			if (facetProcessor.getName().equals(processorName)) {
				return facetProcessor;
			}
		}

		return null;
	}

	protected void addFacetProcessor(FacetProcessor facetProcessor) {

		if (_facetProcessors == null) {
			_facetProcessors = new ArrayList<FacetProcessor>();
		}
		_facetProcessors.add(facetProcessor);
	}

	protected void removeFacetProcessor(FacetProcessor facetProcessor) {

		_facetProcessors.remove(facetProcessor);
	}

	private static final Logger _log =
		LoggerFactory.getLogger(FacetProcessorFactoryImpl.class);

	@Reference(
		bind = "addFacetProcessor", 
		cardinality = ReferenceCardinality.MULTIPLE, 
		policy = ReferencePolicy.DYNAMIC, 
		service = FacetProcessor.class, 
		unbind = "removeFacetProcessor"
	)
	private volatile List<FacetProcessor> _facetProcessors;
}
