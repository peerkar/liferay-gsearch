package fi.soveltia.liferay.gsearch.opennlp.query.context.contributor;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.context.contributor.QueryContextContributor;
import fi.soveltia.liferay.gsearch.opennlp.configuration.ModuleConfiguration;
import fi.soveltia.liferay.gsearch.opennlp.constants.OpenNlpConfigurationVariables;
import fi.soveltia.liferay.gsearch.opennlp.constants.OpenNlpParameterNames;
import fi.soveltia.liferay.gsearch.opennlp.service.api.OpenNlpService;

/**
 * Adds weather data to querycontext.
 *
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.opennlp.configuration.ModuleConfiguration",
	immediate = true, 
	service = QueryContextContributor.class
)
public class OpenNlpQueryContextContributor implements QueryContextContributor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void contribute(QueryContext queryContext) throws Exception {
		
		//  Keywords may not be in query context here. Taking them directly from the request.
		
		String keywords = queryContext.getKeywords();
		
		if (Validator.isBlank(keywords)) {
		
			HttpServletRequest httpServletRequest =
					(HttpServletRequest)queryContext.getParameter(
						ParameterNames.HTTP_SERVLET_REQUEST);
	
			PortletRequest portletRequest =  (PortletRequest)httpServletRequest.getAttribute(
					"javax.portlet.request");
	
			if (portletRequest == null) {
				return;
				
			} else {
				keywords = ParamUtil.getString(portletRequest, ParameterNames.KEYWORDS);
			}
		}
		
		if (!_moduleConfiguration.isEnabled() || Validator.isBlank(keywords)) {
			
			if (_log.isDebugEnabled()) {
				_log.debug("Module not enabled or keywords null. Not proceeding.");
			}
			
			return;
		}
				
		JSONObject metadata = _openNlpService.extractData(keywords, true);
		
		if (metadata == null) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Metadata: "  + metadata.toString());
		}

		_contribute(queryContext, metadata);
	}
	
	@Override
	public String getName() {
		return _NAME;
	}	

	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) {

		_moduleConfiguration = ConfigurableUtil.createConfigurable(
			ModuleConfiguration.class, properties);
	}
	
	/**
	 * Adds configuration variables and parameters to query context.
	 * 
	 * @param queryContext
	 * @param metadata
	 */
	private void _contribute(
		QueryContext queryContext, JSONObject metadata) {

		// Configuration variables
		
		JSONArray locations = _getMetadata(metadata, "locations");
		if (locations != null) {
			queryContext.addConfigurationVariable(
					OpenNlpConfigurationVariables.LOCATIONS, 
					jsonArrayToString(locations));		
		}
		
		JSONArray persons = _getMetadata(metadata, "persons");
		if (persons != null) {
			queryContext.addConfigurationVariable(
					OpenNlpConfigurationVariables.PERSONS, 
					jsonArrayToString(persons));		
		}
		
		JSONArray dates = _getMetadata(metadata, "dates");
		if (dates != null) {
			queryContext.addConfigurationVariable(
					OpenNlpConfigurationVariables.DATES, 
					jsonArrayToString(dates));		
		}
		
		// Context parameters
		
		queryContext.setParameter(OpenNlpParameterNames.OPEN_NLP_DATA, metadata);
		
	}
	
	/**
	 * Gets the entities object.
	 * 
	 * @param metadata
	 * @return
	 * @throws Exception
	 */
	private JSONObject _getEntitiesObject(JSONObject metadata) 
			throws Exception {
		
		return metadata.getJSONArray("docs").
				getJSONObject(0).getJSONObject("doc").getJSONObject("_source").
				getJSONObject("entities");
		
	}

	/**
	 * Gets the named metadata.
	 * 
	 * @param metadata
	 * @param key
	 * @return
	 */
	private JSONArray _getMetadata(JSONObject metadata, String key) {

		try {
			JSONObject entities = _getEntitiesObject(metadata);
		
			if (entities != null) {
				return  entities.getJSONArray(key);
			}
		} catch (Exception e) {
			_log.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	private String jsonArrayToString(JSONArray array) {
		
		StringBundler sb = new StringBundler();
		
		for (int i = 0; i < array.length(); i++) {
			
			if (i > 0) {
				sb.append(" ");
			}
			
			sb.append(array.getString(i).toLowerCase());
		}
		
		return sb.toString();
	}
		
	private static final Logger _log = LoggerFactory.getLogger(
			OpenNlpQueryContextContributor.class);

	private static final String _NAME = "open_nlp";

	private volatile ModuleConfiguration _moduleConfiguration;

	@Reference
	private OpenNlpService _openNlpService;

}