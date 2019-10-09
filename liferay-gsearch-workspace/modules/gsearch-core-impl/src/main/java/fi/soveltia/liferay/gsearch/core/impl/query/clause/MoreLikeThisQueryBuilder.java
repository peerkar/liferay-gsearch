
package fi.soveltia.liferay.gsearch.core.impl.query.clause;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.query.MoreLikeThisQuery;
import com.liferay.portal.search.query.MoreLikeThisQuery.DocumentIdentifier;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ClauseConfigurationValues;
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
			QueryContext queryContext, JSONObject configuration)
		throws Exception {

		String[]likeTexts = new String[0];
		
		JSONArray likeTextsArray = configuration.getJSONArray(("like_texts"));
		
		if (likeTextsArray != null && likeTextsArray.length() > 0) {
			likeTexts = new String[likeTextsArray.length()];
			
			for (int i=0; i < likeTextsArray.length(); i++) {
				likeTexts[i] = likeTextsArray.getString(i);
			}
		}
		
		DocumentIdentifier[] documentIdentifiers = 
				(DocumentIdentifier[])queryContext.getParameter(
			ParameterNames.DOCUMENT_IDENTIFIERS);
		
		if (likeTexts.length == 0 && documentIdentifiers == null) {
			return null;
		}

		Locale locale = (Locale)queryContext.getParameter(
			ParameterNames.LOCALE);

		// Fields configuration

		JSONArray fieldsConfig = configuration.getJSONArray(
				ClauseConfigurationKeys.FIELDS);

		List<String> fields = new ArrayList<>();

		for (int i = 0; i < fieldsConfig.length(); i++) {
			JSONObject item = fieldsConfig.getJSONObject(i);

			// Add non translated version

			String fieldName = item.getString(
					ClauseConfigurationKeys.FIELD_NAME);

			fields.add(fieldName);

			// Add translated version

			boolean localized = GetterUtil.getBoolean(
				item.get("localized"), false);

			if (localized) {
				String localizedFieldName = fieldName + "_" + locale.toString();

				fields.add(localizedFieldName);
			}
		}
		
		MoreLikeThisQuery moreLikeThisQuery = _queries.moreLikeThis(fields, likeTexts);

		moreLikeThisQuery.addDocumentIdentifiers(documentIdentifiers);

		// Analyzer

		if (Validator.isNotNull(configuration.get(
				ClauseConfigurationKeys.ANALYZER))) {
			moreLikeThisQuery.setAnalyzer(configuration.getString(
					ClauseConfigurationKeys.ANALYZER));
		}

		// Boost

		if (Validator.isNotNull(configuration.get(
				ClauseConfigurationKeys.BOOST))) {
			moreLikeThisQuery.setBoost(
				GetterUtil.getFloat(configuration.get(
						ClauseConfigurationKeys.BOOST)));
		}

		// Include input

		if (Validator.isNotNull(configuration.get("include"))) {
			moreLikeThisQuery.setIncludeInput(
				configuration.getBoolean("include"));
		}

		// Like text.

		if (Validator.isNotNull(configuration.get("like_text"))) {
			moreLikeThisQuery.addLikeText(configuration.getString("like_text"));
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

			List<String> list = new ArrayList<>();

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
		return querytype.equals(_QUERY_TYPE);
	}

	private static final String _QUERY_TYPE = 
			ClauseConfigurationValues.QUERY_TYPE_MORE_LIKE_THIS;

	@Reference
	private Queries _queries;

}