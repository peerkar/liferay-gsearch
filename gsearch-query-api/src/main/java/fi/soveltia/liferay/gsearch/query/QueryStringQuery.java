
package fi.soveltia.liferay.gsearch.query;

import com.liferay.portal.kernel.search.BaseQueryImpl;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.generic.StringQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * QueryStringQuery API for the GSearch.
 *
 * This is made as an extension of StringQuery to avoid further customizations
 * for the portal search API (like QueryVisitor interface).
 * 
 * Comments for the property methods are mostly taken from Elasticsearch API documention.
 * 
 * @author Petteri Karttunen
 */
public class QueryStringQuery extends StringQuery implements Query {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor inherited from our base class.
	 * 
	 * @param query
	 */
	public QueryStringQuery(String query) {
		super(query);
		this.query = query;
	}

	/**
	 * Adds a field to run the query string against.
	 * 
	 * @param field
	 */
	public void addField(String field) {

		if (fields == null) {
            fields = new ArrayList<>();
        }
        fields.add(field);
	}

	/**
	 * Add field with boost.
	 * 
	 * @param field
	 * @param boost
	 */
	public void addField(String field, float boost) {

		if (fields == null) {
            fields = new ArrayList<>();
        }
        fields.add(field);
        
        if (fieldsBoosts == null) {
            fieldsBoosts = new HashMap<String, Float>();
        }
        fieldsBoosts.put(field, boost);
	}
	
	/**
	 * Should leading wildcards be allowed or not. Defaults to true.
	 * 
	 * @return
	 */
	public Boolean isAllowLeadingWildcard() {

		return allowLeadingWildcard;
	}

	/**
	 * Should leading wildcards be allowed or not. Defaults to true.
	 * 
	 * @param allowLeadingWildcard
	 */
	public void setAllowLeadingWildcard(boolean allowLeadingWildcard) {

		this.allowLeadingWildcard = allowLeadingWildcard;
	}

	/**
	 * The optional analyzer used to analyze the query string. Note, if a field
	 * has search analyzer defined for it, then it will be used automatically.
	 * Defaults to the smart search analyzer.
	 * 
	 * @return
	 */
	public String getAnalyzer() {

		return analyzer;
	}

	public void setAnalyzer(String analyzer) {

		this.analyzer = analyzer;
	}

	/**
	 * Is analysis enabled on wildcard and prefix queries.
	 * 
	 * @return
	 */
	public Boolean isAnalyzeWildcard() {

		return analyzeWildcard;
	}

	/**
	 * Set to true to enable analysis on wildcard and prefix queries.
	 * 
	 * @return
	 */
	public void setAnalyzeWildcard(boolean analyzeWildcard) {

		this.analyzeWildcard = analyzeWildcard;
	}

	/**
	 * Get autogenerate phrase queries.
	 * 
	 * @return boolean
	 */
	public Boolean isAutoGeneratePhraseQueries() {

		return autoGeneratePhraseQueries;
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

		this.autoGeneratePhraseQueries = autoGeneratePhraseQueries;
	}

	/**
	 * Get boost.
	 * 
	 * @return float
	 */
	public float getBoost() {

		if (boost == null) {
			return BaseQueryImpl.BOOST_DEFAULT;
		} else {
			return boost;
		}
	}

	/**
	 * Sets the boost for this query. Documents matching this query will (in
	 * addition to the normal weightings) have their score multiplied by the
	 * boost provided.
	 * 
	 * @param boost
	 */
	public void setBoost(float boost) {

		this.boost = boost;
	}

	/**
	 * Get default field name.
	 * 
	 * @return String
	 */
	public String getDefaultField() {

		return defaultField;
	}

	/**
	 * The default field to run against when no prefix field is specified. Only
	 * relevant when not explicitly adding fields the query string will run
	 * against.
	 * 
	 * @param defaultField
	 */
	public void setDefaultField(String defaultField) {

		this.defaultField = defaultField;
	}

	/**
	 * Get default operator
	 * 
	 * @return
	 */
	public Operator getDefaultOperator() {

		return defaultOperator;
	}

	/**
	 * Sets the boolean operator of the query parser used to parse the query
	 * string. In default mode (Operator.OR) terms without any modifiers are
	 * considered optional: for example capital of Hungary is equal to capital
	 * OR of OR Hungary.
	 * 
	 * @param defaultOperator
	 */
	public void setDefaultOperator(Operator defaultOperator) {

		this.defaultOperator = defaultOperator;
	}

	public Boolean isEnablePositionIncrements() {

		return enablePositionIncrements;
	}

	/**
	 * Set to true to enable position increments in result query. Defaults to
	 * true. When set, result phrase and multi-phrase queries will be aware of
	 * position increments. Useful when e.g. a StopFilter increases the position
	 * increment of the token that follows an omitted token.
	 * 
	 * @param enablePositionIncrements
	 */
	public void setEnablePositionIncrements(boolean enablePositionIncrements) {

		this.enablePositionIncrements = enablePositionIncrements;
	}

	/**
	 * Get escape
	 * 
	 * @return
	 */
	public Boolean isEscape() {

		return escape;
	}

	/**
	 * Set to true to enable escaping of the query string
	 * 
	 * @param escape
	 */
	public void setEscape(boolean escape) {

		this.escape = escape;
	}

	/**
	 * Get fields
	 * 
	 * @return
	 */
	public List<String> getFields() {
		return fields;
	}

	/**
	 * Get field boosts
	 * 
	 * @return
	 */
	public HashMap<String, Float> getFieldBoosts() {

		return fieldsBoosts;
	}

	/**
	 * Get fuzziness
	 * 
	 * @return Fuzziness
	 */
	public Float getFuzziness() {

		return fuzziness;
	}

	/**
	 * Set the edit distance for fuzzy queries. Default is "AUTO".
	 * 
	 * @param fuzziness
	 */
	public void setFuzziness(float fuzziness) {

		this.fuzziness = fuzziness;
	}

	/**
	 * Get fuzzy max expansions
	 * 
	 * @return int
	 */
	public Integer getFuzzyMaxExpansions() {

		return fuzzyMaxExpansions;
	}

	/**
	 * Set fuzzy max expansions.
	 * 
	 * @param fuzzyMaxExpansions
	 */
	public void setFuzzyMaxExpansions(int fuzzyMaxExpansions) {

		this.fuzzyMaxExpansions = fuzzyMaxExpansions;
	}

	/**
	 * Get fuzzy prefix length.
	 * 
	 * @return int
	 */
	public Integer getFuzzyPrefixLength() {

		return fuzzyPrefixLength;
	}

	/**
	 * Set the minimum prefix length for fuzzy queries. Default is 1.
	 * 
	 * @param fuzzyPrefixLength
	 */
	public void setFuzzyPrefixLength(int fuzzyPrefixLength) {

		this.fuzzyPrefixLength = fuzzyPrefixLength;
	}

	/**
	 * Get fuzzy rewrite.
	 * 
	 * @return String
	 */
	public String getFuzzyRewrite() {

		return fuzzyRewrite;
	}

	/**
	 * Set fuzzy rewrite.
	 * 
	 * @param fuzzyRewrite
	 */
	public void setFuzzyRewrite(String fuzzyRewrite) {

		this.fuzzyRewrite = fuzzyRewrite;
	}

	/**
	 * Get lenient.
	 * 
	 * @return
	 */
	public Boolean isLenient() {

		return lenient;
	}

	/**
	 * Sets the query string parser to be lenient when parsing field values,
	 * defaults to the index setting and if not set, defaults to false.
	 * 
	 * @param lenient
	 */
	public void setLenient(boolean lenient) {

		this.lenient = lenient;
	}

	public Locale getLocale() {

		return locale;
	}

	public void setLocale(Locale locale) {

		this.locale = locale;
	}

	/**
	 * Get lowercase expanded terms.
	 * 
	 * @return boolean
	 */
	public Boolean isLowercaseExpandedTerms() {

		return lowercaseExpandedTerms;
	}

	/**
	 * Whether terms of wildcard, prefix, fuzzy and range queries are to be
	 * automatically lower-cased or not. Default is true.
	 * 
	 * @param lowercaseExpandedTerms
	 */
	public void setLowercaseExpandedTerms(boolean lowercaseExpandedTerms) {

		this.lowercaseExpandedTerms = lowercaseExpandedTerms;
	}

	/**
	 * Get max count of determinized states.
	 * 
	 * @return
	 */
	public Integer getMaxDeterminizedStates() {

		return maxDeterminizedStates;
	}

	/**
	 * Protects against too-difficult regular expression queries.
	 * 
	 * @param maxDeterminizedStates
	 */
	public void setMaxDeterminizedStates(int maxDeterminizedStates) {

		this.maxDeterminizedStates = maxDeterminizedStates;
	}

	/**
	 * Get minimun should match.
	 * 
	 * @return
	 */
	public String getMinimumShouldMatch() {

		return minimumShouldMatch;
	}

	/**
	 * Set minimun should match count.
	 * 
	 * @param minimumShouldMatch
	 */
	public void setMinimumShouldMatch(String minimumShouldMatch) {

		this.minimumShouldMatch = minimumShouldMatch;
	}

	/**
	 * Get phrase slop
	 * 
	 * @return int
	 */
	public Integer getPhraseSlop() {

		return phraseSlop;
	}

	/**
	 * Sets the default slop for phrases. If zero, then exact phrase matches are
	 * required. Default value is zero.
	 * 
	 * @param phraseSlop
	 */
	public void setPhraseSlop(int phraseSlop) {

		this.phraseSlop = phraseSlop;
	}

	/**
	 * Get query
	 * 
	 * @return String
	 */
	public String getQuery() {

		return query;
	}

	/**
	 * Set query.
	 * 
	 * @param query
	 */
	public void setQuery(String query) {

		this.query = query;
	}

	/**
	 * Get query name.
	 *
	 * @return
	 */
	public String getQueryName() {

		return queryName;
	}

	/**
	 * Sets the query name for the filter that can be used when searching for
	 * matched_filters per hit.
	 * 
	 * @param queryName
	 */
	public void setQueryName(String queryName) {

		this.queryName = queryName;
	}

	/**
	 * Get quote analyzer.
	 * 
	 * @return
	 */
	public String getQuoteAnalyzer() {

		return quoteAnalyzer;
	}

	/**
	 * Sets the query name for the filter that can be used when searching for
	 * matched_filters per hit.
	 * 
	 * @param quoteAnalyzer
	 */
	public void setQuoteAnalyzer(String quoteAnalyzer) {

		this.quoteAnalyzer = quoteAnalyzer;
	}

	/**
	 * Get quote field suffix.
	 * 
	 * @return String
	 */
	public String getQuoteFieldSuffix() {

		return quoteFieldSuffix;
	}

	/**
	 * An optional field name suffix to automatically try and add to the field
	 * searched when using quoted text.
	 * 
	 * @param quoteFieldSuffix
	 */
	public void setQuoteFieldSuffix(String quoteFieldSuffix) {

		this.quoteFieldSuffix = quoteFieldSuffix;
	}

	public String getRewrite() {

		return rewrite;
	}

	public void setRewrite(String rewrite) {

		this.rewrite = rewrite;
	}

	/**
	 * Get tiebreaker.
	 * 
	 * @return float
	 */
	public Float getTieBreaker() {

		return tieBreaker;
	}

	/**
	 * When more than one field is used with the query string, and combined
	 * queries are using dis max, control the tie breaker for it.
	 * 
	 * @param tieBreaker
	 */
	public void setTieBreaker(float tieBreaker) {

		this.tieBreaker = tieBreaker;
	}

	/**
	 * Get timezone.
	 * 
	 * @return String
	 */
	public String getTimeZone() {

		return timeZone;
	}

	/**
	 * In case of date field, we can adjust the from/to fields using a timezone
	 * 
	 * @param timeZone
	 */
	public void setTimeZone(String timeZone) {

		this.timeZone = timeZone;
	}

	/**
	 * Is DisMax query.
	 * 
	 * @return boolean
	 */
	public Boolean isDisMax() {
		
		return disMax;
	}

	/**
	 * When more than one field is used with the query string, should queries be
	 * combined using dis max, or boolean query. Defaults to dis max (true).
	 * 
	 * @param disMax
	 */
	public void setDisMax(boolean disMax) {

		this.disMax = disMax;
	}
	
	protected Boolean allowLeadingWildcard;
	protected String analyzer;
	protected Boolean analyzeWildcard;
	protected Boolean autoGeneratePhraseQueries;
	protected Float boost;
	protected String defaultField;
	protected Operator defaultOperator;
	protected Boolean enablePositionIncrements;
	protected Boolean escape;
	protected List<String> fields;
    protected HashMap<String, Float> fieldsBoosts;
	protected Float fuzziness;
	protected Integer fuzzyMaxExpansions;
	protected Integer fuzzyPrefixLength;
	protected String fuzzyRewrite;
	protected Boolean lenient;
	protected Locale locale;
	protected Boolean lowercaseExpandedTerms;
	protected Integer maxDeterminizedStates;
	protected String minimumShouldMatch;
	protected Integer phraseSlop;
	protected String query;
	protected String queryName;
	protected String quoteAnalyzer;
	protected String quoteFieldSuffix;
	protected String rewrite;
	protected Float tieBreaker;
	protected String timeZone;
	protected Boolean disMax;
}
