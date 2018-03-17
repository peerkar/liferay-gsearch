package fi.soveltia.liferay.gsearch.core.impl.queryparams;

import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.queryparams.RequestParamValidator;

/**
 * Request parameter validator class.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = RequestParamValidator.class
)
public class RequestParamValidatorImpl implements RequestParamValidator {

	/**
	 * {@inheritDoc}
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
