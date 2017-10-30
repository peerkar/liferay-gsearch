
package fi.soveltia.liferay.gsearch.web.search.internal.suggest;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.suggest.AggregateSuggester;
import com.liferay.portal.kernel.search.suggest.PhraseSuggester;
import com.liferay.portal.kernel.search.suggest.QuerySuggester;
import com.liferay.portal.kernel.search.suggest.Suggester;
import com.liferay.portal.kernel.search.suggest.SuggesterResult;
import com.liferay.portal.kernel.search.suggest.SuggesterResult.Entry;
import com.liferay.portal.kernel.search.suggest.SuggesterResult.Entry.Option;
import com.liferay.portal.kernel.search.suggest.SuggesterResults;
import com.liferay.portal.kernel.search.suggest.TermSuggester;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.web.configuration.GSearchDisplayConfiguration;
import fi.soveltia.liferay.gsearch.web.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.web.search.suggest.GSearchKeywordSuggester;

/**
 * GSearch keywords suggester implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(immediate = true, service = GSearchKeywordSuggester.class)
public class GSearchKeywordSuggesterImpl implements GSearchKeywordSuggester {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONArray getSuggestions(
		PortletRequest portletRequest,
		GSearchDisplayConfiguration gSearchDisplayConfiguration)
		throws Exception {

		String[] suggestions = getSuggestionsAsStringArray(portletRequest, gSearchDisplayConfiguration);
		
		// Build results JSON object.

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
	public String[] getSuggestionsAsStringArray(
		PortletRequest portletRequest,
		GSearchDisplayConfiguration gSearchDisplayConfiguration)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		// Create searchcontext.

		SearchContext searchContext = new SearchContext();
		searchContext.setCompanyId(themeDisplay.getCompanyId());

		String keywords =
			ParamUtil.getString(portletRequest, GSearchWebKeys.KEYWORDS);

		String suggestionField =
			"keywordSearch_" + themeDisplay.getLanguageId();

		Suggester suggester = getPhraseSuggester(
			gSearchDisplayConfiguration, suggestionField, keywords);

		// Build results JSON object.

		List<String>suggestions = new ArrayList<String>();
		
		SuggesterResults suggesterResults =
			_querySuggester.suggest(searchContext, suggester);

		SuggesterResult suggesterResult =
			suggesterResults.getSuggesterResult(suggester.getName());

		if (suggesterResult != null) {

			for (Entry entry : suggesterResult.getEntries()) {

				for (Option option : entry.getOptions()) {

					if (_log.isDebugEnabled()) {
						_log.debug("Adding suggestion:" + option.getText());
					}

					suggestions.add(option.getText());
				}
			}
		}
		return suggestions.stream().toArray(String[]::new);
	}

	/**
	 * Get aggregate suggester.
	 * 
	 * @param gSearchDisplayConfiguration
	 * @param suggestionField
	 * @param keywords
	 * @return
	 */
	protected Suggester getAggregateSuggester(
		GSearchDisplayConfiguration gSearchDisplayConfiguration,
		String suggestionField, String keywords) {

		AggregateSuggester suggester =
			new AggregateSuggester(GSEARCH_SUGGESTION_NAME, keywords);

		suggester.addSuggester(
			getTermSuggester(
				gSearchDisplayConfiguration, suggestionField, keywords));

		return suggester;
	}

	/**
	 * Get phrase suggester.
	 * 
	 * @param gSearchDisplayConfiguration
	 * @param suggestionField
	 * @param keywords
	 * @return
	 */
	protected Suggester getPhraseSuggester(
		GSearchDisplayConfiguration gSearchDisplayConfiguration,
		String suggestionField, String keywords) {

		PhraseSuggester suggester = new PhraseSuggester(
			GSEARCH_SUGGESTION_NAME, suggestionField, keywords);

		// These are the most important parameter when tuning how easily to
		// triger showing suggestion
		// Please see the elasticsearch documentation for more information.

		suggester.setRealWordErrorLikelihood(
			gSearchDisplayConfiguration.keywordSuggestionsRealWordErrorLikelihood());
		suggester.setMaxErrors(
			gSearchDisplayConfiguration.keywordSuggestionsMaxErrors());
		suggester.setSize(gSearchDisplayConfiguration.keywordSuggestionsMax());
		suggester.setConfidence(
			gSearchDisplayConfiguration.keywordSuggestionsConfidence());

		return suggester;
	}

	/**
	 * Get term suggester.
	 * 
	 * @param gSearchDisplayConfiguration
	 * @param suggestionField
	 * @param keywords
	 * @return
	 */
	protected Suggester getTermSuggester(
		GSearchDisplayConfiguration gSearchDisplayConfiguration,
		String suggestionField, String keywords) {

		// Termsuggester example.
		// There are quite a few options to set for the suggester
		// but this is here only serving as a baseline.

		TermSuggester suggester = new TermSuggester(
			GSEARCH_SUGGESTION_NAME, suggestionField, keywords);

		return suggester;
	}

	public static final String GSEARCH_SUGGESTION_NAME = "gsearchSuggestion";

	@Reference(unbind = "-")
	protected void setQuerySuggester(QuerySuggester querySuggester) {

		_querySuggester = querySuggester;
	}

	@Reference
	protected QuerySuggester _querySuggester;

	private static final Log _log =
		LogFactoryUtil.getLog(GSearchKeywordSuggesterImpl.class);

}
