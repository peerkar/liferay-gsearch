
package fi.soveltia.liferay.gsearch.morelikethis.results.item.processor;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Document;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.core.api.results.item.processor.ResultItemProcessor;

/**
 * Add geolocation properties required for the maps result layout.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ResultItemProcessor.class
)
public class DocumentUIDResultItemProcessor implements ResultItemProcessor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process(
		PortletRequest portletRequest, QueryParams queryParams,
		Document document, ResultItemBuilder resultItemBuilder, JSONObject resultItem)
		throws Exception {
		
		if (queryParams.getExtraParams() == null ||
			queryParams.getExtraParams().get("includeDocUID") == null) {
			return;
		}
		resultItem.put("uid", document.get("uid"));
	}
}
