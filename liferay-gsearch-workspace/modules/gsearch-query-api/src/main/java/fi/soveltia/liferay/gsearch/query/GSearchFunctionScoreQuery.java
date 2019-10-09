package fi.soveltia.liferay.gsearch.query;

import com.liferay.portal.search.query.FunctionScoreQuery;
import com.liferay.portal.search.query.Query;

import fi.soveltia.liferay.gsearch.query.core.FunctionScoreQueryImpl;

/**
 * Function Score Query.
 *
 * See https://www.elastic.co/guide/en/elasticsearch/guide/current/decay-functions.html
 *
 * @author Petteri Karttunen
 */
public abstract class GSearchFunctionScoreQuery 
	extends FunctionScoreQueryImpl implements Query, FunctionScoreQuery {

	public GSearchFunctionScoreQuery(Query query) {
		super(query);
	}
		
	/**
	 * Get boost mode
	 *
	 * @return
	 */
	public String getBoostMode() {
		return _boostMode;
	}

	/**
	 * Set boost mode
	 *
	 * @param boostMode
	 */
	public void setBoostMode(String boostMode) {
		_boostMode = boostMode;
	}

	private static final long serialVersionUID = 1L;

	private String _boostMode;

}