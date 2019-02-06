package fi.soveltia.liferay.gsearch.core.impl.params;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;

import fi.soveltia.liferay.gsearch.core.api.constants.ParameterNames;
import fi.soveltia.liferay.gsearch.core.api.exception.ParameterValidationException;
import fi.soveltia.liferay.gsearch.core.api.params.ParameterBuilder;
import fi.soveltia.liferay.gsearch.core.api.query.context.QueryContext;

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
	public void addParameter(
		PortletRequest portletRequest, QueryContext queryContext)
		throws Exception {

		String keywords = ParamUtil.getString(portletRequest, ParameterNames.KEYWORDS);

		// Try to check if we have a Lucene style search eg. userName:foo title:bar
		// and don't lowercase the field names.
		// Also, don't lowercase BOOLEAN uppercase operators.
		
		StringBundler sb = new StringBundler();
		
		String keywordsArray[] = keywords.split(" ");
		
		for (String s : keywordsArray) {

			String fields[] = s.split(":");

			if (fields.length == 2) {
				sb.append(fields[0]);
				sb.append(":");
				sb.append(fields[1].toLowerCase());
			} else if (s.equals("AND") || s.equals("OR") || s.equals("NOT")) {
				sb.append(s);
			} else {
				sb.append(s.toLowerCase());
			}
			sb.append(" ");
		}
				
		queryContext.setKeywords(sb.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean validate(PortletRequest portletRequest) throws ParameterValidationException {
		
		String keywords = ParamUtil.getString(portletRequest, ParameterNames.KEYWORDS);

		if (Validator.isNotNull(keywords) &&
			keywords.length() <= KEYWORDS_MAX_LENGTH) {
			return true;
		} else {
			throw new ParameterValidationException("Keywords max length exceeded.");
		}
	}

	public static final int KEYWORDS_MAX_LENGTH = 100;	
}
