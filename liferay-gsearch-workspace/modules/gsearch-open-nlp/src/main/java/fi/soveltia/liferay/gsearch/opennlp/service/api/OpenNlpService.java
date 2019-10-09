package fi.soveltia.liferay.gsearch.opennlp.service.api;

import com.liferay.portal.kernel.json.JSONObject;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

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
	
}
