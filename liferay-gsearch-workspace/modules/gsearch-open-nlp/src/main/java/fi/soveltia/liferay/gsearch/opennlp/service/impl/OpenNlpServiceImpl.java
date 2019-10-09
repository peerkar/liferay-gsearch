package fi.soveltia.liferay.gsearch.opennlp.service.impl;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.SingleVMPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.opennlp.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.opennlp.service.api.OpenNlpService;

/**
 * Open NLP service implementation.
 * 
 * Note that places and names identification is depending on capitalization.
 *
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.opennlp.configuration.ModuleConfiguration",
	immediate = true, 
	service = OpenNlpService.class
)
public class OpenNlpServiceImpl implements OpenNlpService {
	
	/**
	 * {@inheritDoc}
	 */
	public JSONObject extractData(String keywords, boolean cache) throws Exception {

		keywords = keywords.trim();
		
		// Try to get data from cache first.

		if (cache) {

			JSONObject data = _portalCache.get(keywords);

			if (data != null) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Data for keywords " + keywords + " found from cache.");
				}
	
				return data;
			}
		}

		JSONObject command = createCommand(keywords);
		
		return execute(command);
	}
	
	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}
	
	/**
	 * Creates the request parameter JSON object. 
	 * 
	 * @param queryContext
	 * @return
	 */
	private JSONObject createCommand(String keywords) {
		
		JSONObject ingestionCommand = JSONFactoryUtil.createJSONObject();

		// Pipeline
		
		JSONObject pipeline = JSONFactoryUtil.createJSONObject();
	
		JSONArray  processors = JSONFactoryUtil.createJSONArray();
	
		JSONObject opennlp = JSONFactoryUtil.createJSONObject();
		opennlp.put("field", "gsearch_metadata");
	
		processors.put(opennlp);
		pipeline.put("processors", processors);
	
		// Docs
	
		JSONArray  docs = JSONFactoryUtil.createJSONArray();
	
		JSONObject doc1 = JSONFactoryUtil.createJSONObject();
	
		JSONObject source = JSONFactoryUtil.createJSONObject();
		source.put("gsearch_metadata", keywords);
	
		doc1.put("_source", source);
		docs.put(doc1);
	
		ingestionCommand.put("pipeline", pipeline);
		ingestionCommand.put("docs", docs);

		return ingestionCommand;
	}
	
	/**
	 * Executes the command
	 * 
	 * @param command
	 * @return
	 */
	private JSONObject execute(JSONObject command) {
	
		String engineURL = _moduleConfiguration.engineURL();
		
		if (Validator.isBlank(engineURL)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Engine URL empty. Check configuration.");
			}			
			return null;
		}
				
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
	
		try {
			
		    HttpPost request = new HttpPost(engineURL);

		    StringEntity params = new StringEntity(command.toString());
		    request.addHeader("content-type", "application/json");
		    request.setEntity(params);
		    
		    CloseableHttpResponse response = httpClient.execute(request);
		    HttpEntity entity = response.getEntity();
		    
		    String json = EntityUtils.toString(entity, StandardCharsets.UTF_8);
		    
		    return JSONFactoryUtil.createJSONObject(json);

		} catch (Exception e) {
			
			_log.error("There was an error in getting NLP data.");
			_log.error("Engine URL: " + engineURL);
			_log.error("Command: " + command);
			_log.error(e.getMessage(), e);
			
		} finally {
			
		    try {
				httpClient.close();
			} catch (IOException e) {
				_log.error(e.getMessage(), e);
			}
		}
		
		return null;
	}	
	
	@Reference(unbind = "-")
	@SuppressWarnings("unchecked")
	private void setSingleVMPool(SingleVMPool singleVMPool) {
		_portalCache =
			(PortalCache<String, JSONObject>)singleVMPool.getPortalCache(
				OpenNlpServiceImpl.class.getName());
	}

	private static final Logger _log = LoggerFactory.getLogger(
			OpenNlpServiceImpl.class);

	private volatile ModuleConfiguration _moduleConfiguration;
	private PortalCache<String, JSONObject> _portalCache;	
}
