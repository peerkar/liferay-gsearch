
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
import fi.soveltia.liferay.gsearch.mini.web.suggestions.QuerySuggestionGroup;
import fi.soveltia.liferay.gsearch.mini.web.suggestions.QuerySuggestionGroupData;
import fi.soveltia.liferay.gsearch.mini.web.suggestions.QuerySuggestionsResponse;
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

		QuerySuggestionsResponse querySuggestions = mapGSearchResultsToSuggestions(resourceRequest, items);

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, new Gson().toJson(querySuggestions));

	}

	private QuerySuggestionsResponse mapGSearchResultsToSuggestions(ResourceRequest resourceRequest, JSONArray items) {
		List<QuerySuggestion> querySuggestions = new ArrayList<>();
		Map<String, QuerySuggestionGroupData> groups = new HashMap<>();

		for (int i = 0; i < items.length(); i++) {
			JSONObject searchResult = items.getJSONObject(i);
			String originalTypeKey = searchResult.getString("typeKey");
			String typeKey = getSuggestionTypeKey(originalTypeKey);

			QuerySuggestionGroup group = QuerySuggestionGroup.getGroupForTypeKey(typeKey);
			if (!groups.containsKey(typeKey)) {
				groups.put(typeKey, QuerySuggestionGroupData.newBuilder().group(group.getAsMap()).build());
			} else {
				groups.get(typeKey).addOneCount();
			}
			// prevent returning excess results
			if (groups.get(typeKey).getCount() > (group.getMaxSuggestions())) {
				continue;
			}
			String icon = getIcon(originalTypeKey);
			String localizedType = getLocalization("suggestion-result-group-" + typeKey, resourceRequest.getLocale());
			String url = searchResult.getString("link");
			String description = searchResult.getString("breadcrumbs");
			String date = originalTypeKey.equals("news") ? searchResult.getString("date") : "";
			String title = searchResult.getString("title");
			querySuggestions.add(
				QuerySuggestion.newBuilder()
					.value(title)
					.data(QuerySuggestionData.newBuilder()
						.type(localizedType)
						.typeKey(typeKey)
						.icon(icon)
						.url(url)
						.description(description)
						.date(date)
						.build())
					.build()
			);
		}
		return QuerySuggestionsResponse.newBuilder().groups(groups).suggestions(querySuggestions).build();
	}

	private String getIcon(String typeKey) {
		// TODO set icon classes as specified in FXP-385
		switch (typeKey) {
			case "news":
				return "news";
			case "content":
				return "content";
			case "file":
				return "file-text";
			case "person":
				return "person";
			default:
				return "";
		}
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
