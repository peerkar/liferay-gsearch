package fi.soveltia.liferay.gsearch.core.impl.query.clause;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.generic.MoreLikeThisQuery;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.params.QueryParams;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseBuilder;

/**
 * More Like This query builder service implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ClauseBuilder.class
)
public class MoreLikeThisQueryBuilderImpl implements ClauseBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query buildClause(
		JSONObject configurationObject, QueryParams queryParams)
		throws Exception {

		MoreLikeThisQuery moreLikeThisQuery = new MoreLikeThisQuery(queryParams.getCompanyId());
		
		// Doc UID 
		
		Map<String, Object>extraParams = queryParams.getExtraParams();
		if (extraParams == null || extraParams.get("docUID") == null) {
			return null;
		}

		// Add single like document.
		
		String docUID = (String)extraParams.get("docUID");
		
		moreLikeThisQuery.addDocumentUID(docUID);

		// Fields configuration
		
		JSONArray fieldsConfig = configurationObject.getJSONArray("fields");

		List<String> fields = new ArrayList<String>();
		
		for (int i = 0; i < fieldsConfig.length(); i++) {

			JSONObject item = fieldsConfig.getJSONObject(i);

			// Add non translated version

			String fieldName = item.getString("fieldName");

			fields.add(fieldName);

			// Add translated version

			boolean isLocalized =
				GetterUtil.getBoolean(item.get("localized"), false);

			if (isLocalized) {

				String localizedFieldName =
					fieldName + "_" + queryParams.getLocale().toString();

				fields.add(localizedFieldName);
			}
		}
		
		moreLikeThisQuery.addFields(fields);

		// Max query terms.
		
		if (configurationObject.getString("maxQueryTerms") != null) {
			int maxQueryTerms = GetterUtil.getInteger(
				configurationObject.getString("maxQueryTerms"), 12);
			moreLikeThisQuery.setMaxQueryTerms(maxQueryTerms);
		}

		// Min term freq.
		
		if (configurationObject.getString("minTermFreq") != null) {
			int minTermFreq = GetterUtil.getInteger(
				configurationObject.getString("minTermFreq"), 2);
			moreLikeThisQuery.setMinTermFrequency(minTermFreq);
		}

		// Min doc freq.
		
		if (configurationObject.getString("minDocFreq") != null) {
			int minDocFreq = GetterUtil.getInteger(
				configurationObject.getString("minDocFreq"), 3);
			moreLikeThisQuery.setMinDocFrequency(minDocFreq);
		}

		// Max doc freq.
		
		if (configurationObject.getString("maxDocFreq") != null) {
			int maxDocFreq = GetterUtil.getInteger(
				configurationObject.getString("maxDocFreq"), 0);
			moreLikeThisQuery.setMaxDocFrequency(maxDocFreq);
		}

		// Min word length.
		
		if (configurationObject.getString("minWordLength") != null) {
			int minWordLength = GetterUtil.getInteger(
				configurationObject.getString("minWordLength"), 0);
			moreLikeThisQuery.setMinWordLength(minWordLength);
		}

		// Max word length.
		
		if (configurationObject.getString("maxWordLength") != null) {
			int maxWordLength = GetterUtil.getInteger(
				configurationObject.getString("maxWordLength"), 0);
			moreLikeThisQuery.setMaxWordLength(maxWordLength);
		}

		// Min should match.
		
		if (configurationObject.getString("minimumShouldMatch") != null) {
			String minimumShouldMatch = 
					configurationObject.getString("minimumShouldMatch", "30%");
			moreLikeThisQuery.setMinShouldMatch(minimumShouldMatch);
		}

		// Include input
		
		moreLikeThisQuery.setIncludeInput(configurationObject.getBoolean("includeInput", false));
		
		// Stopwords.

		if (configurationObject.getJSONArray("stopWords") != null) {
			 JSONArray stopWords = 
					configurationObject.getJSONArray("stopWords");
			 
			 List<String> list = new ArrayList<String>();
			 
			 for (int i=0; i< stopWords.length(); i++) {
			     list.add(stopWords.getString(i) );
			 }
			 
			 moreLikeThisQuery.addStopWords(list);
		}
		
		// Boost
		
		if (configurationObject.getString("boost") != null) {
			float boost =
				GetterUtil.getFloat(configurationObject.get("boost"), 1.0f);
			moreLikeThisQuery.setBoost(boost);
		}
		
		return moreLikeThisQuery;		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canBuild(String querytype) {

		return (querytype.equals(QUERY_TYPE));
	}

	private static final String QUERY_TYPE = "more_like_this";
}
