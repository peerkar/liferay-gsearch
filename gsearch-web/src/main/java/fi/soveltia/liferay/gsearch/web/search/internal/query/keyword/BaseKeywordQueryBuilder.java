
package fi.soveltia.liferay.gsearch.web.search.internal.query.keyword;

import java.util.List;

import fi.soveltia.liferay.gsearch.web.search.internal.queryparams.QueryParams;
import fi.soveltia.liferay.gsearch.web.search.query.keyword.KeywordQueryBuilder;

/**
 * Abstract base class for keyword query builders.
 * 
 * @author Petteri Karttunen
 */
public abstract class BaseKeywordQueryBuilder implements KeywordQueryBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setQueryParams(QueryParams queryParams) {

		_queryParams = queryParams;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<KeywordFieldParam> getKeywordFieldParams() {

		return _keywordFieldParams;
	}

	protected QueryParams _queryParams;

	protected static List<KeywordFieldParam> _keywordFieldParams;
}
