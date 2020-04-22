package fi.soveltia.liferay.gsearch.core.impl.query.postprocessor;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.soveltia.liferay.gsearch.core.api.constants.SessionAttributes;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.api.query.postprocessor.QueryPostProcessor;
import fi.soveltia.liferay.gsearch.core.impl.configuration.WOIConfiguration;
import fi.soveltia.liferay.gsearch.core.impl.util.GSearchUtil;
import opennlp.tools.stemmer.PorterStemmer;

/**
 * Words Of Interest processor. 
 * 
 * Whenever there's a succesfull search, the keywords are put
 * in the previous queries session store and made available 
 * for clause configuration template variables for subsequent
 * queries.
 *
 * @author Petteri Karttunen
 */
@Component(
	configurationPid = "i.soveltia.liferay.gsearch.core.impl.configuration.WOIConfiguration",
	immediate = true, 
	service = QueryPostProcessor.class
)
public class WOIProcessorImpl implements QueryPostProcessor {

	@Override
	public boolean process(
			QueryContext queryContext, SearchSearchResponse searchResponse) 
					throws Exception {		
		
		if (_log.isDebugEnabled()) {
			_log.debug("Processing Words of Interest");
		}

		Hits hits = searchResponse.getHits();

		// Process only if it was a succesfull query.
		
		if (Validator.isBlank(queryContext.getKeywords()) ||
			(hits.getLength() == 0)) {

			return true;
		}

		PortletRequest portletRequest = 
				GSearchUtil.getPortletRequestFromContext(queryContext);

		PortletSession session = portletRequest.getPortletSession();

		// Store the previous search phrase as is in session.
		// This is for the click tracking

		String keywords = queryContext.getKeywords();

		session.setAttribute(SessionAttributes.PREVIOUS_SEARCH_PHRASE, 
				keywords, PortletSession.APPLICATION_SCOPE);

		Map<String, Integer> previousKeywords = _getPreviousKeywords(session);

		keywords = keywords.trim().toLowerCase();
		
		keywords = keywords.replaceAll("[^a-zA-Z0-9 ]", "");
		
		String[] words = keywords.split("[ ]+");

		List<String>processedwords = new ArrayList<String>();
		
		PorterStemmer porterStemmer = new PorterStemmer();
		
		for (String word : words) {
			
			// Strip some basic stop words
			// Don't process words shorter than 3 chars.
			
			if (_isStopWord(word) || word.length() < 3) {
				continue;
			}
			
		    String stem = porterStemmer.stem(word);
		    
		    if (processedwords.contains(stem)) {
		    	continue;
		    }

		    processedwords.add(stem);
		    _updatePreviousKeywords(stem, previousKeywords);
		}
		
		// Store in session.
		
		session.setAttribute(SessionAttributes.PREVIOUS_KEYWORDS, 
				previousKeywords, PortletSession.APPLICATION_SCOPE);

		return true;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_woiConfiguration = ConfigurableUtil.createConfigurable(
			WOIConfiguration.class, properties);
	}
	
	/**
	 * Gets the previous keywords from session.
	 * 
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Integer> _getPreviousKeywords(PortletSession session) {

		Map<String, Integer> previousKeywords = 
				(Map<String, Integer>)session.getAttribute(
				SessionAttributes.PREVIOUS_KEYWORDS, PortletSession.APPLICATION_SCOPE);

		if (previousKeywords != null) {
			return previousKeywords;
		} else {
			return new HashMap<String, Integer>();
		}
	}
	
	/**
	 * Checks whether the word in on the stop word list.
	 * 
	 * @param word
	 * @return
	 */
	private boolean _isStopWord(String word) {
		
		for (String s : _STOP_WORDS) {
			if (s.equalsIgnoreCase(word)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Updates the previous keywords map.
	 * 
	 * @param word
	 * @param previousKeywords
	 */
	private void _updatePreviousKeywords(String word, Map<String, Integer> previousKeywords) {
		
		if (_log.isDebugEnabled()) {
			_log.debug("Current previous keywords:");
			
			for (Entry<String, Integer>entry : previousKeywords.entrySet()) {
				_log.debug(entry.getKey() + ":" + entry.getValue());
			}

			_log.debug("Map size: " + previousKeywords.size());
		}
		
	    if (previousKeywords.containsKey(word)) {

	    	previousKeywords.compute(word, (key, value) -> value+1);

	    } else {

	    	previousKeywords.put(word, 1);
	    }
	    
	    // Remove least frequent mapping, if store full.
	    // This is a brute force O(n) removal for the fist entry having a value 1.
	    
	    if (previousKeywords.size() >= _woiConfiguration.storeSize()) {

			for (Entry<String, Integer>entry : previousKeywords.entrySet()) {
				
				if (entry.getValue() == 1) {
					previousKeywords.remove(entry.getKey());
					
					if (_log.isDebugEnabled()) {
						_log.debug("Removed previous keyword: " + entry.getKey());
					}
					
					break;
				}
			}
	    }
	    
		if (_log.isDebugEnabled()) {
			_log.debug("Updated previous keywords:");
			
			for (Entry<String, Integer>entry : previousKeywords.entrySet()) {
				_log.debug(entry.getKey() + ":" + entry.getValue());
			}
			
			_log.debug("Map size: " + previousKeywords.size());
		}
	}
	
	private static final Logger _log = LoggerFactory.getLogger(
		WOIProcessorImpl.class);

	// An example of using Englisgh stop words list.
	
	private static final String[] _STOP_WORDS = new String[] {
			"a", "about", "above", "after", "again", "against", "all", "am", "an",
			"and", "any", "are", "aren't", "as", "at", "be", "because", "been",
			"before", "being", "below", "between", "both", "but", "by", "can't", 
			"cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't", 
			"doing", "don't", "down", "during", "each", "few", "for", "from", "further", 
			"had", "hadn't", "has", "hasn't", "have", "haven't", "having", "he", "he'd", 
			"he'll", "he's", "her", "here", "here's", "hers", "herself", "him", "himself", 
			"his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", 
			"is", "isn't", "it", "it's", "its", "itself", "let's", "me", "more", "most", 
			"mustn't", "my", "myself", "no", "nor", "not", "of", "off", "on", "once", 
			"only", "or", "other", "ought", "our", "ours", "ourselves", "out", "over",
			"own", "same", "shan't", "she", "she'd", "she'll", "she's", "should",
			"shouldn't", "so", "some", "such", "than", "that", "that's", "the", "their",
			"theirs", "them", "themselves", "then", "there", "there's", "these", "they",
			"they'd", "they'll", "they're", "they've", "this", "those", "through", "to",
			"too", "under", "until", "up", "very", "was", "wasn't", "we", "we'd", "we'll",
			"we're", "we've", "were", "weren't", "what", "what's", "when", "when's", "where",
			"where's", "which", "while", "who", "who's", "whom", "why", "why's", "with",
			"won't", "would", "wouldn't", "you", "you'd", "you'll", "you're", "you've",
			"your", "yours", "yourself", "yourselves"
	};

	private volatile WOIConfiguration
		_woiConfiguration;

}