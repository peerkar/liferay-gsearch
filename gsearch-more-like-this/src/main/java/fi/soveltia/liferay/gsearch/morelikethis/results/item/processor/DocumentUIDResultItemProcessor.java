
package fi.soveltia.liferay.gsearch.morelikethis.results.item.processor;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.util.GetterUtil;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.results.item.ResultItemBuilder;
import fi.soveltia.liferay.gsearch.core.api.results.item.processor.ResultItemProcessor;
import fi.soveltia.liferay.gsearch.morelikethis.portlet.GSearchWebKeys;

/**
 * Adds index document uid to results for use in More Like This query.
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
		PortletRequest portletRequest, PortletResponse portletResponse, QueryContext queryContext,
		Document document, ResultItemBuilder resultItemBuilder,
		JSONObject resultItem)
		throws Exception {

		boolean includeDocUID = GetterUtil.getBoolean(
			queryContext.getParameter(GSearchWebKeys.INCLUDE_DOC_UID), false);

		if (includeDocUID) {
			resultItem.put("uid", document.get("uid"));
		}
	}
}
