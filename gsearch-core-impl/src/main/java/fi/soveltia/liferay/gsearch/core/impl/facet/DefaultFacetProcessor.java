
package fi.soveltia.liferay.gsearch.core.impl.facet;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.facet.FacetProcessor;

/**
 * Default facet processor.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true,
	service = FacetProcessor.class
)
public class DefaultFacetProcessor extends BaseFacetProcessor
	implements FacetProcessor {

	@Override
	public String getName() {

		return NAME;
	}

	private static final String NAME = "default";

}
