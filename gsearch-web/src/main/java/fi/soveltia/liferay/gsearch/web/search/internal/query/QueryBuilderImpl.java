
package fi.soveltia.liferay.gsearch.web.search.internal.query;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.StringQuery;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringBundler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.web.search.internal.queryparams.QueryParams;
import fi.soveltia.liferay.gsearch.web.search.query.QueryBuilder;
import fi.soveltia.liferay.gsearch.web.search.query.filter.QueryFilterBuilder;
import fi.soveltia.liferay.gsearch.web.search.query.keyword.KeywordQueryBuilderFactory;

/**
 * Query builder implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = QueryBuilder.class
)
public class QueryBuilderImpl implements QueryBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BooleanQuery buildQuery(
		PortletRequest portletRequest, QueryParams queryParams)
		throws Exception {

		BooleanQuery query = new BooleanQueryImpl();

		// Build keywords query. 
		// This will be updated along custom Elasticsearch adapter 
		// to use a better Liferay implementation of Elasticsearch StringQuery

		buildRawKeywordsQuery(query, queryParams);

		// Add filters

		BooleanFilter preBooleanFilter =
			_queryFilterBuilder.buildQueryFilter(portletRequest, queryParams);
		query.setPreBooleanFilter(preBooleanFilter);

		return query;
	}

	/**
	 * Build keywords query.
	 * 
	 * Please notice that buildKeywordQuery() methods are currently not implemented
	 * in the asset type specific classes.
	 * 
	 * @param query
	 * @param queryParams
	 * @throws Exception
	 */
	/*
	protected void buildKeywordsQuery(
		BooleanQuery query, QueryParams queryParams) throws Exception {

		BooleanQuery keywordsQuery = new BooleanQueryImpl();

		for (Class<?> clazz : queryParams.getClazzes()) {
			KeywordQueryBuilder keywordQueryBuilder =
				_keywordQueryBuilderFactory.getKeywordQueryBuilder(
					clazz.getName(), queryParams);

			if(clazz.getName().equals(JournalArticle.class.getName())) {
				Query classQuery = keywordQueryBuilder.buildKeywordQuery();
				keywordsQuery.add(classQuery, BooleanClauseOccur.SHOULD);
			}
		}
		query.add(keywordsQuery, BooleanClauseOccur.MUST);
	}
	*/

	/**
	 * Build keywords query using raw "StringQuery".
     * 
     * Liferay's StringQuery is by no means equivalent to
	 * Elasticsearch StringQuery type but just a raw query.
	 * 
	 * @param query
	 * @param queryParams
	 * @throws Exception
	 */
	protected void buildRawKeywordsQuery(
		BooleanQuery query, QueryParams queryParams) throws Exception {

		StringBundler sb = new StringBundler();

		sb.append(queryParams.getKeywords());

		StringQuery stringQuery = new StringQuery(sb.toString());
		query.add(stringQuery, BooleanClauseOccur.MUST);
	}	
	
	public static final DateFormat INDEX_DATE_FORMAT =
		new SimpleDateFormat("yyyyMMddHHmmss");

	@Reference
	private Portal _portal;

	@Reference
	private KeywordQueryBuilderFactory _keywordQueryBuilderFactory;

	@Reference
	private QueryFilterBuilder _queryFilterBuilder;

	@SuppressWarnings("unused")
	private static final Log _log =
		LogFactoryUtil.getLog(QueryBuilderImpl.class);
}
