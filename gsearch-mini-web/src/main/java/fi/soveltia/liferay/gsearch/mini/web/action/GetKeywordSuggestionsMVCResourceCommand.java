
package fi.soveltia.liferay.gsearch.mini.web.action;

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
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContextBuilder;
import fi.soveltia.liferay.gsearch.core.api.suggest.GSearchKeywordSuggester;
import fi.soveltia.liferay.gsearch.mini.web.constants.GSearchMiniPortletKeys;
import fi.soveltia.liferay.gsearch.mini.web.constants.GSearchMiniResourceKeys;

/**
 * Resource command for getting suggestions (autocomplete).
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	property = {
		"javax.portlet.name=" + GSearchMiniPortletKeys.GSEARCH_MINIPORTLET,
		"mvc.command.name=" + GSearchMiniResourceKeys.GET_KEYWORD_SUGGESTIONS
	}, 
	service = MVCResourceCommand.class
)
public class GetKeywordSuggestionsMVCResourceCommand
	extends BaseMVCResourceCommand {

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
					httpServletRequest, null, themeDisplay.getCompanyId(),
					themeDisplay.getScopeGroupId(), themeDisplay.getLocale(),
					keywords);

			response = _gSearchSuggester.getSuggestions(queryContext);
			
		}
		catch (Exception e) {

			_log.error(e.getMessage(), e.getCause());

			return;
		}

		// Write response to the output stream.

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, response);
	}

	@Reference
	private GSearchKeywordSuggester _gSearchSuggester;

	@Reference
	private Portal _portal;

	@Reference
	private QueryContextBuilder _queryContextBuilder;
	
	private static final Logger _log =
		LoggerFactory.getLogger(GetKeywordSuggestionsMVCResourceCommand.class);

}
