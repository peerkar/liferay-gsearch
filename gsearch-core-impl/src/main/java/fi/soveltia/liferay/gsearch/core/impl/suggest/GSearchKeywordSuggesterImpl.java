
package fi.soveltia.liferay.gsearch.core.impl.suggest;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
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
import java.util.Map;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import fi.soveltia.liferay.gsearch.core.api.constants.GSearchWebKeys;
import fi.soveltia.liferay.gsearch.core.api.suggest.GSearchKeywordSuggester;
import fi.soveltia.liferay.gsearch.core.configuration.GSearchConfiguration;

/**
 * GSearch keywords suggester implementation.
 * 
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "fi.soveltia.liferay.gsearch.core.configuration.GSearchConfiguration", 
	immediate = true, 
	service = GSearchKeywordSuggester.class
)
public class GSearchKeywordSuggesterImpl implements GSearchKeywordSuggester {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_gSearchConfiguration = ConfigurableUtil.createConfigurable(
			GSearchConfiguration.class, properties);
	}		
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public JSONArray getSuggestions(
		PortletRequest portletRequest)
		throws Exception {

		String[] suggestions = getSuggestionsAsStringArray(portletRequest);
		
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
		PortletRequest portletRequest)
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
			suggestionField, keywords);

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
	 * @param GSearchConfiguration
	 * @param suggestionField
	 * @param keywords
	 * @return
	 */
	protected Suggester getAggregateSuggester(
		GSearchConfiguration GSearchConfiguration,
		String suggestionField, String keywords) {

		AggregateSuggester suggester =
			new AggregateSuggester(GSEARCH_SUGGESTION_NAME, keywords);

		suggester.addSuggester(
			getTermSuggester(
				GSearchConfiguration, suggestionField, keywords));

		return suggester;
	}

	/**
	 * Get phrase suggester.
	 * 
	 * @param suggestionField
	 * @param keywords
	 * @return
	 */
	protected Suggester getPhraseSuggester(
		String suggestionField, String keywords) {

		PhraseSuggester suggester = new PhraseSuggester(
			GSEARCH_SUGGESTION_NAME, suggestionField, keywords);

		// These are the most important parameter when tuning how easily to
		// triger showing suggestion
		// Please see the elasticsearch documentation for more information.

		suggester.setRealWordErrorLikelihood(
			_gSearchConfiguration.keywordSuggestionsRealWordErrorLikelihood());
		suggester.setMaxErrors(
			_gSearchConfiguration.keywordSuggestionsMaxErrors());
		suggester.setSize(_gSearchConfiguration.keywordSuggestionsMax());
		suggester.setConfidence(
			_gSearchConfiguration.keywordSuggestionsConfidence());

		return suggester;
	}

	/**
	 * Get term suggester.
	 * 
	 * @param GSearchConfiguration
	 * @param suggestionField
	 * @param keywords
	 * @return
	 */
	protected Suggester getTermSuggester(
		GSearchConfiguration GSearchConfiguration,
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

	private volatile GSearchConfiguration _gSearchConfiguration;

	private static final Log _log =
		LogFactoryUtil.getLog(GSearchKeywordSuggesterImpl.class);

}
