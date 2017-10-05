package fi.soveltia.liferay.gsearch.web.search.query;

import com.liferay.portal.kernel.util.Validator;

/**
 * Request parameter validator
 * 
 * @author Petteri Karttunen
 */
public class RequestParamValidator {

	/**
	 * Validate keywords
	 * 
	 * @return
	 */
	public boolean validateKeywords(String keywords) {

		if (Validator.isNotNull(keywords) &&
			keywords.length() <= KEYWORDS_MAX_LENGTH) {
			return true;
		}
		return false;
	}

	public static final int KEYWORDS_MAX_LENGTH = 100;
}
