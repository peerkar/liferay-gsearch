
package fi.soveltia.liferay.gsearch.core.api.query.context;

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
	 * @param companyId
	 * @param locale
	 * @param keywords
	 * @return
	 * @throws Exception
	 */
	public QueryContext buildQueryContext(
		HttpServletRequest httpServletRequest, 
		long companyId, Locale locale, String keywords)
		throws Exception;
	
	/**
	 * Creates a QueryContext object.
	 *
	 * @param httpServletRequest
	 * @param filterConfiguration
	 * @param clauseConfiguration
	 * @param facetConfiguration
	 * @param sortConfiguration
	 * @return
	 * @throws Exception
	 */
	public QueryContext buildQueryContext(
		HttpServletRequest httpServletRequest, String[] filterConfiguration,
		String[] clauseConfiguration, String[] facetConfiguration,
		String[] sortConfiguration, String[] suggesterConfiguration, Locale locale)
		throws Exception;

	/**
	 * Create a QueryContext for suggesters.
	 * 
	 * @param httpServletRequest
	 * @param suggesterConfiguration
	 * @return
	 * @throws Exception
	 */
	public QueryContext buildSuggesterQueryContext(
		HttpServletRequest httpServletRequest, String[] suggesterConfiguration, 
		long companyId, long groupId, Locale locale, String keywords)
		throws Exception;

	/**
	 * Parses request parameters.
	 * 
	 * @param queryContext
	 * @throws Exception
	 */
	public void parseParameters(QueryContext queryContext)
		throws Exception;

	/**
	 * Parses parameters. Used from headless API.
	 * 
	 * @param httpServletRequest
	 * @param parameterMap
	 * @throws Exception
	 */
	public void parseParameters(
		QueryContext queryContext, Map<String, Object> parameterMap)
		throws Exception;

}
