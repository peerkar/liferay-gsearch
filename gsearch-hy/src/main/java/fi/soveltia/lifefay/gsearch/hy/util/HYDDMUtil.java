
package fi.soveltia.lifefay.gsearch.hy.util;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.constants.ConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

public class HYDDMUtil {

	/**
	 * Get HY content DDM structure keys.
	 * 
	 * @param queryContext
	 * @return
	 */
	public static List<String> getHYContentStructureKeys(
		QueryContext queryContext) {

		List<String> keys = new ArrayList<String>();

		JSONObject processorParams;
		try {
			processorParams =
				getHYCompositeFacetConfiguration(queryContext).getJSONObject(
					"processor_params");

			keys = getDDMStructureKeys(
				"contents_ddm_structure_keys", processorParams);
		}
		catch (JSONException e) {
			_log.error("Couldn't resolve HY composite facet configuration.", e);
		}

		return keys;
	}

	/**
	 * Get HY news DDM structure keys.
	 * 
	 * @param queryContext
	 * @return
	 */
	public static List<String> getHYNewsDDMStructureKeys(
		QueryContext queryContext) {

		List<String> keys = new ArrayList<String>();

		try {
			
			JSONObject configuration = 	getHYCompositeFacetConfiguration(queryContext);
			
			if (configuration != null) {
				
				JSONObject processorParams = configuration.getJSONObject(
						"processor_params");				
				keys = getDDMStructureKeys(HY_NEWS__KEY, processorParams);
			}

		}
		catch (JSONException e) {
			_log.error("Couldn't resolve HY composite facet configuration.", e);
		}

		return keys;
	}

	/**
	 * Get HY news DDM structure keys.
	 * 
	 * @return
	 */
	public static List<String> getHYNewsDDMStructureKeys(
		String[] facetConfiguration) {

		try {
			for (int i = 0; i < facetConfiguration.length; i++) {

				JSONObject configurationItem =
					JSONFactoryUtil.createJSONObject(facetConfiguration[i]);

				String processor =
					configurationItem.getString("processor_name");

				if ("hy_composite_facet".equals(processor)) {

					return getDDMStructureKeys(
						HY_NEWS__KEY,
						configurationItem.getJSONObject("processor_params"));
				}
			}
		}
		catch (JSONException e) {
			_log.error("Couldn't resolve HY composite facet configuration.", e);
		}

		return new ArrayList<String>();

	}

	public static List<String> getDDMStructureKeys(
		String key, JSONObject processorParameters) {

		JSONArray keys = processorParameters.getJSONArray(key);

		List<String> values = new ArrayList<String>();

		for (int i = 0; i < keys.length(); i++) {

			values.add(keys.getString(i));
		}

		return values;
	}

	/**
	 * Get HY composite facet configuration.
	 * 
	 * @param queryContext
	 * @return
	 * @throws JSONException
	 */
	public static JSONObject getHYCompositeFacetConfiguration(
		QueryContext queryContext)
		throws JSONException {

		String[] facetConfiguration =
			queryContext.getConfiguration(ConfigurationKeys.FACET);

		if (facetConfiguration == null) {
			return null;
		}
		
		for (int i = 0; i < facetConfiguration.length; i++) {

			JSONObject configurationItem =
				JSONFactoryUtil.createJSONObject(facetConfiguration[i]);

			String processor = configurationItem.getString("processor_name");

			if ("hy_composite_facet".equals(processor)) {
				return configurationItem;
			}
		}

		return null;
	}

	private static final Logger _log = LoggerFactory.getLogger(HYDDMUtil.class);

	private static final String HY_NEWS__KEY = "news_ddm_structure_keys";

}
