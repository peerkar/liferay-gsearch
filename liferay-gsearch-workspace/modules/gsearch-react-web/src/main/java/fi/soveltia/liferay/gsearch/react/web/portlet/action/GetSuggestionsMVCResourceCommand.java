
package fi.soveltia.liferay.gsearch.react.web.portlet.action;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.constants.QueryContextContributorNames;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContextBuilder;
import fi.soveltia.liferay.gsearch.core.api.suggest.GSearchKeywordSuggester;
import fi.soveltia.liferay.gsearch.react.web.constants.GSearchReactWebPortletKeys;
import fi.soveltia.liferay.gsearch.react.web.constants.ResourceRequestKeys;

/**
 * Resource command for keyword suggestions (autocomplete).
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	property = {
		"javax.portlet.name=" + GSearchReactWebPortletKeys.GSEARCH_REACT_PORTLET,
		"mvc.command.name=" + ResourceRequestKeys.GET_SUGGESTIONS
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

			ThemeDisplay themeDisplay =
				(ThemeDisplay) resourceRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			HttpServletRequest httpServletRequest =
				_portal.getHttpServletRequest(resourceRequest);

			String keywords =
				ParamUtil.getString(resourceRequest, ParameterNames.KEYWORDS);

			QueryContext queryContext =
				_queryContextBuilder.buildSuggesterQueryContext(
					httpServletRequest, null,
					themeDisplay.getScopeGroupId(), themeDisplay.getLocale(),
					keywords);
			
			// Process query context contributors.
			
			_queryContextBuilder.processQueryContextContributor(
					queryContext, QueryContextContributorNames.CONTEXT);

			response = _gSearchSuggester.getSuggestions(queryContext);
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

	@Reference
	protected Portal _portal;

	@Reference
	protected QueryContextBuilder _queryContextBuilder;
}
