package fi.soveltia.liferay.gsearch.web.search.internal.queryparams;

import com.liferay.portal.kernel.util.Validator;

/**
 * Request parameter validator class.
 * 
 * @author Petteri Karttunen
 */
public class RequestParamValidator {

	/**
	 * Validate keywords.
	 * 
	 * @return true if valid, false or not
	 */
	public boolean validateKeywords(String keywords) {

		if (Validator.isNotNull(keywords) &&
			keywords.length() <= KEYWORDS_MAX_LENGTH) {
			return true;
		}
		return false;
	}

	// Maximum length of keywords. Could be put in the configuration.
	
	public static final int KEYWORDS_MAX_LENGTH = 100;
}
