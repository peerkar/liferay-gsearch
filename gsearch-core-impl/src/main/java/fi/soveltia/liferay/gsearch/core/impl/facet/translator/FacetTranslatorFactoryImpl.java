package fi.soveltia.liferay.gsearch.core.impl.facet.translator;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.facet.translator.FacetTranslator;
import fi.soveltia.liferay.gsearch.core.api.facet.translator.FacetTranslatorFactory;

@Component(
	immediate = true, 
	service = FacetTranslatorFactory.class
)
public class FacetTranslatorFactoryImpl implements FacetTranslatorFactory {

	@Override
	public FacetTranslator getTranslator(String fieldName) {

		FacetTranslator translator = null;
		
		if ("ddmStructureKey".equals(fieldName)) {
			
			translator = new WebContentStructureFacetTranslator();
	
		} else if ("extension".equals(fieldName)) {
			
			 translator = new DocumentExtensionFacetTranslator();
			 
		} else if ("fileEntryTypeId".equals(fieldName)) {
			
			 translator = new DocumentTypeFacetTranslator();
		}
		
		if (translator != null) {
			translator.setFacetName(fieldName);
		}
		
		return translator;
	}
}
