
package fi.soveltia.liferay.gsearch.core.impl.params;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.exception.ParameterValidationException;
import fi.soveltia.liferay.gsearch.core.api.params.ParameterBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;
import fi.soveltia.liferay.gsearch.core.impl.util.GSearchUtil;

/**
 * Parses keyword request parameter.
 * 
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ParameterBuilder.class
)
public class KeywordParameterBuilder implements ParameterBuilder {

	@Override
	public void addParameter(QueryContext queryContext)
		throws Exception {

		PortletRequest portletRequest =
			GSearchUtil.getPortletRequestFromContext(queryContext);

		String keywords =
			ParamUtil.getString(portletRequest, ParameterNames.KEYWORDS);

		addKeywords(queryContext, keywords);
	}

	@Override
	public void addParameter(
		QueryContext queryContext, Map<String, Object> parameters)
		throws Exception {

		String keywords = (String) parameters.get(ParameterNames.KEYWORDS);
		addKeywords(queryContext, keywords);
	}

	@Override
	public boolean validate(QueryContext queryContext)
		throws ParameterValidationException {

		PortletRequest portletRequest =
			GSearchUtil.getPortletRequestFromContext(queryContext);

		String keywords =
			ParamUtil.getString(portletRequest, ParameterNames.KEYWORDS);

		return validateKeywords(queryContext, keywords);
	}

	@Override
	public boolean validate(
		QueryContext queryContext, Map<String, Object> parameters)
		throws ParameterValidationException {

		String keywords = (String) parameters.get(ParameterNames.KEYWORDS);

		return validateKeywords(queryContext, keywords);
	}

	/**
	 * Add keywords.
	 * 
	 * @param queryContext
	 * @param keywords
	 */
	protected void addKeywords(QueryContext queryContext, String keywords) {

		// Try to check if we have a Lucene style search eg. userName:foo
		// title:bar and don't lowercase the field names.
		// Also, don't lowercase BOOLEAN uppercase operators.

		StringBundler sb = new StringBundler();

		String keywordsArray[] = keywords.split(" ");

		for (String s : keywordsArray) {

			String fields[] = s.split(":");

			if (fields.length == 2) {
				sb.append(fields[0]);
				sb.append(":");
				sb.append(fields[1].toLowerCase());
			}
			else if (s.equals("AND") || s.equals("OR") || s.equals("NOT")) {
				sb.append(s);
			}
			else {
				sb.append(s.toLowerCase());
			}
			sb.append(" ");
		}

		queryContext.setKeywords(sb.toString().trim());
	}

	/**
	 * Validate keywords.
	 * 
	 * @param queryContext
	 * @param keywords
	 * @return
	 * @throws ParameterValidationException
	 */
	protected boolean validateKeywords(QueryContext queryContext, String keywords)
		throws ParameterValidationException {

		if (Validator.isNotNull(keywords)) {

			if (keywords.length() <= KEYWORDS_MAX_LENGTH) {
				return true;
			}
			else {
				throw new ParameterValidationException(
					"Keywords max length exceeded.");
			}
		}

		return true;
	}

	public static final int KEYWORDS_MAX_LENGTH = 100;
}
