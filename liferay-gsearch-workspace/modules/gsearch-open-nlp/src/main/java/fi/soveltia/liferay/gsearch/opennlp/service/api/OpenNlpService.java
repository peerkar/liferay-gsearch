package fi.soveltia.liferay.gsearch.opennlp.service.api;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;

/**
 * Liferay GSearch Open NLP service.
 * 
 * @author Petteri Karttunen
 */
public interface OpenNlpService {
	
	/**
	 * Extracts NLP data
	 * 
	 * @param keywords
	 * @param cache Is cache enabled
	 * @return
	 * @throws Exception
	 */
	public JSONObject extractData(String keywords, boolean cache) 
			throws Exception;
	
	/**
	 * Gets a metadata entity.
	 * 
	 * @param metadata
	 * @param key
	 * @return
	 */
	public JSONArray getMetadata(JSONObject metadata, String key);
}
