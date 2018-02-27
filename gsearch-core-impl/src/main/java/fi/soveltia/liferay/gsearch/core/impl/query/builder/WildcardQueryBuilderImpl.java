package fi.soveltia.liferay.gsearch.core.impl.query.builder;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.WildcardQuery;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.WildcardQueryImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.query.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.query.builder.WildcardQueryBuilder;

/**
 * WildcardQuery builder service implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = WildcardQueryBuilder.class
)
public class WildcardQueryBuilderImpl implements WildcardQueryBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query buildQuery(
		JSONObject configurationObject, QueryParams queryParams)
		throws Exception {
		
		// If there's a predefined value in the configuration, use that

		String value = configurationObject.getString("value");

		if (Validator.isNull(value)) {
			value = queryParams.getKeywords();
		}

		// Splitter?
		
		String keywordSplitter = configurationObject.getString("keywordSplitter");

		if (keywordSplitter != null && keywordSplitter.length() > 0) {

			BooleanQuery query = new BooleanQueryImpl();
					
			String [] keywords = value.split(keywordSplitter);
				
			for (String keyword : keywords) {
				WildcardQuery q = buildClause(configurationObject, keyword);
				query.add(q, BooleanClauseOccur.SHOULD);
			}
			
			// Boost
			
			float boost = GetterUtil.getFloat(configurationObject.get("boost"), 1.0f);
			query.setBoost(boost);
			
			return query;
		
		} else {

			WildcardQuery wildcardQuery = buildClause(configurationObject, value);

			// Boost

			float boost = GetterUtil.getFloat(configurationObject.get("boost"), 1.0f);
			wildcardQuery.setBoost(boost);
			
			return wildcardQuery;
			
		}
	}

	/**
	 * Build single clause
	 * 
	 * @param configurationObject
	 * @param keywords
	 * @return
	 */
	protected WildcardQuery buildClause(JSONObject configurationObject, String keyword) {
		
		String fieldName = configurationObject.getString("fieldName");

		if (fieldName == null) {
			return null;
		}

		StringBundler sb = new StringBundler();
		
		String prefix = configurationObject.getString("valuePrefix");
		if (Validator.isNotNull(prefix)) {
			sb.append(prefix);
		}

		sb.append(keyword);

		String suffix = configurationObject.getString("valueSuffix");
		if (Validator.isNotNull(suffix)) {
			sb.append(suffix);
		}

		WildcardQuery wildcardQuery =
			new WildcardQueryImpl(fieldName, sb.toString());
		
		return wildcardQuery;
	}
}
