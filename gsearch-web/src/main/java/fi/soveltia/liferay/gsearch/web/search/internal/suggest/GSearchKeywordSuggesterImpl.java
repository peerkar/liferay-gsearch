
package fi.soveltia.liferay.gsearch.web.search.internal.suggest;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.suggest.PhraseSuggester;
import com.liferay.portal.kernel.search.suggest.QuerySuggester;
import com.liferay.portal.kernel.search.suggest.SuggesterResult;
import com.liferay.portal.kernel.search.suggest.SuggesterResult.Entry;
import com.liferay.portal.kernel.search.suggest.SuggesterResult.Entry.Option;
import com.liferay.portal.kernel.search.suggest.SuggesterResults;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

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
@Component(
	immediate = true, 
	service = GSearchKeywordSuggester.class
)
public class GSearchKeywordSuggesterImpl implements GSearchKeywordSuggester {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONArray getSuggestions(
		PortletRequest portletRequest, PortletResponse portletResponse,
		GSearchDisplayConfiguration gSearchDisplayConfiguration)
		throws Exception {

		return getSuggestions(portletRequest, gSearchDisplayConfiguration);
	}

	/**
	 * Get suggestions as a JSON array.
	 * 
	 * @param portletRequest
	 * @param gSearchDisplayConfiguration
	 * @return array of suggestions
	 * @throws SearchException
	 */
	protected JSONArray getSuggestions(
		PortletRequest portletRequest,
		GSearchDisplayConfiguration gSearchDisplayConfiguration)
		throws SearchException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

		// Create searchcontext.

		SearchContext searchContext = new SearchContext();
		searchContext.setCompanyId(themeDisplay.getCompanyId());

		String keywords =
			ParamUtil.getString(portletRequest, GSearchWebKeys.KEYWORDS);

		String suggestionField = "keywordSearch_" + themeDisplay.getLanguageId();

		/*
		
		// Termsuggester example 

		TermSuggester suggester = new TermSuggester(GSEARCH_SUGGESTION_NAME, suggestionField, keywords);
		  
		// Aggregatesuggester example 
		 
		AggregateSuggester Suggester = new AggregateSuggester(GSEARCH_SUGGESTION_NAME, keywords); 
		
		... create suggesters and add them to aggregation suggester.addSuggester(suggester1);

		 */

		// Using phrasesuggester.
		
		PhraseSuggester suggester =
			new PhraseSuggester(GSEARCH_SUGGESTION_NAME, suggestionField, keywords);

		// These are the most important parameter when tuning how easily to triger showing suggestion
		// Please see the elasticsearch documentation for more information.
		
		suggester.setRealWordErrorLikelihood(gSearchDisplayConfiguration.keywordSuggestionsRealWordErrorLikelihood());
		suggester.setMaxErrors(gSearchDisplayConfiguration.keywordSuggestionsMaxErrors());
		suggester.setSize(gSearchDisplayConfiguration.keywordSuggestionsMax());
		suggester.setConfidence(gSearchDisplayConfiguration.keywordSuggestionsConfidence());

		// Build results JSON object.

		JSONArray resultsArray = JSONFactoryUtil.createJSONArray();

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

					resultsArray.put(option.getText());
				}
			}
		}
		return resultsArray;
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
