
package fi.soveltia.liferay.gsearch.core.impl.facet.translator;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.facet.translator.FacetTranslator;
import fi.soveltia.liferay.gsearch.core.api.facet.translator.FacetTranslatorFactory;

/**
 * Facet translator factory implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = FacetTranslatorFactory.class
)
public class FacetTranslatorFactoryImpl implements FacetTranslatorFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FacetTranslator getTranslator(String facetName) {

		if (_facetTranslators == null) {

			_log.error("No facet translators found.");

			return null;
		}

		for (FacetTranslator facetTranslator : _facetTranslators) {
			if (facetTranslator.canTranslate(facetName)) {
				return facetTranslator;
			}
		}

		return null;
	}

	/**
	 * Add facet translator.
	 * 
	 * @param facetTranslator
	 */
	protected void addFacetTranslator(FacetTranslator facetTranslator) {

		if (_facetTranslators == null) {
			_facetTranslators = new ArrayList<FacetTranslator>();
		}
		_facetTranslators.add(facetTranslator);
	}

	/**
	 * Remove facet translator.
	 * 
	 * @param facetTranslator
	 */
	protected void removeFacetTranslator(FacetTranslator facetTranslator) {

		_facetTranslators.remove(facetTranslator);
	}

	private static final Logger _log =
		LoggerFactory.getLogger(FacetTranslatorFactoryImpl.class);

	@Reference(
		bind = "addFacetTranslator", 
		cardinality = ReferenceCardinality.MULTIPLE, 
		policy = ReferencePolicy.DYNAMIC, 
		service = FacetTranslator.class, 
		unbind = "removeFacetTranslator"
	)
	private volatile List<FacetTranslator> _facetTranslators;
}
