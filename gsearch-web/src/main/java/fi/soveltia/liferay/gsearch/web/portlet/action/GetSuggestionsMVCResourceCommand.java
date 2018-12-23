
package fi.soveltia.liferay.gsearch.web.portlet.action;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.suggest.GSearchKeywordSuggester;
import fi.soveltia.liferay.gsearch.web.constants.GSearchPortletKeys;
import fi.soveltia.liferay.gsearch.web.constants.GSearchResourceKeys;

/**
 * Resource command for keyword suggestions (autocomplete).
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	property = {
		"javax.portlet.name=" + GSearchPortletKeys.GSEARCH_PORTLET,
		"mvc.command.name=" + GSearchResourceKeys.GET_SUGGESTIONS
	}, 
	service = MVCResourceCommand.class
)
public class GetSuggestionsMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("GetSuggestionsMVCResourceCommand.doServeResource()");
		}

		JSONArray response = null;

		try {
			response = _gSearchSuggester.getSuggestions(resourceRequest);
		}
		catch (Exception e) {

			_log.error(e.getMessage(), e);

			return;
		}

		// Write response to the output stream.

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, response);
	}

	private static final Logger _log =
					LoggerFactory.getLogger(GetSuggestionsMVCResourceCommand.class);

	@Reference
	protected GSearchKeywordSuggester _gSearchSuggester;

}
