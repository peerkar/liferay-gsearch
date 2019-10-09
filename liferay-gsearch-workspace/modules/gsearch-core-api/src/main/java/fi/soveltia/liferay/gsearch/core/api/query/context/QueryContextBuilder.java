
package fi.soveltia.liferay.gsearch.core.api.query.context;

import com.liferay.portal.kernel.json.JSONArray;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Builds query context object.
 *
 * @author Petteri Karttunen
 */
public interface QueryContextBuilder {

	/**
	 * Creates a QueryContext object.
	 *
	 * @param httpServletRequest
	 * @param locale
	 * @param keywords
	 * @return
	 * @throws Exception
	 */
	public QueryContext buildQueryContext(
			HttpServletRequest httpServletRequest, Locale locale, 
			String keywords)
		throws Exception;

	/**
	 * Creates a QueryContext object.
	 * 
	 * @param httpServletRequest
	 * @param locale
	 * @param filterConfiguration
	 * @param clauseConfiguration
	 * @param facetConfiguration
	 * @param sortConfiguration
	 * @param suggesterConfiguration
	 * @param rescorerConfiguration
	 * @param keywords
	 * @return
	 * @throws Exception
	 */
	public QueryContext buildQueryContext(
			HttpServletRequest httpServletRequest, Locale locale, 
			JSONArray filterConfiguration, JSONArray clauseConfiguration,
			JSONArray facetConfiguration, JSONArray sortConfiguration, 
			JSONArray suggesterConfiguration, JSONArray rescorerConfiguration,
			String keywords)
		throws Exception;

	/**
	 * Create a QueryContext for suggesters.
	 *
	 * @param httpServletRequest
	 * @param suggesterConfiguration
	 * @param groupId
	 * @param locale
	 * @param keywords
	 * @return
	 * @throws Exception
	 */
	public QueryContext buildSuggesterQueryContext(
			HttpServletRequest httpServletRequest,
			JSONArray suggesterConfiguration, long groupId,
			Locale locale, String keywords)
		throws Exception;

	/**
	 * Parses request parameters.
	 *
	 * This overload is meant for parsing the parameters
	 * from PortletRequest, i.e. when called from a portlet.
	 *
	 * @param queryContext
	 * @throws Exception
	 */
	public void parseParameters(QueryContext queryContext) throws Exception;

	/**
	 * Parses parameters. 
	 * 
	 * This overload is meant for parsing parameters when 
	 * PortletRequest is not available.
	 *
	 * @param httpServletRequest
	 * @param parameterMap
	 * @throws Exception
	 */
	public void parseParametersHeadless(
			QueryContext queryContext, Map<String, Object> parameterMap)
		throws Exception;
	
	/**
	 * Processes a single, named query context contributors.
	 * 
	 * @param queryContext
	 * @throws Exception
	 */
	public void processQueryContextContributor(QueryContext queryContext, String name) 
			throws Exception;
	
	/**
	 * Processes query context contributors.
	 * 
	 * @param queryContext
	 * @throws Exception
	 */
	public void processQueryContextContributors(QueryContext queryContext) 
			throws Exception;	

}