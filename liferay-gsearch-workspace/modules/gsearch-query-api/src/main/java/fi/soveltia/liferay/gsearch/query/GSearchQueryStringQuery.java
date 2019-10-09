package fi.soveltia.liferay.gsearch.query;

import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.query.StringQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import fi.soveltia.liferay.gsearch.query.core.StringQueryImpl;

/**
 * QueryStringQuery query type for the Liferay GSearch.
 *
 * The most important difference to it's base implementation is field level boosting.
 * 
 * Comments for the property methods are mostly taken from Elasticsearch API documention.
 *
 * @author Petteri Karttunen
 */
public class GSearchQueryStringQuery 
	extends StringQueryImpl implements Query, StringQuery {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor inherited from our base class.
	 * 
	 * @param query
	 */
	public GSearchQueryStringQuery(String query) {
		super(query);
		_query = query;
	}
	
	/**
	 * Adds a field to run the query string against.
	 * 
	 * @param field
	 */
	public void addField(String field) {

		if (_fields == null) {
            _fields = new ArrayList<>();
        }
        _fields.add(field);
	}

	/**
	 * Add field with boost.
	 * 
	 * @param field
	 * @param boost
	 */
	public void addField(String field, float boost) {

		if (_fields == null) {
            _fields = new ArrayList<>();
        }
        _fields.add(field);
        
        if (_fieldsBoosts == null) {
            _fieldsBoosts = new HashMap<String, Float>();
        }
        _fieldsBoosts.put(field, boost);
	}

	/**
	 * Get autogenerate phrase queries.
	 * 
	 * @return boolean
	 */
	public Boolean isAutoGeneratePhraseQueries() {

		return _autoGeneratePhraseQueries;
	}

	/**
	 * Set to true if phrase queries will be automatically generated when the
	 * analyzer returns more than one term from whitespace delimited text. NOTE:
	 * this behavior may not be suitable for all languages. Set to false if
	 * phrase queries should only be generated when surrounded by double quotes.
	 * 
	 * @param autoGeneratePhraseQueries
	 */
	public void setAutoGeneratePhraseQueries(
		boolean autoGeneratePhraseQueries) {

		_autoGeneratePhraseQueries = autoGeneratePhraseQueries;
	}

	/**
	 * Get escape
	 * 
	 * @return
	 */
	public Boolean isEscape() {

		return _escape;
	}

	/**
	 * Set to true to enable escaping of the query string
	 * 
	 * @param escape
	 */
	public void setEscape(boolean escape) {

		_escape = escape;
	}

	/**
	 * Get fields
	 * 
	 * @return
	 */
	public List<String> getFields() {
		return _fields;
	}

	/**
	 * Get field boosts
	 * 
	 * @return
	 */
	public HashMap<String, Float> getFieldBoosts() {

		return _fieldsBoosts;
	}

	/**
	 * Get fuzzy rewrite.
	 * 
	 * @return String
	 */
	public String getFuzzyRewrite() {

		return _fuzzyRewrite;
	}

	/**
	 * Set fuzzy rewrite.
	 * 
	 * @param fuzzyRewrite
	 */
	public void setFuzzyRewrite(String fuzzyRewrite) {

		_fuzzyRewrite = fuzzyRewrite;
	}

	public Locale getLocale() {

		return _locale;
	}

	public void setLocale(Locale locale) {

		_locale = locale;
	}

	/**
	 * Get lowercase expanded terms.
	 * 
	 * @return boolean
	 */
	public Boolean isLowercaseExpandedTerms() {

		return _lowercaseExpandedTerms;
	}

	/**
	 * Whether terms of wildcard, prefix, fuzzy and range queries are to be
	 * automatically lower-cased or not. Default is true.
	 * 
	 * @param lowercaseExpandedTerms
	 */
	public void setLowercaseExpandedTerms(boolean lowercaseExpandedTerms) {

		_lowercaseExpandedTerms = lowercaseExpandedTerms;
	}

	/**
	 * Get minimun should match.
	 * 
	 * @return
	 */
	public String getMinimumShouldMatch() {

		return _minimumShouldMatch;
	}

	/**
	 * Set minimun should match count.
	 * 
	 * @param minimumShouldMatch
	 */
	public void setMinimumShouldMatch(String minimumShouldMatch) {

		_minimumShouldMatch = minimumShouldMatch;
	}

	/**
	 * Get query
	 * 
	 * @return String
	 */
	public String getQuery() {

		return _query;
	}

	/**
	 * Set query.
	 * 
	 * @param query
	 */
	public void setQuery(String query) {

		_query = query;
	}

	/**
	 * Get query name.
	 *
	 * @return
	 */
	public String getQueryName() {

		return _queryName;
	}

	/**
	 * Sets the query name for the filter that can be used when searching for
	 * matched_filters per hit.
	 * 
	 * @param queryName
	 */
	public void setQueryName(String queryName) {

		_queryName = queryName;
	}

	/**
	 * Get tiebreaker.
	 * 
	 * @return float
	 */
	public Float getTieBreaker() {

		return _tieBreaker;
	}

	/**
	 * When more than one field is used with the query string, and combined
	 * queries are using dis max, control the tie breaker for it.
	 * 
	 * @param tieBreaker
	 */
	public void setTieBreaker(float tieBreaker) {

		_tieBreaker = tieBreaker;
	}

	/**
	 * Is DisMax query.
	 * 
	 * @return boolean
	 */
	public Boolean isDisMax() {
		
		return _disMax;
	}

	/**
	 * When more than one field is used with the query string, should queries be
	 * combined using dis max, or boolean query. Defaults to dis max (true).
	 * 
	 * @param disMax
	 */
	public void setDisMax(boolean disMax) {

		_disMax = disMax;
	}
	
	private Boolean _autoGeneratePhraseQueries;
	private Boolean _escape;
	private List<String> _fields;
	private HashMap<String, Float> _fieldsBoosts;
	private String _fuzzyRewrite;
	private Locale _locale;
	private Boolean _lowercaseExpandedTerms;
	private String _minimumShouldMatch;
	private String _query;
	private String _queryName;
	private Float _tieBreaker;
	private Boolean _disMax;
}