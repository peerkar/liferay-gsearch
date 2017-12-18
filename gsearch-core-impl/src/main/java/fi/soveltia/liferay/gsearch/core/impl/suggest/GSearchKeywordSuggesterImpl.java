
package fi.soveltia.liferay.gsearch.core.impl.suggest;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
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
		
		Locale locale = themeDisplay.getLocale();
		
		String keywords =
						ParamUtil.getString(portletRequest, GSearchWebKeys.KEYWORDS);

		AggregateSuggester aggregateSuggester =
						new AggregateSuggester(GSEARCH_SUGGESTION_NAME, keywords);

		JSONArray configurationArray = JSONFactoryUtil.createJSONArray(_gSearchConfiguration.suggestConfiguration());
		
		for (int i = 0; i < configurationArray.length(); i++) {
			
			JSONObject item = configurationArray.getJSONObject(i);
		
			String suggesterType = item.getString("suggesterType");

			Suggester suggester = null;
			if ("phrase".equals(suggesterType)) {
				suggester = getPhraseSuggester(item, locale, keywords);
				
			} else if ("completion".equals(suggesterType)) {
				suggester = getCompletionSuggester(item, locale, keywords);
			}

			if (suggester != null) {
				aggregateSuggester.addSuggester(suggester);
			}
		}
		
		// Create searchcontext.

		SearchContext searchContext = new SearchContext();
		searchContext.setCompanyId(themeDisplay.getCompanyId());

		// Build results JSON object.

		List<String>suggestions = new ArrayList<String>();
				
		SuggesterResults suggesters =
			_querySuggester.suggest(searchContext, aggregateSuggester);

		Collection<SuggesterResult> suggesterResults =
						suggesters.getSuggesterResults();

		if (suggesterResults != null) {

			for (SuggesterResult suggesterResult : suggesterResults) {

				_log.info(suggesterResults.size());

				for (Entry entry : suggesterResult.getEntries()) {
	
					for (Option option : entry.getOptions()) {
	
						if (_log.isDebugEnabled()) {
							_log.debug("Adding suggestion:" + option.getText());
						}
	
						if (!suggestions.contains(option.getText())) {
							suggestions.add(option.getText());
						}
					}
				}
			}
		}
		return suggestions.stream().toArray(String[]::new);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_gSearchConfiguration = ConfigurableUtil.createConfigurable(
			GSearchConfiguration.class, properties);
	}		
	
	/**
	 * Get completion suggester
	 * 
	 * @param configuration
	 * @param locale
	 * @param keywords
	 * @return
	 * @throws Exception
	 */
	protected Suggester getCompletionSuggester(
		JSONObject configuration, Locale locale, String keywords) throws Exception {

		StringBundler sb = new StringBundler();

		sb.append(configuration.getString("fieldPrefix"));

		if (configuration.getBoolean(("isLocalized"))) {
			sb.append(locale.toString());
		}
		sb.append(configuration.getString("fieldSuffix"));
		
		CompletionSuggester suggester = new CompletionSuggester(
			configuration.getString("suggesterName"), sb.toString(), keywords);
		
		suggester.setSize(configuration.getInt("numberOfSuggestions"));
		
		String analyzer = configuration.getString("analyzer");

		if (analyzer != null) {
			suggester.setAnalyzer(analyzer);
		}

		return suggester;
	}
	
	/**
	 * Get phrase suggester.
	 * 
	 * @param configuration
	 * @param locale
	 * @param keywords
	 * @return
	 * @throws Exception
	 */
	protected Suggester getPhraseSuggester(
		JSONObject configuration, Locale locale, String keywords) throws Exception {
		
		StringBundler sb = new StringBundler();

		sb.append(configuration.getString("fieldPrefix"));

		if (configuration.getBoolean(("isLocalized"))) {
			sb.append(locale.toString());
		}
		sb.append(configuration.getString("fieldSuffix"));
		
		PhraseSuggester suggester = new PhraseSuggester(
			configuration.getString("suggesterName"), sb.toString(), keywords);
	
		
		int size = GetterUtil.getInteger(configuration.get("numberOfSuggestions"), 5);
		suggester.setSize(size);
		
		float confidence = GetterUtil.getFloat(configuration.get("confidence"), 0.1f);
		suggester.setConfidence(confidence);

		int gramSize = GetterUtil.getInteger(configuration.get("gramSize"), 2);
		suggester.setGramSize(gramSize);

		float maxErrors = GetterUtil.getFloat(configuration.get("maxErrors"), 2.0f);
		suggester.setMaxErrors(maxErrors);

		float realWordErrorLikelihood = GetterUtil.getFloat(configuration.get("realWordErrorLikelihood"), 0.95f);
		suggester.setRealWordErrorLikelihood(realWordErrorLikelihood);

		return suggester;
	}

	@Reference(unbind = "-")
	protected void setQuerySuggester(QuerySuggester querySuggester) {

		_querySuggester = querySuggester;
	}

	public static final String GSEARCH_SUGGESTION_NAME = "gsearchSuggestion";

	private volatile GSearchConfiguration _gSearchConfiguration;

	private QuerySuggester _querySuggester;

	private static final Log _log =
		LogFactoryUtil.getLog(GSearchKeywordSuggesterImpl.class);

}
