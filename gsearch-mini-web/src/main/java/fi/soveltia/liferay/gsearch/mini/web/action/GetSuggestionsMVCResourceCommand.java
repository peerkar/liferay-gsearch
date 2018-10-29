
package fi.soveltia.liferay.gsearch.mini.web.action;

import com.google.gson.Gson;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
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
import java.util.Map;

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




		// Write response to the output stream.
		List<QuerySuggestion> results = new ArrayList<>();


		JSONArray items = responseObject.getJSONArray("items");

		for (int i = 0; i < items.length(); i++) {
			JSONObject searchResult = items.getJSONObject(i);
			results.add(
				QuerySuggestion.newBuilder()
					.value(searchResult.getString("title"))
					.data(QuerySuggestionData.newBuilder()
						.type(searchResult.getString("type"))
						.build())
					.build()
			);
		}

//		todo
//			lisää group by js puolelle devbridgeautocompleteen
//			autocomplete pois päältä varsinaisessa hakuportletissa (päällä vain miniportletissa)

		Map<String, Object> j = new HashMap<>();
		Gson gson = new Gson();
		j.put("suggestions", results);
		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, gson.toJson(j));

	}

	@Reference(unbind = "-")
	protected void setGSearch(GSearch gSearch) {

		_gSearch = gSearch;
	}

	@Reference
	protected GSearch _gSearch;

	private static final Log _log =
		LogFactoryUtil.getLog(GetSuggestionsMVCResourceCommand.class);
}
