
package fi.soveltia.liferay.gsearch.mini.web.action;

import com.google.gson.Gson;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import fi.soveltia.liferay.gsearch.core.api.GSearch;
import fi.soveltia.liferay.gsearch.mini.web.constants.GSearchMiniPortletKeys;
import fi.soveltia.liferay.gsearch.mini.web.constants.GSearchMiniResourceKeys;
import fi.soveltia.liferay.gsearch.mini.web.suggestions.QuerySuggestion;
import fi.soveltia.liferay.gsearch.mini.web.suggestions.QuerySuggestionData;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Resource command for getting keyword suggestions (autocomplete).
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + GSearchMiniPortletKeys.GSEARCH_MINIPORTLET,
		"mvc.command.name=" + GSearchMiniResourceKeys.GET_SUGGESTIONS
	},
	service = MVCResourceCommand.class
)
public class GetSuggestionsMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONObject responseObject = _gSearch.getSearchResults(resourceRequest, resourceResponse);

		JSONArray items = responseObject.getJSONArray("items");

		List<QuerySuggestion> results =	mapGSearchResultsToSuggestions(resourceRequest, items);

		Map<String, Object> suggestionsJson = new HashMap<>();
		suggestionsJson.put("suggestions", results);
		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, new Gson().toJson(suggestionsJson));

	}

	private List<QuerySuggestion> mapGSearchResultsToSuggestions(ResourceRequest resourceRequest, JSONArray items) {
		List<QuerySuggestion> querySuggestions = new ArrayList<>();
		for (int i = 0; i < items.length(); i++) {
			JSONObject searchResult = items.getJSONObject(i);
			String typeKey = getSuggestionTypeKey(searchResult.getString("typeKey"));
			querySuggestions.add(
				QuerySuggestion.newBuilder()
					.value(searchResult.getString("title"))
					.data(QuerySuggestionData.newBuilder()
						.type(getLocalization("suggestion-result-group-" + typeKey, resourceRequest.getLocale()))
						.typeKey(typeKey)
						.url(searchResult.getString("link"))
						.description(searchResult.getString("breadcrumbs"))
						.build())
					.build()
			);
		}
		return querySuggestions;
	}

	private String getSuggestionTypeKey(String typeKey) {
		switch (typeKey) {
			case "person":
				return "person";
			case "tool":
				return "tool";
			default:
				return "content";
		}
	}

	@Reference(unbind = "-")
	protected void setGSearch(GSearch gSearch) {

		_gSearch = gSearch;
	}

	private String getLocalization(String key, Locale locale) {

		if (_resourceBundle == null) {
			_resourceBundle = ResourceBundleUtil.getBundle(
				"content.Language", locale, GetSuggestionsMVCResourceCommand.class);
		}
		try {
			return _resourceBundle.getString(key);
		}
		catch (Exception e) {
			_log.error(e, e);
		}
		return key;
	}

	private ResourceBundle _resourceBundle;

	@Reference
	protected GSearch _gSearch;

	private static final Log _log =
		LogFactoryUtil.getLog(GetSuggestionsMVCResourceCommand.class);
}
