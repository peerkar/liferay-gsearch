
package fi.soveltia.liferay.gsearch.web.search.results;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;

import java.util.Map.Entry;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;

/**
 * Search results JSON array builder
 * 
 * @author Petteri Karttunen
 */
public class ResultsBuilder {

	public ResultsBuilder() {
	}

	public ResultsBuilder(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse,
		Hits hits, GSearchDisplayConfiguration gSearchDisplayConfiguration) {
		_resourceRequest = resourceRequest;
		_resourceResponse = resourceResponse;
		_hits = hits;
		_gSearchDisplayConfiguration = gSearchDisplayConfiguration;
	}

	/**
	 * Create array of result items.
	 * 
	 * @return
	 */
	public JSONArray createItemsArray() {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		Document[] docs = _hits.getDocs();

		if (_hits == null || docs.length == 0) {
			return jsonArray;
		}

		for (int i = 0; i < docs.length; i++) {

			Document document = docs[i];

			try {

				if (_log.isDebugEnabled()) {
					_log.debug(
						"##############################################");

					for (Entry<String, Field> e : document.getFields().entrySet()) {
						_log.debug(e.getKey() + ":" + e.getValue().getValue());
					}
				}

				JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

				ResultBuilder resultBuilder =
					ResultBuilderFactory.getResultBuilder(
						_resourceRequest, _resourceResponse, document, _gSearchDisplayConfiguration.assetPublisherPage());

				// Title

				jsonObject.put("title", resultBuilder.getTitle());

				// Date

				jsonObject.put("date", resultBuilder.getDate());

				// Description

				jsonObject.put("description", resultBuilder.getDescription());

				// Type

				jsonObject.put("type", resultBuilder.getType());

				// Link

				jsonObject.put("link", resultBuilder.getLink());

				// Put item to array

				jsonArray.put(jsonObject);

			}
			catch (Exception e) {
				_log.error(e, e);
			}
		}

		return jsonArray;
	}

	private GSearchDisplayConfiguration _gSearchDisplayConfiguration;
	private Hits _hits;
	private ResourceRequest _resourceRequest;
	private ResourceResponse _resourceResponse;

	private static final Log _log = LogFactoryUtil.getLog(ResultsBuilder.class);
}
