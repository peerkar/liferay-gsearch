
package fi.soveltia.liferay.gsearch.core.impl.suggest;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.suggest.AggregateSuggester;
import com.liferay.portal.kernel.search.suggest.CompletionSuggester;
import com.liferay.portal.kernel.search.suggest.PhraseSuggester;
import com.liferay.portal.kernel.search.suggest.QuerySuggester;
import com.liferay.portal.kernel.search.suggest.Suggester;
import com.liferay.portal.kernel.search.suggest.SuggesterResult;
import com.liferay.portal.kernel.search.suggest.SuggesterResult.Entry;
import com.liferay.portal.kernel.search.suggest.SuggesterResult.Entry.Option;
import com.liferay.portal.kernel.search.suggest.SuggesterResults;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.configuration.ConfigurationHelper;
import fi.soveltia.liferay.gsearch.core.api.constants.ConfigurationKeys;
import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.suggest.GSearchKeywordSuggester;

/**
 * Keyword suggester implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.core.impl.configuration.ModuleConfiguration", 
	immediate = true, 
	service = GSearchKeywordSuggester.class
)
public class GSearchKeywordSuggesterImpl implements GSearchKeywordSuggester {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONArray getSuggestions(QueryContext queryContext)
		throws Exception {

		String[] suggestions = getSuggestionsAsStringArray(queryContext);

		JSONArray resultsArray = JSONFactoryUtil.createJSONArray();

		if (suggestions != null) {

			for (String s : suggestions) {
				resultsArray.put(s);
			}
		}
		return resultsArray;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getSuggestionsAsStringArray(QueryContext queryContext)
		throws Exception {

		String keywords = queryContext.getKeywords();

		if (Validator.isNull(keywords)) {
			return new String[0];
		}

		List<String> suggestions = new ArrayList<String>();

		// We don't need uppercases in suggestions.

		keywords = keywords.toLowerCase();

		AggregateSuggester aggregateSuggester =
			new AggregateSuggester(GSEARCH_SUGGESTER_NAME, keywords);

		String[] configuration =
			queryContext.getConfiguration(ConfigurationKeys.SUGGESTER);

		Suggester suggester = null;

		for (int i = 0; i < configuration.length; i++) {

			JSONObject item =
				JSONFactoryUtil.createJSONObject(configuration[i]);

			suggester = null;

			String suggesterType = item.getString("type");

			if ("phrase".equals(suggesterType)) {
				suggester = getPhraseSuggester(item, queryContext, keywords);

			}
			else if ("completion".equals(suggesterType)) {
				suggester =
					getCompletionSuggester(item, queryContext, keywords);
			}

			if (suggester != null) {
				aggregateSuggester.addSuggester(suggester);
			}
		}

		// Create searchcontext.

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(
			(long) queryContext.getParameter(ParameterNames.COMPANY_ID));

		// As localized suggestion index fields are template based, it might
		// be that there are no fields yet. This might throw a harmless
		// exception.

		try {
			SuggesterResults suggesters =
				_querySuggester.suggest(searchContext, aggregateSuggester);

			Collection<SuggesterResult> suggesterResults =
				suggesters.getSuggesterResults();

			if (suggesterResults != null) {

				for (SuggesterResult suggesterResult : suggesterResults) {

					for (Entry entry : suggesterResult.getEntries()) {

						for (Option option : entry.getOptions()) {

							if (_log.isDebugEnabled()) {
								_log.debug(
									"Adding suggestion:" + option.getText());
							}

							if (!suggestions.contains(option.getText())) {
								suggestions.add(option.getText());
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			_log.warn(e.getMessage(), e);
		}
		return suggestions.stream().toArray(String[]::new);
	}

	/**
	 * Get a completion suggester
	 * 
	 * @param configuration
	 * @param queryContext
	 * @param keywords
	 * @return
	 * @throws Exception
	 */
	protected Suggester getCompletionSuggester(
		JSONObject configuration, QueryContext queryContext, String keywords)
		throws Exception {

		String fieldName = _configurationHelper.parseConfigurationVariables(
			queryContext, configuration.getString("field_name"));

		CompletionSuggester suggester =
			new CompletionSuggester(createSuggesterName(), fieldName, keywords);

		// Set some meaningful default for the number of suggestions.

		suggester.setSize(configuration.getInt("number_of_suggestions", 5));

		if (Validator.isNotNull(configuration.get("analyzer"))) {
			suggester.setAnalyzer(configuration.getString("analyzer"));
		}

		return suggester;
	}

	/**
	 * Get a phrase suggester.
	 * 
	 * @param configuration
	 * @param queryContext
	 * @param keywords
	 * @return
	 * @throws Exception
	 */
	protected Suggester getPhraseSuggester(
		JSONObject configuration, QueryContext queryContext, String keywords)
		throws Exception {

		String fieldName = _configurationHelper.parseConfigurationVariables(
			queryContext, configuration.getString("field_name"));

		PhraseSuggester suggester =
			new PhraseSuggester(createSuggesterName(), fieldName, keywords);

		// Set some meaningful default for the number of suggestions.

		int size = configuration.getInt("number_of_suggestions", 5);
		suggester.setSize(size);

		if (Validator.isNotNull(configuration.get("analyzer"))) {
			suggester.setAnalyzer(configuration.getString("analyzer"));
		}

		if (Validator.isNotNull(configuration.get("confidence"))) {
			suggester.setConfidence(
				GetterUtil.getFloat(configuration.get("confidence")));
		}

		if (Validator.isNotNull(configuration.get("gram_size"))) {
			suggester.setGramSize((configuration.getInt("gram_size")));
		}

		if (Validator.isNotNull(configuration.get("max_errors"))) {
			suggester.setMaxErrors(
				GetterUtil.getFloat(configuration.get("max_errors")));
		}

		if (Validator.isNotNull(configuration.get("pre_highlight_tag"))) {
			suggester.setPreHighlightFilter(
				configuration.getString("pre_highlight_tag"));
		}

		if (Validator.isNotNull(configuration.get("post_highlight_tag"))) {
			suggester.setPreHighlightFilter(
				configuration.getString("post_highlight_tag"));
		}

		if (Validator.isNotNull(
			configuration.get("real_word_error_likelihood"))) {
			suggester.setRealWordErrorLikelihood(
				GetterUtil.getFloat(
					configuration.get("real_word_error_likelihood")));
		}

		return suggester;
	}

	/**
	 * Create name for a suggester.
	 * 
	 * @return
	 */
	protected String createSuggesterName() {

		if (random == null) {
			random = new Random();
		}

		return "suggester" + random.nextInt(1000);
	}

	public static final String GSEARCH_SUGGESTER_NAME = "gsearchSuggester";

	private static Random random;

	private static final Logger _log =
		LoggerFactory.getLogger(GSearchKeywordSuggesterImpl.class);

	@Reference
	protected ConfigurationHelper _configurationHelper;

	@Reference
	private QuerySuggester _querySuggester;
}
