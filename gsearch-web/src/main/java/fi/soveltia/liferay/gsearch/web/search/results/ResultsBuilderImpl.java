
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

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;

/**
 * ResultsBuilder implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(immediate = true)
public class ResultsBuilderImpl implements ResultsBuilder {

	@Override
	public JSONArray getItemsArray(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Hits hits, GSearchDisplayConfiguration gSearchDisplayConfiguration) {

		return createItemsArray(
			portletRequest, portletResponse, hits, gSearchDisplayConfiguration);
	}

	/**
	 * Get results
	 * 
	 * @return JSONArray
	 */
	protected JSONArray createItemsArray(
		PortletRequest portletRequest, PortletResponse portletResponse,
		Hits hits, GSearchDisplayConfiguration gSearchDisplayConfiguration) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		Document[] docs = hits.getDocs();

		if (hits == null || docs.length == 0) {
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
						portletRequest, portletResponse, document,
						gSearchDisplayConfiguration.assetPublisherPage());

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

	private static final Log _log =
		LogFactoryUtil.getLog(ResultsBuilderImpl.class);
}
