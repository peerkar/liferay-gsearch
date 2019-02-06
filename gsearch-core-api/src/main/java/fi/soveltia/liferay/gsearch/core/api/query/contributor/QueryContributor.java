
package fi.soveltia.liferay.gsearch.core.api.query.contributor;

import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Query;

import javax.portlet.PortletRequest;

/**
 * Adds clauses/subqueries/signals to the main query. 
 * 
 * This is meant to be an interface for add on modules. An example case could 
 * be adding a subquery based audience targeting information to increase 
 * relevance for content targeted to current user.
 * 
 * @author Petteri Karttunen
 */
public interface QueryContributor {

	/**
	 * Builds query.
	 * 
	 * @param portletRequest
	 * @return Query
	 * @throws Exception
	 */
	public Query buildQuery(PortletRequest portletRequest)
		throws Exception;

	/**
	 * Get occur.
	 * 
	 * @return
	 */
	public BooleanClauseOccur getOccur();

	/**
	 * Is contributor enabled.
	 * 
	 * @return
	 */
	public boolean isEnabled();
}
