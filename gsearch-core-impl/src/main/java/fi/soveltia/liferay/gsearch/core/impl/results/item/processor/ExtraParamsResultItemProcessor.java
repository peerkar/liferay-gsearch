package fi.soveltia.liferay.gsearch.core.impl.results.item.processor;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.core.api.results.item.processor.ResultItemProcessor;
import fi.soveltia.liferay.gsearch.core.impl.results.layout.ImageListResultLayout;
import fi.soveltia.liferay.gsearch.core.impl.results.layout.ThumbnailListResultLayout;

@Component(
	immediate = true, 
	service = ResultItemProcessor.class
)
public class DefaultLayoutResultItemProcessor implements ResultItemProcessor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {

		return true;
	}
	
	@Override
	public void process(
		PortletRequest portletRequest, QueryParams queryParams, Document document,
		ResultItemBuilder resultItemBuilder, JSONObject resultItem)
		throws Exception {

		// Image src. Some optimization to not include image for all the searches (reduces db queries). 

		if (queryParams.getResultsLayout() != null 
						&& (queryParams.getResultsLayout().equals(ThumbnailListResultLayout.KEY)
						|| queryParams.getResultsLayout().equals(ImageListResultLayout.KEY))
						&& document.get(Field.ENTRY_CLASS_NAME).equals(DLFileEntry.class.getName())) {

			resultItem.put("imageSrc", resultItemBuilder.getImageSrc(portletRequest, document));
		}
	}
}
