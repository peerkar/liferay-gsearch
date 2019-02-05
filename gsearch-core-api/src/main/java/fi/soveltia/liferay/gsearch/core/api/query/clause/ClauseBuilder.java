
package fi.soveltia.liferay.gsearch.core.api.query.clause;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Query;

import javax.portlet.PortletRequest;

import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Builds a single query clause (Match, QueryStringQuery etc).
 * 
 * @author Petteri Karttunen
 */
public interface ClauseBuilder {

	/**
	 * Builds clause.
	 * 
	 * @param portletRequest
	 * @param configuration
	 * @param queryContext
	 * @return Query object
	 * @throws Exception
	 */
	public Query buildClause(
		PortletRequest portletRequest, JSONObject configuration,
		QueryContext queryContext)
		throws Exception;

	/**
	 * Checks if this builder can build the requested query type
	 * 
	 * @param queryType
	 * @return
	 */
	public boolean canBuild(String queryType);
}
