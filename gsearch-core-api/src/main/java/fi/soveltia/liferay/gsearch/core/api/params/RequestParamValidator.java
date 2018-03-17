package fi.soveltia.liferay.gsearch.core.api.params;

/**
 * Request parameter validation service.
 * 
 * @author Petteri Karttunen
 *
 */
public interface RequestParamValidator {

	/**
	 * Simple keywords validation
	 * 
	 * @param keywords
	 * @return
	 */
	public boolean validateKeywords(String keywords);
}
