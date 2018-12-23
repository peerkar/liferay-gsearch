
package fi.soveltia.liferay.gsearch.core.api.query.clause;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Query;

import javax.portlet.PortletRequest;

import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;

/**
 * Query clause builder. 
 * 
 * Implementations of this build typed (Match, QueryStringQuery etc) clauses
 * to be added to the main query.
 * 
 * @author Petteri Karttunen
 */
public interface ClauseBuilder {
	
	/**
	 * Build clause.
	 * 
	 * @param portletRequest
	 * @param configuration
	 * @param queryParams
	 * @return Query object
	 * @throws Exception
	 */
	public Query buildClause(PortletRequest portletRequest,
		JSONObject configuration, QueryParams queryParams)
		throws Exception;	

	/**
	 * Check if this builder can build the requested query type
	 * 
	 * @param queryType
	 * @return
	 */
	public boolean canBuild(String queryType);
}
