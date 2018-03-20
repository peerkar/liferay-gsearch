package fi.soveltia.liferay.gsearch.query;

import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.generic.StringQuery;

/**
 *  Function Score Query for GSearch.
 * 
 * This is made as an example and as an extension of StringQuery to avoid customizations
 * to the portal search API (QueryVisitor interface should be overridden).
 * 
 * Please see https://www.elastic.co/guide/en/elasticsearch/guide/current/decay-functions.html 
 * for more information.
 * 
 * @author Petteri Karttunen
 */
public abstract class FunctionScoreQuery extends StringQuery implements Query {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor inherited from our base class.
	 * 
	 * This is here just for complying with the original StringQuery.
	 * 
	 * @param query
	 */
	public FunctionScoreQuery(String query) {
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
	
		this._boostMode = boostMode;
	}

	/**
	 * Get max boost.
	 * 
	 * @return
	 */
	public Float getMaxBoost() {
	
		return _maxBoost;
	}

	/**
	 * Set max boost.
	 * 
	 * @param maxBoost
	 */
	public void setMaxBoost(float maxBoost) {
	
		this._maxBoost = maxBoost;
	}
	
	/**
	 * Get min score.
	 * 
	 * @return
	 */
	public Float getMinScore() {
	
		return _minScore;
	}

	/**
	 * Set min score.
	 * 
	 * @param minScore
	 */
	public void setMinScore(float minScore) {
	
		this._minScore = minScore;
	}
	
	/**
	 * Get score mode.
	 * 
	 * @return
	 */
	public String getScoreMode() {
	
		return _scoreMode;
	}

	/**
	 *	Set score mode.
	 *
	 *  Score mode defines how results of individual score 
	 *	functions will be aggregated. Can be first, avg, max, sum, min, multiply
	 *
	 * @param scoreMode
	 */
	public void setScoreMode(String scoreMode) {
		_scoreMode = scoreMode;
	}	

	private String _boostMode;
	private Float _maxBoost;
	private Float _minScore;
	private String _scoreMode;
}