
package fi.soveltia.liferay.gsearch.core.impl.params;

import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.exception.ParameterValidationException;
import fi.soveltia.liferay.gsearch.core.api.params.ParameterBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

/**
 * Keywords are set to query context in the context builder already.
 * This parser only does the validation.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ParameterBuilder.class
)
public class KeywordParameterBuilder implements ParameterBuilder {

	public static final int KEYWORDS_MAX_LENGTH = 100;

	@Override
	public void addParameter(QueryContext queryContext) throws Exception {
	}

	@Override
	public void addParameterHeadless(
			QueryContext queryContext, Map<String, Object> parameters)
		throws Exception {
	}

	@Override
	public boolean validate(QueryContext queryContext)
		throws ParameterValidationException {

		String keywords = queryContext.getKeywords();

		return validateKeywords(queryContext, keywords);
	}

	@Override
	public boolean validateHeadless(
			QueryContext queryContext, Map<String, Object> parameters)
		throws ParameterValidationException {

		String keywords = queryContext.getKeywords();

		return validateKeywords(queryContext, keywords);
	}

	/**
	 * Validate keywords.
	 *
	 * @param queryContext
	 * @param keywords
	 * @return
	 * @throws ParameterValidationException
	 */
	protected boolean validateKeywords(
			QueryContext queryContext, String keywords)
		throws ParameterValidationException {

		if (Validator.isNotNull(keywords)) {
			if (keywords.length() <= KEYWORDS_MAX_LENGTH) {
				return true;
			}

			throw new ParameterValidationException(
				"Keywords max length exceeded.");
		}

		return true;
	}
}