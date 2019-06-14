
package fi.soveltia.liferay.gsearch.core.impl.query.clause.condition;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.query.clause.ClauseConditionHandler;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Keyword match condition handler.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ClauseConditionHandler.class
)
public class KeywordMatchClauseConditionHandler
	implements ClauseConditionHandler {

	@Override
	public boolean canProcess(String handlerName) {

		return (handlerName.equals(HANDLER_NAME));
	}

	@Override
	public boolean isTrue(
		QueryContext queryContext,
		JSONObject configuration)
		throws Exception {

		String keywords = queryContext.getKeywords();

		String matchType = configuration.getString("match_type");

		if ("full_phrase".equals(matchType)) {

			return isFullPhraseMatch(configuration, keywords);

		}
		else if ("full_word".equals(matchType)) {

			return isFullWordMatch(configuration, keywords);
		}

		return false;
	}

	/**
	 * Check full phrase match.
	 * 
	 * @param configuration
	 * @param keywords
	 * @return
	 */
	public boolean isFullPhraseMatch(
		JSONObject configuration, String keywords) {

		JSONArray matchWords = configuration.getJSONArray("match_words");

		for (int i = 0; i < matchWords.length(); i++) {

			if (keywords.equals(matchWords.getString(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Handle full word matching case. Values for match_occur: should | must
	 * 
	 * @param configuration
	 * @param keywords
	 * @return
	 */
	public boolean isFullWordMatch(JSONObject configuration, String keywords) {

		String splitter =
			configuration.getString("keyword_splitter_regexp", " ");

		JSONArray matchWords = configuration.getJSONArray("match_words");

		String matchOccur = configuration.getString("match_occur", "should");

		String[] keywordArray = keywords.split(splitter);
		
		for (String keyword : keywordArray) {

			boolean isWordMatch = false;
			
			for (int i = 0; i < matchWords.length(); i++) {

				if (matchWords.getString(i).equals(keyword)) {

					if ("must_not".equals(matchOccur)) {

						return false;
					}		
					else if ("should".equals(matchOccur)) {

						return true;
					}
					else {
	
						isWordMatch = true;

						break;
					}
				}
			}
			
			if ("must".equals(matchOccur) && !isWordMatch) {
				
				return false;
			}
		}
		
		return true;
	}

	private static final String HANDLER_NAME = "keyword_match";

}
