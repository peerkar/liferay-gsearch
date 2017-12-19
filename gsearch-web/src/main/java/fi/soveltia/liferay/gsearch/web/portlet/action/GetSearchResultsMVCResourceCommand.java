
package fi.soveltia.liferay.gsearch.web.portlet.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.GSearch;
import fi.soveltia.liferay.gsearch.core.api.query.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.queryparams.QueryParamsBuilder;
import fi.soveltia.liferay.gsearch.web.constants.GSearchResourceKeys;
import fi.soveltia.liferay.gsearch.web.constants.GSearchPortletKeys;

/**
 * Resource command for getting the search results.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	property = {
		"javax.portlet.name=" + GSearchPortletKeys.GSEARCH_PORTLET,
		"mvc.command.name=" + GSearchResourceKeys.GET_SEARCH_RESULTS
	}, 
	service = MVCResourceCommand.class
)
public class GetSearchResultsMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("GetSearchResultsMVCResourceCommand.doServeResource()");
		}

		JSONObject responseObject = null;

		// Build query parameters object.

		QueryParams queryParams = null;

		try {
			queryParams = _queryParamsBuilder.buildQueryParams(
				resourceRequest);
		}
		catch (PortalException e) {

			_log.error(e, e);

			return;
		}

		// Try to get search results.
		
		try {
			responseObject = _gSearch.getSearchResults(
				resourceRequest, resourceResponse, queryParams);
		}
		catch (Exception e) {

			_log.error(e, e);

			return;
		}

		// Write response to output stream.

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, responseObject);
	}

	@Reference(unbind = "-")
	protected void setGSearch(GSearch gSearch) {

		_gSearch = gSearch;
	}

	@Reference(unbind = "-")
	protected void setQueryParamsBuilder(QueryParamsBuilder queryParamsBuilder) {

		_queryParamsBuilder = queryParamsBuilder;
	}

	@Reference
	protected GSearch _gSearch;

	@Reference
	protected QueryParamsBuilder _queryParamsBuilder;

	private static final Log _log =
		LogFactoryUtil.getLog(GetSearchResultsMVCResourceCommand.class);
}
