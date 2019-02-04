
package fi.soveltia.liferay.gsearch.core.impl.query.clause;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.generic.MoreLikeThisQuery;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * More Like This query builder.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ClauseBuilder.class
)
public class MoreLikeThisQueryBuilder implements ClauseBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Query buildClause(
		PortletRequest portletRequest, JSONObject configuration,
		QueryContext queryContext)
		throws Exception {
		
		Locale locale = (Locale)queryContext.getParameter(ParameterNames.LOCALE);

		MoreLikeThisQuery moreLikeThisQuery =
			new MoreLikeThisQuery((long)queryContext.getParameter(ParameterNames.COMPANY_ID));
		
		String docUID = (String)queryContext.getParameter(ParameterNames.DOC_UID);
		
		if (docUID == null) {
			return null;
		}

		// Add a single like -document.

		moreLikeThisQuery.addDocumentUID(docUID);

		// Fields configuration

		JSONArray fieldsConfig = configuration.getJSONArray("fields");

		List<String> fields = new ArrayList<String>();

		for (int i = 0; i < fieldsConfig.length(); i++) {

			JSONObject item = fieldsConfig.getJSONObject(i);

			// Add non translated version

			String fieldName = item.getString("field_name");

			fields.add(fieldName);

			// Add translated version

			boolean isLocalized =
				GetterUtil.getBoolean(item.get("localized"), false);

			if (isLocalized) {

				String localizedFieldName =
					fieldName + "_" + locale.toString();

				fields.add(localizedFieldName);
			}
		}

		moreLikeThisQuery.addFields(fields);

		// Analyzer

		if (Validator.isNotNull(configuration.get("analyzer"))) {
			moreLikeThisQuery.setAnalyzer(configuration.getString("analyzer"));
		}

		// Boost

		if (Validator.isNotNull(configuration.get("boost"))) {
			moreLikeThisQuery.setBoost(
				GetterUtil.getFloat(configuration.get("boost")));
		}

		// Include input

		if (Validator.isNotNull(configuration.get("include"))) {
			moreLikeThisQuery.setIncludeInput(
				configuration.getBoolean("include"));
		}

		// Like text.

		if (Validator.isNotNull(configuration.get("like_text"))) {
			moreLikeThisQuery.setLikeText(configuration.getString("like_text"));
		}

		// Max doc frequency.

		if (Validator.isNotNull(configuration.get("max_doc_freq"))) {
			moreLikeThisQuery.setMaxDocFrequency(
				configuration.getInt("max_doc_freq"));
		}

		// Max query terms.

		if (Validator.isNotNull(configuration.get("max_query_terms"))) {
			moreLikeThisQuery.setMaxQueryTerms(
				configuration.getInt("max_query_terms"));
		}

		// Max word length.

		if (Validator.isNotNull(configuration.get("max_word_length"))) {
			moreLikeThisQuery.setMaxWordLength(
				configuration.getInt("max_word_length"));
		}

		// Min doc frequency.

		if (Validator.isNotNull(configuration.get("min_doc_freq"))) {
			moreLikeThisQuery.setMinDocFrequency(
				configuration.getInt("min_doc_freq"));
		}

		// Min should match.

		if (Validator.isNotNull(configuration.get("minimum_should_match"))) {
			moreLikeThisQuery.setMinShouldMatch(
				configuration.getString("minimum_should_match"));
		}

		// Min term freq.

		if (Validator.isNotNull(configuration.get("min_term_freq"))) {
			moreLikeThisQuery.setMinTermFrequency(
				configuration.getInt("min_term_freq"));
		}

		// Min word length.

		if (Validator.isNotNull(configuration.get("min_word_length"))) {
			moreLikeThisQuery.setMinWordLength(
				configuration.getInt("min_word_length"));
		}

		// Stopwords.

		if (Validator.isNotNull(configuration.get("stop_words"))) {

			JSONArray stopWords = configuration.getJSONArray("stop_words");

			List<String> list = new ArrayList<String>();

			for (int i = 0; i < stopWords.length(); i++) {
				list.add(stopWords.getString(i));
			}

			moreLikeThisQuery.addStopWords(list);
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
