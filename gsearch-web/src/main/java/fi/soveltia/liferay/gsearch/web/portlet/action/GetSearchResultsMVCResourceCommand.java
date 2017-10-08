
package fi.soveltia.liferay.gsearch.web.portlet.action;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;

import java.util.Map;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;
import fi.soveltia.liferay.gsearch.web.constants.GSearchResourceKeys;
import fi.soveltia.liferay.gsearch.web.constants.GsearchWebPortletKeys;
import fi.soveltia.liferay.gsearch.web.search.GSearch;
import fi.soveltia.liferay.gsearch.web.search.query.QueryParams;
import fi.soveltia.liferay.gsearch.web.search.query.QueryParamsBuilder;

/**
 * Get Search Results Resource Command
 * 
 * @author Petteri Karttunen
 */
@Component(configurationPid = "fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration", 
	immediate = true, 
	property = {
		"javax.portlet.name=" + GsearchWebPortletKeys.SEARCH_PORTLET,
		"mvc.command.name=" + GSearchResourceKeys.GET_SEARCH_RESULTS
	}, 
	service = MVCResourceCommand.class
)
public class GetSearchResultsMVCResourceCommand extends BaseMVCResourceCommand {

	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) {

		_gSearchDisplayConfiguration = ConfigurableUtil.createConfigurable(
			GSearchDisplayConfiguration.class, properties);
	}

	@Override
	protected void doServeResource(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		if (_log.isDebugEnabled()) {
			_log.debug("GetSearchResultsMVCResourceCommand.doServeResource()");
		}

		JSONObject responseObject = null;

		// Get params

		QueryParams queryParams = null;

		try {
			queryParams = _queryParamsBuilder.buildQueryParams(
				resourceRequest, _gSearchDisplayConfiguration);
		}
		catch (PortalException e) {

			_log.error(e, e);

			return;
		}

		try {
			responseObject = _gsearch.getSearchResults(
				resourceRequest, resourceResponse, queryParams,
				_gSearchDisplayConfiguration);
		}
		catch (Exception e) {

			_log.error(e, e);

			return;

		}

		// Write to output stream

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, responseObject);
	}

	@Reference
	protected GSearch _gsearch;

	@Reference
	protected QueryParamsBuilder _queryParamsBuilder;

	private volatile GSearchDisplayConfiguration _gSearchDisplayConfiguration;

	private static final Log _log =
		LogFactoryUtil.getLog(GetSearchResultsMVCResourceCommand.class);
}
