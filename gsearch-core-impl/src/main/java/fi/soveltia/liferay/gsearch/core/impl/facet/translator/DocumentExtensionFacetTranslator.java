
package fi.soveltia.liferay.gsearch.core.impl.facet.translator;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.facet.translator.FacetTranslator;
import fi.soveltia.liferay.gsearch.core.api.query.QueryParams;

/**
 * Facet translator for document extension.
 *
 * {@see FacetTranslator}
 *  
 * @author Petteri Karttunen
 */
@Component(
	immediate = true
)
public class DocumentExtensionFacetTranslator implements FacetTranslator {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFacetName(String facetName) {

		_facetName = facetName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONArray translateValues(
		QueryParams queryParams, FacetCollector facetCollector,
		JSONObject configuration)
		throws Exception {

		Map<String, Integer> termMap = new HashMap<String, Integer>();

		JSONArray aggregations = configuration.getJSONArray("aggregations");

		List<TermCollector> termCollectors = facetCollector.getTermCollectors();

		// First aggregate frequency counts

		boolean mappingFound = false;

		for (TermCollector tc : termCollectors) {

			mappingFound = false;

			for (int i = 0; i < aggregations.length(); i++) {

				JSONObject aggregation = aggregations.getJSONObject(i);

				String key = aggregation.getString("key");
				String[] values = aggregation.getString("values").split(",");

				for (int j = 0; j < values.length; j++) {
					if (values[j].equals(tc.getTerm())) {
						if (termMap.get(key) != null) {
							int newValue = termMap.get(key) + tc.getFrequency();
							termMap.put(key, newValue);
						}
						else {
							termMap.put(key, tc.getFrequency());;
						}
						mappingFound = true;
						continue;
					}
				}
			}

			if (!mappingFound) {
				termMap.put(tc.getTerm(), tc.getFrequency());;
			}
		}

		// Then build JSON array

		JSONArray facetArray = JSONFactoryUtil.createJSONArray();

		for (Entry<String, Integer> entry : termMap.entrySet()) {

			JSONObject item = JSONFactoryUtil.createJSONObject();

			item.put("frequency", entry.getValue());
			item.put("name", entry.getKey());
			item.put("term", entry.getKey());

			facetArray.put(item);
		}

		return facetArray;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] translateParams(String value, JSONObject configuration) {
		
		String[] values = null;
		
		JSONArray aggregations = configuration.getJSONArray("aggregations");

		for (int i = 0; i < aggregations.length(); i++) {
			
			JSONObject aggregation = aggregations.getJSONObject(i);

			if (aggregation.getString("key").equals(value)) {
				values = aggregation.getString("values").split(",");
				break;
			}
		}

		if (values == null) {
			values = new String[]{value};
		}

		return values;
	}
	
	protected String _facetName;
}
